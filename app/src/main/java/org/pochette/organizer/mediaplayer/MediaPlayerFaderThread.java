package org.pochette.organizer.mediaplayer;


import org.pochette.utils_lib.logg.Logg;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * this class allows fade in and fade out
 */

@SuppressWarnings("unused")
class MediaPlayerFaderThread extends Thread {
    private final String TAG = "FEHA (MPFaderThread)";
    public static final String THREAD_NAME = "MediaPlayerFaderThread";

    //Variables
    private final float mStandardVolume = 1.f;
    private final float mDeltaVolume = 0.025f;
    private final float mFadeTime = 1.0f; // seconds
    private final int mSleepTime = Math.round(mFadeTime * 1000.f /
            (mStandardVolume / mDeltaVolume)); // millicsenods
    private float mCurrentVolume = mStandardVolume;
    private String mCurrentDirection;
    private boolean mFadeIsRunning = false; // this tracks, whether currently a fading is active
    private final MediaPlayerStateful mMediaPlayerStateful;

    private String mFinishAction = "";
    private String mStartAction = "";

    //Constructor
    MediaPlayerFaderThread(MediaPlayerStateful iMediaPlayerStateful) {
        super(THREAD_NAME);
        mMediaPlayerStateful = iMediaPlayerStateful;
        mCurrentDirection = "";
        // ensure only one thread running on  this MediaPlayer
        for (Thread lThread : Thread.getAllStackTraces().keySet()) {
            if (lThread.getName().equals(THREAD_NAME)) {
                if (lThread instanceof MediaPlayerFaderThread) {
                    MediaPlayerFaderThread lMediaPlayerFaderThread =
                            (MediaPlayerFaderThread) lThread;
                    if (lMediaPlayerFaderThread.mMediaPlayerStateful.equals(iMediaPlayerStateful)) {
                        Logg.i(TAG, "interrupt old: " + lMediaPlayerFaderThread.getId());
                        Logg.i(TAG, "keep running: " + this.getId());
                        lThread.interrupt();
                    }
                }
            }
        }
    }

    public MediaPlayerStateful getMediaPlayerStateful() {
        return mMediaPlayerStateful;
    }

    public float getCurrentVolume() {
        return mCurrentVolume;
    }

    @Override
    public void run() {
        while (!interrupted()) {
            if (mMediaPlayerStateful != null) {
                switch (mCurrentDirection) {
                    case "up":
                        mCurrentVolume = min(mStandardVolume, mCurrentVolume + mDeltaVolume);
                        mMediaPlayerStateful.setVolume(mCurrentVolume);
                        if (mCurrentVolume >= mStandardVolume) {
                            mFadeIsRunning = false;
                        }
                        break;
                    case "down":
                        mCurrentVolume = max(0, mCurrentVolume - mDeltaVolume);
                        mMediaPlayerStateful.setVolume(mCurrentVolume);
                        if (mCurrentVolume == 0) {
                            mFadeIsRunning = false;
                        }
                        break;
                    case "full":
                        mCurrentVolume = mStandardVolume;
                        mMediaPlayerStateful.setVolume(mCurrentVolume);
                        mFadeIsRunning = false;
                        break;
                    case "quiet":
                        mCurrentVolume = 0;
                        mMediaPlayerStateful.setVolume(mCurrentVolume);
                        mFadeIsRunning = false;
                        break;
                    default:
                    case "":
                        mFadeIsRunning = false;
                        break;
                }

                if (mStartAction != null && !mStartAction.isEmpty()) {
                    if (mStartAction.equals("play")) {
                        mMediaPlayerStateful.start();
                    }
                    mStartAction = "";
                }
                if (!mFadeIsRunning && mFinishAction != null && !mFinishAction.isEmpty()) {
                    if (mFinishAction.equals("pause")) {
                        mMediaPlayerStateful.pause();
                    }
                    mFinishAction = "";
                }
                if (!mFadeIsRunning) {
                    mCurrentDirection = "";
                }
            }
            try {
                //noinspection BusyWait
                Thread.sleep(mSleepTime);
            } catch(InterruptedException e) {
                Logg.w(TAG, e.toString());
                this.interrupt();
            }
        }
    }

    //Setter and Getter

    void setFullAndPlay() {
        mFadeIsRunning = true;
        mStartAction = "play";
        mFinishAction = "";
        this.mCurrentDirection = "full";
        mCurrentVolume = mStandardVolume;
        //mFEHA_MediaPlayer.setVolume(mCurrentVolume);
        Logg.i(TAG, "setFullAndPlay");
    }

    void setFadeInAndPlay() {
        mCurrentVolume = mMediaPlayerStateful.getVolume();
        mFadeIsRunning = true;
        if (mMediaPlayerStateful.isPlaying()) {
            mStartAction = "";
        } else {
            mStartAction = "play";
        }
        mFinishAction = "";
        this.mCurrentDirection = "up";
        Logg.i(TAG, "setFadeInAndPlay");
    }

    void setFadeOutAndPause() {
        mCurrentVolume = mMediaPlayerStateful.getVolume();
        mFadeIsRunning = true;
        mStartAction = "";
        mFinishAction = "pause";
        this.mCurrentDirection = "down";
        Logg.i(TAG, "setFadeOutAndPause");
    }

    void setQuietAndPause() {
        mFadeIsRunning = true;
        mStartAction = "";
        mFinishAction = "pause";
        this.mCurrentDirection = "quiet";
        Logg.i(TAG, "setQuietAndPause");
    }


    //Livecycle
    //Static Methods
    //Internal Organs
    //Interface
}
