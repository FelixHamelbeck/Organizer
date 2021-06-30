package org.pochette.data_library.database_management;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.pochette.utils_lib.logg.Logg;
import org.pochette.utils_lib.shouting.Shout;
import org.pochette.utils_lib.shouting.Shouting;

import java.util.Objects;

/**
 * Scddb_Helper is the central SQLiteOpenHelper for the scddb database, <br>
 * it does know about the Ldb and how to attach it
 * it provides initalisation etc
 * it does not yet know about Search and all those Scddb and Ldb Classes
 * <p>
 * All methods used before the database is fully operational all through static calls, so no getInstance will be called
 * static boolean checkStandAloneHealth()
 * static boolean checkAttachedHealth()
 * static void attach(Shouting iShouting)
 * static boolean detach(Context iContext)
 * static void startStandAlone(Shouting iShouting)
 * static void initializeLdb(Context iContext)
 * static SQLiteDatabase getReadableDatabaseForStaticMethod()
 * static void closeDB()
 * static void destroy()
 * <p>
 * There are only very few methods once the database is available
 * static Scddb_Helper getInstance(Context iContext)
 * static Scddb_Helper getInstance()
 * <p>
 * void report()
 * String toDataString(String tTable, int tId)
 * int getSize(String tTable)
 * Cursor getCursor(String tSql)
 * void closeCursor(Cursor tCursor)
 */

public class Scddb_Helper extends SQLiteOpenHelper {

    //Variables
    private static final String TAG = "FEHA (Scddb_Helper)";
    private static Scddb_Helper mInstance = null;
    private static final int DATABASE_VERSION = 1;

    // Constructor
    private Scddb_Helper(Context iContext) {
        super(iContext, Scddb_File.getInstance().getDatabaseFilePath(),
                null, DATABASE_VERSION);
        Logg.i(TAG, "Constructor looks for DB at" + Scddb_File.getInstance().getDatabaseFilePath());
    }

    /**
     * Initialize Instance from Context, usually with this call the instance is not uese
     *
     * @param iContext of the database
     * @return the instance of the scddb_helper
     */
    public static Scddb_Helper createInstance(Context iContext) {
        if (mInstance == null) {
            try {
                mInstance = new Scddb_Helper(iContext);
            } catch (RuntimeException e) {
                Logg.w(TAG, e.toString());
                mInstance = null;
                throw new RuntimeException("Scddb_Helper.createInstance failed");
            }
        }
        return mInstance;
    }

    /**
     * This getInstance is the usual one to be call, when the helper has already been initalized
     *
     * @return the instance of the scddb_helper
     */
    public static Scddb_Helper getInstance() {
        if (mInstance == null) {
            Logg.w(TAG, "First call of Scddb_Helper.getInstance() requires Context");
            Logg.e(TAG, Log.getStackTraceString(new Exception()));
        }
        return mInstance;
    }

    //Setter and Getter
    //Livecycle
    public void onCreate(SQLiteDatabase scddb) {
    }

    public void onUpgrade(SQLiteDatabase scddb, int OldVersion, int NewVersion) {
    }

    //Livecycle
    //Static Methods
    //Internal Organs
    private static void closeDB() {
        try {
            Scddb_Helper tScddb_Helper = Scddb_Helper.getInstance();
            if (tScddb_Helper == null) {
                return;
            }
            if (Objects.requireNonNull(tScddb_Helper.getWritableDatabase()).isOpen()) {
                Objects.requireNonNull(tScddb_Helper.getWritableDatabase()).close();
            }
            if (Objects.requireNonNull(tScddb_Helper.getReadableDatabase()).isOpen()) {
                Objects.requireNonNull(tScddb_Helper.getReadableDatabase()).close();
            }
        } catch (Exception e) {
            Logg.w(TAG, "While closeDB()");
            Logg.w(TAG, e.toString());
        }
    }

    private Cursor getLowLevelCursor(String tSql) {
        Cursor tCursor = null;
        try {
            tCursor = this.getReadableDatabase().rawQuery(tSql, null);
        } catch (Exception e) {
            Logg.w(TAG, "Cursor error");
            Logg.w(TAG, tSql);
            Logg.w(TAG, e.getClass().getName());
            Logg.e(TAG, e.toString());
        }
        return tCursor;
    }


    public String tableToString( String tableName) {
        String tableString = String.format("Table %s:\n", tableName);
        Cursor allRows  = this.getReadableDatabase().rawQuery("SELECT * FROM " + tableName, null);
        tableString += cursorToString(allRows);
        return tableString;
    }

    static String cursorToString(Cursor cursor){
        String cursorString = "";
        if (cursor.moveToFirst() ){
            String[] columnNames = cursor.getColumnNames();
            StringBuilder cursorStringBuilder = new StringBuilder();
            for (String name: columnNames) {
                cursorStringBuilder.append(String.format("%s ][ ", name));
            }
            cursorString = cursorStringBuilder.toString();
            cursorString += "\n";
            StringBuilder cursorStringBuilderValue = new StringBuilder(cursorString);
            do {
                for (String name: columnNames) {
                    cursorStringBuilderValue.append(String.format("%s ][ ",
                            cursor.getString(cursor.getColumnIndex(name))));
                }
                cursorStringBuilderValue.append("\n");
            } while (cursor.moveToNext());
            cursorString = cursorStringBuilderValue.toString();
        }
        return cursorString;
    }


    // Interface




    /**
     * attach,
     * this method is where the first getInstance call is done,
     * this method is the only method where getInstance(Context) is done
     * except for the static methods used for maintenance
     *
     * @param iShouting receiver of shout
     */
    public void attachLdbToScddb(Shouting iShouting) {

        String tLdbPath ="";
        try {
            // close everything with the scddb
            Scddb_Helper.closeDB();
            Objects.requireNonNull(Scddb_Helper.getInstance()).close();
            // close everything with the ldb
            Ldb_Helper.getInstance().closeDB();
            Ldb_Helper.getInstance().close();
            // get the path to Ldb
            tLdbPath = Ldb_Helper.getFilePath();
            Logg.i(TAG, "Attach uses Ldb located at " + tLdbPath);
            SQLiteDatabase tScddb = Objects.requireNonNull(Scddb_Helper.getInstance()).getWritableDatabase();

            tScddb.execSQL("attach database ? as ldb ", new String[]{tLdbPath,});
            Logg.i(TAG, "Ldb attached to Scddb");
        } catch (Exception e) {
            Logg.w(TAG, "Ldb could not attached to Scddb: "+ tLdbPath);
            throw new RuntimeException("could not attach Ldb to Scddb");
        }
        if (iShouting != null) {
            Shout tShout = new Shout("Scddb_Helper");
            tShout.mLastObject = "Database";
            tShout.mLastAction = "attached";
            iShouting.shoutUp(tShout);
        }
    }

    public void purge(String tTable) throws SQLiteException {
        try {
            Scddb_Helper tSccdb_helper = Scddb_Helper.getInstance();
            tSccdb_helper.getWritableDatabase().execSQL("delete from " + tTable);
        } catch(Exception e) {
            Logg.w(TAG, e.toString());
        }
    }

    @SuppressWarnings("rawtypes")
    public void dropAndCreate(Class iClass) throws SQLiteException {
        SqlContract tSqlContract = new SqlContract();
        String tCreateStatement = tSqlContract.getCreateString(iClass);
        if (tCreateStatement == null || tCreateStatement.isEmpty()) {
            Logg.w(TAG, "No create statement available for class" + iClass.getSimpleName());
        }
        String tTable = tSqlContract.getTableString(iClass);
        try {
            Scddb_Helper tSccdb_helper = Scddb_Helper.getInstance();
            tSccdb_helper.getWritableDatabase().execSQL("drop TABLE " + tTable);
            Logg.i(TAG, "dropped " + tTable);
            tCreateStatement= Objects.requireNonNull(tCreateStatement).replace("TABLE ", "TABLE LDB.");
            Logg.i(TAG,"Create statement:\n"+ tCreateStatement);
            tSccdb_helper.getWritableDatabase().execSQL(tCreateStatement);
        } catch(Exception e) {
            Logg.w(TAG, e.toString());
        }
    }


    /**
     * get the size of any Table in sccdb
     *
     * @param tTable Tablename
     * @return number of rows
     */
    public int getSize(String tTable) throws SQLiteException {
        Scddb_Helper sccdb_helper = Scddb_Helper.getInstance();
        String sql = "SELECT COUNT(*) AS C FROM " + tTable;
        int result = 0;
        Cursor cursor = null;
        //noinspection TryFinallyCanBeTryWithResources
        try {
            cursor = Objects.requireNonNull(sccdb_helper).getReadableDatabase().rawQuery(sql, null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                result = cursor.getInt(cursor.getColumnIndex("C"));
            }
        } catch (Exception e) {
            Logg.w(TAG, "Cursor error for getSize( " + tTable + ")");
            Logg.w(TAG, e.getClass().getName());
            Logg.e(TAG, e.toString());
            throw new SQLiteException(tTable + " not available");
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;
    }


    /** Write via Logg.d a list of tables
     *
     */
    public static void loggListOfTables() {
        Logg.d(TAG, "checkListOfTables");
        Cursor tCursor = null;
        try {
            tCursor = mInstance.getReadableDatabase().rawQuery(
                    "SELECT name AS TABLE_NAME FROM sqlite_master WHERE type='table'",
                    null);
            if (tCursor.moveToFirst()) {
                while (!tCursor.isAfterLast()) {
                    Logg.d(TAG, "Table " + tCursor.getString(tCursor.getColumnIndex("TABLE_NAME")));
                    tCursor.moveToNext();
                }
            }
        } catch (SQLiteException e) {
            Logg.e(TAG, e.toString());
        } finally {
            if (tCursor != null && !tCursor.isClosed()) {
                tCursor.close();
            }
        }
    }


    /**
     * performas rawQuery and returns the cursor
     *
     * @param tSql select statement to create the cursor
     * @return cursor with query results
     */
    public Cursor getCursor(String tSql) {
        return getLowLevelCursor(tSql);

    }

    /**
     * close the cursor
     *
     * @param tCursor to be closed
     */
    public void closeCursor(Cursor tCursor) {
        if (tCursor != null) {
            tCursor.close();
        }
    }
//

}