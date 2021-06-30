package org.pochette.organizer.chained_search;

import android.widget.LinearLayout;

import org.json.JSONException;
import org.json.JSONObject;
import org.pochette.organizer.gui.UserChoice;
import org.pochette.organizer.formation.FormationSearch;
import org.pochette.utils_lib.logg.Logg;
import org.pochette.utils_lib.shouting.Shout;
import org.pochette.utils_lib.shouting.Shouting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.TreeSet;

import static org.pochette.organizer.chained_search.ChainedListThread.NODE_ALL;
import static org.pochette.organizer.chained_search.ChainedListThread.NODE_AND;
import static org.pochette.organizer.chained_search.ChainedListThread.NODE_NOT;
import static org.pochette.organizer.chained_search.ChainedListThread.NODE_OR;
import static org.pochette.organizer.chained_search.ChainedListThread.NODE_SEARCH;
import static org.pochette.organizer.chained_search.ChainedListThreadView.VARIANT_SEARCH_BOOLEAN;
import static org.pochette.organizer.chained_search.ChainedListThreadView.VARIANT_SEARCH_FORMATION;
import static org.pochette.organizer.chained_search.ChainedListThreadView.VARIANT_SEARCH_SPINNER;
import static org.pochette.organizer.chained_search.ChainedListThreadView.VARIANT_SEARCH_VALUE;
import static org.pochette.organizer.chained_search.ChainedListThreadView.VARIANT_SUPER;
import static org.pochette.organizer.chained_search.SearchOption.VALUE_TYPE_ENUM;
import static org.pochette.organizer.chained_search.SearchOption.VALUE_TYPE_NONE;

/**
 * This class connects the data of ChainedList to the GUI representation of ChainedListThreadView
 * <p>
 * Operations to be supported are<br>
 * + take a ChainedList and display it on the GUI
 * + edit a Node of type search
 * + drag and drop nodes into nodes of type and, or + not
 */

@SuppressWarnings("unused")
public class ChainedListThreadAdapter implements Shouting {
    private static final String TAG = "FEHA (CLTA)";
    // variables
    private ChainedListThread mRootChainedListThread;
    private LinearLayout mRootLinearLayout;
    private final HashMap<ChainedListThread, ChainedListThreadView> mHM;

    private ChainedListThreadView mActiveChainedListThreadView;
    private ChainedListThread mActiveChainedListThread;

    int mHorizontalMargin = 10;
    int mVerticalMargin = 10;

    boolean mFlagDragActive = false;

    Shout mGlassFloor;
    Shouting mShouting;
    // constructor

    public ChainedListThreadAdapter() {
        mHM = new HashMap<>(0);
    }

    // setter and getter

    public void setRootChainedListThread(ChainedListThread iChainedListThread) {
        mRootChainedListThread = iChainedListThread;
        navigateListAndPrep(iChainedListThread);
    }

    private void navigateListAndPrep(ChainedListThread iChainedListThread) {
        iChainedListThread.addShouting(this);
        ArrayList<ChainedListThread> tAL = iChainedListThread.getAL_Feeder();
        if (tAL != null && tAL.size() > 0) {
            for (ChainedListThread lChainedListThread : tAL) {
                navigateListAndPrep(lChainedListThread);
            }
        }
    }


    public void setRootLinearLayout(LinearLayout rootLinearLayout) {
        mRootLinearLayout = rootLinearLayout;
    }


    public void setShouting(Shouting shouting) {
        mShouting = shouting;
    }

    public TreeSet<Integer> getTreeSet() {
        ChainedListThread tChainedListThread;
        if (mActiveChainedListThread != null) {
            tChainedListThread = mActiveChainedListThread;
        } else {
            tChainedListThread = mRootChainedListThread;
        }
        return tChainedListThread.getTS_List();

    }


    // lifecylce and override
    // internal
    // public methods


    void addToMap(ChainedListThread iChainedListThread, ChainedListThreadView iChainedListThreadView) {
        mHM.put(iChainedListThread, iChainedListThreadView);
    }

    void removeFromMap(ChainedListThread iChainedListThread, ChainedListThreadView iChainedListThreadView) {
        mHM.remove(iChainedListThread);
    }


    void displayOneNode(ChainedListThread iChainedListThread, LinearLayout iParent) {
        ChainedListThreadView tChainedListThreadView;
//        if (mHM.containsKey(iChainedListThread) ) {
//            tChainedListThreadView = mHM.get(iChainedListThread);
//            if (tChainedListThreadView != null) {
//                tChainedListThreadView.refresh();
//            }
//            return;
//        } else {
        tChainedListThreadView = new ChainedListThreadView(mRootLinearLayout.getContext());
        pushData(tChainedListThreadView, iChainedListThread);
        addToMap(iChainedListThread, tChainedListThreadView);
        iChainedListThread.addShouting(this);

        LinearLayout.LayoutParams tLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tLayoutParams.setMargins(2 * mHorizontalMargin, mVerticalMargin, mHorizontalMargin, mVerticalMargin);
        iParent.addView(tChainedListThreadView, tLayoutParams);
        tChainedListThreadView.setShouting(this);
        iChainedListThread.addShouting(this);
        //   }

        ArrayList<ChainedListThread> tAL = iChainedListThread.getAL_Feeder();
        if (tAL != null && tAL.size() > 0) {
            for (ChainedListThread lChainedListThread : tAL) {
                displayOneNode(lChainedListThread, tChainedListThreadView);
            }
        }
    }


    void pushData(ChainedListThreadView iChainedListThreadView, ChainedListThread iChainedListThread) {
        if (iChainedListThreadView == null || iChainedListThread == null) {
            return;
        }
        iChainedListThreadView.setNodeType(iChainedListThread.getNodeType());
        iChainedListThreadView.setHashCodeOfChainedList(iChainedListThread.hashCode());
        iChainedListThreadView.setCount(iChainedListThread.getSize());
        iChainedListThreadView.setTextMethod("");
        iChainedListThreadView.setTextValue("");
        iChainedListThreadView.setUpToDefinition(iChainedListThread.isUpToDefinition());
        switch (iChainedListThread.getNodeType()) {
            case NODE_AND:
                iChainedListThreadView.setTextOption("AND");
                iChainedListThreadView.setDisplayVariant(VARIANT_SUPER);
                break;
            case NODE_OR:
                iChainedListThreadView.setTextOption("OR");
                iChainedListThreadView.setDisplayVariant(VARIANT_SUPER);
                break;
            case NODE_NOT:
                iChainedListThreadView.setTextOption("NOT");
                iChainedListThreadView.setDisplayVariant(VARIANT_SUPER);
                break;
            case NODE_ALL:
                iChainedListThreadView.setTextOption("ALL");
                iChainedListThreadView.setDisplayVariant(VARIANT_SUPER);
                break;
            case NODE_SEARCH:
                iChainedListThreadView.setTextOption("Search");
                String tDisplayText = "N/A";
                String tMethod = "N/A";
                String tValue = "N/A";
                SearchOption tSearchOption = iChainedListThread.getSearchOption();
                if (tSearchOption != null) {
                    tMethod = tSearchOption.mSqlContractMethod;
                    tDisplayText = tSearchOption.mDisplayText;
                    if (tSearchOption.mValueType.equals(VALUE_TYPE_ENUM)) {
                        tValue = iChainedListThread.getValue();
                        iChainedListThreadView.setDisplayVariant(VARIANT_SEARCH_SPINNER);
                    } else if (!tSearchOption.mValueType.equals(VALUE_TYPE_NONE)) {
                        tValue = iChainedListThread.getValue();
                        iChainedListThreadView.setDisplayVariant(VARIANT_SEARCH_VALUE);
                    } else {
                        tValue = "";
                        iChainedListThreadView.setDisplayVariant(VARIANT_SEARCH_BOOLEAN);
                    }
                }
                iChainedListThreadView.setTextMethod(tDisplayText);
                iChainedListThreadView.setTextValue(tValue);
                if (tMethod.equals("FORMATION")) {
                    FormationSearch tFormationSearch = FormationSearch.fromJson(tValue);
                    if (tFormationSearch != null) {
                        iChainedListThreadView.setFormationSearch(tFormationSearch);
                    }
                    iChainedListThreadView.setDisplayVariant(VARIANT_SEARCH_FORMATION);
                }
                break;
        }
        iChainedListThreadView.setDescription("View of " + iChainedListThread.toString());
        iChainedListThreadView.refresh();
    }


    ChainedListThreadView getViewFromHashCode(int iHashCode) {
        ChainedListThread tChainedListThread = getNodeFromHashCode(iHashCode);
        if (tChainedListThread == null) {
            return null;
        }
        if (mHM.containsKey(tChainedListThread)) {
            return mHM.get(tChainedListThread);
        }
        return null;
    }

    ChainedListThread getNodeFromHashCode(int iHashCode) {
        if (mHM.size() == 0) {
            return null;
        }
        for (ChainedListThread lChainedListThread : mHM.keySet()) {
            if (lChainedListThread.hashCode() == iHashCode) {
                return lChainedListThread;
            }
        }
        return null;
    }


    void walkUp(ChainedListThread iChainedListThread) {
        ChainedListThread tParentChainedListThread = iChainedListThread.getParent_ChainedListThread();
        if (tParentChainedListThread != null) {
            tParentChainedListThread.pushChildrenChanged();
            tParentChainedListThread.requestCalculate();
            walkUp(tParentChainedListThread);
        }
    }

    void addNewNode(ChainedListThread iChainedListThread) {
        SearchOption tNewSearchOption = null;
        UserChoice tUserChoice = new UserChoice("Choose");
        tUserChoice.addQuestion("AND", "And");
        tUserChoice.addQuestion("OR", "OR");
        tUserChoice.addQuestion("NOT", "Not");
        tUserChoice.addQuestion("SEARCH", "Search");
        tUserChoice.addQuestion("ALL", "All");
        String tResult = tUserChoice.poseQuestion(mRootLinearLayout.getContext());
        int tNodeType = 0;
        switch (tResult) {
            case "AND":
                tNodeType = NODE_AND;
                break;
            case "OR":
                tNodeType = NODE_OR;
                break;
            case "NOT":
                tNodeType = NODE_NOT;
                break;
            case "SEARCH":
                tNodeType = NODE_SEARCH;
                break;
            case "ALL":
                tNodeType = NODE_ALL;
                break;
        }

        // Create
        ChainedListThread tNewChainedListThread;
        tNewChainedListThread = new ChainedListThread(mRootChainedListThread.getObjectClass(),
                tNodeType);
        tNewChainedListThread.start();
        Logg.i(TAG, tNewChainedListThread.toString());
        // add to model
        iChainedListThread.addFeederChainedList(tNewChainedListThread);
        // add to GUI
        ChainedListThreadView tParentChainedListThreadView =
                getViewFromHashCode(iChainedListThread.hashCode());
        displayOneNode(tNewChainedListThread, tParentChainedListThreadView);
    }


    void processShouting() {
        int tHashCode = 0;
        try {
            tHashCode = new JSONObject(mGlassFloor.mJsonString).getInt("hashCode");
            mActiveChainedListThread = getNodeFromHashCode(tHashCode);
            mActiveChainedListThreadView = getViewFromHashCode(tHashCode);
        } catch(JSONException e) {
            Logg.i(TAG, mGlassFloor.mJsonString);
            Logg.w(TAG, e.toString());
        }
        //
        if (mGlassFloor.mActor.equals("ChainedListThread")) {
            if (mGlassFloor.mLastAction.equals("achieved") &&
                    mGlassFloor.mLastObject.equals("STATUS_UPTODEFINITION")) {
                if (mActiveChainedListThreadView == null || mActiveChainedListThread == null) {
                    Logg.w(TAG, "something is missing for " + tHashCode);
                    return;
                }
                String tText = String.format(Locale.ENGLISH, "%s achieved", mActiveChainedListThread.toString());
                Logg.i(TAG, tText);
                Logg.i(TAG, mRootChainedListThread.toString());
                pushData(mActiveChainedListThreadView, mActiveChainedListThread);
                mActiveChainedListThreadView.refresh();
                walkUp(mActiveChainedListThread);
                if (mActiveChainedListThread.equals(mRootChainedListThread) && mShouting != null) {
                    Shout tShout = new Shout(this.getClass().getSimpleName());
                    Logg.i(TAG, "push to RV");
                    Logg.i(TAG, mActiveChainedListThread.toString());
                    Logg.i(TAG, mRootChainedListThread.toString());
                    tShout.mLastAction = mGlassFloor.mLastAction;
                    tShout.mLastObject = mGlassFloor.mLastObject;
                    mShouting.shoutUp(tShout);
                }
            }
        }
        if (mGlassFloor.mActor.equals("ChainedListThreadView")) {
            if (mActiveChainedListThread != null && mActiveChainedListThreadView != null) {
                if (
                        (mGlassFloor.mLastAction.equals("edited") && mGlassFloor.mLastObject.equals("Value")) ||
                                (mGlassFloor.mLastAction.equals("choosen") && mGlassFloor.mLastObject.equals("MethodCode"))) {
                    if (mActiveChainedListThread.getNodeType() == NODE_SEARCH) {
                        String tSearchOptionCode = null;
                        String tMethodCode = null;
                        String tValue = null;
                        try {
                            JSONObject tJsonObject = new JSONObject(mGlassFloor.mJsonString);
                            if (tJsonObject.has("SearchOptionCode")) {
                                tSearchOptionCode = tJsonObject.getString("SearchOptionCode");
                            }
                            if (tJsonObject.has("MethodCode")) {
                                tMethodCode = tJsonObject.getString("MethodCode");
                            }
                            if (tJsonObject.has("Value")) {
                                tValue = tJsonObject.getString("Value");
                            }
                        } catch(JSONException e) {
                            Logg.w(TAG, e.toString());
                        }
                        boolean tHasChanged = false;
                        if (tSearchOptionCode != null && !tSearchOptionCode.isEmpty()) {
                            SearchOption tSearchOption = mActiveChainedListThread.getSearchOption();
                            SearchOption sSearchOption =
                                    SearchOption.getByCode(mActiveChainedListThread.getObjectClass(), tSearchOptionCode);
                            if (!sSearchOption.equals(tSearchOption)) {
                                String tUseValue;
                                if (tValue == null || tValue.isEmpty()) {
                                    tUseValue = sSearchOption.mDefaultValue;
                                } else {
                                    tUseValue = tValue;
                                }
                                Logg.w(TAG, sSearchOption.mCode);
                                Logg.w(TAG, tUseValue);
                                mActiveChainedListThread.updateSearchSetting(
                                        sSearchOption,
                                        tUseValue);
                                tHasChanged = true;
                            }
                        }
                        if (tMethodCode != null && !tMethodCode.isEmpty()) {
                            SearchOption tSearchOption = mActiveChainedListThread.getSearchOption();
                            SearchOption sSearchOption =
                                    SearchOption.getByMethodCode(mActiveChainedListThread.getObjectClass(), tMethodCode);
                            if (!sSearchOption.equals(tSearchOption)) {
                                String tUseValue;
                                if (tValue == null || tValue.isEmpty()) {
                                    tUseValue = sSearchOption.mDefaultValue;
                                } else {
                                    tUseValue = tValue;
                                }
                                Logg.i(TAG, sSearchOption.mCode);
                                Logg.i(TAG, tUseValue);
                                mActiveChainedListThread.updateSearchSetting(
                                        sSearchOption,
                                       tUseValue);
                                tHasChanged = true;
                            }
                        }
                        if (!tHasChanged &&  tValue != null && !tValue.isEmpty()) {
                            if (mActiveChainedListThread.getSearchOption() != null &&
                                    !mActiveChainedListThread.getSearchOption().mValueType.equals(VALUE_TYPE_NONE)) {
                                if (mActiveChainedListThread.getValue() == null ||
                                        !mActiveChainedListThread.getValue().equals(tValue)) {
                                    mActiveChainedListThread.updateSearchSetting(
                                            mActiveChainedListThread.getSearchOption(),
                                            tValue);
                                    tHasChanged = true;
                                }
                            }
                        }

//                    else if (mGlassFloor.mLastAction.equals("choosen") &&
//                                mGlassFloor.mLastObject.equals("MethodCode") &&
//                                tCode != null) {

                        if (tHasChanged) {
                            mActiveChainedListThread.requestCalculate();
                            pushData(mActiveChainedListThreadView, mActiveChainedListThread);
                            mActiveChainedListThreadView.refresh();
                            walkUp(mActiveChainedListThread);
                        }
                    }
                } else if (mGlassFloor.mLastAction.equals("requested") &&
                        mGlassFloor.mLastObject.equals("AdditionOfNode")) {
                    addNewNode(mActiveChainedListThread);
                }
            }
        }
        if (mGlassFloor.mActor.equals("CLTV_DragListener")) {
            if (mActiveChainedListThread != null && mActiveChainedListThreadView != null) {
                try {
                    tHashCode = new JSONObject(mGlassFloor.mJsonString).getInt("MovingHashCode");
                    ChainedListThread mMovedChainedListThread = getNodeFromHashCode(tHashCode);
                    ChainedListThreadView mMovedChainedListThreadView = getViewFromHashCode(tHashCode);
                    switch (mGlassFloor.mLastObject) {
                        case "ACTION_DROP":
                            mActiveChainedListThreadView.onDropped();
                            executeDragDrop(mActiveChainedListThread, mMovedChainedListThread);
                            mFlagDragActive = false;
                            break;
                        case "ACTION_DRAG_ENDED":
                            if (mFlagDragActive) {
                                // call only once as it is a delete
                                executeDragEnded(mMovedChainedListThread);
                            }
                            mFlagDragActive = false;
                            break;
                        case "ACTION_DRAG_ENTERED":
                            mFlagDragActive = true;
                            mActiveChainedListThreadView.onDragEnter();
                            break;
                        case "ACTION_DRAG_EXITED":
                            mActiveChainedListThreadView.onDragExit();
                            break;
                    }
                } catch(JSONException e) {
                    Logg.w(TAG, e.toString());
                }
            }
        }


        if (mActiveChainedListThreadView != null && mActiveChainedListThread != null) {
            pushData(mActiveChainedListThreadView, mActiveChainedListThread);
            mActiveChainedListThreadView.refresh();
        }
    }


    void executeDragEnded(ChainedListThread iChainedListThread) {
        // this is only called when the drop is made outside, so a delete is done
        // find parent first
        ChainedListThread tPreviousParentChainedListThread =
                iChainedListThread.getParent_ChainedListThread();
        ChainedListThreadView tPreviousParentChainedListThreadView =
                getViewFromHashCode(tPreviousParentChainedListThread.hashCode());

        ChainedListThreadView tMovedChainedListThreadView =
                getViewFromHashCode(iChainedListThread.hashCode());
        // model
        tPreviousParentChainedListThread.removeFromFeederChainedList(iChainedListThread);
        //GUI
        tPreviousParentChainedListThreadView.removeView(tMovedChainedListThreadView);
        tPreviousParentChainedListThreadView.refresh();

    }

    void executeDragDrop(ChainedListThread iNewParentChainedListThread, ChainedListThread iMovedChainedListThread) {

        // find parent first
        ChainedListThread tPreviousParentChainedListThread =
                iMovedChainedListThread.getParent_ChainedListThread();
        ChainedListThreadView tPreviousParentChainedListThreadView =
                getViewFromHashCode(tPreviousParentChainedListThread.hashCode());
        ChainedListThreadView tMovedChainedListThreadView =
                getViewFromHashCode(iMovedChainedListThread.hashCode());
        ChainedListThreadView tNewParentChainedListThreadView =
                getViewFromHashCode(iNewParentChainedListThread.hashCode());

        // model
        tPreviousParentChainedListThread.removeFromFeederChainedList(iMovedChainedListThread);
        iNewParentChainedListThread.addFeederChainedList(iMovedChainedListThread);
        //GUI
        tPreviousParentChainedListThreadView.removeView(tMovedChainedListThreadView);
        displayOneNode(iMovedChainedListThread, tNewParentChainedListThreadView);
        tMovedChainedListThreadView.refresh();
        tPreviousParentChainedListThreadView.refresh();
        tNewParentChainedListThreadView.refresh();

    }


    //Interface
    @Override
    public void shoutUp(Shout tShoutToCeiling) {
        Logg.i(TAG, tShoutToCeiling.toString());
        mGlassFloor = tShoutToCeiling;
        processShouting();
    }

    /**
     * this method takes the ChainedList and displays it on the GUI
     */
    public void display() {
        // start from outside
        if (mHM.size() == 0) {
            mRootLinearLayout.removeAllViews();
        }
        displayOneNode(mRootChainedListThread, mRootLinearLayout);
    }

}
