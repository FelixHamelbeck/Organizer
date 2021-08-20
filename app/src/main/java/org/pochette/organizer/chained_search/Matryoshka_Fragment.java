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

public class Matryoshka_Fragment extends Fragment implements Shouting, LifecycleOwner {

    private static final String TAG = "FEHA (MY_Fragment)";

    //Variables
    Shouting mShouting;
    Shout mShoutToCeiling;
    private Shout mGlassFloor;
    LinearLayout mRootLinearLayout;
    RecyclerView mRV_Dance;
    FloatingActionButton mFB_Save;
    FloatingActionButton mFB_Reset;

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
        mFB_Save = iView.findViewById(R.id.ChainedList_FAB_Requestlist);
        mFB_Reset = iView.findViewById(R.id.ChainedList_FAB_Reset);
        if (mRootLinearLayout == null) {
            Logg.w(TAG, "root not found");
        }
        if (mRV_Dance == null) {
            Logg.w(TAG, "RV not found");
        }
        if (mFB_Save != null) {
            mFB_Save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Logg.k(TAG, "Floating Button onClick");
                    saveToRequestlist_Part1();
                }
            });
            mFB_Save.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Logg.k(TAG, "Floating Button onLongClick");
                    saveToRequestlist_Part1();
                    return true;
                }
            });
        }
        if (mFB_Reset != null) {
            mFB_Reset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Logg.k(TAG, "Floating Button onClick");
                 resetController();
                }
            });
            mFB_Reset.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Logg.k(TAG, "Floating Button onLongClick");
                    resetController();
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
        Matryoshka_Controller.getInstance().setRootLinearLayout(mRootLinearLayout);
        Matryoshka_Controller.getInstance().mShouting = this;
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

//    @SuppressWarnings("unused")
//    void callTest() {
//
//
//        mMatryoshka_Controller = new Matryoshka_Controller(mRootLinearLayout);
//        mMatryoshka_Controller.readPreferenceDefintion();
//        mMatryoshka_Controller.getRootMatryoshka().setDataShouting(this);
//        mMatryoshka_Controller.display();
//
//
//    }
//



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

    private void resetController() {
        Matryoshka_Controller.getInstance().reset();
       // mRootLinearLayout.removeAllViews();
    }


    private void saveToRequestlist_Part1() {
        Logg.i(TAG, "Part1 ");
       Requestlist_Action.callExecute(mFB_Save, this,
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
            mSlimDance_Adapter.setA(iA);
            mSlimDance_Adapter.notifyDataSetChanged();
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
        if (mGlassFloor.mActor.equals("Matryoshka_Controller")) {
            if (mGlassFloor.mLastAction.equals("completed") &&
                    mGlassFloor.mLastObject.equals("Calculation")) {
                try {
                    HashSet<Integer> tHS = Matryoshka_Controller.getInstance().getHashSet();
                    if (tHS != null) {
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
                    }
                } catch(Exception e) {
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