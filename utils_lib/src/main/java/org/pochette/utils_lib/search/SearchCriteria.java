package org.pochette.utils_lib.search;

import org.json.JSONException;
import org.json.JSONObject;
import org.pochette.utils_lib.logg.Logg;


/**
 * The class SearchCriteria defines the lower level of the SearchPattern
 * One item represent one part of a where clause
 */
@SuppressWarnings("unused")
public class SearchCriteria {

    private final static  String TAG = "FEHA (Search_Criteria)";
    //Variables
    private String mMethod;
    private String mValue;

    //Constructor
    public SearchCriteria(String mMethod, String mValue) {
        this.mMethod = mMethod;
        this.mValue = mValue;
    }

    public SearchCriteria(String mMethod, int mValue) {
        this.mMethod = mMethod;
        this.mValue = ""+mValue;
    }

    public String getMethod() {
        return mMethod;
    }

    public void setMethod(String tMethod) {
        mMethod = tMethod;
    }

    public String getValue() {
        return mValue;
    }

    public void setValue(String tValue) {
        mValue = tValue;
    }

    //Livecycle
    //Static Methods
    //Internal Organs
    //Interface

    @Override
    public boolean equals( Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (! obj.getClass().equals(SearchCriteria.class)) {
            return false;
        }
        SearchCriteria oSearchCriteria = (SearchCriteria) obj;
        if (mMethod == null || oSearchCriteria.mMethod == null) {
            return false;
        }
        if (mMethod.equals(oSearchCriteria.mMethod)) {
            String oValue = oSearchCriteria.mValue;
            if (mValue == null && oValue == null) {
                return true;
            } else //noinspection ConstantConditions
                if (mValue == null && oValue != null) {
                return false;
            } else //noinspection ConstantConditions
                    if( mValue != null && oValue == null) {
                return false;
            } else //noinspection RedundantIfStatement
                        if (mValue.equals(oSearchCriteria.mValue)) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {
        int tMethod = mMethod.hashCode();
        int tValue;
        if (mValue != null) {
            tValue = mValue.hashCode();
        } else {
            tValue = 0;
        }
        //noinspection UnnecessaryLocalVariable
        int tHashCode = tMethod + tValue;
        return tHashCode;
    }

	public String toString() {
        return mMethod + " : " + mValue;
    }


    public JSONObject toJson() {
        JSONObject tJsonObject = new JSONObject();
        try {
            tJsonObject.put("Method", mMethod);
            tJsonObject.put("Value", mValue);
            return tJsonObject;
        } catch (JSONException e) {
            Logg.e(TAG, e.toString());
            return null;
        }
    }

    public static SearchCriteria fromJson(JSONObject iJsonObject) {
        String tMethod;
        String tValue;
        try {
            tMethod = iJsonObject.getString("Method");
            tValue = iJsonObject.getString("Value");
            return new SearchCriteria(tMethod, tValue);
        } catch (JSONException e) {
            Logg.e(TAG, e.toString());
            return null;
        }
    }

}
