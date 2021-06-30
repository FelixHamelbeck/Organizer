package org.pochette.data_library.pairing;


import android.database.Cursor;

import org.pochette.data_library.database_management.SearchCall;
import org.pochette.data_library.music.MusicDirectory;
import org.pochette.data_library.scddb_objects.Album;
import org.pochette.utils_lib.logg.Logg;
import org.pochette.utils_lib.search.SearchPattern;

import java.util.ArrayList;
import java.util.Locale;

/**
 * A POJO class to store on MusicDirectory and its pairings, needed for the display
 */
public class MusicDirectory_Pairing {

    private static final String TAG = "FEHA (MDP)";

    // variables
    private final MusicDirectory mMusicDirectory;
    private final ArrayList<Pairing> mAL_Pairing;
    private final ArrayList<Album> mAL_ALbum;


    // constructor
    public MusicDirectory_Pairing(MusicDirectory iMusicDirectory) {
        mMusicDirectory = iMusicDirectory;
        mAL_Pairing = new ArrayList<>(0);
        mAL_ALbum = new ArrayList<>(0);
    }
    // setter and getter

    public void add(Pairing iPairing) {
        if (iPairing.mPairingObject != PairingObject.MUSIC_DIRECTORY) {
            throw new IllegalArgumentException("Only Directory implemeneted");
        }
        if (iPairing.mLdb_Id != mMusicDirectory.mId) {
            String tText = String.format(Locale.ENGLISH,
                    "Pairing of MD %d does not belong in Details to %d", iPairing.mLdb_Id, mMusicDirectory.mId);
            throw new IllegalArgumentException(tText);
        }
        //
        for (Pairing lPairing : mAL_Pairing) {
            if (lPairing.mId == iPairing.mId) {
                return;
            }
        }
        Album tAlbum = null;
        try {
            tAlbum = new Album( iPairing.mScddb_Id);
        } catch(Exception e) {
            Logg.w(TAG, e.toString());
        }
        if (tAlbum != null) {
            mAL_Pairing.add(iPairing);
            mAL_ALbum.add(tAlbum);
        }
    }

    public void add(Pairing iPairing, Album iAlbum) {
        if (iPairing.mPairingObject != PairingObject.MUSIC_DIRECTORY) {
            throw new IllegalArgumentException("Only Directory implemeneted");
        }
        if (iPairing.mLdb_Id != mMusicDirectory.mId) {
            String tText = String.format(Locale.ENGLISH,
                    "Pairing of MD %d does not belong in Details to %d", iPairing.mLdb_Id, mMusicDirectory.mId);
            throw new IllegalArgumentException(tText);
        }
        for (Pairing lPairing : mAL_Pairing) {
            if (lPairing.mId == iPairing.mId) {
                return;
            }
        }
        if (iAlbum.mId != iPairing.getScddb_Id()) {
            String tText = String.format(Locale.ENGLISH,
                    "Pairing of Album %d does not belong in Details to %d", iPairing.mScddb_Id, iAlbum.mId);
            throw new IllegalArgumentException(tText);
        }
        mAL_Pairing.add(iPairing);
        mAL_ALbum.add(iAlbum);
    }

    public MusicDirectory getMusicDirectory() {
        return mMusicDirectory;
    }

    public ArrayList<Pairing> getAL_Pairing() {
        return mAL_Pairing;
    }

    public ArrayList<Album> getAL_ALbum() {
        return mAL_ALbum;
    }
    // lifecylce and override
    // internal
    // public methods

    /**
     * Take the database cursor and convert to MusicDirectory_Pairing
     * @param tCursor the database cursor
     * @return the new MusicDirectory_Pairing
     */
    public static MusicDirectory_Pairing convertCursor(Cursor tCursor) {
       // PairingDetail tPairingDetail;
        MusicDirectory_Pairing tMusicDirectory_Pairing;
        Pairing tPairing;
        Album  tAlbum;
        MusicDirectory tMusicDirectory;
        tMusicDirectory = MusicDirectory.convertCursor(tCursor);
        tMusicDirectory_Pairing = new MusicDirectory_Pairing(tMusicDirectory);
        int tColumnPairingId = tCursor.getColumnIndex("PR_ID");
        int tColumnAId = tCursor.getColumnIndex("A_ID");
        boolean tPairingExist = true;

        try {
            int tA_Id = tCursor.getInt(tColumnAId);
            if (tA_Id <= 0) {
                tPairingExist = false;
            }
            int tPairing_Id = tCursor.getInt(tColumnPairingId);
            if (tPairing_Id <= 0) {
                tPairingExist = false;
            }
        } catch(Exception e) {
            Logg.w(TAG, e.toString());
            tPairingExist = false;
        }
        if (tPairingExist) {
            tPairing = Pairing.convertCursor(tCursor);
            tAlbum = Album.convertCursor(tCursor);
            tMusicDirectory_Pairing.add(tPairing, tAlbum);
        }
        return tMusicDirectory_Pairing;
    }


    /**
     * Due to dependencies of the data the selection of MDP cannot be done straight through search call
     * if a pairing of a MD is part of the tempAR, all pairing of this MD should be listed for display
      * @param iSearchPattern
     * @return
     */
    public static ArrayList<MusicDirectory_Pairing> get(SearchPattern iSearchPattern) {

        Logg.i(TAG, "start get(SearchPattern)" );
        ArrayList<MusicDirectory_Pairing> tempAR;
        Logg.i(TAG, iSearchPattern.toString());
        SearchCall tSearchCall = new SearchCall(MusicDirectory_Pairing.class,
                iSearchPattern, null);
        tempAR = tSearchCall.produceArrayList();
        if (tempAR == null) {
            Logg.w(TAG, "Problem in readArrayList");
            return null;
        }
        Logg.i(TAG, "found in database" + tempAR.size());

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
            }
            if (lMusicDirectory_Pairing.getAL_Pairing().size() == 1) {
                jMusicDirectory_Pairing.add(
                        lMusicDirectory_Pairing.getAL_Pairing().get(0),
                        lMusicDirectory_Pairing.getAL_ALbum().get(0));
            } else //noinspection StatementWithEmptyBody
                if (lMusicDirectory_Pairing.getAL_Pairing().size() == 0) {

            } else {
                Logg.w(TAG, "bad pairing for " + lMusicDirectory_Pairing.getMusicDirectory().mT1);
                throw new RuntimeException("wtf " + lMusicDirectory_Pairing.getAL_Pairing().size());
            }
        }
        Logg.i(TAG, "end with " + tAR_MusicDirectory_Pairing.size());
        return tAR_MusicDirectory_Pairing;

    }




}
