package org.pochette.organizer.album;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.pochette.data_library.scddb_objects.Album;
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

public class DialogFragment_Album extends DialogFragment implements Shouting {

    private static final String TAG = "FEHA (ActDi_Album)";
    //Variables
    Album_Fragment mAlbum_Fragment;
    private TextView mTV_Title;
    private ImageView mIV_Confirm;

    private String mTitle;
    private String mSignatureString;
    private boolean mSelectionActive;

    private Shouting mShouting; // to send the result back to
    private Shout mGlassFloor;

    //Constructor
    public DialogFragment_Album() {

    }

    public static DialogFragment_Album newInstance(String iTitle, String iSignatureString) {
        DialogFragment_Album tDialogFragment_Album = new DialogFragment_Album();
     //   frag.setTitle(iTitle);
     //   frag.setSignatureString(iSignatureString);
        Bundle args = new Bundle();
        args.putString("title", iTitle);
        args.putString("signatureString", iSignatureString);
        args.putBoolean("SelectionActive",false);
        tDialogFragment_Album.setArguments(args);
        //frag.mSelectionActive = false;
        return tDialogFragment_Album;
    }

    //Setter and Getter
    public void setTitle(String title) {
        mTitle = title;
        if (mTV_Title != null) {
            mTV_Title.setText(mTitle);
        }
    }

    public void setSignatureString(String iSignatureString) {
        mSignatureString = iSignatureString;
        if (mAlbum_Fragment != null ) {
            mAlbum_Fragment.setSignatureString(iSignatureString);
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
            Album tAlbum = mAlbum_Fragment.mAlbum_Adapter.getSelectedAlbum();
            if (tAlbum == null) {
                dismiss();
            }
            tShout.storeObject(tAlbum);
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
        if (mGlassFloor.mActor.equals("Album_Adapter")) {
            if (mGlassFloor.mLastAction.equals("performed") &&
                    mGlassFloor.mLastObject.equals("Selection")) {
                mSelectionActive = true;
                refresh();
            } else if (mGlassFloor.mLastAction.equals("cleared") &&
                    mGlassFloor.mLastObject.equals("Selection")) {
                mSelectionActive = false;
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
        if (mAlbum_Fragment == null) {
            mAlbum_Fragment = new Album_Fragment();
            mAlbum_Fragment.setShouting(this);
            mAlbum_Fragment.setWithSearchValueStorage(false);
        }
        if (mSignatureString != null && !mSignatureString.isEmpty()) {
            mAlbum_Fragment.setSignatureString(mSignatureString);
        }
        FragmentManager tFragmentManager = getChildFragmentManager();
        FragmentTransaction tFragmentTransaction = tFragmentManager.beginTransaction();
        tFragmentTransaction.add(R.id.FL_DialogHeader, mAlbum_Fragment);
        tFragmentTransaction.commit();
    }

    //Interface
    public static void create(View iView, String iTitle, String iSignatureString, Shouting iShouting) {

        DialogFragment_Album t_DialogFragmentAlbum =
                DialogFragment_Album.newInstance("Some Title", iSignatureString);
        t_DialogFragmentAlbum.mShouting = iShouting;
        AppCompatActivity tActivity = ((AppCompatActivity) iView.getContext());
        t_DialogFragmentAlbum.show(tActivity.getSupportFragmentManager(), null);
        t_DialogFragmentAlbum.setTitle(iTitle);
        t_DialogFragmentAlbum.setSignatureString(iSignatureString);
        Logg.i(TAG, "create: "+ iSignatureString);
    }

    @Override
    public void shoutUp(Shout tShoutToCeiling) {
        Logg.i(TAG, tShoutToCeiling.toString());
        mGlassFloor = tShoutToCeiling;
        process_shouting();
    }
}
