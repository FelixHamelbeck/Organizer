package org.pochette.organizer.chained_search;

import android.view.DragEvent;
import android.widget.LinearLayout;

import org.json.JSONException;
import org.json.JSONObject;
import org.pochette.data_library.scddb_objects.Dance;
import org.pochette.organizer.formation.FormationSearch;
import org.pochette.organizer.gui.UserChoice;
import org.pochette.utils_lib.logg.Logg;
import org.pochette.utils_lib.shouting.Shout;
import org.pochette.utils_lib.shouting.Shouting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
//
import static org.pochette.organizer.chained_search.Matryoshka.NODE_ALL;
import static org.pochette.organizer.chained_search.Matryoshka.NODE_AND;
import static org.pochette.organizer.chained_search.Matryoshka.NODE_NOT;
import static org.pochette.organizer.chained_search.Matryoshka.NODE_OR;
import static org.pochette.organizer.chained_search.Matryoshka.NODE_SEARCH;
import static org.pochette.organizer.chained_search.Matryoshka_View.VARIANT_SEARCH_BOOLEAN;
import static org.pochette.organizer.chained_search.Matryoshka_View.VARIANT_SEARCH_FORMATION;
import static org.pochette.organizer.chained_search.Matryoshka_View.VARIANT_SEARCH_SPINNER;
import static org.pochette.organizer.chained_search.Matryoshka_View.VARIANT_SEARCH_VALUE;
import static org.pochette.organizer.chained_search.Matryoshka_View.VARIANT_SUPER;

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

@SuppressWarnings("rawtypes")
public class Matryoshka_Controller implements Shouting {
    private static final String TAG = "FEHA (MYC)";
    // variables
    private Matryoshka mRootMatryoshka;
    private final LinearLayout mRootLinearLayout;
    private HashMap<Matryoshka, Matryoshka_View> mHM;
    //
//    private ChainedListThreadView mActiveChainedListThreadView;
//    private ChainedListThread mActiveChainedListThread;
//
    int mHorizontalMargin = 10;
    int mVerticalMargin = 10;
    //
    boolean mFlagDragActive;
    //
    Shout mGlassFloor;
    Shouting mShouting;

    //    // constructor
//
    public Matryoshka_Controller(LinearLayout iLinearLayout) {
        mRootLinearLayout = iLinearLayout;
        mFlagDragActive = false;
        mHM = new HashMap<>(0);
    }


//
    // setter and getter


    public void setRootMatryoshka(Matryoshka iRootMatryoshka) {
        mRootMatryoshka = iRootMatryoshka;
        mRootMatryoshka.setGuiShouting(this);

    }

    public void setShouting(Shouting shouting) {
        mShouting = shouting;
    }
    // lifecylce and override
    // internal

    void executeDisplay() {
        if (mRootMatryoshka == null || mRootLinearLayout == null) {
            throw new RuntimeException("Layout and Matryoshka need to be available");
        }
        displayOneMV(mRootLinearLayout, mRootMatryoshka);
    }

    void addToHashMap(Matryoshka_View iMatryoshka_View, Matryoshka iMatryoshka) {
        //noinspection SynchronizeOnNonFinalField
        synchronized (mHM) {
            HashMap<Matryoshka, Matryoshka_View> tHM = mHM;
            tHM.put(iMatryoshka, iMatryoshka_View);
            mHM = tHM;
        }
    }

      void displayOneMV(LinearLayout iParent, Matryoshka iMatryoshka) {
        Logg.i(TAG, "displayOneNode");
        Matryoshka_View tMatryoshka_View;
        if (mHM.containsKey(iMatryoshka)) {
            tMatryoshka_View = mHM.get(iMatryoshka);
        } else {
            tMatryoshka_View = new Matryoshka_View(mRootLinearLayout.getContext());
            tMatryoshka_View.setShouting(this);
            addToHashMap(tMatryoshka_View, iMatryoshka);
            LinearLayout.LayoutParams tLayoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            tLayoutParams.setMargins(2 * mHorizontalMargin, mVerticalMargin, mHorizontalMargin, mVerticalMargin);
            iParent.addView(tMatryoshka_View, tLayoutParams);
        }
        pushData(tMatryoshka_View, iMatryoshka);

        ArrayList<Matryoshka> tAL = iMatryoshka.getAL_Feeder();
        if (tAL != null && tAL.size() > 0) {
            for (Matryoshka lMatryoshka : tAL) {
                Logg.i(TAG, "call displayOneNode" + lMatryoshka.hashCode());
                displayOneMV(tMatryoshka_View, lMatryoshka);
            }
        }
    }

    void pushData(Matryoshka_View iMatryoshka_View, Matryoshka iMatryoshka) {
        if (iMatryoshka_View == null || iMatryoshka == null) {
            return;
        }
        iMatryoshka_View.setNodeType(iMatryoshka.getNodeType());
        iMatryoshka_View.setHashCodeOfMatryochka(iMatryoshka.hashCode());
        iMatryoshka_View.setCount(iMatryoshka.getSize());
        iMatryoshka_View.setTextMethod("");
        iMatryoshka_View.setTextValue("");
        iMatryoshka_View.setUpToDefinition(iMatryoshka.isUpToDefinition());
        switch (iMatryoshka.getNodeType()) {
            case NODE_AND:
                iMatryoshka_View.setTextOption("AND");
                iMatryoshka_View.setDisplayVariant(VARIANT_SUPER);
                break;
            case NODE_OR:
                iMatryoshka_View.setTextOption("OR");
                iMatryoshka_View.setDisplayVariant(VARIANT_SUPER);
                break;
            case NODE_NOT:
                iMatryoshka_View.setTextOption("NOT");
                iMatryoshka_View.setDisplayVariant(VARIANT_SUPER);
                break;
            case NODE_ALL:
                iMatryoshka_View.setTextOption("ALL");
                iMatryoshka_View.setDisplayVariant(VARIANT_SUPER);
                break;
            case NODE_SEARCH:
                iMatryoshka_View.setTextOption("Search");
                String tDisplayText = "N/A";
                String tMethod = "N/A";
                String tValue = "N/A";
                SearchOption tSearchOption = iMatryoshka.getSearchOption();
                if (tSearchOption != null) {
                    tMethod = tSearchOption.mSqlContractMethod;
                    tDisplayText = tSearchOption.mDisplayText;
                    if (tSearchOption.mValueType.equals(VALUE_TYPE_ENUM)) {
                        tValue = iMatryoshka.getValue();
                        iMatryoshka_View.setDisplayVariant(VARIANT_SEARCH_SPINNER);
                    } else if (!tSearchOption.mValueType.equals(VALUE_TYPE_NONE)) {
                        tValue = iMatryoshka.getValue();
                        iMatryoshka_View.setDisplayVariant(VARIANT_SEARCH_VALUE);
                    } else {
                        tValue = "";
                        iMatryoshka_View.setDisplayVariant(VARIANT_SEARCH_BOOLEAN);
                    }
                }
                iMatryoshka_View.setTextMethod(tDisplayText);
                iMatryoshka_View.setTextValue(tValue);
                if (tMethod.equals("FORMATION")) {
                    FormationSearch tFormationSearch = FormationSearch.fromJson(tValue);
                    if (tFormationSearch != null) {
                        iMatryoshka_View.setFormationSearch(tFormationSearch);
                    }
                    iMatryoshka_View.setDisplayVariant(VARIANT_SEARCH_FORMATION);
                }
                break;
        }
        //   iMatryoshka_View.setDescription("View of " + iMatryoshka.toString());
        iMatryoshka_View.refresh();
    }


    Matryoshka getMatryoshkaFromHashCode(int iHashCode) {
        for (Matryoshka lMatryoshka : mHM.keySet()) {
            if (lMatryoshka.hashCode() == iHashCode) {
                return lMatryoshka;
            }
        }
        return null;
    }

//
//    void walkUp(ChainedListThread iChainedListThread) {
//        ChainedListThread tParentChainedListThread = iMatryoshka.getParent_ChainedListThread();
//        if (tParentChainedListThread != null) {
//            tParentMatryoshka.pushChildrenChanged();
//            tParentMatryoshka.requestCalculate();
//            walkUp(tParentChainedListThread);
//        }
//    }
//
//    void addNewNode(ChainedListThread iChainedListThread) {
////        SearchOption tNewSearchOption = null;
//        UserChoice tUserChoice = new UserChoice("Choose");
//        tUserChoice.addQuestion("AND", "And");
//        tUserChoice.addQuestion("OR", "OR");
//        tUserChoice.addQuestion("NOT", "Not");
//        tUserChoice.addQuestion("SEARCH", "Search");
//        tUserChoice.addQuestion("ALL", "All");
//        String tResult = tUserChoice.poseQuestion(mRootLinearLayout.getContext());
//        int tNodeType = 0;
//        switch (tResult) {
//            case "AND":
//                tNodeType = NODE_AND;
//                break;
//            case "OR":
//                tNodeType = NODE_OR;
//                break;
//            case "NOT":
//                tNodeType = NODE_NOT;
//                break;
//            case "SEARCH":
//                tNodeType = NODE_SEARCH;
//                break;
//            case "ALL":
//                tNodeType = NODE_ALL;
//                break;
//        }
//
//        // Create
//        ChainedListThread tNewChainedListThread;
//        tNewChainedListThread = new ChainedListThread(mRootMatryoshka.getObjectClass(),
//                tNodeType);
//        tNewMatryoshka.start();
//        Logg.i(TAG, tNewMatryoshka.toString());
//        // add to model
//        iMatryoshka.addFeederChainedList(tNewChainedListThread);
//        // add to GUI
//        ChainedListThreadView tParentChainedListThreadView =
//                getViewFromHashCode(iMatryoshka.hashCode());
//        displayOneNode(tNewChainedListThread, tParentChainedListThreadView);
//    }
//
//


    void processShouting() {
        Matryoshka_View tMatryoshka_View = null;
        Matryoshka tMatryoshka = null;


        int tHashCode;
        try {
            tHashCode = new JSONObject(mGlassFloor.mJsonString).getInt("hashCode");
            tMatryoshka = getMatryoshkaFromHashCode(tHashCode);
            tMatryoshka_View = mHM.get(tMatryoshka);
            if (tMatryoshka != null) {
                Logg.i(TAG, tMatryoshka.toString());
            }
        } catch(JSONException e) {
            Logg.i(TAG, mGlassFloor.mJsonString);
            Logg.w(TAG, e.toString());
        }

//        //
        switch (mGlassFloor.mActor) {
            case "Matryoshka":
                if (mGlassFloor.mLastAction.equals("achieved") &&
                        mGlassFloor.mLastObject.equals("STATUS_UPTODEFINITION")) {
                    processUptodate(tMatryoshka_View, tMatryoshka);
                }
                break;
            case "Matryoshka_View":
                if (mGlassFloor.mLastAction.equals("edited") &&
                        mGlassFloor.mLastObject.equals("Value")) {
                    try {
                        String tValue = new JSONObject(mGlassFloor.mJsonString).getString("Value");
                        processEditValue(tMatryoshka_View, tMatryoshka, tValue);
                    } catch(JSONException e) {
                        Logg.i(TAG, mGlassFloor.mJsonString);
                        Logg.w(TAG, e.toString());
                    }
                }
                if (mGlassFloor.mLastAction.equals("requested") &&
                        mGlassFloor.mLastObject.equals("AdditionOfMatryoshka")) {
                    processAdditionofMatryoshka(tMatryoshka_View, tMatryoshka);

                }
                if (mGlassFloor.mLastAction.equals("choosen") &&
                        mGlassFloor.mLastObject.equals("MethodCode")) {
                    try {
                        String tSearchOptionCode = new JSONObject(mGlassFloor.mJsonString).getString("SearchOptionCode");
                        processChangedMethodCode(tMatryoshka_View, tMatryoshka, tSearchOptionCode);
                    } catch(JSONException e) {
                        Logg.i(TAG, mGlassFloor.mJsonString);
                        Logg.w(TAG, e.toString());
                    }
                }
                break;
            case "Matryoshka_DragListener":
                if (mGlassFloor.mLastAction.equals("received") &&
                        mGlassFloor.mLastObject.equals("DragAndDropEvent")) {
                    try {
                        JSONObject tJsonObject = new JSONObject(mGlassFloor.mJsonString);
                        int tMatryoshka_dragged_HashCode = tJsonObject.getInt("DraggedHashCode");
                        int tAction = tJsonObject.getInt("Action");
                        processDragShout(tMatryoshka, tMatryoshka_dragged_HashCode, tAction);
                    } catch(JSONException e) {
                        Logg.i(TAG, mGlassFloor.mJsonString);
                        Logg.w(TAG, e.toString());
                    }
                }
                break;
        }


//                    Logg.w(TAG, "something is missing for " + tHashCode);
//                    return;
//                }
//                String tText = String.format(Locale.ENGLISH, "%s achieved", mActiveMatryoshka.toString());
//                Logg.i(TAG, tText);
//                Logg.i(TAG, mRootMatryoshka.toString());
//                pushData(mActiveChainedListThreadView, mActiveChainedListThread);
//                mActiveChainedListThreadView.refresh();
//                walkUp(mActiveChainedListThread);
//                if (mActiveMatryoshka.equals(mRootChainedListThread) && mShouting != null) {
//                    Shout tShout = new Shout(this.getClass().getSimpleName());
//                    Logg.i(TAG, "push to RV");
//                    Logg.i(TAG, mActiveMatryoshka.toString());
//                    Logg.i(TAG, mRootMatryoshka.toString());
//                    tShout.mLastAction = mGlassFloor.mLastAction;
//                    tShout.mLastObject = mGlassFloor.mLastObject;
//                    mShouting.shoutUp(tShout);
//                }
//            }
//        }
//        if (mGlassFloor.mActor.equals("ChainedListThreadView")) {
//            if (mActiveChainedListThread != null && mActiveChainedListThreadView != null) {
//                if (
//                        (mGlassFloor.mLastAction.equals("edited") && mGlassFloor.mLastObject.equals("Value")) ||
//                                (mGlassFloor.mLastAction.equals("choosen") && mGlassFloor.mLastObject.equals("MethodCode"))) {
//                    if (mActiveMatryoshka.getNodeType() == NODE_SEARCH) {
//                        String tSearchOptionCode = null;
//                        String tMethodCode = null;
//                        String tValue = null;
//                        try {
//                            JSONObject tJsonObject = new JSONObject(mGlassFloor.mJsonString);
//                            if (tJsonObject.has("SearchOptionCode")) {
//                                tSearchOptionCode = tJsonObject.getString("SearchOptionCode");
//                            }
//                            if (tJsonObject.has("MethodCode")) {
//                                tMethodCode = tJsonObject.getString("MethodCode");
//                            }
//                            if (tJsonObject.has("Value")) {
//                                tValue = tJsonObject.getString("Value");
//                            }
//                        } catch(JSONException e) {
//                            Logg.w(TAG, e.toString());
//                        }
//                        boolean tHasChanged = false;
//                        if (tSearchOptionCode != null && !tSearchOptionCode.isEmpty()) {
//                            SearchOption tSearchOption = mActiveMatryoshka.getSearchOption();
//                            SearchOption sSearchOption =
//                                    SearchOption.getByCode(mActiveMatryoshka.getObjectClass(), tSearchOptionCode);
//                            if (!sSearchOption.equals(tSearchOption)) {
//                                String tUseValue;
//                                if (tValue == null || tValue.isEmpty()) {
//                                    tUseValue = sSearchOption.mDefaultValue;
//                                } else {
//                                    tUseValue = tValue;
//                                }
//                                Logg.w(TAG, sSearchOption.mCode);
//                                Logg.w(TAG, tUseValue);
//                                mActiveMatryoshka.updateSearchSetting(
//                                        sSearchOption,
//                                        tUseValue);
//                                tHasChanged = true;
//                            }
//                        }
//                        if (tMethodCode != null && !tMethodCode.isEmpty()) {
//                            SearchOption tSearchOption = mActiveMatryoshka.getSearchOption();
//                            SearchOption sSearchOption =
//                                    SearchOption.getByMethodCode(mActiveMatryoshka.getObjectClass(), tMethodCode);
//                            if (!sSearchOption.equals(tSearchOption)) {
//                                String tUseValue;
//                                if (tValue == null || tValue.isEmpty()) {
//                                    tUseValue = sSearchOption.mDefaultValue;
//                                } else {
//                                    tUseValue = tValue;
//                                }
//                                Logg.i(TAG, sSearchOption.mCode);
//                                Logg.i(TAG, tUseValue);
//                                mActiveMatryoshka.updateSearchSetting(
//                                        sSearchOption,
//                                       tUseValue);
//                                tHasChanged = true;
//                            }
//                        }
//                        if (!tHasChanged &&  tValue != null && !tValue.isEmpty()) {
//                            if (mActiveMatryoshka.getSearchOption() != null &&
//                                    !mActiveMatryoshka.getSearchOption().mValueType.equals(VALUE_TYPE_NONE)) {
//                                if (mActiveMatryoshka.getValue() == null ||
//                                        !mActiveMatryoshka.getValue().equals(tValue)) {
//                                    mActiveMatryoshka.updateSearchSetting(
//                                            mActiveMatryoshka.getSearchOption(),
//                                            tValue);
//                                    tHasChanged = true;
//                                }
//                            }
//                        }
//
////                    else if (mGlassFloor.mLastAction.equals("choosen") &&
////                                mGlassFloor.mLastObject.equals("MethodCode") &&
////                                tCode != null) {
//
//                        if (tHasChanged) {
//                            mActiveMatryoshka.requestCalculate();
//                            pushData(mActiveChainedListThreadView, mActiveChainedListThread);
//                            mActiveChainedListThreadView.refresh();
//                            walkUp(mActiveChainedListThread);
//                        }
//                    }
//                } else if (mGlassFloor.mLastAction.equals("requested") &&
//                        mGlassFloor.mLastObject.equals("AdditionOfNode")) {
//                    addNewNode(mActiveChainedListThread);
//                }
//            }
//        }
//        if (mGlassFloor.mActor.equals("CLTV_DragListener")) {
//            if (mActiveChainedListThread != null && mActiveChainedListThreadView != null) {
//                try {
//                    tHashCode = new JSONObject(mGlassFloor.mJsonString).getInt("MovingHashCode");
//                    ChainedListThread mMovedChainedListThread = getNodeFromHashCode(tHashCode);
////                    ChainedListThreadView mMovedChainedListThreadView = getViewFromHashCode(tHashCode);
//                    switch (mGlassFloor.mLastObject) {
//                        case "ACTION_DROP":
//                            mActiveChainedListThreadView.onDropped();
//                            executeDragDrop(mActiveChainedListThread, mMovedChainedListThread);
//                            mFlagDragActive = false;
//                            break;
//                        case "ACTION_DRAG_ENDED":
//                            if (mFlagDragActive) {
//                                // call only once as it is a delete
//                                executeDragEnded(mMovedChainedListThread);
//                            }
//                            mFlagDragActive = false;
//                            break;
//                        case "ACTION_DRAG_ENTERED":
//                            mFlagDragActive = true;
//                            mActiveChainedListThreadView.onDragEnter();
//                            break;
//                        case "ACTION_DRAG_EXITED":
//                            mActiveChainedListThreadView.onDragExit();
//                            break;
//                    }
//                } catch(JSONException e) {
//                    Logg.w(TAG, e.toString());
//                }
//            }
//        }
//
//
//        if (mActiveChainedListThreadView != null && mActiveChainedListThread != null) {
//            pushData(mActiveChainedListThreadView, mActiveChainedListThread);
//            mActiveChainedListThreadView.refresh();
//        }
    }

    void processUptodate(Matryoshka_View iMatryoshka_View, Matryoshka iMatryoshka) {
        if (iMatryoshka == null) {
            return;
        }
        if (iMatryoshka_View == null) {
            return;
        }
        pushData(iMatryoshka_View, iMatryoshka);
        iMatryoshka_View.refresh();
    }


    void processEditValue(Matryoshka_View iMatryoshka_View, Matryoshka iMatryoshka,
                          String iValue) {
        if (iMatryoshka == null) {
            return;
        }
        if (iMatryoshka_View == null) {
            return;
        }
        iMatryoshka.updateSearchSetting(iMatryoshka.getSearchOption(), iValue);
        pushData(iMatryoshka_View, iMatryoshka);
        iMatryoshka_View.refresh();
    }

    void processAdditionofMatryoshka(Matryoshka_View iMatryoshka_View, Matryoshka iMatryoshka) {
        if (iMatryoshka == null) {
            return;
        }
        if (iMatryoshka_View == null) {
            return;
        }
        if (iMatryoshka.getNodeType() == Matryoshka.NODE_SEARCH) {
            Logg.w(TAG, "Cannot add child to NODE_SEARCH");
            return;
        }
        if (iMatryoshka.getNodeType() == Matryoshka.NODE_ALL) {
            Logg.w(TAG, "Cannot add child to NODE_ALL");
            return;
        }
        if (iMatryoshka.getNodeType() == Matryoshka.NODE_NOT && iMatryoshka.getAL_Feeder().size() > 0) {
            Logg.w(TAG, "Cannot add child to NODE_NOT if a child is already present");
            return;
        }

        UserChoice tUserChoice = new UserChoice("Choose");
        tUserChoice.addQuestion("AND", "And");
        tUserChoice.addQuestion("OR", "OR");
        tUserChoice.addQuestion("NOT", "Not");
        tUserChoice.addQuestion("SEARCH", "Search");
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
        }



        Matryoshka tMatryoshka = getNewMatryoshka(tNodeType);
        iMatryoshka.addSubMatryoshka(tMatryoshka);
        displayOneMV(iMatryoshka_View, tMatryoshka);

    }

    Matryoshka getNewMatryoshka(int tNodeType) {
        Matryoshka tMatryoshka = null;
        Class tClass = Dance.class;
        switch (tNodeType) {
            case NODE_AND:
            case NODE_OR:
            case NODE_NOT:
                tMatryoshka = new Matryoshka(tClass, tNodeType);
                break;
            case NODE_SEARCH:
                tMatryoshka = new Matryoshka(tClass, Matryoshka.NODE_SEARCH);
                SearchOption tSearchOption = SearchOption.getByCode(tClass, "DANCENAME");
                tMatryoshka.updateSearchSetting(tSearchOption, "Rant");
                break;
        }
        return tMatryoshka;
    }

    void processChangedMethodCode(Matryoshka_View iMatryoshka_View, Matryoshka iMatryoshka,
                                  String iSearchOptionCode) {
        if (iMatryoshka == null) {
            return;
        }
        if (iMatryoshka_View == null) {
            return;
        }
        if (iSearchOptionCode != null && !iSearchOptionCode.isEmpty()) {
            SearchOption tSearchOption = iMatryoshka.getSearchOption();
            SearchOption sSearchOption =
                    SearchOption.getByCode(iMatryoshka.getObjectClass(), iSearchOptionCode);
            if (!sSearchOption.equals(tSearchOption)) {
                String tUseValue = sSearchOption.mDefaultValue;
                Logg.i(TAG, sSearchOption.mCode);
                Logg.i(TAG, tUseValue);
                iMatryoshka.updateSearchSetting(
                        sSearchOption,
                        tUseValue);
                pushData(iMatryoshka_View, iMatryoshka);
                iMatryoshka_View.refresh();
            }
        }
    }


    void processDragShout(Matryoshka iReceivingMatryoshka, int iHashCode_dragged, int iAction) {
        Matryoshka tDraggedMatryoshka = getMatryoshkaFromHashCode(iHashCode_dragged);
        if (tDraggedMatryoshka == null) {
            return;
        }
        Matryoshka_View tReceivingMatryoshka_View;
        if (!mHM.containsKey(iReceivingMatryoshka)) {
            return;
        }
        tReceivingMatryoshka_View = mHM.get(iReceivingMatryoshka);
        switch (iAction) {
            case DragEvent.ACTION_DRAG_ENTERED:
                tReceivingMatryoshka_View.onDragEnter();
                // tReceivingMatryoshka_View.refresh();
                Logg.i(TAG, "past onDragEnter");
                break;
            case DragEvent.ACTION_DRAG_EXITED:
            case DragEvent.ACTION_DRAG_ENDED:
                tReceivingMatryoshka_View.onDragExit();
                //    tReceivingMatryoshka_View.refresh();
                break;
            case DragEvent.ACTION_DROP:
                processDrop(iReceivingMatryoshka, tDraggedMatryoshka);
                break;
        }
    }



    void processDrop(Matryoshka iReceivingMatryoshka, Matryoshka iDraggedMatryoshka) {
        // do not do anything if the receiving is child of, as this would be a loop
        if (iReceivingMatryoshka.isChildOf(iDraggedMatryoshka)) {
            Logg.i(TAG, "is child of");
            return;
        }
        // do not do anythins, if the receiver is already the parent
        if (iReceivingMatryoshka == iDraggedMatryoshka.getParent_Matryoshka()) {
            Logg.i(TAG, "is already parent of");
            return;
        }

        if (!mHM.containsKey(iDraggedMatryoshka)) {
            Logg.i(TAG, "no View");
            return;
        }
        Matryoshka_View tDraggedMatryoshkaView = mHM.get(iDraggedMatryoshka);
        Matryoshka tParentMaryoshka = iDraggedMatryoshka.getParent_Matryoshka();
        if (tParentMaryoshka == null) {
            Logg.i(TAG, "no parent");
            return;
        }
        if (!mHM.containsKey(tParentMaryoshka)) {
            Logg.i(TAG, "no View for parent");
            return;
        }
        if (!mHM.containsKey(iReceivingMatryoshka)) {
            Logg.i(TAG, "no View for receiving");
            return;
        }
        Matryoshka_View tParentMatryoshkaView = mHM.get(tParentMaryoshka);
        Matryoshka_View tReceivingMatryoshkaView = mHM.get(iReceivingMatryoshka);
        // so far nothing has changed
        tParentMatryoshkaView.removeView(tDraggedMatryoshkaView);
        tParentMaryoshka.removeFromFeederMatryoshka(iDraggedMatryoshka);
        iReceivingMatryoshka.addSubMatryoshka(iDraggedMatryoshka);
        pushData(tReceivingMatryoshkaView,iReceivingMatryoshka);
        tReceivingMatryoshkaView.refresh();
    }



//
//
//    void executeDragEnded(ChainedListThread iChainedListThread) {
//        // this is only called when the drop is made outside, so a delete is done
//        // find parent first
//        ChainedListThread tPreviousParentChainedListThread =
//                iMatryoshka.getParent_ChainedListThread();
//        ChainedListThreadView tPreviousParentChainedListThreadView =
//                getViewFromHashCode(tPreviousParentMatryoshka.hashCode());
//
//        ChainedListThreadView tMovedChainedListThreadView =
//                getViewFromHashCode(iMatryoshka.hashCode());
//        // model
//        tPreviousParentMatryoshka.removeFromFeederChainedList(iChainedListThread);
//        //GUI
//        tPreviousParentChainedListThreadView.removeView(tMovedChainedListThreadView);
//        tPreviousParentChainedListThreadView.refresh();
//
//    }
//
//    void executeDragDrop(ChainedListThread iNewParentChainedListThread, ChainedListThread iMovedChainedListThread) {
//
//        // find parent first
//        ChainedListThread tPreviousParentChainedListThread =
//                iMovedMatryoshka.getParent_ChainedListThread();
//        ChainedListThreadView tPreviousParentChainedListThreadView =
//                getViewFromHashCode(tPreviousParentMatryoshka.hashCode());
//        ChainedListThreadView tMovedChainedListThreadView =
//                getViewFromHashCode(iMovedMatryoshka.hashCode());
//        ChainedListThreadView tNewParentChainedListThreadView =
//                getViewFromHashCode(iNewParentMatryoshka.hashCode());
//
//        // model
//        tPreviousParentMatryoshka.removeFromFeederChainedList(iMovedChainedListThread);
//        iNewParentMatryoshka.addFeederChainedList(iMovedChainedListThread);
//        //GUI
//        tPreviousParentChainedListThreadView.removeView(tMovedChainedListThreadView);
//        displayOneNode(iMovedChainedListThread, tNewParentChainedListThreadView);
//        tMovedChainedListThreadView.refresh();
//        tPreviousParentChainedListThreadView.refresh();
//        tNewParentChainedListThreadView.refresh();
//
//    }
//
//
//    // public methods
//    //Interface
//
//
//    /**
//     * this method takes the ChainedList and displays it on the GUI
//     */
//    public void display() {
//        // start from outside
//        if (mHM.size() == 0) {
//            mRootLinearLayout.removeAllViews();
//        }
//        displayOneNode(mRootChainedListThread, mRootLinearLayout);
//    }

    public void display() {
        executeDisplay();
    }

    public HashSet<Integer> getHashSet() {
        return mRootMatryoshka.getHS_List();
    }


    @Override
    public void shoutUp(Shout tShoutToCeiling) {
        if (!tShoutToCeiling.mActor.equals("Matryoshka_DragListener")) {
            Logg.i(TAG, tShoutToCeiling.toString());
        }
        mGlassFloor = tShoutToCeiling;
        processShouting();
    }
}
