package org.pochette.organizer.music;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.pochette.data_library.database_management.DataService;
import org.pochette.data_library.database_management.Refreshable;
import org.pochette.data_library.music.MusicFile;
import org.pochette.data_library.music.MusicPreference;
import org.pochette.data_library.scddb_objects.Dance;
import org.pochette.organizer.R;
import org.pochette.organizer.app.DataServiceSingleton;
import org.pochette.utils_lib.logg.Logg;
import org.pochette.utils_lib.search.SearchCriteria;
import org.pochette.utils_lib.search.SearchPattern;
import org.pochette.utils_lib.shouting.Shout;
import org.pochette.utils_lib.shouting.Shouting;

import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import static java.lang.Thread.sleep;

public class MusicFile_Adapter extends RecyclerView.Adapter<MusicFile_ViewHolder>
        implements Shouting, Refreshable {

    //public class MusicFile_Adapter extends RecyclerView.Adapter<MusicFile_Adapter.ViewHolder>
    //Variables
    @SuppressWarnings("FieldMayBeFinal")
    private static String TAG = "FEHA (MF_Adapter)";

    public final static int MODE_STANDARD = 1;
    public final static int MODE_PREFERENCE = 2;

    private int mMode = MODE_STANDARD;
   // private ArrayList<MusicFile> mAR_MusicFile;
    private Dance mDanceForPreference;
    public Integer mSelectedMusicFileId = 0;
    private Integer[] mA;
   private ArrayList<MusicPreference> mAL_MusicPreference = null;
    // if this adapter is used in connection with music preference an additional array is used


    private boolean mAR_Any_Lock = false; // internal flag, to control workings on the mAR* take place only once at a time.

    FragmentManager mFragmentManager;

    //Timer mAdapterTimer;

    Shouting mShouting;
    Shout mShoutToCeiling;
    Shout mGlassFloor;

    //Constructor

    @SuppressLint("UseSparseArrays")
    public MusicFile_Adapter(Context iContext, RecyclerView iRecyclerView, Shouting upstairs) {
        //mMusicFile_Adapter = this;
        //mContext = context;
        mShouting = upstairs;
        mShoutToCeiling = new Shout(getClass().getSimpleName());
        waitAndLock();
        //mAR_MusicFile = new ArrayList<>(0);
        mA = null;
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

    public MusicFile getSelectedMusicFile() {
        if (mSelectedMusicFileId == 0) {
            return null;
        }
        return MusicFile_Cache.getById(mSelectedMusicFileId);
    }

    @SuppressWarnings("unused")
    public void setSelectedMusicFile(MusicFile  iSelectedMusicFile) {
        if (iSelectedMusicFile == null) {
            mSelectedMusicFileId = 0;
        }
        mSelectedMusicFileId =  iSelectedMusicFile.mId;
    }
    public Dance getDanceForPreference() {
        return mDanceForPreference;
    }

    public ArrayList<MusicPreference> getAL_MusicPreference() {
        return mAL_MusicPreference;
    }

    public void setDanceForPreference(Dance iDanceForPreference) {
        mDanceForPreference = iDanceForPreference;
        if (mDanceForPreference == null) {
            mAL_MusicPreference = null;
        } else {
            SearchPattern tSearchPattern = new SearchPattern(MusicPreference.class);
            tSearchPattern.addSearch_Criteria(
                    new SearchCriteria("DANCE_ID",""+ mDanceForPreference.mId));
            DataServiceSingleton tDataServiceSingleton = DataServiceSingleton.getInstance();
            DataService tDataService = tDataServiceSingleton.getDataService();
            mAL_MusicPreference = tDataService.readArrayList(tSearchPattern);
        }
    }

    public void setA(Integer[] iA) {
        mA = iA;
    }


    public void setFragmentManager(FragmentManager iFragmentManager) {
        mFragmentManager = iFragmentManager;
    }

    //</editor-fold>

    //<editor-fold desc="Live cycle">
//    @NonNull
//    @Override
//    // public MusicFile_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup iParent, int iViewType) {
    @SuppressWarnings("NullableProblems")
    public MusicFile_ViewHolder onCreateViewHolder(@NonNull ViewGroup iParent, int iViewType) {
        LayoutInflater inflater = LayoutInflater.from(iParent.getContext());
        View tView;
        MusicFile_ViewHolder tViewHolder;
        tView = inflater.inflate(R.layout.row_musicfile, iParent, false);

        tViewHolder = new MusicFile_ViewHolder(tView);
        tViewHolder.setMusicFile_Adapter(this);
        tView.setOnClickListener(iView -> {
            Logg.k(TAG, "OnClick ");
            toggleSelect(tViewHolder.mMusicFile);
            if (mSelectedMusicFileId == 0) {
                Logg.i(TAG, " nothing selected");
            } else {
                Logg.i(TAG, MusicFile_Cache.getById( mSelectedMusicFileId).toString());
            }
        });
        return tViewHolder;
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MusicFile_ViewHolder iViewHolder, int iDoNotUsePosition) {
        if (mA == null || iViewHolder.getAdapterPosition() > mA.length) {
            return;
        }
        int tPosition = iViewHolder.getAdapterPosition();
        int tId = mA[tPosition];
        final MusicFile tMusicFile;
        tMusicFile =   MusicFile_Cache.getById(tId);
        if (tMusicFile != null) {
            iViewHolder.setMusicFile( tMusicFile);
        }
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
        //Logg.i(TAG, "save SearchValue"+mSearchString_Name);
    }

    private void retrieveSearchValue() {
    }


    private void toggleSelect(MusicFile tMusicFile) {
        if (mSelectedMusicFileId == 0) {
            mSelectedMusicFileId = tMusicFile.mId;
            mShoutToCeiling.mLastAction = "select";
            mShoutToCeiling.storeObject(tMusicFile);
        } else if (mSelectedMusicFileId == tMusicFile.mId) {
            mSelectedMusicFileId = 0;
            mShoutToCeiling.mLastAction = "unselect";
            mShoutToCeiling.removeObject();
        } else {
            mSelectedMusicFileId = tMusicFile.mId;
            mShoutToCeiling.mLastAction = "select";
            mShoutToCeiling.storeObject(tMusicFile);
        }
        if (mShouting != null) {
            mShoutToCeiling.mLastObject = "MusicFile";
            mShouting.shoutUp(mShoutToCeiling);
        }
        this.notifyDataSetChanged();
    }


    @SuppressWarnings("unused")
    private void setSelect(int tPosition) {
        if (mA != null && mA.length > 0) {
            mSelectedMusicFileId = mA[tPosition];
        } else {
            mSelectedMusicFileId =0;
        }
        refresh();
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
        if (mA != null && mA.length >0) {
            Logg.i(TAG, "size" + mA.length);
        }
        new Handler(Looper.getMainLooper()).post(this::notifyDataSetChanged);
    }

    @Override
    public void shoutUp(Shout tShoutToCeiling) {
        Logg.i(TAG, "Shout:\n" + tShoutToCeiling.toString());
        if (tShoutToCeiling.mJsonString != null) {
            Logg.i(TAG, tShoutToCeiling.mJsonString);
        }
        mGlassFloor = tShoutToCeiling;
        process_shouting();
    }

    @Override
    public int getItemCount() {
        int tResult;
        //  Logg.i(TAG, "start itemCoun");
        waitAndLock();
        if (mA == null) {
            tResult = 0;
        } else {
            tResult = mA.length;
        }
        unlock();
        //       Logg.i(TAG, "GetItemCount: " + tResult);
        return tResult;
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }
}