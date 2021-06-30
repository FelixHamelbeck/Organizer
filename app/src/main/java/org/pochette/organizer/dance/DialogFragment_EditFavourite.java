package org.pochette.organizer.dance;

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

import static org.pochette.data_library.scddb_objects.DanceClassification.*;


public class DialogFragment_EditFavourite extends DialogFragment {

    private final static String TAG = "FEHA (DialogFragment_EditFavourite)";

    //Variables
    DialogFragment_EditFavourite m_DialogFragmentEditFavourite;

    Context mContext;

    ImageView mIV_Horrible;
    ImageView mIV_RatherNot;
    ImageView mIV_Unknown;
    ImageView mIV_Neutral;
    ImageView mIV_Okay;
    ImageView mIV_Good;
    ImageView mIV_VeryGood;
//	Button mPB_Cancel;
//	Button mPB_OK;

    int[] mLocation;


    //DanceFavourite mDanceFavourite;
    String mFavourite_Code;

    Shouting mShouting;
    Shout mShoutToCeiling;

    //Constructor
    public DialogFragment_EditFavourite() {
        m_DialogFragmentEditFavourite = this;
    }

    public static DialogFragment_EditFavourite newInstance(String iFavourite_Code) {
        DialogFragment_EditFavourite t_DialogFragmentEditFavourite = new DialogFragment_EditFavourite();
        t_DialogFragmentEditFavourite.mFavourite_Code = iFavourite_Code;
        return t_DialogFragmentEditFavourite;
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
        return inflater.inflate(R.layout.dialog_edit_classification, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mShoutToCeiling = new Shout(this.getClass().getSimpleName());
        mIV_Horrible = view.findViewById(R.id.IV_EditClassification_Favourite_Horrible);
        mIV_RatherNot = view.findViewById(R.id.IV_EditClassification_Favourite_RatherNot);
        mIV_Unknown = view.findViewById(R.id.IV_EditClassification_Favourite_Unknown);
        mIV_Neutral = view.findViewById(R.id.IV_EditClassification_Favourite_Neutral);
        mIV_Okay = view.findViewById(R.id.IV_EditClassification_Favourite_Okay);
        mIV_Good = view.findViewById(R.id.IV_EditClassification_Favourite_Good);
        mIV_VeryGood = view.findViewById(R.id.IV_EditClassification_Favourite_VeryGood);

        this.moveLocation();

        //<editor-fold desc="Listner">
        mIV_Horrible.setOnClickListener(iView -> {
            Logg.k(TAG, "HORR_Button OnClick");
            process("HORR", true);
        });
        mIV_RatherNot.setOnClickListener(iView -> {
            Logg.k(TAG, "RANO_Button OnClick");
            process("RANO", true);
        });
        mIV_Unknown.setOnClickListener(iView -> {
            Logg.k(TAG, "UNKN_Button OnClick");
            process("UNKN", true);
        });
        mIV_Neutral.setOnClickListener(iView -> {
            Logg.k(TAG, "NEUT_Button OnClick");
            process("NEUT", true);
        });
        mIV_Okay.setOnClickListener(iView -> {
            Logg.k(TAG, "OKAY_Button OnClick");
            process("OKAY", true);
        });
        mIV_Good.setOnClickListener(iView -> {
            Logg.k(TAG, "GOOD_Button OnClick");
            process("GOOD", true);
        });
        mIV_VeryGood.setOnClickListener(iView -> {
            Logg.k(TAG, "VYGO_Button OnClick");
            process("VYGO", true);
        });
        mIV_Horrible.setOnLongClickListener(iView -> {
            Logg.k(TAG, "HORR_Button OnLongClick");
            process("HORR", false);
            return true;
        });
        mIV_RatherNot.setOnLongClickListener(iView -> {
            Logg.k(TAG, "RANO_Button OnLongClick");
            process("RANO", false);
            return true;
        });
        mIV_Unknown.setOnLongClickListener(iView -> {
            Logg.k(TAG, "UNKN_Button OnLongClick");
            process("UNKN", false);
            return true;
        });
        mIV_Neutral.setOnLongClickListener(iView -> {
            Logg.k(TAG, "NEUT_Button OnLongClick");
            process("NEUT", false);
            return true;
        });
        mIV_Okay.setOnLongClickListener(iView -> {
            Logg.k(TAG, "OKAY_Button OnLongClick");
            process("OKAY", false);
            return true;
        });
        mIV_Good.setOnLongClickListener(iView -> {
            Logg.k(TAG, "GOOD_Button OnLongClick");
            process("GOOD", false);
            return true;
        });
        mIV_VeryGood.setOnLongClickListener(iView -> {
            Logg.k(TAG, "VYGO_Button OnLongClick");
            process("VYGO", false);
            return true;
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
            lp.x = mLocation[0];        //lp.x = 0;
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


    public void process(String tCode, boolean tFlagShortClick) {
        mFavourite_Code = tCode;
        if (mFavourite_Code != null) {
            Logg.i(TAG, "process" + mFavourite_Code + " flag" + tFlagShortClick);
        }
        confirm();
    }

    private void colorize() {

        int tBg_Standard = ContextCompat.getColor(mContext, R.color.bg_light);
        mIV_Horrible.setBackgroundColor(tBg_Standard);
        mIV_RatherNot.setBackgroundColor(tBg_Standard);
        mIV_Unknown.setBackgroundColor(tBg_Standard);
        mIV_Neutral.setBackgroundColor(tBg_Standard);
        mIV_Okay.setBackgroundColor(tBg_Standard);
        mIV_Good.setBackgroundColor(tBg_Standard);
        mIV_VeryGood.setBackgroundColor(tBg_Standard);
        Drawable tCircleDrawable;
        tCircleDrawable = ResourcesCompat.getDrawable(
                Objects.requireNonNull(this.getContext()).getResources(),
                R.drawable.ic_circleframe,
                this.getContext().getTheme());
        ImageView tImageView = null;
        switch (mFavourite_Code) {
            case HORR:
                tImageView = mIV_Horrible;
                break;
            case RANO:
                tImageView = mIV_RatherNot;
                break;
            case UNKN:
                tImageView = mIV_Unknown;
                break;
            case NEUT:
                tImageView = mIV_Neutral;
                break;
            case OKAY:
                tImageView = mIV_Okay;
                break;
            case GOOD:
                tImageView = mIV_Good;
                break;
            case VYGO:
                tImageView = mIV_VeryGood;
                break;
        }
        if (tImageView != null) {
            tImageView.setBackground(tCircleDrawable);
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
            tJson.put("Favourite", mFavourite_Code);
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
