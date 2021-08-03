package org.pochette.organizer.requestlist;


import android.view.View;

import org.pochette.data_library.music.MusicFile;
import org.pochette.data_library.requestlist.Requestlist;
import org.pochette.data_library.scddb_objects.Dance;
import org.pochette.data_library.scddb_objects.Recording;
import org.pochette.organizer.gui.UserChoice;
import org.pochette.utils_lib.logg.Logg;
import org.pochette.utils_lib.shouting.Shout;
import org.pochette.utils_lib.shouting.Shouting;

/**
 * This class implements the logic behind the Requestlist icon
 * it can be called
 */
public class Requestlist_Action implements Shouting {

    public final static int CLICK_ICON_CREATE = 1;
    public final static int CLICK_ICON_DELETE = 2;
    public final static int CLICK_ICON_REQUESTLIST = 3;
    public final static int CLICK_TYPE_SHORT = 1;
    public final static int CLICK_TYPE_LONG = 2;

    private final static String TAG = "FEHA (Requestlist_Action)";
    //Variables
    private final Shouting mShouting;
    private final View mLayout;

    private final int mClickType;
    private final int mClickIcon;
    private final Requestlist mRequestlist;
    private final MusicFile mMusicFile;
    private final Dance mDance;
    private final Recording mRecording;

    private boolean mPendingAddToDefault = false;

    private Shout mGlassFloor;

    //Constructor
    private Requestlist_Action(View iView, Shouting iShouting,
                               int iClickType, int iClickIcon,
                               Requestlist iRequestlist, Dance iDance, MusicFile iMusicFile,Recording iRecording) {
        mLayout = iView;
        mShouting = iShouting;
        mClickIcon = iClickIcon;
        mClickType = iClickType;
        mRequestlist = iRequestlist;
        mDance = iDance;
        mMusicFile = iMusicFile;
        mRecording = iRecording;
    }

    public static void callExecute(View iView, Shouting iShouting,
                                   int iClickType, int iClickIcon,
                                   Requestlist iRequestlist,
                                   Dance iDance, MusicFile iMusicFile,Recording iRecording) {
        Requestlist_Action tRequestlistAction = new Requestlist_Action(iView, iShouting,
                iClickType, iClickIcon,
                iRequestlist, iDance, iMusicFile,iRecording);
        tRequestlistAction.execute();
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
            case CLICK_ICON_REQUESTLIST:
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
            DialogFragment_RequestlistAction.create(mLayout,
                    DialogFragment_RequestlistAction.CREATE_MODE, this);
        }
    }

    private void executeDelete() {
        UserChoice tUserChoice = new UserChoice("Do your really want to delete this list?");
        tUserChoice.addQuestion("YES", "Yes, DELETE Requestlist");
        tUserChoice.addQuestion("CANCEL", "Do not delete");
        String tResult = tUserChoice.poseQuestion(mLayout.getContext());
        if (tResult.equals("YES") && mRequestlist != null) {
            mRequestlist.delete();
        }
    }

    private void executeAddToDefault() {
        if (mMusicFile == null && mDance == null) {
            return;
        }
        Requestlist tRequestlist = Requestlist.getDefaultRequestlist();

        tRequestlist.add(mMusicFile, mDance);
    }

    private void executeSelection() {
        if (mLayout != null) {
            DialogFragment_RequestlistAction.create(mLayout,
                    DialogFragment_RequestlistAction.SELECT_MODE, this);
        }
    }

    private void executeDefault(boolean iFlagPendingAdd) {
        mPendingAddToDefault = iFlagPendingAdd;
        executeSelection();
    }

    private void executeChoice() {
        UserChoice tUserChoice = new UserChoice("Choose");
        tUserChoice.addQuestion("CREATE", "Create new Requestlist");
        tUserChoice.addQuestion("DEFAULT_AND_ADD", "Define Default Requestlist and add");
        tUserChoice.addQuestion("DEFAULT", "Define Default Requestlist");
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
            Shout tShout = new Shout(Requestlist_Action.class.getSimpleName());
            tShout.mLastObject = "Action";
            tShout.mLastAction = "finished";
            mShouting.shoutUp(tShout);
        }
    }

    private void process_shouting() {
        if ("DialogFragment_RequestlistAction".equals(mGlassFloor.mActor)) {
            if (mGlassFloor.mLastObject.equals("Choice") && mGlassFloor.mLastAction.equals("performed")) {
                Requestlist tRequestlist = mGlassFloor.returnObject();
                if (tRequestlist != null) {
                    Requestlist.setDefaultRequestlist(tRequestlist);
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
