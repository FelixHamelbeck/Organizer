package org.pochette.organizer.album;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.pochette.data_library.database_management.Refreshable;
import org.pochette.data_library.scddb_objects.Album;
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
public class Album_Adapter extends RecyclerView.Adapter<Album_ViewHolder>
        implements Shouting, Refreshable {

    private static final String TAG = "FEHA (Album_Adapter)";
    //Variables

    public final static int MODE_STANDARD = 1;
    public final static int MODE_PREFERENCE = 2;

    private final RecyclerView mRecyclerView;

    private int mMode = MODE_STANDARD;
    private ArrayList<Album> mAR_Album;
    private Album mSelectedAlbum = null;
    private String mSignatureString4Comparison;


    private boolean mAR_Any_Lock = false; // internal flag, to control workings on the mAR* take place only once at a time.

    Shouting mShouting;
    Shout mShoutToCeiling;
    Shout mGlassFloor;

    //Constructor

    @SuppressLint("UseSparseArrays")
    public Album_Adapter(Context iContext, RecyclerView iRecyclerView, Shouting upstairs) {
        //mAlbum_Adapter = this;
        //mContext = context;
        mRecyclerView = iRecyclerView;
        mShouting = upstairs;
        mShoutToCeiling = new Shout(getClass().getSimpleName());
        waitAndLock();
        mAR_Album = new ArrayList<>(0);
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

    public void setMode(int mode) {
        mMode = mode;
    }

    public int getMode() {
        return mMode;
    }

    public void setSignatureString4Comparison(String signatureString4Comparison) {
        mSignatureString4Comparison = signatureString4Comparison;
    }

    public Album getSelectedAlbum() {
        return mSelectedAlbum;
    }

    public void OnClickOnViewHolder(Album_ViewHolder iAlbum_ViewHolder) {
        Logg.k(TAG, "onClick" + iAlbum_ViewHolder.mAlbum.toString());
        Album tAlbum = iAlbum_ViewHolder.mAlbum;
        if (mSelectedAlbum == null) {
            mSelectedAlbum = tAlbum;

            Logg.i(TAG, mSelectedAlbum.mSignature);
            Logg.i(TAG, mSignatureString4Comparison);
        } else if (mSelectedAlbum.mId == tAlbum.mId) {
            mSelectedAlbum = null;
        } else {
            Album tOldSelectedAlbum = mSelectedAlbum;
            mSelectedAlbum = tAlbum;
            for (int childCount = mRecyclerView.getChildCount(), i = 0; i < childCount; ++i) {
                final Album_ViewHolder fVH =
                        (Album_ViewHolder) mRecyclerView.getChildViewHolder(
                                mRecyclerView.getChildAt(i));
                if (fVH.mAlbum.mId == tOldSelectedAlbum.mId) {
                    fVH.refreshDisplay();
                    break;
                }
            }
        }


        iAlbum_ViewHolder.refreshDisplay();
        if(mShouting != null) {
            Shout tShout = new Shout(Album_Adapter.class.getSimpleName());
            tShout.mLastObject = "Selection";
            if (mSelectedAlbum == null) {
                tShout.mLastAction = "cleared";
            } else{
                tShout.mLastAction = "performed";
            }
            mShouting.shoutUp(tShout);
        }

    }


    public void setAR(ArrayList<Album> iAR_Album) {
        waitAndLock();
        mAR_Album = iAR_Album;
        unlock();
        getItemCount();
        this.notifyDataSetChanged();
    }

    public ArrayList<Album> getAR() {
        return mAR_Album;
    }

//    public void setFragmentManager(FragmentManager iFragmentManager) {
//        mFragmentManager = iFragmentManager;
//    }

    //</editor-fold>

    //<editor-fold desc="Live cycle">
//    @NonNull
//    @Override
//    // public Album_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup iParent, int iViewType) {
    @SuppressWarnings("NullableProblems")
    public Album_ViewHolder onCreateViewHolder(@NonNull ViewGroup iParent, int iViewType) {
        LayoutInflater inflater = LayoutInflater.from(iParent.getContext());
        View tView;
        Album_ViewHolder tViewHolder;
        tView = inflater.inflate(R.layout.row_album, iParent, false);
        tViewHolder = new Album_ViewHolder(tView);
        tViewHolder.setAlbum_Adapter(this);
        if (mSignatureString4Comparison != null && !mSignatureString4Comparison.isEmpty()) {
            tViewHolder.setSignatureString4Comparison( mSignatureString4Comparison);
        }
        return tViewHolder;
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull Album_ViewHolder iViewHolder, int iDoNotUsePosition) {
        if (iViewHolder.getAdapterPosition() > mAR_Album.size()) {
            return;
        }
        int tPosition = iViewHolder.getAdapterPosition();
        final Album tAlbum;
        tAlbum = mAR_Album.get(tPosition);
        iViewHolder.setAlbum(tAlbum);
    }
    //</editor-fold>


    //Static Methods

    //Internal Organs

    // internal class
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View mLayout;
        TextView mTV_Left;
        TextView mTV_Right;

        ViewHolder(View v) {
            super(v);
            mLayout = v;
            mTV_Left = v.findViewById(R.id.RowTwoColumn_TV_Left);
            mTV_Right = v.findViewById(R.id.RowTwoColumn_TV_Right);
        }
    }


    private void saveSearchValue() {
    }

    private void retrieveSearchValue() {
    }




    @SuppressWarnings("unused")
    private void sort() {
        if (mAR_Album == null) {
            return;
        }
        if (mAR_Album.size() > 1) {
            Collections.sort(mAR_Album, (o1, o2) -> {
                        if (o1.mArtistName.equals(o2.mArtistName)) {
                            return o1.mName.compareToIgnoreCase(o2.mName);
                        } else {
                            return o1.mArtistName.compareToIgnoreCase(o2.mArtistName);
                        }
                    }
            );
        }
    }


    void process_shouting() {

    }


    // Dead simple low weight locking of the array
    private void waitAndLock() {
        int tCount = 0;
        while (mAR_Any_Lock) {
            tCount++;
            try {
                //noinspection BusyWait
                sleep(10);
            } catch(InterruptedException e) {
                Logg.e(TAG, e.toString());
            }
            if ((tCount % 100) == 0) {
                Logg.w(TAG, "waitAndLock: " + tCount);
            }
        }
        // as it become available wait is over and lock starts
        mAR_Any_Lock = true;
        //mAR_Any_Lock_Source = Log.getStackTraceString(new Exception());
    }


    private void unlock() {
        mAR_Any_Lock = false;
    }

    //Interface
    public void refresh() {
        if (mAR_Album != null) {
            Logg.i(TAG, "size" + mAR_Album.size());
        }
        new Handler(Looper.getMainLooper()).post(this::notifyDataSetChanged);
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
        if (mAR_Album == null) {
            tResult = 0;
        } else {
            tResult = mAR_Album.size();
        }
        unlock();
        return tResult;
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }
}