package org.pochette.organizer.chained_search;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.pochette.data_library.scddb_objects.Dance;
import org.pochette.organizer.R;
import org.pochette.organizer.formation.DialogFragment_ChooseFormation;
import org.pochette.organizer.formation.FormationSearch;
import org.pochette.organizer.gui.UserChoice;
import org.pochette.organizer.gui_assist.CustomSpinnerAdapter;
import org.pochette.organizer.gui_assist.CustomSpinnerItem;
import org.pochette.organizer.gui_assist.SpinnerItemFactory;
import org.pochette.utils_lib.logg.Logg;
import org.pochette.utils_lib.shouting.Shout;
import org.pochette.utils_lib.shouting.Shouting;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import static org.pochette.organizer.chained_search.Matryoshka.NODE_SEARCH;


/**
 * View part of MCV
 */
@SuppressWarnings({"FieldCanBeLocal", "unused"})
class Matryoshka_View extends LinearLayout implements Shouting {

    private static final String TAG = "FEHA (MYV)";

    private static final int SIZE_SHOW_ALL = 1;
    private static final int SIZE_SHOW_ONELINE = 2;
//    private static final int SIZE_SHOW_TINY = 3;
//    private static final int SIZE_SHOW_GONE = 4;

    public static final int VARIANT_SUPER = 1; // one line, for and, or , not and all
    public static final int VARIANT_SEARCH_VALUE = 2; // two lines, search with EditText
    public static final int VARIANT_SEARCH_BOOLEAN = 3; // two lines,
    public static final int VARIANT_SEARCH_SPINNER = 4; // two lines, with spinner
    public static final int VARIANT_SEARCH_FORMATION = 5; // two lines,


    //   CLTV_DragListener mCLTV_DragListener;
    // variables
    private int mDisplayVariant;
    private int mSizeMode;
    private int mModeHover;
    private final int mDelay = 15;
//    private String mDescription;

    private TextView mTV_Id;
    private TextView mTV_Key;
    private TextView mTV_Count;
    //private TextView mTV_Status;
    private TextView mTV_SearchMethod;
    private TextView mTV_FormationValue;
    private EditText mET_Value;
    private Spinner mSP_SpinnerValue;
    private ImageView mIV_Plus;

    private Matryoshka_DragListener mMatryoshka_DragListener;

    private LinearLayout mLL_SecondLine;

    SpinnerItemFactory mSpinnerItemFactory;
    ArrayList<CustomSpinnerItem> mAL_CSI;
    CustomSpinnerAdapter mCSA_Spinner;

    private int mRimSize;
    private int mFontSize;
    private int mTextBoxHeight;

    private int mNodeType;
    private int mCount;
    private String mTextOption;
    private String mTextMethod;
    private String mTextValue;
    @SuppressWarnings("FieldCanBeLocal")
//    private String mKey;
    private FormationSearch mFormationSearch;
    private int mHashCodeOfMatryochka; // hash code of Matryochka
    private boolean mUpToDefinition;


    // Paint LinePaint;
    private Timer mTimer;
    Shouting mShouting;
    Shout mGlassFloor;


    // constructor
    public Matryoshka_View(Context context) {
        super(context);
    }

    public Matryoshka_View(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public Matryoshka_View(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    // lifecylce and override

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        initOnAttached();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int tTotHeight;
        tTotHeight = getLoopHeight(widthMeasureSpec, heightMeasureSpec);
        int tWidthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthNewMeasureSpec = MeasureSpec.makeMeasureSpec(tWidthSize, MeasureSpec.AT_MOST);
        int heightNewMeasureSpec = MeasureSpec.makeMeasureSpec(tTotHeight, MeasureSpec.EXACTLY);
        super.onMeasure(widthNewMeasureSpec, heightNewMeasureSpec);
    }


// setter and getter

    public void setDisplayVariant(int iDisplayVariant) {
        mDisplayVariant = iDisplayVariant;
        //Logg.i(TAG, "set variant " + mDisplayVariant);
    }

    public void setShouting(Shouting shouting) {
        mShouting = shouting;
        if (mMatryoshka_DragListener != null) {
            mMatryoshka_DragListener.setShouting(mShouting);
        }
    }

    public void setNodeType(int nodeType) {
        mNodeType = nodeType;
        //Logg.i(TAG, "setNodeType");
        refresh();
    }

    public void setCount(int count) {
        mCount = count;
    }

    public void setTextOption(String textOption) {
        mTextOption = textOption;
    }

    public void setTextMethod(String textMethod) {
        mTextMethod = textMethod;
    }

    public void setTextValue(String textValue) {
        //Logg.i(TAG, "setTextVlalue");
        mTextValue = textValue;
        refresh();
    }

    public void setUpToDefinition(boolean upToDefinition) {
        mUpToDefinition = upToDefinition;
    }

    public void setHashCodeOfMatryochka(int iHashCodeOfMatryochka) {
        mHashCodeOfMatryochka = iHashCodeOfMatryochka;
        if (mMatryoshka_DragListener != null) {
            mMatryoshka_DragListener.setHashCode(iHashCodeOfMatryochka);
        }
    }

    public int getHashCodeOfMatryochka() {
        return mHashCodeOfMatryochka;
    }

    public void setFormationSearch(FormationSearch formationSearch) {
        mFormationSearch = formationSearch;
        refresh();
    }

    // internal

    int getLoopHeight(int widthMeasureSpec, int heightMeasureSpec) {
        int tTotHeight;
        tTotHeight = 0;
        for (int l = 0; l < this.getChildCount(); l++) {
            View lView = this.getChildAt(l);
            lView.measure(widthMeasureSpec, heightMeasureSpec);
            int tHeight = lView.getMeasuredHeight();
            tTotHeight += tHeight;
            if (mDisplayVariant == VARIANT_SUPER) {
                LayoutParams lLayoutParams = (LayoutParams) lView.getLayoutParams();
                tTotHeight += lLayoutParams.topMargin + lLayoutParams.bottomMargin;
            }
        }
        return tTotHeight;
    }

    void initOnAttached() {
        mSpinnerItemFactory = new SpinnerItemFactory();
        this.setId(View.generateViewId());
        mRimSize = 5;
        mFontSize = this.getContext().getResources().getDimensionPixelSize(R.dimen.Detail_textview_font_size);
        mTextBoxHeight = this.getContext().getResources().getDimensionPixelSize(
                R.dimen.Detail_row_height) * 2;

        mModeHover = 0;
        this.setOrientation(LinearLayout.VERTICAL);
        mSizeMode = SIZE_SHOW_ALL;


        initFirstLine();
        initSecondLine();

        GradientDrawable border = new GradientDrawable();
        border.setColor(0xFFFFFFFF); //white background
        border.setStroke(1, 0xFF000000); //black border with full opacity
        this.setBackground(border);

        setDragAndDropListener();
    }

    void setDragAndDropListener() {
        this.setOnLongClickListener(new OnLongClickListener() {
            public boolean onLongClick(View iView) {
                Logg.k(TAG, "LongClick ");
                excuteLongClickAsStartDrag();
                return true;
            }
        });


        mMatryoshka_DragListener = new Matryoshka_DragListener(mHashCodeOfMatryochka, mShouting);
        this.setOnDragListener(mMatryoshka_DragListener);
    }


    void excuteLongClickAsStartDrag() {

//            ClipData.Item item = new ClipData.Item((CharSequence) this.getTag());
//            String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};
//            String tText = "123";
//            ClipData data = new ClipData(tText, mimeTypes, item);
        DragShadowBuilder dragshadow = new DragShadowBuilder(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //Logg.i(TAG, "callStartDragAndDrop");
            this.startDragAndDrop(null      // data to be dragged
                    , dragshadow  // drag shadow
                    , this            // local data about the drag and drop operation
                    , 0          // flags set to 0 because not using currently);
            );
        }
    }


    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    void initFirstLine() {
        LinearLayout LL_FirstLine = new LinearLayout(this.getContext());
        LL_FirstLine.setId(View.generateViewId());
        LL_FirstLine.setOrientation(HORIZONTAL);

        LayoutParams tLayoutParams = new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        this.addView(LL_FirstLine, tLayoutParams);

        // Text view
        mTV_Id = new TextView(this.getContext());
        LL_FirstLine.addView(mTV_Id);
        mTV_Id.setId(View.generateViewId());
        mTV_Id.setLayoutParams(new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));
        mTV_Id.setPadding(mRimSize, mRimSize, mRimSize, mRimSize);
        mTV_Id.setHeight(mTextBoxHeight);
        mTV_Id.setTextSize(mFontSize);
        mTV_Id.setTextColor(getResources().getColor(R.color.scream_black, null));

        //
        mTV_Key = new TextView(this.getContext());
        LL_FirstLine.addView(mTV_Key);
        mTV_Key.setId(View.generateViewId());
        mTV_Key.setLayoutParams(new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));

        mTV_Key.setPadding(mRimSize, mRimSize, mRimSize, mRimSize);
        mTV_Key.setHeight(mTextBoxHeight);
        mTV_Key.setTextSize(mFontSize);
        mTV_Key.setTextColor(getResources().getColor(R.color.scream_black, null));

        // Status
//        mTV_Status = new TextView(this.getContext());
//        mLL_FirstLine.addView(mTV_Status);
//        mTV_Status.setId(View.generateViewId());
//        mTV_Status.setLayoutParams(new LayoutParams(
//                LayoutParams.WRAP_CONTENT,
//                LayoutParams.WRAP_CONTENT));
//
//        mTV_Status.setPadding(mRimSize, mRimSize, mRimSize, mRimSize);
//        mTV_Status.setHeight(mTextBoxHeight);
//        mTV_Status.setTextSize(mFontSize);

        // Count
        mTV_Count = new TextView(this.getContext());
        LL_FirstLine.addView(mTV_Count);
        mTV_Count.setId(View.generateViewId());

        mTV_Count.setLayoutParams(new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));

        mTV_Count.setPadding(mRimSize, mRimSize, mRimSize, mRimSize);
        mTV_Count.setHeight(mTextBoxHeight);
        mTV_Count.setTextSize(mFontSize);

        // Icon
        mIV_Plus = new ImageView(this.getContext());
        mIV_Plus.setImageResource(R.drawable.ic_addnew);
        mIV_Plus.setId(View.generateViewId());
        int tIconSize = 60;
        mIV_Plus.setLayoutParams(new LayoutParams(tIconSize, tIconSize));

        mIV_Plus.setPadding(mRimSize, mRimSize, mRimSize, mRimSize);
        mIV_Plus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Logg.k(TAG, "onClick");
                addNewMatryoshka();
            }
        });
        LL_FirstLine.addView(mIV_Plus);
        if (mSizeMode == SIZE_SHOW_ALL || mSizeMode == SIZE_SHOW_ONELINE) {
            LL_FirstLine.setVisibility(VISIBLE);
        } else {
            LL_FirstLine.setVisibility(GONE);
        }
        refreshFirstLine();

    }


    void initSecondLine() {
        if (mNodeType != NODE_SEARCH) {
            if (mLL_SecondLine != null) {
                mLL_SecondLine.setVisibility(GONE);
            }
            return;
        }
        if (mLL_SecondLine != null) {
            mLL_SecondLine.setVisibility(VISIBLE);
            return;
        }


        //Logg.i(TAG, "SecondeLine");

        mLL_SecondLine = new LinearLayout(this.getContext());
        mLL_SecondLine.setId(View.generateViewId());
        mLL_SecondLine.setVisibility(VISIBLE);
        mLL_SecondLine.setOrientation(HORIZONTAL);
        mLL_SecondLine.setLayoutParams(new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mLL_SecondLine.setBaselineAligned(true);
        LayoutParams tLayoutParams = new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        tLayoutParams.setMargins(mRimSize, mRimSize, mRimSize, mRimSize);
        this.addView(mLL_SecondLine, tLayoutParams);

        // Text vie
        mTV_SearchMethod = new TextView(this.getContext());
        mLL_SecondLine.addView(mTV_SearchMethod);
        mTV_SearchMethod.setId(View.generateViewId());
        mTV_SearchMethod.setLayoutParams(new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));

        mTV_SearchMethod.setPadding(mRimSize, mRimSize, mRimSize, mRimSize);
        mTV_SearchMethod.setHeight(mTextBoxHeight);
        mTV_SearchMethod.setTextSize(mFontSize);
        mTV_SearchMethod.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
        mTV_SearchMethod.setTextColor(getResources().getColor(R.color.scream_black, null));
        mTV_SearchMethod.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Logg.k(TAG, "onClick SearchOption");
                processMethodClick();
            }
        });

        // EditText
        mET_Value = new EditText(this.getContext());
        mLL_SecondLine.addView(mET_Value);
        mET_Value.setId(View.generateViewId());
        mET_Value.setHeight(mTextBoxHeight);
        mET_Value.setTextSize(mFontSize);
        mET_Value.setPadding(mRimSize, mRimSize, mRimSize, mRimSize);
        mET_Value.setTextColor(getResources().getColor(R.color.scream_black, null));
        mET_Value.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                processValueTextChanged(s);
            }
        });


        // EditText
        mSP_SpinnerValue = new Spinner(this.getContext());
        mLL_SecondLine.addView(mSP_SpinnerValue);
        mSP_SpinnerValue.setId(View.generateViewId());
        mSP_SpinnerValue.setPadding(mRimSize, mRimSize, mRimSize, mRimSize);
        int tWidth;
        int tHeight;
        tHeight = getResources().getDimensionPixelSize(R.dimen.Head_icon_size);
        tWidth = getResources().getDimensionPixelSize(R.dimen.RV_basic_width_02);
        mSP_SpinnerValue.setLayoutParams(new LayoutParams(tWidth, tHeight));
        mSP_SpinnerValue.setDropDownWidth(tWidth);
        mAL_CSI = mSpinnerItemFactory.getSpinnerItems(SpinnerItemFactory.FIELD_RHYTHM, false);
        mCSA_Spinner =
                new CustomSpinnerAdapter(this.getContext(), mAL_CSI);
        mCSA_Spinner.setTitleMode(CustomSpinnerAdapter.MODE_ICON_TEXT);
        mCSA_Spinner.setDropdownMode(CustomSpinnerAdapter.MODE_ICON_TEXT);
        mSP_SpinnerValue.setAdapter(mCSA_Spinner);
        mSP_SpinnerValue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent,
                                       View view, int position, long id) {
                Logg.k(TAG, "SP_Favourite: " +
                        parent.getItemAtPosition(position).toString());
                CustomSpinnerItem tCustomSpinnerItem = (CustomSpinnerItem) mCSA_Spinner.getItem(position);
                //Logg.i(TAG, tCustomSpinnerItem.toString());
                processValueSpinnerChanged(tCustomSpinnerItem);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Text view
        mTV_FormationValue = new TextView(this.getContext());
        mLL_SecondLine.addView(mTV_FormationValue);
        mTV_FormationValue.setId(View.generateViewId());
        mTV_FormationValue.setTextColor(getResources().getColor(R.color.scream_black, null));
        mTV_FormationValue.setLayoutParams(new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));
        mTV_FormationValue.setPadding(mRimSize, mRimSize, mRimSize, mRimSize);
        mTV_FormationValue.setHeight(mTextBoxHeight);
        mTV_FormationValue.setTextSize(mFontSize);
        mTV_FormationValue.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Logg.k(TAG, "onClick FormationValue");
                editFormationSearch();
            }
        });
        mTV_FormationValue.setVisibility(GONE);

        //
        refreshSecondLine();
    }

    @SuppressLint("SetTextI18n")
    void refreshFirstLine() {
        if (mTV_Id != null) {
            mTV_Id.setText(mTextOption);
        }
//        if (mTV_Status != null) {
//            if (mUpToDefinition) {
//                mTV_Status.setText("OK");
//                mTV_Status.setTextColor(getResources().getColor(R.color.scream_black, null));
//            } else {
//                mTV_Status.setText("not OK");
//                mTV_Status.setTextColor(getResources().getColor(R.color.scream_red, null));
//            }
//        }
        if (mTV_Count != null) {
            mTV_Count.setText("(" + mCount + ")");
            if (mUpToDefinition) {
                mTV_Count.setTextColor(getResources().getColor(R.color.scream_black, null));
            } else {
                mTV_Count.setTextColor(getResources().getColor(R.color.scream_red, null));
            }
        }
        if (mIV_Plus != null) {

            if (mDisplayVariant == VARIANT_SUPER) {
                mIV_Plus.setVisibility(VISIBLE);
            } else {
                mIV_Plus.setVisibility(GONE);
            }
        }
    }

    @SuppressLint("SetTextI18n")
    void refreshSecondLine() {
        if (mTV_SearchMethod != null && mDisplayVariant != VARIANT_SUPER) {
//            if (!mTV_SearchMethod.getText().equals(mTextMethod)) {
//                // only when the method changes update the edit text, to avoid inifinite loops
//
//            }
            mTV_SearchMethod.setText(mTextMethod);
        }
        if (mET_Value != null) {
            if (mDisplayVariant != VARIANT_SEARCH_VALUE) {
                mET_Value.setVisibility(GONE);
            } else {
                mET_Value.setVisibility(VISIBLE);
                mET_Value.setMinimumWidth(90);
                String tTextOfView = mET_Value.getText().toString();
                if (!(mTextValue.equals(tTextOfView))) {
//                    Logg.i(TAG, "Vari|" + mTextValue + "|");
//                    Logg.i(TAG, "View|" + tTextOfView + "|");
//                    Logg.i(TAG, "Equal" + mTextValue.equals(tTextOfView));
//                    Logg.i(TAG, "Call setText 489, ID: " + Thread.currentThread().getId());
                    mET_Value.setText(mTextValue);
                }
            }
        }
        if (mSP_SpinnerValue != null) {
            if (mDisplayVariant != VARIANT_SEARCH_SPINNER) {
                mSP_SpinnerValue.setVisibility(GONE);
            } else {
                mSP_SpinnerValue.setVisibility(VISIBLE);
            }
        }

        if (mTV_FormationValue != null) {
            if (mDisplayVariant == VARIANT_SEARCH_FORMATION) {
                mTV_FormationValue.setVisibility(VISIBLE);
                if (mFormationSearch != null) {
                    mTV_FormationValue.setText(mFormationSearch.getDisplayText());
                }
            } else {
                mTV_FormationValue.setVisibility(GONE);
            }
        }
    }

    @SuppressLint("SetTextI18n")
    void refresh() {
        //Logg.i(TAG, "hashCode: " + mHashCodeOfMatryochka);
        //Logg.i(TAG, "Method" + mTextMethod);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                try {
                    refreshFirstLine();
                    if (mNodeType == NODE_SEARCH && mLL_SecondLine == null) {
                        initSecondLine();
                    }
                    refreshSecondLine();
                    applyColors();
                } catch(Exception e) {
                    Logg.w(TAG, e.toString());
                }
            }
        });
    }

    void onDragExit() {
        Logg.i(TAG, "Exit " + mHashCodeOfMatryochka);
        mModeHover = 0;
        applyColors();
        delayHoverReset();
    }

    void onDragEnter() {
        //Logg.i(TAG, "Enter " + mHashCodeOfMatryochka);
        mModeHover = 1;
        applyColors();
        delayHoverReset();
    }

    void onDropped() {
        //Logg.i(TAG, "Drop " + mHashCodeOfMatryochka);
        mModeHover = 2;
        applyColors();
        delayHoverReset();
    }

    void applyColors() {
        // Border
        int tStrokeColor;
        int tColor;
        switch (mModeHover) {
            default:
            case 0: // normal
                tStrokeColor = getResources().getColor(R.color.scream_black, null);
                break;
            case 1: // hovering
                if (mDisplayVariant == VARIANT_SUPER) {
                    tStrokeColor = getResources().getColor(R.color.scream_green, null);
                } else {
                    tStrokeColor = getResources().getColor(R.color.scream_red, null);
                }
                break;
            case 2: //dropped
                tStrokeColor = getResources().getColor(R.color.scream_cyan, null);
                break;
        }
        tColor = getResources().getColor(R.color.scream_transparent, null);
        GradientDrawable tGradientDrawable = new GradientDrawable();
        tGradientDrawable.setColor(tColor); // Changes this drawbale to use a single color instead of a gradient
        tGradientDrawable.setCornerRadius(5);
        tGradientDrawable.setStroke(5, tStrokeColor);
        this.setBackground(tGradientDrawable);
    }


    void editFormationSearch() {
        startFormationChoice(mFormationSearch);
    }

    void processMethodClick() {
        UserChoice tUserChoice = new UserChoice("Choose");
        for (SearchOption lSearchOption : SearchOption.getSearchOptions(Dance.class)) {
            tUserChoice.addQuestion(lSearchOption.mCode, lSearchOption.mDisplayText);
        }
        String tResult = tUserChoice.poseQuestion(this.getContext());
        if (tResult == null || tResult.isEmpty()) {
            return;
        }
        if (tResult.equals("FORMATION")) {
            startFormationChoice(null);
        }
        if (mShouting != null) {
            Shout tShout = new Shout(this.getClass().getSimpleName());
            tShout.mLastObject = "MethodCode";
            tShout.mLastAction = "choosen";
            try {
                JSONObject tJsonObject = new JSONObject();
                tJsonObject.put("SearchOptionCode", tResult);
                tJsonObject.put("hashCode", mHashCodeOfMatryochka);
                tShout.mJsonString = tJsonObject.toString();
            } catch(JSONException e) {
                Logg.w(TAG, e.toString());
            }
            mShouting.shoutUp(tShout);
        }
    }

    void processValueSpinnerChanged(CustomSpinnerItem iCustomSpinnerItem) {
        String tKey = iCustomSpinnerItem.mKey;
        String tMethod = iCustomSpinnerItem.mMethod;

        if (mTimer != null) {
            mTimer.cancel();
        }
        Shout tShout = null;
        try {
            if (tKey != null) {
                tShout = new Shout(this.getClass().getSimpleName());
                tShout.mLastObject = "Value";
                tShout.mLastAction = "edited";
                JSONObject tJsonObject = new JSONObject();
                tJsonObject.put("Value", tKey);
                tJsonObject.put("MethodCode", tMethod);
                tJsonObject.put("hashCode", mHashCodeOfMatryochka);
                tShout.mJsonString = tJsonObject.toString();
            }
        } catch(JSONException e) {
            Logg.w(TAG, e.toString());
        }
        mTimer = new Timer("SD VM Search" + mHashCodeOfMatryochka);
        final Shout fShout = tShout;
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (mShouting != null && fShout != null) {
                    mShouting.shoutUp(fShout);
                }
            }
        }, mDelay);
    }

    void processValueTextChanged(Editable iEditable) {
        // void processValueTextChanged(CharSequence iEditable) {
        String tString = iEditable.toString();
        if (mTimer != null) {
            mTimer.cancel();
        }
        Shout tShout = null;
        try {
            if (tString != null) {
                tShout = new Shout(this.getClass().getSimpleName());
                tShout.mLastObject = "Value";
                tShout.mLastAction = "edited";
                JSONObject tJsonObject = new JSONObject();
                tJsonObject.put("Value", tString);
                tJsonObject.put("hashCode", mHashCodeOfMatryochka);
                tShout.mJsonString = tJsonObject.toString();
            }
        } catch(JSONException e) {
            Logg.w(TAG, e.toString());
        }
        mTimer = new Timer("SD VM Search" + mHashCodeOfMatryochka);
        final Shout fShout = tShout;
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (mShouting != null && fShout != null) {
                    mShouting.shoutUp(fShout);
                }
            }
        }, mDelay);
    }

    void startFormationChoice(FormationSearch iFormationSearch) {
        DialogFragment_ChooseFormation tDialogFragment_chooseFormation =
                DialogFragment_ChooseFormation.newInstance(iFormationSearch);
        tDialogFragment_chooseFormation.setShouting(this);
        FragmentManager fm = ((FragmentActivity) Objects.requireNonNull(mET_Value.getContext())).getSupportFragmentManager();
        tDialogFragment_chooseFormation.show(fm, null);
    }


    //<editor-fold desc="drag and drop">


    void delayHoverReset() {
        mTimer = new Timer("MYV " + mHashCodeOfMatryochka);
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mModeHover = 0;
                refresh();
            }
        }, 500);
    }


    //</editor-fold>


    void process_shouting() {
        if (mGlassFloor.mActor.equals("DialogFragment_ChooseFormation")) {
            if (mGlassFloor.mLastAction.equals("selected") &&
                    mGlassFloor.mLastObject.equals("TreeSetOfId")) {
                if (mShouting != null) {
                    Shout tShout;
                    tShout = new Shout(this.getClass().getSimpleName());
                    tShout.mLastObject = "Value";
                    tShout.mLastAction = "edited";
                    try {
                        JSONObject tJsonObject = new JSONObject();
                        tJsonObject.put("Value", mGlassFloor.mJsonString);
                        tJsonObject.put("hashCode", mHashCodeOfMatryochka);
                        tShout.mJsonString = tJsonObject.toString();
                    } catch(JSONException e) {
                        Logg.w(TAG, e.toString());
                    }
                    Logg.w(TAG, "shout up");
                    mShouting.shoutUp(tShout);
                }
            }
        }
    }

    // public methods

    void addNewMatryoshka() {
        if (mShouting != null) {
            Shout tShout;
            tShout = new Shout(this.getClass().getSimpleName());
            tShout.mLastObject = "AdditionOfMatryoshka";
            tShout.mLastAction = "requested";
            try {
                JSONObject tJsonObject = new JSONObject();
                tJsonObject.put("hashCode", mHashCodeOfMatryochka);
                tShout.mJsonString = tJsonObject.toString();
            } catch(JSONException e) {
                Logg.w(TAG, e.toString());
            }
            mShouting.shoutUp(tShout);
        }
    }


    public void removeRecursive() {
        for (int i= 0; i < this.getChildCount(); i++) {
            View lView = this.getChildAt(i);
            if (lView instanceof Matryoshka_View) {
                ((Matryoshka_View) lView).removeRecursive();
                this.removeView(lView);
            }
        }
    }

    @Override
    public void shoutUp(Shout tShoutToCeiling) {
        Logg.i(TAG, tShoutToCeiling.toString());
        mGlassFloor = tShoutToCeiling;
        process_shouting();
    }
}
