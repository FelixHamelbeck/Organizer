package org.pochette.data_library.scddb_objects;


import android.database.Cursor;

import java.util.Locale;

import androidx.annotation.NonNull;


/**
 * Album correspond to a crib as available on scddb
 * <p>
 * the method converts created the criblines from the ScddbText
 */
@SuppressWarnings("unused")
public class Crib {

    //Variables
    @SuppressWarnings("FieldMayBeFinal")
    private static String TAG = "FEHA (Crib)";
    public int mId;
    public int mScddbDance_id;
    public String mScddbText;
    public int mReliablilty;

    //Constructor
    public Crib(int mId, int mScddbDance_id, String mScddbText, int mReliablilty) {
        this.mId = mId;
        this.mScddbDance_id = mScddbDance_id;
        this.mScddbText = mScddbText;
        this.mReliablilty = mReliablilty;
    }

//    public Crib(int tScddbDance_id) throws Exception {
//        Logg.d(TAG, String.format(Locale.ENGLISH, "Construct with id: %d", tScddbDance_id));
//        SearchPattern tSearchPattern = new SearchPattern(Crib.class);
//        tSearchPattern.addSearch_Criteria(
//                new SearchCriteria("DANCE_ID",
//                        String.format(Locale.ENGLISH, "%d", tScddbDance_id)));
//        Crib tScddbCrib;
//        tScddbCrib = new SearchCall(
//                Crib.class, tSearchPattern, null).produceFirst();
//
//
//        //tScddbCrib = new ReadAction(Crib.class, tSearchPattern, null).produceFirst();
//
//        if (tScddbCrib == null) {
//            throw new RuntimeException("No Crib for DanceId" + tScddbDance_id);
//        }
//        this.mId = tScddbCrib.mId;
//        this.mScddbDance_id = tScddbCrib.mScddbDance_id;
//        this.mScddbText = tScddbCrib.mScddbText;
//        this.mReliablilty = tScddbCrib.mReliablilty;
//        if (mScddbDance_id != tScddbDance_id) {
//            throw new Exception("CRIB FOR DANCE WITH ID DOES NOT EXIST: " + tScddbDance_id);
//        }
//    }
//

    //Setter and Getter
    public static String getClassname_DB() {
        return "org.pochette.data_library.business_objects.ScddbCrib_DB";
    }

    //Livecycle

    //Static Methods

    //Internal Organs

//    public ArrayList<CribLine> convert() {
//        if (mScddbText == null) {
//            return new ArrayList<>(0);
//        }
//        ArrayList<CribLine> output = new ArrayList<>(0);
//        try {
//            int i_outline = 0;
//            String s_bar = "";
//            String s_description;
//            String[] lines = mScddbText.split(Character.toString((char) 10));// newline
//            for (String line : lines) {
//                if (line.endsWith("::")) {
//                    i_outline++;
//                    s_bar = line.substring(0, line.lastIndexOf("::"));
//                } else {
//                    s_description = line.trim();
//                    if (s_description.equals("{{explanation}}")) {
//                        continue;
//                    }
//                    if (s_description.isEmpty()) {
//                        continue;
//                    }
//                    CribLine tCribLine = new CribLine(i_outline, s_bar, s_description);
//                    output.add(tCribLine);
//                    s_bar = "";
//                    //noinspection UnusedAssignment
//                    s_description = ""; // reset for safety reasons
//                }
//            }
//            return output;
//        } catch(Exception e) {
//            Logg.e(TAG, e.toString());
//            return null;
//        }
//    }

    //Interface
    public static Crib convertCursor(Cursor tCursor) {
        Crib result;
        //Crib(int mId, int mScddbDance_id, String mScddbText, int mReliablilty) {
        int id = tCursor.getInt(tCursor.getColumnIndex("_ID"));
        int dance_id = tCursor.getInt(tCursor.getColumnIndex("DANCE_ID"));
        String tText = tCursor.getString(tCursor.getColumnIndex("SCDDBTEXT"));
        int relia = tCursor.getInt(tCursor.getColumnIndex("RELIABILITY"));


        return new Crib(
                tCursor.getInt(tCursor.getColumnIndex("_ID")),
                tCursor.getInt(tCursor.getColumnIndex("DANCE_ID")),
                tCursor.getString(tCursor.getColumnIndex("SCDDBTEXT")),
                tCursor.getInt(tCursor.getColumnIndex("RELIABILITY")));
    }


    @NonNull
    public String toString() {
        return String.format(Locale.ENGLISH, "%d [%d]: %s (of %d)",
                mScddbDance_id, mId, mScddbText.substring(1, 20), mScddbText.length());
    }
}