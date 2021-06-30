package org.pochette.data_library.scddb_objects;


import android.database.Cursor;

import org.pochette.data_library.database_management.SearchCall;
import org.pochette.utils_lib.search.SearchCriteria;
import org.pochette.utils_lib.search.SearchPattern;

import java.util.ArrayList;
import java.util.Locale;

import androidx.annotation.NonNull;

/**
 * Album correspond to an album as available on scddb
 */
public class Album {

    //Variables
    @SuppressWarnings("unused")
    private static final String TAG = "FEHA (Album)";

    public int mId;
    public String mName;
    public String mShortname;
    public int mArtistId;
    public String mArtistName;
    public int mAlphaorder;
    public int mCountRecording;
    public int mCountLdbDirectory;
    public String mSignature;


    //Constructor
    public Album(int tID) throws Exception {
        SearchPattern tSearchPattern = new SearchPattern(Album.class);
        tSearchPattern.addSearch_Criteria(
                new SearchCriteria("ID",
                        String.format(Locale.ENGLISH, "%d", tID)));
        SearchCall tSearchCall =
                new SearchCall(Album.class, tSearchPattern, null);
        Album tAlbum = tSearchCall.produceFirst();

        mId = tAlbum.mId;
        mName = tAlbum.mName;
        mShortname = tAlbum.mShortname;
        mArtistId = tAlbum.mArtistId;
        mArtistName = tAlbum.mArtistName;
        mAlphaorder = tAlbum.mAlphaorder;
        mCountRecording = tAlbum.mCountRecording;
        mCountLdbDirectory = tAlbum.mCountLdbDirectory;
        if (tAlbum.mSignature== null) {
            mSignature = "";
        } else {
            mSignature = tAlbum.mSignature;
        }
        if (mId != tID) {
            throw new Exception("ALBUM WITH ID DOES NOT EXIST" + tID);
        }
    }

    public Album(int iId, String iName, String iShortname, int iArtistId, String iArtistName,
                 int iAlphaorder, int iCountRecording, int iCountLdbDirectory, String iSignature) {
        mId = iId;
        mName = iName;
        mShortname = iShortname;
        mArtistId = iArtistId;
        mArtistName = iArtistName;
        mAlphaorder = iAlphaorder;
        mCountRecording = iCountRecording;
        mCountLdbDirectory = iCountLdbDirectory;
        if (iSignature == null) {
            mSignature = "";
        } else {
            mSignature = iSignature;
        }
    }

    //Setter and Getter
	public String getSignature() {
		if (mSignature == null || mSignature.isEmpty()) {
            mSignature = "";
		}
		return mSignature;
	}

    //Livecycle
    //Static Methods
    //Internal Organs
    //Interface

    public ArrayList<Recording> getAL_Recording() {
        SearchPattern tSearchPattern = new SearchPattern(Recording.class);
        tSearchPattern.addSearch_Criteria(
                new SearchCriteria("ALBUM_ID",""+mId)
        );
        SearchCall tSearchCall = new SearchCall(Recording.class, tSearchPattern, null);
        return tSearchCall.produceArrayList();
    }

    @NonNull
    public String toString() {
        return String.format(Locale.ENGLISH, "%s [%s]", mName, mArtistName);
    }

    @Override
    public boolean equals(Object tObject) {
        if (this == tObject) return true;
        if (tObject == null) return false;
        if (getClass() != tObject.getClass()) return false;
        final Album tAlbum = (Album) tObject;
        return (mId == tAlbum.mId);
    }

    public static Album convertCursor(Cursor tCursor) {
        Album result;
        result = new Album(
                tCursor.getInt(tCursor.getColumnIndex("A_ID")),
                tCursor.getString(tCursor.getColumnIndex("A_NAME")),
                tCursor.getString(tCursor.getColumnIndex("A_SHORTNAME")),
                tCursor.getInt(tCursor.getColumnIndex("A_ARTIST_ID")),
                tCursor.getString(tCursor.getColumnIndex("A_ARTIST_NAME")),
                tCursor.getInt(tCursor.getColumnIndex("A_ALPHAORDER")),
                tCursor.getInt(tCursor.getColumnIndex("A_COUNT_RECORDINGS")),
                tCursor.getInt(tCursor.getColumnIndex("A_COUNT_LDB_DIRECTORIES")),
                tCursor.getString(tCursor.getColumnIndex("A_SIGNATURE"))
        );
        return result;
    }

}