package org.pochette.organizer.app;

import android.app.Activity;
import android.app.ActivityManager;
import android.util.Log;

import org.pochette.utils_lib.logg.Logg;

import java.util.List;

class OrganizerThread extends Thread {

    private static final String TAG = "FEHA (OrganizerThread)";

    private final static int SLEEP_MIN = 50;
    private final static int SLEEP_MAX = 2000;

    OrganizerApp mOrganizerApp;

    // variables
    // constructor
    // setter and getter
    // lifecylce and override
    @Override
    public synchronized void start() {
        Logg.i(TAG, "start");
        init();
        super.start();
    }

    @Override
    public void run() {
        super.run();
        int tSleepMs = SLEEP_MIN;
        while (!isInterrupted()) {
            boolean tAnyAction;
            try {
                sleep(tSleepMs);
                Logg.d(TAG, "Time: "+ OrganizerStatus.getInstance().isDbAvailable());
                tAnyAction= mOrganizerApp.executeNextAction();
                if (tAnyAction) {
                    tSleepMs = SLEEP_MIN;
                } else {
                    tSleepMs =(int) ( (tSleepMs+10)* 1.05);
                    tSleepMs = Math.min(tSleepMs, SLEEP_MAX);
                }
            } catch(InterruptedException e) {
                Logg.w(TAG, e.toString());
            }
        }

    }
    // internal

//    void listService() {
//        ActivityManager am = (ActivityManager) mOrganizerApp.getSystemService(Activity.ACTIVITY_SERVICE);
//        List<ActivityManager.RunningServiceInfo> rs = am.getRunningServices(150);
//        String message = null;
//
//        Logg.w(TAG, "found " + rs.size());
//        for (int i = 0; i < rs.size(); i++) {
//            ActivityManager.RunningServiceInfo rsi = rs.get(i);
//            if (rsi.process.contains("huawei")) {
//                continue;
//            }
//
//            if (rsi.process.contains("google")) {
//                continue;
//            }
//            if (! rsi.process.contains("pochette")) {
//                continue;
//            }
//            Logg.i(TAG, "Process " + rsi.process + " with component " + rsi.service.getClassName());
//            message = message + rsi.process;
//        }
//    }

    void init() {

    }
    // public methods

}
