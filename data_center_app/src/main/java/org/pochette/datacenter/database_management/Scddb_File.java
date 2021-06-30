package org.pochette.data_library.database_management;


import org.pochette.utils_lib.logg.Logg;
import org.pochette.utils_lib.shouting.Shout;
import org.pochette.utils_lib.shouting.Shouting;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Date;
import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;


@SuppressWarnings({"TryFinallyCanBeTryWithResources", "unused", "FieldCanBeLocal"})
public class Scddb_File {

    private static final String TAG = "FEHA (Scddb_File)";

    private final static String SCDDB_FILENAME = "scddata-2.0.db";
    private final static String SCDDB_URL_STRING = "https://media.strathspey.org/scddata/scddata-2.0.db";
    // variables
    private static Scddb_File mInstance;
    private Date mLastWebDate;
    private boolean mFileAvailable;
    private final String mDatabaseDirectoryPath;
    private final String mDatabaseFilePath;
    private final String mTmpDatabaseFilePath;

    private final File mDatabaseFile;
    private final File mTmpDatabaseFile;

    // constructor

    private Scddb_File() {
        // We need one database to know where to store the external
        File tLdb_File = Ldb_Helper.getInstance().getLdbFile();
        if (tLdb_File == null) {
            throw new RuntimeException("No Local DB installed yet");
        }
        File tDb_Directory = tLdb_File.getParentFile();
        mDatabaseDirectoryPath = Objects.requireNonNull(tDb_Directory).getAbsolutePath();
        if (mDatabaseDirectoryPath.isEmpty()) {
            throw new RuntimeException("Could not find a path to store the private copy of Scddb");
        }
        mDatabaseFilePath = mDatabaseDirectoryPath + "/" + SCDDB_FILENAME;
        mTmpDatabaseFilePath = mDatabaseDirectoryPath + "/tmp_" + SCDDB_FILENAME;
        mDatabaseFile = new File(mDatabaseFilePath);
        mTmpDatabaseFile = new File(mTmpDatabaseFilePath);
        Logg.i(TAG, mTmpDatabaseFilePath);
        Logg.i(TAG, mDatabaseFilePath);

        getLastWebdate(); // this starts a thread to store the web date locally, so we have it cached

    }

//    public static void createInstance() {
//        //Double check locking pattern
//        if (mInstance == null) { //Check for the first time
//            synchronized (Scddb_File.class) {   //Check for the second time.
//                if (mInstance == null) {
//                    mInstance = new Scddb_File();
//                }
//            }
//        }
//    }

    public static Scddb_File getInstance() {
        if (mInstance == null) { //Check for the first time
            synchronized (Scddb_File.class) {   //Check for the second time.
                if (mInstance == null) {
                    mInstance = new Scddb_File();
                }
            }
        }
        return mInstance;
    }


    // setter and getter
    // lifecylce and override
    // internal

    private void readFromWeb() {
        int tKbyte = 0;
        BufferedInputStream tBufferedInputStream;
        FileOutputStream tOutputStream;

        byte[] buffer = new byte[1024];
        int tRead;
        try {
            URL tUrl;
            tUrl = new URL(SCDDB_URL_STRING);
            HttpsURLConnection urlConnection =
                    (HttpsURLConnection) tUrl.openConnection();
            InputStream tSslInputStream = urlConnection.getInputStream();
            tBufferedInputStream = new BufferedInputStream(tSslInputStream);
            tOutputStream = new FileOutputStream(mTmpDatabaseFile);
            while ((tRead = tBufferedInputStream.read(buffer)) != -1) {
                tOutputStream.write(buffer, 0, tRead);
                tKbyte++;
            }
            tBufferedInputStream.close();
            tSslInputStream.close();
            tOutputStream.flush();
            tOutputStream.close();
        } catch(Exception e) {
            Logg.w(TAG, e.toString());
            Logg.w(TAG, "download failed");
            return;
        }

        // copy tmp file to main location
        if (mTmpDatabaseFile.isFile() && mTmpDatabaseFile.length() > 10 * 1000) {
            try {
                InputStream tFileInputStream = new FileInputStream(mTmpDatabaseFile);
                try {
                    OutputStream tFileOutputStream = new FileOutputStream(mDatabaseFile);
                    try {
                        // Transfer bytes from in to out
                        byte[] buf = new byte[1024];
                        int len;
                        while ((len = tFileInputStream.read(buf)) > 0) {
                            tFileOutputStream.write(buf, 0, len);
                        }
                    } finally {
                        tFileOutputStream.close();
                    }
                } finally {
                    tFileInputStream.close();
                }
            } catch(Exception e) {
                Logg.e(TAG, e.toString());
            }

            if (mDatabaseFile.isFile()) {
                Logg.i(TAG, "Database size: " + mDatabaseFile.length() / 1024);
                Logg.i(TAG, "Database location " + mDatabaseFile.getAbsolutePath());
            } else {
                Logg.w(TAG, "Download failed");
            }
        } else {
            Logg.w(TAG, "No temp copy available, continue with old file");
        }

    }

    private void getDateFromWeb() {
        //Logg.i(TAG, "getDateFromWeb");
        BufferedInputStream tBufferedInputStream;

        byte[] buffer = new byte[1024];
        int tRead;
        try {
            URL tUrl;
            tUrl = new URL(SCDDB_URL_STRING);
            HttpsURLConnection urlConnection =
                    (HttpsURLConnection) tUrl.openConnection();
            InputStream tSslInputStream = urlConnection.getInputStream();
            tBufferedInputStream = new BufferedInputStream(tSslInputStream);
            mLastWebDate = new Date(urlConnection.getLastModified());
            tBufferedInputStream.close();
            tSslInputStream.close();
        } catch(Exception e) {
            Logg.w(TAG, e.toString());
            Logg.w(TAG, "get WebDate failed");
            return;
        }

        Logg.i(TAG, "gotDateFromWeb"+ mLastWebDate);
    }


    // public methods

    /**
     * Copy the whole database file
     * <p>
     * thread is used
     */
    public void copyFromWeb(Shouting iShouting) {
        //noinspection Convert2Lambda
        Thread tThread = new Thread(new Runnable() {
            @Override
            public void run() {
                readFromWeb();
                if (iShouting != null) {
                    Shout tShout = new Shout("Scddb_File");
                    tShout.mLastObject = "ScddbWebFile";
                    tShout.mLastAction = "downloaded";
                    iShouting.shoutUp(tShout);
                }
            }
        });
        tThread.setName("copyFromWeb");
        tThread.start();
    }

    /**
     *
     * @return true when a private copy of Scddb exists and is ready for database use
     */
    public boolean isDbFileAvailable() {
        if (mDatabaseFilePath == null || mDatabaseFilePath.isEmpty()) {
            Logg.i(TAG, "Called exist before the path information is set");
            return false;
        }
        File tFile = new File(mDatabaseFilePath);
        if (!tFile.exists()) {
            Logg.i(TAG, "File does not exist");
            return false;
        }
        long tSize = tFile.length()/1024;
        if (tSize < 10000) {
            Logg.i(TAG, "File is too small");
            return false;
        }
        return true;
    }

    /**
     * delete the database file ; main purpose is for testing, where we want to start without a file
     */
    public void delete() {
        if (mDatabaseFilePath == null || mDatabaseFilePath.isEmpty()) {
            Logg.i(TAG, "Called delete before the path information is set");
           return;
        }
        File tFile = new File(mDatabaseFilePath);
        //noinspection ResultOfMethodCallIgnored
        tFile.delete();

    }

    public String getDatabaseFilePath() {
        return mDatabaseFilePath;
    }

    /**
     * This method return the LastModified date of the local copy of Scddb
     * This method is also called via SettingControl, so do not delete
     *
     * @return File date of the local copy of Scddb
     */
    public Date getLocalScddbDate() {
        if (mDatabaseFile == null) {
            return null;
        }
        return new Date(Objects.requireNonNull(mDatabaseFile).lastModified());
    }
    /**
     * This call returns the stored value of the webdate. A process to get an update on the webdate
     * is started asyncronously
     * @return the last webdate received from the web as currently known.
     */
    public Date getLastWebdate() {
        Logg.d(TAG, "getLastWebdate called");
        //noinspection Convert2Lambda,Anonymous2MethodRef
        Thread tThread = new Thread(new Runnable() {
            @Override
            public void run() {
                getDateFromWeb();
            }
        });
        tThread.setName("getDateFromWeb");
        tThread.start();
        return mLastWebDate;
    }


}
