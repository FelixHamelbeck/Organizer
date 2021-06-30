package org.pochette.data_library.scddb_objects;


import android.database.Cursor;

import org.pochette.data_library.database_management.SearchCall;
import org.pochette.utils_lib.logg.Logg;
import org.pochette.utils_lib.search.SearchCriteria;
import org.pochette.utils_lib.search.SearchPattern;

import java.util.ArrayList;
import java.util.Locale;

import androidx.annotation.NonNull;


/**
 * Album correspond to a crib as available on scddb
 * <p>
 * the method converts created the criblines from the ScddbText
 */
public class Crib {

    //Variables
    private static final String TAG = "FEHA (Crib)";
    public int mId;
    public int mDance_id;
    public String mText;
    public int mReliablilty;

    //Constructor
    public Crib(int mId, int mScddbDance_id, String mScddbText, int mReliablilty) {
        this.mId = mId;
        this.mDance_id = mScddbDance_id;
        this.mText = mScddbText;
        this.mReliablilty = mReliablilty;
    }

    public Crib(int iDance_id) throws Exception {
        Logg.d(TAG, String.format(Locale.ENGLISH, "Construct with id: %d", iDance_id));
        SearchPattern tSearchPattern = new SearchPattern(Crib.class);
        tSearchPattern.addSearch_Criteria(
                new SearchCriteria("DANCE_ID",
                        String.format(Locale.ENGLISH, "%d", iDance_id)));
        Crib tCrib;
        tCrib = new SearchCall(
                Crib.class, tSearchPattern, null).produceFirst();
        if (tCrib == null) {
            throw new RuntimeException("No Crib for DanceId" + iDance_id);
        }
        this.mId = tCrib.mId;
        this.mDance_id = tCrib.mDance_id;
        this.mText = tCrib.mText;
        this.mReliablilty = tCrib.mReliablilty;
        if (mDance_id != iDance_id) {
            throw new Exception("CRIB FOR DANCE WITH ID DOES NOT EXIST: " + iDance_id);
        }
    }


    //Setter and Getter

    //Livecycle

    //Static Methods

    //Internal Organs

    public ArrayList<CribLine> getAL_CribLine() {
        if (mText == null) {
            return new ArrayList<>(0);
        }
        ArrayList<CribLine> output = new ArrayList<>(0);
        try {
            int i_outline = 0;
            String s_bar = "";
            String s_description;
            String[] lines = mText.split(Character.toString((char) 10));// newline
            for (String line : lines) {
                if (line.endsWith("::")) {
                    i_outline++;
                    s_bar = line.substring(0, line.lastIndexOf("::"));
                } else {
                    s_description = line.trim();
                    if (s_description.equals("{{explanation}}")) {
                        continue;
                    }
                    if (s_description.isEmpty()) {
                        continue;
                    }
                    CribLine tCribLine = new CribLine(i_outline, s_bar, s_description);
                    output.add(tCribLine);
                    s_bar = "";
                    //noinspection UnusedAssignment
                    s_description = ""; // reset for safety reasons
                }
            }
            return output;
        } catch(Exception e) {
            Logg.e(TAG, e.toString());
            return null;
        }
    }

    //Interface
    public static Crib convertCursor(Cursor tCursor) {
        return new Crib(
                tCursor.getInt(tCursor.getColumnIndex("_ID")),
                tCursor.getInt(tCursor.getColumnIndex("DANCE_ID")),
                tCursor.getString(tCursor.getColumnIndex("SCDDBTEXT")),
                tCursor.getInt(tCursor.getColumnIndex("RELIABILITY")));
    }

    @NonNull
    public String toString() {
        return String.format(Locale.ENGLISH, "%d [%d]: %s (of %d)",
                mDance_id, mId, mText.substring(1, 20), mText.length());
    }
}