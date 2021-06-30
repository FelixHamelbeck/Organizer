package org.pochette.organizer.gui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;
import org.pochette.organizer.R;
import org.pochette.utils_lib.logg.Logg;
import org.pochette.utils_lib.shouting.Shout;
import org.pochette.utils_lib.shouting.Shouting;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;

import static java.lang.Thread.interrupted;
import static java.lang.Thread.sleep;

@SuppressWarnings("unused")
public class TopBar_Fragment extends Fragment
        implements Shouting, LifecycleOwner, PopupMenu.OnMenuItemClickListener {

    //Variables
    private static final String TAG = "FEHA (TopBar_Fragment)";

    View mView;
    ImageView mIV_Dance;
    ImageView mIV_MusicFile;
    ImageView mIV_Playlist;
    ImageView mIV_Player;
    ImageView mIV_Menu;
    TextView mTV_InfoTop;
    TextView mTV_InfoBottom;

    Shouting mShouting;
    Shout mShoutToCeiling;
    private Shout mGlassFloor;

    // information to be displayed
    float mDisplayPosition = 0.f;
    float mDisplayDuration = 0.f;
    String mStringDance;
    String mStringMusician;
    String mStringAlbum;
    String mStringTitle;
    int mTopTextLoop = 0;

    ArrayList<String> mAL_InfoText = new ArrayList<>(0);
    Thread mThread;



    @SuppressWarnings("unused")
//Constructor

//Setter and Getter
    public void setShouting(Shouting mShouting) {
        this.mShouting = mShouting;
    }

    //Livecycle
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mShoutToCeiling = new Shout(this.getClass().getSimpleName());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_topbar, container, false);
        Fragment tParentFragment = getParentFragment();
        if (tParentFragment != null) {
            try {
                mShouting = (Shouting) tParentFragment;
            } catch(ClassCastException e) {
                throw new ClassCastException(tParentFragment.toString()
                        + " must implement Shouting");
            }
        }
        return mView;
    }

    public void setupReceiver(Activity iActivity) {
        TopBar_BroadCastReceiver tTopBar_BroadCastReceiver;
        //tTopBar_BroadCastReceiver = new TopBar_BroadCastReceiver(this, this);
        tTopBar_BroadCastReceiver = new TopBar_BroadCastReceiver(this);
        tTopBar_BroadCastReceiver.mShouting = this;
        IntentFilter filter = new IntentFilter();
        filter.addAction("org.pochette.musicplayer.mediaplayer.MediaPlayerService.NEW_POSITION");
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        iActivity.registerReceiver(tTopBar_BroadCastReceiver, filter);
    }

    @Override
    public void onViewCreated(@Nullable View view, @Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            Logg.i(TAG, savedInstanceState.toString());
        }
        mTV_InfoTop = requireView().findViewById(R.id.TV_TopBar_InfoTop);
        mTV_InfoBottom = requireView().findViewById(R.id.TV_TopBar_InfoBottom);

        mIV_Dance = requireView().findViewById(R.id.IV_TopBar_Dance);
        mIV_MusicFile = requireView().findViewById(R.id.IV_TopBar_MusicFile);
        mIV_Playlist = requireView().findViewById(R.id.IV_TopBar_Playlist);
        mIV_Player = requireView().findViewById(R.id.IV_TopBar_Player);
        mIV_Menu = requireView().findViewById(R.id.IV_TopBar_MenuIcon);

        mIV_Dance.setOnClickListener(iView -> {
            Logg.k(TAG, "IV_Dance OnClick");
            shoutIconClick("Dance");
        });
        mIV_MusicFile.setOnClickListener(iView -> {
            Logg.k(TAG, "IV_Musicfile OnClick");
            shoutIconClick("MusicFile");
        });
        mIV_Playlist.setOnClickListener(iView -> {
            Logg.k(TAG, "IV_Playlist OnClick");
            shoutIconClick("Playlist");
        });
        mIV_Player.setOnClickListener(iView -> {
            Logg.k(TAG, "IV_Player OnClick");
            shoutIconClick("MediaPlayer");
        });

        mIV_Menu.setOnClickListener(iView -> {
            Logg.k(TAG, "IV_Menu OnClick");
            PopupMenu tPopupMenu;//View will be an anchor for PopupMenu
            tPopupMenu = new PopupMenu(mView.getContext(), mIV_Menu);
            tPopupMenu.inflate(R.menu.action_bar_menu);
            tPopupMenu.setOnMenuItemClickListener(this);
            tPopupMenu.show();
        });
        //</editor-fold>
        refreshBottomText();
        setBroadcastReceivers();
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        prepValues(outState);
    }



    /**
     * Called when the fragment is no longer attached to its activity.  This
     * is called after {@link #onDestroy()}.
     */
    @Override
    public void onDetach() {
        super.onDetach();
    }


    void prepValues(Bundle iBundle) {
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStop() {
        super.onStop();
        stopThread();
    }

    @Override
    public void onStart() {
        super.onStart();
        startThread();
    }
//Static Methods

    //Internal Organs
    void startThread() {
        Runnable tRunnable = new Runnable() {
            @Override
            public void run() {
                while (!interrupted()) {
                    try {
                        //noinspection BusyWait
                        sleep(4000);
                    } catch(InterruptedException e) {
                        Logg.i(TAG, e.toString());
                        // it is okay to stop the thread
                    }
                    refreshTopText();
                }
            }
        };
        mThread = new Thread(tRunnable);
        mThread.start();
    }

    void stopThread() {
        Logg.i(TAG, "StopThread");
        if (mThread != null) {
            mThread.interrupt();
            mThread = null;
        }
    }

    private void refreshTopText() {
        if (mTV_InfoTop == null || mAL_InfoText == null || mAL_InfoText.size() == 0) {
            return;
        }
        mTopTextLoop++;
        mTopTextLoop = mTopTextLoop % mAL_InfoText.size();
        String tNewText;
        tNewText = mAL_InfoText.get(mTopTextLoop);

        if (tNewText != null && !tNewText.isEmpty()) {
            final String fNewText = tNewText;
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    mTV_InfoTop.setText(fNewText);

                }
            });
        }
    }

    private void refreshBottomText() {
        if (mTV_InfoBottom != null) {
            int tIntPosition = (int) mDisplayPosition;
            int tIntDuration = (int) mDisplayDuration;
            String mBottomString =
                    String.format(Locale.ENGLISH,
                            "%d:%02d / %d:%02d ",
                            tIntPosition / 60, tIntPosition % 60,
                            tIntDuration / 60, tIntDuration % 60);
            mTV_InfoBottom.setText(mBottomString);
        }
    }

    private void shoutIconClick(String iLastObject) {
        if (mShouting != null) {
            Shout tShout = new Shout("TopBar_Fragment");
            tShout.mLastObject = iLastObject;
            tShout.mLastAction = "requested";
            mShouting.shoutUp(tShout);
        }
    }

    private void process_shouting() {
        if (mGlassFloor.mActor.equals("TopBar_BroadCastReceiver")) {

            if (mGlassFloor.mLastAction.equals("received") &&
                    mGlassFloor.mLastObject.equals("NEW_POSITION")) {
                try {
                    JSONObject tJsonObject = new JSONObject(mGlassFloor.mJsonString);
                    mDisplayPosition = (float) tJsonObject.getDouble("position");
                    mDisplayDuration = (float) tJsonObject.getDouble("duration");
                    refreshBottomText();
                } catch(Exception e) {
                    Logg.w(TAG, e.toString());
                }
            }
        }
    }

    void setBroadcastReceivers() {
        IntentFilter tIntentFilter;
        // STATUS
        tIntentFilter = new IntentFilter();
        tIntentFilter.addAction("org.pochette.musicplayer.mediaplayer.MediaPlayerService.STATUS");

        BroadcastReceiver tBroadcastReceiver_Status = new BroadcastReceiver() {
            @SuppressWarnings("Convert2Lambda")
            @Override
            public void onReceive(Context iContext, Intent iIntent) {
                //   Logg.i(TAG, "onReceive Status in Fragment");
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        // check if a context is available (rotation change?), which is needed for colors ...
                        boolean tChangePossible = true;
                        Context tContext = null;
                        try {
                            tContext = requireContext();
                        } catch(IllegalStateException e) {
                            tChangePossible = false;
                        }
                        if (tContext == null) {
                            //noinspection ConstantConditions
                            tChangePossible = false;
                        }
                        if (tChangePossible) {
                            mStringMusician = iIntent.getStringExtra("artist");
                            mStringAlbum = iIntent.getStringExtra("album");
                            mStringDance = iIntent.getStringExtra("title");
                            if (mStringMusician != null) Logg.i(TAG, mStringMusician);
                            if (mStringAlbum != null) Logg.i(TAG, mStringAlbum);
                            if (mStringDance != null) Logg.i(TAG, mStringDance);
                            mAL_InfoText.clear();
                            mTopTextLoop = 0;
                            if (mStringMusician != null) mAL_InfoText.add(mStringMusician);
                            if (mStringAlbum != null) mAL_InfoText.add(mStringAlbum);
                            if (mStringDance != null) mAL_InfoText.add(mStringDance);
                        }
                    }
                });
            }
        };
        Objects.requireNonNull(getContext()).registerReceiver(tBroadcastReceiver_Status, tIntentFilter);
    }


//Interface

    public boolean onMenuItemClick(MenuItem item) {
        if (mShouting != null) {
            Shout tShout = new Shout("TopBar_Fragment");
            tShout.mLastObject = String.valueOf(item.getTitle());
            tShout.mLastAction = "requested";
            mShouting.shoutUp(tShout);
        }
        return false;
    }


    /**
     * Receive the shout
     *
     * @param tShoutToCeiling the shout
     */
    @Override
    public void shoutUp(Shout tShoutToCeiling) {
        Logg.i(TAG,tShoutToCeiling.toString());
        mGlassFloor = tShoutToCeiling;
        process_shouting();
    }
}