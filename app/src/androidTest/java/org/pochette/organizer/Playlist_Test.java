package org.pochette.organizer;

import android.content.Context;

import junit.framework.TestCase;

import org.junit.Rule;
import org.junit.Test;
import org.pochette.data_library.database_management.Ldb_Helper;
import org.pochette.data_library.database_management.Scddb_Helper;
import org.pochette.data_library.music.MusicFile;
import org.pochette.data_library.playlist.Playinstruction;
import org.pochette.data_library.playlist.Playlist;
import org.pochette.data_library.playlist.Playlist_Purpose;
import org.pochette.utils_lib.logg.Logg;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ServiceTestRule;

import static java.lang.Thread.sleep;

public class Playlist_Test extends TestCase {

    private final static String TAG = "FEHA (DataServiceTest)";


    @Rule
    public final ServiceTestRule mServiceRule = new ServiceTestRule();




    @Test
    public void test_Playlist_01() {
        prepareDatabases();
        Playlist tPlaylist = prepareTestData();

        Logg.i(TAG, tPlaylist.toString());
        assertNotNull("playlist must be available ",tPlaylist);
    }

    @Test
    public void test_Playlist_02() {
        prepareDatabases();
        Scddb_Helper.getInstance().purge("LDB.PLAYLIST");
        Scddb_Helper.getInstance().purge("LDB.PLAY_INSTRUCTION");

        Playlist tPlaylist;
        Playlist sPlaylist;
        tPlaylist = new Playlist( "XYZ", Playlist_Purpose.EVENT);


        MusicFile tMusicfile;
        tMusicfile = new MusicFile(1, "P1","N1","TZ1", "TE1", 101,1001);
        tPlaylist.add(new Playinstruction(0, tMusicfile, 11, 111,1.f,0L, 0L, 0, 0));
        tPlaylist.save();


//        Logg.i(TAG, "test start");
//        sPlaylist = Playlist.getByName("XYZ");
//        assertNotNull("playlist must be available ",sPlaylist);
//        sPlaylist.report();
//
//        sPlaylist.loadAL();
//        Logg.i(TAG, "past load");
//        sPlaylist.report();

    }


    public static Playlist prepareTestData() {

        Scddb_Helper.getInstance().purge("LDB.PLAYLIST");
        Scddb_Helper.getInstance().purge("LDB.PLAY_INSTRUCTION");
        Playlist tPlaylist = new Playlist( "XYZ", Playlist_Purpose.EVENT);


        MusicFile tMusicfile;
        tMusicfile = new MusicFile(1, "P1","N1","TZ1", "TE1", 101,1001);
        tPlaylist.add(new Playinstruction(0, tMusicfile, 11, 111,1.f,0L, 0L, 0, 0));
        tMusicfile = new MusicFile(2, "P2","N2","TZ2", "TE2", 102,1002);
        tPlaylist.add(new Playinstruction(0, tMusicfile, 12, 112,1.f,0L, 0L, 0, 0));
        tMusicfile = new MusicFile(3, "P3","N3","TZ3", "TE3", 103,1003);
        tPlaylist.add(1, new Playinstruction(0, tMusicfile, 13, 113,1.f,0L, 0L, 0, 0));
        tMusicfile = new MusicFile(4, "P4","N4","TZ4", "TE3", 104,1004);
        tPlaylist.add(new Playinstruction(0, tMusicfile, 14, 114,1.f,0L, 0L, 0, 0));

        tPlaylist.swap(0,2);
        tPlaylist.save();

        return tPlaylist;
    }





    public static void prepareDatabases() {

        Logg.i(TAG, "prep");
        Context tContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Ldb_Helper.createInstance(tContext);
        Scddb_Helper.createInstance(tContext);
        Scddb_Helper.getInstance().attachLdbToScddb(null);
        try {
            sleep(1000);
        } catch(InterruptedException e) {
            Logg.w(TAG, e.toString());
        }
        Logg.i(TAG, "prep done");

    }

    // preparation



}