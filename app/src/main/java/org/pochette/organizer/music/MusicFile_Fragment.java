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
import android.widget.ImageView;
import android.widget.Spinner;

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

public class MusicFile_Fragment extends Fragment implements Shouting, LifecycleOwner {

    //Variables
    private static final String TAG = "FEHA (MF_Fragment)";


    private MusicFile_ViewModel mModel;
    private EditText mET_Artist;
    private EditText mET_Album;
    private EditText mET_Name;

    ImageView mIV_Search;
    Spinner mSP_Purpose;
    CustomSpinnerAdapter mCSA_Purpose;
    Spinner mSP_Signature;
    CustomSpinnerAdapter mCSA_Signature;
    @SuppressWarnings("FieldCanBeLocal")
    private RecyclerView mRV;

    MusicFile_Adapter mMusicFile_Adapter;
    Shouting mShouting;
    Shout mShoutToCeiling;
    private Shout mGlassFloor;

    //Constructor

    //Setter and Getter
    public void setShouting(Shouting mShouting) {
        this.mShouting = mShouting;
    }

    @SuppressWarnings("unused")
    MusicFile_ViewModel getModel() {
        if (mModel == null) {
            createModel();
        }
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
        //noinspection UnnecessaryLocalVariable
        View view = iInflater.inflate(R.layout.fragment_musicfile, iContainer, false);
        return view;
    }

    @Override
    public void onViewCreated(@Nullable View view, @Nullable Bundle savedInstanceState) {
        mET_Artist = requireView().findViewById(R.id.ET_Musicfile_Artist);
        mET_Album = requireView().findViewById(R.id.ET_Musicfile_Album);
        mET_Name = requireView().findViewById(R.id.ET_Musicfile_Name);
        mIV_Search = requireView().findViewById(R.id.IV_Musicfile_Search);
        mSP_Purpose= requireView().findViewById(R.id.SP_Musicfile_Purpose);
        mSP_Signature= requireView().findViewById(R.id.SP_Musicfile_Signature);

        mRV = requireView().findViewById(R.id.RV_Musicfile);


        if (mMusicFile_Adapter == null) {
            mMusicFile_Adapter = new MusicFile_Adapter(getContext(), mRV, this);
            mMusicFile_Adapter.setMode(MusicFile_Adapter.MODE_STANDARD);
        }
        RecyclerView.LayoutManager LM_MusicFile = new LinearLayoutManager(getContext());
        mRV.setLayoutManager(LM_MusicFile);
        mRV.setAdapter(mMusicFile_Adapter);


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
                    }
                }
            });
//            mET_Artist.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//                @Override
//                public void onFocusChange(View iView, boolean iHasFocus) {
//                }
//            });
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
                    if (mModel != null) {
                        mModel.setSearchAlbum(s.toString());
                      //  mModel.forceSearch();
                    }
                }
            });
//            mET_Album.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//                @Override
//                public void onFocusChange(View iView, boolean iHasFocus) {
//                    if (iHasFocus) {
//                        //user has focused
//                    } else {
//                        //focus has stopped perform your desired action
//                        mModel.forceSearch();
//                    }
//                }
//            });
        }
        if (mET_Name != null) {
            mET_Name.addTextChangedListener(new TextWatcher() {
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
                 //       mModel.forceSearch();
                    }
                }
            });
//            mET_Name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//                @Override
//                public void onFocusChange(View iView, boolean iHasFocus) {
//                    if (iHasFocus) {
//                        //user has focused
//                    } else {
//                        //focus has stopped perform your desired action
//                        mModel.forceSearch();
//                    }
//                }
//            });
        }

        if (mIV_Search != null) {
            mIV_Search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Logg.k(TAG, "OnClick Search");
                    processOnClickSearch();
                }
            });

        }

        SpinnerItemFactory tSpinnerItemFactory;
        ArrayList<CustomSpinnerItem> tAL_Custom_SpinnerItem;
        tSpinnerItemFactory = new SpinnerItemFactory();
        if (mSP_Purpose != null) {
            tAL_Custom_SpinnerItem = tSpinnerItemFactory.
                    getSpinnerItems(SpinnerItemFactory.FIELD_MUSICFILE_PURPOSE, true);
            mCSA_Purpose = new CustomSpinnerAdapter(this.getContext(), tAL_Custom_SpinnerItem);
            mCSA_Purpose.setTitleMode(CustomSpinnerAdapter.MODE_ICON_ONLY);
            mCSA_Purpose.setDropdownMode(CustomSpinnerAdapter.MODE_ICON_TEXT);
            mSP_Purpose.setAdapter(mCSA_Purpose);
          //  mSP_Purpose.setSelection();

            mSP_Purpose.setSelection(mCSA_Purpose.getPosition(getModel().getCsiPurpose().mKey));

            mSP_Purpose.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent,
                                           View view, int position, long id) {
                    Logg.k(TAG, "SP_Purpose: " +
                            parent.getItemAtPosition(position).toString());
                    CustomSpinnerItem tCustomSpinnerItem = (CustomSpinnerItem) mCSA_Purpose.getItem(position);
                    mModel.setCsiPurpose(tCustomSpinnerItem);
                 //   mModel.forceSearch();
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        }
        if (mSP_Signature != null) {
            tAL_Custom_SpinnerItem = tSpinnerItemFactory.
                    getSpinnerItems(SpinnerItemFactory.FIELD_MUSICFILE_SIGNATURE, true);
            mCSA_Signature = new CustomSpinnerAdapter(this.getContext(), tAL_Custom_SpinnerItem);
            mCSA_Signature.setTitleMode(CustomSpinnerAdapter.MODE_TEXT_ONLY);
            mCSA_Signature.setDropdownMode(CustomSpinnerAdapter.MODE_TEXT_ONLY);
            mSP_Signature.setAdapter(mCSA_Signature);

         //   mSP_Signature.setSelection(mCSA_Signature.getPosition(getModel().getCsiSignature().mKey));
            mSP_Signature.setSelection(mCSA_Signature.getPosition("SALL"));

            mSP_Signature.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent,
                                           View view, int position, long id) {
                    Logg.k(TAG, "SP_Signature: " +
                            parent.getItemAtPosition(position).toString());
                    CustomSpinnerItem tCustomSpinnerItem = (CustomSpinnerItem) mCSA_Signature.getItem(position);
                    mModel.setCsiSignature(tCustomSpinnerItem);
                  //  mModel.forceSearch();
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
        if (mModel != null) {
            return;
        }
        Application tApllication;
        tApllication = this.requireActivity().getApplication();
        mModel = ViewModelProvider.AndroidViewModelFactory.
                getInstance(tApllication).create(MusicFile_ViewModel.class);

        mModel.mMLD_A.observe(getViewLifecycleOwner(), iAR_Object -> {
            mMusicFile_Adapter.setA(iAR_Object);
            mMusicFile_Adapter.notifyDataSetChanged();
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
        if (mMusicFile_Adapter != null) {
            mMusicFile_Adapter.onStop();
        }
    }

    //Static Methods
    //Internal Organs

    void processOnClickSearch() {
        Logg.i(TAG, "search click");
        mModel.forceSearch();
    }

    void drawHeader() {
        boolean mSearchAvailable;
        if (mModel != null) {
            if (mET_Artist != null) {
                mET_Artist.setText(mModel.getSearchArtist());
            }
            if (mET_Album != null) {
                mET_Album.setText(mModel.getSearchAlbum());
            }
            if (mET_Name != null) {
                mET_Name.setText(mModel.getSearchName());
            }
            mSearchAvailable = mModel.isSearchPossible();
        } else {
            mSearchAvailable = false;
        }
        if (mIV_Search != null) {
            mIV_Search.setActivated(mSearchAvailable);
        }

    }


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
        Logg.i(TAG, tShoutToCeiling.toString());
        mGlassFloor = tShoutToCeiling;
        process_shouting();
    }

}