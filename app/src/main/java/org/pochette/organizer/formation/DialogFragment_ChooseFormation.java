package org.pochette.organizer.formation;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.pochette.data_library.scddb_objects.Formation;
import org.pochette.data_library.scddb_objects.FormationRoot;
import org.pochette.organizer.R;
import org.pochette.utils_lib.logg.Logg;
import org.pochette.utils_lib.shouting.Shout;
import org.pochette.utils_lib.shouting.Shouting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.widget.LinearLayout.HORIZONTAL;

public class DialogFragment_ChooseFormation extends DialogFragment implements Shouting {

    private final String TAG = "FEHA (DF_ChooseFormation)";
    //Variables
    Context mContext;

    TextView mTV_Title;
    ImageView mIV_Confirm;
    TextView mTV_Selected;
    RecyclerView mRV_Formation;
    //   ScrollView mSC_Formation;
    LinearLayout mLL_Formation;

    FormationSearch mFormationSearch;


    int mMargin;
    int mWindowWidth = 0;

    FormationData mFormationData;
    FormationAdapter mFormationAdapter;
    ArrayList<FormationView> mAL_FormationView;

    Shouting mShouting;
    private Shout mGlassFloor;

    //Constructor
    private DialogFragment_ChooseFormation(FormationSearch iFormationSearch) {
        if (iFormationSearch == null) {
            mFormationSearch = new FormationSearch();
        } else {
            mFormationSearch = iFormationSearch;
        }
        mFormationData = new FormationData();
    }

    public static DialogFragment_ChooseFormation newInstance(FormationSearch iFormationSearch) {
        return new DialogFragment_ChooseFormation(iFormationSearch);
    }

    //Setter and Getter


    public void setTargetFragment(Fragment iFragment, int iRequestCode) {
        super.setTargetFragment(iFragment, iRequestCode);
        try {
            mShouting = (Shouting) iFragment;
        } catch(Exception e) {
            Logg.e(TAG, e.toString());
        }
    }

    public void setShouting(Shouting tShouting) {
        mShouting = tShouting;
    }



    //<editor-fold desc="Live Cylce">
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog tDialog = super.onCreateDialog(savedInstanceState);
        mMargin = 10;
        return tDialog;

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = getContext();
        return inflater.inflate(R.layout.dialog_choose_formation, container);
    }


    @Override
    public void onViewCreated(@NonNull View iView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(iView, savedInstanceState);

        mTV_Title = iView.findViewById(R.id.TV_DialogFormationTitle);
        mTV_Selected = iView.findViewById(R.id.TV_DialogFormationSelected);
        mIV_Confirm = iView.findViewById(R.id.IV_DialogFormationSelected_Confirm);
        mRV_Formation = iView.findViewById(R.id.RV_DialogFormation);
        mLL_Formation = iView.findViewById(R.id.LL_DialogFormation);

        Display display = Objects.requireNonNull(Objects.requireNonNull(this.getDialog()).getWindow()).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        mWindowWidth = size.x;

        if (mIV_Confirm != null) {
            mIV_Confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    confirm();
                }
            });
        }
        Objects.requireNonNull(mIV_Confirm).setVisibility(View.GONE);
        refreshDisplay();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshDisplay();
    }
    //</editor-fold>
    //Static Methods
    //Internal Organs

    @SuppressLint("SetTextI18n")
    private void refreshDisplay() {
        if (mTV_Selected != null) {
            if (mFormationSearch.getFormationRoot() == null) {
                mTV_Selected.setText("Choose Root");
            } else {
                mTV_Selected.setText(mFormationSearch.getFormationRoot().mName);
            }
        }
        if (mFormationSearch.getFormationRoot() == null) {
            prepRoot();
        } else {
            prepFormation();
        }
        if (mIV_Confirm != null) {
            if (mFormationSearch.getAL_Formation().size() > 0) {
                mIV_Confirm.setVisibility(View.VISIBLE);
            } else {
                mIV_Confirm.setVisibility(View.GONE);
            }
        }
    }


    void prepRoot() {
        mRV_Formation.setVisibility(View.VISIBLE);
        mLL_Formation.setVisibility(View.GONE);
        if (mFormationAdapter == null) {
            mFormationAdapter = new FormationAdapter(getContext(), mRV_Formation, this);
        }
        RecyclerView.LayoutManager LM_ScddbCrib = new LinearLayoutManager(getActivity());
        mRV_Formation.setAdapter(mFormationAdapter);
        mRV_Formation.setLayoutManager(LM_ScddbCrib);
        ArrayList<FormationRoot> tAL_FormationRoot = mFormationData.getAllFormatianRoot();
        mFormationAdapter.setAL_FormationRoot(tAL_FormationRoot);
        mFormationAdapter.notifyDataSetChanged();
    }

    void prepFormation() {

        mRV_Formation.setVisibility(View.GONE);
        mLL_Formation.setVisibility(View.VISIBLE);
        if (mFormationSearch.getFormationRoot() != null) {
            ArrayList<Formation> tAL_FormationBasedOnRoot = new ArrayList<>(0);
            LinearLayout tCurrentLine;
            int tUsedWidth;
            tCurrentLine = new LinearLayout(getContext());
            tCurrentLine.setId(View.generateViewId());
            tCurrentLine.setOrientation(HORIZONTAL);
            tCurrentLine.setBackground(null);
            mLL_Formation.addView(tCurrentLine);
            FormationView tFormationView;
            // find the longest text
            int tWidthWidest = 0;
            LinearLayout.LayoutParams tLayoutParams;
            tLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            tLayoutParams.leftMargin = mMargin;
            tLayoutParams.topMargin = mMargin;

            for (Formation lFormation : mFormationData.getAllFormation()) {
                if (lFormation.mKey.startsWith(mFormationSearch.getFormationRoot().mKey)) {
                    tAL_FormationBasedOnRoot.add(lFormation);
                    tFormationView = new FormationView(getContext());
                    tFormationView.setLayoutParams(tLayoutParams);
                    tFormationView.setKey(lFormation.mKey);
                    tFormationView.setText(lFormation.mName);
                    tFormationView.setCount(lFormation.mCountDances);
                    tFormationView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                    int tMeasuredWidth = tFormationView.getMeasuredWidth();
                    tWidthWidest = Math.max(tWidthWidest, tMeasuredWidth);
                }
            }

            int tBoxWidth = 4 * mMargin + 3 * tWidthWidest;
            tBoxWidth = Math.min(tBoxWidth, 3 * mWindowWidth / 4);
            Collections.sort(tAL_FormationBasedOnRoot, new Comparator<Formation>() {
                @Override
                public int compare(Formation o1, Formation o2) {
                    return o2.mCountDances - o1.mCountDances;
                }

            });
            ConstraintLayout.LayoutParams tLP;
            tLP = (ConstraintLayout.LayoutParams) mLL_Formation.getLayoutParams();
            tLP.width = tBoxWidth;
            tLP.leftMargin = mMargin;
            tLP.rightMargin = mMargin;
            tLP.topMargin = mMargin;
            tLP.bottomMargin = mMargin;

            mLL_Formation.setLayoutParams(tLP);

            tUsedWidth = mMargin;
            mLL_Formation.removeAllViews();
            mAL_FormationView = new ArrayList<>(0);
            for (Formation lFormation : tAL_FormationBasedOnRoot) {
                tFormationView = new FormationView(getContext());
                tFormationView.setShouting(this);
                tFormationView.setLayoutParams(tLayoutParams);
                tFormationView.setKey(lFormation.mKey);
                tFormationView.setText(lFormation.mName);
                tFormationView.setCount(lFormation.mCountDances);
                tFormationView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

                tUsedWidth += tFormationView.getMeasuredWidth() + mMargin;
                if (tUsedWidth > tBoxWidth) {
                    tCurrentLine = new LinearLayout(getContext());
                    tCurrentLine.setId(View.generateViewId());
                    tCurrentLine.setOrientation(HORIZONTAL);
                    tCurrentLine.setBackground(null);
                    mLL_Formation.addView(tCurrentLine);
                    tUsedWidth = mMargin;
                    tUsedWidth += tFormationView.getMeasuredWidth() + mMargin;
                }
                tCurrentLine.addView(tFormationView);
                mAL_FormationView.add(tFormationView);
                if (mFormationSearch != null) {
                    for (Formation kFormation : mFormationSearch.getAL_Formation()) {
                        if (lFormation.mKey.equals(kFormation.mKey)) {
                            tFormationView.setSelected(true);
                            tFormationView.refresh();
                        }
                    }
                }
            }
        }
    }

    void processNewSelection() {
        boolean tAtLeastOneSelected = false;
        ArrayList<Formation> tAL_Formation = new ArrayList<>(0);
        for (FormationView lFormationView : mAL_FormationView) {
            if (lFormationView.isSelected()) {
                Formation lFormation = mFormationData.getFormationByKey(lFormationView.getKey());
                tAL_Formation.add(lFormation);
                tAtLeastOneSelected= true;
            }
        }
        mFormationSearch.setAL_Formation(tAL_Formation);
        if (tAtLeastOneSelected) {
            mIV_Confirm.setVisibility(View.VISIBLE);
        } else {
            mIV_Confirm.setVisibility(View.GONE);
        }
    }

    void process_shouting() {
        if (mGlassFloor.mActor.equals("FormationAdapter") &&
                mGlassFloor.mLastAction.equals("selected") &&
                mGlassFloor.mLastObject.equals("Formation")) {
            try {
                JSONObject tJsonObject = new JSONObject(mGlassFloor.mJsonString);
                String tKey = tJsonObject.getString("Key");
                mFormationSearch.setFormationRoot(mFormationData.getFormationRootByKey(tKey));
                prepFormation();
//                refreshDisplay();
            } catch(JSONException e) {
                e.printStackTrace();
            }
        }

        if (mGlassFloor.mActor.equals("FormationView") &&
                mGlassFloor.mLastAction.equals("changed") &&
                mGlassFloor.mLastObject.equals("Selection")) {
            processNewSelection();
        }
    }






    @Override
    public void dismiss() {
        super.dismiss();
    }


    public void confirm() {
        if (mShouting != null) {
            if (mFormationSearch.getTS_Id() != null) {
                Shout tShout = new Shout(this.getClass().getSimpleName());
                tShout.mLastObject = "TreeSetOfId";
                tShout.mLastAction = "selected";
                tShout.mJsonString = mFormationSearch.toJson();
                mShouting.shoutUp(tShout);
            }
        }
        super.dismiss();
    }
    //Interface

    @Override
    public void shoutUp(Shout tShoutToCeiling) {
        Logg.i(TAG, tShoutToCeiling.toString());
        mGlassFloor = tShoutToCeiling;
        process_shouting();
    }

}
