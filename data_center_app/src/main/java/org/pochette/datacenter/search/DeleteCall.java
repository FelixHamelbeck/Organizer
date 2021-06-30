package org.pochette.data_library.search;

import android.database.sqlite.SQLiteDatabase;

import org.pochette.data_library.database_management.Scddb_Helper;
import org.pochette.utils_lib.logg.Logg;

import java.lang.reflect.Method;

@SuppressWarnings({"rawtypes", "unchecked"})
public class DeleteCall {

    private static final String TAG = "FEHA (DeleteCall)";

    // variables
    Class mClass;
    Object mObject;
    // constructor

    public DeleteCall(Class iClass, Object iObject) {
        boolean tIllegalArguments = false;
        if (iClass == null || iObject == null) {
            tIllegalArguments = true;
        }
        if (!iClass.isInstance(iObject)) {
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


    public int delete() {
       // Logg.i(TAG, "delete");
        try {
            SQLiteDatabase tSqLiteDatabase = Scddb_Helper.getInstance().getReadableDatabase();
            SqlContract tSqlContract = new SqlContract();
            String tTable = "LDB." + (tSqlContract.getTableString(mClass)).trim();
         //   Logg.i(TAG, tTable);

            String tWhereClause;
            tWhereClause = tSqlContract.getWhereIdString(mClass);
            Method tMethod;
            tMethod = mClass.getMethod("getId", (Class<?>[]) null);
            int tId;
            tId = (int) tMethod.invoke((mObject), (Object[]) null);
            String[] tWhereArgs={""+tId, };


            long tLong = tSqLiteDatabase.delete(tTable, tWhereClause, tWhereArgs);
            //    Logg.i(TAG, "new ID:"+tLong);
            return (int) tLong;
        } catch(Exception e) {

            Logg.e(TAG, e.toString());
            return 0;
        }
    }

}
