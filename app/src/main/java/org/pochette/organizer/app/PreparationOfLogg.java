package org.pochette.organizer.app;

import android.app.Activity;

import org.pochette.utils_lib.logg.Logg;

@SuppressWarnings("unused")
public class PreparationOfLogg {

    private static final String TAG = "FEHA (PreparationOfLogg)";

    // variables
    // constructor
    // setter and getter
    // lifecylce and override
    // internal
    // public methods

    public static void execute(boolean iDebug) {
        Logg.setDebug(iDebug);
        // todo reactivate after development is finished
        Logg.setDebug(false);

        // always log e and w
        Logg.addControlLine("*", "*", "*", "e",
                "*", true, true);
        Logg.addControlLine("*", "*", "*", "w",
                "*", true, true);

        Logg.addControlLine("*", "MainActivity", "*", "i",
                "*", true, true);
        Logg.addControlLine("*", "LdbBackup", "*", "i",
                "*", true, true);
        Logg.addControlLine("gui", "*", "*", "i",
                "*", true, true);

        Logg.addControlLine("app", "*", "*", "i",
                "*", true, true);


        Logg.addControlLine("*", "*", "*", "k",
                "w", true, false);


        // last default: do not log
        Logg.addControlLine("*", "*", "*", "*",
                "*", true, false);


    }

}
