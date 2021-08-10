package org.pochette.data_library.music;


import android.content.ContentValues;
import android.database.Cursor;

import org.pochette.data_library.database_management.SearchCall;
import org.pochette.data_library.database_management.WriteCall;
import org.pochette.utils_lib.logg.Logg;
import org.pochette.utils_lib.search.SearchCriteria;
import org.pochette.utils_lib.search.SearchPattern;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;

/**
 * This class represents a single music file, incl references to dance and recording
 */
public class MusicFile {
    private static final String TAG = "FEHA (MusicFile)";
    //Variables
    public int mId;
    public String mPath;
    public String mName;
    public String mT2; // seconde but last level, usually Artist
    public String mT1; // letzter Level, typischerweise Albbumtitle
    public int mMediaId; // Mediastore
    public int mTrackNo;
    public int mRecordingId;
    public int mDanceId;
    public float mGainAvg;
    public float mGainMax;
    public int mDuration; // sec
    public MusicFileFavourite mFavourite;
    public MusicFilePurpose mPurpose;
    public String mFileType;
    public boolean mFlagDiagram;
    public boolean mFlagCrib;
    public boolean mFlagRscds;
    public int mDirectoryId;
    public String mSignature;

    static Pattern mPattern = Pattern.compile("^[0-9][ 0-9] ");


    //Constructor
    public MusicFile(int iId, String iPath, String iName, String iT2, String iT1,
                     int iMediaId,
                     int iScddbRecordingId) {

       
        mId = iId;
        mPath = iPath;
        mName = iName;
        mT2 = iT2;
        mT1 = iT1;
        mMediaId = iMediaId;
        mTrackNo = getTrackNo(mName);
        mRecordingId = iScddbRecordingId;
        mDanceId = 0;
        mGainAvg = 0;
        mGainMax = 0;
        mDuration = 0;
        mFavourite = MusicFileFavourite.UNKNOWN;
        mPurpose = MusicFilePurpose.UNKNOWN;
        mFileType = "";
        mFlagDiagram = false;
        mFlagCrib = false;
        mFlagRscds = false;
        mDirectoryId = 0;
        mSignature = "";
    }


    public static MusicFile getById(int iMusicFile_id) {
        MusicFile tMusicFile;
        SearchPattern tSearchPattern = new SearchPattern(MusicFile.class);
        SearchCriteria tSearchCriteria = new SearchCriteria("ID", ""+iMusicFile_id);
        tSearchPattern.addSearch_Criteria(tSearchCriteria);
        SearchCall tSearchCall = new SearchCall(MusicFile.class, tSearchPattern, null);
        tMusicFile = tSearchCall.produceFirst();
        return tMusicFile;

    }


    //<editor-fold desc="Setter and Getter">
    @NonNull
    public String getPath() {
        return mPath;
    }

    public void setId(int tId) {
        mId = tId;
    }

    @SuppressWarnings("unused")
    public int getTrackNo() {
        return mTrackNo;
    }

    public static int getTrackNo(String iName) {
        int result = 0;
        Matcher tMatcher = mPattern.matcher(iName);
        String t;
        try {
            if (tMatcher.find()) {
                t = iName.substring(0, tMatcher.end() - 1);
                result = Integer.parseInt(t);
            }
        } catch (NumberFormatException e) {
            Logg.e(TAG, e.toString());
        }
        return result;
    }


    //</editor-fold>

    //Livecycle
    //Static Methods
    //Internal Organs
    //Interface

    public static ArrayList<MusicFile> getByDirectoryId( int  tMusicDirectoryId) {
        SearchPattern tSearchPattern = new SearchPattern(MusicFile.class);
        SearchCriteria tSearchCriteria = new SearchCriteria("DIRECTORY_ID",
                "" + tMusicDirectoryId);
        tSearchPattern.addSearch_Criteria(tSearchCriteria);
        SearchCall tSearchCall = new SearchCall(MusicFile.class, tSearchPattern, null);
        return tSearchCall.produceArrayList();
    }

    public MusicFile save() {
        WriteCall tWriteCall = new WriteCall(MusicFile.class, this);
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
        tContentValues.put("NAME", mName);
        tContentValues.put("TRACK_NO", mTrackNo);

        tContentValues.put("MEDIA_ID", mMediaId);
        tContentValues.put("RECORDING_ID", mRecordingId);
        tContentValues.put("DANCE_ID", mDanceId);
        tContentValues.put("GAIN_AVG", mGainAvg);
        tContentValues.put("GAIN_MAX", mGainMax);
        tContentValues.put("DURATION", mDuration);
        if (mDirectoryId > 0) {
            tContentValues.put("MUSICDIRECTORY_ID", mDirectoryId);
        } else {
            Logg.w(TAG, "insert without DirectoryId: " + mName);
        }
        if (mPurpose != null) {
            tContentValues.put("MUSIC_PURPOSE", mPurpose.getCode());
        }
        if (mFavourite != null) {
            tContentValues.put("FAVOURITE", mFavourite.getCode());
        }
        if (mSignature != null) {
            tContentValues.put("SIGNATURE", mSignature);
        }
        return tContentValues;
    }

    public static MusicFile convertCursor(Cursor tCursor) {
        MusicFile result;
        result = new MusicFile(
                tCursor.getInt(tCursor.getColumnIndex("MF_ID")),
                tCursor.getString(tCursor.getColumnIndex("MF_PATH")),
                tCursor.getString(tCursor.getColumnIndex("MF_NAME")),
                tCursor.getString(tCursor.getColumnIndex("MF_T2")),
                tCursor.getString(tCursor.getColumnIndex("MF_T1")),
                tCursor.getInt(tCursor.getColumnIndex("MF_MEDIA_ID")),
                tCursor.getInt(tCursor.getColumnIndex("MF_RECORDING_ID")));
        result.mTrackNo = tCursor.getInt(tCursor.getColumnIndex("MF_TRACK_NO"));
        result.mDirectoryId = tCursor.getInt(tCursor.getColumnIndex("MF_MUSICDIRECTORY_ID"));
        result.mDanceId = tCursor.getInt(tCursor.getColumnIndex("MF_DANCE_ID"));
        result.mGainAvg = tCursor.getInt(tCursor.getColumnIndex("MF_GAIN_AVG"));
        result.mGainMax = tCursor.getInt(tCursor.getColumnIndex("MF_GAIN_MAX"));
        result.mDuration = tCursor.getInt(tCursor.getColumnIndex("MF_DURATION"));
        result.mSignature = tCursor.getString(tCursor.getColumnIndex("MF_SIGNATURE"));

        if (tCursor.getString(tCursor.getColumnIndex("MF_FAVOURITE")) != null &&
                !tCursor.getString(tCursor.getColumnIndex("MF_FAVOURITE")).equals("")) {
            result.mFavourite = MusicFileFavourite.fromCode(
                    tCursor.getString(tCursor.getColumnIndex("MF_FAVOURITE")));
        }
        if (tCursor.getString(tCursor.getColumnIndex("MF_MUSIC_PURPOSE")) != null &&
                !tCursor.getString(tCursor.getColumnIndex("MF_MUSIC_PURPOSE")).equals("")) {
            result.mPurpose = MusicFilePurpose.fromCode(
                    tCursor.getString(tCursor.getColumnIndex("MF_MUSIC_PURPOSE")));
        }
        result.mFileType = tCursor.getString(tCursor.getColumnIndex("MF_FILETYPE"));

        String tYN;
        tYN = tCursor.getString(tCursor.getColumnIndex("MF_RSCDS_YN"));
        result.mFlagRscds = (tYN.equals("Y"));
        tYN = tCursor.getString(tCursor.getColumnIndex("MF_CRIBS_YN"));
        result.mFlagCrib = (tYN.equals("Y"));
        tYN = tCursor.getString(tCursor.getColumnIndex("MF_DIAGRAMS_YN"));
        result.mFlagDiagram = (tYN.equals("Y"));

        result.mFavourite = MusicFileFavourite.fromCode(
                tCursor.getString(tCursor.getColumnIndex("MF_FAVOURITE")));

        return result;
    }

    public int getId() {
        return mId;
    }

    public String getDirectoryPath() {
        if (mPath == null || mPath.isEmpty()) {
            return null;
        }
        int tPosition = mPath.lastIndexOf("/");
        if (tPosition <= 0) {
            return null;
        }
        String tDirectoryPath = mPath.substring(0, tPosition );
        return tDirectoryPath;
    }

    public String getFilename() {
        if (mPath == null || mPath.isEmpty()) {
            return null;
        }
        int tPosition = mPath.lastIndexOf("/");
        if (tPosition <= 0) {
            return null;
        }
        return mPath.substring( tPosition +1);
    }

    @NonNull
    public String toString() {
        return mName;
    }

}