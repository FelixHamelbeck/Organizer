package org.pochette.data_library.database;

import android.content.Intent;
import android.os.IBinder;

import junit.framework.TestCase;

import org.junit.Rule;
import org.junit.Test;
import org.pochette.data_library.music.MusicFile;
import org.pochette.data_library.search.DictionaryWhereMethod;
import org.pochette.data_library.scddb_objects.Crib;
import org.pochette.data_library.scddb_objects.Dance;
import org.pochette.data_library.search.SqlContract;
import org.pochette.data_library.service.DataService;
import org.pochette.data_library.service.DataServiceTest;
import org.pochette.utils_lib.logg.Logg;
import org.pochette.utils_lib.search.SearchCriteria;
import org.pochette.utils_lib.search.SearchPattern;

import java.util.concurrent.TimeoutException;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ServiceTestRule;

import static java.lang.Thread.sleep;

public class DataService_ReadTest extends TestCase {

    private final static String TAG = "FEHA (DataServiceTest)";
    DataService mDataService;

    @Rule
    public final ServiceTestRule mServiceRule = new ServiceTestRule();




    @Test
    public void test_Crib_ReadFirst() {
        prepService();

        SearchPattern tSearchPattern = new SearchPattern(Crib.class);
        Crib tCrib;
        tCrib = mDataService.readFirst(tSearchPattern);

        Logg.i(TAG, tCrib.toString());
        tSearchPattern.addSearch_Criteria(new SearchCriteria("ID", "11781"));
        tCrib = mDataService.readFirst(tSearchPattern);
        Logg.i(TAG, tCrib.toString());

    }

    @Test
    public void test_Dance_ReadFirst() {

        prepService();
        SearchPattern tSearchPattern = new SearchPattern(Dance.class);
        Dance tDance;
        tSearchPattern.addSearch_Criteria(new SearchCriteria("DANCENAME", "RANT"));
        tSearchPattern.addSearch_Criteria(new SearchCriteria("DANCENAME", "ROB"));
        tDance = mDataService.readFirst(tSearchPattern);
        if (tDance == null) {
            Logg.w(TAG, "Nothing returned");
        } else {
            Logg.i(TAG, tDance.toString());
        }


    }

    @Test
    public void test_Dance_ReadError() {
        prepService();
        SearchPattern tSearchPattern = new SearchPattern(Dance.class);
        boolean tExceptionThrown = false;
        Dance tDance = null;
        try {
            tSearchPattern.addSearch_Criteria(new SearchCriteria("DANCENAxME", "RANT"));
            tDance = mDataService.readFirst(tSearchPattern);
            if (tDance == null) {
                Logg.i(TAG, "not found");
            } else {
                Logg.i(TAG, tDance.toString());
            }
        } catch(Exception e) {
            Logg.i(TAG, e.toString());
            tExceptionThrown= true;
        }
        assertNull("no dance should have been found ", tDance);
        assertFalse("no Exception should have been thrown", tExceptionThrown);



    }

    @Test
    public void test_Read_AllWhere() {
        prepService();
        SearchPattern tSearchPattern = new SearchPattern(Dance.class);

        SqlContract tSqlContract = new SqlContract();
        for (DictionaryWhereMethod lDictionaryWhereMethod : tSqlContract.getAR_DictionaryWhereMethod()) {
            if (lDictionaryWhereMethod.mClass == MusicFile.class || 1 == 1 ) {

                Logg.w(TAG, "Start");
                Logg.i(TAG, lDictionaryWhereMethod.toString());
                testOneWhereMethod(lDictionaryWhereMethod);
            }
        }
    }

    @Test
    public void test_Read_AllConvertCursor() {
        prepService();
        SearchPattern tSearchPattern = new SearchPattern(Dance.class);

        Class tClass = null;
        SqlContract tSqlContract = new SqlContract();
        for (DictionaryWhereMethod lDictionaryWhereMethod : tSqlContract.getAR_DictionaryWhereMethod()) {
            if (tClass == null ||  lDictionaryWhereMethod.mClass != tClass ) {
                tClass = lDictionaryWhereMethod.mClass;
                Logg.w(TAG, "Start");
                Logg.i(TAG, tClass.toString());
                testOneConvertCursorMethod(tClass);
            }
        }
    }



    @Test
    public void prepService() {
        DataServiceTest.deleteDatabase();
        Scddb_FileTest.prepSSL();
        startService();
        mDataService.createScdbHelper(InstrumentationRegistry.getInstrumentation().getTargetContext());
        mDataService.attachLdbToScddb();
    }


    public void testOneWhereMethod(DictionaryWhereMethod iDictionaryWhereMethod) {
        SearchPattern tSearchPattern = new SearchPattern(iDictionaryWhereMethod.mClass);
        boolean tExceptionThrown = false;
        Object tObject;
        try {
            tSearchPattern.addSearch_Criteria(
                    new SearchCriteria(iDictionaryWhereMethod.mMethod, "3"));
            tObject = (Object) mDataService.readFirst(tSearchPattern);
            if (tObject == null) {
                Logg.i(TAG, "not found");
            } else {
                Logg.i(TAG, tObject.toString());
            }
        } catch(Exception e) {
            Logg.i(TAG, e.toString());
            tExceptionThrown= true;
        }

        if (tExceptionThrown) {
            Logg.i(TAG, iDictionaryWhereMethod.toString());
        }
        assertFalse("no Exception should have been thrown", tExceptionThrown);
    }


    public void testOneConvertCursorMethod(Class iClass) {
        SearchPattern tSearchPattern = new SearchPattern(iClass);
        boolean tExceptionThrown = false;
        Object tObject;
        try {

            tObject = (Object) mDataService.readFirst(tSearchPattern);
            if (tObject == null) {
                Logg.i(TAG, "not found");
            } else {
                Logg.i(TAG, tObject.toString());
            }
        } catch(Exception e) {
            Logg.i(TAG, e.toString());
            tExceptionThrown= true;
        }

        if (tExceptionThrown) {
            Logg.i(TAG, iClass.toString());
        }
        assertFalse("no Exception should have been thrown", tExceptionThrown);
    }






    // preparation

    void startService() {
        if (mDataService == null) {
            try {
                // Create the service Intent.
                Intent serviceIntent =
                        new Intent(InstrumentationRegistry.getInstrumentation().getTargetContext(), DataService.class);
                // Bind the service and grab a reference to the binder.
                IBinder binder = null;
                try {
                    binder = mServiceRule.bindService(serviceIntent);
                } catch(TimeoutException e) {
                    Logg.w(TAG, e.toString());
                }
                // Get the reference to the service, or you can call public methods on the binder directly.
                mDataService = ((DataService.LocalBinder) binder).getService();
            } catch(Exception e) {
                e.printStackTrace();
            }
        }

    }


}