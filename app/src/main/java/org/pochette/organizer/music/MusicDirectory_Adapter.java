package org.pochette.organizer.music;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.pochette.data_library.database_management.Refreshable;
import org.pochette.data_library.music.MusicDirectory;
import org.pochette.data_library.music.MusicPreference;
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
public class MusicDirectory_Adapter extends RecyclerView.Adapter<MusicDirectory_ViewHolder>
        implements Shouting, Refreshable {

    //public class MusicDirectory_Adapter extends RecyclerView.Adapter<MusicDirectory_Adapter.ViewHolder>
    //Variables
    private static final String TAG = "FEHA (MF_Adapter)";

    public final static int MODE_STANDARD = 1;
    public final static int MODE_PREFERENCE = 2;

    private int mMode = MODE_STANDARD;
    private ArrayList<MusicDirectory> mAR_MusicDirectory;
    public MusicDirectory mSelectedMusicDirectory = null;
    private final ArrayList<MusicPreference> mAL_MusicPreference = null;
    // if this adapter is used in connection with music preference an additional array is used


    private boolean mAR_Any_Lock = false; // internal flag, to control workings on the mAR* take place only once at a time.

    //FragmentManager mFragmentManager;

    //Timer mAdapterTimer;

    Shouting mShouting;
    Shout mShoutToCeiling;
    Shout mGlassFloor;

    //Constructor

    @SuppressLint("UseSparseArrays")
    public MusicDirectory_Adapter(Context iContext, RecyclerView iRecyclerView, Shouting upstairs) {
        //mMusicDirectory_Adapter = this;
        //mContext = context;
        mShouting = upstairs;
        mShoutToCeiling = new Shout(getClass().getSimpleName());
        waitAndLock();
        mAR_MusicDirectory = new ArrayList<>(0);
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

    public MusicDirectory getSelectedMusicDirectory() {
        return mSelectedMusicDirectory;
    }

    public void setSelectedMusicDirectory(MusicDirectory selectedMusicDirectory) {
        mSelectedMusicDirectory = selectedMusicDirectory;
    }

    //public ArrayList<MusicPreference> getAL_MusicPreference() {
//        return mAL_MusicPreference;
//    }

//    public void setDanceForPreference(Dance iDanceForPreference) {
//        mDanceForPreference = iDanceForPreference;
//        if (mDanceForPreference == null) {
//            mAL_MusicPreference = null;
//        } else {
//            SearchPattern tSearchPattern = new SearchPattern(MusicPreference.class);
//            tSearchPattern.addSearch_Criteria(
//                    new SearchCriteria("DANCE_ID",""+ mDanceForPreference.mId));
//
//
//            DataServiceSingleton tDataServiceSingleton = DataServiceSingleton.getInstance();
//            DataService tDataService = tDataServiceSingleton.getDataService();
//            mAL_MusicPreference = tDataService.readArrayList(tSearchPattern);
//            Logg.i(TAG, "found Preferences " + mAL_MusicPreference.size());
//            for (MusicPreference lMusicPreference : mAL_MusicPreference) {
//                Logg.i(TAG, String.format(Locale.ENGLISH,
//                        "dance id %d, MusicDirectory id %d, code %s",
//                        lMusicPreference.getDance().mId,
//                        lMusicPreference.getMusicDirectory().mId,
//                        lMusicPreference.getMusicPreferenceFavourite().getCode()));
//            }
//
//
//
//        }
//    }


    public void setAR(ArrayList<MusicDirectory> iAR_MusicDirectory) {
        waitAndLock();
        mAR_MusicDirectory = iAR_MusicDirectory;
        unlock();
        getItemCount();
        this.notifyDataSetChanged();
    }

    public ArrayList<MusicDirectory> getAR() {
        return mAR_MusicDirectory;
    }


    //</editor-fold>

    //<editor-fold desc="Live cycle">
//    @NonNull
//    @Override
//    // public MusicDirectory_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup iParent, int iViewType) {
    @SuppressWarnings("NullableProblems")
    public MusicDirectory_ViewHolder onCreateViewHolder(@NonNull ViewGroup iParent, int iViewType) {
        LayoutInflater inflater = LayoutInflater.from(iParent.getContext());
        View tView;
        MusicDirectory_ViewHolder tViewHolder;
        tView = inflater.inflate(R.layout.row_musicdirectory, iParent, false);

        tViewHolder = new MusicDirectory_ViewHolder(tView);
        tViewHolder.setMusicDirectory_Adapter(this);
        tView.setOnClickListener(iView -> {
            Logg.k(TAG, "OnClick ");
            toggleSelect(tViewHolder.mMusicDirectory);
        });
        return tViewHolder;
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MusicDirectory_ViewHolder iViewHolder, int iDoNotUsePosition) {
        if (iViewHolder.getAdapterPosition() > mAR_MusicDirectory.size()) {
            return;
        }
        int tPosition = iViewHolder.getAdapterPosition();
        final MusicDirectory tMusicDirectory;
        tMusicDirectory = mAR_MusicDirectory.get(tPosition);
        iViewHolder.setMusicDirectory( tMusicDirectory);

//
//        int tBg_Background = ContextCompat.getColor(iViewHolder.mLayout.getContext(), R.color.bg_list_standard);
//        if (this.mSelectedMusicDirectory != null &&
//                tMusicDirectory.mId == this.mSelectedMusicDirectory.mId) {
//            tBg_Background = ContextCompat.getColor(iViewHolder.mLayout.getContext(), R.color.bg_list_selected);
//        }
//        iViewHolder.mLayout.setBackgroundColor(tBg_Background);
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


    private void toggleSelect(MusicDirectory tMusicDirectory) {
        if (mSelectedMusicDirectory == null) {
            mSelectedMusicDirectory = tMusicDirectory;
            mShoutToCeiling.mLastAction = "select";
        } else if (mSelectedMusicDirectory.mId == tMusicDirectory.mId) {
            mSelectedMusicDirectory = null;
            mShoutToCeiling.mLastAction = "Unselect";
        } else {
            mSelectedMusicDirectory = tMusicDirectory;
            mShoutToCeiling.mLastAction = "select";
        }
        if (mShouting != null) {
            mShouting.shoutUp(mShoutToCeiling);
        }
        this.notifyDataSetChanged();
    }


    @SuppressWarnings("unused")
    private void setSelect(int tPosition) {
        mSelectedMusicDirectory = mAR_MusicDirectory.get(tPosition);
        refresh();
    }


    @SuppressWarnings("unused")
    private void sort() {
        if (mAR_MusicDirectory == null) {
            return;
        }
        if (mAR_MusicDirectory.size() > 1) {
            Collections.sort(mAR_MusicDirectory, (o1, o2) -> {
                        if (o1.mT2.equals(o2.mT2)) {
                            return o1.mT1.compareToIgnoreCase(o2.mT1);
                        } else {
                            return o1.mT2.compareToIgnoreCase(o2.mT1);
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
            } catch (InterruptedException e) {
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
        if (mAR_MusicDirectory == null) {
            tResult = 0;
        } else {
            tResult = mAR_MusicDirectory.size();
        }
        unlock();
        return tResult;
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }
}