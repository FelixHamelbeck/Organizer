package org.pochette.organizer.music;

import android.app.Application;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.pochette.data_library.music.MusicFile;
import org.pochette.data_library.music.MusicPreference;
import org.pochette.data_library.music.MusicPreferenceFavourite;
import org.pochette.data_library.scddb_objects.Dance;
import org.pochette.organizer.R;
import org.pochette.utils_lib.logg.Logg;
import org.pochette.utils_lib.shouting.Shout;
import org.pochette.utils_lib.shouting.Shouting;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * DialogFragment, where the music preference to a dance can be defined
 */

public class DialogFragment_MusicPreference extends DialogFragment implements Shouting {

    private final String TAG = "FEHA (DF_MusicPreference)";
    //Variables

    private Dance mDance;

    private MusicFile_ViewModel mModel;
    private FragmentManager mFragmentManager = null;

    private TextView mTV_Title;
    private TextView mTV_Dance;
    private ImageView mIV_Pause;
    private ImageView mIV_Exit;
    @SuppressWarnings("unused")
    private FrameLayout mFL_Placeholder;
    private RecyclerView mRV;

    private MusicFile_Adapter mMusicFile_Adapter;

    String mTitle;
    boolean mIsPlaying;
    boolean mAnyChange;

   // MusicPreference_Fragment mMusicPreference_Fragment;
    Shout mGlassFloor;
    Shouting mShouting;


    //Constructor
    public DialogFragment_MusicPreference() {
        mTitle = "";
        mAnyChange = false;
    }

    public static DialogFragment_MusicPreference newInstance(String iText) {
        DialogFragment_MusicPreference tDialogFragment_MusicPreference;
        tDialogFragment_MusicPreference = new DialogFragment_MusicPreference();
        tDialogFragment_MusicPreference.mTitle = iText;
        return tDialogFragment_MusicPreference;
    }

    //Setter and Getter
    public void setShouting(Shouting shouting) {
        mShouting = shouting;
    }

    public void setDance(Dance iDance) {
        if (iDance == null) {
            throw new RuntimeException("iDance cannot be null");
        }
        mDance = iDance;
        updateViews();
    }

    public void setTitle(String iTitle) {
        mTitle = iTitle;
    }

    public void setFragmentManager(FragmentManager iFragmentManager) {
        mFragmentManager = iFragmentManager;
        if (mMusicFile_Adapter != null) {
            mMusicFile_Adapter.setFragmentManager(iFragmentManager);
        }
    }

    //Livecycle
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //noinspection UnnecessaryLocalVariable
        View tView = inflater.inflate(R.layout.dialog_musicpreferences, container);
        return tView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTV_Title = view.findViewById(R.id.DialogMusicPreference_TV_Title);
        mTV_Dance = view.findViewById(R.id.DialogMusicPreference_TV_Dance);
        mIV_Pause = view.findViewById(R.id.DialogMusicPreference_IV_Pause);
        mIV_Exit = view.findViewById(R.id.DialogMusicPreference_IV_Exit);

        mRV = requireView().findViewById(R.id.DialogMusicPreference_RV);

        updateViews();

        updateRV();

    }

    @Override
    public void onResume() {
        super.onResume();
        if (mIV_Exit != null) {
            mIV_Exit.setOnClickListener(iView -> {
                Logg.k(TAG, "mIV_ExitCancel");
                confirm();
            });
        }
    }

    @Override
    public void dismiss() {
        if (mAnyChange) {
            shoutChange();
        }
        super.dismiss();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        if (mAnyChange) {
            shoutChange();
        }
        super.onDismiss(dialog);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

//Static Methods

    //Internal Organs

    private void createModel() {
        if (mModel != null) {
            return;
        }
        Application tApllication;
        tApllication = this.requireActivity().getApplication();
        mModel = ViewModelProvider.AndroidViewModelFactory.
                getInstance(tApllication).create(MusicFile_ViewModel.class);
        mModel.setSearchDance(mDance);
        mModel.mMLD_AR.observe(getViewLifecycleOwner(), iAR_Object -> {
            if (iAR_Object != null) {
                try {
                    ArrayList<MusicFile> tAR_MusicFile = new ArrayList<>(0);
                    for (Object lObject : iAR_Object) {
                        MusicFile lMusicFile;
                        lMusicFile = (MusicFile) lObject;
                        tAR_MusicFile.add(lMusicFile);
                    }
                    mMusicFile_Adapter.setAR(tAR_MusicFile);
                    mMusicFile_Adapter.notifyDataSetChanged();
                } catch(Exception e) {
                    Logg.w(TAG, e.toString());
                }
            }
        });
        mModel.forceSearch();
    }

    void updateRV() {
        if (mRV == null) {
            return;
        }
        createModel();
        if (mMusicFile_Adapter == null) {
            mMusicFile_Adapter = new MusicFile_Adapter(getContext(), mRV, this);
            mMusicFile_Adapter.setDanceForPreference(mDance);
            mMusicFile_Adapter.setMode(MusicFile_Adapter.MODE_PREFERENCE);
            mMusicFile_Adapter.setFragmentManager(mFragmentManager);
        }
        RecyclerView.LayoutManager LM_MusicFile = new LinearLayoutManager(getContext());
        mRV.setLayoutManager(LM_MusicFile);
        mRV.setAdapter(mMusicFile_Adapter);
    }


    void updateViews() {
        if (mTV_Title != null) {
            mTV_Title.setText(mTitle);
        }
        if (mTV_Dance != null) {
            mTV_Dance.setText(mDance.toString());
        }
        if (mIV_Pause != null) {
            if (mIsPlaying) {
                mIV_Pause.setVisibility(View.VISIBLE);
            } else {
                mIV_Pause.setVisibility(View.INVISIBLE);
            }
        }
    }
//
//    void checkIsPlaying(boolean iIsPlaying) {
//        if (mIsPlaying != iIsPlaying) {
//            mIsPlaying = iIsPlaying;
////            RecyclerView tRV = mMusicPreference_Fragment.mMusicPreference_Adapter.mRecyclerView;
////            for (int i = 0; i < tRV.getChildCount(); i++) {
////                MusicPreference_ViewHolder lMusicPreference_ViewHolder =
////                        (MusicPreference_ViewHolder) tRV.findViewHolderForLayoutPosition(i);
////                if (lMusicPreference_ViewHolder != null) {
////                    lMusicPreference_ViewHolder.requestRefresh();
////                }
////            }
//        }
//    }

    void shoutChange() {
        if (mShouting != null) {
            Shout tShout = new Shout(DialogFragment_MusicPreference.class.getSimpleName());
            tShout.mLastAction = "changed";
            tShout.mLastObject = "Data";
            mShouting.shoutUp(tShout);
        }
    }


    void process_shouting() {


        if (mGlassFloor.mActor.equals("MusicPreference_ViewHolder") &&
                mGlassFloor.mLastAction.equals("saved") &&
                mGlassFloor.mLastObject.equals("Preference")) {
            mAnyChange = true;
        }

        if (mGlassFloor.mActor.equals("Dance_ViewHolder")) {
            if (mGlassFloor.mLastObject.equals("Id")) {
                //noinspection StatementWithEmptyBody
                if (mGlassFloor.mLastAction.equals("selected")) {
//                    MusicFile tMusicFile;
//                    tMusicFile = mMusicFile_Adapter.getSelectedMusicFile();
                    //Logg.i(TAG, "do not care about "+ tMusicFile.toString());
                }
            }
//            try {
//                int tMusicFileId;
//                String tJsonString = mGlassFloor.mJsonString;
//                JSONObject tJsonObject = new JSONObject(tJsonString);
//                tMusicFileId = tJsonObject.getInt("MusicFileId");
//                MusicFile tMusicFile = MusicFile.getById(tMusicFileId);
//                MusicPreference tMusicPreference = new MusicPreference(0, tMusicFile, mDance, MusicPreferenceFavourite.ANYG, false);
//                tMusicPreference.save();
//                mMusicPreference_Fragment.requestRefresh();
//
//            } catch (JSONException e) {
//                Logg.w(TAG, e.toString());
//            }
        }
        new Handler(Looper.getMainLooper()).post(this::updateViews);
    }


    public void confirm() {
        if (mMusicFile_Adapter != null) {
            MusicFile tMusicFile = mMusicFile_Adapter.getSelectedMusicFile();
            if (tMusicFile != null) {
                MusicPreference tMusicPreference = new MusicPreference(0, tMusicFile, mDance, MusicPreferenceFavourite.OKAY, true);
                tMusicPreference.save();
            }
        }
        // all the edits are done and saved in the viewholder, only dismiss needed
        super.dismiss();
    }


    //Interface


    /**
     * Receive the shout
     *
     * @param tShoutToCeiling the shout
     */
    @Override
    public void shoutUp(Shout tShoutToCeiling) {
        Logg.i(TAG, tShoutToCeiling.toString());
        mGlassFloor = tShoutToCeiling;
        process_shouting();
    }
}
