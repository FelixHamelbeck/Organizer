package org.pochette.organizer.pairing;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.pochette.data_library.music.MusicDirectory;
import org.pochette.data_library.music.MusicDirectoryPurpose;
import org.pochette.data_library.music.MusicFile;
import org.pochette.data_library.music.MusicFilePurpose;
import org.pochette.data_library.pairing.MusicDirectory_Pairing;
import org.pochette.data_library.pairing.Pairing;
import org.pochette.data_library.pairing.PairingObject;
import org.pochette.data_library.pairing.PairingSource;
import org.pochette.data_library.pairing.PairingStatus;
import org.pochette.data_library.pairing.Signature;
import org.pochette.data_library.scddb_objects.Album;
import org.pochette.organizer.R;
import org.pochette.organizer.album.DialogFragment_Album;
import org.pochette.organizer.gui_assist.CustomSpinnerItem;
import org.pochette.organizer.gui_assist.SpinnerItemFactory;
import org.pochette.organizer.music.DialogFragment_EditPurpose;
import org.pochette.utils_lib.logg.Logg;
import org.pochette.utils_lib.shouting.Shout;
import org.pochette.utils_lib.shouting.Shouting;

import java.util.Locale;
import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

class MusicDirectory_Pairing_ViewHolder extends RecyclerView.ViewHolder implements Shouting {

    private final String TAG = "FEHA (MDP_ViewHolder)";
    //Variables

    private MusicDirectory_Pairing mMusicDirectory_Pairing;
    private boolean mFlagSurelyNotScd;
    private boolean mFlagContainsPairing;
    private boolean mFlagExpanded;


    public View mLayout;

    private final TextView mTV_Artist;
    private final TextView mTV_Album;
    private final TextView mTV_Count;
    private final ImageView mIV_Purpose;
    private final ImageView mIV_Pairing;
    private final ImageView mIV_Caret;
    private final RecyclerView mRV_Sub;

    MusicDirectory_Pairing_SubAdapter mMusicDirectory_Pairing_SubAdapter;

    SpinnerItemFactory mSpinnerItemFactory;
    Shout mGlassFloor;


    //Constructor

    public MusicDirectory_Pairing_ViewHolder(
            @SuppressWarnings("unused") MusicDirectory_Pairing_Adapter iMusicDirectory_Pairing_Adapter, View iView) {
        super(iView);

        mFlagSurelyNotScd = false;
        mFlagContainsPairing = false;
        mFlagExpanded = false;

        mSpinnerItemFactory = new SpinnerItemFactory();
        mLayout = iView;

        mTV_Artist = mLayout.findViewById(R.id.TV_RowMusicDirectoryPairing_Artist);
        mTV_Album = mLayout.findViewById(R.id.TV_RowMusicDirectoryPairing_Album);
        mTV_Count = mLayout.findViewById(R.id.TV_RowMusicDirectoryPairing_Count);

        mIV_Purpose = mLayout.findViewById(R.id.IV_RowMusicDirectoryPairing_Purpose);
        mIV_Pairing = mLayout.findViewById(R.id.IV_RowMusicDirectoryPairing_Pairing);
        mIV_Caret = mLayout.findViewById(R.id.IV_RowMusicDirectoryPairing_Caret);

        mRV_Sub = mLayout.findViewById(R.id.RV_RowMusicDirectoryPairing);

        setupListener();


    }

    //Setter and Getter
    void setMusicDirectory_Pairing(MusicDirectory_Pairing iMusicDirectory_Pairing) {
        mMusicDirectory_Pairing = iMusicDirectory_Pairing;
        mFlagSurelyNotScd = false;
        mFlagContainsPairing = false;
        mFlagExpanded = false;
        MusicDirectory tMusicDirectory = mMusicDirectory_Pairing.getMusicDirectory();
        if (tMusicDirectory.mMusicDirectoryPurpose != MusicDirectoryPurpose.SCD &&
                tMusicDirectory.mMusicDirectoryPurpose != MusicDirectoryPurpose.UNKNOWN) {
            mFlagSurelyNotScd = true;
        } else {
            if (mMusicDirectory_Pairing.getAL_Pairing().size() > 0) {
                mFlagContainsPairing = true;
                mFlagExpanded = true;
            }
        }
        refreshView();
    }

    //Livecycle
    //Static Methods
    //Internal Organs


    void setupListener() {
        if (mIV_Purpose != null) {
            mIV_Purpose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View iView) {
                    Logg.k(TAG, "onClick Purpose");
                    int[] tLocation = new int[2];
                    iView.getLocationOnScreen(tLocation);
                    startEditPurpose(tLocation);
                }
            });
        }
        if (mIV_Pairing != null) {
            mIV_Pairing.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Logg.k(TAG, "onClick Pairing");
                    startNewPairing();
                }
            });
        }
        if (mIV_Caret != null) {
            mIV_Caret.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mFlagExpanded = !mFlagExpanded;
                    Logg.k(TAG, "onClick Caret" + mFlagExpanded);
                    refreshView();
                }
            });
        }
    }

    void setupSub() {
        if (mFlagExpanded) {
            mRV_Sub.setVisibility(View.VISIBLE);
            if (mMusicDirectory_Pairing_SubAdapter == null) {
                mMusicDirectory_Pairing_SubAdapter =
                        new MusicDirectory_Pairing_SubAdapter(mLayout.getContext(), mRV_Sub, this);
            }
            mMusicDirectory_Pairing_SubAdapter.setData(
                    mMusicDirectory_Pairing.getAL_Pairing(),
                    mMusicDirectory_Pairing.getAL_ALbum(),
                    mMusicDirectory_Pairing.getMusicDirectory());
            RecyclerView.LayoutManager LM_MusicDirectory_Pairing =
                    new LinearLayoutManager(mLayout.getContext());
            mRV_Sub.setLayoutManager(LM_MusicDirectory_Pairing);
            mRV_Sub.setAdapter(mMusicDirectory_Pairing_SubAdapter);
        } else {
            mRV_Sub.setVisibility(View.GONE);
        }
    }


    private void refreshView() {
        MusicDirectory tMusicDirectory = mMusicDirectory_Pairing.getMusicDirectory();
        if (mTV_Artist != null) {
            mTV_Artist.setText(tMusicDirectory.mT2);
        }
        if (mTV_Album != null) {
            mTV_Album.setText(tMusicDirectory.mT1);
        }
        if (mTV_Count != null) {
            mTV_Count.setText(String.format(Locale.ENGLISH, "%d", tMusicDirectory.mCountTracks));
        }
        if (mIV_Purpose != null) {
            String tCode = tMusicDirectory.mMusicDirectoryPurpose.getCode();
            CustomSpinnerItem tCustomSpinnerItem =
                    mSpinnerItemFactory.getSpinnerItem(
                            SpinnerItemFactory.FIELD_MUSICDIRECTORY_PURPOSE,
                            tCode);
            mIV_Purpose.setImageResource(tCustomSpinnerItem.mImageResource);
        }
        if (mIV_Pairing != null) {
            if (mFlagSurelyNotScd) {
                mIV_Pairing.setVisibility(View.INVISIBLE);
            } else {
                mIV_Pairing.setVisibility(View.VISIBLE);
            }
        }
        if (mIV_Caret != null) {
            if (mFlagContainsPairing && ! mFlagSurelyNotScd ) {
                int tResourceId;
                if (mFlagExpanded) {
                    tResourceId = R.drawable.ic_caret_up;
                } else {
                    tResourceId = R.drawable.ic_caret_down;
                }
                mIV_Caret.setVisibility(View.VISIBLE);
                mIV_Caret.setImageResource(tResourceId);
            } else {
                mIV_Caret.setVisibility(View.INVISIBLE);
            }
        }
        setupSub();
    }
//


    private void startEditPurpose(int[] iLocation) {
        String tCode = mMusicDirectory_Pairing.getMusicDirectory().mMusicDirectoryPurpose.getCode();
        @SuppressWarnings("AccessStaticViaInstance") DialogFragment_EditPurpose tDialogFragment_EditPurpose =
                DialogFragment_EditPurpose.newInstance(tCode).newInstance(null);
        tDialogFragment_EditPurpose.setShouting(this);
        tDialogFragment_EditPurpose.setLocation(iLocation);
        AppCompatActivity tActivity = ((AppCompatActivity) mLayout.getContext());
        FragmentManager tFragmentManager = tActivity.getSupportFragmentManager();
        tDialogFragment_EditPurpose.show(tFragmentManager, null);
    }


    void process_shouting() {
        if (mGlassFloor.mActor.equals("DialogFragment_Album")) {
            if (mGlassFloor.mLastObject.equals("Choice") &&
                    mGlassFloor.mLastAction.equals("confirmed")) {

                try {
                    Album tAlbum = mGlassFloor.returnObject();
                    Logg.i(TAG, "call create new Pairing");
                    createNewPairing(tAlbum);
                } catch(Exception e) {
                    Logg.w(TAG, e.toString());
                }
            }
        }

        if (mGlassFloor.mActor.equals("DialogFragment_EditPurpose")) {
            if (mGlassFloor.mLastObject.equals("DataEntry") &&
                    mGlassFloor.mLastAction.equals("confirm")) {
                String tCode = null;
                try {
                    JSONObject tJsonObject = new JSONObject(mGlassFloor.mJsonString);
                    tCode = tJsonObject.getString("Purpose");
                } catch(JSONException e) {
                    Logg.w(TAG, e.toString());
                }
                processPurpose(tCode);

            }
        }

        if (mGlassFloor.mActor.equals("MusicDirectory_Pairing_SubAdapter")) {
            if (mGlassFloor.mLastObject.equals("Data") &&
                    mGlassFloor.mLastAction.equals("changed")) {
                refreshView();
            }
        }
    }

    void processPurpose(String iCode) {
        if (iCode != null && !iCode.isEmpty()) {
            MusicDirectoryPurpose tMusicDirectoryPurpose = MusicDirectoryPurpose.fromCode(iCode);
            mMusicDirectory_Pairing.getMusicDirectory().mMusicDirectoryPurpose =
                    tMusicDirectoryPurpose;
            Logg.i(TAG, Objects.requireNonNull(tMusicDirectoryPurpose).getText());
            Runnable tRunnable = new Runnable() {
                @Override
                public void run() {
                    mMusicDirectory_Pairing.getMusicDirectory().save();
                    for (MusicFile lMusicFile :
                            mMusicDirectory_Pairing.getMusicDirectory().getAL_MusicFile()) {
                        if (lMusicFile.mPurpose == MusicFilePurpose.UNKNOWN) {
                            lMusicFile.mPurpose = MusicFilePurpose.fromCode(iCode);
                            lMusicFile.save();
                        }
                    }
                }
            };
            new Thread(tRunnable).start();
            refreshView();
        } else {
            Logg.w(TAG, "processPurpose: Code should be meaningful");
        }
    }


    void createNewPairing(Album iAlbum) {
        MusicDirectory tMusicDirectory = mMusicDirectory_Pairing.getMusicDirectory();
        float tScore ;
        Signature tMD_Signature = new Signature(tMusicDirectory.mSignature);
        Signature tAM_Signature = new Signature(iAlbum.mSignature);
        tScore = tMD_Signature.compare(tAM_Signature);
        Pairing tPairing = new Pairing(PairingObject.MUSIC_DIRECTORY,
                iAlbum.mId, tMusicDirectory.mId,
                PairingSource.MANUAL, PairingStatus.CANDIDATE, null, tScore);
        tPairing.save();
        MusicDirectory_Pairing tMDP = new MusicDirectory_Pairing(mMusicDirectory_Pairing.getMusicDirectory());
        tMDP.add(tPairing);
        this.setMusicDirectory_Pairing(tMDP);
//        mMusicDirectory_Pairing.add(tPairing, iAlbum);
        refreshView();
    }


    private void startNewPairing() {
        Logg.i(TAG, "create new Pairing");
        String tTitle = "Pair for " + mMusicDirectory_Pairing.getMusicDirectory().mT1 +
                " / " + mMusicDirectory_Pairing.getMusicDirectory().mT2;
        //tTitle = tTitle.substring(0, Math.min(570, tTitle.length()));
        DialogFragment_Album.create(mLayout, tTitle,
              ( mMusicDirectory_Pairing.getMusicDirectory().mSignature),
                this);
    }


    //Interface
    @Override
    public void shoutUp(Shout tShoutToCeiling) {
        Logg.i(TAG, tShoutToCeiling.toString());
        mGlassFloor = tShoutToCeiling;
        process_shouting();
    }

}
