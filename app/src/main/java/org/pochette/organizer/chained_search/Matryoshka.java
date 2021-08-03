package org.pochette.organizer.chained_search;

import org.json.JSONException;
import org.json.JSONObject;
import org.pochette.data_library.database_management.SearchRetrieval;
import org.pochette.organizer.app.DataServiceSingleton;
import org.pochette.organizer.formation.FormationSearch;
import org.pochette.utils_lib.logg.Logg;
import org.pochette.utils_lib.shouting.Shout;
import org.pochette.utils_lib.shouting.Shouting;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;

import androidx.annotation.Nullable;

import static org.pochette.organizer.chained_search.SearchOption.VALUE_TYPE_NONE;


/**
 * POJO class for matryoshka
 * data are the search definition with any sub matrzoshkas, the parent matryoska (may be null), the status and the result HashSet
 * methods are the calculation, change of search definition and the retrieval of status and the hashset
 */
public class Matryoshka implements Shouting {

    private static final String TAG = "FEHA (MY)";

    public static final int NODE_ALL = 1;
    public static final int NODE_SEARCH = 3;
    public static final int NODE_NOT = 4;
    public static final int NODE_AND = 5;
    public static final int NODE_OR = 6;

    static final int STATUS_DEFINTION_CHANGED = 1; // the HashSet and the definition are out of sync, though no action taken yet
    static final int STATUS_WAITING_SUB_NODES = 2; // for NODE_ALL the calculation of mHS_ALL is deemed waiting for sub node
    static final int STATUS_WAITING_SUB_DATABASE = 3; // for NODE_ALL the calculation of mHS_ALL is deemed waiting for sub node
    static final int STATUS_CALCULATING = 4; // dealing with this
    static final int STATUS_READY_SUB = 6;
    static final int STATUS_UPTODEFINITION = 10; // everything is fine



    // Any change of these five variables needs to be synchronized with mLock
    private SearchOption mSearchOption;
    private String mValue;
    private int mStatus;
    private HashSet<Integer> mHS_List;
    private ArrayList<Matryoshka> mAL_Feeder;
    private final Object mLock = new Object();

    private SearchRetrieval mSearchRetrieval;
    private static HashSet<Integer> mHS_All;

    // variables
    @SuppressWarnings("rawtypes")
    private final Class mObjectClass;
    private final int mNodeType;


    private Matryoshka mParent_Matryoshka;
    private Matryoshka_Thread mMatryoshka_Thread;

    private Shouting mDataShouting; // to be called for data processing, shoud not be the parent matroshka, hence usally null
    private Shouting mGuiShouting; // to be call for GUI
    private Shout mGlassFloor;

    // constructor
    public Matryoshka(Class<?> iClass, int iNodeType) {
        mObjectClass = iClass;
        mNodeType = iNodeType;

        SearchOption tSearchOption = null;
        String tValue = null;
        HashSet<Integer> tHS_List;
        ArrayList<Matryoshka> tAL_Feeder;

        tHS_List = new HashSet<>();

        if (iNodeType == NODE_AND || iNodeType == NODE_OR || iNodeType == NODE_NOT) {
            tAL_Feeder = new ArrayList<>(0);
        } else {
            tAL_Feeder = null;
        }
        if (iNodeType == NODE_SEARCH) {
            tSearchOption = SearchOption.getByCode(mObjectClass, "DANCENAME");
            tValue = "";
        }
        mDataShouting = null;
        mGuiShouting = null;

        synchronized (mLock) {
            mSearchOption = tSearchOption;
            mValue = tValue;
            mStatus = STATUS_DEFINTION_CHANGED;
            mHS_List = tHS_List;
            mAL_Feeder = tAL_Feeder;
        }

        mMatryoshka_Thread = new Matryoshka_Thread(this);
        mMatryoshka_Thread.start();

    }


    void processDestruction() {
        if (mMatryoshka_Thread != null) {
            mMatryoshka_Thread.interrupt();
            mMatryoshka_Thread = null;
        }
        if (mAL_Feeder != null) {
            for (Matryoshka lMatryoshka : mAL_Feeder) {
                lMatryoshka.destroy();
            }
            mAL_Feeder.clear();
            mAL_Feeder = null;
        }
        Shout tShout = new Shout("Matryoshka");
        tShout.mLastObject = "Matryoshka";
        tShout.mLastAction = "destroyed";
        if (mGuiShouting != null) {
            mGuiShouting.shoutUp(tShout);
            mGuiShouting = null;
        }
        if (mDataShouting != null) {
            mDataShouting.shoutUp(tShout);
            mDataShouting = null;
        }
    }

    // setter and getter
    //<editor-fold desc="Setter and Getter">


    public int getStatus() {
        return mStatus;
    }

    public void setDataShouting(Shouting dataShouting) {
        mDataShouting = dataShouting;
    }

    public void setGuiShouting(Shouting iGuiShouting) {
        mGuiShouting = iGuiShouting;
        if (mAL_Feeder != null && mAL_Feeder.size() > 0) {
            for (Matryoshka lMatryoshka : mAL_Feeder) {
                lMatryoshka.setGuiShouting(iGuiShouting);
            }
        }
    }

    public Class<?> getObjectClass() {
        return mObjectClass;
    }

    public int getNodeType() {
        return mNodeType;
    }

    public HashSet<Integer> getHS_List() {
        return mHS_List;
    }


    public SearchOption getSearchOption() {
        return mSearchOption;
    }

    public String getValue() {
        return mValue;
    }

    public ArrayList<Matryoshka> getAL_Feeder() {
        return mAL_Feeder;
    }
//</editor-fold>

    // method checked
    private void addFeederMatryoshka(Matryoshka iMatryoshka) {
        if (iMatryoshka == null) {
            throw new RuntimeException("you cannot add null");
        }
        if (mNodeType == NODE_ALL || mNodeType == NODE_SEARCH) {
            throw new RuntimeException("addMatryoshka not allowed for Nodetype " + mNodeType);
        }
        if (mNodeType == NODE_NOT && mAL_Feeder.size() != 0) {
            throw new RuntimeException("only one SubNode allowed in NODE_NOT");
        }
        //      if the same list is added again, do not add
        if (!mAL_Feeder.contains(iMatryoshka)) {
            synchronized (mLock) {
                mStatus = STATUS_DEFINTION_CHANGED;
                mAL_Feeder.add(iMatryoshka);
            }
            iMatryoshka.setParent_Matryoshka(this);
            iMatryoshka.setGuiShouting(mGuiShouting);
        }
    }

    // method checked
    void removeFromFeederMatryoshka(Matryoshka iMatryoshka) {
        if (iMatryoshka == null) {
            throw new RuntimeException("you cannot remove list null");
        }
        if (mNodeType == NODE_ALL || mNodeType == NODE_SEARCH) {
            throw new RuntimeException("remove not allowed for Nodetype " + mNodeType);
        }
        if (mNodeType == NODE_NOT && mAL_Feeder.size() != 1) {
            throw new RuntimeException("only one SubNode allowed in NODE_NOT");
        }
        if (mAL_Feeder.contains(iMatryoshka)) {
            synchronized (mLock) {
                mStatus = STATUS_DEFINTION_CHANGED;
                mAL_Feeder.remove(iMatryoshka);
            }
            Logg.i(TAG, "remove " + iMatryoshka.toString());
        }
    }


    // method checked
    void executeUpdateSearchSetting(SearchOption iSearchOption, String iValue) {
        if (mNodeType != NODE_SEARCH) {
            throw new RuntimeException("updateSearchSetting may only be set for Node=Search");
        }
        if (mObjectClass != iSearchOption.mClass) {
            String tString = String.format(Locale.ENGLISH, "List looks at class %s, but SearchOption at %s",
                    mObjectClass.getSimpleName(), iSearchOption.mClass.getSimpleName());
            throw new RuntimeException(tString);
        }
        String tValue;
        if (iSearchOption.mValueType.equals(VALUE_TYPE_NONE)) {
            tValue = null;
        } else {
            if (iValue == null) {
                tValue = mSearchOption.mDefaultValue;
            } else {
                tValue = iValue;
            }
        }
        synchronized (mLock) {
            mSearchOption = iSearchOption;
            mValue = tValue;
            mStatus = STATUS_DEFINTION_CHANGED;
        }
    }


    // method checked
    public boolean isUpToDefinition() {
        synchronized (mLock) {
            if (mStatus == STATUS_DEFINTION_CHANGED) {
                return false;
            } else if (mStatus == STATUS_WAITING_SUB_NODES) {
                return false;
            } else if (mStatus == STATUS_WAITING_SUB_DATABASE) {
                return false;
            } else if (mStatus == STATUS_CALCULATING) {
                return false;
            } else if (mStatus == STATUS_READY_SUB) {
                return false;
            } else if (mStatus == STATUS_UPTODEFINITION) {
                return true;
            }
            throw new RuntimeException("undefined status " + mStatus);
        }
    }


    // internal


    // method checked
    private void processShouting() {
        if (mGlassFloor.mActor.equals("SearchRetrieval")) {
            if (mGlassFloor.mLastAction.equals("finished") &&
                    mGlassFloor.mLastObject.equals("DB_Retrieval")) {
                if (mSearchRetrieval != null) {
                    synchronized (mLock) {
                        if (mStatus == STATUS_WAITING_SUB_DATABASE) {
                            mHS_List = mSearchRetrieval.getHashSet();
                            mStatus = STATUS_UPTODEFINITION;
                        }
                        mSearchRetrieval.stop();
                        mSearchRetrieval = null;
                    }

                    if (mStatus == STATUS_UPTODEFINITION) {
                        Logg.i(TAG, "getHashSet from Retrieval" + mHS_List.size());
                        shoutUpToDefintion();
                    }
                }
            }
        }
    }


    //</editor-fold>

    // method checked
    private void shoutUpToDefintion() {

        Logg.i(TAG, "Uptodate" + toString());
        try {
            Shout tShout = new Shout(this.getClass().getSimpleName());
            tShout.mLastAction = "achieved";
            tShout.mLastObject = "STATUS_UPTODEFINITION";
            JSONObject tJsonObject = new JSONObject();
            tJsonObject.put("hashCode", this.hashCode());
            tShout.mJsonString = tJsonObject.toString();
            if (mGuiShouting != null) {
                //  Logg.i(TAG, "GUI Shout");
                mGuiShouting.shoutUp(tShout);
            }
            if (mDataShouting != null) {
                //      Logg.i(TAG, "Data Shout");
                mDataShouting.shoutUp(tShout);
            }
            if (mParent_Matryoshka != null) {
                mParent_Matryoshka.pushChildrenChanged();
            }
        } catch(JSONException e) {
            Logg.w(TAG, e.toString());
        }
    }

    //<editor-fold desc="calculate">
    // method checked
    private void manageCalculation() {
        String tAction = "";
        synchronized (mLock) {
            switch (mStatus) {
                case STATUS_UPTODEFINITION:
                case STATUS_WAITING_SUB_DATABASE:
                    tAction = "NOTHING";
                    break;
                case STATUS_DEFINTION_CHANGED:
                case STATUS_READY_SUB:
                    tAction = "REQUEST_CALCULATE";
                    mStatus = STATUS_CALCULATING;
                    break;
                case STATUS_WAITING_SUB_NODES:
                    tAction = "CHECK_SUB_DB";
                    break;
            }
        }
        switch (tAction) {
            case "NOTHING":
                break;
            case "REQUEST_CALCULATE":
                calculate();
                break;
            case "CHECK_SUB_DB":
                boolean tAllUptodate = true;
                if (mAL_Feeder != null) {
                    for (Matryoshka lCLV : mAL_Feeder) {
                        boolean tLocal = lCLV.isUpToDefinition();
                        tAllUptodate = tAllUptodate && tLocal;
                    }
                }
                if (tAllUptodate) {
                    synchronized (mLock) {
                        mStatus = STATUS_READY_SUB;
                    }
                }
                break;
        }
    }

    // method checked
    private boolean checkAllFeedersUptodate() {
        boolean tResult = true;
        if (mAL_Feeder != null) {
            for (Matryoshka lMatryoshka : mAL_Feeder) {
                if (!lMatryoshka.isUpToDefinition()) {
                    tResult = false;
                    break;
                }
            }
        }
        return tResult;
    }

    // method checked
    private void calculate() {
        if (checkAllFeedersUptodate()) {
            //   Logg.i(TAG, "call calc sub method  " + this.toString());
            HashSet<Integer> tHS_List = null;
            switch (mNodeType) {
                case NODE_ALL:
                    tHS_List = getHS_All();
                    break;
                case NODE_AND:
                    tHS_List = getHS_And();
                    break;
                case NODE_OR:
                    tHS_List = getHS_Or();
                    break;
                case NODE_NOT:
                    tHS_List = getHS_Not();
                    break;
                case NODE_SEARCH:
                    requestTS_Search();
                    break;
            }
            synchronized (mLock) {
                mHS_List = tHS_List;
            }
            //  Logg.i(TAG, "post calculate " + this.toString());
        } else {
            synchronized (mLock) {
                mStatus = STATUS_WAITING_SUB_NODES;
            }
        }
    }


    private HashSet<Integer> getHS_All() {
        if (mHS_All == null) {
            mHS_All = SearchRetrieval.getHS_All();
        }
        HashSet<Integer> tResult;
        tResult = (HashSet<Integer>) mHS_All.clone();
        synchronized (mLock) {
            mHS_List = tResult;
            mStatus = STATUS_UPTODEFINITION;
        }
        shoutUpToDefintion();
        return tResult;
    }

    private HashSet<Integer> getHS_Or() {
        HashSet<Integer> tResult;
        if (mAL_Feeder == null) {
            throw new RuntimeException("ArrayList of Feeder must be availble for AND");
        }
        tResult = new HashSet<>();
        if (mAL_Feeder.size() > 0) {
            for (Matryoshka lMatryoshka : mAL_Feeder) {
                tResult.addAll(lMatryoshka.getHS_List());
            }
        }
        synchronized (mLock) {
            mHS_List = tResult;
            mStatus = STATUS_UPTODEFINITION;
        }
        shoutUpToDefintion();
        return tResult;
    }

    private HashSet<Integer> getHS_And() {
        HashSet<Integer> tResult;
        synchronized (mLock) {
            mStatus = STATUS_CALCULATING;
        }
        if (mAL_Feeder == null) {
            throw new RuntimeException("ArrayList of Feeder must be availble for AND");
        }
        if (mAL_Feeder.size() == 0) {
            tResult = new HashSet<>();
        } else {
            tResult = new HashSet<>(mAL_Feeder.get(0).getHS_List());
            for (Matryoshka lMatryoshka : mAL_Feeder.subList(1, mAL_Feeder.size())) {
                tResult.retainAll(lMatryoshka.getHS_List());
            }
        }
        synchronized (mLock) {
            mHS_List = tResult;
            mStatus = STATUS_UPTODEFINITION;
        }
        shoutUpToDefintion();
        return tResult;
    }

    private HashSet<Integer> getHS_Not() {
        HashSet<Integer> tResult;
        if (mAL_Feeder == null || mAL_Feeder.size() != 1) {
            throw new RuntimeException("ArrayList of Feeder must be have 1 member for NOT");
        }
        //noinspection unchecked
        tResult = (HashSet<Integer>) getHS_All().clone();
        tResult.removeAll(mAL_Feeder.get(0).getHS_List());
        return tResult;
    }


    private void requestTS_Search() {
        if (mSearchOption == null) {
            throw new RuntimeException("SearchOption must be available");
        }
        // because of Formation we need to split between SearchCriteria.mValeu and CLT.mValue
        String tSearchRetrievalValue = mValue;
        String tSearchRetrievalMethod = mSearchOption.mSqlContractMethod;
        if (!mSearchOption.mValueType.equals(VALUE_TYPE_NONE)) {
            if (mSearchOption.mSqlContractMethod.equals("FORMATION")) {
                Logg.i(TAG, mValue);
                FormationSearch tFormationSearch = FormationSearch.fromJson(mValue);
                if (tFormationSearch != null) {
                    tSearchRetrievalValue = tFormationSearch.getSearchValue();
                }
            } else if (mSearchOption.mSqlContractMethod.equals("RHYTHM")) {
                if (mValue.equals("RHYTHM_QUICK")) {
                    tSearchRetrievalMethod = "RHYTHM_QUICK";
                    tSearchRetrievalValue = "";
                } else if (mValue.equals("SALL")) {
                    tSearchRetrievalMethod = null;
                    tSearchRetrievalValue = null;
                }
            }
        }
        Logg.i(TAG, "call DB " + this.toString());
        if (mSearchRetrieval != null) {
            mSearchRetrieval.stop();
            mSearchRetrieval = null;
        }
        synchronized (mLock) {
            mStatus = STATUS_WAITING_SUB_DATABASE;
        }
        //    Logg.i(TAG, "Method" + mSearchOption.mSqlContractMethod);
//        if (tSearchRetrievalValue != null) {
//            Logg.i(TAG, "Value" + tSearchRetrievalValue);
//        }
        mSearchRetrieval = DataServiceSingleton.getInstance().getDataService().
                requestHashSet(mObjectClass, tSearchRetrievalMethod, tSearchRetrievalValue, this);


    }


    // public methods

    // method checked
    public String toString() {
        String tResult = "";
        String tSingle;
        switch (mNodeType) {
            case NODE_SEARCH:
                if (mSearchOption == null) {
                    tResult = "Search, no Option yet";
                    return tResult;
                }
                if (mSearchOption.mValueType.equals(VALUE_TYPE_NONE) || mValue.isEmpty()) {
                    tSingle = mSearchOption.mSqlContractMethod;
                    tResult = "Search with Method " + tSingle;
                } else {
                    tSingle = mValue;
                    tResult = "Search with Value " + tSingle;
                }
                break;
            case NODE_ALL:
                tSingle = "ALL";
                tResult = tSingle;
                break;
            case NODE_NOT:
                tSingle = "NOT";
                tResult = tSingle;
                break;
            case NODE_AND:
                tSingle = String.format(Locale.ENGLISH,
                        " AND of %d feeders", mAL_Feeder.size());
                tResult = tSingle;
                break;
            case NODE_OR:
                tSingle = String.format(Locale.ENGLISH,
                        " OR of %d feeders", mAL_Feeder.size());
                tResult = tSingle;
                break;
        }
        if (mHS_List != null) {
            tResult = tResult + "  " + mHS_List.size();
        }
        return tResult;
    }

    public void requestChildRecalculate() {

    }

    // method checked

    /**
     * set the parent, so pushChildrenChanged can be called
     *
     * @param iParent_Matryoshka the parent
     */
    public void setParent_Matryoshka(Matryoshka iParent_Matryoshka) {
        mParent_Matryoshka = iParent_Matryoshka;
    }

    public Matryoshka getParent_Matryoshka() {
        return mParent_Matryoshka;
    }

    /**
     * to be called from one of the childeren
     */
    public void pushChildrenChanged() {
        synchronized (mLock) {
            mStatus = STATUS_DEFINTION_CHANGED;
        }
    }

    // method checked

    /**
     * method to chaned the search setting (Node == SEARCH)
     *
     * @param iSearchOption the searchoption of the node
     * @param iValue        if needed the searchvale of the node
     */
    public void updateSearchSetting(SearchOption iSearchOption, String iValue) {
        executeUpdateSearchSetting(iSearchOption, iValue);
    }


    // method checked

    /**
     * method to add a feeder matryoshka
     *
     * @param iMatryoshka to be added as feeder
     */
    public void addSubMatryoshka(Matryoshka iMatryoshka) {
        addFeederMatryoshka(iMatryoshka);
    }

    // method checked 

    /**
     * method to remove a feeder matryoshka
     *
     * @param iMatryoshka to be added as feeder
     */
    public void removeSubMatryoshka(Matryoshka iMatryoshka) {
        removeFromFeederMatryoshka(iMatryoshka);
    }

    /**
     * destroy thie matryoshka and all children, stop and destroy the thread, call the Shoutings
     */
    public void destroy() {
        processDestruction();
    }


    // method checked
    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!obj.getClass().equals(Matryoshka.class)) {
            return false;
        }
        Matryoshka oMatryoshka = (Matryoshka) obj;
        if (oMatryoshka.getObjectClass() != mObjectClass) {
            return false;
        }
        if (oMatryoshka.getNodeType() != mNodeType) {
            return false;
        }
        if (mNodeType == NODE_ALL) {
            return true;
        }
        if (mNodeType == NODE_NOT) {
            if (mAL_Feeder.size() == 1) {
                if (oMatryoshka.mAL_Feeder.size() != 1) {
                    return false;
                }
                return oMatryoshka.mAL_Feeder.get(0).equals(mAL_Feeder.get(0));
            } else {
                //noinspection RedundantIfStatement
                if (oMatryoshka.mAL_Feeder.size() != 0) {
                    return false;
                } else {
                    return true;
                }
            }
        }
        if (mNodeType == NODE_AND || mNodeType == NODE_OR) {
            if (mAL_Feeder.size() != oMatryoshka.mAL_Feeder.size()) {
                return false;
            }
            // the mAL may have different sequences, so match any by any and keep track
            //ArrayList<Integer> tAL_OfL = new ArrayList<>(0);
            ArrayList<Integer> tAL_OfK = new ArrayList<>(0);
            for (Matryoshka lMatryoshka : mAL_Feeder) {
                int k = 0;
                for (Matryoshka kMatryoshka : oMatryoshka.mAL_Feeder) {
                    if (kMatryoshka.equals(lMatryoshka)) {
                        tAL_OfK.add(k);
                        break;
                    }
                    k++;
                }
            }
            //noinspection RedundantIfStatement
            if (tAL_OfK.size() == mAL_Feeder.size()) {
                return true;
            } else {
                return false;
            }
        } else if (mNodeType == NODE_SEARCH) {
            if (mSearchOption.equals(oMatryoshka.mSearchOption)) {
                return mValue.equals(oMatryoshka.mValue);
            } else {
                return false;
            }
        } else {
            throw new RuntimeException("false undefined");
        }
    }


    public void requestCalculate() {
        manageCalculation();
    }

    /**
     * check whether this is child of
     *
     * @param iMatryoshka possible parent or other ancestor
     * @return true if this is directly below iMatryoshka
     */
    public boolean isChildOf(Matryoshka iMatryoshka) {
        Matryoshka tParent = this;
        while (tParent != null) {
            if (iMatryoshka  == tParent) {
                return true;
            }
            tParent = tParent.getParent_Matryoshka();
        }
        return false;
    }


    // method checked 
    public int getSize() {
        if (mHS_List == null) {
            return 0;
        }
        return mHS_List.size();
    }

    // method checked 
    @Override
    public void shoutUp(Shout iShoutToCeiling) {
        Logg.i(TAG, iShoutToCeiling.toString());
        mGlassFloor = iShoutToCeiling;
        processShouting();
    }
}
