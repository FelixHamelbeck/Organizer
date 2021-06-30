package org.pochette.organizer.dance;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.pochette.data_library.scddb_objects.Crib;
import org.pochette.data_library.scddb_objects.Dance;
import org.pochette.data_library.scddb_objects.DanceClassification;
import org.pochette.data_library.scddb_objects.Danceinfo;
import org.pochette.organizer.R;
import org.pochette.organizer.diagram.DiagramManager;
import org.pochette.organizer.gui_assist.CustomSpinnerItem;
import org.pochette.organizer.gui_assist.SpinnerItemFactory;
import org.pochette.organizer.music.MusicFile_Action;
import org.pochette.organizer.playlist.Playlist_Action;
import org.pochette.utils_lib.logg.Logg;
import org.pochette.utils_lib.shouting.Shout;
import org.pochette.utils_lib.shouting.Shouting;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Display AnyText, no interaction
 */

public class DialogFragment_DanceInfo extends DialogFragment
        implements Shouting {

    //  private static final android.R.attr R = ;
    private final String TAG = "FEHA (DialogFragment_CreatePlaylist)";
    //Variables
    //   DialogFragment_DanceInfo m_DialogFragement_DanceInfo;
    Shout mGlassFloor;
    private FragmentManager mFragmentManager;
    private View mView;
    private ScrollView mScrolView;
    private TextView mTV_Name;
    private ImageView mIV_Favourite;
    private ImageView mIV_Playlist;
    private ImageView mIV_MusicFile;
    private ImageView mIV_Rscds;
    private ImageView mDiagram;
    private RecyclerView mCrib_RV;
    private Crib_Adapter mCrib_Adapter;
    private TextView mTV_ItalicDots;
    private TextView mTL_Devised;
    private TextView mTL_Intensity;
    private TextView mTL_Formation;
    private TextView mTL_Step;
    private TextView mTL_Published;
    private TextView mTL_Music;
    private TextView mTL_Extra;
    private TextView mTV_Devised;
    private TextView mTV_Intensity;
    private TextView mTV_Formation;
    private TextView mTV_Step;
    private TextView mTV_Published;
    private TextView mTV_Music;
    private TextView mTV_Extra;
    private Dance mDance;
    private Danceinfo mDanceinfo;
    private int mWidth = 0;

    private SpinnerItemFactory mSpinnerItemFactory;


    //Constructor
    public DialogFragment_DanceInfo() {
//        m_DialogFragement_DanceInfo = this;
    }

    @SuppressWarnings("unused")
    public static DialogFragment_DanceInfo newInstance(String iText) {
        DialogFragment_DanceInfo tDialogFragment_DanceInfo;
        tDialogFragment_DanceInfo = new DialogFragment_DanceInfo();
        return tDialogFragment_DanceInfo;

    }

//    public static void show(FragmentManager iFragmentManager, Dance iDance) {
//
//        DialogFragment_DanceInfo tDialogFragment_DanceInfo = DialogFragment_DanceInfo.newInstance("About");
//        tDialogFragment_DanceInfo.mFragmentManager = iFragmentManager;
//
//        if (iDance != null) {
//            tDialogFragment_DanceInfo.show(iFragmentManager, iDance);
//        }
//    }

    public static void   show(FragmentManager iFragmentManager, Dance iDance) {

        DialogFragment_DanceInfo tDialogFragment_DanceInfo = DialogFragment_DanceInfo.newInstance("About");
        tDialogFragment_DanceInfo.mFragmentManager = iFragmentManager;
        tDialogFragment_DanceInfo.mDance = iDance;
        tDialogFragment_DanceInfo.mDanceinfo = Danceinfo.getBy(iDance.mId);
        tDialogFragment_DanceInfo.show(iFragmentManager, "fragment_edit_name");
    }

    //Setter and Getter
    public void setTargetFragment(Fragment fragment,
                                  int requestCode) {

        super.setTargetFragment(fragment, requestCode);
    }

    //Livecycle
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View tView = inflater.inflate(R.layout.dialog_danceinfo, container);
        moveLocation();
        return tView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
    //Static Methods

    @Override
    public void onViewCreated(@NonNull View iView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(iView, savedInstanceState);
        mSpinnerItemFactory = new SpinnerItemFactory();

        mView = iView;
        mScrolView = requireView().findViewById(R.id.DanceInfo_SC);
        mTV_Name = requireView().findViewById(R.id.DanceInfo_TV_Name);
        mIV_Favourite = requireView().findViewById(R.id.DanceInfo_IV_Favourite);
        mIV_Playlist = requireView().findViewById(R.id.DanceInfo_IV_Dancelist);
        mIV_MusicFile = requireView().findViewById(R.id.DanceInfo_IV_Musicfile);
        mIV_Rscds = requireView().findViewById(R.id.DanceInfo_IV_RSCDS);
        mDiagram = requireView().findViewById(R.id.DanceInfo_IV_DisplayDiagram);
        mCrib_RV = requireView().findViewById(R.id.DanceInfo_RV_Crib);
        mTV_ItalicDots = requireView().findViewById(R.id.DanceInfo_TV_ItalicDots);
        mTL_Devised = requireView().findViewById(R.id.DanceInfo_TL_Devised);
        mTL_Intensity = requireView().findViewById(R.id.DanceInfo_TL_Intensity);
        mTL_Formation = requireView().findViewById(R.id.DanceInfo_TL_Formation);
        mTL_Step = requireView().findViewById(R.id.DanceInfo_TL_Step);
        mTL_Published = requireView().findViewById(R.id.DanceInfo_TL_Published);
        mTL_Music = requireView().findViewById(R.id.DanceInfo_TL_Music);
        mTL_Extra = requireView().findViewById(R.id.DanceInfo_TL_Extra);
        mTV_Devised = requireView().findViewById(R.id.DanceInfo_TV_Devised);
        mTV_Intensity = requireView().findViewById(R.id.DanceInfo_TV_Intensity);
        mTV_Formation = requireView().findViewById(R.id.DanceInfo_TV_Formation);
        mTV_Step = requireView().findViewById(R.id.DanceInfo_TV_Step);
        mTV_Published = requireView().findViewById(R.id.DanceInfo_TV_Published);
        mTV_Music = requireView().findViewById(R.id.DanceInfo_TV_Music);
        mTV_Extra = requireView().findViewById(R.id.DanceInfo_TV_Extra);

        moveLocation();

        setListener();

        mView.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        mWidth = mView.getWidth();
                        mView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        refresh();
                    }
                });

    }

    void setListener() {
        if (mIV_Favourite != null) {
            //   final Dance_ViewHolder fDance_ViewHolder = this;
            mIV_Favourite.setOnClickListener(view -> {
                Logg.k(TAG, "IV_Favourite OnClick");
                int[] tLocation = new int[2];
                view.getLocationOnScreen(tLocation);


                DialogFragment_EditFavourite t_dialogFragmentEditFavourite =
                        DialogFragment_EditFavourite.newInstance(mDance.getFavouriteCode());
                t_dialogFragmentEditFavourite.setShouting(this);
                t_dialogFragmentEditFavourite.setLocation(tLocation);
                t_dialogFragmentEditFavourite.show(mFragmentManager, "EditFavourite");
            });
        }
        if (mIV_Playlist != null) {
            mIV_Playlist.setOnClickListener(view -> {
                Logg.k(TAG, "IV_Playlist OnClick");
                if (mDance != null && mDance.mCountofRecordings > 0) {
                    Playlist_Action.callExecute(mView, this,
                            Playlist_Action.CLICK_TYPE_SHORT, Playlist_Action.CLICK_ICON_PLAYLIST, null, mDance, null);
                }
            });
            mIV_Playlist.setOnLongClickListener(view -> {
                Logg.k(TAG, "IV_Playlist OnLongClick");
                if (mDance != null && mDance.mCountofRecordings > 0) {
                    Playlist_Action.callExecute(mView, this,
                            Playlist_Action.CLICK_TYPE_LONG, Playlist_Action.CLICK_ICON_PLAYLIST, null, mDance, null);

                }
                return true;
            });
        }
        if (mIV_MusicFile != null) {
            mIV_MusicFile.setOnClickListener(view -> {
                Logg.k(TAG, "IV_MusicFile OnClick");
                MusicFile_Action.callExecute(mView, this,
                        MusicFile_Action.SHORT_CLICK,
                        MusicFile_Action.CLICK_ICON_PLAY,
                        mDance, null);
            });
            mIV_MusicFile.setOnLongClickListener(View -> {
                Logg.k(TAG, "IV_MusicFile OnLongClick");
                MusicFile_Action.callExecute(mView, this,
                        MusicFile_Action.LONG_CLICK,
                        MusicFile_Action.CLICK_ICON_PLAY,
                        mDance, null);
                return true;
            });
        }

    }


    @Override
    public void onResume() {
        moveLocation();
        refresh();
        super.onResume();
    }

    void showDiagram() {
        if (mDiagram != null && mWidth > 0) {
            if (mDance.mCountofDiagrams > 0) {
                Bitmap tBitmap;
                DiagramManager tDiagramManager = new DiagramManager();
                tBitmap = tDiagramManager.getBitmap(mDance);
                if (tBitmap != null) {
                    mDiagram.setVisibility(View.VISIBLE);
                    mDiagram.setImageBitmap(tBitmap);
                } else {
                    mTV_ItalicDots.setVisibility(View.GONE);
                }
            } else {
                mDiagram.setVisibility(View.GONE);
            }
        }
    }

    void showCrib() {
        if (mCrib_RV != null) {
            try {
                Crib tCrib;
                tCrib = new Crib(mDance.mId);
                if (mCrib_Adapter == null) {
                    mCrib_Adapter = new Crib_Adapter(mView.getContext(), mCrib_RV, null);
                    RecyclerView.LayoutManager LM_Crib = new LinearLayoutManager(mView.getContext());
                    mCrib_RV.setAdapter(mCrib_Adapter);
                    mCrib_RV.setLayoutManager(LM_Crib);


                    mCrib_Adapter.setCrib(tCrib);
                }
            } catch(Exception e) {
                Logg.w(TAG, e.toString());
            }

        }

    }

    void refresh() {
        if (mDance == null) {
            Logg.i(TAG, "no dance available");
            return;
        }
        String tText;
        if (mTV_Name != null) {
            tText = String.format(Locale.ENGLISH, "%s (%s)", mDance.mName, mDance.getSignature());
            mTV_Name.setText(tText);
        }

        if (mIV_Favourite != null) {

            CustomSpinnerItem tCustomSpinnerItem = mSpinnerItemFactory.getSpinnerItem(
                    SpinnerItemFactory.FIELD_DANCE_FAVOURITE,
                    mDance.getFavouriteCode()
            );
            mIV_Favourite.setImageResource(tCustomSpinnerItem.mImageResource);
        }
        if (mIV_MusicFile != null) {
            if (mDance.mCountofRecordings > 1) {
                mIV_MusicFile.setImageResource(R.drawable.ic_music_multiple);
                mIV_MusicFile.setVisibility(View.VISIBLE);
            } else if (mDance.mCountofRecordings == 1) {
                mIV_MusicFile.setImageResource(R.drawable.ic_music_single);
                mIV_MusicFile.setVisibility(View.VISIBLE);
            } else {
                mIV_MusicFile.setImageResource(R.drawable.ic_music_none);
                mIV_MusicFile.setVisibility(View.VISIBLE);
            }
        }

        if (mIV_Rscds != null) {
            if (mDance.mFlagRscds) {
                mIV_Rscds.setVisibility(View.VISIBLE);
            } else {
                mIV_Rscds.setVisibility(View.INVISIBLE);
            }
        }


        if (mIV_Rscds != null) {
            if (mDance.mFlagRscds) {
                mIV_Rscds.setVisibility(View.VISIBLE);
            } else {
                mIV_Rscds.setVisibility(View.INVISIBLE);
            }
        }


        tText = "";

        if (mTV_ItalicDots != null) {
            if (mDance != null) {
                String tType = mDance.mType;
                int tBarsPerRepeat = mDance.mBarsperrepeat;
                String tCouples = mDance.mCouples;
                String tShape = mDance.mShape;
                String tProgression = mDance.mProgression;
                tText = String.format(Locale.ENGLISH,
                        "%s \u00B7 %d bars \u00B7  %s \u00B7  %s \u00B7  %s", // 00B7 is the unicode character middle dot
                        tType, tBarsPerRepeat, tCouples, tShape, tProgression);
            }
            if (tText.isEmpty()) {
                mTV_ItalicDots.setVisibility(View.GONE);
            } else {
                mTV_ItalicDots.setVisibility(View.VISIBLE);
                mTV_ItalicDots.setText(tText);
            }
        }


        tText = "";
        if (mTV_Devised != null) {
            if (mDanceinfo != null && mDanceinfo.mDevisorName != null) {
                tText = mDanceinfo.mDevisorName;
                if (mDanceinfo.mDevicesDate != null) {
                    Date tDate = mDanceinfo.mDevicesDate;
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("yyyy");
                    tText = String.format(Locale.ENGLISH, "%s (%s)", tText,
                            df.format(tDate));
                }
            }
            if (tText.isEmpty()) {
                mTL_Devised.setVisibility(View.GONE);
                mTV_Devised.setVisibility(View.GONE);
            } else {
                mTL_Devised.setVisibility(View.VISIBLE);
                mTV_Devised.setVisibility(View.VISIBLE);
                mTV_Devised.setText(tText);
            }
        }

        tText = "";
        if (mTV_Intensity != null) {
            if (mDanceinfo != null && mDanceinfo.mIntensityBars != null) {
                if (mDanceinfo.mIntensity > 0) {
                    tText = String.format(Locale.ENGLISH, "%s  = %d %% (1 turn), %d %% (whole dance)",
                            mDanceinfo.mIntensityBars, mDanceinfo.mIntensity, mDanceinfo.mIntensityPerTurn);
                }
            }
            if (tText.isEmpty()) {
                mTL_Intensity.setVisibility(View.GONE);
                mTV_Intensity.setVisibility(View.GONE);
            } else {
                mTL_Intensity.setVisibility(View.VISIBLE);
                mTV_Intensity.setVisibility(View.VISIBLE);
                mTV_Intensity.setText(tText);
            }
        }

        tText = "";
        if (mTV_Formation != null) {
            if (mDanceinfo != null && mDanceinfo.mFormationString != null) {
                tText = mDanceinfo.mFormationString;
            }
            if (tText.isEmpty()) {
                mTL_Formation.setVisibility(View.GONE);
                mTV_Formation.setVisibility(View.GONE);
            } else {
                mTL_Formation.setVisibility(View.VISIBLE);
                mTV_Formation.setVisibility(View.VISIBLE);
                mTV_Formation.setText(tText);
            }
        }

        tText = "";
        if (mTV_Step != null) {
            if (mDanceinfo != null && mDanceinfo.mStepsString != null) {
                tText = mDanceinfo.mStepsString;
            }
            if (tText.isEmpty()) {
                mTL_Step.setVisibility(View.GONE);
                mTV_Step.setVisibility(View.GONE);
            } else {
                mTL_Step.setVisibility(View.VISIBLE);
                mTV_Step.setVisibility(View.VISIBLE);
                mTV_Step.setText(tText);
            }
        }

        tText = "";
        if (mTV_Published != null) {
            if (mDanceinfo != null && mDanceinfo.mPublicationString != null) {
                tText = mDanceinfo.mPublicationString;
            }
            if (tText.isEmpty()) {
                mTL_Published.setVisibility(View.GONE);
                mTV_Published.setVisibility(View.GONE);

            } else {
                mTL_Published.setVisibility(View.VISIBLE);
                mTV_Published.setVisibility(View.VISIBLE);
                mTV_Published.setText(tText);
            }
        }

        tText = "";
        if (mTV_Music != null) {
            //noinspection ConstantConditions
            if (tText.isEmpty()) {
                mTL_Music.setVisibility(View.GONE);
                mTV_Music.setVisibility(View.GONE);
            } else {
                mTL_Music.setVisibility(View.VISIBLE);
                mTV_Music.setVisibility(View.VISIBLE);
                mTV_Music.setText(tText);
            }
        }


        tText = "";
        if (mTV_Extra != null) {
            if (mDanceinfo != null && !mDanceinfo.mNotes.isEmpty()) {
                tText = mDanceinfo.mNotes;
                tText = tText.replace("##>>", "");
                tText = tText.replace("##<<", "");
                tText = tText.replaceFirst("^\n", "");
                tText = tText.replaceAll("\n\n+", "\n");
            }
            if (tText.isEmpty()) {
                mTL_Extra.setVisibility(View.GONE);
                mTV_Extra.setVisibility(View.GONE);
            } else {
                mTL_Extra.setVisibility(View.VISIBLE);
                mTV_Extra.setVisibility(View.VISIBLE);
                mTV_Extra.setText(tText);
            }
        }

        showDiagram();
        showCrib();

        if (mScrolView != null) {

            mScrolView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    //  mScrolView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    mScrolView.fullScroll(View.FOCUS_UP);
                }
            });
        }
    }

    private void moveLocation() {
        final Window dialogWindow = Objects.requireNonNull(getDialog()).getWindow();
        WindowManager.LayoutParams lp;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        Objects.requireNonNull(dialogWindow).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int tScreenHeight = displayMetrics.heightPixels;
        int tScreebWidth = displayMetrics.widthPixels;
        lp = Objects.requireNonNull(dialogWindow).getAttributes();
        lp.width = (int) (0.8f * tScreebWidth);
        lp.height = (int) (0.8f * tScreenHeight);
        lp.gravity = Gravity.CENTER;
        dialogWindow.setAttributes(lp);
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
    }


    //Interface

    @Override
    public void dismiss() {
        super.dismiss();
    }

    void process_shouting() {
        // maybe the data has changed
        if (mGlassFloor.mActor.equals("DialogFragment_EditFavourite") &&
                mGlassFloor.mLastAction.equals("confirm") &&
                mGlassFloor.mLastObject.equals("DataEntry")) {
            try {
                JSONObject tJsonObject = new JSONObject(mGlassFloor.mJsonString);
                String tCode = tJsonObject.getString("Favourite");
                // save in memory
                mDance.setFavouriteCode(tCode);
                // save to DB
                DanceClassification tDanceClassification = new DanceClassification();
                tDanceClassification.setFavourite_Code(tCode);
                tDanceClassification.setObject_Id(mDance.mId);
                tDanceClassification.save();
                // refresh

                refresh();
            } catch(JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void shoutUp(Shout tShoutToCeiling) {
        Logg.i(TAG, tShoutToCeiling.toString());
        mGlassFloor = tShoutToCeiling;
        process_shouting();
    }

}
