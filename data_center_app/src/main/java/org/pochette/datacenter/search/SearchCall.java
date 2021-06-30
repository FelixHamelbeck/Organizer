package org.pochette.data_library.search;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import org.pochette.utils_lib.logg.Logg;
import org.pochette.data_library.scddb_objects.Dance;
import org.pochette.data_library.database_management.Scddb_Helper;
import org.pochette.utils_lib.search.SearchPattern;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;

import androidx.annotation.NonNull;

/**
 * The class SearchCall provides methods to execute database searches in a standard way
 * <p>
 * The template procedure is as follows
 */
@SuppressWarnings({"rawtypes", "FieldMayBeFinal", "unchecked", "unused"})
public class SearchCall {
    private static final String TAG = "FEHA (SearchCall)";

    //Variables
    private Class mClass;
    private Refreshable mRefreshable; // interface to be called
    private SearchPattern mSearchPattern; // structure with search information
    private boolean mFirstOnly = false;

    //Constructor
    public SearchCall(Class iClass, SearchPattern iSearchPattern, Refreshable iRefreshable) {
        mClass = iClass;
        mSearchPattern = iSearchPattern;
        mRefreshable = iRefreshable;
    }

    @NonNull
    public String toString() {
        return String.format(Locale.ENGLISH,
                "%s: %s [%d]", TAG, mClass.getName(), mSearchPattern.getAL_SearchCriteria().size());
    }

    public <T> T produceFirst() {
        mFirstOnly = true;
        ArrayList<T> tAL = this.produceArrayList();
        if (tAL == null || tAL.size() < 1) {
            return null;
        }
        return tAL.get(0);
    }

    public <T> ArrayList<T> produceArrayList() {
        T tT;
        ArrayList<T> tAL = new ArrayList<>(0);
        // Start Cursor
        Cursor tCursor = createCursor();
        Method tMethod;
        if (tCursor == null ) {
            throw new RuntimeException("Cursor could not be created for class " + mClass.getSimpleName());
        }
        if ( tCursor.getCount() == 0) {
            return tAL;
        }
        try {
            //Class<?> tT_DB = Class.forName(mClassname_DB);
            tMethod = mClass.getMethod("convertCursor", Cursor.class);
            //   Logg.v(mHashString, "CUS " + tCursor.getCount());
            while (tCursor.moveToNext()) {
                try {
                    //noinspection unchecked
                    tT = (T) tMethod.invoke(mClass, tCursor);
                    tAL.add(tT);
                    if (mFirstOnly) {
                        //				Logg.d(TAG, "break");
                        break;
                    }
                } catch(InvocationTargetException e) {
                    Logg.i(TAG, "When invoking " + tMethod.toString());
                    Logg.w(TAG, e.toString());
                } catch(IllegalAccessException | IllegalArgumentException e) {

                    Logg.i(TAG, "produceArrayList()");
                    Logg.e(TAG, e.toString(), e);
                    Logg.e(TAG, String.format(Locale.ENGLISH, "%s\n%s", mClass.getName(),
                            this.mSearchPattern.toString()));
                    Logg.e(TAG, Log.getStackTraceString(new Exception()));

                }

            }
            //Logg.d(TAG, "SIZE "+tAL.size());
            if (mRefreshable != null) {
                mRefreshable.refresh();
            }
        } catch(Exception e) {
            Logg.i(TAG, "produceArrayList()");
            Logg.e(TAG, e.toString(), e);
            tAL = null;
        }
        if (tCursor != null && !tCursor.isClosed()) {
            tCursor.close();
        }


        return tAL;
    }


    public Cursor createCursor() {

        String tJoin="";
        String[] tColumns= new String[0];
        String tSelection="";
        String[] tSelectionArgs = new String[0];
        String tOrderBy;
        Cursor tCursor;
        try {
            SqlStringAuthor tSqlStringAuthor = new SqlStringAuthor(mSearchPattern);
            SQLiteDatabase tSqLiteDatabase = Scddb_Helper.getInstance().getReadableDatabase();



            tJoin = tSqlStringAuthor.getJoinString();
            tColumns = tSqlStringAuthor.getColumnArrayOfString();
            tSelection = tSqlStringAuthor.getSelect();
            tSelectionArgs = tSqlStringAuthor.getSelectArgs();
            tOrderBy = tSqlStringAuthor.getOrderBy();

//            Logg.i(TAG, tSelection);
//            for (String tString : tSelectionArgs) {
//                Logg.i(TAG, tString);
//            }
            tCursor = tSqLiteDatabase.query(tJoin, tColumns, tSelection, tSelectionArgs, null, null, tOrderBy);
            String tText = String.format(Locale.ENGLISH,
                    "query for %s found %d rows", mClass.getSimpleName(), tCursor.getCount()
            );

            Logg.v(TAG, tText );
        } catch(Exception e) {
            Logg.i(TAG, tJoin);
            Logg.i(TAG, tSelection);

            for (String tString : tColumns) {
                Logg.i(TAG, tString);
            }
            for (String tString : tSelectionArgs) {
                Logg.i(TAG, tString);
            }


            Logg.w(TAG, e.toString());
            return null;
        }
        return tCursor;
    }


    /**
     * get the current object
     *
     * @param iCursor the current position on the cursor is converted to object
     *                no move is performed
     */
    public <T> T getDataObject(Cursor iCursor) {
        T tT;
        Method tMethod;
        try {
//            Class<?> tT_DB = Class.forName(mClassname_DB);
            tMethod = mClass.getMethod("convertCursor", Cursor.class);
            try {
                tT = (T) tMethod.invoke(mClass, iCursor);
                return tT;
            } catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                Logg.i(TAG, "getDataObject: " + mSearchPattern.getSearchClass().getSimpleName());
                Logg.e(TAG, e.toString(), e);
                Logg.e(TAG, String.format(Locale.ENGLISH, "%s\n%s", mClass.getName(),
                        this.mSearchPattern.toString()));
                Logg.e(TAG, Log.getStackTraceString(new Exception()));
                return null;
            }
        } catch(
                NoSuchMethodException e) {
            Logg.i(TAG, "getDataObject: " + mSearchPattern.getSearchClass().getSimpleName());
            Logg.e(TAG, e.toString(), e);
            return null;
        } catch(
                Exception e) {
            Logg.i(TAG, "getDataObject");
            Logg.e(TAG, e.toString(), e);
            return null;
        }
    }


    public HashSet<Integer> produceHashSet() {

        HashSet<Integer> tHS = new HashSet<>(0);
        // Start Cursor
        Scddb_Helper scddb_helper = Scddb_Helper.getInstance();
        Cursor tCursor;
        tCursor = createCursor();
        if (tCursor == null) {
            Logg.i(TAG, "produceHashSet()");
            Logg.e(TAG, "Cursor == null");
            return null;
        }
        try {
            int tColumn4Id;

            if (mClass == Dance.class) {
                tColumn4Id = tCursor.getColumnIndex("D_ID");
            } else {
                throw new RuntimeException("produceHashSet only available for dance");
            }
            while (tCursor.moveToNext()) {
                tHS.add(tCursor.getInt(tColumn4Id));
            }
            if (mRefreshable != null) {
                mRefreshable.refresh();
            }

        } catch(Exception e) {
            Logg.i(TAG, "produceHashSet()");
            Logg.e(TAG, e.toString(), e);
            tHS = null;
        }
        //noinspection ConstantConditions
        if (tCursor != null && !tCursor.isClosed()) {
            tCursor.close();
        }
        return tHS;
    }


    public int produceCount() {
        int tResult;
        // Start Cursor
        Cursor tCursor;
        try {

            tCursor = createCursor();
            tResult = tCursor.getCount();
        } catch(SQLiteException e) {
            Logg.i(TAG, "SqlliteError");
            Logg.e(TAG, e.toString());
            return 0;
        } catch(Exception e) {
            Logg.i(TAG, "produceCount()");
            Logg.e(TAG, e.toString());
            return 0;
        }
        //noinspection ConstantConditions
        if (tCursor != null && !tCursor.isClosed()) {
            tCursor.close();
        }
        return tResult;
    }


    //Setter and Getter
    public void setSearch_Pattern(SearchPattern tSearchPattern) {
        mSearchPattern = tSearchPattern;
    }
    //Livecycle
    //Static Methods
    //Internal Organs


}
