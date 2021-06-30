package org.pochette.organizer.app;

import android.app.Activity;

import com.google.android.gms.security.ProviderInstaller;

import org.pochette.utils_lib.logg.Logg;

import javax.net.ssl.SSLContext;

@SuppressWarnings("unused")
public class PreparationMiscelleanous {

    private static final String TAG = "FEHA (PrepMisc)";

    // variables
    // constructor
    // setter and getter
    // lifecylce and override
    // internal
    // public methods

    public static void execute(Activity iActivity, boolean iDebug) {

        SSLContext sslContext;
        try {
            ProviderInstaller.installIfNeeded(iActivity.getApplicationContext());
            sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, null, null);
            sslContext.createSSLEngine();
        } catch(Exception e) {
            Logg.w(TAG, e.toString());
        }
    }
}
