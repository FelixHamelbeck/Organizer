package org.pochette.organizer.mediaplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.pochette.data_library.music.MusicFile;
import org.pochette.organizer.R;
import org.pochette.utils_lib.logg.Logg;
import org.pochette.utils_lib.shouting.Shout;
import org.pochette.utils_lib.shouting.Shouting;

import java.util.Locale;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import static android.app.Activity.RESULT_OK;
import static java.lang.Math.round;

public class MediaPlayerControl_Fragment extends Fragment implements Shouting {

    private static final String TAG = "FEHA (MPC_Fragment)";
    private static final int REQ_PICK_LOCAL_MUSICFILE = 10001;

    //Variables
    View mView;
    Button mPB_play;
    Button mPB_stop;
    Button mPB_continue;
    Button mPB_choose;
    TextView mTV_Volume;
    TextView mTV_Speed;
    Button mPB_raiseVolume;
    Button mPB_lowerVolume;
    Button mPB_playlist;
    Button mPB_raiseSpeed;
    Button mPB_normalSpeed;
    Button mPB_lowerSpeed;

    TextView mTV_player_time;
    TextView mTV_player_title;
    TextView mTV_player_album;
    TextView mTV_player_artist;
    ProgressBar PR_track;

    MediaPlayerService mMediaPlayerService;
    int mTextStandardColor;
    int mTextInactiveColor;

    boolean mShowTextInfo = true;

    // MyMediaFile mMyMediaFile;


    public Shout mShoutToCeiling;
    Shouting mShouting;
    Shout mGlassFloor;


    //Constructor
    public MediaPlayerControl_Fragment() {
        super();
    }

    @SuppressWarnings("unused")
    public MediaPlayerControl_Fragment(int contentLayoutId) {
        super(contentLayoutId);
    }


    //Setter and Getter

    @SuppressWarnings("unused")
    public void setMediaPlayerService(MediaPlayerService iMediaPlayerService) {
        mMediaPlayerService = iMediaPlayerService;
    }

    public void setShowTextInfo(boolean showTextInfo) {
        mShowTextInfo = showTextInfo;
        obeyShowInfoText();
    }
//<editor-fold desc="LiveCycle">

    //Livecycle
    // onAttach -> onCreate -> onCreateView -> onActivityCreated -> onStart -> onResume -> onPause -> onStop -> onDestroyView -> onDetach#
    @Override
    public void onAttach(@NonNull Context tContext) {
        super.onAttach(tContext);
        mShoutToCeiling = new Shout(this.getClass().getSimpleName());
        mTextStandardColor = ContextCompat.getColor(tContext, R.color.txt_standard);
        mTextInactiveColor = ContextCompat.getColor(tContext, R.color.txt_inactive);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBroadcastReceivers();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.media_player_controls, container, false);
        return mView;
    }


    @Override
    public void onViewCreated(@Nullable View view, @Nullable Bundle savedInstanceState) {
        mPB_play = mView.findViewById(R.id.PB_player_play);
        mPB_stop = mView.findViewById(R.id.PB_player_stop);
        mPB_continue = mView.findViewById(R.id.PB_player_continue);
        mPB_choose = mView.findViewById(R.id.PB_player_choose);
        mTV_Volume = mView.findViewById(R.id.TV_volume);
        mTV_Speed = mView.findViewById(R.id.TV_speed);
        mPB_raiseVolume = mView.findViewById(R.id.PB_player_raiseVolume);
        mPB_lowerVolume = mView.findViewById(R.id.PB_player_lowerVolume);
        mPB_playlist = mView.findViewById(R.id.PB_player_playlist);
        mPB_raiseSpeed = mView.findViewById(R.id.PB_player_raiseSpeed);
        mPB_normalSpeed = mView.findViewById(R.id.PB_player_normalSpeed);
        mPB_lowerSpeed = mView.findViewById(R.id.PB_player_lowerSpeed);
        mTV_player_time = mView.findViewById(R.id.TV_player_time);
        mTV_player_title = mView.findViewById(R.id.TV_title);
        mTV_player_album = mView.findViewById(R.id.TV_album);
        mTV_player_artist = mView.findViewById(R.id.TV_artist);
        PR_track = mView.findViewById(R.id.PR_player_track);

        //<editor-fold desc="Push Buttons">
        if (mPB_play != null) {
            mPB_play.setOnClickListener(v -> {
                Logg.k(TAG, "Play_Button OnClick");
                MusicFile tMusicFile = mMediaPlayerService.getLastMusicFile();
                if (tMusicFile != null) {
                    if (mMediaPlayerService != null) {
                        mMediaPlayerService.play(tMusicFile);
                    }
                }
            });
        }
        if (mPB_stop != null) {
            mPB_stop.setOnClickListener(v -> {
                Logg.k(TAG, "Stop_Button OnClick");
                mMediaPlayerService.pause();
            });
        }
        if (mPB_continue != null) {
            mPB_continue.setOnClickListener(v -> {
                Logg.k(TAG, "Continue_Button OnClick");
                if (!mMediaPlayerService.isPlaying()) {
                    mMediaPlayerService.resumePlay();
                }
            });
        }
        if (mPB_choose != null) {
            mPB_choose.setOnClickListener(v -> {
                Logg.k(TAG, "Choose_Button OnClick");
                choose();
            });
        }
        if (mPB_playlist != null) {
            mPB_playlist.setOnClickListener(v -> {
                Logg.k(TAG, "Playlist_Button OnClick");
                mShoutToCeiling.mLastObject = "PB_Playlist";
                mShoutToCeiling.mLastAction = "clicked";
                if (mShouting != null) {
                    mShouting.shoutUp(mShoutToCeiling);
                }
            });
        }
        if (mPB_lowerVolume != null) {
            mPB_lowerVolume.setOnClickListener(v -> {
                Logg.k(TAG, "LowerVolume_Button OnClick");
                mMediaPlayerService.decreaseVolume();
            });
        }
        if (mPB_raiseVolume != null) {
            mPB_raiseVolume.setOnClickListener(v -> {
                Logg.k(TAG, "RaiseVolume_Button OnClick");
                mMediaPlayerService.increaseVolume();
            });
        }
        if (mPB_raiseSpeed != null) {
            mPB_raiseSpeed.setOnClickListener(v -> {
                Logg.k(TAG, "RaiseSpeed_Button OnClick");
                mMediaPlayerService.increaseSpeed();
            });
        }
        if (mPB_normalSpeed != null) {
            mPB_normalSpeed.setOnClickListener(v -> {
                Logg.k(TAG, "NormalSpeed_Button OnClick");
                mMediaPlayerService.resetSpeed();
            });
        }
        if (mPB_lowerSpeed != null) {
            mPB_lowerSpeed.setOnClickListener(v -> {
                Logg.k(TAG, "LowerSpeed_Button OnClick");
                mMediaPlayerService.decreaseSpeed();
            });
        }
        obeyShowInfoText();
        //</editor-fold>
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        publish();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        publish();
    }


    //</editor-fold>

    //Static Methods

    //Internal Organs

    void choose() {
        Intent tIntent = new Intent(Intent.ACTION_GET_CONTENT,
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(tIntent, REQ_PICK_LOCAL_MUSICFILE);
    }

    void obeyShowInfoText() {
        int tVisibility;

        if (mShowTextInfo) {
            tVisibility = View.VISIBLE;
        } else {
            tVisibility = View.GONE;
        }
        if (mTV_player_album != null) {
            mTV_player_album.setVisibility(tVisibility);
        }
        if (mTV_player_artist != null) {
            mTV_player_artist.setVisibility(tVisibility);
        }
        if (mTV_player_time != null) {
            mTV_player_time.setVisibility(tVisibility);
        }
        if (mTV_player_title != null) {
            mTV_player_title.setVisibility(tVisibility);
        }


    }


    void setBroadcastReceivers() {
        IntentFilter tIntentFilter;
        // STATUS
        tIntentFilter = new IntentFilter();
        tIntentFilter.addAction("org.pochette.musicplayer.mediaplayer.MediaPlayerService.STATUS");

        BroadcastReceiver tBroadcastReceiver_Status = new BroadcastReceiver() {
            @SuppressWarnings("Convert2Lambda")
            @Override
            public void onReceive(Context iContext, Intent iIntent) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        // check if a context is available (rotation change?), which is needed for colors ...
                        boolean tChangePossible = true;
                        Context tContext = null;
                        try {
                            tContext = requireContext();
                        } catch(IllegalStateException e) {
                            tChangePossible = false;
                        }
                        if (tContext == null) {
                            //noinspection ConstantConditions
                            tChangePossible = false;
                        }
                        if (tChangePossible) {
                            setVolumeText(iIntent.getFloatExtra("volumeNormalized",
                                    0.f));
                            setSpeedText(iIntent.getFloatExtra("speed", 1.f));
                            String tArtist = iIntent.getStringExtra("artist");
                            String tAlbum = iIntent.getStringExtra("album");
                            setArtistAlbumText(tArtist, tAlbum);
                            String tTitle = iIntent.getStringExtra("title");
                            setTitle(tTitle);

                            float tPosition = iIntent.getFloatExtra("position", 0.f);
                            float tDuration = iIntent.getFloatExtra("duration", 0.f);
                            setPosition(tPosition, tDuration);
                            setProgressBar(tPosition, tDuration);
                        }
                    }
                });
            }
        };
        Objects.requireNonNull(getContext()).registerReceiver(tBroadcastReceiver_Status, tIntentFilter);

        // NEW_POSITION
        IntentFilter filter = new IntentFilter();
        filter.addAction("org.pochette.musicplayer.mediaplayer.MediaPlayerService.NEW_POSITION");
        BroadcastReceiver tBroadcastReceiver_NewPosition;
        tBroadcastReceiver_NewPosition = new BroadcastReceiver() {
            @Override
            public void onReceive(Context iContext, Intent iIntent) {
                float tPosition = iIntent.getFloatExtra("position", 0.f);
                float tDuration = iIntent.getFloatExtra("duration", 0.f);
                setPosition(tPosition, tDuration);
                setProgressBar(tPosition, tDuration);
            }
        };
        Objects.requireNonNull(getContext()).registerReceiver(tBroadcastReceiver_NewPosition, filter);
    }

    void fromService() {
        boolean tChangePossible = true;
        Context tContext = null;
        try {
            tContext = requireContext();
        } catch(IllegalStateException e) {
            tChangePossible = false;
        }
        if (tContext == null) {
            //noinspection ConstantConditions
            tChangePossible = false;
        }
        if (mMediaPlayerService == null) {
            tChangePossible = false;
        }
        if (tChangePossible) {
            setVolumeText(mMediaPlayerService.getVolume());
            setSpeedText(mMediaPlayerService.getSpeed());
            String tArtist = mMediaPlayerService.getArtist();
            String tAlbum = mMediaPlayerService.getAlbum();
            setArtistAlbumText(tArtist, tAlbum);
            String tTitle = mMediaPlayerService.getTitle();
            setTitle(tTitle);

            float tPosition = mMediaPlayerService.getPosition();
            float tDuration = mMediaPlayerService.getDuration();
            setPosition(tPosition, tDuration);
            setProgressBar(tPosition, tDuration);
        }
    }


    //<editor-fold desc="Information Display">

    void setPosition(float tPosition, float tDuration) {
        if (mTV_player_time != null) {
            int tPositionMinutes = (int) tPosition / 60;
            int tPositionSeconds = (int) tPosition % 60;
            int tDurationMinutes = (int) tDuration / 60;
            int tDurationSeconds = (int) tDuration % 60;
            String tString = String.format(Locale.ENGLISH,
                    "%d:%02d / %d:%02d ",
                    tPositionMinutes, tPositionSeconds,
                    tDurationMinutes, tDurationSeconds);
            mTV_player_time.setText(tString);
        }
    }


    void setVolumeText(float tVolume) {
        if (mTV_Volume != null) {
            mTV_Volume.setText(String.format(Locale.ENGLISH, "V: %d%%", round(tVolume * 100)));
        }
    }

    void setSpeedText(float tSpeed) {
        if (mTV_Speed != null) {
            mTV_Speed.setText(String.format(Locale.ENGLISH, "S: %d%%", round(tSpeed * 100)));
            if (tSpeed != 1.0f) {
                mTV_Speed.setTextColor(ContextCompat.getColor(
                        this.requireContext(), R.color.txt_warning));
            } else {
                mTV_Speed.setTextColor(ContextCompat.getColor(
                        this.requireContext(), R.color.txt_standard));
            }
        }
    }

    void setArtistAlbumText(String tArtist, String tAlbum) {
        if (mTV_player_album != null) {
            String tString;
            if (tAlbum == null) {
                tString = "na";
            } else {
                tString = tAlbum;
            }
            mTV_player_album.setText(tString);
        }

        if (mTV_player_artist != null) {
            String tString;
            if (tArtist == null) {
                tString = "na";
            } else {
                tString = tArtist;
            }
            mTV_player_artist.setText(tString);
        }

    }

    void setTitle(String tTitle) {
        if (mTV_player_title != null) {
            String tString;
            if (tTitle == null) {
                tString = "na";
            } else {
                tString = tTitle;
            }
            mTV_player_title.setText(tString);
        }
    }


    void setProgressBar(float tPosition, float tDuration) {
        // Progress Bar
        if (PR_track != null) {
            PR_track.setMax((int) (tDuration * 1000));
            PR_track.setProgress((int) (tPosition * 1000));
        }
    }
    //</editor-fold>

    void json2display(String tJsonString) {
        int tCurrentPosition = 0;
        int tDuration = 0;
        try {
            JSONObject tJsonObject = new JSONObject(tJsonString);
            tCurrentPosition = tJsonObject.getInt("CurrentPosition");
            tDuration = tJsonObject.getInt("Duration");
        } catch(JSONException e) {
            e.printStackTrace();
        }

        final String t = String.format(Locale.ENGLISH, "%02d:%02d/%02d:%02d",
                tCurrentPosition / 60000, (tCurrentPosition % 60000) / 1000,
                tDuration / 60000, (tDuration % 60000) / 1000);
        new Handler(Looper.getMainLooper()).post(() -> mTV_player_time.setText(t));
        // Progress Bax
        if (PR_track != null) {

            PR_track.setMax(tDuration);
            PR_track.setProgress(tCurrentPosition);
        }
    }

    @SuppressWarnings("SwitchStatementWithTooFewBranches")
    void process_shouting() {
        switch (mGlassFloor.mActor) {
            case "MediaPlayerStateful_Singleton":
                if (mGlassFloor.mLastAction.equals("communicate") &&
                        mGlassFloor.mLastObject.equals("Progress")) {
                    json2display(mGlassFloor.mJsonString);
                }
                break;
        }
    }


    @SuppressWarnings("unused")
    void changeButton(Button iButton, @SuppressWarnings("SameParameterValue") String iText,
                      @SuppressWarnings("SameParameterValue") boolean iEnable) {
        if (iButton == null) {
            return;
        }
        int tBackgroundColor;
        //tFontColor = ContextCompat.getColor(mContext, R.color.txt_standard);
        if (iEnable) {
            tBackgroundColor = ContextCompat.getColor(Objects.requireNonNull(this.getContext()),
                    R.color.bg_pushbutton_standard);
            iButton.setEnabled(true);
        } else {
            tBackgroundColor = ContextCompat.getColor(this.requireContext(),
                    R.color.bg_pushbutton_inactive);
            iButton.setEnabled(false);
        }
        iButton.setBackgroundColor(tBackgroundColor);
    }

    //Interface
    public void publish() {
        fromService();
    }

      public void setShouting(Shouting tShouting) {
        mShouting = tShouting;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*check whether you're working on correct request using requestCode , In this case 1*/
        Logg.i(TAG, "OnActivityResult with RequestCode " + requestCode);
        if (resultCode == RESULT_OK && requestCode == REQ_PICK_LOCAL_MUSICFILE) {
            Uri tUri = data.getData();
            Logg.i(TAG, Objects.requireNonNull(tUri).toString());
            if (mMediaPlayerService != null) {
                mMediaPlayerService.play(tUri);
            } else {
                Logg.w(TAG, "MediaPlayerService required");
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void shoutUp(Shout tGlassFloor) {
        if (tGlassFloor == null) {
            return;
        }
        mGlassFloor = tGlassFloor;
        process_shouting();
    }

}
