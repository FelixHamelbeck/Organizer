package org.pochette.data_library.scddb_objects;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;

import org.pochette.data_library.database_management.SearchCall;
import org.pochette.data_library.database_management.WriteCall;
import org.pochette.utils_lib.search.SearchCriteria;
import org.pochette.utils_lib.search.SearchPattern;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.NonNull;
import androidx.annotation.StringDef;

/**
 * The class Classification contains the instruction to play one item
 */

public class DanceClassification {

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({VYGO, GOOD, OKAY, NEUT, UNKN, RANO, HORR})
    public @interface Filter{}

    static final String TAG = "FEHA (DanceClassification)";

    public static final String VYGO = "VYGO";
    public static final String GOOD = "GOOD";
    public static final String OKAY = "OKAY";
    public static final String NEUT = "NEUT";
    public static final String UNKN = "UNKN";
    public static final String RANO = "RANO";
    public static final String HORR = "HORR";



    int mId;
    int mObject_Id;
    String mFavourite_Code;

//	/**
//	 * Constructor when the database ID is knwon
//	 * @param tId
//	 */
//	public DanceClassification(int tId) {
//
//	}

    /**
     * Constructor, when the referenced object is known
     */
    public DanceClassification() {
        mId = -1;
        mFavourite_Code = "UNKN";

    }

    /**
     * Constructor for convertCursor
     *
     * @param tId             database Id
     * @param tObject_Id      id of referenced object
     * @param iFavourite_Code DanceFavourite
     */
    public DanceClassification(int tId, int tObject_Id, String iFavourite_Code) {
        mId = tId;
        mObject_Id = tObject_Id;
        mFavourite_Code = iFavourite_Code;
    }


    //<editor-fold desc="Setter and Getter">

    public int getId() {
        return mId;
    }

    public void setId(int tId) {
        mId = tId;
    }

    public int getObject_Id() {
        return mObject_Id;
    }

    public void setObject_Id(int tObject_Id) {
        mObject_Id = tObject_Id;
    }

    public String getFavourite_Code() {
        return mFavourite_Code;
    }

    public void setFavourite_Code(String favourite_Code) {
        mFavourite_Code = favourite_Code;
    }

//</editor-fold>


    @NonNull
    @Override
    public String toString() {
        return "Classification{" +
                ", Id=" + mObject_Id +
                '}' + mFavourite_Code;
    }


    /**
     * copy the redundant information from MusicFile to the extra attributes
     */

    public static String getClassname_DB() {
        return "org.pochette.quarry.n_bo_dance.DanceClassification_DB";
    }


    public ContentValues getContentValues() {
        ContentValues tContentValues = new ContentValues();

        tContentValues.put("OBJECT_ID", mObject_Id);
        tContentValues.put("FAVOURITE", mFavourite_Code);
        return tContentValues;
    }

    public static DanceClassification convertCursor(Cursor tCursor) {
        DanceClassification result;
        result = new DanceClassification(
                tCursor.getInt(tCursor.getColumnIndex("ID")),
                tCursor.getInt(tCursor.getColumnIndex("OBJECT_ID")),
                tCursor.getString(tCursor.getColumnIndex("FAVOURITE")));
        return result;
    }


    public DanceClassification save() {
        WriteCall tWriteCall = new WriteCall(DanceClassification.class, this);
        if (mId <= 0) {
            try {
                mId = tWriteCall.insert();
            } catch(SQLiteConstraintException e) {
                  // duplicate key requires an update statement
                // just to read the id, we select first
                SearchPattern tSearchPattern;
                SearchCall tSearchCall;
                tSearchPattern = new SearchPattern(DanceClassification.class);
                tSearchPattern.addSearch_Criteria(new SearchCriteria("OBJECT_ID", "" + mObject_Id));
                tSearchCall = new SearchCall(DanceClassification.class, tSearchPattern, null);
                DanceClassification  tDanceClassificationExist = tSearchCall.produceFirst();
                mId = tDanceClassificationExist.mId;
                tWriteCall.update();
            }
        } else {
            tWriteCall.update();
        }
        return this;
    }


}
