package org.pochette.data_library.music;

import android.content.Context;

import junit.framework.TestCase;

import org.junit.Rule;
import org.junit.Test;
import org.pochette.data_library.database.DataService_WriteTest;
import org.pochette.data_library.database_management.Scddb_Helper;
import org.pochette.data_library.scddb_objects.Dance;
import org.pochette.data_library.search.DeleteCall;
import org.pochette.data_library.search.SearchCall;
import org.pochette.utils_lib.logg.Logg;
import org.pochette.utils_lib.search.SearchCriteria;
import org.pochette.utils_lib.search.SearchPattern;
import org.pochette.utils_lib.shouting.Shout;
import org.pochette.utils_lib.shouting.Shouting;

import java.util.ArrayList;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ServiceTestRule;

import static java.lang.Thread.sleep;

public class DataService_Musicscan extends TestCase implements Shouting {

    private final static String TAG = "FEHA (Musicscan)";
    Shout mGlassFloor;

    @Rule
    public final ServiceTestRule mServiceRule = new ServiceTestRule();


    @Test
    public void test_ScanMusic() {
        prepDatabases();
        String tMusicFile = "MusicFile";
        int tCount;
        tCount = Scddb_Helper.getInstance().getSize(tMusicFile);
        assertEquals("Table should be empty", 0, tCount);

        MusicScan tMusicScan = new MusicScan(InstrumentationRegistry.getInstrumentation().getTargetContext());
        ArrayList<MusicFile> tAR_MusicFile = tMusicScan.execute();
        try {
            sleep(10 * 1000);
        } catch(InterruptedException e) {
            Logg.e(TAG, e.toString());
        }
        tCount = Scddb_Helper.getInstance().getSize(tMusicFile);
        Logg.i(TAG, "count" + tCount);
        assertTrue("Table should be filled", tCount > 50);

        String tMethodName = new Object() {
        }.getClass().getEnclosingMethod().getName();
        Logg.i(TAG, tMethodName + " finished");
    }


//    @Test
//    public void test_ScanService() {
//        // the scan must run in own thread and send a shout back
//        prepDatabases();
//        mGlassFloor = null;
//        String tMusicFile = "MusicFile";
//        int tCount;
//        tCount = Scddb_Helper.getInstance().getSize(tMusicFile);
//        assertEquals("Table should be empty", 0, tCount);
//
//        Context tContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
//        mDataService.scanMusic(tContext, this);
//
//        boolean tStillRunning = true;
//        int tIteration = 0;
//        while (tStillRunning) {
//            Logg.i(TAG, "still running");
//            tIteration++;
//            try {
//                sleep(500);
//            } catch(InterruptedException e) {
//                e.printStackTrace();
//            }
//
//            if (mGlassFloor != null) {
//                tStillRunning = false;
//
//            }
//            assertTrue("30 iterations is too much", tIteration < 30); // ie 15 seconds
//        }
//
//        tCount = Scddb_Helper.getInstance().getSize(tMusicFile);
//        Logg.i(TAG, "count" + tCount);
//        assertTrue("Table should be filled", tCount > 50);
//
//        String tMethodName = new Object() {
//        }.getClass().getEnclosingMethod().getName();
//        Logg.i(TAG, tMethodName + " finished");
//    }

    @Test
    public void test_ScanDeletScan() {
        // the scan must run in own thread and send a shout back
        prepDatabases();
        mGlassFloor = null;
        String tMusicFile = "MusicFile";
        int tCount;
        int tCountAfterFirst;
        tCount = Scddb_Helper.getInstance().getSize(tMusicFile);

        Context tContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        new
    (tContext).execute();



        tCount = Scddb_Helper.getInstance().getSize(tMusicFile);
        Logg.i(TAG, "count" + tCount);
        tCountAfterFirst = tCount;
        assertTrue("Table should be filled", tCount > 50);

        // delete
        ArrayList<MusicFile> tAR_MusicFile;
        SearchPattern tSearchPattern = new SearchPattern(MusicFile.class);
        Dance tDance;
        tSearchPattern.addSearch_Criteria(new SearchCriteria("NAME", "NOTHING"));

        tAR_MusicFile = new SearchCall(MusicFile.class, tSearchPattern, null).produceArrayList();


        assertNotNull("ArrayListe for delete must be not null", tAR_MusicFile);
        int tSize = tAR_MusicFile.size();
        Logg.i(TAG, "Size" + tSize);
        if (tAR_MusicFile != null) {
            for (MusicFile lMusicFile : tAR_MusicFile) {
                Logg.i(TAG, lMusicFile.mName);
                DeleteCall tDeleteCall = new DeleteCall(MusicFile.class, lMusicFile);
                tDeleteCall.delete();
            }
        }
        tCount = Scddb_Helper.getInstance().getSize(tMusicFile);
        Logg.i(TAG, "count" + tCount);
        assertTrue("some items should have been removed", tCountAfterFirst - tCount > 0);

        // scan again

        new MusicScan(tContext).execute();


        Logg.i(TAG, "done");
        tCount = Scddb_Helper.getInstance().getSize(tMusicFile);
        Logg.i(TAG, "count" + tCount);
        assertEquals("same number of Musicfiles should be in database",  tCountAfterFirst, tCount);

        String tMethodName = new Object() {
        }.getClass().getEnclosingMethod().getName();
        Logg.i(TAG, tMethodName + " finished");
    }


    // preparation

    void prepDatabases() {

        DataService_WriteTest.prepareDatabases();
    }


    public void shoutUp(Shout iShout) {
        Logg.i(TAG, iShout.toString());
        mGlassFloor = iShout;
    }


}