package org.pochette.organizer.app;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import org.pochette.utils_lib.logg.Logg;

import java.util.Locale;

public class PreparationOfPreference {

    @SuppressWarnings("unused")
    private static final String TAG = "FEHA (PrepPref)";

    // variables
    // constructor
    // setter and getter
    // lifecylce and override
    // internal
    // public methods

    public static void execute(Activity iActivity) {
        String tPackageName = iActivity.getApplicationContext().getPackageName();
        SharedPreferences tSharedPreferences =
                iActivity.getApplicationContext().getSharedPreferences(
                        tPackageName,
                        Context.MODE_PRIVATE);
        MyPreferences.setSharedPreferences(tSharedPreferences);
        String tText=String.format(Locale.ENGLISH, "PreparationOfPreference for %s finished",tPackageName );
        Logg.i(TAG, tText);
    }


    public static void execute(Context iContext) {
        String tPackageName = iContext.getPackageName();
        SharedPreferences tSharedPreferences =
                iContext.getSharedPreferences(
                        tPackageName,
                        Context.MODE_PRIVATE);
        MyPreferences.setSharedPreferences(tSharedPreferences);
        String tText=String.format(Locale.ENGLISH, "PreparationOfPreference for %s finished",tPackageName );
        Logg.i(TAG, tText);
    }


}
