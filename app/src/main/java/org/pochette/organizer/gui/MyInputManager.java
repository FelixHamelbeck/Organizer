package org.pochette.organizer.gui;

import android.view.KeyEvent;

import org.pochette.utils_lib.logg.Logg;

/**
 * the input manager separates the key events from the onScreen keyboard from all other key events
 * the assumption is, that the other key events are send from a remote control unit
 *
 * Which
 */
public class MyInputManager {

    private final static String TAG = "FEHA (MyInputManager)";

    public static boolean swallowKeyDown(@SuppressWarnings("unused") int iKeyCode, KeyEvent iKeyEvent) {
        boolean tFromController = false;
        // -1 is the device ID of the onScreenKeyboard
        if (iKeyEvent.getDeviceId() != -1) {
            tFromController = true;
        }
        if (tFromController) {
            MyInputMapping.map(iKeyEvent);
            Logg.i(TAG, "swallowed" + iKeyEvent.toString());
            return true;
        } else {

            return false;
        }
    }

    public static boolean swallowKeyUp(@SuppressWarnings("unused") int iKeyCode, KeyEvent iKeyEvent) {
        boolean tFromController = false;
        if (iKeyEvent.getDeviceId() != -1) {
            tFromController = true;
        }
        if (tFromController) {
            MyInputMapping.map(iKeyEvent);
            Logg.i(TAG, "swallowed" + iKeyEvent.toString());
            return true;
        } else {
            return false;
        }
    }


}
