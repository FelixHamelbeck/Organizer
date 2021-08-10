package org.pochette.data_library.music;


import android.content.ContentValues;
import android.database.Cursor;

import org.pochette.data_library.database_management.DeleteCall;
import org.pochette.data_library.database_management.SearchCall;
import org.pochette.data_library.database_management.WriteCall;
import org.pochette.data_library.requestlist.Requestlist;
import org.pochette.utils_lib.search.SearchCriteria;
import org.pochette.utils_lib.search.SearchPattern;

import java.util.ArrayList;

import androidx.annotation.NonNull;

/**
 * This class stores the data of a directory containing MusicFiles
 */
public class MusicDirectory {

    //Variables
    @SuppressWarnings("unused")
    private static final String TAG = "FEHA (MusicDirectory)";

    public int mId;
    public String mPath;
    public String mT1; // last level, typischerweise Titel
    public String mT2; // second but last level, usually Artist
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


    @SuppressWarnings("unused")
    public MusicDirectory(String tPath) throws Exception {
        if (tPath == null) {
            throw new RuntimeException("Not Path provided");
        }
        SearchPattern tSearchPattern = new SearchPattern(MusicDirectory.class);
        SearchCriteria tSearchCriteria = new SearchCriteria("PATH", tPath);
        tSearchPattern.addSearch_Criteria(tSearchCriteria);
        SearchCall tSearchCall = new SearchCall(MusicDirectory.class, tSearchPattern, null);
        MusicDirectory tMusicDirectory = tSearchCall.produceFirst();
        if (tMusicDirectory == null || !tMusicDirectory.mPath.equals(tPath)) {
            mPath = tPath;
            fillFromPath();
            tMusicDirectory = this.save();
            if (tMusicDirectory == null) {
                throw new Exception("DIRECTORY WITH tPATH DOES NOT EXIST" + tPath);
            }
            mId = tMusicDirectory.mId;
            if (mId == 0 || !this.mPath.equals(tPath)) {
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

    public void delete() {
        DeleteCall tDeleteCall;
        for (MusicFile lMusicFile : getAL_MusicFile()) {
            tDeleteCall = new DeleteCall(MusicFile.class, lMusicFile);
            tDeleteCall.delete();
        }
        tDeleteCall = new DeleteCall(MusicDirectory.class, this);
        tDeleteCall.delete();
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
                tCursor.getString(tCursor.getColumnIndex("MD_MUSICDIRECTORY_PURPOSE")));
        if (tPurpose == null) {
            tPurpose = MusicDirectoryPurpose.UNKNOWN;
        }
        return new MusicDirectory(
                tCursor.getInt(tCursor.getColumnIndex("MD_ID")),
                tCursor.getString(tCursor.getColumnIndex("MD_PATH")),
                tCursor.getString(tCursor.getColumnIndex("MD_T2")),
                tCursor.getString(tCursor.getColumnIndex("MD_T1")),
                tCursor.getInt(tCursor.getColumnIndex("MD_ALBUM_ID")),
                tCursor.getInt(tCursor.getColumnIndex("MD_COUNT_TRACKS")),
                tCursor.getString(tCursor.getColumnIndex("MD_SIGNATURE")),
                tPurpose);
    }


    public int getId() {
        return mId;
    }

    //Static Methods
    //Internal Organs

    private void fillFromPath() {
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
    public ArrayList<MusicFile> getAL_MusicFile() {
        SearchPattern tSearchPattern = new SearchPattern(MusicFile.class);
        tSearchPattern.addSearch_Criteria(
                new SearchCriteria("DIRECTORY_ID", "" + mId)
        );
        SearchCall tSearchCall = new SearchCall(MusicFile.class, tSearchPattern, null);
        return tSearchCall.produceArrayList();
    }


    public static MusicDirectory getById(int iMusicDirectory_id) {
        MusicDirectory tMusicDirectory;
        SearchPattern tSearchPattern = new SearchPattern(MusicDirectory.class);
        SearchCriteria tSearchCriteria = new SearchCriteria("ID", ""+iMusicDirectory_id);
        tSearchPattern.addSearch_Criteria(tSearchCriteria);
        SearchCall tSearchCall = new SearchCall(MusicDirectory.class, tSearchPattern, null);
        tMusicDirectory = tSearchCall.produceFirst();
        return tMusicDirectory;

    }

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
}