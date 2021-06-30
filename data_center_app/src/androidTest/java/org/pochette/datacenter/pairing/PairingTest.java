package org.pochette.data_library.pairing;

import android.content.Context;

import junit.framework.TestCase;

import org.junit.Rule;
import org.junit.Test;
import org.pochette.data_library.database.DataService_LdbTest;
import org.pochette.data_library.database.DataService_WriteTest;
import org.pochette.data_library.database.Scddb_FileTest;
import org.pochette.data_library.database_management.Ldb_Helper;
import org.pochette.data_library.database_management.Scddb_File;
import org.pochette.data_library.database_management.Scddb_Helper;
import org.pochette.data_library.music.MusicDirectory;
import org.pochette.data_library.music.MusicScan;
import org.pochette.data_library.scddb_objects.Album;
import org.pochette.data_library.search.SearchCall;
import org.pochette.utils_lib.logg.Logg;
import org.pochette.utils_lib.search.SearchPattern;
import org.pochette.utils_lib.shouting.Shout;
import org.pochette.utils_lib.shouting.Shouting;

import java.util.ArrayList;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ServiceTestRule;

import static java.lang.Thread.sleep;

public class PairingTest extends TestCase implements Shouting {

    private final static String TAG = "FEHA (DataServiceTest)";
    boolean mScddbAvailable = false;

    @Rule
    public final ServiceTestRule mServiceRule = new ServiceTestRule();


    @Test
    public void test_Pairing_SignaturesOfDirectory() {
        boolean tAllHaveSignature = true;
        ArrayList<MusicDirectory> tAR = new ArrayList<>(0);
        SearchPattern tSearchPattern;
        SearchCall tSearchCall;

        prepLdbData();
        Logg.w(TAG, "start of test");
        tSearchPattern = new SearchPattern(MusicDirectory.class);
        tSearchCall = new SearchCall(MusicDirectory.class, tSearchPattern, null);
        tAR = tSearchCall.produceArrayList();
        if (tAR != null) {
            for (MusicDirectory lMusicDirectory : tAR) {
                String lSignature = lMusicDirectory.mSignature;
                        Logg.i(TAG,lSignature+"=>"+ lMusicDirectory.mT1);
                assertFalse("Signature should not be empty", lSignature.isEmpty());
                if (lSignature.isEmpty()) {
                    tAllHaveSignature = false;
                }
            }
        }
        String tMethodName = new Object() {
        }.getClass().getEnclosingMethod().getName();
        Logg.i(TAG, tMethodName + " finished");
    }

    @Test
    public void test_Pairing_SignaturesOfAlbum() {
        prepScddbData();
        boolean tAllHaveSignature = true;
        ArrayList<Album> tAR = new ArrayList<>(0);
        SearchPattern tSearchPattern;
        SearchCall tSearchCall;
        Logg.w(TAG, "start of test");
        tSearchPattern = new SearchPattern(Album.class);
        tSearchCall = new SearchCall(Album.class, tSearchPattern, null);
        tAR = tSearchCall.produceArrayList();
        if (tAR != null) {
            for (Album lAlbum : tAR) {
                String lSignature = lAlbum.getSignature();
                if (lSignature == null ||  lSignature.isEmpty()) {
                    tAllHaveSignature = false;
                }
                Logg.i(TAG, lSignature + "=>" + lAlbum.mName);

                assertFalse("Signature should not be empty", lSignature.isEmpty());
            }
        }
        String tMethodName = new Object() {
        }.getClass().getEnclosingMethod().getName();
        Logg.i(TAG, tMethodName + " finished");
    }

    @Test
    public void test_Pairing_PairingProcessExecute() {
        prepScddbData();
        int tCount = Scddb_Helper.getInstance().getSize("LDB.PAIRING");
        Logg.i(TAG, "Start with pairings" + tCount);
        PairingProcess tPairingProcess;
        tPairingProcess = new PairingProcess();
        tPairingProcess.execute();
        String tMethodName = new Object() {
        }.getClass().getEnclosingMethod().getName();
        Logg.i(TAG, tMethodName + " finished");
    }

    @Test
    public void test_Pairing_Synchronize() {
        prepScddbData();

        int tCount = Scddb_Helper.getInstance().getSize("LDB.PAIRING");
        Logg.i(TAG, "Start with pairings" + tCount);
        PairingProcess tPairingProcess;
        tPairingProcess = new PairingProcess();


        tPairingProcess.synchronizeexecute();



        String tMethodName = new Object() {
        }.getClass().getEnclosingMethod().getName();
        Logg.i(TAG, tMethodName + " finished");
    }



    public void prepScddbData() {
        boolean tRecreate = false;
      //  tRecreate = true;
        Context tContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        if (tRecreate) {
            Scddb_FileTest.prepSSL();
            DataService_LdbTest.destroyEverything();
        }
        Ldb_Helper.createInstance(tContext);
        if (tRecreate) {
            Ldb_Helper.getInstance().prepareTables();
        }
        Logg.w(TAG, "Ldb prep done");
        Scddb_Helper.createInstance(tContext);
        if (tRecreate) {
            Scddb_File.getInstance().copyFromWeb(this);
            while (!mScddbAvailable) {
                Logg.i(TAG, "wait for ScddbCopy");
                try {
                    sleep(2000);
                } catch(InterruptedException e) {
                    Logg.w(TAG, e.toString());
                }
            }
            Logg.w(TAG, "download done");
        }
        Scddb_Helper.getInstance().attachLdbToScddb(null);
        Logg.w(TAG, "attach done");
        if (tRecreate) {
            new MusicScan(tContext).execute();
        }
        Logg.w(TAG, "MusicScan done");
    }


    public void prepLdbData() {
        DataService_WriteTest.prepareDatabases();
        Context tContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        new MusicScan(tContext).execute();

    }

    // preparation


    @Override
    public void shoutUp(Shout tShoutToCeiling) {
        mScddbAvailable = true;
    }
}