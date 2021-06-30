package org.pochette.data_library.search;

import org.pochette.data_library.pairing.Pairing;
import org.pochette.data_library.scddb_objects.Album;
import org.pochette.data_library.scddb_objects.Crib;
import org.pochette.data_library.scddb_objects.Dance;
import org.pochette.data_library.diagram.Diagram;
import org.pochette.data_library.music.MusicDirectory;
import org.pochette.data_library.music.MusicFile;
import org.pochette.data_library.music.MusicPreference;
import org.pochette.data_library.playlist.Playinstruction;
import org.pochette.data_library.playlist.Playlist;
import org.pochette.data_library.scddb_objects.Recording;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

@SuppressWarnings({"rawtypes", "unused"})
public class SqlContract {

    private static final String TAG = "FEHA (SqlContract)";

    HashMap<Class, String> mHM_CreateString; //table definition
    HashMap<Class, String> mHM_JoinString; // the join part of the select statement
    HashMap<Class, String> mHM_WhereIdString; //  where id = ? for update and delete statemente
    HashMap<Class, String[]> mHM_ColumnArray; // the array for each column for the column list in selections
    ArrayList<DictionaryWhereMethod> mAR_DictionaryWhereMethod;
    ArrayList<String> mAR_OrderByString;


    ArrayList<Class> mAR_LdbClass; // the list of local tables
    HashMap<Class, String> mHM_LdbTableString;

    // A  ALBUM
    // C  CRIB
    // D  DANCE
    // R  RECORDING
    // DC DANCECRIB
    // PE PERSON

    // DG Diagram
    // MF MusicFile
    // MD MusicDirectory
    // MP MusicPreference
    // PL Playlist
    // PI Playinstruction
    // PR Pairing


    // variables
    // constructor

    public SqlContract() {
        mHM_CreateString = new HashMap<>(0);
        mHM_WhereIdString = new HashMap<>(0);
        mHM_JoinString = new HashMap<>(0);
        mHM_ColumnArray = new HashMap<>(0);

        mAR_DictionaryWhereMethod = new ArrayList<>(0);
        mAR_OrderByString = new ArrayList<>(0);

        mAR_LdbClass = new ArrayList<>(0);
        mHM_LdbTableString = new HashMap<>(0);

        // as the following define call create the array of tables, there is no iteration possible
        // read the definitions table by table
        defineAlbum();
        defineCrib();
        defineDance();
        defineRecording();

        // LDB Tables
        defineDiagram();
        defineMusicDirectory();
        defineMusicFile();
        defineMusicPreference();
        definePairing();
        definePlayinstruction();
        definePlaylist();


    }


    // setter and getter
    // lifecylce and override
    // internal
    void addWhereMethod(Class iClass, String iMethod,
                        String iDescription,
                        String iSelect, boolean iFlagQuestionMark,
                        boolean iFlagEscape, boolean iFlagWildcard,
                        boolean iFlagTrim, boolean iFlagLower) {
        DictionaryWhereMethod tDictionaryWhereMethod;
        tDictionaryWhereMethod = new DictionaryWhereMethod(iClass, iMethod,
                iDescription,
                iSelect, iFlagQuestionMark,
                iFlagEscape, iFlagWildcard,
                iFlagTrim, iFlagLower);
        mAR_DictionaryWhereMethod.add(tDictionaryWhereMethod);
    }

    ///////////// SCDDB TABLES ////////////////

    void defineAlbum() {
        Class tClass = Album.class;
        String tJoinString;
        String[] tColumnArray;
        tJoinString = " ALBUM AS A " +
                " LEFT OUTER JOIN PERSON AS PE ON A.ARTIST_ID = PE.ID  " +
//                " LEFT OUTER JOIN ( " +
//                "  SELECT T.ALBUM_ID , COUNT(*) AS C " +
//                "  FROM ALBUMSRECORDINGSMAP T " +
//                "  GROUP BY T.ALBUM_ID " +
//                "  ) AS ARM ON ARM.ALBUM_ID = A.ID" +

                " LEFT OUTER JOIN (" +
                "                 SELECT TARM.ALBUM_ID , COUNT(*) AS C  , GROUP_CONCAT(IFNULL(TDT.SHORT_NAME, 'X')||TR.REPETITIONS||'x'||CASE WHEN TR.BARSPERREPEAT = 0 THEN '00' ELSE CAST( TR.BARSPERREPEAT as vVARCHAR) end ,',') as SIGNATURE " +
                "         FROM ALBUMSRECORDINGSMAP TARM" +
                "         INNER JOIN RECORDING AS TR ON TR.ID = TARM.recording_ID" +
                "         INNER JOIN DANCETYPE AS TDT ON TDT.ID = TR.TYPE_ID" +
                "         GROUP BY TARM.ALBUM_ID  ) AS ARM ON ARM.ALBUM_ID = A.ID " +


                " LEFT OUTER JOIN ( " +
                "  SELECT T.ALBUM_ID, COUNT(*) AS C" +
                "  FROM  LDB.MUSICDIRECTORY AS T " +
                "  GROUP BY T.ALBUM_ID ) AS LMD ON LMD.ALBUM_ID = A.ID ";

        tColumnArray = new String[]{
                "A.id AS ID",
                "A.NAME AS NAME",
                "A.SHORTNAME AS SHORTNAME",
                "PE.ID AS ARTIST_ID",
                "PE.DISPLAY_NAME AS ARTIST_NAME",
                "A.ALPHAORDER AS ALPHAORDER",
                "ARM.C AS COUNT_RECORDINGS",
                "LMD.C AS COUNT_LDB_DIRECTORIES",
                "CASE WHEN LMD.C IS NULL THEN 'N' WHEN LMD.C = 0 THEN 'N' ELSE 'J' END AS PAIRING ",
                " IFNULL(PRINTF(\"%0.3d,\",ARM.C)|| ARM.SIGNATURE , '000,') AS SIGNATURE "


        };

        addWhereMethod(tClass, "ID",
                "Search by Database ID",
                "A.ID = ?", true, false, false, false, false);

        addWhereMethod(tClass, "ALBUM_SHORTNAME",
                "Search by approx album name ",
                "LOWER(A.SHORTNAME ) LIKE ?", true, false, true, true, true);

        addWhereMethod(tClass, "ARTIST_SHORTNAME",
                "Search by approx album name ",
                "LOWER(PE.DISPLAY_NAME)  LIKE ?", true, false, true, true, true);

        addWhereMethod(tClass, "COUNT_RECORDING",
                "Search by number of recordings in album ",
                "ARM.C = ?", true, false, false, false, false);

        mHM_JoinString.put(tClass, tJoinString);
        mHM_ColumnArray.put(tClass, tColumnArray);

    }

    void defineCrib() {
        Class tClass = Crib.class;
        String tJoinString;
        String[] tColumnArray;
        tJoinString = "DANCECRIB AS DC";


        tColumnArray = new String[]{
                "DC.ID AS _ID",
                "DC.DANCE_ID AS DANCE_ID",
                "DC.TEXT AS SCDDBTEXT",
                "DC.RELIABILITY AS RELIABILITY "};

        addWhereMethod(tClass, "ID",
                "Search by Database ID",
                "DC.ID = ?", true, false, false, false, false);


        addWhereMethod(tClass, "DANCE_ID",
                "Search by dance ID",
                "DC.DANCE_ID = ?", true, false, false, false, false);


        mHM_JoinString.put(tClass, tJoinString);
        mHM_ColumnArray.put(tClass, tColumnArray);

    }

    void defineDance() {
        Class tClass = Dance.class;
        String tJoinString;
        String[] tColumnArray;
        tJoinString = " DANCE AS D " +
                " LEFT OUTER JOIN DANCETYPE AS DT ON DT.ID = D.TYPE_ID " +
                " LEFT OUTER JOIN MEDLEYTYPE AS MT ON MT.ID = D.MEDLEYTYPE_ID " +
                " LEFT OUTER JOIN SHAPE AS S ON S.ID = D.SHAPE_ID " +
                " LEFT OUTER JOIN COUPLES AS C ON C.ID = D.COUPLES_ID " +
                " LEFT OUTER JOIN PROGRESSION AS P ON P.ID = D.PROGRESSION_ID" +
                " LEFT OUTER JOIN ( SELECT DC.DANCE_ID, COUNT(*) AS C " +
                "       FROM DANCECRIB AS DC " +
                "       GROUP BY DC.DANCE_ID )  as SDC ON SDC.DANCE_ID = D.ID" +
                " LEFT OUTER JOIN ( SELECT DG.DANCE_ID, COUNT(*) AS C " +
                "       FROM LDB.DIAGRAM AS DG " +
                "       GROUP BY DG.DANCE_ID )  as SDI ON SDI.DANCE_ID = D.ID" +
                // The album matches are stored in the database only as recording_id, not dance_id
                // later it might be possible to store manual connections from dance to musicfile
                " LEFT OUTER JOIN ( SELECT DRM.DANCE_ID, COUNT(DISTINCT MF._ID) AS C " +
                "       FROM LDB.MUSICFILE AS MF " +
                "       LEFT OUTER JOIN DANCESRECORDINGSMAP AS DRM ON MF.RECORDING_ID = DRM.RECORDING_ID " +
                "       WHERE MF.RECORDING_ID = DRM.RECORDING_ID OR MF.DANCE_ID = DRM.DANCE_ID " +
                "       GROUP BY DRM.DANCE_ID ) AS SMF ON SMF.DANCE_ID = D.ID " +
                " LEFT OUTER JOIN ( SELECT MP.DANCE_ID AS DANCE_ID, " +
                "       SUM(CASE WHEN MP.PAIRING_EXIST = 0 THEN 1 ELSE 0 END) AS COUNT_ANYGOOD " +
                "       FROM LDB.MUSIC_PREFERENCE AS MP " +
                "       GROUP BY MP.DANCE_ID ) AS SMP ON SMP.DANCE_ID = D.ID " +
                " LEFT OUTER JOIN ( SELECT DPM.DANCE_ID, CASE WHEN SUM(P.RSCDS) > 0 THEN 'Y' ELSE 'N' END AS RSCDS_YN" +
                "       FROM DANCESPUBLICATIONSMAP AS DPM " +
                "       LEFT OUTER JOIN PUBLICATION AS P ON P.ID = DPM.PUBLICATION_ID " +
                "       GROUP BY DPM.DANCE_ID ) AS SDPM ON SDPM.DANCE_ID = D.ID " +
//                        " LEFT OUTER JOIN LDB.DANCE_CLASSIFICATION AS CL " +
//                        "       ON CL.OBJECT_ID = D.ID " +
                "";


        tColumnArray = new String[]{
                " D.ID AS D_ID", " D.NAME AS D_NAME", " DT.NAME AS D_TYPENAME", " DT.SHORT_NAME AS D_TYPESHORTNAME",
                " D.BARSPERREPEAT AS D_BARSPERREPEAT", " MT.DESCRIPTION AS D_MEDLEYTYPE", " S.SHORTNAME AS D_SHAPE",
                " C.NAME AS D_COUPLES", " P.NAME AS D_PROGRESSION," + " SDC.C AS D_COUNT_OF_CRIBS",
                " CASE WHEN SDPM.DANCE_ID IS NULL THEN 'N' else SDPM.RSCDS_YN END AS D_RSCDS_YN "};

        addWhereMethod(tClass, "ID",
                "Search by Database ID",
                "D.ID = ?", true, false, false, false, false);

        addWhereMethod(tClass, "DANCENAME",
                "Search by approx dance name ",
                "LOWER(D.NAME ) LIKE ?", true, false, true, true, true);

        addWhereMethod(tClass, "CRIB_REQUIRED",
                "Only dance with cribs ",
                " SDC.C > 0 ", false, false, false, false, false);

        addWhereMethod(tClass, "RECORDING_ID",
                "Search dances linked to the recording",
                "D.id IN (SELECT DRM.DANCE_ID FROM DANCESRECORDINGSMAP AS DRM " +
                        " WHERE DRM.RECORDING_ID = ? GROUP BY DRM.DANCE_ID) ",
                true, false, false, false, false);


        mHM_JoinString.put(tClass, tJoinString);
        mHM_ColumnArray.put(tClass, tColumnArray);

    }

    void defineRecording() {
        Class tClass = Recording.class;
        String tJoinString;
        String[] tColumnArray;
        tJoinString = " RECORDING AS R " +
                " LEFT OUTER JOIN ALBUMSRECORDINGSMAP AS ARM ON ARM.RECORDING_ID = R.ID " +
                " LEFT OUTER JOIN ALBUM AS A ON A.ID = ARM.ALBUM_ID " +
                " LEFT OUTER JOIN PERSON AS PE ON PE.ID = A.ARTIST_ID " +
                " LEFT OUTER JOIN DANCETYPE AS DT ON DT.ID = R.TYPE_ID " +
                " LEFT OUTER JOIN MEDLEYTYPE AS MT ON MT.ID = R.MEDLEYTYPE_ID " +

                " LEFT OUTER JOIN ( SELECT T.RECORDING_ID, COUNT(*) AS C " +
                "                   FROM LDB.MUSICFILE AS T " +
                "                   GROUP BY T.RECORDING_ID ) " +
                "                   AS L_MF ON L_MF.RECORDING_ID = R.ID  ";

        tColumnArray = new String[]{
                "R.ID AS R_ID",
                "R.NAME AS R_NAME",
                "ARM.ALBUM_ID AS R_ALBUM_ID",
                "A.NAME AS R_ALBUMNAME",
                "A.SHORTNAME AS R_ALBUMSHORTNAME",
                "PE.NAME AS R_ALBUMARTISTNAME",
                "ARM.TRACKNUMBER AS R_TRACKNUMBER",
                "DT.NAME AS R_TYPENAME",
                "DT.SHORT_NAME AS R_TYPESHORTNAME",
                "R.REPETITIONS AS R_REPETITIONS",
                "R.BARSPERREPEAT AS R_BARSPERREPEAT",
                "MT.DESCRIPTION AS R_MEDLEYTYPE",
                "R.PLAYINGSECONDS AS R_PLAYINGSECONDS",
                "R.TWOCHORDS AS R_TWOCHORDS",
                "IFNULL(L_MF.C, 0) AS R_COUNT_MUSICFILES "};

        addWhereMethod(tClass, "ID",
                "Search by Database ID",
                "R.ID = ?", true, false, false, false, false);

        addWhereMethod(tClass, "ARTIST_SHORTNAME",
                "Search by approx album artist name ",
                "LOWER(PE.DISPLAY_NAME)  LIKE ?", true, false, true, true, true);

        addWhereMethod(tClass, "ALBUM_ID",
                "Search by album id",
                "ARM.ALBUM_ID = ?", true, false, false, false, false);

        addWhereMethod(tClass, "ALBUMNAME",
                "Search by approx album name ",
                "LOWER(A.SHORTNAME ) LIKE  ?", true, false, true, true, true);

        addWhereMethod(tClass, "RECORDINGNAME",
                "Search by approx recording name ",
                "LOWER(R.NAME ) LIKE  ?", true, false, true, true, true);

        addWhereMethod(tClass, "DANCE_ID",
                "Search by dance id",
                "R.ID IN (SELECT DRM.RECORDING_ID FROM DANCESRECORDINGSMAP AS DRM WHERE DRM.DANCE_ID = ? )",
                true, false, false, false, false);

        // addWhereMethod(tClass, "SIGNATURE",
        //todo

        addWhereMethod(tClass, "CONFIRMED_MATCH",
                "Search by approx recording name ",
                "R.ID IN (SELECT ARM.RECORDING_ID  FROM LDB.PAIRING AS PR  INNER JOIN ALBUMSRECORDINGSMAP AS ARM ON ARM.ALBUM_ID = PR.SCDDB_ID WHERE PR.PAIRING_OBJECT = 'MUSIC_DIRECTORY' AND PR.PAIRING_STATUS = 'CONFIRMED') ",
                false, false, false, false, false);
        mHM_JoinString.put(tClass, tJoinString);
        mHM_ColumnArray.put(tClass, tColumnArray);
    }

    //////////// LDB Tables ///////////////
    void defineDiagram() {
        Class tClass = Diagram.class;
        String tTableName;
        String tCreateString;
        String tJoinString;
        String[] tColumnArray;
        String tWhereIdString;

        tTableName = "DIAGRAM";

        tCreateString = "CREATE TABLE DIAGRAM (" +
                " _id INTEGER PRIMARY KEY AUTOINCREMENT ,  " +
                " PATH DISPLAY_TEXT , " +
                " AUTHOR DISPLAY_TEXT , " +
                " DANCE_ID INTEGER,  " +
                " BITMAP BLOB " +
                " ) ";

        tWhereIdString = "_id = ? ";

        tJoinString = "LDB.DIAGRAM AS DG";

        tColumnArray = new String[]{
                "DG._ID AS ID",
                "DG.PATH AS PATH",
                "DG.AUTHOR AS AUTHOR",
                "DG.DANCE_ID AS DANCE_ID",
                "DG.BITMAP AS BITMAP"};


        addWhereMethod(tClass, "ID",
                "Search by Database ID",
                "DG._ID = ?", true, false, false, false, false);

        addWhereMethod(tClass, "PATH",
                "Search by path",
                "DG.PATH = ?", true, true, false, false, false);

        addWhereMethod(tClass, "DANCE_ID",
                "Search by dance id ",
                "DG.DANCE_ID = ?", true, false, false, false, false);

        addWhereMethod(tClass, "AUTHOR",
                "Search by approx author ",
                "LOWER(DG.AUTHOR)  LIKE ?", true, false, true, true, true);

        mAR_LdbClass.add(tClass);
        mHM_LdbTableString.put(tClass, tTableName);
        mHM_WhereIdString.put(tClass, tWhereIdString);
        mHM_CreateString.put(tClass, tCreateString);
        mHM_JoinString.put(tClass, tJoinString);
        mHM_ColumnArray.put(tClass, tColumnArray);

    }

    void defineMusicDirectory() {
        Class tClass = MusicDirectory.class;
        String tTableName;
        String tCreateString;
        String tJoinString;
        String[] tColumnArray;
        String tWhereIdString;

        tTableName = "MUSICDIRECTORY";

        tCreateString = "CREATE TABLE MUSICDIRECTORY (" +
                " _id INTEGER PRIMARY KEY AUTOINCREMENT ,  " +
                " PATH DISPLAY_TEXT , " +
                " T1 DISPLAY_TEXT , " +
                " T2 DISPLAY_TEXT , " +
                " ALBUM_ID INTEGER , " +
                " SIGNATURE DISPLAY_TEXT," +
                " MUSICDIRECTORY_PURPOSE DISPLAY_TEXT " +
                " ) ";

        tWhereIdString = "_id = ? ";

        tJoinString = "LDB.MUSICDIRECTORY  AS MD " +
                " LEFT OUTER JOIN ( SELECT T.MUSICDIRECTORY_ID, COUNT(*) AS C FROM LDB.MUSICFILE AS T " +
                "     GROUP BY MUSICDIRECTORY_ID) AS SMF ON SMF.MUSICDIRECTORY_ID = MD._ID  ";

        tColumnArray = new String[]{
                "MD._ID AS _ID",
                "MD.PATH AS PATH",
                "MD.T1 AS T1",
                "MD.T2 AS T2",
                "MD.ALBUM_ID AS ALBUM_ID",
                "MD.SIGNATURE AS SIGNATURE",
                "MD.MUSICDIRECTORY_PURPOSE AS MUSICDIRECTORY_PURPOSE",
                "SMF.C AS COUNT_TRACKS",
                "CASE WHEN  ALBUM_ID IS NULL  THEN 'N' WHEN  ALBUM_ID <= 0 THEN 'N'  ELSE 'J' END AS PAIRING"};


        addWhereMethod(tClass, "ID",
                "Search by Database ID",
                "MD._ID = ?", true, false, false, false, false);

        addWhereMethod(tClass, "PATH",
                "Search by path",
                "MD.PATH = ?", true, true, false, false, false);

        addWhereMethod(tClass, "T2",
                "search by directory name T2",
                "LOWER(MD.T2) LIKE ?", true, false, true, true, true);

        addWhereMethod(tClass, "T1",
                "search by directory name T1",
                "LOWER(MD.T1) LIKE ?", true, false, true, true, true);

        addWhereMethod(tClass, "PAIRING",
                "has pairing",
                "MD.ALBUM_ID <> 0", false, false, true, true, true);

        addWhereMethod(tClass, "NO_PAIRING",
                "search those without a pairing ",
                " MD._ID NOT IN (SELECT PR.LDB_ID FROM LDB.PAIRING AS PR " +
                        " WHERE PR.PAIRING_STATUS <> 'REJECTED' AND " +
                        " PR.PAIRING_OBJECT = 'MUSIC_DIRECTORY' ) ",
                false, false, false, false, false);

        addWhereMethod(tClass, "ALBUM_ID",
                "Search by album id ",
                "MD.ALBUM_ID = ?",
                true, false, false, false, false);

        addWhereMethod(tClass, "SIGNATURE",
                "Search by signature ",
                "MD.SIGNATURE  = ?",
                true, false, false, false, false);

        addWhereMethod(tClass, "PURPOSE",
                "Search by purpose of directory ",
                "MD.MUSICDIRECTORY_PURPOSE  = ?",
                true, false, false, false, false);


        mAR_LdbClass.add(tClass);
        mHM_LdbTableString.put(tClass, tTableName);

        mHM_WhereIdString.put(tClass, tWhereIdString);
        mHM_CreateString.put(tClass, tCreateString);
        mHM_JoinString.put(tClass, tJoinString);
        mHM_ColumnArray.put(tClass, tColumnArray);

    }

    void defineMusicFile() {
        Class tClass = MusicFile.class;
        String tTableName;
        String tCreateString;
        String tJoinString;
        String[] tColumnArray;
        String tWhereIdString;

        tTableName = "MUSICFILE";

        tCreateString = "CREATE TABLE MUSICFILE (" +
                " _id INTEGER PRIMARY KEY AUTOINCREMENT ,  " +
                " PATH DISPLAY_TEXT , " +
                " NAME DISPLAY_TEXT , " +
                " TRACK_NO INTEGER , " +
                " MUSICDIRECTORY_ID INTEGER , " +
                " MEDIA_ID INTEGER, " +
                " FILETYPE DISPLAY_TEXT , " +
                " MUSIC_PURPOSE DISPLAY_TEXT , " +
                " RECORDING_ID INTEGER  ,  " +
                " DANCE_ID INTEGER ,   " +
                " GAIN_AVG REAL  ,  " +
                " GAIN_MAX REAL ,   " +
                " DURATION INT ,   " +
                " FAVOURITE DISPLAY_TEXT  " +
                " ) ";


        tWhereIdString = "_id = ? ";

        tJoinString = "LDB.MUSICFILE AS MF " +
                " LEFT OUTER JOIN LDB.MUSICDIRECTORY  AS MD ON MF.MUSICDIRECTORY_ID = MD._ID " +
                " LEFT OUTER JOIN (	" +
                "       SELECT R.ID AS RECORDING_ID, SDC.C AS COUNT_OF_CRIBS, " +
                "       SDI.C AS COUNT_OF_DIAGRAMS, " +
                "       CASE WHEN SUM(P.RSCDS) > 0 THEN 'Y' ELSE 'N' END AS RSCDS_YN " +
                "     FROM RECORDING AS R " +
                "       INNER JOIN DANCESRECORDINGSMAP AS DRM ON DRM.RECORDING_ID = R.ID " +
                "       LEFT OUTER JOIN ( SELECT DC.DANCE_ID, COUNT(*) AS C FROM DANCECRIB AS DC " +
                "               GROUP BY DC.DANCE_ID )  as SDC ON SDC.DANCE_ID = DRM.DANCE_ID " +
                "       LEFT OUTER JOIN DANCESPUBLICATIONSMAP AS DPM ON DPM.DANCE_ID = DRM.DANCE_ID	" +
                "       LEFT OUTER JOIN PUBLICATION AS P ON P.ID = DPM.PUBLICATION_ID " +
                "       LEFT OUTER JOIN ( SELECT DI.DANCE_ID, COUNT(*) AS C  " +
                "           FROM LDB.DIAGRAM AS DI GROUP BY DI.DANCE_ID )  as SDI ON SDI.DANCE_ID = DRM.DANCE_ID " +
                "       GROUP BY R.ID  ) AS SCDDB ON SCDDB.RECORDING_ID = MF.RECORDING_ID";


        tColumnArray = new String[]{
                "MF._ID AS MF_ID",
                "MF.PATH AS MF_PATH",
                "MF.NAME AS MF_NAME",
                "MF.TRACK_NO AS MF_TRACK_NO",
                "MF.MUSICDIRECTORY_ID AS MF_MUSICDIRECTORY_ID",
                "MD.T2 AS MF_T2",
                "MD.T1 AS MF_T1",
                "MF.MEDIA_ID AS MF_MEDIA_ID",
                "MF.FILETYPE AS MF_FILETYPE",
                "MF.MUSIC_PURPOSE AS MF_MUSIC_PURPOSE",
                "MF.RECORDING_ID AS MF_RECORDING_ID",
                "MF.DANCE_ID AS MF_DANCE_ID",
                "MF.GAIN_MAX AS MF_GAIN_MAX",
                "MF.GAIN_AVG AS MF_GAIN_AVG",
                "MF.DURATION AS MF_DURATION",
                "MF.FAVOURITE AS MF_FAVOURITE",
                "CASE WHEN RECORDING_ID IS NULL THEN 'N' WHEN RECORDING_ID = 0 THEN 'N' ELSE 'J' END AS MF_MATCH",
                "CASE WHEN IFNULL(SCDDB.COUNT_OF_CRIBS, 0)  > 0 THEN 'Y' ELSE 'N' END AS MF_CRIBS_YN",
                "CASE WHEN IFNULL(SCDDB.COUNT_OF_DIAGRAMS, 0)  > 0 THEN 'Y' ELSE 'N' END AS MF_DIAGRAMS_YN",
                "IFNULL( SCDDB.RSCDS_YN, 'N') AS MF_RSCDS_YN",
                "'UNKN' AS MF_FAVOURITE"};


        addWhereMethod(tClass, "ID",
                "Search by Database ID",
                "MF._ID = ?", true, false, false, false, false);

        addWhereMethod(tClass, "PATH",
                "Search by path",
                "MF.PATH = ?", true, true, false, false, false);

        addWhereMethod(tClass, "NAME",
                "Search by approx name ",
                "LOWER(MF.NAME) LIKE ?", true, false, true, true, true);

        addWhereMethod(tClass, "ARTIST",
                "Search by approx artist ",
                "LOWER(MD.T2) LIKE ?", true, false, true, true, true);

        addWhereMethod(tClass, "MEDIA_ID",
                "Search by media id ",
                "MF.MEDIA_ID = ? ", true, false, false, false, false);

        addWhereMethod(tClass, "DIRECTORY",
                "Search by approx directory ",
                "LOWER(MD.T1) LIKE ?", true, false, true, true, true);

        addWhereMethod(tClass, "DIRECTORY_ID",
                "Search by directory id ",
                "MF.RECORDING_ID  = ? ", true, false, false, false, false);

        addWhereMethod(tClass, "DANCE_ID",
                "Search by dance id ",
                "MF.RECORDING_ID IN ( SELECT RECORDING_ID FROM DANCESRECORDINGSMAP WHERE DANCE_ID = ? )",
                true, false, false, false, false);

        addWhereMethod(tClass, "TRACK_NO",
                "Search by Track No ",
                "MF.TRACK_NO = ?",
                true, false, false, false, false);

        addWhereMethod(tClass, "HAS_DANCELINK",
                "search those connected to a dance ",
                "MF.DANCE_ID  <> 0",
                false, false, false, false, false);

        addWhereMethod(tClass, "CONFIRMED_PAIRING",
                "search those who are confirmed pairing ",
                "MF.MUSICDIRECTORY_ID IN (SELECT LDB_ID FROM LDB.PAIRING WHERE PAIRING_OBJECT = 'MUSIC_DIRECTORY' AND PAIRING_STATUS = 'CONFIRMED') ",
                false, false, false, false, false);

        addWhereMethod(tClass, "NO_CONFIRMED_PAIRING",
                "search those who are not in a confirmed pairing ",
                " MF._ID NOT IN (SELECT  TMF._id FROM " +
                        " PAIRING AS PR " +
                        " INNER JOIN MUSICFILE AS TMF on TMF.MUSICDIRECTORY_ID = PR.LDB_ID " +
                        " WHERE PR.PAIRING_OBJECT = \"MUSIC_DIRECTORY\" AND PR.PAIRING_STATUS = \"CONFIRMED\" ) ",
                false, false, false, false, false);

        addWhereMethod(tClass, "NO_DURATION",
                "search those who do not have a stored duration yet ",
                "(DURATION = 0 OR DURATION IS NULL)",
                false, false, false, false, false);

        addWhereMethod(tClass, "LIST_OF_ID",
                "search those with the listed ids  ",
                // todo
                //single = " MF._ID IN ( " + tSearchCriteria.getValue() + " ) ";
                "MF._ID = -5",
                false, false, false, false, false);


        mAR_LdbClass.add(tClass);
        mHM_LdbTableString.put(tClass, tTableName);
        mHM_CreateString.put(tClass, tCreateString);
        mHM_WhereIdString.put(tClass, tWhereIdString);
        mHM_JoinString.put(tClass, tJoinString);
        mHM_ColumnArray.put(tClass, tColumnArray);

    }

    void defineMusicPreference() {
        Class tClass = MusicPreference.class;
        String tTableName;
        String tCreateString;
        String tJoinString;
        String[] tColumnArray;
        String tWhereIdString;

        tTableName = "MUSIC_PREFERENCE";

        tCreateString = "CREATE TABLE MUSIC_PREFERENCE (" +
                " _id INTEGER PRIMARY KEY AUTOINCREMENT ,  " +
                " DANCE_ID INTEGER , " +
                " MUSICFILE_ID INTEGER , " +
                " FAVOURITE STRING,  " +
                " PAIRING_EXIST INTEGER  " +
                " ) ";


        tWhereIdString = "_id = ? ";

        tJoinString = "LDB.MUSIC_PREFERENCE AS MP";

        tColumnArray = new String[]{
                "MP._ID AS MP_ID",
                "MP.DANCE_ID AS MP_DANCE_ID",
                "MP.MUSICFILE_ID AS MP_MUSICFILE_ID",
                "MP.FAVOURITE AS MP_FAVOURITE",
                "MP.PAIRING_EXIST AS MP_PAIRING_EXIST"};


        addWhereMethod(tClass, "ID",
                "Search by Database ID",
                "MP._ID = ?", true, false, false, false, false);

        addWhereMethod(tClass, "DANCE_ID",
                "Search by dance ID",
                "MP.DANCE_ID = ?", true, false, false, false, false);

        addWhereMethod(tClass, "MUSICFILE_ID",
                "Search by musicfile ID",
                "MP.MUSICFILE_ID = ?", true, false, false, false, false);

        mAR_LdbClass.add(tClass);
        mHM_LdbTableString.put(tClass, tTableName);
        mHM_CreateString.put(tClass, tCreateString);

        mHM_WhereIdString.put(tClass, tWhereIdString);
        mHM_JoinString.put(tClass, tJoinString);
        mHM_ColumnArray.put(tClass, tColumnArray);

    }

    void definePairing() {
        Class tClass = Pairing.class;
        String tTableName;
        String tCreateString;
        String tJoinString;
        String[] tColumnArray;
        String tWhereIdString;

        tTableName = "PAIRING";

        tCreateString = " CREATE TABLE PAIRING (" +
                " _id INTEGER PRIMARY KEY AUTOINCREMENT ,  " +
                " PAIRING_OBJECT DISPLAY_TEXT, " +
                " SCDDB_ID INTEGER  ,  " +
                " LDB_ID INTEGER ,   " +
                " PAIRING_SOURCE DISPLAY_TEXT ," +
                " PAIRING_STATUS DISPLAY_TEXT ," +
                " LAST_CHANGE_DATE DATE ," +
                " SCORE REAL," +
                " UNIQUE(SCDDB_ID, LDB_ID) " +
                " ) ";


        tWhereIdString = "_id = ? ";

        tJoinString = " LDB.PAIRING AS PR ";

        tColumnArray = new String[]{
                "PR._id AS ID",
                "PR.PAIRING_OBJECT AS PAIRING_OBJECT",
                "PR.SCDDB_ID AS SCDDB_ID",
                "PR.LDB_ID AS LDB_ID",
                "PR.PAIRING_SOURCE AS PAIRING_SOURCE",
                "PR.PAIRING_STATUS AS PAIRING_STATUS",
                "PR.LAST_CHANGE_DATE AS LAST_CHANGE_DATE",
                "PR.SCORE AS SCORE "};

        addWhereMethod(tClass, "ID",
                "Search by database id ",
                "PR._ID = ? ", true, false, false, false, false);

        addWhereMethod(tClass, "NOT_ID",
                "Search any oother database id ",
                "PR._ID <> ? ", true, false, false, false, false);

        addWhereMethod(tClass, "NOT_REJECTED",
                "Search all not rejected pairings",
                " PR.PAIRING_STATUS <> \"REJECTED\" ", false, false, false, false, false);

        addWhereMethod(tClass, "LDB_ID",
                "Search by Ldb Object id ",
                " PR.LDB_ID = ? ", true, false, false, false, false);

        addWhereMethod(tClass, "SCDDB_ID",
                "Search by Scddb Object id ",
                " PR.SCDDB_ID = ? ", true, false, false, false, false);

        addWhereMethod(tClass, "PAIRING_OBJECT",
                "Search by pairing object type ",
                " PR.PAIRING_OBJECT = ? ", true, false, false, false, false);

        addWhereMethod(tClass, "PAIRING_STATUS",
                "Search by pairing status ",
                " PR.PAIRING_STATUS = ? ", true, false, false, false, false);

        mAR_LdbClass.add(tClass);
        mHM_LdbTableString.put(tClass, tTableName);
        mHM_WhereIdString.put(tClass, tWhereIdString);
        mHM_CreateString.put(tClass, tCreateString);
        mHM_JoinString.put(tClass, tJoinString);
        mHM_ColumnArray.put(tClass, tColumnArray);

    }

    void definePlayinstruction() {
        Class tClass = Playinstruction.class;
        String tTableName;
        String tCreateString;
        String tJoinString;
        String[] tColumnArray;
        String tWhereIdString;

        tTableName = "PLAY_INSTRUCTION";

        tCreateString = "CREATE TABLE PLAY_INSTRUCTION (" +
                " _id INTEGER PRIMARY KEY AUTOINCREMENT ,  " +
                " MUSICFILE_ID INTEGER , " +
                " RECORDING_ID INTEGER  ,  " +
                " DANCE_ID INTEGER ,   " +
                " VOLUME_FACTOR REAL ," +
                " START_MS INTEGER , " +
                " END_MS INTEGER,  " +
                " PLAYLIST_ID INTEGER, " +
                " PLAYLIST_POS INTEGER " +
                " ) ";


        tWhereIdString = "_id = ? ";

        tJoinString = " LDB.PLAY_INSTRUCTION AS PI " +
                " INNER JOIN LDB.MUSICFILE AS MF ON MF._ID = PI.MUSICFILE_ID " +
                " LEFT OUTER JOIN LDB.MUSICDIRECTORY  AS MD ON MF.MUSICDIRECTORY_ID = MD._ID" +
                " LEFT OUTER JOIN DANCE AS D ON D.ID = PI.DANCE_ID";

        tColumnArray = new String[]{
                "PI._id AS ID",
                "PI.MUSICFILE_ID AS MUSICFILE_ID",
                "PI.RECORDING_ID AS RECORDING_ID",
                "PI.DANCE_ID AS DANCE_ID",
                "PI.VOLUME_FACTOR AS VOLUME_FACTOR",
                "PI.START_MS AS START_MS",
                "PI.END_MS AS END_MS",
                "PI.PLAYLIST_ID AS PLAYLIST_ID",
                "PI.PLAYLIST_POS AS PLAYLIST_POS",
                "MD.T2 AS T2",
                "MD.T1 AS T1",
                "MF.PATH AS MF_PATH",
                "MF.NAME AS MF_NAME",
                "MF.MEDIA_ID AS MF_MEDIA_ID",
                "MF.FILETYPE AS FILETYPE",
                "MF.MUSIC_PURPOSE AS MUSIC_PURPOSE",
                "MF.RECORDING_ID AS MF_RECORDING_ID",
                "MF.DANCE_ID AS DANCE_ID",
                "MF.GAIN_MAX AS GAIN_MAX",
                "MF.GAIN_AVG AS GAIN_AVG",
                "MF.DURATION AS DURATION",
                "MF.FAVOURITE AS FAVOURITE",
                "IFNULL(D.NAME, '') AS DANCE_NAME"};

        addWhereMethod(tClass, "PLAYLIST_ID",
                "Search by playlist id ",
                "PI.PLAYLIST_ID = ? ", true, false, false, false, false);

        mAR_LdbClass.add(tClass);
        mHM_LdbTableString.put(tClass, tTableName);

        mHM_WhereIdString.put(tClass, tWhereIdString);
        mHM_CreateString.put(tClass, tCreateString);
        mHM_JoinString.put(tClass, tJoinString);
        mHM_ColumnArray.put(tClass, tColumnArray);

    }

    void definePlaylist() {
        Class tClass = Playlist.class;
        String tTableName;
        String tCreateString;
        String tJoinString;
        String[] tColumnArray;
        String tWhereIdString;

        tTableName = "PLAYLIST";

        tCreateString = "CREATE TABLE PLAYLIST (" +
                " _id INTEGER PRIMARY KEY AUTOINCREMENT ,  " +
                " NAME STRING UNIQUE, " +
                " PRIORITY INTEGER , " +
                " LAST_EDIT_DATE DATE , " +
                " LAST_DATE_USED DATE , " +
                " STATUS STRING " +
                " ) ";


        tWhereIdString = "_id = ? ";

        tJoinString = " LDB.PLAYLIST AS PL" +
                " LEFT OUTER JOIN ( SELECT PLAYLIST_ID , COUNT(*) AS C " +
                " FROM LDB.PLAY_INSTRUCTION GROUP BY PLAYLIST_ID ) AS SPL ON SPL.PLAYLIST_ID = PL._ID ";


        tColumnArray = new String[]{
                "PL._ID as ID",
                "PL.NAME AS NAME",
                "PL.LAST_EDIT_DATE AS LAST_EDIT_DATE",
                "PL.LAST_DATE_USED AS LAST_DATE_USED",
                "PL.STATUS AS STATUS",
                "IFNULL(SPL.C, 0) AS COUNT_PLAY_INSTRUCTION"};


        addWhereMethod(tClass, "ID",
                "Search by Database ID",
                "PL._ID = ?", true, false, false, false, false);

        addWhereMethod(tClass, "NAME",
                "Search by name",
                "LOWER(PL.NAME) LIKE  ? ", true, false, false, false, false);

        addWhereMethod(tClass, "OTHER_PURPOSE",
                "Search by purpose undefined",
                "PL.STATUS IN ('TMP', 'UNDEFINED' , '')", false, false, false, false, false);

//        addWhereMethod(tClass, "PURPOSE",
//                "Search by name",
//                "LOWER(PL.NAME) LIKE  ? ", true, false, false, false, false);
//        single = tSearchCriteria.getValue();
        // todo


        mAR_LdbClass.add(tClass);
        mHM_LdbTableString.put(tClass, tTableName);

        mHM_WhereIdString.put(tClass, tWhereIdString);
        mHM_CreateString.put(tClass, tCreateString);
        mHM_JoinString.put(tClass, tJoinString);
        mHM_ColumnArray.put(tClass, tColumnArray);

    }

    // public methods

    public String getCreateString(Class iClass) {
        // Logg.i(TAG, "get table for " + iClass.getSimpleName());
        if (mHM_CreateString.containsKey(iClass)) {
            return mHM_CreateString.get(iClass);
        }
        throw new RuntimeException("Missing contract information about the create string for class " +
                iClass.getSimpleName());
    }


    public String getTableString(Class iClass) {
        // Logg.i(TAG, "get table for " + iClass.getSimpleName());
        if (mHM_LdbTableString.containsKey(iClass)) {
            return mHM_LdbTableString.get(iClass);
        }
        throw new RuntimeException("Missing contract information about the table for class " +
                iClass.getSimpleName());
    }

    public String getWhereIdString(Class iClass) {
        // Logg.i(TAG, "get table for " + iClass.getSimpleName());
        if (mHM_WhereIdString.containsKey(iClass)) {
            return mHM_WhereIdString.get(iClass);
        }
        throw new RuntimeException("Missing contract information about the WhereId for class " +
                iClass.getSimpleName());
    }


    public String getJoinString(Class iClass) {
        // Logg.i(TAG, "get table for " + iClass.getSimpleName());
        if (mHM_JoinString.containsKey(iClass)) {
            return mHM_JoinString.get(iClass);
        }
        throw new RuntimeException("Missing contract information about the join for class " +
                iClass.getSimpleName());
    }


    public String[] getColumnArray(Class iClass) {
        //Logg.i(TAG, "get columns for " + iClass.getSimpleName());
        if (mHM_ColumnArray.containsKey(iClass)) {
            return mHM_ColumnArray.get(iClass);
        }
        throw new RuntimeException("Missing contract information about the columns for class " +
                iClass.getSimpleName());
    }

    public DictionaryWhereMethod getWhereMethod(Class iClass, String iMethod) {
        for (DictionaryWhereMethod lDictionaryWhereMethod : mAR_DictionaryWhereMethod) {
            if (lDictionaryWhereMethod.mClass.getCanonicalName()
                    .equals(iClass.getCanonicalName())) {
                if (lDictionaryWhereMethod.mMethod.equals(iMethod)) {
                    return lDictionaryWhereMethod;
                }
            }
        }
        throw new RuntimeException(String.format(Locale.ENGLISH,
                "Missing contract information about the where method %s for class %s ",
                iMethod, iClass.getSimpleName()));
    }

    public ArrayList<Class> getAR_LdbClass() {
        return mAR_LdbClass;
    }


    public ArrayList<DictionaryWhereMethod> getAR_DictionaryWhereMethod() {
        return mAR_DictionaryWhereMethod;
    }

}
