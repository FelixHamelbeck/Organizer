package org.pochette.data_library.database;



import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.google.android.gms.security.ProviderInstaller;

import junit.framework.TestCase;


import org.junit.Rule;
import org.junit.Test;
import org.pochette.data_library.database_management.Ldb_Helper;
import org.pochette.data_library.database_management.Scddb_File;
import org.pochette.data_library.service.DataService;
import org.pochette.utils_lib.logg.Logg;

import androidx.test.platform.app.InstrumentationRegistry;

import java.util.Date;
import java.util.concurrent.TimeoutException;


import androidx.test.rule.ServiceTestRule;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

import static java.lang.Thread.sleep;

public class Scddb_FileTest extends TestCase {

    private final static String TAG = "FEHA (Scddb_FileTest)";
    DataService mDataService;

    @Rule
    public final ServiceTestRule mServiceRule = new ServiceTestRule();
    Scddb_File mScddb_file;

    @Test
    public void test_ScddbFile_download() {


        prepSSL();

        Context tContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Ldb_Helper.createInstance(tContext);
        Ldb_Helper tLdb_Helper = Ldb_Helper.getInstance();




      //  Scddb_File.createInstance();
        mScddb_file = Scddb_File.getInstance();
        assertNotNull("ScddbFile Instance should exist",mScddb_file);

        mScddb_file.delete();
        boolean tFlag;
        tFlag = mScddb_file.isDbFileAvailable();
        assertFalse("the should be no database file ",tFlag);


        mScddb_file.copyFromWeb(null);
        Logg.i(TAG, "main thread");

        try {
            sleep(20 * 1000);
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
        Logg.i(TAG, "test done");

        tFlag = mScddb_file.isDbFileAvailable();
        assertTrue("there should be a database file ",tFlag);

        Date tWebDate = mScddb_file.getLastWebdate();
        Logg.i(TAG, "Web " + tWebDate.toString());
        Date tLocalDate = mScddb_file.getLocalScddbDate();
        Logg.i(TAG, "Web " + tLocalDate.toString());
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






    public static void prepSSL() {
        try {
            ProviderInstaller.installIfNeeded(InstrumentationRegistry.getInstrumentation().getTargetContext());
            SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, null, null);
            SSLEngine engine = sslContext.createSSLEngine();
        } catch(Exception e) {
            Logg.e(TAG, e.toString());
        }
    }

}