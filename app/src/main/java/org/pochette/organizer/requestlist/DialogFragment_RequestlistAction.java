package org.pochette.organizer.requestlist;

import android.annotation.SuppressLint;
import android.app.Application;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.pochette.data_library.database_management.SearchCall;
import org.pochette.data_library.requestlist.Requestlist;
import org.pochette.data_library.requestlist.Requestlist_Cache;
import org.pochette.data_library.requestlist.Requestlist_Purpose;
import org.pochette.organizer.R;
import org.pochette.organizer.gui_assist.CustomSpinnerAdapter;
import org.pochette.organizer.gui_assist.CustomSpinnerItem;
import org.pochette.organizer.gui_assist.SpinnerItemFactory;
import org.pochette.utils_lib.logg.Logg;
import org.pochette.utils_lib.search.SearchCriteria;
import org.pochette.utils_lib.search.SearchPattern;
import org.pochette.utils_lib.shouting.Shout;
import org.pochette.utils_lib.shouting.Shouting;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * DialogFragment to work with Requestlist Header
 */
@SuppressWarnings("FieldCanBeLocal")
public class DialogFragment_RequestlistAction extends DialogFragment implements Shouting {

    public final static int SEARCH_MODE = 0;
    public final static int SELECT_MODE = 1; // Search+ Create
    public final static int EDIT_MODE = 2;
    public final static int CREATE_MODE = 3;
    private static final String TAG = "FEHA (ActDi_Requestlist)";
    //Variables

    private int mMode;
    private Requestlist_Purpose mRequestlist_Purpose;
    private Requestlist mRequestlist; // for edit mode

    private TextView mTV_Title;
    private TextView mTV_Requestlist_Label;
    private TextView mTV_Requestlist_Default;
    private Button mPB_Requestlist_Create;
    private EditText mET_Name;
    private Spinner mSP_Purpose;
    private RecyclerView mRequestlistTheme_RecyclerView;
    private RecyclerView mRequestlistEvent_RecyclerView;
    private RecyclerView mRequestlistOther_RecyclerView;

    private Requestlist_ViewModel mRequestlistTheme_ViewModel;
    private Requestlist_ViewModel mRequestlistEvent_ViewModel;
    private Requestlist_ViewModel mRequestlistOther_ViewModel;
    private Requestlist_Adapter mRequestlistTheme_Adapter;
    private Requestlist_Adapter mRequestlistEvent_Adapter;
    private Requestlist_Adapter mRequestlistOther_Adapter;
    private CustomSpinnerAdapter mCSA_Purpose;

    //private Shouting mShoutingInternal; // within the dialog
    private Shouting mShoutingCaller; // to send the result back to
    private Shout mGlassFloor;

    //Constructor
    public DialogFragment_RequestlistAction() {
        mMode = SEARCH_MODE;
        mRequestlist_Purpose = Requestlist_Purpose.UNDEFINED;
    }

    public static DialogFragment_RequestlistAction newInstance(String title) {
        DialogFragment_RequestlistAction frag = new DialogFragment_RequestlistAction();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    //Setter and Getter
    public void setRequestlist(Requestlist iRequestlist) {
        mRequestlist = iRequestlist;
    }

    /**
     * @param iMode SEARCH_MODE(2), SELECT_MODE (1), EDIT_MODE (0)
     */
    public void setMode(int iMode) {
        mMode = iMode;
    }

    //Livecycle
    public void setShoutingCaller(Shouting tShouting) {
        mShoutingCaller = tShouting;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_requestlist, container);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View iView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(iView, savedInstanceState);
        Logg.i(TAG, "onViewCreated " + mMode + " " + this.hashCode());
        mTV_Title = iView.findViewById(R.id.TV_RequestlistAction_Title);
        mTV_Requestlist_Label = iView.findViewById(R.id.TV_RequestlistAction_Label);
        mTV_Requestlist_Default = iView.findViewById(R.id.TV_RequestlistAction_DefaultRequestlist);
        mPB_Requestlist_Create = iView.findViewById(R.id.PB_RequestlistAction_Create);
        mET_Name = iView.findViewById(R.id.ET_RequestlistAction_Name);

        switch (mMode) {
            case SEARCH_MODE:
                mTV_Title.setText("Search");
                mPB_Requestlist_Create.setVisibility(View.GONE);
                showDefault();
                showET();
                showSP();
                showThreeRV(iView);
                break;
            case SELECT_MODE:
                mTV_Title.setText("Select");
                mPB_Requestlist_Create.setVisibility(View.INVISIBLE);
                showDefault();
                hideET();
                hideSP();
                showThreeRV(iView);
                break;
            case EDIT_MODE:
                if (mRequestlist == null) {
                    throw new RuntimeException("Edit requires an existing Requestlist");
                }
                mTV_Title.setText("Edit");
                mET_Name.setText(mRequestlist.getName());
                mPB_Requestlist_Create.setVisibility(View.VISIBLE);
                mPB_Requestlist_Create.setText("Save");
                hideDefault();
                showET();
                showSP();
                hideThreeRV(iView);
                mSP_Purpose.setSelection(mCSA_Purpose.getPosition(mRequestlist.getRequestlist_Purpose().getCode()));
                mPB_Requestlist_Create.setOnClickListener(v -> {
                    String tName = mET_Name.getText().toString();
                    updateRequestlist(tName, mRequestlist_Purpose);
                    dismiss();
                });
                break;
            case CREATE_MODE:
                mTV_Title.setText("Create Requestlist");
                mPB_Requestlist_Create.setVisibility(View.VISIBLE);
                mPB_Requestlist_Create.setText("Save");
                hideDefault();
                showET();
                showSP();
                hideThreeRV(iView);
                mPB_Requestlist_Create.setOnClickListener(v -> {
                    String tName = mET_Name.getText().toString();
                    createNewRequestlist(tName, mRequestlist_Purpose);
                    dismiss();
                });
                break;
        }


    }

    @Override
    public void dismiss() {
        if (mShoutingCaller != null) {
            Shout tShout = new Shout(this.getClass().getSimpleName());
            tShout.mLastObject = "Dialog";
            tShout.mLastAction = "dismissed";
            mShoutingCaller.shoutUp(tShout);
        }
        super.dismiss();
    }

    //Static Methods
    //Internal Organs

    void hideDefault() {
        mTV_Requestlist_Default.setVisibility(View.GONE);
        mTV_Requestlist_Label.setVisibility(View.GONE);
    }

    @SuppressLint("SetTextI18n")
    void showDefault() {
        mTV_Requestlist_Default.setVisibility(View.VISIBLE);
        Requestlist tRequestlist = Requestlist.getDefaultRequestlist();
        if (tRequestlist == null) {
            mTV_Requestlist_Default.setText("not defined");
            mTV_Requestlist_Default.setTypeface(null, Typeface.ITALIC);
            mTV_Requestlist_Default.setOnClickListener(null);
        } else {
            mTV_Requestlist_Default.setText(tRequestlist.getName());
            mTV_Requestlist_Default.setTypeface(null, Typeface.NORMAL);
            mTV_Requestlist_Default.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // the default list has been clicked, so just confirm the choice
                    executeChoice(Requestlist.getDefaultRequestlist());
                }
            });
        }
        mTV_Requestlist_Label.setVisibility(View.VISIBLE);
        mTV_Requestlist_Default.setHint("Name");
    }


    void hideET() {
        mET_Name.setVisibility(View.GONE);
    }

    void showET() {
        mET_Name.setVisibility(View.VISIBLE);
    }

    void hideSP() {
        mSP_Purpose = requireView().findViewById(R.id.SP_RequestlistAction_Purpose);
        mSP_Purpose.setVisibility(View.GONE);
    }

    void showSP() {
        SpinnerItemFactory tSpinnerItemFactory = new SpinnerItemFactory();
        ArrayList<CustomSpinnerItem> tAL_CustomSpinnerItem;
        mSP_Purpose = requireView().findViewById(R.id.SP_RequestlistAction_Purpose);
        mSP_Purpose.setVisibility(View.VISIBLE);
        tAL_CustomSpinnerItem = tSpinnerItemFactory.
                getSpinnerItems(SpinnerItemFactory.FIELD_REQUESTLIST_PURPOSE, false);
        mCSA_Purpose = new CustomSpinnerAdapter(this.getContext(), tAL_CustomSpinnerItem);
        mCSA_Purpose.setTitleMode(CustomSpinnerAdapter.MODE_ICON_TEXT);
        mCSA_Purpose.setDropdownMode(CustomSpinnerAdapter.MODE_ICON_TEXT);
        mSP_Purpose.setAdapter(mCSA_Purpose);
        mSP_Purpose.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent,
                                       View view, int position, long id) {
                Logg.k(TAG, "SP_Favourite: " +
                        parent.getItemAtPosition(position).toString());
                CustomSpinnerItem tCustomSpinnerItem = (CustomSpinnerItem) mCSA_Purpose.getItem(position);
                //Logg.i(TAG, tCustomSpinnerItem.mValue);
                mRequestlist_Purpose = Requestlist_Purpose.fromCode(tCustomSpinnerItem.mValue);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    void showThreeRV(View iView) {
        Requestlist_Purpose tRequestlist_Purpose;
        SpinnerItemFactory tSpinnerItemFactory = new SpinnerItemFactory();
        CustomSpinnerItem tCustomSpinnerItem;
        RecyclerView.LayoutManager tLayoutManagerTheme;
        Application tApllication = this.requireActivity().getApplication();

        ////////////////////////////////////////////
        // setup the Requestlist column theme = purpose
        tRequestlist_Purpose = Requestlist_Purpose.THEME;
        tCustomSpinnerItem = tSpinnerItemFactory.getSpinnerItem(
                SpinnerItemFactory.FIELD_REQUESTLIST_PURPOSE, tRequestlist_Purpose.getCode());


        mRequestlistTheme_RecyclerView = iView.findViewById(R.id.RV_RequestlistAction_Theme);
        tLayoutManagerTheme = new LinearLayoutManager(this.getContext());
        mRequestlistTheme_RecyclerView.setLayoutManager(tLayoutManagerTheme);
        mRequestlistTheme_RecyclerView.setVisibility(View.VISIBLE);

        mRequestlistTheme_Adapter = new Requestlist_Adapter(getContext(), null, this);
        mRequestlistTheme_Adapter.setLayoutMode(Requestlist_Adapter.LAYOUT_MODE_COMPACT);
        mRequestlistTheme_RecyclerView.setAdapter(mRequestlistTheme_Adapter);

        mRequestlistTheme_ViewModel = ViewModelProvider.AndroidViewModelFactory.
                getInstance(tApllication).create(Requestlist_ViewModel.class);
        mRequestlistTheme_ViewModel.setSearchPurpose(tCustomSpinnerItem);
        mRequestlistTheme_ViewModel.mMLD_AR.observe(getViewLifecycleOwner(), iAR_Object -> {
            if (iAR_Object != null) {
                ArrayList<Requestlist> tAR = new ArrayList<>(0);
                for (Object lObject : iAR_Object) {
                    Requestlist lRequestlist;
                    lRequestlist = (Requestlist) lObject;
                    tAR.add(lRequestlist);
                }
                mRequestlistTheme_Adapter.setAR(tAR);
                mRequestlistTheme_Adapter.notifyDataSetChanged();
            }
        });
        mRequestlistTheme_ViewModel.forceSearch();

        ////////////////////////////////////////////
        // setup the Requestlist column theme = Event

        tRequestlist_Purpose = Requestlist_Purpose.EVENT;

        tCustomSpinnerItem = tSpinnerItemFactory.getSpinnerItem(
                SpinnerItemFactory.FIELD_REQUESTLIST_PURPOSE, tRequestlist_Purpose.getCode());

        mRequestlistEvent_RecyclerView = iView.findViewById(R.id.RV_RequestlistAction_Event);
        tLayoutManagerTheme = new LinearLayoutManager(this.getContext());
        mRequestlistEvent_RecyclerView.setLayoutManager(tLayoutManagerTheme);
        mRequestlistEvent_RecyclerView.setVisibility(View.VISIBLE);

        mRequestlistEvent_Adapter = new Requestlist_Adapter(getContext(), null, this);
        mRequestlistEvent_Adapter.setLayoutMode(Requestlist_Adapter.LAYOUT_MODE_COMPACT);
        mRequestlistEvent_RecyclerView.setAdapter(mRequestlistEvent_Adapter);

        mRequestlistEvent_ViewModel = ViewModelProvider.AndroidViewModelFactory.
                getInstance(tApllication).create(Requestlist_ViewModel.class);
        mRequestlistEvent_ViewModel.setSearchPurpose(tCustomSpinnerItem);
        mRequestlistEvent_ViewModel.mMLD_AR.observe(getViewLifecycleOwner(), iAR_Object -> {
            if (iAR_Object != null) {
                ArrayList<Requestlist> tAR = new ArrayList<>(0);
                for (Object lObject : iAR_Object) {
                    Requestlist lRequestlist;
                    lRequestlist = (Requestlist) lObject;
                    tAR.add(lRequestlist);
                }
                mRequestlistEvent_Adapter.setAR(tAR);
                mRequestlistEvent_Adapter.notifyDataSetChanged();
            }
        });
        mRequestlistEvent_ViewModel.forceSearch();

        ////////////////////////////////////////////
        // setup the Requestlist column theme = Event

        tRequestlist_Purpose = Requestlist_Purpose.UNDEFINED;

        tCustomSpinnerItem = tSpinnerItemFactory.getSpinnerItem(
                SpinnerItemFactory.FIELD_REQUESTLIST_PURPOSE, tRequestlist_Purpose.getCode());


        mRequestlistOther_RecyclerView = iView.findViewById(R.id.RV_RequestlistAction_Other);
        tLayoutManagerTheme = new LinearLayoutManager(this.getContext());
        mRequestlistOther_RecyclerView.setLayoutManager(tLayoutManagerTheme);
        mRequestlistOther_RecyclerView.setVisibility(View.VISIBLE);

        mRequestlistOther_Adapter = new Requestlist_Adapter(getContext(), null, this);
        mRequestlistOther_Adapter.setLayoutMode(Requestlist_Adapter.LAYOUT_MODE_COMPACT);
        mRequestlistOther_RecyclerView.setAdapter(mRequestlistOther_Adapter);

        mRequestlistOther_ViewModel = ViewModelProvider.AndroidViewModelFactory.
                getInstance(tApllication).create(Requestlist_ViewModel.class);
        mRequestlistOther_ViewModel.setSearchPurpose(tCustomSpinnerItem);
        mRequestlistOther_ViewModel.mMLD_AR.observe(getViewLifecycleOwner(), iAR_Object -> {
            if (iAR_Object != null) {
                ArrayList<Requestlist> tAR = new ArrayList<>(0);
                for (Object lObject : iAR_Object) {
                    Requestlist lRequestlist;
                    lRequestlist = (Requestlist) lObject;
                    tAR.add(lRequestlist);
                }
                mRequestlistOther_Adapter.setAR(tAR);
                mRequestlistOther_Adapter.notifyDataSetChanged();
            }
        });
        mRequestlistOther_ViewModel.forceSearch();
    }

    void hideThreeRV(View iView) {
        iView.findViewById(R.id.RV_RequestlistAction_Theme).setVisibility(View.GONE);
        iView.findViewById(R.id.RV_RequestlistAction_Event).setVisibility(View.GONE);
        iView.findViewById(R.id.RV_RequestlistAction_Other).setVisibility(View.GONE);
        iView.findViewById(R.id.TV_RequestlistAction_Theme).setVisibility(View.GONE);
        iView.findViewById(R.id.TV_RequestlistAction_Event).setVisibility(View.GONE);
        iView.findViewById(R.id.TV_RequestlistAction_Other).setVisibility(View.GONE);
    }


    void updateRequestlist(String iName, Requestlist_Purpose iRequestlist_purpose) {
        if (mRequestlist != null) {
            mRequestlist.setRequestlist_Purpose(iRequestlist_purpose);
            mRequestlist.setName(iName);
            mRequestlist.save();
            Requestlist_Cache.updateCache(mRequestlist);
        }
    }

    void createNewRequestlist(String iName, Requestlist_Purpose iRequestlist_purpose) {
        // check whether name already exist,
        SearchPattern tSearchPattern;
        tSearchPattern = new SearchPattern(Requestlist.class);
        tSearchPattern.addSearch_Criteria(new SearchCriteria("NAME", iName));
        SearchCall tSearchCall =
                new SearchCall(Requestlist.class, tSearchPattern, null);
        Requestlist tRequestlist = tSearchCall.produceFirst();
        if (tRequestlist != null) {
            Logg.w(TAG, "Requestlist exists already " + iName);
        }
        tRequestlist = new Requestlist(iName, iRequestlist_purpose);
        tRequestlist.save();
        Requestlist.setDefaultRequestlist(tRequestlist);
        Requestlist_Cache.updateCache(tRequestlist);
        if (mShoutingCaller != null) {
            Shout tShout = new Shout(this.getClass().getSimpleName());
            tShout.storeObject(tRequestlist);
            tShout.mLastObject = "Choice";
            tShout.mLastAction = "performed";
            mShoutingCaller.shoutUp(tShout);
        }
    }

    private void executeChoice(Requestlist iRequestlist) {
        // do no wait for the finishing, so put in thread
        Runnable tRunnable = new Runnable() {
            @Override
            public void run() {
                Thread.currentThread().setName("exeChoice Requestlist_Action");
                if (iRequestlist != Requestlist.getDefaultRequestlist()) {
                    Requestlist.setDefaultRequestlist(iRequestlist);
                }
                if (mShoutingCaller != null) {
                    Shout tShout = new Shout("DialogFragment_RequestlistAction");
                    tShout.mLastObject = "Choice";
                    tShout.mLastAction = "performed";
                    JSONObject tJson = new JSONObject();
                    try {
                        tJson.put("Id", iRequestlist.getId());
                        tJson.put("Name", iRequestlist.getName());
                        tJson.put("Purpose", iRequestlist.getRequestlist_Purpose().getCode());
                        tShout.mJsonString = tJson.toString();
                        tShout.storeObject(iRequestlist);
                        mShoutingCaller.shoutUp(tShout);
                    } catch(JSONException e) {
                        Logg.e(TAG, e.toString());
                    }
                }
            }
        };
        Thread tThread = new Thread(tRunnable);
        tThread.start();
    }

    //Interface

    private void process_shouting() {
//		if (mGlassFloor.mActor.equals("Requestlist_Action")) {
//			Logg.i(TAG, mGlassFloor.toString());
//		}
        if (mGlassFloor.mActor.equals("Requestlist_Adapter")) {
            if (mGlassFloor.mLastObject.equals("Row")) {
                if (mGlassFloor.mLastAction.equals("selected")) {
                    if (mGlassFloor.mJsonString != null && !mGlassFloor.mJsonString.isEmpty()) {
                        Requestlist tRequestlist;
                        int tRequestlist_Id = 0;
                        //noinspection IfStatementWithIdenticalBranches
                        if (mGlassFloor.carriesObject()) {
                            tRequestlist = mGlassFloor.returnObject();
                            if (tRequestlist != null) {
                                //Logg.i(TAG, "O" + tRequestlist.toString());
                                executeChoice(tRequestlist);
                            }
                        } else {
                            try {
                                JSONObject tParameter = new JSONObject(mGlassFloor.mJsonString);
                                tRequestlist_Id = tParameter.getInt("Id");
                            } catch(JSONException e) {
                                Logg.e(TAG, e.toString());
                            }
//                            SearchPattern tSearchPattern;
//                            tSearchPattern = new SearchPattern(Requestlist.class);
//                            tSearchPattern.addSearch_Criteria(new SearchCriteria("ID", "" + tRequestlist_Id));
//                            SearchCall tSearchCall =
//                                    new SearchCall(Requestlist.class, tSearchPattern, null);
//                            tRequestlist = tSearchCall.produceFirst();

                            tRequestlist = Requestlist_Cache.getById(tRequestlist_Id);
                            if (tRequestlist != null) {
                                //Logg.i(TAG, "U" + tRequestlist.toString());
                                executeChoice(tRequestlist);
                            }
                        }
                        dismiss();
                    }
                }
            }
        }
    }


    public static void create(View iView, int iMode, Shouting iShouting) {
        DialogFragment_RequestlistAction t_DialogFragmentRequestlist =
                DialogFragment_RequestlistAction.newInstance("Some Title");
        t_DialogFragmentRequestlist.setMode(iMode);
        //  Logg.i(TAG, "setMode past with view");
        t_DialogFragmentRequestlist.mShoutingCaller = iShouting;

        AppCompatActivity tActivity = ((AppCompatActivity) iView.getContext());
        t_DialogFragmentRequestlist.show(tActivity.getSupportFragmentManager(), null);
    }

    @Override
    public void shoutUp(Shout tShoutToCeiling) {
        Logg.i(TAG, tShoutToCeiling.toString());
        mGlassFloor = tShoutToCeiling;
        process_shouting();
    }

}
