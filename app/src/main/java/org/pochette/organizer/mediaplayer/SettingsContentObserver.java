package org.pochette.organizer.mediaplayer;

import android.content.Context;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.os.Handler;

import org.pochette.utils_lib.shouting.Shout;
import org.pochette.utils_lib.shouting.Shouting;


/**
 * SettingsContentObserver extends ContentObserver to monitor Audiosettings and connects them to the shouting ladder
 */
public class SettingsContentObserver extends ContentObserver {

	//Variables
	@SuppressWarnings("unused")
	private final String TAG = "FEHA (SettingsContentObserver)";
	int previousVolume;
	AudioManager mAudioManager;
	Shouting mShouting;
	Shout mShoutToCeiling;

	//Constructor
	public SettingsContentObserver(Context iContext, Handler iHandler, Shouting iShouting) {
		super(iHandler);
		mShouting = iShouting;
		mAudioManager = (AudioManager) iContext.getSystemService(Context.AUDIO_SERVICE);
		if (mAudioManager != null) {
			previousVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		}
		mShoutToCeiling = new Shout("SettingsContentObserver");
		mShoutToCeiling.mLastObject = "Volume/STREAM_MUSIC";
		mShoutToCeiling.mLastAction = "notified";
	}

	//Setter and Getter
	//Livecycle
	//Static Methods
	//Internal Organs
	//Interface

	/**
	 * This method wraps read the Volume from the AudioManager and feeds it into the shouting ladder.
	 * @param selfChange True if this is a self-change notification.
	 */
	@Override
	public void onChange(boolean selfChange) {
		super.onChange(selfChange);
		mShouting.shoutUp(mShoutToCeiling);
	}
}


