package org.pochette.organizer.gui;


import android.annotation.SuppressLint;
//import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import org.pochette.organizer.R;
import org.pochette.organizer.album.Album_Fragment;
import org.pochette.organizer.app.MediaPlayerServiceSingleton;
import org.pochette.organizer.app.OrganizerApp;
import org.pochette.organizer.app.OrganizerStatus;
import org.pochette.organizer.chained_search.ChainedList_Fragment;
import org.pochette.organizer.dance.Dance_Fragment;
import org.pochette.organizer.dance.SlimDance_Fragment;
import org.pochette.organizer.mediaplayer.DialogFragment_PlayerControl;
import org.pochette.organizer.mediaplayer.MediaPlayerControl_Fragment;
//import org.pochette.organizer.mediaplayer.MediaPlayerService;
import org.pochette.organizer.mediaplayer.MediaPlayerService;
import org.pochette.organizer.music.MusicDirectory_Fragment;
import org.pochette.organizer.music.MusicFile_Fragment;
import org.pochette.organizer.pairing.MusicDirectory_Pairing_Fragment;
import org.pochette.organizer.playlist.Playlist_Fragment;
import org.pochette.utils_lib.logg.Logg;
import org.pochette.utils_lib.shouting.Shout;
import org.pochette.utils_lib.shouting.Shouting;

import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import static java.lang.Thread.sleep;

/**
 * This is the main application used for playing music and teaching the class
 */
public class MainActivity extends AppCompatActivity implements Shouting {

    private final String TAG = "FEHA (MainActivity)";


    Dance_Fragment mDance_Fragment;
    SlimDance_Fragment mSlimDance_Fragment;
    MusicFile_Fragment mMusicFile_Fragment;
    MusicDirectory_Fragment mMusicDirectory_Fragment;
    Album_Fragment mAlbum_Fragment;
    Playlist_Fragment mPlaylist_Fragment;
    MusicDirectory_Pairing_Fragment mMusicDirectoryPairing_Fragment;
    ChainedList_Fragment mChainedList_Fragment;
    MediaPlayerControl_Fragment mMediaPlayerControl_Fragment;
    //  MediaPlayerService mMediaPlayerService;

    //boolean mMediaPlayerServiceBound;

    TopBar_Fragment mTopBar_Fragment;

    Shout mGlassFloor;
    //Constructor
    //Setter and Getter
    //Livecycle


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // all the database prep stuff is moved to OrganizerApp
        Logg.i(TAG, "OnCreate");


        // Start with UI
        setContentView(R.layout.activity_main);

        // prep the mediaplayer and controls

//        MediaPlayerServiceSingleton.createInstance(this);
//        MediaPlayerServiceSingleton.getInstance().prepMediaPlayerService();

        // start the TopBar
        startFragmentTopBar();
        // start the Splas
        startFragmentSplash();
        if (1 == 2) {
            // stay on splash screen
            return;
        }
        // now show same usable data, once the DB is up and running
        Thread tThread = new Thread(new Runnable() {
            @Override
            public void run() {
                startDataFragment();
            }
        });
        tThread.start();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return true;
    }


    void startDataFragment() {


        // only start once the database is ready
        OrganizerApp tOrganizerApp = (OrganizerApp) getApplication();
        while (!OrganizerStatus.getInstance().isDbAvailable()) {
            try {
                //noinspection BusyWait
                sleep(5000);
            } catch(InterruptedException e) {
                Logg.w(TAG, e.toString());
            }
        }
        Logg.i(TAG, "call the first dataFragement after waiting for database");
        //startFragmentDance();
//        startFragmentSlimDance();
        startFragmentPlaylist();
//        startFragmentMusicDirectoryPairing();
//                   // startFragmentChainedList();
//                    //startFragmentMediaPlayerControl();
        //startFragmentMusicFile();
//                    //   startFragmentMusicDirectory();
//                    //startFragmentAlbum();
//                }
//            });
//        }

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Logg.i(TAG, "id = " + id);
//        switch (item.getItemId()) {
//            case R.id.action_bar_settings:
//                startFragmentMyPreference();
//                break;
//            case R.id.action_bar_menu_dance:
//                startFragmentDance();
//                break;
//            case R.id.action_bar_menu_musicfile:
//                startFragmentMusicfile();
//                break;
//            case R.id.action_bar_menu_dynamic_search:
//                startFragmentDynamicSearch();
//                break;
//            case R.id.action_bar_menu_playlist:
//                startFragmentPlaylist();
//                break;
//            case R.id.action_bar_menu_play:
//                startFragmentDiskjockey();
//                break;
//            case R.id.action_bar_match_process:
//                startFragmentMatchProcess();
//                break;
//            case R.id.action_bar_match_edit:
//                startFragmentMatchEdit();
//                break;
//            case R.id.action_bar_about:
//                circuit();
//                // about();
//                break;
//        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
//        if (mMediaPlayerService != null) {
//            mMediaPlayerService.contextualize(getApplicationContext());
//        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        Logg.w(TAG, "onLowMemory");
        super.onLowMemory();
    }


//    void connectWithMediaPlayerService() {
//        Logg.i(TAG, "out" + Thread.currentThread().toString());
//        final Context fContext = getApplicationContext();
//        @SuppressWarnings("Convert2Lambda") Runnable tRunnable = new Runnable() {
//            @Override
//            public void run() {
//                Logg.i(TAG, "in" + Thread.currentThread().toString());
//                Intent intent = new Intent(fContext, MediaPlayerService.class);
//                startService(intent);
//                Logg.i(TAG, "past start, per bind");
//                bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
////                try {
////                    sleep(1000);
////                } catch(InterruptedException e) {
////                    e.printStackTrace();
////                }
//                Logg.i(TAG, " run finished");
//            }
//        };
//        Thread tThread = new Thread(tRunnable);
//        tThread.setName("connector");
//        tThread.start();
//        Logg.i(TAG, "out" + Thread.currentThread().toString());
//        // make sure control GUI is running
//        //  startFragmentMediaPlayerControl();
//    }
//

    @SuppressWarnings("FieldMayBeFinal")
//    private ServiceConnection mServiceConnection = new ServiceConnection() {
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//            mMediaPlayerServiceBound = false;
//        }
//
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            Logg.i(TAG, "onServiceConnected");
//            MediaPlayerService.MyBinder myBinder = (MediaPlayerService.MyBinder) service;
//            mMediaPlayerService = myBinder.getService();
//            mMediaPlayerService.contextualize(getApplicationContext());
//            mMediaPlayerServiceBound = true;
//
//            if (mMediaPlayerControl_Fragment != null) {
//                mMediaPlayerControl_Fragment.setMediaPlayerService(mMediaPlayerService);
//            }
//
//        }
//    };

        //<editor-fold desc="Start Fragments ">

    void startFragmentDance() {
        if (mDance_Fragment == null) {
            mDance_Fragment = new Dance_Fragment();
        }
        mDance_Fragment.setShouting(this);
        FragmentManager tFragementManager = getSupportFragmentManager();
        FragmentTransaction tTransaction = tFragementManager.beginTransaction();
        tTransaction.replace(R.id.FL_Activity_PH, mDance_Fragment);
        tTransaction.commitAllowingStateLoss();
    }

    void startFragmentSlimDance() {
        if (mSlimDance_Fragment == null) {
            mSlimDance_Fragment = new SlimDance_Fragment();
        }
        mSlimDance_Fragment.setShouting(this);
        FragmentManager tFragementManager = getSupportFragmentManager();
        FragmentTransaction tTransaction = tFragementManager.beginTransaction();
        tTransaction.replace(R.id.FL_Activity_PH, mSlimDance_Fragment);
        tTransaction.commitAllowingStateLoss();
    }

    void startFragmentMusicFile() {
        if (mMusicFile_Fragment == null) {
            mMusicFile_Fragment = new MusicFile_Fragment();
        }
        mMusicFile_Fragment.setShouting(this);
        FragmentManager tFragementManager = getSupportFragmentManager();
        FragmentTransaction tTransaction = tFragementManager.beginTransaction();
        tTransaction.replace(R.id.FL_Activity_PH, mMusicFile_Fragment);
        tTransaction.commitAllowingStateLoss();
    }

    void startFragmentMusicDirectory() {
        if (mMusicDirectory_Fragment == null) {
            mMusicDirectory_Fragment = new MusicDirectory_Fragment();
        }
        mMusicDirectory_Fragment.setShouting(this);
        FragmentManager tFragementManager = getSupportFragmentManager();
        FragmentTransaction tTransaction = tFragementManager.beginTransaction();
        tTransaction.replace(R.id.FL_Activity_PH, mMusicDirectory_Fragment);
        tTransaction.commitAllowingStateLoss();
    }


    void startFragmentAlbum() {
        if (mAlbum_Fragment == null) {
            mAlbum_Fragment = new Album_Fragment();
        }
        mAlbum_Fragment.setShouting(this);
        FragmentManager tFragementManager = getSupportFragmentManager();
        FragmentTransaction tTransaction = tFragementManager.beginTransaction();
        tTransaction.replace(R.id.FL_Activity_PH, mAlbum_Fragment);
        tTransaction.commitAllowingStateLoss();
    }

    void startFragmentSplash() {

        Splash_Fragment tSplash_Fragment = new Splash_Fragment();
        tSplash_Fragment.setShouting(this);
        FragmentManager tFragementManager = getSupportFragmentManager();
        FragmentTransaction tTransaction = tFragementManager.beginTransaction();
        tTransaction.replace(R.id.FL_Activity_PH, tSplash_Fragment);
        tTransaction.commitAllowingStateLoss();
    }


    void startFragmentPlaylist() {
        if (mPlaylist_Fragment == null) {
            mPlaylist_Fragment = new Playlist_Fragment();
        }
        mPlaylist_Fragment.setShouting(this);
        FragmentManager tFragementManager = getSupportFragmentManager();
        FragmentTransaction tTransaction = tFragementManager.beginTransaction();
        tTransaction.replace(R.id.FL_Activity_PH, mPlaylist_Fragment);
        tTransaction.commitAllowingStateLoss();
    }

    void startFragmentMusicDirectoryPairing() {
        if (mMusicDirectoryPairing_Fragment == null) {
            mMusicDirectoryPairing_Fragment = new MusicDirectory_Pairing_Fragment();
        }
        mMusicDirectoryPairing_Fragment.setShouting(this);
        FragmentManager tFragementManager = getSupportFragmentManager();
        FragmentTransaction tTransaction = tFragementManager.beginTransaction();
        tTransaction.replace(R.id.FL_Activity_PH, mMusicDirectoryPairing_Fragment);
        tTransaction.commitAllowingStateLoss();
    }

    void startFragmentChainedList() {
        if (mChainedList_Fragment == null) {
            mChainedList_Fragment = new ChainedList_Fragment();
        }
        mChainedList_Fragment.setShouting(this);
        FragmentManager tFragementManager = getSupportFragmentManager();
        FragmentTransaction tTransaction = tFragementManager.beginTransaction();
        tTransaction.replace(R.id.FL_Activity_PH, mChainedList_Fragment);
        tTransaction.commitAllowingStateLoss();
    }

    @SuppressWarnings("unused")
    void startFragmentMediaPlayerControl() {
        if (mMediaPlayerControl_Fragment == null) {
            mMediaPlayerControl_Fragment =
                    new MediaPlayerControl_Fragment();
        }
        MediaPlayerService tMediaPlayerService;
        tMediaPlayerService = MediaPlayerServiceSingleton.getInstance().getMediaPlayerService();
        if (tMediaPlayerService != null) {
            mMediaPlayerControl_Fragment.setMediaPlayerService(tMediaPlayerService);
        }
        FragmentManager tFragementManager = getSupportFragmentManager();
        FragmentTransaction tTransaction = tFragementManager.beginTransaction();
        tTransaction.replace(R.id.FL_Activity_PH, mMediaPlayerControl_Fragment);
        tTransaction.commitAllowingStateLoss();
    }


    void startFragmentTopBar() {
        if (mTopBar_Fragment == null) {
            mTopBar_Fragment = new TopBar_Fragment();
        }
        mTopBar_Fragment.setShouting(this);
        FragmentManager tFragementManager = getSupportFragmentManager();
        FragmentTransaction tTransaction = tFragementManager.beginTransaction();
        tTransaction.replace(R.id.FL_Activity_TopBar, mTopBar_Fragment);
        tTransaction.commitAllowingStateLoss();
        mTopBar_Fragment.setupReceiver(this);
    }


    //</editor-fold>


    //Static Methods

    //Internal Organs


    void processShouting() {
//        if (mGlassFloor.mLastAction.equals("resume") &&
//                mGlassFloor.mLastObject.equals("Fragment")) {
//            mToolbarUtils.setFragmentName(mGlassFloor.mActor);
//        }

        if (mGlassFloor.mActor.equals("TopBar_Fragment") &&
                mGlassFloor.mLastAction.equals("requested")) {
            switch (mGlassFloor.mLastObject) {
                case "Dance":
                    startFragmentDance();
                    break;
//                case "Pairing":
//                    startFragmentPairingDetail();
//                    break;
                case "MusicFile":
                    startFragmentMusicFile();
                    break;
                case "MediaPlayer":
                    //  startFragmentMediaPlayerControl();
                    startDialogPlayerControl();
                    break;
                case "Playlist":
                    startFragmentPlaylist();
                    break;
                case "MusicDirectory":
                    startFragmentMusicDirectory();
                    break;
                case "MusicDirectoryPairing":
                    startFragmentMusicDirectoryPairing();
                    break;
                case "ChainedList":
                    startFragmentChainedList();
                    break;
            }
        }
        if (mGlassFloor.mActor.equals("DataServiceSingleton") &&
                mGlassFloor.mLastObject.equals("Database") &&
                mGlassFloor.mLastAction.equals("prepared")) {
            startDataFragment();
        }
//        if (mGlassFloor.mActor.equals("ToolbarUtils") &&
//                mGlassFloor.mLastAction.equals("changed") &&
//                mGlassFloor.mLastObject.equals("Value")) {
//            if (mSA_Fragment_Dance != null && mSA_Fragment_Dance.isVisible()) {
//                mSA_Fragment_Dance.shoutUp(mGlassFloor);
//            }
//        }
    }


    private void startDialogPlayerControl() {
        DialogFragment_PlayerControl tDialogFragment_PlayerControl =
                DialogFragment_PlayerControl.newInstance();
        tDialogFragment_PlayerControl.setShouting(this);
        FragmentManager tFragmentManager = getSupportFragmentManager();

        Logg.i(TAG, tFragmentManager.toString());
        tDialogFragment_PlayerControl.show(tFragmentManager, null);
    }

//Interface


    @Override
    public void shoutUp(Shout iShoutToCeiling) {
        Logg.i(TAG, iShoutToCeiling.toString());
        mGlassFloor = iShoutToCeiling;
        processShouting();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, @NonNull int[] grantResults) {

        Logg.i(TAG, String.format(Locale.ENGLISH,
                "onRequestPermssionResult RC: %d [Count %d]", requestCode, permissions.length));
        Logg.i(TAG, "RC " + requestCode);
        Logg.i(TAG, "perm " + permissions.length);

        int i;
        for (i = 0; i < permissions.length; i++) {
            Logg.i(TAG, String.format(Locale.ENGLISH, "Permission %s -> %d", permissions[i],
                    grantResults[i]));
        }
    }

//
//    @Override
//    protected void onActivityResult(int iRequestCode, int iResultCode, Intent iIntentData) {
//
//        int tRequestCode;
//        int tDivisor = 0x10000; // this may be added n times from the result code provided by a fragment if you process it in the activity
//        tRequestCode = iRequestCode % tDivisor;
//        Logg.i(TAG, String.format(Locale.ENGLISH, "Shift from %d to %d", iRequestCode, tRequestCode));
//        /*check whether you're working on correct request using requestCode , In this case 1*/
//        Logg.i(TAG, "RequestCode " + iRequestCode);
////        switch (tRequestCode) {
////            case REQUEST_CODE_PICK_DIRECTORY_SCD:
////                if (iResultCode == Activity.RESULT_OK) {
////                    Uri tUri = iIntentData.getData();
////                    Logg.i(TAG, "Uri for Scd" + Objects.requireNonNull(tUri).toString());
////                    MyPreferences.savePreferenceString(
////                            MyPreferences.PREFERENCE_KEY_SCD_DIRECTORY_URI,
////                            tUri.toString());
////                    MatchProcess.callStoreScdDir();
////                }
////                break;
////            case REQUEST_CODE_PICK_DIRECTORY_NONSCD:
////                if (iResultCode == Activity.RESULT_OK) {
////                    Uri tUri = iIntentData.getData();
////                    Logg.i(TAG, "Uri for NonSCD" + Objects.requireNonNull(tUri).toString());
////                    MyPreferences.savePreferenceString(
////                            MyPreferences.PREFERENCE_KEY_NONSCD_DIRECTORY_URI,
////                            tUri.toString());
////                }
////                break;
////            case REQUEST_CODE_PICK_DIRECTORY_DIAGRAM:
////                if (iResultCode == Activity.RESULT_OK) {
////                    Uri tUri = iIntentData.getData();
////                    Logg.i(TAG, "Uri for Diagrams " + Objects.requireNonNull(tUri).toString());
////                    MyPreferences.savePreferenceString(
////                            MyPreferences.PREFERENCE_KEY_DIAGRAM_DIRECTORY_URI,
////                            tUri.toString());
////                }
////                break;
////        }
//        //informFragment();
//
//
//        Logg.i(TAG, "RequestCode " + iRequestCode);
//        super.onActivityResult(iRequestCode, iResultCode, iIntentData);
//    }


}
