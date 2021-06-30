package org.pochette.organizer.music;

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
import android.widget.Spinner;

import org.pochette.data_library.music.MusicDirectory;
import org.pochette.organizer.R;
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

@SuppressWarnings("FieldCanBeLocal")
public class MusicDirectory_Fragment extends Fragment implements Shouting, LifecycleOwner {

    //Variables
    private static final String TAG = "FEHA (MD_Fragment)";

    private MusicDirectory_ViewModel mModel;
    boolean mWithSearchValueStorage = true;

    private EditText mET_Artist;
    private EditText mET_AlbumName;
    private Spinner mSP_PurposeType;
    private CustomSpinnerAdapter mCSA_Purpose;

    @SuppressWarnings("FieldCanBeLocal")
    private RecyclerView mRV;

    MusicDirectory_Adapter mMusicDirectory_Adapter;
    Shouting mShouting;
    Shout mShoutToCeiling;
    private Shout mGlassFloor;

    //Constructor

    //Setter and Getter
    public void setShouting(Shouting mShouting) {
        this.mShouting = mShouting;
    }

    @SuppressWarnings("unused")
    MusicDirectory_ViewModel getModel() {
        return mModel;
    }

    public void setWithSearchValueStorage(boolean withSearchValueStorage) {
        mWithSearchValueStorage = withSearchValueStorage;
        if (mModel != null) {
            mModel.setWithSearchValueStorage(mWithSearchValueStorage);
        }
    }

    //Livecycle
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Logg.i(TAG, "onAttach");
        mShoutToCeiling = new Shout(this.getClass().getSimpleName());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater iInflater, ViewGroup iContainer,
                             Bundle iSavedInstanceState) {

        Logg.i(TAG, "onCreateView");
        //noinspection UnnecessaryLocalVariable
        View view = iInflater.inflate(R.layout.fragment_musicdirectory, iContainer, false);
        return view;
    }

    @Override
    public void onViewCreated(@Nullable View view, @Nullable Bundle savedInstanceState) {
        Logg.i(TAG, "onViewCreated");
        mET_Artist = requireView().findViewById(R.id.ET_MusicDirectory_Artist);
        mET_AlbumName = requireView().findViewById(R.id.ET_MusicDirectory_Album);
        mSP_PurposeType = requireView().findViewById(R.id.SP_MusicDirectory_Purpose);
     //   mET_Name = requireView().findViewById(R.id.ET_MusicDirectory_Name);

        mRV = requireView().findViewById(R.id.RV_MusicDirectory);


        if (mMusicDirectory_Adapter == null) {
            mMusicDirectory_Adapter = new MusicDirectory_Adapter(getContext(), mRV, this);
        }
        RecyclerView.LayoutManager LM_MusicDirectory = new LinearLayoutManager(getContext());
        mRV.setLayoutManager(LM_MusicDirectory);
        mRV.setAdapter(mMusicDirectory_Adapter);


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
                    if (mModel != null) {
                        mModel.setSearchArtist(s.toString());
                        mModel.forceSearch();
                    }
                }
            });
        }
        if (mET_AlbumName != null) {
            mET_AlbumName.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (mModel != null) {
                        mModel.setSearchName(s.toString());
                        mModel.forceSearch();
                    }
                }
            });
        }


        if (mSP_PurposeType != null) {

            SpinnerItemFactory tSpinnerItemFactory;
            ArrayList<CustomSpinnerItem> tAL_Custom_SpinnerItem;
            tSpinnerItemFactory = new SpinnerItemFactory();
            tAL_Custom_SpinnerItem = tSpinnerItemFactory.
                    getSpinnerItems(SpinnerItemFactory.FIELD_MUSICDIRECTORY_PURPOSE, true);
            mCSA_Purpose = new CustomSpinnerAdapter(this.getContext(), tAL_Custom_SpinnerItem);
            mCSA_Purpose.setTitleMode(CustomSpinnerAdapter.MODE_ICON_ONLY);
            mCSA_Purpose.setDropdownMode(CustomSpinnerAdapter.MODE_ICON_TEXT);
            mSP_PurposeType.setAdapter(mCSA_Purpose);
            mSP_PurposeType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent,
                                           View view, int position, long id) {
                    Logg.k(TAG, "SP_Favourite: " +
                            parent.getItemAtPosition(position).toString());
//                CustomSpinnerItem tCustomSpinnerItem = (CustomSpinnerItem) mCSA_Favourite.getItem(position);
//                mModel.setCsiFavourite(tCustomSpinnerItem);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        }



        drawHeader();
    }


    @Override
    public void onResume() {
        super.onResume();

        createModel();
    }

    private void createModel() {
        Logg.v(TAG, "CreateModel");
        if (mModel != null) {
            return;
        }
        Application tApllication;
        tApllication = this.requireActivity().getApplication();
        mModel = ViewModelProvider.AndroidViewModelFactory.
                getInstance(tApllication).create(MusicDirectory_ViewModel.class);
        mModel.setWithSearchValueStorage(mWithSearchValueStorage);


        mModel.mMLD_AR.observe(getViewLifecycleOwner(), iAR_Object -> {

            Logg.v(TAG, "observeModel");
            if (iAR_Object != null) {
                try {
                    ArrayList<MusicDirectory> tAR_MusicDirectory = new ArrayList<>(0);
                    for (Object lObject : iAR_Object) {
                        MusicDirectory lMusicDirectory;
                        lMusicDirectory = (MusicDirectory) lObject;
                        tAR_MusicDirectory.add(lMusicDirectory);
                    }
                    mMusicDirectory_Adapter.setAR(tAR_MusicDirectory);
                    mMusicDirectory_Adapter.notifyDataSetChanged();
                } catch(Exception e) {
                    Logg.w(TAG, e.toString());
                }
            }
            //  Logg.v(TAG, " observe AR:" + tText);
        });
        mModel.forceSearch();
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onStop() {
        super.onStop();
        if (mMusicDirectory_Adapter != null) {
            mMusicDirectory_Adapter.onStop();
        }
    }

    //Static Methods
    //Internal Organs
    void drawHeader() {
        if (mModel != null) {
            if (mET_Artist != null) {
                mET_Artist.setText(mModel.getSearchArtist());
            }
        }
    }



    @SuppressWarnings("WeakerAccess")
    void process_shouting() {
        if (mShouting != null) {
            mShouting.shoutUp(mGlassFloor);
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
        Logg.i(TAG, "run shoutUP");
        Logg.i(TAG, tShoutToCeiling.toString());
        mGlassFloor = tShoutToCeiling;
        process_shouting();
    }

}