package org.pochette.organizer.album;

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

import org.pochette.data_library.scddb_objects.Album;
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
public class Album_Fragment extends Fragment implements Shouting, LifecycleOwner {

    //Variables
    private static final String TAG = "FEHA (Album_Fragment)";

    private Album_ViewModel mModel;
    private String mSignatureString;
    boolean mWithSearchValueStorage = true;

    private EditText mET_Artist;
    private EditText mET_AlbumName;
    private Spinner mSP_PurposeType;
    private CustomSpinnerAdapter mCSA_Purpose;

    @SuppressWarnings("FieldCanBeLocal")
    private RecyclerView mRV;

    Album_Adapter mAlbum_Adapter;
    Shouting mShouting;
    Shout mShoutToCeiling;
    private Shout mGlassFloor;

    //Constructor

    //Setter and Getter
    public void setShouting(Shouting mShouting) {
        this.mShouting = mShouting;
    }

    @SuppressWarnings("unused")
    Album_ViewModel getModel() {
        if (mModel == null) {
            createModel();
        }
        return mModel;
    }

    public void setWithSearchValueStorage(boolean withSearchValueStorage) {
        mWithSearchValueStorage = withSearchValueStorage;
        if (mModel != null) {
            mModel.setWithSearchValueStorage(mWithSearchValueStorage);
        }
    }

    public void setSignatureString(String iSignatureString) {
        if (iSignatureString == null || iSignatureString.isEmpty()) {
            Logg.i(TAG, "Leave setString");
            return;
        }
        Logg.i(TAG,"setString"+ iSignatureString);
        if (mModel != null) {
            Logg.i(TAG,"call model.setString"+ iSignatureString);
            mModel.setSignatureString(iSignatureString);
        }
        if (mAlbum_Adapter != null) {
            mAlbum_Adapter.setSignatureString4Comparison(iSignatureString);
        }
        mSignatureString = iSignatureString;
        Logg.i(TAG,"finished "+ mSignatureString);
    }

    //Livecycle
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Logg.i(TAG, "onAttach");
        mShoutToCeiling = new Shout(this.getClass().getSimpleName());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            getModel();
            String tSigantureString = savedInstanceState.getString("signatureString");
            if (tSigantureString != null) {
                Logg.i(TAG, tSigantureString);
                mModel.setSignatureString(tSigantureString);
            }
        }

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater iInflater, ViewGroup iContainer,
                             Bundle iSavedInstanceState) {
        //noinspection UnnecessaryLocalVariable
        View view = iInflater.inflate(R.layout.fragment_album, iContainer, false);
        return view;
    }

    @Override
    public void onViewCreated(@Nullable View view, @Nullable Bundle savedInstanceState) {
        mET_Artist = requireView().findViewById(R.id.ET_Album_Artist);
        mET_AlbumName = requireView().findViewById(R.id.ET_Album_Album);
        mSP_PurposeType = requireView().findViewById(R.id.SP_Album_Purpose);
        //   mET_Name = requireView().findViewById(R.id.ET_Album_Name);

        mRV = requireView().findViewById(R.id.RV_Album);


        if (mAlbum_Adapter == null) {
            mAlbum_Adapter = new Album_Adapter(getContext(), mRV, this);
        }
        if (mSignatureString != null && !mSignatureString.isEmpty()) {
            mAlbum_Adapter.setSignatureString4Comparison(mSignatureString);
        }
        RecyclerView.LayoutManager LM_Album = new LinearLayoutManager(getContext());
        mRV.setLayoutManager(LM_Album);
        mRV.setAdapter(mAlbum_Adapter);


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
        mRV.requestFocus();
        mET_AlbumName.clearFocus();
        mET_Artist.clearFocus();

    }


    @Override
    public void onResume() {
        super.onResume();

        createModel();
    }

    private void createModel() {
        if (mModel != null) {
//            if (mSignatureString != null && !mSignatureString.isEmpty()) {
//                mModel.setSignatureString(mSignatureString);
//            }
            return;
        }
        Application tApplication;
        tApplication = this.requireActivity().getApplication();
        mModel = ViewModelProvider.AndroidViewModelFactory.
                getInstance(tApplication).create(Album_ViewModel.class);
        mModel.setWithSearchValueStorage(mWithSearchValueStorage);
        if (mSignatureString != null && !mSignatureString.isEmpty()) {
            Logg.i(TAG, "delayed " + mSignatureString);
            mModel.setSignatureString(mSignatureString);
        }


        mModel.mMLD_AR.observe(getViewLifecycleOwner(), iAR_Object -> {
            if (iAR_Object != null) {
                try {
                    ArrayList<Album> tAR_Album = new ArrayList<>(0);
                    for (Object lObject : iAR_Object) {
                        Album lAlbum;
                        lAlbum = (Album) lObject;
                        tAR_Album.add(lAlbum);
                    }
                    mAlbum_Adapter.setAR(tAR_Album);
                    mAlbum_Adapter.notifyDataSetChanged();
                } catch(Exception e) {
                    Logg.w(TAG, e.toString());
                }
            }
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
        if (mAlbum_Adapter != null) {
            mAlbum_Adapter.onStop();
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

    void process_shouting() {
        if (mShouting != null) {
            mShouting.shoutUp(mGlassFloor);
        } else {
            Logg.i(TAG, "no shouting");
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