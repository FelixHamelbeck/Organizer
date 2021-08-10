package org.pochette.data_library.music;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Looper;
import android.provider.MediaStore;

import org.pochette.data_library.BuildConfig;
import org.pochette.data_library.database_management.Scddb_Helper;
import org.pochette.data_library.database_management.SearchCall;
import org.pochette.data_library.pairing.Signature;
import org.pochette.utils_lib.logg.Logg;
import org.pochette.utils_lib.report.Report;
import org.pochette.utils_lib.report.ReportSystem;
import org.pochette.utils_lib.search.SearchPattern;
import org.pochette.utils_lib.shouting.Shout;
import org.pochette.utils_lib.shouting.Shouting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MusicScan {

    private static final String TAG = "FEHA (MusicScan)";

    // variables
    Context mContext;
    HashMap<String, MusicDirectory> tHM_DirectoryPath2MusicDirectory;
    HashMap<String, ArrayList<MusicFile>> tHM_DirectoryPath2AR_MusicFile;
    Pattern mSignaturePattern;
    Pattern mTrackPattern;
    Comparator<MusicFile> mMusicFileComparator;
    Shouting mShouting;
    // constructor

    public MusicScan(Context iContext) {
        mContext = iContext;
        String tSignaturePatter = Signature.getPattern();
        mSignaturePattern = Pattern.compile(tSignaturePatter);
        mTrackPattern = Pattern.compile("^[0-9][ 0-9] ");

        mMusicFileComparator = new Comparator<MusicFile>() {
            @Override
            public int compare(MusicFile o1, MusicFile o2) {
                if (o2.mTrackNo == o1.mTrackNo) {
                    return o1.mName.compareTo(o2.mName);
                } else {
                    return o1.mTrackNo - o2.mTrackNo;
                }
            }
        };
    }

    // setter and getter

    public void setShouting(Shouting shouting) {
        mShouting = shouting;
    }

    // lifecylce and override
    // internal
    public void scan() {
        if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
            Logg.w(TAG, "Scan running in thread" + Thread.currentThread().toString());
            Logg.w(TAG, "Same as MainLooper Thread" + Looper.getMainLooper().toString());
            throw new RuntimeException("MusicScan may not run on main thread");
        }

        ArrayList<MusicFile> tAR_MusicFile;
        ArrayList<MusicDirectory> tAR_MusicDirectory = new ArrayList<>(0);
        tHM_DirectoryPath2MusicDirectory = new HashMap<>(0);
        tHM_DirectoryPath2AR_MusicFile = new HashMap<>(0);
        Uri allsongsuri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection;
        String[] selectionArguments;
        selectionArguments = null;
        selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

        Cursor tCursor = null;
        int tCountFiles = 0;

//        selection = MediaStore.Audio.Media.ALBUM + " LIKE ? ";
//        selectionArguments = new String[]{ "%Take%" };


        MediaMetadataRetriever tMetaRetriever = null;
        try {
            tCursor = mContext.getContentResolver().query(allsongsuri, null, selection,selectionArguments, null);
            if (BuildConfig.DEBUG) {
                Logg.i(TAG, String.format(Locale.ENGLISH,
                        "In %s tCursor created", "scan"));
            }
            if (tCursor != null && tCursor.getCount() > 0) {

                Logg.i(TAG, String.format(Locale.ENGLISH,
                        "In MusicScan.scan() tCursor with %d rows",  tCursor.getCount()));

                int tColumnId = tCursor.getColumnIndex(MediaStore.Audio.Media._ID);
                int tColumnTitle = tCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
                int tColumnPath = tCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
                int tColumnAlbum = tCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
                int tColumnArtist = tCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
                int tColumnTrack = tCursor.getColumnIndex(MediaStore.Audio.Media.TRACK);
                int tColumnDuration = -1;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    tColumnDuration = tCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
                }
                if (tColumnDuration < 0) {
                    // if column is not available go through MediaMetadataRetriever
                    tMetaRetriever = new MediaMetadataRetriever();
                }

                while (tCursor.moveToNext() && tCountFiles < 15000 ) {
                    int tId;
                    String tTitle;
                    String tPath;
                    String tArtist;
                    String tAlbum;
                    int tDuration;
                    int tTrack;

                    tId = tCursor.getInt(tColumnId);
                    tTitle = tCursor.getString(tColumnTitle);
                    tPath = tCursor.getString(tColumnPath);
                    tArtist = tCursor.getString(tColumnArtist);
                    tAlbum = tCursor.getString(tColumnAlbum);
                    tTrack = tCursor.getInt(tColumnTrack);

                    if (tMetaRetriever != null) {
                        tMetaRetriever.setDataSource(tPath);
                        String tDurationString =
                                tMetaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                        tDuration = Integer.parseInt(tDurationString) / 1000;
                    } else {
                        tDuration = tCursor.getInt(tColumnDuration) / 1000;
                    }
                    MusicFile tMusicfile;
                    tMusicfile = new MusicFile(0, tPath, tTitle, tArtist, tAlbum, tId, 0);
                    tMusicfile.mDuration = tDuration;
                    Matcher tMatcher;
                    tMatcher = mSignaturePattern.matcher(tTitle);
                    String tSignatureString = null;
                    if (tMatcher.matches()) {
                        tSignatureString = tMatcher.group(2);
                    }
                    // if signature not available from title in mediastore, try filename
                    if (tSignatureString == null || Objects.requireNonNull(tSignatureString).isEmpty()) {
                        String tFilename = tMusicfile.getFilename();
                        tMatcher = mSignaturePattern.matcher(tFilename);
                        if (tMatcher.matches()) {
                            tSignatureString = tMatcher.group(2);
                        }
                    }
                    if (tSignatureString == null || Objects.requireNonNull(tSignatureString).isEmpty()) {
                        tSignatureString = Signature.getEmpty();
                    }

                    // if not track no is available from mediastore, try title
                    if (tTrack <= 0 && !tTitle.isEmpty()) {
                        String t;
                        try {
                            tMatcher = mTrackPattern.matcher(tTitle);
                            if (tMatcher.find()) {
                                t = tTitle.substring(0, tMatcher.end() - 1);
                                tTrack = Integer.parseInt(t);
                            }
                        } catch(NumberFormatException e) {
                            // this exception is caught for non standard file name, but is okay
                            continue;
                        }
                    }
                    // if mediastore title does not provide trackno, try filename
                    if (tTrack <= 0) {
                        String t;
                        try {
                            String tFilename = tMusicfile.getFilename();
                            tMatcher = mTrackPattern.matcher(tFilename);
                            if (tMatcher.find()) {
                                t = tFilename.substring(0, tMatcher.end() - 1);
                                tTrack = Integer.parseInt(t);
                            }
                        } catch(NumberFormatException e) {
                            // this exception is caught for non standard file name, but is okay
                            continue;
                        }
                    }
                    tMusicfile.mTrackNo = tTrack;
                    tMusicfile.mSignature = tSignatureString;
                 //   Logg.i(TAG, tMusicfile.mName + "->" + tSignatureString);
                    String tDirectoryPath = tMusicfile.getDirectoryPath();
                 //   Logg.i(TAG,"DirPath frpm MF "+ tDirectoryPath);
                    if (tDirectoryPath != null) {
                        if (!tHM_DirectoryPath2MusicDirectory.containsKey(tDirectoryPath)) {
                            MusicDirectory tMusicDirectory =
                                    new MusicDirectory(0, tDirectoryPath, tArtist, tAlbum, 0, 0, "",
                                            MusicDirectoryPurpose.UNKNOWN);
                            ArrayList<MusicFile> tDirectory_AR_MusicFile = new ArrayList<>(0);
                            tDirectory_AR_MusicFile.add(tMusicfile);
                            tHM_DirectoryPath2MusicDirectory.put(tDirectoryPath, tMusicDirectory);
                            tHM_DirectoryPath2AR_MusicFile.put(tDirectoryPath, tDirectory_AR_MusicFile);
                        } else {
                            Objects.requireNonNull(tHM_DirectoryPath2AR_MusicFile.get(tDirectoryPath)).
                                    add(tMusicfile);
                        }
                    }
                    tCountFiles++;
                    if (tCountFiles % 150 == 0) {
                        Logg.i(TAG, "processed " + tCountFiles);
                    }
                    if (BuildConfig.DEBUG) {
                        Logg.d(TAG, String.format(Locale.ENGLISH,
                                "In %s tCursor with %d rows, processed %d", "scan", tCursor.getCount(), tCountFiles));
                    }
                }
                String tText;
                tText = String.format(Locale.ENGLISH,
                        "%d musicfiles found in mediastore", tCountFiles);
                Logg.i(TAG, tText);
                ReportSystem.receive(tText);
            } else {
                Logg.w(TAG, "no music found");
            }
        } catch(SQLiteException e) {
            Logg.e(TAG, e.toString());
        } finally {
            if (tCursor != null) {
                tCursor.close();
            }
        }


        int tCountDirectory = 0;
        for (String lDirectoryPath : tHM_DirectoryPath2MusicDirectory.keySet()) {
            MusicDirectory lMusicDirectory = tHM_DirectoryPath2MusicDirectory.get(lDirectoryPath);
            ArrayList<MusicFile> lAR_MusicFile = tHM_DirectoryPath2AR_MusicFile.get(lDirectoryPath);
            // sort the array by trackno, then title
            if (lAR_MusicFile != null && lMusicDirectory != null) {
                Collections.sort(lAR_MusicFile, mMusicFileComparator);
                String mDirectorySignature;
                int mLastTrackNo;
                int mCurrentTrackNo;
                int mCountSignatures;
                mDirectorySignature = "";
                mLastTrackNo = 0;
                mCountSignatures = 0;
                for (MusicFile lMusicFile : lAR_MusicFile) {
                    mCurrentTrackNo = lMusicFile.mTrackNo;
                    if (mCurrentTrackNo == mLastTrackNo + 1) {
                        // track number no gap
                        mDirectorySignature = String.format(Locale.ENGLISH, "%s,%s",
                                mDirectorySignature, lMusicFile.mSignature);
                        mCountSignatures++;
                    } else if (mCurrentTrackNo > 0) {
                        // track number, but gap
                        mDirectorySignature = String.format(Locale.ENGLISH, "%s,%s",
                                mDirectorySignature, Signature.getEmpty());
                        mCountSignatures++;
                        mDirectorySignature = String.format(Locale.ENGLISH, "%s,%s",
                                mDirectorySignature, lMusicFile.mSignature);
                        mCountSignatures++;
                    } else {
                        // no track numbers found
                        mDirectorySignature = String.format(Locale.ENGLISH, "%s,%s",
                                mDirectorySignature, lMusicFile.mSignature);
                        mCountSignatures++;
                    }
                    mLastTrackNo = mCurrentTrackNo;
                }
                mDirectorySignature = String.format(Locale.ENGLISH, "%03d%s",
                        mCountSignatures, mDirectorySignature);
                lMusicDirectory.mSignature = mDirectorySignature;
            }
            tAR_MusicDirectory.add(lMusicDirectory);
            tCountDirectory++;
        }
        if (BuildConfig.DEBUG) {
            Logg.d(TAG, "Bulk save directories");
        }

        HashMap<String, Integer> tHM_Path2Id = bulkInsertDirectory(tAR_MusicDirectory);
        if (BuildConfig.DEBUG) {
            Logg.d(TAG, "Put Id in files");
        }
        tAR_MusicFile = new ArrayList<>(0);
        for (String lDirectoryPath : tHM_DirectoryPath2MusicDirectory.keySet()) {
            if (tHM_Path2Id.containsKey(lDirectoryPath)) {
                int tDirectoryId;
                Integer tInteger = tHM_Path2Id.get(lDirectoryPath);
                if (tInteger != null) {
                    tDirectoryId = tInteger;
                } else {
                    throw new RuntimeException("after checking containsKeys that same key does not find a value");
                }
                ArrayList<MusicFile> lAR_MusicFile = tHM_DirectoryPath2AR_MusicFile.get(lDirectoryPath);
                if (lAR_MusicFile != null) {
                    for (MusicFile lMusicFile : lAR_MusicFile) {
                        lMusicFile.mDirectoryId = tDirectoryId;
                        tAR_MusicFile.add(lMusicFile);
                    }
                }
            } else {
                Logg.w(TAG, String.format(Locale.ENGLISH,
                        "HashMap with %d directories should contain %s",
                        tHM_Path2Id.size(), lDirectoryPath));
            }
        }
        bulkInsertFile(tAR_MusicFile);
        String tText = String.format(Locale.ENGLISH,
                "%5d files in %3d directories scanned and saved to Ldb", tCountFiles, tCountDirectory);
        Logg.i(TAG, tText);
        ReportSystem.receive(tText);

        ArrayList<MusicFile> tAR_DB_MusicFile;
        SearchPattern tSearchPattern = new SearchPattern(MusicFile.class);
        SearchCall tSearchCall = new SearchCall(MusicFile.class, tSearchPattern, null);
        tAR_DB_MusicFile = tSearchCall.produceArrayList();
        Logg.i(TAG, "DB has" + tAR_DB_MusicFile.size());

        HashMap<String, MusicDirectory> tHM_DeletedDirectory = new HashMap<>();

        for (MusicFile lMusicFile : tAR_DB_MusicFile) {
            String lDB_DirectoryPath = lMusicFile.getDirectoryPath();
            if (!tHM_DirectoryPath2MusicDirectory.containsKey(lDB_DirectoryPath)) {
                if (!tHM_DeletedDirectory.containsKey(lDB_DirectoryPath)) {
                    MusicDirectory tMusicDirectory = MusicDirectory.getById(lMusicFile.mDirectoryId);
                    tHM_DeletedDirectory.put(lDB_DirectoryPath, tMusicDirectory);
                    Logg.i(TAG, "delete  " + lDB_DirectoryPath);
                    if (tMusicDirectory != null) {
                        tMusicDirectory.delete();
                    }
                }
            }
        }
        if (tHM_DeletedDirectory.size() > 0) {
            tText = "Directories removed " + tHM_DeletedDirectory.size();
            Logg.i(TAG, tText);
            ReportSystem.receive(tText);
        }



    }

    void bulkInsertFile(ArrayList<MusicFile> iAR) {
        SQLiteDatabase tDB = Objects.requireNonNull(Scddb_Helper.getInstance()).getWritableDatabase();
        String tSql = "SELECT _id as _ID, PATH FROM LDB.MUSICFILE ORDER BY PATH";
        Cursor tCursor = tDB.rawQuery(tSql, null);
        HashMap<String, Integer> tHM_FromDb = new HashMap<>(0);
        if (tCursor != null && tCursor.getCount() > 0) {
            while (tCursor.moveToNext()) {
                int tId = tCursor.getInt(tCursor.getColumnIndex("_ID"));
                String tPath = tCursor.getString(tCursor.getColumnIndex("PATH"));
                tHM_FromDb.put(tPath, tId);
            }
        }
        if (tCursor != null && !tCursor.isClosed()) {
            tCursor.close();
        }
        if (BuildConfig.DEBUG) {
            Logg.d(TAG, "BulkInsert: found old MusicFiles" + tHM_FromDb.size());
        }
        // first loop all the inserts
        int tCountInsert;
        tCountInsert = 0;
        tDB.beginTransaction();
        for (MusicFile tMusicFile : iAR) {
            if (!tHM_FromDb.containsKey(tMusicFile.mPath)) {
                tCountInsert++;
                ContentValues tContentValues = tMusicFile.getContentValues();
                tMusicFile.mId = (int) tDB.insertOrThrow(
                        "LDB.MUSICFILE ", null, tContentValues);
            }
        }
        tDB.setTransactionSuccessful();
        tDB.endTransaction();
        // second loop all the updates
        int tCountUpdate;
        tCountUpdate = 0;
        tDB.beginTransaction();
        for (MusicFile lMusicFile : iAR) {
            if (tHM_FromDb.containsKey(lMusicFile.mPath)) {
                Integer tIntegerId = tHM_FromDb.get(lMusicFile.mPath);
                if (tIntegerId != null) {
                    tCountUpdate++;
                    int tId = tIntegerId;
                    ContentValues tContentValues = lMusicFile.getContentValues();
                    tDB.update("LDB.MUSICFILE ", tContentValues, " _id = ? ",
                            new String[]{tId + ""});
                    lMusicFile.mId = tId;
                }
            }
        }
        tDB.setTransactionSuccessful();
        tDB.endTransaction();
        Logg.i(TAG, String.format(Locale.ENGLISH, "bulkInsertFile %d; Update %d",
                tCountInsert, tCountUpdate));
    }

    HashMap<String, Integer> bulkInsertDirectory(ArrayList<MusicDirectory> iAR) {
        if (BuildConfig.DEBUG) {
            Logg.d(TAG, "Bulk Insert MusicDirectory: " + iAR.size());
        }
        HashMap<String, Integer> tHM_Path2Id = new HashMap<>(0);
        try {
            HashMap<String, Integer> tHM_FromDb = new HashMap<>(0);
            SQLiteDatabase tDB;
            tDB = Objects.requireNonNull(Scddb_Helper.getInstance()).getWritableDatabase();
            String tSql = "SELECT _id as _ID, PATH FROM LDB.MUSICDIRECTORY ORDER BY PATH";
            Cursor tCursor = tDB.rawQuery(tSql, null);
            if (tCursor != null && tCursor.getCount() > 0) {
                while (tCursor.moveToNext()) {
                    int tId = tCursor.getInt(tCursor.getColumnIndex("_ID"));
                    String tPath = tCursor.getString(tCursor.getColumnIndex("PATH"));
                    tHM_FromDb.put(tPath, tId);
                }
            }
            if (tCursor != null && !tCursor.isClosed()) {
                tCursor.close();
            }
            if (BuildConfig.DEBUG) {
                Logg.d(TAG, "BulkInsert: found old Ids" + tHM_FromDb.size());
            }
            // first loop all the inserts
            int tCountInsert;
            tCountInsert = 0;
            tDB.beginTransaction();
            for (MusicDirectory tMusicDirectory : iAR) {
                if (!tHM_FromDb.containsKey(tMusicDirectory.mPath)) {
                    tCountInsert++;
                    ContentValues tContentValues = tMusicDirectory.getContentValues();
                    tMusicDirectory.mId = (int) tDB.insertOrThrow(
                            "LDB.MUSICDIRECTORY ", null, tContentValues);
                    tHM_Path2Id.put(tMusicDirectory.mPath, tMusicDirectory.mId);
                }
            }
            tDB.setTransactionSuccessful();
            tDB.endTransaction();
            // second loop all the updates
            int tCountUpdate;
            tCountUpdate = 0;
            tDB.beginTransaction();
            for (MusicDirectory lMusicDirectory : iAR) {
                if (tHM_FromDb.containsKey(lMusicDirectory.mPath)) {
                    Integer tIntegerId = tHM_FromDb.get(lMusicDirectory.mPath);
                    if (tIntegerId != null) {
                        tCountUpdate++;
                        int tId = tIntegerId;
                        ContentValues tContentValues = lMusicDirectory.getContentValues();
                        tDB.update("LDB.MUSICDIRECTORY ", tContentValues, " _id = ? ",
                                new String[]{tId + ""});
                        lMusicDirectory.mId = tId;
                        tHM_Path2Id.put(lMusicDirectory.mPath, tId);
                    }
                }
            }
            tDB.setTransactionSuccessful();
            tDB.endTransaction();
            Logg.i(TAG, String.format(Locale.ENGLISH, "bulkInsertDirectory %d; Update %d",
                    tCountInsert, tCountUpdate));
            return tHM_Path2Id;

        } catch(SQLiteException e) {
            Logg.e(TAG, e.toString());
        }
        return null;
    }


    // public methods

    public ArrayList<MusicFile> execute() {
        Thread tThread;
        Runnable tRunnable;
        tRunnable = new Runnable() {
            @Override
            public void run() {
                Shout tShout = new Shout(MusicScan.class.getSimpleName());
                tShout.mLastObject = "Execution";
                try {
                    Logg.i(TAG, "Call Scan");
                    scan();
                    Logg.i(TAG, "Past Scan");
                    tShout.mLastAction = "succeeded";
                } catch(Exception e) {
                    Logg.w(TAG, "Exception during MusicScan.scan()");
                    Logg.w(TAG, e.toString());
                    tShout.mLastAction = "failed";
                }
                if (mShouting != null) {
                    mShouting.shoutUp(tShout);
                }
            }
        };
        tThread = new Thread(tRunnable);
        tThread.setName("MusicScan");
        tThread.start();
        return null;
    }
}
