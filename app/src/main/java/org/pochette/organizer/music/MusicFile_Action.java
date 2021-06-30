package org.pochette.organizer.music;


import android.view.View;

import org.pochette.data_library.music.MusicFile;
import org.pochette.data_library.playlist.Playinstruction;
import org.pochette.data_library.playlist.Playlist;
import org.pochette.data_library.scddb_objects.Dance;
import org.pochette.organizer.app.MediaPlayerServiceSingleton;
import org.pochette.organizer.mediaplayer.MediaPlayerService;
import org.pochette.utils_lib.logg.Logg;
import org.pochette.utils_lib.shouting.Shout;
import org.pochette.utils_lib.shouting.Shouting;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

/**
 * This class implements the logic behind the playlist icon
 */
public class MusicFile_Action implements Shouting {

    private final static String TAG = "FEHA (MusicFile_Action)";
    public final static int SHORT_CLICK = 1;
    public final static int LONG_CLICK = 2;

    public final static int CLICK_ICON_PLAY = 1;
    public final static int CLICK_ICON_MUSIC = 2;
    public final static int CLICK_ICON_ADD = 3;
    //Variables
    private final Shouting mShouting;
    private final View mLayout;

    private final int mClickType;
    private final int mClickIcon;
    private final MusicFile mMusicFile;
    private final Dance mDance;

    private Shout mGlassFloor;
    //Constructor

    private MusicFile_Action(View iView, Shouting iShouting,
                             int iClickType, int iClickIcon,
                             Dance iDance, MusicFile iMusicFile) {
        mLayout = iView;
        mShouting = iShouting;
        mClickIcon = iClickIcon;
        mClickType = iClickType;
        mDance = iDance;
        mMusicFile = iMusicFile;
    }


//    public MusicFile_Action(Fragment iFragment, Shouting iShouting, int iCallerMode,
//                            @Nullable MusicFile iMusicFile, @Nullable Dance iDance) {
//
//        if (iFragment == null) {
//            Logg.i(TAG, "no fragment provided");
//            throw new RuntimeException("no Fragment provided");
//        }
//        mContext= iFragment.getContext();
//     //   WeakReference<Fragment> wFragment = new WeakReference<>(iFragment);
//        mFragmentManager = iFragment.getChildFragmentManager();
//        mShouting = iShouting;
//        mCallerMode = iCallerMode;
//        mMusicFile = iMusicFile;
//        mDance = iDance;
//
//    }


    public static void callExecute(View iView, Shouting iShouting,
                                   int iClickType, int iClickIcon,
                                   Dance iDance, MusicFile iMusicFile) {

        MusicFile_Action tMusicFile_Action = new MusicFile_Action(iView, iShouting,
                iClickType, iClickIcon,
                iDance, iMusicFile);
        tMusicFile_Action.execute();
    }

    //Setter and Getter
    //Livecycle
    //Static Methods
    //Internal Organs
    private void execute() {

        switch (mClickIcon) {

            case CLICK_ICON_PLAY:
                executePlay();
                break;
            case CLICK_ICON_MUSIC:
                if (mClickType == LONG_CLICK) {
                    executeDefineMusicPreference();
                } else {
                    executePlay();
                }
            case CLICK_ICON_ADD:
                executeSelect();
                break;

        }

    }

    void executeSelect() {
        Logg.i(TAG, "executeSelect");
        String tTitle = "Add to Playlist";
        DialogFragment_MusicFile.create(mLayout,tTitle, this);

    }


    void executePlay() {
        Playinstruction tPlayinstruction;
        tPlayinstruction = Playinstruction.getPlayinstruction(mMusicFile, mDance);
        if (tPlayinstruction != null) {
            MediaPlayerService tMediaPlayerService =
                    MediaPlayerServiceSingleton.getInstance().getMediaPlayerService();
            tMediaPlayerService.play(tPlayinstruction);
            shoutPlayStarted();
        }
    }

    void executeDefineMusicPreference() {
        Logg.i(TAG, "choose from");
        DialogFragment_MusicPreference tDialogFragment_MusicPreference =
                DialogFragment_MusicPreference.newInstance("Choose musicfile");
        tDialogFragment_MusicPreference.setTitle("Which Music for Dance");
        tDialogFragment_MusicPreference.setShouting(this);
        tDialogFragment_MusicPreference.setDance(mDance);
        AppCompatActivity activity = ((AppCompatActivity) mLayout.getContext());
        FragmentManager tFragmentManager = activity.getSupportFragmentManager();
        tDialogFragment_MusicPreference.show(tFragmentManager, null);
        tDialogFragment_MusicPreference.setFragmentManager(tFragmentManager);
    }

    void shoutPlayStarted() {
        if (mShouting != null) {
            Shout tShout = new Shout(MusicFile_Action.class.getSimpleName());
            tShout.mLastAction = "started";
            tShout.mLastObject = "MusicPlaying";
            mShouting.shoutUp(tShout);
        }
    }

    @SuppressWarnings("unused")
    void shoutPreferenceChanged() {
        if (mShouting != null) {
            Shout tShout = new Shout(MusicFile_Action.class.getSimpleName());
            tShout.mLastAction = "changed";
            tShout.mLastObject = "Preference";
            mShouting.shoutUp(tShout);
        }
    }

    private void process_shouting() {
        if ("DialogFragment_MusicPreference".equals(mGlassFloor.mActor)) {
            if (mGlassFloor.mLastObject.equals("") && mGlassFloor.mLastAction.equals("performed")) {
                if (mDance != null) {
                    Playlist tPlaylist = mGlassFloor.returnObject();
                    tPlaylist.add(mDance);
                }
                if (mShouting != null) {
                    Shout tShout = new Shout(mGlassFloor);
                    tShout.mActor = MusicFile_Action.class.getSimpleName();
                    mShouting.shoutUp(tShout);
                }

            } else if (mGlassFloor.mLastObject.equals("Data") &&
                    mGlassFloor.mLastAction.equals("changed")) {
                if (mShouting != null) {
                    Shout tShout = new Shout(mGlassFloor);
                    tShout.mActor = MusicFile_Action.class.getSimpleName();
                    mShouting.shoutUp(tShout);
                }
            }
        }
        if ("DialogFragment_MusicFile".equals(mGlassFloor.mActor)) {
            if (mGlassFloor.mLastObject.equals("Choice") && mGlassFloor.mLastAction.equals("confirmed")) {
                if (mShouting != null) {
                    mShouting.shoutUp(mGlassFloor);
                }
//                MusicFile tMusicFile;
//                try {
//                    tMusicFile = (MusicFile) mGlassFloor.returnObject();
//                } catch(Exception e) {
//                    Logg.w(TAG, e.toString());
//                    tMusicFile = null;
//                }
//                if (tMusicFile != null) {
//
//                }

            }
        }
    }
    //Interface

    @Override
    public void shoutUp(Shout tShoutToCeiling) {
        Logg.i(TAG, tShoutToCeiling.toString());
        mGlassFloor = tShoutToCeiling;
        process_shouting();
    }
}
