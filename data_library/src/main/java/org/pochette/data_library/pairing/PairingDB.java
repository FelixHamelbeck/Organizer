package org.pochette.data_library.pairing;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.pochette.data_library.database_management.Scddb_Helper;
import org.pochette.utils_lib.logg.Logg;



/**
 * this class covers the DB functionality needed for the pairing process,
 * as it cannot be covered by SqlContract
 * <p>
 * Coverage: /br
 * update MUSICDIRECTORY.ALBUM_ID
 * update MUSICFILE.RECORDING_ID and MUSICFILE.DANCE_ID
 */
@SuppressWarnings("unused")
public
class PairingDB {

    private static final String TAG = "FEHA (PairingDB)";

    // variables
    // constructor
    public PairingDB() {
    }

    // setter and getter
    // lifecylce and override
    // internal
    // public methods


    public void updateMusicFile(Integer iMusicDirectoryId) {
        //    Logg.i(TAG, "synchronize");
        String tSql;
        // the min(dance_Id) seems an obvious choice, as the more orginal/traditional dance will have the smaller number
        if (iMusicDirectoryId != null && iMusicDirectoryId > 0) {
            tSql = " SELECT MF._ID AS MF_ID, ARM.RECORDING_ID AS RECORDING_ID, " +
                    "   IFNULL(SDRM.DANCE_ID , 0) AS DANCE_ID " +
                    " FROM LDB.MUSICDIRECTORY AS MD " +
                    " INNER JOIN LDB.MUSICFILE AS MF ON MD._ID = MF.MUSICDIRECTORY_ID " +
                    " LEFT OUTER JOIN ALBUM AS A ON A.id = MD.ALBUM_ID" +
                    " LEFT OUTER JOIN ALBUMSRECORDINGSMAP AS ARM ON " +
                    "	A.ID = ARM.ALBUM_ID AND " +
                    "	ARM.TRACKNUMBER = MF.TRACK_NO   " +
                    " LEFT OUTER JOIN ( SELECT TDRM.RECORDING_ID, MIN(TDRM.DANCE_ID) AS DANCE_ID " +
                    "    FROM DANCESRECORDINGSMAP AS TDRM GROUP BY TDRM.RECORDING_ID ) AS SDRM " +
                    "   ON SDRM.RECORDING_ID = ARM.RECORDING_ID "  +
                    " WHERE MD._ID = " + iMusicDirectoryId.toString();
        } else {
            tSql = " SELECT MF._ID AS MF_ID, ARM.RECORDING_ID AS RECORDING_ID, " +
                    "   IFNULL(SDRM.DANCE_ID , 0) AS DANCE_ID " +
                    " FROM LDB.MUSICDIRECTORY AS MD " +
                    " LEFT OUTER JOIN LDB.MUSICFILE AS MF ON MD._ID = MF.MUSICDIRECTORY_ID " +
                    " LEFT OUTER JOIN ALBUM AS A ON A.id = MD.ALBUM_ID" +
                    " LEFT OUTER JOIN ALBUMSRECORDINGSMAP AS ARM ON " +
                    "	A.ID = ARM.ALBUM_ID AND " +
                    "	ARM.TRACKNUMBER = MF.TRACK_NO   " +
                    " LEFT OUTER JOIN ( SELECT TDRM.RECORDING_ID, MIN(TDRM.DANCE_ID) AS DANCE_ID " +
                    "    FROM DANCESRECORDINGSMAP AS TDRM GROUP BY TDRM.RECORDING_ID ) AS SDRM " +
                    "   ON SDRM.RECORDING_ID = ARM.RECORDING_ID ";
        }

        SQLiteDatabase tSqLiteDatabase = Scddb_Helper.getInstance().getWritableDatabase();

        tSqLiteDatabase.beginTransaction();
        int tCount = 0;
        try {
            Cursor tCursor = null;
            //noinspection TryFinallyCanBeTryWithResources
            try {
                tCursor = tSqLiteDatabase.rawQuery(tSql, null);
                if (tCursor != null) {
                    Logg.i(TAG, "Add Cursor:" + tCursor.getCount());
                    int tColumnIndexMfId = tCursor.getColumnIndex("MF_ID");
                    int tColumnIndexRecordingId = tCursor.getColumnIndex("RECORDING_ID");
                    int tColumnIndexDanceId = tCursor.getColumnIndex("DANCE_ID");
                    while (tCursor.moveToNext()) {
                        int tMfId = tCursor.getInt(tColumnIndexMfId);
                        int tRecordingId = tCursor.getInt(tColumnIndexRecordingId);
                        int tDanceId = tCursor.getInt(tColumnIndexDanceId);
                        ContentValues tContentValues = new ContentValues();
                        tContentValues.put("RECORDING_ID", tRecordingId);
                        tContentValues.put("DANCE_ID", tDanceId);
                        String[] tSA_Where = new String[]{String.valueOf(tMfId),};
                        tSqLiteDatabase.updateWithOnConflict("LDB.MUSICFILE", tContentValues,
                                "_ID = ? ", tSA_Where, SQLiteDatabase.CONFLICT_ROLLBACK);
                        tCount++;
                    }
                }
            } finally {
                if (tCursor != null) {
                    tCursor.close();
                }
            }
            tSqLiteDatabase.setTransactionSuccessful();
        } catch(Exception e) {
            Logg.w(TAG, e.toString());
        } finally {
            tSqLiteDatabase.endTransaction();
        }
        Logg.i(TAG, "MusicFiles updated " + tCount);
    }


    public void updateMusicDirectory(Integer iMusicDirectoryId) {

        String tSql;
        if (iMusicDirectoryId != null && iMusicDirectoryId > 0) {
            tSql = " SELECT MD._ID AS MD_ID, PR.SCDDB_ID AS SCDDB_ID  " +
                    " FROM LDB.MUSICDIRECTORY AS MD  " +
                    " LEFT OUTER JOIN LDB.PAIRING AS PR ON  " +
                    "   MD._ID = PR.LDB_ID AND PR.PAIRING_OBJECT = 'MUSIC_DIRECTORY' " +
                    "   AND PR.PAIRING_STATUS = 'CONFIRMED'" +
                    " WHERE MD._ID = " + iMusicDirectoryId.toString();
            Logg.i(TAG, tSql);
        } else {
            tSql = " SELECT MD._ID AS MD_ID, PR.SCDDB_ID AS SCDDB_ID  " +
                    " FROM LDB.MUSICDIRECTORY AS MD  " +
                    " LEFT OUTER JOIN LDB.PAIRING AS PR ON  " +
                    "   MD._ID = PR.LDB_ID AND PR.PAIRING_OBJECT = 'MUSIC_DIRECTORY' " +
                    "   AND PR.PAIRING_STATUS = 'CONFIRMED'";
        }


        SQLiteDatabase tSqLiteDatabase = Scddb_Helper.getInstance().getWritableDatabase();
        Cursor tCursor = null;
        tSqLiteDatabase.beginTransaction();
        int tCount = 0;

        try {
            try {
                tCursor = tSqLiteDatabase.rawQuery(tSql, null);
                if (tCursor != null) {
                    int tColumnIndexMdId = tCursor.getColumnIndex("MD_ID");
                    int tColumnIndexScddbId = tCursor.getColumnIndex("SCDDB_ID");
                    while (tCursor.moveToNext()) {
                        int tMdId = tCursor.getInt(tColumnIndexMdId);
                        int tScddbId = tCursor.getInt(tColumnIndexScddbId);
                        ContentValues tContentValues = new ContentValues();
                        tContentValues.put("ALBUM_ID", tScddbId);
                        String[] tSA_Where = new String[]{String.valueOf(tMdId),};
                        tSqLiteDatabase.updateWithOnConflict("LDB.MUSICDIRECTORY", tContentValues,
                                "_ID = ? ", tSA_Where, SQLiteDatabase.CONFLICT_ROLLBACK);
                        tCount++;
                    }
                }
            } finally {
                if (tCursor != null) {
                    tCursor.close();
                }
            }
            tSqLiteDatabase.setTransactionSuccessful();
        } catch(Exception e) {
            Logg.w(TAG, e.toString());
        } finally {
            tSqLiteDatabase.endTransaction();
        }
        Logg.i(TAG, "MusicDirectories updated: " + tCount);
    }


    public void updateMusicFile_old() {
        //    Logg.i(TAG, "synchronize");
        String tSql;
        tSql = "" +
                " SELECT MF._ID AS MF_ID, ARM.RECORDING_ID AS RECORDING_ID, " +
                "   IFNULL(SDRM.DANCE_ID , 0) AS DANCE_ID " +
                " FROM LDB.MUSICDIRECTORY AS MD " +
                " LEFT OUTER JOIN LDB.MUSICFILE AS MF ON MD._ID = MF.MUSICDIRECTORY_ID " +
                " INNER JOIN ALBUM AS A ON A.id = MD.ALBUM_ID" +
                " INNER JOIN ALBUMSRECORDINGSMAP AS ARM ON " +
                "	A.ID = ARM.ALBUM_ID AND " +
                "	ARM.TRACKNUMBER = MF.TRACK_NO   " +
                " LEFT OUTER JOIN ( SELECT TDRM.RECORDING_ID, MIN(TDRM.DANCE_ID) AS DANCE_ID " +
                "    FROM DANCESRECORDINGSMAP AS TDRM GROUP BY TDRM.RECORDING_ID ) AS SDRM " +
                "   ON SDRM.RECORDING_ID = ARM.RECORDING_ID ";
        // the min(dance_Id) seems an obvious choice, as the more orginal/traditional dance will have the smaller number

        SQLiteDatabase tSqLiteDatabase = Scddb_Helper.getInstance().getWritableDatabase();
        tSqLiteDatabase.beginTransaction();
        int tCount = 0;
        try {
            Cursor tCursor = null;
            //noinspection TryFinallyCanBeTryWithResources
            try {
                tCursor = tSqLiteDatabase.rawQuery(tSql, null);
                if (tCursor != null) {
                    // Logg.i(TAG, "Add Cursor:" + tCursor.getCount());
                    int tColumnIndexMfId = tCursor.getColumnIndex("MF_ID");
                    int tColumnIndexRecordingId = tCursor.getColumnIndex("RECORDING_ID");
                    int tColumnIndexDanceId = tCursor.getColumnIndex("DANCE_ID");
                    while (tCursor.moveToNext()) {
                        int tMfId = tCursor.getInt(tColumnIndexMfId);
                        int tRecordingId = tCursor.getInt(tColumnIndexRecordingId);
                        int tDanceId = tCursor.getInt(tColumnIndexDanceId);
                        ContentValues tContentValues = new ContentValues();
                        tContentValues.put("RECORDING_ID", tRecordingId);
                        tContentValues.put("DANCE_ID", tDanceId);
                        String[] tSA_Where = new String[]{String.valueOf(tMfId),};
                        tSqLiteDatabase.updateWithOnConflict("LDB.MUSICFILE", tContentValues,
                                "_ID = ? ", tSA_Where, SQLiteDatabase.CONFLICT_ROLLBACK);
                        tCount++;
                    }
                }
            } finally {
                if (tCursor != null) {
                    tCursor.close();
                }
            }
            tSqLiteDatabase.setTransactionSuccessful();
        } catch(Exception e) {
            Logg.w(TAG, e.toString());
        } finally {
            tSqLiteDatabase.endTransaction();
        }
        Logg.i(TAG, "MusicFiles updated " + tCount);
    }

    public void updateMusicDirectory_old() {
        Logg.v(TAG, "synchronize");

        SQLiteDatabase tSqLiteDatabase = Scddb_Helper.getInstance().getWritableDatabase();
        String tSql;
        Cursor tCursor = null;
        //noinspection RedundantStringFormatCall
        tSql = String.format(" SELECT MD._ID AS MD_ID, PR.SCDDB_ID AS SCDDB_ID  " +
                " FROM LDB.MUSICDIRECTORY AS MD  " +
                " INNER JOIN LDB.PAIRING AS PR ON  " +
                "   MD._ID = PR.LDB_ID AND PR.PAIRING_OBJECT = 'MUSIC_DIRECTORY' " +
                "   AND PR.PAIRING_STATUS = 'CONFIRMED'");
        tSqLiteDatabase.beginTransaction();
        int tCount = 0;

        try {

            try {
                tCursor = tSqLiteDatabase.rawQuery(tSql, null);
                Logg.v(TAG, "Cursor " + tCursor.getCount());
                //noinspection ConstantConditions
                if (tCursor != null) {
                    int tColumnIndexMdId = tCursor.getColumnIndex("MD_ID");
                    int tColumnIndexScddbId = tCursor.getColumnIndex("SCDDB_ID");
                    while (tCursor.moveToNext()) {
                        int tMdId = tCursor.getInt(tColumnIndexMdId);
                        int tScddbId = tCursor.getInt(tColumnIndexScddbId);
                        ContentValues tContentValues = new ContentValues();
                        tContentValues.put("ALBUM_ID", tScddbId);
                        String[] tSA_Where = new String[]{String.valueOf(tMdId),};
                        tSqLiteDatabase.updateWithOnConflict("LDB.MUSICDIRECTORY", tContentValues,
                                "_ID = ? ", tSA_Where, SQLiteDatabase.CONFLICT_ROLLBACK);
                        tCount++;
                    }
                }
            } finally {
                if (tCursor != null) {
                    tCursor.close();
                }
            }
            tSqLiteDatabase.setTransactionSuccessful();
        } catch(Exception e) {
            Logg.w(TAG, e.toString());
        } finally {
            tSqLiteDatabase.endTransaction();
        }
        Logg.i(TAG, "MusicDirectories updated: " + tCount);
    }
}
