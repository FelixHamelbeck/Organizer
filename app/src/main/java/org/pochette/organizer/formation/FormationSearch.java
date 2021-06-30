package org.pochette.organizer.formation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.pochette.data_library.scddb_objects.Formation;
import org.pochette.data_library.scddb_objects.FormationRoot;
import org.pochette.utils_lib.logg.Logg;

import java.util.ArrayList;
import java.util.Locale;
import java.util.TreeSet;

@SuppressWarnings("unused")
public class FormationSearch {

    private static final String TAG = "FEHA (FormationSearch)";

    // variables
    private FormationRoot mFormationRoot; // if one is already selected
    private ArrayList<Formation> mAL_Formation;
    private TreeSet<Integer> mTS_Id;
    private static FormationData mFormationData;
    // constructor

    public FormationSearch() {
        mFormationRoot = null;
        mAL_Formation = new ArrayList<>(0);
        mTS_Id = new TreeSet<>();
        if (mFormationData == null) {
            mFormationData = new FormationData();
        }
    }

    // setter and getter

    public FormationRoot getFormationRoot() {
        return mFormationRoot;
    }

    public void setFormationRoot(FormationRoot formationRoot) {
        mFormationRoot = formationRoot;
    }

    public ArrayList<Formation> getAL_Formation() {
        return mAL_Formation;
    }

    public void setAL_Formation(ArrayList<Formation> AL_Formation) {
        mAL_Formation = AL_Formation;
    }

    public TreeSet<Integer> getTS_Id() {
        return mTS_Id;
    }

    public void setTS_Id(TreeSet<Integer> TS_Id) {
        mTS_Id = TS_Id;
    }

    // lifecylce and override
    // internal
    // public methods

    public String getSearchValue() {
        if (mAL_Formation == null || mAL_Formation.size() == 0) {
            return "-1";
        }
        String tResult = "" + mAL_Formation.get(0).mId;
        for (Formation lFormation : mAL_Formation.subList(1, mAL_Formation.size())) {
            tResult = String.format(Locale.ENGLISH, "%s, %d", tResult, lFormation.mId);
        }
        Logg.i(TAG, tResult);
        return tResult;
    }

    public static FormationSearch fromJson(String iString) {
        try {
            if (iString == null || iString.isEmpty()) {
                return null;
            }
            FormationSearch tFormationSearch = new FormationSearch();
            String tKey;
            JSONObject tJsonObject = new JSONObject(iString);
            tKey = tJsonObject.getString("RootKey");
            tFormationSearch.setFormationRoot(
                    mFormationData.getFormationRootByKey(tKey));
            JSONArray tJsonArray;
            tJsonArray = tJsonObject.getJSONArray("FormationArray");
            ArrayList<Formation> tAL_Formation = new ArrayList<>(0);
            for (int i = 0; i < tJsonArray.length(); i++) {
                JSONObject tSelectJsonObject = tJsonArray.getJSONObject(i);
                tKey = tSelectJsonObject.getString("Key");
                Formation lFormation = mFormationData.getFormationByKey(tKey);
                tAL_Formation.add(lFormation);
            }
            tFormationSearch.setAL_Formation(tAL_Formation);
            return tFormationSearch;
        } catch(JSONException e) {
            Logg.w(TAG, "fromJson");
            Logg.w(TAG, iString);
            Logg.w(TAG, e.toString());
            return null;
        }
    }

    public String getDisplayText() {
        String tResult;

        if (mAL_Formation == null || mAL_Formation.size() == 0) {
            tResult = mFormationRoot.mName;
        } else if (mAL_Formation.size() == 1) {
            tResult = mAL_Formation.get(0).mName;
        } else {
            tResult = mFormationRoot.mName + " [" + mAL_Formation.size() + "]";
        }
        return tResult;
    }

    public String toJson() {
        String tResult = "";
        try {
            JSONObject tJsonObject = new JSONObject();
            tJsonObject.put("RootKey", mFormationRoot.mKey);
            JSONArray tJsonArray;
            tJsonArray = new JSONArray();
            for (Formation lFormation : mAL_Formation) {
                JSONObject tSelectJsonObject = new JSONObject();
                tSelectJsonObject.put("Key", lFormation.mKey);
                tSelectJsonObject.put("Id", lFormation.mId);
                tJsonArray.put(tSelectJsonObject);
            }
            tJsonObject.put("FormationArray", tJsonArray);
            tResult = tJsonObject.toString();
        } catch(JSONException e) {
            Logg.w(TAG, "toJson");
            Logg.w(TAG, e.toString());
        }
        return tResult;
    }
}
