package org.pochette.organizer.dance;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import java.util.Locale;
import java.util.Objects;
import java.util.Timer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

@SuppressWarnings("unused")
public class SlimDance_Fragment extends Fragment implements Shouting, LifecycleOwner {

    //Variables
    @SuppressWarnings("FieldMayBeFinal")
    private static String TAG = "FEHA (SlimDance_Fragment)";


    private SlimDance_ViewModel mModel;
    View mView;
    EditText mET_SlimDance_Name;

    Spinner mSP_RhythmType;
    CustomSpinnerAdapter mCSA_RhythmType;
    Spinner mSP_Favourite;
    CustomSpinnerAdapter mCSA_Favourite;
    Spinner mSP_Shape;
    CustomSpinnerAdapter mCSA_Shape;


    ImageView mIV_Search;
    ImageView mIV_MusicFile;
    ImageView mIV_Diagram;
    ImageView mIV_Crib;
    ImageView mIV_Rscds;
    ImageView mIV_Sort;
    RecyclerView mRV_SlimDance;
    //int mRV_Width = 0;
    int mFragmentWidth;


    public SlimDance_Adapter mSlimDance_Adapter;
    Shouting mShouting;
    Shout mShoutToCeiling;
    private Shout mGlassFloor;


    @SuppressWarnings("unused")
    private Timer timer;
    //Constructor

    //Setter and Getter
    public void setShouting(Shouting mShouting) {
        this.mShouting = mShouting;
    }

    public void setFragmentWidth(int iWidth) {
        mFragmentWidth = iWidth;
        if (mSlimDance_Adapter != null) {
            mSlimDance_Adapter.setAvailableWidth(mFragmentWidth);
        }
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
        mView = inflater.inflate(R.layout.fragment_dance, container, false);
        Fragment tParentFragment = getParentFragment();
        if (tParentFragment != null) {
            try {
                mShouting = (Shouting) tParentFragment;
            } catch(ClassCastException e) {
                throw new ClassCastException(tParentFragment.toString()
                        + " must implement Shouting");
            }
        }

        Logg.i(TAG, "onCreateView");
        mView.post(new Runnable() {
            @Override
            public void run() {
                // for instance
                setFragmentWidth(mView.getWidth());
                Logg.i(TAG, String.format(Locale.ENGLISH, "IN POST plain %d x %d", mView.getWidth(), mView.getHeight()));
                Logg.i(TAG, String.format(Locale.ENGLISH, "IN POST masure %d x %d", mView.getMeasuredWidth(), mView.getMeasuredHeight()));
                mView.measure(View.MeasureSpec.AT_MOST, View.MeasureSpec.AT_MOST);
                Logg.i(TAG, String.format(Locale.ENGLISH, "IN POST masure after at most %d x %d", mView.getMeasuredWidth(), mView.getMeasuredHeight()));
            }
        });


        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mSlimDance_Adapter == null) {
            mSlimDance_Adapter = new SlimDance_Adapter(getContext(), mRV_SlimDance, this);
            //noinspection ResultOfMethodCallIgnored
            mSlimDance_Adapter.hasStableIds();
        }
        RecyclerView.LayoutManager LM_SlimDance = new LinearLayoutManager(getContext());
        mRV_SlimDance.setLayoutManager(LM_SlimDance);
        mRV_SlimDance.setAdapter(mSlimDance_Adapter);
        mSlimDance_Adapter.setFragment(this);
        mSlimDance_Adapter.setAvailableWidth(mFragmentWidth);
        createModel();
    }

    @Override
    public void onViewCreated(@Nullable View view, @Nullable Bundle savedInstanceState) {

        if (view != null) {
            Logg.i(TAG, String.format(Locale.ENGLISH, "plain %d x %d", view.getWidth(), view.getHeight()));
            Logg.i(TAG, String.format(Locale.ENGLISH, "masure %d x %d", view.getMeasuredWidth(), view.getMeasuredHeight()));
            view.measure(View.MeasureSpec.AT_MOST, View.MeasureSpec.AT_MOST);
            Logg.i(TAG, String.format(Locale.ENGLISH, "masure after at most %d x %d", view.getMeasuredWidth(), view.getMeasuredHeight()));
        }


        mIV_Search = requireView().findViewById(R.id.IV_Dance_Search);
        if (mIV_Search != null) {
            mIV_Search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Logg.k(TAG, "OnClick Search");
                    processOnClickSearch();
                }
            });
        }

        //<editor-fold desc="ET_RECORDING_Artist">
        mET_SlimDance_Name = requireView().findViewById(R.id.ET_Dance_Dancename);
        mET_SlimDance_Name.setText("");
        mET_SlimDance_Name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                createModel();
                // avoid searches with one or two characters
                String tName = s.toString();
                mModel.setDancename(tName);
            }
        });

        SpinnerItemFactory tSpinnerItemFactory;
        ArrayList<CustomSpinnerItem> tAL_Custom_SpinnerItem;
        tSpinnerItemFactory = new SpinnerItemFactory();

        mSP_Favourite = requireView().findViewById(R.id.SP_Dance_Favourite);
        tAL_Custom_SpinnerItem = tSpinnerItemFactory.
                getSpinnerItems(SpinnerItemFactory.FIELD_DANCE_FAVOURITE, true);
        mCSA_Favourite = new CustomSpinnerAdapter(this.getContext(), tAL_Custom_SpinnerItem);
        mCSA_Favourite.setTitleMode(CustomSpinnerAdapter.MODE_ICON_ONLY);
        mCSA_Favourite.setDropdownMode(CustomSpinnerAdapter.MODE_ICON_TEXT);
        mSP_Favourite.setAdapter(mCSA_Favourite);
        mSP_Favourite.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent,
                                       View view, int position, long id) {
                Logg.k(TAG, "SP_Favourite: " +
                        parent.getItemAtPosition(position).toString());
                CustomSpinnerItem tCustomSpinnerItem = (CustomSpinnerItem) mCSA_Favourite.getItem(position);
                mModel.setCsiFavourite(tCustomSpinnerItem);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        mSP_RhythmType = requireView().findViewById(R.id.SP_Dance_Type);
        tAL_Custom_SpinnerItem = tSpinnerItemFactory.getSpinnerItems(SpinnerItemFactory.FIELD_RHYTHM, true);
        mCSA_RhythmType = new CustomSpinnerAdapter(this.getContext(), tAL_Custom_SpinnerItem);

        mCSA_RhythmType.setTitleMode(CustomSpinnerAdapter.MODE_ICON_ONLY);
        mCSA_RhythmType.setDropdownMode(CustomSpinnerAdapter.MODE_ICON_TEXT);
        mSP_RhythmType.setAdapter(mCSA_RhythmType);
        mSP_RhythmType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent,
                                       View view, int position, long id) {
                Logg.i(TAG, "SP_RhythmType: " +
                        parent.getItemAtPosition(position).toString());
                CustomSpinnerItem tCustomSpinnerItem = (CustomSpinnerItem) mCSA_RhythmType.getItem(position);
                mModel.setCsiRhythmType(tCustomSpinnerItem);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        mSP_Shape = requireView().findViewById(R.id.SP_Dance_Shape);
        tAL_Custom_SpinnerItem = tSpinnerItemFactory.getSpinnerItems(SpinnerItemFactory.FIELD_SHAPE, true);
        mCSA_Shape = new CustomSpinnerAdapter(this.getContext(), tAL_Custom_SpinnerItem);
        mCSA_Shape.setTitleMode(CustomSpinnerAdapter.MODE_TEXT_ONLY);
        mCSA_Shape.setDropdownMode(CustomSpinnerAdapter.MODE_TEXT_ONLY);
        mSP_Shape.setAdapter(mCSA_Shape);

        mSP_Shape.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent,
                                       View view, int position, long id) {
                Logg.k(TAG, "SP_Shape: " +
                        parent.getItemAtPosition(position).toString());
                CustomSpinnerItem tCustomSpinnerItem = (CustomSpinnerItem) mCSA_Shape.getItem(position);
                mModel.setEnumShape(tCustomSpinnerItem);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        mIV_MusicFile = requireView().findViewById(R.id.Dance_IV_MusicFile);
        mIV_MusicFile.setOnClickListener(iView -> {
            Logg.k(TAG, "IV_Music OnClick");
            Log.w(TAG, "MusicClick");
            mModel.setFlagMusic(!mModel.getFlagMusic());
            drawHeader();
        });

        mIV_Diagram = requireView().findViewById(R.id.Dance_IV_Diagram);
        if (mIV_Diagram != null) {
            mIV_Diagram.setOnClickListener(iView -> {
                Logg.k(TAG, "IV_Diagram OnClick");
                mModel.setFlagDiagram(!mModel.getFlagDiagram());
//                mCModel.setFlagDiagram(!mCModel.getFlagDiagram());
                drawHeader();
            });
            mIV_Diagram.setOnLongClickListener(iView -> {
                Logg.k(TAG, "IV_Crib OnLongClick");
                mSlimDance_Adapter.cycleDiagramExpansion();
                return true;
            });
        }


        mIV_Crib = requireView().findViewById(R.id.IV_Dance_Crib);
        if (mIV_Crib != null) {
            mIV_Crib.setOnClickListener(iView -> {
                Logg.k(TAG, "IV_Crib OnClick");
                mModel.setFlagCrib(!mModel.getFlagCrib());
//                mCModel.setFlagCrib(!mCModel.getFlagCrib());
                drawHeader();
            });
            mIV_Crib.setOnLongClickListener(iView -> {
                Logg.k(TAG, "IV_Crib OnLongClick");
                mSlimDance_Adapter.cycleCribExpansion();

                return true;
            });
        }


        mIV_Rscds = requireView().findViewById(R.id.IV_Dance_Rscds);
        mIV_Rscds.setOnClickListener(iView -> {
            Logg.k(TAG, "IV_Rscds OnClick");
            mModel.setFlagRscds(!mModel.getFlagRscds());
//            mCModel.setFlagRscds(!mCModel.getFlagRscds());
            drawHeader();
        });

        mIV_Sort = requireView().findViewById(R.id.IV_Dance_Sort);
        mIV_Sort.setOnClickListener(iView -> {
            Logg.k(TAG, "IV_Sort OnClick");
            mSlimDance_Adapter.cycleSortMode();
            drawHeader();

        });


        ///////////////////////recyclerview//////////////////////////////
        mRV_SlimDance = requireView().findViewById(R.id.RV_Dance_Dance);
        //</editor-fold>
        drawHeader();


    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        prepValues(outState);
    }


    /**
     * Called when the fragment is no longer attached to its activity.  This
     * is called after {@link #onDestroy()}.
     */
    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onStart() {
        super.onStart();
        Logg.i(TAG, "onStart");
        if (mModel == null) {
            createModel();
        } else {
            mModel.forceSearch();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mSlimDance_Adapter != null) {
            mSlimDance_Adapter.onStop();
        }
    }

    @SuppressWarnings("WeakerAccess")
    void prepValues(Bundle iBundle) {
        String tKey = "SlimDancename";
        iBundle.putString(tKey, mModel.getDancename());
    }


    /**
     * set the headers as stored in the model
     */
    private void drawHeader() {
//
        int tColorNotSelected = ContextCompat.getColor(Objects.requireNonNull(mIV_Rscds.getContext()), R.color.scream_transparent);
        int tColorSelected = ContextCompat.getColor(Objects.requireNonNull(mIV_Rscds.getContext()), R.color.bg_list_standard);
        int tColor;

        createModel();

        mET_SlimDance_Name.setText(mModel.getDancename());


        if (mModel.getFlagMusic()) {
            tColor = tColorSelected;
        } else {
            tColor = tColorNotSelected;
        }
        mIV_MusicFile.setBackgroundColor(tColor);


        if (mModel.getFlagDiagram()) {
            tColor = tColorSelected;
        } else {
            tColor = tColorNotSelected;
        }
        mIV_Diagram.setBackgroundColor(tColor);

        if (mModel.getFlagCrib()) {
            tColor = tColorSelected;
        } else {
            tColor = tColorNotSelected;
        }
        mIV_Crib.setBackgroundColor(tColor);

        if (mModel.getFlagRscds()) {
            tColor = tColorSelected;
        } else {
            tColor = tColorNotSelected;
        }
        mIV_Rscds.setBackgroundColor(tColor);

        if (mIV_Sort != null && mSlimDance_Adapter != null) {

            int tSortMode = mSlimDance_Adapter.getSortMode();
            if (tSortMode == 1) {
                mIV_Sort.setImageResource(R.drawable.ic_sort_byfavorite);
            } else {

                mIV_Sort.setImageResource(R.drawable.ic_sort_byname);
            }
        }

        boolean mSearchAvailable;
        if (mModel != null) {
            mSearchAvailable = mModel.isSearchPossible();
        } else {
            mSearchAvailable = false;
        }
        if (mIV_Search != null) {
            mIV_Search.setActivated(mSearchAvailable);
        }

    }


    private void createModel() {
        if (mModel != null) {
            return;
        }
        Application tApllication;
        tApllication = this.requireActivity().getApplication();
        mModel = ViewModelProvider.AndroidViewModelFactory.
                getInstance(tApllication).create(SlimDance_ViewModel.class);

        mModel.mMLD_A.observe(getViewLifecycleOwner(), iAR_Object -> {
            Logg.i(TAG, "obervce for A");
            Integer[] tA = (Integer[]) iAR_Object;
            mSlimDance_Adapter.setA(tA);
            mSlimDance_Adapter.notifyDataSetChanged();
        });

        mModel.forceSearch();
    }


    void processOnClickSearch() {
        Logg.i(TAG, "search click");
        mModel.forceSearch();
    }

    //Static Methods

    //Internal Organs
    private void process_shouting() {
        //noinspection SwitchStatementWithTooFewBranches
        switch (mGlassFloor.mActor) {
            case "ScddbRecording_Adapter":
                if (mGlassFloor.mLastObject.equals("Search") &&
                        mGlassFloor.mLastAction.equals("Finished")) {
                    //	Logg.i(TAG, "Call notify");

                    mSlimDance_Adapter.notifyDataSetChanged();
                }
        }
        if (mGlassFloor.mLastObject.equals("Item") &&
                (mGlassFloor.mLastAction.equals("LongClick") || mGlassFloor.mLastAction.equals("select"))) {
            mShoutToCeiling.mLastObject = mGlassFloor.mLastObject;
            mShoutToCeiling.mLastAction = mGlassFloor.mLastAction;
            if (mShouting != null) {
                mShouting.shoutUp(mShoutToCeiling);
            }

            if (mGlassFloor.mLastAction.equals("LongClick")) {
                createModel();
                mModel.forceSearch();
                //           mCModel.forceSearch();
            }
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
        mGlassFloor = tShoutToCeiling;
        process_shouting();
    }

}