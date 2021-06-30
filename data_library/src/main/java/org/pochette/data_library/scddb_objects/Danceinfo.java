package org.pochette.data_library.scddb_objects;

import android.database.Cursor;

import org.pochette.data_library.database_management.SearchCall;
import org.pochette.utils_lib.logg.Logg;
import org.pochette.utils_lib.search.SearchCriteria;
import org.pochette.utils_lib.search.SearchPattern;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;

/**
 * A simple POJO class with information about a dance. It has more information compared to Dance
 * Its usage is usually when all availble info about a single dance needs to be displayed
 */
public class Danceinfo {

    //Variables
    private static final String TAG = "FEHA (Danceinfo)";

    public int mId;
    public String mName;
    public String mTypeName;
    public int mBarsPerRepeat;
    public String mCouplesString;
    public String mShapesString;
    public String mShapeShortnames;
    public String mProgressionName;
    public String mDevisorName;
    public Date mDevicesDate;
    public String mNotes;
    public int mIntensity;
    public String mIntensityBars;
    public int mIntensityPerTurn;
    public String mFormationString;
    public String mFormationDetailString;
    public String mStepsString;
    public String mStepsShortnames;
    public String mPublicationString;

    //Constructor


    public static Danceinfo getBy(int iId) {
        SearchPattern tSearchPattern = new SearchPattern(Danceinfo.class);
        tSearchPattern.addSearch_Criteria(
                new SearchCriteria("ID",
                        String.format(Locale.ENGLISH, "%d", iId)));
        SearchCall tSearchCall =
                new SearchCall(Danceinfo.class, tSearchPattern, null);
        return tSearchCall.produceFirst();
    }

    public Danceinfo(int iId, String iName,
                     String iTypeName, int iBarsPerRepeat, String iCouplesString,
                     String iShapesString, String iShapeShortnames, String iProgressionName,
                     String iDevisorName, Date iDevicesDate, String iNotes, int iIntensity,
                     String iIntensityBars, int iIntensityPerTurn, String iFormationString,
                     String iFormationDeatilString,
                     String iStepsString, String iStepsShortnames, String iPublicationString) {
        mId = iId;
        mName = iName;
        mTypeName = iTypeName;
        mBarsPerRepeat = iBarsPerRepeat;
        mCouplesString = iCouplesString;
        mShapesString = iShapesString;
        mShapeShortnames = iShapeShortnames;
        mProgressionName = iProgressionName;
        mDevisorName = iDevisorName;
        mDevicesDate = iDevicesDate;
        mNotes = iNotes;
        mIntensity = iIntensity;
        mIntensityBars = iIntensityBars;
        mIntensityPerTurn = iIntensityPerTurn;
        mFormationString = iFormationString;
		mFormationDetailString = iFormationDeatilString;
        mStepsString = iStepsString;
        mStepsShortnames = iStepsShortnames;
        mPublicationString = iPublicationString;
    }

    //Setter and Getter
    //Livecycle
    //Static Methods
    //Internal Organs
    //Interface

    @NonNull
    public String toString() {
        return String.format(Locale.ENGLISH, "%s [%s]", mName, mId);
    }

    public static Danceinfo convertCursor(Cursor tCursor) {
        Date tDevisedDate = null;
        String tDateString =
                tCursor.getString(tCursor.getColumnIndex("DEVISED_DATE"));
        if (tDateString != null) {

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            try {
                tDevisedDate = format.parse(tDateString);
            } catch (ParseException e) {
                tDevisedDate = null;
            }
        }
        int tId;
        String tName;
        String tTypeName;
        int tBarsPerRepeat;
        String tCouplesString;
        String tShapesString;
        String tShapeShortnames;
        String tProgressionName;
        String tDevisorName;
        String tNotesString;
        int tIntensity;
        String tIntensityBars;
        int tIntensityPerTurn;
        String tFormationString;
        String tFormationDetailString;
        String tStepsString;
        String tStepsShortnames;
        String tPublicationString;
        try {
            tId = tCursor.getInt(tCursor.getColumnIndex("D_ID"));
            tName = tCursor.getString(tCursor.getColumnIndex("D_NAME"));
            tTypeName = tCursor.getString(tCursor.getColumnIndex("D_TYPENAME"));
            tBarsPerRepeat = tCursor.getInt(tCursor.getColumnIndex("D_BARSPERREPEAT"));
            tCouplesString = tCursor.getString(tCursor.getColumnIndex("D_COUPLES"));
            tShapesString = tCursor.getString(tCursor.getColumnIndex("D_SHAPE"));
            tShapeShortnames = tCursor.getString(tCursor.getColumnIndex("D_SHAPE_SHORTNAME"));
            tProgressionName = tCursor.getString(tCursor.getColumnIndex("D_PROGRESSION"));
            tDevisorName = tCursor.getString(tCursor.getColumnIndex("DEVISOR_NAME"));
            tNotesString = tCursor.getString(tCursor.getColumnIndex("NOTES"));
            tIntensity = tCursor.getInt(tCursor.getColumnIndex("INTENSITY"));
            tIntensityBars = tCursor.getString(tCursor.getColumnIndex("INTENSITY_BARS"));
            tIntensityPerTurn = tCursor.getInt(tCursor.getColumnIndex("INTENSITY_PER_TURN"));
            tFormationString = tCursor.getString(tCursor.getColumnIndex("FORMATION_STRING"));
            tFormationDetailString = tCursor.getString(tCursor.getColumnIndex("FORMATIONDETAIL_STRING"));
            tStepsString = tCursor.getString(tCursor.getColumnIndex("STEPS_STRING"));
            tStepsShortnames = tCursor.getString(tCursor.getColumnIndex("STEPS_SHORTNAME"));
            tPublicationString = tCursor.getString(tCursor.getColumnIndex("PUBLICATIONS_STRING"));
        } catch (Exception e) {
            Logg.e(TAG, e.toString());
            return null;
        }
        return new Danceinfo(
                tId,
                tName,
                tTypeName,
                tBarsPerRepeat,
                tCouplesString,
                tShapesString,
                tShapeShortnames,
                tProgressionName,
                tDevisorName,
                tDevisedDate,
                tNotesString,
                tIntensity,
                tIntensityBars,
                tIntensityPerTurn,
                tFormationString,
                tFormationDetailString,
                tStepsString,
                tStepsShortnames,
                tPublicationString
        );
    }


    public static Danceinfo getByDanceId(int tId) {
        SearchPattern tSearchPattern = new SearchPattern(Danceinfo.class);
        tSearchPattern.addSearch_Criteria(
                new SearchCriteria("ID",
                        String.format(Locale.ENGLISH, "%d", tId)));
        SearchCall tSearchCall =
                new SearchCall(Danceinfo.class, tSearchPattern, null);
        return tSearchCall.produceFirst();
    }
}