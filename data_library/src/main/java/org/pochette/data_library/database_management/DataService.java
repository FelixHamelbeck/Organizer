package org.pochette.data_library.database_management;


import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.database.Cursor;
import android.os.Binder;
import android.util.ArraySet;

import com.google.android.gms.security.ProviderInstaller;

import org.pochette.data_library.BuildConfig;
import org.pochette.data_library.music.MusicScan;
import org.pochette.data_library.pairing.PairingProcess;
import org.pochette.utils_lib.logg.Logg;
import org.pochette.utils_lib.search.SearchPattern;
import org.pochette.utils_lib.shouting.Shout;
import org.pochette.utils_lib.shouting.Shouting;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.net.ssl.SSLContext;

/*
  All functions in data_library must be called through DataService
  All functions in data_library are executed synchronously, i.e. none of them starts a thread.
  If asynchronous execution is sensible (MusicScan ...), this is covered in DataService
 */


/**
 * Database Managment<br>
 * public void downloadScddb(Shouting iShouting)
 * download ScddbFile from the web, then shoutup<br>
 * <br>
 * <br>
 * <br>
 * Database Select<br>
 * public &ltT&gt T readFirst(SearchPattern iSearchPattern)
 * execute select and return first row<br>
 * public &ltT&gt ArrayList&ltT&gt readArrayList(SearchPattern iSearchPattern)
 * execute select and return ArrayList of all rows<br>
 * <p>
 * Media service<br>
 * public void scanMusic(Context tContext, Shouting iShouting)
 * read Musicfiles (mp3) from Context.getContentResolver().query, then shoutUp<br>
 * public void identifyPairing(Shouting iShouting)
 * work through signatures to identify pairings of Album and MusicDirectories, then shoutUp<br>
 * public void synchronizePairing(Shouting iShouting)
 * take Pairing and write MusicDirectory and Musicfile, then shoutUp<br>
 */

@SuppressWarnings("rawtypes")
public class DataService implements Shouting {

    private static final String TAG = "FEHA (DataService)";
    // variables
    boolean mScddbFileAvailable;
    boolean mLdbAvailable;
    boolean mDatabaseReady;
    Context mContext;
    Shouting mShouting;

    Shout mGlassFloor;
    // constructor
    // setter and getter
//    public boolean isDatabaseReady() {
//        return mDatabaseReady;
//    }


    public DataService(Context iContext) {
        mContext = iContext;
        init();
    }


    void init() {
        Logg.i(TAG, "init");
        mLdbAvailable = false;
        mScddbFileAvailable = false;
        mDatabaseReady = false;
    }

    public void setShouting(Shouting iShouting) {
        Logg.i(TAG, "setShouting)");
        mShouting = iShouting;
    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        Logg.w(TAG, "onStartCommand");
//        //return super.onStartCommand(intent, flags, startId);
//        return START_NOT_STICKY;
//    }

//    @Override
//    protected void onHandleIntent(@Nullable Intent intent) {
//
//        Logg.w(TAG, "onHandleIntent");
//    }
    //<editor-fold desc="Binder">


    //  private final IBinder mBinder = new LocalBinder();

//
//
//    @Override
//    public IBinder onBind(Intent intent) {
//        Logg.w(TAG, "IBinder onBind");
//      //  return super.onBind(intent);
//        return mBinder;
//    }

    public class LocalBinder extends Binder {


        public DataService getService() {
            Logg.i(TAG, "getService (return LocalBinder)");
            return DataService.this;
        }
    }


    //</editor-fold>

    //<editor-fold desc="Service Lifecycle and override">
    // lifecylce and override


//    @Override
//    public void onCreate() {
//        super.onCreate();
//        Logg.w(TAG, "onCreate");
//        mLdbAvailable = false;
//        mScddbFileAvailable = false;
//        mDatabaseReady = false;
//    }
//
//    @Override
//    public void onDestroy() {
//        Logg.i(TAG, "onDestroy");
//        super.onDestroy();
//    }
    //</editor-fold>

    // internal


    private void prepSSL(Context iContext) {
        try {
            ProviderInstaller.installIfNeeded(iContext);
            SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, null, null);
            sslContext.createSSLEngine();
        } catch(Exception e) {
            Logg.w(TAG, e.toString());
        }
    }


    // static
    public static boolean isRunning(Application iApplication) {
        ActivityManager am = (ActivityManager) iApplication.getSystemService(Activity.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> rs = Objects.requireNonNull(am).getRunningServices(150);

        Logg.w(TAG, "found " + rs.size());
        for (int i = 0; i < rs.size(); i++) {
            ActivityManager.RunningServiceInfo rsi = rs.get(i);
            if (!rsi.process.contains("pochette")) {
                return true;
            }
        }
        return false;
    }

    // public methods


    //<editor-fold desc="public interface ManagementInterface">


    public void downloadScddb(Shouting iShouting) {

        final Shouting fShouting = iShouting;
        Logg.i(TAG, "Start downloadScddb");

        Thread tThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Shout tShout = new Shout(DataService.class.getSimpleName());
                tShout.mLastObject = "DownloadScddb";
                try {
                    Scddb_File tScddb_file;
                    tScddb_file = Scddb_File.getInstance();
                    tScddb_file.readFromWeb();
                    tShout.mLastAction = "succeeded";
                } catch(Exception e) {
                    Logg.w(TAG, e.toString());
                    Logg.w(TAG, "DataService.downloadScddb failed");
                    tShout.mLastAction = "failed";
                }
                if (fShouting != null) {
                    fShouting.shoutUp(tShout);
                }
            }
        });
        tThread.setName("DownloadScddbFile");
        tThread.start();

        Logg.i(TAG, "Finished downloadScddb (Thread might still be running)");
    }

    public Date getLocalScddbDate() {
        return Scddb_File.getInstance().getLocalScddbDate();
    }

    /**
     * This call returns the stored value of the webdate. A process to get an update on the webdate
     * is started asyncronously
     *
     * @return the last webdate received from the web as currently known.
     */
    public Date getLastWebdate() {
        return Scddb_File.getInstance().getLastWebdate();
    }

    public boolean isScddbDbFileAvailable() {
        return Scddb_File.getInstance().isDbFileAvailable();
    }

    public void deleteScddbDbFile() {
        Scddb_File.getInstance().delete();
    }


    public void attachLdbToScddb() {
        Scddb_Helper.getInstance().attachLdbToScddb(this);
    }

    public void prepareEverything(boolean iDeleteLdb) {
        Logg.i(TAG, "Call prepareDatabase with delete " + iDeleteLdb);
        prepareDatabase(iDeleteLdb);
        Logg.i(TAG, "Finished prepareDatabase");
    }

    private void processShouting() {
//        if (iShout.mActor.equals("Scddb_File")) {
//            if (iShout.mLastObject.equals("ScddbWebFile") &&
//                    iShout.mLastAction.equals("downloaded")) {
//                mScddbFileAvailable = true;
//                if (mLdbAvailable) {
//                    attachLdbToScddb();
//                }
//            }
//        }
////        if (iShout.mActor.equals("Scddb_Helper")) {
////            if (iShout.mLastObject.equals("Database") &&
////                    iShout.mLastAction.equals("attached")) {
////                mDatabaseReady = true;
////                if (mShouting != null) {
////                    Shout tShout = new Shout(this.getClass().getSimpleName());
////                    tShout.mLastObject = "Database";
////                    tShout.mLastAction = "prepared";
////                    mShouting.shoutUp(tShout);
////                }
////                scanMusic(mContext, this);
////            }
////        }
////
//
//        if (iShout.mActor.equals("DataService")) {
//            if (iShout.mLastObject.equals("PairingProcess") &&
//                    iShout.mLastAction.equals("finished")) {
//                if (mShouting != null && mShouting != this) {
//                    mShouting.shoutUp(iShout);
//                }
//            }
//        }
    }


    /**
     * This method prepares both databases LDB and SCD
     * A) If requested the local databas LDB is deleted
     * B) if required the local database LDB  and its tables are created
     * C) if the Scddb_File is not available the file is copied from the web
     * D) if possible the ldb is attached to scddb
     *
     * @param iDeleteLdb true, when the local database must be deleted
     */
    public void prepareDatabase(boolean iDeleteLdb) {
        // SSL
        prepSSL(mContext);
        // LDB
        Ldb_Helper tLdbHelper;
        Ldb_Helper.createInstance(mContext);
        tLdbHelper = Ldb_Helper.getInstance();
        if (iDeleteLdb) {
            try {
                Logg.i(TAG, "About to delete LDB");
                tLdbHelper.closeDB();
                Ldb_Helper.destroyInstance();
                boolean tSuccess = mContext.deleteDatabase("Ldb");
                if (!tSuccess) {
                    Logg.w(TAG, "Delete failed");
                }
                Ldb_Helper.createInstance(mContext);
                tLdbHelper = Ldb_Helper.getInstance();
                Logg.i(TAG, "Ldb deleted and created");
            } catch(Exception e) {
                Logg.w(TAG, e.toString());
            }
        }
        if (!tLdbHelper.isLdbAvailable()) {
            Logg.i(TAG, "Call prepareTables for LDB");
            tLdbHelper.prepareTables();
        }
        // SCDDB
        Scddb_Helper.createInstance(mContext);
        Scddb_File tScddb_file;
        tScddb_file = Scddb_File.getInstance();
        if (tScddb_file.isDbFileAvailable()) {
            Logg.i(TAG, "Call attach");
            Scddb_Helper.getInstance().attachLdbToScddb(this);
        } else {
            Logg.i(TAG, "Call Scddb_File.copyFromWeb");
            //     tScddb_file.copyFromWeb(this);
            // attach will be called in process_shouting
        }
    }

    /**
     * This method prepares both databases LDB and SCD
     * A) If requested the local databas LDB is deleted
     * B) if required the local database LDB  and its tables are created
     * C) if the Scddb_File is not available the file is copied from the web
     * D) if possible the ldb is attached to scddb
     *
     * @param iDeleteLdb true, when the local database must be deleted
     */
    public boolean prepareLdb(boolean iDeleteLdb) {

        // LDB
        Ldb_Helper tLdbHelper;
        Ldb_Helper.createInstance(mContext);
        tLdbHelper = Ldb_Helper.getInstance();
        if (iDeleteLdb) {
            try {
                Logg.w(TAG, "About to delete LDB");
                tLdbHelper.closeDB();
                Ldb_Helper.destroyInstance();
                boolean tSuccess = mContext.deleteDatabase("Ldb");
                if (!tSuccess) {
                    Logg.w(TAG, "Delete failed");
                }
                Ldb_Helper.createInstance(mContext);
                tLdbHelper = Ldb_Helper.getInstance();
                Logg.i(TAG, "Deleted and created");
            } catch(Exception e) {
                Logg.w(TAG, e.toString());
            }
        }
        if (!tLdbHelper.isLdbAvailable()) {
            Logg.i(TAG, "Call prepareTables for LDB");
            tLdbHelper.prepareTables();
        }

        return tLdbHelper.isLdbAvailable();

    }

    /**
     * This method prepares both databases LDB and SCD
     * A) If requested the local databas LDB is deleted
     * B) if required the local database LDB  and its tables are created
     * C) if the Scddb_File is not available the file is copied from the web
     * D) if possible the ldb is attached to scddb
     */
    public void prepareScddbFile() {
        // SSL
        prepSSL(mContext);
        // SCDDB
        Scddb_Helper.createInstance(mContext);
        Scddb_File.getInstance();

    }

    public void prepareSsl() {
        // SSL
        prepSSL(mContext);
    }

    //</editor-fold>

    // CRUD

    /**
     * Generic Read Call, where only one or zero object are returned
     * Usually used, if you already know the id or the select is used just to retrieve a text ..
     *
     * @param iSearchPattern the SearchPattern defining the search
     * @param <T>            the class of the object
     * @return the first object found or null
     */
    public <T> T readFirst(SearchPattern iSearchPattern) {
        Logg.i(TAG, "start readFirst" + iSearchPattern.getSearchClass().getSimpleName());
        SearchCall tSearchCall = new SearchCall(iSearchPattern.getSearchClass(), iSearchPattern, null);
        Cursor tCursor = null;
        try {
            if (BuildConfig.DEBUG) {
                Logg.d(TAG, "readFirst: Call createCursor");
            }
            tCursor = tSearchCall.createCursor();
            if (tCursor == null || tCursor.getCount() == 0) {
                return null;
            }
            tCursor.moveToFirst();
            return tSearchCall.getDataObject(tCursor);
        } catch(RuntimeException e) {
            Logg.w(TAG, e.toString());
            return null;
        } finally {
            if (tCursor != null) {
                tCursor.close();
            }
        }
    }

    /**
     * Generic Read Call, where the array of all fitting objects is returned. If nothing is found a empty ArrayList is returned
     * This is the most common call to the database
     *
     * @param iSearchPattern the SearchPattern defining the search
     * @param <T>            the class of the object
     * @return the ArrayList of objects
     */
    public <T> ArrayList<T> readArrayList(SearchPattern iSearchPattern) {
        SearchCall tSearchCall = new SearchCall(iSearchPattern.getSearchClass(), iSearchPattern, null);
        Cursor tCursor = null;
        ArrayList<T> tAL;
        try {
            if (BuildConfig.DEBUG) {
                Logg.d(TAG, "readArrayList: Call createCursor");
            }
            tCursor = tSearchCall.createCursor();
            if (tCursor == null) {
                return null;
            }
            if (BuildConfig.DEBUG) {
                Logg.d(TAG, "readArrayList: cursor size = " + tCursor.getCount());
            }
            Logg.i(TAG, "Start readCursor");
            tAL = new ArrayList<>(0);
            while (tCursor.moveToNext()) {
                tAL.add(tSearchCall.getDataObject(tCursor));
            }
            return tAL;
        } catch(RuntimeException e) {
            Logg.w(TAG, e.toString());
            return null;
        } finally {
            if (tCursor != null) {
                tCursor.close();
            }
        }
    }

    public Integer[] readArray(SearchPattern iSearchPattern) {
        SearchCall tSearchCall = new SearchCall(iSearchPattern.getSearchClass(), iSearchPattern, null);
        Cursor tCursor = null;
        Integer[] tA;
        try {
            if (BuildConfig.DEBUG) {
                Logg.d(TAG, "readArrayList: Call createCursor");
            }
            tCursor = tSearchCall.createCursor();
            if (tCursor == null) {
                return null;
            }
            if (BuildConfig.DEBUG) {
                Logg.d(TAG, "readArrayList: cursor size = " + tCursor.getCount());
            }
            Logg.i(TAG, "Start readCursor");
            tA= new Integer[tCursor.getCount()] ;
            int tIndex = tCursor.getColumnIndex("D_ID");
            int i = 0;
            while (tCursor.moveToNext()) {
                tA[i] = tCursor.getInt(tIndex);
                i++;
            }
            return tA;
        } catch(RuntimeException e) {
            Logg.w(TAG, e.toString());
            return null;
        } finally {
            if (tCursor != null) {
                tCursor.close();
            }
        }
    }


    /**
     * Generic Read Call, where the array of all fitting objects is returned. If nothing is found a empty ArrayList is returned
     * This is the most common call to the database
     *
     * @param iSearchPattern the SearchPattern defining the search
     * @return the count of objects
     */
    public int readCount(SearchPattern iSearchPattern) {
        SearchCall tSearchCall = new SearchCall(iSearchPattern.getSearchClass(), iSearchPattern, null);
        Cursor tCursor = null;
        try {
            if (BuildConfig.DEBUG) {
                Logg.d(TAG, "readArrayList: Call createCursor");
            }
            tCursor = tSearchCall.createCursor();
            if (tCursor == null) {
                return 0;
            }
            if (BuildConfig.DEBUG) {
                Logg.d(TAG, "readArrayList: cursor size = " + tCursor.getCount());
            }
            return tCursor.getCount();
        } catch(RuntimeException e) {
            Logg.w(TAG, e.toString());
            return 0;
        } finally {
            if (tCursor != null) {
                tCursor.close();
            }
        }
    }


    /**
     * Method for chained search, the database is always called async, so iShouting must be provided, as it is called when the database request is finished
     *
     * @param iObjectClass : the class of the objects, currently only Dance is implemented
     * @param iMethod      : the method as defined by SqlContract
     * @param iValue:      the value as defined by SqlContracgt
     * @param iShouting    to be informed
     * @return the TreeSet of Ids
     */
    @SuppressWarnings("rawtypes")
    public SearchRetrieval getTreeSet(Class iObjectClass, String iMethod, String iValue, Shouting iShouting) {
        final SearchRetrieval tSearchRetrieval = new SearchRetrieval(iObjectClass, iMethod, iValue, iShouting);
        tSearchRetrieval.execute();
        return tSearchRetrieval;
    }

    public SearchRetrieval requestTreeSet(Class iObjectClass, String iMethod, String iValue, Shouting iShouting) {
        if (iShouting == null) {
            throw new RuntimeException("Shouting must be available for async");
        }
        final SearchRetrieval tSearchRetrieval = new SearchRetrieval(iObjectClass, iMethod, iValue, iShouting);
        Runnable tRunnable = new Runnable() {
            @Override
            public void run() {
                Logg.i(TAG, "Call SearchRetrieval.execute");
                tSearchRetrieval.execute();
                Logg.i(TAG, "Past SearchRetrieval.execute");
            }
        };
        Thread tThread = new Thread(tRunnable, "requestTreeSet");
        tThread.start();
        return tSearchRetrieval;
    }


    // Media

    /**
     * Read the MediaStore and store all music files in LDB
     *
     * @param iShouting to be informed
     */
    public void scanMusic(Context tContext, Shouting iShouting) {
        final Shouting fShouting = iShouting;
        Thread tThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Logg.i(TAG, "Call MusicScan.scan()");
                MusicScan tMusicScan = new MusicScan(tContext);
                tMusicScan.scan();
                //tMusicScan.execute();
                if (fShouting != null) {
                    Shout tShout = new Shout("DataService");
                    tShout.mLastAction = "finished";
                    tShout.mLastObject = "MusicScan";
                    fShouting.shoutUp(tShout);
                }
                Logg.i(TAG, "After MusicScan.scan()");
            }
        });
        tThread.setName("scanMusic");
        tThread.start();
    }

    /**
     * Create pairing of musicdirectories of LDB with albums of SCDDB and synchronize MusicDirectory and Musicfile
     *
     * @param iShouting to be informed
     */
    public void executeIdentifyPairing(Shouting iShouting) {
        Thread tThread = new Thread(new Runnable() {
            @Override
            public void run() {
                PairingProcess tPairingProcess = new PairingProcess();

                Logg.i(TAG, "Call PairingProcess.executeIdentify");
                tPairingProcess.executeIdentify();
                Logg.i(TAG, "Past PairingProcess.executeIdentify");
//                Logg.i(TAG, "Call PairingProcess.executeSynchronize");
//                tPairingProcess.excuteSynchronize();
//                Logg.i(TAG, "Past PairingProcess.executeSynchronize");
                if (iShouting != null) {
                    Shout tShout = new Shout("DataService");
                    tShout.mLastAction = "finished";
                    tShout.mLastObject = "PairingProcess.Identification";
                    iShouting.shoutUp(tShout);
                }
            }
        });
        tThread.setName("executePairing");
        tThread.start();
    }

    /**
     * Based on the data store in pairing, update MusicFile and MusicDirectory with links to SCDDB
     *
     * @param iShouting to be informed
     */
    public void executeSynchronizePairing(Shouting iShouting) {
        Thread tThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Logg.i(TAG, "Call executeSynchronize");
                PairingProcess tPairingProcess = new PairingProcess();
                tPairingProcess.excuteSynchronize();
                Logg.i(TAG, "Past executeSynchronize");
                if (iShouting != null) {
                    Shout tShout = new Shout("DataService");
                    tShout.mLastAction = "finished";
                    tShout.mLastObject = "PairingProcess.Synchronisation";
                    iShouting.shoutUp(tShout);
                }
                Logg.i(TAG, "Finished synchronizePairing");
            }
        });
        tThread.setName("synchronizePairing");
        tThread.start();
    }

    @Override
    public void shoutUp(Shout iShoutToCeiling) {
        Logg.i(TAG, iShoutToCeiling.toString());
        mGlassFloor = iShoutToCeiling;
        processShouting();
    }
}
