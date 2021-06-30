package org.pochette.organizer.pairing;

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
import org.pochette.organizer.BuildConfig;
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


public class DialogFragment_EditPairingStatus extends DialogFragment {

    private final static String TAG = "FEHA (DF_EditPairingStatus)";

    //Variables
    DialogFragment_EditPairingStatus m_DialogFragmentEditPairingStatus;

    Context mContext;

    ImageView mIV_Confirmed;
    ImageView mIV_Candidate;
    ImageView mIV_Rejected;
    int[] mLocation;
    //DanceFavourite mDanceFavourite;
    String mStatus_Code;

    Shouting mShouting;
    Shout mShoutToCeiling;

    //Constructor
    public DialogFragment_EditPairingStatus() {
        m_DialogFragmentEditPairingStatus = this;
    }

    public static DialogFragment_EditPairingStatus newInstance(String iStatus_Code) {
        DialogFragment_EditPairingStatus t_DialogFragmentEditPairingStatus = new DialogFragment_EditPairingStatus();
        t_DialogFragmentEditPairingStatus.mStatus_Code = iStatus_Code;
        return t_DialogFragmentEditPairingStatus;
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
        return inflater.inflate(R.layout.dialog_edit_pairingstatus, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mShoutToCeiling = new Shout(this.getClass().getSimpleName());
        mIV_Confirmed = view.findViewById(R.id.IV_EditPairingStatus_Confirmed);
        mIV_Candidate = view.findViewById(R.id.IV_EditPairingStatus_Candidate);
        mIV_Rejected = view.findViewById(R.id.IV_EditPairingStatus_Rejected);

        this.moveLocation();

        //<editor-fold desc="Listner">
        mIV_Confirmed.setOnClickListener(iView -> {
            {
                Logg.k(TAG, "Confirmed_Button OnClick");
                process("CONFIRMED", true);
            }
        });
        mIV_Candidate.setOnClickListener(iView -> {
            Logg.k(TAG, "Candidate_Button OnClick");
            process("CANDIDATE", true);
        });

        mIV_Rejected.setOnClickListener(iView -> {
            Logg.k(TAG, "Rejected_Button OnClick");
            process("REJECTED", true);
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

    void moveLocation() {
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

    @SuppressWarnings("SameParameterValue")
    void process(String tCode, boolean tFlagShortClick) {
        mStatus_Code = tCode;
        if (mStatus_Code != null) {
            if (BuildConfig.DEBUG) {
                Logg.d(TAG, "process" + mStatus_Code + " flag" + tFlagShortClick);
            }

        }
        confirm();
    }

    void colorize() {
        @SuppressWarnings("unused") int tBg_Emphasis = ContextCompat.getColor(mContext, R.color.bg_emphasis);
        int tBg_Standard = ContextCompat.getColor(mContext, R.color.bg_light);
        mIV_Confirmed.setBackgroundColor(tBg_Standard);
        mIV_Candidate.setBackgroundColor(tBg_Standard);
        mIV_Rejected.setBackgroundColor(tBg_Standard);

        Drawable tCircleDrawable;
        tCircleDrawable = ResourcesCompat.getDrawable(
                Objects.requireNonNull(this.getContext()).getResources(),
                R.drawable.ic_circleframe,
                this.getContext().getTheme());
        ImageView tImageView;
        if (mStatus_Code != null) {

            switch (mStatus_Code) {
                case "CONFIRMED":
                    tImageView = mIV_Confirmed;
                    break;
                case "CANDIDATE":
                    tImageView = mIV_Candidate;
                    break;
                case "REJECTED":
                    tImageView = mIV_Rejected;
                    break;
                default:
                    tImageView = null;
            }
            if (tImageView != null) {
                tImageView.setBackground(tCircleDrawable);
            }
        }
    }

    void confirm() {
        // Shout
        if (mShouting == null) {
            Logg.w(TAG, "No Upstairs Shouting defined");
            return;
        }
        mShoutToCeiling.mLastObject = "DataEntry";
        mShoutToCeiling.mLastAction = "confirm";
        JSONObject tJson = new JSONObject();
        String output;
        try {
            tJson.put("Status", mStatus_Code);
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
