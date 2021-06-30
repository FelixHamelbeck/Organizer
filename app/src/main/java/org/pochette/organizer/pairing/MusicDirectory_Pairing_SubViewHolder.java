package org.pochette.organizer.pairing;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.pochette.data_library.music.MusicFile;
import org.pochette.data_library.pairing.Pairing;
import org.pochette.data_library.scddb_objects.Album;
import org.pochette.organizer.R;
import org.pochette.organizer.gui_assist.CustomSpinnerItem;
import org.pochette.organizer.gui_assist.SpinnerItemFactory;
import org.pochette.utils_lib.logg.Logg;
import org.pochette.utils_lib.shouting.Shout;
import org.pochette.utils_lib.shouting.Shouting;

import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

class MusicDirectory_Pairing_SubViewHolder extends RecyclerView.ViewHolder implements Shouting {

    private final String TAG = "FEHA (MDPS_ViewHolder)";
    //Variables

    private Pairing mPairing;
    private Album mAlbum;
    public View mLayout;

    private final TextView mTV_Artist;
    private final TextView mTV_Album;
    private final TextView mTV_Score;
    private final TextView mTV_Count;
    private final ImageView mIV_PairingStatus;
    private final ImageView mIV_Pairing;
    private final ImageView mIV_Caret;
    private final RecyclerView mRV_Tracks;

    private boolean mFlagShowTracks = false;

    SpinnerItemFactory mSpinnerItemFactory;

    MusicfileRecording_Adapter mMusicFileRecording_Adapter;

    Shout mGlassFloor;
    Shouting mShouting;


    //Constructor

    @SuppressWarnings("unused")
    public MusicDirectory_Pairing_SubViewHolder(
            MusicDirectory_Pairing_SubAdapter iMusicDirectory_Pairing_SubAdapter, View iView) {
        super(iView);
        mLayout = iView;
        mSpinnerItemFactory = new SpinnerItemFactory();

        mTV_Artist = mLayout.findViewById(R.id.TV_RowMusicDirectoryPairingSub_Artist);
        mTV_Album = mLayout.findViewById(R.id.TV_RowMusicDirectoryPairingSub_Album);
        mTV_Score = mLayout.findViewById(R.id.TV_RowMusicDirectoryPairingSub_Score);
        mTV_Count = mLayout.findViewById(R.id.TV_RowMusicDirectoryPairingSub_Count);
        mIV_PairingStatus = mLayout.findViewById(R.id.IV_RowMusicDirectoryPairingSub_PairingStatus);
        mIV_Pairing = mLayout.findViewById(R.id.IV_RowMusicDirectoryPairingSub_Pairing);
        mIV_Caret = mLayout.findViewById(R.id.IV_RowMusicDirectoryPairingSub_Caret);
        mRV_Tracks = mLayout.findViewById(R.id.RV_RowMusicDirectoryPairingSub_Track);

        setListener();
    }

    //Setter and Getter
    void setData(Pairing iPairing, Album iAlbum) {
        mPairing = iPairing;
        mAlbum = iAlbum;
        refreshView();
    }

    public void setShouting(Shouting shouting) {
        mShouting = shouting;
    }


    //Livecycle
    //Static Methods
    //Internal Organs


    private void refreshView() {

        if (mTV_Artist != null) {
            mTV_Artist.setText(mAlbum.mArtistName);
        }
        if (mTV_Score != null) {
            Logg.i(TAG, " " + mPairing.getScore());
            mTV_Score.setText(String.format(Locale.ENGLISH, "%d %%", (int) (100* mPairing.getScore())));
        }

        if (mTV_Album != null) {
            mTV_Album.setText(String.format("%s", mAlbum.mName));
            //mTV_Album.setText(mAlbum.mName);
        }
        if (mTV_Count != null) {
            mTV_Count.setText(String.format(Locale.ENGLISH, "%d", mAlbum.mCountRecording));
        }
        if (mIV_PairingStatus != null) {
            CustomSpinnerItem tCustomSpinnerItem =
                    mSpinnerItemFactory.getSpinnerItem(SpinnerItemFactory.FIELD_PAIRING_STATUS,
                            mPairing.getPairingStatus().getCode());
            mIV_PairingStatus.setImageResource(tCustomSpinnerItem.mImageResource);
        }
        if (mIV_Pairing != null) {
            mIV_Pairing.setVisibility(View.INVISIBLE);
        }
        if (mIV_Caret != null) {
            if (mFlagShowTracks) {
                mIV_Caret.setImageResource(R.drawable.ic_caret_up);
            } else {
                mIV_Caret.setImageResource(R.drawable.ic_caret_down);
            }
        }

        if (mRV_Tracks != null) {
            if (mFlagShowTracks) {
                mRV_Tracks.setVisibility(View.VISIBLE);
                if (mMusicFileRecording_Adapter == null) {
                    mMusicFileRecording_Adapter = new MusicfileRecording_Adapter(mLayout.getContext(), mRV_Tracks);
                    RecyclerView.LayoutManager tLayoutManager = new LinearLayoutManager(mLayout.getContext());
                    mRV_Tracks.setLayoutManager(tLayoutManager);
                    mRV_Tracks.setAdapter(mMusicFileRecording_Adapter);
                }


                int tId = mPairing.getLdb_Id();

                mMusicFileRecording_Adapter.mAL_MusicFile = MusicFile.getByDirectoryId(tId);
                Logg.i(TAG, "MF " + mMusicFileRecording_Adapter.mAL_MusicFile.size());
                mMusicFileRecording_Adapter.mAL_Recording = mAlbum.getAL_Recording();
                Logg.i(TAG, "REC " + mMusicFileRecording_Adapter.mAL_Recording.size());
                mMusicFileRecording_Adapter.sortData();
                mMusicFileRecording_Adapter.refresh();

            } else {
                mRV_Tracks.setVisibility(View.GONE);
            }
        }


    }

    private void setListener() {
        if (mIV_Caret != null) {
            mIV_Caret.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mFlagShowTracks = !mFlagShowTracks;
                    Logg.k(TAG, "Caret: " + mFlagShowTracks);
                    refreshView();
                }
            });
        }
        if (mIV_PairingStatus != null) {
            mIV_PairingStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View iView) {

                    Logg.k(TAG, "PairingStatus");
                    int[] tLocation = new int[2];
                    iView.getLocationOnScreen(tLocation);
                    startEditPairingStatus(tLocation);
                }
            });
        }
    }

    private void startEditPairingStatus(int[] iLocation) {
        String tCode = mPairing.getPairingStatus().getCode();
        DialogFragment_EditPairingStatus tDialogFragment_EditPairingStatus =
                DialogFragment_EditPairingStatus.newInstance(tCode);
        tDialogFragment_EditPairingStatus.setShouting(this);
        tDialogFragment_EditPairingStatus.setLocation(iLocation);
        AppCompatActivity tActivity = ((AppCompatActivity) mLayout.getContext());
        FragmentManager tFragmentManager = tActivity.getSupportFragmentManager();
        tDialogFragment_EditPairingStatus.show(tFragmentManager, null);
    }

    private void process_shouting() {
        if (mGlassFloor.mActor.equals("DialogFragment_EditPairingStatus")) {
            if (mGlassFloor.mLastObject.equals("DataEntry") &&
                    mGlassFloor.mLastAction.equals("confirm")) {
                if (mShouting != null) {
                    mGlassFloor.storeObject(mPairing);
                    mShouting.shoutUp(mGlassFloor);
                }
            }
        }
    }


    //Interface
    @Override
    public void shoutUp(Shout tShoutToCeiling) {
        Logg.i(TAG, tShoutToCeiling.toString());
        mGlassFloor = tShoutToCeiling;
        process_shouting();
    }

}
