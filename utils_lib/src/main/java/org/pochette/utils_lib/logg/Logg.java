package org.pochette.utils_lib.logg;


import android.util.Log;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * This class is a wrapper around the Log class: Stacktrace data is read and compared to the setting
 * defined in LoggControlArray.
 * <p>
 * If the Stacktrace data, the settings and the debuglevel fit, the corresponding Log.x method is called.
 */

@SuppressWarnings("unused")
public class Logg {

	private static final String TAG = "FEHA (Logg)";
	private static boolean DEBUG = true;


	/**
	 * Wrapper around Log.v
	 *
	 * @param tTag:    Tag of the Log call
	 * @param tMessage Mesaage for logging
	 */
	public static void v(String tTag, String tMessage) {
		if (!DEBUG) {
			return;
		}
		String tDebugLevel = "v";
		Box tBox = getCallerData();
		String tLevelOutput = getLevelOutput(tDebugLevel, tBox);
		if (tLevelOutput != null) {
			output2Log(tLevelOutput, tTag, tMessage);
			output2File(tLevelOutput, tTag, tMessage);
		}
	}

	/**
	 * Wrapper around Log.d
	 *
	 * @param tTag:    Tag of the Log call
	 * @param tMessage Mesaage for logging
	 */
	public static void d(String tTag, String tMessage) {
		if (!DEBUG) {
			return;
		}
		String tDebugLevel = "d";
		Box tBox = getCallerData();
		String tLevelOutput = getLevelOutput(tDebugLevel, tBox);
		if (tLevelOutput != null) {
			output2Log(tLevelOutput, tTag, tMessage);
			output2File(tLevelOutput, tTag, tMessage);
		}
	}

	/**
	 * Wrapper around Log.i
	 *
	 * @param tTag:    Tag of the Log call
	 * @param tMessage Mesaage for logging
	 */
	public static void i(String tTag, String tMessage) {
		String tDebugLevel = "i";
		Box tBox = getCallerData();

		String tLevelOutput = getLevelOutput(tDebugLevel, tBox);
		if (tLevelOutput != null) {
			output2Log(tLevelOutput, tTag, tMessage);
			output2File(tLevelOutput, tTag, tMessage);
		}
	}

	/**
	 * Wrapper around Log.w
	 *
	 * @param tTag:    Tag of the Log call
	 * @param tMessage Mesaage for logging
	 */
	public static void w(String tTag, String tMessage) {
		String tDebugLevel = "w";
		Box tBox = getCallerData();

		String tLevelOutput = getLevelOutput(tDebugLevel, tBox);
		if (tLevelOutput != null) {
			output2Log(tLevelOutput, tTag, tMessage);
			output2File(tLevelOutput, tTag, tMessage);
		}
	}

	/**
	 * Wrapper around Log.e
	 *
	 * @param tTag:    Tag of the Log call
	 * @param tMessage Mesaage for logging
	 */
	public static void e(String tTag, String tMessage) {
		String tDebugLevel = "e";
		Box tBox = getCallerData();
		String tLevelOutput = getLevelOutput(tDebugLevel, tBox);
		if (tLevelOutput != null) {
			output2Log(tLevelOutput, tTag, tMessage);
			output2Log(tLevelOutput, tTag, Log.getStackTraceString(new Exception()));
			output2File(tLevelOutput, tTag, tMessage);
			output2File(tLevelOutput, tTag, Log.getStackTraceString(new Exception()));
		}
	}

	/**
	 * Wrapper around Log.e
	 *
	 * @param tTag:    Tag of the Log call
	 * @param tMessage Message for logging
	 */
	public static void e(String tTag, String tMessage, Exception tException) {
		String tDebugLevel = "e";
		Box tBox = getCallerData();
		String tLevelOutput = getLevelOutput(tDebugLevel, tBox);
		if (tLevelOutput != null) {
			output2Log(tLevelOutput, tTag, tMessage);
			output2File(tLevelOutput, tTag, tMessage);
		}

	}


	/** Logg code for sql which should go a completly different way
	 *
	 * @param tTag for Logging
	 * @param tMessage for Logging
	 */
	public static void s(String tTag, String tMessage) {

		String tDebugLevel = "s";
		Box tBox = getCallerData();
		String tLevelOutput = getLevelOutput(tDebugLevel, tBox);
		if (tLevelOutput != null) {
			output2Log(tLevelOutput, tTag, tMessage);
			//output2SqlFile(tLevelOutput, tTag, tMessage);
		}

	}


	/**
	 * Artifical Outputlevel for Inputevents
	 * As the letter i is already used for info, k = keys is used, though all user events are treated like k, example touch, button, bluetooth.
	 *
	 * @param tTag     Tag of the Log call
	 * @param tMessage Message for Logging
	 */


	public static void k(String tTag, String tMessage) {
		String tDebugLevel = "k";
		Box tBox = getCallerData();
		String tLevelOutput = getLevelOutput(tDebugLevel, tBox);
		if (tLevelOutput != null) {
			output2Log(tLevelOutput, tTag, tMessage);
			output2File(tLevelOutput, tTag, tMessage);
		}

	}


	private static void output2Log(String tOutputLevel, String tTag, String tMessage) {
		if (tTag.length() > 23) {
			tTag = tTag.substring(0, 22);
		}
		switch (tOutputLevel) {
			case "v":
				Log.v(tTag, tMessage);
				break;
			case "d":
				Log.d(tTag, tMessage);
				break;
			case "s":
			case "i":
				Log.i(tTag, tMessage);
				break;
			case "w":
				Log.w(tTag, tMessage);
				break;
			case "e":
				Log.e(tTag, tMessage);
				break;
		}
	}


	private static void output2File(String tOutputLevel, String tTag, String tMessage) {
		LoggFile.addLine(tTag, tOutputLevel, tMessage, true);
	}

//	private static void output2SqlFile(String tOutputLevel, String tTag, String tMessage) {
//	//	LoggFile.addSQLLine(tTag, tOutputLevel, tMessage);
//	}



	/**
	 * Internal class to store PackageName, ClassName and MethodName
	 */
	private static class Box {
		String iPackageName;
		String iClassName;
		String iMethodName;
		Box(String tPackageName, String tClassName, String tMethodName) {
			iPackageName = tPackageName;
			iClassName = tClassName;
			if (iClassName.contains("$")) {
				iClassName = iClassName.substring(0, iClassName.indexOf("$"));
			}
			iMethodName = tMethodName;
		}
	}

	//Variables

	//Constructor

	//Setter and Getter

	/**
	 * Switch Debug Mode on and off: If Debug Mode is off only debug levels e, w and i are available
	 * No scope checking for any other scope
	 * @param iDebug true if all levels should be used
	 */
	public static void setDebug(boolean iDebug) {
		DEBUG = iDebug;
	}

	/**
	 * \* can be used as wildcard for the lookup
	 * @param iPackageName PackageName for lookup
	 * @param iClassName ClassName for lookup
	 * @param iMethodName MethodName for lookup
	 * @param iDebugLevelInput Debuglevel of the caller for lookup
	 * @param iDebugLevelOutput resulting debug level for the low level Log call, asterix allowed
	 * @param iRelevant true if this line is relevant
	 * @param iShow true if corresponding call should result in a low level Log call.
	 */
	public static void addControlLine(
			String iPackageName, String iClassName, String iMethodName,
			String iDebugLevelInput, String iDebugLevelOutput, boolean iRelevant, boolean iShow

	) {
		LoggControlLine tLoggControlLine = new LoggControlLine(iPackageName, iClassName, iMethodName,
				iDebugLevelInput, iDebugLevelOutput, iRelevant, iShow);

		LoggControlArray.getInstance().getArray().add(tLoggControlLine);

	}

	//Livecycle

	//Static Methods

	//Internal Organs

	/**
	 * Method to provide the Box information
	 * Stacktrace data is used
	 *
	 * @return Box
	 */
	private static Box getCallerData() {
		String tPackageName = "";
		String tClassname = "";
		String tMethodname = "";
		StackTraceElement[] tStackTraceElements = Thread.currentThread().getStackTrace();
		for (int i = 1; i < tStackTraceElements.length; i++) {
			StackTraceElement tStackTraceElement = tStackTraceElements[i];
			if (!tStackTraceElement.getClassName().equals(Logg.class.getName()) &&
					tStackTraceElement.getClassName().indexOf("java.lang.Thread") != 0) {
				String t = tStackTraceElement.getClassName();
				Pattern tPattern = Pattern.compile("\\$[0-9]");
				Matcher tMatcher = tPattern.matcher(t);
				if (tMatcher.find()) {
					t = t.substring(0, tMatcher.start());
				}
				String[] ts = t.split("\\.");
				tClassname = ts[ts.length - 1];
				tPackageName = ts[ts.length - 2];
				tMethodname = tStackTraceElement.getMethodName();
				break;
			}
		}
		return new Box(tPackageName, tClassname, tMethodname);
	}

	/** method to check the stacktrace data stored in the box to the ControlArray
	 *
	 * @param tDebugLevel the debug level provided from the caller
	 * @param tBox the stacktrace data
	 * @return the debug level for android.log; null if no debug output should be provided
	 */
	private static String getLevelOutput(String tDebugLevel, Box tBox) {
		ArrayList<LoggControlLine> tAR_LoggControlLine = LoggControlArray.getInstance().getArray();
		boolean tResult = false;
		String tOutputLevel = "";
		if (tAR_LoggControlLine == null || tAR_LoggControlLine.size() == 0) {
			tResult = true;
			tOutputLevel = tDebugLevel;
		} else {
			for (LoggControlLine tLoggControlLine : tAR_LoggControlLine) {
				if (!tLoggControlLine.mRelevant) {
					continue;
				}
				if (tLoggControlLine.mPackageName.equals(tBox.iPackageName) ||
						tLoggControlLine.mPackageName.equals("*")) {
					if (tLoggControlLine.mClassName.equals(tBox.iClassName) ||
							tLoggControlLine.mClassName.equals("*")) {
						if (tLoggControlLine.mMethodName.equals(tBox.iMethodName) ||
								tLoggControlLine.mMethodName.equals("*")) {
							if (tLoggControlLine.mDebugLevelInput.equals(tDebugLevel) ||
									tLoggControlLine.mDebugLevelInput.equals("*")) {
								tResult = tLoggControlLine.mShow;
								tOutputLevel = tLoggControlLine.mDebugLevelOutput;
								break;
							}
						}
					}
				}
			}
		}
		if (tResult) {

			switch (tOutputLevel) {
				case "*":
					if (tDebugLevel.equals("k")) {
						tOutputLevel = "i";
					} else {
						tOutputLevel = tDebugLevel;
					}
					break;
				case "k":
					tOutputLevel = "i";
					break;
				case "e":
				case "w":
				case "i":
				case "d":
				case "v":
					break;
				default:
					Log.e(TAG, "OutputLevel" + tOutputLevel + " not supported");
					break;
			}
			return tOutputLevel;
		} else {
			return null;
		}

	}
	//Interface

}
