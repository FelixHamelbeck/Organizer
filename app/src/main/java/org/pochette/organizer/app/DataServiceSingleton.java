package org.pochette.organizer.app;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;

import org.pochette.data_library.database_management.Scddb_Helper;
import org.pochette.data_library.database_management.DataService;
import org.pochette.organizer.diagram.DiagramManager;
import org.pochette.utils_lib.logg.Logg;
import org.pochette.utils_lib.shouting.Shout;
import org.pochette.utils_lib.shouting.Shouting;

import java.util.Locale;

import static java.lang.Thread.sleep;

public
class DataServiceSingleton implements Shouting {

    private static final String TAG = "FEHA (DataServiceSingleton)";

    // variables
    @SuppressLint("StaticFieldLeak")
    private static DataServiceSingleton mInstance;
    private DataService mDataService;
    private Application mApplication;

    private Context mContext; // release once service is connected
    private boolean mServiceBound;
    // private final boolean mMusicScanExecuted;
    // private final boolean mPairingExecuted;
    private ServiceConnection mServiceConnection;
    private Shouting mShouting;
    // private boolean mDeleteLdbOnStart;

    private Shouting mApplicationShouting;// the Application as Shouting

    // constructor

    public DataServiceSingleton(Context iContext, Application iApplication) {
        mContext = iContext;
        mApplication = iApplication;
        mServiceBound = false;
        //    mMusicScanExecuted = true;
        //   mPairingExecuted = true;
        mServiceConnection = null;
        mDataService = null;
        //   mDeleteLdbOnStart = false;
    }

    public static void createInstance(Context iContext, Application iApplication) {
        if (iContext == null) {
            throw new IllegalArgumentException("Context needed");
        }
        //Double check locking pattern
        if (mInstance == null) { //Check for the first time
            synchronized (DataServiceSingleton.class) {   //Check for the second time.
                if (mInstance == null) {
                    mInstance = new DataServiceSingleton(iContext, iApplication);
                }
            }
        }
    }

    public static DataServiceSingleton getInstance() {
        if (mInstance == null) { //Check for the first time
            synchronized (DataServiceSingleton.class) {   //Check for the second time.
                if (mInstance == null) {
                    throw new RuntimeException("First call to DataServiceSingleton " +
                            "should be createInstance with Context");
                }
            }
        }
        return mInstance;
    }

    // setter and getter
//    public boolean isMusicScanExecuted() {
//        return mMusicScanExecuted;
//    }
//
//    public boolean isPairingExecuted() {
//        return mPairingExecuted;
//    }

    public void setShouting(Shouting shouting) {
        mShouting = shouting;
    }

//    public void setDeleteLdbOnStart(boolean iDeleteLdbOnStart) {
//        mDeleteLdbOnStart = iDeleteLdbOnStart;
//    }

    public DataService getDataService() {
        if (mServiceBound) {
            return mDataService;
        }

        mDataService = new DataService(mContext);
        mServiceBound = true;

        if (1 == 1) {
            return mDataService;
        }


        while (!mServiceBound) {
            try {
                //noinspection BusyWait
                sleep(500);
            } catch(InterruptedException e) {
                Logg.w(TAG, e.toString());
            }
        }
        if (mDataService == null) {
            throw new RuntimeException("DataService should have started");
        }
//        Logg.i(TAG, "Service up, waiting for DB");
//        while (!mDataService.isDatabaseReady()) {
//            try {
//                //noinspection BusyWait
//                sleep(43);
//            } catch(InterruptedException e) {
//                Logg.i(TAG, e.toString());
//            }
//        }
//        Logg.i(TAG, "Service up, DB up " + Thread.currentThread().toString());
        return mDataService;
    }
    // lifecylce and override
    // internal

//    void createServiceConnection() {
//        mServiceConnection = new ServiceConnection() {
//            @Override
//            public void onServiceDisconnected(ComponentName name) {
//                mServiceBound = false;
//                Logg.w(TAG, "onService Disconnected "+ name.flattenToShortString());
//            }
//
//            @Override
//            public void onBindingDied(ComponentName name) {
//
//                Logg.w(TAG, "onService onBindingDied "+ name.flattenToShortString());
//            }
//
//            @Override
//            public void onNullBinding(ComponentName name) {
//                Logg.w(TAG, "onService onNullBinding "+ name.flattenToShortString());
//            }
//
//            @Override
//            public void onServiceConnected(ComponentName name, IBinder iService) {
//                Logg.w(TAG, "onServiceConnected "+ name.flattenToShortString());
//                mContext = null; // release context to avoid leak
//                mServiceBound = true;
//                mDataService = ((DataService.LocalBinder) iService).getService();
//                processServiceConnected();
//            }
//        };
//    }

//    void startService(Application iApplication) {
//        try {
//            // Create the service Intent.
//            if (mContext == null) {
//                Logg.w(TAG, "Context must not be null");
//                return;
//            }
//            Intent tIntent =
//                    new Intent(mContext, DataService.class);
//            if (tIntent == null) {
//                Logg.w(TAG, "ServiceIntent must not be null");
//                return;
//            }
//            // Bind the service and grab a reference to the binder.
//            if (mServiceConnection == null) {
//                Logg.w(TAG, "ServiceConnection must not be null");
//                return;
//            }
//            if (DataService.isRunning(iApplication)) {
//
//            } else {
//
//            }
//
//
//
//            try {
//                boolean tResult;
//                Logg.w(TAG, "call bindService ");
//                ComponentName tComponentName =
//                mContext.startService(tIntent);
////                tResult = mContext.bindService(
////                        tServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
//
//                Logg.i(TAG, "bindService returned " + tComponentName.flattenToShortString());
//            } catch(Exception e) {
//                Logg.w(TAG, e.toString());
//            }
//        } catch(Exception e) {
//            e.printStackTrace();
//        }
//    }

    void processServiceConnected() {
        Logg.i(TAG, "Start processServiceConnected");
        if (mDataService == null) {
            Logg.i(TAG, "null");
        } else {
            mDataService.setShouting(this);
            if (mApplicationShouting != null) {
                Shout tShout = new Shout(this.getClass().getSimpleName());
                tShout.mLastObject = "DataService";
                tShout.mLastAction = "connected";
                mApplicationShouting.shoutUp(tShout);
            }
            //         mDataService.prepareEverything(mDeleteLdbOnStart);
        }
    }

    void processShouting(Shout iShout) {
        Logg.i(TAG, iShout.toString());
        if (iShout.mActor.equals("DataService")) {
//            if (iShout.mLastObject.equals("PairingProcess") &&
//                    iShout.mLastAction.equals("finished")) {
//                DiagramManager tDiagramManager = new DiagramManager();
//                Logg.i(TAG, "call downloadAbsentDiagram");
//                DataServiceSingleton.getInstance().reportDiagram();
//                tDiagramManager.downloadAbsentDiagrams();
//                DataServiceSingleton.getInstance().reportDiagram();
//            }
            if (iShout.mLastObject.equals("Database") &&
                    iShout.mLastAction.equals("prepared")) {
                Logg.i(TAG, "DB is prepared");
                if (mShouting != null) {
                    Shout tShout = new Shout(this.getClass().getSimpleName());
                    tShout.mLastObject = "Database";
                    tShout.mLastAction = "prepared";
                    mShouting.shoutUp(tShout);
                }
            }
        }

    }

    // public methods


    public void reportDiagram() {
        Cursor tCursor = null;
        SQLiteDatabase tSqLiteDatabase = Scddb_Helper.getInstance().getReadableDatabase();
        String tSql = "SELECT AVG(LENGTH(BITMAP)) AS AVG_SIZE , " +
                "SUM(LENGTH(BITMAP)) AS TOT_SIZE , COUNT(*) AS COUNT FROM LDB.DIAGRAM ";
        long tTotSize;
        long tAvgSize;
        long tCount;

        try {
            tCursor = tSqLiteDatabase.rawQuery(tSql, null);
            tCursor.moveToFirst();
            Logg.i(TAG, "count" + tCursor.getCount());
            tTotSize = tCursor.getLong(tCursor.getColumnIndex("TOT_SIZE"));
            tAvgSize = tCursor.getLong(tCursor.getColumnIndex("AVG_SIZE"));
            tCount = tCursor.getLong(tCursor.getColumnIndex("COUNT"));
        } catch(Exception e) {
            Logg.w(TAG, e.toString());
            return;
        } finally {
            if (tCursor != null) {
                tCursor.close();
            }
        }
        String tString = String.format(Locale.ENGLISH,
                "%d diagrams with avg size of %5.2f kBytes use up %5.2f MBytes",
                tCount, tAvgSize / 1000.f, tTotSize / 1E6f);
        Logg.i(TAG, tString);
    }

//    public void prepDataService() {
//        Thread tThread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                createServiceConnection();
//                startService(mApplication);
//                Logg.i(TAG, "finished prepDataService: CreateService done and bindService called");
//            }
//        });
//        tThread.setName("prepDataService");
//        tThread.start();
//    }

//    public void requestDataService(Shouting iShouting) {
//        mApplicationShouting = iShouting;
//        createServiceConnection();
//        Logg.i(TAG, "past call createServiceConnection");
//        startService(mApplication);
//        Logg.i(TAG, "past call startService");
//    }


    public void shoutUp(Shout iShoutToCeiling) {
        processShouting(iShoutToCeiling);
    }


}
