package org.pochette.data_library.search;

import org.pochette.utils_lib.search.SearchCriteria;
import org.pochette.utils_lib.search.SearchPattern;

import java.util.ArrayList;
import java.util.Locale;

class SqlStringAuthor {

    private static final String TAG = "FEHA (SqlStringAuthor_s)";

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
        //Logg.i(TAG, "PrepSelection");
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
                String tSingleArg = tDictionaryWhereMethod.getSelectionArg(lSearchCriteria);
                mSelect = String.format(Locale.ENGLISH,
                        " %s AND %s ", mSelect, tSingleSelect);
                if (tSingleArg != null && !tSingleArg.isEmpty()) {
                    mAR_SelectionArg.add(tSingleArg);
                }
//                Logg.i(TAG, tSingleSelect);
//                if (tSingleArg == null) {
//                    Logg.i(TAG, "no arg");
//                } else {
//                    Logg.i(TAG, "arg:<"+tSingleArg+">");
//                }

            }
        }


    }

//    String getSingleSelect(Class iClass, SearchCriteria iSearchCriteria) {
//        return "  D.id = ? ";
//    }
//
//    String getSingleSelectArg(Class iClass, SearchCriteria iSearchCriteria) {
//        return "  376 ";
//    }


    // public methods


    public String getTableString() {
        String tTableString = "";
//        switch (mSearchPattern.getSearchClass().getSimpleName()) {
//            case "Crib":
//                tTableString = " DANCECRIB AS DC ";
//                break;
//            case "Dance":
//
//        }
        tTableString = mSqlContract.getTableString(mSearchPattern.getSearchClass());
        return tTableString;
    }

    public String getJoinString() {
        String tTableString = "";
        tTableString = mSqlContract.getJoinString(mSearchPattern.getSearchClass());
        return tTableString;
    }


    public String[] getColumnArrayOfString() {
        String[] tResult = null;
        String[] tStrings;
        tStrings = mSqlContract.getColumnArray(mSearchPattern.getSearchClass());

        if (1 == 1) {
            return tStrings;
        }

        switch (mSearchPattern.getSearchClass().getSimpleName()) {
            case "Crib":
                tStrings = new String[]{"ID AS _ID", "DANCE_ID AS DANCE_ID",
                        "TEXT AS SCDDBTEXT", "RELIABILITY AS RELIABILITY"};
                tResult = tStrings;
                break;
            case "Dance":
            default:
//                tStrings = new String[]{
//                        " D.ID AS D_ID", " D.NAME AS D_NAME", " DT.NAME AS D_TYPENAME", " DT.SHORT_NAME AS D_TYPESHORTNAME",
//                        " D.BARSPERREPEAT AS D_BARSPERREPEAT", " MT.DESCRIPTION AS D_MEDLEYTYPE", " S.SHORTNAME AS D_SHAPE",
//                        " C.NAME AS D_COUPLES", " P.NAME AS D_PROGRESSION," + " SDC.C AS D_COUNT_OF_CRIBS",
//                        " SDI.C AS D_COUNT_OF_DIAGRAMS", " SMF.C AS D_COUNT_OF_RECORDINGS",
//                        " SMP.COUNT_ANYGOOD AS D_COUNT_OF_ANYGOOD_RECORDINGS",
//                        " CASE WHEN SDPM.DANCE_ID IS NULL THEN 'N' else SDPM.RSCDS_YN END AS D_RSCDS_YN " ,
//                        " IFNULL(CL.FAVOURITE,'UNKN') AS D_DanceFavourite "};
//                tStrings = new String[]{
//                        " D.ID AS D_ID", " D.NAME AS D_NAME", " DT.NAME AS D_TYPENAME", " DT.SHORT_NAME AS D_TYPESHORTNAME",
//                        " D.BARSPERREPEAT AS D_BARSPERREPEAT", " MT.DESCRIPTION AS D_MEDLEYTYPE", " S.SHORTNAME AS D_SHAPE",
//                        " C.NAME AS D_COUPLES", " P.NAME AS D_PROGRESSION," + " SDC.C AS D_COUNT_OF_CRIBS",
//                        " CASE WHEN SDPM.DANCE_ID IS NULL THEN 'N' else SDPM.RSCDS_YN END AS D_RSCDS_YN "};

                tStrings = mSqlContract.getColumnArray(mSearchPattern.getSearchClass());

                tResult = tStrings;
                break;
        }
        return tResult;
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

        //    tStrings = ( String[] ) mAR_SelectionArg.toArray();
        return tStrings;
    }

    public String getOrderBy() {

        String tOrderByString = "";
        //noinspection SwitchStatementWithTooFewBranches
        switch (mSearchPattern.getSearchClass().getSimpleName()) {
            case "Crib":
                tOrderByString = null;
                break;
        }
        return tOrderByString;
    }


}
