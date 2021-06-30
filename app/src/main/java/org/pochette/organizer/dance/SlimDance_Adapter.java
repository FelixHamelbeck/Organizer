package org.pochette.organizer.dance;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.ArraySet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.pochette.data_library.scddb_objects.Dance;
import org.pochette.data_library.scddb_objects.SlimDance;
import org.pochette.organizer.R;
import org.pochette.utils_lib.logg.Logg;
import org.pochette.utils_lib.shouting.Shout;
import org.pochette.utils_lib.shouting.Shouting;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import static java.lang.Thread.sleep;

/**
 * Adapterclass for ScddbRecording
 */
@SuppressWarnings("WeakerAccess")
public class SlimDance_Adapter extends RecyclerView.Adapter<SlimDance_ViewHolder>
        implements Shouting {

    public final static int LAYOUT_MODE_STANDARD = 0; // orginal
    public final static int LAYOUT_MODE_COMPACT = 1; // sh
    public final static int LAYOUT_MODE_DYNAMIC = 2;

    public final static int EXPANSION_CRIB_NEVER = 0;
    public final static int EXPANSION_CRIB_ALLOWED = 3;
    public final static int EXPANSION_CRIB_REQUESTED = 9;
    public final static int EXPANSION_DIAGRAM_NEVER = 0;
    public final static int EXPANSION_DIAGRAM_CLOSE_ALL = 2;
    public final static int EXPANSION_DIAGRAM_ALLOWED = 3;
    public final static int EXPANSION_DIAGRAM_REQUESTED = 9;

    // Desire in ViehHolder 0 = not allowed; 1 = not allowed, but would;  2 = single allowed, but not desired, 4 = single allowed and desired, 9 = always, where available
    int mExpansionCrib;
    int mExpansionDiagram;
    // Expansion in Adapter 0 = not allowed, 3 allowed, but not always; 9 = always, where available

    //Variables
    private static String TAG = "FEHA (Dance_Adapter)";
    @SuppressWarnings("unused")
    private static final String MAIN_CLASS = Dance.class.getName();


    Context mContext;
    Fragment mFragment; // needed for DialogCalls

  //  private ArrayList<SlimDance> mAR_SlimDANCE;
 //   private ArraySet<Integer> mAS;
    private Integer[] mA;
    public int mSelectedDanceId;
    private int mAvailableWidth = 0;

    private int mLayoutMode;
    private int mSortMode; // 0 =alpha by name, 1 by score

    RecyclerView mRecyclerView;


    private boolean mAR_Any_Lock = false; // internal flag, to control workings on the mAR* take place only once at a time.

    //  Timer mAdapterTimer;

    Shouting mShouting;
    Shout mShoutToCeiling;
    Shout mGlassFloor;

    //Constructor

    @SuppressLint("UseSparseArrays")
    public SlimDance_Adapter(Context context, RecyclerView iRecyclerView, Shouting upstairs) {
        mContext = context;
        mShouting = upstairs;
        mShoutToCeiling = new Shout(getClass().getSimpleName());
        waitAndLock();
     //   mAS = new ArraySet<>(0);
        //mAR_SlimDANCE = new ArrayList<>(0);
        unlock();
        mRecyclerView = iRecyclerView;

        mLayoutMode = LAYOUT_MODE_COMPACT;
        mExpansionCrib = EXPANSION_CRIB_ALLOWED;
        mExpansionDiagram = EXPANSION_DIAGRAM_REQUESTED;

        DividerItemDecoration tDividerItemDecoration = new DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
        tDividerItemDecoration.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(context, R.drawable.divider)));
        mRecyclerView.addItemDecoration(tDividerItemDecoration);

        mSelectedDanceId = 0;

    }

    // when Fragment onStop, so we can save sharedpref
    public void onStop() {
    }

    //Setter and Getter
    //<editor-fold desc="Setter and Getter ">


//    public void setAS(ArraySet<Integer> iAS) {
//        mAS = iAS;
//    }

    public void setA(Integer[] iA) {
        mA = iA;
    }

//    public void setAR_SlimDANCE(ArrayList<SlimDance> AR_SlimDANCE) {
//        mAR_SlimDANCE = AR_SlimDANCE;
//        sort();
//        requestDiagramCaching();
//    }

    //public ArrayList<SlimDance> getAR_SlimDANCE() {
//        return mAR_SlimDANCE;
//    }

    public void setAvailableWidth(int iAvailableWidth) {
        Logg.i(TAG, "setAvailable With:" + iAvailableWidth);
        mAvailableWidth = iAvailableWidth;
        if (mAvailableWidth > 1000) {
            mLayoutMode = SlimDance_Adapter.LAYOUT_MODE_STANDARD;
        } else {
            mLayoutMode = SlimDance_Adapter.LAYOUT_MODE_COMPACT;
        }
        notifyDataSetChanged();
    }

    public void setFragment(Fragment iFragment) {
        mFragment = iFragment;
    }

//    public void setcBackground(int cBackground) {
//        this.cBackground = cBackground;
//    }

    public void cycleCribExpansion() {
        if (mExpansionCrib == EXPANSION_CRIB_NEVER) {
            mExpansionCrib = EXPANSION_CRIB_ALLOWED;
        } else if (mExpansionCrib == EXPANSION_CRIB_ALLOWED) {
            mExpansionCrib = EXPANSION_CRIB_REQUESTED;
        } else if (mExpansionCrib == EXPANSION_CRIB_REQUESTED) {
            // not logic but service should prevail
            mExpansionCrib = EXPANSION_CRIB_NEVER;
        } else {
            mExpansionCrib = EXPANSION_CRIB_ALLOWED;
        }
        Logg.i(TAG, "Expansion Crib: " + mExpansionCrib);
        notifyDataSetChanged();
    }

    public void cycleDiagramExpansion() {
        if (mExpansionDiagram == EXPANSION_DIAGRAM_NEVER) {
            mExpansionDiagram = EXPANSION_DIAGRAM_ALLOWED;
        } else if (mExpansionDiagram == EXPANSION_DIAGRAM_CLOSE_ALL) {
            mExpansionDiagram = EXPANSION_DIAGRAM_ALLOWED;
        } else if (mExpansionDiagram == EXPANSION_DIAGRAM_ALLOWED) {
            mExpansionDiagram = EXPANSION_DIAGRAM_REQUESTED;
        } else if (mExpansionDiagram == EXPANSION_DIAGRAM_REQUESTED) {
            // not logic but service should prevail
            mExpansionDiagram = EXPANSION_DIAGRAM_CLOSE_ALL;
        } else {
            mExpansionDiagram = EXPANSION_DIAGRAM_REQUESTED;
        }
        Logg.i(TAG, "Expansion DiagramManager: " + mExpansionDiagram);
        notifyDataSetChanged();
    }

    public int getSortMode() {
        return mSortMode;
    }
//</editor-fold>

    //<editor-fold desc="Live cycle">
    @NonNull
    @Override
    // public Dance_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup iParent, int iViewType) {
    public SlimDance_ViewHolder onCreateViewHolder(@NonNull ViewGroup iParent, int iViewType) {

//        Logg.i(TAG, String.format(Locale.ENGLISH, "early %d x %d",iParent.getWidth() ,iParent.getHeight() ));
//        Logg.i(TAG, String.format(Locale.ENGLISH, "early measured %d x %d",iParent.getMeasuredWidth() ,iParent.getMeasuredHeight() ));
//        iParent.measure(View.MeasureSpec.AT_MOST,View.MeasureSpec.AT_MOST);
//        Logg.i(TAG, String.format(Locale.ENGLISH, "at most measured %d x %d",iParent.getMeasuredWidth() ,iParent.getMeasuredHeight() ));
//        iParent.measure(View.MeasureSpec.EXACTLY,View.MeasureSpec.EXACTLY);
//        Logg.i(TAG, String.format(Locale.ENGLISH, "excactlz measured %d x %d",iParent.getMeasuredWidth() ,iParent.getMeasuredHeight() ));
//        iParent.measure(View.MeasureSpec.UNSPECIFIED,View.MeasureSpec.UNSPECIFIED);
//        Logg.i(TAG, String.format(Locale.ENGLISH, "unspecified measured %d x %d",iParent.getMeasuredWidth() ,iParent.getMeasuredHeight() ));

        LayoutInflater inflater = LayoutInflater.from(iParent.getContext());
        View tView;
        SlimDance_ViewHolder tViewHolder;
        //Logg.w(TAG, " ViewType" + iViewType);
        switch (iViewType) {

            case Dance_ViewHolder.VARIANT_MAX:
                tView = inflater.inflate(R.layout.row_dance_broad, iParent, false);
                tViewHolder = new SlimDance_ViewHolder(tView);
                break;
            case Dance_ViewHolder.VARIANT_STACK:
                tView = inflater.inflate(R.layout.row_dance_stack, iParent, false);
                tViewHolder = new SlimDance_ViewHolder(tView);
                break;
            default:
                Logg.e(TAG, "should not be here");
                tView = inflater.inflate(R.layout.row_dance_broad, iParent, false);
                tViewHolder = new SlimDance_ViewHolder(tView);
                break;
        }
       // tViewHolder.setDisplayVariant(iViewType);
        return tViewHolder;
    }


    @Override
    public int getItemViewType(int position) {
        int tViewType;
        switch (mLayoutMode) {
            case SlimDance_Adapter.LAYOUT_MODE_COMPACT:
                tViewType = Dance_ViewHolder.VARIANT_STACK ;
                break;
            default:
                tViewType = Dance_ViewHolder.VARIANT_MAX;
        }
        //Logg.i(TAG, String.format(Locale.ENGLISH, "Mode %d, Type %d",mLayoutMode,tViewType ));
        return tViewType;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull SlimDance_ViewHolder iDance_ViewHolder, int iDoNotUsePosition) {
        if (iDance_ViewHolder.getAdapterPosition() > mA.length ) {
            return;
        }
        int tPosition = iDance_ViewHolder.getAdapterPosition();

        Integer tId = mA[tPosition];


        Logg.i(TAG, String.format(Locale.ENGLISH, "Position %d, Id %d",tPosition,tId ));
        //SlimDance tDance = mAR_SlimDANCE.get(tPosition);
        if (tId != null) {
           // iDance_ViewHolder.setSlimDance(tDance);
            iDance_ViewHolder.setDanceId(tId);
//            int tWidth = 0;
//            if (mRecyclerView != null) {
//                tWidth = mRecyclerView.getWidth();
//                requestDiagramCaching();
//            }
         //   iDance_ViewHolder.setLayoutWidth(tWidth);
            iDance_ViewHolder.setShouting(this);
            iDance_ViewHolder.setDance_Adapter(this);
        }
    }
    //</editor-fold>


    @Override
    public void onViewRecycled(@NonNull SlimDance_ViewHolder holder) {
        super.onViewRecycled(holder);
        //  Logg.i(TAG, "onViewRecycled");
    }


    @Override
    public void onViewAttachedToWindow(@NonNull SlimDance_ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        holder.processSunrise();
    }


    @Override
    public void onViewDetachedFromWindow(@NonNull SlimDance_ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.processSunset();
    }


    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    /**
     * Called by RecyclerView when it stops observing this Adapter.
     *
     * @param recyclerView The RecyclerView instance which stopped observing this adapter.
     * @see #onAttachedToRecyclerView(RecyclerView)
     */
    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
    }
//Static Methods
    //Internal Organs

    private void requestDiagramCaching() {
        if (1 == 1) {
            return;
        }
//        Logg.i(TAG, "requestDiagramCaching");
//        int tWidth = 0;
////        if (mExample_ViewHolder != null) {
////            try {
////                tWidth = mExample_ViewHolder.getDiagramWidth();
////            } catch(Exception e) {
////                Logg.w(TAG, e.toString());
////                return;
////            }
////        }
//        if (tWidth <= 0) {
//            return;
//        }
//        for (Dance lDance : mAR_DANCE) {
//
//            //    Logg.i(TAG, "Loop DiagramCaching" + tWidth);
////            if (lDance.mCountofDiagrams > 0) {
////                DiagramCache.requestPreScaling(tWidth, lDance.mId);
////            }
//        }
    }

    private void sort() {
        Logg.i(TAG, "sort");
//        if (mAR_SlimDANCE == null) {
//            return;
//        }
//        if (mAR_SlimDANCE.size() > 1) {
//            if (mSortMode == 1) {
//                Logg.i(TAG, "sortorder Score/Name");
//                Collections.sort(mAR_SlimDANCE, (o1, o2) -> {
//                            if (o1.getScore() != o2.getScore()) {
//                                return o2.getScore() - o1.getScore();
//                            } else {
//                                return o1.mName.compareToIgnoreCase(o2.mName);
//                            }
//                        }
//                );
//            } else {
//                Logg.i(TAG, "sortorder Name");
//                Collections.sort(mAR_SlimDANCE, (o1, o2) -> o1.mName.compareToIgnoreCase(o2.mName));
//            }
            //todo
    //    }
//        for (Dance lDance : mAR_DANCE) {
//            //       Logg.i(TAG, lDance.toShortString());
//        }
    }

    void process_shouting() {

    }


    // Dead simple low weight locking of the array
    private void waitAndLock() {
        int tCount = 0;
        while (mAR_Any_Lock) {
            tCount++;
            try {
                sleep(10);
            } catch(InterruptedException e) {
                Logg.e(TAG, e.toString());
            }
            if ((tCount % 100) == 0) {
                Logg.d(TAG, "waitAndLock: " + tCount);
            }
        }
        // as it become available wait is over and lock starts
        mAR_Any_Lock = true;
    }

    private void unlock() {
        mAR_Any_Lock = false;
    }

    //Interface

    public void refresh() {
        if (mA != null) {
            //Logg.i(TAG, "size" + mAR_DANCE.size());
            if (mRecyclerView.getVisibility() != View.VISIBLE) {
                return;
            }
            //Logg.i(TAG, "size" + mAR_DANCE.size());
        }

        new Handler(Looper.getMainLooper()).post(() -> {
            //noinspection Convert2MethodRef
            notifyDataSetChanged();
        });

    }

    public void cycleSortMode() {
        Logg.i(TAG, "cycleSortMode");
        if (mSortMode == 0) {
            mSortMode = 1;
        } else {
            mSortMode = 0;
        }
        sort();
        notifyDataSetChanged();
    }


    @Override
    public void shoutUp(Shout tShoutToCeiling) {

        //Logg.i(TAG, "start shoutUP");
        Logg.i(TAG, "Shout: " + tShoutToCeiling.toString());
        if (tShoutToCeiling.mJsonString != null) {
            Logg.i(TAG, tShoutToCeiling.mJsonString);
        }
        mGlassFloor = tShoutToCeiling;
        process_shouting();
    }

    @Override
    public int getItemCount() {
        int tResult;
        waitAndLock();
        if (mA == null) {
            tResult = 0;
        } else {
            tResult = mA.length;
        }
        unlock();
        return tResult;
    }

    @Override
    public long getItemId(int position) {
        return mA[position];
        //return super.getItemId(position);
    }
}