package org.pochette.data_library.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import junit.framework.TestCase;

import org.junit.Rule;
import org.junit.Test;
import org.pochette.data_library.database_management.Ldb_Helper;
import org.pochette.utils_lib.logg.Logg;

import java.util.Locale;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ServiceTestRule;

public class DataService_LdbTest extends TestCase {

    private final static String TAG = "FEHA (DataServiceTest)";


    @Rule
    public final ServiceTestRule mServiceRule = new ServiceTestRule();


    @Test
    public void test_CreateHelper() {
        destroyEverything();

        boolean tUnwantedException = false;

        try {
            Context tContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
            Ldb_Helper.createInstance(tContext);
        } catch(Exception e) {
            Logg.w(TAG, e.toString());
            tUnwantedException = true;
        }
        assertFalse("No Exception should have been thrown", tUnwantedException);
        String tMethodName = new Object() {        }.getClass().getEnclosingMethod().getName();
        produceReport();
        Logg.i(TAG, tMethodName + " finished");

    }

    @Test
    public void test_CreateDatabase() {
        boolean tUnwantedException;
        tUnwantedException = false;
        try {
            Context tContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
            Ldb_Helper.createInstance(tContext);
        } catch(Exception e) {
            Logg.w(TAG, e.toString());
            tUnwantedException = true;
        }
        assertFalse("No Exception should have been thrown", tUnwantedException);
        tUnwantedException = false;
        try {
            Ldb_Helper.getInstance().prepareTables();
        } catch(Exception e) {
            Logg.w(TAG, e.toString());
            tUnwantedException = true;
        }
        produceReport();
        String tMethodName = new Object() {
        }.getClass().getEnclosingMethod().getName();
        Logg.i(TAG, tMethodName + " finished");
    }

    @Test
    public void test_CloseDatabase() {
        boolean tUnwantedException;
        tUnwantedException = false;
        try {
            Context tContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
            Ldb_Helper.createInstance(tContext);
        } catch(Exception e) {
            Logg.w(TAG, e.toString());
            tUnwantedException = true;
        }
        assertFalse("No Exception should have been thrown", tUnwantedException);
        tUnwantedException = false;
        try {
            Ldb_Helper.getInstance().prepareTables();
        } catch(Exception e) {
            Logg.w(TAG, e.toString());
            tUnwantedException = true;
        }
        Ldb_Helper.getInstance().closeDB();

        produceReport();
        String tMethodName = new Object() {
        }.getClass().getEnclosingMethod().getName();
        Logg.i(TAG, tMethodName + " finished");
    }




    // internal

    public static void destroyEverything() {
        Ldb_Helper.destroyInstance();
        Logg.i(TAG, "Ldb Helper destroyed");
        Context tContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        tContext.deleteDatabase("Ldb");
        Logg.i(TAG, "Ldb database is deleted");
    }

    void produceReport() {
        Ldb_Helper tLdbHelper;
        SQLiteDatabase tSqLiteDatabase = null;
        Cursor tCursor;
        try {
            tLdbHelper = Ldb_Helper.getInstance();
        } catch(Exception e) {
            Logg.i(TAG, "LdbHelper not available");
            Logg.w(TAG, e.toString());
            return;
        }
        try {
            tSqLiteDatabase = Ldb_Helper.getInstance().getReadableDatabase();
        } catch(Exception e) {
            Logg.i(TAG, "Ldb Database not available");
            Logg.w(TAG, e.toString());
            return;
        }

        try {
            tCursor = tSqLiteDatabase.rawQuery("SELECT name AS TABLE_NAME FROM sqlite_master WHERE type='table'", null);
        } catch(Exception e) {
            Logg.i(TAG, "Could not create cursor");
            Logg.w(TAG, e.toString());
            return;
        }

        try {
            Logg.i(TAG, "Tables found: " + tCursor.getCount());
            int tColumnIndex = tCursor.getColumnIndex("TABLE_NAME");
            while (tCursor.moveToNext()) {
                String tTableName = tCursor.getString(tColumnIndex);
                String tText = String.format(Locale.ENGLISH,
                        "TABLE %s exist", tTableName);
                Logg.i(TAG, tText);
            }
        } catch(Exception e) {
            Logg.i(TAG, "problem with cursor");
            Logg.w(TAG, e.toString());
        }

    }


}