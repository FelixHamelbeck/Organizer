package org.pochette.organizer.dance;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import org.pochette.data_library.scddb_objects.Crib;
import org.pochette.data_library.scddb_objects.CribLine;
import org.pochette.organizer.R;
import org.pochette.utils_lib.logg.Logg;
import org.pochette.utils_lib.shouting.Shouting;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Crib_Adapter is the Adapter for RecyclerView for Cribs
 */
@SuppressWarnings("unused")
public class Crib_Adapter extends RecyclerView.Adapter<Crib_Adapter.ViewHolder> {

    //Variables
    private static final String TAG = "FEHA (Crib_Adapter)";

    public Crib mCrib;
    ArrayList<CribLine> mAR_CribLine;

    //Constructor
    public Crib_Adapter(Context context, RecyclerView recyclerView, Shouting upstairs) {
    }

    // internal class
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View layout;
        TextView TV_Line;
        TextView TV_Text;
        int _Id;
        ViewHolder(View v) {
            super(v);
            layout = v;
            TV_Line = v.findViewById(R.id.ScddbCrib_TV_Bar);
            TV_Text = v.findViewById(R.id.ScddbCrib_TV_Text);
        }
    }

    //Setter and Getter
    //Livecycle

    @NonNull
    public Crib_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.row_crib, parent, false);
        return new ViewHolder(v);
    }

    /**
     * @param holder   the holder for the binding
     * @param position the linenumber
     */
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        if (mAR_CribLine == null) {
            return;
        }
        CribLine tCribLine = mAR_CribLine.get(position);
        try {
            holder.TV_Line.setText(tCribLine.mBarColumn);
            holder.TV_Text.setText(tCribLine.mDescColumn);
            holder._Id = tCribLine.mLineNUmber;
        } catch (Exception e) {
            Logg.i(TAG, tCribLine.toString());
            Logg.e(TAG, e.toString());
        }
    }
    //Static Methods
    //Internal Organs
    //Interface

    /**
     * setCrib should be the only way to set the data
     *
     * @param tCrib is the filled crib class
     *                   The adapter does not connect to the database
     */
    public void setCrib(Crib tCrib) {
        mCrib = tCrib;
        if (mCrib == null) {
            mAR_CribLine = new ArrayList<>(0);
        } else {
            mAR_CribLine = mCrib.getAL_CribLine();
        }
        Logg.i(TAG, "" + mAR_CribLine.size());
        notifyDataSetChanged();
    }

	/**
	 * return the ItemCount, i.e. the number of lines
	 */
	@Override
    public int getItemCount() {
        if (mAR_CribLine == null) {
            return 0;
        }
        return mAR_CribLine.size();
    }
}