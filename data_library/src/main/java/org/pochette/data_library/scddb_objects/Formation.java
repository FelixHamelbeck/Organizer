package org.pochette.data_library.scddb_objects;

import android.database.Cursor;

import java.util.ArrayList;

import androidx.annotation.NonNull;

/**
 * Formationcorrespond to an formation as available on scddb
 */
public class Formation {

    //Variables
    @SuppressWarnings("unused")
    private static final String TAG = "FEHA (Formation)";

    public int mId;
    public String mKey;
    public String mName;
    public int mRootId;
    public String mSearchId;
    public ArrayList<FormationModifier> mFormationModifiers;
    public int mCountDances;
    public int mCountFormation;

    //Constructor
    public Formation(int iId, String iKey, String iName, int iRootId, int iCountDances, int iCountFormation) {
        mId = iId;
        mKey = iKey;
        mName = iName;
        mRootId = iRootId;
        mCountDances = iCountDances;
        mCountFormation = iCountFormation;
    }


    //Setter and Getter
    //Livecycle
    //Static Methods
    //Internal Organs
    //Interface

    public static Formation convertCursor(Cursor tCursor) {
        return new Formation(
                tCursor.getInt(tCursor.getColumnIndex("ID")),
                tCursor.getString(tCursor.getColumnIndex("SEARCHID")),
                tCursor.getString(tCursor.getColumnIndex("NAME")),
                tCursor.getInt(tCursor.getColumnIndex("ROOT_ID")),
                tCursor.getInt(tCursor.getColumnIndex("COUNT_DANCE")),
                tCursor.getInt(tCursor.getColumnIndex("COUNT_FORMATION"))
        );
    }

    @NonNull
    public String toString() {
        return mKey;
    }

}