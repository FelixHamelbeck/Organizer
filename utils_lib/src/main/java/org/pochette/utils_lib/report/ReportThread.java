package org.pochette.utils_lib.report;


import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import org.pochette.utils_lib.logg.Logg;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


/**
 * ReportThread is a java thread to continually loop and read the next report from the ReportSystem<br>
 * Report Thread is a singleton: private constructor and public getInstance method
 * If a new report is to be shown, it is handed over to the MessageHandler (Activity)<br>
 * mReportHandler = new HandlerExtension(getActivity()); <br>
 * ReportSystem.getInstance().startThread(mReportHandler); <br>
 */
@SuppressWarnings({"unused", "NullableProblems"})
public class ReportThread extends HandlerThread {

    private static final String TAG = "FEHA (ReportThread)";

    //Variables
    private static ReportThread mInstance;
    private Handler mHandler;
    private boolean isRunning = false;
    private boolean isToBeKilled = false;
    private boolean isLogging = false;

    //Constructor

    /**
     * Constructor
     */
    private ReportThread() {
        super("ReportThread");
    }

    static ReportThread getInstance() {
        if (mInstance == null) { //Check for the first time
            synchronized (ReportThread.class) {
                if (mInstance == null) {
                    mInstance = new ReportThread();
                }
            }
        }
        return mInstance;
    }

    // Setter

    static void setLogging(boolean iIsLogging) {
        getInstance().isLogging = iIsLogging;
    }
    // interface


    public void start() {
        super.start();
        isRunning = true;
        isToBeKilled = false;

        Looper mLooper = this.getLooper();
        mHandler = new Handler(mLooper) {
            private Message tMsg;
            @Override
            public void handleMessage(Message iMsg) {
                tMsg = iMsg;
                Logg.d(TAG, iMsg.toString());
            }
        };
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Thread.currentThread().setName("ReportThread Runnable");
                Date started = Calendar.getInstance().getTime();
                Date now;
                float duration = 2.5f; // visibility time per report line in seconds
                Message msg;
                int tSleepMs = 100; // how often to check the clock in MS
                String text = "";
                while (!isToBeKilled) {
                    if (Thread.currentThread().isInterrupted()) {
                        break;
                    }
                    try {
                        //noinspection BusyWait
                        sleep(tSleepMs);
                        boolean tMessageWasSend = false;
                        if (isRunning) {
                            now = Calendar.getInstance().getTime();
                            long diffInMS = now.getTime() - started.getTime();
                            if (diffInMS > duration * 1000.f || text.isEmpty()) {
                                Report mReport = ReportSystem.getNext();
                                if (mReport != null &&
                                        mReport.mText != null &&
                                        !mReport.toString().equals(text)) {
                                    text = mReport.toString();
                                    started = Calendar.getInstance().getTime();
                                } else {
                                    text = "";
                                    started = Calendar.getInstance().getTime();
                                }
                                Bundle bundle = new Bundle();
                                bundle.putString("text", text);
                                msg = new Message();
                                msg.setData(bundle);
                                if (mHandler != null) {
                                    if (isLogging) {
                                        Log.i(TAG, "send Message:" + text);
                                    }
                                    mHandler.sendMessage(msg);
                                    tMessageWasSend = true;
                                }
                                int tNumberOpenReports = ReportSystem.getNumberOpenReports();
                                if (tNumberOpenReports > 100) {
                                    duration = 0.1f;
                                } else if (tNumberOpenReports < 10) {
                                    duration = 2.5f;
                                }
                            }
                            if (tMessageWasSend) {
                                tSleepMs = 100;
                            } else if (tSleepMs < 2000) {
                                tSleepMs *= 1.10f;
                            }
                        }
                    } catch(InterruptedException e) {
                        Log.i(TAG, String.format(Locale.ENGLISH, "Thread %s interrupted: Close down",
                                Thread.currentThread().getName()));
                    }
                }
                Logg.w(TAG, "ReportThread finieshed");
            }
        });
    }


    //Interface

    /**
     * kill the thread
     */
    public static void requestKill() {
        Log.d(TAG, "requestKill");
        mInstance.isToBeKilled = true;
    }

    /**
     * switch off, that the report action is performed per loop.
     * The thread is continuing
     */
    static void stopRunning() {
        getInstance().isRunning = false;
    }

    /**
     * switch on, that the report action is performed per loop.
     */
    static void startRunning() {
        getInstance().isRunning = true;
    }

    /**
     * @return whether action is performed per loop
     */
    static boolean isRunning() {
        return getInstance().isRunning;
    }

    static void setHandler(Handler tHandler) {
        getInstance().mHandler = tHandler;
    }

    static void startThread() {
        getInstance().start();
    }


    static void stopThread() {
        mInstance.isToBeKilled =true;
        getInstance().interrupt();
    }
}