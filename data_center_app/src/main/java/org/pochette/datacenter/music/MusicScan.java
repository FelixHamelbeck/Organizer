package org.pochette.data_library.music;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.provider.MediaStore;

import org.pochette.utils_lib.logg.Logg;
import org.pochette.data_library.database_management.Scddb_Helper;
import org.pochette.data_library.pairing.Signature;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public class MusicScan {

    private static final String TAG = "FEHA (MusicScan)";

    // variables
    Context mContext;
    // ArrayList<MusicFile> mAR_MusicFile;
    HashMap<String, MusicDirectory> mHM_DirectoryPath2MusicDirectory;
    HashMap<String, ArrayList<MusicFile>> mHM_DirectoryPath2AR_MusicFile;
    Pattern mSignaturePattern;
    Pattern mTrackPattern;
    Comparator<MusicFile> mMusicFileComparator;
    // constructor

    @SuppressWarnings("Convert2Lambda")
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
    // lifecylce and override
    // internal

    @SuppressWarnings("ConstantConditions")
    void scan() {

        ArrayList<MusicFile> mAR_MusicFile;
        ArrayList<MusicDirectory> mAR_MusicDirectory = new ArrayList<>(0);
        mHM_DirectoryPath2MusicDirectory = new HashMap<>(0);
        mHM_DirectoryPath2AR_MusicFile = new HashMap<>(0);

        Uri allsongsuri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        String[] STAR = null;
        Cursor tCursor = null;
        int tCountFiles = 0;
        try {
            tCursor = mContext.getContentResolver().query(allsongsuri, STAR, selection, null, null);
            if (tCursor != null && tCursor.getCount() > 0) {
                int tColumnId = tCursor.getColumnIndex(MediaStore.Audio.Media._ID);
                int tColumnTitle = tCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE);
                int tColumnPath = tCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
                int tColumnArtist = tCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
                int tColumnAlbum = tCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);


                //int tColumnDuration = tCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
                int tColumnTrack = tCursor.getColumnIndex(MediaStore.Audio.Media.TRACK);

                while (tCursor.moveToNext()) {
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
                    //   tDuration = tCursor.getInt(tColumnDuration);
                    tTrack = tCursor.getInt(tColumnTrack);

                    MusicFile tMusicfile;
                    //tMusicfile = null;
                    tMusicfile = new MusicFile(0, tPath, tTitle, tArtist, tAlbum, tId, 0);
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
                    if (tTrack <= 0 && ! tTitle.isEmpty()) {
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
                    // if mediastore title does not provide  trackno, try filename
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


                    if (tMusicfile != null) {
                        //     tMusicfile.mDuration = tDuration;
                        tMusicfile.mTrackNo = tTrack;
                        tMusicfile.mSignature = tSignatureString;
                        String tDirectoryPath = tMusicfile.getDirectoryPath();
                        if (tDirectoryPath != null) {
                            if (!mHM_DirectoryPath2MusicDirectory.containsKey(tDirectoryPath)) {
                                MusicDirectory tMusicDirectory =
                                        new MusicDirectory(0, tDirectoryPath, tArtist, tAlbum, 0, 0, "",
                                                MusicDirectoryPurpose.UNKNOWN);
                                ArrayList<MusicFile> tDirectory_AR_MusicFile = new ArrayList<>(0);
                                tDirectory_AR_MusicFile.add(tMusicfile);
                                mHM_DirectoryPath2MusicDirectory.put(tDirectoryPath, tMusicDirectory);
                                mHM_DirectoryPath2AR_MusicFile.put(tDirectoryPath, tDirectory_AR_MusicFile);
                            } else {
                                Objects.requireNonNull(mHM_DirectoryPath2AR_MusicFile.get(tDirectoryPath)).
                                        add(tMusicfile);
                            }
                        }
                    }
                    tCountFiles++;
                    if (tCountFiles > 50000) {
                        break;
                    }
                }
                Logg.i(TAG, "found in Mediastore: " + tCountFiles);
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
        for (String lDirectoryPath : mHM_DirectoryPath2MusicDirectory.keySet()) {
            //     Logg.w(TAG, lDirectoryPath);
            MusicDirectory lMusicDirectory = mHM_DirectoryPath2MusicDirectory.get(lDirectoryPath);
            ArrayList<MusicFile> lAR_MusicFile = mHM_DirectoryPath2AR_MusicFile.get(lDirectoryPath);
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
                        // track numberm no gap
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
            mAR_MusicDirectory.add(lMusicDirectory);
            tCountDirectory++;
        }

        Logg.i(TAG, "Bulk save directories");

        HashMap<String, Integer> tHM_Path2Id = bulkInsertDirectory(mAR_MusicDirectory);

        Logg.i(TAG, "Put Id in files");
        mAR_MusicFile = new ArrayList<>(0);
        for (String lDirectoryPath : mHM_DirectoryPath2MusicDirectory.keySet()) {
            if (tHM_Path2Id.containsKey(lDirectoryPath)) {
                int tDirectoryId = tHM_Path2Id.get(lDirectoryPath);
                ArrayList<MusicFile> lAR_MusicFile = mHM_DirectoryPath2AR_MusicFile.get(lDirectoryPath);
                if (lAR_MusicFile != null) {
                    for (MusicFile lMusicFile : lAR_MusicFile) {
                        lMusicFile.mDirectoryId = tDirectoryId;
                        mAR_MusicFile.add(lMusicFile);
                    }
                }
            } else {
                Logg.w(TAG, "should not happen " + lDirectoryPath);
            }
        }
        bulkInsertFile(mAR_MusicFile);


        Logg.i(TAG, String.format(Locale.ENGLISH,
                "%5d files in %3d directories scanned", tCountFiles, tCountDirectory));

    }


    public void bulkInsertFile(ArrayList<MusicFile> iAR) {
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
        Logg.i(TAG, "BulkInsert: found old MusicFiles" + tHM_FromDb.size());

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
        Logg.i(TAG, String.format(Locale.ENGLISH, "Insert %d; Update %d",
                tCountInsert, tCountUpdate));


    }


    public HashMap<String, Integer> bulkInsertDirectory(ArrayList<MusicDirectory> iAR) {

        Logg.i(TAG, "Bulk Insert MusicDirectory: " + iAR.size());
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
            Logg.i(TAG, "BulkInsert: found old Ids" + tHM_FromDb.size());

            // first loop all the inserts
            int tCountInsert;
            tCountInsert = 0;
            tDB.beginTransaction();
            for (MusicDirectory tMusicDirectory : iAR) {
                //    String tPath = tMusicDirectory.mPath;
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
            Logg.i(TAG, String.format(Locale.ENGLISH, "Insert %d; Update %d",
                    tCountInsert, tCountUpdate));
            return tHM_Path2Id;

        } catch(SQLiteException e) {
            Logg.e(TAG, e.toString());
        }
        return null;
    }


    // public methods

    public ArrayList<MusicFile> execute() {
        Logg.i(TAG, "Scan");
        scan();
        Logg.i(TAG, "Scan finished");
        return null;
    }
}