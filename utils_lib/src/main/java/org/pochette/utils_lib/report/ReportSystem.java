package org.pochette.utils_lib.report;

import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;


/**
 * ReportSystem is a singleton to process Reports
 * <p>
 * <p>
 * Reports are kept in memory rather than DB<br>
 * ReportSystem receives Reports through the method receive<br>
 * ReportSystem provides the ReportThread with the next Report through getNext<br>
 * </p>
 */
@SuppressWarnings("unused")
public class ReportSystem {

    //Variables
	@SuppressWarnings("FieldCanBeLocal")
    private static final String TAG = "FEHA (ReportSystem)";
    private static ReportSystem mInstance;

    //private ReportThread mReportThread;
    /**
     * The position in the array list mAL_Report of the next report to be send.
     */
    private int mCurrentPosition;
    /**
     * The array where the reports are kept. After sending the report to the handler,
     * the message is kept.
     */
    private ArrayList<Report> mAL_Reports;
    private final Object mLock;
    private Calendar mCalendar;
    private boolean mLoggingFlag = false;

    //Constructor + Singleton
    private ReportSystem() {
        mAL_Reports = new ArrayList<>(0);
        mCurrentPosition = 0;
        mCalendar = Calendar.getInstance();
        mLock = new Object();
        ReportThread.startThread();
        ReportThread.setLogging(mLoggingFlag);
    }

    /**
     * ReportSystem is a Singleton
     *
     * @return the instance of the ReportSystem
     */
    public static ReportSystem getInstance() {
        if (mInstance == null) { //Check for the first time
            synchronized (ReportSystem.class) {
                if (mInstance == null) {
                    mInstance = new ReportSystem();
                }
            }
        }
        return mInstance;
    }


    //Setter and Getter
    //Livecycle

    //Static Methods
    public void startThread(Handler tHandler) {
        //<editor-fold desc="Report System Start">
        String tThreadName = "ReportThread";
       // mReportThread = ReportThread.getInstance();
        if (!ReportThread.isRunning()) {
            ReportThread.startThread();
        }
        ReportThread.setHandler(tHandler);
        //</editor-fold>
    }

    //Internal Organs
    private Report getNextSync() {
        synchronized (mLock) {
            if (mAL_Reports.size() == 0) {
                return null;
            }
            int tMaxPosition = mAL_Reports.size() - 1;
            if (mCurrentPosition < 0) {
                return null;
            }
            if (mCurrentPosition > tMaxPosition) {
                return null;
            }
            Report tReport = mAL_Reports.get(mCurrentPosition);
            tReport.mShownDate = mCalendar.getTime();
            mCurrentPosition++;
            if (mCurrentPosition > tMaxPosition+1) {
                mCurrentPosition = tMaxPosition+1;
            }
            return tReport;
        }
    }

    //Interface

    /**
     * @return the number of open reports
     */
    public static int getNumberOpenReports() {
        synchronized (getInstance().mLock) {
            if (getInstance().mAL_Reports == null) {
                return 0;
            }
            return getInstance().mAL_Reports.size() - 1 - getInstance().mCurrentPosition;
        }
    }

	/**
	 *
	 * @param iText the message to be reported
	 */
	public static void receive(String iText) {
    	String tType = "";
		ReportSystem.receive(iText, tType);
	}

   	/**
	 * subsequent messages of the same type are suppressed, if not yet reported.
	 * The most recent message of the same type is kept. Earlier messages, not yet reported are discarded
	 * @param iText the message to be reported
	 * @param iType for checking
	 */
	public static void receive(String iText, String iType) {
        String tType;
        if (iText == null || iText.isEmpty()) {
            return;
        }
        // for the very fist call
        if (mInstance == null) {
            getInstance();
        }
        if (iType == null || iType.isEmpty()) {
            tType = "";
        } else {
            tType = iType;
        }

        synchronized (mInstance.mLock) {
			Report tReport;

			int tComparePosition = mInstance.mAL_Reports.size()-1;
			if (mInstance.mAL_Reports.size() > 0 &&
					tComparePosition >= 0 ) {
				String sType = mInstance.mAL_Reports.get(tComparePosition).mType;
				// if the getNext Report and the new report are of the same type (and not empty),
				// than remove the next item and store the new.
				if (sType != null && !sType.isEmpty()) {
					if (tType.equals(sType)) {
                        if (mInstance.mLoggingFlag) {
                            Log.i(TAG, "Type s t " + sType + " " + tType);
                            Log.i(TAG, "remove current: " + tComparePosition);
                        }
						mInstance.mAL_Reports.remove(tComparePosition);
					}
				}
			}
			// the new one is added anyway
			String[] lines = iText.split("\\r?\\n");
			for (String line : lines) {
				tReport = new Report(line, iType);
				if (tReport != null) {
                    mInstance.mAL_Reports.add(tReport);
                }
			}

        }
    }

    /**
     * retrieve the next report to be displayed and shift currentPosition by one
     *
     * @return the report to be shown
     */
    public static Report getNext() {
        return getInstance().getNextSync();
    }

    /**
     * destroy the report system
     * kill the ReportThread and set singleton to null
     */
    public static void destroy() {

        ReportThread.requestKill();
        if (mInstance != null ) {
            try {

                getInstance().mAL_Reports = null;
                getInstance().mCalendar = null;
            } catch (Exception e) {
                Log.i(TAG, "destroy error");
                Log.e(TAG, e.toString());
            }
        }
        mInstance = null;
    }

    public static void setLogging(boolean iLoggingFlag) {
        getInstance().mLoggingFlag = iLoggingFlag;
        ReportThread.setLogging( iLoggingFlag);
    }

}
