package org.pochette.organizer;

import android.content.Context;
import android.util.Log;

import junit.framework.TestCase;

import org.junit.Rule;
import org.junit.Test;
import org.pochette.organizer.app.DataServiceSingleton;
import org.pochette.organizer.dance.Dance_ViewModel;
import org.pochette.organizer.music.MusicFile_ViewModel;
import org.pochette.utils_lib.logg.Logg;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ServiceTestRule;

import static java.lang.Thread.sleep;

public class ViewModel_Test extends TestCase {

    private final static String TAG = "FEHA (DataServiceTest)";

    Dance_ViewModel mDanceModel;
    MusicFile_ViewModel mMusicFileModel;
//    PairingDetail_ViewModel mPairingModel;

    @Rule
    public final ServiceTestRule mServiceRule = new ServiceTestRule();


    @Test
    public void test_ViewModel_01() {
        Log.i(TAG, "start"+ Thread.currentThread().toString());

        prepareDatabases();

        waitForSeconds(3);

        Log.i(TAG, "finished"+ Thread.currentThread().toString());
    }

    @Test
    public void test_DanceModel_02() {
        Log.i(TAG, "start"+ Thread.currentThread().toString());
        prepareDatabases();
        waitForSeconds(3);
        Logg.w(TAG, "START OF REAL TEST");

        createDanceModel();
        mDanceModel.forceSearch();
        mDanceModel.setDancename("Red");
        mDanceModel.forceSearch();
        mDanceModel.forceSearch();


        waitForSeconds(10);
        Log.i(TAG, "finished:"+ Thread.currentThread().toString());
    }

    @Test
    public void test_MixModel() {
        Log.i(TAG, "start"+ Thread.currentThread().toString());
        prepareDatabases();
        waitForSeconds(3);
        Logg.w(TAG, "START OF REAL TEST");

        createDanceModel();
        createMusicFileModel();
        //createPairingModel();
        //mPairingModel.forceSearch();
        mMusicFileModel.forceSearch();
        mDanceModel.forceSearch();

//        mDanceModel.forceSearch();
//        mDanceModel.setDancename("Red");
//        mDanceModel.forceSearch();
//        mDanceModel.forceSearch();


        waitForSeconds(10);
        Log.i(TAG, "finished:"+ Thread.currentThread().toString());
    }


    @Test
    public void test_ScanSolo() {
        Log.i(TAG, "start"+ Thread.currentThread().toString());
        prepareDatabases();
        waitForSeconds(3);
        Logg.w(TAG, "START OF REAL TEST");

        callScan();
        Log.i(TAG, "finished:"+ Thread.currentThread().toString());

        waitForSeconds(30);
    }


    private void createDanceModel() {
        if (mDanceModel != null) {
            return;
        }
        Context tContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        mDanceModel = new Dance_ViewModel(false);
    }
    private void createMusicFileModel() {
        if (mMusicFileModel != null) {
            return;
        }
        Context tContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        mMusicFileModel = new MusicFile_ViewModel(false);
    }
//    private void createPairingModel() {
//        if (mPairingModel != null) {
//            return;
//        }
//        Context tContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
//        mPairingModel = new PairingDetail_ViewModel(false);
//    }


    void waitForSeconds(int iSeconds) {
        Logg.i(TAG, "wait " + iSeconds + " " + Thread.currentThread().toString());
        try {
            sleep(1000 * iSeconds);
        } catch(InterruptedException e) {
            Logg.w(TAG, e.toString());
        }
    }


    public static void callScan() {
        Context tContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        DataServiceSingleton.createInstance(tContext);
        DataServiceSingleton.getInstance().prepDataService()  ;
        DataServiceSingleton.getInstance().getDataService().scanMusic(tContext,null);

    }


    public static void prepareDatabases() {

        Logg.i(TAG, "prep");
        Context tContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        Runnable tRunnable = new Runnable() {
            @Override
            public void run() {
                Logg.i(TAG, "start DataService " + Thread.currentThread().toString());
                DataServiceSingleton.createInstance(tContext);
                DataServiceSingleton.getInstance().prepDataService();
                DataServiceSingleton.getInstance().getDataService();
            }
        };
        Thread tThread = new Thread(tRunnable);
        tThread.start();


        Logg.i(TAG, "prep done");

    }

    // preparation


}