package org.pochette.data_library.playlist;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.net.Uri;

import org.pochette.data_library.database_management.DeleteCall;
import org.pochette.data_library.database_management.SearchCall;
import org.pochette.data_library.database_management.WriteCall;
import org.pochette.data_library.music.MusicFile;
import org.pochette.data_library.scddb_objects.Dance;
import org.pochette.data_library.scddb_objects.Recording;
import org.pochette.utils_lib.logg.Logg;
import org.pochette.utils_lib.search.SearchCriteria;
import org.pochette.utils_lib.search.SearchPattern;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;

/**
 * The class PlayInstruction contains the instruction to play one item
 * The tool executing the instruction in DiskJockey
 * Usually PlayInstructions are combined into a Playlist
 */
public class Playinstruction {

	static final String TAG = "FEHA (PlayInstruction)";

	int mId;
	String mT2;
	int mDuration;
	String mShortname;
	int mMusicFile_Id;
	private MusicFile mMusicFile;
	int mDance_Id;
	private Dance mDance;
	int mRecording_id;
	private Recording mRecording;
	long mStart_ms;
	long mEnd_ms;
	float mVolume_factor;

	public Uri mUri;
	private int mPlaylist_Id;
	private int mPlaylist_Position;


	public Playinstruction(int tId, MusicFile tMusicFile, int tRecording_id, int tDance_id,
						   float tVolume_factor, long tStart_ms, long tEnd_ms,
						   int iPlaylist_id, int iPlaylist_Position) {
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
		mDance_Id = tDance_id;
		mRecording_id = tRecording_id;
		mVolume_factor = tVolume_factor;
		mStart_ms = tStart_ms;
		mEnd_ms = tEnd_ms;
		mPlaylist_Id = iPlaylist_id;
		mPlaylist_Position = iPlaylist_Position;
	}

	public Playinstruction(MusicFile tMusicFile) throws IllegalArgumentException {
		if (tMusicFile == null) {
			throw new IllegalArgumentException("no Musicfile provided");
		}
		this.mMusicFile = tMusicFile;
		grindMusicFile();
		this.mDance_Id = tMusicFile.mDanceId;
		this.mRecording_id = tMusicFile.mRecordingId;
		this.mStart_ms = 0;
		this.mEnd_ms = tMusicFile.mDuration;
		this.mVolume_factor = 1.f;
	}

	//<editor-fold desc="Setter and Getter">
	public int getId() {
		return mId;
	}

	public void setId(int tId) {
		mId = tId;
	}

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

	//<editor-fold desc="Dance">
	public int getDance_Id() {
		if (mDance_Id == 0) {
			if (mMusicFile == null) {
				return 0;
			}
			mDance_Id = mMusicFile.mDanceId;
		}
		return mDance_Id;
	}

	public void setDance_Id(int tDance_id) {
		mDance_Id = tDance_id;
	}

	public void setDance(Dance iDance) {
		if (iDance != null) {
			mDance = iDance;
			mDance_Id = iDance.mId;
		}
	}

	public Dance getDance() {
		if (mDance == null && mDance_Id > 0) {
			try {
				mDance = new Dance(mDance_Id);
			} catch (Exception e) {
				Logg.e(TAG, e.toString());
			}
		}
		return mDance;
	}
	//</editor-fold>

	//<editor-fold desc="Recording">
	public int getRecording_id() {
		return mRecording_id;
	}

	public void setRecording_id(int tRecording_id) {
		mRecording_id = tRecording_id;
	}

	public void setRecording(Recording iRecording) {
		mRecording = iRecording;
		mRecording_id = mRecording.mId;
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

	public int getPlaylist_Id() {
		return mPlaylist_Id;
	}

	public void setPlaylist_Id(int playlist_Id) {
		mPlaylist_Id = playlist_Id;
	}

	public int getPlaylist_Position() {
		return mPlaylist_Position;
	}

	public void setPlaylist_Position(int playlist_Position) {
		mPlaylist_Position = playlist_Position;
	}

//</editor-fold>


	@NonNull
	@Override
	public String toString() {
		return "Playinstruction{" +
				"mId=" + mId +
				", mMusicFile=" + mMusicFile +
				", Position =" + mPlaylist_Position +
				", of PL=" + mPlaylist_Id +
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
		mDuration = mMusicFile.mDuration;
		mMusicFile_Id = mMusicFile.mId;
	}

	public static Playinstruction getPlayinstruction(MusicFile iMusicFile, Dance iDance) throws IllegalArgumentException {
		Playinstruction tPlayinstruction;
		if (iMusicFile == null && iDance == null) {
			throw new IllegalArgumentException("Musicfile or Dance is required");
		}
		if (iMusicFile != null) {
			tPlayinstruction = new Playinstruction(iMusicFile);
		} else if (iDance != null) {
			MusicFile tMusicFile = iDance.getMusicFile();
			if (tMusicFile == null) {
				Logg.i(TAG, "could not identify Music for Dance " + iDance.toShortString());
				return null;
			}
			tPlayinstruction = new Playinstruction(tMusicFile);
		} else {
			throw new RuntimeException("Musicfile or Dance must proviced for executeAddToDefault");
		}
		if (tPlayinstruction != null && iDance != null) {
			tPlayinstruction.setDance(iDance);
		}
		return tPlayinstruction;
	}


	public void delete() {
		Logg.i(TAG, "delete");
		DeleteCall tDeleteCall = new DeleteCall(Playinstruction.class, this);
	}

	public Playinstruction save() {
		Logg.i(TAG, "Save" + toString());
		WriteCall tWriteCall = new WriteCall(Playinstruction.class, this);
		if (mId <= 0) {
			try {
				mId = tWriteCall.insert();
			} catch(SQLiteConstraintException e) {
				// duplicate key requires an update statement
				// just to read the id, we select first
				SearchPattern tSearchPattern;
				SearchCall tSearchCall;
				tSearchPattern = new SearchPattern(Playinstruction.class);
				tSearchPattern.addSearch_Criteria(new SearchCriteria("PLAYLIST_ID", "" + mPlaylist_Id));
				tSearchPattern.addSearch_Criteria(new SearchCriteria("PLAYLIST_POS", "" + mPlaylist_Position));
				tSearchCall = new SearchCall(Playinstruction.class, tSearchPattern, null);
				Playinstruction tPlayinstruction = tSearchCall.produceFirst();
				mId = tPlayinstruction.mId;
				tWriteCall.update();
			}
		} else {
			tWriteCall.update();
		}
		return this;
	}

	public ContentValues getContentValues() {
		ContentValues tContentValues = new ContentValues();
		tContentValues.put("MUSICFILE_ID", getMusicFile_Id());
		tContentValues.put("RECORDING_ID", getRecording_id());
		tContentValues.put("DANCE_ID", getDance_Id());
		tContentValues.put("VOLUME_FACTOR", getVolume_factor());
		tContentValues.put("START_MS", getStart_ms());
		tContentValues.put("END_MS", getEnd_ms());
		Logg.i(TAG, "playlist id" + mPlaylist_Id);
		tContentValues.put("PLAYLIST_ID", mPlaylist_Id);
		tContentValues.put("PLAYLIST_POS", mPlaylist_Position);
		return tContentValues;
	}

	public static Playinstruction convertCursor(Cursor tCursor) {
		MusicFile tMusicFile = MusicFile.convertCursor(tCursor);
		Playinstruction tPlayinstruction = new Playinstruction(
				tCursor.getInt(tCursor.getColumnIndex("ID")),
				tMusicFile,
				tCursor.getInt(tCursor.getColumnIndex("RECORDING_ID")),
				tCursor.getInt(tCursor.getColumnIndex("DANCE_ID")),
				tCursor.getFloat(tCursor.getColumnIndex("VOLUME_FACTOR")),
				tCursor.getLong(tCursor.getColumnIndex("START_MS")),
				tCursor.getLong(tCursor.getColumnIndex("END_MS")),
				tCursor.getInt(tCursor.getColumnIndex("PLAYLIST_ID")),
				tCursor.getInt(tCursor.getColumnIndex("PLAYLIST_POS")));
		return tPlayinstruction;
	}


}
