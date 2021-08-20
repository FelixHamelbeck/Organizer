package org.pochette.organizer.music;

import org.pochette.data_library.database_management.DataService;
import org.pochette.data_library.database_management.SearchCall;
import org.pochette.data_library.music.MusicFile;
import org.pochette.organizer.app.DataServiceSingleton;
import org.pochette.utils_lib.logg.Logg;
import org.pochette.utils_lib.search.SearchCriteria;
import org.pochette.utils_lib.search.SearchPattern;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;

/**
 * Cache to store MusicFile data, so on scorlling etc the database call can be avoided
 * it is assumed you know already the id of the MusicFile
 */
public class MusicFile_Cache {

    private static final String TAG = "FEHA (MusicFile_Cache)";
    private final static int mMaxNumber = 120;
    private final static int mPackageSize = 10;

    private static MusicFile_Cache mInstance;

    // variables

    private final Object mLock;
    private final HashMap<Integer, MusicFile> mCache;
    private final ArrayList<Integer> mUsage;

    private HashMap<String, ArrayList<Integer>> mHM_Name;
    private HashMap<String, ArrayList<Integer>> mHM_T2;
    private HashMap<String, ArrayList<Integer>> mHM_T1;
    private HashMap<Integer, ArrayList<Integer>> mHM_Dance;
    private HashMap<String, ArrayList<Integer>> mHM_Signature;
    private HashMap<String, ArrayList<Integer>> mHM_Purpose;
    private boolean mFlagUptodate;


    // constructor
    public static MusicFile_Cache getInstance() {
        //Double check locking pattern
        if (mInstance == null) { //Check for the first time
            synchronized (DataServiceSingleton.class) {   //Check for the second time.
                if (mInstance == null) {
                    mInstance = new MusicFile_Cache();
                }
            }
        }
        return mInstance;
    }

    private MusicFile_Cache() {
        mCache = new HashMap<>(0);
        mUsage = new ArrayList<>(0);
        mLock = new Object();
        mFlagUptodate = false;
        fillHashMaps();
    }
    // setter and getter
    // lifecylce and override

    // internal

    void fillHashMaps() {

        Logg.i(TAG, "start fillHashMaps");
        SearchPattern tSearchPattern = new SearchPattern(MusicFile.class);
        SearchCall tSearchCall = new SearchCall(MusicFile.class, tSearchPattern, null);
        ArrayList<MusicFile> tAR = tSearchCall.produceArrayList();
        Logg.i(TAG, "read for HM, found " + tAR.size());

        mHM_Name = new HashMap<>(0);
        mHM_T1 = new HashMap<>(0);
        mHM_T2 = new HashMap<>(0);
        mHM_Dance = new HashMap<>(0);
        mHM_Signature = new HashMap<>(0);
        mHM_Purpose = new HashMap<>(0);
        for (MusicFile lMusicFile : tAR) {
            String tKey;
            tKey = lMusicFile.mName.toLowerCase();
            if (mHM_Name.containsKey(tKey)) {
                mHM_Name.get(tKey).add(lMusicFile.mId);
            } else {
                ArrayList<Integer> tAR4Key = new ArrayList<>(0);
                tAR4Key.add(lMusicFile.mId);
                mHM_Name.put(tKey, tAR4Key);
            }

            tKey = lMusicFile.mT1.toLowerCase();
            if (mHM_T1.containsKey(tKey)) {
                mHM_T1.get(tKey).add(lMusicFile.mId);
            } else {
                ArrayList<Integer> tAR4Key = new ArrayList<>(0);
                tAR4Key.add(lMusicFile.mId);
                mHM_T1.put(tKey, tAR4Key);
            }
            tKey = lMusicFile.mT2.toLowerCase();
            if (mHM_T2.containsKey(tKey)) {
                mHM_T2.get(tKey).add(lMusicFile.mId);
            } else {
                ArrayList<Integer> tAR4Key = new ArrayList<>(0);
                tAR4Key.add(lMusicFile.mId);
                mHM_T2.put(tKey, tAR4Key);
            }
            tKey = lMusicFile.mSignature.toLowerCase();
            if (mHM_Signature.containsKey(tKey)) {
                mHM_Signature.get(tKey).add(lMusicFile.mId);
            } else {
                ArrayList<Integer> tAR4Key = new ArrayList<>(0);
                tAR4Key.add(lMusicFile.mId);
                mHM_Signature.put(tKey, tAR4Key);
            }
            tKey = lMusicFile.mPurpose.getCode().toLowerCase();
            if (mHM_Purpose.containsKey(tKey)) {
                mHM_Purpose.get(tKey).add(lMusicFile.mId);
            } else {
                ArrayList<Integer> tAR4Key = new ArrayList<>(0);
                tAR4Key.add(lMusicFile.mId);
                mHM_Purpose.put(tKey, tAR4Key);
            }
            if (lMusicFile.mDanceId > 0) {
                int tDanceid = lMusicFile.mDanceId;
                if (mHM_Dance.containsKey(tDanceid)) {
                    mHM_Dance.get(tDanceid).add(lMusicFile.mId);
                } else {
                    ArrayList<Integer> tAR4Key = new ArrayList<>(0);
                    tAR4Key.add(lMusicFile.mId);
                    mHM_Dance.put(tDanceid, tAR4Key);
                }
            }
        }
        Logg.i(TAG, "stored in HM, found " + tAR.size());
        mFlagUptodate = true;
    }

    public Integer[] searchFromHM(SearchPattern iSearchPattern) {

        //   Logg.i(TAG, "do i need to call fill");
        if (!mFlagUptodate) {
            fillHashMaps();
        }
        Logg.i(TAG, "searchFrom HM Start");
        HashSet<Integer> tHS_Result = null;
        // tHS_Result.addAll(mHM_Name.values());
        int lLoop = 0;
        for (SearchCriteria lSearchCriteria : iSearchPattern.getAL_SearchCriteria()) {
            HashSet<Integer> lHSResult = new HashSet<>(0);
            if (lSearchCriteria.getMethod().equals("NAME")) {
                String lCompString = lSearchCriteria.getValue().toLowerCase();
                for (Map.Entry<String, ArrayList<Integer>> lEntry : mHM_Name.entrySet()) {
                    if (lEntry.getKey().contains(lCompString)) {
                        lHSResult.addAll(lEntry.getValue());
                        //          Logg.i(TAG, lCompString + "->" + lEntry.getKey()+" has "+ lEntry.getValue().size());
                    }
                }
            } else if (lSearchCriteria.getMethod().equals("ARTIST")) {
                String lCompString = lSearchCriteria.getValue().toLowerCase();
                for (Map.Entry<String, ArrayList<Integer>> lEntry : mHM_T2.entrySet()) {
                    if (lEntry.getKey().contains(lCompString)) {
                        lHSResult.addAll(lEntry.getValue());
                        //      Logg.i(TAG, lCompString + "->" + lEntry.getKey()+" has "+ lEntry.getValue().size());
                    }
                }
            } else if (lSearchCriteria.getMethod().equals("ALBUM")) {
                String lCompString = lSearchCriteria.getValue().toLowerCase();
                for (Map.Entry<String, ArrayList<Integer>> lEntry : mHM_T1.entrySet()) {
                    if (lEntry.getKey().contains(lCompString)) {
                        lHSResult.addAll(lEntry.getValue());
                    }
                }
            } else if (lSearchCriteria.getMethod().equals("DANCE_ID")) {
                try {
                    Integer tDanceId = Integer.valueOf(lSearchCriteria.getValue());
                    for (Map.Entry<Integer, ArrayList<Integer>> lEntry : mHM_Dance.entrySet()) {
                        if (lEntry.getKey() == tDanceId) {
                            lHSResult.addAll(lEntry.getValue());
                        }
                    }
                } catch(NumberFormatException e) {
                    Logg.w(TAG, e.toString());
                }
            } else {
                // if it is not implemented return null, so the caller calls a proper DB query
                return null;
            }
            Logg.i(TAG, "searchFrom loop " + lSearchCriteria.getMethod() + " found " + lHSResult.size());
            if (lLoop == 0) {
                tHS_Result = lHSResult;
            } else {
                tHS_Result.retainAll(lHSResult);
            }
            Logg.i(TAG, "still " + tHS_Result.size());
            // no need to continue
            if (tHS_Result.size() == 0) {
                Integer[] tA = new Integer[0];
                return tA;
            }
            lLoop++;
        }
        if (lLoop == 0) {
            // no criteria, return all
            for (Map.Entry<String, ArrayList<Integer>> lEntry : mHM_T2.entrySet()) {
                tHS_Result = new HashSet<>(0);
                tHS_Result.addAll(lEntry.getValue());
            }
        }
        Integer[] tA = new Integer[tHS_Result.size()];
        tA = tHS_Result.toArray(new Integer[tHS_Result.size()]);
        return tA;
    }


    @SuppressWarnings("unused")
    void logUsage() {
        Logg.i(TAG, "size " + mUsage.size());
        for (Integer lId : mUsage) {
            Logg.i(TAG, "" + lId);
        }
    }

    MusicFile getFromCache(Integer iId) {
        //   Logg.i(TAG, "getFromCacge " + iId);
        synchronized (mLock) {
            if (mCache.containsKey(iId)) {
                return mCache.get(iId);
            }
        }
        //logUsage();
        return null;
    }

    void addToCache(Integer iId, MusicFile iMusicFile) {
        synchronized (mLock) {
            mCache.put(iId, iMusicFile);
            Integer tToBeDeletedId = remeberIdWasUsed(iId);
            if (tToBeDeletedId > 0) {
                //       Logg.i(TAG, "to be deleted " + tToBeDeletedId);
                mCache.remove(tToBeDeletedId);
            }
        }
    }


    void addToCache(ArrayList<MusicFile> iAL) {

        // Logg.i(TAG, "start add bz AL " + mUsage.size());
        synchronized (mLock) {
            for (MusicFile lMusicFile : iAL) {
                mCache.put(lMusicFile.mId, lMusicFile);
                Integer tToBeDeletedId = remeberIdWasUsed(lMusicFile.mId);
                if (tToBeDeletedId > 0) {
                    //Logg.i(TAG, "to be deleted " + tToBeDeletedId);
                    mCache.remove(tToBeDeletedId);
                }
            }
        }
        //   Logg.i(TAG, "add bz AL " + mUsage.size());
    }


    Integer remeberIdWasUsed(Integer iId) {
        // do not use synchronized insided, as this methid is only called from within sychnronized

        // remove if the id is already in the list further down
        //  if (mUsage.contains(iId)) {
        mUsage.remove(iId);
        //  }
        // add at the top
        mUsage.add(0, iId);
        // remove at the end if the tail is to long
        if (mUsage.size() > mMaxNumber) {
            int tIndex = mUsage.size() - 1;
            Integer tToBeDeletedId = mUsage.get(tIndex);
            mUsage.remove(tIndex);
            return tToBeDeletedId;
        }
        return 0;
        //  logUsage();
    }


    // public methods

    /**
     * If the data has changed remove item from cache
     *
     * @param iId to be removed
     */
    @SuppressWarnings("unused")
    public static void removeFromCacheId(int iId) {
        MusicFile_Cache tMusicFileCache = MusicFile_Cache.getInstance();
        tMusicFileCache.mUsage.remove(iId);
        tMusicFileCache.mCache.remove(iId);
    }


    /**
     * If the correct data is known and should be cached
     *
     * @param iMusicFile to be refreshed in the cache
     */
    @SuppressWarnings("unused")
    public static void updateCache(MusicFile iMusicFile) {
        MusicFile_Cache tMusicFileCache = MusicFile_Cache.getInstance();
        tMusicFileCache.mCache.put(iMusicFile.mId, iMusicFile);
        tMusicFileCache.remeberIdWasUsed(iMusicFile.mId);
    }

    /**
     * retrieve from cache, if not yet cached, get from database
     *
     * @param iId to be returned
     * @return the MusicFile
     */
    public static MusicFile getById(int iId) {

        MusicFile_Cache tMusicFileCache = MusicFile_Cache.getInstance();
        MusicFile tMusicFile;
        tMusicFile = tMusicFileCache.getFromCache(iId);
        if (tMusicFile == null) {
            try {
                tMusicFile = MusicFile.getById(iId);
                tMusicFileCache.addToCache(iId, tMusicFile);
            } catch(Exception e) {
                Logg.w(TAG, e.toString());
            }
        } else {
            tMusicFileCache.remeberIdWasUsed(iId);
        }
        return tMusicFile;
    }

    /**
     * This method prereads the MusicFiles of Array iA
     *
     * @param iA Array of Ids to be preread
     */
    public static void preread(Integer[] iA) {
        if (iA == null || iA.length == 0) {
            return;
        }
        Logg.i(TAG, "preread " + iA.length);
        DataService tDataService = DataServiceSingleton.getInstance().getDataService();
        MusicFile_Cache tMusicFileCache = MusicFile_Cache.getInstance();
        String tListOfId = "";
        int jArray = 0;
        int jList = 0;
        while (jArray < iA.length && jArray < mMaxNumber) {
            Integer tId = iA[jArray];
            //         Logg.i(TAG, " " + jArray + " " + jList+ "->"+ tId);
            jArray++;
            if (tMusicFileCache.mUsage.contains(tId)) {
                // if it is already in cache exclude from retrieval
                // worst would be if removed from cache prematurely
                //            Logg.w(TAG, "no preread for " + tId);
                continue;
            }
            if (jList == 0) {
                tListOfId = String.format(Locale.ENGLISH, "%d", tId);
            } else {
                tListOfId = String.format(Locale.ENGLISH, "%s, %d", tListOfId, tId);
            }
            jList++;
            if (jList == mPackageSize || jArray == iA.length || jArray == mMaxNumber) {
                //  Logg.i(TAG, "Search with list  " + tListOfId);
                SearchPattern tSearchPattern = new SearchPattern(MusicFile.class);
                tSearchPattern.addSearch_Criteria(new SearchCriteria("LIST_OF_ID", tListOfId));
                ArrayList<MusicFile> tAL = tDataService.readArrayList(tSearchPattern);
                Logg.i(TAG, "got from " + tListOfId + " found " + tAL.size());
                tMusicFileCache.addToCache(tAL);
                tListOfId = "";
                jList = 0;
            }
//            if (1 == 2) {
//                int sId = 928;
//                Logg.w(TAG, "read single " + sId);
//                MusicFile sMusicFile = MusicFile.getById(sId);
//                if (sMusicFile == null) {
//                    Logg.w(TAG, "nothing");
//                } else {
//                    Logg.w(TAG, sMusicFile.toString()+" "+ sMusicFile.mId);
//                }
//
//            }
        }
//        Logg.i(TAG, "done");
    }


}
