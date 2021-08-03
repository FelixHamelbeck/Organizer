package org.pochette.data_library.requestlist;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.pochette.data_library.database_management.DeleteCall;
import org.pochette.data_library.database_management.SearchCall;
import org.pochette.data_library.database_management.WriteCall;
import org.pochette.data_library.music.MusicFile;
import org.pochette.data_library.music.MusicPreference;
import org.pochette.data_library.scddb_objects.Dance;
import org.pochette.utils_lib.logg.Logg;
import org.pochette.utils_lib.report.ReportSystem;
import org.pochette.utils_lib.search.SearchCriteria;
import org.pochette.utils_lib.search.SearchPattern;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * The class Requestlist is a arraylist of Request
 * <p>
 * The arraylist is only populated when needed, so insert resources
 * mCountDances is always correct,
 * mJsonString is always correct, mAL_Request is treated as a slave
 */
@SuppressWarnings("unused")
public class Requestlist {

    //Variables
    private static final String TAG = "FEHA (Requestlist)";
    private static Requestlist mDefaultRequestlist = null;
    private int mId;
    private String mName;
    private Requestlist_Purpose mRequestlist_Purpose;
    private int mRequestCount;
    private Date mLastEditDate;
    private Date mLastDateUsed;
    private ArrayList<Request> mAL_Request;
    private String mJsonString;

    //<editor-fold desc="Constructor">
    /**
     * Constructor as used by the GUI, i.e. when the Requestlist is created first time
     *
     * @param iName                of the Requestlist
     * @param iRequestlist_purpose its purpose
     */
    public Requestlist(String iName, Requestlist_Purpose iRequestlist_purpose) {
        SearchCriteria tSearchCriteria = new SearchCriteria("NAME", standarizeName(iName));
        SearchPattern tSearchPattern = new SearchPattern(Requestlist.class);
        tSearchPattern.addSearch_Criteria(tSearchCriteria);
        SearchCall tSearchCall = new SearchCall(Requestlist.class,
                tSearchPattern, null);

        if (tSearchCall.produceCount() > 0) {
            Logg.w(TAG, "Requestlist already exists");
        }
        mName = standarizeName(iName);
        mId = 0;
        mRequestlist_Purpose = iRequestlist_purpose;
        mAL_Request = new ArrayList<>(0);
        generateJsonString();
        mRequestCount = 0;
    }

    /**
     * Constructor for convertCursor, hence private
     *
     * @param tId                  the database ID
     * @param tName                the name of the Requestlist
     * @param tRequestlist_Purpose its purpose
     * @param tCount_Request       playinstrucyion count
     * @param iLastEditDate        its lastEditDate
     * @param iLastDateUsed        its last used Date
     */
    private Requestlist(int tId, String tName, Requestlist_Purpose tRequestlist_Purpose,
                        int tCount_Request,
                        Date iLastEditDate, Date iLastDateUsed,
                        String tJsonString) {
        mName = standarizeName(tName);
        mAL_Request = null;
        if (tRequestlist_Purpose == null) {
            mRequestlist_Purpose = Requestlist_Purpose.UNDEFINED;
        } else {
            mRequestlist_Purpose = tRequestlist_Purpose;
        }
        mId = tId;
        mLastEditDate = iLastEditDate;
        mLastDateUsed = iLastDateUsed;
        mRequestCount = tCount_Request;
        mJsonString = tJsonString;
    }
    //</editor-fold>

    //<editor-fold desc="Setter and Getter">
    public int getId() {
        return mId;
    }

    public void setId(int mId) {
        this.mId = mId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = standarizeName(mName);
    }

    public int getRequestCount() {
        return mRequestCount;
    }

    public Requestlist_Purpose getRequestlist_Purpose() {
        return mRequestlist_Purpose;
    }

    public void setRequestlist_Purpose(Requestlist_Purpose tStatus) {
        mRequestlist_Purpose = tStatus;
    }

    public ArrayList<Request> getAR_Request() {
        prepareAR_Request();
        return mAL_Request;
    }

    public Date getLastEditDate() {
        return mLastEditDate;
    }

    public Date getLastDateUsed() {
        return mLastDateUsed;
    }

    public boolean isDefault() {
        if (mDefaultRequestlist == null) {
            return false;
        } else {
            return mDefaultRequestlist.mId == mId;
        }
    }

    public String toString() {
        String tText;
        tText = String.format(Locale.ENGLISH,
                "%s (%d)", mName, mRequestCount);
        return tText;
    }

//</editor-fold>

    //<editor-fold desc="adding stuff">
    private static void defineDefaultRequestlist() {
        if (mDefaultRequestlist == null) {
            SearchPattern tSearchPattern = new SearchPattern(Requestlist.class);
            tSearchPattern.setSortOrder("MOST_RECENT");
            SearchCall tSearchCall = new SearchCall(Requestlist.class,
                    tSearchPattern, null);
            mDefaultRequestlist = tSearchCall.produceFirst();
            if (mDefaultRequestlist == null) {
                Requestlist tRequestlist = new Requestlist("Default", Requestlist_Purpose.UNDEFINED);
                tRequestlist.save();
                mDefaultRequestlist = tRequestlist;
            }
            mDefaultRequestlist = tSearchCall.produceFirst();
        }
    }

    //</editor-fold>


    //Livecycle
    //Static Methods
    public static Requestlist getById(int iId) {
        SearchCriteria tSearchCriteria = new SearchCriteria("ID", "" + iId);
        SearchPattern tSearchPattern = new SearchPattern(Requestlist.class);
        tSearchPattern.addSearch_Criteria(tSearchCriteria);
        SearchCall tSearchCall = new SearchCall(Requestlist.class,
                tSearchPattern, null);
        return tSearchCall.produceFirst();
    }

    //Internal Organs
    private static String standarizeName(String tName) {
        return tName.replaceAll("[\\n\\t]", "");
    }

    private void prepareAR_Request() {
        if (mAL_Request == null) {
            mAL_Request = new ArrayList<>(0);
        }
        convertJson2AL();
        mRequestCount = mAL_Request.size();
    }


    private void convertJson2AL() {
        if (mJsonString != null && !mJsonString.isEmpty()) {
            try {
                Logg.i(TAG, mJsonString);
                ArrayList<Request> tAL = new ArrayList<>();
                JSONObject tJsonObject = new JSONObject(mJsonString);
                JSONArray tJsonArray = tJsonObject.getJSONArray("List");
                for (int i = 0; i < tJsonArray.length(); i++) {
                    JSONObject lJsonObject = tJsonArray.getJSONObject(i);
                    Request lRequest = new Request(lJsonObject);
                    tAL.add(lRequest);
                }
                mAL_Request = tAL;
            } catch(JSONException e) {
                Logg.i(TAG, mJsonString);
                Logg.w(TAG, e.toString());
            }
        }
    }



    private void manageAdd(int iPosition, Request iRequest) {
        if (mAL_Request == null) {
            prepareAR_Request();
        }
        if (iPosition < 0 || iPosition > mAL_Request.size()) {
            String tString = String.format(Locale.ENGLISH,
                    "Add at position %d not allowed for arraylist of size %d",
                    iPosition, mAL_Request.size()
            );
            throw new IllegalArgumentException(tString);
        }
        if (mId == 0) {
            this.save();
        }
        // to add at the end no shift has to be done
        if (iPosition == mAL_Request.size()) {
            mAL_Request.add(iRequest);
        } else {
            for (int i = mAL_Request.size() - 1; i >= iPosition; i--) {
                Request tRequest = mAL_Request.get(i);
            }
            // after shifting, add the new one
            mAL_Request.add(iPosition, iRequest);
        }
        mRequestCount = mAL_Request.size();
    }


    private void manageRemove(int iPosition) {
        if (iPosition < 0 || iPosition >= mAL_Request.size()) {
            String tString = String.format(Locale.ENGLISH,
                    "Remove at position %d not allowed for arraylist of size %d",
                    iPosition, mAL_Request.size()
            );
            throw new IllegalArgumentException(tString);
        }
        if (mId == 0) {
            this.save();
        }
        mAL_Request.remove(iPosition);
        // after delete, close any gap
        for (int i = iPosition; i < mAL_Request.size(); i++) {
            Request tRequest = mAL_Request.get(i);
        }
        mRequestCount = mAL_Request.size();
    }

    private void manageSwap(int iPosition_A, int iPosition_B) {
        int tPosition_A;
        int tPosition_B;
        // make sure A is the smaller position
        if (iPosition_A == iPosition_B) {
            // trivial request
            return;
        } else if (iPosition_A < iPosition_B) {
            tPosition_A = iPosition_A;
            tPosition_B = iPosition_B;
        } else {
            tPosition_A = iPosition_B;
            tPosition_B = iPosition_A;
        }
        if (tPosition_A < 0 || tPosition_A >= mAL_Request.size()) {
            String tString = String.format(Locale.ENGLISH,
                    "Switch at position %d not allowed for arraylist of size %d",
                    tPosition_A, mAL_Request.size()
            );
            throw new IllegalArgumentException(tString);
        }
        if (tPosition_B >= mAL_Request.size()) {
            String tString = String.format(Locale.ENGLISH,
                    "Switch at position %d not allowed for arraylist of size %d",
                    tPosition_B, mAL_Request.size()
            );
            throw new IllegalArgumentException(tString);
        }
        if (mId == 0) {
            this.save();
        }
        // the database does not allow a duplicate key Requestlist_id, Requestlist_pos
        // so delete A is required
        Request tRequest_A = mAL_Request.get(tPosition_A);
        // move B to A
        Request tRequest_B = mAL_Request.get(tPosition_B);

        // insert A at position of B
        // after DB done, change in memory
        mAL_Request.remove(tPosition_A);
        mAL_Request.add(tPosition_A, tRequest_B);
        mAL_Request.remove(tPosition_B);
        mAL_Request.add(tPosition_B, tRequest_A);
        mRequestCount = mAL_Request.size();
    }

    private void generateJsonString() {
        JSONArray tJsonArray;
        try {
            JSONObject tJsonObject = new JSONObject();
            tJsonArray = new JSONArray();
            if (mAL_Request == null) {
                mAL_Request = new ArrayList<>();
            }
            for (Request lRequest : mAL_Request) {
                JSONObject lJsonObject = lRequest.getJsonObject();
                tJsonArray.put(lJsonObject);
            }
            tJsonObject.put("List", tJsonArray);
            mJsonString = tJsonObject.toString();
        } catch(JSONException e) {
            Logg.w(TAG, e.toString());
        }
    }


    //Interface


    public Requestlist save() {
        WriteCall tWriteCall = new WriteCall(Requestlist.class, this);
        if (mId <= 0) {
            try {
                mId = tWriteCall.insert();
            } catch(SQLiteConstraintException e) {

                // duplicate key requires an update statement
                // just to read the id, we select first
                SearchPattern tSearchPattern;
                SearchCall tSearchCall;
                tSearchPattern = new SearchPattern(Requestlist.class);
                tSearchPattern.addSearch_Criteria(new SearchCriteria("NAME", mName));
                tSearchCall = new SearchCall(MusicPreference.class, tSearchPattern, null);
                Requestlist tRequestlist = tSearchCall.produceFirst();
                if (tRequestlist != null) {
                    mId = tRequestlist.mId;
                    tWriteCall.update();
                }
            }
        } else {
            tWriteCall.update();
        }
        //Requestlist_Cache.updateCache(this);
        return this;
    }


    public ContentValues getContentValues() {
        ContentValues tContentValues = new ContentValues();
        tContentValues.put("NAME", mName);
        tContentValues.put("PURPOSE", mRequestlist_Purpose.getCode());
        tContentValues.put("COUNT_REQUEST", mRequestCount);
        if (mLastEditDate != null) {
            tContentValues.put("LAST_EDIT_DATE", mLastEditDate.toString());
        }
        if (mLastDateUsed != null) {
            tContentValues.put("LAST_DATE_USED", mLastDateUsed.toString());
        }
        generateJsonString();
        tContentValues.put("JSON_STRING", mJsonString);
        return tContentValues;
    }

    public static Requestlist convertCursor(Cursor tCursor) {
        Requestlist result = null;
        try {
            Requestlist_Purpose tRequestlist_Purpose = Requestlist_Purpose.fromCode(
                    tCursor.getString(tCursor.getColumnIndex("PURPOSE")));
            Date tDate_LastEdit;
            tDate_LastEdit = new Date(tCursor.getLong(tCursor.getColumnIndex("LAST_EDIT_DATE")));
            Date tDate_LastUsed;
            tDate_LastUsed = new Date(tCursor.getLong(tCursor.getColumnIndex("LAST_DATE_USED")));
            int tPI_Count = tCursor.getInt(tCursor.getColumnIndex("COUNT_REQUEST"));
            String tJsonString = tCursor.getString(tCursor.getColumnIndex("JSON_STRING"));

            result = new Requestlist(
                    tCursor.getInt(tCursor.getColumnIndex("ID")),
                    tCursor.getString(tCursor.getColumnIndex("NAME")),
                    tRequestlist_Purpose,
                    tPI_Count,
                    tDate_LastEdit,
                    tDate_LastUsed,
                    tJsonString
            );
        } catch(NullPointerException e) {
            Logg.e(TAG, e.toString());
            Logg.e(TAG, Log.getStackTraceString(new Exception()));
        }
        Requestlist_Cache.updateCache(result);
        return result;
    }


    // to keep everyhting synchronized all edits of AL must be followed by
//    generateJsonString();
//        this.save();
//        Requestlist_Cache.updateCache(this);

    public void delete() {
//        if (mAL_Request != null) {
//            for (int i = mAL_Request.size() - 1; i >= 0; i--) {
//                manageRemove(i);
//            }
//        }
        Requestlist_Cache.removeFromCacheId(mId);
        DeleteCall tDeleteCall;
        tDeleteCall = new DeleteCall(Requestlist.class, this);
        tDeleteCall.delete();
    }

    public static Requestlist getDefaultRequestlist() {
        if (mDefaultRequestlist == null) {
            defineDefaultRequestlist();
        }
        return mDefaultRequestlist;
    }

    public static void setDefaultRequestlist(Requestlist iRequestlist) {
        mDefaultRequestlist = iRequestlist;
    }

    public void add(MusicFile iMusicFile) {
        Request tRequest = new Request(iMusicFile, null, null);
        int tPosition;
        if (mAL_Request == null) {
            tPosition = 0;
        } else {
            tPosition = mAL_Request.size();
        }
        manageAdd(tPosition, tRequest);
        generateJsonString();
        this.save();
        Requestlist_Cache.updateCache(this);
    }

    public void add(Dance iDance) {
        MusicFile tMusicFile = iDance.getMusicFile();
        Request tRequest = new Request(tMusicFile, iDance, null);
        int tPosition;
        if (mAL_Request == null) {
            tPosition = 0;
        } else {
            tPosition = mAL_Request.size();
        }
        manageAdd(tPosition, tRequest);
        generateJsonString();
        this.save();
        Requestlist_Cache.updateCache(this);

        String tText;
        tText = String.format(Locale.ENGLISH,
                "%s added to %s", iDance.mName, mName);
        ReportSystem.receive(tText);
    }

    public void add(MusicFile iMusicFile, Dance iDance) {
        Request tRequest = new Request(iMusicFile, iDance, null);
        int tPosition;
        if (mAL_Request == null) {
            tPosition = 0;
        } else {
            tPosition = mAL_Request.size();
        }
        manageAdd(tPosition, tRequest);
        generateJsonString();
        this.save();
        Requestlist_Cache.updateCache(this);
        //
        String tText;
        if (iMusicFile != null) {
            tText = iMusicFile.mName;
        } else {
            tText = iDance.mName;
        }
        tText = String.format(Locale.ENGLISH,
                "%s added to %s", tText, mName);
        ReportSystem.receive(tText);
    }

    public void putAL(ArrayList<Request> iAL) {
        mAL_Request = iAL;
        mRequestCount = iAL.size();
        generateJsonString();
        this.save();
        Requestlist_Cache.updateCache(this);
    }


    public void remove(int iPosition) {
        manageRemove(iPosition);
        generateJsonString();
        this.save();
        Requestlist_Cache.updateCache(this);
    }

    public void swap(int iPosition_A, int iPosition_B) {
        manageSwap(iPosition_A, iPosition_B);
        generateJsonString();
        this.save();
        Requestlist_Cache.updateCache(this);
    }


}
