package org.pochette.data_library.requestlist;

import android.database.Cursor;

import org.pochette.data_library.BuildConfig;
import org.pochette.data_library.database_management.SearchCall;
import org.pochette.utils_lib.logg.Logg;
import org.pochette.utils_lib.search.SearchPattern;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Cache to store Requestlist data, so on scorlling etc the database call can be avoided
 * it is assumed you know already the id of the Requestlist
 */
@SuppressWarnings("unused")
public class Requestlist_Cache {

    private static final String TAG = "FEHA (Requestlist_Cache)";
    private final static int mMaxNumber = 120;
    private final static int mPackageSize = 10;

    private static Requestlist_Cache mInstance;

    // variables

    private final Object mLock;
    private final HashMap<Integer, Requestlist> mCache;
    private final ArrayList<Integer> mUsage;

    // constructor
    public static Requestlist_Cache getInstance() {
        //Double check locking pattern
        if (mInstance == null) { //Check for the first time
            synchronized (Requestlist.class) {   //Check for the second time.
                if (mInstance == null) {
                    mInstance = new Requestlist_Cache();
                }
            }
        }
        return mInstance;
    }

    private Requestlist_Cache() {
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

    Requestlist getFromCache(Integer iId) {
        //   Logg.i(TAG, "getFromCacge " + iId);
        synchronized (mLock) {
            if (mCache.containsKey(iId)) {
                return mCache.get(iId);
            }
        }
        logUsage();
        return null;
    }

    void addToCache(Integer iId, Requestlist iRequestlist) {
        synchronized (mLock) {
            mCache.put(iId, iRequestlist);
            Integer tToBeDeletedId = remeberIdWasUsed(iId);
            if (tToBeDeletedId > 0) {
                //       Logg.i(TAG, "to be deleted " + tToBeDeletedId);
                mCache.remove(tToBeDeletedId);
            }
        }
    }


    void addToCache(ArrayList<Requestlist> iAL) {
        synchronized (mLock) {
            for (Requestlist lRequestlist : iAL) {
                mCache.put(lRequestlist.getId(), lRequestlist);
                Integer tToBeDeletedId = remeberIdWasUsed(lRequestlist.getId());
                if (tToBeDeletedId > 0) {
                    mCache.remove(tToBeDeletedId);
                }
            }
        }
    }


    Integer remeberIdWasUsed(Integer iId) {
        // do not use synchronized insided, as this methid is only called from within sychnronized

        // remove if the id is already in the list further down

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
        Requestlist_Cache tRequestlistCache = Requestlist_Cache.getInstance();
        Integer tIntegerId = (Integer) iId;

        if (mInstance.mUsage.contains(tIntegerId)) {
            tRequestlistCache.mUsage.remove(tIntegerId);
        }
        if (mInstance.mCache.containsKey(tIntegerId)) {
            tRequestlistCache.mCache.remove(tIntegerId);
        }
    }


    /**
     * If the correct data is known and should be cached
     *
     * @param iRequestlist to be refreshed in the cache
     */
    public static void updateCache(Requestlist iRequestlist) {
        Requestlist_Cache tRequestlistCache = Requestlist_Cache.getInstance();
        tRequestlistCache.mCache.put(iRequestlist.getId(), iRequestlist);
        tRequestlistCache.remeberIdWasUsed(iRequestlist.getId());
    }

    /**
     * retrieve from cache, if not yet cached, get from database
     *
     * @param iId to be returned
     * @return the Requestlist
     */
    public static Requestlist getById(int iId) {
        Requestlist_Cache tRequestlistCache = Requestlist_Cache.getInstance();
        Requestlist tRequestlist;
        tRequestlist = tRequestlistCache.getFromCache(iId);
        if (tRequestlist == null) {
            try {
                tRequestlist = Requestlist.getById(iId);
                tRequestlistCache.addToCache(iId, tRequestlist);
            } catch(Exception e) {
                Logg.w(TAG, e.toString());
            }
        } else {
            tRequestlistCache.remeberIdWasUsed(iId);
        }
        return tRequestlist;
    }

    public static ArrayList<Requestlist> getByPattern(SearchPattern iSearchPattern) {
        ArrayList<Requestlist> tAL = getAL(iSearchPattern);
        if (tAL != null && tAL.size() > 0) {
            for (Requestlist lRequestlist : tAL) {
                getInstance().addToCache(lRequestlist.getId(), lRequestlist);
            }
        }
        return tAL;

    }



//    /**
//     * This method prereads the Requestlists of Array iA
//     *
//     * @param iA Array of Ids to be preread
//     */
//    public static void preread(Integer[] iA) {
//        if (iA == null || iA.length == 0) {
//            return;
//        }
//        Logg.w(TAG, "preread " + iA.length);
//       // DataService tDataService = DataServiceSingleton.getInstance().getDataService();
//        Requestlist_Cache tRequestlistCache = Requestlist_Cache.getInstance();
//        String tListOfId = "";
//        int jArray = 0;
//        int jList = 0;
//        while (jArray < iA.length && jArray < mMaxNumber) {
//            Integer tId = iA[jArray];
//            //         Logg.i(TAG, " " + jArray + " " + jList+ "->"+ tId);
//            jArray++;
//            if (tRequestlistCache.mUsage.contains(tId)) {
//                // if it is already in cache exclude from retrieval
//                // worst would be if removed from cache prematurely
//                Logg.w(TAG, "no preread for " + tId);
//                continue;
//            }
//            if (jList == 0) {
//                tListOfId = String.format(Locale.ENGLISH, "%d", tId);
//            } else {
//                tListOfId = String.format(Locale.ENGLISH, "%s,%d", tListOfId, tId);
//            }
//            jList++;
//            if (jList == mPackageSize || jArray == iA.length) {
//                SearchPattern tSearchPattern = new SearchPattern(Requestlist.class);
//                tSearchPattern.addSearch_Criteria(new SearchCriteria("LIST_OF_ID", tListOfId));
//                //ArrayList<Requestlist> tAL = tDataService.readArrayList(tSearchPattern);
//                ArrayList<Requestlist> tAL = getAL(tSearchPattern);
//                tRequestlistCache.addToCache(tAL);
//                tListOfId = "";
//                jList = 0;
//            }
//        }
//        Logg.i(TAG, "done");
//    }

     static ArrayList<Requestlist> getAL(SearchPattern iSearchPattern){
        SearchCall tSearchCall = new SearchCall(iSearchPattern.getSearchClass(), iSearchPattern, null);
        Cursor tCursor = null;
        ArrayList<Requestlist> tAL;
        try {
            if (BuildConfig.DEBUG) {
                Logg.d(TAG, "readArrayList: Call createCursor");
            }
            tCursor = tSearchCall.createCursor();
            if (tCursor == null) {
                return null;
            }
            if (BuildConfig.DEBUG) {
                Logg.d(TAG, "readArrayList: cursor size = " + tCursor.getCount());
            }
            Logg.i(TAG, "Start readCursor");
            tAL = new ArrayList<>(0);
            while (tCursor.moveToNext()) {
                tAL.add(tSearchCall.getDataObject(tCursor));
            }
            return tAL;
        } catch(RuntimeException e) {
            Logg.w(TAG, e.toString());
            return null;
        } finally {
            if (tCursor != null) {
                tCursor.close();
            }
        }
    }


}
