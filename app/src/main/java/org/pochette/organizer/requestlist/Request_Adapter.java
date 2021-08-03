package org.pochette.organizer.requestlist;

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
import org.pochette.data_library.pairing.Signature;
import org.pochette.data_library.requestlist.Request;
import org.pochette.data_library.requestlist.Requestlist;
import org.pochette.data_library.requestlist.Requestlist_Cache;
import org.pochette.organizer.R;
import org.pochette.organizer.app.MediaPlayerServiceSingleton;
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
public class Request_Adapter extends RecyclerView.Adapter<Request_Adapter.ViewHolder>
        implements Refreshable {

    //Variables
    private static final String TAG = "FEHA (PI_Adapter)";

    Context mContext;
    Request_Adapter mRequest_Adapter;

    public ArrayList<Request> mAR_Request;

    private Requestlist mRequestlist;

    private String mMode;
    // QUEUE is currently the only alternative--

    int mCurrentRequest = -1;
    int mNextRequest = -1;
    int mSelectedArPosition = -1;
    int mHighlightArPosition = -1;
    //int mCurrentArPosition = -1;

    Shouting mShouting;
    Shout mShoutToCeiling;

    //Constructor

    @SuppressWarnings("unused")
    public Request_Adapter(Context context, RecyclerView recyclerView, Shouting upstairs) {
        mRequest_Adapter = this;
        mContext = context;
        mShouting = upstairs;
        //mShoutToCeiling = new Shout(getClass().getSimpleName());
        mAR_Request = new ArrayList<>(0);
        mShoutToCeiling = new Shout("Request_Adapter");
        mMode = "NORM";
    }

    // internal class
    public class ViewHolder extends RecyclerView.ViewHolder {
        public Request hRequest;
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
            mTV_Artist = iView.findViewById(R.id.Request_TV_Artist);
            mTV_Signature = iView.findViewById(R.id.Request_TV_Signature);
            mTV_Shortname = iView.findViewById(R.id.Request_TV_Shortname);
            mTV_Duration = iView.findViewById(R.id.Request_TV_Duration);
            mIV_Play = iView.findViewById(R.id.Request_IV_Play);
            mIV_Up = iView.findViewById(R.id.Request_IV_Up);
            mIV_Delete = iView.findViewById(R.id.Request_IV_Delete);
            iView.setTag(this);
            if (mIV_Play != null) {
                mIV_Play.setOnClickListener(jView -> {
                    Logg.k(TAG, "IV_Play");
                    if (hRequest != null) {
                        MusicFile tMusicFile = hRequest.getMusicFile();
                        if (tMusicFile != null) {
                            MediaPlayerServiceSingleton.getInstance().getMediaPlayerService().play(tMusicFile);
                        }
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

    public Requestlist getRequestlist() {
        return mRequestlist;
    }

    public void setRequestlist(Requestlist tRequestlist) {
        if (tRequestlist != null) {
            mRequestlist = tRequestlist;
            mAR_Request = mRequestlist.getAR_Request();
        }
    }

    public Request getSelectedRequest() {
        return mAR_Request.get(mSelectedArPosition);
    }

    public void setSelectedRequest(Request tSelectedRequest) {
        for (int i = 0; i < mAR_Request.size(); i++) {
            if (tSelectedRequest.equals(mAR_Request.get(i))) {
                mSelectedArPosition = i;
                break;
            }
        }
    }

    @SuppressWarnings("unused")
    public Request getHighlightRequest() {
        return mAR_Request.get(mHighlightArPosition);

    }

    public void setHighlightRequest(Request tHighlightRequest) {
        for (int i = 0; i < mAR_Request.size(); i++) {
            if (tHighlightRequest.equals(mAR_Request.get(i))) {
                mHighlightArPosition = i;
                break;
            }
        }
    }


    public void setCurrentRequest(int tCurrentRequest) {
        mCurrentRequest = tCurrentRequest;
    }

    public int getSelectedArPosition() {
        return mSelectedArPosition;
    }

    public int getCurrentRequest() {
        return mCurrentRequest;
    }

    public int getNextRequest() {
        return mNextRequest;
    }


    public void setNextRequest(int tNextRequest) {
        this.mNextRequest = tNextRequest;
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
    public Request_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.row_request, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.hRequest = mAR_Request.get(holder.getAdapterPosition());
        int tPosition = holder.getAdapterPosition();
        Logg.d(TAG, holder.hRequest.toString());
        Request tRequest = holder.hRequest;
        if (tRequest == null) {
            return;
        }
        try {
            holder.mTV_Artist.setText(tRequest.getArtist());
            String tSignatureString = tRequest.getSignature();
            if (tSignatureString.equals(Signature.getEmpty())) {
                holder.mTV_Signature.setText("");
            } else {
                holder.mTV_Signature.setText(tSignatureString);
            }
            holder.mTV_Shortname.setText(tRequest.getName());
            int tDuration = tRequest.getDuration();
            SimpleDateFormat df;
            df = new SimpleDateFormat("mm:ss", Locale.ENGLISH);
            String tMmss = df.format(new Date(tDuration * 1000));
            holder.mTV_Duration.setText(tMmss);
            if (tPosition == 0) {
                holder.mIV_Up.setVisibility(View.INVISIBLE);
            } else {
                holder.mIV_Up.setVisibility(View.VISIBLE);
            }
        } catch(Exception e) {
            Logg.i(TAG, tRequest.toString());
            Logg.e(TAG, e.toString());
        }

        int tBackgroundColor = ContextCompat.getColor(mContext, R.color.bg_list_standard);
        int tTextColor = ContextCompat.getColor(mContext, R.color.txt_standard);
        Typeface tTypeface = Typeface.DEFAULT;
        if (mMode == null) {
            mMode = "NORM";
        }
        if (mMode.equals("QUEUE")) {
            if (position == mCurrentRequest) {
                tTextColor = ContextCompat.getColor(mContext, R.color.txt_selected);
                tBackgroundColor = ContextCompat.getColor(mContext, R.color.bg_list_selected);
                if (position == mSelectedArPosition) {
                    tTypeface = Typeface.DEFAULT_BOLD;
                }
            } else if (position == mNextRequest) {
                tTextColor = ContextCompat.getColor(mContext, R.color.txt_selected);
                tBackgroundColor = ContextCompat.getColor(mContext, R.color.bg_list_selected);
                if (position == mSelectedArPosition) {
                    tTypeface = Typeface.DEFAULT_BOLD;
                } else {
                    tBackgroundColor = ContextCompat.getColor(mContext, R.color.bg_list_preselected);
                }
            } else if (position < mCurrentRequest) {
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
            Logg.i(TAG, tRequest.toString());
        }

        if (tRequest.getMusicFile_id() == 0) {
            holder.mIV_Play.setVisibility(View.INVISIBLE);
        } else {
            holder.mIV_Play.setVisibility(View.VISIBLE);
        }

    }
    //</editor-fold>


    //Static Methods
    //Internal Organs

    void saveData() {
        mRequestlist.save();
        Requestlist_Cache.updateCache(mRequestlist);
    }

    //Interface

    /**
     * exchange
     *
     * @param tPositionFrom previous position
     * @param tPositionTo   target position
     */
    public void exchange(int tPositionFrom, int tPositionTo) {
        mRequestlist.swap(tPositionFrom, tPositionTo);
        mAR_Request = mRequestlist.getAR_Request();
        notifyDataSetChanged();
    }

    /**
     * remove the item at position
     *
     * @param tPosition position of item to be removed
     */
    public void remove(int tPosition) {
        mRequestlist.remove(tPosition);
        mAR_Request = mRequestlist.getAR_Request();
        notifyItemRemoved(tPosition);
        saveData();
    }

    public void refresh() {
        new Handler(Looper.getMainLooper()).post(this::notifyDataSetChanged);
    }

    @Override
    public int getItemCount() {
        if (mAR_Request == null) {
            return 0;
        }
        return mAR_Request.size();
    }
}