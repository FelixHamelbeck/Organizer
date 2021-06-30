package org.pochette.organizer.pairing;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import org.pochette.data_library.music.MusicFile;
import org.pochette.data_library.scddb_objects.Recording;
import org.pochette.organizer.R;
import org.pochette.utils_lib.logg.Logg;

import java.util.ArrayList;
import java.util.Collections;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Adapterclass for ScddbRecording
 */
public class MusicfileRecording_Adapter extends RecyclerView.Adapter<MusicfileRecording_Adapter.ViewHolder> {
    //Variables
    @SuppressWarnings({"FieldCanBeLocal", "FieldMayBeFinal"})
    private static String TAG = "FEHA (MusicfileRecording_Adapter)";

    ArrayList<MusicFile> mAL_MusicFile;
    ArrayList<Recording> mAL_Recording;
    int mMusicDirectory_Id;
    int mScddbAlbun_Id;

    //Constructor
    @SuppressWarnings("unused")
    public MusicfileRecording_Adapter(Context context, RecyclerView recyclerView) {
        mAL_MusicFile = new ArrayList<>(0);
        mAL_Recording = new ArrayList<>(0);
    }


    // internal class

    void sortData() {
        if( mAL_Recording != null) {
            Collections.sort(mAL_Recording, (o1, o2) -> o1.mTrackNumber - o2.mTrackNumber);
        }
        if( mAL_MusicFile != null) {
            Collections.sort(mAL_MusicFile, (o1, o2) -> o1.mTrackNo - o2.mTrackNo);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View mLayout;
        TextView TV_MusicFile;
        TextView TV_Recording;

        ViewHolder(View v) {
            super(v);
            mLayout = v;
            TV_MusicFile = v.findViewById(R.id.RowMusicFileRecording_TV_MusicFile);
            TV_Recording = v.findViewById(R.id.RowMusicFileRecording_TV_Recording);
        }
    }


    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @NonNull
    @Override
    public MusicfileRecording_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup iParent, int iViewType) {
      //  LayoutInflater inflater = LayoutInflater.from(iParent.getContext());
        View v = LayoutInflater.from(iParent.getContext()).inflate(R.layout.row_musicfile_recording, iParent, false);
        return new ViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder iHolder, int iDoNotUsePosition) {
        int tPosition = iHolder.getLayoutPosition();
        if (mAL_Recording == null) {
            return;
        }
        if (mAL_MusicFile.size() > tPosition) {
            String tText = mAL_MusicFile.get(tPosition).mName;
            iHolder.TV_MusicFile.setText(tText);
        } else {
            iHolder.TV_MusicFile.setText("");
        }
        if (mAL_Recording.size() > tPosition) {
            String tText = mAL_Recording.get(tPosition).mName ;
            iHolder.TV_Recording.setText(tText);
        } else {
            iHolder.TV_Recording.setText("");
        }
    }
    //Static Methods
    //Internal Organs

    //Interface

//    public void reload() {
//        fillData();
//        refresh();
//    }

    public void refresh() {
        new Handler(Looper.getMainLooper()).post(this::notifyDataSetChanged);
    }

    @Override
    public int getItemCount() {
        int tResult;
        if (mAL_MusicFile == null || mAL_Recording == null) {
            tResult = 0;
        } else {
            tResult = Math.max(mAL_MusicFile.size(), mAL_Recording.size());
        }
      //  Logg.i(TAG, "itemcount" + tResult);
        return tResult;
    }

}