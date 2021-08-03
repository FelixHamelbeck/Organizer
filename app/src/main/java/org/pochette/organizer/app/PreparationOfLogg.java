package org.pochette.organizer.app;

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
        Logg.setDebug(true);

        // always log e and w
        Logg.addControlLine("*", "*", "*", "e",
                "*", true, true);
        Logg.addControlLine("*", "*", "*", "w",
                "*", true, true);


        Logg.addControlLine("gui_assist", "*", "*", "i",
                "*", true, true);
        Logg.addControlLine("dance", "*", "*", "i",
                "*", true, true);

        Logg.addControlLine("*", "SearchCall", "*", "i",
                "*", true, true);
        Logg.addControlLine("*", "MainActivity", "*", "i",
                "*", true, true);

        Logg.addControlLine("*", "TopBar_Fragment", "*", "i",
                "*", true, true);
        Logg.addControlLine("*", "SearchCall", "*", "i",
                "*", true, true);
        Logg.addControlLine("music", "*", "*", "i",
                "*", true, true);



        Logg.addControlLine("*", "*", "*", "k",
                "w", true, false);


        // last default: do not log
        Logg.addControlLine("*", "*", "*", "*",
                "*", true, false);

    }

}
