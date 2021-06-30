package org.pochette.organizer.diagram;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.AndroidRuntimeException;

import org.pochette.data_library.database_management.SearchCall;
import org.pochette.data_library.diagram.Diagram;
import org.pochette.data_library.scddb_objects.Dance;
import org.pochette.organizer.BuildConfig;
import org.pochette.organizer.app.DataServiceSingleton;
import org.pochette.organizer.app.OrganizerApp;
import org.pochette.organizer.app.OrganizerStatus;
import org.pochette.utils_lib.logg.Logg;
import org.pochette.utils_lib.search.SearchCriteria;
import org.pochette.utils_lib.search.SearchPattern;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

/**
 * This class represents the dance diagrams as locally stored and provide downloads for strathspey.org
 */
@SuppressWarnings("ConstantConditions")
public class DiagramManager {

    private static final String TAG = "FEHA (DiagramManager)";

    //<editor-fold desc="Constructor">
    public DiagramManager() {
    }
    //</editor-fold>


//    /**
//     * This methods downloads the diagram for strathspey.org
//     * All three authors kr, heiko, scddb are copied
//     *
//     * @param tDanceId Dance Id for which diagrams will be downloaded
//     */

    public void download(Dance iDance) {
        URL tInputUrl;
        Bitmap tBitmap = null;
        boolean tDownloadFound = false;
        String[] authors = {"kr", "heiko", "scddb"};
        for (String lAuthor : authors) {
            // Prepare input
            String tInputUrlString = String.format(Locale.ENGLISH,
                    "https://my.strathspey.org/dd/diagram/%s/%d/?f=png;w=900",
                    lAuthor, iDance.mId);
            tInputUrl = null;
            try {
                tInputUrl = new URL(tInputUrlString);
            } catch(MalformedURLException e) {
                Logg.e(TAG, "Malformed: " + e.toString());
            }
            if (tInputUrl == null) {
                Logg.w(TAG, "Throw no url: " + tInputUrlString);
                throw new AndroidRuntimeException("No url");
            }
            InputStream input = null;
            try { // this try is concerned with the web file
                input = tInputUrl.openStream();
                if (input != null) {
                    try {
                        tBitmap = BitmapFactory.decodeStream(input);
                        tDownloadFound = true;
                    } catch(Exception e) {
                        tDownloadFound = false;
                        Logg.e(TAG, e.toString());
                    }
                } else {
                    tDownloadFound = false;
                }
            } catch(IOException e) {
                tDownloadFound = false;
                Logg.i(TAG, "Diagram not available: " + tInputUrlString);
            } catch(Exception e) {
                tDownloadFound = false;
                Logg.e(TAG, e.toString());
            } finally {
                if (input != null) {
                    try {
                        input.close();
                    } catch(IOException e) {
                        Logg.w(TAG, e.toString());
                    }
                }
            }
            if (tDownloadFound) {
                Diagram tDiagram;
                tDiagram = new Diagram(0, "", lAuthor, iDance.mId, tBitmap);
                if (BuildConfig.DEBUG) {
                    String tString = String.format(Locale.ENGLISH,
                            "Diagram for dance %s with id %d and author %s found and stored in DB",
                            iDance.mName, iDance.mId, lAuthor);
                    Logg.d(TAG, tString);
                }
                tDiagram.save();
                break;
            }
        }
        if (!tDownloadFound) {
            String tString = String.format(Locale.ENGLISH,
                    "No Diagram found for dance %s with id %d", iDance.mName, iDance.mId);
            Logg.i(TAG, tString);
        }
    }



    public void downloadAbsentDiagrams() {
        if (! OrganizerStatus.getInstance().isOnline()) {
            Logg.i(TAG, "Offline");
            return;
        }
        ArrayList<Dance> tAR_Dance;
        SearchPattern tSearchPattern = new SearchPattern(Dance.class);
        tSearchPattern.addSearch_Criteria(
                new SearchCriteria("MUSIC_REQUIRED", ""));
        tSearchPattern.addSearch_Criteria(
                new SearchCriteria("DIAGRAM_ABSENT", ""));
        SearchCall tSearchCall =
                new SearchCall(Dance.class, tSearchPattern, null);
        tAR_Dance = tSearchCall.produceArrayList();
        Logg.i(TAG, "found " + tAR_Dance.size());
        for (Dance lDance : tAR_Dance) {
            Logg.i(TAG, "try download " + lDance.toShortString());
            download(lDance);
            if (!OrganizerStatus.getInstance().isOnline()) {
                Logg.i(TAG, "Offline");
                return;
            }
        }
    }



    public Bitmap getBitmap(Dance iDance) {
        if (iDance == null || iDance.mCountofDiagrams == 0) {
            return null;
        }
        SearchPattern tSearchPattern = new SearchPattern(Diagram.class);
        tSearchPattern.addSearch_Criteria(
                new SearchCriteria("DANCE_ID", "" + iDance.mId));
        Diagram tDiagam =
                DataServiceSingleton.getInstance().getDataService().readFirst(tSearchPattern);
        return tDiagam.getBitmap();
    }

}
