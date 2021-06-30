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
import java.util.Locale;
import java.util.TreeSet;

import androidx.annotation.Nullable;

import static org.pochette.organizer.chained_search.SearchOption.VALUE_TYPE_NONE;

//import static java.lang.Thread.sleep;

public class ChainedListThread extends Thread implements Shouting {

    private static final String TAG = "FEHA (CLT)";

    private static final int DELAY_MIN = 200;
    private static final int DELAY_MAX = 1500;

    public static final int NODE_ALL = 1;
    public static final int NODE_SEARCH = 3;
    public static final int NODE_NOT = 4;
    public static final int NODE_AND = 5;
    public static final int NODE_OR = 6;


    static final int STATUS_DEFINTION_CHANGED = 1; // the treeset and the definition are out of sync, though no action taken yet
    static final int STATUS_WAITING_SUB_NODES = 2; // for NODE_ALL the calculation of mHS_ALL is deemed waiting for sub node
    static final int STATUS_WAITING_SUB_DATABASE = 3; // for NODE_ALL the calculation of mHS_ALL is deemed waiting for sub node
    static final int STATUS_CALCULATING = 4; // dealing with this
    static final int STATUS_READY_SUB = 6;

    static final int STATUS_UPTODEFINITION = 10; // everything is fine


    // Any change of these four variables needs to be synchronized with mLock
    private SearchOption mSearchOption;
    private String mValue;
    private int mStatus;
    private TreeSet<Integer> mTS_List;
    private final Object mLock = new Object();

    private SearchRetrieval mSearchRetrieval;

    // variables
    @SuppressWarnings("rawtypes")
    private final Class mObjectClass;
    private final int mNodeType;

    private final ArrayList<ChainedListThread> mAL_Feeder;

    private ChainedListThread mParent_ChainedListThread;

    private int mDelay;
    @SuppressWarnings("FieldCanBeLocal")
    private final ArrayList<Shouting> mAL_Shouting;
    private Shout mGlassFloor;

    // constructor
    public ChainedListThread(Class<?> iClass, int iNodeType) {
        mObjectClass = iClass;
        mNodeType = iNodeType;
        if (iNodeType == NODE_ALL) {
            mTS_List = getTS_All();
        } else {
            mTS_List = new TreeSet<>();
        }
        if (iNodeType == NODE_AND || iNodeType == NODE_OR || iNodeType == NODE_NOT) {
            mAL_Feeder = new ArrayList<>(0);
        } else {
            mAL_Feeder = null;
        }
        if (iNodeType == NODE_SEARCH) {
            mSearchOption = SearchOption.getByCode(mObjectClass, "DANCENAME");
            mValue = "";
        }
        mDelay = DELAY_MIN;
        mAL_Shouting = new ArrayList<>(0);
        mStatus = STATUS_DEFINTION_CHANGED;
        this.setName("CLT" + hashCode());
    }

    @Override
    public void run() {
        super.run();
        while (!interrupted()) {
            manageCalculation();
            try {
                //noinspection BusyWait
                sleep(mDelay);
                mDelay = (int) (mDelay * 1.1f + 1);
                mDelay = Math.min(DELAY_MAX, mDelay);
            } catch(InterruptedException e) {
                Logg.w(TAG, e.toString());
            }
        }
    }

    // setter and getter
    //<editor-fold desc="Setter and Getter">
    public Class<?> getObjectClass() {
        return mObjectClass;
    }

    public int getNodeType() {
        return mNodeType;
    }

    public TreeSet<Integer> getTS_List() {
        return mTS_List;
    }


    public void updateSearchSetting(SearchOption iSearchOption, String iValue) {
        if (mNodeType != NODE_SEARCH) {
            throw new RuntimeException("updateSearchSetting may only be set for Node=Search");
        }
        if (mObjectClass != iSearchOption.mClass) {
            String tString = String.format(Locale.ENGLISH, "List looks at class %s, but SearchOption at %s",
                    mObjectClass.getSimpleName(), iSearchOption.mClass.getSimpleName());
            throw new RuntimeException(tString);
        }
        synchronized (mLock) {
            mSearchOption = iSearchOption;
            if (mSearchOption.mValueType.equals(VALUE_TYPE_NONE)) {
                mValue = null;
            } else {
                if (iValue == null) {
                    mValue = mSearchOption.mDefaultValue;
                } else {
                    mValue = iValue;
                }
            }
            mStatus = STATUS_DEFINTION_CHANGED;
        }
    }

    @SuppressWarnings("unused")
    public SearchOption getSearchOption() {
        return mSearchOption;
    }

    public String getValue() {
        return mValue;
    }

    public void addFeederChainedList(ChainedListThread iChainedList) {
        if (iChainedList == null) {
            throw new RuntimeException("you cannot add list null");
        }
        if (mNodeType == NODE_ALL || mNodeType == NODE_SEARCH) {
            throw new RuntimeException("addChainedList not allowed for Nodetype " + mNodeType);
        }
        if (mNodeType == NODE_NOT && mAL_Feeder.size() != 0) {
            throw new RuntimeException("only one SubNode allowed in NODE_NOT");
        }
        //      if the same list is added again, do not add
        if (!mAL_Feeder.contains(iChainedList)) {
            synchronized (mLock) {
                mStatus = STATUS_DEFINTION_CHANGED;
                mAL_Feeder.add(iChainedList);
                iChainedList.setParent_ChainedListThread(this);
            }
        }
    }

    public void removeFromFeederChainedList(ChainedListThread iChainedList) {
        if (iChainedList == null) {
            throw new RuntimeException("you cannot remove list null");
        }
        if (mNodeType == NODE_ALL || mNodeType == NODE_SEARCH) {
            throw new RuntimeException("remove not allowed for Nodetype " + mNodeType);
        }
        if (mNodeType == NODE_NOT && mAL_Feeder.size() != 1) {
            throw new RuntimeException("only one SubNode allowed in NODE_NOT");
        }
        //      if the same list is added again, do not add
        if (mAL_Feeder.contains(iChainedList)) {
            synchronized (mLock) {
                mStatus = STATUS_DEFINTION_CHANGED;
                mAL_Feeder.remove(iChainedList);
                Logg.i(TAG, "remove "+iChainedList.toString());
            }
        }
    }


    public ArrayList<ChainedListThread> getAL_Feeder() {
        return mAL_Feeder;
    }

    public ChainedListThread getParent_ChainedListThread() {
        return mParent_ChainedListThread;
    }

    public void setParent_ChainedListThread(ChainedListThread iParent_ChainedList) {
        mParent_ChainedListThread = iParent_ChainedList;
    }

    public void addShouting(Shouting iShouting) {
        if (iShouting != null) {
            if (!mAL_Shouting.contains(iShouting)) {
                mAL_Shouting.add(iShouting);
            }
        }
    }
//
//    public void removeShouting(Shouting iShouting) {
//        if (iShouting != null) {
//            if (!mAL_Shouting.contains(iShouting)) {
//                mAL_Shouting.remove(iShouting);
//            }
//        }
//    }

    public void pushChildrenChanged() {
        synchronized (mLock) {
            mStatus = STATUS_DEFINTION_CHANGED;
        }
    }

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


    //</editor-fold>

    // lifecylce and override
    // internal

    //<editor-fold desc="calculate">
    private TreeSet<Integer> getTS_All() {
        TreeSet<Integer> tResult = new TreeSet<>();
        int i;
        for (i = 1; i < 100; i = i + 1) {
            tResult.add(i);
        }
        return tResult;
    }

    private TreeSet<Integer> getTS_Or() {
        TreeSet<Integer> tResult;
        if (mAL_Feeder == null) {
            throw new RuntimeException("ArrayList of Feeder must be availble for AND");
        }
        tResult = new TreeSet<>();
        if (mAL_Feeder.size() > 0) {
            for (ChainedListThread lChainedList : mAL_Feeder) {
                tResult.addAll(lChainedList.getTS_List());
            }
        }
        synchronized (mLock) {
            mTS_List = tResult;
            mStatus = STATUS_UPTODEFINITION;
        }
        shoutUpToDefintion();
        return tResult;
    }

    private TreeSet<Integer> getTS_And() {
        TreeSet<Integer> tResult;
        synchronized (mLock) {
            mStatus = STATUS_CALCULATING;
        }
        if (mAL_Feeder == null) {
            throw new RuntimeException("ArrayList of Feeder must be availble for AND");
        }
        if (mAL_Feeder.size() == 0) {
            tResult = new TreeSet<>();
        } else {
            tResult = new TreeSet<>(mAL_Feeder.get(0).getTS_List());
            for (ChainedListThread lChainedList : mAL_Feeder.subList(1, mAL_Feeder.size())) {
                tResult.retainAll(lChainedList.getTS_List());
            }
        }
        synchronized (mLock) {
            mTS_List = tResult;
            mStatus = STATUS_UPTODEFINITION;
        }
        shoutUpToDefintion();
        return tResult;
    }

    private TreeSet<Integer> getTS_Not() {
        TreeSet<Integer> tResult;
        if (mAL_Feeder == null || mAL_Feeder.size() != 1) {
            throw new RuntimeException("ArrayList of Feeder must be have 1 member for NOT");
        }
        //noinspection unchecked
        tResult = (TreeSet<Integer>) getTS_All().clone();
        tResult.removeAll(mAL_Feeder.get(0).getTS_List());
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
                } else if  (mValue.equals("SALL")) {
                    tSearchRetrievalMethod = null ;
                    tSearchRetrievalValue = null ;
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
        Logg.i(TAG, "Method" + mSearchOption.mSqlContractMethod);
        if (tSearchRetrievalValue != null) {
            Logg.i(TAG, "Value" + tSearchRetrievalValue);
        }
        mSearchRetrieval = DataServiceSingleton.getInstance().getDataService().
                requestTreeSet(mObjectClass,tSearchRetrievalMethod , tSearchRetrievalValue, this);


    }


    private void processShouting() {
        mDelay = DELAY_MIN;
        if (mGlassFloor.mActor.equals("SearchRetrieval")) {
            if (mGlassFloor.mLastAction.equals("finished") &&
                    mGlassFloor.mLastObject.equals("DB_Retrieval")) {
                if (mSearchRetrieval != null) {
                    synchronized (mLock) {
                        if (mStatus == STATUS_WAITING_SUB_DATABASE) {
                            mTS_List = mSearchRetrieval.getTreeSet();
                            Logg.i(TAG, "getTressSet from Retrieval" + mTS_List.size());
                            mStatus = STATUS_UPTODEFINITION;
                            mSearchRetrieval.stop();
                            mSearchRetrieval = null;
                        }
                    }
                    shoutUpToDefintion();
                }
            }
        }
    }

    private  void calculate() {
        if (checkAllFeedersUptodate()) {
            Logg.i(TAG, "call calc sub method  " + this.toString());
            switch (mNodeType) {
                case NODE_ALL:
                    mTS_List = getTS_All();
                    break;
                case NODE_AND:
                    mTS_List = getTS_And();
                    break;
                case NODE_OR:
                    mTS_List = getTS_Or();
                    break;
                case NODE_NOT:
                    mTS_List = getTS_Not();
                    break;
                case NODE_SEARCH:
                    requestTS_Search();
                    break;
            }

            Logg.i(TAG, "post calculate " + this.toString());
        } else {
            synchronized (mLock) {
                mStatus = STATUS_WAITING_SUB_NODES;
            }
        }

    }
    //</editor-fold>

    private void shoutUpToDefintion() {

        if (mAL_Shouting != null && mAL_Shouting.size() > 0) {
            try {
                Shout tShout = new Shout(this.getClass().getSimpleName());
                tShout.mLastAction = "achieved";
                tShout.mLastObject = "STATUS_UPTODEFINITION";
                JSONObject tJsonObject = new JSONObject();
                tJsonObject.put("hashCode", this.hashCode());
                tShout.mJsonString = tJsonObject.toString();
                for (Shouting lShouting : mAL_Shouting) {
                    lShouting.shoutUp(tShout);
                }
            } catch(JSONException e) {
                Logg.w(TAG, e.toString());
            }
        }
    }


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
                    for (ChainedListThread lCLV : mAL_Feeder) {
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

    private boolean checkAllFeedersUptodate() {
        boolean tResult = true;
        if (mAL_Feeder != null) {
            for (ChainedListThread lChainedList : mAL_Feeder) {
                if (!lChainedList.isUpToDefinition()) {
                    tResult = false;
                    break;
                }
            }
        }
        return tResult;
    }
    // public methods

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
//        tResult = String.format(Locale.ENGLISH, "Stat: %2d (%5d) [%s]",
//                mStatus, mTS_List.size(), tResult);
//
        return tResult;
    }

    @SuppressWarnings("unused")
    public String showList() {
        String tResult = "";
        if (mTS_List == null) {
            tResult = "no list";
        } else if (mTS_List.size() == 0) {
            tResult = "empty list";
        } else {
            for (Integer i : mTS_List) {
                if (tResult.isEmpty()) {
                    tResult = String.format(Locale.ENGLISH, "{ %d", i);
                } else {
                    tResult = String.format(Locale.ENGLISH, "%s, %d", tResult, i);
                }
            }
            tResult = String.format(Locale.ENGLISH, "%s}", tResult);
        }
        return tResult;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!obj.getClass().equals(ChainedListThread.class)) {
            return false;
        }
        ChainedListThread oChainedList = (ChainedListThread) obj;
        if (oChainedList.getObjectClass() != mObjectClass) {
            return false;
        }
        if (oChainedList.getNodeType() != mNodeType) {
            return false;
        }
        if (mNodeType == NODE_ALL) {
            return true;
        }
        if (mNodeType == NODE_NOT) {
            if (mAL_Feeder.size() == 1) {
                if (oChainedList.mAL_Feeder.size() != 1) {
                    return false;
                }
                return oChainedList.mAL_Feeder.get(0).equals(mAL_Feeder.get(0));
            } else {
                //noinspection RedundantIfStatement
                if (oChainedList.mAL_Feeder.size() != 0) {
                    return false;
                } else {
                    return true;
                }
            }
        }
        if (mNodeType == NODE_AND || mNodeType == NODE_OR) {
            if (mAL_Feeder.size() != oChainedList.mAL_Feeder.size()) {
                return false;
            }
            // the mAL may have different sequences, so match any by any and keep track
            //ArrayList<Integer> tAL_OfL = new ArrayList<>(0);
            ArrayList<Integer> tAL_OfK = new ArrayList<>(0);
            for (ChainedListThread lChainedList : mAL_Feeder) {
                int k = 0;
                for (ChainedListThread kChainedList : oChainedList.mAL_Feeder) {
                    if (kChainedList.equals(lChainedList)) {
                        //          tAL_OfL.add(l);
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
            if (mSearchOption.equals(oChainedList.mSearchOption)) {
                return mValue.equals(oChainedList.mValue);
            } else {
                return false;
            }
        } else {
            throw new RuntimeException("false undefined");
        }
    }


    public void requestCalculate() {
        if (mStatus == STATUS_DEFINTION_CHANGED) {
            mStatus = STATUS_CALCULATING;
            final ChainedListThread fChainedList = this;
            Logg.i(TAG, "call calculate" + fChainedList.toString());
            calculate();
        }
    }

    public int getSize() {
        return mTS_List.size();
    }

    @Override
    public void shoutUp(Shout iShoutToCeiling) {
        Logg.i(TAG, iShoutToCeiling.toString());
        mGlassFloor = iShoutToCeiling;
        processShouting();
    }
}
