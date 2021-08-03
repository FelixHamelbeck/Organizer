package org.pochette.organizer.requestlist;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONException;
import org.json.JSONObject;
import org.pochette.data_library.database_management.Refreshable;
import org.pochette.data_library.requestlist.Requestlist;
import org.pochette.organizer.R;
import org.pochette.utils_lib.logg.Logg;
import org.pochette.utils_lib.shouting.Shout;
import org.pochette.utils_lib.shouting.Shouting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import static java.lang.Thread.sleep;

/**
 * Adapterclass for ScddbRecording
 */
@SuppressWarnings("unused")
public class Requestlist_Adapter extends RecyclerView.Adapter<Requestlist_ViewHolder>
        implements Shouting, Refreshable {

    public final static int LAYOUT_MODE_STANDARD = 0; // orginal
    public final static int LAYOUT_MODE_COMPACT = 1; // sh
    //  public final static int LAYOUT_MODE_DYNAMIC = 2;

    private boolean mAR_Any_Lock = false; // internal flag, to control workings on the mAR* take place only once at a time.

    //Variables
    private static final String TAG = "FEHA (Requestlist_Adapter)";
    private int mLayoutMode;

    private ArrayList<Requestlist> mAR_Requestlist;
    public Requestlist mSelectedRequestlist = null;

    Shouting mShouting;
    Shout mShoutToCeiling;
    Shout mGlassFloor;

    //Constructor
    @SuppressLint("UseSparseArrays")
    public Requestlist_Adapter(Context iContext, RecyclerView iRecyclerView, Shouting upstairs) {
        mLayoutMode = LAYOUT_MODE_STANDARD;
        mShouting = upstairs;
        mShoutToCeiling = new Shout(getClass().getSimpleName());
        waitAndLock();
        mAR_Requestlist = new ArrayList<>(0);
        unlock();
        if (iRecyclerView != null) {
            DividerItemDecoration tDividerItemDecoration = new DividerItemDecoration(iContext, DividerItemDecoration.VERTICAL);
            tDividerItemDecoration.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(iContext, R.drawable.divider)));
            iRecyclerView.addItemDecoration(tDividerItemDecoration);
        }
        retrieveSearchValue();
    }

    // when Fragment onStop, so we can save sharedpref
    public void onStop() {
        saveSearchValue();
    }

    //Setter and Getter
    //<editor-fold desc="Setter and Getter ">

    public void setAR(ArrayList<Requestlist> iAR_Requestlist) {
        waitAndLock();
        mAR_Requestlist = iAR_Requestlist;
        unlock();
        getItemCount();
        this.notifyDataSetChanged();
    }

    public ArrayList<Requestlist> getAR() {
        return mAR_Requestlist;
    }

    public void setLayoutMode(int layoutMode) {
        if (layoutMode == LAYOUT_MODE_STANDARD || layoutMode == LAYOUT_MODE_COMPACT) {
            mLayoutMode = layoutMode;
            return;
        }
        throw new RuntimeException("Layoutmode not available " + layoutMode);
    }

    //</editor-fold>

    //<editor-fold desc="Live cycle">
    @NonNull
    @Override
    // public Requestlist_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup iParent, int iViewType) {
    public Requestlist_ViewHolder onCreateViewHolder(@NonNull ViewGroup iParent, int iViewType) {
        LayoutInflater inflater = LayoutInflater.from(iParent.getContext());
        View tView;
        Requestlist_ViewHolder tViewHolder;
        if (mLayoutMode == LAYOUT_MODE_COMPACT) {
            tView = inflater.inflate(R.layout.row_requestlist_compact, iParent, false);
        } else {
            tView = inflater.inflate(R.layout.row_requestlist, iParent, false);

        }
        tViewHolder = new Requestlist_ViewHolder(tView);
        tViewHolder.setShouting(this);
        tView.setOnClickListener(iView -> {
            Logg.k(TAG, "Row OnClick ");
            toggleSelect(tViewHolder.mRequestlist);
        });
        return tViewHolder;
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull Requestlist_ViewHolder iViewHolder, int iDoNotUsePosition) {
        if (iViewHolder.getAdapterPosition() > mAR_Requestlist.size()) {
            return;
        }
        int tPosition = iViewHolder.getAdapterPosition();
        iViewHolder.setRequestlist(mAR_Requestlist.get(tPosition));
    }
    //</editor-fold>


    //Static Methods
    //Internal Organs
    // internal class
    private void saveSearchValue() {
    }
    private void retrieveSearchValue() {
    }


    private void toggleSelect(Requestlist tRequestlist) {
        JSONObject tJson = new JSONObject();
        String tJsonString ="";
        try {
            tJson.put("Id", tRequestlist.getId());
            tJsonString = tJson.toString();
        } catch (JSONException e) {
            Logg.e(TAG, e.toString());
        }
        if (mSelectedRequestlist == null) {
            mSelectedRequestlist = tRequestlist;
            mShoutToCeiling.mLastObject = "Row";
            mShoutToCeiling.mLastAction = "selected";
            mShoutToCeiling.mJsonString = tJsonString;
        } else if (mSelectedRequestlist.getId() == tRequestlist.getId()) {
            mSelectedRequestlist = null;
            mShoutToCeiling.mLastObject = "Row";
            mShoutToCeiling.mLastAction = "unselected";
        } else {
            mSelectedRequestlist = tRequestlist;
            mShoutToCeiling.mLastObject = "Row";
            mShoutToCeiling.mLastAction = "selected";
            mShoutToCeiling.mJsonString = tJsonString;
        }
        if (mShouting != null) {
            mShouting.shoutUp(mShoutToCeiling);
        }
        this.notifyDataSetChanged();
    }

    private void setSelect(int tPosition) {
        mSelectedRequestlist = mAR_Requestlist.get(tPosition);
        refresh();
    }

    private void sort() {
        if (mAR_Requestlist == null) {
            return;
        }
        if (mAR_Requestlist.size() > 1) {
            Collections.sort(mAR_Requestlist, (o1, o2) -> {
                        if (o1.getRequestlist_Purpose().getPriority() == o2.getRequestlist_Purpose().getPriority()) {
                            return o1.getId() - o2.getId();
                        } else {
                            return o1.getRequestlist_Purpose().getPriority() - o2.getRequestlist_Purpose().getPriority();
                        }
                    }
            );
        }
    }

    void process_shouting() {
        if (mShouting != null) {
            mShouting.shoutUp(mGlassFloor);
        }
    }


    // Dead simple low weight locking of the array
    @SuppressWarnings("BusyWait")
    private void waitAndLock() {
        int tCount = 0;
        while (mAR_Any_Lock) {
            tCount++;
            try {
                sleep(10);
            } catch (InterruptedException e) {
                Logg.e(TAG, e.toString());
            }
            if ((tCount % 100) == 0) {
                Logg.w(TAG, "waitAndLock: " + tCount);
            }
        }
        // as it become available wait is over and lock starts
        mAR_Any_Lock = true;
    }


    private void unlock() {
        mAR_Any_Lock = false;
    }

    //Interface


    public void refresh() {
        new Handler(Looper.getMainLooper()).post(() -> {
            //noinspection Convert2MethodRef
            notifyDataSetChanged();
        });

    }


    @Override
    public void shoutUp(Shout tShoutToCeiling) {
        Logg.i(TAG, tShoutToCeiling.toString());
        if (tShoutToCeiling.mJsonString != null) {
            Logg.i(TAG, tShoutToCeiling.mJsonString);
        }
        mGlassFloor = tShoutToCeiling;
        process_shouting();
    }

    @Override
    public int getItemCount() {
        int tResult;
        waitAndLock();
        if (mAR_Requestlist == null) {
            tResult = 0;
        } else {
            tResult = mAR_Requestlist.size();
        }
        unlock();
        return tResult;
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }
}