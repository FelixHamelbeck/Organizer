package org.pochette.organizer.gui;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;

import org.pochette.data_library.music.MusicDirectory;
import org.pochette.data_library.music.MusicScan;
import org.pochette.organizer.R;
import org.pochette.organizer.app.DataServiceSingleton;
import org.pochette.organizer.app.MediaPlayerServiceSingleton;
import org.pochette.organizer.app.OrganizerStatus;
import org.pochette.organizer.chained_search.Matryoshka_Fragment;
import org.pochette.organizer.dance.SlimDance_Fragment;
import org.pochette.organizer.diagram.DiagramManager;
import org.pochette.organizer.mediaplayer.DialogFragment_PlayerControl;
import org.pochette.organizer.mediaplayer.MediaPlayerControl_Fragment;
import org.pochette.organizer.mediaplayer.MediaPlayerService;
import org.pochette.organizer.music.MusicDirectory_Fragment;
import org.pochette.organizer.music.MusicFile_Fragment;
import org.pochette.organizer.pairing.MusicDirectory_Pairing_Fragment;
import org.pochette.organizer.requestlist.Requestlist_Fragment;
import org.pochette.utils_lib.logg.Logg;
import org.pochette.utils_lib.shouting.Shout;
import org.pochette.utils_lib.shouting.Shouting;

import java.util.Locale;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import static androidx.core.app.NotificationCompat.Builder;
import static androidx.core.app.NotificationCompat.PRIORITY_MIN;
import static java.lang.Thread.sleep;


/**
 * This is the main application used for playing music and teaching the class
 */
@SuppressWarnings("unused")
public class MainActivity extends AppCompatActivity implements Shouting {

    private final String TAG = "FEHA (MainActivity)";


    // Dance_Fragment mDance_Fragment;
    SlimDance_Fragment mSlimDance_Fragment;
    MusicFile_Fragment mMusicFile_Fragment;
    MusicDirectory_Fragment mMusicDirectory_Fragment;
    //Album_Fragment mAlbum_Fragment;
    Requestlist_Fragment mRequestlist_Fragment;
    MusicDirectory_Pairing_Fragment mMusicDirectoryPairing_Fragment;
    Matryoshka_Fragment mMatryoshka_Fragment;
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
        Logg.d(TAG, "Time: onCreate");
        super.onCreate(savedInstanceState);
        // all the database prep stuff is moved to OrganizerApp
        Logg.i(TAG, "OnCreate");
        // Start with UI
        setContentView(R.layout.activity_main);


        // start the TopBar
        startFragmentTopBar();
        // start the Splash
        startFragmentSplash();

        // now show same usable data, once the DB is up and running
        Thread tThread;
        //noinspection Anonymous2MethodRef
        tThread = new Thread(new Runnable() {
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
        //OrganizerApp tOrganizerApp = (OrganizerApp) getApplication();
        while (!OrganizerStatus.getInstance().isDbAvailable()) {
            try {
                //noinspection BusyWait
                sleep(51);
            } catch(InterruptedException e) {
                Logg.w(TAG, e.toString());
            }
        }
        Logg.d(TAG, "Time: call first real fragment");
//        MusicDirectory tMusicDirectory;
//        try {
//`            tMusicDirectory = MusicDirectory.getById(219);
//        } catch(Exception e) {
//            Logg.w(TAG, e.toString());
//        }
        connectFragmentTopBar();
        //Log.
        Logg.i(TAG, "call the first dataFragement after waiting for database");

        startFragmentSlimDance();


        //startFragmentMusicDirectoryPairing();
        //startFragmentMatryoshka();
        // startFragmentRequestlist();
        //startFragmentMediaPlayerControl();
//        startDialogPlayerControl();
//        startFragmentMusicFile();
//                    //   startFragmentMusicDirectory();
//                    //startFragmentAlbum();
//
//        MusicScan tMusicScan = new MusicScan(this);
//        tMusicScan.setShouting(this);
//        tMusicScan.execute(); // this starts a separate thread

//        Logg.i(TAG, "call download Diargam");
//        DiagramManager tDiagramManager = new DiagramManager();
//        tDiagramManager.downloadAbsentDiagrams();
    }

    @SuppressWarnings("unused")
    void startMedia() {

        Logg.w(TAG, "startMedia");

        ContextCompat.startForegroundService(
                MainActivity.this.getApplicationContext(),
                new Intent(MainActivity.this.getApplicationContext(), MediaSessionService.class));

        Logg.w(TAG, "finished with startMedia");
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
        Logg.i(TAG, "onPause");
        performCleanUP();
        Logg.i(TAG, "onPausefinsihed ");

    }

    void performCleanUP() {
        if (mTopBar_Fragment != null) {
            mTopBar_Fragment.stopThread();
            mTopBar_Fragment.onStop();
            mTopBar_Fragment.onDestroy();
            mTopBar_Fragment.onDetach();
            mTopBar_Fragment = null;
        }
//        Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
//
//        for (Thread lThread : threadSet) {
//            Logg.i(TAG, lThread.toString());
//        }
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

    public TopBar_Fragment getTopBar_Fragment() {
        return mTopBar_Fragment;
    }


    //<editor-fold desc="Start Fragments ">


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


//    void startFragmentAlbum() {
//        if (mAlbum_Fragment == null) {
//            mAlbum_Fragment = new Album_Fragment();
//        }
//        mAlbum_Fragment.setShouting(this);
//        FragmentManager tFragementManager = getSupportFragmentManager();
//        FragmentTransaction tTransaction = tFragementManager.beginTransaction();
//        tTransaction.replace(R.id.FL_Activity_PH, mAlbum_Fragment);
//        tTransaction.commitAllowingStateLoss();
//    }

    void startFragmentSplash() {
        Splash_Fragment tSplash_Fragment = new Splash_Fragment();
        tSplash_Fragment.setShouting(this);
        FragmentManager tFragementManager = getSupportFragmentManager();
        FragmentTransaction tTransaction = tFragementManager.beginTransaction();
        tTransaction.replace(R.id.FL_Activity_PH, tSplash_Fragment);
        tTransaction.commitAllowingStateLoss();
    }

    void startFragmentRequestlist() {
        if (mRequestlist_Fragment == null) {
            mRequestlist_Fragment = new Requestlist_Fragment();
        }
        mRequestlist_Fragment.setShouting(this);
        FragmentManager tFragementManager = getSupportFragmentManager();
        FragmentTransaction tTransaction = tFragementManager.beginTransaction();
        tTransaction.replace(R.id.FL_Activity_PH, mRequestlist_Fragment);
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


    void startFragmentMatryoshka() {
        if (mMatryoshka_Fragment == null) {
            mMatryoshka_Fragment = new Matryoshka_Fragment();
        }
        mMatryoshka_Fragment.setShouting(this);
        FragmentManager tFragementManager = getSupportFragmentManager();
        FragmentTransaction tTransaction = tFragementManager.beginTransaction();
        tTransaction.replace(R.id.FL_Activity_PH, mMatryoshka_Fragment);
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
    }

    void connectFragmentTopBar() {
        if (mTopBar_Fragment != null) {
            mTopBar_Fragment.setupReceiver(this);
        }
    }


    void startMusicScan() {
        DataServiceSingleton.getInstance().getDataService().scanMusic(this, this);
        Logg.i(TAG, "scanMusic called");
    }


    //</editor-fold>


    //Static Methods

    //Internal Organs


    void processShouting() {

        if (mGlassFloor.mActor.equals("TopBar_Fragment") &&
                mGlassFloor.mLastAction.equals("requested")) {
            switch (mGlassFloor.mLastObject) {
                case "Dance":
                    startFragmentSlimDance();
                    break;
                case "MusicFile":
                    startFragmentMusicFile();
                    break;
                case "MusicScan":
                    startMusicScan();
                    break;
                case "MediaPlayer":
                    startDialogPlayerControl();
                    break;
                case "Requestlist":
                    startFragmentRequestlist();
                    break;
                case "MusicDirectory":
                    startFragmentMusicDirectory();
                    break;
                case "MusicDirectoryPairing":
                    startFragmentMusicDirectoryPairing();
                    break;
                case "Complex Search":
                    startFragmentMatryoshka();
                    break;
                default:
                    Logg.w(TAG, "Fragement not available " + mGlassFloor.mLastObject);
            }
        }
        if (mGlassFloor.mActor.equals("DataServiceSingleton") &&
                mGlassFloor.mLastObject.equals("Database") &&
                mGlassFloor.mLastAction.equals("prepared")) {
            startDataFragment();
        }
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

    @Override
    public boolean onKeyDown(int iKeyCode, KeyEvent iKeyEvent) {
        Logg.i(TAG, iKeyEvent.toString());
        if (MyInputManager.swallowKeyDown(iKeyCode, iKeyEvent)) {
            return true;
        } else {
            Logg.i(TAG, "normal");
            Logg.i(TAG, iKeyEvent.toString());
            return super.onKeyDown(iKeyCode, iKeyEvent);
        }

    }

    @Override
    public boolean onKeyUp(int iKeyCode, KeyEvent iKeyEvent) {
        Logg.i(TAG, iKeyEvent.toString());
        if (MyInputManager.swallowKeyUp(iKeyCode, iKeyEvent)) {
            return true;
        } else {
            Logg.i(TAG, "normal");
            Logg.i(TAG, iKeyEvent.toString());
            return super.onKeyDown(iKeyCode, iKeyEvent);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startMyOwnForeground() {
        String NOTIFICATION_CHANNEL_ID = "com.example.simpleapp";
        String channelName = "My Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        Builder notificationBuilder = new Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }

    private void startForeground(int iInt, Notification iNotification) {
        String channelId;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//             createNotificationChannel("my_service", "My Background Service")
//        } else {
//            // If earlier version channel ID is not used
//            // https://developer.android.com/reference/android/support/v4/app/NotificationCompat.Builder.html#NotificationCompat.Builder(android.content.Context)
//            ""
//        }
        channelId = "2901";
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(PRIORITY_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(101, notification);
    }

//    @RequiresApi(api = Build.VERSION_CODES.O)
//    private void startMyOwnForeground(){
//        String NOTIFICATION_CHANNEL_ID = "com.example.simpleapp";
//        String channelName = "My Background Service";
//        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, MediaNotificationManager.IMPORTANCE_NONE);
//        chan.setLightColor(Color.BLUE);
//        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
//        MediaNotificationManager manager = (MediaNotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        assert manager != null;
//        manager.createNotificationChannel(chan);
//
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
//        Notification notification = notificationBuilder.setOngoing(true)
//                .setSmallIcon(R.drawable.ic_launcher)
//                .setContentTitle("App is running in background")
//                .setPriority(MediaNotificationManager.IMPORTANCE_MIN)
//                .setCategory(Notification.CATEGORY_SERVICE)
//                .build();
//        startForeground(2, notification);
//    }


}
