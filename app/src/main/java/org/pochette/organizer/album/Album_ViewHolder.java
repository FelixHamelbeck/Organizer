package org.pochette.organizer.album;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.pochette.data_library.pairing.Signature;
import org.pochette.data_library.scddb_objects.Album;
import org.pochette.organizer.R;
import org.pochette.utils_lib.logg.Logg;
import org.pochette.utils_lib.shouting.Shout;
import org.pochette.utils_lib.shouting.Shouting;

import java.util.Locale;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class Album_ViewHolder extends RecyclerView.ViewHolder implements Shouting {

    private final String TAG = "FEHA (Album_ViewHolder)";

    //Variables
    Album mAlbum;
    public View mLayout;
    Album_Adapter mAlbum_Adapter;
    TextView mTV_Artist;
    TextView mTV_Album;
    TextView mTV_Count;
    TextView mTV_Signature;
    ImageView mIV_Purpose;
    ImageView mIV_Pairing;

    String mSignatureString4Comparison;
    Signature mSignature4Comparison;

    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private Shout mGlassFloor;
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private Shouting mShouting;

    //Constructor
    public Album_ViewHolder(View iView) {
        super(iView);
        mLayout = iView;
        mTV_Artist = mLayout.findViewById(R.id.TV_RowAlbum_Artist);
        mTV_Album = mLayout.findViewById(R.id.TV_RowAlbum_Album);
        mIV_Purpose = mLayout.findViewById(R.id.IV_RowAlbum_Purpose);
        mIV_Pairing = mLayout.findViewById(R.id.IV_RowAlbum_Pairing);
        mTV_Count = mLayout.findViewById(R.id.TV_RowAlbum_Count);
        mTV_Signature = mLayout.findViewById(R.id.TV_RowAlbum_Signature);
        setListener();
    }


    //Setter and Getter

    void setAlbum_Adapter(Album_Adapter iAlbum_Adapter) {
        mAlbum_Adapter = iAlbum_Adapter;
    }

    void setAlbum(Album iAlbum) {
        mAlbum = iAlbum;
        refreshDisplay();
    }

    public void setShouting(Shouting shouting) {
        mShouting = shouting;
    }

    public void setSignatureString4Comparison(String iString) {
        mSignatureString4Comparison = iString;
        mSignature4Comparison = new Signature(iString);
    }

    //Livecycle
    //Static Methods
    //Internal Organs


    void refreshDisplay() {
        // set the basic data
        try {
            if (mTV_Artist != null) {
                mTV_Artist.setText(mAlbum.mArtistName.trim());
            }
            if (mTV_Album != null) {
                mTV_Album.setText(mAlbum.mName.trim());
            }
//            if (mTV_Name != null) {
//                mTV_Name.setText(mAlbum.mSignature);
//            }
            if (mTV_Count != null) {
                mTV_Count.setText(String.format(Locale.ENGLISH,
                        "%d", mAlbum.mCountRecording));
            }
        } catch(Exception e) {
            Logg.e(TAG, e.toString());
        }

        if (mTV_Signature != null) {
            String tText ;
            if (mSignature4Comparison != null) {
                Float tScore = mSignature4Comparison.compare(new Signature(mAlbum.mSignature));
                tText = String.format(Locale.ENGLISH, "%3d %% %s",((int) (tScore * 100.f)), mAlbum.getSignature());
                tText = String.format(Locale.ENGLISH, "%3d %%",((int) (tScore * 100.f)));
                mTV_Signature.setText(tText);
            } else {
                mTV_Signature.setVisibility(View.GONE);
                //tText = mAlbum.getSignature();
            }
        }


//        if (mIV_Purpose != null) {
//        }

        int tColor;
        //if (mLayout.isSelected()) {
        if (isSelected()) {
            tColor = ContextCompat.getColor(mLayout.getContext(), R.color.bg_list_selected);
        } else {
            tColor = ContextCompat.getColor(mLayout.getContext(), android.R.color.transparent);
        }
        try {
            View tParentView;
            if (mTV_Album != null) {
                tParentView = (View) mTV_Album.getParent();
                tParentView.setBackgroundColor(tColor);
            }
        } catch(Exception e) {
            Logg.e(TAG, e.toString());
        }
    }

    public void setListener() {
        if (mLayout != null) {
            Album_ViewHolder fViewHolder = this;
            mLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAlbum_Adapter.OnClickOnViewHolder(fViewHolder);
                }
            });
        }
    }


    private boolean isSelected() {
        if (mAlbum_Adapter == null) {
            return false;
        } else {
            Album tAlbum = mAlbum_Adapter.getSelectedAlbum();
            if (tAlbum == null) {
                return false;
            } else {
                return tAlbum.mId == mAlbum.mId;
            }
        }
    }

    private void process_shouting() {
//

    }


    //Interface
    @Override
    public void shoutUp(Shout tShoutToCeiling) {
        Logg.i(TAG, tShoutToCeiling.toString());
        mGlassFloor = tShoutToCeiling;
        process_shouting();
    }
}
