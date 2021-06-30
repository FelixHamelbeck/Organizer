package org.pochette.organizer;

import android.content.Context;
import android.util.Log;

import junit.framework.TestCase;

import org.junit.Rule;
import org.junit.Test;
import org.pochette.data_library.scddb_objects.Dance;
import org.pochette.organizer.app.DataServiceSingleton;
import org.pochette.organizer.chained_search.ChainedListThread;
import org.pochette.organizer.chained_search.SearchOption;
import org.pochette.utils_lib.logg.Logg;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ServiceTestRule;

import static java.lang.Thread.sleep;

public class ChainedListThread_Test extends TestCase {

    private final static String TAG = "FEHA (CLT_Test)";

    Class mClass = Dance.class;


    @Rule
    public final ServiceTestRule mServiceRule = new ServiceTestRule();


    @Test
    public void test_CLT_Search_RSCDS_setup() {
        prepareDatabases();
        Log.w(TAG, "Start");
        ChainedListThread tChainedListThread = new ChainedListThread(mClass, ChainedListThread.NODE_SEARCH);
        SearchOption tSearchOption = SearchOption.getByCode(mClass, "RSCDS_REQUIRED");
        tChainedListThread.updateSearchSetting(tSearchOption, null);
        Logg.i(TAG, tChainedListThread.toString());
        assertNotNull(tChainedListThread);
    }



    @Test
    public void test_CLT_RSCDS_search_thread() {
        prepareDatabases();
        Log.w(TAG, "Start");
        ChainedListThread tChainedListThread = new ChainedListThread(mClass, ChainedListThread.NODE_SEARCH);
        tChainedListThread.start();
        SearchOption tSearchOption = SearchOption.getByCode(mClass, "RSCDS_REQUIRED");
        tChainedListThread.updateSearchSetting(tSearchOption, null);
        tChainedListThread.requestCalculate();
        Logg.i(TAG, tChainedListThread.toString());

        try {
            sleep(109 * 1000);
        } catch(InterruptedException e) {
            Logg.w(TAG, e.toString());
        }


        assertNotNull(tChainedListThread);
    }

    @Test
    public void test_CLT_RSCDS_search_thread_with_Change() {
        prepareDatabases();
        Log.w(TAG, "Start");
        ChainedListThread tChainedListThread = new ChainedListThread(mClass, ChainedListThread.NODE_SEARCH);
        tChainedListThread.start();
        SearchOption tSearchOption;
        tSearchOption = SearchOption.getByCode(mClass, "RSCDS_REQUIRED");
        tChainedListThread.updateSearchSetting(tSearchOption, null);
        tChainedListThread.requestCalculate();
        Logg.i(TAG, tChainedListThread.toString());

        try {
            sleep(5 * 1000);
        } catch(InterruptedException e) {
            Logg.w(TAG, e.toString());
        }
        tSearchOption = SearchOption.getByCode(mClass, "CRIB_REQUIRED");
        tChainedListThread.updateSearchSetting(tSearchOption, null);
        tChainedListThread.requestCalculate();

        try {
            sleep(105 * 1000);
        } catch(InterruptedException e) {
            Logg.w(TAG, e.toString());
        }
        assertNotNull(tChainedListThread);
    }


    @Test
    public void test_CLT_two_parallel() {
        prepareDatabases();
        Log.w(TAG, "Start");
        ChainedListThread tChainedListThread = new ChainedListThread(mClass, ChainedListThread.NODE_SEARCH);
        tChainedListThread.start();
        SearchOption tSearchOption;
        tSearchOption = SearchOption.getByCode(mClass, "RSCDS_REQUIRED");
        tChainedListThread.updateSearchSetting(tSearchOption, null);
        tChainedListThread.requestCalculate();
        Logg.i(TAG, tChainedListThread.toString());

        ChainedListThread sChainedListThread = new ChainedListThread(mClass, ChainedListThread.NODE_SEARCH);
        sChainedListThread.start();
        SearchOption sSearchOption;
        sSearchOption = SearchOption.getByCode(mClass, "CRIB_REQUIRED");
        sChainedListThread.updateSearchSetting(sSearchOption, null);
        sChainedListThread.requestCalculate();
        Logg.i(TAG, sChainedListThread.toString());
        try {
            sleep(105 * 1000);
        } catch(InterruptedException e) {
            Logg.w(TAG, e.toString());
        }
        assertNotNull(tChainedListThread);
        Logg.i(TAG, "test finished");
    }


    @Test
    public void test_CLT_AND_() {
        prepareDatabases();
        Log.w(TAG, "Start");


        ChainedListThread uChainedListThread = new ChainedListThread(mClass, ChainedListThread.NODE_AND);
        uChainedListThread.start();


        try {
            sleep(105 * 1000);
        } catch(InterruptedException e) {
            Logg.w(TAG, e.toString());
        }
        assertNotNull(uChainedListThread);
        assertTrue("AND should have found something", uChainedListThread.getTS_List().size() > 0);
        Logg.i(TAG, "test finished");
    }



    @Test
    public void test_CLT_AND_two_parallel() {
        prepareDatabases();
        Log.w(TAG, "Start");
        ChainedListThread tChainedListThread = new ChainedListThread(mClass, ChainedListThread.NODE_SEARCH);
        tChainedListThread.start();
        SearchOption tSearchOption;
        tSearchOption = SearchOption.getByCode(mClass, "RSCDS_REQUIRED");
        tChainedListThread.updateSearchSetting(tSearchOption, null);
        tChainedListThread.requestCalculate();
        Logg.i(TAG, tChainedListThread.toString());

        ChainedListThread sChainedListThread = new ChainedListThread(mClass, ChainedListThread.NODE_SEARCH);
        sChainedListThread.start();
        SearchOption sSearchOption;
        sSearchOption = SearchOption.getByCode(mClass, "CRIB_REQUIRED");
        sChainedListThread.updateSearchSetting(sSearchOption, null);
        sChainedListThread.requestCalculate();
        Logg.i(TAG, sChainedListThread.toString());

        ChainedListThread uChainedListThread = new ChainedListThread(mClass, ChainedListThread.NODE_AND);
        uChainedListThread.start();
        uChainedListThread.addFeederChainedList(tChainedListThread);
        uChainedListThread.addFeederChainedList(sChainedListThread);


        try {
            sleep(105 * 1000);
        } catch(InterruptedException e) {
            Logg.w(TAG, e.toString());
        }
        assertNotNull(uChainedListThread);
        assertTrue("AND should have found something", uChainedListThread.getTS_List().size() > 0);
        Logg.i(TAG, "test finished");
    }

    @Test
    public void test_CLT_OR_AND() {
        prepareDatabases();
        Log.w(TAG, "Start");
        ChainedListThread tChainedListThread = new ChainedListThread(mClass, ChainedListThread.NODE_SEARCH);
        tChainedListThread.start();
        SearchOption tSearchOption;
        tSearchOption = SearchOption.getByCode(mClass, "RSCDS_REQUIRED");
        tChainedListThread.updateSearchSetting(tSearchOption, null);
        tChainedListThread.requestCalculate();
        Logg.i(TAG, tChainedListThread.toString());

        ChainedListThread sChainedListThread = new ChainedListThread(mClass, ChainedListThread.NODE_SEARCH);
        sChainedListThread.start();
        SearchOption sSearchOption;
        sSearchOption = SearchOption.getByCode(mClass, "CRIB_REQUIRED");
        sChainedListThread.updateSearchSetting(sSearchOption, null);
        sChainedListThread.requestCalculate();
        Logg.i(TAG, sChainedListThread.toString());

        ChainedListThread uChainedListThread = new ChainedListThread(mClass, ChainedListThread.NODE_AND);
        uChainedListThread.start();
        uChainedListThread.addFeederChainedList(tChainedListThread);
        uChainedListThread.addFeederChainedList(sChainedListThread);


        ChainedListThread vChainedListThread = new ChainedListThread(mClass, ChainedListThread.NODE_SEARCH);
        vChainedListThread.start();
        SearchOption vSearchOption;
        vSearchOption = SearchOption.getByCode(mClass, "DANCENAME");
        vChainedListThread.updateSearchSetting(vSearchOption, "RANT");
        vChainedListThread.requestCalculate();
        Logg.i(TAG, vChainedListThread.toString());


        ChainedListThread wChainedListThread = new ChainedListThread(mClass, ChainedListThread.NODE_OR);
        wChainedListThread.start();
        wChainedListThread.addFeederChainedList(uChainedListThread);
        wChainedListThread.addFeederChainedList(vChainedListThread);



        try {
            sleep(105 * 1000);
        } catch(InterruptedException e) {
            Logg.w(TAG, e.toString());
        }
        assertNotNull(uChainedListThread);
        assertTrue("AND should have found something", uChainedListThread.getTS_List().size() > 0);
        Logg.i(TAG, "test finished");
    }








    // utilities


    public static void prepareDatabases() {

        Logg.i(TAG, "prep");
        Runnable tRunnable = new Runnable() {
            @Override
            public void run() {
                Logg.i(TAG, "pre DB");
                Context tContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
//                DataServiceSingleton.createInstance(tContext);
//                Ldb_Helper.createInstance(tContext);
//                Scddb_Helper.createInstance(tContext);
//                Scddb_Helper.getInstance().attachLdbToScddb(null);


                DataServiceSingleton.createInstance(tContext);
                DataServiceSingleton.getInstance().setDeleteLdbOnStart(false); // reset LDB with true
                DataServiceSingleton.getInstance().prepDataService();

                Logg.i(TAG, "post attach");
            }
        };
        Thread tThread = new Thread(tRunnable);
        tThread.start();

        try {
            sleep(2000);
        } catch(InterruptedException e) {
            Logg.w(TAG, e.toString());
        }
        Logg.i(TAG, "prep done");

    }

}