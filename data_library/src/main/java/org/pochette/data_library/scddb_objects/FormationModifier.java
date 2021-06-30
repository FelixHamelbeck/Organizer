package org.pochette.data_library.scddb_objects;

import android.database.Cursor;

import java.util.Locale;

import androidx.annotation.NonNull;

/**
 * FormationModifier  corresponds to an FormationModifier as available on scddb
 */
public class FormationModifier {

    //Variables
	@SuppressWarnings("unused")
    private final static String TAG = "FEHA (Formation)";
    public int mId;
    public String mKey;
    public String mName;
    //Constructor
    public FormationModifier(int iId, String iKey, String iName) {
        mId = iId;
        mKey = iKey;
        mName = iName;
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

    public static FormationModifier convertCursor(Cursor tCursor) {
        return new FormationModifier(
                tCursor.getInt(tCursor.getColumnIndex("ID")),
                tCursor.getString(tCursor.getColumnIndex("KEY")),
                tCursor.getString(tCursor.getColumnIndex("NAME"))
        );
    }

}