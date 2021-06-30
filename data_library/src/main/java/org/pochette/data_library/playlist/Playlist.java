package org.pochette.data_library.playlist;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import org.pochette.data_library.database_management.DeleteCall;
import org.pochette.data_library.database_management.SearchCall;
import org.pochette.data_library.database_management.WriteCall;
import org.pochette.data_library.music.MusicFile;
import org.pochette.data_library.scddb_objects.Dance;
import org.pochette.utils_lib.logg.Logg;
import org.pochette.utils_lib.search.SearchCriteria;
import org.pochette.utils_lib.search.SearchPattern;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
/**
 * The class playlist is a arraylist of playinstruction
 * <p>
 * The arraylist is only populated when needed, so insert resources
 * mCountDances is always correct,
 */
public class Playlist {

    //Variables
    private static final String TAG = "FEHA (Playlist)";
    private static Playlist mDefaultPlaylist = null;
    int mId;
    private String mName;
    private Playlist_Purpose mPlaylist_Purpose;
    private Date mLastEditDate;
    private Date mLastDateUsed;
    private boolean mHeaderOnly; // true if the AR is has not been used at all, or might be out of sync with the db

    private int mCount;
    private ArrayList<Playinstruction> mAR_Playinstruction;

    //<editor-fold desc="Constructor">
    /**
     * Constructor as used by the GUI, i.e. when the playlist is created first time
     *
     * @param iName of the playlist
     * @param iPlaylist_purpose its purpose
     */
    public Playlist(String iName, Playlist_Purpose iPlaylist_purpose) {
        SearchCriteria tSearchCriteria = new SearchCriteria("NAME", standarizeName(iName));
        SearchPattern tSearchPattern = new SearchPattern(Playlist.class);
        tSearchPattern.addSearch_Criteria(tSearchCriteria);
        SearchCall tSearchCall = new SearchCall(Playlist.class,
                tSearchPattern, null);

        if (tSearchCall.produceCount() > 0) {
            Logg.w(TAG, "playlist already exists");
        }
        mName = standarizeName(iName);
        mId = 0;
        mPlaylist_Purpose = iPlaylist_purpose;
        mAR_Playinstruction = new ArrayList<>(0);
        mCount = 0;
        mHeaderOnly = true;
    }

    /**
     * Constructor for convertCursor, hence private
     * @param tId the database ID
     * @param tName the name of the playlist
     * @param tPlaylist_Purpose its purpose
     * @param iLastEditDate its lastEditDate
     * @param iLastDateUsed its last used Date
     * @param iPI_Count playinstrucyion count
     */
    private Playlist(int tId, String tName, Playlist_Purpose tPlaylist_Purpose,
                    Date iLastEditDate, Date iLastDateUsed, int iPI_Count) {
        mName = standarizeName(tName);
        mAR_Playinstruction = null;
        if (tPlaylist_Purpose == null) {
            mPlaylist_Purpose = Playlist_Purpose.UNDEFINED;
        } else {
            mPlaylist_Purpose = tPlaylist_Purpose;
        }
        mId = tId;
        mHeaderOnly = true;
        mLastEditDate = iLastEditDate;
        mLastDateUsed = iLastDateUsed;
        mCount = iPI_Count;
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
    public int getCount() {
        return mCount;
    }
    public Playlist_Purpose getPlaylist_Purpose() {
        return mPlaylist_Purpose;
    }
    public void setPlaylist_Purpose(Playlist_Purpose tStatus) {
        mPlaylist_Purpose = tStatus;
    }
    public ArrayList<Playinstruction> getAR_Playinstruction() {
        prepareAR_Playinstruction();
        sortAR();
        return mAR_Playinstruction;
    }
    public Date getLastEditDate() {
        return mLastEditDate;
    }
    public Date getLastDateUsed() {
        return mLastDateUsed;
    }
    public boolean isDefault() {
        if (mDefaultPlaylist == null) {
            return false;
        } else {
            return mDefaultPlaylist.mId == mId;
        }
    }

//</editor-fold>

    //<editor-fold desc="adding stuff">
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

    public void add(MusicFile iMusicFile) {
        Playinstruction tPlayinstruction = new Playinstruction(iMusicFile);
        this.add(tPlayinstruction);
    }

    public void add(Dance iDance) {
        MusicFile tMusicFile = iDance.getMusicFile();
        if (tMusicFile == null) {
            Logg.i(TAG, "no musicfile for " + iDance.toShortString());
            return;
        }
        Playinstruction tPlayinstruction = new Playinstruction(tMusicFile);
        tPlayinstruction.setDance(iDance);
        this.add(tPlayinstruction);
    }

    public void fill(ArrayList<Dance> iAL) {
        if (iAL != null) {
            prepareAR_Playinstruction();
            mAR_Playinstruction.clear();
            for (Dance lDance : iAL) {
                this.add(lDance);
            }
            prepareAR_Playinstruction();
            this.save();
        }
    }


    /**
     * Add to default playlist
     *
     * @param iMusicFile File to be added to playlist
     */
    public static void addToDefault(MusicFile iMusicFile) {
        getDefaultPlaylist().add(iMusicFile);
        getDefaultPlaylist().save();
    }

    /**
     * This method adds the music of the dance to the playlist
     *
     * @param iDance the dance to be added
     */
    public static void addToDefault(Dance iDance) {
        getDefaultPlaylist().add(iDance);
        getDefaultPlaylist().save();
    }

    //</editor-fold>



    //Livecycle
    //Static Methods
    public static Playlist getById(int iId) {
        SearchCriteria tSearchCriteria = new SearchCriteria("ID", "" + iId);
        SearchPattern tSearchPattern = new SearchPattern(Playlist.class);
        tSearchPattern.addSearch_Criteria(tSearchCriteria);
        SearchCall tSearchCall = new SearchCall(Playlist.class,
                tSearchPattern, null);
        return tSearchCall.produceFirst();
    }
//
//    public static Playlist getByName(String iName) {
//        String tName = standarizeName(iName);
//        SearchCriteria tSearchCriteria = new SearchCriteria("NAME", "" + tName);
//        SearchPattern tSearchPattern = new SearchPattern(Playlist.class);
//        tSearchPattern.addSearch_Criteria(tSearchCriteria);
//        SearchCall tSearchCall = new SearchCall(Playlist.class,
//                tSearchPattern, null);
//        return tSearchCall.produceFirst();
//    }

    //Internal Organs
    private static String standarizeName(String tName) {
        return tName.replaceAll("[\\n\\t]", "");
    }

    private void prepareAR_Playinstruction() {
        if (mAR_Playinstruction == null) {
            mAR_Playinstruction = new ArrayList<>(0);
        }
        mCount = mAR_Playinstruction.size();
        mHeaderOnly = false;
    }

    void sortAR() {
        if (mAR_Playinstruction != null && mAR_Playinstruction.size() > 1) {
            Collections.sort(mAR_Playinstruction, new Comparator<Playinstruction>() {
                @Override
                public int compare(Playinstruction o1, Playinstruction o2) {
                    return o1.getPlaylist_Position() - o2.getPlaylist_Position();
                }
            });
        }




    }


    private void manageloadAL() {
        SearchCriteria tSearchCriteria = new SearchCriteria("PLAYLIST_ID", "" + mId);
        SearchPattern tSearchPattern = new SearchPattern(Playinstruction.class);
        tSearchPattern.addSearch_Criteria(tSearchCriteria);
        SearchCall tSearchCall = new SearchCall(Playinstruction.class,
                tSearchPattern, null);
        mAR_Playinstruction = tSearchCall.produceArrayList();
        sortAR();
    }


    /**
     * All additions of a instruction need to go through this function, so we can control the database save
     * the Arraylist in memory and the database changed are linked
     * @param iPosition from 0 till tAL.size()
     * @param iPlayinstruction to be added
     */
    private void manageAdd(int iPosition, Playinstruction iPlayinstruction) {
        if (mAR_Playinstruction == null || mHeaderOnly) {
            prepareAR_Playinstruction();
            mHeaderOnly = false;
        }
        if (iPosition < 0 || iPosition > mAR_Playinstruction.size()) {
            String tString = String.format(Locale.ENGLISH,
                    "Add at position %d not allowed for arraylist of size %d",
                    iPosition, mAR_Playinstruction.size()
            );
            throw new IllegalArgumentException(tString);
        }
        if (mId == 0) {
            this.save();
        }
        // to add at the end no shift has to be done
        if (iPosition == mAR_Playinstruction.size()) {
            iPlayinstruction.setPlaylist_Position(iPosition);
            iPlayinstruction.setPlaylist_Id(this.mId);
            mAR_Playinstruction.add(iPlayinstruction);
        } else {
            for (int i = mAR_Playinstruction.size() - 1; i >= iPosition; i--) {
                Playinstruction tPlayinstruction = mAR_Playinstruction.get(i);
                tPlayinstruction.setPlaylist_Id(this.mId);
                tPlayinstruction.setPlaylist_Position(i+1); // shift by one Position
                tPlayinstruction.save();
            }
            // after shifting, add the new one
            iPlayinstruction.setPlaylist_Position(iPosition);
            iPlayinstruction.setPlaylist_Id(this.mId);
            mAR_Playinstruction.add(iPosition, iPlayinstruction);
        }
        mCount = mAR_Playinstruction.size();
        mHeaderOnly = false;
        // save the new
        iPlayinstruction.save();
        mCount = mAR_Playinstruction.size();
        this.save();
        sortAR();
        this.report();
    }

    /**
     * All removals of a instruction need to go through this function, so we can control the database
     * the Arraylist in memory and the database changed are linked
     * @param iPosition from 0 till tAL.size()
     */
    private void manageRemove(int iPosition) {
        if (iPosition < 0 || iPosition >= mAR_Playinstruction.size()) {
            String tString = String.format(Locale.ENGLISH,
                    "Remove at position %d not allowed for arraylist of size %d",
                    iPosition, mAR_Playinstruction.size()
            );
            throw new IllegalArgumentException(tString);
        }
        if (mId == 0) {
            this.save();
        }
        mAR_Playinstruction.get(iPosition).delete();
        mAR_Playinstruction.remove(iPosition);
        // after delete, close any gap
        for (int i = iPosition; i < mAR_Playinstruction.size(); i++) {
            Playinstruction tPlayinstruction = mAR_Playinstruction.get(i);
            tPlayinstruction.setPlaylist_Id(this.mId);
            tPlayinstruction.setPlaylist_Position(i); // shift by one Position
            tPlayinstruction.save();
        }
        mCount = mAR_Playinstruction.size();
        this.save();
        sortAR();
        this.report();
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
        } else{
            tPosition_A = iPosition_B;
            tPosition_B = iPosition_A;
        }
        if (tPosition_A < 0 || tPosition_A >= mAR_Playinstruction.size()) {
            String tString = String.format(Locale.ENGLISH,
                    "Switch at position %d not allowed for arraylist of size %d",
                    tPosition_A, mAR_Playinstruction.size()
            );
            throw new IllegalArgumentException(tString);
        }
        if (tPosition_B >= mAR_Playinstruction.size()) {
            String tString = String.format(Locale.ENGLISH,
                    "Switch at position %d not allowed for arraylist of size %d",
                    tPosition_B, mAR_Playinstruction.size()
            );
            throw new IllegalArgumentException(tString);
        }
        if (mId == 0) {
            this.save();
        }
        // the database does not allow a duplicate key playlist_id, playlist_pos
        // so delete A is required
        Playinstruction tPlayinstruction_A = mAR_Playinstruction.get(tPosition_A);
        tPlayinstruction_A.delete();
        // move B to A
        Playinstruction tPlayinstruction_B = mAR_Playinstruction.get(tPosition_B);
        tPlayinstruction_B.setPlaylist_Position(tPosition_A);
        tPlayinstruction_B.save();
        // insert A at position of B
        tPlayinstruction_A.setPlaylist_Position(tPosition_B);
        tPlayinstruction_A.save();
        // after DB done, change in memory
        mAR_Playinstruction.remove(tPosition_A);
        mAR_Playinstruction.add(tPosition_A, tPlayinstruction_B);
        mAR_Playinstruction.remove(tPosition_B);
        mAR_Playinstruction.add(tPosition_B, tPlayinstruction_A);
        sortAR();
        this.report();
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
        return String.format(Locale.ENGLISH, "Playlist %s with %d items in DB and %d items in array", mName, mCount,c);
    }
//
//    public Playinstruction getItem(int i) {
//        prepareAR_Playinstruction();
//        return mAR_Playinstruction.get(i);
//    }

    public int size() {
        if (mHeaderOnly || mAR_Playinstruction == null) {
            return mCount;
        }
        return mAR_Playinstruction.size();
    }

    public void remove(int iPosition) {
        manageRemove(iPosition);
    }

    public void swap(int iPosition_A, int iPosition_B) {
        manageSwap(iPosition_A, iPosition_B);
    }

    public void loadAL() {
        manageloadAL();
    }

    public void add(Playinstruction iPlayinstruction) {
        int tPosition;
        if (mAR_Playinstruction == null) {
            tPosition = 0;
        } else {
            tPosition = mAR_Playinstruction.size();
        }
        manageAdd(tPosition, iPlayinstruction);
    }

    public void add(int iPosition, Playinstruction iPlayinstruction) {
        manageAdd(iPosition, iPlayinstruction);
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
        try {
            Playlist_Purpose tPlaylist_Purpose = Playlist_Purpose.fromCode(
                    tCursor.getString(tCursor.getColumnIndex("STATUS")));
            Date tDate_LastEdit;
            tDate_LastEdit = new Date(tCursor.getLong(tCursor.getColumnIndex("LAST_EDIT_DATE")));
            Date tDate_LastUsed;
            tDate_LastUsed = new Date(tCursor.getLong(tCursor.getColumnIndex("LAST_DATE_USED")));
            int tPI_Count = tCursor.getInt(tCursor.getColumnIndex("COUNT_PLAY_INSTRUCTION"));

            result = new Playlist(
                    tCursor.getInt(tCursor.getColumnIndex("ID")),
                    tCursor.getString(tCursor.getColumnIndex("NAME")),
                    tPlaylist_Purpose,
                    tDate_LastEdit,
                    tDate_LastUsed,
                    tPI_Count);
        } catch(NullPointerException e) {
            Logg.e(TAG, e.toString());
            Logg.e(TAG, Log.getStackTraceString(new Exception()));
        }
        return result;
    }


    public void delete() {
        if (mAR_Playinstruction != null) {
            for(int i = mAR_Playinstruction.size()-1; i>=0; i--){
                manageRemove(i);
            }
        }
        DeleteCall tDeleteCall;
        tDeleteCall = new DeleteCall(Playlist.class, this);
        tDeleteCall.delete();
    }



    public void report() {
        Logg.i(TAG, this.toString());
        if (mAR_Playinstruction != null) {
            for (Playinstruction lPlayinstruction : mAR_Playinstruction) {
                if (lPlayinstruction == null) {
                    Logg.i(TAG, " null ");
                } else {
                    Logg.i(TAG, lPlayinstruction.toString());
                }
            }
        }
    }

}
