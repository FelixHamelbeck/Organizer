package org.pochette.organizer.pairing;


import org.pochette.data_library.pairing.MusicDirectory_Pairing;
import org.pochette.organizer.app.MyPreferences;
import org.pochette.organizer.dance.My_ViewModel;
import org.pochette.organizer.gui_assist.CustomSpinnerItem;
import org.pochette.organizer.gui_assist.SpinnerItemFactory;
import org.pochette.utils_lib.logg.Logg;
import org.pochette.utils_lib.search.SearchCriteria;
import org.pochette.utils_lib.search.SearchPattern;

import java.util.ArrayList;

import javax.security.auth.login.LoginException;

import static java.lang.Thread.sleep;

/**
 * ViewModel for Dance using MutableLiveDate
 * The values for the searchCriteria are stored her and the execution of the search is organized, to avoid double execution at any time
 */
@SuppressWarnings("unused")
public class MusicDirectory_Pairing_ViewModel extends My_ViewModel {

    private final String TAG = "FEHA (MDP_VM)";

    //Variables
    private String mSearchArtist;
    private String mSearchAlbum;

    private CustomSpinnerItem mCsiPurpose= null;
    private CustomSpinnerItem mCsiPairingStatus= null;


    //  private PairingStatus mPairingStatus;

    boolean mWithSearchValueStorage; // use for testing

    //Constructor
//    public MusicDirectory_Pairing_ViewModel(ArrayList<MusicDirectory_Pairing> iAR_MusicDirectory_Pairing) {
//        super(MusicDirectory_Pairing.class);
//    }

//    public MusicDirectory_Pairing_ViewModel(boolean iWithSearchValueStorage) {
//        super(MusicDirectory_Pairing.class);
//        mWithSearchValueStorage = iWithSearchValueStorage;
//    }

    public MusicDirectory_Pairing_ViewModel() {
        super(MusicDirectory_Pairing.class);
        mWithSearchValueStorage = true;
        prepValues();
    }


    //<editor-fold desc="setSearchValues">

    public String getSearchArtist() {
        return mSearchArtist;
    }

    public void setSearchArtist(String searchArtist) {
        mSearchArtist = searchArtist;
        newSearchData();
    }

    public String getSearchAlbum() {
        return mSearchAlbum;
    }

    public void setSearchAlbum(String searchAlbum) {
        mSearchAlbum = searchAlbum;
        newSearchData();
    }


    public CustomSpinnerItem getCsiPurpose() {
        return mCsiPurpose;
    }

    public void setCsiPurpose(CustomSpinnerItem iCustomSpinnerItem) {
        if (iCustomSpinnerItem == null) {
            return;
        }
        if (mCsiPurpose == null) {
            mCsiPurpose = iCustomSpinnerItem;
            newSearchData();
            return;
        }
        if (iCustomSpinnerItem.mKey.equals(mCsiPurpose.mKey)) {
            return;
        }
        mCsiPurpose = iCustomSpinnerItem;
        newSearchData();
    }

    public CustomSpinnerItem getCsiPairingStatus() {
        return mCsiPairingStatus;
    }

    public void setCsiPairingStatus(CustomSpinnerItem iCustomSpinnerItem) {
        if (iCustomSpinnerItem == null) {
            return;
        }
        if (mCsiPairingStatus == null) {
            mCsiPairingStatus = iCustomSpinnerItem;
            newSearchData();
            return;
        }
        if (iCustomSpinnerItem.mKey.equals(mCsiPairingStatus.mKey)) {
            return;
        }
        mCsiPairingStatus = iCustomSpinnerItem;
        newSearchData();
    }


    //</editor-fold>

    //Livecycle


    //Static Methods

    //Internal Organs


    /**
     * prep the SearchPattern and call the generic search, i.e. the DB
     */
    @Override
    public void search() {
//
//        try {
//            Logg.w(TAG, "start sleep");
//            sleep(5000);
//            Logg.w(TAG, "finish sleep");
//        } catch(InterruptedException e) {
//            Logg.w(TAG, e.toString());
//        }


        //  Logg.w(TAG, "Search for " + mClass.getSimpleName()+" "+Thread.currentThread().toString());
        //Logg.i(TAG, "different Search in Thread" + Thread.currentThread().hashCode());
        storeValues();
        SearchPattern tSearchPattern = getSearchPattern();
        ArrayList<MusicDirectory_Pairing> tAR =MusicDirectory_Pairing.get(tSearchPattern);
        ArrayList<Object> sAR = new ArrayList<>(0);
        //   if (lMusicDirectoryPairing.getMusicDirectory().mT1.contains("eel")) {
        //   }
        sAR.addAll(tAR);


        //ArrayList<Object> tAR = new ArrayList<>(0);


//        SearchPattern tSearchPattern = getSearchPattern();
//        if (tSearchPattern == null) {
//            Logg.i(TAG, "getSearchPattern must return  not null");
//            throw new RuntimeException("getSearchPattern must return  not null");
//        }
//        ArrayList<Object> tAR;
//        DataServiceSingleton tDataServiceSingleton = DataServiceSingleton.getInstance();
//        DataService tDataService = tDataServiceSingleton.getDataService();
//        tAR = tDataService.readArrayList(tSearchPattern);
//        if (tAR == null) {
//            Logg.w(TAG, "Problem in readArrayListe");
//            Logg.i(TAG, tSearchPattern.toString());
//            return;
//        }
        Logg.i(TAG, "size of tAR" + sAR.size() + " " + Thread.currentThread().toString());
        storeAR(sAR);
    }


    public SearchPattern getSearchPattern() {
        Logg.d(TAG, "getSearchPattern: "+ mWithSearchValueStorage);
        storeValues();
        SearchPattern tSearchPattern = new SearchPattern(MusicDirectory_Pairing.class);
        SearchCriteria tSearchCriteria ;
        if (mSearchAlbum != null && !mSearchAlbum.isEmpty()) {
            tSearchCriteria = new SearchCriteria("T1", mSearchAlbum);
            tSearchPattern.addSearch_Criteria(tSearchCriteria);
        }
        if (mSearchArtist != null && !mSearchArtist.isEmpty()) {
            tSearchCriteria = new SearchCriteria("T2", mSearchArtist);
            tSearchPattern.addSearch_Criteria(tSearchCriteria);
        }
        if (mCsiPurpose != null) {
            Logg.d(TAG, mCsiPurpose.mMethod);
            Logg.d(TAG, mCsiPurpose.mValue);
            if (!mCsiPurpose.mMethod.equals("SALL")) {
                tSearchPattern.addSearch_Criteria(
                        new SearchCriteria(mCsiPurpose.mMethod, mCsiPurpose.mValue));
            }
        }
        if (mCsiPairingStatus != null) {
            if (!mCsiPairingStatus.mMethod.equals("SALL")) {
                tSearchPattern.addSearch_Criteria(
                        new SearchCriteria(mCsiPairingStatus.mMethod, mCsiPairingStatus.mValue));
            }
        }

//        if (mPairingStatus != null) {
//            SearchCriteria tSearchCriteria =
//                    new SearchCriteria("Pairing_STATUS", mPairingStatus.getCode());
//            tSearchPattern.addSearch_Criteria(tSearchCriteria);
//        }
        return tSearchPattern;
    }


    public void storeValues() {
        if (!mWithSearchValueStorage) {
            return;
        }
        String tBasicKey = this.getClass().getSimpleName() + "/";
        String tKey;
        String tCode;
        tKey = "Artist";
        MyPreferences.savePreferenceString(tBasicKey + tKey,mSearchArtist);
        tKey = "Album";
        MyPreferences.savePreferenceString(tBasicKey + tKey,mSearchAlbum);
        SpinnerItemFactory tSpinnerItemFactory = new SpinnerItemFactory();
        tKey = "Purpose";
        if (mCsiPurpose == null) {
            tCode = "SALL";
        } else {
            tCode = mCsiPurpose.mKey;
        }
        Logg.d(TAG,"save "+ tBasicKey + tKey);
        Logg.d(TAG,"save "+ tCode);
        MyPreferences.savePreferenceString(tBasicKey + tKey, tCode);

     //   logValues();
    }

    public void logValues() {
        String tBasicKey = this.getClass().getSimpleName() + "/";
        String tKey;
        String tCode;
        tKey = "Artist";
        tCode= MyPreferences.getPreferenceString(tBasicKey + tKey,"");
        Logg.i(TAG, mSearchArtist + "<>" + tCode);
        tKey = "Album";
        tCode = MyPreferences.getPreferenceString(tBasicKey + tKey,"");
        Logg.i(TAG, mSearchAlbum + "<>" + tCode);
    }


    public void prepValues() {

        if (!mWithSearchValueStorage) {
            return;
        }
        String tBasicKey = this.getClass().getSimpleName() + "/";
        String tKey;
        String tCode;

        tKey = "Artist";
        mSearchArtist = MyPreferences.getPreferenceString(tBasicKey + tKey,"");
        tKey = "Album";
        mSearchAlbum = MyPreferences.getPreferenceString(tBasicKey + tKey,"");
        SpinnerItemFactory tSpinnerItemFactory = new SpinnerItemFactory();
        try {
            tKey = "Purpose";

            Logg.d(TAG,"get "+ tBasicKey + tKey);
            tCode = MyPreferences.getPreferenceString(tBasicKey + tKey, "SxALL");

            Logg.d(TAG,"245 "+ tCode);
            mCsiPurpose = tSpinnerItemFactory.getSpinnerItem(
                    SpinnerItemFactory.FIELD_MUSICDIRECTORY_PURPOSE, tCode);
        } catch(Exception e) {
            Logg.w(TAG, e.toString());
        }

     //   logValues();
    }

    //Interface


}
