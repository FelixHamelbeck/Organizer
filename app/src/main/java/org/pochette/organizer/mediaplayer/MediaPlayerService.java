package org.pochette.organizer.mediaplayer;

import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.pochette.data_library.music.MusicFile;
import org.pochette.data_library.requestlist.Request;
import org.pochette.organizer.R;
import org.pochette.organizer.app.MyPreferences;
import org.pochette.organizer.gui.MediaSessionService;
import org.pochette.utils_lib.logg.Logg;
import org.pochette.utils_lib.shouting.Shout;
import org.pochette.utils_lib.shouting.Shouting;

import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media.MediaBrowserServiceCompat;
import androidx.media.MediaSessionManager;
import androidx.media.session.MediaButtonReceiver;

import static java.lang.Math.max;
import static java.lang.Math.min;

@SuppressWarnings("unused")
//public class MediaPlayerService extends MediaBrowserServiceCompat implements Shouting {

    public class MediaPlayerService extends Service implements Shouting {
        private static final String TAG = "FEHA (MediaPlayerService)";
        // variables
        private MediaPlayerStateful mMediaPlayerStateful;
        private AudioManager mAudioManager; // needed for volume
        private SettingsContentObserver mSettingsContentObserver;
        private Context mContext;

        //   private MediaPlayer mMediaPlayer;
        private MediaSessionManager mManager;
        private MediaSession mSession;
        private MediaController mController;

        private final float mMaxSpeed = 1.25f;
        private final float mMinSpeed = 1.f / mMaxSpeed;
        private final float mStepSpeed = 0.01f;

        Shout mGlassFloor;
        Shouting mShouting;
        private MusicFile mLastMusicFile;

        private MediaSessionCompat mMediaSessionCompat;

        //<editor-fold desc="Binder">
        private final IBinder mBinder = new LocalBinder();


        private void initMediaSession() {
            Logg.i(TAG, "initMediaSession");
            ComponentName mediaButtonReceiver = new ComponentName(getApplicationContext(), MediaButtonReceiver.class);
            mMediaSessionCompat = new MediaSessionCompat(getApplicationContext(), "Tag", mediaButtonReceiver, null);

            mMediaSessionCompat.setCallback(mMediaSessionCallback);
            mMediaSessionCompat.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

            Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
            mediaButtonIntent.setClass(this, MediaButtonReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, mediaButtonIntent, 0);
            mMediaSessionCompat.setMediaButtonReceiver(pendingIntent);

            //setSessionToken(mMediaSessionCompat.getSessionToken());
        }

//        @Nullable
//        @Override
//        public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
//            Logg.i(TAG, "onGetRoot");
//            if (TextUtils.equals(clientPackageName, getPackageName())) {
//                return new MediaBrowserServiceCompat.BrowserRoot(getString(R.string.app_name), null);
//            }
//
//            return null;
//        }
//
//        //Not important for general audio service, required for class
//        @Override
//        public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
//
//            Logg.i(TAG, "loadChild");
//            result.sendResult(null);
//        }

        private MediaSessionCompat.Callback mMediaSessionCallback = new MediaSessionCompat.Callback() {

            @Override
            public void onPlay() {

                super.onPlay();
                Logg.w(TAG, "onPlay");
            }

            @Override
            public void onPause() {
                super.onPause();
                Logg.w(TAG, "onPlay");
            }

            @Override
            public void onPlayFromMediaId(String mediaId, Bundle extras) {
                super.onPlayFromMediaId(mediaId, extras);
                Logg.w(TAG, "onPlayFromMediaId");
            }
        };

        @Override
        public IBinder onBind(Intent intent) {
            return mBinder;
        }

        public class LocalBinder extends MyBinder {
            public MediaPlayerService getService() {
                return MediaPlayerService.this;
            }
        }

        public class MyBinder extends Binder {
            public MediaPlayerService getService() {
                return MediaPlayerService.this;
            }
        }
        //</editor-fold>

        // constructor
        // setter and getter
        public void setShouting(Shouting iShouting) {
            mShouting = iShouting;
        }


        public MusicFile getLastMusicFile() {
            return mLastMusicFile;
        }

        public MediaPlayerState getState() {
           return  mMediaPlayerStateful.getMediaPlayer_State();
        }

        // lifecylce and override

        //<editor-fold desc="LifeCycle">
        @Override
        public void onCreate() {
            super.onCreate();
            startMediaPlayer();


        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            return super.onStartCommand(intent, flags, startId);
        }

        @Override
        public void onDestroy() {
            mSettingsContentObserver = null;
            super.onDestroy();
        }

        /**
         * store things we can get from Context, e.g. AudioManager
         *
         * @param iContext to retrieve contextual date like AudioManager
         */
        public void contextualize(Context iContext) {
            Logg.i(TAG, "HS" + this.hashCode());
            Logg.i(TAG, "Contextulize MediaPlayerService " + iContext.toString());
            mContext = iContext;
            // store AudioManager
            mAudioManager = null;
            mAudioManager = (AudioManager) iContext.getSystemService(Context.AUDIO_SERVICE);
            if (mAudioManager == null) {
                Logg.w(TAG, "no audiomanager");
            }
            // define observer
            mSettingsContentObserver = new SettingsContentObserver(iContext, new Handler(), this);
            iContext.getContentResolver().registerContentObserver(
                    Settings.System.CONTENT_URI,
                    true, mSettingsContentObserver);

//        try {
//             Object tService = mContext.getSystemService(Context.MEDIA_SESSION_SERVICE);
//             mManager = (MediaSessionManager) tService;
//            mSession = Objects.requireNonNull(mManager).createSession("sample session");
//            mController = MediaController.fromToken( mSession.getSessionToken() );
//        } catch(Exception e) {
//            Logg.w(TAG, e.toString());
//        }

        }
        //</editor-fold>

        // internal

        //<editor-fold desc="internal calls">
        private void startMediaPlayer() {
            if (mMediaPlayerStateful == null) {
                mMediaPlayerStateful = new MediaPlayerStateful();
                mMediaPlayerStateful.initializeMediaPlayer(null);
                mMediaPlayerStateful.setShouting(this);
            }
        }

        private float calcDecreasedSpeed() {
            float tSpeed = mMediaPlayerStateful.getSpeed();
            tSpeed = tSpeed - mStepSpeed;
            tSpeed = max(mMinSpeed, min(tSpeed, mMaxSpeed));
            return tSpeed;
        }

        private float calcIncreasedSpeed() {
            float tSpeed = mMediaPlayerStateful.getSpeed();
            tSpeed = tSpeed + mStepSpeed;
            tSpeed = max(mMinSpeed, min(tSpeed, mMaxSpeed));
            return tSpeed;
        }

        private void broadcastPosition(Shout tShout) {
            float tPosition;
            float tDuration;
            try {
                JSONObject tJsonObject = new JSONObject(tShout.mJsonString);
                tPosition = ((float) tJsonObject.getInt("CurrentPosition")) / 1000.f;
                tDuration = ((float) tJsonObject.getInt("Duration")) / 1000.f;
                Intent tIntent = new Intent();
                tIntent.setAction("org.pochette.musicplayer.mediaplayer.MediaPlayerService.NEW_POSITION");
                tIntent.putExtra("position", tPosition);
                tIntent.putExtra("duration", tDuration);
                sendBroadcast(tIntent);
            } catch(JSONException e) {
                Logg.w(TAG, tShout.mJsonString);
                Logg.e(TAG, e.toString());
            }
        }

        private void broadcastStatus() {
            try {
                if (mMediaPlayerStateful != null) {
                    String tArtist;
                    String tAlbum;
                    String tTitle;

                    float tPosition = ((float) mMediaPlayerStateful.getCurrentPosition()) / 1000.f;
                    float tDuration = ((float) mMediaPlayerStateful.getDuration()) / 1000.f;
                    float tSpeed = mMediaPlayerStateful.getSpeed();
                    float tVolume;
                    if (mAudioManager != null) {
                        tVolume = ((float) mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC)) / 15.f;
                    } else {
                        tVolume = 0.f;
                    }
                    tArtist = mMediaPlayerStateful.getArtist();
                    tAlbum = mMediaPlayerStateful.getAlbum();
                    tTitle = mMediaPlayerStateful.getTitle();

                    //
                    Intent tIntent = new Intent();
                    tIntent.setAction("org.pochette.musicplayer.mediaplayer.MediaPlayerService.STATUS");
                    tIntent.putExtra("status", mMediaPlayerStateful.getMediaPlayer_State().name());
                    tIntent.putExtra("artist", tArtist);
                    tIntent.putExtra("album", tAlbum);
                    tIntent.putExtra("title", tTitle);
                    tIntent.putExtra("position", tPosition);
                    tIntent.putExtra("duration", tDuration);
                    tIntent.putExtra("volumeNormalized", tVolume);
                    tIntent.putExtra("speed", tSpeed);
                    sendBroadcast(tIntent);
                }
            } catch(Exception e) {
                Logg.e(TAG, e.toString());
            }
        }

        private void processShouting() {
            if (mGlassFloor.mActor.equals("MediaPlayerStateful")) {
                if (mGlassFloor.mLastObject.equals("Position") &&
                        mGlassFloor.mLastAction.equals("communicate")) {
                    broadcastPosition(mGlassFloor);
                } else if (mGlassFloor.mLastObject.equals("State") &&
                        mGlassFloor.mLastAction.equals("change")) {
                    broadcastStatus();
                }
            } else if (mGlassFloor.mActor.equals("SettingsContentObserver")) {
                if (mGlassFloor.mLastObject.equals("Volume/STREAM_MUSIC") &&
                        mGlassFloor.mLastAction.equals("notified")) {
                    Logg.i(TAG, "volume change shout");
                    broadcastStatus();
                }
            }
        }
        //</editor-fold>

        // public methods
        //<editor-fold desc="Commands">
        public void playNow(Uri iUri) {
            if (mMediaPlayerStateful == null) {
                startMediaPlayer();
            }
            mMediaPlayerStateful.executePlayNow(mContext, iUri);
        }

        public void play(Uri iUri) {
            if (mMediaPlayerStateful == null) {
                startMediaPlayer();
            }
            Logg.i(TAG, "HS" + this.hashCode());
            mMediaPlayerStateful.executePlay(mContext, iUri);
        }


        public void play(MusicFile iMusicFile) {
            Logg.i(TAG, "HS" + this.hashCode());
            mLastMusicFile = iMusicFile;
            Request tRequest = new Request(iMusicFile, null, null);
            play(tRequest);
        }

        public void play() {
            Logg.i(TAG, "HS" + this.hashCode());
            Request tRequest = new Request(mLastMusicFile, null, null);
            play(tRequest);
        }

        public void play(Request iRequest) {
            if (mMediaPlayerStateful == null) {
                startMediaPlayer();
            }
            Logg.i(TAG, "play from playinstruction");
            //  if (iPlayinstruction.mUri == null) {
            MusicFile tMusicFile = iRequest.getMusicFile();
            mLastMusicFile = tMusicFile;
            MyPreferences.savePreferenceInt("LastMediaId", tMusicFile.mMediaId);
            Logg.w(TAG, "LastMediaId" + tMusicFile.mMediaId);
            Uri tUri = Uri.withAppendedPath(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    Integer.toString(tMusicFile.mMediaId));
            mMediaPlayerStateful.executePlay(
                    mContext,
                    tUri);
        }

        public void pauseNow() {
            if (mMediaPlayerStateful == null) {
                startMediaPlayer();
            }
            mMediaPlayerStateful.executePauseNow();
        }

        public void pause() {
            if (mMediaPlayerStateful == null) {
                startMediaPlayer();
            }
            mMediaPlayerStateful.executePause();
        }

        public void togglePause() {
            if (mMediaPlayerStateful == null) {
                startMediaPlayer();
            }
            mMediaPlayerStateful.executeTogglePause();
        }

        public void resumePlay() {
            if (mMediaPlayerStateful == null) {
                startMediaPlayer();
            }
            mMediaPlayerStateful.executeResume();
        }
        //</editor-fold>

        //<editor-fold desc="Speed">
        public float getSpeed() {
            if (mMediaPlayerStateful == null) {
                startMediaPlayer();
            }
            return mMediaPlayerStateful.getSpeed();
        }

        public void setSpeed(float iSpeed) {
            if (mMediaPlayerStateful == null) {
                startMediaPlayer();
            }
            mMediaPlayerStateful.setSpeed(iSpeed);
            broadcastStatus();
        }

        public void increaseSpeed() {
            setSpeed(calcIncreasedSpeed());
        }

        public void resetSpeed() {
            float mDefaultSpeed = 1.f;
            setSpeed(mDefaultSpeed);
        }

        public void decreaseSpeed() {
            setSpeed(calcDecreasedSpeed());
        }
        //</editor-fold>

        //<editor-fold desc="Volume">
        // volume is not going to the player but to the AudioManager
        public void increaseVolume() {
            mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                    AudioManager.ADJUST_RAISE, 0);
        }

        public float getVolume() {
            int currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            return currentVolume / 15.f;
        }

        public void decreaseVolume() {
            mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                    AudioManager.ADJUST_LOWER, 0);
        }
        //</editor-fold>

        //<editor-fold desc="Info">
        public boolean isPlaying() {
            if (mMediaPlayerStateful != null) {
                mMediaPlayerStateful.isPlaying();
            }
            return false;
        }

        public String getArtist() {
            if (mMediaPlayerStateful != null) {
                mMediaPlayerStateful.getArtist();
            }
            return "";
        }

        public String getAlbum() {
            if (mMediaPlayerStateful != null) {
                mMediaPlayerStateful.getAlbum();
            }
            return "";
        }

        public String getTitle() {
            if (mMediaPlayerStateful != null) {
                mMediaPlayerStateful.getTitle();
            }
            return "";
        }

        public float getPosition() {
            if (mMediaPlayerStateful != null) {
                return ((float) mMediaPlayerStateful.getCurrentPosition()) / 1000.f;
            }
            return 0.f;
        }

        public float getDuration() {
            if (mMediaPlayerStateful != null) {
                return ((float) mMediaPlayerStateful.getDuration()) / 1000.f;
            }
            return 0.f;
        }
        //</editor-fold>

        @Override
        public void shoutUp(Shout tShoutToCeiling) {
            mGlassFloor = tShoutToCeiling;
            processShouting();
        }
    }
