package org.pochette.data_library.database;

import android.content.Context;

import junit.framework.TestCase;

import org.junit.Rule;
import org.junit.Test;
import org.pochette.data_library.database_management.Ldb_Helper;
import org.pochette.data_library.database_management.Scddb_Helper;
import org.pochette.data_library.pairing.Pairing;
import org.pochette.data_library.pairing.PairingObject;
import org.pochette.data_library.pairing.PairingSource;
import org.pochette.data_library.pairing.PairingStatus;
import org.pochette.data_library.diagram.Diagram;
import org.pochette.data_library.music.MusicDirectory;
import org.pochette.data_library.music.MusicDirectoryPurpose;
import org.pochette.data_library.music.MusicFile;
import org.pochette.data_library.music.MusicPreference;
import org.pochette.data_library.music.MusicPreferenceFavourite;
import org.pochette.data_library.playlist.Playinstruction;
import org.pochette.data_library.playlist.Playlist;
import org.pochette.data_library.playlist.Playlist_Purpose;
import org.pochette.data_library.scddb_objects.Dance;
import org.pochette.data_library.search.SearchCall;
import org.pochette.utils_lib.logg.Logg;
import org.pochette.utils_lib.search.SearchPattern;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ServiceTestRule;

import static java.lang.Thread.sleep;

public class DataService_WriteTest extends TestCase {

    private final static String TAG = "FEHA (DataServiceTest)";


    @Rule
    public final ServiceTestRule mServiceRule = new ServiceTestRule();




    @Test
    public void test_Diagram_Write() {
        prepareDatabases();
        Diagram tDiagram;
        try {
            tDiagram = new Diagram(0,"Path","Author",23, null);
            tDiagram.save();
        } catch(Exception e) {
            Logg.e(TAG, e.toString());
        }
        SearchPattern tSearchPattern = new SearchPattern(Diagram.class);
        SearchCall tSearchCall = new SearchCall(tSearchPattern.getSearchClass(), tSearchPattern, null);
        Diagram sDiagram= tSearchCall.produceFirst();
        Logg.i(TAG, "ID: " + sDiagram.getId());
        String tMethodName = new Object() {
        }.getClass().getEnclosingMethod().getName();
        Logg.i(TAG, tMethodName + " finished");
    }



    @Test
    public void test_MusicDirectory_Write() {
        prepareDatabases();
        MusicDirectory tMusicDirectory;
        try {
            tMusicDirectory = new MusicDirectory(0,"Path","T2","T1",23,56,"Signature", MusicDirectoryPurpose.CELTIC_LISTENING);
            tMusicDirectory.save();
        } catch(Exception e) {
            Logg.e(TAG, e.toString());
        }
        SearchPattern tSearchPattern = new SearchPattern(MusicDirectory.class);
        SearchCall tSearchCall = new SearchCall(tSearchPattern.getSearchClass(), tSearchPattern, null);
        MusicDirectory sMusicDirector = tSearchCall.produceFirst();
        Logg.i(TAG, "ID: " + sMusicDirector.mId);

        String tMethodName = new Object() {
        }.getClass().getEnclosingMethod().getName();
        Logg.i(TAG, tMethodName + " finished");
    }

    @Test
    public void test_MusicFile_Write() {
        prepareDatabases();
        MusicFile tMusicFile;
        try {
            tMusicFile = new MusicFile(0,"Path","Name","T2","T1",23,23);
            tMusicFile.save();
            tMusicFile = new MusicFile(0,"Pathx","Name","T2","T1",25,24);
            tMusicFile.save();
        } catch(Exception e) {
            Logg.e(TAG, e.toString());
        }
        SearchPattern tSearchPattern = new SearchPattern(MusicFile.class);
        SearchCall tSearchCall = new SearchCall(tSearchPattern.getSearchClass(), tSearchPattern, null);
        MusicFile sMusicFile = tSearchCall.produceFirst();
        Logg.i(TAG, "ID: " + sMusicFile.mId);

        String tMethodName = new Object() {
        }.getClass().getEnclosingMethod().getName();
        Logg.i(TAG, tMethodName + " finished");
    }


    @Test
    public void test_MusicPreference_Write() {
        prepareDatabases();
        MusicPreference tMusicPreference;
        try {
            MusicFile tMusicFile = new MusicFile(0,"Path","Name","T2","T1",76,23);
            tMusicFile.save();
            Dance tDance = Dance.getById(567);

            tMusicPreference = new MusicPreference(0, tMusicFile,tDance, MusicPreferenceFavourite.OKAY,true);
            tMusicPreference.save();
        } catch(Exception e) {
            Logg.e(TAG, e.toString());
        }
        SearchPattern tSearchPattern = new SearchPattern(MusicPreference.class);
        SearchCall tSearchCall = new SearchCall(tSearchPattern.getSearchClass(), tSearchPattern, null);
        MusicPreference sMusicPreference = tSearchCall.produceFirst();
        Logg.i(TAG, "ID: " + sMusicPreference. getId());

        String tMethodName = new Object() {
        }.getClass().getEnclosingMethod().getName();
        Logg.i(TAG, tMethodName + " finished");
    }

    @Test
    public void test_Playinstruction_Write() {
        prepareDatabases();
        Playinstruction tPlayinstruction;
        try {
            MusicFile tMusicFile = new MusicFile(0,"Path","Nxxame","T2","T1",99,23);
            tMusicFile.save();
            tPlayinstruction= new Playinstruction(0, tMusicFile, 12,15,.56f, 120l, 130l);

            tPlayinstruction.save();
        } catch(Exception e) {
            Logg.e(TAG, e.toString());
        }
        SearchPattern tSearchPattern = new SearchPattern(Playinstruction.class);
        SearchCall tSearchCall = new SearchCall(tSearchPattern.getSearchClass(), tSearchPattern, null);
        Playinstruction sPlayinstruction = tSearchCall.produceFirst();
        Logg.i(TAG, "ID: " + sPlayinstruction. getId());

        String tMethodName = new Object() {
        }.getClass().getEnclosingMethod().getName();
        Logg.i(TAG, tMethodName + " finished");
    }

    @Test
    public void test_Playlist_Write() {
        prepareDatabases();
        Playlist tPlaylist;
        try {
            tPlaylist= new Playlist(0, "Test name", Playlist_Purpose.EVENT);
            tPlaylist.save();
        } catch(Exception e) {
            Logg.e(TAG, e.toString());
        }
        SearchPattern tSearchPattern = new SearchPattern(Playlist.class);
        SearchCall tSearchCall = new SearchCall(tSearchPattern.getSearchClass(), tSearchPattern, null);
        Playlist sPlaylist = tSearchCall.produceFirst();
        Logg.i(TAG, "ID: " + sPlaylist.getId());

        String tMethodName = new Object() {
        }.getClass().getEnclosingMethod().getName();
        Logg.i(TAG, tMethodName + " finished");
    }


    @Test
    public void test_Pairing_Write() {
        prepareDatabases();
        Pairing tPairing;
        try {
            tPairing = new Pairing(PairingObject.MUSIC_DIRECTORY,
                    454, 123,
                    PairingSource.DIRECTORY_SCORE, PairingStatus.CONFIRMED,
                    null,
                    .4f);

            tPairing.save();
        } catch(Exception e) {
            Logg.e(TAG, e.toString());
        }
        SearchPattern tSearchPattern = new SearchPattern(Pairing.class);
        SearchCall tSearchCall = new SearchCall(tSearchPattern.getSearchClass(), tSearchPattern, null);
        Pairing sPairing = tSearchCall.produceFirst();
        Logg.i(TAG, "ID: " + sPairing. getId());
        String tMethodName = new Object() {
        }.getClass().getEnclosingMethod().getName();
        Logg.i(TAG, tMethodName + " finished");
    }





    @Test
    public static void prepareDatabases() {

        Scddb_FileTest.prepSSL();
        Context tContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        DataService_LdbTest.destroyEverything();

        Ldb_Helper.createInstance(tContext);
        Ldb_Helper.getInstance().prepareTables();

        Scddb_Helper.createInstance(tContext);
        Scddb_Helper.getInstance().attachLdbToScddb(null);

    }

    // preparation



}