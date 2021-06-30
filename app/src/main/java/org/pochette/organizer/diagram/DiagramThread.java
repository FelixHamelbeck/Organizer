package org.pochette.organizer.diagram;

import org.pochette.organizer.app.OrganizerApp;
import org.pochette.organizer.app.OrganizerStatus;
import org.pochette.utils_lib.logg.Logg;

public class DiagramThread extends Thread {

    private static final String TAG = "FEHA (DiagramThread)";

    private int mSSleep;
    private int mSDownloadEvery;
    private int mMinSDownloadEvery;
    private int mMaxSDownloadEvery;

    private boolean mExecuteInNextLoop;

    // variables
    // constructor
    // setter and getter

    public void forceNow() {
        mExecuteInNextLoop = true;
        Logg.i(TAG, "forceNow");
    }


    // lifecylce and override

    @Override
    public synchronized void start() {
        Logg.w(TAG, "start");
        init();
        super.start();
    }

    @Override
    public void run() {
        super.run();
        DiagramManager tDiagramManager = new DiagramManager();
        int tMsLoop;
        tMsLoop = 0;
        while (!isInterrupted()) {
            try {
                //noinspection BusyWait
                sleep(mSSleep* 1000);
            } catch(InterruptedException e) {
                Logg.w(TAG, e.toString());
            }
            tMsLoop += mSSleep;
            if (tMsLoop >= mSDownloadEvery || mExecuteInNextLoop) {

                if (OrganizerStatus.getInstance().isDbAvailable()) {
                    Logg.i(TAG, "DiagramManager.downloadAbsentDiagrams");
                    tDiagramManager.downloadAbsentDiagrams();
                }
                if (mExecuteInNextLoop) {
                    mExecuteInNextLoop = false;
                }
                mSDownloadEvery *= 5f;
                if (mSDownloadEvery < 0) {
                    mSDownloadEvery = mMinSDownloadEvery;
                }
                if (mSDownloadEvery > mMaxSDownloadEvery) {
                    mSDownloadEvery = mMaxSDownloadEvery;
                }
                tMsLoop = 0;
            }
        }

    }

    // internal
    void init() {
        mExecuteInNextLoop = false;
        mExecuteInNextLoop = true;
        mMinSDownloadEvery = 1* 1*60; // 1 Minute
        mMaxSDownloadEvery = 3*60*60; // 3*hour
        mSSleep = 1; // seconds for sleep in the inner loop
        mSDownloadEvery = mMinSDownloadEvery;
    }
    // public methods

}
