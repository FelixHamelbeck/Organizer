package org.pochette.data_library.pairing;

import org.pochette.data_library.BuildConfig;
import org.pochette.data_library.database_management.SearchCall;
import org.pochette.data_library.music.MusicDirectory;
import org.pochette.data_library.music.MusicDirectoryPurpose;
import org.pochette.data_library.scddb_objects.Album;
import org.pochette.utils_lib.logg.Logg;
import org.pochette.utils_lib.report.ReportSystem;
import org.pochette.utils_lib.search.SearchPattern;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TreeSet;
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

    int mCountAlbum;
    int mCountMusicDirectory;
    int mCountMusicDirectoryRelevantSignature;
    int mCountMusicDirecortygByIdentity;
    int mCountMusicDirectoryByScoreCandiate;
    int mCountMusicDirectoryByScoreConfirmed;

    int mCountMusicFiles;
    int mCountMusicFilesPaired;

    private final float mScoreConfirmedLimit ;
    private final float mScoreCandiateLimit ;

    public PairingProcess() {
        mHM_MusicDirectory = new HashMap<>(0);
        mHM_Album = new HashMap<>(0);
        mAR_NewPairing = new ArrayList<>(0);
        mXX_New = new HashMap<>(0);
        mXX_Past = new HashMap<>(0);
        mCountAlbum = 0;
        mCountMusicDirectory = 0;
        mCountMusicDirectoryRelevantSignature = 0;
        mCountMusicFiles = 0;
        mCountMusicFilesPaired = 0;
        mCountMusicDirecortygByIdentity = 0;
        mCountMusicDirectoryByScoreCandiate = 0;
        mCountMusicDirectoryByScoreConfirmed = 0;

        mScoreConfirmedLimit = 0.7f;
        mScoreCandiateLimit = 0.3f;


    }

    // variables
    // constructor
    // setter and getter
    // lifecylce and override
    // internal

    void readAlbum() {
        ArrayList<Album> tAR_Album;
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
            Logg.d(TAG, "HashMap " + mHM_Album.size());
            Logg.d(TAG, "Doubles " + tHS_DoubleSignature.size());
            for (String lString : tHS_DoubleSignature) {
                mHM_Album.remove(lString);
            }
            Logg.d(TAG, "HashMap " + mHM_Album.size());
        }
        mCountAlbum = Objects.requireNonNull(tAR_Album).size();
    }

    void readMusicDirectory() {
        ArrayList<MusicDirectory> tAR_MusicDirectory;
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
                    if (lMusicDirectory.mMusicDirectoryPurpose != MusicDirectoryPurpose.SCD) {
                        lMusicDirectory.mMusicDirectoryPurpose = MusicDirectoryPurpose.SCD;
                        lMusicDirectory.save();
                    }
                } else {
                    Logg.d(TAG, "ignore " + lMusicDirectory.mT1);
                    Logg.d(TAG, "because of boring signature " + lMusicDirectory.mSignature);
                    if (lMusicDirectory.mMusicDirectoryPurpose == MusicDirectoryPurpose.SCD) {
                        lMusicDirectory.mMusicDirectoryPurpose = MusicDirectoryPurpose.LISTENING;
                        lMusicDirectory.save();
                    }
                }
            }
            Logg.d(TAG, "HashMap " + mHM_MusicDirectory.size());
        }
        mCountMusicDirectory = Objects.requireNonNull(tAR_MusicDirectory).size();
        mCountMusicDirectoryRelevantSignature = mHM_MusicDirectory.size();
    }

    void pairByIdentity() {
        Logg.d(TAG, "PairByIdentiy");
        Pairing tPairing;

        // loop through a tmp clone, so we can remove from the orginal
        @SuppressWarnings("unchecked")
        HashMap<String, Integer> tHM_MusicDirectory = (HashMap<String, Integer>) mHM_MusicDirectory.clone();
        for (String lMusicDirectorySignature : tHM_MusicDirectory.keySet()) {
            //Logg.i(TAG, lMusicDirectorySignature);

            if (mHM_Album.containsKey(lMusicDirectorySignature)) {
                int tLdbId;
                int tScddbId;
                Integer tInteger;
                tInteger = tHM_MusicDirectory.get(lMusicDirectorySignature);
                if (tInteger != null) {
                    tLdbId = tInteger;
                } else {
                    throw new RuntimeException("MusicDirectory ID missing");
                }
                tInteger = mHM_Album.get(lMusicDirectorySignature);
                if (tInteger != null) {
                    tScddbId = tInteger;
                } else {
                    throw new RuntimeException("Album ID missing");
                }
                tPairing = new Pairing(PairingObject.MUSIC_DIRECTORY,
                        tScddbId, tLdbId,
                        PairingSource.DIRECTORY_SIGNATURE,
                        PairingStatus.CONFIRMED,
                        null,
                        1.f);
                mAR_NewPairing.add(tPairing);
                if (mXX_New.containsKey(tLdbId)) {
                    Objects.requireNonNull(mXX_New.get(tLdbId)).add(tPairing);
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
        Logg.d(TAG, "Pairings " + mAR_NewPairing.size());
        Logg.d(TAG, "Still in HM" + mHM_MusicDirectory.size());
        mCountMusicDirecortygByIdentity = mAR_NewPairing.size();

    }

    void pairByScore() {
        Logg.i(TAG, "PairByScore");
        Pairing tPairing;
        // loop through a tmp clone, so we can remove from the orginal
        HashMap<String, Integer> tHM_MusicDirectory;
        //noinspection unchecked
        tHM_MusicDirectory = (HashMap<String, Integer>) mHM_MusicDirectory.clone();
        for (Map.Entry<String, Integer> lMapEntryLdb : tHM_MusicDirectory.entrySet()) {
            String tLdbSignatureString = lMapEntryLdb.getKey();
            Signature tLdbSignature = new Signature(tLdbSignatureString);
            Integer tLdbId = lMapEntryLdb.getValue();
            Map.Entry<String, Integer> tEntry;
            Iterator<Map.Entry<String, Integer>> tIterator;
            tIterator = mHM_Album.entrySet().iterator();
            TreeSet<Pairing> tTS = new TreeSet<>(Pairing.getComparator());
            while (tIterator.hasNext()) {
                tEntry = tIterator.next();
                Signature tScddbSignature = new Signature(tEntry.getKey());
                float tScore = tLdbSignature.compare(tScddbSignature);
                if (tScore > mScoreCandiateLimit) {
                    int tScddbId = tEntry.getValue();
                    tPairing = new Pairing(PairingObject.MUSIC_DIRECTORY,
                            tScddbId, tLdbId,
                            PairingSource.DIRECTORY_SIGNATURE,
                            PairingStatus.CANDIDATE,
                            null,
                            tScore);
                //    mAR_NewPairing.add(tPairing);
                    tTS.add(tPairing);
//                    if (mXX_New.containsKey(tLdbId)) {
//                        Objects.requireNonNull(mXX_New.get(tLdbId)).add(tPairing);
//                    } else {
//                        ArrayList<Pairing> tAR_Pairing = new ArrayList<>(0);
//                        tAR_Pairing.add(tPairing);
//                        mXX_New.put(tLdbId, tAR_Pairing);
//                    }
                }
            }
            if (tTS.size() > 0) {

                Pairing tBestPairing = tTS.first();
         //       Logg.i(TAG, String.format(Locale.ENGLISH, "Best %5f %d ", tBestPairing.getScore() * 100, tBestPairing.getScddb_Id()));
//                for (Pairing lPairing : tTS) {
//                    Logg.i(TAG, String.format(Locale.ENGLISH, "%5f %d ", lPairing.getScore() * 100, lPairing.getScddb_Id()));
//                }
                // if the Best is above the confirmed limit add only this
                if (tBestPairing.getScore() > mScoreConfirmedLimit) {
                    mAR_NewPairing.add(tBestPairing);
                    if (mXX_New.containsKey(tLdbId)) {
                        Objects.requireNonNull(mXX_New.get(tLdbId)).add(tBestPairing);
                    } else {
                        mCountMusicDirectoryByScoreConfirmed ++;
                        ArrayList<Pairing> tAR_Pairing = new ArrayList<>(0);
                        tAR_Pairing.add(tBestPairing);
                        mXX_New.put(tLdbId, tAR_Pairing);
                    }
                } else {
                    for (Pairing lPairing : tTS) {
                        mAR_NewPairing.add(lPairing);
                        if (mXX_New.containsKey(tLdbId)) {
                            Objects.requireNonNull(mXX_New.get(tLdbId)).add(lPairing);
                        } else {
                            mCountMusicDirectoryByScoreCandiate ++;
                            ArrayList<Pairing> tAR_Pairing = new ArrayList<>(0);
                            tAR_Pairing.add(lPairing);
                            mXX_New.put(tLdbId, tAR_Pairing);
                        }
                    }
                }
            }
        }
        if (BuildConfig.DEBUG) {
            Logg.d(TAG, "Pairings " + mAR_NewPairing.size());
            Logg.d(TAG, "Still in HM" + mHM_MusicDirectory.size());
        }
    }


//    void produceReport() {
//        for (Pairing lPairing : mAR_NewPairing) {
//
//            MusicDirectory tMusicDirectory;
//            Album tAlbum;
//            SearchPattern tSearchPattern;
//            SearchCall tSearchCall;
//            tSearchPattern = new SearchPattern(MusicDirectory.class);
//            tSearchPattern.addSearch_Criteria(new SearchCriteria("ID", "" + lPairing.mLdb_Id));
//            tSearchCall = new SearchCall(MusicDirectory.class, tSearchPattern, null);
//            tMusicDirectory = tSearchCall.produceFirst();
//
//            tSearchPattern = new SearchPattern(Album.class);
//            tSearchPattern.addSearch_Criteria(new SearchCriteria("ID", "" + lPairing.mScddb_Id));
//            tSearchCall = new SearchCall(Album.class, tSearchPattern, null);
//            tAlbum = tSearchCall.produceFirst();
//
//            String tText = String.format(Locale.ENGLISH,
//                    "%d: %d <=> %d %7.3f\n %s\n %s",
//                    lPairing.mId, lPairing.mLdb_Id, lPairing.mScddb_Id, lPairing.mScore,
//                    tMusicDirectory.toString(),
//                    tAlbum.toString());
//            // Logg.i(TAG, tText);
//        }
//    }

    void readPairing() {
        //Logg.i(TAG, "readPairing");
        SearchPattern tSearchPattern;
        SearchCall tSearchCall;
        tSearchPattern = new SearchPattern(Pairing.class);
        tSearchCall = new SearchCall(Pairing.class, tSearchPattern, null);

        mAR_PastPairing = tSearchCall.produceArrayList();
        for (Pairing lPairing : mAR_PastPairing) {
            // Logg.i(TAG, lPairing.toString());
            if (mXX_Past.containsKey(lPairing.mLdb_Id)) {
                Objects.requireNonNull(mXX_Past.get(lPairing.mLdb_Id)).add(lPairing);
            } else {
                ArrayList<Pairing> tAR_Pairing = new ArrayList<>(0);
                tAR_Pairing.add(lPairing);
                mXX_Past.put(lPairing.mLdb_Id, tAR_Pairing);
            }
        }
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
            if (lAR_Past.size() == 0) {
                mAR_WritePairing.addAll(Objects.requireNonNull(lAR_New));
            } else {
                int tCountNewConfirmed = 0;
                int tCountPastConfirmed = 0;
                HashMap<Integer, Pairing> tHM_Scddb_New = new HashMap<>(0);
                HashMap<Integer, Pairing> tHM_Scddb_Past = new HashMap<>(0);
                Pairing tConfirmedPairing = null;
                for (Pairing lPairing : Objects.requireNonNull(lAR_New)) {
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
                if (tCountPastConfirmed > 0) {
                    mAR_WritePairing.add(tConfirmedPairing);
                } else if (tCountNewConfirmed > 0) {
                    ArrayList<Pairing> tAR_Clone;
                    //noinspection unchecked
                    tAR_Clone = (ArrayList<Pairing>) lAR_New.clone();
                    for (Pairing lPairing : tAR_Clone) {
                        if (lPairing.mPairingStatus == PairingStatus.CONFIRMED) {
                            mAR_WritePairing.add(lPairing);
                            lAR_New.remove(lPairing);
                        }
                    }
                } else { // no confirmed in either new or past
                    for (Integer lScddbId : tHM_Scddb_New.keySet()) {
                        if (!tHM_Scddb_Past.containsKey(lScddbId)) {
                            mAR_WritePairing.add(tHM_Scddb_New.get(lScddbId));
                        }
                        //else {
                        // decide who wins
                        // new       +      past -> result
                        // CANDIDATE + CANDIDATE -> no Write, as nothing new
                        // CANDIDATE + REJECTED  -> no Write, as the database holds human edited data
                        // REJECTED  + *         -> no Write, impossible as no rejected are in new
                        //}
                    }
                }
            }
        }
    }

    void writePairing() {
        for (Pairing lPairing : mAR_WritePairing) {
            if (lPairing != null) {
                lPairing.save();
            }
        }
    }

    // public methods


    /**
     * only confirmed pairs are applied, candidate pairs are not written to MusicDirectory and MusicFile
     */

    public void excuteSynchronize() {
        Logg.i(TAG, "Start Synchronize");
        PairingDB tPairingDB = new PairingDB();
        tPairingDB.updateMusicDirectory(null);
        tPairingDB.updateMusicFile(null);
        Logg.i(TAG, "Finished Synchronize");
        String tText;
        tText = "Synchronisation of pairs finished ";
        ReportSystem.receive(tText);
    }

    public void executeIdentify() {
        Logg.i(TAG, "Start execute Identify");
        readAlbum();
        if (BuildConfig.DEBUG) {
            Logg.d(TAG, "HM Album " + mHM_Album.size());
        }
        readMusicDirectory();
        if (BuildConfig.DEBUG) {
            Logg.d(TAG, "HM Album " + mHM_MusicDirectory.size());
        }
        pairByIdentity();
        if (BuildConfig.DEBUG) {
            Logg.d(TAG, "past identiy");
        }
        pairByScore();
        if (BuildConfig.DEBUG) {
            Logg.d(TAG, "past score");
        }
        readPairing();
        if (BuildConfig.DEBUG) {
            Logg.d(TAG, "past read pairing");
        }
        mergeNewWithPast();
        if (BuildConfig.DEBUG) {
            Logg.d(TAG, "past merge");
        }
        writePairing();
        if (BuildConfig.DEBUG) {
            Logg.d(TAG, "past write");
        }
        String tText;
        tText = String.format(Locale.ENGLISH,
                "%d Albums to be paired with %d directories, of which %d show a signature",
                mCountAlbum, mCountMusicDirectory, mCountMusicDirectoryRelevantSignature);
        Logg.i(TAG, tText);

        ReportSystem.receive(tText);
        tText = String.format(Locale.ENGLISH,
                "%5d pairs by identical signatures (Confirmed), " +
                        "%5d pairs by very good fit (Confirmed) and %d pairs by score (Candidates)",
                mCountMusicDirecortygByIdentity,
                mCountMusicDirectoryByScoreConfirmed,
                mCountMusicDirectoryByScoreCandiate);
        Logg.i(TAG, tText);
        ReportSystem.receive(tText);
        Logg.i(TAG, "Start finished Identify");
        tText = "Identification of pairs finished ";
        ReportSystem.receive(tText);
    }
}
