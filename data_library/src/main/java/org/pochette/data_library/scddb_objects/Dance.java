package org.pochette.data_library.scddb_objects;


import android.database.Cursor;

import org.pochette.data_library.database_management.SearchCall;
import org.pochette.data_library.music.MusicFile;
import org.pochette.utils_lib.logg.Logg;
import org.pochette.utils_lib.search.SearchCriteria;
import org.pochette.utils_lib.search.SearchPattern;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

import androidx.annotation.NonNull;

/**
 * Dance correspond to an dance as available on scddb
 */
public class Dance {

    //Variables
    private static final String TAG = "FEHA (Dance)";
    //private static String MAIN_CLASS = Dance.class.getName();

    public int mId;
    public String mName;

    public String mType;
    public int mBarsperrepeat;
    public String mMedleytype;
    public String mShape;
    public String mCouples;
    public String mProgression;
    public int mCountofCribs;
    public int mCountofDiagrams;
    public int mCountofRecordings; // recording due to matching
    public int mCountofAnyGoodRecordings; // recordinng unmatched, but preferred
    public int mCountofVideos;
    public int mCountofInstructions;
    public boolean mFlagRscds;
    private String mFavouriteCode;

    //Constructor

    public Dance(int tID) throws Exception {
        SearchPattern tSearchPattern = new SearchPattern(Dance.class);
        tSearchPattern.addSearch_Criteria(
                new SearchCriteria("ID",
                        String.format(Locale.ENGLISH, "%d", tID)));
        SearchCall tSearchCall =
                new SearchCall(Dance.class, tSearchPattern, null);
        Dance tDance = tSearchCall.produceFirst();
        mId = tDance.mId;
        mName = tDance.mName;
        mType = tDance.mType;
        mBarsperrepeat = tDance.mBarsperrepeat;
        mMedleytype = tDance.mMedleytype;
        mShape = tDance.mShape;
        mCouples = tDance.mCouples;
        mProgression = tDance.mProgression;
        mCountofCribs = tDance.mCountofCribs;
        mCountofDiagrams = tDance.mCountofDiagrams;
        mCountofRecordings = tDance.mCountofRecordings;
        mCountofAnyGoodRecordings = tDance.mCountofAnyGoodRecordings;
        mCountofVideos = tDance.mCountofVideos;
        mCountofInstructions = tDance.mCountofInstructions;
        mFlagRscds = tDance.mFlagRscds;
        mFavouriteCode = tDance.mFavouriteCode;
        if (mId != tID) {
            throw new Exception("Dance WITH ID DOES NOT EXIST" + tID);
        }
    }

    public Dance(int tId, String tName, String tType, int tBarsperrepeat,
                 String tMedleytype, String tShape, String tCouples, String tProgression,
                 int tCountofCribs,
                 int tCountofDiagrams, int tCountofRecordings,
                 int tCountofAnyGoodRecordings,
                 boolean tFlagRscds,
                 String iFavouriteCode) {
        mId = tId;
        mName = tName;
        mType = tType;
        mBarsperrepeat = tBarsperrepeat;
        mMedleytype = tMedleytype;
        mShape = tShape;
        mCouples = tCouples;
        mProgression = tProgression;
        mCountofCribs = tCountofCribs;
        mCountofDiagrams = tCountofDiagrams;
        mCountofRecordings = tCountofRecordings;
        mCountofAnyGoodRecordings = tCountofAnyGoodRecordings;
        mFlagRscds = tFlagRscds;
        mFavouriteCode = iFavouriteCode;

    }
    //Setter and Getter

    public String getFavouriteCode() {
        return mFavouriteCode;
    }

    public void setFavouriteCode(@DanceClassification.Filter String iFavouriteCode) {
        Logg.w(TAG, iFavouriteCode);
        mFavouriteCode = iFavouriteCode;
    }

    //Livecycle

    //Static Methods
//	static String getSelectStatement() {
//
//	}


    public static ArrayList<Dance> DANCEsGetByRecording(int tRECORDING_ID) {
        SearchPattern tSearchPattern = new SearchPattern(Dance.class);
        tSearchPattern.addSearch_Criteria(
                new SearchCriteria("RECORDING_ID",
                        String.format(Locale.ENGLISH, "%d", tRECORDING_ID)));
        SearchCall tSearchCall =
                new SearchCall(Dance.class, tSearchPattern, null);
        return tSearchCall.produceArrayList();
    }


    //Internal Organs

    int calcScore() {
        int tScore = 0;
        DanceFavourite tDanceFavourite = DanceFavourite.fromCode(mFavouriteCode);
		tScore+= tDanceFavourite.getPriority()* 10000; // as Prio is between 1 and 100, tscore is between 10.000 and 1.000.000
		tScore+= Math.min(9, mCountofRecordings) * 1000; // more than nine recording are treated as 9
		if (mCountofDiagrams > 0) {
			tScore += 400;
		}
		if (mFlagRscds ) {
			tScore += 200;
		}
		if (mCountofCribs > 0) {
			tScore += 100;
		}
        return tScore;
    }
    //Interface
    public int getScore() {
        return calcScore();
    }

    public String toShortString() {
        return String.format(Locale.ENGLISH, "%s [%s]", mName, mType);
    }

    @NonNull
    public String toString() {
        return String.format(Locale.ENGLISH, "%s [%s] %s", mName, this.getSignature(), mShape);
    }

    public String getSignature() {
        return String.format(Locale.ENGLISH, "%s %dx%d", mType.substring(0, 1), 8, mBarsperrepeat);
    }

    public MusicFile getMusicFile() {
        if (mCountofRecordings == 0) {
            Logg.i("No MusicFile for %s", this.toString());
            return null;
        }
        SearchPattern tSearchPattern = new SearchPattern(MusicFile.class);
        SearchCriteria tSearchCriteria = new SearchCriteria("DANCE_ID", "" + mId);
        tSearchPattern.addSearch_Criteria(tSearchCriteria);
        SearchCall tSearchCall = new SearchCall(MusicFile.class, tSearchPattern, null);
        ArrayList<MusicFile> tAR_Musicfile = tSearchCall.produceArrayList();
        if (tAR_Musicfile.size() == 0) {
            return null;
        }
        Comparator<MusicFile> tMusicFileComparator = (o1, o2) -> {
            if (o2.mFavourite.getPriority() == o1.mFavourite.getPriority()) {
                return o2.mId - o1.mId;
            } else {
                return o2.mFavourite.getPriority() - o1.mFavourite.getPriority();
            }
        };
        MusicFile tMusicFile;
        Collections.sort(tAR_Musicfile, tMusicFileComparator);
        tMusicFile = tAR_Musicfile.get(0);
        return tMusicFile;
    }
//
//    public static Dance getById(int tId) {
//        SearchPattern tSearchPattern = new SearchPattern(Dance.class);
//        SearchCriteria tSearchCriteria = new SearchCriteria("ID", String.format(Locale.ENGLISH, "%d", tId));
//        tSearchPattern.addSearch_Criteria(tSearchCriteria);
//        SearchCall tSearchCall = new SearchCall(Dance.class, tSearchPattern, null);
//        return tSearchCall.produceFirst();
//    }

//
//    public static Dance getById(ArrayList<Dance> tAL, int id) {
//        for (Dance t : tAL) {
//            if (t.mId == id) {
//                return t;
//            }
//        }
//        return null;
//    }

//    public static Dance getByRecordingId(int tId) {
//        SearchPattern tSearchPattern = new SearchPattern(Dance.class);
//        tSearchPattern.addSearch_Criteria(
//                new SearchCriteria("RECORDING_ID",
//                        String.format(Locale.ENGLISH, "%d", tId)));
//        SearchCall tSearchCall =
//                new SearchCall(Dance.class, tSearchPattern, null);
//        ArrayList<Dance> tAL_Dance = tSearchCall.produceArrayList();
//
//        switch (tAL_Dance.size()) {
//            case 0:
//                return null;
//            case 1:
//                //noinspection DuplicateBranchesInSwitch
//                return tAL_Dance.get(0);
//            default:
//                return tAL_Dance.get(0);
//        }
//    }
//
//    public static int getPositionById(ArrayList<Dance> tD, int id) {
//        if (tD == null) {
//            return -1;
//        }
//        for (int i = 0; i < tD.size(); i++) {
//            if (tD.get(i).mId == id) {
//                return i;
//            }
//        }
//        return -1;
//    }



    public static String getClassname_DB() {
        return "org.pochette.data_library.scddb_objects.Dance";
    }

    public static Dance convertCursor(Cursor iCursor) {
        String tRSCDS_YN = iCursor.getString(iCursor.getColumnIndex("D_RSCDS_YN"));
        boolean tFlagRscds = (tRSCDS_YN.equals("Y"));
        return new Dance(
                iCursor.getInt(iCursor.getColumnIndex("D_ID")),
                iCursor.getString(iCursor.getColumnIndex("D_NAME")),
                iCursor.getString(iCursor.getColumnIndex("D_TYPENAME")),
                iCursor.getInt(iCursor.getColumnIndex("D_BARSPERREPEAT")),
                iCursor.getString(iCursor.getColumnIndex("D_MEDLEYTYPE")),
                iCursor.getString(iCursor.getColumnIndex("D_SHAPE")),
                iCursor.getString(iCursor.getColumnIndex("D_COUPLES")),
                iCursor.getString(iCursor.getColumnIndex("D_PROGRESSION")),
                iCursor.getInt(iCursor.getColumnIndex("D_COUNT_OF_CRIBS")),
                iCursor.getInt(iCursor.getColumnIndex("D_COUNT_OF_DIAGRAMS")),
                iCursor.getInt(iCursor.getColumnIndex("D_COUNT_OF_RECORDINGS")),
                iCursor.getInt(iCursor.getColumnIndex("D_COUNT_OF_ANYGOOD_RECORDINGS")),
                tFlagRscds,
                iCursor.getString(iCursor.getColumnIndex("D_FAVOURITE_CODE"))
        );
    }


//	public static HashSet<String> getCompleteHS() {
//		if (mHS_Complete == null) {
//			mHS_Complete = new HashSet<>(0);
//			SearchPattern tSearchPattern = new SearchPattern(Dance.class);
//			SearchCall tSearchCall =
//					new SearchCall(Dance.class, tSearchPattern, null);
//			mHS_Complete = tSearchCall.produceHashSet();
//
//			Logg.i(TAG, "HashSet" + mHS_Complete.size());
//		}
//		return mHS_Complete;
//	}


//    public static HashSet<String> getEmptyHS() {
//        return new HashSet<>(0);
//    }


}