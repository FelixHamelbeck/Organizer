package org.pochette.organizer.music;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import org.json.JSONException;
import org.json.JSONObject;
import org.pochette.organizer.R;
import org.pochette.utils_lib.logg.Logg;
import org.pochette.utils_lib.shouting.Shout;
import org.pochette.utils_lib.shouting.Shouting;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

public class DialogFragment_EditPurpose extends DialogFragment {

    private final static String TAG = "FEHA (DialogFragment_EditPurpose)";
    //Variables
    DialogFragment_EditPurpose m_DialogFragmentEditPurpose;
    Context mContext;
    ImageView mIV_SCD;
    ImageView mIV_WarmUpCoolDown;
    ImageView mIV_StepPractise;
    ImageView mIV_ClassMixed;
    ImageView mIV_CelticListening;
    ImageView mIV_Listening;
    ImageView mIV_Unknown;
//	Button mPB_Cancel;
//	Button mPB_OK;

    int[] mLocation;


    //DanceFavourite mDanceFavourite;
    String mPurpose_Code;

    Shouting mShouting;
    Shout mShoutToCeiling;

    //Constructor
    public DialogFragment_EditPurpose() {
        m_DialogFragmentEditPurpose = this;
    }

    public static DialogFragment_EditPurpose newInstance(String iPurpose_Code) {
        DialogFragment_EditPurpose t_DialogFragmentEditPurpose = new DialogFragment_EditPurpose();
        t_DialogFragmentEditPurpose.mPurpose_Code = iPurpose_Code;
        return t_DialogFragmentEditPurpose;
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

    public void setLocation(int[] iLocation) {
        if (iLocation != null && iLocation.length == 2) {
            mLocation = iLocation;
        } else {
            mLocation = null;
        }
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
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = getContext();
        this.moveLocation();
        return inflater.inflate(R.layout.dialog_edit_purpose, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mShoutToCeiling = new Shout(this.getClass().getSimpleName());
        mIV_SCD = view.findViewById(R.id.IV_EditPurpose_SCD);
        mIV_WarmUpCoolDown = view.findViewById(R.id.IV_EditPurpose_WarmuUpCoolDown);
        mIV_StepPractise = view.findViewById(R.id.IV_EditPurpose_StepPractise);
        mIV_ClassMixed = view.findViewById(R.id.IV_EditPurpose_ClassMixed);
        mIV_CelticListening = view.findViewById(R.id.IV_EditPurpose_CelticListening);
        mIV_Listening = view.findViewById(R.id.IV_EditPurpose_Listening);
        mIV_Unknown = view.findViewById(R.id.IV_EditPurpose_Unknown);

        this.moveLocation();

        //<editor-fold desc="Listner">
        mIV_SCD.setOnClickListener(iView -> {
            Logg.k(TAG, "SCD_Button OnClick");
            process("SCD", true);
        });
        mIV_WarmUpCoolDown.setOnClickListener(iView -> {
            Logg.k(TAG, "WUCD_Button OnClick");
            process("WUCD", true);
        });
        mIV_StepPractise.setOnClickListener(iView -> {
            Logg.k(TAG, "STEP_Button OnClick");
            process("STEP", true);
        });
        mIV_ClassMixed.setOnClickListener(iView -> {
            Logg.k(TAG, "CLMX_Button OnClick");
            process("CLMX", true);
        });
        mIV_CelticListening.setOnClickListener(iView -> {
            Logg.k(TAG, "CELC_Button OnClick");
            process("CELC", true);
        });
        mIV_Listening.setOnClickListener(iView -> {
            Logg.k(TAG, "LISG_Button OnClick");
            process("LISG", true);
        });
        mIV_Unknown.setOnClickListener(iView -> {
            Logg.k(TAG, "UNKN_Button OnClick");
            process("UNKN", true);
        });

        //</editor-fold>
        colorize();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        this.moveLocation();
    }

    @Override
    public void onResume() {
        super.onResume();
        this.moveLocation();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
    //</editor-fold>

    //Static Methods
    //Internal Organs
    private void moveLocation() {
        final Window dialogWindow = Objects.requireNonNull(getDialog()).getWindow();
        if (mLocation != null) {
            WindowManager.LayoutParams lp = Objects.requireNonNull(dialogWindow).getAttributes();
            lp.gravity = Gravity.TOP | Gravity.START;
            lp.x = mLocation[0];        // set your SearchRetrieval position here
            lp.y = mLocation[1];        // set your Y position here
            dialogWindow.setAttributes(lp);
        }
    }

    @Override
    public void dismiss() {
        mShoutToCeiling.mLastObject = "DataEntry";
        mShoutToCeiling.mLastAction = "cancel";
        mShouting.shoutUp(mShoutToCeiling);
        super.dismiss();
    }

    public void process(String tCode, @SuppressWarnings("unused") boolean tFlagShortClick) {
        mPurpose_Code = tCode;
        confirm();
    }

    private void colorize() {
        @SuppressWarnings("unused") int tBg_Emphasis = ContextCompat.getColor(mContext, R.color.bg_emphasis);
        int tBg_Standard = ContextCompat.getColor(mContext, R.color.bg_light);
        mIV_SCD.setBackgroundColor(tBg_Standard);
        mIV_WarmUpCoolDown.setBackgroundColor(tBg_Standard);
        mIV_StepPractise.setBackgroundColor(tBg_Standard);
        mIV_ClassMixed.setBackgroundColor(tBg_Standard);
        mIV_CelticListening.setBackgroundColor(tBg_Standard);
        mIV_Listening.setBackgroundColor(tBg_Standard);
        mIV_Unknown.setBackgroundColor(tBg_Standard);
        Drawable tCircleDrawable;

        tCircleDrawable = ResourcesCompat.getDrawable(Objects.requireNonNull(this.getContext()).getResources(),
                R.drawable.ic_circleframe,
                Objects.requireNonNull(this.getContext()).getTheme());
        ImageView tImageView;
        if (mPurpose_Code != null) {
            switch (mPurpose_Code) {
                case "SCD":
                    tImageView = mIV_SCD;
                    break;
                case "WUCD":
                    tImageView = mIV_WarmUpCoolDown;
                    break;
                case "STEP":
                    tImageView = mIV_StepPractise;
                    break;
                case "CLMX":
                    tImageView = mIV_ClassMixed;
                    break;
                case "CELC":
                    tImageView = mIV_CelticListening;
                    break;
                case "LISG":
                    tImageView = mIV_Listening;
                    break;
                case "UNKN":
                    tImageView = mIV_Unknown;
                    break;
                default:
                    tImageView = null;
            }
            if (tImageView != null) {
                tImageView.setBackground(tCircleDrawable);
            }
        }


    }


    public void confirm() {
        // Shout
        if (mShouting == null) {
            Logg.e(TAG, "No Upstairs Shouting defined");
            return;
        }
        mShoutToCeiling.mLastObject = "DataEntry";
        mShoutToCeiling.mLastAction = "confirm";
        JSONObject tJson = new JSONObject();
        String output;
        try {
            tJson.put("Purpose", mPurpose_Code);
        } catch(JSONException e) {
            Logg.e(TAG, e.toString());
        }
        output = tJson.toString();
        mShoutToCeiling.mJsonString = output;
        mShouting.shoutUp(mShoutToCeiling);
        super.dismiss();
    }
    //Interface


}
