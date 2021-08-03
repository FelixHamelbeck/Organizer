package org.pochette.organizer.app;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import org.pochette.data_library.database_management.DataService;
import org.pochette.data_library.database_management.Scddb_Helper;
import org.pochette.data_library.music.MusicScan;
import org.pochette.organizer.BuildConfig;
import org.pochette.organizer.diagram.DiagramThread;
import org.pochette.organizer.formation.FormationData;
import org.pochette.utils_lib.logg.Logg;
import org.pochette.utils_lib.logg.LoggFile;
import org.pochette.utils_lib.shouting.Shout;
import org.pochette.utils_lib.shouting.Shouting;

import static org.pochette.organizer.app.OrganizerStatus.DATABASE;
import static org.pochette.organizer.app.OrganizerStatus.DATA_SERVICE;
import static org.pochette.organizer.app.OrganizerStatus.DIAGRAM;
import static org.pochette.organizer.app.OrganizerStatus.FORMATION;
import static org.pochette.organizer.app.OrganizerStatus.MEDIA_PLAYER;
import static org.pochette.organizer.app.OrganizerStatus.MUSIC_SCAN;
import static org.pochette.organizer.app.OrganizerStatus.PAIRING_IDENTIFICATION;
import static org.pochette.organizer.app.OrganizerStatus.PAIRING_SYNCHRONOISATION;
import static org.pochette.organizer.app.OrganizerStatus.STATUS_FAILED;
import static org.pochette.organizer.app.OrganizerStatus.STATUS_OPEN;
import static org.pochette.organizer.app.OrganizerStatus.STATUS_SETUP_RUNNING;
import static org.pochette.organizer.app.OrganizerStatus.STATUS_SUCCESS;

@SuppressWarnings("FieldCanBeLocal")
public class OrganizerApp extends Application implements Shouting {

    private static final String TAG = "FEHA (OrganizerApp)";

    // variables
    private final OrganizerThread mOrganizerThread;
    private DiagramThread mDiagramThread;
    private boolean mHoldPreparation;

    Thread mThreadCreateCalls;
    Shout mGlassFloor;

    // constructor
    public OrganizerApp() {
        // this method fires only once per application start.
        // getApplicationContext returns null here
        Log.i(TAG, "Time: Constructor fired");

        mHoldPreparation = false;
        PreparationOfLogg.execute(BuildConfig.DEBUG);
        OrganizerStatus.createInstance(this, this);
        mOrganizerThread = new OrganizerThread();
        mOrganizerThread.mOrganizerApp = this;
        mOrganizerThread.start();
        Logg.d(TAG, "Time: end of constructor");
    }
//    @Override
//    void attachBaseContext(Context iBase) {
//        super.attachBaseContext(iBase);
//        Logg.i(TAG, "attachBaseContext");
//    }

// lifecylce and override

    @Override
    public void onCreate() {
        Logg.d(TAG, "Time: onCreate starte");
        super.onCreate();
        startEverything();
        Logg.d(TAG, "Time: onCreate finished");
    }

    @Override
    protected void attachBaseContext(Context iBase) {
        super.attachBaseContext(iBase);
        LoggFile.initialize(this);
        LoggFile.startLoggFile(this);
        Logg.i(TAG, "attachBaseContext");
        Logg.d(TAG, "Time: attachBaseContext finished");
    }

    public String toString() {
        try {
            OrganizerStatus tOrganizerStatus = OrganizerStatus.getInstance();
            return tOrganizerStatus.toString();
        } catch(Exception e) {
            return "Status not yet set";
        }
    }

    void startEverything() {
        initStatus(false);
        // this method fires once as well as constructor
        // but also application has context here
        if (mThreadCreateCalls == null || !mThreadCreateCalls.isAlive()) {
            Runnable tRunnable = this::onCreateCalls;
            mThreadCreateCalls = new Thread(tRunnable, "onCreateCalls");
            mThreadCreateCalls.start();
        }

        Logg.d(TAG, "Time: startEverything finished");
     //   PreparationOfBluetooth.execute(this);
    }


    void initStatus(@SuppressWarnings("SameParameterValue") boolean iForceAll) {

        OrganizerStatus.getInstance().setStatus(DATA_SERVICE, STATUS_OPEN);
        OrganizerStatus.getInstance().setStatus(DATABASE, STATUS_OPEN);
        OrganizerStatus.getInstance().setStatus(MEDIA_PLAYER, STATUS_OPEN);
        OrganizerStatus.getInstance().setStatus(FORMATION, STATUS_OPEN);
        // by default mFlagDataPreparation = false and all the datasetup is done
        if (iForceAll) {  // devlopment
            OrganizerStatus.getInstance().setStatus(MUSIC_SCAN, STATUS_OPEN);
            OrganizerStatus.getInstance().setStatus(PAIRING_IDENTIFICATION, STATUS_OPEN);
            OrganizerStatus.getInstance().setStatus(PAIRING_SYNCHRONOISATION, STATUS_OPEN);
            OrganizerStatus.getInstance().setStatus(DIAGRAM, STATUS_OPEN);

        } else {
            Logg.i(TAG, "prep of data waiting for splash");
            OrganizerStatus.getInstance().setStatus(MUSIC_SCAN, STATUS_SUCCESS);
            OrganizerStatus.getInstance().setStatus(PAIRING_IDENTIFICATION, STATUS_SUCCESS);
            OrganizerStatus.getInstance().setStatus(PAIRING_SYNCHRONOISATION, STATUS_SUCCESS);
            OrganizerStatus.getInstance().setStatus(DIAGRAM, STATUS_SUCCESS);
        }
        Logg.d(TAG, "Time: initStatus finished");
    }


    // setter and getter


    public void setFlagDataPreparation(boolean flagDataPreparation) {
        Logg.i(TAG, "setFlagPreapration to " + flagDataPreparation);
        OrganizerStatus.getInstance().setStatus(MUSIC_SCAN, STATUS_OPEN);
        OrganizerStatus.getInstance().setStatus(PAIRING_IDENTIFICATION, STATUS_OPEN);
        OrganizerStatus.getInstance().setStatus(PAIRING_SYNCHRONOISATION, STATUS_OPEN);
        OrganizerStatus.getInstance().setStatus(DIAGRAM, STATUS_OPEN);
     //   executeNextAction();
    }

    public void holdPreparation() {

        mHoldPreparation = true;
        Logg.i(TAG, "HoldPreparation set to true");
        initStatus(false);
    }

    public void startFromBeginning(boolean iFlagDataPreparation) {
        setFlagDataPreparation(iFlagDataPreparation);
        mHoldPreparation = false;
        Logg.i(TAG, "HoldPreparation set to false");
        startEverything();
    }


// internal

    void onCreateCalls() {
        Logg.i(TAG, "onCreateCalls started");
        PreparationOfPreference.execute(this);
        Logg.i(TAG, "past PrepOfPreference");

        if (OrganizerStatus.getInstance().getStatus(DATA_SERVICE).equals(STATUS_OPEN)) {
            DataServiceSingleton.createInstance(this, this);
            DataServiceSingleton.getInstance().setShouting(this);
            Logg.i(TAG, "Call requestDataService");
            DataService tDS =
                    DataServiceSingleton.getInstance().getDataService();
            if (tDS != null) {
                OrganizerStatus.getInstance().setStatus(DATA_SERVICE, STATUS_SUCCESS);
                Logg.w(TAG, "DataService -> Success");
//                requestDatabase();
//                requestMediaPlayer();
                executeNextAction();
            } else {
                Logg.w(TAG, "not happy");
            }
        }
        //    DataServiceSingleton.getInstance().setDeleteLdbOnStart(false); // reset LDB with true
        //     DataServiceSingleton.getInstance().prepDataService();
        Logg.i(TAG, "past call prepDataService");

        Logg.i(TAG, "onCreateCalls finished");
    }


    void requestMediaPlayer() {
//        DataService tDataService = DataServiceSingleton.getInstance().getDataService();

        OrganizerStatus.getInstance().setStatus(MEDIA_PLAYER, STATUS_SETUP_RUNNING);
        Logg.i(TAG, "call  MediaPlayerServiceSingleton");
        MediaPlayerServiceSingleton.createInstance(this);
        MediaPlayerServiceSingleton.getInstance().prepMediaPlayerService();
        Logg.i(TAG, "past call MediaPlayerServiceSingleton.getInstance().prepMediaPlayerService()");
        OrganizerStatus.getInstance().setStatus(MEDIA_PLAYER, STATUS_SUCCESS);
    }

    /**
     * this method may be call repeately
     */
    void requestDatabase() {
        Logg.i(TAG, "Start requestDatabase");
        OrganizerStatus.getInstance().setStatus(DATABASE, STATUS_SETUP_RUNNING);
        if (!OrganizerStatus.getInstance().getStatus(DATA_SERVICE).equals(STATUS_SUCCESS)) {
            Logg.w(TAG, "Without DataServiceStatus = Success the database is not managable");
            return;
        }
        DataService tDataService = DataServiceSingleton.getInstance().getDataService();
        if (tDataService == null) {
            Logg.w(TAG, "Without DataService the database is not managable");
            return;
        }
        // 0 Ssl
        tDataService.prepareSsl();
        Logg.i(TAG, "Past prepare Ssl");
        // 1 LdbDatabase
        boolean tLdbPrepared;
        try {
            tLdbPrepared = tDataService.prepareLdb(false);
        } catch(Exception e) {
            tLdbPrepared = false;
            Logg.w(TAG, e.toString());
        }
        Logg.i(TAG, "Past prepareLdb");
        if (!tLdbPrepared) {
            OrganizerStatus.getInstance().setStatus(DATABASE, STATUS_FAILED);
            return;
        }
        // 2 Scddb_File
        //  tDataService.deleteScddbDbFile(); infinite loop!

        if (tDataService.isScddbDbFileAvailable()) {
            Logg.i(TAG, "ScddbFile is available");
            Scddb_Helper.createInstance(this.getApplicationContext());
        } else {
            Logg.i(TAG, "Call download of ScddbFile");
            tDataService.downloadScddb(this);
            return;
        }
        // 3 attach
        boolean tAttachWorked;
        try {
            if (Scddb_Helper.getInstance() == null) {
                tAttachWorked = false;
            } else {
                tDataService.attachLdbToScddb();
                tAttachWorked = true;
            }
        } catch(Exception e) {
            Logg.w(TAG, e.toString());
            tAttachWorked = false;
        }
        Logg.d(TAG, "Time: attached="+tAttachWorked);
        if (tAttachWorked) {
            OrganizerStatus.getInstance().setStatus(DATABASE, STATUS_SUCCESS);
            Logg.w(TAG, "DataBase -> Success");
        } else {
            OrganizerStatus.getInstance().setStatus(DATABASE, STATUS_FAILED);
        }


        Logg.i(TAG, "finished requestDatabse with status " + OrganizerStatus.getInstance().getStatus(DATABASE));
        Logg.d(TAG, "Time: RequestDatabase finished" + OrganizerStatus.getInstance().getStatus(DATABASE));

//        DataService sDataService = DataServiceSingleton.getInstance().getDataService();
//
//        int tSize= Scddb_Helper.getInstance().getSize("Dance");
//        Logg.d(TAG, "Time: past size" + tSize);

     //   executeNextAction();
//        if (OrganizerStatus.getInstance().getStatus(DATABASE).equals(STATUS_SUCCESS)) {
//            Logg.i(TAG, "call DB Prep from request DB");
        //  processDatabasePrepared();
//        }

    }


    void startDiagramThread() {
        if (mDiagramThread != null) {
            if (mDiagramThread.isInterrupted()) {
                mDiagramThread.start();
            }
            return;
        }
        mDiagramThread = new DiagramThread();
        mDiagramThread.start();

        OrganizerStatus.getInstance().setStatus(DIAGRAM, STATUS_SUCCESS);

        Logg.w(TAG, "Diagram -> Success");
     //   executeNextAction();
    }

    void requestFormationData() {
        if (OrganizerStatus.getInstance().getStatus(DATABASE).equals(STATUS_SUCCESS)) {
            OrganizerStatus.getInstance().setStatus(FORMATION, STATUS_SETUP_RUNNING);
          //  new FormationData();
            // todo reactivate
            OrganizerStatus.getInstance().setStatus(FORMATION, STATUS_SUCCESS);
            Logg.w(TAG, "Formation-> Success");
            Logg.i(TAG, "Past new FormationData");
       //     executeNextAction();
        }
    }





    /**
     * the method executeNextAction may be called at any time
     * controll is completley by the status fields
     * hence the status changes must be done before hand
     */

    boolean executeNextAction() {
        if (mHoldPreparation) {
            Logg.i(TAG, "onHold");
            return false;
        }
        boolean tAnyAction = false;
        //Logg.i(TAG, "exec-> "+this.toString());
        // the dataService becomes available
        if (OrganizerStatus.getInstance().getStatus(DATA_SERVICE).equals(STATUS_SUCCESS) &&
                OrganizerStatus.getInstance().getStatus(DATABASE).equals(STATUS_OPEN)) {
            Logg.d(TAG, "Time: call requestDatabase");
            requestDatabase();
            tAnyAction = true;
            Logg.d(TAG, "Time: past call requestDatabase");
        }
        if (OrganizerStatus.getInstance().getStatus(DATA_SERVICE).equals(STATUS_SUCCESS) &&
                OrganizerStatus.getInstance().getStatus(MEDIA_PLAYER).equals(STATUS_OPEN)) {

            Logg.d(TAG, "Time: call requestMediaplayer");
            requestMediaPlayer();
            tAnyAction = true;
            Logg.d(TAG, "Time: past call requestMediaplayer");
            // return immediately
        }
        // the dataBase becomes available
        if (OrganizerStatus.getInstance().getStatus(DATABASE).equals(STATUS_SUCCESS) &&
                OrganizerStatus.getInstance().getStatus(FORMATION).equals(STATUS_OPEN)) {
            Logg.d(TAG, "Time: call requestFormayionData");
            requestFormationData();
            tAnyAction = true;
            Logg.d(TAG, "Time: past call requestFormationDate");
            // return immediately
        }
        if (OrganizerStatus.getInstance().getStatus(DATABASE).equals(STATUS_SUCCESS) &&
                OrganizerStatus.getInstance().getStatus(MUSIC_SCAN).equals(STATUS_OPEN)) {

            Logg.d(TAG, "Time: call MusicScan");
            Logg.i(TAG, "Call MusicScan.excute");

            OrganizerStatus.getInstance().setStatus(MUSIC_SCAN, STATUS_SETUP_RUNNING);
            MusicScan tMusicScan = new MusicScan(this);
            tMusicScan.setShouting(this);
            tMusicScan.execute(); // this starts a separate thread
            tAnyAction = true;
            Logg.i(TAG, "Past MusicScan.execute (Thread may be running");

            Logg.d(TAG, "Time: past call requestMusicsacn");
            // continued in shouting
        }
        if (OrganizerStatus.getInstance().getStatus(DATABASE).equals(STATUS_SUCCESS) &&
                OrganizerStatus.getInstance().getStatus(MUSIC_SCAN).equals(STATUS_SUCCESS) &&
                OrganizerStatus.getInstance().getStatus(PAIRING_IDENTIFICATION).equals(STATUS_OPEN)) {
            OrganizerStatus.getInstance().setStatus(PAIRING_IDENTIFICATION, STATUS_SETUP_RUNNING);
            DataService tDataService = DataServiceSingleton.getInstance().getDataService();

            Logg.d(TAG, "Time: call identify Pairing");
            tDataService.executeIdentifyPairing(this);
            tAnyAction = true;
            Logg.d(TAG, "Time: past call identify Pairing");
            Logg.i(TAG, "Past Pairing Ident (Thread may be running");
        }


        if (OrganizerStatus.getInstance().getStatus(DATABASE).equals(STATUS_SUCCESS) &&
                OrganizerStatus.getInstance().getStatus(PAIRING_IDENTIFICATION).equals(STATUS_SUCCESS) &&
                OrganizerStatus.getInstance().getStatus(PAIRING_SYNCHRONOISATION).equals(STATUS_OPEN)) {
            OrganizerStatus.getInstance().setStatus(PAIRING_SYNCHRONOISATION, STATUS_SETUP_RUNNING);
            DataService tDataService = DataServiceSingleton.getInstance().getDataService();
            tDataService.executeSynchronizePairing(this);
            tAnyAction = true;
            Logg.d(TAG, "Time: call synchronize Pairing");
            Logg.i(TAG, "Past Pairing Sync (Thread may be running");
            Logg.d(TAG, "Time: past call synchronize Pairing");
        }


        if (OrganizerStatus.getInstance().getStatus(DATABASE).equals(STATUS_SUCCESS) &&
                OrganizerStatus.getInstance().getStatus(PAIRING_IDENTIFICATION).equals(STATUS_SUCCESS) &&
                OrganizerStatus.getInstance().getStatus(DIAGRAM).equals(STATUS_OPEN)) {
            Logg.d(TAG, "Time: call Diagram");
            startDiagramThread();
            tAnyAction = true;
            Logg.d(TAG, "Time: past call Diagram");
        }
        return tAnyAction;
    }

    void processShouting() {
        switch (mGlassFloor.mActor) {
            case "DataServiceSingleton":
                // not in use
//                if (mGlassFloor.mLastAction.equals("prepared") &&
//                        mGlassFloor.mLastObject.equals("Database")) {
//
//                    Logg.i(TAG, "call DB Prep from  process shouting");
//                    processDatabasePrepared();
//                }
                if (mGlassFloor.mLastAction.equals("connected") &&
                        mGlassFloor.mLastObject.equals("DataService")) {

                    processDataServiceConnected();
                }
                break;
            case "DataService":
                if (mGlassFloor.mLastAction.equals("succeeded") &&
                        mGlassFloor.mLastObject.equals("DownloadScddb")) {
                    requestDatabase(); // this would not be the first call
                }
                if (mGlassFloor.mLastAction.equals("connected") &&
                        mGlassFloor.mLastObject.equals("DataService")) {
                    Logg.w(TAG, "does this ever happen?");
                    //      processDataServiceConnected();
                }
                if (mGlassFloor.mLastAction.equals("finished")) {
                    if (mGlassFloor.mLastObject.equals("PairingProcess.Identification")) {
                        Logg.i(TAG, "second call");

                        OrganizerStatus.getInstance().setStatus(PAIRING_IDENTIFICATION, STATUS_SUCCESS);
                        Logg.w(TAG, "PairingIdentification-> Success");
                        executeNextAction();
                        //startPairing(true, false);
                    } else if (mGlassFloor.mLastObject.equals("PairingProcess.Synchronisation")) {
                        OrganizerStatus.getInstance().setStatus(PAIRING_SYNCHRONOISATION, STATUS_SUCCESS);
                        Logg.w(TAG, "PairingSynchronoisation-> Success");
      //                  executeNextAction();
                    }
                }
                break;
            case "MusicScan":
                if (mGlassFloor.mLastAction.equals("succeeded") &&
                        mGlassFloor.mLastObject.equals("Execution")) {
                    //processMusicScanSuccess();
                    Logg.w(TAG, "MusicScan -> Success");
                    OrganizerStatus.getInstance().setStatus(MUSIC_SCAN, STATUS_SUCCESS);
      //              executeNextAction();
                }

                break;
        }
    }

    void processDataServiceConnected() {
        Logg.i(TAG, "processDataServiceConnected");
        try {
            DataService tDataService = DataServiceSingleton.getInstance().getDataService();
            if (tDataService != null) {
                OrganizerStatus.getInstance().setStatus(DATA_SERVICE, STATUS_SUCCESS);

                Logg.w(TAG, "DataService -> Success");
                //  Logg.i(TAG, "DataServiceStatus = Success");
            } else {
                OrganizerStatus.getInstance().setStatus(DATA_SERVICE, STATUS_FAILED);
                Logg.i(TAG, "DataServiceStatus = Failed");
            }
        } catch(Exception e) {
            OrganizerStatus.getInstance().setStatus(DATA_SERVICE, STATUS_FAILED);
            Logg.i(TAG, "DataServiceStatus = Failed");
            Logg.w(TAG, e.toString());
        }
        executeNextAction();
//        move to nextAction
//        if (OrganizerStatus.getInstance().getStatus(DATA_SERVICE).equals(STATUS_SUCCESS)) {
//            requestDatabase();
//        }
//        if (OrganizerStatus.getInstance().getStatus(DATA_SERVICE).equals(STATUS_SUCCESS)) {
//            requestMediaPlayer();
//        }

    }


    // public methods

    public void requestPairingSynchronisation() {
        OrganizerStatus.getInstance().setStatus(PAIRING_SYNCHRONOISATION, STATUS_OPEN);
    }

    @Override
    public void shoutUp(Shout iShoutToCeiling) {
        Logg.i(TAG, iShoutToCeiling.toString());
        mGlassFloor = iShoutToCeiling;
        processShouting();
    }




}
