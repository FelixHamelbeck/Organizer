package org.pochette.data_library.diagram;

import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;

import org.pochette.data_library.search.SearchCall;
import org.pochette.data_library.search.WriteCall;
import org.pochette.utils_lib.logg.Logg;
import org.pochette.utils_lib.search.SearchCriteria;
import org.pochette.utils_lib.search.SearchPattern;

import java.io.FileNotFoundException;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.documentfile.provider.DocumentFile;

import static java.lang.StrictMath.max;

/**
 * This class represents the dance diagrams as locally stored and provide downloads for strathspey.org
 */
@SuppressWarnings({"unused", "FieldCanBeLocal", "RedundantThrows", "FieldMayBeFinal"})
public class Diagram {

    private static String TAG = "FEHA (Diagram)";
    private static final int MINIMUM_BYTE = 999;
    @SuppressWarnings("WeakerAccess")
    public static Date mLastHouseCleaning;
    private static DocumentFile mDirectory;

    int mId;
    String mPath;
    String mAuthor;
    Bitmap mBitmap; // this should always correspond to the original, ie 900 wide
    int mDanceId = 0;

    //<editor-fold desc="Constructor">
    public Diagram(String iPath) throws FileNotFoundException {
        if (iPath == null) {
            throw new RuntimeException("No path provided for constructor");
        }
        mPath = iPath;
//        DocumentFile tDocumentFile = DocumentFile.fromTreeUri(
//                MyPreferences.getContext(),
//                Uri.parse(mPath));
//        if (Objects.requireNonNull(tDocumentFile).exists()) {
//            String tFilename = tDocumentFile.getName();
//            String[] parts = Objects.requireNonNull(tFilename).split("\\.");
//            try {
//                mDanceId = Integer.parseInt(parts[0]);
//            } catch (NumberFormatException e) {
//                Logg.e(TAG, e.toString());
//            }
//            mAuthor = parts[1];
//        } else {
//            throw new FileNotFoundException(mPath + "not found");
//        }
        mBitmap = null;
        this.save();
    }

    public Diagram(DocumentFile iDocumentFile) throws FileNotFoundException {
        mPath = iDocumentFile.toString();
        if (mPath.isEmpty()) {
            throw new FileNotFoundException();
        }
        String tFilename = iDocumentFile.getName();
        String[] parts = Objects.requireNonNull(tFilename).split("\\.");
        try {
            mDanceId = Integer.parseInt(parts[0]);
        } catch (NumberFormatException e) {
            Logg.e(TAG, e.toString());
        }
        mAuthor = parts[1];
        Uri tUri = iDocumentFile.getUri();

//        try {
//            //noinspection deprecation
//            mBitmap = MediaStore.Images.Media.getBitmap(
//                    MyPreferences.getContext().getContentResolver(), tUri);
//
//        } catch (IOException e) {
//            Logg.w(TAG, e.toString());
//        }
        this.save();
    }


    public Diagram(int tId, String tPath, String tAuthor, int tDanceId, Bitmap tBitmap) {
        mId = tId;
        mPath = tPath;
        mAuthor = tAuthor;
        mDanceId = tDanceId;
        mBitmap = tBitmap;
    }

    //</editor-fold>

    //<editor-fold desc="Setter and Getter">
    public int getId() {
        return mId;
    }

    public void setId(int mId) {
        this.mId = mId;
    }

    @SuppressWarnings("unused")
    public String getAuthor() {
        return mAuthor;
    }

    @SuppressWarnings("unused")
    public void setAuthor(String mAuthor) {
        this.mAuthor = mAuthor;
    }

    public String getPath() {
        return mPath;
    }

    public void setPath(String mPath) {
        this.mPath = mPath;
    }

    public int getDanceId() {
        return mDanceId;
    }

    public void setDanceId(int mDanceId) {
        this.mDanceId = mDanceId;
    }

    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

//    public DocumentFile getDocumentFile() throws FileNotFoundException {
//        String tFilename = String.format(Locale.ENGLISH, "%06d.%s.png", mDanceId, mAuthor);
//        DocumentFile tDiagramDirectory = Diagram.getDiagramDirectory();
//        DocumentFile tDocumentFile = Objects.requireNonNull(tDiagramDirectory).findFile(tFilename);
//        if (Objects.requireNonNull(tDocumentFile).exists() && tDocumentFile.isFile()) {
//            return tDocumentFile;
//        } else {
//            throw new FileNotFoundException("Could not find " + tDocumentFile);
//        }
//    }


//    public Bitmap getLowLevelBitmap() throws FileNotFoundException {
//        Bitmap tBitmap = null;
//        DocumentFile tDocumentFile = null;
//        Uri tUri = null;
//
//        String tFilename = String.format(Locale.ENGLISH, "%06d.%s.png", mDanceId, mAuthor);
//
//        if (tFilename == null || tFilename.isEmpty()) {
//            Logg.w(TAG, "could not construct Filename");
//            return null;
//        }
//
//        DocumentFile tDirectory = Diagram.getDiagramDirectory();
//        if (tDirectory == null || !tDirectory.exists()) {
//            Logg.w(TAG, "could not get Directory");
//            return null;
//        }
//        tDocumentFile = Objects.requireNonNull(tDirectory).findFile(tFilename);
//        if (tDocumentFile == null || !tDocumentFile.exists() && tDirectory.isFile()) {
//            Logg.w(TAG, "" + mDanceId+"."+ mAuthor);
//            Logg.w(TAG, "could not get DocumentFile");
//            return null;
//        }
//        tUri = tDocumentFile.getUri();
//        if (tUri == null ) {
//            Logg.w(TAG, "could not get uri");
//            return null;
//        }
//
//
//        try {
//            tBitmap = MediaStore.Images.Media.getBitmap(
//                    MyPreferences.getContext().getContentResolver(), tUri);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        if (tBitmap == null || tBitmap.getWidth() == 0) {
//            Logg.w(TAG, "could not get a good Bitmap");
//            return null;
//        }
//
//        return tBitmap;
//    }

    //</editor-fold>


    @NonNull
    public String toString() {
        return String.format(Locale.ENGLISH, "%d for %d[%s]: %s", mId, mDanceId, mAuthor, mPath.substring(max(0, mPath.length() - 20)));
    }

    public static Diagram getByDanceId(int tDanceId) {
        SearchPattern tSearchPattern = new SearchPattern(Diagram.class);
        SearchCriteria tSearchCriteria = new SearchCriteria("DANCE_ID", "" + (tDanceId));
        tSearchPattern.addSearch_Criteria(tSearchCriteria);
        SearchCall tSearchCall = new SearchCall(
                Diagram.class, tSearchPattern, null);
        Diagram tDiagram;
        tDiagram = tSearchCall.produceFirst();

        return tDiagram;
    }


//    public static DocumentFile getDiagramDirectory() {
//        if (mDirectory != null) {
//            return mDirectory;
//
//        }
//        //     String tKey = MyPreferences.PREFERENCE_KEY_DIAGRAM_DIRECTORY_URI;
//        String tDirectoryPath;
//        String tDefaultDirectoryPath;
//        File tFile = Scddb_File.getScddbFile();
//        tFile = tFile.getParentFile();
//        if (tFile.getName().toLowerCase().contains("database")) {
//
//            tFile = tFile.getParentFile();
//        }
//        //tFile = tFile.
//
//
////        tDirectoryPath = MyPreferences.getPreferenceString(tKey, "");
////        if (tDirectoryPath.isEmpty()) {
////            return null;
////        }
////        Uri tUri = Uri.parse(tDirectoryPath);
////        DocumentFile tDocumentFile = DocumentFile.fromTreeUri(MyPreferences.getContext(), tUri);
//        DocumentFile tDocumentFile = DocumentFile.fromFile(tFile);
//        tDocumentFile = tDocumentFile.createDirectory("diagram");
//
//        if (!Objects.requireNonNull(tDocumentFile).exists()) {
//            DocumentFile tParent = tDocumentFile.getParentFile();
//            String tName = tDocumentFile.getName();
//            tDocumentFile = Objects.requireNonNull(tParent).createDirectory(Objects.requireNonNull(tName));
//            if (tDocumentFile == null) {
//                Logg.w(TAG, "Could not create the diagram dir at " + tDocumentFile.toString());
//                throw new RuntimeException("Could not create the diagram dir ");
//            }
//        }
//        mDirectory = tDocumentFile;
//        return mDirectory;
//    }


    //<editor-fold desc="HouseCleaning">

//    /**
//     * This method get the diagram data into good shape
//     * 1) Files which are too small, will be removed from disk
//     * 2) MetaFile Data in Ldb is synchronized with Files
//     */

//    @SuppressWarnings("unused")
//    public static void requestHouseCleaning(Shouting iShouting) {
//        Logg.i(TAG, "requestHouseCleaning");
//        AsyncExecuteHouseCleaning tAsyncExecuteHouseCleaning = new AsyncExecuteHouseCleaning();
//        tAsyncExecuteHouseCleaning.mShouting = iShouting;
//        tAsyncExecuteHouseCleaning.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//    }

//    private static class AsyncExecuteHouseCleaning extends AsyncTask<URL, Void, String> {
//        Shouting mShouting;
//
//        @Override
//        protected String doInBackground(URL... urls) {
//            Thread.currentThread().setName("AsyncExecuteHouseCleaning");
//            // Step 0
////			download(13588);
////			download(6373);
////			download(5525);
////			download(5525);
////			download(11543);
//
//            // Step 1: Preparation read Diagram and Dance with music from the DB, read the files available from disk
//            SearchPattern tSearchPattern;
//            SearchCall tSearchCall;
//
//            ArrayList<Diagram> tAR_Diagram; // Diagramm data on the DB
//            ArrayList<Dance> tAR_Dance; // Dances with music on the DB
//            ArrayList<DocumentFile> tAL_Images; // image files as on disk
//            HashMap<String, Integer> tHM_Images_PathDanceId = new HashMap<>(0);
//            // image files as hashtable path, dance_id
//
//            tSearchPattern = new SearchPattern(Diagram.class);
//            tSearchCall = new SearchCall(Diagram.class, tSearchPattern, null);
//            tAR_Diagram = tSearchCall.produceArrayList();
//
//            tSearchPattern = new SearchPattern(Dance.class);
//            tSearchPattern.addSearch_Criteria(new SearchCriteria("MUSIC_REQUIRED", ""));
//            tSearchCall = new SearchCall(Dance.class, tSearchPattern, null);
//            tAR_Dance = tSearchCall.produceArrayList();
//
//            tAL_Images = new ArrayList<>(0);
//            DocumentFile tDF_Directory = Diagram.getDiagramDirectory();
//            //String tDirectoryPath = Diagram.getDiagramDirectory();
//            //File tDFirectory = new File(tDirectoryPath);
//            if (!(Objects.requireNonNull(tDF_Directory).exists() && tDF_Directory.isDirectory())) {
//                Logg.w(TAG, "Why is " + tDF_Directory + "not a directory?");
//                return "";
//            } else {
//                Logg.i(TAG, " Look in " + tDF_Directory);
//                for (DocumentFile lDocumentFile : tDF_Directory.listFiles()) {
//                    String tName = lDocumentFile.getName();
//                    Logg.i(TAG, tName);
//                    if (Objects.requireNonNull(tName).endsWith("png")) {
//                        tAL_Images.add(lDocumentFile);
//                    }
//                }
//            }
//
//
//            // Step 2 Quality Control on files
//            {
//                //noinspection unchecked
//                ArrayList<DocumentFile> sAL_Images =
//                        (ArrayList<DocumentFile>) Objects.requireNonNull(tAL_Images).clone();
//                for (DocumentFile lDocumentFile : sAL_Images) {
//                    boolean tHealthy = true;
//
//                    String tFileName = lDocumentFile.getName();
//                    int tDanceId = 0;
//                    try {
//                        tDanceId = Integer.parseInt(Objects.requireNonNull(tFileName).substring(0, 6));
//                        //Logg.i(TAG, "id" + tDanceId);
//                    } catch (NumberFormatException e) {
//                        Logg.w(TAG, e.toString());
//                        tHealthy = false;
//                    }
//                    if (lDocumentFile.length() < MINIMUM_BYTE) {
//                        tHealthy = false;
//                    }
//                    if (tHealthy) {
//                        tHM_Images_PathDanceId.put(lDocumentFile.toString(), tDanceId);
//                    } else {
//                        lDocumentFile.delete();
//                        tAL_Images.remove(lDocumentFile);
//                        tHM_Images_PathDanceId.remove(lDocumentFile.toString());
//                    }
//                }
//                //noinspection unchecked,UnusedAssignment
//                tAL_Images = (ArrayList<DocumentFile>) sAL_Images.clone();
//            }
//
//            // Step 3 Are all DB entries in the file list, if not delete on DB
//            {
//                //noinspection unchecked
//                ArrayList<Diagram> sAR_Diagram = (ArrayList<Diagram>) tAR_Diagram.clone();
//                for (Diagram lDiagram : sAR_Diagram) {
//                    if (!tHM_Images_PathDanceId.containsKey(lDiagram.mPath)) {
//                        //Logg.i(TAG, "in DB but not on File: " + lDiagram.toString());
//                        lDiagram.delete();
//                        sAR_Diagram.remove(lDiagram);
//                        tHM_Images_PathDanceId.remove(lDiagram.mPath);
//                    }
//                }
//                //noinspection unchecked
//                tAR_Diagram = (ArrayList<Diagram>) sAR_Diagram.clone();
//            }
//
//            // Step 4 Are all files in the Database?
//            for (String lPath : tHM_Images_PathDanceId.keySet()) {
//                boolean tFound = false;
//                for (Diagram lDiagram : tAR_Diagram) {
//                    if (lPath.equals(lDiagram.mPath)) {
//                        tFound = true;
//                        break;
//                    }
//                }
//                if (!tFound) {
//                    try {
//                        //Logg.i(TAG, lPath + " found but not in DB -> save");
//                        DocumentFile tDocumentFile = DocumentFile.fromTreeUri(MyPreferences.getContext(),
//                                Uri.parse(lPath));
//                        Diagram tDiagram = new Diagram(Objects.requireNonNull(tDocumentFile));
//                        tAR_Diagram.add(tDiagram);
//                        tDiagram.save();
//                    } catch (FileNotFoundException e) {
//                        Logg.e(TAG, e.toString());
//                    }
//                }
//            }
//
//            // Step 5 place request for dance with music no diagram and no open getDiagram Task
//            Logg.i(TAG, "Start step 5");
//            for (Dance lDance : tAR_Dance) {
//                if (!tHM_Images_PathDanceId.containsValue(lDance.mId)) {
//                    Logg.w(TAG, "GetDiagramTask switched off");
//
//                    DiagramTask.createTask(lDance.mId, false);
//                }
//            }
//
//            tSearchPattern = new SearchPattern(Task.class);
//            tSearchPattern.addSearch_Criteria(
//                    new SearchCriteria("TASK_NAME", "GetDiagramTask"));
//            tSearchPattern.addSearch_Criteria(
//                    new SearchCriteria("OPEN", ""));
//            tSearchCall = new SearchCall(Task.class, tSearchPattern, null);
//            ArrayList<Task> tAR_Task = tSearchCall.produceArrayList();
//            HashSet<Integer> tHS_Task = new HashSet<>(0);
//            for (Task lTask : tAR_Task) {
//                try {
//                    int tDance_id;
//                    tDance_id = DiagramTask.getScddbDanceId(lTask.mJsonString);
//                    tHS_Task.add(tDance_id);
//                } catch (IllegalArgumentException e) {
//                    Logg.w(TAG, e.toString());
//                }
//            }
//            Logg.i(TAG, "tasks" + tAR_Task.size() + " " + tHS_Task.size());
//            for (Dance lDance : tAR_Dance) {
//                boolean tTaskNeeded = true;
//                // i already got at least one diagram
//                for (Diagram lDiagram : tAR_Diagram) {
//                    if (lDiagram.mDanceId == lDance.mId) {
//                        tTaskNeeded = false;
//                        break;
//                    }
//                }
//                if (!tTaskNeeded) {
//                    continue;
//                }
//                if (tHS_Task.contains(lDance.mId)) {
//                    continue;
//                }
//                DiagramTask.createTask(lDance.mId, false);
//                //Logg.i(TAG, "Create Task for " + lDance.mName);
//            }
//            Logg.i(TAG, "done");
//            mLastHouseCleaning = Calendar.getInstance().getTime();
//            return "";
//        }
//    }

    //</editor-fold>


    /**
     * This function reads all the available diagrams into the database
     * Do not delete, it is call from Setting as defined in SettingControl
     */
    @SuppressWarnings("unused")
//    public static void loadDirectory() {
//        String rText;
//        String tPath = Diagram.getDiagramDirectory();
//        File storagePath = new File(tPath);
//        Logg.i(TAG, storagePath.toString());
//        Diagram.purge();
//        int i = 0;
//        File[] tL_File = storagePath.listFiles();
//
//        rText = String.format(Locale.ENGLISH, "Found %d files in %s",
//                Objects.requireNonNull(tL_File).length, storagePath);
//        ReportSystem.receive(rText);
//        for (File tDiagramFile : tL_File) {
//            try {
//                Diagram tScddbDiagram = new Diagram(tDiagramFile.getAbsolutePath());
//            } catch (FileNotFoundException e) {
//                Logg.e(TAG, tDiagramFile.toString());
//                Logg.e(TAG, e.toString());
//            }
//            if ((i % 250) == 0) {
//                rText = String.format(Locale.ENGLISH, "Load Diagrams from Directory: %5d of %5d done",
//                        i, tL_File.length);
//                ReportSystem.receive(rText);
//            }
//            i++;
//        }
//
//        rText = String.format(Locale.ENGLISH, "Load Diagrams from Directory: %5d of %5d finished",
//                i, tL_File.length);
//        ReportSystem.receive(rText);
//
////		Logg.i(TAG, "Ldb Diagram Size: " +
////				Objects.requireNonNull(Scddb_Helper.getInstance()).getSize("LDB.DIAGRAM"));
//    }


//    public static void requestPopular(Shouting iShouting) {
//        Logg.i(TAG, "requestPopular");
//        AsyncExecuteRequestByPublication tAsyncExecuteRequestByPublication = new AsyncExecuteRequestByPublication();
//        tAsyncExecuteRequestByPublication.mPublicationId = 342; // 342 = Thirty Popular, count 30
//        //tAsyncExecuteRequestByPublication.mPublicationId = 775; //"775"	"Guide to Scottish Country Dancing (ex-Collins), A", count 109
//        //tAsyncExecuteRequestByPublication.mPublicationId = 1407; // "1407"	"Scottish Country Dances in Diagrams. Ed. 9", count 584
//
//        tAsyncExecuteRequestByPublication.mShouting = iShouting;
//        tAsyncExecuteRequestByPublication.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//    }
//
//    private static class AsyncExecuteRequestByPublication extends AsyncTask<URL, Void, String> {
//        Shouting mShouting;
//        int mPublicationId = 0;
//
//        @Override
//        protected String doInBackground(URL... urls) {
//            Thread.currentThread().setName("AsyncExecuteRequestByPublication");
//            //MaintenanceSystem.rememberThread(Thread.currentThread());
//            SearchPattern tSearchPattern = new SearchPattern(Dance.class);
//            tSearchPattern.addSearch_Criteria(
//                    new SearchCriteria("PUBLICATION_ID",
//                            String.format(Locale.ENGLISH, "%d", mPublicationId)));
//            SearchCall tSearchCall =
//                    new SearchCall(Dance.class, tSearchPattern, null);
//            ArrayList<Dance> tAR_Dance = tSearchCall.produceArrayList();
//            for (Dance lDance : tAR_Dance) {
//                Logg.i(TAG, "create for " + lDance.mName);
//
//                DiagramTask.createTask(lDance.mId, false);
//            }
//            return "";
//        }
//
//
//        protected void onPostExecute(String iResult) {
//            super.onPostExecute(iResult);
//            Logg.i(TAG, "onPost:" + iResult);
//            if (mShouting != null) {
//                Shout tShout = new Shout("AsyncExecuteRequestByPublication");
//                tShout.mLastObject = "Execution";
//                if (!iResult.equals("")) {
//                    tShout.mLastAction = "failed";
//                } else {
//                    tShout.mLastAction = "finished";
//                }
//                Logg.d(TAG, tShout.toString());
//                mShouting.shoutUp(tShout);
//            }
//        }
//
//    }

//    /**
//     * This methods downloads the diagram for strathspey.org
//     * All three authors kr, heiko, scddb are copied
//     *
//     * @param tDanceId Dance Id for which diagrams will be downloaded
//     */

//    public static void download(int tDanceId) throws FileNotFoundException {
//        DocumentFile tDirectory = Diagram.getDiagramDirectory();
//        URL url;
//        if (tDirectory == null) {
//            throw new FileNotFoundException("Null from File ");
//        }
//        if (!tDirectory.exists()) {
//            Logg.w(TAG, "Directory does not exist " + tDirectory.getName());
//            return;
//        }
//        if (!tDirectory.isDirectory()) {
//            Logg.w(TAG, "Not a directory" + tDirectory.getName());
//            return;
//        }
//
//        boolean tDownloadFound;
//        String[] authors = {"kr", "heiko", "scddb"};
//        for (String author : authors) {
//            String tFileName;
//            String tFileNameNoSuffix;
//            // Prepare input
//            String url_string = String.format(Locale.ENGLISH,
//                    "https://my.strathspey.org/dd/diagram/%s/%d/?f=png;w=900",
//                    author, tDanceId);
//            url = null;
//            try {
//                url = new URL(url_string);
//            } catch (MalformedURLException e) {
//                Logg.e(TAG, "Malformed: " + e.toString());
//            }
//            if (url == null) {
//                Logg.d(TAG, "Throw no url: " + url_string);
//                throw new AndroidRuntimeException("No url");
//            }
//            // perpare local storage
//            tFileName = String.format(Locale.ENGLISH, "%06d.%s.png", tDanceId, author);
//            tFileNameNoSuffix = String.format(Locale.ENGLISH, "%06d.%s", tDanceId, author);
//            DocumentFile tFile = tDirectory.createFile("image/png", tFileNameNoSuffix);
//            // Logg.i(TAG, Objects.requireNonNull(tFile).getName());
//            Uri tUriOutput = Objects.requireNonNull(tFile).getUri();
//            //String tFilePath = tDirectoryPath + "/" + tFileName;
//            // get the file, if possible
//            InputStream input = null;
//            OutputStream output = null;
//            try { // this try is concerned with the web file
//                input = url.openStream();
//                if (input != null) {
//                    try {
//
//                        output = MyPreferences.getContext().getContentResolver().openOutputStream(tUriOutput);
//                        // this try covers the writing and the reading
//                        byte[] buffer = new byte[1024];
//                        int bytesRead;
//                        if (input != null) {
//                            while ((bytesRead = input.read(buffer, 0, buffer.length)) >= 0) {
//                                //noinspection ConstantConditions
//                                output.write(buffer, 0, bytesRead);
//                            }
//                        }
//                        tDownloadFound = true;
//                    } catch (Exception e) {
//                        tDownloadFound = false;
//                        Logg.e(TAG, e.toString());
//                    }
//                } else {
//                    tDownloadFound = false;
//                }
//            } catch (IOException e) {
//                tDownloadFound = false;
//                Logg.i(TAG, "Diagram not available: " + url_string);
//
//            } catch (Exception e) {
//                tDownloadFound = false;
//                Logg.e(TAG, e.toString());
//            } finally {
//                if (input != null) {
//                    try {
//                        input.close();
//                    } catch (IOException e) {
//                        Logg.w(TAG, e.toString());
//                    }
//                }
//                if (output != null) {
//                    try {
//                        output.close();
//                    } catch (IOException e) {
//                        Logg.w(TAG, e.toString());
//                    }
//                }
//            }
//
//
//            DocumentFile tControlFile = tDirectory.findFile(tFileName);
//            if (tControlFile != null && Objects.requireNonNull(tControlFile).exists()) {
//                if (tControlFile.length() < Diagram.MINIMUM_BYTE) {
//                    // Logg.i(TAG, "File too small");
//                    tControlFile.delete();
//                }
//            }
//            if (tDownloadFound) {
//                // now save this data to the database
//                Diagram tDiagram = new Diagram(tControlFile);
//                tDiagram.save();
//
//                Uri tUri = tFile.getUri();
//                Bitmap tBitmap;
//                try {
//                    //noinspection deprecation
//                    tBitmap = MediaStore.Images.Media.getBitmap(
//                            MyPreferences.getContext().getContentResolver(), tUri);
//                    if (tBitmap != null) {
//                        // Logg.i(TAG, "got bitmap" + tBitmap.getWidth());
//                        tDiagram.setBitmap(tBitmap);
//                        tDiagram.save();
//                        break;
//                    }
//                } catch (IOException e) {
//                    Logg.w(TAG, e.toString());
//                }
//            }
//            if (tDownloadFound) {
//                Logg.i(TAG, "Break");
//                break;
//            }
//
//        }
//
//
//    }

//    public static Bitmap getBitmap(int iDiagramWidth, int iDanceId, Shouting iShouting) {
//        Diagram tDiagram = Diagram.getByDanceId(iDanceId);
//        Bitmap tOrginialBitmap;
//        Bitmap tBitmap;
//        int tHeight;
//        if (iDiagramWidth <= 0) {
//            return null;
//        }
//        if (tDiagram == null) {
//            return null;
//        }
//        try {
//            if (tDiagram.mDanceId == 2691) {
//                Logg.i(TAG, "catch BP");
//            }
//            tOrginialBitmap = tDiagram.getLowLevelBitmap();
//            if (tOrginialBitmap == null) {
//                Logg.w(TAG, "OrgBM = null");
//                return null;
//            }
//            tHeight = iDiagramWidth * tOrginialBitmap.getHeight() / tOrginialBitmap.getWidth();
//            tBitmap = Bitmap.createScaledBitmap(tOrginialBitmap, iDiagramWidth, tHeight, true);
//        } catch (Exception e) {
//            Logg.e(TAG, e.toString());
//            return null;
//        }
//
//        Logg.v(TAG, "end of scale " + iDanceId);
//        return tBitmap;
//
//    }

    //<editor-fold desc="Database stuff">
    public Diagram save() {
        WriteCall tWriteCall = new WriteCall(Diagram.class, this);
        if (mId <= 0) {
            mId = tWriteCall.insert();
        } else {
            tWriteCall.update();
        }
        return this;
    }

    public ContentValues getContentValues() {
        ContentValues tContentValues = new ContentValues();
        tContentValues.put("PATH",mPath);
        tContentValues.put("AUTHOR", mAuthor);
        tContentValues.put("DANCE_ID", mDanceId);
        if (mBitmap != null) {
            // todo
//            byte[] tByteArray = Bitmapy2ByteArray(tScddbDiagram.mBitmap);
//            if (tByteArray.length > 100) {
//                tContentValues.put("BITMAP", tByteArray);
//            }
        }
        return tContentValues;
    }

    public static Diagram convertCursor(Cursor tCursor) {
        Diagram result;
        Bitmap tBitmap = null;
        byte[] tByteArray = tCursor.getBlob(tCursor.getColumnIndex("BITMAP"));
//        if (tByteArray != null && tByteArray.length > 100) {
//            tBitmap = ByteArray2Bitmap(tByteArray);
//        }
        // todo

        result = new Diagram(
                tCursor.getInt(tCursor.getColumnIndex("ID")),
                tCursor.getString(tCursor.getColumnIndex("PATH")),
                tCursor.getString(tCursor.getColumnIndex("AUTHOR")),
                tCursor.getInt(tCursor.getColumnIndex("DANCE_ID")),
                tBitmap);

        return result;
    }


    private void delete() {
   //     Diagram_DB.delete(this);
    }



    //</editor-fold>


}
