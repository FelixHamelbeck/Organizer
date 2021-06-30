package org.pochette.data_library.playlist;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import org.pochette.utils_lib.logg.Logg;
import org.pochette.utils_lib.search.SearchCriteria;
import org.pochette.utils_lib.search.SearchPattern;
import org.pochette.utils_lib.shouting.Shout;
import org.pochette.utils_lib.shouting.Shouting;
import org.pochette.data_library.music.MusicFile;
import org.pochette.data_library.search.SearchCall;
import org.pochette.data_library.search.WriteCall;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


@SuppressWarnings({"unused", "UnusedReturnValue"})
public class Playlist implements Shouting {

    /**
     * The class playlist is a arraylist of playinstruction
     * <p>
     * The arraylist is only populated when needed, so insert resources
     * mCountDances is always correct,
     */


    //Variables
    private static final String TAG = "FEHA (Playlist)";
    private static Playlist mDefaultPlaylist = null;
    int mId;
    String mName;
    Playlist_Purpose mPlaylist_Purpose;
    Date mLastEditDate;
    Date mLastDateUsed;
    //boolean mChangedSinceLastWrite = true; // if a difference between the local copy (this) and the database may exist
    Shout mGlassFloor;
    private boolean mHeaderOnly; // true if the AR is has not been used at all, or might be out of sync with the db


    //three possible setups
    // HeaderOnly = true; mAR = null, mChangedSinceLastWrite meaningless
    // HeaderOnly = false; mAR <> null, mChangeSiceLastWrite = false => this and database in sync
    // HeaderOnly = false; mAR <> null, mChangeSiceLastWrite = true => this and database not in sync
    private int mCount;
    private ArrayList<Playinstruction> mAR_Playinstruction;

    //Constructor
    public Playlist(String tName) {
        mName = standarizeName(tName);
        mId = 0;
        mPlaylist_Purpose = Playlist_Purpose.UNDEFINED;
        mAR_Playinstruction = new ArrayList<>(0);
        mCount = 0;
        mHeaderOnly = true;
        SearchCriteria tSearchCriteria = new SearchCriteria("NAME", tName);
        SearchPattern tSearchPattern = new SearchPattern(Playlist.class);
        tSearchPattern.addSearch_Criteria(tSearchCriteria);
        SearchCall tSearchCall = new SearchCall(Playlist.class,
                tSearchPattern, null);
        ArrayList<Playlist> tAR = tSearchCall.produceArrayList();
        if (tAR == null || tAR.size() == 0) {
            mId = 0;
            //mChangedSinceLastWrite = false;
        } else if (tAR.size() > 1) {
            Logg.e(TAG, "More than one List by the Name: " + tName);
        } else {
            Playlist tPlaylist = tAR.get(0);
            mId = tPlaylist.mId;
            mAR_Playinstruction = tPlaylist.getAR_Playinstruction();
            mPlaylist_Purpose = tPlaylist.mPlaylist_Purpose;
            mCount = tPlaylist.mCount;
            //mChangedSinceLastWrite = true;
            mLastEditDate = tPlaylist.mLastEditDate;
            mLastDateUsed = tPlaylist.mLastDateUsed;
            Logg.d(TAG, "count from DB: " + this.getCount());
        }
    }

    public Playlist(int tId) {
        SearchCriteria tSearchCriteria = new SearchCriteria("ID", "" + tId);
        SearchPattern tSearchPattern = new SearchPattern(Playlist.class);
        tSearchPattern.addSearch_Criteria(tSearchCriteria);
        SearchCall tSearchCall = new SearchCall(Playlist.class,
                tSearchPattern, null);
        Playlist tPlaylist = tSearchCall.produceFirst();
        if (tPlaylist != null) {
            mName = tPlaylist.mName;
            mPlaylist_Purpose = tPlaylist.mPlaylist_Purpose;
            mId = tPlaylist.mId;
            mCount = tPlaylist.mCount;
            mLastEditDate = tPlaylist.mLastEditDate;
            mLastDateUsed = tPlaylist.mLastDateUsed;
            //mChangedSinceLastWrite = true;
            mHeaderOnly = true;
        }
    }

    public Playlist(int tId, String tName, Playlist_Purpose tPlaylist_Purpose) {
        mName = standarizeName(tName);
        mAR_Playinstruction = null;
        if (tPlaylist_Purpose == null) {
            mPlaylist_Purpose = Playlist_Purpose.UNDEFINED;
        } else {
            mPlaylist_Purpose = tPlaylist_Purpose;
        }
        mId = tId;
        mHeaderOnly = true;
        //mChangedSinceLastWrite = true;
    }


    private static void defineDefaultPlaylist() {
        if (mDefaultPlaylist == null) {
            SearchPattern tSearchPattern = new SearchPattern(Playlist.class);
            tSearchPattern.setSortOrder("MOST_RECENT");
            SearchCall tSearchCall = new SearchCall(Playlist.class,
                    tSearchPattern, null);
            mDefaultPlaylist = tSearchCall.produceFirst();
        }
    }

    public static Playlist getDefaultPlaylist() {
        if (mDefaultPlaylist == null) {
            defineDefaultPlaylist();
        }
        return mDefaultPlaylist;
    }

    public static void setDefaultPlaylist(Playlist iPlaylist) {
        mDefaultPlaylist = iPlaylist;
    }

    /**
     * This method adds the music of the dance to the playlist
     *
     * @param tScddbDance the dance to be added
     */
//    public static void addToDefault(Dance tScddbDance) {
//        //Logg.i(TAG, "addToDefault: " + tScddbDance.toString());
//        if (tScddbDance == null) {
//            Logg.e(TAG, "addToDefault: No Dance provided");
//            return;
//        }
//        Logg.i(TAG, tScddbDance.toString());
//
//        // find the MusicFile
//        MusicFile tMusicFile = null;
//        try {
//            ArrayList<MusicFile> tAR_MusicFile = MusicFile.getARByDance(tScddbDance);
//            if (tAR_MusicFile == null || tAR_MusicFile.size() == 0) {
//                return;
//            }
//            tMusicFile = tAR_MusicFile.get(0);
//            Logg.i(TAG, tMusicFile.toString());
//        } catch(FileNotFoundException e) {
//            Log.e(TAG, e.toString());
//        }
//
//        Playinstruction tPlayinstruction = new Playinstruction(tMusicFile);
//        tPlayinstruction.setScddbDance(tScddbDance);
//        Playlist tPlaylist = Playlist.getDefaultPlaylist();
//        if (tPlaylist != null) {
//            if (tPlaylist.mAR_Playinstruction == null) {
//                tPlaylist.prepareAR_Playinstruction();
//            }
//            tPlaylist.add(tPlayinstruction);
//        }
//    }

    /**
     * Add to default playlist
     *
     * @param iMusicFile File to be added to playlist
     */
    public static void addToDefault(MusicFile iMusicFile) {
        Playinstruction tPlayinstruction = new Playinstruction(iMusicFile);
        Playlist tPlaylist = Playlist.getDefaultPlaylist();
        if (tPlaylist != null) {
            if (tPlaylist.mAR_Playinstruction == null) {
                tPlaylist.prepareAR_Playinstruction();
            }
            tPlaylist.add(tPlayinstruction);
        }
    }


    public static Playlist getById(int iId) {
        SearchCriteria tSearchCriteria = new SearchCriteria("ID", "" + iId);
        SearchPattern tSearchPattern = new SearchPattern(Playlist.class);
        tSearchPattern.addSearch_Criteria(tSearchCriteria);
        SearchCall tSearchCall = new SearchCall(Playlist.class,
                tSearchPattern, null);
        return tSearchCall.produceFirst();
    }

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

    public int getCount() {
        return mCount;
    }

    // only allowed usage is for convert_cursor
    void setCount(int iCount) {
        mCount = iCount;
        mHeaderOnly = true;
    }

    public Playlist_Purpose getPlaylist_Purpose() {
        return mPlaylist_Purpose;
    }

    public void setPlaylist_Purpose(Playlist_Purpose tStatus) {
        mPlaylist_Purpose = tStatus;
    }

    public ArrayList<Playinstruction> getAR_Playinstruction() {

        prepareAR_Playinstruction();
        return mAR_Playinstruction;
    }

    public void setAR_Playinstruction(ArrayList<Playinstruction> tAR_Playinstruction) {
        if (tAR_Playinstruction != null) {
            mAR_Playinstruction = tAR_Playinstruction;
            mCount = tAR_Playinstruction.size();
            mHeaderOnly = false;
        }
    }

    public Date getLastEditDate() {
        return mLastEditDate;
    }

    public void setLastEditDate(Date iLastEditDate) {
        mLastEditDate = iLastEditDate;
        //    Playlist_DB.saveTimestamps(this);
    }

//</editor-fold>

    //Livecycle

    //Static Methods

    public Date getLastDateUsed() {
        return mLastDateUsed;
    }

    public void setLastDateUsed(Date iLastDateUsed) {
        mLastDateUsed = iLastDateUsed;
        // Playlist_DB.saveTimestamps(this);
    }

    public boolean isDefault() {
        if (mDefaultPlaylist == null) {
            return false;
        } else {
            return mDefaultPlaylist.mId == mId;
        }
    }

    public static void setDefault(Fragment iFragment) {
        Logg.i(TAG, "setDefault");
//		FragmentManager fm = iFragment.getFragmentManager();
//		DialogFragment_Playlist t_dialogFragemen_Playlist =
//				DialogFragment_Playlist.newInstance(null);
//		// this playlist only use is to be called by shouting
//		Playlist tPlaylist_Dummy = new Playlist(0, "", Playlist_Purpose.TMP);
//		t_dialogFragemen_Playlist.setShouting(tPlaylist_Dummy );
//		assert fm != null;
//		t_dialogFragemen_Playlist.show(fm, "fragment_edit_name");
    }

//    public void addTo(Dance tScddbDance) {
//        //Logg.i(TAG, "addToDefault: " + tScddbDance.toString());
//        if (tScddbDance == null) {
//            Logg.e(TAG, "addToDefault: No Dance provided");
//            return;
//        }
//        Logg.i(TAG, tScddbDance.toString());
//
//        // find the MusicFile
//        MusicFile tMusicFile = null;
//        try {
//            ArrayList<MusicFile> tAR_MusicFile = MusicFile.getARByDance(tScddbDance);
//            if (tAR_MusicFile == null || tAR_MusicFile.size() == 0) {
//                return;
//            }
//            tMusicFile = tAR_MusicFile.get(0);
//            Logg.i(TAG, tMusicFile.toString());
//        } catch(FileNotFoundException e) {
//            Log.e(TAG, e.toString());
//        }
//        Playinstruction tPlayinstruction = new Playinstruction(tMusicFile);
//        tPlayinstruction.setScddbDance(tScddbDance);
//        this.add(tPlayinstruction);
//    }

    public void addTo(MusicFile iMusicFile) {
        //Logg.i(TAG, "addToDefault: " + tScddbDance.toString());
        Playinstruction tPlayinstruction = new Playinstruction(iMusicFile);
        this.add(tPlayinstruction);
    }

    //Internal Organs
    private String standarizeName(String tName) {
        return tName.replaceAll("[\\n\\t]", "");
    }

    void saveAllItems() {
        if (mAR_Playinstruction == null) {
            return;
        }
        Playinstruction tPlayinstruction;
        for (int i = 0; i < mAR_Playinstruction.size(); i++) {
            tPlayinstruction = mAR_Playinstruction.get(i);
            tPlayinstruction.mPos = i;
            tPlayinstruction.save();

        }
    }
//
//	private void markChange() {
//		// when mAR has been changed call this function
//		mCount = mAR_Playinstruction.size();
//		//mChangedSinceLastWrite = true;
//	}

    public void prepareAR_Playinstruction() {
        //	if (mChangedSinceLastWrite || mHeaderOnly || mAR_Playinstruction == null) {
//        if (mHeaderOnly || mAR_Playinstruction == null) {
//            mAR_Playinstruction = Playlist_DB.getAR(mId);
//        }
        if (mAR_Playinstruction == null) {
            mAR_Playinstruction = new ArrayList<>(0);
        }
        //mChangedSinceLastWrite = false;
        mCount = mAR_Playinstruction.size();
        mHeaderOnly = false;
    }

    //Interface
    @NonNull
    public String toString() {
        int c;
        if (mAR_Playinstruction == null) {
            c = 0;
            //return String.format(Locale.ENGLISH,"Playlist %s [%s] with no items", mName, mId);
        } else {
            c = mAR_Playinstruction.size();
        }
        return String.format(Locale.ENGLISH, "Playlist %s with %d items", mName, c);
    }

    private void process_shouting() {
        if (this.mName.isEmpty() && this.mId == 0) {
            if (mGlassFloor.mActor.equals("DialogFragment_Playlist") &&
                    mGlassFloor.mLastObject.equals("Playlist") &&
                    mGlassFloor.mLastAction.equals("selected")) {
                Logg.i(TAG, "selected");
//				try {
//					JSONObject tJsonObject = new JSONObject(mGlassFloor.mJsonString);
//					int tPlaylist_Id = tJsonObject.getInt("Id");
//					mCurrentPlaylist = new Playlist(tPlaylist_Id);
//					String rText = "CurrentPlaylist=" + mCurrentPlaylist.toString();
//					ReportSystem.receive(rText);
//
//				} catch (JSONException e) {
//					e.printStackTrace();
//				}
            }
        }
    }

    public Playinstruction getItem(int i) {
        prepareAR_Playinstruction();
        return mAR_Playinstruction.get(i);
    }

    public int size() {

        //if (mHeaderOnly || mChangedSinceLastWrite || mAR_Playinstruction == null) {
        if (mHeaderOnly || mAR_Playinstruction == null) {
            return mCount;
        }
        return mAR_Playinstruction.size();
    }
//
//	private void initalizeAR() {
//		if (mAR_Playinstruction == null) {
//			mAR_Playinstruction = new ArrayList<>(0);
//		}
//	}

    public void add(Playinstruction tPlayinstruction) {
        Logg.i(TAG, "add");
        Logg.i(TAG, tPlayinstruction.toString());
        Logg.i(TAG, this.toString());
        if (mAR_Playinstruction == null || mHeaderOnly) {

            prepareAR_Playinstruction();
            //mAR_Playinstruction = new ArrayList<>(0);
            mHeaderOnly = false;
        }
        mAR_Playinstruction.add(tPlayinstruction);

        int tPos = mAR_Playinstruction.size() - 1;
        //    Playinstruction_DB.save(tPlayinstruction, this, tPos);
        //Logg.d(TAG, "count Instruction in Playlist: " + mAR_Playinstruction.size());
        mCount = mAR_Playinstruction.size();
        mHeaderOnly = false;

//        String rText = String.format(Locale.ENGLISH,
//                "Add %s to %s [%d]", tPlayinstruction.getMajorText(), mName, mCount);

    }

    public void removeItem(MusicFile tmf) {
        Logg.i(TAG, "removeDbFile: " + tmf.toString());
        if (mAR_Playinstruction == null || mHeaderOnly) {
            prepareAR_Playinstruction();
            mHeaderOnly = false;
        }
        for (int i = 0; i < mAR_Playinstruction.size(); i++) {
            if (mAR_Playinstruction.get(i).mMusicFile_Id == tmf.mId) {
                mAR_Playinstruction.remove(i);
                return;
            }
        }
        mCount = mAR_Playinstruction.size();
        mHeaderOnly = false;
        this.save();
    }

    public void removePosition(int tPos) {
        if (mAR_Playinstruction == null || mHeaderOnly) {
            prepareAR_Playinstruction();
            mHeaderOnly = false;
        }
        mAR_Playinstruction.remove(tPos);
        mCount = mAR_Playinstruction.size();
        mHeaderOnly = false;
        this.save();
    }

    public void purgeAR() {
        mAR_Playinstruction = new ArrayList<>(0);
        mCount = mAR_Playinstruction.size();
        mHeaderOnly = false;
        this.save();
    }

    public Playlist save() {
        WriteCall tWriteCall = new WriteCall(Playlist.class, this);
        if (mId <= 0) {
            mId = tWriteCall.insert();
        } else {
            tWriteCall.update();
        }
        return this;
    }

    public ContentValues getContentValues() {
        ContentValues tContentValues = new ContentValues();
        tContentValues.put("NAME", getName());
        tContentValues.put("STATUS", getPlaylist_Purpose().getCode());
        if (getLastEditDate() != null) {
            tContentValues.put("LAST_EDIT_DATE", getLastEditDate().toString());
        }
        if (getLastDateUsed() != null) {
            tContentValues.put("LAST_DATE_USED", getLastDateUsed().toString());
        }
        return tContentValues;
    }

    public static Playlist convertCursor(Cursor tCursor) {
        Playlist result = null;
        Playlist_Purpose tPlaylist_Purpose = Playlist_Purpose.fromCode(
                tCursor.getString(tCursor.getColumnIndex("STATUS")));
        try {
            result = new Playlist(
                    tCursor.getInt(tCursor.getColumnIndex("ID")),
                    tCursor.getString(tCursor.getColumnIndex("NAME")),
                    tPlaylist_Purpose);
            Date tDate;
            tDate = new Date(tCursor.getLong(tCursor.getColumnIndex("LAST_EDIT_DATE")));
            result.setLastEditDate(tDate);
            tDate = new Date(tCursor.getLong(tCursor.getColumnIndex("LAST_DATE_USED")));
            result.setLastDateUsed(tDate);
            int c = tCursor.getInt(tCursor.getColumnIndex("COUNT_PLAY_INSTRUCTION"));
            result.setCount(c);
        } catch(NullPointerException e) {
            Logg.e(TAG, e.toString());
            Logg.e(TAG, Log.getStackTraceString(new Exception()));
        }
        return result;
    }





    public void delete() {
//        Logg.d(TAG, "delete");
//        if (mAR_Playinstruction != null) {
//            for (Playinstruction tPlayinstruction : mAR_Playinstruction) {
//                Playinstruction_DB.delete(tPlayinstruction);
//            }
//        }
//        Playlist_DB.delete(this);
    }

//    public void storeHsDances(HashSet<Integer> iHS_Id) {
//        if (iHS_Id == null) {
//            return;
//        }
//
//        mAR_Playinstruction = new ArrayList<>(0);
//        for (Integer lId : iHS_Id) {
//            Dance tDance = Dance.getById(lId);
//            if (tDance != null) {
//                this.addTo(tDance);
//            }
//        }
//        save();
//    }


    @Override
    public void shoutUp(Shout tShoutToCeiling) {
        Logg.i(TAG, "run shoutUP");
        Logg.i(TAG, tShoutToCeiling.toString());
        mGlassFloor = tShoutToCeiling;
        process_shouting();
    }

}
