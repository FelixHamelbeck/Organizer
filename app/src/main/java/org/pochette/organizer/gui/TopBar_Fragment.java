package org.pochette.organizer.gui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;
import org.pochette.data_library.database_management.DataService;
import org.pochette.data_library.database_management.Ldb_Helper;
import org.pochette.data_library.database_management.Scddb_File;
import org.pochette.organizer.R;
import org.pochette.organizer.app.DataServiceSingleton;
import org.pochette.organizer.app.MediaPlayerServiceSingleton;
import org.pochette.organizer.diagram.DiagramManager;
import org.pochette.organizer.mediaplayer.MediaPlayerState;
import org.pochette.utils_lib.logg.Logg;
import org.pochette.utils_lib.report.ReportSystem;
import org.pochette.utils_lib.shouting.Shout;
import org.pochette.utils_lib.shouting.Shouting;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;

import static java.lang.Thread.interrupted;
import static java.lang.Thread.sleep;
import static org.pochette.organizer.mediaplayer.MediaPlayerState.*;

@SuppressWarnings("unused")
public class TopBar_Fragment extends Fragment
        implements Shouting, LifecycleOwner, PopupMenu.OnMenuItemClickListener {

    //Variables
    private static final String TAG = "FEHA (TopBar_Fragment)";

    View mView;
    ImageView mIV_AppIcon;
    ImageView mIV_PlayerControl;
    ImageView mIV_Dance;
    ImageView mIV_MusicFile;
    ImageView mIV_Requestlist;
    ImageView mIV_Player;
    ImageView mIV_Menu;
    TextView mTV_InfoTop;
    TextView mTV_InfoBottom;
    TextView mTV_Report;

    Shouting mShouting;
    Shout mShoutToCeiling;
    private Shout mGlassFloor;

    // information to be displayed
    float mDisplayPosition = 0.f;
    float mDisplayDuration = 0.f;
    String mStringDance;
    String mStringMusician;
    String mStringAlbum;
    String mStringTitle;
    int mTopTextLoop = 0;
    boolean mAppIconActive = true;
    String mMediaNext;

    private MediaPlayerState mMediaPlayerState;

    ArrayList<String> mAL_InfoText = new ArrayList<>(0);
    Thread mThread;


    @SuppressWarnings("unused")
//Constructor

//Setter and Getter
    public void setShouting(Shouting mShouting) {
        this.mShouting = mShouting;
    }

    //Livecycle
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mShoutToCeiling = new Shout(this.getClass().getSimpleName());

        mStringDance ="";
        mStringMusician="";
        mStringAlbum="";
        mStringTitle="";
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_topbar, container, false);
        Fragment tParentFragment = getParentFragment();
        if (tParentFragment != null) {
            try {
                mShouting = (Shouting) tParentFragment;
            } catch(ClassCastException e) {
                throw new ClassCastException(tParentFragment.toString()
                        + " must implement Shouting");
            }
        }
        return mView;
    }

    public void setupReceiver(Activity iActivity) {
        TopBar_BroadCastReceiver tTopBar_BroadCastReceiver;
        //tTopBar_BroadCastReceiver = new TopBar_BroadCastReceiver(this, this);
        tTopBar_BroadCastReceiver = new TopBar_BroadCastReceiver(this);
        tTopBar_BroadCastReceiver.mShouting = this;
        IntentFilter filter = new IntentFilter();
        filter.addAction("org.pochette.musicplayer.mediaplayer.MediaPlayerService.NEW_POSITION");
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        iActivity.registerReceiver(tTopBar_BroadCastReceiver, filter);
    }

    @Override
    public void onViewCreated(@Nullable View view, @Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            Logg.i(TAG, savedInstanceState.toString());
        }

        mIV_AppIcon = requireView().findViewById(R.id.IV_TopBar_AppIcon);
        mTV_InfoTop = requireView().findViewById(R.id.TV_TopBar_InfoTop);
        mTV_InfoBottom = requireView().findViewById(R.id.TV_TopBar_InfoBottom);
        mTV_Report = requireView().findViewById(R.id.TV_TopBar_Report);

        mIV_PlayerControl = requireView().findViewById(R.id.IV_TopBar_PlayerControl);
        mIV_Dance = requireView().findViewById(R.id.IV_TopBar_Dance);
        mIV_MusicFile = requireView().findViewById(R.id.IV_TopBar_MusicFile);
        mIV_Requestlist = requireView().findViewById(R.id.IV_TopBar_Requestlist);
        mIV_Player = requireView().findViewById(R.id.IV_TopBar_Player);
        mIV_Menu = requireView().findViewById(R.id.IV_TopBar_MenuIcon);

        mIV_AppIcon.setOnClickListener(iView -> {
            Logg.k(TAG, "IV_App OnClick");
            appIconShortClick();
        });

        mIV_AppIcon.setOnLongClickListener(iView -> {
            Logg.k(TAG, "IV_App OnClick");
            appIconLongClick();
            return true;
        });

        mIV_PlayerControl.setOnClickListener(iView -> {
            Logg.k(TAG, "IV_Player OnClick");
            executePlayerControl();
        });
        mIV_Dance.setOnClickListener(iView -> {
            Logg.k(TAG, "IV_Dance OnClick");
            shoutIconClick("Dance");
        });
        mIV_MusicFile.setOnClickListener(iView -> {
            Logg.k(TAG, "IV_Musicfile OnClick");
            shoutIconClick("MusicFile");
        });
        mIV_Requestlist.setOnClickListener(iView -> {
            Logg.k(TAG, "IV_Requestlist OnClick");
            shoutIconClick("Requestlist");
        });
        mIV_Player.setOnClickListener(iView -> {
            Logg.k(TAG, "IV_Player OnClick");
            shoutIconClick("MediaPlayer");
        });

        mIV_Menu.setOnClickListener(iView -> {
            Logg.k(TAG, "IV_Menu OnClick");
            PopupMenu tPopupMenu;//View will be an anchor for PopupMenu
            tPopupMenu = new PopupMenu(mView.getContext(), mIV_Menu);
            tPopupMenu.inflate(R.menu.action_bar_menu);
            tPopupMenu.setOnMenuItemClickListener(this);
            tPopupMenu.show();
        });
        //</editor-fold>
        refreshBottomText();
        setBroadcastReceivers();
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        prepValues(outState);
    }


    /**
     * Called when the fragment is no longer attached to its activity.  This
     * is called after {@link #onDestroy()}.
     */
    @Override
    public void onDetach() {
        super.onDetach();
    }


    void prepValues(Bundle iBundle) {
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStop() {
        super.onStop();
        stopThread();
    }

    @Override
    public void onStart() {
        super.onStart();
        startThread();

        startReportSystem();
    }
//Static Methods

    //Internal Organs


    static public class HandlerExtension extends Handler {
        private final WeakReference<Activity> fCurrentActivity;
        //     private final WeakReference<TextView> fTV_Report = this.mTV_Report;

        HandlerExtension(Activity activity) {
            fCurrentActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@SuppressWarnings("NullableProblems") Message msg) {
            try {
                Activity activity = fCurrentActivity.get();
                MainActivity tMainActivity = (MainActivity) activity;
                TopBar_Fragment tTopBar_Fragment = tMainActivity.getTopBar_Fragment();
                String tText = msg.getData().getString("text", "Empty");
                Logg.d(TAG, "handle " + tText);
                tTopBar_Fragment.updateReportSystem(tText);
            } catch(Exception e) {
                Logg.w(TAG, e.toString());
            }
        }
    }

    void updateReportSystem(String iText) {
        if (mTV_Report != null) {
            if (iText.isEmpty()) {
                mTV_Report.setVisibility(View.INVISIBLE);
            } else {
                mTV_Report.setVisibility(View.VISIBLE);
            }
            mTV_Report.setText(iText);
        }
    }

    void startReportSystem() {

        Logg.i(TAG, "Start ReportSystem");

        Handler mReportHandler;
        mReportHandler = new HandlerExtension(getActivity());
        ReportSystem.getInstance().startThread(mReportHandler);
    }

    void startThread() {
        Runnable tRunnable = new Runnable() {
            @Override
            public void run() {
                while (!interrupted()) {
                    try {
                        //noinspection BusyWait
                        sleep(4000);
                    } catch(InterruptedException e) {
                        Logg.i(TAG, e.toString());
                        // it is okay to stop the thread
                    }
                    refreshTopText();
                }
            }
        };
        mThread = new Thread(tRunnable);
        mThread.start();
    }

    void stopThread() {
        Logg.i(TAG, "StopThread");
        if (mThread != null) {
            mThread.interrupt();
            mThread = null;
        }
    }

    private void refreshTopText() {
        if (mTV_InfoTop == null || mAL_InfoText == null || mAL_InfoText.size() == 0) {
            return;
        }
        mTopTextLoop++;
        mTopTextLoop = mTopTextLoop % mAL_InfoText.size();
        String tNewText;
        tNewText = mAL_InfoText.get(mTopTextLoop);

        if (tNewText != null && !tNewText.isEmpty()) {
            final String fNewText = tNewText;
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    mTV_InfoTop.setText(fNewText);

                }
            });
        }
    }

    private void refreshBottomText() {
        if (mTV_InfoBottom != null) {
            int tIntPosition = (int) mDisplayPosition;
            int tIntDuration = (int) mDisplayDuration;
            String mBottomString =
                    String.format(Locale.ENGLISH,
                            "%d:%02d / %d:%02d ",
                            tIntPosition / 60, tIntPosition % 60,
                            tIntDuration / 60, tIntDuration % 60);
            mTV_InfoBottom.setText(mBottomString);
        }
    }

    private void executePlayerControl() {
        if (MediaPlayerServiceSingleton.getInstance() != null &&
                MediaPlayerServiceSingleton.getInstance().getMediaPlayerService() != null) {
            if (mMediaNext.equals("play")) {
                if (MediaPlayerServiceSingleton.getInstance().getMediaPlayerService().getLastMusicFile() != null) {
                    MediaPlayerServiceSingleton.getInstance().getMediaPlayerService().play();
                }
            } else if (mMediaNext.equals("pause")) {
                MediaPlayerServiceSingleton.getInstance().getMediaPlayerService().pause();
            }
        }
    }

    private void shoutIconClick(String iLastObject) {
        if (mShouting != null) {
            Shout tShout = new Shout("TopBar_Fragment");
            tShout.mLastObject = iLastObject;
            tShout.mLastAction = "requested";
            mShouting.shoutUp(tShout);
        }
    }

    private void process_shouting() {
        if (mGlassFloor.mActor.equals("TopBar_BroadCastReceiver")) {

            if (mGlassFloor.mLastAction.equals("received") &&
                    mGlassFloor.mLastObject.equals("NEW_POSITION")) {
                try {
                    JSONObject tJsonObject = new JSONObject(mGlassFloor.mJsonString);
                    mDisplayPosition = (float) tJsonObject.getDouble("position");
                    mDisplayDuration = (float) tJsonObject.getDouble("duration");
                    //refreshBottomText();
                    refreshBottomText();
                } catch(Exception e) {
                    Logg.w(TAG, e.toString());
                }
            }

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
                //   Logg.i(TAG, "onReceive Status in Fragment");
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Logg.w(TAG, "receive");
                        Logg.w(TAG, iIntent.toString());

                        mMediaPlayerState =
                                MediaPlayerServiceSingleton.getInstance().getMediaPlayerService().
                                        getState();
                        refreshPlayerControl();

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
                            mStringMusician = iIntent.getStringExtra("artist");
                            mStringAlbum = iIntent.getStringExtra("album");
                            mStringDance = iIntent.getStringExtra("title");
                            if (mStringMusician != null) Logg.i(TAG, mStringMusician);
                            if (mStringAlbum != null) Logg.i(TAG, mStringAlbum);
                            if (mStringDance != null) Logg.i(TAG, mStringDance);
                            mAL_InfoText.clear();
                            mTopTextLoop = 0;
                            if (mStringMusician != null) mAL_InfoText.add(mStringMusician);
                            if (mStringAlbum != null) mAL_InfoText.add(mStringAlbum);
                            if (mStringDance != null) mAL_InfoText.add(mStringDance);
                        }
                    }
                });
            }
        };
        Objects.requireNonNull(getContext()).registerReceiver(tBroadcastReceiver_Status, tIntentFilter);
    }


    void appIconShortClick() {
        if (mAppIconActive) {
            UserChoice tUserChoice = new UserChoice("Maintenance");
            tUserChoice.addQuestion("MUSIC", "Music Collection");
            tUserChoice.addQuestion("DB", "Database");
            tUserChoice.addQuestion("REPORT", "Report");
            tUserChoice.addQuestion("CANCEL", "Cancel");
            String tResult = tUserChoice.poseQuestion(mView.getContext());

            switch (tResult) {
                case "DB":
                    offerDbChoice();
                    break;
                case "MUSIC":
                    offerMusicChoice();
                    break;
                case "REPORT":
                    String tText;
                    tText = "Time: " +
                            new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());
                    ReportSystem.receive(tText);
                    break;

                case "CANCEL":
                default:
                    Logg.i(TAG, "default");
            }
        }
    }

    void offerDbChoice() {
        Logg.i(TAG, "DB Choice");
        if (mAppIconActive) {
            UserChoice tUserChoice = new UserChoice("Database");
            tUserChoice.addQuestion("DOWNLOAD", "Download current SCD");
            tUserChoice.addQuestion("PRINT", "Show version dates");
            tUserChoice.addQuestion("REPORT", "Report size of Ldb tables");
            tUserChoice.addQuestion("PURGE", "Purge all Ldb tables, no more questions!");
            tUserChoice.addQuestion("DELETE", "Delete Ldb, no more questions!");
            tUserChoice.addQuestion("CREATE", "Create Ldb, no more questions!");
            tUserChoice.addQuestion("CANCEL", "Cancel");
            String tResult = tUserChoice.poseQuestion(mView.getContext());

            switch (tResult) {
                case "DOWNLOAD":
                    executeDownload();
                    break;
                case "PRINT":
                    executePrintScd();
                    break;
                case "PURGE":
                    executePurgeLdb();
                    break;
                case "REPORT":
                    executeReportLdb();
                    break;
                case "DELETE":
                    executeDeleteLdb();
                    break;
                case "CREATE":
                    executeCreateLdb();
                    break;
                case "CANCEL":
                default:
                    Logg.i(TAG, "defaultt");
            }
        }
    }

    void offerMusicChoice() {
        Logg.i(TAG, "Music Choice");
        if (mAppIconActive) {
            UserChoice tUserChoice = new UserChoice("Database");
            tUserChoice.addQuestion("SCAN", "Scan for mp3 files");
            tUserChoice.addQuestion("IDENTIFY", "Identify pairs");
            tUserChoice.addQuestion("SYNCHRONIZE", "Write pairs to music files");
            tUserChoice.addQuestion("DIAGRAM", "download diagram");
            tUserChoice.addQuestion("REPORT", "Report on mp3 files");
            tUserChoice.addQuestion("CANCEL", "Cancel");
            String tResult = tUserChoice.poseQuestion(mView.getContext());

            switch (tResult) {
                case "SCAN":
                    executeMusicScan();
                    break;
                case "IDENTIFY":
                    executeMusicIdentifyPairing();
                    break;
                case "SYNCHRONIZE":
                    executeMusicSynchronizePairing();
                    break;
                case "DIAGRAM":
                    executeRequestDiagram();
                    break;
                case "CANCEL":
                default:
                    Logg.i(TAG, "defaultt");
            }
        }
    }


    void executeDownload() {
        Runnable tRunnable = new Runnable() {
            @Override
            public void run() {
                DataService.prepSslStatic(mView.getContext());
                Ldb_Helper.createInstance(mView.getContext());
                Scddb_File.getInstance().readFromWeb();
                Logg.i(TAG, "finished");
            }
        };
        Thread tThread = new Thread(tRunnable);
        tThread.start();
    }

    void executePrintScd() {
        Runnable tRunnable = new Runnable() {
            @Override
            public void run() {
                DataService.prepSslStatic(mView.getContext());
                Ldb_Helper.createInstance(mView.getContext());
                Date tWebDate;
                Date tLocalDate;
                tWebDate = Scddb_File.getInstance().getLastWebdate();
                tLocalDate = Scddb_File.getInstance().getLocalScddbDate();
                SimpleDateFormat tSimpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.ENGLISH);

                if (tWebDate == null) {
                    Logg.w(TAG, "Webdate not available");
                    return;
                }
                if (tLocalDate == null) {
                    Logg.w(TAG, "Localdate not available");
                    return;
                }

                String tText = String.format(Locale.ENGLISH,
                        "Web from %s; Local version from  %s",
                        tSimpleDateFormat.format(tWebDate),
                        tSimpleDateFormat.format(tLocalDate));

                ReportSystem.receive(tText);
                Logg.i(TAG, "finished" + tText);
            }
        };
        Thread tThread = new Thread(tRunnable);
        tThread.start();

    }

    void executePurgeLdb() {
        Runnable tRunnable = new Runnable() {
            @Override
            public void run() {
                Ldb_Helper.createInstance(mView.getContext());
                Ldb_Helper.getInstance().purgeTables();
                Logg.i(TAG, "finished purge ");
            }
        };
        Thread tThread = new Thread(tRunnable);
        tThread.start();
    }

    void executeReportLdb() {
        Runnable tRunnable = new Runnable() {
            @Override
            public void run() {
                Ldb_Helper.createInstance(mView.getContext());
                Ldb_Helper.getInstance().report();
                Logg.i(TAG, "finished report ");
            }
        };
        Thread tThread = new Thread(tRunnable);
        tThread.start();
    }

    void executeDeleteLdb() {
        Runnable tRunnable = new Runnable() {
            @Override
            public void run() {

                try {
                    String tDatabaseName = Ldb_Helper.getNameofDatabase();
                    Ldb_Helper.getInstance().closeDB();
                    Ldb_Helper.getInstance().deleteLdb(mView.getContext());
                    //mView.getContext().deleteDatabase(tDatabaseName);
                    Ldb_Helper.destroyInstance();
                    String tText = "Ldb deleted ";
                    ReportSystem.receive(tText);

                } catch(Exception e) {
                    Logg.w(TAG, e.toString());
                }

                Logg.i(TAG, "finished delete ");
            }
        };
        Thread tThread = new Thread(tRunnable);
        tThread.start();
    }

    void executeCreateLdb() {
        Runnable tRunnable = new Runnable() {
            @Override
            public void run() {

                try {
                    Ldb_Helper.createInstance(mView.getContext());
                    Ldb_Helper tLdbHelper;
                    tLdbHelper = Ldb_Helper.getInstance();
                    tLdbHelper.prepareTables();
//                    String tDatabaseName = Ldb_Helper.getNameofDatabase();
//                    Ldb_Helper.getInstance().closeDB();
//                    Ldb_Helper.getInstance().deleteLdb(mView.getContext());
//                    //mView.getContext().deleteDatabase(tDatabaseName);
//                    Ldb_Helper.destroyInstance();
                    String tText = "Ldb created";
                    ReportSystem.receive(tText);

                } catch(Exception e) {
                    Logg.w(TAG, e.toString());
                }

                Logg.i(TAG, "finished create");
            }
        };
        Thread tThread = new Thread(tRunnable);
        tThread.start();
    }

    void executeMusicScan() {
        Runnable tRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    DataServiceSingleton.getInstance().getDataService().
                            scanMusic(mView.getContext(), null);
                    String tText = "Scan call done";
                    ReportSystem.receive(tText);
                } catch(Exception e) {
                    Logg.w(TAG, e.toString());
                }
                Logg.i(TAG, "finished create");
            }
        };
        Thread tThread = new Thread(tRunnable);
        tThread.start();
    }

    void executeMusicIdentifyPairing() {
        DataServiceSingleton.getInstance().getDataService().executeIdentifyPairing(null);
        String tText = "IdentifyPairing call done";
        ReportSystem.receive(tText);
    }

    void executeMusicSynchronizePairing() {
        DataServiceSingleton.getInstance().getDataService().executeSynchronizePairing(null);
        String tText = "SynchronizePairing call done";
        ReportSystem.receive(tText);
    }

    void executeRequestDiagram() {
        Runnable tRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    DiagramManager t = new DiagramManager();
                    t.downloadAbsentDiagrams();
                } catch(Exception e) {
                    Logg.w(TAG, e.toString());
                }
                String tText = "download of diagrams done";
                ReportSystem.receive(tText);
            }
        };
        Thread tThread = new Thread(tRunnable);
        tThread.start();
        String tText = "download absent diagrams call done";
        ReportSystem.receive(tText);
    }


    void refreshPlayerControl() {
        if (mIV_PlayerControl != null) {
            int tResourceId;
            if (mMediaPlayerState == STARTED) {
                tResourceId = R.drawable.ic_action_pause;
                mMediaNext = "pause";
            } else {
                tResourceId = R.drawable.ic_action_play;
                mMediaNext = "play";
            }

            mIV_PlayerControl.setVisibility(View.VISIBLE);
            mIV_PlayerControl.setImageResource(tResourceId);
        }
    }

    void appIconLongClick() {
        mAppIconActive = !mAppIconActive;
    }

//Interface

    public boolean onMenuItemClick(MenuItem item) {
        if (mShouting != null) {
            Shout tShout = new Shout("TopBar_Fragment");
            tShout.mLastObject = String.valueOf(item.getTitle());
            tShout.mLastAction = "requested";
            mShouting.shoutUp(tShout);
        }
        return false;
    }


    /**
     * Receive the shout
     *
     * @param tShoutToCeiling the shout
     */
    @Override
    public void shoutUp(Shout tShoutToCeiling) {
        //       Logg.i(TAG, tShoutToCeiling.toString());
        mGlassFloor = tShoutToCeiling;
        process_shouting();
    }
}