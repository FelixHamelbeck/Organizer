package org.pochette.data_library.database;

import junit.framework.TestCase;

import org.junit.Rule;
import org.junit.Test;
import org.pochette.data_library.database_management.Scddb_Helper;
import org.pochette.data_library.diagram.Diagram;
import org.pochette.data_library.music.MusicDirectory;
import org.pochette.data_library.music.MusicDirectoryPurpose;
import org.pochette.data_library.music.MusicFile;
import org.pochette.data_library.music.MusicPreference;
import org.pochette.data_library.music.MusicPreferenceFavourite;
import org.pochette.data_library.pairing.Pairing;
import org.pochette.data_library.pairing.PairingObject;
import org.pochette.data_library.pairing.PairingSource;
import org.pochette.data_library.pairing.PairingStatus;
import org.pochette.data_library.playlist.Playinstruction;
import org.pochette.data_library.playlist.Playlist;
import org.pochette.data_library.playlist.Playlist_Purpose;
import org.pochette.data_library.scddb_objects.Dance;
import org.pochette.data_library.search.DeleteCall;
import org.pochette.data_library.search.SearchCall;
import org.pochette.data_library.service.DataService;
import org.pochette.utils_lib.logg.Logg;
import org.pochette.utils_lib.search.SearchPattern;

import androidx.test.rule.ServiceTestRule;

import static java.lang.Thread.sleep;

public class DataService_DeleteTest extends TestCase {

    private final static String TAG = "FEHA (DataServiceTest)";
    DataService mDataService;

    @Rule
    public final ServiceTestRule mServiceRule = new ServiceTestRule();




    @Test
    public void test_Diagram_Delete() {
        int tCountFirst;
        int tCount;
        String tTableName = "LDB.DIAGRAM";
        prepService();
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

        tCountFirst = Scddb_Helper.getInstance().getSize(tTableName);
        Logg.i(TAG, tTableName + ":" + tCountFirst);
        assertNotNull(sDiagram);
        assertTrue("At least one item in DB", tCountFirst > 0);

        new DeleteCall(Diagram.class, sDiagram).delete();
        tCount = Scddb_Helper.getInstance().getSize(tTableName);

        Logg.i(TAG, tTableName + ":" + tCount);
        assertEquals("Precisley one item removed", tCountFirst -1, tCount);
        Logg.i(TAG, "done");
    }

    @Test
    public void test_MusicDirectory_Delete() {
        int tCountFirst;
        int tCount;
        String tTableName = "LDB.MUSICDIRECTORY";
        prepService();
        MusicDirectory tMusicDirectory;
        try {
            tMusicDirectory = new MusicDirectory(0,"Path","T2","T1",23,56,"Signature", MusicDirectoryPurpose.CELTIC_LISTENING);
            tMusicDirectory.save();
        } catch(Exception e) {
            Logg.e(TAG, e.toString());
        }
        SearchPattern tSearchPattern = new SearchPattern(MusicDirectory.class);
        SearchCall tSearchCall = new SearchCall(tSearchPattern.getSearchClass(), tSearchPattern, null);
        MusicDirectory sMusicDirectory = tSearchCall.produceFirst();
        Logg.i(TAG, "ID: " + sMusicDirectory.mId);

        tCountFirst = Scddb_Helper.getInstance().getSize(tTableName);
        Logg.i(TAG, tTableName + ":" + tCountFirst);
        assertNotNull(sMusicDirectory);
        assertTrue("At least one item in DB", tCountFirst > 0);

        new DeleteCall(MusicDirectory.class, sMusicDirectory).delete();
        tCount = Scddb_Helper.getInstance().getSize(tTableName);

        Logg.i(TAG, tTableName + ":" + tCount);
        assertEquals("Precisley one item removed", tCountFirst -1, tCount);
        Logg.i(TAG, "done");
    }

    @Test
    public void test_MusicFile_Delete() {
        int tCountFirst;
        int tCount;
        String tTableName = "LDB.MUSICFILE";
        prepService();
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

        tCountFirst = Scddb_Helper.getInstance().getSize(tTableName);
        Logg.i(TAG, tTableName + ":" + tCountFirst);
        assertNotNull(sMusicFile);
        assertTrue("At least one item in DB", tCountFirst > 0);

        new DeleteCall(MusicFile.class, sMusicFile).delete();
        tCount = Scddb_Helper.getInstance().getSize(tTableName);

        Logg.i(TAG, tTableName + ":" + tCount);
        assertEquals("Precisley one item removed", tCountFirst -1, tCount);
        Logg.i(TAG, "done");

    }


    @Test
    public void test_MusicPreference_Delete() {
        int tCountFirst;
        int tCount;
        String tTableName = "LDB.MUSIC_PREFERENCE";
        prepService();
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
        tCountFirst = Scddb_Helper.getInstance().getSize(tTableName);
        Logg.i(TAG, tTableName + ":" + tCountFirst);
        assertNotNull(sMusicPreference);
        assertTrue("At least one item in DB", tCountFirst > 0);
        new DeleteCall(MusicPreference.class, sMusicPreference).delete();
        tCount = Scddb_Helper.getInstance().getSize(tTableName);

        Logg.i(TAG, tTableName + ":" + tCount);
        assertEquals("Precisley one item removed", tCountFirst -1, tCount);
        Logg.i(TAG, "done");
    }

    @Test
    public void test_Playinstruction_Delete() {
        int tCountFirst;
        int tCount;
        String tTableName = "LDB.PLAY_INSTRUCTION";
        prepService();
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
        tCountFirst = Scddb_Helper.getInstance().getSize(tTableName);
        Logg.i(TAG, tTableName + ":" + tCountFirst);
        assertNotNull(sPlayinstruction);
        assertTrue("At least one item in DB", tCountFirst > 0);

        new DeleteCall(Playinstruction.class, sPlayinstruction).delete();
        tCount = Scddb_Helper.getInstance().getSize(tTableName);

        Logg.i(TAG, tTableName + ":" + tCount);
        assertEquals("Precisley one item removed", tCountFirst -1, tCount);
        Logg.i(TAG, "done");
    }

    @Test
    public void test_Playlist_Delete() {
        int tCountFirst;
        int tCount;
        String tTableName = "LDB.PLAYLIST";
        prepService();
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
        tCountFirst = Scddb_Helper.getInstance().getSize(tTableName);
        Logg.i(TAG, tTableName + ":" + tCountFirst);
        assertNotNull(sPlaylist);
        assertTrue("At least one item in DB", tCountFirst > 0);

        new DeleteCall(Playlist.class, sPlaylist).delete();
        tCount = Scddb_Helper.getInstance().getSize(tTableName);

        Logg.i(TAG, tTableName + ":" + tCount);
        assertEquals("Precisley one item removed", tCountFirst -1, tCount);
        Logg.i(TAG, "done");



    }


    @Test
    public void test_Pairing_Write() {
        int tCountFirst;
        int tCount;
        String tTableName = "LDB.PAIRING";
        prepService();
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

        tCountFirst = Scddb_Helper.getInstance().getSize(tTableName);
        Logg.i(TAG, tTableName + ":" + tCountFirst);
        assertNotNull(sPairing);
        assertTrue("At least one item in DB", tCountFirst > 0);
        new DeleteCall(Pairing.class, sPairing).delete();

        tCount = Scddb_Helper.getInstance().getSize(tTableName);

        Logg.i(TAG, tTableName + ":" + tCount);
        assertEquals("Precisley one item removed", tCountFirst -1, tCount);
        Logg.i(TAG, "done");

    }






    public void prepService() {
        DataService_WriteTest.prepareDatabases();

    }

    // preparation




}