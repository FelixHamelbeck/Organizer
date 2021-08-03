package org.pochette.organizer.gui;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.KeyEvent;
import android.widget.ListPopupWindow;

import org.pochette.organizer.app.MediaPlayerServiceSingleton;
import org.pochette.organizer.mediaplayer.MediaPlayerService;
import org.pochette.utils_lib.logg.Logg;

import androidx.annotation.Nullable;

public class MediaSessionService extends Service {
    public MediaPlayer mediaPlayer;
    public static final String TAG = "MediaSessionService";
    public static final int NOTIFICATION_ID = 888;
    private MediaNotificationManager mMediaNotificationManager;
    private MediaSessionCompat mediaSession;

    @Override
    public void onCreate() {
        super.onCreate();
           Logg.w(TAG, "onCreate");
        mediaPlayer = new MediaPlayer();
        mMediaNotificationManager = new MediaNotificationManager(this);
        mediaSession = new MediaSessionCompat(this, "SOME_TAG");
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mediaSession.setCallback(new MediaSessionCompat.Callback() {
            @Override
            public void onPlay() {
                Logg.w(TAG, "onPlay");
                mediaPlayer.start();
            }

            @Override
            public void onPause() {
                Logg.w(TAG, "onPause");
                mediaPlayer.pause();
            }
        });
        Notification notification =
                mMediaNotificationManager.getNotification(
                        getMetadata(), getState(), mediaSession.getSessionToken());

        startForeground(NOTIFICATION_ID, notification);

        Logg.w(TAG, "Constructor finished");
    }

    public MediaMetadataCompat getMetadata() {
        Logg.i(TAG, "getMetadata");
        MediaMetadataCompat.Builder builder = new MediaMetadataCompat.Builder();

        if (MediaPlayerServiceSingleton.isAvailable() &&
                MediaPlayerServiceSingleton.getInstance().isServiceBound()) {
            MediaPlayerService tMediaPlayerService =
                    MediaPlayerServiceSingleton.getInstance().getMediaPlayerService();
            if (tMediaPlayerService == null) {

                builder.putString(MediaMetadataCompat.METADATA_KEY_ARTIST, "artist");
                builder.putString(MediaMetadataCompat.METADATA_KEY_TITLE, "my importtant title");
                builder.putLong(MediaMetadataCompat.METADATA_KEY_DURATION, 1234);
            } else {
                String tText;
                tText = "Hello world";
                Logg.w(TAG, tText);
                builder.putString(MediaMetadataCompat.METADATA_KEY_ARTIST, "artist");
                builder.putLong(MediaMetadataCompat.METADATA_KEY_DURATION, 1234);
                builder.putString(MediaMetadataCompat.METADATA_KEY_TITLE, tText);
            }
        } else {
            builder.putString(MediaMetadataCompat.METADATA_KEY_ARTIST, "artist");
            builder.putString(MediaMetadataCompat.METADATA_KEY_TITLE, "not importtant title");
            builder.putLong(MediaMetadataCompat.METADATA_KEY_DURATION, 1234);
        }
        return builder.build();
    }

    private PlaybackStateCompat getState() {

        Logg.w(TAG, "getState");
        long actions = mediaPlayer.isPlaying() ? PlaybackStateCompat.ACTION_PAUSE : PlaybackStateCompat.ACTION_PLAY;
        int state = mediaPlayer.isPlaying() ? PlaybackStateCompat.STATE_PLAYING : PlaybackStateCompat.STATE_PAUSED;

        final PlaybackStateCompat.Builder stateBuilder = new PlaybackStateCompat.Builder();
        stateBuilder.setActions(actions);
        stateBuilder.setState(state,
                mediaPlayer.getCurrentPosition(),
                1.0f,
                SystemClock.elapsedRealtime());
        return stateBuilder.build();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logg.w(TAG, "onStartCommand");
        if ("android.intent.action.MEDIA_BUTTON".equals(intent.getAction())) {
            KeyEvent keyEvent = (KeyEvent) intent.getExtras().get("android.intent.extra.KEY_EVENT");
            if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_MEDIA_PAUSE) {
                mediaPlayer.pause();
            } else {
                mediaPlayer.start();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        Logg.w(TAG, "onBind");
        return null;
    }

}
