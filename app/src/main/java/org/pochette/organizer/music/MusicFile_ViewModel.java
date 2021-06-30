package org.pochette.organizer.music;

import org.pochette.data_library.music.MusicFile;
import org.pochette.data_library.scddb_objects.Dance;
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
public class MusicFile_ViewModel extends My_ViewModel {

    @SuppressWarnings("FieldCanBeLocal")
    private final String TAG = "FEHA (MusicFile_ViewModel)";
    //Variables
    private String mSearchArtist;
    private String mSearchAlbum;
    private String mSearchName;
    private Dance mSearchDance;

    private CustomSpinnerItem mCsiPurpose = null;

    boolean mWithSearchValueStorage; // use for testing

    //Constructor
    public MusicFile_ViewModel(boolean iWithSearchValueStorage) {
        super(MusicFile.class);
        mWithSearchValueStorage = iWithSearchValueStorage;
        prepValues();
    }

    // needed for the factory
    public MusicFile_ViewModel() {
        super(MusicFile.class);
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

    public String getSearchAlbum() {
        return mSearchAlbum;
    }

    public void setSearchAlbum(String searchAlbum) {
        mSearchAlbum = searchAlbum;
    }

    public String getSearchName() {
        return mSearchName;
    }

    public void setSearchName(String searchName) {
        mSearchName = searchName;
    }

    public Dance getSearchDance() {
        return mSearchDance;
    }

    public void setSearchDance(Dance searchDance) {
        mSearchDance = searchDance;
    }

    public CustomSpinnerItem getCsiPurpose() {
        return mCsiPurpose;
    }

    public void setCsiPurpose(CustomSpinnerItem csiPurpose) {
        mCsiPurpose = csiPurpose;
    }
//</editor-fold>

    //Livecycle
    //Static Methods
    //Internal Organs
    //Interface


    public SearchPattern getSearchPattern() {
        storeValues();
        SearchPattern tSearchPattern = new SearchPattern(MusicFile.class);
        SearchCriteria tSearchCriteria;
        if (mSearchArtist != null && !mSearchArtist.isEmpty()) {
            tSearchCriteria = new SearchCriteria("ARTIST", mSearchArtist);
            tSearchPattern.addSearch_Criteria(tSearchCriteria);
        }
        if (mSearchAlbum != null && !mSearchAlbum.isEmpty()) {
            tSearchCriteria = new SearchCriteria("DIRECTORY", mSearchAlbum);
            tSearchPattern.addSearch_Criteria(tSearchCriteria);
        }
        if (mSearchName != null && !mSearchName.isEmpty()) {
            tSearchCriteria = new SearchCriteria("NAME", mSearchName);
            tSearchPattern.addSearch_Criteria(tSearchCriteria);
        }
        if (mSearchDance != null) {
            tSearchCriteria = new SearchCriteria("DANCE_ID", "" + mSearchDance.mId);
            tSearchPattern.addSearch_Criteria(tSearchCriteria);
        }

        if (mCsiPurpose != null) {
            if (!mCsiPurpose.mMethod.equals("SALL")) {
                tSearchPattern.addSearch_Criteria(
                        new SearchCriteria(mCsiPurpose.mMethod, mCsiPurpose.mValue));
            }
        }
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
        tKey = "AlbumName";
        MyPreferences.savePreferenceString(tBasicKey + tKey, mSearchAlbum);
        tKey = "Name";
        MyPreferences.savePreferenceString(tBasicKey + tKey, mSearchName);
        tKey = "Purpose";
        if (mCsiPurpose == null) {
            mCsiPurpose = new SpinnerItemFactory().getSpinnerItem(
                    SpinnerItemFactory.FIELD_MUSICFILE_PURPOSE, "SALL");
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
        tKey = "AlbumName";
        mSearchAlbum = MyPreferences.getPreferenceString(tBasicKey + tKey, "");
        tKey = "Name";
        mSearchName = MyPreferences.getPreferenceString(tBasicKey + tKey, "");

        try {
            tKey = "Purpose";
            String tCode = MyPreferences.getPreferenceString(tBasicKey + tKey, "SALL");
            mCsiPurpose = new SpinnerItemFactory().getSpinnerItem(
                    SpinnerItemFactory.FIELD_MUSICFILE_PURPOSE, tCode);
        } catch(Exception e) {
            Logg.w(TAG, e.toString());
        }
    }
}
