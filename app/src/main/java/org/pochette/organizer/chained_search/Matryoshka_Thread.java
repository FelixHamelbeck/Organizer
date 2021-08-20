package org.pochette.organizer.chained_search;

import org.pochette.utils_lib.logg.Logg;

//import static java.lang.Thread.sleep;

public class Matryoshka_Thread extends Thread {

    private static final String TAG = "FEHA (MYT)";

    private static final int DELAY_MIN = 5500;
    private static final int DELAY_MAX = 11500;
    // variables
    @SuppressWarnings("FieldMayBeFinal")
    private Matryoshka mMatryoshka;
    private int mDelay;

    @SuppressWarnings("FieldCanBeLocal")

    // constructor
    public Matryoshka_Thread(Matryoshka iMatryoshka) {
        mMatryoshka = iMatryoshka;
        this.setName("MYT" + iMatryoshka.hashCode());
    }

    @Override
    public void run() {
        super.run();
        boolean tKeepGoing = true;
        while (!interrupted() && tKeepGoing) {
            try {
                boolean tAction = false;
                if (mMatryoshka != null) {
                    int tStatus = mMatryoshka.getStatus();
                    String tText = mMatryoshka.toString();
                    tText = String.format("Thread %d: %s ON %d w Stat=%d",
                            this.hashCode(), tText,mMatryoshka.hashCode(), tStatus);
                    Logg.w(TAG, tText);
                    if (tStatus == Matryoshka.STATUS_READY_SUB ||
                            tStatus == Matryoshka.STATUS_DEFINTION_CHANGED) {
                        mMatryoshka.requestCalculate();
                        mDelay = DELAY_MIN;
                        tAction = true;
                    }
                }
                if(!tAction){
                    //noinspection BusyWait
                    sleep(mDelay);
                    mDelay = (int) (mDelay * 2.f + 1);
                    mDelay = Math.min(DELAY_MAX, mDelay);
                }
            } catch(InterruptedException e) {
                Logg.w(TAG, e.toString());
               // mMatryoshka.
                tKeepGoing = false;
            }
        }
    }

    // setter and getter

    // lifecylce and override
    // internal

    //<editor-fold desc="calculate">
    //</editor-fold>
    // public methods



//
//    public void requestCalculate() {
////        if (mStatus == STATUS_DEFINTION_CHANGED) {
////            mStatus = STATUS_CALCULATING;
////            final Matryoshka_Thread fChainedList = this;
////            Logg.i(TAG, "call calculate" + fChainedList.toString());
////            calculate();
////        }
//    }


}
