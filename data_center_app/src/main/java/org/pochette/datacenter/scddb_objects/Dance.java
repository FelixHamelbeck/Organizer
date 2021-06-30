package org.pochette.data_library.scddb_objects;


import android.database.Cursor;

import org.pochette.data_library.search.SearchCall;
import org.pochette.utils_lib.search.SearchCriteria;
import org.pochette.utils_lib.search.SearchPattern;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;

import androidx.annotation.NonNull;

/**
 * Album correspond to an dance as available on scddb
 */
public class Dance {

	//Variables
	@SuppressWarnings("FieldCanBeLocal")
	private static String TAG = "FEHA (Dance)";
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
	public DanceFavourite mDanceFavourite;

	//Constructor

	public Dance(int tID) throws Exception{

		//Logg.d(TAG, String.format(Locale.ENGLISH,"Construct with id: %d", tID));

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
		mDanceFavourite = tDance.mDanceFavourite;

		//Logg.i(TAG, "from ID COR:" + mCountofRecordings);
		if (mId != tID) {
			throw new Exception("ALBUM WITH ID DOES NOT EXIST" + tID);
		}


	}

	public Dance(int tId, String tName, String tType, int tBarsperrepeat,
                 String tMedleytype, String tShape, String tCouples, String tProgression,
                 int tCountofCribs,
//				 int tCountofDiagrams, int tCountofRecordings,
//                 int tCountofAnyGoodRecordings,
                 int tCountofVideos, int tCountofInstructions,
				 boolean tFlagRscds
//			,
    //             DanceFavourite tDanceFavourite
	) {
		mId = tId;
		mName = tName;
		mType = tType;
		mBarsperrepeat = tBarsperrepeat;
		mMedleytype = tMedleytype;
		mShape = tShape;
		mCouples = tCouples;
		mProgression = tProgression;
		mCountofCribs = tCountofCribs;
//		mCountofDiagrams = tCountofDiagrams;
//		mCountofRecordings = tCountofRecordings;
//		mCountofAnyGoodRecordings = tCountofAnyGoodRecordings;
//		mCountofVideos = tCountofVideos;
//		mCountofInstructions =tCountofInstructions;
		mFlagRscds = tFlagRscds;
//		mDanceFavourite = tDanceFavourite;
//
		//	Logg.i(TAG, "from Full COR:" + mCountofRecordings);
	}
	//Setter and Getter

	//Livecycle

	//Static Methods
//	static String getSelectStatement() {
//
//	}


	public static ArrayList<Dance> DANCEsGetByRecording(int tRECORDING_ID){

		//Logg.i(TAG, "Start DANCEsGetByRecording(int RECORDING_ID)" + tRECORDING_ID);


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
//		tScore+= mDanceFavourite.getPriority()* 10000; // as Prio is between 1 and 100, tscore is between 10.000 and 1.000.000
//		tScore+= Math.min(9, mCountofRecordings) * 1000;
//		if (mCountofDiagrams > 0) {
//			tScore += 400;
//		}
//		if (mFlagRscds ) {
//			tScore += 200;
//		}
//		if (mCountofCribs > 0) {
//			tScore += 100;
//		}

		return tScore;


	}



	//Interface


	public int getScore() {
		return calcScore();
	}


	public String toShortString(){
		return String.format(Locale.ENGLISH,"%s [%s]",mName,mType);
	}

	@NonNull
	public String toString(){
		return String.format(Locale.ENGLISH,"%s [%s] %s",mName,this.getSignature(),mShape);
	}

	public String getSignature(){
		return String.format(Locale.ENGLISH,"%s %dx%d",mType.substring(0,1 ),8,mBarsperrepeat);
	}


	public static Dance getById(int tId) {
		SearchPattern tSearchPattern = new SearchPattern(Dance.class);
		SearchCriteria tSearchCriteria = new SearchCriteria("ID", String.format(Locale.ENGLISH, "%d", tId));
		tSearchPattern.addSearch_Criteria(tSearchCriteria);
		SearchCall tSearchCall = new SearchCall(Dance.class, tSearchPattern, null);
		return tSearchCall.produceFirst();
	}


	public static Dance getById(ArrayList<Dance> tAL, int id){

		for ( Dance t : tAL){
			if (t.mId == id){
				return t;
			}
		}
		return null;
	}

	public static Dance getByRecordingId(int tId){

		SearchPattern tSearchPattern = new SearchPattern(Dance.class);
		tSearchPattern.addSearch_Criteria(
				new SearchCriteria("RECORDING_ID",
						String.format(Locale.ENGLISH, "%d", tId)));
		SearchCall tSearchCall =
				new SearchCall(Dance.class, tSearchPattern, null);
		ArrayList<Dance> tAL_Dance = tSearchCall.produceArrayList();

		switch (tAL_Dance.size()) {
			case 0:
				return null;
			case 1:
				//noinspection DuplicateBranchesInSwitch
				return tAL_Dance.get(0);
			default:
				return tAL_Dance.get(0);


		}

	}

	public static int getPositionById(ArrayList<Dance> tD, int id){
		if( tD == null){
			return -1;
		}
		for ( int i = 0 ; i < tD.size();  i++){
			if ( tD.get(i).mId == id ){
				return i;
			}
		}
		return -1;
	}
	private static HashSet<String> mHS_Complete;

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
//				iCursor.getInt(iCursor.getColumnIndex("D_COUNT_OF_DIAGRAMS")),
//				iCursor.getInt(iCursor.getColumnIndex("D_COUNT_OF_RECORDINGS")),
//				iCursor.getInt(iCursor.getColumnIndex("D_COUNT_OF_ANYGOOD_RECORDINGS")),
				0,
				0,
				tFlagRscds//,
		//		DanceFavourite.fromCode(iCursor.getString(iCursor.getColumnIndex("D_DanceFavourite")))
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


	public static HashSet<String> getEmptyHS() {
		return new HashSet<>(0);
	}


}