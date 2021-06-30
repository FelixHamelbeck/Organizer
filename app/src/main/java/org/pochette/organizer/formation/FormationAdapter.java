package org.pochette.organizer.formation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.pochette.data_library.scddb_objects.FormationRoot;
import org.pochette.organizer.R;
import org.pochette.utils_lib.logg.Logg;
import org.pochette.utils_lib.shouting.Shout;
import org.pochette.utils_lib.shouting.Shouting;

import java.util.ArrayList;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Crib_Adapter is the Adapter for RecyclerView for Cribs
 */
@SuppressWarnings("unused")
public class FormationAdapter extends RecyclerView.Adapter<FormationAdapter.ViewHolder> {

    //Variables
    private static final String TAG = "FEHA (FormationAdapter)";

    private ArrayList<FormationRoot> mAL_FormationRoot;
    Shouting mShouting;


    //Constructor
    @SuppressWarnings("unused")
    public FormationAdapter(Context context, RecyclerView recyclerView, Shouting upstairs) {
        mShouting = upstairs;
    }

    // internal class
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View layout;
        TextView TV_Name;
        TextView TV_Count;
        FormationRoot mFormationRoot;
        Shouting hShouting;
    //    int _Id;

        ViewHolder(View v) {
            super(v);
            layout = v;
            TV_Name = v.findViewById(R.id.Formation_TV_Name);
            TV_Count = v.findViewById(R.id.Formation_TV_Count);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (hShouting != null) {

                        try {
                            Shout tShout = new Shout("FormationAdapter");
                            tShout.mLastObject = "Formation";
                            tShout.mLastAction = "selected";
                            JSONObject tJsonObject = new JSONObject();
                            tJsonObject.put("Key", mFormationRoot.mKey);
                            tShout.mJsonString = tJsonObject.toString();
                            hShouting.shoutUp(tShout);
                        } catch(JSONException e) {
                            Logg.w(TAG, e.toString());
                        }
                    }
                }
            });
        }
    }

    //Setter and Getter
    //Livecycle

    @NonNull
    public FormationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.row_formation, parent, false);
        return new ViewHolder(v);
    }

    /**
     * @param holder   the holder for the binding
     * @param position the linenumber
     */
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        if (mAL_FormationRoot == null) {
            return;
        }
        holder.mFormationRoot = mAL_FormationRoot.get(position);
        try {
            holder.TV_Name.setText(holder.mFormationRoot.mName);
            holder.TV_Count.setText(String.format(Locale.ENGLISH,
                    "%d", holder.mFormationRoot.mCountDances));
            holder.hShouting = mShouting; 
          //  holder._Id = tCribLine.mLineNUmber;
        } catch(Exception e) {
            Logg.e(TAG, e.toString());
        }
    }
    //Static Methods
    //Internal Organs

    void executeClick(FormationRoot iFormationRoot) {

    }

    //Interface
    public void setAL_FormationRoot(ArrayList<FormationRoot> iAL) {
        mAL_FormationRoot =iAL;
        notifyDataSetChanged();
    }

    /**
     * return the ItemCount, i.e. the number of lines
     */
    @Override
    public int getItemCount() {
        if (mAL_FormationRoot == null) {
            return 0;
        }
        return mAL_FormationRoot.size();
    }
}