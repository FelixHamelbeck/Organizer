package org.pochette.data_library.scddb_objects;


import android.database.Cursor;

import org.pochette.data_library.database_management.SearchCall;
import org.pochette.data_library.music.MusicFile;
import org.pochette.utils_lib.logg.Logg;
import org.pochette.utils_lib.search.SearchCriteria;
import org.pochette.utils_lib.search.SearchPattern;
import java.util.Locale;

import androidx.annotation.NonNull;

/**
 * Recording correspond to an recording as available on scddb
 */
public class Recording {

    private static final String TAG = "FEHA (Recording)";
    //Variables
    public int mId;
    public String mName;
    public int mAlbumId;
    public String mAlbumName;
    public String mAlbumShortname;
    public String mAlbumArtistName;
    public int mTrackNumber;
    public String mType;
    public int mRepetitions;
    public int mBarsperrepeat;
    public int mPlayingseconds;
    public String mSignature;
    public String mMedleytype;
    public boolean mTwochords;
    public int mCountMusicfile;

    //Constructor
    public Recording(int tID) throws Exception {
        Logg.i(TAG, "Start Recording(int tID): " + tID);
        SearchPattern tSearchPattern = new SearchPattern(Recording.class);
        tSearchPattern.addSearch_Criteria(
                new SearchCriteria("ID",
                        String.format(Locale.ENGLISH, "%d", tID)));
        SearchCall tSearchCall =
                new SearchCall(Recording.class, tSearchPattern, null);
        Recording tRecording = tSearchCall.produceFirst();
        mId = tRecording.mId;
        mName = tRecording.mName;
        mAlbumName = tRecording.mAlbumName;
        mAlbumShortname = tRecording.mAlbumShortname;
        mAlbumArtistName = tRecording.mAlbumArtistName;
        mType = tRecording.mType;
        mRepetitions = tRecording.mRepetitions;
        mBarsperrepeat = tRecording.mBarsperrepeat;
        mPlayingseconds = tRecording.mPlayingseconds;
        mMedleytype = tRecording.mMedleytype;
        mCountMusicfile = tRecording.mCountMusicfile;
        mTrackNumber = tRecording.mTrackNumber;
        mTwochords = tRecording.mTwochords;
        mSignature = tRecording.mSignature;
        mAlbumId = tRecording.mAlbumId;
        if (mId != tID) {
            throw new Exception("ALBUM WITH ID DOES NOT EXIST" + tID);
        }
    }

    public Recording(int tId, String tName, int tAlbumUId, String tAlbumName, String tAlbumShortname,
                     String tAlbumArtistName, int tTrackNumber, String tType, int tRepetitions,
                     int tBarsperrepeat, int tPlayingseconds, String tSignature,
                     String tMedleytype, boolean tTwochords, int tCountMusicfile) {
        mId = tId;
        mName = tName;
        mAlbumId = tAlbumUId;
        mAlbumName = tAlbumName;
        mAlbumShortname = tAlbumShortname;
        mAlbumArtistName = tAlbumArtistName;
        mTrackNumber = tTrackNumber;
        mType = tType;
        mRepetitions = tRepetitions;
        mBarsperrepeat = tBarsperrepeat;
        mPlayingseconds = tPlayingseconds;
        mSignature = tSignature;
        mMedleytype = tMedleytype;
        mTwochords = tTwochords;
        mCountMusicfile = tCountMusicfile;
    }

    //Setter and Getter
    //Static Methods
//
//    public static ArrayList<Recording> RECORDINGGetByAlbum(int tScddbAlbum_id) {
//        SearchPattern tSearchPattern = new SearchPattern(Recording.class);
//        tSearchPattern.addSearch_Criteria(
//                new SearchCriteria("ALBUM_ID",
//                        String.format(Locale.ENGLISH, "%d", tScddbAlbum_id)));
//        SearchCall tSearchCall =
//                new SearchCall(Recording.class, tSearchPattern, null);
//        return tSearchCall.produceArrayList();
//    }

//
//    public static Recording getById(int tId) {
//        SearchPattern tSearchPattern = new SearchPattern(Recording.class);
//        SearchCriteria tSearchCriteria = new SearchCriteria("ID", String.format(Locale.ENGLISH, "%d", tId));
//        tSearchPattern.addSearch_Criteria(tSearchCriteria);
//        SearchCall tSearchCall = new SearchCall(Recording.class, tSearchPattern, null);
//        return tSearchCall.produceFirst();
//    }

    public static Recording getByMusicFile(MusicFile tMusicFile) {
        if (tMusicFile == null || tMusicFile.mRecordingId == 0) {
            return null;
        }
        try {
            return new Recording(tMusicFile.mRecordingId);
        } catch(Exception e) {
            Logg.e(TAG, e.toString());
            return null;
        }
    }


    //Internal Organs
    //Interface
    public static Recording convertCursor(Cursor tCursor) {
        boolean tTwochords = false;
        if (tCursor.getInt(tCursor.getColumnIndex("R_TWOCHORDS")) == 1) {
            tTwochords = true;
        }
        String tType = tCursor.getString(tCursor.getColumnIndex("R_TYPENAME"));
        int tRepetitions = tCursor.getInt(tCursor.getColumnIndex("R_REPETITIONS"));
        int tBarsperrepeat = tCursor.getInt(tCursor.getColumnIndex("R_BARSPERREPEAT"));
        String tSignature = String.format(Locale.ENGLISH, "%s%dx%d",
                (tType == null ? "SearchRetrieval" : tType.substring(0, 1)),
                tRepetitions, tBarsperrepeat);
        return new Recording(
                tCursor.getInt(tCursor.getColumnIndex("R_ID")),
                tCursor.getString(tCursor.getColumnIndex("R_NAME")),
                tCursor.getInt(tCursor.getColumnIndex("R_ALBUM_ID")),
                tCursor.getString(tCursor.getColumnIndex("R_ALBUMNAME")),
                tCursor.getString(tCursor.getColumnIndex("R_ALBUMSHORTNAME")),
                tCursor.getString(tCursor.getColumnIndex("R_ALBUMARTISTNAME")),
                tCursor.getInt(tCursor.getColumnIndex("R_TRACKNUMBER")),
                tType,
                tCursor.getInt(tCursor.getColumnIndex("R_REPETITIONS")),
                tBarsperrepeat,
                tCursor.getInt(tCursor.getColumnIndex("R_PLAYINGSECONDS")),
                tSignature,
                tCursor.getString(tCursor.getColumnIndex("R_MEDLEYTYPE")),
                tTwochords,
                tCursor.getInt(tCursor.getColumnIndex("R_COUNT_MUSICFILES")));
    }

    @NonNull
    public String toString() {
        return String.format(Locale.ENGLISH, "%s [%s]", mName, mSignature);
    }

}