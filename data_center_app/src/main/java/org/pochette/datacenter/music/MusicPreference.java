package org.pochette.data_library.music;


import android.content.ContentValues;
import android.database.Cursor;

import org.pochette.utils_lib.logg.Logg;
import org.pochette.data_library.scddb_objects.Dance;
import org.pochette.data_library.search.SearchCall;
import org.pochette.data_library.search.WriteCall;
import org.pochette.utils_lib.search.SearchCriteria;
import org.pochette.utils_lib.search.SearchPattern;

import java.util.Locale;

import androidx.annotation.NonNull;

/**
 * This class stores the indivual preference which musicfile is to be used for which dance
 */


@SuppressWarnings("unused")
public class MusicPreference {

    //Variables
    private static final String TAG = "FEHA (MusicPreference)";

    private static final String MAIN_CLASS = MusicPreference.class.getName();

    int mId;
    MusicFile mMusicFile;
    Dance mDance;
    MusicPreferenceFavourite mMusicPreferenceFavourite;
    boolean mMatchExist; // true if the pair of Dance and Musicfile is based on a match, false if is created without a match (any good!)


    //Constructor
    public MusicPreference(int iId, int iMusicFileId, int iDanceId,
                           MusicPreferenceFavourite iMusicPreferenceFavourite, boolean iMatchExist) {
        try {
            mId = iId;
            mMusicFile = MusicFile.getById(iMusicFileId);
            if (mMusicFile == null) {
                throw new RuntimeException("MusicFile with ID does not exist" + iMusicFileId);
            }
            mDance = Dance.getById(iDanceId);
            if (mDance == null) {
                throw new RuntimeException("Dance with ID does not exist" + iDanceId);
            }
            if (iMusicPreferenceFavourite == null) {
                mMusicPreferenceFavourite = MusicPreferenceFavourite.fromCode("UNKN");
            } else {
                mMusicPreferenceFavourite = iMusicPreferenceFavourite;
            }
            mMatchExist = iMatchExist;
        } catch (Exception e) {

            Logg.w(TAG, e.toString());
        }
    }

    public MusicPreference(int iId, MusicFile iMusicFile, Dance iDance,
                           MusicPreferenceFavourite iMusicPreferenceFavourite, boolean iMatchExist) {
        try {
            mId = iId;
            mMusicFile = iMusicFile;
            if (mMusicFile == null) {
                throw new RuntimeException("MusicFile not provied");
            }
            mDance = iDance;
            if (mDance == null) {
                throw new RuntimeException("Dance not provided");
            }
            if (iMusicPreferenceFavourite == null) {
                mMusicPreferenceFavourite = MusicPreferenceFavourite.fromCode("UNKN");
            } else {
                mMusicPreferenceFavourite = iMusicPreferenceFavourite;
            }
            mMatchExist = iMatchExist;
        } catch (Exception e) {
            Logg.w(TAG, e.toString());
        }
    }



    public static MusicPreference getById(int iMusicFile_id) {
        MusicPreference tMusicFile;
        SearchPattern tSearchPattern = new SearchPattern(MusicPreference.class);
        SearchCriteria tSearchCriteria = new SearchCriteria("ID", "" + iMusicFile_id);
        tSearchPattern.addSearch_Criteria(tSearchCriteria);
        SearchCall tSearch_Call = new SearchCall(MusicPreference.class,
                tSearchPattern, null);
        tMusicFile = tSearch_Call.produceFirst();
        return tMusicFile;

    }


    //<editor-fold desc="Setter and Getter">

    public int getId() {
        return mId;
    }

    public MusicFile getMusicFile() {
        return mMusicFile;
    }

    public MusicPreferenceFavourite getMusicPreferenceFavourite() {
        return mMusicPreferenceFavourite;
    }

    public boolean isMatchExist() {
        return mMatchExist;
    }


    //</editor-fold>

    //Livecycle

    //Static Methods





    //Internal Organs
    //Interface





    public MusicPreference save() {
        WriteCall tWriteCall = new WriteCall(MusicPreference.class, this);
        if (mId <= 0) {
            mId = tWriteCall.insert();
        } else {
            tWriteCall.update();
        }
        return this;
    }

    public ContentValues getContentValues() {
        ContentValues tContentValues = new ContentValues();
        tContentValues.put("MUSICFILE_ID", mMusicFile.mId);
        tContentValues.put("DANCE_ID", mDance.mId);
        tContentValues.put("FAVOURITE", mMusicPreferenceFavourite.getCode());
        tContentValues.put("PAIRING_EXIST", mMatchExist);
        return tContentValues;
    }



    public static MusicPreference convertCursor(Cursor tCursor) {
        MusicPreference result;
        MusicPreferenceFavourite tMusicPreferenceFavourite = MusicPreferenceFavourite.fromCode(
                tCursor.getString(tCursor.getColumnIndex("MP_FAVOURITE"))
        );
        result = new MusicPreference(
                tCursor.getInt(tCursor.getColumnIndex("MP_ID")),
                tCursor.getInt(tCursor.getColumnIndex("MP_MUSICFILE_ID")),
                tCursor.getInt(tCursor.getColumnIndex("MP_DANCE_ID")),
                tMusicPreferenceFavourite,
                tCursor.getInt(tCursor.getColumnIndex("MP_PAIRING_EXIST")) > 0
        );
        return result;
    }


    @NonNull
    public String toString() {
        return String.format(Locale.ENGLISH, "Id %d: Dance %s - MusicFile %s (%s)", mId, mDance.toShortString(),
                mMusicFile.toString(), mMusicPreferenceFavourite.getCode());
    }

}