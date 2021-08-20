package org.pochette.organizer.chained_search;

import android.view.DragEvent;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;
import org.pochette.utils_lib.logg.Logg;
import org.pochette.utils_lib.shouting.Shout;
import org.pochette.utils_lib.shouting.Shouting;

/**
 * View part of MCV
 */

public class Matryoshka_DragListener implements View.OnDragListener {

    @SuppressWarnings("FieldCanBeLocal")
    private final String TAG = "FEHA (MY_DragListener)";

   private int mHashCode; // the receiver
    private Shouting mShouting;

    public Matryoshka_DragListener(int iHashCode, Shouting iShouting) {
        Logg.i(TAG, "Construct Listener " + iHashCode);
        mHashCode = iHashCode;
        mShouting = iShouting;
    }

    public void setShouting(Shouting shouting) {
        mShouting = shouting;
    }

    public void setHashCode(int hashCode) {
        mHashCode = hashCode;
    }

    /**
     * OnDrag shouts the action and the hash code of the dragged elements
     * @param iView v
     * @param iEvent the event
     * @return always true
     */

    @Override
    public boolean onDrag(View iView, DragEvent iEvent) {
        Matryoshka_View tMatryoshka_View = null;
        try {
            tMatryoshka_View = (Matryoshka_View) iEvent.getLocalState();
        } catch(Exception e) {
            Logg.w(TAG, e.toString());
        }
        int tMatryoshka_dragged_HashCode;
        if (tMatryoshka_View == null) {
            tMatryoshka_dragged_HashCode = -99;
        } else {
            tMatryoshka_dragged_HashCode = tMatryoshka_View.getHashCodeOfMatryochka();
        }
        String tActionString = "";
        switch (iEvent.getAction()) {
            case 3:
                tActionString = "ACTION_DROP";
                break;
            case 4:
                tActionString = "ACTION_DRAG_ENDED";
                break;
            case 5:
                tActionString = "ACTION_DRAG_ENTERED";
                break;
            case 6:
                tActionString = "ACTION_DRAG_EXITED";
                break;
        }
        Logg.i(TAG, tActionString);

        if (mShouting != null && !tActionString.isEmpty()) {
            try {
                Shout tShout = new Shout(this.getClass().getSimpleName());
                tShout.mLastAction = "received";
                tShout.mLastObject = "DragAndDropEvent";
                JSONObject tJsonObject = new JSONObject();
                tJsonObject.put("DraggedHashCode", tMatryoshka_dragged_HashCode);
                tJsonObject.put("hashCode", mHashCode);
                tJsonObject.put("Action", iEvent.getAction());
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
//    static String fromDrag(DragEvent iEvent) {
//        String tAction;
//        switch (iEvent.getAction()) {
//            case ACTION_DRAG_ENDED:
//                tAction = "Ended";
//                break;
//            case ACTION_DRAG_ENTERED:
//                tAction = "Entered";
//                break;
//            case ACTION_DRAG_EXITED:
//                tAction = "Exit";
//                break;
//            case ACTION_DRAG_LOCATION:
//                tAction = "Location";
//                break;
//            case ACTION_DRAG_STARTED:
//                tAction = "Start";
//                break;
//            case ACTION_DROP:
//                tAction = "Drop";
//                break;
//            default:
//                tAction = "???";
//        }
//        return tAction;
//    }

    //Interface


}
