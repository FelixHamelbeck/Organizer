package org.pochette.data_library.pairing;

import org.pochette.utils_lib.logg.Logg;
import org.pochette.data_library.music.MusicDirectory;
import org.pochette.data_library.scddb_objects.Album;
import org.pochette.data_library.search.SearchCall;
import org.pochette.utils_lib.search.SearchCriteria;
import org.pochette.utils_lib.search.SearchPattern;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PairingProcess {

    private static final String TAG = "FEHA (PairingProcess)";

    HashMap<String, Integer> mHM_MusicDirectory;
    HashMap<String, Integer> mHM_Album;
    ArrayList<Pairing> mAR_NewPairing; // the pairing calculated here
    ArrayList<Pairing> mAR_PastPairing; // the pairing found in the database
    ArrayList<Pairing> mAR_WritePairing; // the pairing, which will be written to the database

    HashMap<Integer, ArrayList<Pairing>> mXX_New;
    HashMap<Integer, ArrayList<Pairing>> mXX_Past;


    @SuppressWarnings({"FieldCanBeLocal", "FieldMayBeFinal"})
    private float mScoreLimit = 0.5f;

    public PairingProcess() {
        mHM_MusicDirectory = new HashMap<>(0);
        mHM_Album = new HashMap<>(0);
        mAR_NewPairing = new ArrayList<>(0);
        mXX_New = new HashMap<>(0);
        mXX_Past = new HashMap<>(0);
    }

    // variables
    // constructor
    // setter and getter
    // lifecylce and override
    // internal

    void readAlbum() {
        ArrayList<Album> tAR_Album = new ArrayList<>(0);
        SearchPattern tSearchPattern;
        SearchCall tSearchCall;
        tSearchPattern = new SearchPattern(Album.class);
        tSearchCall = new SearchCall(Album.class, tSearchPattern, null);
        tAR_Album = tSearchCall.produceArrayList();
        Integer tPrevValue;
        HashSet<String> tHS_DoubleSignature = new HashSet<>(0);
        if (tAR_Album != null) {
            for (Album lAlbum : tAR_Album) {
                tPrevValue = mHM_Album.put(lAlbum.getSignature(), lAlbum.mId);
                if (tPrevValue != null) {
                    tHS_DoubleSignature.add(lAlbum.getSignature());
                }
            }
            Logg.v(TAG, "HashMap " + mHM_Album.size());
            Logg.v(TAG, "Doubles " + tHS_DoubleSignature.size());
            for (String lString : tHS_DoubleSignature) {
                mHM_Album.remove(lString);
            }
            Logg.v(TAG, "HashMap " + mHM_Album.size());
        }
    }

    void readMusicDirectory() {
        ArrayList<MusicDirectory> tAR_MusicDirectory = new ArrayList<>(0);
        SearchPattern tSearchPattern;
        SearchCall tSearchCall;
        tSearchPattern = new SearchPattern(MusicDirectory.class);
        tSearchCall = new SearchCall(MusicDirectory.class, tSearchPattern, null);
        tAR_MusicDirectory = tSearchCall.produceArrayList();

        // directories without RJSMW are boring and ignored
        Pattern tPattern = Pattern.compile(".*[RJSMW]+.*");

//        Integer tPrevValue;
//        HashSet<String> tHS_DoubleSignature = new HashSet<>(0);
        if (tAR_MusicDirectory != null) {
            for (MusicDirectory lMusicDirectory : tAR_MusicDirectory) {
                // Logg.i(TAG, lMusicDirectory.mSignature);

                Matcher tMatcher = tPattern.matcher(lMusicDirectory.mSignature);
                if (tMatcher.find()) {
                    mHM_MusicDirectory.put(lMusicDirectory.mSignature, lMusicDirectory.mId);
                } else {
                    Logg.v(TAG, "ignore " + lMusicDirectory.mT1);
                    Logg.v(TAG, "because of boring signature " + lMusicDirectory.mSignature);
                }
            }
            Logg.v(TAG, "HashMap " + mHM_MusicDirectory.size());
        }
    }

    void pairByIdentity() {
        Logg.i(TAG, "PairByIdentiy");
        Pairing tPairing;

        // loop through a tmp clone, so we can remove from the orginal
        HashMap<String, Integer> tHM_MusicDirectory = (HashMap<String, Integer>) mHM_MusicDirectory.clone();
        for (String lMusicDirectorySignature : tHM_MusicDirectory.keySet()) {
            //Logg.i(TAG, lMusicDirectorySignature);

            if (mHM_Album.containsKey(lMusicDirectorySignature)) {
                int tLdbId = tHM_MusicDirectory.get(lMusicDirectorySignature);
                int tScddbId = mHM_Album.get(lMusicDirectorySignature);

                tPairing = new Pairing(PairingObject.MUSIC_DIRECTORY,
                        tScddbId, tLdbId,
                        PairingSource.DIRECTORY_SIGNATURE,
                        PairingStatus.CONFIRMED,
                        null,
                        1.f);
                mAR_NewPairing.add(tPairing);
                if (mXX_New.containsKey(tLdbId)) {
                    mXX_New.get(tLdbId).add(tPairing);
                } else {
                    ArrayList<Pairing> tAR_Pairing = new ArrayList<>(0);
                    tAR_Pairing.add(tPairing);
                    mXX_New.put(tLdbId, tAR_Pairing);
                }
                //Logg.i(TAG, "Pairing done");
                mHM_MusicDirectory.remove(lMusicDirectorySignature);
            }
            //    break;
        }
        Logg.i(TAG, "Pairings " + mAR_NewPairing.size());
        Logg.i(TAG, "Still in HM" + mHM_MusicDirectory.size());

    }

    void pairByScore() {
        Logg.i(TAG, "xPairByScore");
        Pairing tPairing;
        // loop through a tmp clone, so we can remove from the orginal
        HashMap<String, Integer> tHM_MusicDirectory = (HashMap<String, Integer>) mHM_MusicDirectory.clone();
        for (Map.Entry<String, Integer> lMapEntryLdb : tHM_MusicDirectory.entrySet()) {
            String tLdbSignatureString = lMapEntryLdb.getKey();
            Signature tLdbSignature = new Signature(tLdbSignatureString);
            Integer tLdbId = lMapEntryLdb.getValue();
            Map.Entry<String, Integer> tEntry;
            Iterator<Map.Entry<String, Integer>> tIterator;
            tIterator = mHM_Album.entrySet().iterator();
            TreeMap<Pairing,> tTS = new TreeSet
            //= new TreeSet<>( Pairing.getComparator(););
            while (tIterator.hasNext()) {
                tEntry = tIterator.next();
                Signature tScddbSignature = new Signature(tEntry.getKey());
                float tScore = tLdbSignature.compare(tScddbSignature);
                if (tScore > mScoreLimit) {
                    int tScddbId = tEntry.getValue();
//                    Logg.i(TAG, String.format(Locale.ENGLISH, "\nScore of %5.3f for \n%s and \n%s\n\n",
//                            tScore, tLdbSignature.mSignature, tScddbSignature.mSignature));
                    tPairing = new Pairing(PairingObject.MUSIC_DIRECTORY,
                            tScddbId, tLdbId,
                            PairingSource.DIRECTORY_SIGNATURE,
                            PairingStatus.CANDIDATE,
                            null,
                            tScore);
                    tTS.
                    mAR_NewPairing.add(tPairing);
                    if (mXX_New.containsKey(tLdbId)) {
                        mXX_New.get(tLdbId).add(tPairing);
                    } else {
                        ArrayList<Pairing> tAR_Pairing = new ArrayList<>(0);
                        tAR_Pairing.add(tPairing);
                        mXX_New.put(tLdbId, tAR_Pairing);
                    }
                }
            }



            // break;
        }
        Logg.i(TAG, "Pairings " + mAR_NewPairing.size());
        Logg.i(TAG, "Still in HM" + mHM_MusicDirectory.size());
    }


    void produceReport() {
        //for (Map.Entry<Integer, Pairing>  lEntry: mAR_NewPairing.entrySet()) {
        for (Pairing lPairing : mAR_NewPairing) {

            MusicDirectory tMusicDirectory;
            Album tAlbum;
//            Pairing lPairing;
//            lPairing = lEntry.getValue();
            SearchPattern tSearchPattern;
            SearchCall tSearchCall;
            tSearchPattern = new SearchPattern(MusicDirectory.class);
            tSearchPattern.addSearch_Criteria(new SearchCriteria("ID", "" + lPairing.mLdb_Id));
            tSearchCall = new SearchCall(MusicDirectory.class, tSearchPattern, null);
            tMusicDirectory = tSearchCall.produceFirst();

            tSearchPattern = new SearchPattern(Album.class);
            tSearchPattern.addSearch_Criteria(new SearchCriteria("ID", "" + lPairing.mScddb_Id));
            tSearchCall = new SearchCall(Album.class, tSearchPattern, null);
            tAlbum = tSearchCall.produceFirst();

            String tText = String.format(Locale.ENGLISH,
                    "%d: %d <=> %d %7.3f\n %s\n %s",
                    lPairing.mId, lPairing.mLdb_Id, lPairing.mScddb_Id, lPairing.mScore,
                    tMusicDirectory.toString(),
                    tAlbum.toString());
           // Logg.i(TAG, tText);
        }
    }

    void readPairing() {
        Logg.i(TAG, "readPairing");
        SearchPattern tSearchPattern;
        SearchCall tSearchCall;
        tSearchPattern = new SearchPattern(Pairing.class);
        tSearchCall = new SearchCall(Pairing.class, tSearchPattern, null);

        mAR_PastPairing = tSearchCall.produceArrayList();
        for (Pairing lPairing : mAR_PastPairing) {
           // Logg.i(TAG, lPairing.toString());
            if (mXX_Past.containsKey(lPairing.mLdb_Id)) {
                mXX_Past.get(lPairing.mLdb_Id).add(lPairing);
            } else {
                ArrayList<Pairing> tAR_Pairing = new ArrayList<>(0);
                tAR_Pairing.add(lPairing);
                mXX_Past.put(lPairing.mLdb_Id, tAR_Pairing);
            }
        }


        // create the hashmap
//        mHM_PastPairing = new HashMap<>(0);
//        for (Pairing lPairing : tAR_Pairing) {
//            mHM_PastPairing.put(lPairing.mLdb_Id, lPairing);
//        }

        Logg.i(TAG, "pastPairing" + mAR_PastPairing.size());
    }

    void mergeNewWithPast() {
        // the logic applies to each Ldb_id, so that is the main loop
        mAR_WritePairing = new ArrayList<>(0);
        for (Integer lLdb_Id : mXX_New.keySet()) {

            ArrayList<Pairing> lAR_New = mXX_New.get(lLdb_Id);

            ArrayList<Pairing> lAR_Past = mXX_Past.get(lLdb_Id);
            if (lAR_Past == null) {
                lAR_Past = new ArrayList<>(0);
            }
            String tText;

            if (lAR_Past.size() == 0) {
                mAR_WritePairing.addAll(lAR_New);
//                tText = String.format(Locale.ENGLISH,
//                        "No Past: Working on %d: New %d, Past %d => In WriteList %d",
//                        lLdb_Id, lAR_New.size(), lAR_Past.size(), mAR_WritePairing.size());
//                Logg.i(TAG, tText);
            } else {
                int tCountNewConfirmed = 0;
                int tCountPastConfirmed = 0;
                HashMap<Integer, Pairing> tHM_Scddb_New = new HashMap<>(0);
                HashMap<Integer, Pairing> tHM_Scddb_Past = new HashMap<>(0);
                Pairing tConfirmedPairing = null;
                for (Pairing lPairing : lAR_New) {
                    if (lPairing.mPairingStatus == PairingStatus.CONFIRMED) {
                        tCountNewConfirmed++;
                        tConfirmedPairing = lPairing;
                    }
                    tHM_Scddb_New.put(lPairing.mScddb_Id, lPairing);
                }
                for (Pairing lPairing : lAR_Past) {
                    if (lPairing.mPairingStatus == PairingStatus.CONFIRMED) {
                        tCountPastConfirmed++;
                    }
                    tHM_Scddb_Past.put(lPairing.mScddb_Id, lPairing);
                }
//                tText = String.format(Locale.ENGLISH,
//                        "Confirmed New %d: Past %d",
//                        tCountNewConfirmed, tCountPastConfirmed);
//                Logg.i(TAG, tText);
                if (tCountPastConfirmed > 0) {
               //     Logg.i(TAG, "Already confirmed in DB, the new stuff does not matter");
                    mAR_WritePairing.add(tConfirmedPairing);
//                    tText = String.format(Locale.ENGLISH,
//                            "Confirmed Past %d: New %d, Past %d => In WriteList %d",
//                            lLdb_Id, lAR_New.size(), lAR_Past.size(), mAR_WritePairing.size());
//                    Logg.i(TAG, tText);
                } else if (tCountNewConfirmed > 0) {
                    ArrayList<Pairing> tAR_Clone = (ArrayList<Pairing>) lAR_New.clone();
                    for (Pairing lPairing : tAR_Clone) {
                        if (lPairing.mPairingStatus == PairingStatus.CONFIRMED) {
                            mAR_WritePairing.add(lPairing);
                            lAR_New.remove(lPairing);
                        }
                    }
//                    tText = String.format(Locale.ENGLISH,
//                            "Confirmed New %d: New %d, Past %d => In WriteList %d",
//                            lLdb_Id, lAR_New.size(), lAR_Past.size(), mAR_WritePairing.size());
//                    Logg.i(TAG, tText);
                } else { // no confirmed in either new or past
                    for (Integer lScddbId : tHM_Scddb_New.keySet()) {
                        if (!tHM_Scddb_Past.containsKey(lScddbId)) {
                            mAR_WritePairing.add(tHM_Scddb_New.get(lScddbId));
                        } else {
                            // decide who wins
                            // new       +      past -> result
                            // CANDIDATE + CANDIDATE -> no Write, as nothing new
                            // CANDIDATE + REJECTED  -> no Write, as the database holds human edited data
                            // REJECTED  + *         -> no Write, impossible as no rejected are in new
                        }
                    }

                }


            }
//            tText = String.format(Locale.ENGLISH,
//                    "Working on %d: New %d, Past %d => In WriteList %d",
//                    lLdb_Id, lAR_New.size(), lAR_Past.size(), mAR_WritePairing.size());
//            Logg.i(TAG, tText);

        }


//        HashSet<Integer> tHS_NewId = new HashSet<>(0);
//        Logg.i(TAG, "NewPairing: " + mAR_NewPairing.size());
//        for (Pairing lPairing : mAR_NewPairing) {
//            tHS_NewId.add(lPairing.mLdb_Id);
//        }
//        Logg.i(TAG, "NeWId: " + tHS_NewId.size());
//        for (Integer lLdb_Id : tHS_NewId) {
//            Logg.i(TAG, "Ldb_Id" + lLdb_Id);
//            ArrayList<Pairing> lAR_New = new ArrayList<>(0);
//
//            ArrayList<Pairing> lAR_Past = new ArrayList<>(0);
//
//
//            break;
//        }

    }


    void writePairing() {
        for (Pairing lPairing : mAR_WritePairing) {
            lPairing.save();
        }
    }

    // public methods


    public void synchronizeexecute() {
        PairingDB tPairingDB = new PairingDB();
        Logg.i(TAG, "call MD");

        tPairingDB.updateMusicDirectory();

        Logg.i(TAG, "MD done");
        Logg.i(TAG, "call MF");
        tPairingDB.updateMusicFile();
        Logg.i(TAG, "MF done");

    }

    public void execute() {


        readAlbum();
        Logg.i(TAG, "HM Album " + mHM_Album.size());

        readMusicDirectory();
        Logg.i(TAG, "HM Album " + mHM_MusicDirectory.size());

        pairByIdentity();
        Logg.i(TAG, "past identiy");

        pairByScore();

        Logg.i(TAG, "past score");
        readPairing();
        Logg.i(TAG, "past read pairing");

        mergeNewWithPast();
        Logg.i(TAG, "past merge");

        writePairing();
        Logg.i(TAG, "past write");

        // produceReport();


    }
}
