package org.pochette.data_library.database_management;


import android.annotation.SuppressLint;
import android.content.Context;

import org.pochette.utils_lib.logg.Logg;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.security.InvalidParameterException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LdbBackup {

    private static final String TAG = "FEHA (LdbBackup)";
    @SuppressLint("StaticFieldLeak")
    private static LdbBackup mInstance;
    private Context mContext;

    private LdbBackup(Context iContext) {
        mContext = iContext;
    }

    public static void createInstance(Context iContext) {
        if (iContext == null) {
            throw new InvalidParameterException("Context must be provided");
        }
        //Double check locking pattern
        if (mInstance == null) { //Check for the first time
            synchronized (LdbBackup.class) {   //Check for the second time.
                if (mInstance == null) {
                    mInstance = new LdbBackup(iContext);
                }
            }
        }
    }

    public static void destroyInstance() {
        mInstance.mContext = null;
        mInstance = null;
    }

    public static LdbBackup getInstance() {
        if (mInstance == null) { //Check for the first time
            synchronized (LdbBackup.class) {   //Check for the second time.
                if (mInstance == null) {
                    throw new RuntimeException("First call to LdbBackup " +
                            "should be createInstance with Context");
                }
            }
        }
        return mInstance;
    }


    // variables
    // constructor
    // setter and getter
    // lifecylce and override
    // internal

    public Ldb_Helper getLdb_Helper() {
        Ldb_Helper tLdb_Helper;
        try {
            tLdb_Helper = Ldb_Helper.getInstance();
            if (tLdb_Helper == null) {
                Ldb_Helper.createInstance(mContext);
                tLdb_Helper = Ldb_Helper.getInstance();
            }
        } catch(Exception e) {
            Ldb_Helper.createInstance(mContext);
            tLdb_Helper = Ldb_Helper.getInstance();
        }
        return tLdb_Helper;
    }

    // public methods
    public void copy(File tSource, File tTarget) {
        try {
            FileInputStream inStream = new FileInputStream(tSource);
            FileOutputStream outStream = new FileOutputStream(tTarget);
            FileChannel inChannel = inStream.getChannel();
            FileChannel outChannel = outStream.getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);
            inStream.close();
            outStream.close();
            Logg.i(TAG, "copy done to " + tTarget);
        } catch(IOException e) {
            Logg.w(TAG, e.toString());
        }
    }

    public File getDirectory() {
        Ldb_Helper tLdb_Helper = this.getLdb_Helper();
        File tLdb_File = tLdb_Helper.getLdbFile();
        return tLdb_File.getParentFile();
    }

    public void load(File iBackupFile) {
        Ldb_Helper tLdb_Helper = this.getLdb_Helper();
        File tLdb_File = tLdb_Helper.getLdbFile();
        Ldb_Helper.destroyInstance();
        copy(iBackupFile,tLdb_File);
        Logg.i(TAG, "load done from  " + iBackupFile.getName());
    }



    public void backup() {
        Ldb_Helper tLdb_Helper = this.getLdb_Helper();
        if (tLdb_Helper == null) {
            Logg.w(TAG, "Cannot perform backup, as LdbHelper cannot be created");
            return;
        }
        File tLdb_File = tLdb_Helper.getLdbFile();
        String tBackupFilename =
                "Ldb." +
                        new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date()) +
                        ".db";
        File tBackupFile = new File(tLdb_File.getParent() + "/" + tBackupFilename);
        copy(tLdb_File,tBackupFile);
        Logg.i(TAG, "backup done to "+ tBackupFile.getAbsolutePath());
    }

}
