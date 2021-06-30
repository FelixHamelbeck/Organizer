package org.pochette.data_library.scddb_objects;


import android.database.Cursor;

import java.util.Locale;

import androidx.annotation.NonNull;

/**
 * FormationRoot  correspond to an FormationRoot  as available on scddb
 */
public class FormationRoot {

    //Variables
    @SuppressWarnings("unused")
    private static final String TAG = "FEHA (FormationRoot)";

    public int mId;
    public String mKey;
    public String mName;
    public int mCountDances;
    public int mCountFormation;

    //Constructor
    public FormationRoot(int iId, String iKey, String iName, int iCountDances, int iCountFormation) {
        mId = iId;
        mKey = iKey;
        mName = iName;
        mCountDances = iCountDances;
        mCountFormation = iCountFormation;
    }

    //Setter and Getter
    //Livecycle
    //Static Methods
    //Internal Organs
    //Interface

    @NonNull
    public String toString() {
        return String.format(Locale.ENGLISH, "%s [%s]", mName, mKey);
    }

    public static FormationRoot convertCursor(Cursor tCursor) {
        return new FormationRoot(
                tCursor.getInt(tCursor.getColumnIndex("ID")),
                tCursor.getString(tCursor.getColumnIndex("KEY")),
                tCursor.getString(tCursor.getColumnIndex("NAME")),
                tCursor.getInt(tCursor.getColumnIndex("COUNT_DANCE")),
                tCursor.getInt(tCursor.getColumnIndex("COUNT_FORMATION"))
        );
    }


}