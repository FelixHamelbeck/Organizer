package org.pochette.data_library.requestlist;

import org.json.JSONException;
import org.json.JSONObject;
import org.pochette.data_library.music.MusicFile;
import org.pochette.data_library.scddb_objects.Dance;
import org.pochette.data_library.scddb_objects.Recording;
import org.pochette.utils_lib.logg.Logg;

import java.security.InvalidParameterException;

import androidx.annotation.Nullable;

@SuppressWarnings("unused")
public class Request {

    private static final String TAG = "FEHA (Request)";

    // variables
    MusicFile mMusicFile;
    Dance mDance;
    Recording mRecording;
    int mMusicFile_id;
    int mDance_Id;
    int mRecording_Id;
    String mArtist;
    String mName;
    String mSignature;
    int mDuration;

    // constructor
    Request(JSONObject iJsonObject) {
        try {
            mMusicFile_id = iJsonObject.getInt("M");
            mDance_Id = iJsonObject.getInt("D");
            mRecording_Id = iJsonObject.getInt("R");
            mArtist = iJsonObject.getString("Artist");
            mName = iJsonObject.getString("Name");
            mSignature = iJsonObject.getString("Signature");
            mDuration = iJsonObject.getInt("Duration");
        } catch(JSONException e) {
            Logg.w(TAG, e.toString());
            MusicFile tMusicFile = MusicFile.getById(mMusicFile_id);
            Dance tDance = Dance.getById(mDance_Id);
            Recording tRecording = Recording.getById(mRecording_Id);
            Request tRequest = new Request(tMusicFile, tDance, tRecording);
            mArtist = tRequest.mArtist;
            mName = tRequest.mName;
            mSignature = tRequest.mSignature;
            mDuration  = tRequest.mDuration;
//            throw new IllegalArgumentException("Not a valid JsonObject " + iJsonObject.toString());
        }

    }

   public  Request(MusicFile iMusicFile, Dance iDance, Recording iRecording) {
        if (iDance == null && iRecording == null && iMusicFile == null) {
            throw new InvalidParameterException("Cannot construct Request without any input");
        }
        if (iDance != null) {
            mDance = iDance;
            mDance_Id = mDance.mId;
        }
        if (iRecording != null) {
            mRecording = iRecording;
            mRecording_Id = mRecording.mId;
        }
        if (iMusicFile != null) {
            mMusicFile = iMusicFile;
            mMusicFile_id = mMusicFile.mId;
        }
        if (mDance != null) {
            mName = mDance.mName;
        } else if (mMusicFile != null) {
            mName = mMusicFile.mName;
        } else if (mRecording != null) {
            mName = mRecording.mName;
        } else {
            mName = "";
        }
        if (mMusicFile != null) {
            mSignature = mMusicFile.mSignature;
        } else if (mDance != null) {
            mSignature = mDance.getSignature();
        } else if (mRecording != null) {
            mName = mRecording.mSignature;
        } else {
            mName = "";
        }
        if (mMusicFile != null) {
            mArtist = mMusicFile.mT2;
            mDuration = mMusicFile.mDuration;
        } else if (mRecording != null) {
            mArtist = mRecording.mAlbumArtistName;
            mDuration = mRecording.mPlayingseconds;
        } else {
            mArtist = "";
            mDuration = 0;
        }
    }


    // setter and getter


    public MusicFile getMusicFile() {
        if (mMusicFile == null || mMusicFile_id > 0) {
            mMusicFile = MusicFile.getById(mMusicFile_id);
        }
        return mMusicFile;
    }

    public void setMusicFile(MusicFile musicFile) {
        mMusicFile = musicFile;
        mMusicFile_id = mMusicFile.mId;
    }

    public int getMusicFile_id() {
        return mMusicFile_id;
    }

    public void setMusicFile_id(int musicFile_id) {
        mMusicFile_id = musicFile_id;
        if (mMusicFile != null && mMusicFile.mId != mMusicFile_id) {
            mMusicFile = null;
        }
    }

    public Dance getDance() {
        if (mDance == null || mDance_Id > 0) {
            mDance = Dance.getById(mDance_Id);
        }
        return mDance;
    }

    public void setDance(Dance dance) {
        mDance = dance;
        mDance_Id = mDance.mId;
    }

    public int getDance_Id() {
        return mDance_Id;
    }

    public void setDance_Id(int dance_Id) {
        mDance_Id = dance_Id;
        if (mDance != null && mDance.mId != mDance_Id) {
            mDance = null;
        }
    }

    public Recording getRecording() {
        if (mRecording == null || mRecording_Id > 0) {
            mRecording = Recording.getById(mRecording_Id);
        }
        return mRecording;
    }

    public void setRecording(Recording recording) {
        mRecording = recording;
        mRecording_Id = mRecording.mId;
    }

    public int getRecording_Id() {
        return mRecording_Id;
    }

    public void setRecording_Id(int recording_Id) {
        mRecording_Id = recording_Id;
        if (mRecording != null && mRecording.mId != mRecording_Id) {
            mRecording = null;
        }
    }

    public String getName() {
//        if (mDance_Id > 0) {
//            getDance();
//            return mDance.mName;
//        } else if (mMusicFile_id > 0) {
//            getMusicFile();
//            return mMusicFile.mName;
//        } else {
//            getRecording();
//            return mRecording.mName;
//        }
        return  mName;
    }

    public String getArtist() {
//        if (mRecording_Id > 0) {
//            getRecording();
//            return mRecording.mAlbumArtistName;
//
//        } else if (mMusicFile_id > 0) {
//            getMusicFile();
//            return mMusicFile.mT2;
//        } else {
//            return "";
//        }
        return mArtist;
    }

    public String getSignature() {
//        if (mMusicFile_id > 0) {
//            getMusicFile();
//            return mMusicFile.mSignature;
//        } else if (mDance_Id > 0) {
//            getDance();
//            return mDance.getSignature();
//        } else {
//            getRecording();
//            return mRecording.mSignature;
//        }
        return  mSignature;
    }

    public int getDuration() {
//        if (mMusicFile_id > 0) {
//            getMusicFile();
//            return mMusicFile.mDuration;
//        } else if (mRecording_Id > 0) {
//            getRecording();
//            return mRecording.mPlayingseconds;
//
//        } else {
//            return 0;
//        }
        return mDuration;
    }




    // lifecylce and override
    // internal
    void populate() {
        if (mMusicFile_id > 0) {
            mMusicFile = MusicFile.getById(mMusicFile_id);
        }
        if (mDance_Id > 0) {
            mDance = Dance.getById(mDance_Id);
        }
        if (mRecording_Id > 0) {
            mRecording = Recording.getById(mRecording_Id);
        }
    }

    private JSONObject convertToJsonObject() {
        try {
            JSONObject tJsonObject = new JSONObject();
            tJsonObject.put("M", mMusicFile_id);
            tJsonObject.put("R", mRecording_Id);
            tJsonObject.put("D", mDance_Id);

            tJsonObject.put("Artist", mArtist);
            tJsonObject.put("Name", mName);
            tJsonObject.put("Signature", mSignature);
            tJsonObject.put("Duration", mDuration);

            return tJsonObject;
        } catch(JSONException e) {
            Logg.w(TAG, e.toString());
            return null;
        }
    }
    // public methods


    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!obj.getClass().equals(Request.class)) {
            return false;
        }
        Request oRequest = (Request) obj;
        if (mMusicFile_id != oRequest.mMusicFile_id) {
            return false;
        }
        if (mDance_Id != oRequest.mDance_Id) {
            return false;
        }
        return mRecording_Id == oRequest.mRecording_Id;
    }

    public JSONObject getJsonObject() {
        return convertToJsonObject();
    }


}
