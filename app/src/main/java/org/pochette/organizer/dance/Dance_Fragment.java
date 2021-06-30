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

import org.json.JSONException;
import org.json.JSONObject;
import org.pochette.data_library.scddb_objects.Dance;
import org.pochette.organizer.gui_assist.CustomSpinnerAdapter;
import org.pochette.organizer.R;
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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

@SuppressWarnings("unused")
public class Dance_Fragment extends Fragment implements Shouting, LifecycleOwner {

    //Variables
    @SuppressWarnings("FieldMayBeFinal")
    private static String TAG = "FEHA (Dance_Fragment)";


    private Dance_ViewModel mModel;
    View mView;
    EditText mET_Dance_Name;

    Spinner mSP_RhythmType;
    CustomSpinnerAdapter mCSA_RhythmType;
    Spinner mSP_Favourite;
    CustomSpinnerAdapter mCSA_Favourite;
    Spinner mSP_Shape;
    CustomSpinnerAdapter mCSA_Shape;

    ImageView mIV_MusicFile;
    ImageView mIV_Diagram;
    ImageView mIV_Crib;
    ImageView mIV_Rscds;
    ImageView mIV_Sort;
    RecyclerView mRV_Dance;
    //int mRV_Width = 0;
    int mFragmentWidth;


    public Dance_Adapter mDance_Adapter;
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
        if (mDance_Adapter != null) {
            mDance_Adapter.setAvailableWidth(mFragmentWidth);
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

        Logg.w(TAG, "onCreateView");
        mView.post(new Runnable() {
            @Override
            public void run() {
                // for instance
                setFragmentWidth(mView.getWidth());
                Logg.i(TAG, String.format(Locale.ENGLISH, "IN POST plain %d x %d",mView.getWidth(),mView.getHeight() ));
                Logg.i(TAG, String.format(Locale.ENGLISH, "IN POST masure %d x %d",mView.getMeasuredWidth(),mView.getMeasuredHeight() ));
                mView.measure(View.MeasureSpec.AT_MOST,View.MeasureSpec.AT_MOST);
                Logg.i(TAG, String.format(Locale.ENGLISH, "IN POST masure after at most %d x %d",mView.getMeasuredWidth(),mView.getMeasuredHeight() ));
            }
        });



        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mDance_Adapter == null) {
            mDance_Adapter = new Dance_Adapter(getContext(), mRV_Dance, this);
            //noinspection ResultOfMethodCallIgnored
            mDance_Adapter.hasStableIds();
        }
        RecyclerView.LayoutManager LM_Dance = new LinearLayoutManager(getContext());
        mRV_Dance.setLayoutManager(LM_Dance);
        mRV_Dance.setAdapter(mDance_Adapter);
        mDance_Adapter.setFragment(this);
        mDance_Adapter.setAvailableWidth(mFragmentWidth);
        createModel();
    }

    @Override
    public void onViewCreated(@Nullable View view, @Nullable Bundle savedInstanceState) {

        if (view != null) {
            Logg.i(TAG, String.format(Locale.ENGLISH, "plain %d x %d",view.getWidth(),view.getHeight() ));
            Logg.i(TAG, String.format(Locale.ENGLISH, "masure %d x %d",view.getMeasuredWidth(),view.getMeasuredHeight() ));
            view.measure(View.MeasureSpec.AT_MOST,View.MeasureSpec.AT_MOST);
            Logg.i(TAG, String.format(Locale.ENGLISH, "masure after at most %d x %d",view.getMeasuredWidth(),view.getMeasuredHeight() ));
        }
        //<editor-fold desc="ET_RECORDING_Artist">
        mET_Dance_Name = requireView().findViewById(R.id.ET_Dance_Dancename);
        mET_Dance_Name.setText("");
        mET_Dance_Name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                createModel();
                Log.w(TAG, "Call setDanceName");
                mModel.setDancename(s.toString());
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


        mSP_RhythmType = requireView().findViewById(R.id.SP_Dance_Playlist);
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
                mDance_Adapter.cycleDiagramExpansion();
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
                mDance_Adapter.cycleCribExpansion();

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
            mDance_Adapter.cycleSortMode();
            drawHeader();

        });


        ///////////////////////recyclerview//////////////////////////////
        mRV_Dance = requireView().findViewById(R.id.RV_Dance_Dance);
//        mRV_Dance.addOnItemTouchListener(new RecyclerItemClickListener(mRV_Dance.getContext(),
//                (view1, position) -> {
//                }));

//        if (mRV_Dance != null) {
//            Logg.i(TAG, "mRV Dance");
//
//            mRV_Dance.post(new Runnable() {
//                @Override
//                public void run() {
//                    Logg.w(TAG, "in run");
//                    mRV_Dance.requestLayout();
//
//                    mRV_Dance.measure(View.MeasureSpec.AT_MOST, View.MeasureSpec.AT_MOST);
//                    Logg.i(TAG, String.format(Locale.ENGLISH, "at most in post measured %d x %d",mRV_Dance.getMeasuredWidth() ,mRV_Dance.getMeasuredHeight() ));
//                    Logg.i(TAG, "post layout");
//                }
//            });
//            mRV_Dance.measure(View.MeasureSpec.EXACTLY, View.MeasureSpec.EXACTLY);
//            Logg.i(TAG, String.format(Locale.ENGLISH, "exactlz measured %d x %d",mRV_Dance.getMeasuredWidth() ,mRV_Dance.getMeasuredHeight() ));
//
//            mRV_Dance.measure(View.MeasureSpec.AT_MOST, View.MeasureSpec.AT_MOST);
//            Logg.i(TAG, String.format(Locale.ENGLISH, "at moset measured %d x %d",mRV_Dance.getMeasuredWidth() ,mRV_Dance.getMeasuredHeight() ));
////            mDance_Adapter.setAvailableWidth(mRV_Width);
//        }



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
        if (mDance_Adapter != null) {
            mDance_Adapter.onStop();
        }
    }

    @SuppressWarnings("WeakerAccess")
    void prepValues(Bundle iBundle) {
        String tKey = "Dancename";
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

        mET_Dance_Name.setText(mModel.getDancename());



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

        if (mIV_Sort != null && mDance_Adapter != null) {

            int tSortMode = mDance_Adapter.getSortMode();
            if (tSortMode == 1) {
                mIV_Sort.setImageResource(R.drawable.ic_sort_byfavorite);
            } else {

                mIV_Sort.setImageResource(R.drawable.ic_sort_byname);
            }
        }


    }


    private void createModel() {
        //Logg.v(TAG, "CreateModel");
        if (mModel != null) {
            return;
        }
        Application tApllication;
        tApllication = this.requireActivity().getApplication();
        mModel = ViewModelProvider.AndroidViewModelFactory.
                getInstance(tApllication).create(Dance_ViewModel.class);
        MutableLiveData<ArrayList<Object>> tMLD_AR;
        //        if (tMLD_AR == null) {
//            Logg.w(TAG, "null");
//        } else {
//            Logg.i(TAG, tMLD_AR.toString());
//        }

        mModel.mMLD_AR.observe(getViewLifecycleOwner(), iAR_Object -> {
            if (iAR_Object != null) {
                try {
                    ArrayList<Dance> tAR_Dance = new ArrayList<>(0);
                    int i = 0;
                    for (Object lObject : iAR_Object) {
                        Dance lDance;
                        lDance = (Dance) lObject;
                        tAR_Dance.add(lDance);
//                        if (i % 50 == 0) {
//                            Logg.i(TAG, i + "->" + lDance.toString());
//                        }
                        i++;
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



    //Static Methods

    //Internal Organs
    private void process_shouting() {
        //noinspection SwitchStatementWithTooFewBranches
        switch (mGlassFloor.mActor) {
            case "ScddbRecording_Adapter":
                if (mGlassFloor.mLastObject.equals("Search") &&
                        mGlassFloor.mLastAction.equals("Finished")) {
                    //	Logg.i(TAG, "Call notify");

                    mDance_Adapter.notifyDataSetChanged();
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