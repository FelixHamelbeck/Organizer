package org.pochette.data_library.database_management;

import android.database.sqlite.SQLiteDatabase;

import org.pochette.data_library.BuildConfig;
import org.pochette.utils_lib.logg.Logg;

import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Objects;

/**
 * Class to deal with delete calls. Typical sequence for the caller is
 * new DeleteCall(Class, Object).delete()
 */
@SuppressWarnings({"rawtypes", "unchecked", "ConstantConditions"})
public class DeleteCall {

    private static final String TAG = "FEHA (DeleteCall)";

    // variables
    Class mClass;
    Object mObject;
    // constructor

    public DeleteCall( Class iClass, Object iObject) {
        boolean tIllegalArguments = false;
        if (iClass == null || iObject == null) {
            tIllegalArguments = true;
        }
        if (!Objects.requireNonNull(iClass).isInstance(iObject)) {
            tIllegalArguments = true;
        }
        if (tIllegalArguments) {
            throw new IllegalArgumentException("delete illegal arguments");
        }
        mClass = iClass;
        mObject = iObject;
    }

    // setter and getter
    // lifecylce and override
    // internal
    // public methods


    public int delete() {
        Logg.i(TAG, "delete "+ mClass.getSimpleName());
        try {
            SQLiteDatabase tSqLiteDatabase = Scddb_Helper.getInstance().getReadableDatabase();
            SqlContract tSqlContract = new SqlContract();
            String tTable = "LDB." + (tSqlContract.getTableString(mClass)).trim();

            String tWhereClause;
            tWhereClause = tSqlContract.getWhereIdString(mClass);
            Method tMethod;
            tMethod = mClass.getMethod("getId", (Class<?>[]) null);
            int tId;
            tId = (int) tMethod.invoke((mObject), (Object[]) null);
            String[] tWhereArgs={""+tId, };
            long tLong = tSqLiteDatabase.delete(tTable, tWhereClause, tWhereArgs);
            if (BuildConfig.DEBUG) {
                Logg.d(TAG, String.format(Locale.ENGLISH,
                        "Delete in Table %s of Row %d deleted %d rows",
                        mClass.getSimpleName(),tId,tLong));
            }
            return (int) tLong;
        } catch(Exception e) {
            Logg.e(TAG, e.toString());
            return 0;
        }
    }
}
