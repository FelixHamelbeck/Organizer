package org.pochette.data_library.music;


import android.content.ContentValues;
import android.database.Cursor;

import org.pochette.utils_lib.logg.Logg;
import org.pochette.data_library.search.SearchCall;
import org.pochette.data_library.search.WriteCall;
import org.pochette.utils_lib.search.SearchCriteria;
import org.pochette.utils_lib.search.SearchPattern;

import java.util.ArrayList;
import java.util.Locale;

import androidx.annotation.NonNull;

public class MusicDirectory {


    /**
     * This class stores the data of a directory containing MusicFiles
     */
    //Variables
    private static String TAG = "FEHA (MusicDirectory)";

    public int mId;
    public String mPath;
    public String mT1; // letzter Level, typischerweise Titel
    public String mT2; // seconde but last level, usually Artist
    public int mScddbAlbumId;
    public int mCountTracks;
    public String mSignature;
    public MusicDirectoryPurpose mMusicDirectoryPurpose;

    //Constructor

    public MusicDirectory(int tId, String tPath, String tT2, String tT1,
                          int tScddbAlbumId, int tCountTracks,
                          String tSignatures, MusicDirectoryPurpose tPurpose) {
        this.mId = tId;
        this.mPath = tPath;
        this.mT1 = tT1;
        this.mT2 = tT2;
        this.mScddbAlbumId = tScddbAlbumId;
        this.mCountTracks = tCountTracks;
        this.mSignature = tSignatures;
        this.mMusicDirectoryPurpose = tPurpose;
    }

    public MusicDirectory(int tId) throws Exception {

        SearchPattern tSearchPattern;
        tSearchPattern = new SearchPattern(MusicDirectory.class);
        SearchCriteria tSearchCriteria = new SearchCriteria("ID",
                String.format(Locale.ENGLISH, "%d", tId));
        tSearchPattern.addSearch_Criteria(tSearchCriteria);
        SearchCall tSearchCall = new SearchCall(MusicDirectory.class,
                tSearchPattern, null);
        ArrayList<MusicDirectory> tAR_MusicDirectory;
        tAR_MusicDirectory = tSearchCall.produceArrayList();
        if (tAR_MusicDirectory == null || tAR_MusicDirectory.size() == 0) {
            //Logg.d(TAG, "nothing found for path: " + tId);
            throw new RuntimeException("No MusicDirectory found for Id " + tId);

        }
        MusicDirectory tMusicDirectory = tAR_MusicDirectory.get(0);
        if (tMusicDirectory.mId != tId) {
            throw new Exception("DIRECTORY WITH ID DOES NOT EXIST" + tId);
        }

        mId = tId;
        mPath = tMusicDirectory.mPath;
        mT1 = tMusicDirectory.mT1;
        mT2 = tMusicDirectory.mT2;
        mScddbAlbumId = tMusicDirectory.mScddbAlbumId;
        mCountTracks = tMusicDirectory.mCountTracks;
        mSignature = tMusicDirectory.mSignature;
        mMusicDirectoryPurpose = tMusicDirectory.mMusicDirectoryPurpose;
    }

    public MusicDirectory(String tPath) throws Exception {


        if (tPath == null) {
            throw new RuntimeException("Not Path provided");
        }
//		Uri tUri = Uri.parse(tPath);
//		DocumentFile tDocumentFile =
//				DocumentFile.fromTreeUri(MyPreferences.getContext(), tUri);


//		if ((!Objects.requireNonNull(tDocumentFile).exists()) || (!tDocumentFile.isDirectory())) {
//			throw new Exception("This is not a directory path: " + tPath);
//		}
        SearchPattern tSearchPattern = new SearchPattern(MusicDirectory.class);
        SearchCriteria tSearchCriteria = new SearchCriteria("PATH", tPath);
        tSearchPattern.addSearch_Criteria(tSearchCriteria);
        SearchCall tSearchCall = new SearchCall(MusicDirectory.class, tSearchPattern, null);
        MusicDirectory tMusicDirectory = tSearchCall.produceFirst();
        if (tMusicDirectory == null || !tMusicDirectory.mPath.equals(tPath)) {
            Logg.i(TAG, "Directory not in DB, so check and store: \n" + tPath);
            mPath = tPath;
            fillFromPath();
            tMusicDirectory = this.save();
            if (tMusicDirectory == null) {
                throw new Exception("DIRECTORY WITH tPATH DOES NOT EXIST" + tPath);
            }
            mId = tMusicDirectory.mId;
            if (mId == 0 || !this.mPath.equals(tPath)) {
                //	Logg.i(TAG, tMusicDirectory.mPath);
                //	Logg.i(TAG, tPath);
                throw new Exception("DIRECTORY WITH tPATH DOES NOT EXIST" + tPath);
            }
        }
        mId = tMusicDirectory.mId;
        mPath = tMusicDirectory.mPath;
        mT1 = tMusicDirectory.mT1;
        mT2 = tMusicDirectory.mT2;
        mScddbAlbumId = tMusicDirectory.mScddbAlbumId;
        mCountTracks = tMusicDirectory.mCountTracks;
        mSignature = tMusicDirectory.mSignature;
        mMusicDirectoryPurpose = tMusicDirectory.mMusicDirectoryPurpose;
    }

    public static String getClassname_DB() {
        return "MusicDirectory_DB";
    }

    //Setter and Getter

    //Livecycle

    public MusicDirectory save() {
        WriteCall tWriteCall = new WriteCall(MusicDirectory.class, this);
        if (mId <= 0) {
            mId = tWriteCall.insert();
        } else {
            tWriteCall.update();
        }
        return this;
    }

    public ContentValues getContentValues() {
        ContentValues tContentValues = new ContentValues();
        tContentValues.put("PATH", mPath);
        tContentValues.put("T1", mT1);
        tContentValues.put("T2", mT2);
        tContentValues.put("ALBUM_ID", mScddbAlbumId);
        tContentValues.put("SIGNATURE", mSignature);
        tContentValues.put("MUSICDIRECTORY_PURPOSE", mMusicDirectoryPurpose.getCode());
        return tContentValues;
    }

    public static MusicDirectory convertCursor(Cursor tCursor) {
        MusicDirectoryPurpose tPurpose = MusicDirectoryPurpose.fromCode(
                tCursor.getString(tCursor.getColumnIndex("MUSICDIRECTORY_PURPOSE")));
        if (tPurpose == null) {
            tPurpose = MusicDirectoryPurpose.UNKNOWN;
        }
        return new MusicDirectory(
                tCursor.getInt(tCursor.getColumnIndex("_ID")),
                tCursor.getString(tCursor.getColumnIndex("PATH")),
                tCursor.getString(tCursor.getColumnIndex("T2")),
                tCursor.getString(tCursor.getColumnIndex("T1")),
                tCursor.getInt(tCursor.getColumnIndex("ALBUM_ID")),
                tCursor.getInt(tCursor.getColumnIndex("COUNT_TRACKS")),
                tCursor.getString(tCursor.getColumnIndex("SIGNATURE")),
                tPurpose);
    }


    public int getId() {
        return mId;
    }

    //Static Methods


    public static void removeLinks() {
        //	MusicDirectory_DB.removeLinks();
    }


    public static MusicDirectory getById(ArrayList<MusicDirectory> tAL, int tId) {
        for (MusicDirectory t : tAL) {
            if (t.mId == tId) {
                return t;
            }
        }
        return null;
    }

    public ArrayList<MusicFile> getAR_MusicFile() {
        SearchPattern tSearchPattern = new SearchPattern(MusicFile.class);
        tSearchPattern.addSearch_Criteria(
                new SearchCriteria("DIRECTORY_ID", "" + mId)
        );
        SearchCall tSearchCall = new SearchCall(MusicFile.class, tSearchPattern, null);
        return tSearchCall.produceArrayList();
    }


    //Internal Organs

    private void fillFromPath() {
        //Logg.i(TAG, String.format(Locale.ENGLISH,"fillFromPat(String tPath)", Thread.currentThread().getName()));
        if (mPath == null) {
            return;
        }
        String[] t = mPath.split("/");

        int l = t.length;
        if (l < 3) {
            mT2 = "N/A";
            mT1 = "N/A";
        } else {
            mT2 = t[l - 2];
            mT1 = t[l - 1];
        }

    }

    //Interface


    @NonNull
    public String toString() {
        return mT2 + "/" + mT1;
    }

    @Override
    public boolean equals(Object tObject) {
        if (this == tObject) return true;
        if (tObject == null) return false;
        if (getClass() != tObject.getClass()) return false;
        final MusicDirectory tMusicDirectory = (MusicDirectory) tObject;

        return this.mId == tMusicDirectory.mId && this.mScddbAlbumId == tMusicDirectory.mScddbAlbumId;
    }

    /**
     * This methord transforms the Path into the class object
     * If it is not yet stored in the database, it will be stored
     *
     * @param tDirPath Path for which the object is created
     * @return MusicDirectory the object created
     */
    public static MusicDirectory getOrCreateByPath(String tDirPath) {
        MusicDirectory tMusicDirectory;
        SearchPattern tSearchPattern = new SearchPattern(MusicDirectory.class);
        SearchCriteria tSearchCriteria = new SearchCriteria("PATH", tDirPath);
        tSearchPattern.addSearch_Criteria(tSearchCriteria);
        SearchCall tSearchCall = new SearchCall(MusicDirectory.class, tSearchPattern, null);
        ArrayList<MusicDirectory> tAR_MusicDirectory;
        tAR_MusicDirectory = tSearchCall.produceArrayList();
        if (tAR_MusicDirectory == null || tAR_MusicDirectory.size() == 0) {

            Logg.d(TAG, "nothing found for path: " + tDirPath);
            try {
                // If not available store it
                tMusicDirectory = new MusicDirectory(tDirPath);
                return tMusicDirectory;
            } catch(Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        tMusicDirectory = tAR_MusicDirectory.get(0);
        return tMusicDirectory;
    }

    /**
     * return MusicDirectory from Album_id
     *
     * @param tId Album_id
     * @return MusicDirectory, or null if not available
     */

    public static ArrayList<MusicDirectory> getByAlbumId(int tId) {
        SearchPattern tSearchPattern = new SearchPattern(MusicDirectory.class);
        SearchCriteria tSearchCriteria = new SearchCriteria("ALBUM_ID", String.format(Locale.ENGLISH, "%d", tId));
        tSearchPattern.addSearch_Criteria(tSearchCriteria);
        SearchCall tSearchCall = new SearchCall(MusicDirectory.class, tSearchPattern, null);
        ArrayList<MusicDirectory> tAR_MusicDirectory;
        tAR_MusicDirectory = tSearchCall.produceArrayList();
        if (tAR_MusicDirectory == null || tAR_MusicDirectory.size() == 0) {
            Logg.d(TAG, "nothing found for path: " + tId);
            return null;
        }
        return tAR_MusicDirectory;
    }


}