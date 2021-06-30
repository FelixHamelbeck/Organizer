package org.pochette.organizer.gui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.json.JSONException;
import org.json.JSONObject;
import org.pochette.utils_lib.logg.Logg;
import org.pochette.utils_lib.shouting.Shout;
import org.pochette.utils_lib.shouting.Shouting;

import java.util.Objects;

public class TopBar_BroadCastReceiver extends BroadcastReceiver {

    private static final String TAG = "FEHA (TopBar_BroadCastReceiver)";

    // variables
    Shouting mShouting;
    // constructor

    public TopBar_BroadCastReceiver(Shouting iShouting) {
        mShouting = iShouting;
        Logg.i(TAG, "BroadCast Receiver Constructor");
    }

    public TopBar_BroadCastReceiver() {
        mShouting = null;
        Logg.i(TAG, "BroadCast Receiver Constructor");
    }

    // setter and getter
    // lifecylce and override

    @Override
    public void onReceive(Context iContext, Intent iIntent) {
        float tPosition = 0.f;
        float tDuration = 0.f;
        if (Objects.requireNonNull(iIntent.getAction()).equals("org.pochette.musicplayer.mediaplayer.MediaPlayerService.NEW_POSITION")) {
            tPosition = iIntent.getFloatExtra("position", tPosition);
            tDuration = iIntent.getFloatExtra("duration", tDuration);
            if (mShouting != null) {
                try {
                    Shout tShout = new Shout(this.getClass().getSimpleName());
                    tShout.mLastObject = "NEW_POSITION";
                    tShout.mLastAction = "received";
                    JSONObject tJsonObject = new JSONObject();
                    tJsonObject.put("position", tPosition);
                    tJsonObject.put("duration", tDuration);
                    tShout.mJsonString = tJsonObject.toString();
                    mShouting.shoutUp(tShout);
                } catch(JSONException e) {
                    Logg.w(TAG, e.toString());
                }
            }
        } else {
            Logg.w(TAG, " got something else");
            Logg.w(TAG, iIntent.getAction());
        }
    }
    // internal
    // public methods
}
