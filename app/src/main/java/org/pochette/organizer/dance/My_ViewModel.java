package org.pochette.organizer.dance;

import android.os.AsyncTask;
import android.os.Looper;

import org.pochette.data_library.database_management.DataService;
import org.pochette.data_library.scddb_objects.Dance;
import org.pochette.data_library.scddb_objects.SlimDance;
import org.pochette.organizer.app.DataServiceSingleton;
import org.pochette.utils_lib.logg.Logg;
import org.pochette.utils_lib.search.SearchPattern;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * ViewModel for Dance using MutableLiveDate
 * The values for the searchCriteria are stored her and the execution of the search is organized, to avoid double execution at any time
 */
@SuppressWarnings("rawtypes")
public abstract class My_ViewModel extends ViewModel {

    private final static int STATUS_NO_SEARCH = 1;
    private final static int STATUS_SEARCH_RUNNING = 2;
    private final static int STATUS_SEARCH_RUNNING_CRITERIA_OUTDATED = 3;

    private final String TAG = "FEHA (ViewModel)";
    private final Object mLock;
    private final Class mClass;
    //Variables
    public MutableLiveData<ArrayList<Object>> mMLD_AR; // the standard way
    public MutableLiveData<Integer[]> mMLD_A; // to be used for slimDance

    /**
     * 1 = No search is running
     * 2 = Search is running, since start no new request posted
     * 3 = Search is running, since start at least one new request posted
     */
    private Integer mStatus;
    private Timer mViewModelTimer;

    public My_ViewModel(Class iClass) {
        super();
        Logg.w(TAG, "Const");
        mClass = iClass;
        mLock = new Object();
        init();
    }

    private void init() {
        mMLD_AR = new MutableLiveData<>();
        mMLD_A = new MutableLiveData<>();
        storeAR(null);
        //mStatus = 1;
        mStatus = STATUS_NO_SEARCH;
        prepValues();
    }

//
    //</editor-fold>

    //Livecycle

    /**
     * This method will be called when this ViewModel is no longer used and will be destroyed.
     * <p>
     * It is useful when ViewModel observes some data and you need to clear this subscription to
     * prevent a leak of this ViewModel.
     */
    @Override
    protected void onCleared() {
        super.onCleared();
        mMLD_AR = null;
        mMLD_A = null;
    }
    //Static Methods

    //Internal Organs

    /**
     * is called when a new search is required.
     * search might be delayed, if a search is already ongoing
     * once the blocking search is finished, the search is started again with the most current criteria
     */
    public void newSearchData() {
        Logg.i(TAG, "new SearchData in ViewModel for class " + mClass.getSimpleName());
        synchronized (mLock) {
            switch (mStatus) {
                case STATUS_NO_SEARCH:
                    //     case 1:
                    int tDelay = 450;
                    if (mViewModelTimer != null) {
                        Logg.i(TAG, "cancel");
                        mViewModelTimer.cancel();
                    }
                    mViewModelTimer = new Timer("SD VM Search" + this.hashCode());
                    mViewModelTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            synchronized (mLock) {
                                mStatus = STATUS_SEARCH_RUNNING;
                                //mStatus = 2;
                            }
                            Logg.i(TAG, "Call search " + mClass.getSimpleName());
                            if (mClass.equals(Dance.class)) {
                                testSlimDance();
                            }
                            search(); // outside of sync as this is the time consuming task we are managing

                            Logg.i(TAG, "past search " + mClass.getSimpleName());
                            synchronized (mLock) {
                                if (mStatus == STATUS_SEARCH_RUNNING_CRITERIA_OUTDATED) {
                                    //     if (mStatus == 3) {
//                                    mStatus = 1;
                                    mStatus = STATUS_NO_SEARCH;
//                                    Logg.w(TAG, "request next with asynz");
                                    AsyncTask.execute(() -> { // Async needed to allow get Lock in recursive call
                                        newSearchData();
                                    });
                                } else {
//                                    mStatus = 1;
                                    mStatus = STATUS_NO_SEARCH;
                                }
                            }
                            Logg.i(TAG, "done with run");
                            mViewModelTimer = null; // after execution remove timer, so there is no object to call cancel on
                        }
                    }, tDelay);
                    break;
                case STATUS_SEARCH_RUNNING:
                    // when new search arrives, while a search is running, postpone
                    synchronized (mLock) {
                        mStatus = STATUS_SEARCH_RUNNING_CRITERIA_OUTDATED;
                    }
                    break;
                case STATUS_SEARCH_RUNNING_CRITERIA_OUTDATED:
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + mStatus);
            }
        }
    }

    void testSlimDance() {
        Logg.i(TAG, "start testSlimDance");


        SearchPattern tSearchPattern = getSearchPattern();
        if (tSearchPattern == null) {
            Logg.i(TAG, "getSearchPattern must return  not null");
            throw new RuntimeException("getSearchPattern must return  not null");
        }
        tSearchPattern.setSearchClass(SlimDance.class);

        ArrayList<Object> tAR;
        DataServiceSingleton tDataServiceSingleton = DataServiceSingleton.getInstance();
        DataService tDataService = tDataServiceSingleton.getDataService();
        tAR = tDataService.readArrayList(tSearchPattern);
        if (tAR == null) {
            Logg.i(TAG, tSearchPattern.toString());
            Logg.w(TAG, "Problem in readArrayList for class " + tSearchPattern.getSearchClass().getSimpleName());
            return;
        }
        Logg.i(TAG, "size of tAR" + tAR.size() + " " + Thread.currentThread().toString());
        //  storeAR(tAR);


        Logg.i(TAG, "finshed testSlimDance");
    }


    /**
     * prep the SearchPattern and call the generic search, i.e. the DB
     */
    public void search() {
//        if (mClass == SlimDance.class) {
//
//
//        }


        Logg.i(TAG, "Search for " + mClass.getSimpleName() + " " + Thread.currentThread().toString());
        storeValues();
        SearchPattern tSearchPattern = getSearchPattern();
        if (tSearchPattern == null) {
            Logg.i(TAG, "getSearchPattern must return  not null");
            throw new RuntimeException("getSearchPattern must return  not null");
        }
        ArrayList<Object> tAR;
        DataServiceSingleton tDataServiceSingleton = DataServiceSingleton.getInstance();
        DataService tDataService = tDataServiceSingleton.getDataService();
        tAR = tDataService.readArrayList(tSearchPattern);
        if (tAR == null) {
            Logg.i(TAG, tSearchPattern.toString());
            Logg.w(TAG, "Problem in readArrayList for class " + tSearchPattern.getSearchClass().getSimpleName());
            return;
        }
        Logg.i(TAG, "size of tAR" + tAR.size() + " " + Thread.currentThread().toString());
        storeAR(tAR);
    }

    /**
     * Depending on which tread we are on, use different calls to send the new data to the observers
     *
     * @param iAR the AR to be stored
     */
    public void storeAR(ArrayList<Object> iAR) {
        if (mClass != SlimDance.class) { // SlimDance uses Array not ArrayList
            ArrayList<Object> jAR = localSort(iAR);
            if (Looper.getMainLooper() == Looper.myLooper()) {
                mMLD_AR.setValue(jAR);
            } else {
                mMLD_AR.postValue(jAR);
            }
        }
    }

    // is used !!!
    @SuppressWarnings("unused")
    public void storeA(Integer[] iA) {
        if (mClass == SlimDance.class) {
            if (Looper.getMainLooper() == Looper.myLooper()) {
                mMLD_A.setValue(iA);
            } else {
                mMLD_A.postValue(iA);
            }
        }
    }


    public ArrayList<Object> localSort(ArrayList<Object> iAR) {
        return iAR;
    }

    abstract public void storeValues();

    abstract public void prepValues();

    abstract public SearchPattern getSearchPattern();

    //Interface

    /**
     * This method forces a database search, even when no data has changed
     */
    public void forceSearch() {
        newSearchData();
    }

}
