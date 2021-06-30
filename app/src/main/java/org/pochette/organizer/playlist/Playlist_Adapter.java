package org.pochette.organizer.playlist;

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
import org.pochette.data_library.playlist.Playlist;
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
public class Playlist_Adapter extends RecyclerView.Adapter<Playlist_ViewHolder>
        implements Shouting, Refreshable {

    public final static int LAYOUT_MODE_STANDARD = 0; // orginal
    public final static int LAYOUT_MODE_COMPACT = 1; // sh
    //  public final static int LAYOUT_MODE_DYNAMIC = 2;

    private boolean mAR_Any_Lock = false; // internal flag, to control workings on the mAR* take place only once at a time.

    //Variables
    private static final String TAG = "FEHA (Playlist_Adapter)";
    private int mLayoutMode;

    private ArrayList<Playlist> mAR_Playlist;
    public Playlist mSelectedPlaylist = null;

    Shouting mShouting;
    Shout mShoutToCeiling;
    Shout mGlassFloor;

    //Constructor
    @SuppressLint("UseSparseArrays")
    public Playlist_Adapter(Context iContext, RecyclerView iRecyclerView, Shouting upstairs) {
        mLayoutMode = LAYOUT_MODE_STANDARD;
        mShouting = upstairs;
        mShoutToCeiling = new Shout(getClass().getSimpleName());
        waitAndLock();
        mAR_Playlist = new ArrayList<>(0);
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

    public void setAR(ArrayList<Playlist> iAR_Playlist) {
        waitAndLock();
        mAR_Playlist = iAR_Playlist;
        unlock();
        getItemCount();
        this.notifyDataSetChanged();

//        String tString = Scddb_Helper.getInstance().tableToString("LDB.PLAYLIST");
//        Logg.i(TAG, tString);

    }

    public ArrayList<Playlist> getAR() {
        return mAR_Playlist;
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
    // public Playlist_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup iParent, int iViewType) {
    public Playlist_ViewHolder onCreateViewHolder(@NonNull ViewGroup iParent, int iViewType) {
        LayoutInflater inflater = LayoutInflater.from(iParent.getContext());
        View tView;
        Playlist_ViewHolder tViewHolder;
        if (mLayoutMode == LAYOUT_MODE_COMPACT) {
            tView = inflater.inflate(R.layout.row_playlist_compact, iParent, false);
        } else {
            tView = inflater.inflate(R.layout.row_playlist, iParent, false);

        }
        tViewHolder = new Playlist_ViewHolder(tView);
        tViewHolder.setShouting(this);
        tView.setOnClickListener(iView -> {
            Logg.k(TAG, "Row OnClick ");
            toggleSelect(tViewHolder.mPlaylist);
        });
        return tViewHolder;
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull Playlist_ViewHolder iViewHolder, int iDoNotUsePosition) {
        if (iViewHolder.getAdapterPosition() > mAR_Playlist.size()) {
            return;
        }
        int tPosition = iViewHolder.getAdapterPosition();
        iViewHolder.setPlaylist(mAR_Playlist.get(tPosition));
    }
    //</editor-fold>


    //Static Methods
    //Internal Organs
    // internal class
    private void saveSearchValue() {
    }
    private void retrieveSearchValue() {
    }


    private void toggleSelect(Playlist tPlaylist) {
        JSONObject tJson = new JSONObject();
        String tJsonString ="";
        try {
            tJson.put("Id", tPlaylist.getId());
            tJsonString = tJson.toString();
        } catch (JSONException e) {
            Logg.e(TAG, e.toString());
        }
        if (mSelectedPlaylist == null) {
            mSelectedPlaylist = tPlaylist;
            mShoutToCeiling.mLastObject = "Row";
            mShoutToCeiling.mLastAction = "selected";
            mShoutToCeiling.mJsonString = tJsonString;
        } else if (mSelectedPlaylist.getId() == tPlaylist.getId()) {
            mSelectedPlaylist = null;
            mShoutToCeiling.mLastObject = "Row";
            mShoutToCeiling.mLastAction = "unselected";
        } else {
            mSelectedPlaylist = tPlaylist;
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
        mSelectedPlaylist = mAR_Playlist.get(tPosition);
        refresh();
    }

    private void sort() {
        if (mAR_Playlist == null) {
            return;
        }
        if (mAR_Playlist.size() > 1) {
            Collections.sort(mAR_Playlist, (o1, o2) -> {
                        if (o1.getPlaylist_Purpose().getPriority() == o2.getPlaylist_Purpose().getPriority()) {
                            return o1.getId() - o2.getId();
                        } else {
                            return o1.getPlaylist_Purpose().getPriority() - o2.getPlaylist_Purpose().getPriority();
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
        if (mAR_Playlist == null) {
            tResult = 0;
        } else {
            tResult = mAR_Playlist.size();
        }
        unlock();
        return tResult;
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }
}