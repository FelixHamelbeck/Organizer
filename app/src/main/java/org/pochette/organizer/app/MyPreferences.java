package org.pochette.organizer.app;

import android.content.SharedPreferences;

import org.pochette.organizer.BuildConfig;
import org.pochette.utils_lib.logg.Logg;


@SuppressWarnings("unused")
public class MyPreferences {

    static SharedPreferences mSharedPreferences;
    private final static String TAG = "FEHA (MyPreferences)";

    public final static String PREFERENCE_KEY_SCD_DIRECTORY_URI = "SCD_DIRECTORY_URI";
    public final static String PREFERENCE_KEY_NONSCD_DIRECTORY_URI = "NONSCD_DIRECTORY_URI";
    public final static String PREFERENCE_KEY_DIAGRAM_DIRECTORY_URI = "DIAGRAM_DIRECTORY_URI";

    public final static String PREFERENCE_KEY_PREVIOUS_COUNT_MP3_SCD = "PREVIOUS_COUNT_MP3_SCD";
    public final static String PREFERENCE_KEY_PREVIOUS_COUNT_MP3_NONSCD = "PREVIOUS_COUNT_MP3_NONSCD";

    public final static String PREFERENCE_KEY_SCREEN_HEIGHT = "SCREEN_HEIGHT";
    public final static String PREFERENCE_KEY_SCREEN_WIDTH = "SCREEN_WIDTH";

    public final static String PREFERENCE_KEY_TIME_A = "TIME_A";
    public final static String PREFERENCE_KEY_TIME_B = "TIME_B";


    //Variables
    //Constructor

    //Setter and Getter


    static void setSharedPreferences(SharedPreferences iSharedPreferences) {
        mSharedPreferences = iSharedPreferences;
    }

    //Livecycle
    //Static Methods
    //Internal Organs
    //Interface

    ////////////////////////////// String /////////////////////////
    public static void savePreferenceString(String iKey, String iNewValue) {
        if (BuildConfig.DEBUG) {
            Logg.d(TAG, iKey + " stores string value " + iNewValue);
        }
        SharedPreferences.Editor tEditor = mSharedPreferences.edit();
        tEditor.putString(iKey, iNewValue);
        tEditor.apply();
    }

    public static String getPreferenceString(String iKey, String iDefault) {
        String tValue;
        if (mSharedPreferences.contains(iKey)) {
            tValue = mSharedPreferences.getString(iKey, iDefault);
        } else {
            tValue = iDefault;
        }
        if (BuildConfig.DEBUG) {
            Logg.d(TAG, iKey + " get  " + tValue);
        }
        return tValue;
    }

    ///////////////////////////// int //////////////////////////////////
    public static void savePreferenceInt(String iKey, int iNewValue) {
        if (BuildConfig.DEBUG) {
            Logg.d(TAG, iKey + " stores int value " + iNewValue);
        }
        SharedPreferences.Editor tEditor = mSharedPreferences.edit();
        tEditor.putInt(iKey, iNewValue);
        //tEditor.apply();
        tEditor.commit();
    }

    public static Integer getPreferenceInt(String iKey, int iDefault) {
        @SuppressWarnings("WrapperTypeMayBePrimitive") Integer tValue;
        if (mSharedPreferences.contains(iKey)) {
            tValue = mSharedPreferences.getInt(iKey, iDefault);
        } else {
            savePreferenceInt(iKey, iDefault);
            tValue = iDefault;
        }
        if (BuildConfig.DEBUG) {
            Logg.d(TAG, iKey + " get  " + tValue);
        }
        return tValue;
    }

    /////////////////////////////////// boolean /////////////////////////////////////
    public static void savePreferenceBoolean(String iKey, boolean iNewValue) {
        if (BuildConfig.DEBUG) {
            Logg.d(TAG, iKey + " stores boolean value " + iNewValue);
        }
        SharedPreferences.Editor tEditor = mSharedPreferences.edit();
        tEditor.putBoolean(iKey, iNewValue);
        tEditor.apply();
    }

    public static boolean getPreferenceBoolean(String iKey, boolean iDefault) {
        boolean tValue;
        if (mSharedPreferences.contains(iKey)) {
            tValue = mSharedPreferences.getBoolean(iKey, iDefault);
        } else {
            tValue = iDefault;
        }
        if (BuildConfig.DEBUG) {
            Logg.d(TAG, iKey + " get  " + tValue);
        }
        return tValue;
    }

}
