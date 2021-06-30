package org.pochette.data_library.database_management;

import android.content.ContentValues;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import org.pochette.utils_lib.logg.Logg;

import java.lang.reflect.Method;
import java.util.Arrays;

public class WriteCall {

    private static final String TAG = "FEHA (WriteCall)";

    // variables
    Class mClass;
    Object mObject;
    // constructor
    public WriteCall( Class iClass, Object iObject) {
        Logg.i(TAG, "WriteCall for Class"+iClass.getSimpleName());
        boolean tIllegalArguments = false;
        if (iObject == null) {
            tIllegalArguments = true;
        }
        if (! iClass.isInstance(iObject)) {
            tIllegalArguments = true;
        }
        if (tIllegalArguments) {
            throw new IllegalArgumentException("write illegal arguments");
        }
        mClass = iClass;
        mObject = iObject;
    }

    // setter and getter
    // lifecylce and override
    // internal
    // public methods

    public int insert() {
        try {
            SQLiteDatabase tSqLiteDatabase = Scddb_Helper.getInstance().getReadableDatabase();
            SqlContract tSqlContract = new SqlContract();
            String tTable = "LDB." + (tSqlContract.getTableString(mClass)).trim();
            Method tMethod;
            tMethod = mClass.getMethod("getContentValues", (Class<?>[]) null);
            ContentValues tContentValues = (ContentValues) tMethod.invoke ( ( mObject),(Object[]) null);
            long tLong = tSqLiteDatabase.insertOrThrow(tTable, null, tContentValues);
            return (int) tLong;
        } catch(SQLiteConstraintException tSQLiteConstraintException) {
            // when insert fail due to duplicate key, call update
            // in order to return the id a select is required first
            Logg.i(TAG, tSQLiteConstraintException.toString());
            throw tSQLiteConstraintException;
        } catch(Exception e) {
            Logg.e(TAG, e.toString());
            return 0;
        }
    }

    // all LDB tables must have a id column
    public void update() {
        String tTable;
        Method tMethod;
        ContentValues tContentValues;
        String  tWhereClause;
        int tId;
        String[] tWhereArgs;
        try {
            SQLiteDatabase tSqLiteDatabase = Scddb_Helper.getInstance().getReadableDatabase();
            SqlContract tSqlContract = new SqlContract();
            tTable = "LDB." + (tSqlContract.getTableString(mClass)).trim();
            tMethod = mClass.getMethod("getContentValues", (Class<?>[]) null);
            tContentValues = (ContentValues) tMethod.invoke ( ( mObject),(Object[]) null);
            tWhereClause = tSqlContract.getWhereIdString(mClass);
            tMethod = mClass.getMethod("getId", (Class<?>[]) null);
            tId = (int) tMethod.invoke((mObject), (Object[]) null);
            tWhereArgs = new String[]{""+tId, };
            long tLong = tSqLiteDatabase.update(tTable, tContentValues, tWhereClause,tWhereArgs);
            if (tLong != 1) {
                Logg.i(TAG, "update in "+tTable);
                Logg.i(TAG, Arrays.toString(tWhereArgs));
                throw new RuntimeException("update works line by line");
            }
        } catch(Exception e) {
            Logg.e(TAG, e.toString());
        }
    }
}
