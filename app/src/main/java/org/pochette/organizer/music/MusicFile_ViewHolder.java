package org.pochette.organizer.music;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.pochette.data_library.music.MusicFile;
import org.pochette.data_library.music.MusicFilePurpose;
import org.pochette.data_library.music.MusicPreference;
import org.pochette.data_library.music.MusicPreferenceFavourite;
import org.pochette.data_library.pairing.Signature;
import org.pochette.data_library.scddb_objects.Dance;
import org.pochette.organizer.R;
import org.pochette.organizer.app.MediaPlayerServiceSingleton;
import org.pochette.organizer.gui_assist.CustomSpinnerItem;
import org.pochette.organizer.gui_assist.SpinnerItemFactory;
import org.pochette.organizer.mediaplayer.MediaPlayerService;
import org.pochette.organizer.requestlist.Requestlist_Action;
import org.pochette.utils_lib.logg.Logg;
import org.pochette.utils_lib.shouting.Shout;
import org.pochette.utils_lib.shouting.Shouting;

import java.util.ArrayList;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

public class MusicFile_ViewHolder extends RecyclerView.ViewHolder implements Shouting {

    private final String TAG = "FEHA (MF_ViewHolder)";


    //Variables
    MusicFile mMusicFile;
    public View mLayout;
    MusicFile_Adapter mMusicFile_Adapter;

    TextView mTV_Artist;
    TextView mTV_Album;
    ImageView mIV_Purpose;
    TextView mTV_Name;
    TextView mTV_Signature;
    TextView mTV_Duration;
    ImageView mIV_Requestlist;
    ImageView mIV_Play;
    FrameLayout mFL_Preference;
    ImageView mIV_Preference;
    ImageView mIV_Dance;

    SpinnerItemFactory mSpinnerItemFactory;

    private Shout mGlassFloor;
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private Shouting mShouting;

    //Constructor
    public MusicFile_ViewHolder(View iView) {
        super(iView);
        mLayout = iView;
        mTV_Artist = mLayout.findViewById(R.id.TV_RowMusicFile_Artist);
        mTV_Album = mLayout.findViewById(R.id.TV_RowMusicFile_Album);
        mIV_Purpose = mLayout.findViewById(R.id.IV_RowMusicFile_Purpose);

        mIV_Dance = mLayout.findViewById(R.id.IV_RowMusicFile_Dance);
        mTV_Name = mLayout.findViewById(R.id.TV_RowMusicFile_Name);
        mTV_Signature = mLayout.findViewById(R.id.TV_RowMusicFile_Signature);
        mTV_Duration = mLayout.findViewById(R.id.TV_RowMusicFile_Duration);
        mIV_Requestlist = mLayout.findViewById(R.id.IV_RowMusicFile_Requestlist);
        mIV_Play = mLayout.findViewById(R.id.IV_RowMusicFile_Play);

        mFL_Preference = mLayout.findViewById(R.id.FL_RowMusicFile_Preference);
        mIV_Preference = mLayout.findViewById(R.id.IV_RowMusicFile_Preference);
        setListener();
        mSpinnerItemFactory = new SpinnerItemFactory();
    }


    //Setter and Getter

    void setMusicFile_Adapter(MusicFile_Adapter iMusicFile_Adapter) {
        mMusicFile_Adapter = iMusicFile_Adapter;
    }

    void setMusicFile(MusicFile iMusicFile) {
        mMusicFile = iMusicFile;
        refreshDisplay();
    }

    public void setShouting(Shouting shouting) {
        mShouting = shouting;
    }

    //Livecycle
    //Static Methods
    //Internal Organs

    String getDurationString() {
        int tDuration = mMusicFile.mDuration;
        if (tDuration == 0) {
            return " -- ";
        }
        return String.format(Locale.ENGLISH, "%d:%02d",
                tDuration / 60,
                tDuration % 60);
    }

    void refreshDisplay() {
        // set the basic data
        try {
            if (mTV_Artist != null) {
                mTV_Artist.setText(mMusicFile.mT2.trim());
            }
            if (mTV_Album != null) {
                mTV_Album.setText(mMusicFile.mT1.trim());
            }
            if (mTV_Name != null) {
                mTV_Name.setText(mMusicFile.mName.trim());
            }
            if (mTV_Duration != null) {
                mTV_Duration.setText(getDurationString());
            }
            if (mTV_Signature != null) {
                if (mMusicFile.mSignature.equals(Signature.getEmpty())) {
                    mTV_Signature.setVisibility(View.INVISIBLE);
                } else {
                    mTV_Signature.setVisibility(View.VISIBLE);
                }
                mTV_Signature.setText(mMusicFile.mSignature);
            }
        } catch(Exception e) {
            Logg.e(TAG, e.toString());
        }

        if (mIV_Preference != null) {
            if (mMusicFile_Adapter != null && mMusicFile_Adapter.getMode() == MusicFile_Adapter.MODE_PREFERENCE) {
                mFL_Preference.setVisibility(View.VISIBLE);
                ArrayList<MusicPreference> tAL = mMusicFile_Adapter.getAL_MusicPreference();
                MusicPreference tMusicPreference;
                MusicPreferenceFavourite tMusicPreferenceFavourite = null;
                for (MusicPreference lMusicPreference : tAL) {
                    if (lMusicPreference.getMusicFile().getId() == mMusicFile.getId()) {
                        tMusicPreference = lMusicPreference;
                        tMusicPreferenceFavourite = tMusicPreference.getMusicPreferenceFavourite();
                        break;
                    }
                }
                if (tMusicPreferenceFavourite == null) {
                    tMusicPreferenceFavourite = MusicPreferenceFavourite.UNKNOWN;
                }
                SpinnerItemFactory tSpinnerItemFactory = new SpinnerItemFactory();
                CustomSpinnerItem tCustomSpinnerItem = tSpinnerItemFactory.
                        getSpinnerItem(SpinnerItemFactory.FIELD_MUSIC_PREFERENCE,
                                tMusicPreferenceFavourite.getCode());
                int tResourceId = tCustomSpinnerItem.mImageResource;
                mIV_Preference.setImageResource(tResourceId);
            } else {
                mFL_Preference.setVisibility(View.GONE);
            }
        }
        if (mIV_Purpose != null) {
            CustomSpinnerItem tCustomSpinnerItem =
                    mSpinnerItemFactory.getSpinnerItem(
                            SpinnerItemFactory.FIELD_MUSICDIRECTORY_PURPOSE,
                            mMusicFile.mPurpose.getCode());
            mIV_Purpose.setImageResource(tCustomSpinnerItem.mImageResource);
        }
        if (mIV_Dance != null) {
            if (mMusicFile.mDanceId > 0) {
                mIV_Dance.setVisibility(View.VISIBLE);
            } else {
                mIV_Dance.setVisibility(View.INVISIBLE);
            }
        }
        int tColor;
        if (isSelected()) {
            tColor = ContextCompat.getColor(mLayout.getContext(), R.color.bg_list_selected);
        } else {
            tColor = ContextCompat.getColor(mLayout.getContext(), android.R.color.transparent);
        }
        try {
            View tParentView;
            if (mTV_Name != null) {
                tParentView = (View) mTV_Name.getParent();
                tParentView.setBackgroundColor(tColor);
            }
        } catch(Exception e) {
            Logg.e(TAG, e.toString());
        }
    }

    public void setListener() {
        if (mIV_Requestlist != null) {
            mIV_Requestlist.setOnClickListener(iView -> {
                Logg.k(TAG, "IV_Requestlist OnClick");
                    Requestlist_Action.callExecute(mLayout, this,
                            Requestlist_Action.CLICK_TYPE_SHORT, Requestlist_Action.CLICK_ICON_REQUESTLIST,
                            null, null, mMusicFile,null);
            });
            mIV_Requestlist.setOnLongClickListener(iView -> {
                Logg.k(TAG, "IV_Requestlist OnLongClick");
                Requestlist_Action.callExecute(mLayout, this,
                        Requestlist_Action.CLICK_TYPE_LONG, Requestlist_Action.CLICK_ICON_REQUESTLIST,
                        null, null, mMusicFile,null);
                return true;
            });
        }

        if (mIV_Purpose != null) {
            mIV_Purpose.setOnClickListener(iView -> {
                Logg.k(TAG, "IV_Purpose OnClick");
                int[] tLocation = new int[2];
                iView.getLocationOnScreen(tLocation);
                startEditPurpose(tLocation);

            });
        }
        if (mIV_Dance != null) {
            mIV_Dance.setOnClickListener(iView -> {
                Logg.k(TAG, "IV_Dance OnClick");
             //   Dance
//                int[] tLocation = new int[2];
//                iView.getLocationOnScreen(tLocation);
//                startEditPurpose(tLocation);

            });
        }

        if (mIV_Play != null) {
            mIV_Play.setOnClickListener(iView -> {
                Logg.k(TAG, "IV_Play OnClick");
                MediaPlayerService tMediaPlayerService;
                tMediaPlayerService = MediaPlayerServiceSingleton.getInstance().getMediaPlayerService();
                Logg.i(TAG, mMusicFile.toString());
                tMediaPlayerService.play(mMusicFile);
            });
        }
        if (mIV_Preference != null) {
            mIV_Preference.setOnClickListener(iView -> {
                Logg.k(TAG, "IV_Preference OnClick");

                int[] tLocation = new int[2];
                iView.getLocationOnScreen(tLocation);
                startEditMusicPreference(tLocation);

            });
        }
    }

    private void startEditMusicPreference(int[] iLocation) {
        DialogFragment_EditMusicFavourite tDialogFragmentEditFavourite =
                DialogFragment_EditMusicFavourite.newInstance(null);
        tDialogFragmentEditFavourite.setShouting(this);
        tDialogFragmentEditFavourite.setLocation(iLocation);
        FragmentManager tFragmentManager = null;
        if (mMusicFile_Adapter != null) {
            tFragmentManager = mMusicFile_Adapter.mFragmentManager;
        }
//        Logg.i(TAG, mLayout.toString());
//        AppCompatActivity tActivity = ((AppCompatActivity) mLayout.getAcContext());
        if (tFragmentManager != null) {
            tDialogFragmentEditFavourite.show(tFragmentManager, null);
        }

    }

    private void startEditPurpose(int[] iLocation) {
        String tCode = mMusicFile.mPurpose.getCode();
        DialogFragment_EditPurpose tDialogFragment_EditPurpose =
                DialogFragment_EditPurpose.newInstance(tCode);
        tDialogFragment_EditPurpose.setShouting(this);
        tDialogFragment_EditPurpose.setLocation(iLocation);
        AppCompatActivity tActivity = ((AppCompatActivity) mLayout.getContext());
        FragmentManager tFragmentManager = tActivity.getSupportFragmentManager();
        tDialogFragment_EditPurpose.show(tFragmentManager, null);
    }


    private boolean isSelected() {
        if (mMusicFile_Adapter == null) {
            return false;
        } else {
            MusicFile tMusicFile = mMusicFile_Adapter.getSelectedMusicFile();
            if (tMusicFile == null) {
                return false;
            } else {
                return tMusicFile.mId == mMusicFile.mId;
            }

        }

    }

    private void process_shouting() {
        switch (mGlassFloor.mActor) {
            case "DialogFragment_EditMusicFavourite":
                if (mGlassFloor.mLastAction.equals("confirm") &&
                        mGlassFloor.mLastObject.equals("DataEntry")) {
                    String tCode = "";
                    try {
                        JSONObject tJsonObject = new JSONObject(mGlassFloor.mJsonString);
                        tCode = tJsonObject.getString("Favourite");
                    } catch(JSONException e) {
                        Logg.w(TAG, e.toString());
                    }
                    MusicPreferenceFavourite tMusicPreferenceFavourite = MusicPreferenceFavourite.fromCode(tCode);
                    if (tMusicPreferenceFavourite == null) {
                        tMusicPreferenceFavourite = MusicPreferenceFavourite.UNKNOWN;
                    }
                    Dance tDance = mMusicFile_Adapter.getDanceForPreference();
                    if (tDance != null) {
                        MusicPreference tMusicPreference = new MusicPreference(0, mMusicFile, tDance, tMusicPreferenceFavourite, true);
                        tMusicPreference.save();
                    }
                    refreshDisplay();


                }


                break;
            case "DialogFragment_EditPurpose":
                if (mGlassFloor.mLastAction.equals("confirm") &&
                        mGlassFloor.mLastObject.equals("DataEntry")) {
                    String tCode = "";
                    try {
                        JSONObject tJsonObject = new JSONObject(mGlassFloor.mJsonString);
                        tCode = tJsonObject.getString("Purpose");
                    } catch(JSONException e) {
                        Logg.w(TAG, e.toString());
                    }
                    MusicFilePurpose tMusicFilePurpose = MusicFilePurpose.fromCode(tCode);
                    if (tMusicFilePurpose == null) {
                        tMusicFilePurpose = MusicFilePurpose.UNKNOWN;
                    }
                    mMusicFile.mPurpose = tMusicFilePurpose;
                    mMusicFile.save();
                    @SuppressWarnings("unused") Dance tDance = mMusicFile_Adapter.getDanceForPreference();
                    refreshDisplay();
                }
                break;
        }
    }

    //Interface
    @Override
    public void shoutUp(Shout tShoutToCeiling) {
        Logg.w(TAG, tShoutToCeiling.toString());
        mGlassFloor = tShoutToCeiling;
        process_shouting();
    }
}
