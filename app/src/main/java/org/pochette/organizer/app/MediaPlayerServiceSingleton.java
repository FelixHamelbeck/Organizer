package org.pochette.organizer.app;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import org.pochette.organizer.mediaplayer.MediaPlayerService;
import org.pochette.utils_lib.logg.Logg;
import org.pochette.utils_lib.shouting.Shout;
import org.pochette.utils_lib.shouting.Shouting;

import static java.lang.Thread.sleep;

public class MediaPlayerServiceSingleton implements Shouting {

    private static final String TAG = "FEHA (MediaPlayerServiceSingleton)";

    // variables
    @SuppressLint("StaticFieldLeak")
    private static MediaPlayerServiceSingleton mInstance;

    private final Context mContext; // release once service is connected
    private boolean mServiceBound;
    private ServiceConnection mServiceConnection;
    private MediaPlayerService mMediaPlayerService;
    Shouting mShouting;
    Shout mGlassFloor;

    // constructor

    public MediaPlayerServiceSingleton(Context iContext) {
        mContext = iContext;
        mServiceBound = false;
        mServiceConnection = null;
        mMediaPlayerService = null;
    }

    public static void createInstance(Context iContext) {
        if (iContext == null) {
            throw new IllegalArgumentException("Context needed");
        }
        //Double check locking pattern
        if (mInstance == null) { //Check for the first time
            synchronized (MediaPlayerServiceSingleton.class) {   //Check for the second time.
                if (mInstance == null) {
                    mInstance = new MediaPlayerServiceSingleton(iContext);
                }
            }
        }
    }

    public static MediaPlayerServiceSingleton getInstance() {
        if (mInstance == null) { //Check for the first time
            synchronized (MediaPlayerServiceSingleton.class) {   //Check for the second time.
                if (mInstance == null) {
                    throw new RuntimeException("First call to MediaPlayerServiceSingleton " +
                            "should be createInstance with Context");
                }
            }
        }
        return mInstance;
    }

    public static boolean isAvailable() {
        return mInstance != null;
    }
    // setter and getter

    public void setShouting(Shouting iShouting) {
        mShouting = iShouting;
    }

    public boolean isServiceBound() {
        return mServiceBound;
    }

    public MediaPlayerService getMediaPlayerService() {
        while (!mServiceBound) {
            try {
                //noinspection BusyWait
                sleep(500);
            } catch(InterruptedException e) {
                Logg.i(TAG, e.toString());
            }
        }
        if (mMediaPlayerService == null) {
            throw new RuntimeException("MediaPlayerService should have started");
        }

        return mMediaPlayerService;
    }
    // lifecylce and override
    // internal

    void createServiceConnection() {
        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceDisconnected(ComponentName name) {
                mServiceBound = false;
                Logg.i(TAG, "onService Dis Connected");
            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder iService) {
                Logg.i(TAG, "onServiceConnected");
             //   mContext = null; // release context to avoid leak
                mServiceBound = true;
                mMediaPlayerService = ((MediaPlayerService.LocalBinder) iService).getService();
                processServiceConnected();
            }
        };
    }

    void startService() {
        try {
            // Create the service Intent.
            Intent serviceIntent =
                    new Intent(mContext, MediaPlayerService.class);
            // Bind the service and grab a reference to the binder.
            try {
                mContext.bindService(
                        serviceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
            } catch(Exception e) {
                Logg.w(TAG, e.toString());
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    void processServiceConnected() {
        if (mMediaPlayerService != null) {
            if (mContext == null) {
                throw new RuntimeException("No context available to provide to MediaPlayer");
            }
            mMediaPlayerService.contextualize(mContext);
            mMediaPlayerService.setShouting(this);
        }
    }

    void processShouting() {
        // nothing
    }

    // public methods
    public void prepMediaPlayerService() {
        createServiceConnection();
        startService();
    }

    public void shoutUp(Shout iShoutToCeiling) {
        Logg.i(TAG, iShoutToCeiling.toString());
        mGlassFloor = iShoutToCeiling;
        processShouting();
    }

}
