package org.pochette.organizer.music;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.pochette.data_library.music.MusicFile;
import org.pochette.organizer.R;
import org.pochette.utils_lib.logg.Logg;
import org.pochette.utils_lib.shouting.Shout;
import org.pochette.utils_lib.shouting.Shouting;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

/**
 * DialogFragment to work with
 */

public class DialogFragment_MusicFile extends DialogFragment implements Shouting {

    private static final String TAG = "FEHA (ActDi_MusicFile)";
    //Variables
    MusicFile_Fragment mMusicFile_Fragment;
    private TextView mTV_Title;
    private ImageView mIV_Confirm;

    private String mTitle;
    private boolean mSelectionActive;

    private Shouting mShouting; // to send the result back to
    private Shout mGlassFloor;

    //Constructor
    public DialogFragment_MusicFile() {

    }

    public static DialogFragment_MusicFile newInstance(String iTitle) {
        DialogFragment_MusicFile tDialogFragment_MusicFile = new DialogFragment_MusicFile();
        Bundle args = new Bundle();
        args.putString("title", iTitle);
        args.putBoolean("SelectionActive",false);
        tDialogFragment_MusicFile.setArguments(args);
        return tDialogFragment_MusicFile;
    }

    //Setter and Getter
    public void setTitle(String title) {
        mTitle = title;
        if (mTV_Title != null) {
            mTV_Title.setText(mTitle);
        }
    }

    public void setShouting(Shouting shouting) {
        mShouting = shouting;
    }

    //Livecycle
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_header, container);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addFragment();
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            Logg.i(TAG, "getArguments");
        }
        if (savedInstanceState != null) {
            Logg.i(TAG, "saved");
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View iView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(iView, savedInstanceState);
        mTV_Title = requireView().findViewById(R.id.TV_DialogHeader);
        mIV_Confirm = requireView().findViewById(R.id.IV_DialogHeader_Confirm);
        if (mTitle != null && !mTitle.isEmpty()) {
            mTV_Title.setText(mTitle);
        }
        addListener();
        refresh();
    }

    @Override
    public void dismiss() {
        if (mShouting != null) {
            Shout tShout = new Shout(this.getClass().getSimpleName());
            tShout.mLastObject = "Dialog";
            tShout.mLastAction = "dismissed";
            mShouting.shoutUp(tShout);
        }
        super.dismiss();
    }

    //Static Methods
    //Internal Organs

    private void confirm() {
        if (mShouting != null) {
            Shout tShout = new Shout(this.getClass().getSimpleName());
            tShout.mLastObject = "Choice";
            tShout.mLastAction = "confirmed";
            MusicFile  tMusicFile  = mMusicFile_Fragment.mMusicFile_Adapter.getSelectedMusicFile();
            if (tMusicFile == null) {
                dismiss();
                return;
            }
            tShout.storeObject(tMusicFile);
            mShouting.shoutUp(tShout);
        }
        dismiss();
    }

    private void refresh() {
        if (mIV_Confirm != null) {
            if (mSelectionActive) {
                mIV_Confirm.setVisibility(View.VISIBLE);
            } else {
                mIV_Confirm.setVisibility(View.GONE);
            }
        }
    }


    private void process_shouting() {
        if (mGlassFloor.mActor.equals("MusicFile_Adapter")) {
            if (mGlassFloor.mLastAction.equals("Unselect") &&
                    mGlassFloor.mLastObject.equals("MusicFile")) {
                mSelectionActive = false;
                refresh();
            } else if (mGlassFloor.mLastAction.equals("select") &&
                    mGlassFloor.mLastObject.equals("MusicFile")) {
                mSelectionActive = true;
                refresh();
            }
        }
    }

    private void addListener() {
        if (mIV_Confirm != null) {
            mIV_Confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Logg.k(TAG, "OnClick");
                    confirm();
                }
            });
        }
    }

    private void addFragment() {
        if (mMusicFile_Fragment == null) {
            mMusicFile_Fragment = new MusicFile_Fragment();
            mMusicFile_Fragment.setShouting(this);
         //   mMusicFile_Fragment.setWithSearchValueStorage(false);
        }

        FragmentManager tFragmentManager = getChildFragmentManager();
        FragmentTransaction tFragmentTransaction = tFragmentManager.beginTransaction();
        tFragmentTransaction.add(R.id.FL_DialogHeader, mMusicFile_Fragment);
        tFragmentTransaction.commit();
    }

    //Interface
    public static void create(View iView, String iTitle, Shouting iShouting) {

        DialogFragment_MusicFile t_DialogFragment_MusicFile =
                DialogFragment_MusicFile.newInstance("Some Title");
        t_DialogFragment_MusicFile.mShouting = iShouting;
        AppCompatActivity tActivity = ((AppCompatActivity) iView.getContext());
        t_DialogFragment_MusicFile.show(tActivity.getSupportFragmentManager(), null);
        t_DialogFragment_MusicFile.setTitle(iTitle);
    }

    @Override
    public void shoutUp(Shout tShoutToCeiling) {
        Logg.i(TAG, tShoutToCeiling.toString());
        mGlassFloor = tShoutToCeiling;
        process_shouting();
    }
}
