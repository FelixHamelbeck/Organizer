package org.pochette.organizer.chained_search;

import org.pochette.data_library.scddb_objects.Dance;
import org.pochette.organizer.R;
import org.pochette.utils_lib.search.SearchPattern;

import java.util.ArrayList;

import androidx.annotation.Nullable;

/**
 * this is a pojo class to describe a possible final node of the dynamic search
 */
@SuppressWarnings({"unused", "rawtypes"})
public class SearchOption {

    public static final String VALUE_TYPE_NONE = "NONE";
    public static final String VALUE_TYPE_STRING = "STRING";
    @SuppressWarnings("unused")
    public static final String VALUE_TYPE_INT = "INT";
    public static final String VALUE_TYPE_ENUM = "ENUM";
    public static final String VALUE_TYPE_SQL = "SQL";
    public static final String VALUE_TYPE_PICK = "PICK"; // top ENUM than radio
    @SuppressWarnings("unused")
    private final String TAG = "FEHA (SearchOption)";


    //Variables
    /**
     * the class of objects for which the dynamic search is done
     */
    public Class mClass;
    /**
     * Text visible to the user as header for the search option
     */
    public String mCode;
    /**
     * human readable unique key, i.e. used for equal()
     */
    public String mDisplayText;
    /**
     * Id of image resource
     */
    public int mResourceId;
    /**
     * the method available a SearchCriteria for the class
     */
    public String mFormatText;
    /**
     * String to be used for Short text e.g. in Views mFormatText = "Dance: %s" should be shown as "Dance: Rant"
     */
    public String mSqlContractMethod;
    /**
     * if the search method requires input, the type of input. Available are NONE, STRING, INT, ENUM, SQL
     */
    public String mValueType;
    /**
     * A string represenation of the default input
     */
    public String mDefaultValue;
    /**
     * class name of the enum
     */
    public Class mEnumClass;

    /**
     * String of the sql condition
     */
    //public String mSql;

    //Constructor
    public SearchOption(Class iClass, String iCode, String iDisplayText, int iResourceId,
                        String iFormatText, String iMethod, String iValueType, String iDefaultValue) {
        mClass = iClass;
        mCode = iCode;
        mDisplayText = iDisplayText;
        mSqlContractMethod = iMethod;
        mValueType = iValueType;
        mDefaultValue = iDefaultValue;
        mResourceId = iResourceId;
        mFormatText = iFormatText;
    }

    //Setter and Getter
    //Livecycle
    //Static Methods
    //Internal Organs

    public static ArrayList<SearchOption> getSearchOptions(Class iClass) {

        if (iClass != Dance.class) {
            throw new RuntimeException("DynamicSearch only available for class dance");
        }

        ArrayList<SearchOption> mAR_SearchOptions;
        mAR_SearchOptions = new ArrayList<>(0);
        SearchOption tSearchOption;


        // okay options
        tSearchOption = new SearchOption(Dance.class, "COLLECT", "MyCollection Only", R.drawable.ic_music_single, "MyCollection",
                "MUSIC_REQUIRED", SearchOption.VALUE_TYPE_NONE, "");
        mAR_SearchOptions.add(tSearchOption);
        tSearchOption = new SearchOption(Dance.class, "CRIB_REQUIRED", "Only with Crib", R.drawable.ic_crib, "Crib",
                "CRIB_REQUIRED", SearchOption.VALUE_TYPE_NONE, "");
        mAR_SearchOptions.add(tSearchOption);
        tSearchOption = new SearchOption(Dance.class, "RSCDS_REQUIRED", "Only RSCDS", R.drawable.ic_rscds_crown, "RSCDS",
                "RSCDS_REQUIRED", SearchOption.VALUE_TYPE_NONE, "");
        mAR_SearchOptions.add(tSearchOption);
        tSearchOption = new SearchOption(Dance.class, "DIAGRAM_REQUIRED", "Only with Diagram", R.drawable.ic_diagram, "Diagram",
                "DIAGRAM_REQUIRED", SearchOption.VALUE_TYPE_NONE, "");
        mAR_SearchOptions.add(tSearchOption);
        tSearchOption = new SearchOption(Dance.class, "DANCENAME", "Dance Name", R.drawable.ic_allemande05_bw, "Name\u2248%s",
                "DANCENAME", SearchOption.VALUE_TYPE_STRING, "");
        mAR_SearchOptions.add(tSearchOption);



        tSearchOption = new SearchOption(Dance.class, "FORMATION", "Formation", R.drawable.ic_figures_popular, "Formation=%s",
                "FORMATION", SearchOption.VALUE_TYPE_STRING, "");
        mAR_SearchOptions.add(tSearchOption);


//        tSearchOption = new SearchOption("STEP", "Steps", R.drawable.ic_purpose_step, "Step=%s",
//                "STEPS_SQL", SearchOption.VALUE_TYPE_ENUM, "SALL"); // longwise 4
//        tSearchOption.mEnumClass = Step.class;
//        mAR_SearchOptions.add(tSearchOption);
//        tSearchOption = new SearchOption("COUPLES", "Couples", R.drawable.ic_couples, "Couples=%s",
//                "COUPLES_SQL", SearchOption.VALUE_TYPE_ENUM, "SALL"); // 3 couples
//        tSearchOption.mEnumClass = Couples.class;
//        mAR_SearchOptions.add(tSearchOption);
//        tSearchOption = new SearchOption("SHAPE", "Shape", R.drawable.ic_shape, "Shape: %s",
//                "SHAPE_SQL", SearchOption.VALUE_TYPE_ENUM, "SALL"); // longwise 4
//        tSearchOption.mEnumClass = Shape_Type.class;
//        mAR_SearchOptions.add(tSearchOption);


        tSearchOption = new SearchOption(Dance.class, "AUTHOR", "Author", R.drawable.ic_book, "Author\u2248 %s",
                "AUTHOR_NAME", SearchOption.VALUE_TYPE_STRING, "");
        mAR_SearchOptions.add(tSearchOption);

        tSearchOption = new SearchOption(Dance.class, "ALBUM", "Album", R.drawable.ic_album, "Album\u2248 %s",
                "ALBUM_NAME", SearchOption.VALUE_TYPE_STRING, "");
        mAR_SearchOptions.add(tSearchOption);

//        tSearchOption = new SearchOption("DANCE_FAVOURITE", "DanceFavourite", R.drawable.ic_favourite_searchall, "%s",
//                "DANCE_FAVOURITE", SearchOption.VALUE_TYPE_ENUM, "GORB");
//        tSearchOption.mEnumClass = DanceFavourite.class;
//        mAR_SearchOptions.add(tSearchOption);

        // nok options		v


        tSearchOption = new SearchOption(Dance.class,"RHYTHM", "Rhythm", R.drawable.ic_rhythmnode, "Rhythm",
                "RHYTHM_SINGLE", SearchOption.VALUE_TYPE_ENUM, "STRATHSPEY");

        mAR_SearchOptions.add(tSearchOption);
        // untested

        return mAR_SearchOptions;
    }

    public SearchPattern convert(String iCode, String iValue) {
        return null;
    }

    public static SearchOption getByCode(Class iClass, String iCode) {
        SearchOption tSearchOption = null;
        if (iClass.equals(Dance.class)) {
            for (SearchOption lSearchOption : SearchOption.getSearchOptions(iClass)) {
                if (lSearchOption.mCode.equals(iCode)) {
                    tSearchOption = lSearchOption;
                    break;
                }
            }
            if (tSearchOption == null) {
                throw new RuntimeException("Code not available " + iCode);
            }
        } else {
            throw new RuntimeException("SearchOption not avaialble for class " + iClass.getSimpleName());
        }
        tSearchOption.mClass = iClass;
        return tSearchOption;
    }

    public static SearchOption getByMethodCode(Class iClass, String iMethodCode) {
        SearchOption tSearchOption = null;
        if (iClass.equals(Dance.class)) {
            for (SearchOption lSearchOption : SearchOption.getSearchOptions(iClass)) {
                if (lSearchOption.mSqlContractMethod.equals(iMethodCode)) {
                    tSearchOption = lSearchOption;
                    break;
                }
            }
            if (tSearchOption == null) {
                throw new RuntimeException("MethodCode not available " + iMethodCode);
            }
        } else {
            throw new RuntimeException("SearchOption not avaialble for class " + iClass.getSimpleName());
        }
        tSearchOption.mClass = iClass;
        return tSearchOption;
    }


    @Override
    public int hashCode() {
        return mCode.hashCode();
    }




    //Interface

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!obj.getClass().equals(SearchOption.class)) {
            return false;
        }
        SearchOption oSearchOption = (SearchOption) obj;
        return mCode.equals(oSearchOption.mCode);
    }


}
