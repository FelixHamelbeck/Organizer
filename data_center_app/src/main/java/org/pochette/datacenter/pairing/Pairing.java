package org.pochette.data_library.pairing;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;

import org.pochette.data_library.search.SearchCall;
import org.pochette.data_library.search.WriteCall;
import org.pochette.utils_lib.search.SearchPattern;
import org.pochette.utils_lib.search.SearchCriteria;

import java.util.Date;
import java.util.Locale;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class Pairing {

    private static final String TAG = "FEHA (Pairing)";

    // variables
    int mId;
    PairingObject mPairingObject;
    int mScddb_Id;
    int mLdb_Id;
    PairingSource mPairingSource;
    PairingStatus mPairingStatus;
    Date mLastChangeDate;
    float mScore; // perfection is 1.f
    // constructor

    public Pairing(PairingObject iPairingObject, int iScddb_Id, int iLdb_Id,
                   PairingSource iPairingSource, PairingStatus iPairingStatus,
                   Date iLastChangeDate, float iScore) {
        mPairingObject = iPairingObject;
        mScddb_Id = iScddb_Id;
        mLdb_Id = iLdb_Id;
        mPairingSource = iPairingSource;
        mPairingStatus = iPairingStatus;
        mLastChangeDate = iLastChangeDate;
        mScore = iScore;
    }


    // setter and getter

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public PairingObject getPairingObject() {
        return mPairingObject;
    }

    public void setPairingObject(PairingObject pairingObject) {
        mPairingObject = pairingObject;
    }

    public int getScddb_Id() {
        return mScddb_Id;
    }

    public void setScddb_Id(int scddb_Id) {
        mScddb_Id = scddb_Id;
    }

    public int getLdb_Id() {
        return mLdb_Id;
    }

    public void setLdb_Id(int ldb_Id) {
        mLdb_Id = ldb_Id;
    }

    public PairingSource getPairingSource() {
        return mPairingSource;
    }

    public void setPairingSource(PairingSource pairingSource) {
        mPairingSource = pairingSource;
    }

    public PairingStatus getPairingStatus() {
        return mPairingStatus;
    }

    public void setPairingStatus(PairingStatus pairingStatus) {
        mPairingStatus = pairingStatus;
    }

    public Date getLastChangeDate() {
        return mLastChangeDate;
    }

    public void setLastChangeDate(Date lastChangeDate) {
        mLastChangeDate = lastChangeDate;
    }

    public float getScore() {
        return mScore;
    }

    public void setScore(float score) {
        mScore = score;
    }


    // lifecylce and override
    // internal
    // public methods
    public Pairing save() {
        WriteCall tWriteCall = new WriteCall(Pairing.class, this);
        if (mId <= 0) {
            try {
                mId = tWriteCall.insert();
            } catch(SQLiteConstraintException e) {
                //UNIQUE constraint failed: PAIRING.SCDDB_ID, PAIRING.LDB_ID (Sqlite code 2067), (OS error - 2:No such file or directory)
                // duplicate key requires an update statement
                // just to read the id, we select first
                SearchPattern tSearchPattern;
                SearchCall tSearchCall;
                tSearchPattern = new SearchPattern(Pairing.class);
                tSearchPattern.addSearch_Criteria(new SearchCriteria("LDB_ID", "" + mLdb_Id));
                tSearchPattern.addSearch_Criteria(new SearchCriteria("SCDDB_ID", "" + mScddb_Id));
                tSearchCall = new SearchCall(Pairing.class, tSearchPattern, null);

                Pairing tPairingExist = tSearchCall.produceFirst();
                mId = tPairingExist.mId;
                //Logg.i(TAG, "Id> " + mId);
                tWriteCall.update();
            }
        } else {
            tWriteCall.update();
        }
        return this;
    }

    public ContentValues getContentValues() {
        ContentValues tContentValues = new ContentValues();
        tContentValues.put("PAIRING_OBJECT", getPairingObject().getCode());
        tContentValues.put("SCDDB_ID", getScddb_Id());
        tContentValues.put("LDB_ID", getLdb_Id());
        tContentValues.put("PAIRING_SOURCE", getPairingSource().getCode());
        tContentValues.put("PAIRING_STATUS", getPairingStatus().getCode());
        if (getLastChangeDate() != null) {
            tContentValues.put("LAST_CHANGE_DATE", getLastChangeDate().toString());
        }
        tContentValues.put("SCORE", getScore());
        return tContentValues;
    }

    public static Pairing convertCursor(Cursor tCursor) {
        Pairing result;

        result = new Pairing(

                PairingObject.getByCode(tCursor.getString(tCursor.getColumnIndex("PAIRING_OBJECT"))),
                tCursor.getInt(tCursor.getColumnIndex("SCDDB_ID")),
                tCursor.getInt(tCursor.getColumnIndex("LDB_ID")),
                PairingSource.fromCode(tCursor.getString(tCursor.getColumnIndex("PAIRING_SOURCE"))),
                PairingStatus.fromCode(tCursor.getString(tCursor.getColumnIndex("PAIRING_STATUS"))),
                new Date(tCursor.getLong(tCursor.getColumnIndex("LAST_CHANGE_DATE"))),
                tCursor.getFloat(tCursor.getColumnIndex("SCORE")));
        result.mId = tCursor.getInt(tCursor.getColumnIndex("ID"));
        return result;
    }


    
    public String toString() {
        @SuppressWarnings("UnnecessaryLocalVariable")
        String tString = String.format(Locale.ENGLISH,
                "Pairing of %s Scddb %d ==  Ldb %d",
                mPairingObject.getText(), mScddb_Id, mLdb_Id);
        return tString;
    }

//    public void save() {
//        Pairing_DB.getInstance().save(this);
//    }
//
//    public void delete() {
//        Pairing_DB.getInstance().delete(this);
//    }

}
