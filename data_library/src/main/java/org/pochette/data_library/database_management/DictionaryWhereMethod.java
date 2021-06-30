package org.pochette.data_library.database_management;


import org.pochette.utils_lib.search.SearchCriteria;

/**
 * In SqlContract a multiple set of possibilities for where statements are defined.
 * The class DictionaryWhereMethod is the POJO class used to describe these where statement
 */
@SuppressWarnings({"rawtypes", "unused"})
public class DictionaryWhereMethod {

    private static final String TAG = "FEHA (DictionaryWhereMethod)";

    // variables
    public Class mClass;
    public String mMethod;
    String mDescription;
    String mSqlSelect; // this is used to build Selection in DB.query()
    boolean mQuestionMark; // mQuestionMark is a question mark part of mSqlSelect, set to true, so the search value is converted and added to Selectionargs
    boolean mAtSign; //if true replace the @ in the sql with the value
    boolean mFlagEscape; // the string is run through  tInput = android.database.DatabaseUtils.sqlEscapeString(tInput);
    boolean mFlagWildcard; // SqlWildCard % is added at start and end of string
    boolean mFlagTrim; // whitespace is removed from start and end of string
    boolean mFlagLower; // string is converted to lower case
    String mIdSelect;

    // constructor
    public DictionaryWhereMethod(Class iClass, String iMethod,
                                 String iDescription,
                                 String iSqlSelect,
                                 boolean iFlagQuestionMark,
                                 boolean iAtSign,
                                 boolean iFlagEscape,
                                 boolean iFlagWildcard,
                                 boolean iFlagTrim,
                                 boolean iFlagLower) {
        mClass = iClass;
        mMethod = iMethod;
        mDescription = iDescription;
        mSqlSelect = iSqlSelect;
        mQuestionMark = iFlagQuestionMark;
        mAtSign = iAtSign;
        mFlagEscape = iFlagEscape;
        mFlagWildcard = iFlagWildcard;
        mFlagTrim = iFlagTrim;
        mFlagLower = iFlagLower;
    }

    public DictionaryWhereMethod(Class iClass, String iMethod,
                                 String iDescription,
                                 String iSqlSelect,
                                 boolean iFlagQuestionMark,
                                 boolean iAtSign,
                                 boolean iFlagEscape,
                                 boolean iFlagWildcard,
                                 boolean iFlagTrim,
                                 boolean iFlagLower,
                                 String iIdSelect) {
        mClass = iClass;
        mMethod = iMethod;
        mDescription = iDescription;
        mSqlSelect = iSqlSelect;
        mQuestionMark = iFlagQuestionMark;

        mAtSign = iAtSign;
        mFlagEscape = iFlagEscape;
        mFlagWildcard = iFlagWildcard;
        mFlagTrim = iFlagTrim;
        mFlagLower = iFlagLower;
        mIdSelect = iIdSelect;
    }

    // setter and getter
    // lifecylce and override
    // internal
    // public methods

    public String getSelectionPart(SearchCriteria iSearchCriteria) {
        return mSqlSelect;
    }

    //public String getSelectionArg(SearchCriteria iSearchCriteria) {
    public String getSelectionArg(String iValue) {
        if (mQuestionMark) {
            //  String tInput = iSearchCriteria.getValue();
            String tInput;
            if (mAtSign) {
                tInput = iValue;
            } else {
                tInput = iValue;
                if (mFlagEscape) {
                    tInput = android.database.DatabaseUtils.sqlEscapeString(tInput);
                }
                if (mFlagTrim) {
                    tInput = tInput.trim();
                }
                if (mFlagLower) {
                    tInput = tInput.toLowerCase();
                }
                if (mFlagWildcard) {
                    tInput = "%" + tInput + "%";
                }
            }
            return tInput;
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        return "DictionaryWhereMethod{" +
                "mClass=" + mClass.getSimpleName() +
                ", mMethod='" + mMethod;
    }
}
