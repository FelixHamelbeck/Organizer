package org.pochette.organizer.gui_assist;

import org.pochette.data_library.scddb_objects.DanceClassification;
import org.pochette.organizer.R;

import java.util.ArrayList;
import java.util.Locale;

public class SpinnerItemFactory {

    @SuppressWarnings("unused")
    private static final String TAG = "FEHA (SpinnerItemFactory)";

    public static final String FIELD_RHYTHM = "RHYTHM";
    public static final String FIELD_DANCE_FAVOURITE = "DANCE_FAVOURITE";
    public static final String FIELD_MUSIC_PREFERENCE = "MUSIC_PREFERENCE";
    public static final String FIELD_SHAPE = "SHAPE";
  //  public static final String FIELD_PLAYLIST_PURPOSE = "PLAYLIST_PURPOSE";
    public static final String FIELD_REQUESTLIST_PURPOSE = "REQUESTLIST_PURPOSE";
    public static final String FIELD_MUSICDIRECTORY_PURPOSE = "MUSICDIRECTORY_PURPOSE";
    public static final String FIELD_MUSICFILE_PURPOSE = "MUSICFILE_PURPOSE";
    public static final String FIELD_MUSICFILE_SIGNATURE = "MUSICFILE_SIGNATURE";
    public static final String FIELD_PAIRING_STATUS = "PAIRING_STATUS";
    // variables
    // constructor

    public SpinnerItemFactory() {
    }
    // setter and getter
    // lifecylce and override
    // internal
    // public methods

    public CustomSpinnerItem getSpinnerItem(String iField, String iKey) {
        for (CustomSpinnerItem lCustomSpinnerItem : this.getSpinnerItems(iField, true)) {
            //  Logg.w(TAG, lCustomSpinnerItem.mKey);
            if (lCustomSpinnerItem.mKey.equals(iKey)) {
                return lCustomSpinnerItem;
            }
        }
        String tText = String.format(Locale.ENGLISH, "Key %s not available for field %s", iKey, iField);
        throw new RuntimeException(tText);
    }

    /**
     * Provide the spinner items needed for field
     *
     * @param iField  for which the spinner item ist needed
     * @param iSearch true, if for a search spinner, false for data definition
     * @return an array list of available spinner
     */

    public ArrayList<CustomSpinnerItem> getSpinnerItems(String iField, boolean iSearch) {
        ArrayList<CustomSpinnerItem> tAL_CustomSpinnerItem;
        tAL_CustomSpinnerItem = new ArrayList<>(0);
        switch (iField) {
            //<editor-fold desc="RHYTHM">
            case "RHYTHM":
                if (iSearch) {
                    tAL_CustomSpinnerItem.add(
                            new CustomSpinnerItem("All", "SALL", "", 400, R.drawable.ic_asterix));
                    tAL_CustomSpinnerItem.add(
                            new CustomSpinnerItem("QuickTime", "RHYTHM_QUICK", "Quick", 300, R.drawable.ic_type_quick));
                }
                tAL_CustomSpinnerItem.add(
                        new CustomSpinnerItem("Strathspey", "RHYTHM_SINGLE", "Strathspey", 370, R.drawable.ic_type_strathspey));
                tAL_CustomSpinnerItem.add(
                        new CustomSpinnerItem("Reel", "RHYTHM_SINGLE", "Reel", 340, R.drawable.ic_type_reel));
                tAL_CustomSpinnerItem.add(
                        new CustomSpinnerItem("Jig", "RHYTHM_SINGLE", "Jig", 330, R.drawable.ic_type_jig));
                tAL_CustomSpinnerItem.add(
                        new CustomSpinnerItem("Medley", "RHYTHM_SINGLE", "Medley", 310, 0));
                tAL_CustomSpinnerItem.add(
                        new CustomSpinnerItem("March", "RHYTHM_SINGLE", "March", 190, 0));
                tAL_CustomSpinnerItem.add(
                        new CustomSpinnerItem("Polka", "RHYTHM_SINGLE", "Polka", 180, 0));
                tAL_CustomSpinnerItem.add(
                        new CustomSpinnerItem("Hornpipe (slow)", "RHYTHM_SINGLE", "Hornpipe (slow)", 170, 0));
                tAL_CustomSpinnerItem.add(
                        new CustomSpinnerItem("Step/Highland", "RHYTHM_SINGLE", "Step/Highland", 160, 0));
                tAL_CustomSpinnerItem.add(
                        new CustomSpinnerItem("Slipjig", "RHYTHM_SINGLE", "Jig (9/8)", 150, 0));
                tAL_CustomSpinnerItem.add(
                        new CustomSpinnerItem("Waltz", "RHYTHM_SINGLE", "Waltz", 90, 0));
                tAL_CustomSpinnerItem.add(
                        new CustomSpinnerItem("Other", "RHYTHM_SINGLE", "Other", 80, 0));
                tAL_CustomSpinnerItem.add(
                        new CustomSpinnerItem("Listening", "RHYTHM_SINGLE", "Listening", 70, 0));
                tAL_CustomSpinnerItem.add(
                        new CustomSpinnerItem("(unknown)", "RHYTHM_SINGLE", "(unknown)", 60, 0));
                tAL_CustomSpinnerItem.add(
                        new CustomSpinnerItem("3/4 time", "RHYTHM_SINGLE", "3/4 time", 50, 0));
                tAL_CustomSpinnerItem.add(
                        new CustomSpinnerItem("Quadrille", "RHYTHM_SINGLE", "Quadrille", 50, 0));
                tAL_CustomSpinnerItem.add(
                        new CustomSpinnerItem("Minuet", "RHYTHM_SINGLE", "Minuet", 40, 0));
                break;
            //</editor-fold>
            //<editor-fold desc="DANCE_FAVOURITE">
            case "DANCE_FAVOURITE":
                if (iSearch) {
                    tAL_CustomSpinnerItem.add(
                            new CustomSpinnerItem("All", "SALL", "", 100, R.drawable.ic_asterix));

                    tAL_CustomSpinnerItem.add(
                            new CustomSpinnerItem("Very Good", "DANCE_FAVOURITE_SINGLE", DanceClassification.VYGO, 80, R.drawable.ic_favourite_verygood));
                    tAL_CustomSpinnerItem.add(
                            new CustomSpinnerItem("Good or better", "DANCE_FAVOURITE_GOOD_OR_BETTER", "", 75,
                                    R.drawable.ic_favourite_searchgoodorbetter));
                    tAL_CustomSpinnerItem.add(
                            new CustomSpinnerItem("Good or better", "DANCE_FAVOURITE_OKAY_OR_BETTER", "", 65,
                                    R.drawable.ic_favourite_searchgreen));

                }
                tAL_CustomSpinnerItem.add(
                        new CustomSpinnerItem("Good", "DANCE_FAVOURITE_SINGLE", DanceClassification.GOOD, 70, R.drawable.ic_favourite_good));
                tAL_CustomSpinnerItem.add(
                        new CustomSpinnerItem("Okay", "DANCE_FAVOURITE_SINGLE", DanceClassification.OKAY, 60, R.drawable.ic_favourite_okay));
                tAL_CustomSpinnerItem.add(
                        new CustomSpinnerItem("Neutral", "DANCE_FAVOURITE_SINGLE", DanceClassification.NEUT, 50, R.drawable.ic_favourite_neutral));
                tAL_CustomSpinnerItem.add(
                        new CustomSpinnerItem("Unknown", "DANCE_FAVOURITE_SINGLE", DanceClassification.UNKN, 40, R.drawable.ic_favourite_unknown));

                tAL_CustomSpinnerItem.add(
                        new CustomSpinnerItem("Bad or worse", "DANCE_FAVOURITE_BAD_OR_WORSE", "", 25,
                                R.drawable.ic_favourite_searchbad));
                tAL_CustomSpinnerItem.add(
                        new CustomSpinnerItem("Rather not", "DANCE_FAVOURITE_SINGLE", DanceClassification.RANO, 30, R.drawable.ic_favourite_rathernot));
                tAL_CustomSpinnerItem.add(
                        new CustomSpinnerItem("Surely not", "DANCE_FAVOURITE_SINGLE", DanceClassification.HORR, 20, R.drawable.ic_favourite_horrible));
                break;
            //</editor-fold>
            //<editor-fold desc="DANCE_FAVOURITE">
            case FIELD_MUSIC_PREFERENCE:
                if (iSearch) {
                    tAL_CustomSpinnerItem.add(
                            new CustomSpinnerItem("All", "SALL", "", 100, R.drawable.ic_asterix));
                }
                tAL_CustomSpinnerItem.add(
                        new CustomSpinnerItem("Not OK", "MUSIC_PREFERENCE_SINGLE", "NOOK", 30,
                                R.drawable.ic_favourite_horrible));
                tAL_CustomSpinnerItem.add(
                        new CustomSpinnerItem("Unknown", "MUSIC_PREFERENCE_SINGLE", "UNKN", 40,
                                R.drawable.ic_favourite_unknown));
                tAL_CustomSpinnerItem.add(
                        new CustomSpinnerItem("odd Repeat", "MUSIC_PREFERENCE_SINGLE", "REOD", 45,
                                R.drawable.ic_favourite_oddrepeat));
                tAL_CustomSpinnerItem.add(
                        new CustomSpinnerItem("Neutral", "MUSIC_PREFERENCE_SINGLE", "NEUTRAL", 50,
                                R.drawable.ic_favourite_anygood));
                tAL_CustomSpinnerItem.add(
                        new CustomSpinnerItem("Any Good", "MUSIC_PREFERENCE_SINGLE", "ANYG", 55,
                                R.drawable.ic_favourite_anygood));
                tAL_CustomSpinnerItem.add(
                        new CustomSpinnerItem("Good repeat", "MUSIC_PREFERENCE_SINGLE", "REGO", 65,
                                R.drawable.ic_favourite_goodrepeat));
                tAL_CustomSpinnerItem.add(
                        new CustomSpinnerItem("Okay", "MUSIC_PREFERENCE_SINGLE", "OKAY", 60,
                                R.drawable.ic_favourite_okay));
                tAL_CustomSpinnerItem.add(
                        new CustomSpinnerItem("Good", "MUSIC_PREFERENCE_SINGLE", "GOOD", 70,
                                R.drawable.ic_favourite_good));
                tAL_CustomSpinnerItem.add(
                        new CustomSpinnerItem("Very Good", "MUSIC_PREFERENCE_SINGLE", "VYGO", 80,
                                R.drawable.ic_favourite_verygood));
                tAL_CustomSpinnerItem.add(
                        new CustomSpinnerItem("Today", "MUSIC_PREFERENCE_SINGLE", "TODA", 100,
                                R.drawable.ic_favourite_today));

                break;
            //</editor-fold>
            //<editor-fold desc="SHAPE">
            case  FIELD_SHAPE:
                if (iSearch) {
                    tAL_CustomSpinnerItem.add(
                            new CustomSpinnerItem("All", "SALL", "", 50, 0));
                    tAL_CustomSpinnerItem.add(
                            new CustomSpinnerItem("Any longwise", "SHAPE_ANY_LONG", "", 100,
                                    0));
                }

                tAL_CustomSpinnerItem.add(new CustomSpinnerItem("Longwise - 3", "SHAPE_SINGLE", "L3", 110, 0));
                tAL_CustomSpinnerItem.add(new CustomSpinnerItem("Longwise - 4", "SHAPE_SINGLE", "L4", 120, 0));
                tAL_CustomSpinnerItem.add(new CustomSpinnerItem("Longwise - 5", "SHAPE_SINGLE", "L5", 140, 0));
                tAL_CustomSpinnerItem.add(new CustomSpinnerItem("Longwise - 2", "SHAPE_SINGLE", "L2", 162, 0));

                tAL_CustomSpinnerItem.add(new CustomSpinnerItem("Longwise - 6", "SHAPE_SINGLE", "L6", 166, 0));
                tAL_CustomSpinnerItem.add(new CustomSpinnerItem("Longwise - 7", "SHAPE_SINGLE", "L7", 167, 0));
                tAL_CustomSpinnerItem.add(new CustomSpinnerItem("Longwise - 8", "SHAPE_SINGLE", "L8", 168, 0));
                tAL_CustomSpinnerItem.add(new CustomSpinnerItem("Longwise - any", "SHAPE_SINGLE", "L-any", 190, 0));
                tAL_CustomSpinnerItem.add(new CustomSpinnerItem("Square", "SHAPE_SINGLE", "Square", 400, 0));
                tAL_CustomSpinnerItem.add(new CustomSpinnerItem("Longwise & Square", "SHAPE_SINGLE", "L&S", 401, 0));
                tAL_CustomSpinnerItem.add(new CustomSpinnerItem("Triangular", "SHAPE_SINGLE", "Tri", 430, 0));
                tAL_CustomSpinnerItem.add(new CustomSpinnerItem("Hexagonal", "SHAPE_SINGLE", "Hex", 460, 0));
                tAL_CustomSpinnerItem.add(new CustomSpinnerItem("Pentagonal", "SHAPE_SINGLE", "Pent", 450, 0));
                tAL_CustomSpinnerItem.add(new CustomSpinnerItem("Round the room", "SHAPE_SINGLE", "RtR", 600, 0));
                tAL_CustomSpinnerItem.add(new CustomSpinnerItem("Lines", "SHAPE_SINGLE", "ln", 610, 0));
                tAL_CustomSpinnerItem.add(new CustomSpinnerItem("Circle", "SHAPE_SINGLE", "Circle", 650, 0));
                tAL_CustomSpinnerItem.add(new CustomSpinnerItem("Other ?", "SHAPE_SINGLE", "Other ?", 800, 0));
                tAL_CustomSpinnerItem.add(new CustomSpinnerItem("Other", "SHAPE_SINGLE", "Other", 810, 0));
                tAL_CustomSpinnerItem.add(new CustomSpinnerItem("Unknown", "SHAPE_SINGLE", "?", 820, 0));


                break;
            //</editor-fold>
            //<editor-fold desc="PAIRING_STATUS">
            case FIELD_PAIRING_STATUS:
                if (iSearch) {
                    tAL_CustomSpinnerItem.add(
                            new CustomSpinnerItem("All", "SALL", "", 50, R.drawable.ic_asterix));
                }
                tAL_CustomSpinnerItem.add(new CustomSpinnerItem(
                        "Confirmed", "PAIRING_STATUS_SINGLE", "CONFIRMED", 100,
                        R.drawable.ic_link_confirmed));
                tAL_CustomSpinnerItem.add(new CustomSpinnerItem(
                        "Candidate", "PAIRING_STATUS_SINGLE", "CANDIDATE", 50,
                        R.drawable.ic_link_candidate));
                tAL_CustomSpinnerItem.add(new CustomSpinnerItem(
                        "Rejected", "PAIRING_STATUS_SINGLE", "REJECTED", 30,
                        R.drawable.ic_link_rejected));
                break;
            //</editor-fold>

            //<editor-fold desc="DANCELIST_PURPOSE">
//            case FIELD_PLAYLIST_PURPOSE:
//                if (iSearch) {
//                    tAL_CustomSpinnerItem.add(
//                            new CustomSpinnerItem("All", "SALL", "", 50, R.drawable.ic_asterix));
//                }
//                tAL_CustomSpinnerItem.add(new CustomSpinnerItem(
//                        "Unknown", "DANCELIST_PURPOSE_SINGLE", "UNDEFINED",
//                        0, R.drawable.ic_listpurpose_unkown));
//                tAL_CustomSpinnerItem.add(new CustomSpinnerItem(
//                        "Theme", "DANCELIST_PURPOSE_SINGLE", "THEME",
//                        2, R.drawable.ic_listpurpose_theme));
//                tAL_CustomSpinnerItem.add(new CustomSpinnerItem(
//                        "Event", "DANCELIST_PURPOSE_SINGLE", "EVENT",
//                        3, R.drawable.ic_listpurpose_event));
//                break;
            //</editor-fold>
            //<editor-fold desc="DANCELIST_PURPOSE">
            case FIELD_REQUESTLIST_PURPOSE:
                if (iSearch) {
                    tAL_CustomSpinnerItem.add(
                            new CustomSpinnerItem("All", "SALL", "", 50, R.drawable.ic_asterix));
                }
                tAL_CustomSpinnerItem.add(new CustomSpinnerItem(
                        "Unknown", "REQUESTLIST_PURPOSE_SINGLE", "UNDEFINED",
                        0, R.drawable.ic_listpurpose_unkown));
                tAL_CustomSpinnerItem.add(new CustomSpinnerItem(
                        "Theme", "REQUESTLIST_PURPOSE_SINGLE", "THEME",
                        2, R.drawable.ic_listpurpose_theme));
                tAL_CustomSpinnerItem.add(new CustomSpinnerItem(
                        "Event", "REQUESTLIST_PURPOSE_SINGLE", "EVENT",
                        3, R.drawable.ic_listpurpose_event));
                break;
            //</editor-fold>
            //<editor-fold desc="MUSICDIRECTORY_PURPOSE">
            case FIELD_MUSICDIRECTORY_PURPOSE:

                if (iSearch) {
                    tAL_CustomSpinnerItem.add(
                            new CustomSpinnerItem("All", "SALL", "", 0, R.drawable.ic_asterix));
                }
                tAL_CustomSpinnerItem.add(new CustomSpinnerItem(
                        "SCD", "MUSICDIRECTORY_PURPOSE_SINGLE", "SCD",
                        11, R.drawable.ic_purpose_scd));
                tAL_CustomSpinnerItem.add(new CustomSpinnerItem(
                        "Warmup & Cooldown", "MUSICDIRECTORY_PURPOSE_SINGLE", "WUCD",
                        13, R.drawable.ic_purpose_warmup));
                tAL_CustomSpinnerItem.add(new CustomSpinnerItem(
                        "Step Practise", "MUSICDIRECTORY_PURPOSE_SINGLE", "STEP",
                        16, R.drawable.ic_purpose_step));
                tAL_CustomSpinnerItem.add(new CustomSpinnerItem(
                        "Mixed Class Purpose", "MUSICDIRECTORY_PURPOSE_SINGLE", "CLMX",
                        19, R.drawable.ic_purpose_class));
                tAL_CustomSpinnerItem.add(new CustomSpinnerItem(
                        "Celtic_Listening", "MUSICDIRECTORY_PURPOSE_SINGLE", "CELC",
                        26, R.drawable.ic_purpose_celtic));
                tAL_CustomSpinnerItem.add(new CustomSpinnerItem(
                        "Listening", "MUSICDIRECTORY_PURPOSE_SINGLE", "LISG",
                        49, R.drawable.ic_purpose_world));
                tAL_CustomSpinnerItem.add(new CustomSpinnerItem(
                        "Unkown", "MUSICDIRECTORY_PURPOSE_SINGLE", "UNKN",
                        90, R.drawable.ic_favourite_unknown));
                break;

            //</editor-fold>
            //<editor-fold desc="MUSICFILE_PURPOSE">

            case FIELD_MUSICFILE_PURPOSE:
                if (iSearch) {
                    tAL_CustomSpinnerItem.add(
                            new CustomSpinnerItem("All", "SALL", "", 0, R.drawable.ic_asterix));
                }
                tAL_CustomSpinnerItem.add(new CustomSpinnerItem(
                        "SCD", "MUSICFILE_PURPOSE_SINGLE", "SCD",
                        11, R.drawable.ic_purpose_scd));
                tAL_CustomSpinnerItem.add(new CustomSpinnerItem(
                        "Warmup & Cooldown", "MUSICFILE_PURPOSE_SINGLE", "WUCD",
                        13, R.drawable.ic_purpose_warmup));
                tAL_CustomSpinnerItem.add(new CustomSpinnerItem(
                        "Step Practise", "MUSICFILE_PURPOSE_SINGLE", "STEP",
                        16, R.drawable.ic_purpose_step));
                tAL_CustomSpinnerItem.add(new CustomSpinnerItem(
                        "Mixed Class Purpose", "MUSICFILE_PURPOSE_SINGLE", "CLMX",
                        19, R.drawable.ic_purpose_class));
                tAL_CustomSpinnerItem.add(new CustomSpinnerItem(
                        "Celtic_Listening", "MUSICFILE_PURPOSE_SINGLE", "CELC",
                        26, R.drawable.ic_purpose_celtic));
                tAL_CustomSpinnerItem.add(new CustomSpinnerItem(
                        "Listening", "MUSICFILE_PURPOSE_SINGLE", "LISG",
                        49, R.drawable.ic_purpose_world));
                tAL_CustomSpinnerItem.add(new CustomSpinnerItem(
                        "Unkown", "MUSICFILE_PURPOSE_SINGLE", "UNKN",
                        90, R.drawable.ic_favourite_unknown));
                break;
            //</editor-fold>

            //<editor-fold desc="MUSICFILE_SIGNATURE">

            case FIELD_MUSICFILE_SIGNATURE:
                if (iSearch) {
                    tAL_CustomSpinnerItem.add(
                            new CustomSpinnerItem("All", "SALL", "", 0, R.drawable.ic_asterix));
                }


                tAL_CustomSpinnerItem.add(new CustomSpinnerItem(
                        "EMPTY", "MUSICFILE_SIGNATURE_EMPTY", "",
                        5, 0));

                tAL_CustomSpinnerItem.add(new CustomSpinnerItem(
                        "R8x32", "MUSICFILE_SIGNATURE_SINGLE", "R8x32",
                        11, 0));
                tAL_CustomSpinnerItem.add(new CustomSpinnerItem(
                        "J8x32", "MUSICFILE_SIGNATURE_SINGLE", "J8x32",
                        12, 0));
                tAL_CustomSpinnerItem.add(new CustomSpinnerItem(
                        "S8x32", "MUSICFILE_SIGNATURE_SINGLE", "S8x32",
                        13, 0));

                tAL_CustomSpinnerItem.add(new CustomSpinnerItem(
                        "R ", "MUSICFILE_SIGNATURE_RHYTHM", "R",
                        21, 0));
                tAL_CustomSpinnerItem.add(new CustomSpinnerItem(
                        "J ", "MUSICFILE_SIGNATURE_RHYTHM", "J",
                        22, 0));
                tAL_CustomSpinnerItem.add(new CustomSpinnerItem(
                        "S ", "MUSICFILE_SIGNATURE_RHYTHM", "S",
                        23, 0));





                break;
            //</editor-fold>
            default:
                throw new RuntimeException(iField + " not covered by SpinnerItemFactory");
        }
        return tAL_CustomSpinnerItem;
    }
}
