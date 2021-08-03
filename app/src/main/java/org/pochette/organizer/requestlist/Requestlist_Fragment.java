package org.pochette.organizer.requestlist;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import org.pochette.data_library.requestlist.Requestlist;
import org.pochette.organizer.R;
import org.pochette.organizer.gui_assist.CustomSpinnerAdapter;
import org.pochette.organizer.gui_assist.CustomSpinnerItem;
import org.pochette.organizer.gui_assist.SpinnerItemFactory;
import org.pochette.utils_lib.logg.Logg;
import org.pochette.utils_lib.shouting.Shout;
import org.pochette.utils_lib.shouting.Shouting;

import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class Requestlist_Fragment extends Fragment implements Shouting, LifecycleOwner {

    //Variables
    private static final String TAG = "FEHA (Requestlist_Fragment)";

    private View mLayout;
    private Requestlist_ViewModel mModel;
    private RecyclerView mRV;

    private Spinner mSP_Purpose;
    private CustomSpinnerAdapter mCSA_Purpose;
    private EditText mET_Name;
    private ImageView mIV_AddNew;

    Requestlist_Adapter mRequestlist_Adapter;
    Shouting mShouting;
    Shout mShoutToCeiling;
    private Shout mGlassFloor;

    //Constructor

    //Setter and Getter
    public void setShouting(Shouting mShouting) {
        this.mShouting = mShouting;
    }

    Requestlist_ViewModel getModel() {
        return mModel;
    }

    //Livecycle
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mShoutToCeiling = new Shout(this.getClass().getSimpleName());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater iInflater, ViewGroup iContainer,
                             Bundle iSavedInstanceState) {

        mLayout = iInflater.inflate(R.layout.fragment_requestlist, iContainer, false);
        return mLayout;
    }

    @Override
    public void onViewCreated(@Nullable View view, @Nullable Bundle savedInstanceState) {
        createModel();
        mRV = requireView().findViewById(R.id.RV_Requestlist);
        mSP_Purpose = requireView().findViewById(R.id.SP_Requestlist_Purpose);
        mET_Name = Objects.requireNonNull(view).findViewById(R.id.ET_Requestlist_Name);
        mET_Name.setText(mModel.getSearchName());
        mET_Name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                createModel();
                mModel.setSearchName(s.toString());
            }
        });
        SpinnerItemFactory tSpinnerItemFactory;
        ArrayList<CustomSpinnerItem> tAL_Custom_SpinnerItem;
        tSpinnerItemFactory = new SpinnerItemFactory();

        mSP_Purpose = requireView().findViewById(R.id.SP_Requestlist_Purpose);
        tAL_Custom_SpinnerItem = tSpinnerItemFactory.getSpinnerItems("REQUESTLIST_PURPOSE", true);
        mCSA_Purpose = new CustomSpinnerAdapter(this.getContext(), tAL_Custom_SpinnerItem);

        mCSA_Purpose.setTitleMode(CustomSpinnerAdapter.MODE_ICON_ONLY);
        mCSA_Purpose.setDropdownMode(CustomSpinnerAdapter.MODE_ICON_TEXT);
        mSP_Purpose.setAdapter(mCSA_Purpose);
        mSP_Purpose.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent,
                                       View view, int position, long id) {
                createModel();
                Logg.k(TAG, "SP_Purpose: " +
                        parent.getItemAtPosition(position).toString());
                CustomSpinnerItem tCustomSpinnerItem = (CustomSpinnerItem) mCSA_Purpose.getItem(position);
                mModel.setSearchPurpose(tCustomSpinnerItem);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        mIV_AddNew = view.findViewById(R.id.IV_Requestlist_AddNew);
        mIV_AddNew.setOnClickListener(iView -> {
            Logg.k(TAG, "Addnew");
            Requestlist_Action.callExecute(mLayout, this,
                    Requestlist_Action.CLICK_TYPE_SHORT, Requestlist_Action.CLICK_ICON_CREATE,
                    null, null, null,null);
        });
        mIV_AddNew.setOnLongClickListener(iView -> {
            Logg.k(TAG, "Addnew");
            Requestlist_Action.callExecute(mLayout, this,
                    Requestlist_Action.CLICK_TYPE_SHORT, Requestlist_Action.CLICK_ICON_CREATE,
                    null, null, null,null);
            return true;
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mModel.forceSearch();
    }

    private void createModel() {
        if (mModel != null) {
            return;
        }
        Application tApllication;
        tApllication = this.requireActivity().getApplication();
        mModel = ViewModelProvider.AndroidViewModelFactory.
                getInstance(tApllication).create(Requestlist_ViewModel.class);
        MutableLiveData<ArrayList<Object>> tMLD_AR;


        mModel.mMLD_AR.observe(getViewLifecycleOwner(), iAR_Object -> {
            String tText = "?";
            if (iAR_Object == null) {
                tText = "mAR is null";
            } else {
                try {
                    tText = "found " + iAR_Object.size();
                    ArrayList<Requestlist> tAR = new ArrayList<>(0);
                    for (Object lObject : iAR_Object) {
                        Requestlist lRequestlist;
                        lRequestlist = (Requestlist) lObject;
                        tAR.add(lRequestlist);
                    }
                    mRequestlist_Adapter.setAR(tAR);
                    mRequestlist_Adapter.notifyDataSetChanged();
                } catch(Exception e) {
                    Logg.w(TAG, e.toString());
                }
            }
            Logg.i(TAG, " observe AR:" + tText);
        });
        mModel.forceSearch();
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        createModel();
        if (mRequestlist_Adapter == null) {
            mRequestlist_Adapter = new Requestlist_Adapter(getContext(), mRV, this);
        }
        RecyclerView.LayoutManager LM_Requestlist = new LinearLayoutManager(getContext());
        mRV.setLayoutManager(LM_Requestlist);
        mRV.setAdapter(mRequestlist_Adapter);
        createModel();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mRequestlist_Adapter != null) {
            mRequestlist_Adapter.onStop();
        }
    }
    //Static Methods

    //Internal Organs
    @SuppressWarnings("WeakerAccess")
    void process_shouting() {
        if (mShouting != null) {
            mShouting.shoutUp(mGlassFloor);
        }
        switch (mGlassFloor.mActor) {
            case "Requestlist_ViewHolder":
                if (mGlassFloor.mLastAction.equals("changed") &&
                        mGlassFloor.mLastObject.equals("Data")) {
                    mModel.forceSearch();
                }
                break;
            case "Requestlist_Action":
            case "DialogFragment_RequestlistAction":
                mModel.forceSearch();
                break;
        }
    }

    //Interface

    /**
     * Receive the shout
     *
     * @param tShoutToCeiling the shout
     */
    @Override
    public void shoutUp(Shout tShoutToCeiling) {
        Logg.i(TAG, tShoutToCeiling.toString());
        mGlassFloor = tShoutToCeiling;
        process_shouting();
    }

}