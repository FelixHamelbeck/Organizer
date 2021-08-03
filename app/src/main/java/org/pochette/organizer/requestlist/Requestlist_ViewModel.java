package org.pochette.organizer.requestlist;

import org.pochette.data_library.requestlist.Requestlist;
import org.pochette.organizer.app.MyPreferences;
import org.pochette.organizer.gui_assist.My_ViewModel;
import org.pochette.organizer.gui_assist.CustomSpinnerItem;
import org.pochette.organizer.gui_assist.SpinnerItemFactory;
import org.pochette.utils_lib.search.SearchCriteria;
import org.pochette.utils_lib.search.SearchPattern;

/**
 * ViewModel for Dance using MutableLiveDate
 * The values for the searchCriteria are stored her and the execution of the search is organized, to avoid double execution at any time
 */
@SuppressWarnings("unused")
public class Requestlist_ViewModel extends My_ViewModel {

    @SuppressWarnings("unused")
    private final String TAG = "FEHA (Requestlist_VM)";
    //Variables
    private String mSearchName;
    private CustomSpinnerItem mSearchPurpose;
    //Constructor
    public Requestlist_ViewModel() {
        super(Requestlist.class);
        prepValues();
    }

    //<editor-fold desc="setSearchValues">
    public String getSearchName() {
        return mSearchName;
    }
    public void setSearchName(String searchName) {
        mSearchName = searchName;
        forceSearch();
    }
    public CustomSpinnerItem getSearchPurpose() {
        return mSearchPurpose;
    }
    public void setSearchPurpose(CustomSpinnerItem iSearchPurpose) {
        mSearchPurpose = iSearchPurpose;
        forceSearch();
    }
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
    }
    //Static Methods
    //Internal Organs

    //Interface
    /**
     * prep the SearchPattern and call the generic search, i.e. the DB
     */
    public SearchPattern getSearchPattern() {
        SearchPattern tSearchPattern = new SearchPattern(Requestlist.class);
        SearchCriteria tSearchCriteria;
        if (mSearchName != null && !mSearchName.isEmpty()) {
            tSearchCriteria = new SearchCriteria("NAME", mSearchName);
            tSearchPattern.addSearch_Criteria(tSearchCriteria);
        }
        if (mSearchPurpose != null && ! mSearchPurpose.mMethod.equals("SALL")) {
            tSearchCriteria = new SearchCriteria(mSearchPurpose.mMethod,
                    mSearchPurpose.mValue);
            tSearchPattern.addSearch_Criteria(tSearchCriteria);
        }
        return tSearchPattern;
    }

    public void storeValues() {
        String tBasicKey = this.getClass().getSimpleName() + "/";
        String tKey;
        tKey = "Name";
        MyPreferences.savePreferenceString(tBasicKey + tKey, mSearchName);
        tKey = "Purpose";
        MyPreferences.savePreferenceString(tBasicKey + tKey, mSearchPurpose.mKey);
    }

    public void prepValues() {
        String tBasicKey = this.getClass().getSimpleName() + "/";
        String tKey;
        String tCode;
        SpinnerItemFactory tSpinnerItemFactory = new SpinnerItemFactory();
        tKey = "Name";
        mSearchName = MyPreferences.getPreferenceString(tBasicKey + tKey, "");
        tKey = "Purpose";
        tCode = MyPreferences.getPreferenceString(tBasicKey + tKey, "SALL");
        mSearchPurpose = tSpinnerItemFactory.getSpinnerItem(SpinnerItemFactory.FIELD_REQUESTLIST_PURPOSE, tCode);
    }
}
