package org.pochette.organizer.app;

import android.app.Activity;
import android.app.ActivityManager;

import org.pochette.utils_lib.logg.Logg;

import java.util.List;

class OrganizerThread extends Thread {

    private static final String TAG = "FEHA (OrganizerThread)";

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
        while (!isInterrupted()) {
            try {
                sleep(5000);
                mOrganizerApp.executeNextAction();
            } catch(InterruptedException e) {
                Logg.w(TAG, e.toString());
            }
        }

    }
    // internal

    void listService() {
        ActivityManager am = (ActivityManager) mOrganizerApp.getSystemService(Activity.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> rs = am.getRunningServices(150);
        String message = null;

        Logg.w(TAG, "found " + rs.size());
        for (int i = 0; i < rs.size(); i++) {
            ActivityManager.RunningServiceInfo rsi = rs.get(i);
            if (rsi.process.contains("huawei")) {
                continue;
            }

            if (rsi.process.contains("google")) {
                continue;
            }
            if (! rsi.process.contains("pochette")) {
                continue;
            }
            Logg.i(TAG, "Process " + rsi.process + " with component " + rsi.service.getClassName());
            message = message + rsi.process;
        }
    }

    void init() {

    }
    // public methods

}
