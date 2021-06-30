package org.pochette.organizer.music;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.pochette.data_library.music.MusicDirectory;
import org.pochette.organizer.R;
import org.pochette.organizer.gui_assist.SpinnerItemFactory;
import org.pochette.utils_lib.logg.Logg;
import org.pochette.utils_lib.shouting.Shout;
import org.pochette.utils_lib.shouting.Shouting;

import java.util.Locale;

import androidx.recyclerview.widget.RecyclerView;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class MusicDirectory_ViewHolder extends RecyclerView.ViewHolder implements Shouting {

    private final String TAG = "FEHA (MF_ViewHolder)";

    //Variables
    MusicDirectory mMusicDirectory;
    public View mLayout;
    MusicDirectory_Adapter mMusicDirectory_Adapter;

    TextView mTV_Artist;
    TextView mTV_Album;
    TextView mTV_Count;
    ImageView mIV_Purpose;
    ImageView mIV_Pairing;
    TextView mTV_Siganture;

    private Shout mGlassFloor;
    private Shouting mShouting;

    //Constructor
    public MusicDirectory_ViewHolder(View iView) {
        super(iView);
        mLayout = iView;
        mTV_Artist = mLayout.findViewById(R.id.TV_RowMusicDirectory_Artist);
        mTV_Album = mLayout.findViewById(R.id.TV_RowMusicDirectory_Album);
        mIV_Purpose = mLayout.findViewById(R.id.IV_RowMusicDirectory_Purpose);
        mIV_Pairing = mLayout.findViewById(R.id.IV_RowMusicDirectory_Pairing);
        mTV_Count = mLayout.findViewById(R.id.TV_RowMusicDirectory_Count);
        mTV_Siganture = mLayout.findViewById(R.id.TV_RowMusicDirectory_Signature);
        setListener();
    }


    //Setter and Getter

    void setMusicDirectory_Adapter(MusicDirectory_Adapter iMusicDirectory_Adapter) {
        mMusicDirectory_Adapter = iMusicDirectory_Adapter;
    }

    void setMusicDirectory(MusicDirectory iMusicDirectory) {
        mMusicDirectory = iMusicDirectory;
        refreshDisplay();
    }

    public void setShouting(Shouting shouting) {
        mShouting = shouting;
    }

    //Livecycle
    //Static Methods
    //Internal Organs


    void refreshDisplay() {
        // set the basic data
        try {
            if (mTV_Artist != null) {
                mTV_Artist.setText(mMusicDirectory.mT2.trim());
            }
            if (mTV_Album != null) {
                mTV_Album.setText(mMusicDirectory.mT1.trim());
            }
//            if (mTV_Name != null) {
//                mTV_Name.setText(mMusicDirectory.mSignature);
//            }
            if (mTV_Count != null) {
                mTV_Count.setText(String.format(Locale.ENGLISH,
                        "%d", mMusicDirectory.mCountTracks));
            }
        } catch(Exception e) {
            Logg.e(TAG, e.toString());
        }

        if (mIV_Purpose != null) {
            SpinnerItemFactory tSpinnerItemFactory = new SpinnerItemFactory();
            int tIconResourceId;
            tIconResourceId = tSpinnerItemFactory.
                    getSpinnerItem(SpinnerItemFactory.FIELD_MUSICDIRECTORY_PURPOSE,
                            mMusicDirectory.mMusicDirectoryPurpose.getCode()).mImageResource;
            //   Logg.i(TAG, "IconResource" + tIconResourceId);
            if (tIconResourceId != 0) {
                mIV_Purpose.setImageResource(tIconResourceId);
            }
        }

        if (mTV_Siganture != null) {
            String tText = String.format(Locale.ENGLISH, ":%s",mMusicDirectory.mSignature);

            mTV_Siganture.setText(tText);


        }

//        int tColor;
//        if (isSelected()) {
//            tColor = ContextCompat.getColor(mLayout.getContext(), R.color.bg_list_selected);
//        } else {
//            tColor = ContextCompat.getColor(mLayout.getContext(), android.R.color.transparent);
//        }
//        try {
//            View tParentView;
////            if (mTV_Name != null) {
////                tParentView = (View) mTV_Name.getParent();
////                tParentView.setBackgroundColor(tColor);
////            }
//        } catch(Exception e) {
//            Logg.e(TAG, e.toString());
//        }
    }

    public void setListener() {

//        if (mIV_Purposelist != null) {
//            mIV_Purposelist.setOnClickListener(iView -> {
//                Logg.k(TAG, "IV_Playlist OnClick");
//                    Fragment tFragment = FragmentManager.findFragment(mLayout);
//                    Playlist_Action.callExecute(tFragment, this,
//                            Playlist_Action.SHORT_CLICK, mMusicDirectory);
//            });
//            mIV_Purposelist.setOnLongClickListener(iView -> {
//                Logg.k(TAG, "IV_Playlist OnLongClick");
//                Fragment tFragment = FragmentManager.findFragment(mLayout);
//                Playlist_Action.callExecute(tFragment, this,
//                        Playlist_Action.LONG_CLICK, mMusicDirectory);
//                return true;
//            });
//        }
//        if (mIV_Purpose != null) {
//            mIV_Purpose.setOnClickListener(iView -> {
//                Logg.k(TAG, "IV_Play OnClick");
//                MediaPlayerService tMediaPlayerService;
//                tMediaPlayerService = MediaPlayerServiceSingleton.getInstance().getMediaPlayerService();
//                Logg.i(TAG, mMusicDirectory.toString());
//                tMediaPlayerService.play(mMusicDirectory);
//
//            });
//        }
//        if (mIV_Preference != null) {
//            mIV_Preference.setOnClickListener(iView -> {
//                Logg.k(TAG, "IV_Preference OnClick");
//
//                int[] tLocation = new int[2];
//                iView.getLocationOnScreen(tLocation);
//                startEdidMusicPrefence(tLocation);
//
//            });
//        }
    }

//    private void startEdidMusicPrefence(int[] iLocation) {
//        DialogFragment_EditMusicFavourite tDialogFragmentEditFavourite =
//                DialogFragment_EditMusicFavourite.newInstance(null);
//        tDialogFragmentEditFavourite.setShouting(this);
//        FragmentManager tFragmentManager = null;
//        if (mMusicDirectory_Adapter != null) {
//            tFragmentManager = mMusicDirectory_Adapter.mFragmentManager;
//        }
////        Logg.i(TAG, mLayout.toString());
////        AppCompatActivity tActivity = ((AppCompatActivity) mLayout.getAcContext());
//        if (tFragmentManager != null) {
//            Logg.i(TAG, tFragmentManager.toString());
//            tDialogFragmentEditFavourite.show(tFragmentManager, null);
//        }
//
//    }


    private boolean isSelected() {
        if (mMusicDirectory_Adapter == null) {
            return false;
        } else {
            MusicDirectory tMusicDirectory = mMusicDirectory_Adapter.getSelectedMusicDirectory();
            if (tMusicDirectory == null) {
                return false;
            } else {
                return tMusicDirectory.mId == mMusicDirectory.mId;
            }

        }

    }

    private void process_shouting() {
//        switch (mGlassFloor.mActor) {
//            case "DialogFragment_EditMusicFavourite":
//                if (mGlassFloor.mLastAction.equals("confirm") &&
//                        mGlassFloor.mLastObject.equals("DataEntry")) {
//                    String tCode = "";
//                    try {
//                        JSONObject tJsonObject = new JSONObject(mGlassFloor.mJsonString);
//                        tCode = tJsonObject.getString("Favourite");
//                    } catch(JSONException e) {
//                        Logg.w(TAG, e.toString());
//                    }
//                    MusicPreferenceFavourite tMusicPreferenceFavourite = MusicPreferenceFavourite.fromCode(tCode);
//                    if (tMusicPreferenceFavourite == null) {
//                        tMusicPreferenceFavourite = MusicPreferenceFavourite.UNKNOWN;
//                    }
//                    Dance tDance = mMusicDirectory_Adapter.getDanceForPreference();
//                    if (tDance != null) {
//                        MusicPreference tMusicPreference = new MusicPreference(0, mMusicDirectory, tDance, tMusicPreferenceFavourite, true);
//                        tMusicPreference.save();
//                    }
//
//
//                }
//                break;
//        }

    }


    //Interface
    @Override
    public void shoutUp(Shout tShoutToCeiling) {
        Logg.i(TAG, tShoutToCeiling.toString());
        mGlassFloor = tShoutToCeiling;
        process_shouting();
    }
}
