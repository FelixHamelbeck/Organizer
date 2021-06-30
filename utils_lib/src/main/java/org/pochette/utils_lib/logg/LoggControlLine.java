package org.pochette.utils_lib.logg;



/**
 * POJO to store one line of logg control data
 * Packagename: Asterix as wildcard allowed
 * Classname: Asterix as wildcard allowed
 * MethodName: Asterix as wildcard allowed
 * DebugLevel: Asterix as wildcard allowed
 * Relevant (boolean): ignore this data line, if false
 * Show (boolean): Result, whether or not a corresponding debug call is passed through to Log.x
 */
class LoggControlLine {

	@SuppressWarnings("unused")
	private final String TAG = "FEHA (LoggControlLine)";

	//Variables
	String mPackageName;
	String mClassName;
	String mMethodName;

	String mDebugLevelInput;
	String mDebugLevelOutput;

	boolean mRelevant;
	boolean mShow;
	//Constructor

	LoggControlLine(String tPackageName, String tClassName, String tMethodName,
                    String tDebugLevelInput, String tDebugLevelOutput, boolean tRelevant, boolean tShow) {
		mPackageName =tPackageName;
		mClassName = tClassName;
		mMethodName = tMethodName;
		mDebugLevelInput = tDebugLevelInput;
		mDebugLevelOutput = tDebugLevelOutput;
		mRelevant = tRelevant;
		mShow = tShow;
	}

	//Setter and Getter
	//Livecycle
	//Static Methods
	//Internal Organs
	//Interface

	public String toString() {
		return mPackageName +" / " + mClassName + " / " +
				mMethodName + " [" + mDebugLevelInput + "] ->" +
				mShow ;
	}
}
