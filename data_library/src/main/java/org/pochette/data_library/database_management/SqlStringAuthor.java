package org.pochette.data_library.database_management;

import org.pochette.utils_lib.search.SearchCriteria;
import org.pochette.utils_lib.search.SearchPattern;

import java.util.ArrayList;
import java.util.Locale;

class SqlStringAuthor {

    @SuppressWarnings("unused")
    private static final String TAG = "FEHA (SqlStringAuthor_D)";

    // variables
    SearchPattern mSearchPattern;
    String mSelect;
    ArrayList<String> mAR_SelectionArg;
    SqlContract mSqlContract;
    // constructor

    public SqlStringAuthor(SearchPattern searchPattern) {
        mSearchPattern = searchPattern;
        mAR_SelectionArg = new ArrayList<>(0);
        mSqlContract = new SqlContract();
        prepSelection();
    }

    // setter and getter
    // lifecylce and override
    // internal

    void prepSelection() {
        ArrayList<SearchCriteria> tAL = mSearchPattern.getAL_SearchCriteria();
        if (tAL == null || tAL.size() == 0) {
            mSelect = null;

        } else {
            mSelect = " 2 = 2 ";
            for (SearchCriteria lSearchCriteria : tAL) {
                DictionaryWhereMethod tDictionaryWhereMethod;
                tDictionaryWhereMethod = mSqlContract.getWhereMethod(
                        mSearchPattern.getSearchClass(),
                        lSearchCriteria.getMethod());
                String tSingleSelect = tDictionaryWhereMethod.getSelectionPart(lSearchCriteria);
                String tSingleArg = tDictionaryWhereMethod.getSelectionArg(lSearchCriteria.getValue());
                if (tDictionaryWhereMethod.mAtSign) {
                    tSingleSelect = tSingleSelect.replace("@", tSingleArg);
                    tSingleArg = "";
                }
                mSelect = String.format(Locale.ENGLISH,
                        " %s AND %s ", mSelect, tSingleSelect);
                if (tSingleArg != null && !tSingleArg.isEmpty()) {
                    mAR_SelectionArg.add(tSingleArg);
                }
            }
        }
    }

    // public methods
    public String getTableString() {
        String tTableString;
        tTableString = mSqlContract.getTableString(mSearchPattern.getSearchClass());
        return tTableString;
    }

    public String getJoinString() {
        String tTableString;
        tTableString = mSqlContract.getJoinString(mSearchPattern.getSearchClass());
        return tTableString;
    }

    public String[] getColumnArrayOfString() {
        String[] tStrings;
        tStrings = mSqlContract.getColumnArray(mSearchPattern.getSearchClass());
        return tStrings;
    }

    public String getSelect() {
        if (mSelect == null) {
            return "";
        }
        return mSelect;
    }

    public String[] getSelectArgs() {
        String[] tStrings = new String[mAR_SelectionArg.size()];
        tStrings = mAR_SelectionArg.toArray(tStrings);
        return tStrings;
    }

    public String getOrderBy() {
        String tOrderByString = "";
        if ("Crib".equals(mSearchPattern.getSearchClass().getSimpleName())) {
            tOrderByString = null;
        }
        return tOrderByString;
    }


}
