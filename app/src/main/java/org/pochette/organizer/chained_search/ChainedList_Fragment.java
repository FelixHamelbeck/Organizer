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

import org.pochette.data_library.playlist.Playlist;
import org.pochette.data_library.scddb_objects.Dance;
import org.pochette.organizer.app.DataServiceSingleton;
import org.pochette.organizer.R;
import org.pochette.organizer.dance.Dance_Adapter;
import org.pochette.organizer.dance.Dance_ViewModel;
import org.pochette.organizer.playlist.Playlist_Action;
import org.pochette.utils_lib.logg.Logg;
import org.pochette.utils_lib.shouting.Shout;
import org.pochette.utils_lib.shouting.Shouting;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.TreeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

@SuppressWarnings("rawtypes")
public class ChainedList_Fragment extends Fragment implements Shouting, LifecycleOwner {

    private static final String TAG = "FEHA (ChainedDetail_Fragment)";

    //Variables
    Shouting mShouting;
    Shout mShoutToCeiling;
    private Shout mGlassFloor;
    LinearLayout mRootLinearLayout;
    RecyclerView mRV_Dance;
    FloatingActionButton mFB;

    private Dance_ViewModel mModel;
    public Dance_Adapter mDance_Adapter;


    ChainedListThread mChainedListThread;
    ChainedListThreadAdapter mChainedListThreadAdapter;

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
        prepareChainedList();
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
        mFB = iView.findViewById(R.id.ChainedList_FAB_Playlist);
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
                    saveToPlaylist();
                }
            });
            final Fragment tFragment = this;
            mFB.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Logg.k(TAG, "Floating Button onLongClick");
                    Playlist_Action.callExecute(mRootLinearLayout, (Shouting) tFragment,
                            Playlist_Action.CLICK_TYPE_LONG, Playlist_Action.CLICK_ICON_PLAYLIST,
                            null, null, null);
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
    }

    @Override
    public void onResume() {
        super.onResume();
        resumeChainedListView();
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

    void resumRecyclerView() {

        mRV_Dance = requireView().findViewById(R.id.ChainedList_RV);
        if (mRV_Dance != null) {

            mRV_Dance.measure(View.MeasureSpec.EXACTLY, View.MeasureSpec.EXACTLY);

            @SuppressWarnings("unused") int tRV_Width = mRV_Dance.getMeasuredWidth();
            if (mDance_Adapter == null) {
                mDance_Adapter = new Dance_Adapter(getContext(), mRV_Dance, this);
                //noinspection ResultOfMethodCallIgnored
                mDance_Adapter.hasStableIds();
            }
            RecyclerView.LayoutManager LM_Dance = new LinearLayoutManager(getContext());
            mRV_Dance.setLayoutManager(LM_Dance);
            mRV_Dance.setAdapter(mDance_Adapter);
            mDance_Adapter.setFragment(this);
            //   mDance_Adapter.setAvailableWidth(tRV_Width);
            createModel();
        }

    }


    void resumeChainedListView() {
        mChainedListThreadAdapter = new ChainedListThreadAdapter();
        mChainedListThreadAdapter.setShouting(this);
        mChainedListThreadAdapter.setRootChainedListThread(mChainedListThread);
        mChainedListThreadAdapter.setRootLinearLayout(mRootLinearLayout);
        //mChainedListThreadAdapter.setFragmentManager(getFragmentManager());
        mChainedListThreadAdapter.display();
        mChainedListThread.requestCalculate();
    }

    private void saveToPlaylist() {
        Playlist tPlaylist = Playlist.getDefaultPlaylist();
        if (tPlaylist != null) {
            Logg.i(TAG, tPlaylist.toString());
            ArrayList<Dance> tAL_Dance = mDance_Adapter.getAR_DANCE();
            tPlaylist.fill(tAL_Dance);
        }
    }

    private void createModel() {
        if (mModel != null) {
            return;
        }
        Application tApllication;
        tApllication = this.requireActivity().getApplication();
        mModel = ViewModelProvider.AndroidViewModelFactory.
                getInstance(tApllication).create(Dance_ViewModel.class);

        mModel.mMLD_AR.observe(getViewLifecycleOwner(), iAR_Object -> {
            String tText;
            if (iAR_Object != null) {
                try {
                    tText = "found " + iAR_Object.size();
                    Logg.i(TAG, tText);
                    ArrayList<Dance> tAR_Dance = new ArrayList<>(0);
                    for (Object lObject : iAR_Object) {
                        Dance lDance;
                        lDance = (Dance) lObject;
                        tAR_Dance.add(lDance);
                    }
                    mDance_Adapter.setAR_DANCE(tAR_Dance);
                    mDance_Adapter.notifyDataSetChanged();
                } catch(Exception e) {
                    Logg.w(TAG, e.toString());
                }
            }
        });
        mModel.forceSearch();
    }


    void process_shouting() {
        if (mGlassFloor.mActor.equals("DialogFragment_ChooseFormation")) {
            if (mGlassFloor.mLastAction.equals("selected") &&
                    mGlassFloor.mLastObject.equals("TreeSetOfId")) {
                TreeSet<Integer> tTS_Id = mGlassFloor.returnObject();
                Logg.i(TAG, "size" + tTS_Id.size());
            }
        }
        if (mGlassFloor.mActor.equals("ChainedListThreadAdapter")) {
            if (mGlassFloor.mLastAction.equals("achieved") &&
                    mGlassFloor.mLastObject.equals("STATUS_UPTODEFINITION")) {
                TreeSet<Integer> tTS = mChainedListThreadAdapter.getTreeSet();
                HashSet<Integer> tHS = new HashSet<>(tTS);
                Logg.i(TAG, "got hashSet " + tHS.size());
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (mModel == null) {
                            createModel();
                        }
                        mModel.setListOfId(tHS);
                    }
                });
            }
        }
        if (mGlassFloor.mActor.equals("Playlist_Action")) {
            if (mGlassFloor.mLastAction.equals("finished") &&
                    mGlassFloor.mLastObject.equals("Action")) {
                saveToPlaylist();
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


    void prepareChainedList() {
        Class mClass = Dance.class;
        ChainedListThread tOrChainedListThread;
        ChainedListThread tRantChainedListThread;
        ChainedListThread tReelChainedListThread;
        ChainedListThread tRSCDSChainedListThread;
        ChainedListThread tCribChainedListThread;
        SearchOption tSearchOption;

        mChainedListThread = new ChainedListThread(mClass, ChainedListThread.NODE_AND);
        tOrChainedListThread = new ChainedListThread(mClass, ChainedListThread.NODE_OR);
        tRantChainedListThread = new ChainedListThread(mClass, ChainedListThread.NODE_SEARCH);
        tSearchOption = SearchOption.getByCode(mClass, "DANCENAME");
        tRantChainedListThread.updateSearchSetting(tSearchOption, "Rant");
        tReelChainedListThread = new ChainedListThread(mClass, ChainedListThread.NODE_SEARCH);
        tSearchOption = SearchOption.getByCode(mClass, "DANCENAME");
        tReelChainedListThread.updateSearchSetting(tSearchOption, "wee");

        tOrChainedListThread.addFeederChainedList(tRantChainedListThread);
        tOrChainedListThread.addFeederChainedList(tReelChainedListThread);

        tRSCDSChainedListThread = new ChainedListThread(mClass, ChainedListThread.NODE_SEARCH);
        tSearchOption = SearchOption.getByCode(mClass, "RSCDS_REQUIRED");
        tRSCDSChainedListThread.updateSearchSetting(tSearchOption, null);

        tCribChainedListThread = new ChainedListThread(mClass, ChainedListThread.NODE_SEARCH);
        tSearchOption = SearchOption.getByCode(mClass, "CRIB_REQUIRED");
        tCribChainedListThread.updateSearchSetting(tSearchOption, null);

        mChainedListThread.addFeederChainedList(tOrChainedListThread);
        mChainedListThread.addFeederChainedList(tRSCDSChainedListThread);
        mChainedListThread.addFeederChainedList(tCribChainedListThread);


        tRantChainedListThread.start();
        tReelChainedListThread.start();
        tOrChainedListThread.start();
        tCribChainedListThread.start();
        tRSCDSChainedListThread.start();
        mChainedListThread.start();


    }

}