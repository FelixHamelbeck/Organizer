package org.pochette.data_library.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Binder;
import android.os.IBinder;

//import org.pochette.utils_lib.logg.Logg;
import org.pochette.utils_lib.logg.Logg;
import org.pochette.utils_lib.search.SearchPattern;
import org.pochette.utils_lib.shouting.Shout;
import org.pochette.utils_lib.shouting.Shouting;
import org.pochette.data_library.database_management.Ldb_Helper;
import org.pochette.data_library.database_management.ManagementInterface;
import org.pochette.data_library.database_management.Scddb_File;
import org.pochette.data_library.database_management.Scddb_Helper;
import org.pochette.data_library.music.MusicFile;
import org.pochette.data_library.music.MusicScan;
import org.pochette.data_library.search.DeleteCall;
import org.pochette.data_library.search.SearchCall;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static java.lang.Thread.sleep;

@SuppressWarnings("UnusedReturnValue")
public class DataService extends Service implements Shouting, ManagementInterface {

    private static final String TAG = "FEHA (DataService)";
private Ldb_Helper mLdb_Helper;

    // variables


    boolean mScddbFileAvailable;
    // constructor
    // setter and getter

    //<editor-fold desc="Binder">
    private final IBinder mBinder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {
        Logg.i(TAG, "onBind");
        return mBinder;
    }

    public class LocalBinder extends MyBinder {
        public DataService getService() {
            return DataService.this;
        }
    }

    public class MyBinder extends Binder {
        public DataService getService() {
            Logg.v(TAG, "getService");
            return DataService.this;
        }
    }
    //</editor-fold>

    //<editor-fold desc="Service Lifecycle and override">
    // lifecylce and override
    @Override
    public void onCreate() {
        super.onCreate();
        Logg.i(TAG, "onCreate");

        System.out.println("Servcice.OnCreate");

       // dropAndCreateLdb();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    //</editor-fold>

    // internal

    private void dropAndCreateLdb() {
        this.getApplicationContext().deleteDatabase("Ldb");
        Ldb_Helper.createInstance(this.getApplicationContext());

        mLdb_Helper = Ldb_Helper.getInstance();
        mLdb_Helper.prepareTables();



        calculateScddbFileAvailable();
        createScdbHelper(this.getApplicationContext());

        attachLdbToScddb();
    }


    private void calculateScddbFileAvailable() {
        boolean tResult;
        try {
            Scddb_File tScddb_file;
            tScddb_file = Scddb_File.getInstance();
            tResult = tScddb_file.isDbFileAvailable();
        } catch(Exception e) {
            Logg.i(TAG, e.toString());
            tResult = false;
        }
        mScddbFileAvailable = tResult;
        Logg.i(TAG, "Scddb: " + mScddbFileAvailable);
    }

    private void processShouting(Shout iShout) {
        if (iShout.mActor.equals("Scddb_File")) {
            if (iShout.mLastObject.equals("ScddbWebFile") &&
                    iShout.mLastAction.equals("downloaded")) {
                mScddbFileAvailable = true;
            }
        }
    }





    // public methods





    //<editor-fold desc="public interface ManagementInterface">
    public Ldb_Helper createLdbHelper() {
        Logg.i(TAG, "createLdbHelper");
        return mLdb_Helper;
    }

    public Scddb_Helper createScdbHelper(Context iContext) {
        Logg.i(TAG, "createLdb");
        Scddb_Helper.createInstance(iContext);
        return Scddb_Helper.getInstance();
    }



    @Override
    public SQLiteDatabase getDatabase() {
        Logg.i(TAG, "createDatabase");
        return mLdb_Helper.getWritableDatabase();
    }

    @Override
    public void downloadScddb() {
        try {
            Scddb_File tScddb_file;
            tScddb_file = Scddb_File.getInstance();
            tScddb_file.copyFromWeb(null);
        } catch(Exception e) {
            Logg.w(TAG, e.toString());
            Logg.e(TAG, "DataService.downloadScddb failed");
        }
    }

    public Date getLocalScddbDate() {
        return Scddb_File.getInstance().getLocalScddbDate();
    }

    /**
     * This call returns the stored value of the webdate. A process to get an update on the webdate
     * is started asyncronously
     * @return the last webdate received from the web as currently known.
     */
    public Date getLastWebdate() {
        return Scddb_File.getInstance().getLastWebdate();
    }

    public boolean  isDbFileAvailable() {
        return Scddb_File.getInstance().isDbFileAvailable();
    }


    public void attachLdbToScddb() {
        Scddb_Helper.getInstance().attachLdbToScddb(this);
    }

    // CRUD




    public <T> T readFirst(SearchPattern iSearchPattern) {
        SearchCall tSearchCall = new SearchCall(iSearchPattern.getSearchClass(), iSearchPattern, null);
        Cursor tCursor = null;
     //   tCursor = tSearchCall.createCursor();
        try {
            tCursor = tSearchCall.createCursor();
            if (tCursor == null || tCursor.getCount() == 0) {
                return null;
            }
            Logg.i(TAG, "cursor of length: " + tCursor.getCount());
            tCursor.moveToFirst();
            return tSearchCall.getDataObject(tCursor);
        } catch(RuntimeException  e) {
            Logg.w(TAG, e.toString());
            return null;
        }finally {
            if (tCursor != null) {
                tCursor.close();
            }
        }
    }

    public <T> ArrayList<T> readArrayList(SearchPattern iSearchPattern) {
        SearchCall tSearchCall = new SearchCall(iSearchPattern.getSearchClass(), iSearchPattern, null);
        Cursor tCursor = null;
        ArrayList<T> tAL;
        try {
            tCursor = tSearchCall.createCursor();
            if (tCursor == null || tCursor.getCount() == 0) {
                return null;
            }
            Logg.i(TAG, "cursor of length: " + tCursor.getCount());
            tCursor.moveToFirst();
            tAL = new ArrayList<>(0);
            while (tCursor.moveToNext()) {
                tAL.add(tSearchCall.getDataObject(tCursor));
            }
            return tAL;
        } catch(RuntimeException  e) {
            Logg.w(TAG, e.toString());
            return null;
        } finally {
            if (tCursor != null) {
                tCursor.close();
            }
        }
    }

    public void delete(Object iObject) {
        DeleteCall tDeleteCall;
        tDeleteCall = new DeleteCall(iObject.getClass(), iObject);
        tDeleteCall.delete();
    }



    // Media
    void scanMusic(Context tContext, Shouting iShouting) {


        Thread tThread = new Thread(new Runnable() {
            @Override
            public void run() {
                MusicScan tMusicScan = new MusicScan(tContext);
                ArrayList<MusicFile> tAR_MusicFile = tMusicScan.execute();
                if (iShouting != null) {
                    Shout tShout = new Shout("DataService");
                    tShout.mLastAction = "finished";
                    tShout.mLastObject = "Scan";
                    iShouting.shoutUp(tShout);
                }
            }
        });
        tThread.setName("scanMusic");
        tThread.start();


    }


    //</editor-fold>

    public String getTestString() {
        return Calendar.getInstance().getTime().toString();

    }

    @Override
    public void shoutUp(Shout iShoutToCeiling) {
        processShouting(iShoutToCeiling);
    }
}
