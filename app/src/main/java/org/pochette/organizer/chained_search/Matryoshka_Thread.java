package org.pochette.organizer.chained_search;

import org.pochette.utils_lib.logg.Logg;

import java.util.Locale;

//import static java.lang.Thread.sleep;

public class Matryoshka_Thread extends Thread {

    private static final String TAG = "FEHA (MYT)";

    private static final int DELAY_MIN = 100;
    private static final int DELAY_MAX = 1500;

    private Matryoshka mMatryoshka;


    // Any change of these four variables needs to be synchronized with mLock


    // variables


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
        while (!interrupted()) {

            try {
                boolean tAction = false;
                if (mMatryoshka != null) {
                    int tStatus = mMatryoshka.getStatus();
                    String tText = String.format(Locale.ENGLISH,
                            "%s has status %d", mMatryoshka.toString(), tStatus);
                   // Logg.i(TAG, tText);
                    if (tStatus == Matryoshka.STATUS_READY_SUB ||
                            tStatus == Matryoshka.STATUS_DEFINTION_CHANGED) {
                        mMatryoshka.requestCalculate();
                        mDelay = DELAY_MIN;
                        tAction = true;
                    }
                }
                if(!tAction){
                    sleep(mDelay);
                    mDelay = (int) (mDelay * 2.f + 1);
                    mDelay = Math.min(DELAY_MAX, mDelay);
                }
            } catch(InterruptedException e) {
                Logg.w(TAG, e.toString());
            }
        }
    }

    // setter and getter
    //<editor-fold desc="Setter and Getter">


//
//    public void removeShouting(Shouting iShouting) {
//        if (iShouting != null) {
//            if (!mAL_Shouting.contains(iShouting)) {
//                mAL_Shouting.remove(iShouting);
//            }
//        }
//    }


    //</editor-fold>

    // lifecylce and override
    // internal

    //<editor-fold desc="calculate">


    //</editor-fold>


    // public methods


    @SuppressWarnings("unused")


    public void requestCalculate() {
//        if (mStatus == STATUS_DEFINTION_CHANGED) {
//            mStatus = STATUS_CALCULATING;
//            final Matryoshka_Thread fChainedList = this;
//            Logg.i(TAG, "call calculate" + fChainedList.toString());
//            calculate();
//        }
    }


}
