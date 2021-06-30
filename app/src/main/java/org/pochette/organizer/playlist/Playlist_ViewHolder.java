package org.pochette.organizer.playlist;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.pochette.data_library.music.MusicFile;
import org.pochette.data_library.playlist.Playlist;
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
class Playlist_ViewHolder extends RecyclerView.ViewHolder implements Shouting {

    private final String TAG = "FEHA (Playlist_ViewHolder)";
    //Variables

    Playlist mPlaylist;
    public View mLayout;
    private final ImageView mIV_Purpose;
    private final ImageView mIV_AddNew;
    private final ImageView mIV_Detail;
    private final ImageView mIV_Delete;
    private final ImageView mIV_Edit;
    private final TextView mTV_Purpose;
    private final TextView mTV_Name;
    private final TextView mTV_Count;

    private final RecyclerView mRV_Playinstruction;

    private boolean mFlagShowPlayinstruction;

    Shouting mShouting;
    Shout mGlassFloor;

    //Constructor

    public Playlist_ViewHolder(View iView) {
        super(iView);
        mLayout = iView;
        mFlagShowPlayinstruction = false;

        mIV_Purpose = mLayout.findViewById(R.id.RowPlaylist_IV_Purpose);
        mTV_Purpose = mLayout.findViewById(R.id.RowPlaylist_TV_Purpose);
        mTV_Name = mLayout.findViewById(R.id.RowPlaylist_TV_Name);
        mTV_Count = mLayout.findViewById(R.id.RowPlaylist_TV_Count);
        mIV_AddNew = mLayout.findViewById(R.id.RowPlaylist_IV_AddNew);
        mIV_Detail = mLayout.findViewById(R.id.RowPlaylist_IV_Details);
        mIV_Edit = mLayout.findViewById(R.id.RowPlaylist_IV_Edit);
        mIV_Delete = mLayout.findViewById(R.id.RowPlaylist_IV_Delete);
        mRV_Playinstruction = mLayout.findViewById(R.id.RowPlaylist_RV_Playinstruction);
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
                mFlagShowPlayinstruction = !mFlagShowPlayinstruction;
                refreshView();
            });
        }
        if (mIV_Edit != null) {
            mIV_Edit.setOnClickListener(v -> {
                Logg.k(TAG, "TV_Edit OnClick");
                editPlaylistHead();
            });
        }
        if (mIV_Delete != null) {
            mIV_Delete.setOnClickListener(v -> {
                Logg.k(TAG, "TV_Delete OnClick");
                Playlist_Action.callExecute(mLayout,this,
                        Playlist_Action.CLICK_TYPE_SHORT, Playlist_Action.CLICK_ICON_DELETE,
                        mPlaylist,null, null);
                shoutDataChange();
            });
            mIV_Delete.setOnLongClickListener(v -> {
                Logg.k(TAG, "TV_Delete OnLongClick");
                Playlist_Action.callExecute(mLayout,this,
                        Playlist_Action.CLICK_TYPE_LONG, Playlist_Action.CLICK_ICON_DELETE,
                        mPlaylist,null, null);
                shoutDataChange();
                return true;
            });
        }
    }

    //Setter and Getter
    void setPlaylist(Playlist iPlaylist) {
        mPlaylist = iPlaylist;
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

    private void editPlaylistHead() {
        FragmentManager manager = ((AppCompatActivity) mLayout.getContext()).getSupportFragmentManager();
        DialogFragment_PlaylistAction tDialogFragment_PlaylistAction = new DialogFragment_PlaylistAction();
        tDialogFragment_PlaylistAction.setMode(DialogFragment_PlaylistAction.EDIT_MODE);
        tDialogFragment_PlaylistAction.setShoutingCaller(this);
        tDialogFragment_PlaylistAction.setPlaylist(mPlaylist);
        tDialogFragment_PlaylistAction.show(manager, "Edit Fragment");
    }

    private void refreshView() {
        // set the basic data
        mPlaylist = Playlist.getById(mPlaylist.getId());
        try {
            if (mIV_Purpose != null) {
                SpinnerItemFactory tSpinnerItemFactory = new SpinnerItemFactory();
                CustomSpinnerItem tCustomSpinnerItem = tSpinnerItemFactory.getSpinnerItem(
                        SpinnerItemFactory.FIELD_PLAYLIST_PURPOSE,
                        mPlaylist.getPlaylist_Purpose().getCode());
                int tResourceId = tCustomSpinnerItem.mImageResource;
                mIV_Purpose.setImageResource(tResourceId);
            }
            if (mTV_Purpose != null) {
                mTV_Purpose.setText(mPlaylist.getPlaylist_Purpose().getText());
            }
            if (mTV_Name != null) {
                mTV_Name.setText(mPlaylist.getName());
            }
            if (mTV_Count != null) {
                mTV_Count.setText(String.format(Locale.ENGLISH, "%d", mPlaylist.getCount()));
            }
            if (mIV_Detail != null) {
                if (mPlaylist.getCount() <= 0) {
                    mIV_Detail.setVisibility(View.INVISIBLE);
                } else {
                    mIV_Detail.setVisibility(View.VISIBLE);
                    if (mFlagShowPlayinstruction) {
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
        if (mRV_Playinstruction != null && mFlagShowPlayinstruction) {
            mRV_Playinstruction.setVisibility(View.VISIBLE);
            mPlaylist.loadAL();
            Playinstruction_Adapter mPlayinstruction_Adapter = new Playinstruction_Adapter(
                    mRV_Playinstruction.getContext(), mRV_Playinstruction, null);
            mPlayinstruction_Adapter.setPlaylist(mPlaylist);
            RecyclerView.LayoutManager LM_ScddbCrib = new LinearLayoutManager(mLayout.getContext());
            mRV_Playinstruction.setLayoutManager(LM_ScddbCrib);
            mRV_Playinstruction.setAdapter(mPlayinstruction_Adapter);
            mPlayinstruction_Adapter.notifyDataSetChanged();
        } else {
            if (mRV_Playinstruction != null) {
                mRV_Playinstruction.setVisibility(View.GONE);
            }
        }
    }


    private void shoutDataChange() {
        if (mShouting != null) {
            Shout tShout = new Shout(Playlist_ViewHolder.class.getSimpleName());
            tShout.mLastObject = "Data";
            tShout.mLastAction = "changed";
            mShouting.shoutUp(tShout);
        }

    }

    private void process_shouting() {
        if (mGlassFloor.mActor.equals("DialogFragment_EditPlaylist")) {
            if (mGlassFloor.mLastObject.equals("Change")) {
                if (mGlassFloor.mLastAction.equals("saved")) {
                    refreshView();
                    shoutDataChange();
                }
            }
        }
        if (mGlassFloor.mActor.equals("DialogFragment_PlaylistAction")) {
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
                    mPlaylist.add(tMusicFile);
                    mPlaylist.save();
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
