package org.pochette.utils_lib.logg;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.os.Environment;
import android.util.Log;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;


/**
 * class to store logging output in a file
 */
@SuppressWarnings("unused")
public class LoggFile {

	private static final String TAG = "FEHA (LoggFile)";

	private static LoggFile mInstance;

	private static String mDevice;
	private static String mLogDirectory;
	private static String mLogFilePath;
	@SuppressWarnings("FieldCanBeLocal")
	private static File mLogFile;
	private static SimpleDateFormat mFilenameSimpleDateFormat;
	private static SimpleDateFormat mLogDateFormat;
	private static boolean mFlag; // true if logging to File is up and working
//	private static LoggFile mInstance;



	//Variables

	//Constructor
	@SuppressWarnings("deprecation")
	private LoggFile(Application iApplication) {
        String state = Environment.getExternalStorageState();
// check whether we may currently save
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // We can read and write the media
         mFlag = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // We can only read the media
            mFlag = false;
        } else {
            // Something else is wrong. It may be one of many other states, but all we need
            //  to know is we can neither read nor write
            mFlag = false;
        }

		// continue the setup anyway, as the permission might be granted later
		PackageManager tPackageManager = iApplication.getPackageManager();

		try {
			PackageInfo tPackageInfo = tPackageManager.getPackageInfo(
					iApplication.getPackageName(), 0);
			mLogDirectory = tPackageInfo.applicationInfo.dataDir;
		} catch(PackageManager.NameNotFoundException e) {
			Logg.w(TAG, e.toString());
		}



		try {
			mDevice = "" + (Build.BOARD.length() % 10) + (Build.BRAND.length() % 10) +
					(Build.CPU_ABI.length() % 10) + (Build.DEVICE.length() % 10) +
					(Build.MANUFACTURER.length() % 10) + (Build.MODEL.length() % 10) +
					(Build.PRODUCT.length() % 10);
		} catch (Exception exception) {
			mDevice = "generic"; // some value
		}
		// the filename should show seconds but not milli
		mFilenameSimpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH);
		// the lines in the log file should show milli seconds
		mLogDateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss-SSS", Locale.ENGLISH);
		String mLogFilename = getFilename();
		mLogFilePath = mLogDirectory + "/" + mLogFilename;
		mLogFile = new File(mLogFilePath);
	//	Log.i(TAG, mLogFilePath);

		//Log.i(TAG, "SqlLog");
	//	mLogFilename = getSqlFilename();
		//Log.i(TAG, mLogFilename);
//		mSqlLogFilePath = mLogDirectory + "/" + mLogFilename;
//		mSqlLogFile = new File(mSqlLogFilePath);
//		//Log.i(TAG, mSqlLogFilePath);
	}


	/**
	 * Save reference to preferences in the singelation and call startLoggFile()
	 *
	 * @param iApplication from which to get the basic data for logging
	 */
	public static void initialize(Application iApplication) {
		if (mInstance == null) {
			//noinspection InstantiationOfUtilityClass
			mInstance = new LoggFile(iApplication);
			LoggFile.startLoggFile(iApplication);
		}
	}

	/**
	 * If the write permission is not granted at startup the startLoggFile can be called later
	 */
	public static void startLoggFile(Application iApplication) {
		if (mFlag) {
			File tDirectory = new File(mLogDirectory);
			boolean tResult = false;
			if (!tDirectory.exists()) {
				try {
					tResult = tDirectory.mkdirs();
				} catch (Exception e) {
					Log.e(TAG, e.toString());
				}
				if (tResult) {
					Log.i(TAG, "Directory created " + mLogDirectory);
				} else {
					Log.e(TAG, "Error creating directory" + mLogDirectory);
					mFlag = false;
				}
			}
			String mLogFilename = getFilename();
			//Log.i(TAG, mLogFilename);

			mLogFilePath = mLogDirectory + "/" + mLogFilename;
			mLogFile = new File(mLogFilePath);
		//	Log.w(TAG, mLogFilePath);
			boolean tLogFileNewFile;
			try {
				tLogFileNewFile = mLogFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				mFlag = false;
				Log.w(TAG, "Logging to File not available");
				return;
			}
			if (tLogFileNewFile) {
				generateFirstLines(iApplication);
				cleanupFiles();
			}
		}
	}



	//Setter and Getter

	public static String getLogDirectory() {
		return mLogDirectory;
	}

	public static String getLogFilePath() {
		return mLogFilePath;
	}


	//Livecycle

	//Static Methods

	//Internal Organs

	private static String getFilename() {
		String tFilename;
		String tDateandTime = mFilenameSimpleDateFormat.format(new Date());
		tFilename = String.format(Locale.ENGLISH, "pochette-%s-%s.txt", mDevice, tDateandTime);
		return tFilename;
	}

//	private static String getSqlFilename() {
//		String tFilename;
//		String tDateandTime = mFilenameSimpleDateFormat.format(new Date());
//		tFilename = String.format(Locale.ENGLISH, "pochette-%s-%s.sql.txt", mDevice, tDateandTime);
//		return tFilename;
//	}

	//Interface
	public static void addLine(String tTag, String tDebugLevel, String tMessage, boolean tForceFlush) {
		if (mFlag) {
			try {
				String tDateandTime = mLogDateFormat.format(new Date());
				String tText = String.format(Locale.ENGLISH,
						"%s\t%s\t%s\t%s", tDateandTime, tDebugLevel.toUpperCase(), tTag, tMessage);
				//Log.i(TAG, tText);
				BufferedWriter buf = new BufferedWriter(new FileWriter(mLogFile, true));
				buf.append(tText);
				buf.newLine();
				buf.close();
			} catch (IOException e) {
				Log.e(TAG, e.toString());
			}
		}
	}

//	public static void addSQLLine(String tTag, String tDebugLevel, String tMessage, boolean tForceFlush) {
//
//
//		if (mFlag) {
//			try {
//				String tDateandTime = mLogDateFormat.format(new Date());
//				String tText = String.format(Locale.ENGLISH,
//						"%s\t%s\t%s\t%s", tDateandTime, tDebugLevel.toUpperCase(), tTag, tMessage);
//				//Log.i(TAG, tText);
//				BufferedWriter buf = new BufferedWriter(new FileWriter(mSqlLogFile, true));
//				buf.append(tText);
//				buf.newLine();
//				buf.close();
//			} catch (IOException e) {
//				Log.e(TAG, e.toString());
//			}
//		}
//	}


	/**
	 * Write some basic information into the log file
	 *
	 * @param iApplication which data is to be logged
	 */

	private static void generateFirstLines(Application iApplication) {

		Logg.i(TAG, "New LogFile: " + mLogFilePath);
		Logg.i(TAG, "DEVICE:" + mDevice);
		Logg.i(TAG, "BUILD.FINGERPRINT:" + Build.FINGERPRINT);
		Logg.i(TAG, "BUILD_MANUFACTURE:" + Build.MANUFACTURER);
		Logg.i(TAG, "BUILD_MODEL:" + Build.MODEL);
		Logg.i(TAG, "BUILD_ID:" + Build.ID);
		Logg.i(TAG, "BUILD_DEVICE:" + Build.DEVICE);

		PackageManager packageManager = iApplication.getPackageManager();
		ApplicationInfo tApplicationInfo = null;
		try {
			tApplicationInfo = packageManager.getApplicationInfo(iApplication.getApplicationInfo().packageName, 0);
		} catch (final PackageManager.NameNotFoundException e) {
			Log.e(TAG, e.toString());
		}
		String tAppName;
		if (tApplicationInfo != null) {
			tAppName = (String) packageManager.getApplicationLabel(tApplicationInfo);

		} else {
			tAppName = "Unknown";
		}

		Resources tResources;
		tResources = iApplication.getResources();


		Logg.i(TAG, "DISPLAY_DENSITY:" + tResources.getDisplayMetrics().density);
		Logg.i(TAG, "DISPLAY_DENSITY_DPI:" + tResources.getDisplayMetrics().densityDpi);
		Logg.i(TAG, "DISPLAY_X_DPI:" + tResources.getDisplayMetrics().xdpi);
		Logg.i(TAG, "DISPLAY_Y_DPI:" + tResources.getDisplayMetrics().ydpi);
		Logg.i(TAG, "DISPLAY_WIDTH_PIXELS:" + tResources.getDisplayMetrics().widthPixels);
		Logg.i(TAG, "DISPLAY_HEIGHT_PIXELS:" + tResources.getDisplayMetrics().heightPixels);


		Logg.i(TAG, "VERSION_CODENAME:" + Build.VERSION.CODENAME);
		Logg.i(TAG, "VERSION_INCREMENTAL:" + Build.VERSION.INCREMENTAL);
		Logg.i(TAG, "VERSION_SDK:" + Build.VERSION.SDK_INT);



		Logg.i(TAG, "APP_NAME:" + tAppName);
		Logg.i(TAG, "PACKAGE_NAME:" + iApplication.getPackageName());




	}

	/**
	 * Delete files older than 3 days
	 */
	private static void cleanupFiles() {
		try {
			long tNow = new Date().getTime();
			File tDirectory = new File(mLogDirectory);
			File[] tDirectoryFiles = tDirectory.listFiles();
			// noinspection rawtypes
			Arrays.sort(tDirectoryFiles, new Comparator() {
				public int compare(Object o1, Object o2) {
					return Long.compare(((File) o1).lastModified(), ((File) o2).lastModified());
				}
			});
			Arrays.sort(tDirectoryFiles, new Comparator<File>() {
				public int compare(File f1, File f2) {
					return Long.compare(f1.lastModified(), f2.lastModified());
				}
			});
			if (tDirectoryFiles.length > 0) {
				int tCount = 0;
				for (File lFile : tDirectory.listFiles()) {
					long tDiff = tNow - lFile.lastModified();
					long tDays = tDiff / (60 * 60 * 1000);
					//Logg.i(TAG, lFile.toString() + " " + tDays);
					tCount++;
					//	if (tDays > 3 || tCount > 10 ) {
					if (tDays > 10) {
						boolean tDelete;
						if (!lFile.delete()) {
							Log.e(TAG, "Delete of log file failed");
						}
					}
				}
			}
		} catch (Exception e) {
			// Does not matter really, we might not have write permission
			Logg.i(TAG, e.toString());
		}
	}
}
