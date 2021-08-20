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
    LinearLayout mLL_Formation;

    FormationSearch mFormationSearch;

    ArrayList<Formation> mAL_FormationBasedOnRoot;


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
        prepDisplay();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        prepDisplay();
    }
    //</editor-fold>
    //Static Methods
    //Internal Organs

    @SuppressLint("SetTextI18n")
    private void prepDisplay() {
        if (mTV_Selected != null) {
            if (mFormationSearch.getFormationRoot() == null) {
                mTV_Selected.setText("Choose All");
            } else {
                mTV_Selected.setText(mFormationSearch.getFormationRoot().mName);
            }
        }
        if (mFormationSearch.getFormationRoot() == null) {
            prepRoot();
        } else {
            prepFormation();
        }

    }


    void prepRoot() {
        mRV_Formation.setVisibility(View.VISIBLE);
        mLL_Formation.setVisibility(View.GONE);
        mTV_Selected.setVisibility(View.GONE);
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
        // set visibility
        mRV_Formation.setVisibility(View.GONE);
        mLL_Formation.setVisibility(View.VISIBLE);
        mTV_Selected.setVisibility(View.VISIBLE);

        // prep the data and sort desc by count
        mAL_FormationBasedOnRoot = new ArrayList<>(0);
        for (Formation lFormation : mFormationData.getAllFormation()) {
            if (lFormation.mKey.startsWith(mFormationSearch.getFormationRoot().mKey)) {
                mAL_FormationBasedOnRoot.add(lFormation);
            }
        }
        Collections.sort(mAL_FormationBasedOnRoot, new Comparator<Formation>() {
            @Override
            public int compare(Formation o1, Formation o2) {
                return o2.mCountDances - o1.mCountDances;
            }
        });
        Logg.i(TAG, "show " + mAL_FormationBasedOnRoot.size());

        // measure each FormationView and store that view

        int tWidthWidest = 0;

        mAL_FormationView = new ArrayList<>(0);
        for (Formation lFormation : mAL_FormationBasedOnRoot) {
            if (lFormation.mKey.startsWith(mFormationSearch.getFormationRoot().mKey)) {
                FormationView tFormationView;
                tFormationView = new FormationView(getContext());
                LinearLayout.LayoutParams tLayoutParams;
                tLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                tLayoutParams.leftMargin = mMargin;
                tLayoutParams.topMargin = mMargin;
                tFormationView.setLayoutParams(tLayoutParams);
                tFormationView.setKey(lFormation.mKey);
                tFormationView.setText(lFormation.mName);
                tFormationView.setIsRootFormation(lFormation.isRootFormation());
                tFormationView.setCount(lFormation.mCountDances);
                tFormationView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                int tMeasuredWidth = tFormationView.getMeasuredWidth();
                tWidthWidest = Math.max(tWidthWidest, tMeasuredWidth);
                mAL_FormationView.add(tFormationView);
            }
        }

        int tBoxWidth = 4 * mMargin + 3 * tWidthWidest;
        tBoxWidth = Math.min(tBoxWidth, 3 * mWindowWidth / 4);

        ConstraintLayout.LayoutParams tLP;
        tLP = (ConstraintLayout.LayoutParams) mLL_Formation.getLayoutParams();
        tLP.width = tBoxWidth;
        tLP.leftMargin = mMargin;
        tLP.rightMargin = mMargin;
        tLP.topMargin = mMargin;
        tLP.bottomMargin = mMargin;


        LinearLayout tCurrentLine = null;

        int tUsedWidth = 0;
        for (FormationView lView : mAL_FormationView) {
            int tHypothecialWidth = tUsedWidth + lView.getMeasuredWidth() + 2 * mMargin;
            if (tHypothecialWidth > tBoxWidth) {
                tCurrentLine = null;
            }
            if (tCurrentLine == null) {
                Logg.i(TAG, "new line");
                tCurrentLine = new LinearLayout(getContext());
                tCurrentLine.setId(View.generateViewId());
                tCurrentLine.setOrientation(HORIZONTAL);
                tCurrentLine.setBackground(null);
                tCurrentLine.setLayoutParams(tLP);
                mLL_Formation.addView(tCurrentLine);
                tUsedWidth = mMargin;
            }
            tCurrentLine.addView(lView);
            lView.setShouting(this);
            tUsedWidth += lView.getMeasuredWidth() + 2 * mMargin;

        }

        refresh();
        if (mTV_Selected != null) {
            mTV_Selected.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Logg.w(TAG, "call select ll");
                            selectAll();
                            refresh();
                            //prepDisplay();
                        }
                    }
            );
        }
    }

    void refresh() {
        Logg.i(TAG, "refresh");
        if(mAL_FormationView != null && mAL_FormationView.size() > 0){
            for (FormationView lFormationView : mAL_FormationView) {
                lFormationView.refresh();
            }
        }
        if (mIV_Confirm != null) {
            if (mAL_FormationView != null && mFormationSearch.getAL_Formation().size() > 0) {
                mIV_Confirm.setVisibility(View.VISIBLE);
            } else {
                mIV_Confirm.setVisibility(View.GONE);
            }
        }
    }

    void selectAll() {
        for (Formation lFormation : mAL_FormationBasedOnRoot) {
            mFormationSearch.addFormation(lFormation);
        }
        for (FormationView lFormationView : mAL_FormationView) {
            lFormationView.setSelected(true);
        }
    }

    void processNewSelection() {
        Logg.i(TAG, "start processNewSelection");
        boolean tAtLeastOneSelected = false;
        ArrayList<Formation> tAL_Formation = new ArrayList<>(0);
        for (FormationView lFormationView : mAL_FormationView) {
            if (lFormationView.isSelected()) {
                Formation lFormation = mFormationData.getFormationByKey(lFormationView.getKey());
                tAL_Formation.add(lFormation);
                tAtLeastOneSelected = true;
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
        // the root has been choosenm
        if (mGlassFloor.mActor.equals("FormationAdapter") &&
                mGlassFloor.mLastAction.equals("selected") &&
                mGlassFloor.mLastObject.equals("Formation")) {
            try {
                JSONObject tJsonObject = new JSONObject(mGlassFloor.mJsonString);
                String tKey = tJsonObject.getString("Key");
                mFormationSearch.setFormationRoot(mFormationData.getFormationRootByKey(tKey));
                prepFormation();
            } catch(JSONException e) {
                e.printStackTrace();
            }
        }

        if (mGlassFloor.mActor.equals("FormationView") &&
                mGlassFloor.mLastAction.equals("changed") &&
                mGlassFloor.mLastObject.equals("Selection")) {
            processNewSelection();
            refresh();
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
        Logg.w(TAG, tShoutToCeiling.toString());
        mGlassFloor = tShoutToCeiling;
        process_shouting();
    }

}
