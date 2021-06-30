package org.pochette.organizer.dance;

import org.pochette.data_library.scddb_objects.Dance;
import org.pochette.organizer.app.MyPreferences;
import org.pochette.organizer.gui_assist.CustomSpinnerItem;
import org.pochette.organizer.gui_assist.SpinnerItemFactory;
import org.pochette.utils_lib.logg.Logg;
import org.pochette.utils_lib.search.SearchCriteria;
import org.pochette.utils_lib.search.SearchPattern;

import java.util.HashSet;
import java.util.Locale;

/**
 * ViewModel for Dance using MutableLiveDate
 * The values for the searchCriteria are stored her and the execution of the search is organized, to avoid double execution at any time
 */
@SuppressWarnings("unused")
public class Dance_ViewModel extends My_ViewModel {

    private final String TAG = "FEHA (Dance_ViewModel)";

    //Variables
    private String mDancename="";
    private String mListOfId = "";
    private CustomSpinnerItem mCsiFavourite = null;
    private CustomSpinnerItem mCsiRhythmType = null;
    private CustomSpinnerItem mCsiShapeType = null;
    private boolean mFlagMusic = false;
    private boolean mFlagDiagram = false;
    private boolean mFlagCrib = false;
    private boolean mFlagRscds = false;
    boolean mWithSearchValueStorage;
    //    /**
//     * 1 = No search is running
//     * 2 = Search is running, since start no new request posted
//     * 3 = Search is running, since start at least one new request posted
//     */
//    private Integer mStatus;
//    private Timer mViewModelTimer;

    //Constructor


    public Dance_ViewModel() {
        super(Dance.class);
        mWithSearchValueStorage = true;
        prepValues();
    }

    public Dance_ViewModel(boolean iWithSearchValueStorage) {
        super(Dance.class);
        mWithSearchValueStorage = iWithSearchValueStorage;
        prepValues();
    }

    public String getDancename() {
        return mDancename;
    }

    //<editor-fold desc="setSearchValues">
    public void setDancename(String iDancename) {
        if (iDancename == null ||
                mDancename.equals(iDancename)) {
            return;
        }
        mDancename = iDancename;
        newSearchData();
    }

    public void setListOfId(HashSet<Integer> iHS_Id) {
        if (iHS_Id == null) {
            mListOfId = "";
        } else if (iHS_Id.size() == 0) {
            mListOfId = " -1 "; // find no database id with that id
        } else {
            mListOfId = " -1 "; // -1 doing no harm
            for (Integer lId : iHS_Id) {
                mListOfId = String.format(Locale.ENGLISH, "%s , %d", mListOfId, lId);
            }
        }
        newSearchData();
    }

    public boolean getFlagMusic() {
        return mFlagMusic;
    }

    public void setFlagMusic(boolean iFlagMusic) {
        if (iFlagMusic != mFlagMusic) {
            mFlagMusic = iFlagMusic;
            newSearchData();
        }
    }

    public boolean getFlagDiagram() {
        return mFlagDiagram;
    }

    public void setFlagDiagram(boolean iFlagDiagram) {
        if (iFlagDiagram != mFlagDiagram) {
            mFlagDiagram = iFlagDiagram;
            newSearchData();
        }
    }

    public boolean getFlagCrib() {
        return mFlagCrib;
    }

    public void setFlagCrib(boolean iFlagCrib) {
        if (iFlagCrib != mFlagCrib) {
            mFlagCrib = iFlagCrib;
            newSearchData();
        }
    }

    public boolean getFlagRscds() {
        return mFlagRscds;
    }

    public void setFlagRscds(boolean iFlagRscds) {
        if (iFlagRscds != mFlagRscds) {
            mFlagRscds = iFlagRscds;
            newSearchData();
        }
    }

    //
    public CustomSpinnerItem getCsiFavourite() {
        return mCsiFavourite;
    }

    public void setCsiFavourite(CustomSpinnerItem iCustomSpinnerItem) {
        if (iCustomSpinnerItem == null) {
            return;
        }
        if (mCsiFavourite == null) {
            mCsiFavourite = iCustomSpinnerItem;
            newSearchData();
            return;
        }
        if (iCustomSpinnerItem.mKey.equals(mCsiFavourite.mKey)) {
            return;
        }
        mCsiFavourite = iCustomSpinnerItem;
        newSearchData();
    }

    public void setCsiRhythmType(CustomSpinnerItem iCustomSpinnerItem) {
        if (iCustomSpinnerItem == null) {
            return;
        }
        if (mCsiRhythmType == null) {
            mCsiRhythmType = iCustomSpinnerItem;
            newSearchData();
            return;
        }
        if (iCustomSpinnerItem.mKey.equals(mCsiRhythmType.mKey)) {
            return;
        }
        mCsiRhythmType = iCustomSpinnerItem;
        newSearchData();
    }

    public void setEnumShape(CustomSpinnerItem iCustomSpinnerItem) {
        if (iCustomSpinnerItem == null) {
            return;
        }
        if (mCsiShapeType == null) {
            mCsiShapeType = iCustomSpinnerItem;
            newSearchData();
            return;
        }
        if (iCustomSpinnerItem.mKey.equals(mCsiShapeType.mKey)) {
            return;
        }
        mCsiShapeType = iCustomSpinnerItem;
        newSearchData();
    }


    //</editor-fold>

    //Livecycle

    //Static Methods

    //Internal Organs


    //    /**
//     * prep the SearchPattern and call the generic search, i.e. the DB
//     */
//    private void search() {
    public SearchPattern getSearchPattern() {
        storeValues();
        SearchPattern tSearchPattern = new SearchPattern(Dance.class);
        // copy the search info into the SearchCriteria
        if (mListOfId.equals("")) {
            if (!mDancename.equals("")) {
                tSearchPattern.addSearch_Criteria(
                        new SearchCriteria("DANCENAME", mDancename));
            }
            if (mCsiRhythmType != null) {
                if (!mCsiRhythmType.mMethod.equals("SALL")) {
                    tSearchPattern.addSearch_Criteria(
                            new SearchCriteria(mCsiRhythmType.mMethod, mCsiRhythmType.mValue));
                }
            }
            if (mCsiFavourite != null) {
                if (!mCsiFavourite.mMethod.equals("SALL")) {
                    tSearchPattern.addSearch_Criteria(
                            new SearchCriteria(mCsiFavourite.mMethod, mCsiFavourite.mValue));
                }
            }
            if (mCsiShapeType != null) {
                if (!mCsiShapeType.mMethod.equals("SALL")) {
                    tSearchPattern.addSearch_Criteria(
                            new SearchCriteria(mCsiShapeType.mMethod, mCsiShapeType.mValue));
                }
            }

            if (mFlagMusic) {
                tSearchPattern.addSearch_Criteria(
                        new SearchCriteria("MUSIC_REQUIRED", null));
            }
            if (mFlagDiagram) {
                tSearchPattern.addSearch_Criteria(
                        new SearchCriteria("DIAGRAM_REQUIRED", null));
            }
            if (mFlagCrib) {
                tSearchPattern.addSearch_Criteria(
                        new SearchCriteria("CRIB_REQUIRED", null));
            }
            if (mFlagRscds) {
                tSearchPattern.addSearch_Criteria(
                        new SearchCriteria("RSCDS_REQUIRED", null));
            }
        } else {
            tSearchPattern.addSearch_Criteria(
                    new SearchCriteria("LIST_OF_ID", mListOfId));
        }
        Logg.i(TAG, tSearchPattern.toString());
        return tSearchPattern;
    }


    public void storeValues() {
        if (!mWithSearchValueStorage) {
            return;
        }
        SpinnerItemFactory tSpinnerItemFactory = new SpinnerItemFactory();
        String tBasicKey = this.getClass().getSimpleName() + "/";
        String tKey;
        tKey = "Dancename";
        MyPreferences.savePreferenceString(tBasicKey + tKey, mDancename);
        tKey = "Favourite";
        if (mCsiFavourite == null) {
            mCsiFavourite = tSpinnerItemFactory.getSpinnerItem("DANCE_FAVOURITE", "SALL");
        }
        MyPreferences.savePreferenceString(tBasicKey + tKey, mCsiFavourite.mKey);
        tKey = "RhythmType";
        if (mCsiRhythmType == null) {
            mCsiRhythmType = tSpinnerItemFactory.getSpinnerItem("RHYTHM", "SALL");
        }
        MyPreferences.savePreferenceString(tBasicKey + tKey, mCsiRhythmType.mKey);
        tKey = "ShapeType";
        if (mCsiShapeType == null) {
            mCsiShapeType = tSpinnerItemFactory.getSpinnerItem("SHAPE", "SALL");
        }
        MyPreferences.savePreferenceString(tBasicKey + tKey, mCsiShapeType.mKey);

        tKey = "FlagMusic";
        MyPreferences.savePreferenceBoolean(tBasicKey + tKey, mFlagMusic);
        tKey = "FlagDiagram";
        MyPreferences.savePreferenceBoolean(tBasicKey + tKey, mFlagDiagram);
        tKey = "FlagCrib";
        MyPreferences.savePreferenceBoolean(tBasicKey + tKey, mFlagCrib);
        tKey = "FlagRscds";
        MyPreferences.savePreferenceBoolean(tBasicKey + tKey, mFlagRscds);
    }

    public void prepValues() {
        if (!mWithSearchValueStorage) {
            return;
        }
        SpinnerItemFactory tSpinnerItemFactory = new SpinnerItemFactory();
        String tBasicKey = this.getClass().getSimpleName() + "/";
        String tKey;
        String tCode;
        tKey = "Dancename";
        mDancename = MyPreferences.getPreferenceString(tBasicKey + tKey, "");
        try {
            tKey = "Favourite";
            tCode = MyPreferences.getPreferenceString(tBasicKey + tKey, "SALL");
            mCsiFavourite = tSpinnerItemFactory.getSpinnerItem(
                    SpinnerItemFactory.FIELD_DANCE_FAVOURITE, tCode);
        } catch(Exception e) {
            Logg.w(TAG, e.toString());
        }
        try {
            tKey = "RhythmType";
            tCode = MyPreferences.getPreferenceString(tBasicKey + tKey, "SALL");
            mCsiRhythmType = tSpinnerItemFactory.getSpinnerItem(
                    SpinnerItemFactory.FIELD_RHYTHM, tCode);
        } catch(Exception e) {
            Logg.w(TAG, e.toString());
        }
        try {
            tKey = "ShapeType";
            tCode = MyPreferences.getPreferenceString(tBasicKey + tKey, "SALL");
            mCsiShapeType = tSpinnerItemFactory.getSpinnerItem(
                    SpinnerItemFactory.FIELD_SHAPE, tCode);
        } catch(Exception e) {
            Logg.w(TAG, e.toString());
        }
        tKey = "FlagMusic";
        mFlagMusic = MyPreferences.getPreferenceBoolean(tBasicKey + tKey, false);
        tKey = "FlagDiagram";
        mFlagDiagram = MyPreferences.getPreferenceBoolean(tBasicKey + tKey, false);
        tKey = "FlagCrib";
        mFlagCrib = MyPreferences.getPreferenceBoolean(tBasicKey + tKey, true);
        tKey = "FlagRscds";
        mFlagRscds = MyPreferences.getPreferenceBoolean(tBasicKey + tKey, false);
    }
}
