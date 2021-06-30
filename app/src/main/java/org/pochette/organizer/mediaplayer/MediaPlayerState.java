package org.pochette.organizer.mediaplayer;

/**
 * MediaPlayerState is a status field of FEHA_MediaPlayer.
 *
 * <p>
 * It is based on the state diagram of the Mediaplayer
 *
 *  0   MEDIA_PLAYER_STATE_ERROR    Error <br>
 *  1   MEDIA_PLAYER_IDLE   Idle<br>
 *  2   MEDIA_PLAYER_INITIALIZED    Initialized<br>
 *  4   MEDIA_PLAYER_PREPARING  Preparing<br>
 *  8   MEDIA_PLAYER_PREPARED   Prepared<br>
 *  16  MEDIA_PLAYER_STARTED    Started<br>
 *  32  MEDIA_PLAYER_PAUSED Paused<br>
 *  64  MEDIA_PLAYER_STOPPED    Stopped<br>
 *  128 MEDIA_PLAYER_PLAYBACK_COMPLETE  Playback Complete<br>
 *  256 END End<br>
 * </p>
 */

public enum MediaPlayerState {

//	ERROR("Error", "MEDIA_PLAYER_STATE_ERROR", 0 ),
//	IDLE("Idle", "MEDIA_PLAYER_IDLE", 1),
//	INITIALIZED("Initialized", "MEDIA_PLAYER_INITIALIZED",2),
//	PREPARING("Preparing", "MEDIA_PLAYER_PREPARING",4),
//	PREPARED("Prepared", "MEDIA_PLAYER_PREPARED",8),
//	STARTED("Started", "MEDIA_PLAYER_STARTED",16),
//	PAUSED("Paused", "MEDIA_PLAYER_PAUSED", 32),
//	STOPPED("Stopped", "MEDIA_PLAYER_STOPPED",64),
//	PLAYBACKCOMPLETE("Playback Complete", "MEDIA_PLAYER_PLAYBACK_COMPLETE",128),
//	END("End", "END", 256);


	ERROR("Error", 0 ),
	IDLE("Idle", 1),
	INITIALIZED("Initialized", 2),
	PREPARING("Preparing", 4),
	PREPARED("Prepared", 8),
	STARTED("Started", 16),
	PAUSED("Paused", 32),
	STOPPED("Stopped", 64),
	PLAYBACKCOMPLETE("Playback Complete", 128),
	END("End",  256);


	String mText;
//	private String mCode;
	int mState;

//	MediaPlayerState(String tText, String tCode,  int tState) {
//		mText = tText;
//		mCode = tCode;
//		mState = tState;
//	}


	MediaPlayerState(String tText, int tState) {
		mText = tText;
		mState = tState;
	}

//	public String getText() {
//		return mText;
//	}
//
//	public void setText(String tText) {
//		mText = tText;
//	}

//	public String getCode() {
//		return mCode;
//	}
//
//	public void setCode(String tCode) {
//		mCode = tCode;
//	}

//	public int getState() {
//		return mState;
//	}

//	public void setState(int tState) {
//		mState = tState;
//	}

//	public static MediaPlayerState fromCode(String tCode) {
//		for (MediaPlayerState b : MediaPlayerState.values()) {
//			if (b.mCode.equals(tCode)) {
//				return b;
//			}
//		}
//		return null;
//	}
}
