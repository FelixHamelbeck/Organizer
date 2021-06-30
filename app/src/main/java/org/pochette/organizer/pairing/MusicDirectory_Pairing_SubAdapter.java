package org.pochette.organizer.pairing;

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
import org.pochette.data_library.music.MusicDirectory;
import org.pochette.data_library.music.MusicDirectoryPurpose;
import org.pochette.data_library.pairing.Pairing;
import org.pochette.data_library.pairing.PairingDB;
import org.pochette.data_library.pairing.PairingStatus;
import org.pochette.data_library.scddb_objects.Album;
import org.pochette.organizer.R;
import org.pochette.utils_lib.logg.Logg;
import org.pochette.utils_lib.shouting.Shout;
import org.pochette.utils_lib.shouting.Shouting;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 *
 */
public class MusicDirectory_Pairing_SubAdapter extends RecyclerView.Adapter<MusicDirectory_Pairing_SubViewHolder>
        implements Shouting, Refreshable {

    //Variables
    private static final String TAG = "FEHA (MDP_SubAdapter)";
    private MusicDirectory mMusicDirectory;
    public ArrayList<Pairing> mAL_Pairing;
    public ArrayList<Album> mAL_Album;

    RecyclerView mRecyclerView;

    private boolean mLock = false; // internal flag, to control workings on the mAR* take place only once at a time.

    Shouting mShouting;
    Shout mShoutToCeiling;
    Shout mGlassFloor;

    //Constructor

    @SuppressWarnings("unused")
    public MusicDirectory_Pairing_SubAdapter(Context iContext, RecyclerView iRecyclerView, Shouting iUpstairs) {
        mShouting = iUpstairs;
        mShoutToCeiling = new Shout(getClass().getSimpleName());
        waitAndLock();
        mMusicDirectory = null;
        mAL_Album = new ArrayList<>(0);
        mAL_Pairing = new ArrayList<>(0);
        unlock();
        mRecyclerView = iRecyclerView;
//        DividerItemDecoration tDividerItemDecoration = new DividerItemDecoration(iContext, DividerItemDecoration.VERTICAL);
//        tDividerItemDecoration.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(iContext, R.drawable.divider)));
//        mRecyclerView.addItemDecoration(tDividerItemDecoration);

    }

    // when Fragment onStop, so we can save sharedpref
    public void onStop() {

    }
    //Setter and Getter

    public void setData(ArrayList<Pairing> iAL_Pairing,
                        ArrayList<Album> iAL_Album,
                        MusicDirectory iMusicDirectory) {
        if (iAL_Album == null || iAL_Pairing == null || iAL_Album.size() != iAL_Pairing.size()) {
            throw new IllegalArgumentException("inconsistent data");
        }
        mMusicDirectory = iMusicDirectory;
        ArrayList<Pairing> tAL_Pairing = new ArrayList<>(0);
        ArrayList<Album> tAL_Album = new ArrayList<>(0);

        // defined hashmap based on album_id

        Comparator<Pairing> tComparatorByScore = Pairing.getComparator();
        TreeMap<Pairing, Album> tTM_PA = new TreeMap<>(tComparatorByScore);

        for (int i = 0; i < iAL_Album.size(); i++) {
            tTM_PA.put(iAL_Pairing.get(i), iAL_Album.get(i));
        }

        for (Map.Entry<  Pairing,Album> lEntry : tTM_PA.entrySet()) {
            Logg.i(TAG, String.format(Locale.ENGLISH, "PA: %f %s %s",
                    lEntry.getKey().getScore(),lEntry.getKey().getPairingSource().getCode(), lEntry.getValue().mName));
            tAL_Album.add(lEntry.getValue());
            tAL_Pairing.add(lEntry.getKey());
        }
        mAL_Album = tAL_Album;
        mAL_Pairing = tAL_Pairing;
        notifyDataSetChanged();
    }

    //Live Cycile
    //<editor-fold desc="Live cycle">
    @NonNull
    @Override
    // public MusicDirectory_Pairing_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup iParent, int iViewType) {
    public MusicDirectory_Pairing_SubViewHolder onCreateViewHolder(@NonNull ViewGroup iParent, int iViewType) {
        LayoutInflater inflater = LayoutInflater.from(iParent.getContext());
        View tView;
        MusicDirectory_Pairing_SubViewHolder tViewHolder;
        tView = inflater.inflate(R.layout.row_musicdirectory_pairing_sub, iParent, false);
        tViewHolder = new MusicDirectory_Pairing_SubViewHolder(this, tView);
        tViewHolder.setShouting(this);
        return tViewHolder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(
            @NonNull MusicDirectory_Pairing_SubViewHolder iMusicDirectory_Pairing_SubViewHolder,
            int iDoNotUsePosition) {
        if (iMusicDirectory_Pairing_SubViewHolder.getAdapterPosition() > mAL_Pairing.size()) {
            return;
        }
        int tPosition = iMusicDirectory_Pairing_SubViewHolder.getAdapterPosition();
        final Pairing fPairing;
        fPairing = mAL_Pairing.get(tPosition);
        final Album fAlbum;
        fAlbum = mAL_Album.get(tPosition);
        iMusicDirectory_Pairing_SubViewHolder.setData(fPairing, fAlbum);
        //  iMusicDirectory_Pairing_SubViewHolder.switchOffDetail();
    }
    //</editor-fold>
    //Static Methods

    //Internal Organs

    /**
     * if the status of one pairing is upgraded to confirmed all other confirmed are downgraded to candidate
     *
     * @param iPairing of which the new status is to be  stored
     * @param iCode    the code of the PairingStatus
     */

    void processNewStatus(Pairing iPairing, String iCode) {
        PairingStatus tNewPairingStatus = PairingStatus.fromCode(iCode);
        PairingStatus tOldPairingStatus = iPairing.getPairingStatus();
        boolean tDataChanged = false;

        // take care of all the others pairing for this directory
        if (tNewPairingStatus == PairingStatus.CONFIRMED) {
            for (Pairing lPairing : mAL_Pairing) {
                // set all the other to rejected
                if (lPairing.getScddb_Id() != iPairing.getScddb_Id()) {
                    lPairing.setPairingStatus(PairingStatus.REJECTED);
                    // previously only the confiremd were move to candidates
//                    if (lPairing.getPairingStatus() == PairingStatus.CONFIRMED &&
//                            lPairing.getScddb_Id() != iPairing.getScddb_Id()) {
//                        lPairing.setPairingStatus(PairingStatus.CANDIDATE);
                    lPairing.save();
                    synchronizeMusicDirectory(lPairing.getLdb_Id());
                    tDataChanged = true;
                }
            }
            // if new status is confirmed and it is different from old

        }
        // take care of the iPairing
        if (tOldPairingStatus != tNewPairingStatus) {
            iPairing.setPairingStatus(tNewPairingStatus);
            iPairing.save();
            tDataChanged = true;
            if (tOldPairingStatus == PairingStatus.CONFIRMED) {
                synchronizeMusicDirectory(mMusicDirectory.mId);
            } else if (tNewPairingStatus == PairingStatus.CONFIRMED) {
                mMusicDirectory.mMusicDirectoryPurpose = MusicDirectoryPurpose.SCD;
                mMusicDirectory.save();
                synchronizeMusicDirectory(mMusicDirectory.mId);
            }
        }
        if (tDataChanged) {
            notifyDataSetChanged();
            if (mShouting != null) {
                Shout tShout = new Shout(this.getClass().getSimpleName());
                tShout.mLastObject = "Data";
                tShout.mLastAction = "changed";
                mShouting.shoutUp(tShout);
            }
        }
    }

    void synchronizeMusicDirectory(Integer iMusicDirecoryId) {
        Runnable tRunnable = new Runnable() {
            @Override
            public void run() {
                Logg.i(TAG, "Start pairing DB");
                PairingDB tPairingDB = new PairingDB();
                tPairingDB.updateMusicDirectory(iMusicDirecoryId);
                tPairingDB.updateMusicFile(iMusicDirecoryId);
                Logg.i(TAG, "finish pairing DB");
            }
        };
        Thread tThread = new Thread(tRunnable);
        tThread.start();
    }


    void process_shouting() {
        if (mGlassFloor.mActor.equals("DialogFragment_EditPairingStatus")) {
            if (mGlassFloor.mLastObject.equals("DataEntry") &&
                    mGlassFloor.mLastAction.equals("confirm")) {
                String tCode = null;
                try {
                    JSONObject tJsonObject = new JSONObject(mGlassFloor.mJsonString);
                    tCode = tJsonObject.getString("Status");
                } catch(JSONException e) {
                    Logg.w(TAG, e.toString());
                }
                Pairing tPairing = null;
                try {
                    tPairing = mGlassFloor.returnObject();
                } catch(Exception e) {
                    Logg.w(TAG, e.toString());
                }
                if (tCode != null && tPairing != null) {
                    processNewStatus(tPairing, tCode);
                }
            }
        }
    }

    // Dead simple low weight locking of the array
    @SuppressWarnings("BusyWait")
    private void waitAndLock() {
        int tCount = 0;
        while (mLock) {
            tCount++;
            try {
                Thread.sleep(10);
            } catch(InterruptedException e) {
                Logg.e(TAG, e.toString());
            }
            if ((tCount % 100) == 0) {
                Logg.w(TAG, "waitAndLock: " + tCount);

            }
        }
        // as it become available wait is over and lock starts
        mLock = true;
        //mAL_Any_Lock_Source = Log.getStackTraceString(new Exception());
    }

    private void unlock() {
        mLock = false;
    }

//    void scrollOneDown(int iPosition) {
//        int tBottom = mRecyclerView.getAdapter().getItemCount() - 1;
//        int tNewPosition;
//        tNewPosition = Math.max(0, Math.min(iPosition + 2, tBottom));
//        mRecyclerView.scrollToPosition(tNewPosition);
//    }


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
        if (mAL_Pairing == null) {
            tResult = 0;
        } else {
            tResult = mAL_Pairing.size();
        }
        unlock();
        return tResult;
    }

    @Override
    public long getItemId(int position) {
//		return 		mAL_MusicDirectory_Pairing.get(position).mId
        return super.getItemId(position);
    }
}