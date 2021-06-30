package org.pochette.data_library.search;


import org.pochette.utils_lib.search.SearchCriteria;

@SuppressWarnings({"rawtypes", "unused"})
public class DictionaryWhereMethod {

    private static final String TAG = "FEHA (DictionaryWhereMethod)";

    // variables
    public Class mClass;
    public String mMethod;
    String mDescription;
    String mFormat;
    boolean mQuestionMark;
    boolean mFlagEscape;
    boolean mFlagWildcard;
    boolean mFlagTrim;
    boolean mFlagLower;

    // constructor
    public DictionaryWhereMethod(Class iClass, String iMethod,
                                 String iDescription,
                                 String iFormat,
                                 boolean iFlagQuestionMark,
                                 boolean iFlagEscape,
                                 boolean iFlagWildcard,
                                 boolean iFlagTrim, boolean iFlagLower) {
        mClass = iClass;
        mMethod = iMethod;
        mDescription = iDescription;
        mFormat = iFormat;
        mQuestionMark = iFlagQuestionMark;
        mFlagEscape = iFlagEscape;
        mFlagWildcard = iFlagWildcard;
        mFlagTrim = iFlagTrim;
        mFlagLower = iFlagLower;

      // Ldb_Helper.getInstance().getWritableDatabase().query
    }

    // setter and getter
    // lifecylce and override
    // internal
    // public methods

    public String getSelectionPart(SearchCriteria iSearchCriteria) {
        return mFormat;

    }

    public String getSelectionArg(SearchCriteria iSearchCriteria) {
        if (mQuestionMark) {
            String tInput = iSearchCriteria.getValue();
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
                tInput = "%"+tInput+"%";
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
                ", mMethod='" + mMethod ;
    }
}
