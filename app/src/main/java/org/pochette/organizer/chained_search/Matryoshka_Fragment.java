package org.pochette.organizer.chained_search;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.pochette.data_library.requestlist.Request;
import org.pochette.data_library.requestlist.Requestlist;
import org.pochette.data_library.scddb_objects.Dance;
import org.pochette.organizer.R;
import org.pochette.organizer.dance.Dance_Cache;
import org.pochette.organizer.dance.SlimDance_Adapter;
import org.pochette.organizer.dance.SlimDance_ViewModel;
import org.pochette.organizer.requestlist.Requestlist_Action;
import org.pochette.utils_lib.logg.Logg;
import org.pochette.utils_lib.shouting.Shout;
import org.pochette.utils_lib.shouting.Shouting;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

@SuppressWarnings("rawtypes")
public class Matryoshka_Fragment extends Fragment implements Shouting, LifecycleOwner {

    private static final String TAG = "FEHA (MY_Fragment)";

    //Variables
    Shouting mShouting;
    Shout mShoutToCeiling;
    private Shout mGlassFloor;
    LinearLayout mRootLinearLayout;
    RecyclerView mRV_Dance;
    FloatingActionButton mFB;
    Matryoshka_Controller mMatryoshka_Controller;

    private SlimDance_ViewModel mModel;
    public SlimDance_Adapter mSlimDance_Adapter;

    //Constructor

    //Setter and Getter
    public void setShouting(Shouting mShouting) {
        this.mShouting = mShouting;
    }

    //Livecycle
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mShoutToCeiling = new Shout(this.getClass().getSimpleName());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View tView = inflater.inflate(R.layout.fragment_chained_list, container, false);
        Fragment tParentFragment = getParentFragment();
        if (tParentFragment != null) {
            try {
                mShouting = (Shouting) tParentFragment;
            } catch(ClassCastException e) {
                throw new ClassCastException(tParentFragment.toString()
                        + " must implement Shouting");
            }
        }
        return tView;
    }

    @Override
    public void onViewCreated(@Nullable View iView, @Nullable Bundle savedInstanceState) {
        mRootLinearLayout = Objects.requireNonNull(iView).findViewById(R.id.ChainedList_LL);
        mRV_Dance = iView.findViewById(R.id.ChainedList_RV);
        mFB = iView.findViewById(R.id.ChainedList_FAB_Requestlist);
        if (mRootLinearLayout == null) {
            Logg.w(TAG, "root not found");
        }
        if (mRV_Dance == null) {
            Logg.w(TAG, "RV not found");
        }
        if (mFB != null) {
            mFB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Logg.k(TAG, "Floating Button onClick");
                    saveToRequestlist_Part1();
                }
            });
            mFB.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Logg.k(TAG, "Floating Button onLongClick");
                    saveToRequestlist_Part1();
                    return true;
                }
            });
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();
        callTest();
    }

    @Override
    public void onResume() {
        super.onResume();
        resumRecyclerView();
    }


    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
    //Static Methods

    //Internal Organs

    @SuppressWarnings("unused")
    void callTest() {


        Class mClass = Dance.class;
        Matryoshka tOrMatryoshka;
        Matryoshka tRantMatryoshka;
        Matryoshka tReelMatryoshka;
        Matryoshka tRSCDSMatryoshka;
        Matryoshka tCribMatryoshka;
        Matryoshka tAndMatryoshka ;
        SearchOption tSearchOption;

        tAndMatryoshka = new Matryoshka(mClass, Matryoshka.NODE_AND);
        tOrMatryoshka = new Matryoshka(mClass, Matryoshka.NODE_OR);
        tRantMatryoshka = new Matryoshka(mClass, Matryoshka.NODE_SEARCH);
        tSearchOption = SearchOption.getByCode(mClass, "DANCENAME");
        tRantMatryoshka.updateSearchSetting(tSearchOption, "Rant");
        tReelMatryoshka = new Matryoshka(mClass, Matryoshka.NODE_SEARCH);
        tSearchOption = SearchOption.getByCode(mClass, "DANCENAME");
        tReelMatryoshka.updateSearchSetting(tSearchOption, "wee");

        tOrMatryoshka.addSubMatryoshka(tRantMatryoshka);
        tOrMatryoshka.addSubMatryoshka(tReelMatryoshka);

        tRSCDSMatryoshka = new Matryoshka(mClass, Matryoshka.NODE_SEARCH);
        tSearchOption = SearchOption.getByCode(mClass, "RSCDS_REQUIRED");
        tRSCDSMatryoshka.updateSearchSetting(tSearchOption, null);

        tAndMatryoshka.addSubMatryoshka(tRSCDSMatryoshka);
        tAndMatryoshka.addSubMatryoshka(tOrMatryoshka);


        mMatryoshka_Controller = new Matryoshka_Controller(mRootLinearLayout);
        mMatryoshka_Controller.setRootMatryoshka(tAndMatryoshka);
        tAndMatryoshka.setDataShouting(this);
        mMatryoshka_Controller.display();


        //Matryoshka_Thread tMatryoshka_Thread;
       // tMatryoshka_Thread = new Matryoshka_Thread(tReelMatryoshka);
       // tMatryoshka_Thread.start();




    }




    void resumRecyclerView() {

        mRV_Dance = requireView().findViewById(R.id.ChainedList_RV);
        if (mRV_Dance != null) {
            mRV_Dance.measure(View.MeasureSpec.EXACTLY, View.MeasureSpec.EXACTLY);
            @SuppressWarnings("unused") int tRV_Width = mRV_Dance.getMeasuredWidth();
            if (mSlimDance_Adapter == null) {
                mSlimDance_Adapter = new SlimDance_Adapter(getContext(), mRV_Dance, this);
                //noinspection ResultOfMethodCallIgnored
                mSlimDance_Adapter.hasStableIds();
            }
            RecyclerView.LayoutManager LM_Dance = new LinearLayoutManager(getContext());
            mRV_Dance.setLayoutManager(LM_Dance);
            mRV_Dance.setAdapter(mSlimDance_Adapter);
            mSlimDance_Adapter.setFragment(this);
            //   mDance_Adapter.setAvailableWidth(tRV_Width);
            createDanceModel();
        }
    }



    private void saveToRequestlist_Part1() {

        Logg.i(TAG, "Part1 ");
       Requestlist_Action.callExecute(mFB, this,
               Requestlist_Action.CLICK_TYPE_SHORT, Requestlist_Action.CLICK_ICON_CREATE,
                null, null, null, null);

        Logg.i(TAG, "Part1 finished");
    }

    private void saveToRequestlist_Part2(Requestlist iRequestlist) {
        Logg.i(TAG, "Part2 ");
        if (iRequestlist == null) {
          iRequestlist = Requestlist.getDefaultRequestlist();
        }
        if (iRequestlist != null) {
            Logg.i(TAG, iRequestlist.toString());
            Integer[] tA= mSlimDance_Adapter.getA_SlimDANCE();

            ArrayList<Request> tAL = new ArrayList<>();

            for (Integer lId : tA) {
                Dance lDance = Dance_Cache.getById(lId);
                if (lDance != null) {
                    Request lRequest = new Request(lDance.getMusicFile(), lDance, null);
                    tAL.add(lRequest);
                }
            }
            iRequestlist.putAL(tAL);
        }
        Logg.i(TAG, "Part2 finished");
    }


    private void createDanceModel() {
        if (mModel != null) {
            return;
        }
        Application tApllication;
        tApllication = this.requireActivity().getApplication();
        mModel = ViewModelProvider.AndroidViewModelFactory.
                getInstance(tApllication).create(SlimDance_ViewModel.class);

        mModel.mMLD_A.observe(getViewLifecycleOwner(), iA-> {
            String tText;

            Logg.w(TAG, "obervce for A");
            Integer[] tA = (Integer[]) iA;

            mSlimDance_Adapter.setA(tA);
            mSlimDance_Adapter.notifyDataSetChanged();

//            if (iAR_Object != null) {
//                try {
//                    tText = "found " + iAR_Object.size();
//                    Logg.i(TAG, tText);
//                    ArrayList<Dance> tAR_Dance = new ArrayList<>(0);
//                    for (Object lObject : iAR_Object) {
//                        Dance lDance;
//                        lDance = (Dance) lObject;
//                        tAR_Dance.add(lDance);
//                    }
//              //      mSlimDance_Adapter.setAR_DANCE(tAR_Dance);
//                    mSlimDance_Adapter.setA();
//                    mSlimDance_Adapter.notifyDataSetChanged();
//                } catch(Exception e) {
//                    Logg.w(TAG, e.toString());
//                }
//            }
        });
        mModel.forceSearch();
    }


    void process_shouting() {
//        if (mGlassFloor.mActor.equals("DialogFragment_ChooseFormation")) {
//            if (mGlassFloor.mLastAction.equals("selected") &&
//                    mGlassFloor.mLastObject.equals("TreeSetOfId")) {
//                TreeSet<Integer> tTS_Id = mGlassFloor.returnObject();
//                Logg.i(TAG, "size" + tTS_Id.size());
//            }
//        }
        if (mGlassFloor.mActor.equals("Matryoshka")) {
            if (mGlassFloor.mLastAction.equals("achieved") &&
                    mGlassFloor.mLastObject.equals("STATUS_UPTODEFINITION")) {
                try {

                    HashSet<Integer> tHS = mMatryoshka_Controller.getHashSet();
                    Logg.i(TAG, "got hashSet " + tHS.size());
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            if (mModel == null) {
                                createDanceModel();
                            }
                            mModel.setListOfId(tHS);
                        }
                    });
                } catch(Exception e) {
                    Logg.w(TAG, "273");
                    Logg.e(TAG, e.toString());


                }
            }
        }
//        }
        if (mGlassFloor.mActor.equals("Requestlist_Action")) {
            if (mGlassFloor.mLastAction.equals("finished") &&
                    mGlassFloor.mLastObject.equals("Action")) {
                Requestlist tRequestlist = mGlassFloor.returnObject();
                saveToRequestlist_Part2(tRequestlist);
            }
        }
    }

    //Interface

    @Override
    public void shoutUp(Shout tShoutToCeiling) {
        Logg.i(TAG, tShoutToCeiling.toString());
        mGlassFloor = tShoutToCeiling;
        process_shouting();
    }




}