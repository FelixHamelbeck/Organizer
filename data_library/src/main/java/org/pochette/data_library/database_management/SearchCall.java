package org.pochette.data_library.database_management;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.ArraySet;
import android.util.Log;

import org.pochette.data_library.BuildConfig;
import org.pochette.data_library.scddb_objects.SlimDance;
import org.pochette.utils_lib.logg.Logg;
import org.pochette.utils_lib.search.SearchPattern;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

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
        Logg.d(TAG, "this="+mClass.getSimpleName()+ this.hashCode());
        Logg.i(TAG, "SearchCall: constructor for class " + iClass.getSimpleName());
        mSearchPattern = iSearchPattern;
        mRefreshable = iRefreshable;
    }

    @NonNull
    public String toString() {
        return String.format(Locale.ENGLISH,
                "%s: %s [%d]", TAG, mClass.getName(), mSearchPattern.getAL_SearchCriteria().size());
    }

    public <T> T produceFirst() {
        Logg.d(TAG, "this="+mClass.getSimpleName()+ this.hashCode());
        Logg.i(TAG, "Start produceFirst");
        mFirstOnly = true;
        ArrayList<T> tAL = this.produceArrayList();
        if (tAL == null || tAL.size() < 1) {
            return null;
        }
        Logg.d(TAG, "this="+mClass.getSimpleName()+ this.hashCode());
        Logg.i(TAG, "Finished produceFirst");
        return tAL.get(0);
    }


    public ArraySet<Integer> produceSet() {
        Logg.d(TAG, "this="+mClass.getSimpleName()+ this.hashCode());
        Logg.i(TAG, "Start produceArraySet");
        if (mClass != SlimDance.class) {
            throw new RuntimeException("Only implemented tfor SlimDance");
        }

        // Start Cursor
        Cursor tCursor = createCursor();
        Method tMethod;
        if (tCursor == null) {
            throw new RuntimeException("Cursor could not be created for class " + mClass.getSimpleName());
        }
        if (tCursor.getCount() == 0) {
            return new ArraySet<>(0);
        }
        ArraySet<Integer> tResult = new ArraySet<>(0);
        try {
            int tIndex = tCursor.getColumnIndex("D_ID");
            while (tCursor.moveToNext()) {
                tResult.add(tCursor.getInt(tIndex));
            }
        } catch(Exception e) {
            Logg.w(TAG, e.toString());
        }
        Logg.d(TAG, "this="+mClass.getSimpleName()+ this.hashCode());
        Logg.i(TAG, "Finished produceSet");
        return tResult;

    }

    public <T> ArrayList<T> produceArrayList() {

        Logg.d(TAG, "this="+mClass.getSimpleName()+ this.hashCode());
        Logg.i(TAG, "Start produceArrayList");
        T tT;
        ArrayList<T> tAL = new ArrayList<>(0);
        // Start Cursor
        Cursor tCursor = createCursor();
        Method tMethod;
        if (tCursor == null) {
            throw new RuntimeException("Cursor could not be created for class " + mClass.getSimpleName());
        }
        if (tCursor.getCount() == 0) {
            return tAL;
        }
        try {
            tMethod = mClass.getMethod("convertCursor", Cursor.class);
            while (tCursor.moveToNext()) {
                try {
                    tT = (T) tMethod.invoke(mClass, tCursor);
                    tAL.add(tT);
                    if (mFirstOnly) {
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
            if (mRefreshable != null) {
                mRefreshable.refresh();
            }
        } catch(Exception e) {
            Logg.i(TAG, "produceArrayList()");
            Logg.e(TAG, e.toString(), e);
            tAL = null;
        }
        if (!tCursor.isClosed()) {
            tCursor.close();
        }
        Logg.d(TAG, "this="+mClass.getSimpleName()+ this.hashCode());
        Logg.i(TAG, "Finished produceArrayList with size " + Objects.requireNonNull(tAL).size());
        return tAL;
    }


    public Cursor createCursor() {
        Logg.d(TAG, "this="+mClass.getSimpleName()+ this.hashCode());
        Logg.i(TAG, "Start createCursor");
        String tJoin = "";
        String[] tColumns = new String[0];
        String tSelection = "";
        String[] tSelectionArgs = new String[0];
        String tOrderBy;
        Cursor tCursor;
        try {
            SqlStringAuthor tSqlStringAuthor = new SqlStringAuthor(mSearchPattern);
            SQLiteDatabase tSqLiteDatabase = Scddb_Helper.getInstance().getReadableDatabase();

            if (BuildConfig.DEBUG) {
                Logg.d(TAG, "Got database");
            }
            tJoin = tSqlStringAuthor.getJoinString();
            tColumns = tSqlStringAuthor.getColumnArrayOfString();
            tSelection = tSqlStringAuthor.getSelect();
            tSelectionArgs = tSqlStringAuthor.getSelectArgs();
            tOrderBy = tSqlStringAuthor.getOrderBy();
            if (BuildConfig.DEBUG) {
                Logg.d(TAG, "Call db.query");
                Logg.d(TAG, tJoin);
                for (String tString : tColumns) {
                    Logg.d(TAG, "Column: " + tString);
                }
                Logg.d(TAG, "Selection:" + tSelection);
                for (String tString : tSelectionArgs) {
                    Logg.d(TAG, "SelectionArg: " + tString);
                }
                if (tOrderBy != null && !tOrderBy.isEmpty()) {
                    Logg.d(TAG, "OrderBy:" + tOrderBy);
                }
            }
            tCursor = tSqLiteDatabase.query(tJoin, tColumns, tSelection, tSelectionArgs, null, null, tOrderBy);
            String tText = String.format(Locale.ENGLISH,
                    "DB.Query for %s found %d rows", mClass.getSimpleName(), tCursor.getCount()
            );
//            if (mClass == MusicFile.class && tCursor.getCount() == 0) {
//                int i ;
//                Logg.i(TAG, tText);
//                Logg.i(TAG, mSearchPattern.toString());
//            }
            Logg.i(TAG, tText);
        } catch(Exception e) {
            Logg.i(TAG, tJoin);
            for (String tString : tColumns) {
                Logg.i(TAG, "Column: " + tString);
            }
            Logg.i(TAG, tSelection);
            for (String tString : tSelectionArgs) {
                Logg.i(TAG, "SelectionArg: " + tString);
            }
            Logg.w(TAG, e.toString());
            return null;
        }
        Logg.d(TAG, "this="+mClass.getSimpleName()+ this.hashCode());
        Logg.i(TAG, "Finished produceCursor " );
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

        Logg.d(TAG, "this="+mClass.getSimpleName()+ this.hashCode());
    //    Logg.i(TAG, "Start getDataObject: "+ mClass.getSimpleName());
        try {
            tMethod = mClass.getMethod("convertCursor", Cursor.class);
            try {
                tT = (T) tMethod.invoke(mClass, iCursor);
                Logg.d(TAG, "this="+mClass.getSimpleName()+ this.hashCode());
     //           Logg.i(TAG, "Finished getDataObject " + mClass.getSimpleName());
                return tT;
            } catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                Logg.i(TAG, "getDataObject: " + mSearchPattern.getSearchClass().getSimpleName());
                Logg.e(TAG, e.toString(), e);
                Logg.e(TAG, String.format(Locale.ENGLISH, "%s\n%s", mClass.getName(),
                        this.mSearchPattern.toString()));
                Logg.e(TAG, Log.getStackTraceString(new Exception()));
                return null;
            }
        } catch(NoSuchMethodException e) {
            Logg.i(TAG, "getDataObject: " + mSearchPattern.getSearchClass().getSimpleName());
            Logg.e(TAG, e.toString(), e);
            return null;
        } catch(Exception e) {
            Logg.i(TAG, "getDataObject"+ mClass.getSimpleName());
            Logg.e(TAG, e.toString(), e);
            return null;
        }
    }


    public int produceCount() {
        int tResult;

        Logg.d(TAG, "this="+mClass.getSimpleName()+ this.hashCode());
        Logg.i(TAG, "Start produceCount");
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
        if (!tCursor.isClosed()) {
            tCursor.close();
        }
        Logg.d(TAG, "this="+mClass.getSimpleName()+ this.hashCode());
        Logg.i(TAG, "Finished gproduceCount " );
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
