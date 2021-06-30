package org.pochette.organizer.dance;

import org.pochette.data_library.scddb_objects.DanceFavourite;
import org.pochette.organizer.R;
import org.pochette.utils_lib.search.OldSpinnerSearchItem;

import java.util.ArrayList;

class DanceFavouriteSpinnerSearchItem {

    private static final String TAG = "FEHA (DanceFavouriteSSI)";

    // variables
    // constructor
    // setter and getter
    // lifecylce and override
    // internal
    // public methods


    public static ArrayList<OldSpinnerSearchItem> getAL_SpinnerSearchItem(Class iClass) {
        ArrayList<OldSpinnerSearchItem> tAL = new ArrayList<>(0);

        OldSpinnerSearchItem tOldSpinnerSearchItem;
        DanceFavourite tDanceFavourite;
        tDanceFavourite = DanceFavourite.GOOD;
        tOldSpinnerSearchItem = new OldSpinnerSearchItem(tDanceFavourite.getCode(), tDanceFavourite.getText(),
                true, tDanceFavourite,
                tDanceFavourite.getPriority(), R.drawable.ic_favourite_good);
        tAL.add(tOldSpinnerSearchItem);

        tDanceFavourite = DanceFavourite.VERY_GOOD;
        tOldSpinnerSearchItem = new OldSpinnerSearchItem(tDanceFavourite.getCode(), tDanceFavourite.getText(),
                true, tDanceFavourite,
                tDanceFavourite.getPriority(), R.drawable.ic_favourite_verygood);
        tAL.add(tOldSpinnerSearchItem);


        tDanceFavourite = DanceFavourite.UNKNOWN;
        tOldSpinnerSearchItem = new OldSpinnerSearchItem(tDanceFavourite.getCode(), tDanceFavourite.getText(),
                true, tDanceFavourite,
                tDanceFavourite.getPriority(), R.drawable.ic_favourite_unknown);
        tAL.add(tOldSpinnerSearchItem);

        tDanceFavourite = DanceFavourite.HORRIBLE;
        tOldSpinnerSearchItem = new OldSpinnerSearchItem(tDanceFavourite.getCode(), tDanceFavourite.getText(),
                true, tDanceFavourite,
                tDanceFavourite.getPriority(), R.drawable.ic_favourite_horrible);
        tAL.add(tOldSpinnerSearchItem);

        return tAL;

    }


}
