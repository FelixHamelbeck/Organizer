package org.pochette.organizer.app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.pochette.utils_lib.logg.Logg;

public class OrganizerStatus {

    private static final String TAG = "FEHA (OrganizerStatus)";

    static final String STATUS_OPEN = "OPEN";
    static final String STATUS_SETUP_RUNNING = "SETUP_RUNNING";
    static final String STATUS_SUCCESS = "SUCCESS";
    static final String STATUS_FAILED = "FAILED";

    static final String DATA_SERVICE = "DataService";
    static final String DATABASE = "Database";
    static final String MUSIC_SCAN = "MusicScan";
    static final String MEDIA_PLAYER = "MediaPlayer";
    static final String PAIRING_IDENTIFICATION = "PairingIdentficationStatus";
    static final String PAIRING_SYNCHRONOISATION = "PairingSynchronisationStatus";
    static final String DIAGRAM = "DiagramStatus";
    static final String FORMATION = "FormationStatus";
    static final String MUSICFILE = "MusicFileStatus";


    // variables
    @SuppressLint("StaticFieldLeak")
    private static OrganizerStatus mInstance;
    private final Context mContext;
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private final OrganizerApp mOrganizerApp;


    private String mDataServiceStatus;
    private String mDatabaseStatus;
    private String mMusicScanStatus;
    private String mMediaPlayerStatus;
    private String mPairingIdentficationStatus; // create the pairing data
    private String mPairingSynchronisationStatus; // writhe the pairing date to musicdirectorz and musicfile
    private String mDiagramStatus;
    private String mFormationStatus;
    private String mMusicFileStatus;


    private OrganizerStatus(Context iContext, OrganizerApp iOrganizerApp) {
        mContext = iContext;
        mOrganizerApp = iOrganizerApp;
        mDataServiceStatus = STATUS_OPEN;
        mDatabaseStatus = STATUS_OPEN;
        mMusicScanStatus = STATUS_OPEN;
        mMediaPlayerStatus = STATUS_OPEN;
        mPairingIdentficationStatus = STATUS_OPEN; // create the pairing data
        mPairingSynchronisationStatus = STATUS_OPEN; // writhe the pairing date to musicdirectorz and musicfile
        mDiagramStatus = STATUS_OPEN;
        mFormationStatus = STATUS_OPEN;
        mMusicFileStatus = STATUS_OPEN;
    }

    public static void createInstance(Context iContext, OrganizerApp iOrganizerApp) {
        if (iContext == null) {
            throw new IllegalArgumentException("Context needed");
        }
        if (iOrganizerApp == null) {
            throw new IllegalArgumentException("Application needed");
        }
        //Double check locking pattern
        if (mInstance == null) { //Check for the first time
            synchronized (OrganizerStatus.class) {   //Check for the second time.
                if (mInstance == null) {
                    mInstance = new OrganizerStatus(iContext, iOrganizerApp);
                }
            }
        }
    }

    public void setStatus(String iObjectString, String iStatusString) {
        Logg.d(TAG, "Time: Set " + iObjectString + " to " + iStatusString);
        switch (iObjectString) {
            case DATA_SERVICE:
                mDataServiceStatus = iStatusString;
                break;
            case DATABASE:
                mDatabaseStatus = iStatusString;
                break;
            case MUSIC_SCAN:
                mMusicScanStatus = iStatusString;
                break;
            case MEDIA_PLAYER:
                mMediaPlayerStatus = iStatusString;
                break;
            case PAIRING_IDENTIFICATION:
                mPairingIdentficationStatus = iStatusString;
                break;
            case PAIRING_SYNCHRONOISATION:
                mPairingSynchronisationStatus = iStatusString;
                break;
            case DIAGRAM:
                mDiagramStatus = iStatusString;
                break;
            case FORMATION:
                mFormationStatus = iStatusString;
                break;
            case MUSICFILE:
                mMusicFileStatus = iStatusString;
                break;
            default:
        }
    }

    public String getStatus(String iObjectString) {
        switch (iObjectString) {
            case DATA_SERVICE:
                return mDataServiceStatus;
            case DATABASE:
                return mDatabaseStatus;
            case MUSIC_SCAN:
                return mMusicScanStatus;
            case MEDIA_PLAYER:
                return mMediaPlayerStatus;
            case PAIRING_IDENTIFICATION:
                return mPairingIdentficationStatus;
            case PAIRING_SYNCHRONOISATION:
                return mPairingSynchronisationStatus;
            case DIAGRAM:
                return mDiagramStatus;
            case FORMATION:
                return mFormationStatus;
            case MUSICFILE:
                return mMusicFileStatus;
            default:
                return null;
        }
    }


    public static OrganizerStatus getInstance() {
        if (mInstance == null) {
            throw new RuntimeException("First call to OrganizerStatus must provide context and application");
        }
        return mInstance;
    }


    // variables
    // constructor
    // setter and getter
    // lifecylce and override
    // internal
    // public methods

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isOnline() {
        try {
            boolean tResult;
            ConnectivityManager tConnectivityManager;
            tConnectivityManager = (ConnectivityManager) mContext
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo tNetworkInfo = tConnectivityManager.getActiveNetworkInfo();
            tResult = tNetworkInfo != null && tNetworkInfo.isAvailable() &&
                    tNetworkInfo.isConnected();
            return tResult;
        } catch(Exception e) {
            Logg.w(TAG, "CheckConnectivity Exception: " + e.getMessage());
            Logg.w(TAG, e.toString());
        }
        return false;
    }

    public boolean isDbAvailable() {
        return mDatabaseStatus.equals(STATUS_SUCCESS);
    }

    public String toString() {
        String tText = "";
        int tLength = 3;
        tText += " DS:" + mDataServiceStatus.substring(0, Math.min(tLength, mDataServiceStatus.length()));
        tText += " DB:" + mDatabaseStatus.substring(0, Math.min(tLength, mDatabaseStatus.length()));
        tText += " MS:" + mMusicScanStatus.substring(0, Math.min(tLength, mMusicScanStatus.length()));
        tText += " MP:" + mMediaPlayerStatus.substring(0, Math.min(tLength, mMediaPlayerStatus.length()));
        tText += " PI:" + mPairingIdentficationStatus.substring(0, Math.min(tLength, mPairingIdentficationStatus.length()));
        tText += " PS:" + mPairingSynchronisationStatus.substring(0, Math.min(tLength, mPairingSynchronisationStatus.length()));
        tText += " DI:" + mDiagramStatus.substring(0, Math.min(tLength, mDiagramStatus.length()));
        tText += " FO:" + mFormationStatus.substring(0, Math.min(tLength, mFormationStatus.length()));
        tText += " FO:" + mMusicFileStatus.substring(0, Math.min(tLength, mMusicFileStatus.length()));
        return tText;
    }

}
