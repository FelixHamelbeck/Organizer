package org.pochette.organizer.dance;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.pochette.data_library.scddb_objects.Crib;
import org.pochette.data_library.scddb_objects.Dance;
import org.pochette.data_library.scddb_objects.DanceClassification;
import org.pochette.organizer.R;
import org.pochette.organizer.diagram.DiagramManager;
import org.pochette.organizer.gui_assist.BitmapView;
import org.pochette.organizer.gui_assist.SpinnerItemFactory;
import org.pochette.organizer.music.MusicFile_Action;
import org.pochette.organizer.requestlist.Requestlist_Action;
import org.pochette.utils_lib.logg.Logg;
import org.pochette.utils_lib.shouting.Shout;
import org.pochette.utils_lib.shouting.Shouting;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

//import androidx.fragment.app.FragmentManager;

public class SlimDance_ViewHolder extends RecyclerView.ViewHolder implements Shouting {
    // , DiagramBitmapCallback
    private final String TAG = "FEHA (SlimDance_ViewHolder)";


    /**
     * Stacked: Top row text, 2nd row icons, 3rd diagram and bottom crib
     */

    //Variables

    //private int mDisplayVariant;
    //private SlimDance mSlimDance;
    private Dance mDance;
    private int mPreviousDance_Id;
    public View mLayout;

    TextView mTV_Name;
    ImageView mIV_Favourite;
    ImageView mIV_Requestlist;
    TextView mTV_Type;
    TextView mTV_Shape;
    TextView mTV_Couples;
    TextView mTV_Progression;
    ImageView mIV_MusicFile;
    ImageView mIV_Diagram;
    ImageView mIV_Crib;
    ImageView mIV_Rscds;
    ImageView mIV_Info;
    BitmapView mIV_DisplayDiagram;
    RecyclerView mRV_Scddb_Crib;

    private Crib_Adapter mCrib_Adapter;
    SlimDance_Adapter mDance_Adapter = null;

    DiagramManager mDiagramManager;

    Timer mViewTimer;
    // Logical organisation, layout choices
    private int mCribDesire = 2;   // 0 = not allowed; 2 = single allowed, but not desired, 4 = single allowed and desired, 9 = always, where available
    private int mDiagramDesire = 4;
    // misc
  //  private int mLayoutMode;
    private int mCribLoaded_DanceId;

    private Shout mGlassFloor;
    private Shouting mShouting;

    //Constructor

    public SlimDance_ViewHolder(View iView) {

        super(iView);

        //mDisplayVariant = VARIANT_MAX;
        mLayout = iView;
        mTV_Name = mLayout.findViewById(R.id.TV_RowScddbDance_Name);
        mIV_Favourite = mLayout.findViewById(R.id.IV_RowScddbDance_Favourite);
        mIV_Requestlist = mLayout.findViewById(R.id.IV_RowScddbDance_Requestlist);
        mTV_Type = mLayout.findViewById(R.id.TV_RowScddbDance_Type);
        mTV_Shape = mLayout.findViewById(R.id.TV_RowScddbDance_Shape);
        mTV_Couples = mLayout.findViewById(R.id.TV_RowScddbDance_Couples);
        mTV_Progression = mLayout.findViewById(R.id.TV_RowScddbDance_Progression);
        mIV_MusicFile = mLayout.findViewById(R.id.IV_RowScddbDance_MusicFile);
        mIV_Diagram = mLayout.findViewById(R.id.IV_RowScddbDance_Diagram);
        mIV_Crib = mLayout.findViewById(R.id.IV_RowScddbDance_Crib);
        mIV_Rscds = mLayout.findViewById(R.id.IV_RowScddbDance_RSCDS);
        mIV_Info = mLayout.findViewById(R.id.IV_RowScddbDance_Info);
        mRV_Scddb_Crib = mLayout.findViewById(R.id.RV_RowScddbDance_Crib);
        mIV_DisplayDiagram = mLayout.findViewById(R.id.IV_RowScddbDance_DisplayDiagram);

      //  if (mLayoutMode == Dance_Adapter.LAYOUT_MODE_COMPACT) {
            mIV_DisplayDiagram = mLayout.findViewById(R.id.IV_RowScddbDance_DisplayDiagram);
            //		mFL_Diagram = v.findViewById(R.id.FEHA_IV_RowScddbDance_Diagram);
  //      }
        setListener();
        mDiagramManager = new DiagramManager();

        if (mDance_Adapter != null) {
            int tCribExpansion = mDance_Adapter.mExpansionCrib;
            processExpansionCrib(tCribExpansion);
            int tDiagramExpansion = mDance_Adapter.mExpansionDiagram;
            processExpansionDiagram(tDiagramExpansion);
        }
    }


    //Setter and Getter

    void setDanceId(Integer iId) {
        Logg.i(TAG, "SetSlimDance bz integer ");
        try {
            //    mDance = new Dance(iSlimDance.mId);
            Logg.i(TAG, "setDanceID " + iId);
            mDance = Dance_Cache.getById(iId);
            if (mDance == null) {
                Logg.w(TAG, "error for ID " + iId);
                mDance = Dance_Cache.getById(iId);
                throw new RuntimeException("No Dance found");
            }

            Logg.i(TAG, "found" + mDance.toString());
        } catch(Exception e) {
            Logg.w(TAG, e.toString());
            return;
        }
        if (mDance.mId != mPreviousDance_Id) {
            resetValues();
            mPreviousDance_Id = mDance.mId;
        }
        if (mViewTimer != null) {
            mViewTimer.cancel();
        }
        refreshDisplay();
    }


//    public void setDisplayVariant(int iDisplayVariant) {
//        mDisplayVariant = iDisplayVariant;
//    }

    public void setShouting(Shouting shouting) {
        mShouting = shouting;
    }

    public void setDance_Adapter(SlimDance_Adapter dance_Adapter) {
        mDance_Adapter = dance_Adapter;
    }
//    void switchOnDetail() {
//    }
//
//    void switchOffDetail() {
//        mIV_DisplayDiagram = null;
//    }


    //Livecycle


    //Static Methods

    //Internal Organs

    void resetValues() {

        if (mIV_DisplayDiagram != null) {
            mIV_DisplayDiagram.setVisibility(View.INVISIBLE);
            mIV_DisplayDiagram.setImageResource(android.R.color.transparent);
            mIV_DisplayDiagram.getLayoutParams().height = 10;
        }
//        if (mCrib_Adapter != null) {
//            mCrib_Adapter.setCrib(null);
//        }

    }


    @SuppressLint("SetTextI18n")
    void refreshDisplay() {
        SpinnerItemFactory tSpinnerItemFactory = new SpinnerItemFactory();
        if (mDance_Adapter != null) {
            processExpansionCrib(mDance_Adapter.mExpansionCrib);
            processExpansionDiagram(mDance_Adapter.mExpansionDiagram);
        }
        if (mPreviousDance_Id == 0 || mDance == null || mDance.mId != mPreviousDance_Id) {
            resetValues();
        }
        // set the basic data
        try {
            if (mTV_Name != null) {
                mTV_Name.setText(mDance.mName.trim());
            }
            if (mIV_Favourite != null) {
                int tIconResourceId;
                tIconResourceId = tSpinnerItemFactory.
                        getSpinnerItem("DANCE_FAVOURITE", mDance.getFavouriteCode()).mImageResource;
                mIV_Favourite.setImageResource(tIconResourceId);
            }
            if (mTV_Type != null) {
                mTV_Type.setText(mDance.mType.trim());
            }
            if (mTV_Couples != null) {
                if (mDance.mCouples != null) {
                    mTV_Couples.setText(mDance.mCouples.trim());
                } else {
                    mTV_Couples.setText(R.string.NotAvailable);
                }
            }
            if (mTV_Shape != null) {
                if (mDance.mShape != null) {
                    mTV_Shape.setText(mDance.mShape.trim());
                    mTV_Shape.setText("id "+mDance.mId);
                } else {
                    mTV_Shape.setText(R.string.NotAvailable);
                }
            }
            if (mTV_Progression != null) {
                if (mDance.mProgression != null) {
                    mTV_Progression.setText(mDance.mProgression.trim());
                } else {
                    mTV_Progression.setText(R.string.NotAvailable);
                }
            }
        } catch(Exception e) {
            Logg.e(TAG, e.toString());
        }

        // set color from mDance.mType (jig, reel, ...
        int lColor;
        switch (mDance.mType) {
            case "Jig":
                lColor = ContextCompat.getColor(mLayout.getContext(), R.color.Jig);
                break;
            case "Medley":
                lColor = ContextCompat.getColor(mLayout.getContext(), R.color.Medley);
                break;
            case "Reel":
                lColor = ContextCompat.getColor(mLayout.getContext(), R.color.Reel);
                break;
            case "Strathspey":
                lColor = ContextCompat.getColor(mLayout.getContext(), R.color.Strathspey);
                break;
            case "Other":
            default:
                lColor = ContextCompat.getColor(mLayout.getContext(), R.color.Other);
                break;
        }
        if (isSelect()) {
            lColor = ContextCompat.getColor(mLayout.getContext(), R.color.bg_list_selected);
        }
        // do not set the color of the viewholder, but of the parent of its text view
        try {
            View tParentView;
            if (mTV_Name != null) {
                tParentView = (View) mTV_Name.getParent();
                tParentView.setBackgroundColor(lColor);
            }
        } catch(Exception e) {
            Logg.e(TAG, e.toString());
        }

        if (mIV_MusicFile != null) {
            if (mDance.mCountofRecordings > 1) {
                mIV_MusicFile.setImageResource(R.drawable.ic_music_multiple);
                mIV_MusicFile.setVisibility(View.VISIBLE);
            } else if (mDance.mCountofRecordings == 1) {
                mIV_MusicFile.setImageResource(R.drawable.ic_music_single);
                mIV_MusicFile.setVisibility(View.VISIBLE);
            } else if (mDance.mCountofAnyGoodRecordings >= 1) {
                mIV_MusicFile.setImageResource(R.drawable.ic_music_anygood);
                mIV_MusicFile.setVisibility(View.VISIBLE);
            } else {
                mIV_MusicFile.setImageResource(R.drawable.ic_music_none);
                mIV_MusicFile.setVisibility(View.VISIBLE);
            }
        }
        if (mIV_Crib != null) {
            if (mDance.mCountofCribs > 0) {
                mIV_Crib.setVisibility(View.VISIBLE);
            } else {
                mIV_Crib.setVisibility(View.INVISIBLE);
            }
        }
        if (mIV_Diagram != null) {
            if (mDance.mCountofDiagrams > 0) {
                mIV_Diagram.setVisibility(View.VISIBLE);
            } else {
                mIV_Diagram.setVisibility(View.INVISIBLE);
            }
        }
        if (mIV_Rscds != null) {
            if (mDance.mFlagRscds) {
                mIV_Rscds.setVisibility(View.VISIBLE);
            } else {
                mIV_Rscds.setVisibility(View.INVISIBLE);
            }
        }
        if (mIV_DisplayDiagram != null) {
            if (mDance.mCountofDiagrams > 0 && mDiagramDesire >= 4) {
                displayBitmap();
            }

        }
        if (mRV_Scddb_Crib != null) {
            if (mDance.mCountofCribs > 0 && mCribDesire >= 4) {
                mRV_Scddb_Crib.setVisibility(View.VISIBLE);
                if (mCrib_Adapter == null) {
                    mCrib_Adapter = new Crib_Adapter(mLayout.getContext(),
                            mRV_Scddb_Crib, null);
                    RecyclerView.LayoutManager LM_ScddbCrib = new LinearLayoutManager(mLayout.getContext());
                    mRV_Scddb_Crib.setAdapter(mCrib_Adapter);
                    mRV_Scddb_Crib.setLayoutManager(LM_ScddbCrib);
                }
                Crib tCrib;
                if (mCribLoaded_DanceId == 0 || mCribLoaded_DanceId != mDance.mId) {

                    try {
                        tCrib = new Crib(mDance.mId);
                        mCrib_Adapter.setCrib(tCrib);
                        mCribLoaded_DanceId = mDance.mId;
                    } catch(Exception e) {
                        Logg.e(TAG, e.toString());
                    }
                }
                mCrib_Adapter.notifyDataSetChanged();
            }
        }
    }

    private void setListener() {
        mLayout.setOnClickListener(xView -> {
            Logg.k(TAG, "View OnClick");
            toggleSelect();
        });
        if (mIV_Favourite != null) {
            mIV_Favourite.setOnClickListener(iView -> {
                Logg.k(TAG, "IV_Favourite OnClick");
                int[] tLocation = new int[2];
                iView.getLocationOnScreen(tLocation);
                callEditFavourite(tLocation);
            });
        }
        if (mIV_Requestlist != null) {
            mIV_Requestlist.setOnClickListener(iView -> {
                Logg.k(TAG, "IV_Requestlist OnClick");
                if (mDance != null && mDance.mCountofRecordings > 0) {
                    Requestlist_Action.callExecute(mLayout, this,
                            Requestlist_Action.CLICK_TYPE_SHORT, Requestlist_Action.CLICK_ICON_REQUESTLIST,
                            null, mDance, null,null);
                }
            });
            mIV_Requestlist.setOnLongClickListener(iView -> {
                Logg.k(TAG, "IV_Requestlist OnLongClick");
                if (mDance != null && mDance.mCountofRecordings > 0) {
                    Requestlist_Action.callExecute(mLayout, this,
                            Requestlist_Action.CLICK_TYPE_LONG, Requestlist_Action.CLICK_ICON_REQUESTLIST,
                            null, mDance, null,null);
                }
                return true;
            });
        }
        if (mIV_Diagram != null) {
            mIV_Diagram.setOnClickListener(iView -> {
                Logg.k(TAG, "IV_Diagram OnClick");
                switch (mDiagramDesire) {
                    case 0:
                    case 9:
                        break;
                    case 2:
                        mDiagramDesire = 4;
                        break;
                    case 4:
                        mDiagramDesire = 2;
                        break;
                }
                Logg.i(TAG, "new desire " + mDiagramDesire);
                refreshDisplay();
            });

        }
        if (mIV_MusicFile != null) {
            
            mIV_MusicFile.setOnClickListener(view -> {
                Logg.k(TAG, "IV_MusicFile OnClick");
                MusicFile_Action.callExecute(mLayout, this,
                     MusicFile_Action.CLICK_ICON_MUSIC,   MusicFile_Action.SHORT_CLICK,
                        mDance, null);
            });
            mIV_MusicFile.setOnLongClickListener(iView -> {
                Logg.k(TAG, "IV_MusicFile OnLongClick");
                MusicFile_Action.callExecute(mLayout, this,
                        MusicFile_Action.CLICK_ICON_MUSIC,   MusicFile_Action.LONG_CLICK,
                        mDance, null);
                return true;
            });
        }

//
        if (mIV_Crib != null) {
            mIV_Crib.setOnClickListener(iView -> {
                Logg.k(TAG, "IV_Crib OnClick");
                // 0 = not allowed; 2 = single allowed, but not desired, 4 = single allowed and desired, 9 = always, where available
                switch (mCribDesire) {
                    case 0:
                    case 9:
                        break;
                    case 2:
                        mCribDesire = 4;
                        break;
                    case 4:
                        mCribDesire = 2;
                        break;

                }
                refreshDisplay();
            });
        }
        if (mIV_Info != null) {
            mIV_Info.setOnClickListener(view -> {
                Logg.k(TAG, "IV_Info OnClick");
                Context tContext = view.getContext();
                FragmentManager tFragmentManager = ((FragmentActivity) Objects.requireNonNull(tContext)).getSupportFragmentManager();
                DialogFragment_DanceInfo.show(tFragmentManager, mDance);
            });
        }
    }

    private void displayBitmap() {
        if (1 == 1) {
            return;
        }

        //todo
        Bitmap tBitmap;
        tBitmap = mDiagramManager.getBitmap(mDance);
        if (tBitmap != null) {
            //   Logg.i(TAG, "displayBitmap: " + mDance.mId);
            mIV_DisplayDiagram.setVisibility(View.VISIBLE);
            mIV_DisplayDiagram.setImageBitmap(tBitmap);
        } else {
            mIV_DisplayDiagram.setVisibility(View.GONE);
        }
    }


    private boolean isSelect() {
        if (mDance_Adapter == null) {
            return false;
        }
        return mDance_Adapter.mSelectedDanceId > 0 && mDance_Adapter.mSelectedDanceId == mDance.mId;
    }

    private void toggleSelect() {
        //Logg.i(TAG, "toggleSelect");
        boolean tIsSelect = !isSelect();
        if (mShouting != null) {
            JSONObject tJson = new JSONObject();
            String output;
            try {
                tJson.put("Id", mDance.mId);
                tJson.put("Row", getAdapterPosition());
                output = tJson.toString();
                Shout tShout = new Shout("Dance_ViewHolder");
                tShout.mLastObject = "Id";
                if (tIsSelect) {
                    tShout.mLastAction = "selected";
                } else {
                    tShout.mLastAction = "unselected";
                }
                tShout.mJsonString = output;
                mShouting.shoutUp(tShout);
            } catch(JSONException e) {
                Logg.e(TAG, e.toString());
            }
        }
        refreshDisplay();
        //setColors();
    }


    private void callEditFavourite(int[] iLocation) {
        Logg.i(TAG, "EditFavourite");
        Context tContext = mLayout.getContext();
        FragmentManager fm = ((FragmentActivity) Objects.requireNonNull(tContext)).getSupportFragmentManager();
        DialogFragment_EditFavourite t_dialogFragmentEditFavourite =
                DialogFragment_EditFavourite.newInstance(mDance.getFavouriteCode());
        t_dialogFragmentEditFavourite.setShouting(this);
        if (iLocation != null && iLocation.length == 2) {
            t_dialogFragmentEditFavourite.setLocation(iLocation);
        }
        t_dialogFragmentEditFavourite.show(fm, "EditFavourite");
    }

    private void process_shouting() {
        if (mGlassFloor.mActor.equals("DialogFragment_EditFavourite") &&
                mGlassFloor.mLastAction.equals("confirm") &&
                mGlassFloor.mLastObject.equals("DataEntry")) {
            try {
                JSONObject tJsonObject = new JSONObject(mGlassFloor.mJsonString);
                String tCode = tJsonObject.getString("Favourite");
                mDance.setFavouriteCode(tCode);
                // save to DB
                DanceClassification tDanceClassification = new DanceClassification();
                tDanceClassification.setFavourite_Code(mDance.getFavouriteCode());
                tDanceClassification.setObject_Id(mDance.mId);
                tDanceClassification.save();
                // refresh

                refreshDisplay();
            } catch(JSONException e) {
                e.printStackTrace();
            }
        }
//        } else if (mGlassFloor.mActor.equals("DialogFragment_MusicPreference") &&
//                mGlassFloor.mLastAction.equals("changed") &&
//                mGlassFloor.mLastObject.equals("Data")) {
//            Logg.i(TAG, "refresh Dance");
//            mDance = Dance.getById(mDance.mId);
//            refreshDisplay();
//
//        }
    }


    //Interface

    /**
     * Process the request by the adapater to comply with the general expansion of the crib
     * Expansion in Adapter 0 = not allowed, 3 allowed, but not always; 9 = always, where available
     * Desire in ViehHolder 0 = not allowed; 1 = not allowed, but would;  2 = single allowed, but not desired,
     * 4 = single allowed and desired, 9 = always, where available
     *
     * @param iExpansionCrib Expansion Value defined in the adapter
     */
    void processExpansionCrib(int iExpansionCrib) {
        //   Logg.i(TAG, mDance.toShortString());
        switch (iExpansionCrib) {
            case Dance_Adapter.EXPANSION_CRIB_NEVER:
                mCribDesire = 0;
                break;
            case Dance_Adapter.EXPANSION_CRIB_REQUESTED:
                mCribDesire = 9;
                break;
            case Dance_Adapter.EXPANSION_CRIB_ALLOWED:
            default:
                if (mCribDesire != 4)
                    mCribDesire = 2;
        }
    }

    void processExpansionDiagram(int iExpansionDiagram) {
        //  Logg.i(TAG, mDance.toShortString());
        switch (iExpansionDiagram) {
            case Dance_Adapter.EXPANSION_DIAGRAM_NEVER:
                mDiagramDesire = 0;
                break;
            case Dance_Adapter.EXPANSION_DIAGRAM_CLOSE_ALL:
                if (mDiagramDesire >= 4) {
                    mDiagramDesire = 1;
                }
                break;
            case Dance_Adapter.EXPANSION_DIAGRAM_REQUESTED:
                mDiagramDesire = 9;
                break;
            case Dance_Adapter.EXPANSION_DIAGRAM_ALLOWED:
            default:
                if (mDiagramDesire == 1) {
                    mDiagramDesire = 4;
                }
                if (mDiagramDesire != 4)
                    mDiagramDesire = 2;
        }
    }

    @Override
    public void shoutUp(Shout tShoutToCeiling) {
        Logg.i(TAG, tShoutToCeiling.toString());
        mGlassFloor = tShoutToCeiling;
        process_shouting();
    }

//    // @Override
//    public void receiveBitmap(int iWidth, int iDance_Id, Bitmap iBitmap) {
////        if (mIV_DisplayDiagram != null && mIV_DisplayDiagram.getVisibility() == View.VISIBLE) {
////            if (iBitmap != null && iWidth == mDiagramWidth && iDance_Id == mDance.mId) {
////                Logg.i(TAG, "on receive set bitmap");
////                // refresh will get the bitmap directly from the cache
////                new Handler(Looper.getMainLooper()).post(this::refreshDisplay);
////            }
////        }
//
//        //   mIV_Diagram.setBackgroundColor(Color.YELLOW);
//        new Handler(Looper.getMainLooper()).post(this::displayBitmap);
//    }

    public void processSunrise() {
        if (mViewTimer != null) {
            mViewTimer.cancel();

        }
        mViewTimer = new Timer("DelayViewHolder");
        mViewTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                new Handler(Looper.getMainLooper()).post(() -> {
                    //  mIV_Diagram.setBackgroundColor(Color.BLUE);
                    if (mDance != null) {
                        displayBitmap();
                    }
                });

            }
        }, 1000);
    }

    public void processSunset() {
        if (mViewTimer != null) {
            mViewTimer.cancel();
//            new Handler(Looper.getMainLooper()).post(() -> {
//                mIV_Diagram.setBackgroundColor(Color.RED);
//            });

        }
    }

}
