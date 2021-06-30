package org.pochette.organizer.pairing;

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

import org.pochette.data_library.pairing.MusicDirectory_Pairing;
import org.pochette.organizer.app.DataServiceSingleton;
import org.pochette.organizer.R;
import org.pochette.organizer.app.OrganizerApp;
import org.pochette.organizer.gui_assist.CustomSpinnerAdapter;
import org.pochette.organizer.gui_assist.CustomSpinnerItem;
import org.pochette.organizer.gui_assist.SpinnerItemFactory;
import org.pochette.utils_lib.logg.Logg;
import org.pochette.utils_lib.shouting.Shout;
import org.pochette.utils_lib.shouting.Shouting;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MusicDirectory_Pairing_Fragment extends Fragment implements Shouting, LifecycleOwner {

    //Variables
    private static final String TAG = "FEHA (MDP_Fragment)";

    private MusicDirectory_Pairing_ViewModel mModel;
    private EditText mET_Artist;
    private EditText mET_Album;
    private Spinner mSP_Purpose;
    private Spinner mSP_PairingStatus;
    private CustomSpinnerAdapter mCSA_Purpose;
    private CustomSpinnerAdapter mCSA_PairingStatus;
    @SuppressWarnings("unused")
    private ImageView mIV_Purpose;
    @SuppressWarnings("unused")
    private ImageView mIV_PairingStatus;

    private RecyclerView mRV;

    public MusicDirectory_Pairing_Adapter mMusicDirectory_Pairing_Adapter;
    Shouting mShouting;
    Shout mShoutToCeiling;
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private Shout mGlassFloor;

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
        View tView = inflater.inflate(R.layout.fragment_musicdirectory_pairing, container, false);
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
    public void onViewCreated(@Nullable View view, @Nullable Bundle savedInstanceState) {
        mET_Artist = requireView().findViewById(R.id.ET_MusicDirectoryPairing_Artist);
        mET_Album = requireView().findViewById(R.id.ET_MusicDirectoryPairing_Album);
//        mIV_Purpose =requireView().findViewById(R.id.ET_MusicDirectoryPairing_Album);
//                mIV_PairingStatus = requireView().findViewById(R.id.ET_MusicDirectoryPairing_Album);
        mRV = requireView().findViewById(R.id.RV_MusicDirectoryPairing);


        SpinnerItemFactory tSpinnerItemFactory;
        ArrayList<CustomSpinnerItem> tAL_Custom_SpinnerItem;
        tSpinnerItemFactory = new SpinnerItemFactory();

        mSP_Purpose = requireView().findViewById(R.id.SP_MusicDirectoryPairing_Purpose);
        tAL_Custom_SpinnerItem = tSpinnerItemFactory.
                getSpinnerItems(SpinnerItemFactory.FIELD_MUSICDIRECTORY_PURPOSE, true);
        mCSA_Purpose = new CustomSpinnerAdapter(this.getContext(), tAL_Custom_SpinnerItem);
        mCSA_Purpose.setTitleMode(CustomSpinnerAdapter.MODE_ICON_ONLY);
        mCSA_Purpose.setDropdownMode(CustomSpinnerAdapter.MODE_ICON_TEXT);
        mSP_Purpose.setAdapter(mCSA_Purpose);


        mSP_PairingStatus = requireView().findViewById(R.id.SP_MusicDirectoryPairing_PairingStatus);
        tAL_Custom_SpinnerItem = tSpinnerItemFactory.
                getSpinnerItems(SpinnerItemFactory.FIELD_PAIRING_STATUS, true);
        mCSA_PairingStatus = new CustomSpinnerAdapter(this.getContext(), tAL_Custom_SpinnerItem);
        mCSA_PairingStatus.setTitleMode(CustomSpinnerAdapter.MODE_ICON_ONLY);
        mCSA_PairingStatus.setDropdownMode(CustomSpinnerAdapter.MODE_ICON_TEXT);
        mSP_PairingStatus.setAdapter(mCSA_PairingStatus);


        setupListener();
    }

    private void createModel() {
        if (mModel != null) {
            return;
        }
        Application tApllication;
        tApllication = this.requireActivity().getApplication();
        mModel = ViewModelProvider.AndroidViewModelFactory.
                getInstance(tApllication).create(MusicDirectory_Pairing_ViewModel.class);
        mModel.mMLD_AR.observe(getViewLifecycleOwner(), iAR_Object -> {
            String tText;
            if (iAR_Object == null) {
                tText = "mAR is null";
            } else {
                tText = "found " + iAR_Object.size();
                ArrayList<MusicDirectory_Pairing> tAR_MusicDirectory_Pairing = new ArrayList<>(0);
                for (Object lObject : iAR_Object) {
                    MusicDirectory_Pairing lMusicDirectory_Pairing;
                    lMusicDirectory_Pairing = (MusicDirectory_Pairing) lObject;
                    tAR_MusicDirectory_Pairing.add(lMusicDirectory_Pairing);
                }
                mMusicDirectory_Pairing_Adapter.mAL_MusicDirectory_Pairing = tAR_MusicDirectory_Pairing;
                mMusicDirectory_Pairing_Adapter.refresh();
            }
            Logg.i(TAG, " observe AR:" + tText);
        });
        mModel.forceSearch();
        refresh();
    }

    private void setupListener() {
        if (mET_Artist != null) {
            mET_Artist.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    createModel();
                    mModel.setSearchArtist(s.toString());
                }
            });
        }

        if (mET_Album != null) {
            mET_Album.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    createModel();
                    mModel.setSearchAlbum(s.toString());
                }
            });
        }

        if (mSP_Purpose != null) {
            mSP_Purpose.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent,
                                           View view, int position, long id) {
                    Logg.k(TAG, "SP_Purpose: " +
                            parent.getItemAtPosition(position).toString());
                    CustomSpinnerItem tCustomSpinnerItem = (CustomSpinnerItem) mCSA_Purpose.getItem(position);
                    mModel.setCsiPurpose(tCustomSpinnerItem);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        }

        if (mSP_PairingStatus != null) {
            mSP_PairingStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent,
                                           View view, int position, long id) {
                    Logg.k(TAG, "SP_Pairing: " +
                            parent.getItemAtPosition(position).toString());
                    CustomSpinnerItem tCustomSpinnerItem = (CustomSpinnerItem) mCSA_PairingStatus.getItem(position);
                    mModel.setCsiPairingStatus(tCustomSpinnerItem);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mMusicDirectory_Pairing_Adapter == null) {
            mMusicDirectory_Pairing_Adapter = new MusicDirectory_Pairing_Adapter(getContext(), mRV, this);
        }
        RecyclerView.LayoutManager LM_MusicDirectory_Pairing = new LinearLayoutManager(getContext());
        mRV.setLayoutManager(LM_MusicDirectory_Pairing);
        mRV.setAdapter(mMusicDirectory_Pairing_Adapter);
        createModel();
    }

    @Override
    public void onPause() {
        Logg.i(TAG, "onPause: ");
        callPairingSynchronize();
        super.onPause();
      //  DataServiceSingleton.getInstance().getDataService().synchronizePairing(null,true);
    }

    @Override
    public void onStop() {
        Logg.i(TAG, "onStop: ");
        super.onStop();
    }
    //Static Methods

    //Internal Organs
    private void callPairingSynchronize() {
        Logg.i(TAG, "callPairingSynchronize: ");

        OrganizerApp tOrganizerApp = (OrganizerApp) getActivity().getApplication();
        tOrganizerApp.requestPairingSynchronisation();
        Logg.i(TAG, "  finsihed callPairingSynchronize: ");
    }
    
    
    private void refresh() {
        if (mET_Album != null && mModel != null) {
            mET_Album.setText(mModel.getSearchAlbum());
        }
        if (mET_Artist != null && mModel != null) {
            mET_Artist.setText(mModel.getSearchArtist());
        }
    }
    
    
    void process_shouting() {

    }

    //Interface
    @Override
    public void shoutUp(Shout tShoutToCeiling) {
        Logg.i(TAG, tShoutToCeiling.toString());
        mGlassFloor = tShoutToCeiling;
        process_shouting();
    }

}