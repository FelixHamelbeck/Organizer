package org.pochette.data_library.diagram;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.pochette.data_library.database_management.SearchCall;
import org.pochette.data_library.database_management.WriteCall;
import org.pochette.utils_lib.search.SearchCriteria;
import org.pochette.utils_lib.search.SearchPattern;

import java.io.ByteArrayOutputStream;
import java.util.Locale;

import androidx.annotation.NonNull;

import static java.lang.StrictMath.max;

/**
 * This class represents the dance diagrams as locally stored and provide downloads for strathspey.org
 */
public class Diagram {

    @SuppressWarnings("unused")
    private static final String TAG = "FEHA (Diagram)";

    int mId;
    String mPath;
    String mAuthor;
    int mDanceId;
    Bitmap mBitmap;

    public Diagram(int tId, String tPath, String tAuthor, int tDanceId, Bitmap iBitmap) {
        mId = tId;
        mPath = tPath;
        mAuthor = tAuthor;
        mDanceId = tDanceId;
        mBitmap = iBitmap;
    }

    //</editor-fold>

    //<editor-fold desc="Setter and Getter">
    public int getId() {
        return mId;
    }

    public void setId(int mId) {
        this.mId = mId;
    }

    @SuppressWarnings("unused")
    public String getAuthor() {
        return mAuthor;
    }

    @SuppressWarnings("unused")
    public void setAuthor(String mAuthor) {
        this.mAuthor = mAuthor;
    }

    public String getPath() {
        return mPath;
    }

    public void setPath(String mPath) {
        this.mPath = mPath;
    }

    public int getDanceId() {
        return mDanceId;
    }

    public void setDanceId(int mDanceId) {
        this.mDanceId = mDanceId;
    }

    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }
    //</editor-fold>
 

    @NonNull
    public String toString() {
        return String.format(Locale.ENGLISH, "%d for %d[%s]: %s", mId, mDanceId, mAuthor, mPath.substring(max(0, mPath.length() - 20)));
    }

    public static Diagram getByDanceId(int tDanceId) {
        SearchPattern tSearchPattern = new SearchPattern(Diagram.class);
        SearchCriteria tSearchCriteria = new SearchCriteria("DANCE_ID", "" + (tDanceId));
        tSearchPattern.addSearch_Criteria(tSearchCriteria);
        SearchCall tSearchCall = new SearchCall(Diagram.class, tSearchPattern, null);
        Diagram tDiagram;
        tDiagram = tSearchCall.produceFirst();
        return tDiagram;
    }

    static Bitmap ByteArray2Bitmap(byte[] iImageBytes) {
        return BitmapFactory.decodeByteArray(iImageBytes, 0, iImageBytes.length);
    }

    static byte[] Bitmap2ByteArray(Bitmap iBitmap) {
        ByteArrayOutputStream tByteArrayOutputStream = new ByteArrayOutputStream();
        iBitmap.compress(Bitmap.CompressFormat.PNG, 100, tByteArrayOutputStream);
        return tByteArrayOutputStream.toByteArray();
    }
    //<editor-fold desc="Database stuff">
    public Diagram save() {
        WriteCall tWriteCall = new WriteCall(Diagram.class, this);
        if (mId <= 0) {
            try {
                mId = tWriteCall.insert();
            } catch(SQLiteConstraintException e) {
                // duplicate key requires an update statement
                // just to read the id, we select first
                SearchPattern tSearchPattern;
                SearchCall tSearchCall;
                tSearchPattern = new SearchPattern(Diagram.class);
                tSearchPattern.addSearch_Criteria(new SearchCriteria("DANCE_ID", "" + mDanceId));
                tSearchPattern.addSearch_Criteria(new SearchCriteria("AUTHOR", "" + mAuthor));
                tSearchCall = new SearchCall(Diagram.class, tSearchPattern, null);
                Diagram tDiagramExist = tSearchCall.produceFirst();
                mId = tDiagramExist.mId;
                tWriteCall.update();
            }
        } else {
            tWriteCall.update();
        }
        return this;
    }

    public ContentValues getContentValues() {
        ContentValues tContentValues = new ContentValues();
        tContentValues.put("PATH", mPath);
        tContentValues.put("AUTHOR", mAuthor);
        tContentValues.put("DANCE_ID", mDanceId);
        if (mBitmap != null) {
            byte[] tByteArray = Bitmap2ByteArray(mBitmap);
            if (tByteArray.length > 100) {
                tContentValues.put("BITMAP", tByteArray);
            }
        }
        return tContentValues;
    }


    public static Diagram convertCursor(Cursor tCursor) {
        Diagram result;
        Bitmap tBitmap = null;
        byte[] tByteArray = tCursor.getBlob(tCursor.getColumnIndex("BITMAP"));
        if (tByteArray != null && tByteArray.length > 100) {
            tBitmap = ByteArray2Bitmap(tByteArray);
        }
        result = new Diagram(
                tCursor.getInt(tCursor.getColumnIndex("ID")),
                tCursor.getString(tCursor.getColumnIndex("PATH")),
                tCursor.getString(tCursor.getColumnIndex("AUTHOR")),
                tCursor.getInt(tCursor.getColumnIndex("DANCE_ID")),
                tBitmap);
        return result;
    }
    //</editor-fold>


}
