package org.pochette.organizer.pairing;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.pochette.data_library.database_management.Refreshable;
import org.pochette.data_library.pairing.MusicDirectory_Pairing;
import org.pochette.organizer.R;
import org.pochette.utils_lib.logg.Logg;
import org.pochette.utils_lib.shouting.Shout;
import org.pochette.utils_lib.shouting.Shouting;

import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

/**
 *
 */
public class MusicDirectory_Pairing_Adapter extends RecyclerView.Adapter<MusicDirectory_Pairing_ViewHolder>
        implements Shouting, Refreshable {

    //Variables
    private static final String TAG = "FEHA (MDP_Adapter)";
    public ArrayList<MusicDirectory_Pairing> mAL_MusicDirectory_Pairing;

    RecyclerView mRecyclerView;

    private boolean mAL_Any_Lock = false; // internal flag, to control workings on the mAR* take place only once at a time.

    Shouting mShouting;
    Shout mShoutToCeiling;
    Shout mGlassFloor;

    //Constructor

    @SuppressLint("UseSparseArrays")
    public MusicDirectory_Pairing_Adapter(Context iContext, RecyclerView iRecyclerView, Shouting iUpstairs) {
        mShouting = iUpstairs;
        mShoutToCeiling = new Shout(getClass().getSimpleName());
        waitAndLock();
        mAL_MusicDirectory_Pairing = new ArrayList<>(0);
        unlock();
        mRecyclerView = iRecyclerView;
        DividerItemDecoration tDividerItemDecoration = new DividerItemDecoration(iContext, DividerItemDecoration.VERTICAL);
        tDividerItemDecoration.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(iContext, R.drawable.divider)));
        mRecyclerView.addItemDecoration(tDividerItemDecoration);
    }

    // when Fragment onStop, so we can save sharedpref
    public void onStop() {

    }
    //Setter and Getter

    //Live Cycile
    //<editor-fold desc="Live cycle">
    @NonNull
    @Override
    // public MusicDirectory_Pairing_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup iParent, int iViewType) {
    public MusicDirectory_Pairing_ViewHolder onCreateViewHolder(@NonNull ViewGroup iParent, int iViewType) {
        LayoutInflater inflater = LayoutInflater.from(iParent.getContext());
        View tView;
        MusicDirectory_Pairing_ViewHolder tViewHolder;
        tView = inflater.inflate(R.layout.row_musicdirectory_pairing, iParent, false);
        tViewHolder = new MusicDirectory_Pairing_ViewHolder(this, tView);
        return tViewHolder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MusicDirectory_Pairing_ViewHolder iMusicDirectory_Pairing_VierHolder, int iDoNotUsePosition) {
        if (iMusicDirectory_Pairing_VierHolder.getAdapterPosition() > mAL_MusicDirectory_Pairing.size()) {
            return;
        }
        int tPosition = iMusicDirectory_Pairing_VierHolder.getAdapterPosition();
        final MusicDirectory_Pairing tMusicDirectory_Pairing;
        tMusicDirectory_Pairing = mAL_MusicDirectory_Pairing.get(tPosition);
        iMusicDirectory_Pairing_VierHolder.setMusicDirectory_Pairing(tMusicDirectory_Pairing);

    }
    //</editor-fold>
    //Static Methods

    //Internal Organs


//    private void sort() {
//        Logg.i(TAG, "sort");
//        if (mAL_MusicDirectory_Pairing == null) {
//            return;
//        }
//        if (mAL_MusicDirectory_Pairing.size() > 1) {
//            Collections.sort(mAL_MusicDirectory_Pairing, (o1, o2) -> {
//                        int tResult = 0;
//                        if (o1.getPairing().getPairingStatus() == PairingStatus.CONFIRMED &&
//                                (o2.getPairing().getPairingStatus() != PairingStatus.CONFIRMED)) {
//                            tResult = 2;
//                        } else if (o2.getPairing().getPairingStatus() == PairingStatus.CONFIRMED &&
//                                (o1.getPairing().getPairingStatus() != PairingStatus.CONFIRMED)) {
//                            tResult = -2;
//                        } else if (o1.getPairing().getPairingStatus() == PairingStatus.CANDIDATE &&
//                                (o2.getPairing().getPairingStatus() != PairingStatus.CANDIDATE)) {
//                            tResult = -2;
//                        } else if (o2.getPairing().getPairingStatus() == PairingStatus.CANDIDATE &&
//                                (o1.getPairing().getPairingStatus() != PairingStatus.CANDIDATE)) {
//                            tResult = 2;
//                        }
//                        if (tResult == 0) {
//                            if (o1.getMusicDirectory().mT2.equals(o2.getMusicDirectory().mT2)) {
//                                tResult = o1.getMusicDirectory().mT1.compareToIgnoreCase(o2.getMusicDirectory().mT1);
//                            } else {
//                                tResult = o1.getMusicDirectory().mT2.compareToIgnoreCase(o2.getMusicDirectory().mT2);
//                            }
//                        }
//                        return tResult;
//                    }
//            );
//        }
//    }

    void process_shouting() {
        //        switch (mGlassFloor.mActor) {
//            case "DialogFragment_EditClassification":
//                if (mGlassFloor.mLastObject.equals("DataEntry") &&
//                        mGlassFloor.mLastAction.equals("confirm")) {
//                    @SuppressWarnings("unused") String tCode;
//                    Logg.i(TAG, "New classification" + mGlassFloor.mJsonString);
//                    try {
//                        String tJsonString = mGlassFloor.mJsonString;
//                        JSONObject tJsonObject = new JSONObject(tJsonString);
//                        //noinspection UnusedAssignment
//                        tCode = tJsonObject.getString("Favourite");
//
//                    } catch(JSONException e) {
//                        Logg.e(TAG, e.toString());
//                    }
//                }
//                break;
//        }
    }

    // Dead simple low weight locking of the array
    @SuppressWarnings("BusyWait")
    private void waitAndLock() {
        int tCount = 0;
        while (mAL_Any_Lock) {
            tCount++;
            try {
                Thread.sleep(10);
            } catch(InterruptedException e) {
                Logg.w(TAG, e.toString());
            }
            if ((tCount % 100) == 0) {
                Logg.w(TAG, "waitAndLock: " + tCount);
            }
        }
        // as it become available wait is over and lock starts
        mAL_Any_Lock = true;
        //mAL_Any_Lock_Source = Log.getStackTraceString(new Exception());
    }

    private void unlock() {
        mAL_Any_Lock = false;
    }
//
//    void scrollOneDown(int iPosition) {
//        int tBottom = mRecyclerView.getAdapter().getItemCount()-1;
//        int tNewPosition;
//        tNewPosition = Math.max(0, Math.min( iPosition +2, tBottom));
//        mRecyclerView.scrollToPosition(tNewPosition);
//    }



    //Interface
    public void refresh() {
        if (mAL_MusicDirectory_Pairing != null) {
            if (mRecyclerView.getVisibility() != View.VISIBLE) {
                return;
            }
        //    sort();
        }
        new Handler(Looper.getMainLooper()).post(this::notifyDataSetChanged);
    }

    @Override
    public void shoutUp(Shout tShoutToCeiling) {
        Logg.i(TAG,  tShoutToCeiling.toString());
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
        if (mAL_MusicDirectory_Pairing == null) {
            tResult = 0;
        } else {
            tResult = mAL_MusicDirectory_Pairing.size();
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