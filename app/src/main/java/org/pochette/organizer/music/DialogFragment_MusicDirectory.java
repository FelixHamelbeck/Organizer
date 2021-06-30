package org.pochette.organizer.music;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
 * DialogFragment to work with Playlist Header
 */
public class DialogFragment_MusicDirectory extends DialogFragment implements Shouting {

//    public final static int SEARCH_MODE = 0;
//    public final static int SELECT_MODE = 1; // Search+ Create
//    public final static int EDIT_MODE = 2;
//    public final static int CREATE_MODE = 3;

    private static final String TAG = "FEHA (ActDi_MD)";
    //Variables

    MusicDirectory_Fragment mMusicDirectory_Fragment;

    private Shouting mShoutingCaller; // to send the result back to
    private Shout mGlassFloor;

    //Constructor
    public DialogFragment_MusicDirectory() {
        //mMode = SEARCH_MODE;
        //mPlaylist_Purpose = Playlist_Purpose.UNDEFINED;
    }

    public static DialogFragment_MusicDirectory newInstance(String title) {
        DialogFragment_MusicDirectory frag = new DialogFragment_MusicDirectory();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    //Setter and Getter
    //Livecycle

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_empty, container);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        add();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View iView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(iView, savedInstanceState);
        add();
    }

    @Override
    public void dismiss() {
        if (mShoutingCaller != null) {
            Shout tShout = new Shout(this.getClass().getSimpleName());
            tShout.mLastObject = "Dialog";
            tShout.mLastAction = "dismissed";
            mShoutingCaller.shoutUp(tShout);
        }
        super.dismiss();
    }

    //Static Methods
    //Internal Organs
    //Interface

    private void process_shouting() {
    }

    private void add() {
        if (mGlassFloor == null) {
            mMusicDirectory_Fragment = new MusicDirectory_Fragment();
            mMusicDirectory_Fragment.setShouting(this);
            mMusicDirectory_Fragment.setWithSearchValueStorage(false);
        }
        FragmentManager tFragmentManager = getChildFragmentManager();
        FragmentTransaction tFragmentTransaction = tFragmentManager.beginTransaction();
        tFragmentTransaction.add(R.id.DialogEmpty_FL, mMusicDirectory_Fragment);
        tFragmentTransaction.commit();
    }


    public static void create(View iView, Shouting iShouting) {
        DialogFragment_MusicDirectory t_DialogFragmentPlaylist =
                DialogFragment_MusicDirectory.newInstance("Some Title");
        t_DialogFragmentPlaylist.mShoutingCaller = iShouting;
        AppCompatActivity tActivity = ((AppCompatActivity)iView.getContext());
        t_DialogFragmentPlaylist.show(tActivity.getSupportFragmentManager(),null);
    }

    @Override
    public void shoutUp(Shout tShoutToCeiling) {
        Logg.i(TAG, tShoutToCeiling.toString());
        mGlassFloor = tShoutToCeiling;
        process_shouting();
    }

}
