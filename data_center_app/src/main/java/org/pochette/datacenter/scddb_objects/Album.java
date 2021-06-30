package org.pochette.data_library.scddb_objects;


import android.database.Cursor;

import org.pochette.data_library.search.SearchCall;
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
    @SuppressWarnings("FieldCanBeLocal")
    private static String TAG = "FEHA (Album)";
    @SuppressWarnings("unused")

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
        //Logg.i(TAG, "Start Recording(int tID): " + tID);

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
                 int iAlphaorder, int iCountRecording, int iCountLdbDirectory, String iSignature
    ) {
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

    public static Album getById(ArrayList<Album> tAR_ALBUM, int tId) {
        if (tAR_ALBUM == null) {
            return null;
        }
        for (Album t : tAR_ALBUM) {
            if (t.mId == tId) {
                return t;
            }
        }
        return null;
    }

    //Internal Organs
    //Interface



//    /**
//     * This method returns the HashMap of Signatures to ScddbAlbum_id
//     * If the same signature is related to two albums the signatures will not be part of the HashMap at all!
//     *
//     * @param tForce when true, the signatures is freshly calculated from the album and recording data
//     *               when false, the stored values in HashMapItem are used
//     * @return the HashMap
//     */
//
////    public static HashMap<String, Integer> getSignatures(boolean tForce) {
////        Logg.i(TAG, "Starting getSignatures");
////        HashMap<String, Integer> tHashMap= null;
////       // tHashMap = ScddbAlbum_DB.generateAllSignature();
////     //   Logg.i(TAG, "got Album Signatures done, found: " + tHashMap.size());
////        return tHashMap;
////    }

    public ArrayList<Recording> getAR_ScddbRecording() {
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
                tCursor.getInt(tCursor.getColumnIndex("ID")),
                tCursor.getString(tCursor.getColumnIndex("NAME")),
                tCursor.getString(tCursor.getColumnIndex("SHORTNAME")),
                tCursor.getInt(tCursor.getColumnIndex("ARTIST_ID")),
                tCursor.getString(tCursor.getColumnIndex("ARTIST_NAME")),
                tCursor.getInt(tCursor.getColumnIndex("ALPHAORDER")),
                tCursor.getInt(tCursor.getColumnIndex("COUNT_RECORDINGS")),
                tCursor.getInt(tCursor.getColumnIndex("COUNT_LDB_DIRECTORIES")),
                tCursor.getString(tCursor.getColumnIndex("SIGNATURE"))
        );

        return result;
    }


}