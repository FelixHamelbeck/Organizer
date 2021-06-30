package org.pochette.organizer.playlist;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.pochette.data_library.database_management.Refreshable;
import org.pochette.data_library.music.MusicFile;
import org.pochette.data_library.playlist.Playinstruction;
import org.pochette.data_library.playlist.Playlist;
import org.pochette.organizer.app.MediaPlayerServiceSingleton;
import org.pochette.organizer.R;
import org.pochette.utils_lib.logg.Logg;
import org.pochette.utils_lib.shouting.Shout;
import org.pochette.utils_lib.shouting.Shouting;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;


@SuppressWarnings("unused")
public class Playinstruction_Adapter extends RecyclerView.Adapter<Playinstruction_Adapter.ViewHolder>
        implements Refreshable {

    //Variables
    private static final String TAG = "FEHA (PI_Adapter)";

    Context mContext;
    Playinstruction_Adapter mPlayinstruction_Adapter;

    public ArrayList<Playinstruction> mAR_Playinstruction;

    private Playlist mPlaylist;

    private String mMode;
    // QUEUE is currently the only alternative--

    int mCurrentPlayinstruction = -1;
    int mNextPlayinstruction = -1;
    int mSelectedArPosition = -1;
    int mHighlightArPosition = -1;
    //int mCurrentArPosition = -1;

    Shouting mShouting;
    Shout mShoutToCeiling;

    //Constructor

    @SuppressWarnings("unused")
    public Playinstruction_Adapter(Context context, RecyclerView recyclerView, Shouting upstairs) {
        mPlayinstruction_Adapter = this;
        mContext = context;
        mShouting = upstairs;
        //mShoutToCeiling = new Shout(getClass().getSimpleName());
        mAR_Playinstruction = new ArrayList<>(0);
        mShoutToCeiling = new Shout("Playinstruction_Adapter");
        mMode = "NORM";
    }

    // internal class
    public class ViewHolder extends RecyclerView.ViewHolder {
        public Playinstruction hPlayinstruction;
        public View layout;

        TextView mTV_Artist;
        TextView mTV_Signature;
        TextView mTV_Shortname;
        TextView mTV_Duration;
        ImageView mIV_Play;
        ImageView mIV_Up;
        ImageView mIV_Delete;
        int _Id;

        ViewHolder(View iView) {
            super(iView);
            layout = iView;
            mTV_Artist = iView.findViewById(R.id.Playinstruction_TV_Artist);
            mTV_Signature = iView.findViewById(R.id.Playinstruction_TV_Signature);
            mTV_Shortname = iView.findViewById(R.id.Playinstruction_TV_Shortname);
            mTV_Duration = iView.findViewById(R.id.Playinstruction_TV_Duration);
            mIV_Play = iView.findViewById(R.id.Playinstruction_IV_Play);
            mIV_Up = iView.findViewById(R.id.Playinstruction_IV_Up);
            mIV_Delete = iView.findViewById(R.id.Playinstruction_IV_Delete);
            iView.setTag(this);
            if (mIV_Play != null) {
                mIV_Play.setOnClickListener(jView -> {
                    Logg.k(TAG, "IV_Play");
                    if (hPlayinstruction != null) {
                        MusicFile tMusicFile = hPlayinstruction.getMusicFile();
                        MediaPlayerServiceSingleton.getInstance().getMediaPlayerService().play(tMusicFile);
                    }
                });
            }
            if (mIV_Up != null) {
                mIV_Up.setOnClickListener(jView -> {
                    Logg.k(TAG, "IV_Up");
                    int tPosition = getAdapterPosition();
                    exchange(tPosition - 1, tPosition);
                    refresh();
                });
            }
            if (mIV_Delete != null) {
                mIV_Delete.setOnClickListener(jView -> {
                    Logg.k(TAG, "IV_Delete");
                    remove(this.getAdapterPosition());
                    refresh();
                });
            }
        }
    }
    //<editor-fold desc="Setter and Getter">

    public Playlist getPlaylist() {
        return mPlaylist;
    }

    public void setPlaylist(Playlist tPlaylist) {
        if (tPlaylist != null) {
            mPlaylist = tPlaylist;
            mAR_Playinstruction = mPlaylist.getAR_Playinstruction();
        }
    }

    public Playinstruction getSelectedPlayinstruction() {
        return mAR_Playinstruction.get(mSelectedArPosition);
        //return mSelectedPlayinstruction;
    }

    public void setSelectedPlayinstruction(Playinstruction tSelectedPlayinstruction) {
        for (int i = 0; i < mAR_Playinstruction.size(); i++) {
            if (tSelectedPlayinstruction.getId() == mAR_Playinstruction.get(i).getId()) {
                mSelectedArPosition = i;
                break;
            }
        }
    }

    @SuppressWarnings("unused")
    public Playinstruction getHighlightPlayinstruction() {
        return mAR_Playinstruction.get(mHighlightArPosition);

    }

    public void setHighlightPlayinstruction(Playinstruction tHighlightPlayinstruction) {
        for (int i = 0; i < mAR_Playinstruction.size(); i++) {
            if (tHighlightPlayinstruction.getId() == mAR_Playinstruction.get(i).getId()) {
                mHighlightArPosition = i;
                break;
            }
        }
    }


    public void setCurrentPlayinstruction(int tCurrentPlayinstruction) {
        mCurrentPlayinstruction = tCurrentPlayinstruction;
    }

    public int getSelectedArPosition() {
        return mSelectedArPosition;
    }

    public int getCurrentPlayinstruction() {
        return mCurrentPlayinstruction;
    }

    public int getNextPlayinstruction() {
        return mNextPlayinstruction;
    }


    public void setNextPlayinstruction(int tNextPlayinstruction) {
        this.mNextPlayinstruction = tNextPlayinstruction;
    }

    public void setHighlightArPosition(int mHighlightArPosition) {
        this.mHighlightArPosition = mHighlightArPosition;
    }

    public void setMode(String tMode) {
        mMode = tMode;
    }
    //</editor-fold>

    //<editor-fold desc="Livecycle">
    @NonNull
    public Playinstruction_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.row_playinstruction, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.hPlayinstruction = mAR_Playinstruction.get(holder.getAdapterPosition());
        int tPosition = holder.getAdapterPosition();
        Logg.d(TAG, holder.hPlayinstruction.toString());
        Playinstruction tPlayinstruction = holder.hPlayinstruction;
        if (tPlayinstruction == null) {
            return;
        }
        try {
            holder.mTV_Artist.setText(tPlayinstruction.getMusicFile().mT2);
            holder.mTV_Signature.setText(tPlayinstruction.getMusicFile().mSignature);
            holder.mTV_Shortname.setText(tPlayinstruction.getMusicFile().mName);
            int tDuration = tPlayinstruction.getMusicFile().mDuration;
            SimpleDateFormat df;
            df = new SimpleDateFormat("mm:ss", Locale.ENGLISH);
            String tMmss = df.format(new Date(tDuration * 1000));
            holder.mTV_Duration.setText(tMmss);
            holder._Id = tPlayinstruction.getId();
            if (tPosition == 0) {
                holder.mIV_Up.setVisibility(View.INVISIBLE);
            }
        } catch(Exception e) {
            Logg.i(TAG, tPlayinstruction.toString());
            Logg.e(TAG, e.toString());
        }

        int tBackgroundColor = ContextCompat.getColor(mContext, R.color.bg_list_standard);
        int tTextColor = ContextCompat.getColor(mContext, R.color.txt_standard);
        Typeface tTypeface = Typeface.DEFAULT;
        if (mMode == null) {
            mMode = "NORM";
        }
        if (mMode.equals("QUEUE")) {
            if (position == mCurrentPlayinstruction) {
                tTextColor = ContextCompat.getColor(mContext, R.color.txt_selected);
                tBackgroundColor = ContextCompat.getColor(mContext, R.color.bg_list_selected);
                if (position == mSelectedArPosition) {
                    tTypeface = Typeface.DEFAULT_BOLD;
                }
            } else if (position == mNextPlayinstruction) {
                tTextColor = ContextCompat.getColor(mContext, R.color.txt_selected);
                tBackgroundColor = ContextCompat.getColor(mContext, R.color.bg_list_selected);
                if (position == mSelectedArPosition) {

                    tTypeface = Typeface.DEFAULT_BOLD;
                } else {
                    tBackgroundColor = ContextCompat.getColor(mContext, R.color.bg_list_preselected);
                }
            } else if (position < mCurrentPlayinstruction) {
                tTextColor = ContextCompat.getColor(mContext, R.color.txt_inactive);
            }
        } else {
            tBackgroundColor = ContextCompat.getColor(mContext, R.color.bg_list_standard);
            if (position == mSelectedArPosition) {
                tBackgroundColor = ContextCompat.getColor(mContext, R.color.bg_list_selected);
            } else if (position == mHighlightArPosition) {
                tBackgroundColor = ContextCompat.getColor(mContext, R.color.bg_list_highlight);
            }
        }
        try {
            holder.layout.setBackgroundColor(tBackgroundColor);
            holder.mTV_Artist.setTextColor(tTextColor);
            holder.mTV_Signature.setTextColor(tTextColor);
            holder.mTV_Shortname.setTextColor(tTextColor);
            holder.mTV_Duration.setTextColor(tTextColor);
            holder.mTV_Artist.setTypeface(tTypeface);
            holder.mTV_Signature.setTypeface(tTypeface);
            holder.mTV_Shortname.setTypeface(tTypeface);
            holder.mTV_Duration.setTypeface(tTypeface);
        } catch(Exception e) {
            Logg.e(TAG, e.toString());
            Logg.i(TAG, tPlayinstruction.toString());
        }
    }
    //</editor-fold>


    //Static Methods
    //Internal Organs

    void saveData() {
		mPlaylist.save();
    }

    //Interface

    /**
     * exchange
     *
     * @param tPositionFrom previous position
     * @param tPositionTo   target position
     */
    public void exchange(int tPositionFrom, int tPositionTo) {
        mPlaylist.swap(tPositionFrom, tPositionTo);
        mAR_Playinstruction = mPlaylist.getAR_Playinstruction();
        notifyDataSetChanged();
    }

    /**
     * remove the item at position
     *
     * @param tPosition position of item to be removed
     */
    public void remove(int tPosition) {
        mPlaylist.remove(tPosition);
        mAR_Playinstruction = mPlaylist.getAR_Playinstruction();
        notifyItemRemoved(tPosition);
        saveData();
    }

    public void refresh() {
        new Handler(Looper.getMainLooper()).post(this::notifyDataSetChanged);
    }

    @Override
    public int getItemCount() {
        if (mAR_Playinstruction == null) {
            return 0;
        }
        return mAR_Playinstruction.size();
    }
}