package org.pochette.organizer.album;

import org.pochette.data_library.pairing.Signature;
import org.pochette.data_library.scddb_objects.Album;
import org.pochette.organizer.app.MyPreferences;
import org.pochette.organizer.dance.My_ViewModel;
import org.pochette.organizer.gui_assist.CustomSpinnerItem;
import org.pochette.organizer.gui_assist.SpinnerItemFactory;
import org.pochette.utils_lib.logg.Logg;
import org.pochette.utils_lib.search.SearchCriteria;
import org.pochette.utils_lib.search.SearchPattern;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;
import java.util.TreeSet;

/**
 * ViewModel for Dance using MutableLiveDate
 * The values for the searchCriteria are stored her and the execution of the search is organized, to avoid double execution at any time
 */
@SuppressWarnings({"unused", "rawtypes"})
public class Album_ViewModel extends My_ViewModel {


    private final String TAG = "FEHA (Album_ViewModel)";
    //Variables
    private String mSearchArtist;
    private String mSearchName;
    private String mSignatureString;

    private CustomSpinnerItem mCsiPurpose = null;


    boolean mWithSearchValueStorage; // use for testing
    //Constructor
    @SuppressWarnings("unused")
    public Album_ViewModel(boolean iWithSearchValueStorage) {
        super(Album.class);
        mWithSearchValueStorage = iWithSearchValueStorage;
        prepValues();
    }
// needed for the factory
    @SuppressWarnings("unused")
    public Album_ViewModel() {
        super(Album.class);
        mWithSearchValueStorage = true;
        prepValues();
    }

    @SuppressWarnings("unused")
    public Album_ViewModel(Class iClass) {
        super(iClass);
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

    public String getSignatureString() {
        return mSignatureString;
    }

    public void setSignatureString(String iSignatureString) {
        mSignatureString = iSignatureString;
    }

    public boolean isWithSearchValueStorage() {
        return mWithSearchValueStorage;
    }
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

    public ArrayList<Object> localSort(ArrayList<Object> iAR) {
        //Logg.i(TAG, "start localSort"+ mSignatureString);
        ArrayList<Object> jAR = new ArrayList<>(0);
        if (iAR == null) {
            Logg.i(TAG, " no MLD");
            return null;
        }
        if (mSignatureString == null || mSignatureString.isEmpty()) {
            return iAR;
        }

        Signature tSignature = new Signature(mSignatureString);
        //HashMap<Album,Float> tHM = new HashMap<>(0);

        HashMap<Float, ArrayList<Album>> tX = new HashMap<>(0);
        TreeSet<Float> tHS = new TreeSet<>();
        for (Object lObject : iAR) {
            Album lAlbum = (Album) lObject;
            Signature lSignature = new Signature(lAlbum.mSignature);
            Float tScore = tSignature.compare(lSignature);
            if (tScore <= 0.3f) {
                continue;
            }
            //Logg.i(TAG, lAlbum.toString() + tScore);
            if (tHS.contains(tScore)) {
                ArrayList<Album> tAL = tX.get(tScore);
                Objects.requireNonNull(tAL).add(lAlbum);
            } else {
                ArrayList<Album> tAL = new ArrayList<>(0);
                tAL.add(lAlbum);
                tX.put(tScore, tAL);
                tHS.add(tScore);
            }
            if (tHS.size() > 20) {
                break;
            }
        }

        TreeSet<Float> tHSorted = new TreeSet<>(Float::compareTo);
        tHSorted.addAll(tHS);
        Iterator tIterator = tHSorted.descendingIterator();
        while (tIterator.hasNext()) {
            Float lFloat = (Float) tIterator.next();
            //for (Float lFloat : tHSorted) {
            Logg.i(TAG, "sort " + lFloat);
            ArrayList<Album> tAL = tX.get(lFloat);
            jAR.addAll(Objects.requireNonNull(tAL));
        }
        Logg.i(TAG, " HM " + jAR.size());
        return jAR;
    }

    public SearchPattern getSearchPattern() {
        storeValues();
        SearchPattern tSearchPattern = new SearchPattern(Album.class);
        SearchCriteria tSearchCriteria;
        if (mSearchArtist != null && !mSearchArtist.isEmpty()) {
            tSearchCriteria = new SearchCriteria("ARTIST_SHORTNAME", mSearchArtist);
            tSearchPattern.addSearch_Criteria(tSearchCriteria);
        }
        if (mSearchName != null && !mSearchName.isEmpty()) {
            tSearchCriteria = new SearchCriteria("ALBUM_SHORTNAME", mSearchName);
            tSearchPattern.addSearch_Criteria(tSearchCriteria);
        }
//        if (mSignatureString != null && !mSignatureString.isEmpty()) {
//            Logg.i(TAG, "nonTrivial");
//            tSearchCriteria = new SearchCriteria("NON_TRIVIAL_SIGNATURE", "");
//            tSearchPattern.addSearch_Criteria(tSearchCriteria);
//        }

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
        tKey = "SignatureString";
        MyPreferences.savePreferenceString(tBasicKey + tKey, mSignatureString);
        tKey = "Purpose";
        if (mCsiPurpose == null) {
            SpinnerItemFactory tSpinnerItemFactory = new SpinnerItemFactory();
            mCsiPurpose = tSpinnerItemFactory.getSpinnerItem(SpinnerItemFactory.FIELD_MUSICDIRECTORY_PURPOSE, "SALL");
        }
        MyPreferences.savePreferenceString(tBasicKey + tKey, mCsiPurpose.mKey);
    }

    public void prepValues() {
        Logg.i(TAG, "start prep values");
        if (!mWithSearchValueStorage) {
            Logg.i(TAG, "leave prep" + mWithSearchValueStorage);
            return;
        }
        String tBasicKey = this.getClass().getSimpleName() + "/";
        String tKey;
        tKey = "ArtistName";
        mSearchArtist = MyPreferences.getPreferenceString(tBasicKey + tKey, "");
        tKey = "Name";
        mSearchName = MyPreferences.getPreferenceString(tBasicKey + tKey, "");
        tKey = "SignatureString";
        mSignatureString = MyPreferences.getPreferenceString(tBasicKey + tKey, "");
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
            Logg.i(TAG, e.toString());
        }
    }

}
