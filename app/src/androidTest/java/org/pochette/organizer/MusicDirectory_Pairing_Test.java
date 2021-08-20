package org.pochette.organizer;

import android.content.Context;

import junit.framework.TestCase;

import org.junit.Rule;
import org.junit.Test;
import org.pochette.data_library.database_management.Ldb_Helper;
import org.pochette.data_library.database_management.Scddb_Helper;
import org.pochette.data_library.database_management.SearchCall;
import org.pochette.data_library.pairing.MusicDirectory_Pairing;
import org.pochette.utils_lib.logg.Logg;
import org.pochette.utils_lib.search.SearchPattern;

import java.util.ArrayList;
//
//import androidx.test.platform.app.InstrumentationRegistry;
//import androidx.test.rule.ServiceTestRule;

import static java.lang.Thread.sleep;

public class MusicDirectory_Pairing_Test extends TestCase {

    private final static String TAG = "FEHA (MDPTest)";


//    @Rule
//    public final ServiceTestRule mServiceRule = new ServiceTestRule();


    @Test
    public void test_MDP_01() {
        prepareDatabases();
        SearchPattern tSearchPattern = new SearchPattern(MusicDirectory_Pairing.class);
        ArrayList<MusicDirectory_Pairing> tAR;
        SearchCall tSearchCall = new SearchCall(MusicDirectory_Pairing.class,
                tSearchPattern, null);
        tAR = tSearchCall.produceArrayList();
        if (tAR == null) {
            Logg.w(TAG, "Problem in readArrayListe");
            Logg.i(TAG, tSearchPattern.toString());
            return;
        }
        Logg.i(TAG, "found " + tAR.size());


    }


    @Test
    public void test_MDP_02() {
        prepareDatabases();
        SearchPattern tSearchPattern = new SearchPattern(MusicDirectory_Pairing.class);
        ArrayList<MusicDirectory_Pairing> tempAR;
        SearchCall tSearchCall = new SearchCall(MusicDirectory_Pairing.class,
                tSearchPattern, null);
        tempAR = tSearchCall.produceArrayList();
        if (tempAR == null) {
            Logg.w(TAG, "Problem in readArrayListe");
            Logg.i(TAG, tSearchPattern.toString());
            return;
        }
        Logg.i(TAG, "found " + tempAR.size());

        // l Loop: the available to be slotted
        // k loop: the ones in the resulting array
        // j: the one i would add to currently

        ArrayList<MusicDirectory_Pairing> tAR_MusicDirectory_Pairing = new ArrayList<>(0);
        for (MusicDirectory_Pairing lMusicDirectory_Pairing : tempAR) {
            boolean tAlreadyIn = false;
            MusicDirectory_Pairing jMusicDirectory_Pairing = null;
            for (MusicDirectory_Pairing kMusicDirectory_Pairing : tAR_MusicDirectory_Pairing) {
                if (kMusicDirectory_Pairing.getMusicDirectory().mId ==
                        lMusicDirectory_Pairing.getMusicDirectory().mId
                ) {
                    tAlreadyIn = true;
                    jMusicDirectory_Pairing = kMusicDirectory_Pairing;
                    break;
                }
            }
            if (!tAlreadyIn) {
                jMusicDirectory_Pairing =
                        new MusicDirectory_Pairing(lMusicDirectory_Pairing.getMusicDirectory());
                tAR_MusicDirectory_Pairing.add(jMusicDirectory_Pairing);
                Logg.w(TAG, "add to r array" + jMusicDirectory_Pairing.getMusicDirectory().mT1);
            }
            if (lMusicDirectory_Pairing.getAL_Pairing().size() == 1) {
                jMusicDirectory_Pairing.add(
                        lMusicDirectory_Pairing.getAL_Pairing().get(0),
                        lMusicDirectory_Pairing.getAL_ALbum().get(0));
                Logg.w(TAG, "add Album for " + lMusicDirectory_Pairing.getMusicDirectory().mT1);
            } else if (lMusicDirectory_Pairing.getAL_Pairing().size() == 0) {
                Logg.w(TAG, "nacked pairing for " + lMusicDirectory_Pairing.getMusicDirectory().mT1);
            } else {
                Logg.w(TAG, "bad pairing for " + lMusicDirectory_Pairing.getMusicDirectory().mT1);
                throw new RuntimeException("wtf " + lMusicDirectory_Pairing.getAL_Pairing().size());
            }
        }
        Logg.w(TAG, "end with " + tAR_MusicDirectory_Pairing.size());


    }


    public static void prepareDatabases() {

//        Logg.i(TAG, "prep");
//       // Context tContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
//        Ldb_Helper.createInstance(tContext);
//        Scddb_Helper.createInstance(tContext);
//        Scddb_Helper.getInstance().attachLdbToScddb(null);
//        try {
//            sleep(3000);
//        } catch(InterruptedException e) {
//            Logg.w(TAG, e.toString());
//        }
//        Logg.i(TAG, "prep done");

    }

    // preparation


}