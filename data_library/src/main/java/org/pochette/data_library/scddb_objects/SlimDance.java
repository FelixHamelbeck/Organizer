package org.pochette.data_library.scddb_objects;


import android.database.Cursor;

/**
 * Dance correspond to an dance as available on scddb
 */
@SuppressWarnings("unused")
public class SlimDance {

    //Variables
    private static final String TAG = "FEHA (SlimDance)";
    //private static String MAIN_CLASS = Dance.class.getName();

    public int mId;

    //Constructor



    public SlimDance(int tId) {
        mId = tId;
    }
    //Setter and Getter


    //Livecycle

    //Static Methods
//	static String getSelectStatement() {
//
//	}

    //Internal Organs

    //Interface




    public static SlimDance convertCursor(Cursor iCursor) {
        return new SlimDance(
                iCursor.getInt(iCursor.getColumnIndex("D_ID"))
        );
    }




}