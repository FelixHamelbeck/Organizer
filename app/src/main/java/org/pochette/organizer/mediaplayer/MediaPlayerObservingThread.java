package org.pochette.organizer.mediaplayer;


import org.json.JSONException;
import org.json.JSONObject;
import org.pochette.organizer.BuildConfig;
import org.pochette.utils_lib.logg.Logg;
import org.pochette.utils_lib.shouting.Shout;
import org.pochette.utils_lib.shouting.Shouting;

import androidx.annotation.NonNull;


/**
 * MediaPlayerObservingThread implements Runnable
 * <p>
 * it retrieves the state, duration and position of a MediaPlayer and calls the shouting ladder
 * it reads data and communicates upwards, but no control is exersiced
 * <p>
 * Created by felix on 24.02.18.
 */
public class MediaPlayerObservingThread extends Thread {

    public static final String TAG = "FEHA (MPOThread)";
    public static final String THREAD_NAME = "MediaPlayerObservingThread";

    //Variables
    private final MediaPlayerStateful mMediaPlayerStateful;
    private static final int REFRESH_MS = 45;
    private MediaPlayerState mMediaPlayerState;
    //private boolean isOn;
    private Shouting mShouting;

    //Constructor

    /**
     * Constructor
     */
    public MediaPlayerObservingThread(MediaPlayerStateful iMediaPlayerStateful) {
        super(THREAD_NAME);
        if (iMediaPlayerStateful == null) {
            throw new IllegalArgumentException("MediaPlayerStateful required");
        }
        mMediaPlayerStateful = iMediaPlayerStateful;
        mMediaPlayerState = null;
    }


    @Override
    public void run() {
        try {
            Logg.i(TAG, "Start of run" + Thread.currentThread().getName() +
                    ":" + Thread.currentThread().getId());
            while (!Thread.currentThread().isInterrupted()) {
                Shout tShoutToCeiling;
                if (mMediaPlayerStateful != null) {
                    if (mMediaPlayerState == null ||
                            mMediaPlayerState != mMediaPlayerStateful.getMediaPlayer_State()) {
                         // Communicate a new status
                        if (mShouting != null) {
                            tShoutToCeiling = new Shout(MediaPlayerObservingThread.THREAD_NAME);
                            tShoutToCeiling.mLastObject = "State";
                            tShoutToCeiling.mLastAction = "change";
                            mShouting.shoutUp(tShoutToCeiling);
                            tShoutToCeiling.mJsonString = prepareJsonStringState();
                        }
                        mMediaPlayerState = mMediaPlayerStateful.getMediaPlayer_State();
                        if (BuildConfig.DEBUG) {
                            Logg.d(TAG, "new State of MediaPlayer: " + mMediaPlayerState.mText);
                        }
                    }
                    if (mMediaPlayerStateful.isPlaying()) {
                        // Communicate position
                        if (mShouting != null) {
                            tShoutToCeiling = new Shout(MediaPlayerObservingThread.THREAD_NAME);
                            tShoutToCeiling.mLastObject = "Position";
                            tShoutToCeiling.mLastAction = "communicate";
                            tShoutToCeiling.mJsonString = prepareJsonStringPosition();
                            mShouting.shoutUp(tShoutToCeiling);
                        }
                    }
                }
                //noinspection BusyWait
                Thread.sleep(REFRESH_MS);
            }
        } catch(Exception e) {
            Logg.w(TAG, "in run: " + e.toString());
        }
    }

    /**
     *
     * @return the Json String describing the position and duration
     */
    private String prepareJsonStringPosition() {
        JSONObject tJson = new JSONObject();
        String output = "";
        if (mMediaPlayerStateful.isPlaying()) {
            try {
                tJson.put("CurrentPosition", mMediaPlayerStateful.getCurrentPosition());
                tJson.put("Duration", mMediaPlayerStateful.getDuration());
                output = tJson.toString();
            } catch(JSONException e) {
                Logg.e(TAG, e.toString());
            }
        }
        return output;
    }

    /**
     *
     * @return the Json String describing the state change
     */
    private String prepareJsonStringState() {
        JSONObject tJson = new JSONObject();
        String output = "";
        if (mMediaPlayerStateful != null) {
            try {
                if (mMediaPlayerState == null) {
                    tJson.put("OldState", "");
                } else {
                    tJson.put("OldState", mMediaPlayerState.name());
                }
                tJson.put("NewState", mMediaPlayerStateful.getMediaPlayer_State().name());
                output = tJson.toString();
            } catch(JSONException e) {
                Logg.e(TAG, e.toString());
            }
        }
        return output;
    }

    //Interface

    /**
     * Define the receiver
     *
     * @param tShouting interface implemenation to receive the shouts
     */
    public void setShouting(Shouting tShouting) {
        mShouting = tShouting;
    }

    /**
     * Returns a string representation of this thread, including the
     * thread's name, priority, and thread group.
     *
     * @return a string representation of this thread.
     */
    @NonNull
    @Override
    public String toString() {
        return super.toString() + "[" + getId() + "]";
    }


}
