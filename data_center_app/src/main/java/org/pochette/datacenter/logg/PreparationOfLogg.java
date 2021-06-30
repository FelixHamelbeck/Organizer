package org.pochette.data_library.logg;

import android.app.Activity;

import org.pochette.utils_lib.logg.Logg;

public class PreparationOfLogg {

    private static final String TAG = "FEHA (PreparationOfLogg)";

    // variables
    // constructor
    // setter and getter
    // lifecylce and override
    // internal
    // public methods

    public static void execute(
            @SuppressWarnings("unused") Activity iActivity, boolean iDebug) {
        Logg.setDebug(iDebug);

        // always log e and w
        Logg.addControlLine("*", "*", "*", "e",
                "*", true, true);
        Logg.addControlLine("*", "*", "*", "w",
                "*", true, true);

        Logg.addControlLine("*", "*", "*", "i",
                "*", true, true);
        Logg.addControlLine("*", "*", "*", "k",
                "w", true, true);


        // last default: do not log
        Logg.addControlLine("*", "", "*", "*",
                "*", true, false);


    }

}
