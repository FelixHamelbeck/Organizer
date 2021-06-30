package org.pochette.organizer.chained_search;

import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;

import org.pochette.utils_lib.shouting.Shout;
import org.pochette.utils_lib.shouting.Shouting;


public class CLTV_TouchListener implements View.OnTouchListener {

    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private final String TAG = "FEHA (CLTV_TouchListener)";
    Shouting mShouting;
    ChainedListThreadView mOwnerChainedListThreadView;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View iView, MotionEvent iEvent) {
        if (mShouting != null) {
            Shout tShout = new Shout("Dynamic_TouchListener");
            tShout.mLastAction = "called";
            tShout.mLastObject = "onTouch";
            mShouting.shoutUp(tShout);
        }
        return false;
    }

    //Variables
    //Constructor
    //Setter and Getter
    //Livecycle
    //Static Methods
    //Internal Organs
    //Interface


}
