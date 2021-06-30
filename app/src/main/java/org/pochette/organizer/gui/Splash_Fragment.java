package org.pochette.organizer.gui;

import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.pochette.data_library.database_management.LdbBackup;
import org.pochette.data_library.database_management.Ldb_Helper;
import org.pochette.organizer.R;
import org.pochette.organizer.app.OrganizerApp;
import org.pochette.utils_lib.logg.Logg;
import org.pochette.utils_lib.shouting.Shout;
import org.pochette.utils_lib.shouting.Shouting;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;

public class Splash_Fragment extends Fragment implements Shouting, LifecycleOwner {

    //Variables
    private static final String TAG = "FEHA (Splash_Fragment)";

    ImageView mIV_Icon;


    Shouting mShouting;
    Shout mShoutToCeiling;
    private Shout mGlassFloor;

    //Constructor

    //Setter and Getter
    public void setShouting(Shouting mShouting) {
        this.mShouting = mShouting;
    }


    //Livecycle
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Logg.i(TAG, "onAttach");
        mShoutToCeiling = new Shout(this.getClass().getSimpleName());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater iInflater, ViewGroup iContainer,
                             Bundle iSavedInstanceState) {

        Logg.i(TAG, "onCreateView");
        //noinspection UnnecessaryLocalVariable
        View view = iInflater.inflate(R.layout.fragment_splash, iContainer, false);
        return view;
    }

    @Override
    public void onViewCreated(@Nullable View view, @Nullable Bundle savedInstanceState) {
        Logg.i(TAG, "onViewCreated");

        mIV_Icon = requireView().findViewById(R.id.IV_Splash_AppIcon);

        if (mIV_Icon != null) {
            mIV_Icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Logg.k(TAG, "OnClick");
                    processClick();

                }
            });

            mIV_Icon.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Logg.k(TAG, "OnClick");
                    processLongClick();
                    return false;
                }
            });
        }


    }


    //Static Methods
    //Internal Organs

    void processClick() {
        OrganizerApp tOrganizerApp = (OrganizerApp) getActivity().getApplication();
        if (tOrganizerApp != null) {
            tOrganizerApp.setFlagDataPreparation(true);
        }
    }

    void processLongClick() {
        OrganizerApp tOrganizerApp = (OrganizerApp) getActivity().getApplication();
        if (tOrganizerApp != null) {
            tOrganizerApp.holdPreparation();
        }

        if (Looper.myLooper() == Looper.getMainLooper()) {
            Logg.i(TAG, "onMain");
        } else {

            Logg.i(TAG, "offBroadway");
        }

        String tAnswer;
        UserChoice tUserChoice = new UserChoice("Which Action");
        tUserChoice.addQuestion(new UserQuestion("BACKUP", "Backup Database?"));
        tUserChoice.addQuestion(new UserQuestion("LOAD", "Load Backup?"));
        tUserChoice.addQuestion(new UserQuestion("DELETE", "Delete Backup?"));
        tUserChoice.addQuestion(new UserQuestion("CONTINUE", "Continue"));
        tAnswer = tUserChoice.poseQuestion(this.getContext());


        switch (tAnswer) {
            case "BACKUP":
                processBackupRequest();
                break;
            case "LOAD":
                processLoadRequest();
                break;
            case "DELETE":
                processDeleteRequest();
                break;
            default:
            case "CONTINUE":

        }

        tOrganizerApp.startFromBeginning(false);


    }

    void processBackupRequest() {
        try {
            Logg.w(TAG, "processBackupRequest");
            LdbBackup.createInstance(this.getContext());
            LdbBackup tLdbBackup = LdbBackup.getInstance();
            tLdbBackup.backup();
        } finally {
            LdbBackup.destroyInstance();
        }

//        if (1 == 1) {
//            return;
//        }
//
//        Ldb_Helper tLdb_Helper = tLdbBackup.getLdb_Helper(this.getContext());
////        try {
////            tLdb_Helper = Ldb_Helper.getInstance();
////            if (tLdb_Helper == null) {
////                Ldb_Helper.createInstance(getContext());
////                tLdb_Helper = Ldb_Helper.getInstance();
////            }
////        } catch(Exception e) {
////            Ldb_Helper.createInstance(getContext());
////            tLdb_Helper = Ldb_Helper.getInstance();
////        }
//        if (tLdb_Helper == null) {
//            Logg.w(TAG, "Cannot perform backup, as LdbHelper cannot be created");
//            return;
//        }
//        File tLdb_File = tLdb_Helper.getLdbFile();
//        File tDirectory = tLdb_File.getParentFile();
//
//        SimpleDateFormat tFilenameSimpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH);
//        String tBackupFilename;
//        String tDateandTime = tFilenameSimpleDateFormat.format(new Date());
//        tBackupFilename = "Ldb." + tDateandTime + ".db";
//        File tBackupFile = new File(tDirectory + "/" + tBackupFilename);
//
//        Logg.i(TAG, "start backup to " + tBackupFilename);
//        // execute
//
//        tLdbBackup.copy(tLdb_File,tBackupFile);

//        try {
//            FileInputStream inStream = new FileInputStream(tLdb_File);
//            FileOutputStream outStream = new FileOutputStream(tBackupFile);
//            FileChannel inChannel = inStream.getChannel();
//            FileChannel outChannel = outStream.getChannel();
//            inChannel.transferTo(0, inChannel.size(), outChannel);
//            inStream.close();
//            outStream.close();
//            Logg.i(TAG, "copy done to " + tBackupFilename);
//        } catch(IOException e) {
//            Logg.w(TAG, e.toString());
//        }
    }

    void processDeleteRequest() {

        try {
            LdbBackup.createInstance(this.getContext());
            LdbBackup tLdbBackup = LdbBackup.getInstance();
            File tDirectory = tLdbBackup.getDirectory();
            UserChoice tUserChoice = new UserChoice("Delete which backup");
            for (File lFile : tDirectory.listFiles()) {
                if (lFile.getName().startsWith("Ldb.2")) { // so in year 3 key we need to fix this
                    tUserChoice.addQuestion(lFile.getName(), lFile.getName());
                }
            }
            String tAnswer;
            tAnswer = tUserChoice.poseQuestion(this.getContext());
            Logg.w(TAG, "delete " + tAnswer);
            File tFileToBeDeleted = new File(tDirectory + "/" + tAnswer);
            tFileToBeDeleted.delete();
            Logg.i(TAG, "deleted " + tAnswer);
        } finally {
            LdbBackup.destroyInstance();
        }

//        Ldb_Helper tLdb_Helper;
//        try {
//            tLdb_Helper = Ldb_Helper.getInstance();
//            if (tLdb_Helper == null) {
//                Ldb_Helper.createInstance(getContext());
//                tLdb_Helper = Ldb_Helper.getInstance();
//            }
//        } catch(Exception e) {
//            Ldb_Helper.createInstance(getContext());
//            tLdb_Helper = Ldb_Helper.getInstance();
//        }
//        if (tLdb_Helper == null) {
//            Logg.w(TAG, "Cannot perform backup, as LdbHelper cannot be created");
//            return;
//        }
//        File tLdb_File = tLdb_Helper.getLdbFile();
//        File tDirectory = tLdb_File.getParentFile();

//        UserChoice tUserChoice = new UserChoice("Delete which backup");
//        for (File lFile : tDirectory.listFiles()) {
//            Logg.i(TAG, lFile.getName());
//            if (lFile.getName().startsWith("Ldb.2")) { // so in year 3 key we need to fix this
//                tUserChoice.addQuestion(lFile.getName(), lFile.getName());
//            }
//        }
//        String tAnswer;
//        tAnswer= tUserChoice.poseQuestion(this.getContext());
//        Logg.w(TAG, "delete " + tAnswer);
//        File tFileToBeDeleted = new File(tDirectory + "/" + tAnswer);
//        tFileToBeDeleted.delete();
//        Logg.w(TAG, "deleted " + tAnswer);

    }


    void processLoadRequest() {
        try {
            LdbBackup.createInstance(this.getContext());
            LdbBackup tLdbBackup = LdbBackup.getInstance();
            File tDirectory = tLdbBackup.getDirectory();
            UserChoice tUserChoice = new UserChoice("Load which backup?");
            for (File lFile : tDirectory.listFiles()) {
                if (lFile.getName().startsWith("Ldb.2")) { // so in year 3 key we need to fix this
                    tUserChoice.addQuestion(lFile.getName(), lFile.getName());
                }
            }
            String tAnswer;
            tAnswer = tUserChoice.poseQuestion(this.getContext());
            Logg.i(TAG, "load " + tAnswer);
            File tBackupFile = new File(tDirectory + "/" + tAnswer);
            tLdbBackup.load(tBackupFile);
        } finally {
            LdbBackup.destroyInstance();
        }
    }


    void process_shouting() {

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