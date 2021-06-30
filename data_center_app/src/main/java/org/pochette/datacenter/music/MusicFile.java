package org.pochette.data_library.music;


import android.content.ContentValues;
import android.database.Cursor;

import org.pochette.utils_lib.logg.Logg;
import org.pochette.data_library.pairing.Signature;
import org.pochette.data_library.scddb_objects.Dance;
import org.pochette.data_library.search.SearchCall;
import org.pochette.data_library.search.WriteCall;
import org.pochette.utils_lib.search.SearchCriteria;
import org.pochette.utils_lib.search.SearchPattern;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;


public class MusicFile {

    //Variables
    private static String TAG = "FEHA (MusicFile)";

    private static final String MAIN_CLASS = MusicFile.class.getName();

  
    public int mId;
    public String mPath;
    public String mName;
    public String mT2; // seconde but last level, usually Artist
    public String mT1; // letzter Level, typischerweise Albbumtitle
    public int mMediaId; // Mediastore
    public int mTrackNo;
    public int mScddbRecordingId;
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
        mScddbRecordingId = iScddbRecordingId;
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

//    public String getDurationString() {
//        if (mDuration == 0) {
//            return " -- ";
//        }
//        return String.format(Locale.ENGLISH, "%d:%02d",
//                mDuration / 60,
//                mDuration % 60);
//    }


    public int getTrackNo() {
        return mTrackNo;
        //return getTrackNo(mName);
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

    /**
     * Remove some uninforamtive stuff from the name, i.e. tracknumber and signature
     *
     * @return the nice name
     */
//    public String getNiceName() {
//        String tResult;
//        String tTemp;
//        String tRegEx;
//        Pattern tPattern;
//        Matcher tMatcher;
//
//        //	tRegEx = "(^[0-9][ 0-9] )(.*)([hHjJmMrRsSwW]\\d+x\\d+)(.*)(\\(digitized\\))*(\\.mp3)+";
//        tRegEx = "(^[0-9][ 0-9] )(.*)";
//        tPattern = Pattern.compile(tRegEx);
//        tMatcher = tPattern.matcher(mName);
//        try {
//            if (tMatcher.find()) {
//                tTemp = tMatcher.group(2);
//
//                tTemp = Objects.requireNonNull(tTemp).replaceAll("[ -]*$", "");
//                tResult = tTemp;
//            } else {
//                tResult = mName;
//            }
//        } catch (NumberFormatException e) {
//            Logg.e(TAG, e.toString());
//            return mName;
//        }
//
//        tRegEx = "(.*)([hHjJmMrRsSwW]\\d+x\\d+)(.*)(\\(digitized\\))*(\\.mp3)+";
//        tPattern = Pattern.compile(tRegEx);
//        tMatcher = tPattern.matcher(tResult);
//        try {
//            if (tMatcher.find()) {
//                tTemp = tMatcher.group(1);
//                tTemp = Objects.requireNonNull(tTemp).replaceAll("[ -]*$", "");
//                tResult = tTemp;
//            } else {
//                tResult = mName;
//            }
//        } catch (NumberFormatException e) {
//            Logg.e(TAG, e.toString());
//            return mName;
//        }
//
//        return tResult;
//    }
    //</editor-fold>

    //Livecycle
//    public static String getClassname_DB() {
//        return "MusicFile_DB";
//    }

    //    //Static Methods


//    public static MusicFile getDbDateByPath(String tPath) {
//        MusicFile tMusicFile;
//        SearchPattern tSearchPattern = new SearchPattern(MusicFile.class);
//        SearchCriteria tSearchCriteria = new SearchCriteria("PATH", tPath);
//        tSearchPattern.addSearch_Criteria(tSearchCriteria);
//        SearchCall tSearchCall = new SearchCall(MusicFile.class, tSearchPattern, null);
//        tMusicFile = tSearchCall.produceFirst();
//        return tMusicFile;
//    }


//
//    private static ArrayList<String> getAR_ScoringNames() {
//
//        // Define top dances
//        ArrayList<String> tAR_ScoringNames = new ArrayList<>(0);
//        tAR_ScoringNames.add("PelorusJack".toLowerCase());
//        tAR_ScoringNames.add("MontgomeriesRant".toLowerCase());
//        tAR_ScoringNames.add("MairisWedding".toLowerCase());
//        tAR_ScoringNames.add("ReeloftheRoyalScots".toLowerCase());
//        tAR_ScoringNames.add("DeilAmangtheTailors".toLowerCase());
//        tAR_ScoringNames.add("MinisterontheLoch".toLowerCase());
//        tAR_ScoringNames.add("MaxwellsRant".toLowerCase());
//        tAR_ScoringNames.add("CatchtheWind".toLowerCase());
//        tAR_ScoringNames.add("HoopersJig".toLowerCase());
//        tAR_ScoringNames.add("JoiedeVivre".toLowerCase());
//        tAR_ScoringNames.add("Reelofthe51stDivision".toLowerCase());
//        tAR_ScoringNames.add("ShiftinBobbins".toLowerCase());
//        tAR_ScoringNames.add("ScottMeikle".toLowerCase());
//        tAR_ScoringNames.add("WildGeese".toLowerCase());
//        tAR_ScoringNames.add("Gentleman".toLowerCase());
//        tAR_ScoringNames.add("IrishRover".toLowerCase());
//        tAR_ScoringNames.add("DreamCatcher".toLowerCase());
//        tAR_ScoringNames.add("MissJohnstoneofArdrossan".toLowerCase());
//        tAR_ScoringNames.add("MrsStewartsJig".toLowerCase());
//        tAR_ScoringNames.add("SugarCandie".toLowerCase());
//        return tAR_ScoringNames;
//    }
//
//
//    //</editor-fold>
//
//
////Internal Organs
//    //Interface
//



//    public static MusicFile getByRecording(Recording tScddbRecording) throws
//            FileNotFoundException {
//        if (tScddbRecording.mCountMusicfile == 0) {
//            throw new FileNotFoundException(
//                    String.format(Locale.ENGLISH, "No MusicFile for %s", tScddbRecording.mName));
//        }
//
//        SearchPattern tSearchPattern = new SearchPattern(MusicFile.class);
//        SearchCriteria tSearchCriteria = new SearchCriteria("RECORDING_ID",
//                "" + tScddbRecording.mId);
//        tSearchPattern.addSearch_Criteria(tSearchCriteria);
//        SearchCall tSearchCall = new SearchCall(MusicFile.class, tSearchPattern, null);
//
//        MusicFile tMusicFile;
//        tMusicFile = tSearchCall.produceFirst();
//        if (tMusicFile == null) {
//            throw new FileNotFoundException(
//                    String.format(Locale.ENGLISH, "No MusicFile found for %s", tScddbRecording.mName));
//        }
//
//        return tMusicFile;
//    }

    public static MusicFile getByDance(Dance tDance) throws FileNotFoundException {

        if (tDance.mCountofRecordings == 0) {
            Logg.i("No MusicFile for %s", tDance.toString());
            return null;
        }

        SearchPattern tSearchPattern = new SearchPattern(MusicFile.class);
        SearchCriteria tSearchCriteria = new SearchCriteria("DANCE_ID",
                "" + tDance.mId);
        tSearchPattern.addSearch_Criteria(tSearchCriteria);
        SearchCall tSearchCall = new SearchCall(MusicFile.class, tSearchPattern, null);

        ArrayList<MusicFile> tAR_Musicfile = tSearchCall.produceArrayList();
        Comparator<MusicFile> tMusicFileComparator = (o1, o2) -> {
            if (o2.mFavourite.getPriority() == o1.mFavourite.getPriority()) {
                return o2.mId - o1.mId;
            } else {
                return o2.mFavourite.getPriority() - o1.mFavourite.getPriority();
            }
        };
        MusicFile tMusicFile;
        for (MusicFile sMusicFile : tAR_Musicfile) {
            Logg.i(TAG, sMusicFile.toString());
            Logg.i(TAG, sMusicFile.mFavourite.toString() + " " + sMusicFile.mId);
        }
        Collections.sort(tAR_Musicfile, tMusicFileComparator);
        for (MusicFile sMusicFile : tAR_Musicfile) {

            Logg.i(TAG, sMusicFile.toString());
            Logg.i(TAG, sMusicFile.mFavourite.toString() + " " + sMusicFile.mId);
        }
        tMusicFile = tAR_Musicfile.get(0);
        if (tMusicFile == null) {
            throw new FileNotFoundException(
                    String.format(Locale.ENGLISH, "No MusicFile found for %s", tDance.toString()));
        }
        return tMusicFile;
    }

//    public static ArrayList<MusicFile> getARByDance(Dance tDance) throws
//            FileNotFoundException {
//        if (tDance.mCountofRecordings == 0) {
//            Logg.i("No MusicFile for %s", tDance.toString());
//            return null;
//        }
//        SearchPattern tSearchPattern = new SearchPattern(MusicFile.class);
//        SearchCriteria tSearchCriteria = new SearchCriteria("DANCE_ID",
//                "" + tDance.mId);
//        tSearchPattern.addSearch_Criteria(tSearchCriteria);
//        SearchCall tSearchCall = new SearchCall(MusicFile.class, tSearchPattern, null);
//        ArrayList<MusicFile> tAR_MusicFile;
//        tAR_MusicFile = tSearchCall.produceArrayList();
//        if (tAR_MusicFile == null || tAR_MusicFile.size() == 0) {
//            throw new FileNotFoundException(
//                    String.format(Locale.ENGLISH, "No MusicFiles found for %s", tDance.toString()));
//        }
//        return tAR_MusicFile;
//    }


//    public static ArrayList<MusicFile> getByDirectoryId(int  tMusicDirectoryId) {
//        SearchPattern tSearchPattern = new SearchPattern(MusicFile.class);
//        SearchCriteria tSearchCriteria = new SearchCriteria("DIRECTORY_ID",
//                "" + tMusicDirectoryId);
//        tSearchPattern.addSearch_Criteria(tSearchCriteria);
//        SearchCall tSearchCall = new SearchCall(MusicFile.class, tSearchPattern, null);
//        return tSearchCall.produceArrayList();
//    }

//    public void save(int tMusicDirectoryId) {
//        // todo
//        //MusicFile_DB.save(this, tMusicDirectoryId);
//    }


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
        tContentValues.put("RECORDING_ID", mScddbRecordingId);
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
        String[] parts = Objects.requireNonNull(mPath).split("/");
        int tPosition = mPath.lastIndexOf("/");
        if (tPosition <= 0) {
            return null;
        }
        String tDirectoryPath = mPath.substring(0, tPosition - 1);
        return tDirectoryPath;
    }

    public String getFilename() {
        if (mPath == null || mPath.isEmpty()) {
            return null;
        }
        String[] parts = Objects.requireNonNull(mPath).split("/");
        int tPosition = mPath.lastIndexOf("/");
        if (tPosition <= 0) {
            return null;
        }
        String tFilename = mPath.substring( tPosition +1);
        return tFilename;
    }



    @NonNull
    public String toString() {
        return mName;
    }

    public String getSignature() {
        String result = "";
        String tSignaturePatter = Signature.getPattern();
        Pattern tPattern = Pattern.compile(tSignaturePatter);
        Matcher tMatcher = tPattern.matcher(mName);
        if (tMatcher.matches()) {
            result = tMatcher.group(2);
        }
        if (Objects.requireNonNull(result).isEmpty()) {
            result = Signature.getEmpty();
        }
        return result;
    }


}