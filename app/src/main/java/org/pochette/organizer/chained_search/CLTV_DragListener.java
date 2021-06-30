package org.pochette.organizer.chained_search;

import android.view.DragEvent;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;
import org.pochette.utils_lib.logg.Logg;
import org.pochette.utils_lib.shouting.Shout;
import org.pochette.utils_lib.shouting.Shouting;

import static android.view.DragEvent.ACTION_DRAG_ENDED;
import static android.view.DragEvent.ACTION_DRAG_ENTERED;
import static android.view.DragEvent.ACTION_DRAG_EXITED;
import static android.view.DragEvent.ACTION_DRAG_LOCATION;
import static android.view.DragEvent.ACTION_DRAG_STARTED;
import static android.view.DragEvent.ACTION_DROP;

public class CLTV_DragListener implements View.OnDragListener {

    @SuppressWarnings("FieldCanBeLocal")
    private final String TAG = "FEHA (CLTV_DragListener)";

    private final int mHashCode;// the hashcode of the CLT
    private Shouting mShouting;

//    private ChainedListThreadView mOwnerChainedListThreadView;
//    private ChainedListThreadView mReceivedChainedListThreadView;

    public CLTV_DragListener(int hashCode) {
        mHashCode = hashCode;
    }

    public void setShouting(Shouting shouting) {
        mShouting = shouting;
    }
//    public void setChainedListThreadView(ChainedListThreadView iChainedListThreadView) {
//        mOwnerChainedListThreadView = iChainedListThreadView;
//
//    }

    @Override
    public boolean onDrag(View iView, DragEvent iEvent) {

        ChainedListThreadView tChainedListThreadView = (ChainedListThreadView) iEvent.getLocalState();
        int tDroppedHashCode;
        if (tChainedListThreadView == null) {
            tDroppedHashCode = -99;
        } else {
            tDroppedHashCode = tChainedListThreadView.getHashCodeOfChainedList();
        }
        String tObject = "";
        switch (iEvent.getAction()) {
            case 3:
                tObject = "ACTION_DROP";
                break;
            case 4:
                tObject = "ACTION_DRAG_ENDED";
                break;
            case 5:
                tObject = "ACTION_DRAG_ENTERED";
                break;
            case 6:
                tObject = "ACTION_DRAG_EXITED";
                break;
        }

        if (mShouting != null && !tObject.isEmpty()) {
            try {
                Shout tShout = new Shout(this.getClass().getSimpleName());
                tShout.mLastAction = "received";
                tShout.mLastObject = tObject;
                JSONObject tJsonObject = new JSONObject();
                tJsonObject.put("hashCode", mHashCode);
                tJsonObject.put("MovingHashCode", tDroppedHashCode);
                tShout.mJsonString = tJsonObject.toString();
                mShouting.shoutUp(tShout);
            } catch(JSONException e) {
                Logg.w(TAG, e.toString());
            }
        }

        return true;
    }


    //Variables

    //Constructor

    //Setter and Getter

    //Livecycle

    //Static Methods

    //Internal Organs
    @SuppressWarnings("unused")
    static String fromDrag(DragEvent iEvent) {
        String tAction;
        switch (iEvent.getAction()) {
            case ACTION_DRAG_ENDED:
                tAction = "Ended";
                break;
            case ACTION_DRAG_ENTERED:
                tAction = "Entered";
                break;
            case ACTION_DRAG_EXITED:
                tAction = "Exit";
                break;
            case ACTION_DRAG_LOCATION:
                tAction = "Location";
                break;
            case ACTION_DRAG_STARTED:
                tAction = "Start";
                break;
            case ACTION_DROP:
                tAction = "Drop";
                break;
            default:
                tAction = "???";
        }
        return tAction;
    }

    //Interface


}
