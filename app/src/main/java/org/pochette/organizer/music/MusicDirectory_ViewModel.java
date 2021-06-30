package org.pochette.organizer.music;

import org.pochette.data_library.music.MusicDirectory;
import org.pochette.organizer.app.MyPreferences;
import org.pochette.organizer.dance.My_ViewModel;
import org.pochette.organizer.gui_assist.CustomSpinnerItem;
import org.pochette.organizer.gui_assist.SpinnerItemFactory;
import org.pochette.utils_lib.logg.Logg;
import org.pochette.utils_lib.search.SearchCriteria;
import org.pochette.utils_lib.search.SearchPattern;

/**
 * ViewModel for Dance using MutableLiveDate
 * The values for the searchCriteria are stored her and the execution of the search is organized, to avoid double execution at any time
 */
@SuppressWarnings("unused")
public class MusicDirectory_ViewModel extends My_ViewModel {

    private final String TAG = "FEHA (MusicDirectory_ViewModel)";
    //Variables
    private String mSearchArtist;
    private String mSearchName;
    private CustomSpinnerItem mCsiPurpose = null;

    boolean mWithSearchValueStorage; // use for testing
    //Constructor
//    public MusicDirectory_ViewModel(boolean iWithSearchValueStorage) {
//        super(MusicDirectory.class);
//        mWithSearchValueStorage = iWithSearchValueStorage;
//        prepValues();
//    }
// needed for the factory
    public MusicDirectory_ViewModel() {
        super(MusicDirectory.class);
        mWithSearchValueStorage = true;
        prepValues();
    }


    //<editor-fold desc="setSearchValues">
    public String getSearchArtist() {
        return mSearchArtist;
    }
    public void setSearchArtist(String searchArtist) {
        mSearchArtist = searchArtist;
    }
    public String getSearchName() {
        return mSearchName;
    }
    public void setSearchName(String searchName) {
        mSearchName = searchName;
    }
    public void setCsiPurposee(CustomSpinnerItem iCustomSpinnerItem) {
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


    //public boolean isWithSearchValueStorage() {
//        return mWithSearchValueStorage;
//    }
    public void setWithSearchValueStorage(boolean withSearchValueStorage) {
        mWithSearchValueStorage = withSearchValueStorage;
        if (!mWithSearchValueStorage) {
            deleteValues();
        }
    }
    //</editor-fold>

    //Livecycle
    //Static Methods
    //Internal Organs
    //Interface


    public SearchPattern getSearchPattern() {
        storeValues();
        SearchPattern tSearchPattern = new SearchPattern(MusicDirectory.class);
        SearchCriteria tSearchCriteria;
        if (mSearchArtist != null && !mSearchArtist.isEmpty()) {
            tSearchCriteria = new SearchCriteria("T2", mSearchArtist);
            tSearchPattern.addSearch_Criteria(tSearchCriteria);
        }
        if (mSearchName != null && !mSearchName.isEmpty()) {
            tSearchCriteria = new SearchCriteria("T1", mSearchName);
            tSearchPattern.addSearch_Criteria(tSearchCriteria);
        }
        if (mCsiPurpose != null) {
            if (!mCsiPurpose.mMethod.equals("SALL")) {
                tSearchPattern.addSearch_Criteria(
                        new SearchCriteria(mCsiPurpose.mMethod, mCsiPurpose.mValue));
            }
        }
        Logg.i(TAG, tSearchPattern.toString());
        return tSearchPattern;
    }

    public void storeValues() {
        if (!mWithSearchValueStorage) {
            return;
        }
        String tBasicKey = this.getClass().getSimpleName() + "/";
        String tKey;
        tKey = "ArtistName";
        MyPreferences.savePreferenceString(tBasicKey + tKey, mSearchArtist);
        tKey = "Name";
        MyPreferences.savePreferenceString(tBasicKey + tKey, mSearchName);
        tKey = "Purpose";
        if (mCsiPurpose == null) {
            SpinnerItemFactory tSpinnerItemFactory = new SpinnerItemFactory();
            mCsiPurpose = tSpinnerItemFactory.getSpinnerItem(SpinnerItemFactory.FIELD_MUSICDIRECTORY_PURPOSE, "SALL");
        }
        MyPreferences.savePreferenceString(tBasicKey + tKey, mCsiPurpose.mKey);
        tKey = "Purpose";
        if (mCsiPurpose == null) {
            SpinnerItemFactory tSpinnerItemFactory = new SpinnerItemFactory();
            mCsiPurpose = tSpinnerItemFactory.getSpinnerItem(SpinnerItemFactory.FIELD_MUSICDIRECTORY_PURPOSE, "SALL");
        }
        MyPreferences.savePreferenceString(tBasicKey + tKey, mCsiPurpose.mKey);
    }

    public void prepValues() {
        if (!mWithSearchValueStorage) {
            return;
        }
        String tBasicKey = this.getClass().getSimpleName() + "/";
        String tKey;
        tKey = "ArtistName";
        mSearchArtist = MyPreferences.getPreferenceString(tBasicKey + tKey, "");
        tKey = "Name";
        mSearchName = MyPreferences.getPreferenceString(tBasicKey + tKey, "");
        try {
            SpinnerItemFactory tSpinnerItemFactory = new SpinnerItemFactory();
            tKey = "Purpose";
            String tCode = MyPreferences.getPreferenceString(tBasicKey + tKey, "SALL");
            mCsiPurpose = tSpinnerItemFactory.getSpinnerItem(
                    SpinnerItemFactory.FIELD_MUSICDIRECTORY_PURPOSE, tCode);
        } catch(Exception e) {
            Logg.i(TAG, e.toString());
        }
    }

    public void deleteValues() {
        mSearchArtist =  "";
        mSearchName =  "";
        try {
            SpinnerItemFactory tSpinnerItemFactory = new SpinnerItemFactory();
            String tCode = "SALL";
            mCsiPurpose = tSpinnerItemFactory.getSpinnerItem(
                    SpinnerItemFactory.FIELD_MUSICDIRECTORY_PURPOSE, tCode);
        } catch(Exception e) {
            Logg.w(TAG, e.toString());
        }
    }



}
