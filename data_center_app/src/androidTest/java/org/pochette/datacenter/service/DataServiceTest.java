package org.pochette.data_library.service;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;

import com.google.android.gms.security.ProviderInstaller;

import junit.framework.TestCase;


import org.junit.Rule;
import org.junit.Test;
import org.pochette.data_library.database_management.Ldb_Helper;
import org.pochette.data_library.database_management.Scddb_File;
import org.pochette.data_library.database.Scddb_FileTest;
import org.pochette.data_library.database_management.Scddb_Helper;
import org.pochette.data_library.search.SqlContract;
import org.pochette.utils_lib.logg.Logg;

import androidx.test.platform.app.InstrumentationRegistry;

import java.util.concurrent.TimeoutException;


import androidx.test.rule.ServiceTestRule;

import static java.lang.Thread.sleep;
import static org.junit.Assert.assertThat;

public class DataServiceTest extends TestCase {

    private final static String TAG = "FEHA (DataServiceTest)";
    DataService mDataService;

    @Rule
    public final ServiceTestRule mServiceRule = new ServiceTestRule();



    @Test
    public void test_StartService() {
        stopAndDropService();
        startService();
        assertNotNull("Service available",mDataService);
    }

    @Test
    public void test_createHelper() {
        startService();
        Ldb_Helper tLdb_Helper = mDataService.createLdbHelper();
        assertNotNull("Helper not null",tLdb_Helper);
    }

    @Test
    public void test_createDatabase() {
        deleteDatabase();
        Scddb_FileTest.prepSSL();
        startService();
        SQLiteDatabase tDB = mDataService.getDatabase();
        assertNotNull("Database not null",tDB);

        SqlContract tSqlContract= new SqlContract() ;
        for (Class lClass : tSqlContract.getAR_LdbClass()) {
            String tTableName = tSqlContract.getTableString(lClass);
            Logg.i(TAG, "Look at " + tTableName);
            String tSql = "SELECT COUNT(*) AS COUNT FROM " + tTableName + " ;";
            Logg.i(TAG, tSql);
            Cursor tCursor = tDB.rawQuery(tSql, null);
            tCursor.moveToFirst();
            int tCount = tCursor.getInt(tCursor.getColumnIndex("COUNT"));
            Logg.i(TAG, "Rows " + tCount);


        }

    }

    @Test
    public void test_copyScddb() {
        deleteDatabase();
        startService();
        SQLiteDatabase tDB = mDataService.getDatabase();
        try {
            ProviderInstaller.installIfNeeded(InstrumentationRegistry.getInstrumentation().getTargetContext());
        } catch(Exception e) {
            Logg.e(TAG, e.toString());
        }
      //  Scddb_File.createInstance(InstrumentationRegistry.getInstrumentation().getTargetContext());
        Scddb_File.getInstance().delete();
        assertFalse("Scddb should not be there ", Scddb_File.getInstance().isDbFileAvailable());
        Scddb_File.getInstance().copyFromWeb(null);
        try {
            sleep(20 * 1000);
        } catch(InterruptedException e) {
            Logg.e(TAG, e.toString());
        }
        assertTrue("Scddb should  be there ", Scddb_File.getInstance().isDbFileAvailable());
    }


    @Test
    public void test_ScddbHelper() {

        startService();
        mDataService.createScdbHelper(InstrumentationRegistry.getInstrumentation().getTargetContext());

        Scddb_Helper tScddb_helper = Scddb_Helper.getInstance();
        assertNotNull("ScddbHelper should be not null", tScddb_helper);
    }


    @Test
    public void test_attach() {

        Scddb_FileTest.prepSSL();
        try {
            sleep(1 * 1000);
        } catch(InterruptedException e) {
            Logg.e(TAG, e.toString());
        }
        startService();
        Logg.i(TAG, " start Scddb Helper");
        mDataService.createScdbHelper(InstrumentationRegistry.getInstrumentation().getTargetContext());
        mDataService.attachLdbToScddb();
        try {
            sleep(20 * 1000);
        } catch(InterruptedException e) {
            Logg.e(TAG, e.toString());
        }
    }






    // preparation

    void stopAndDropService() {
        mDataService = null;
    }

    void startService() {
        if (mDataService == null) {
            try {
                // Create the service Intent.
                Intent serviceIntent =
                        new Intent(InstrumentationRegistry.getInstrumentation().getTargetContext(), DataService.class);
                // Bind the service and grab a reference to the binder.
                IBinder binder = null;
                try {
                    binder = mServiceRule.bindService(serviceIntent);
                } catch(TimeoutException e) {
                    Logg.w(TAG, e.toString());
                }
                // Get the reference to the service, or you can call public methods on the binder directly.
                mDataService = ((DataService.LocalBinder) binder).getService();
            } catch(Exception e) {
                e.printStackTrace();
            }
        }

    }

    public static void deleteDatabase() {
        Context tContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        tContext.deleteDatabase("Ldb");
        Logg.i(TAG, "Ldb database is deleted");

    }

}