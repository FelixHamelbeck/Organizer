package org.pochette.data_library.GUI;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.IBinder;
import android.view.MenuItem;
import android.view.WindowManager;


import org.pochette.utils_lib.logg.Logg;
import org.pochette.utils_lib.shouting.Shout;
import org.pochette.utils_lib.shouting.Shouting;
import org.pochette.data_library.R;
import org.pochette.data_library.database_management.Ldb_Helper;
import org.pochette.data_library.database_management.Scddb_File;
import org.pochette.data_library.database_management.Scddb_Helper;
import org.pochette.data_library.logg.PreparationOfLogg;


import java.util.Locale;
import java.util.Objects;

import javax.net.ssl.SSLContext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements Shouting {

    private static final String TAG = "FEHA (MainActivity)";

    private static final int REQ_PICK_LOCAL_MUSICFILE = 10001;

    // variables


    Shout mGlassFloor;

    boolean mServiceBound;
    // lifecylce and override

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prepareEveryting();
        Logg.i(TAG, "onCreate");


        Ldb_Helper.createInstance(this);
        Scddb_Helper.createInstance(this);

        boolean tNetwork = checkInternetConnection();
        Logg.i(TAG, "network " + tNetwork);
        Thread tThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Scddb_File.getInstance().copyFromWeb(null);
            }
        });
        tThread.start();


      //  Scddb_File.getInstance().copyFromWeb();

    }

    @Override
    protected void onResume() {
        super.onResume();
        Logg.i(TAG, "onResume");
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mServiceBound) {
            unbindService(mServiceConnection);
        }
    }


    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onWindowAttributesChanged(WindowManager.LayoutParams params) {
        super.onWindowAttributesChanged(params);

       // Logg.w(TAG, "onWindowAttributesChanged");
    }
//

    @SuppressWarnings("FieldMayBeFinal")
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServiceBound = false;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Logg.i(TAG, "onServiceConnected");


        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, @NonNull int[] grantResults) {
        Logg.i(TAG, String.format(Locale.ENGLISH,
                "onRequestPermssionResult RC: %d [Count %d]", requestCode, permissions.length));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Logg.i(TAG, "OnActivityResult: "+requestCode);

    }


    // internal

    private boolean checkInternetConnection() {
        ConnectivityManager tConnectivityManager = (ConnectivityManager) getSystemService (Context.CONNECTIVITY_SERVICE);
        // ARE WE CONNECTED TO THE NET
        //noinspection RedundantIfStatement
        if (Objects.requireNonNull(tConnectivityManager).getActiveNetworkInfo() != null
                && Objects.requireNonNull(tConnectivityManager.getActiveNetworkInfo()).isAvailable()
                && tConnectivityManager.getActiveNetworkInfo().isConnected()) {
            return true;

        } else {
            return false;
        }
    }





    // lifecylce and override





    void prepareEveryting() {
        PreparationOfLogg.execute(this, false);

        // Permissions Stuff
        int hasWriteExternalPermission = this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        Logg.i(TAG, "Write External Permision " + hasWriteExternalPermission);
        if (hasWriteExternalPermission != PackageManager.PERMISSION_GRANTED) {
            Logg.w(TAG, "Write Permission not available");
            this.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    0);
        } else {
            Logg.i(TAG, "Write Permission available");
        }

        int hasInternetPermission = this.checkSelfPermission(Manifest.permission.INTERNET);
        Logg.i(TAG, "Internet Permision " + hasInternetPermission);
        if (hasInternetPermission != PackageManager.PERMISSION_GRANTED) {
            Logg.w(TAG, "Internet Permission not available");
            this.requestPermissions(new String[]{Manifest.permission.INTERNET},
                    0);
        } else {
            Logg.i(TAG, "Internet Permission available");
        }


        //  Setup for SSL (else strathspey.org creates a handshake error
        try {
            //ProviderInstaller.installIfNeeded(this);
            SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, null, null);
            sslContext.createSSLEngine();
        } catch(Exception e) {
            Logg.e(TAG, e.toString());
        }


    }


    // public methods




    @Override
    public void shoutUp(Shout tShoutToCeiling) {

        Logg.w(TAG, tShoutToCeiling.toString());
        mGlassFloor = tShoutToCeiling;
//        processShouting();
    }


}