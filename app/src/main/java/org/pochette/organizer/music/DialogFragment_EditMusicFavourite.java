package org.pochette.organizer.music;

import android.app.Dialog;
import android.content.Context;
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
import org.pochette.data_library.music.MusicPreferenceFavourite;
import org.pochette.organizer.R;
import org.pochette.utils_lib.logg.Logg;
import org.pochette.utils_lib.shouting.Shout;
import org.pochette.utils_lib.shouting.Shouting;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;


public class DialogFragment_EditMusicFavourite extends DialogFragment {

    private final String TAG = "FEHA (DF_EditMusicFavourite)";
    //Variables
    DialogFragment_EditMusicFavourite m_DialogFragmentEditFavourite;

    Context mContext;

    ImageView mIV_NotOK;
    ImageView mIV_Unknown;
    ImageView mIV_OddRepeat;
    ImageView mIV_Neutral;
    ImageView mIV_AnyGood;
    ImageView mIV_GoodRepeat;
    ImageView mIV_Okay;
    ImageView mIV_Good;
    ImageView mIV_VeryGood;
    ImageView mIV_Today;

    int[] mLocation;

    MusicPreferenceFavourite mMusicPreferenceFavourite;

    Shouting mShouting;
    Shout mShoutToCeiling;
    @SuppressWarnings("unused")
    private Shout mGlassFloor;

    //Constructor
    public DialogFragment_EditMusicFavourite() {
        m_DialogFragmentEditFavourite = this;
    }

    public static DialogFragment_EditMusicFavourite newInstance(MusicPreferenceFavourite iMusicPreferenceFavourite) {
        DialogFragment_EditMusicFavourite t_DialogFragmentEditFavourite = new DialogFragment_EditMusicFavourite();
        if (iMusicPreferenceFavourite == null) {
            t_DialogFragmentEditFavourite.mMusicPreferenceFavourite = MusicPreferenceFavourite.NEUTRAL;
        } else {
            t_DialogFragmentEditFavourite.mMusicPreferenceFavourite = iMusicPreferenceFavourite;
        }
        return t_DialogFragmentEditFavourite;
    }

    //Setter and Getter

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
        return inflater.inflate(R.layout.dialog_edit_music_favourite, container);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mShoutToCeiling = new Shout(this.getClass().getSimpleName());
        mIV_NotOK = view.findViewById(R.id.IV_EditMusicFavourite_Favourite_NotOk);
        mIV_Unknown = view.findViewById(R.id.IV_EditMusicFavourite_Favourite_Unknown);
        mIV_OddRepeat = view.findViewById(R.id.IV_EditMusicFavourite_Favourite_OddRepeat);
        mIV_Neutral = view.findViewById(R.id.IV_EditMusicFavourite_Favourite_Neutral);
        mIV_AnyGood = view.findViewById(R.id.IV_EditMusicFavourite_Favourite_AnyGood);
        mIV_GoodRepeat = view.findViewById(R.id.IV_EditMusicFavourite_Favourite_GoodRepeat);
        mIV_Okay = view.findViewById(R.id.IV_EditMusicFavourite_Favourite_Okay);
        mIV_Good = view.findViewById(R.id.IV_EditMusicFavourite_Favourite_Good);
        mIV_VeryGood = view.findViewById(R.id.IV_EditMusicFavourite_Favourite_VeryGood);
        mIV_Today = view.findViewById(R.id.IV_EditMusicFavourite_Favourite_Today);

        this.moveLocation();
        //<editor-fold desc="Listner">
        mIV_NotOK.setOnClickListener(iView -> {
            Logg.k(TAG, "NotOK_Button OnClick");
            process("NOOK", true);
        });
        mIV_Unknown.setOnClickListener(iView -> {
            Logg.k(TAG, "UNKN_Button OnClick");
            process("UNKN", true);
        });
        mIV_OddRepeat.setOnClickListener(iView -> {
            Logg.k(TAG, "REOD_Button OnClick");
            process("REOD", true);
        });
        mIV_Neutral.setOnClickListener(iView -> {
            Logg.k(TAG, "NEUT_Button OnClick");
            process("NEUT", true);
        });
        mIV_AnyGood.setOnClickListener(iView -> {
            Logg.k(TAG, "ANYG_Button OnClick");
            process("ANYG", true);
        });
        mIV_GoodRepeat.setOnClickListener(iView -> {
            Logg.k(TAG, "REGO_Button OnClick");
            process("REGO", true);
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
        mIV_Today.setOnClickListener(iView -> {
            Logg.k(TAG, "TODA_Button OnClick");
            process("TODA", true);
        });
        //</editor-fold>
        colorize();
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
    public void dismiss() {
        mShoutToCeiling.mLastObject = "DataEntry";
        mShoutToCeiling.mLastAction = "cancel";
        mShouting.shoutUp(mShoutToCeiling);
        super.dismiss();
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
            //lp.x = 0;
            lp.y = mLocation[1];        // set your Y position here
            dialogWindow.setAttributes(lp);
        }
    }

    void process(String tCode, @SuppressWarnings({"SameParameterValue", "unused"}) boolean tFlagShortClick) {
        mMusicPreferenceFavourite = MusicPreferenceFavourite.fromCode(tCode);
        confirm();
    }

    private void colorize() {

        int tBg_Emphasis = ContextCompat.getColor(mContext, R.color.bg_emphasis);
        int tBg_Standard = ContextCompat.getColor(mContext, R.color.bg_light);
        mIV_NotOK.setBackgroundColor(tBg_Standard);
        mIV_Unknown.setBackgroundColor(tBg_Standard);
        mIV_OddRepeat.setBackgroundColor(tBg_Standard);
        mIV_Neutral.setBackgroundColor(tBg_Standard);
        mIV_AnyGood.setBackgroundColor(tBg_Standard);
        mIV_GoodRepeat.setBackgroundColor(tBg_Standard);
        mIV_Okay.setBackgroundColor(tBg_Standard);
        mIV_Good.setBackgroundColor(tBg_Standard);
        mIV_VeryGood.setBackgroundColor(tBg_Standard);
        mIV_Today.setBackgroundColor(tBg_Standard);
        switch (mMusicPreferenceFavourite) {
            case NOT_OK:
                mIV_NotOK.setBackgroundColor(tBg_Emphasis);
                break;
            case UNKNOWN:
                mIV_Unknown.setBackgroundColor(tBg_Emphasis);
                break;
            case REOD:
                mIV_OddRepeat.setBackgroundColor(tBg_Emphasis);
                break;
            case NEUTRAL:
                mIV_Neutral.setBackgroundColor(tBg_Emphasis);
                break;
            case ANYG:
                mIV_AnyGood.setBackgroundColor(tBg_Emphasis);
                break;
            case REGO:
                mIV_GoodRepeat.setBackgroundColor(tBg_Emphasis);
                break;
            case OKAY:
                mIV_Okay.setBackgroundColor(tBg_Emphasis);
                break;
            case GOOD:
                mIV_Good.setBackgroundColor(tBg_Emphasis);
                break;
            case VERY_GOOD:
                mIV_VeryGood.setBackgroundColor(tBg_Emphasis);
                break;
            case TODAY:
                mIV_Today.setBackgroundColor(tBg_Emphasis);
                break;
        }
    }

    public void confirm() {
        if (mShouting == null) {
            Logg.e(TAG, "No Upstairs Shouting defined");
            return;
        }
        mShoutToCeiling.mLastObject = "DataEntry";
        mShoutToCeiling.mLastAction = "confirm";
        JSONObject tJson = new JSONObject();
        String output;
        try {
            tJson.put("Favourite", mMusicPreferenceFavourite.getCode());
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
