package org.pochette.data_library.database_management;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.pochette.data_library.scddb_objects.Dance;
import org.pochette.utils_lib.logg.Logg;
import org.pochette.utils_lib.search.SearchCriteria;
import org.pochette.utils_lib.search.SearchPattern;
import org.pochette.utils_lib.shouting.Shout;
import org.pochette.utils_lib.shouting.Shouting;

import java.util.HashSet;
import java.util.TreeSet;

/**
 * for chained_search a smaller set of select features is required, so SearchCall does not need to run
 * the smaller search is this class SearchRetrieval
 * <p>
 * Only the TreeSet of Ids is needed.
 * Only on Select Criteria is allowed
 */
@SuppressWarnings("rawtypes")
public class SearchRetrieval {
    private static final String TAG = "FEHA (SearchRetrieval)";

    // variables
    HashSet<Integer> mHashSet;
    Class mObjectClass;
    String mMethod;
    String mValue;
    Shouting mShouting;

    // constructor
    public SearchRetrieval(Class iClass, String iMethod, String iValue, Shouting iShouting) {
        if (iClass != Dance.class) {
            throw new IllegalArgumentException("SearchRetrieval only implemented for Dance, but not " + iClass.getSimpleName());
        }
        mObjectClass = iClass;
        mMethod = iMethod;
        mValue = iValue;
        mShouting = iShouting;
        mHashSet = null;
    }
    // setter and getter
    // lifecylce and override
    // internal
    // public methods

    private Cursor createCursorForSingle() {
        SqlContract tSqlContract = new SqlContract();
        DictionaryWhereMethod tDictionaryWhereMethod;
        tDictionaryWhereMethod = tSqlContract.getWhereMethod(mObjectClass, mMethod);
        String tSql;
        tSql = tDictionaryWhereMethod.mIdSelect;

         if (tSql == null || tSql.isEmpty()) {
            // as a fallback
            Logg.w(TAG, "SearchRetrieval has to use SearchCall");
            SearchCriteria tSearchCriteria = new SearchCriteria(tDictionaryWhereMethod.mMethod, mValue);
            SearchPattern tSearchPattern = new SearchPattern(mObjectClass);
            tSearchPattern.addSearch_Criteria(tSearchCriteria);
            SearchCall tSearchCall = new SearchCall(mObjectClass, tSearchPattern, null);
            return tSearchCall.createCursor();
        } else {
            try {
                String[] tAR_SelectionArg;
                String tSingleArg = tDictionaryWhereMethod.getSelectionArg(mValue);
                if (tSingleArg == null) {
                    tAR_SelectionArg = null;
                } else {
                    tAR_SelectionArg = new String[1];
                    tAR_SelectionArg[0] = tSingleArg;
                }
                SQLiteDatabase tSqLiteDatabase = Scddb_Helper.getInstance().getReadableDatabase();
                Cursor tCursor;
                tCursor = tSqLiteDatabase.rawQuery(tSql, tAR_SelectionArg);
                Logg.i(TAG, "createCursorForSinge: cursor with count " + tCursor.getCount());
                return tCursor;
            } catch(Exception e) {
                Logg.i(TAG, tSql);
                Logg.w(TAG, e.toString());
                return null;
            }
        }
    }

    public static HashSet<Integer> getHS_All() {
        String tSql;
        tSql = "SELECT ID AS ID FROM DANCE";
        SQLiteDatabase tSqLiteDatabase = Scddb_Helper.getInstance().getReadableDatabase();
        Cursor tCursor;
        tCursor = tSqLiteDatabase.rawQuery(tSql, null);
        if(tCursor != null && tCursor.getCount() >0 ) {
            int tColumnOfId = tCursor.getColumnIndex("ID");
            HashSet<Integer> tHS = new HashSet<>();
            while (tCursor.moveToNext()) {
                tHS.add(tCursor.getInt(tColumnOfId));
            }
            return tHS;
        }
        throw new RuntimeException("Could not create HS_All");
    }


    public void execute() {
        Cursor tCursor = null;
        HashSet<Integer> tHS = new HashSet<>();
        try {
            new SqlContract();
            Logg.d(TAG, "create Cursor " + Thread.currentThread().toString());
            tCursor = createCursorForSingle();
            if (tCursor == null) {
                Logg.w(TAG, "Cursor  null ");
                return;
            }
            int tColumnOfId = tCursor.getColumnIndex("D_ID");
            while (tCursor.moveToNext()) {
                tHS.add(tCursor.getInt(tColumnOfId));
            }
            mHashSet = tHS;
        } catch(RuntimeException e) {
            Logg.w(TAG, e.toString());
            return;
        } finally {
            if (tCursor != null) {
                tCursor.close();
            }
        }
        if (mShouting != null) {
            Shout tShout = new Shout(this.getClass().getSimpleName());
            tShout.mLastAction = "finished";
            tShout.mLastObject = "DB_Retrieval";
            mShouting.shoutUp(tShout);
        }
    }

    public void stop() {
        mShouting = null;
    }

    public HashSet<Integer> getHashSet() {
        if (mHashSet != null) {
            return mHashSet;
        }
        throw new RuntimeException("DB retrieval not yet finished");
    }

    public TreeSet<Integer> getTreeSet() {
        if (mHashSet != null) {
            TreeSet<Integer> tTs = new TreeSet<>();
            for (Integer lInteger : mHashSet) {
                tTs.add(lInteger);
            }
            return tTs;
        }
        throw new RuntimeException("DB retrieval not yet finished");
    }


}
