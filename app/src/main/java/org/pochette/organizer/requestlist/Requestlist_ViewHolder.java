package org.pochette.organizer.requestlist;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.pochette.data_library.music.MusicFile;
import org.pochette.data_library.requestlist.Requestlist;
import org.pochette.data_library.requestlist.Requestlist_Cache;
import org.pochette.organizer.R;
import org.pochette.organizer.gui_assist.CustomSpinnerItem;
import org.pochette.organizer.gui_assist.SpinnerItemFactory;
import org.pochette.organizer.music.MusicFile_Action;
import org.pochette.utils_lib.logg.Logg;
import org.pochette.utils_lib.shouting.Shout;
import org.pochette.utils_lib.shouting.Shouting;

import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

@SuppressWarnings("FieldCanBeLocal")
class Requestlist_ViewHolder extends RecyclerView.ViewHolder implements Shouting {

    private final String TAG = "FEHA (Requestlist_VH)";
    //Variables

    Requestlist mRequestlist;
    public View mLayout;
    private final ImageView mIV_Purpose;
    private final ImageView mIV_AddNew;
    private final ImageView mIV_Detail;
    private final ImageView mIV_Delete;
    private final ImageView mIV_Edit;
    private final TextView mTV_Purpose;
    private final TextView mTV_Name;
    private final TextView mTV_Count;

    private final RecyclerView mRV_Request;

    private boolean mFlagShowRequest;

    Shouting mShouting;
    Shout mGlassFloor;

    //Constructor

    public Requestlist_ViewHolder(View iView) {
        super(iView);
        mLayout = iView;
        mFlagShowRequest = false;

        mIV_Purpose = mLayout.findViewById(R.id.RowRequestlist_IV_Purpose);
        mTV_Purpose = mLayout.findViewById(R.id.RowRequestlist_TV_Purpose);
        mTV_Name = mLayout.findViewById(R.id.RowRequestlist_TV_Name);
        mTV_Count = mLayout.findViewById(R.id.RowRequestlist_TV_Count);
        mIV_AddNew = mLayout.findViewById(R.id.RowRequestlist_IV_AddNew);
        mIV_Detail = mLayout.findViewById(R.id.RowRequestlist_IV_Details);
        mIV_Edit = mLayout.findViewById(R.id.RowRequestlist_IV_Edit);
        mIV_Delete = mLayout.findViewById(R.id.RowRequestlist_IV_Delete);
        mRV_Request = mLayout.findViewById(R.id.RowRequestlist_RV_Playinstruction);
        // listeners
        if (mIV_AddNew != null) {
            mIV_AddNew.setOnClickListener(v -> {
                Logg.k(TAG, "TV_DeatilOnClick");
                addNew();
                refreshView();
            });
        }
        if (mIV_Detail != null) {
            mIV_Detail.setOnClickListener(v -> {
                Logg.k(TAG, "TV_DeatilOnClick");
                mFlagShowRequest = !mFlagShowRequest;
                refreshView();
            });
        }
        if (mIV_Edit != null) {
            mIV_Edit.setOnClickListener(v -> {
                Logg.k(TAG, "TV_Edit OnClick");
                editRequestlistHead();
            });
        }
        if (mIV_Delete != null) {
            mIV_Delete.setOnClickListener(v -> {
                Logg.k(TAG, "TV_Delete OnClick");
                mRequestlist.delete();
                Requestlist_Action.callExecute(mLayout,this,
                        Requestlist_Action.CLICK_TYPE_SHORT, Requestlist_Action.CLICK_ICON_DELETE,
                        mRequestlist,null, null,null);
                shoutDataChange();
            });
            mIV_Delete.setOnLongClickListener(v -> {
                Logg.k(TAG, "TV_Delete OnLongClick");
                Requestlist_Action.callExecute(mLayout,this,
                        Requestlist_Action.CLICK_TYPE_LONG, Requestlist_Action.CLICK_ICON_DELETE,
                        mRequestlist,null, null,null);
                shoutDataChange();
                return true;
            });
        }
    }

    //Setter and Getter
    void setRequestlist(Requestlist iRequestlist) {
        mRequestlist = iRequestlist;
        refreshView();
    }

    public void setShouting(Shouting shouting) {
        mShouting = shouting;
    }

    //Livecycle
    //Static Methods
    //Internal Organs

    // add new playinstruction
    private void addNew() {
        Logg.i(TAG, "addNew Playinstruction");
        MusicFile_Action.callExecute(mLayout, this,
                MusicFile_Action.SHORT_CLICK, MusicFile_Action.CLICK_ICON_ADD,
               null, null);
    }

    private void editRequestlistHead() {
        FragmentManager manager = ((AppCompatActivity) mLayout.getContext()).getSupportFragmentManager();
        DialogFragment_RequestlistAction tDialogFragment_RequestlistAction = new DialogFragment_RequestlistAction();
        tDialogFragment_RequestlistAction.setMode(DialogFragment_RequestlistAction.EDIT_MODE);
        tDialogFragment_RequestlistAction.setShoutingCaller(this);
        tDialogFragment_RequestlistAction.setRequestlist(mRequestlist);
        tDialogFragment_RequestlistAction.show(manager, "Edit Fragment");
    }

    private void refreshView() {
        // set the basic data
        //mRequestlist = Requestlist.getById(mRequestlist.getId());
        mRequestlist = Requestlist_Cache.getById(mRequestlist.getId());
        if (mRequestlist == null) {
            if (mTV_Name != null) {
                mTV_Name.setText("** deleted **");
            }
            return;
        }
        try {
            if (mIV_Purpose != null) {
                SpinnerItemFactory tSpinnerItemFactory = new SpinnerItemFactory();
                CustomSpinnerItem tCustomSpinnerItem = tSpinnerItemFactory.getSpinnerItem(
                        SpinnerItemFactory.FIELD_REQUESTLIST_PURPOSE,
                        mRequestlist.getRequestlist_Purpose().getCode());
                int tResourceId = tCustomSpinnerItem.mImageResource;
                mIV_Purpose.setImageResource(tResourceId);
            }
            if (mTV_Purpose != null) {
                mTV_Purpose.setText(mRequestlist.getRequestlist_Purpose().getText());
            }
            if (mTV_Name != null) {
                mTV_Name.setText(mRequestlist.getName());
            }
            if (mTV_Count != null) {
                mTV_Count.setText(String.format(Locale.ENGLISH, "%d", mRequestlist.getRequestCount()));
            }
            if (mIV_Detail != null) {
                if (mRequestlist.getRequestCount() <= 0) {
                    mIV_Detail.setVisibility(View.INVISIBLE);
                } else {
                    mIV_Detail.setVisibility(View.VISIBLE);
                    if (mFlagShowRequest) {
                        mIV_Detail.setImageResource(R.drawable.ic_caret_up);
                    } else {
                        mIV_Detail.setImageResource(R.drawable.ic_caret_down);
                    }
                }
            }
        } catch(Exception e) {
            Logg.e(TAG, e.toString());
        }
        // the Tracks
        if (mRV_Request != null && mFlagShowRequest) {
            mRV_Request.setVisibility(View.VISIBLE);
        //    mRequestlist.loadAL();
            Request_Adapter mRequest_Adapter = new Request_Adapter(
                    mRV_Request.getContext(), mRV_Request, null);
            mRequest_Adapter.setRequestlist(mRequestlist);
            RecyclerView.LayoutManager LM_ScddbCrib = new LinearLayoutManager(mLayout.getContext());
            mRV_Request.setLayoutManager(LM_ScddbCrib);
            mRV_Request.setAdapter(mRequest_Adapter);
            mRequest_Adapter.notifyDataSetChanged();
        } else {
            if (mRV_Request != null) {
                mRV_Request.setVisibility(View.GONE);
            }
        }
    }


    private void shoutDataChange() {
        if (mShouting != null) {
            Shout tShout = new Shout(Requestlist_ViewHolder.class.getSimpleName());
            tShout.mLastObject = "Data";
            tShout.mLastAction = "changed";
            mShouting.shoutUp(tShout);
        }

    }

    private void process_shouting() {
        if (mGlassFloor.mActor.equals("DialogFragment_EditRequestlist")) {
            if (mGlassFloor.mLastObject.equals("Change")) {
                if (mGlassFloor.mLastAction.equals("saved")) {
                    refreshView();
                    shoutDataChange();
                }
            }
        }
        if (mGlassFloor.mActor.equals("DialogFragment_RequestlistAction")) {
            if (mGlassFloor.mLastObject.equals("Dialog")) {
                if (mGlassFloor.mLastAction.equals("dismissed")) {
                    Logg.w(TAG, "refresh as dismissed");
                    refreshView();
                    shoutDataChange();
                }
            }
        }

        if ("DialogFragment_MusicFile".equals(mGlassFloor.mActor)) {
            if (mGlassFloor.mLastObject.equals("Choice") && mGlassFloor.mLastAction.equals("confirmed")) {
//                if (mShouting != null) {
//                    mShouting.shoutUp(mGlassFloor);
//                }
                MusicFile tMusicFile;
                try {
                    tMusicFile = (MusicFile) mGlassFloor.returnObject();
                } catch(Exception e) {
                    Logg.w(TAG, e.toString());
                    tMusicFile = null;
                }
                if (tMusicFile != null) {
                    Logg.w(TAG, "Add " + tMusicFile.toString());
                    mRequestlist.add(tMusicFile);
                    mRequestlist.save();
                    Requestlist_Cache.updateCache(mRequestlist);
                    refreshView();
                }

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
}
