package org.pochette.organizer.gui;

import android.view.KeyEvent;

import org.pochette.organizer.app.MediaPlayerServiceSingleton;
import org.pochette.utils_lib.logg.Logg;

import java.util.ArrayList;
import java.util.Locale;

public class MyInputMapping {

    private final static String TAG = "FEHA (MyInputMapping)";

    private final static int INTERMEDIATE_VOLUME_UP = 11;
    private final static int INTERMEDIATE_VOLUME_DOWN = 12;
    private final static int INTERMEDIATE_START = 21;
    private final static int INTERMEDIATE_PAUSE = 31;


    //Variables

    //Constructor

    //Setter and Getter

    //Livecycle

    //Static Methods

    //Internal Organs

    @SuppressWarnings({"DuplicateBranchesInSwitch"})
    public static void  map(KeyEvent iKeyEvent) {
     //   public static ArrayList<String> map(KeyEvent iKeyEvent) {

        // first mapping is to unify the various modes into one
        // seconda mapping is to map these modes to calls within the app

        int iIntermediateKey = 0;

        //noinspection StatementWithEmptyBody
        if (iKeyEvent.getAction() == KeyEvent.ACTION_DOWN) {
            // Logg.i(TAG, "Ignore down");
        } else {
            switch (iKeyEvent.getKeyCode()) {
                case KeyEvent.KEYCODE_VOLUME_UP://  Key-X, MG-X
                case KeyEvent.KEYCODE_BUTTON_X:// Game-X
                    iIntermediateKey = INTERMEDIATE_VOLUME_UP;
                    break;
                case KeyEvent.KEYCODE_VOLUME_DOWN:// Key-A, MG-A
                case KeyEvent.KEYCODE_BUTTON_A:// Game-A
                    iIntermediateKey = INTERMEDIATE_VOLUME_DOWN;
                    break;
                case KeyEvent.KEYCODE_ENTER:// Key-B
                case KeyEvent.KEYCODE_BUTTON_B:// Game-B
                    iIntermediateKey = INTERMEDIATE_PAUSE;
                    break;
                case KeyEvent.KEYCODE_MENU:// Key-Y
                case KeyEvent.KEYCODE_BUTTON_Y:// Game-Y
                    iIntermediateKey = INTERMEDIATE_START;
                    break;
                case KeyEvent.KEYCODE_BACK:// Key-Esc, Game-Esc
                    iIntermediateKey = 0;
                    break;
                case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:// Key-select
                case KeyEvent.KEYCODE_BUTTON_SELECT:// Game-Select
                    iIntermediateKey = 0;
                    break;
                case KeyEvent.KEYCODE_MEDIA_NEXT:// Key-PushAway
                case KeyEvent.KEYCODE_DPAD_UP:// Game-PushAway
                    iIntermediateKey = 0;
                    break;
                case KeyEvent.KEYCODE_MEDIA_PREVIOUS:// Key-PullNearer
                case KeyEvent.KEYCODE_DPAD_DOWN:// Game-PullNearer
                    iIntermediateKey = 0;
                    break;
                case KeyEvent.KEYCODE_MEDIA_REWIND:// Key-PushRight
                case KeyEvent.KEYCODE_DPAD_RIGHT:// Game-PushRight
                    iIntermediateKey = 0;
                    break;
                case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD:// Key-PushLeft
                case KeyEvent.KEYCODE_DPAD_LEFT:// Game-PushLeft
                    iIntermediateKey = 0;
                    break;

            }
            Logg.v(TAG, "intermedoate " + iIntermediateKey);
        }
        int tVolume100;
        switch (iIntermediateKey) {
            case INTERMEDIATE_VOLUME_UP:
                Logg.i(TAG, "Call increase Volume");
                MediaPlayerServiceSingleton.getInstance().getMediaPlayerService().increaseVolume();
                break;
            case INTERMEDIATE_VOLUME_DOWN:
                Logg.i(TAG, "Call decrease Volume");
                MediaPlayerServiceSingleton.getInstance().getMediaPlayerService().decreaseVolume();
                break;
            case INTERMEDIATE_START:
                Logg.i(TAG, "Call play");
               MediaPlayerServiceSingleton.getInstance().getMediaPlayerService().play();

                break;
            case INTERMEDIATE_PAUSE:
                Logg.i(TAG, "Call pause");
                MediaPlayerServiceSingleton.getInstance().getMediaPlayerService().togglePause();
                break;
        }


    }



    //Interface


}
