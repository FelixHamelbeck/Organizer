package org.pochette.utils_lib.logg;


import java.util.ArrayList;

/**
 * This class is a singleton pojo for LoggControlLines.
 * the setting are provided by calling Logg.addControlLine(..)

 */

public class LoggControlArray {
    @SuppressWarnings("unused")
    private static final String TAG = "FEHA (LoggControlArray)";
    private static LoggControlArray mInstance;
    private final ArrayList<LoggControlLine> mAR_LoggControlLine;

    static LoggControlArray getInstance() {
        if (mInstance == null)
            mInstance = new LoggControlArray();
        return mInstance;
    }

    private LoggControlArray() {
                mAR_LoggControlLine = new ArrayList<>(0);
    }

    /**
     * This method provides the LoggControlArray, only call should be in Logg.filter
     *
     * @return the LoggControlArray
     */
    ArrayList<LoggControlLine> getArray() {
        return this.mAR_LoggControlLine;
    }


    //Variables
    //Constructor
    //Setter and Getter
    //Livecycle
    //Static Methods
    //Internal Organs
    //Interface


}



