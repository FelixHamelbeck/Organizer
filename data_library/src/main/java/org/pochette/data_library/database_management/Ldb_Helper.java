package org.pochette.data_library.database_management;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.pochette.data_library.BuildConfig;
import org.pochette.utils_lib.logg.Logg;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;


/**
 * Ldb is the database with you local data.
 * Scddb is a local copy of the public database provided by strathspey.org
 * <p>
 * The usual access to ldb is through a select on Scddb, as ldb is attached to scddb
 * <p>
 * LdbHelper deals with all functionality required to setup and manage ldb, create, create tables ...
 * LdbHelper deals with all functionality required to setup and manage ldb, create, create tables ...
 */
@SuppressWarnings("rawtypes")
public class Ldb_Helper extends SQLiteOpenHelper {

    // Constants
    private static final String TAG = "FEHA (Ldb_Helper)";
    private static final String DATABASE_NAME = "Ldb";

    //Variables
    private static Ldb_Helper mInstance = null;
    private final File mDb_File; // only occasionally needed (copy, getPath ...), but stored as i need context to locate it

    //Constructor
    private Ldb_Helper(@NonNull Context iContext) {
        super(iContext, DATABASE_NAME, null, 1);
        Logg.i(TAG, "Ldb_Helper: Constructor. DatabaseName " + DATABASE_NAME);
        mDb_File = iContext.getDatabasePath(DATABASE_NAME);
    }

    public static void createInstance(Context iContext) {
        //Double check locking pattern
        if (mInstance == null) { //Check for the first time
            synchronized (Ldb_Helper.class) {   //Check for the second time.
                if (mInstance == null) {
                    mInstance = new Ldb_Helper(iContext);
                }
            }
        }
        mInstance.getWritableDatabase();
    }

    public static void destroyInstance() {
        Logg.i(TAG, "destroyInstance: Start");
        if (mInstance != null) {
            try {
                mInstance.close();
            } catch(Exception e) {
                Logg.w(TAG, e.toString());
            }
            mInstance = null;
        }
    }

    public static Ldb_Helper getInstance() {
        if (mInstance == null) {
            throw new RuntimeException("First call to Ldb_Helper.getInstance requires context");
        }
        return mInstance;
    }

    //Livecycle
    @Override
    public void onCreate(SQLiteDatabase iSqLiteDatabase) {
        Logg.i(TAG, "OnCreate");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        Logg.i(TAG, "OnUpgrade");
        Logg.w(TAG, "not implemented");
    }

    /**
     * Close the standalone Ldb, this is needed before it can be attached to the Scddb
     */
    public void closeDB() {
        try {
            Ldb_Helper tLdb_helper = Ldb_Helper.getInstance();
            if (tLdb_helper == null) {
                return;
            }
            if (Objects.requireNonNull(tLdb_helper.getWritableDatabase()).isOpen()) {
                Objects.requireNonNull(tLdb_helper.getWritableDatabase()).close();
            }
            if (Objects.requireNonNull(tLdb_helper.getReadableDatabase()).isOpen()) {
                Objects.requireNonNull(tLdb_helper.getReadableDatabase()).close();
            }
        } catch(Exception e) {
            Logg.i(TAG, "While closeDB()");
            Logg.w(TAG, e.toString());
        }
    }

    public boolean isLdbAvailable() {
        Cursor tCursor = null;
        SQLiteDatabase tSqLiteDatabase;
        int tCountTables;
        try {
            tSqLiteDatabase = this.getWritableDatabase();
            tCursor = tSqLiteDatabase.rawQuery(
                    "SELECT COUNT(*) AS C FROM sqlite_master WHERE type='table'", null);
            int tColumnIndex = tCursor.getColumnIndex("C");

            tCursor.moveToFirst();
            tCountTables = tCursor.getInt(tColumnIndex);
            if (BuildConfig.DEBUG) {
                Logg.d(TAG, format(ENGLISH, "In %s found %d tables, at least 5", "isLdbAvailable", tCountTables));
            }
            return tCountTables > 5;
        } catch(Exception e) {
            Logg.i(TAG, "Could not create cursor");
            Logg.w(TAG, e.toString());
            return false;
        } finally {
            if (tCursor != null) {
                tCursor.close();
            }
        }


    }


    public void prepareTables() {
        Logg.i(TAG, "prepareTables");
        SQLiteDatabase tSQLiteDatabase = Ldb_Helper.getInstance().getWritableDatabase();
        SqlContract tSqlContract = new SqlContract();
        ArrayList<Class> tAR_LdbClass;
        tAR_LdbClass = tSqlContract.getAR_LdbClass();
        String tCreateString = "";
        for (Class lClass : tAR_LdbClass) {
            try {
                String tTableName = tSqlContract.getTableString(lClass);
                boolean tTableExist = false;
                Cursor tCursor = null;
                try {
                    String tSqlString = "select COUNT(*) AS COUNT FROM " + tTableName;
                    tCursor = tSQLiteDatabase.rawQuery(tSqlString, null);
                    tCursor.moveToFirst();
                    tCursor.getInt(tCursor.getColumnIndex("COUNT"));
                    tTableExist = true;
                } catch(Exception e) {
                    Logg.i(TAG, "prepareTables: Table does not yet exist "+ tTableName);
                } finally {
                    if (tCursor != null) {
                        tCursor.close();
                    }
                }
                if (!tTableExist) {
                    tCreateString = tSqlContract.getCreateString(lClass);
                    tSQLiteDatabase.execSQL(tCreateString);
                    Logg.i(TAG, "Table " + lClass.getSimpleName() + " created");
                }
            } catch(SQLException e) {
                Logg.w(TAG, "Table " + lClass.getSimpleName() + " not created");
                Logg.i(TAG, tCreateString);
                Logg.w(TAG, e.toString());
                return;
            }
        }


    }

    public static String getFilePath() {
        if (mInstance != null) {
            return mInstance.getReadableDatabase().getPath();
        }
        throw new RuntimeException("FilePath not available");
    }

    public File getLdbFile() {
        return mDb_File;
    }

}
