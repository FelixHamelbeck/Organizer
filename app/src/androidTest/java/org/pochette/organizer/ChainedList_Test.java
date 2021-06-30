package org.pochette.organizer;

import junit.framework.TestCase;

import org.junit.Rule;
import org.pochette.data_library.scddb_objects.Dance;

import androidx.test.rule.ServiceTestRule;

import static java.lang.Thread.sleep;

public class ChainedList_Test extends TestCase {

    private final static String TAG = "FEHA (DataServiceTest)";

    Class mClass = Dance.class;



    @Rule
    public final ServiceTestRule mServiceRule = new ServiceTestRule();








}