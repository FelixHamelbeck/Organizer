package org.pochette.organizer.mediaplayer;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import org.pochette.organizer.app.MediaPlayerServiceSingleton;
import org.pochette.organizer.R;
import org.pochette.utils_lib.logg.Logg;
import org.pochette.utils_lib.shouting.Shout;
import org.pochette.utils_lib.shouting.Shouting;

import java.util.Locale;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


public class DialogFragment_PlayerControl extends DialogFragment {

    private final String TAG = "FEHA (DF_EditMusicFavourite)";
    //Variables
    DialogFragment_PlayerControl m_DialogFragmentEditFavourite;
    MediaPlayerControl_Fragment mMediaPlayerControl_Fragment;

    Context mContext;
    int[] mLocation;

    Shouting mShouting;
    Shout mShoutToCeiling;

    //Constructor
    public DialogFragment_PlayerControl() {
        m_DialogFragmentEditFavourite = this;
    }

    public static DialogFragment_PlayerControl newInstance() {
        return new DialogFragment_PlayerControl();
    }

    public void setShouting(Shouting tShouting) {
        mShouting = tShouting;
    }

    @SuppressWarnings("unused")
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
        if (mMediaPlayerControl_Fragment == null) {
            mMediaPlayerControl_Fragment = new MediaPlayerControl_Fragment();
        }
        if (mMediaPlayerControl_Fragment.mMediaPlayerService == null) {
            mMediaPlayerControl_Fragment.setMediaPlayerService(
                    MediaPlayerServiceSingleton.getInstance().getMediaPlayerService());
            mMediaPlayerControl_Fragment.setShowTextInfo(false);
        }
        FragmentManager tFragmentManager = getChildFragmentManager();
        FragmentTransaction tFragmentTransaction = tFragmentManager.beginTransaction();
        tFragmentTransaction.add(R.id.DialogEmpty_FL, mMediaPlayerControl_Fragment);
        tFragmentTransaction.commit();
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup iViewGroup,
                             Bundle savedInstanceState) {
        mContext = getContext();
        this.moveLocation();
        return inflater.inflate(R.layout.dialog_empty, iViewGroup);
    }

    @Override
    public void onViewCreated(@NonNull View iView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(iView, savedInstanceState);
        Logg.i(TAG, "onViewCreated");
        mShoutToCeiling = new Shout(this.getClass().getSimpleName());
    }

    @Override
    public void onStart() {
        super.onStart();
        // safety check
        if (getDialog() == null) {
            return;
        }
        int dialogWidth = 400;
        int dialogHeight = 600;
        Objects.requireNonNull(getDialog().getWindow()).setLayout(dialogWidth, dialogHeight);
        this.moveLocation();
    }

    @Override
    public void onResume() {
        super.onResume();
        this.moveLocation();
    }
    //</editor-fold>

    //Static Methods
    //Internal Organs
    private void moveLocation() {
        final Window dialogWindow = Objects.requireNonNull(getDialog()).getWindow();
        Logg.i(TAG, "moveLocation");
        if (mLocation != null) {
            WindowManager.LayoutParams lp = Objects.requireNonNull(dialogWindow).getAttributes();
            lp.gravity = Gravity.TOP | Gravity.START;
            lp.x = mLocation[0];        // set your SearchRetrieval position here
            //lp.x = 0;
            lp.y = mLocation[1];        // set your Y position here
            Logg.i(TAG, String.format(Locale.ENGLISH, "Location %d %d", lp.x, lp.y));
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
}
