package org.pochette.organizer.dance;

import org.pochette.data_library.database_management.DataService;
import org.pochette.data_library.scddb_objects.Dance;
import org.pochette.organizer.app.DataServiceSingleton;
import org.pochette.utils_lib.logg.Logg;
import org.pochette.utils_lib.search.SearchCriteria;
import org.pochette.utils_lib.search.SearchPattern;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

/**
 * Cache to store dance data, so on scorlling etc the database call can be avoided
 * it is assumed you know already the id of the dance
 */
@SuppressWarnings("unused")
public class Dance_Cache {

    private static final String TAG = "FEHA (Dance_Cache)";
    private final static int mMaxNumber = 120;
    private final static int mPackageSize = 10;

    private static Dance_Cache mInstance;

    // variables

    private final Object mLock;
    private final HashMap<Integer, Dance> mCache;
    private final ArrayList<Integer> mUsage;

    // constructor
    public static Dance_Cache getInstance() {
        //Double check locking pattern
        if (mInstance == null) { //Check for the first time
            synchronized (DataServiceSingleton.class) {   //Check for the second time.
                if (mInstance == null) {
                    mInstance = new Dance_Cache();
                }
            }
        }
        return mInstance;
    }

    private Dance_Cache() {
        mCache = new HashMap<>(0);
        mUsage = new ArrayList<>(0);
        mLock = new Object();
    }
    // setter and getter
    // lifecylce and override

    // internal

    void logUsage() {
        Logg.i(TAG, "size " + mUsage.size());
        for (Integer lId : mUsage) {
            Logg.i(TAG, "" + lId);
        }
    }

    Dance getFromCache(Integer iId) {
        //   Logg.i(TAG, "getFromCacge " + iId);
        synchronized (mLock) {
            if (mCache.containsKey(iId)) {
                return mCache.get(iId);
            }
        }
        logUsage();
        return null;
    }

    void addToCache(Integer iId, Dance iDance) {
        synchronized (mLock) {
            mCache.put(iId, iDance);
            Integer tToBeDeletedId = remeberIdWasUsed(iId);
            if (tToBeDeletedId > 0) {
                //       Logg.i(TAG, "to be deleted " + tToBeDeletedId);
                mCache.remove(tToBeDeletedId);
            }
        }
    }


    void addToCache(ArrayList<Dance> iAL) {

        // Logg.i(TAG, "start add bz AL " + mUsage.size());
        synchronized (mLock) {
            for (Dance lDance : iAL) {
                mCache.put(lDance.mId, lDance);
                Integer tToBeDeletedId = remeberIdWasUsed(lDance.mId);
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
        Dance_Cache tDanceCache = Dance_Cache.getInstance();
        tDanceCache.mUsage.remove(iId);
        tDanceCache.mCache.remove(iId);
    }


    /**
     * If the correct data is known and should be cached
     *
     * @param iDance to be refreshed in the cache
     */
    public static void updateCache(Dance iDance) {
        Dance_Cache tDanceCache = Dance_Cache.getInstance();
        tDanceCache.mCache.put(iDance.mId, iDance);
        tDanceCache.remeberIdWasUsed(iDance.mId);
    }

    /**
     * retrieve from cache, if not yet cached, get from database
     *
     * @param iId to be returned
     * @return the dance
     */
    public static Dance getById(int iId) {

        Dance_Cache tDanceCache = Dance_Cache.getInstance();
        Dance tDance;
        tDance = tDanceCache.getFromCache(iId);
        if (tDance == null) {
            try {
                tDance = new Dance(iId);
                tDanceCache.addToCache(iId, tDance);
            } catch(Exception e) {
                Logg.w(TAG, e.toString());
            }
        } else {
            tDanceCache.remeberIdWasUsed(iId);
        }
        return tDance;
    }

    /**
     * This method prereads the dances of Array iA
     *
     * @param iA Array of Ids to be preread
     */
    public static void preread(Integer[] iA) {
        if (iA == null || iA.length == 0) {
            return;
        }
        Logg.w(TAG, "preread " + iA.length);
        DataService tDataService = DataServiceSingleton.getInstance().getDataService();
        Dance_Cache tDanceCache = Dance_Cache.getInstance();
        String tListOfId = "";
        int jArray = 0;
        int jList = 0;
        while (jArray < iA.length && jArray < mMaxNumber) {
            Integer tId = iA[jArray];
            //         Logg.i(TAG, " " + jArray + " " + jList+ "->"+ tId);
            jArray++;
            if (tDanceCache.mUsage.contains(tId)) {
                // if it is already in cache exclude from retrieval
                // worst would be if removed from cache prematurely
                Logg.w(TAG, "no preread for " + tId);
                continue;
            }
            if (jList == 0) {
                tListOfId = String.format(Locale.ENGLISH, "%d", tId);
            } else {
                tListOfId = String.format(Locale.ENGLISH, "%s,%d", tListOfId, tId);
            }
            jList++;
            if (jList == mPackageSize || jArray == iA.length) {
                SearchPattern tSearchPattern = new SearchPattern(Dance.class);
                tSearchPattern.addSearch_Criteria(new SearchCriteria("LIST_OF_ID", tListOfId));
                ArrayList<Dance> tAL = tDataService.readArrayList(tSearchPattern);
                tDanceCache.addToCache(tAL);
                tListOfId = "";
                jList = 0;
            }
        }
        Logg.i(TAG, "done");


    }


}
