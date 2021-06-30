package org.pochette.organizer.playlist;


import android.view.View;

import org.pochette.data_library.music.MusicFile;
import org.pochette.data_library.playlist.Playinstruction;
import org.pochette.data_library.playlist.Playlist;
import org.pochette.data_library.scddb_objects.Dance;
import org.pochette.organizer.gui.UserChoice;
import org.pochette.utils_lib.logg.Logg;
import org.pochette.utils_lib.shouting.Shout;
import org.pochette.utils_lib.shouting.Shouting;

/**
 * This class implements the logic behind the playlist icon
 */
public class Playlist_Action implements Shouting {

    public final static int CLICK_ICON_CREATE = 1;
    public final static int CLICK_ICON_DELETE = 2;
    public final static int CLICK_ICON_PLAYLIST = 3;
    public final static int CLICK_TYPE_SHORT = 1;
    public final static int CLICK_TYPE_LONG = 2;


    public final static int SHORT_CLICK = 1;
    public final static int LONG_CLICK = 2;

    private final static String TAG = "FEHA (Playlist_Action)";
    //Variables
    private final Shouting mShouting;
    private final View mLayout;

    private final int mClickType;
    private final int mClickIcon;
    private final Playlist mPlaylist;
    private final MusicFile mMusicFile;
    private final Dance mDance;

    private boolean mPendingAddToDefault = false;

    private Shout mGlassFloor;

    //Constructor
    private Playlist_Action(View iView, Shouting iShouting,
                            int iClickType, int iClickIcon,
                            Playlist iPlaylist, Dance iDance, MusicFile iMusicFile) {
        mLayout = iView;
        mShouting = iShouting;
        mClickIcon = iClickIcon;
        mClickType = iClickType;
        mPlaylist = iPlaylist;
        mDance = iDance;
        mMusicFile = iMusicFile;
    }

    public static void callExecute(View iView, Shouting iShouting,
                                   int iClickType, int iClickIcon,
                                   Playlist iPlaylist, Dance iDance, MusicFile iMusicFile) {
        Playlist_Action tPlaylistAction = new Playlist_Action(iView, iShouting,
                iClickType, iClickIcon,
                iPlaylist, iDance, iMusicFile);
        tPlaylistAction.execute();
    }

    //Setter and Getter
    //Livecycle
    //Static Methods
    //Internal Organs

    private void execute() {
        boolean tActionIdentified = false;
        switch (mClickIcon) {
            case CLICK_ICON_DELETE:
                tActionIdentified = true;
                executeDelete();
                break;
            case CLICK_ICON_CREATE:
                tActionIdentified = true;
                executeCreation();
                break;
            default:
            case CLICK_ICON_PLAYLIST:
                if (mClickType == CLICK_TYPE_SHORT) {
                    if (mMusicFile != null || mDance != null) {
                        tActionIdentified = true;
                        executeAddToDefault();
                    }
                } else {
                    tActionIdentified = true;
                    executeChoice();
                }
                break;
        }
        if (!tActionIdentified) {
            Logg.w(TAG, " no action identifed");
        }
    }

    private void executeCreation() {
        if (mLayout != null) {
            DialogFragment_PlaylistAction.create(mLayout,
                    DialogFragment_PlaylistAction.CREATE_MODE, this);
        }
    }

    private void executeDelete() {
        UserChoice tUserChoice = new UserChoice("Do your really want to delete?");
        tUserChoice.addQuestion("YES", "Yes, DELETE playlist");
        tUserChoice.addQuestion("CANCEL", "Do not delete");
        String tResult = tUserChoice.poseQuestion(mLayout.getContext());
        if (tResult.equals("YES") && mPlaylist != null) {
            mPlaylist.delete();
        }
    }

    private void executeAddToDefault() {
        Playinstruction tPlayinstruction;
        if (mMusicFile == null && mDance == null) {
            return;
        }
        tPlayinstruction = Playinstruction.getPlayinstruction(mMusicFile, mDance);
        if (tPlayinstruction != null) {
            Playlist tPlaylist = Playlist.getDefaultPlaylist();
            tPlaylist.add(tPlayinstruction);
        }
    }

    private void executeSelection() {
        if (mLayout != null) {
            DialogFragment_PlaylistAction.create(mLayout,
                    DialogFragment_PlaylistAction.SELECT_MODE, this);
        }
    }

    private void executeDefault(boolean iFlagPendingAdd) {
        mPendingAddToDefault = iFlagPendingAdd;
        executeSelection();
    }

    private void executeChoice() {
        UserChoice tUserChoice = new UserChoice("Choose");
        tUserChoice.addQuestion("CREATE", "Create new playlist");
        tUserChoice.addQuestion("DEFAULT_AND_ADD", "Define Default Playlist and add");
        tUserChoice.addQuestion("DEFAULT", "Define Default Playlist");
        String tResult = tUserChoice.poseQuestion(mLayout.getContext());
        switch (tResult) {
            case "CREATE":
                executeCreation();
                break;
            case "DEFAULT_AND_ADD":
                executeDefault(true);
                break;
            case "DEFAULT":
                executeDefault(false);
                break;
        }
    }

    /**
     * in case a GUI action is performed as part of this action, some activties need to wait for the
     * GUI to be finished, i.e. it waits for the shout
     * if anything is pending it will be called here
     */
    void executeAnythingPending() {
        if (mPendingAddToDefault) {
            executeAddToDefault();
        }
    }

    private void shoutFinished() {
        if (mShouting != null) {
            Shout tShout = new Shout(Playlist_Action.class.getSimpleName());
            tShout.mLastObject = "Action";
            tShout.mLastAction = "finished";
            mShouting.shoutUp(tShout);
        }
    }

    private void process_shouting() {
        if ("DialogFragment_PlaylistAction".equals(mGlassFloor.mActor)) {
            if (mGlassFloor.mLastObject.equals("Choice") && mGlassFloor.mLastAction.equals("performed")) {
                Playlist tPlaylist = mGlassFloor.returnObject();
                if (tPlaylist != null) {
                    Playlist.setDefaultPlaylist(tPlaylist);
                    executeAnythingPending();
                }
                shoutFinished();
            }
        }
    }

    @Override
    public void shoutUp(Shout tShoutToCeiling) {
        Logg.i(TAG, tShoutToCeiling.toString());
        mGlassFloor = tShoutToCeiling;
        process_shouting();
    }
}
