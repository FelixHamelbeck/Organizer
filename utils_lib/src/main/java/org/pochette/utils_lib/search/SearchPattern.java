package org.pochette.utils_lib.search;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.pochette.utils_lib.logg.Logg;

import java.util.ArrayList;
import java.util.Locale;


/**
 * The class SearchPattern stores SearchCriteria for an defined ObjectClass
 * <p>
 * It is used to communicate the search requirements to the generic search engine
 * SearchCall
 * <p>
 * Currently 2018/11 all SearchCriteria are connected by a logical AND. Still valid 2020/07
 */

@SuppressWarnings({"rawtypes", "unused"})
public class SearchPattern {

    //Variables
    final static String TAG = "FEHA (SearchPattern)";
    private final ArrayList<SearchCriteria> mAL_SearchCriteria;
    private String mSortOrder;
    private Class mClass;

    //Constructor
    public SearchPattern(Class iClass) {
        mAL_SearchCriteria = new ArrayList<>(0);
        mSortOrder = "";
        mClass = iClass;

    }

    //Setter and Getter
    public ArrayList<SearchCriteria> getAL_SearchCriteria() {
        return mAL_SearchCriteria;
    }

    public Class getSearchClass() {
        return mClass;
    }

    // needed for SlimDance
    public void setSearchClass(Class iClass) {
        mClass = iClass;
    }

    public String getSortOrder() {
        return mSortOrder;
    }

    public void setSortOrder(String iSortOrder) {
        mSortOrder = iSortOrder;

    }

    //Livecycle
    //Static Methods
    //Internal Organs
    //Interface

    public String toString() {
        String result = mClass.getName();
        for (SearchCriteria t : mAL_SearchCriteria) {
            result = String.format(Locale.ENGLISH, "%s\n\t%s", result, t.toString());
        }
        return result;
    }

    public JSONObject toJson() {
        JSONObject tJson = new JSONObject();
        try {
            tJson.put("Class", mClass.getName());
            JSONArray tJsonArray = new JSONArray();
            for (SearchCriteria lSearchCriteria : mAL_SearchCriteria) {
                JSONObject tSub = lSearchCriteria.toJson();
                tJsonArray.put(tSub);
            }
            tJson.put("SearchCriteria", tJsonArray);
            return tJson;
        } catch (JSONException e) {
            Logg.e(TAG, e.toString());
            return null;
        }
    }

    public static SearchPattern fromJson(JSONObject iJsonObject) {
        String tClassString;
        Class tClass;
        SearchPattern tSearchPattern;
        try {
            tClassString = iJsonObject.getString("Class");
            tClass = Class.forName(tClassString);
            tSearchPattern = new SearchPattern(tClass);
            JSONArray tJsonArray ;
            tJsonArray = iJsonObject.getJSONArray("SearchCriteria");
            for (int i = 0; i < tJsonArray.length(); i++) {
                JSONObject lJsonObject = (JSONObject) tJsonArray.get(i);
                SearchCriteria tSearchCriteria = SearchCriteria.fromJson(lJsonObject);
                tSearchPattern.addSearch_Criteria(tSearchCriteria);
            }
            return tSearchPattern;
        } catch (JSONException | ClassNotFoundException e) {

            Logg.e(TAG, e.toString());
            return null;
        }
    }

    public SearchPattern addSearch_Criteria(SearchCriteria tSearchcriteria) {
        mAL_SearchCriteria.add(tSearchcriteria);
        return this;
    }

    @Override
    public int hashCode() {
        int tResult = mClass.hashCode();
        tResult += mAL_SearchCriteria.hashCode();
        int i = 1;
        for (SearchCriteria lSearchCriteria : mAL_SearchCriteria) {
            tResult += i * lSearchCriteria.hashCode();
            i++;
        }
        return tResult;
    }

    @Override
    public boolean equals( Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!obj.getClass().equals(SearchPattern.class)) {
            return false;
        }
        SearchPattern oSearchPattern = (SearchPattern) obj;
        if (!mClass.getSimpleName().equals(oSearchPattern.mClass.getSimpleName())) {
            return false;
        }
        ArrayList<SearchCriteria> oAR_SearchCriteria = oSearchPattern.mAL_SearchCriteria;
        if (mAL_SearchCriteria.size() == oAR_SearchCriteria.size()) {
            for (int i = 0; i < mAL_SearchCriteria.size(); i++) {
                if (! mAL_SearchCriteria.get(i).equals(oAR_SearchCriteria.get(i))) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }
}
