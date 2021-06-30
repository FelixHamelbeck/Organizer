package org.pochette.organizer.playlist;

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
import org.pochette.data_library.playlist.Playlist;
import org.pochette.data_library.playlist.Playlist_Purpose;
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
 * DialogFragment to work with Playlist Header
 */
@SuppressWarnings("FieldCanBeLocal")
public class DialogFragment_PlaylistAction extends DialogFragment implements Shouting {

    public final static int SEARCH_MODE = 0;
    public final static int SELECT_MODE = 1; // Search+ Create
    public final static int EDIT_MODE = 2;
    public final static int CREATE_MODE = 3;
    private static final String TAG = "FEHA (ActDi_Playlist)";
    //Variables

    private int mMode;
    private Playlist_Purpose mPlaylist_Purpose;
    private Playlist mPlaylist; // for edit mode

    private TextView mTV_Title;
    private TextView mTV_Playlist_Label;
    private TextView mTV_Playlist_Default;
    private Button mPB_Playlist_Create;
    private EditText mET_Name;
    private Spinner mSP_Purpose;
    private RecyclerView mPlaylistTheme_RecyclerView;
    private RecyclerView mPlaylistEvent_RecyclerView;
    private RecyclerView mPlaylistOther_RecyclerView;

    private Playlist_ViewModel mPlaylistTheme_ViewModel;
    private Playlist_ViewModel mPlaylistEvent_ViewModel;
    private Playlist_ViewModel mPlaylistOther_ViewModel;
    private Playlist_Adapter mPlaylistTheme_Adapter;
    private Playlist_Adapter mPlaylistEvent_Adapter;
    private Playlist_Adapter mPlaylistOther_Adapter;
    private CustomSpinnerAdapter mCSA_Purpose;

    //private Shouting mShoutingInternal; // within the dialog
    private Shouting mShoutingCaller; // to send the result back to
    private Shout mGlassFloor;

    //Constructor
    public DialogFragment_PlaylistAction() {
        mMode = SEARCH_MODE;
        mPlaylist_Purpose = Playlist_Purpose.UNDEFINED;
    }

    public static DialogFragment_PlaylistAction newInstance(String title) {
        DialogFragment_PlaylistAction frag = new DialogFragment_PlaylistAction();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    //Setter and Getter
    public void setPlaylist(Playlist iPlaylist) {
        mPlaylist = iPlaylist;
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
        return inflater.inflate(R.layout.dialog_playlist, container);
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
        mTV_Title = iView.findViewById(R.id.TV_PlaylistAction_Title);
        mTV_Playlist_Label = iView.findViewById(R.id.TV_PlaylistAction_Label);
        mTV_Playlist_Default = iView.findViewById(R.id.TV_PlaylistAction_DefaultPlaylist);
        mPB_Playlist_Create = iView.findViewById(R.id.PB_PlaylistAction_Create);
        mET_Name = iView.findViewById(R.id.ET_PlaylistAction_Name);

        switch (mMode) {
            case SEARCH_MODE:
                mTV_Title.setText("Search");
                mPB_Playlist_Create.setVisibility(View.GONE);
                showDefault();
                showET();
                showSP();
                showThreeRV(iView);
                break;
            case SELECT_MODE:
                mTV_Title.setText("Select");
                mPB_Playlist_Create.setVisibility(View.INVISIBLE);
                showDefault();
                hideET();
                hideSP();
                showThreeRV(iView);
                break;
            case EDIT_MODE:
                if (mPlaylist == null) {
                    throw new RuntimeException("Edit requires an existing playlist");
                }
                mTV_Title.setText("Edit");
                mET_Name.setText(mPlaylist.getName());
                mPB_Playlist_Create.setVisibility(View.VISIBLE);
                mPB_Playlist_Create.setText("Save");
                hideDefault();
                showET();
                showSP();
                hideThreeRV(iView);
                mSP_Purpose.setSelection(mCSA_Purpose.getPosition(mPlaylist.getPlaylist_Purpose().getCode()));
                mPB_Playlist_Create.setOnClickListener(v -> {
                    String tName = mET_Name.getText().toString();
                    updatePlaylist(tName, mPlaylist_Purpose);
                    dismiss();
                });
                break;
            case CREATE_MODE:
                mTV_Title.setText("Create Playlist");
                mPB_Playlist_Create.setVisibility(View.VISIBLE);
                mPB_Playlist_Create.setText("Save");
                hideDefault();
                showET();
                showSP();
                hideThreeRV(iView);
                mPB_Playlist_Create.setOnClickListener(v -> {
                    String tName = mET_Name.getText().toString();
                    createNewPlaylist(tName, mPlaylist_Purpose);
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
        mTV_Playlist_Default.setVisibility(View.GONE);
        mTV_Playlist_Label.setVisibility(View.GONE);
    }

    @SuppressLint("SetTextI18n")
    void showDefault() {
        mTV_Playlist_Default.setVisibility(View.VISIBLE);
        Playlist tPlaylist = Playlist.getDefaultPlaylist();
        if (tPlaylist == null) {
            mTV_Playlist_Default.setText("not defined");
            mTV_Playlist_Default.setTypeface(null, Typeface.ITALIC);
            mTV_Playlist_Default.setOnClickListener(null);
        } else {
            mTV_Playlist_Default.setText(tPlaylist.getName());
            mTV_Playlist_Default.setTypeface(null, Typeface.NORMAL);
            mTV_Playlist_Default.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // the default list has been clicked, so just confirm the choice
                    executeChoice(Playlist.getDefaultPlaylist());
                }
            });
        }
        mTV_Playlist_Label.setVisibility(View.VISIBLE);
        mTV_Playlist_Default.setHint("Name");
    }


    void hideET() {
        mET_Name.setVisibility(View.GONE);
    }

    void showET() {
        mET_Name.setVisibility(View.VISIBLE);
    }

    void hideSP() {
        mSP_Purpose = requireView().findViewById(R.id.SP_PlaylistAction_Purpose);
        mSP_Purpose.setVisibility(View.GONE);
    }

    void showSP() {
        SpinnerItemFactory tSpinnerItemFactory = new SpinnerItemFactory();
        ArrayList<CustomSpinnerItem> tAL_CustomSpinnerItem;
        mSP_Purpose = requireView().findViewById(R.id.SP_PlaylistAction_Purpose);
        mSP_Purpose.setVisibility(View.VISIBLE);
        tAL_CustomSpinnerItem = tSpinnerItemFactory.
                getSpinnerItems(SpinnerItemFactory.FIELD_PLAYLIST_PURPOSE, false);
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
                mPlaylist_Purpose = Playlist_Purpose.fromCode(tCustomSpinnerItem.mValue);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    void showThreeRV(View iView) {
        Playlist_Purpose tPlaylist_Purpose;
        SpinnerItemFactory tSpinnerItemFactory = new SpinnerItemFactory();
        CustomSpinnerItem tCustomSpinnerItem;
        RecyclerView.LayoutManager tLayoutManagerTheme;
        Application tApllication = this.requireActivity().getApplication();

        ////////////////////////////////////////////
        // setup the playlist column theme = purpose
        tPlaylist_Purpose = Playlist_Purpose.THEME;
        tCustomSpinnerItem = tSpinnerItemFactory.getSpinnerItem(
                SpinnerItemFactory.FIELD_PLAYLIST_PURPOSE, tPlaylist_Purpose.getCode());


        mPlaylistTheme_RecyclerView = iView.findViewById(R.id.RV_PlaylistAction_Theme);
        tLayoutManagerTheme = new LinearLayoutManager(this.getContext());
        mPlaylistTheme_RecyclerView.setLayoutManager(tLayoutManagerTheme);
        mPlaylistTheme_RecyclerView.setVisibility(View.VISIBLE);

        mPlaylistTheme_Adapter = new Playlist_Adapter(getContext(), null, this);
        mPlaylistTheme_Adapter.setLayoutMode(Playlist_Adapter.LAYOUT_MODE_COMPACT);
        mPlaylistTheme_RecyclerView.setAdapter(mPlaylistTheme_Adapter);

        mPlaylistTheme_ViewModel = ViewModelProvider.AndroidViewModelFactory.
                getInstance(tApllication).create(Playlist_ViewModel.class);
        mPlaylistTheme_ViewModel.setSearchPurpose(tCustomSpinnerItem);
        mPlaylistTheme_ViewModel.mMLD_AR.observe(getViewLifecycleOwner(), iAR_Object -> {
            if (iAR_Object != null) {
                ArrayList<Playlist> tAR = new ArrayList<>(0);
                for (Object lObject : iAR_Object) {
                    Playlist lPlaylist;
                    lPlaylist = (Playlist) lObject;
                    tAR.add(lPlaylist);
                }
                mPlaylistTheme_Adapter.setAR(tAR);
                mPlaylistTheme_Adapter.notifyDataSetChanged();
            }
        });
        mPlaylistTheme_ViewModel.forceSearch();

        ////////////////////////////////////////////
        // setup the playlist column theme = Event

        tPlaylist_Purpose = Playlist_Purpose.EVENT;

        tCustomSpinnerItem = tSpinnerItemFactory.getSpinnerItem(
                SpinnerItemFactory.FIELD_PLAYLIST_PURPOSE, tPlaylist_Purpose.getCode());

        mPlaylistEvent_RecyclerView = iView.findViewById(R.id.RV_PlaylistAction_Event);
        tLayoutManagerTheme = new LinearLayoutManager(this.getContext());
        mPlaylistEvent_RecyclerView.setLayoutManager(tLayoutManagerTheme);
        mPlaylistEvent_RecyclerView.setVisibility(View.VISIBLE);

        mPlaylistEvent_Adapter = new Playlist_Adapter(getContext(), null, this);
        mPlaylistEvent_Adapter.setLayoutMode(Playlist_Adapter.LAYOUT_MODE_COMPACT);
        mPlaylistEvent_RecyclerView.setAdapter(mPlaylistEvent_Adapter);

        mPlaylistEvent_ViewModel = ViewModelProvider.AndroidViewModelFactory.
                getInstance(tApllication).create(Playlist_ViewModel.class);
        mPlaylistEvent_ViewModel.setSearchPurpose(tCustomSpinnerItem);
        mPlaylistEvent_ViewModel.mMLD_AR.observe(getViewLifecycleOwner(), iAR_Object -> {
            if (iAR_Object != null) {
                ArrayList<Playlist> tAR = new ArrayList<>(0);
                for (Object lObject : iAR_Object) {
                    Playlist lPlaylist;
                    lPlaylist = (Playlist) lObject;
                    tAR.add(lPlaylist);
                }
                mPlaylistEvent_Adapter.setAR(tAR);
                mPlaylistEvent_Adapter.notifyDataSetChanged();
            }
        });
        mPlaylistEvent_ViewModel.forceSearch();

        ////////////////////////////////////////////
        // setup the playlist column theme = Event

        tPlaylist_Purpose = Playlist_Purpose.UNDEFINED;

        tCustomSpinnerItem = tSpinnerItemFactory.getSpinnerItem(
                SpinnerItemFactory.FIELD_PLAYLIST_PURPOSE, tPlaylist_Purpose.getCode());


        mPlaylistOther_RecyclerView = iView.findViewById(R.id.RV_PlaylistAction_Other);
        tLayoutManagerTheme = new LinearLayoutManager(this.getContext());
        mPlaylistOther_RecyclerView.setLayoutManager(tLayoutManagerTheme);
        mPlaylistOther_RecyclerView.setVisibility(View.VISIBLE);

        mPlaylistOther_Adapter = new Playlist_Adapter(getContext(), null, this);
        mPlaylistOther_Adapter.setLayoutMode(Playlist_Adapter.LAYOUT_MODE_COMPACT);
        mPlaylistOther_RecyclerView.setAdapter(mPlaylistOther_Adapter);

        mPlaylistOther_ViewModel = ViewModelProvider.AndroidViewModelFactory.
                getInstance(tApllication).create(Playlist_ViewModel.class);
        mPlaylistOther_ViewModel.setSearchPurpose(tCustomSpinnerItem);
        mPlaylistOther_ViewModel.mMLD_AR.observe(getViewLifecycleOwner(), iAR_Object -> {
            if (iAR_Object != null) {
                ArrayList<Playlist> tAR = new ArrayList<>(0);
                for (Object lObject : iAR_Object) {
                    Playlist lPlaylist;
                    lPlaylist = (Playlist) lObject;
                    tAR.add(lPlaylist);
                }
                mPlaylistOther_Adapter.setAR(tAR);
                mPlaylistOther_Adapter.notifyDataSetChanged();
            }
        });
        mPlaylistOther_ViewModel.forceSearch();
    }

    void hideThreeRV(View iView) {
        iView.findViewById(R.id.RV_PlaylistAction_Theme).setVisibility(View.GONE);
        iView.findViewById(R.id.RV_PlaylistAction_Event).setVisibility(View.GONE);
        iView.findViewById(R.id.RV_PlaylistAction_Other).setVisibility(View.GONE);
        iView.findViewById(R.id.TV_PlaylistAction_Theme).setVisibility(View.GONE);
        iView.findViewById(R.id.TV_PlaylistAction_Event).setVisibility(View.GONE);
        iView.findViewById(R.id.TV_PlaylistAction_Other).setVisibility(View.GONE);
    }


    void updatePlaylist(String iName, Playlist_Purpose iPlaylist_purpose) {
        if (mPlaylist != null) {
            mPlaylist.setPlaylist_Purpose(iPlaylist_purpose);
            mPlaylist.setName(iName);
            mPlaylist.save();
        }
    }

    void createNewPlaylist(String iName, Playlist_Purpose iPlaylist_purpose) {
        // check whether name already exist,
        SearchPattern tSearchPattern;
        tSearchPattern = new SearchPattern(Playlist.class);
        tSearchPattern.addSearch_Criteria(new SearchCriteria("NAME", iName));
        SearchCall tSearchCall =
                new SearchCall(Playlist.class, tSearchPattern, null);
        Playlist tPlaylist = tSearchCall.produceFirst();
        if (tPlaylist != null) {
            Logg.w(TAG, "Playlist exists already " + iName);
        }
        tPlaylist = new Playlist(iName, iPlaylist_purpose);
        tPlaylist.save();
        Playlist.setDefaultPlaylist(tPlaylist);
        if (mShoutingCaller != null) {
            Shout tShout = new Shout(this.getClass().getSimpleName());
            tShout.mLastObject = "Choice";
            tShout.mLastAction = "performed";
            mShoutingCaller.shoutUp(tShout);
        }
    }

    private void executeChoice(Playlist iPlaylist) {
        // do no wait for the finishing, so put in thread
        Runnable tRunnable = new Runnable() {
            @Override
            public void run() {
                Thread.currentThread().setName("exeChoice Playlist_Action");
                if (iPlaylist != Playlist.getDefaultPlaylist()) {
                    Playlist.setDefaultPlaylist(iPlaylist);
                }
                if (mShoutingCaller != null) {
                    Shout tShout = new Shout("DialogFragment_PlaylistAction");
                    tShout.mLastObject = "Choice";
                    tShout.mLastAction = "performed";
                    JSONObject tJson = new JSONObject();
                    try {
                        tJson.put("Id", iPlaylist.getId());
                        tJson.put("Name", iPlaylist.getName());
                        tJson.put("Purpose", iPlaylist.getPlaylist_Purpose().getCode());
                        tShout.mJsonString = tJson.toString();
                        tShout.storeObject(iPlaylist);
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
//		if (mGlassFloor.mActor.equals("Playlist_Action")) {
//			Logg.i(TAG, mGlassFloor.toString());
//		}
        if (mGlassFloor.mActor.equals("Playlist_Adapter")) {
            if (mGlassFloor.mLastObject.equals("Row")) {
                if (mGlassFloor.mLastAction.equals("selected")) {
                    if (mGlassFloor.mJsonString != null && !mGlassFloor.mJsonString.isEmpty()) {
                        Playlist tPlaylist;
                        int tPlaylist_Id = 0;
                        //noinspection IfStatementWithIdenticalBranches
                        if (mGlassFloor.carriesObject()) {
                            tPlaylist = mGlassFloor.returnObject();
                            if (tPlaylist != null) {
                                //Logg.i(TAG, "O" + tPlaylist.toString());
                                executeChoice(tPlaylist);
                            }
                        } else {
                            try {
                                JSONObject tParameter = new JSONObject(mGlassFloor.mJsonString);
                                tPlaylist_Id = tParameter.getInt("Id");
                            } catch(JSONException e) {
                                Logg.e(TAG, e.toString());
                            }
                            SearchPattern tSearchPattern;
                            tSearchPattern = new SearchPattern(Playlist.class);
                            tSearchPattern.addSearch_Criteria(new SearchCriteria("ID", "" + tPlaylist_Id));
                            SearchCall tSearchCall =
                                    new SearchCall(Playlist.class, tSearchPattern, null);
                            tPlaylist = tSearchCall.produceFirst();
                            if (tPlaylist != null) {
                                //Logg.i(TAG, "U" + tPlaylist.toString());
                                executeChoice(tPlaylist);
                            }
                        }
                        dismiss();
                    }
                }
            }
        }
    }


    public static void create(View iView, int iMode, Shouting iShouting) {
        DialogFragment_PlaylistAction t_DialogFragmentPlaylist =
                DialogFragment_PlaylistAction.newInstance("Some Title");
        t_DialogFragmentPlaylist.setMode(iMode);
        //  Logg.i(TAG, "setMode past with view");
        t_DialogFragmentPlaylist.mShoutingCaller = iShouting;

        AppCompatActivity tActivity = ((AppCompatActivity) iView.getContext());
        t_DialogFragmentPlaylist.show(tActivity.getSupportFragmentManager(), null);
    }

    @Override
    public void shoutUp(Shout tShoutToCeiling) {
        Logg.i(TAG, tShoutToCeiling.toString());
        mGlassFloor = tShoutToCeiling;
        process_shouting();
    }

}
