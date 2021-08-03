package org.pochette.organizer.mediaplayer;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v4.media.session.MediaSessionCompat;

import org.json.JSONException;
import org.json.JSONObject;
import org.pochette.organizer.BuildConfig;
import org.pochette.organizer.app.MyPreferences;
import org.pochette.organizer.gui.MediaSessionService;
import org.pochette.utils_lib.logg.Logg;
import org.pochette.utils_lib.shouting.Shout;
import org.pochette.utils_lib.shouting.Shouting;

import java.io.IOException;
import androidx.annotation.NonNull;
import androidx.media.MediaSessionManager;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * Exposes the functionality of the {@link MediaPlayer} and
 * wraps a some functionality around it
 * - State: all the stati of the status diagram are covered
 * - For all transition, they are checked v this state
 * - Additional information is tracked: volume
 * - All Callbacks from Mediaplayer are trapped here:
 * --- OnCompletionListener
 * --- OnErrorListener
 * --- OnPreparedListener
 * - All Callback upstairs are mapped to ShoutUp
 * <p>
 * It is not a singleton, sp multiple instances of MediaPlayerStateful and wrap their own MediaPlayer each
 *
 * <p>
 * MediaPlayerState is tracked
 * <p>
 * Some methods like getPosition etc should not be called during preparation, so this is taken care of as well
 */
@SuppressWarnings("unused")
public class MediaPlayerStateful
        implements
        Shouting,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnSeekCompleteListener {


    //Variables
    static final String TAG = "FEHA (MPStateful)";

    private MediaPlayer mMediaPlayer;
    private MediaPlayerState mMediaPlayer_State; // The state of the MediaPlayer as tracked in this class
    boolean mPlayPending = false; // if during preparation the method run is called, this flag turns true
    private boolean mCommandPossible = false; // true if the underlying MediaPlayer can accept commands like seekTo, setVolume
    private boolean mUpdatePostponed = false;

    private float mVolume;
    private float mSpeed;
    private int mTargetPosition;
    private boolean mLooping;
    private String mArtist;
    private String mAlbum;
    private String mTitle;


    private MediaPlayerObservingThread mMediaPlayerObservingThread;
    private MediaPlayerFaderThread mMediaPlayerFaderThread;
    private Shouting mShouting;
    private Shout mGlassFloor;



    //Constructor
    public MediaPlayerStateful() {
        setStatus(MediaPlayerState.END);
        mSpeed = 1.0f;
        mVolume = 0.5f;
    }

    //<editor-fold desc="Setter and Getter">
    void setVolume(float iVolume) {
        mVolume = min(max(0.f, iVolume), 1.f);
        if (mMediaPlayer == null) {
            return;
        }
        if (mCommandPossible) {
            mMediaPlayer.setVolume(mVolume, mVolume);
        } else {
            mUpdatePostponed = true;
        }
    }

    float getVolume() {
        return mVolume;
    }

    void setPosition(int tMillisec) {
        if (mCommandPossible) {
            this.seekTo(max(0, min(tMillisec, mMediaPlayer.getDuration())));
        } else {
            mUpdatePostponed = true;
            mTargetPosition = tMillisec;
        }
    }

    int getCurrentPosition() {
        if (mCommandPossible) {
            return mMediaPlayer.getCurrentPosition();
        } else {
            return 0;
        }
    }

    int getDuration() {
        if (mCommandPossible) {
            return mMediaPlayer.getDuration();
        } else {
            return 0;
        }
    }

    void setSpeed(float speed) {
        mSpeed = speed;
        if (mMediaPlayer == null) {
            return;
        }
        if (mCommandPossible) {
            mMediaPlayer.setPlaybackParams(mMediaPlayer.getPlaybackParams().setSpeed(mSpeed));
        } else {
            mUpdatePostponed = true;
        }
    }

    float getSpeed() {
        return mSpeed;
    }

    void setLooping(boolean tFlag) {
        if (mMediaPlayer != null) {
            mLooping = tFlag;
            if (mCommandPossible) {
                mMediaPlayer.setLooping(mLooping);
            } else {
                mUpdatePostponed = true;
            }
        }
    }

    boolean getLooping() {
        if (mCommandPossible) {
            return mMediaPlayer.isLooping();
        } else {
            return false;
        }
    }

    String getArtist() {
        if (mMediaPlayer == null) {
            return "";
        } else {
            return mArtist;
        }
    }

    String getAlbum() {
        if (mMediaPlayer == null) {
            return "";
        } else {
            return mAlbum;
        }
    }

    String getTitle() {
        if (mMediaPlayer == null) {
            return "";
        } else {
            return mTitle;
        }
    }

    MediaPlayerState getMediaPlayer_State() {
        return mMediaPlayer_State;
    }

    //</editor-fold>

    public void initializeMediaPlayer(MediaPlayer iMockedMediaPlayer) {
        mCommandPossible = false;
        boolean tFirstTime = false;
        if (mMediaPlayer == null || iMockedMediaPlayer != null) {
            tFirstTime = true;
        }
        if (iMockedMediaPlayer != null) {
            mMediaPlayer = iMockedMediaPlayer;
        } else if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
        }
        if (tFirstTime) {
            // the MediaPlayserStateful class is the listener to all callbacks from MediaPlayer
            mMediaPlayer.setOnCompletionListener(this);
            mMediaPlayer.setOnErrorListener(this);
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setOnSeekCompleteListener(this);
        }
        // Mediaplayer finished, create the MediaPlayerObservingThread
        if (mMediaPlayerObservingThread == null) {
            mMediaPlayerObservingThread = new MediaPlayerObservingThread(this);
            mMediaPlayerObservingThread.setShouting(this);
            mMediaPlayerObservingThread.start();
        }
        if (!mMediaPlayerObservingThread.isAlive()) {
            mMediaPlayerObservingThread.setShouting(this);
            mMediaPlayerObservingThread.start();
        }
        if (mMediaPlayerFaderThread == null) {
            mMediaPlayerFaderThread = new MediaPlayerFaderThread(this);
            mMediaPlayerFaderThread.start();
        }


    }


    /**
     * while command was disabled the position, volume, speed and looping was not communicated
     * after quarry.start it can enabled and all the stored information should be communicated
     * Note: setSpeed performs a quarry.start() as well,
     * hence the call to enableInfo should be done after calling quarry.start() yourself
     * to avoid accidental start
     */
    void enableCommand() {
        if (!mCommandPossible) {
            mCommandPossible = true;
            // catch up, as we might have missed these updates
            if (BuildConfig.DEBUG) {
                Logg.d(TAG, "catch up on: SeekTo, setVolume, setPlaybackParams, setLooping");
            }
            if(mUpdatePostponed) {
                this.seekTo(max(0, min(mTargetPosition, mMediaPlayer.getDuration())));
                mMediaPlayer.setVolume(mVolume, mVolume);
                mMediaPlayer.setPlaybackParams(mMediaPlayer.getPlaybackParams().setSpeed(mSpeed));
                mMediaPlayer.setLooping(mLooping);
                mUpdatePostponed = false;
            }
        }
    }

    void setShouting(Shouting iShouting) {
        mShouting = iShouting;
    }

    boolean isPlaying() {
        try {
            if (mMediaPlayer == null) {
                return false;
            }
            if (!mMediaPlayer.isPlaying()) {
                return false;
            }
            if (mMediaPlayer_State != MediaPlayerState.STARTED) {
                return false;
            }
        } catch(IllegalStateException e) {
            Logg.e(TAG, e.toString());
            return false;
        }
        return true;
    }

    //Livecycle

    //<editor-fold desc="State Diagram Transitions">
    // One additional posibility: if STATE=Perparing and MediaPlayerStateful.run is called,
    // this is possible and the run is pending the onPrepared Callback

    void setStatus(MediaPlayerState iMediaPlayer_State) {
        mMediaPlayer_State = iMediaPlayer_State;
    }


    void reset() {
        if (mMediaPlayer != null) {
            mCommandPossible = false;
            Logg.i(TAG, "Call MediaPlayer.reset");
            mMediaPlayer.reset();
            setStatus(MediaPlayerState.IDLE);
        }
        notifyLivestep();
    }

    public void setDataSource(@NonNull Context iContext, Uri iUri) {
        Logg.i(TAG, "MPS.setDataSource");
        //noinspection ConstantConditions
        if (iContext == null) {
            throw new RuntimeException("Context musst be available to play Uri");
        }

        if (mMediaPlayer != null) {
            mCommandPossible = false;
            if (mMediaPlayer_State == MediaPlayerState.IDLE) {
                try {
                    mMediaPlayer.setDataSource(iContext, iUri);
                } catch(IOException e) {
                    Logg.e(TAG, e.toString());
                    return;
                }
                setStatus(MediaPlayerState.INITIALIZED);
            }
            MediaMetadataRetriever tMediaMetadataRetriever = new MediaMetadataRetriever();
            tMediaMetadataRetriever.setDataSource(iContext, iUri);
            mArtist = tMediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            mAlbum = tMediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
            mTitle = tMediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        }
        notifyLivestep();
    }

    void prepare() {
        Logg.i(TAG, "MPS.prepare");
        if (mMediaPlayer != null) {
            mCommandPossible = false;
            if (mMediaPlayer_State == MediaPlayerState.INITIALIZED ||
                    mMediaPlayer_State == MediaPlayerState.STOPPED) {
                try {
                    Logg.i(TAG, "Call MediaPlayer.prepare");
                    mMediaPlayer.prepare();
                } catch(IOException e) {
                    e.printStackTrace();
                    return;
                }
                setStatus(MediaPlayerState.PREPARED);
            }
        }
        notifyLivestep();
    }

    void prepareAsync() {
        Logg.i(TAG, "MPS.prepareAsncy");
        if (mMediaPlayer != null) {
            mCommandPossible = false;
            if (mMediaPlayer_State == MediaPlayerState.INITIALIZED ||
                    mMediaPlayer_State == MediaPlayerState.STOPPED) {
                //mTimeStartPrepareAsync = new Date();
                Logg.i(TAG, "Call MediaPlayer.prepareAsync");
                mMediaPlayer.prepareAsync();
                setStatus(MediaPlayerState.PREPARING);
            }
        }
        notifyLivestep();
    }

    void start() {
        Logg.i(TAG, "MPS.run");
        if (mMediaPlayer != null) {
            if (mMediaPlayer_State == MediaPlayerState.PREPARED ||
                    mMediaPlayer_State == MediaPlayerState.PAUSED ||
                    mMediaPlayer_State == MediaPlayerState.STARTED ||
                    mMediaPlayer_State == MediaPlayerState.PLAYBACKCOMPLETE) {
                mPlayPending = false;
                this.setSpeed(mSpeed);
                mMediaPlayer.start();
                enableCommand();
                setStatus(MediaPlayerState.STARTED);
            } else {
                // mPlayPending = (mMediaPlayer_State == MediaPlayerState.PREPARING);
                mPlayPending = true;
            }
        }
        notifyLivestep();
    }

    void pause() {
        Logg.i(TAG, "pause");
        if (mMediaPlayer != null) {
            if (mMediaPlayer_State == MediaPlayerState.STARTED ||
                    mMediaPlayer_State == MediaPlayerState.PAUSED) {
                Logg.i(TAG, "Call MediaPlayer.pause");
                mMediaPlayer.pause();
                setStatus(MediaPlayerState.PAUSED);
            }
        }
        notifyLivestep();
    }

    void stop() {
        Logg.i(TAG, "stop");
        if (mMediaPlayer != null) {
            if (mMediaPlayer_State == MediaPlayerState.PREPARED ||
                    mMediaPlayer_State == MediaPlayerState.STARTED ||
                    mMediaPlayer_State == MediaPlayerState.STOPPED ||
                    mMediaPlayer_State == MediaPlayerState.PAUSED ||
                    mMediaPlayer_State == MediaPlayerState.PLAYBACKCOMPLETE) {
                Logg.i(TAG, "Call MediaPlayer.stop");
                mMediaPlayer.stop();
                setStatus(MediaPlayerState.STOPPED);
            }
        }
        notifyLivestep();
    }

    void seekTo(int iMillisec) {
        Logg.i(TAG, "Call MediaPlayer.seekTo:" + iMillisec);
        int tMillisec = max(1, iMillisec);

        if (mMediaPlayer != null) {
            mTargetPosition = tMillisec;
            if (mCommandPossible) {
                mMediaPlayer.seekTo(tMillisec);
            }
        }
    }

    void release() {
        Logg.i(TAG, "release");
        mCommandPossible = false;
        if (mMediaPlayer != null) {
            Logg.v(TAG, "Call MediaPlayer.release");
            mMediaPlayer.release();
            setStatus(MediaPlayerState.END);
            mMediaPlayer = null;
        }
        notifyLivestep();
    }

    @Override
    public void onCompletion(MediaPlayer iMediaplayer) {
        if (iMediaplayer == null || mMediaPlayer == null || iMediaplayer.hashCode() != mMediaPlayer.hashCode()) {
            throw new RuntimeException("Misfit of MediaPlayer");
        }
        Logg.i(TAG, "Listener caught onCompletion");
        if (mMediaPlayer != null) {
            Logg.i(TAG, "Call MediaPlayer.isLooping");
            if (mMediaPlayer.isLooping()) {
                setStatus(MediaPlayerState.STARTED);
            } else {
                setStatus(MediaPlayerState.PLAYBACKCOMPLETE);
            }
        }
        notifyLivestep();
    }

    @Override
    public void onPrepared(MediaPlayer iMediaplayer) {
        if (iMediaplayer == null || mMediaPlayer == null || iMediaplayer.hashCode() != mMediaPlayer.hashCode()) {
            throw new RuntimeException("Misfit of MediaPlayer");
        }
        //mTimeFinishPrepareAsync = (new Date());
        Logg.i(TAG, "Listener caught onPrepared");
//        long d = mTimeFinishPrepareAsync.getTime() - mTimeStartPrepareAsync.getTime();

        if (mMediaPlayer != null) {
            if (mMediaPlayer_State == MediaPlayerState.PREPARING) {
                setStatus(MediaPlayerState.PREPARED);
                notifyLivestep();
                if (mPlayPending) {
                    mPlayPending = false;
                    Logg.i(TAG, "Call MediaPlayer.run");
                    mMediaPlayer.start();
                    enableCommand();
                    this.setSpeed(mSpeed);
                    setStatus(MediaPlayerState.STARTED);
                    notifyLivestep();
                }
            }
        }
    }

    @Override
    public void onSeekComplete(MediaPlayer iMediaplayer) {
        if (iMediaplayer == null || mMediaPlayer == null || iMediaplayer.hashCode() != mMediaPlayer.hashCode()) {
            throw new RuntimeException("Misfit of MediaPlayer");
        }
        Logg.i(TAG, "Listener caught onSeekComplete");
    }


    @Override
    public boolean onError(MediaPlayer iMediaplayer, int what, int extra) {
        if (iMediaplayer == null || mMediaPlayer == null || iMediaplayer.hashCode() != mMediaPlayer.hashCode()) {
            throw new RuntimeException("Misfit of MediaPlayer");
        }
        Logg.i(TAG, "onError");
        mCommandPossible = false;
        MediaPlayerState tState = this.mMediaPlayer_State;
        this.setStatus(MediaPlayerState.ERROR);
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_IO:
                Logg.e(TAG, "MEDIA_ERROR_IO");
                break;
            case MediaPlayer.MEDIA_ERROR_MALFORMED:
                Logg.e(TAG, "MEDIA_ERROR_MALFORMED");
                break;
            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                Logg.e(TAG, "MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK");
                break;
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                Logg.e(TAG, "MEDIA_ERROR_SERVER_DIED");
                break;
            case MediaPlayer.MEDIA_ERROR_TIMED_OUT:
                Logg.e(TAG, "MEDIA_ERROR_TIMED_OUT");
                break;
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                Logg.e(TAG, "MEDIA_ERROR_UNKNOWN");
                break;
            case MediaPlayer.MEDIA_INFO_AUDIO_NOT_PLAYING:
                Logg.e(TAG, "MEDIA_INFO_AUDIO_NOT_PLAYINGD");
                break;
            case -38:
                // -38 is not regarded as error
                Logg.e(TAG, "Play before prepAsync ready?");
                this.setStatus(tState);
                break;
            default:
                Logg.e(TAG, "generic audio playback error: No" + what);
                break;
        }
        switch (extra) {
            case MediaPlayer.MEDIA_ERROR_IO:
                Logg.e(TAG, "IO media error");
                break;
            case MediaPlayer.MEDIA_ERROR_MALFORMED:
                Logg.e(TAG, "media error, malformed");
                break;
            case MediaPlayer.MEDIA_ERROR_UNSUPPORTED:
                Logg.e(TAG, "unsupported media content");
                break;
            case MediaPlayer.MEDIA_ERROR_TIMED_OUT:
                Logg.e(TAG, "media timeout error");
                break;
            default:
                Logg.e(TAG, "unknown playback error: No" + extra);
                break;
        }
        notifyLivestep();

        reset();
        return true;
    }
    //</editor-fold>

    /**
     * Central internal fuction to shout "Livestep performed"
     */
    private void notifyLivestep() {
        Logg.i(TAG, "notify: " + mMediaPlayer_State.mText);
        if (mShouting != null) {
            Shout tShoutToCeiling = new Shout("MediaPlayerStateful");
            tShoutToCeiling.mLastObject = "Livestep";
            tShoutToCeiling.mLastAction = "performed";
            try {
                JSONObject tParamter = new JSONObject();
                tParamter.put("MediaPlayerState", this.mMediaPlayer_State.name());
                tShoutToCeiling.mJsonString = tParamter.toString();
            } catch(JSONException e) {
                Logg.e(TAG, e.toString());
            }
            mShouting.shoutUp(tShoutToCeiling);
        }
    }

    private void process_shouting() {
        if (mGlassFloor.mActor.equals(MediaPlayerObservingThread.THREAD_NAME)) {
            if (mGlassFloor.mLastObject.equals("Position") &&
                    mGlassFloor.mLastAction.equals("communicate")) {
                if (mShouting != null) {
                    mGlassFloor.mActor = "MediaPlayerStateful";
                    mShouting.shoutUp(mGlassFloor);
                }
            } else if (mGlassFloor.mLastObject.equals("State") &&
                    mGlassFloor.mLastAction.equals("change")) {
                if (mShouting != null) {
                    mGlassFloor.mActor = "MediaPlayerStateful";
                    mShouting.shoutUp(mGlassFloor);
                }
            }
        }
    }

    //Static Methods

    //Internal Organs

    //Interface
    void executePlayNow(Context iContext, Uri iUri) {
        this.reset();
        this.setDataSource(iContext, iUri);
        this.prepareAsync();
        this.getMediaPlayerFaderThread().setFullAndPlay();
    }

    void executePlay(Context iContext, Uri iUri) {
        this.reset();
        this.setDataSource(iContext, iUri);
        this.prepareAsync();
        this.getMediaPlayerFaderThread().setFadeInAndPlay();
    }

    void executeResume() {
        this.getMediaPlayerFaderThread().setFadeInAndPlay();
    }

    void executePause() {
        this.getMediaPlayerFaderThread().setFadeOutAndPause();
    }

    void executeTogglePause() {
        this.getMediaPlayerFaderThread().setTogglePause();
    }

    void executePauseNow() {
        this.getMediaPlayerFaderThread().setQuietAndPause();
    }


    /**
     * Method to cleanup dependent objects: Thread and MediaPlayer
     */



    MediaPlayerObservingThread getMediaPlayerObservingThread() {
        return mMediaPlayerObservingThread;
    }

    MediaPlayerFaderThread getMediaPlayerFaderThread() {
        return mMediaPlayerFaderThread;
    }


    @Override
    public void shoutUp(Shout tShoutToCeiling) {
        mGlassFloor = tShoutToCeiling;
        //Log.i(TAG, tShoutToCeiling.toString());
        process_shouting();
    }
}
