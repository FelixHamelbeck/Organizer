package org.pochette.data_library.playlist;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import org.pochette.utils_lib.logg.Logg;
import org.pochette.data_library.music.MusicFile;
import org.pochette.data_library.scddb_objects.Dance;
import org.pochette.data_library.scddb_objects.Recording;
import org.pochette.data_library.search.WriteCall;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;

/**
 * The class PlayInstruction contains the instruction to play one item
 * The tool executing the instruction in DiskJockey
 * Usually PlayInstructions are combined into a Playlist
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class Playinstruction {

	static final String TAG = "FEHA (PlayInstruction)";

	int mId;
	String mT2;
	String mSignature;
	int mDuration;
	String mShortname;
	int mMusicFile_Id;
	private MusicFile mMusicFile;
	int mScddbDance_Id;
	private Dance mScddbDance;
	int mScddbRecording_id;
	private Recording mRecording;
	long mStart_ms;
	long mEnd_ms;
	float mVolume_factor;
	public int mPos;
	public Context mContext;
	public Uri mUri;
	private String mMinorText="";
	private String mMajorText="";

	public Playinstruction(Context iContext, Uri iUri) {
		if (iUri == null) {
			throw new RuntimeException("no Uri provided");
		}
		if (iContext == null) {
			throw new RuntimeException("no Context provided");
		}
		mContext = iContext;
		mUri = iUri;
	}

	@SuppressWarnings("CopyConstructorMissesField")
	public Playinstruction(Playinstruction iPlayinstruction) {
		mDuration = iPlayinstruction.mDuration;
		mShortname = iPlayinstruction.mShortname;
		mContext = iPlayinstruction.mContext;
		mUri = iPlayinstruction.mUri;
		int mMusicFile_Id;
		int mScddbDance_id;
		int mScddbRecording_id;
		mStart_ms = iPlayinstruction.mStart_ms;
		mEnd_ms = iPlayinstruction.mEnd_ms;
		mVolume_factor = iPlayinstruction.mVolume_factor;
	}

	public Playinstruction(int tId, MusicFile tMusicFile, int tScddbRecording_id, int tScddbDance_id, float tVolume_factor, long tStart_ms, long tEnd_ms) {
		if (tStart_ms < 0) {
			tStart_ms = 0;
		}
		if (tEnd_ms < 0) {
			tEnd_ms = tMusicFile.mDuration;
		}
		if (tVolume_factor < 0) {
			tVolume_factor = 1.f;
		}
		mId = tId;
		mMusicFile = tMusicFile;
		grindMusicFile();
		mScddbDance_Id = tScddbDance_id;
		mScddbRecording_id = tScddbRecording_id;
		mVolume_factor = tVolume_factor;
		mStart_ms = tStart_ms;
		mEnd_ms = tEnd_ms;
		mMajorText = "";
		mMinorText = "";
	}

	/**
	 * Constructor, where the musicfile object is not yet read, but only the data needed for display in playinstruction adapater
	 *
	 * @param tId                Database Id of PlayInstruction
	 * @param tMusicFile_Id      Id of MusicFile
	 * @param tT2                T2 of MusicFile
	 * @param tSignature         Signature of Musicfile
	 * @param tDuration          Duration of Musicfile
	 * @param tScddbRecording_id as Recording_id
	 * @param tScddbDance_id     as Dance_id
	 * @param tVolume_factor     as VolumeFactor
	 * @param tStart_ms          as Start_MS
	 * @param tEnd_ms            as End_MS
	 */
	public Playinstruction(int tId,
                           int tMusicFile_Id, String tShortname, String tT2, String tSignature, int tDuration,
                           int tScddbRecording_id, int tScddbDance_id,
						   float tVolume_factor, long tStart_ms, long tEnd_ms, int tPos) {
		if (tStart_ms < 0) {
			tStart_ms = 0;
		}
		if (tEnd_ms < 0) {
			tEnd_ms = tDuration;
		}
		if (tVolume_factor < 0) {
			tVolume_factor = 1.f;
		}
		mId = tId;
		mMusicFile = null;
		mMusicFile_Id = tMusicFile_Id;
		mShortname = tShortname;
		mT2 = tT2;
		mSignature = tSignature;
		mDuration = tDuration;
		mScddbDance_Id = tScddbDance_id;
		mScddbRecording_id = tScddbRecording_id;
		mVolume_factor = tVolume_factor;
		mStart_ms = tStart_ms;
		mEnd_ms = tEnd_ms;
		mPos = tPos;
		mMajorText = "";
		mMinorText = "";
	}


	public Playinstruction(MusicFile tMusicFile) throws IllegalArgumentException {
		if (tMusicFile == null) {
			throw new IllegalArgumentException("no Musicfile provided");
		}
		this.mMusicFile = tMusicFile;
		grindMusicFile();
		this.mScddbDance_Id = tMusicFile.mDanceId;
		this.mScddbRecording_id = tMusicFile.mScddbRecordingId;
		this.mStart_ms = 0;
		this.mEnd_ms = tMusicFile.mDuration;
		this.mVolume_factor = 1.f;
		mMajorText = "";
		mMinorText = "";



		// if only one dance is linked to the musicfile

	}

//	public Playinstruction(String tPath) {
//		MusicFile tMusicFile = null;
//		try {
//			tMusicFile = new MusicFile(tPath);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		if (tMusicFile != null) {
//			this.mMusicFile = tMusicFile;
//			grindMusicFile();
//			this.mScddbDance_id = tMusicFile.mScddbDanceId;
//			this.mStart_ms = 0;
//			this.mEnd_ms = tMusicFile.mDuration;
//			this.mVolume_factor = 1.f;
//		}
//
//		mMajorText = "";
//		mMinorText = "";
//	}


	//<editor-fold desc="Setter and Getter">
	public int getId() {
		return mId;
	}

	public void setId(int tId) {
		mId = tId;
	}


//	public DocumentFile getDocumentFile() {
//		if (mMusicFile == null) {
//			mMusicFile = MusicFile.getById(mMusicFile_Id);
//		}
//		return mMusicFile.mDocumentFile;
//	}

	public int getMusicFile_Id() {
		return mMusicFile_Id;
	}

	public void setMusicFile_Id(int tMusicFile_Id) {
		if (mMusicFile_Id != tMusicFile_Id) {
			mMusicFile_Id = tMusicFile_Id;
			mMusicFile = null;
		}
	}

	public void setMusicFile(MusicFile tMusicFile) {
		mMusicFile = tMusicFile;
		grindMusicFile();
	}

	public MusicFile getMusicFile() {
		readMusicFile();
		return mMusicFile;
	}


	//<editor-fold desc="ScddbDance">
	public int getScddbDance_Id() {
		if (mScddbDance_Id == 0) {
			if (mMusicFile == null) {
				return 0;
			}
			mScddbDance_Id = mMusicFile.mDanceId;
		}
		return mScddbDance_Id;
	}

	public void setScddbDance_Id(int tScddbDance_id) {
		mScddbDance_Id = tScddbDance_id;
	}

	public void setScddbDance(Dance iScddbDance) {
		if (iScddbDance != null) {
			mScddbDance = iScddbDance;
			mScddbDance_Id = iScddbDance.mId;

		}
	}

	public Dance getScddbDance() {
		if (mScddbDance == null && mScddbDance_Id > 0) {
			try {
				mScddbDance = new Dance(mScddbDance_Id);
			} catch (Exception e) {
				Logg.e(TAG, e.toString());
			}
		}
		return mScddbDance;
	}
	//</editor-fold>

	//<editor-fold desc="Recording">
	public int getScddbRecording_id() {
		return mScddbRecording_id;
	}

	public void setScddbRecording_id(int tScddbRecording_id) {
		mScddbRecording_id = tScddbRecording_id;
	}

	public void setRecording(Recording iRecording) {
		mRecording = iRecording;
		mScddbRecording_id = mRecording.mId;
	}

	public Recording getRecording() {
		return mRecording;
	}
	//</editor-fold>

	public long getStart_ms() {
		return mStart_ms;
	}

	public void setStart_ms(long tStart_ms) {
		mStart_ms = tStart_ms;
	}

	public long getEnd_ms() {
		return mEnd_ms;
	}

	public void setEnd_ms(long tEnd_ms) {
		mEnd_ms = tEnd_ms;
	}

	public float getVolume_factor() {
		return mVolume_factor;
	}

	public void setVolume_factor(float tVolume_factor) {
		mVolume_factor = tVolume_factor;
	}


	//</editor-fold>


	@NonNull
	@Override
	public String toString() {
		return "Playinstruction{" +
				"mId=" + mId +
				", mMusicFile=" + mMusicFile +
				'}';
	}


	private void readMusicFile() {
		if (mMusicFile == null) {
			mMusicFile = MusicFile.getById(mMusicFile_Id);
			grindMusicFile();
		}
	}

	/**
	 * copy the redundant information from MusicFile to the extra attributes
	 */
	private void grindMusicFile() {
		mT2 = mMusicFile.mT2;
		mShortname = mMusicFile.mName;
		//mShortname = getShortMajorText();
		//mSignature = mMusicFile.getSignature();
		mDuration = mMusicFile.mDuration;
		mMusicFile_Id = mMusicFile.mId;
	}


//	public String getMajorText() {
//		if (mMajorText != null && !mMajorText.isEmpty()) {
//			return mMajorText;
//		}
//
//		boolean tFoundIt = false;
//		String result = "";
//		if (mScddbDance_Id != 0) {
//			if (mScddbDance == null) {
//				try {
//					mScddbDance = new Dance(mScddbDance_Id);
//				} catch (Exception e) {
//					Logg.e(TAG, e.toString());
//					result = "???";
//				}
//			}
//			if (mScddbDance != null) {
//				result = mScddbDance.mName;
//				tFoundIt = true;
//			}
//		} else if (mMusicFile != null) {
//			result = mMusicFile.getNiceName();
//			tFoundIt = true;
//		} else if (mMusicFile_Id > 0) {
//			mMusicFile = MusicFile.getById(mMusicFile_Id);
//			result = Objects.requireNonNull(mMusicFile).getNiceName();
//			tFoundIt = true;
//		} else {
//			result = "===";
//		}
//		Logg.i(TAG, "result" + result);
//		if (tFoundIt)
//
//		{
//			mMajorText = result;
//		}
//		return result;
//	}


//	public String getShortMajorText() {
//		String t = getMajorText();
//		int e = -1;
//		// remove any signature
//		Pattern tPattern = Pattern.compile("[ -]*[JWMRS][0-9]+x");
//		Matcher tMatcher = tPattern.matcher(t);
//		if (tMatcher.find()) {
//			e = tMatcher.start();
//		}
//
//		if (e != -1) {
//			t = t.substring(0, e);
//		}
//		return t;
//	}


	public String getMinorText() {
		if (!mMinorText.isEmpty()) {
			return mMinorText;
		}
		String result = "";
		boolean tFoundIt = false;


		if (mScddbRecording_id != 0) {
			if (mRecording == null) {
				try {
					mRecording = new Recording(mScddbRecording_id);
				} catch (Exception e) {
					Logg.e(TAG, e.toString());
				}
			}
			if (mRecording != null) {
				result = mRecording.mAlbumArtistName;
				tFoundIt = true;
			}
		} else if (mMusicFile != null) {
			result = mMusicFile.mT2;
			tFoundIt = true;
		} else if (mMusicFile_Id > 0) {
			mMusicFile = MusicFile.getById(mMusicFile_Id);
			result = Objects.requireNonNull(mMusicFile).mT2;
			tFoundIt = true;
		} else {
			result = "===";
		}
		if (tFoundIt) {
			mMinorText = result;
		}
		return result;
	}




	public void delete() {
		Logg.d(TAG, "delete");
	//	Playinstruction_DB.delete(this);
	}

	public Playinstruction save() {
		WriteCall tWriteCall = new WriteCall(Playinstruction.class, this);
		if (mId <= 0) {
			mId = tWriteCall.insert();
		} else {
			tWriteCall.update();
		}
		return this;
	}

	public ContentValues getContentValues() {
		ContentValues tContentValues = new ContentValues();
		tContentValues.put("MUSICFILE_ID", getMusicFile_Id());
		tContentValues.put("RECORDING_ID", getScddbRecording_id());
		tContentValues.put("DANCE_ID", getScddbDance_Id());
		tContentValues.put("VOLUME_FACTOR", getVolume_factor());
		tContentValues.put("START_MS", getStart_ms());
		tContentValues.put("END_MS", getEnd_ms());
		tContentValues.put("PLAYLIST_ID", getId());
		tContentValues.put("PLAYLIST_POS", mPos);
		return tContentValues;
	}


	public static Playinstruction convertCursor(Cursor tCursor) {
		Playinstruction tPlayinstruction;

		String tText;
		tText = tCursor.getString(tCursor.getColumnIndex("DANCE_NAME"));
		Logg.i(TAG, tText);
		if (tText.equals("")) {
			tText = tCursor.getString(tCursor.getColumnIndex("MF_NAME"));
			Logg.i(TAG, tText);

		}
		String tShortname;

		int e = -1;
		Pattern tPattern = Pattern.compile("[ -]*[JWMRS][0-9]+x");
		Matcher tMatcher = tPattern.matcher(tText);
		if (tMatcher.find()) {
			e = tMatcher.start();
		}
		if (e != -1) {
			tShortname = tText.substring(0, e);
		} else {
			tShortname = tText;
		}

		String tSignature = "";
		//noinspection UnusedAssignment
		tText = tCursor.getString(tCursor.getColumnIndex("MF_NAME"));
		String tSignaturePattern = "abc";
//		String tSignaturePattern = Signature.getPattern();
//		tPattern = Pattern.compile(tSignaturePattern);
//		tMatcher = tPattern.matcher(tText);
//		if (tMatcher.matches()) {
//			tSignature = tMatcher.group(2);
//		}


		tPlayinstruction = new Playinstruction(
				tCursor.getInt(tCursor.getColumnIndex("ID")),
				tCursor.getInt(tCursor.getColumnIndex("MUSICFILE_ID")),
				tShortname,
				tCursor.getString(tCursor.getColumnIndex("T2")),
				tSignature,
				tCursor.getInt(tCursor.getColumnIndex("DURATION")),

				tCursor.getInt(tCursor.getColumnIndex("RECORDING_ID")),
				tCursor.getInt(tCursor.getColumnIndex("DANCE_ID")),
				tCursor.getFloat(tCursor.getColumnIndex("VOLUME_FACTOR")),
				tCursor.getLong(tCursor.getColumnIndex("START_MS")),
				tCursor.getLong(tCursor.getColumnIndex("END_MS")),
				tCursor.getInt(tCursor.getColumnIndex("PLAYLIST_POS")));

		MusicFile tMusicFile = new MusicFile(tPlayinstruction.getMusicFile_Id(),
				tCursor.getString(tCursor.getColumnIndex("MF_PATH")),
				tCursor.getString(tCursor.getColumnIndex("MF_NAME")),
				tCursor.getString(tCursor.getColumnIndex("T2")),
				tCursor.getString(tCursor.getColumnIndex("T1")),
				tCursor.getInt(tCursor.getColumnIndex("MF_MEDIA_ID")),
				tCursor.getInt(tCursor.getColumnIndex("MF_RECORDING_ID"))
		);
		tPlayinstruction.setMusicFile(tMusicFile);


		return tPlayinstruction;
	}

	
	public boolean equal(Playinstruction iPlayinstruction) {
		return false;
	}

}
