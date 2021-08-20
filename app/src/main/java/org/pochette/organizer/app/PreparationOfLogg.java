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


        Logg.addControlLine("dance", "*", "*", "i",
                "*", true, true);


        Logg.addControlLine("*", "MainActivity", "*", "i",
                "*", true, true);

        Logg.addControlLine("*", "DialogFragment_ChooseFormation", "*", "i",
                "*", true, true);
        Logg.addControlLine("xchained_search", "Matryoshka_Controller", "*", "i",
                "*", true, true);



        Logg.addControlLine("*", "*", "*", "k",
                "w", true, false);


        // last default: do not log
        Logg.addControlLine("*", "*", "*", "*",
                "*", true, false);

    }

}
