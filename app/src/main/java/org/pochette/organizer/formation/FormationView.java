package org.pochette.organizer.formation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.pochette.organizer.R;
import org.pochette.utils_lib.logg.Logg;
import org.pochette.utils_lib.shouting.Shout;
import org.pochette.utils_lib.shouting.Shouting;

import java.util.Locale;

import androidx.annotation.Nullable;

@SuppressWarnings("unused")
class FormationView extends LinearLayout implements Shouting {

    private static final String TAG = "FEHA (FormationView)";
    // variables
    @SuppressWarnings("unused")
    private TextView mTV_Key;
    private TextView mTV_Count;
    private TextView mTV_Text;

    private int mCount;
    private String mText;
    private String mKey;

   // private boolean mIsSelected;

    private Shouting mShouting;


    // constructor
    public FormationView(Context context) {
        super(context);
        initViews();
        //       init();
    }


    public FormationView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initViews();
        ///       init();
    }

    public FormationView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews();
        //      init();
    }

    public FormationView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initViews();
//        init();
    }

    public void setShouting(Shouting shouting) {
        mShouting = shouting;
    }

    public void setKey(String iKey) {
        mKey = iKey;
        if (mTV_Key != null) {
            mTV_Key.setText(iKey);
        }
    }

    public void setCount(int count) {
        mCount = count;
        if (mTV_Count != null) {
            mTV_Count.setText(String.format(Locale.ENGLISH, "(%d)",mCount ));
        }
    }

    public int getCount() {
        return mCount;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
        if (mTV_Text != null) {
            mTV_Text.setText(mText);
        }
    }

    public String getKey() {
        return mKey;
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int tWidth;
        int tHeight;
        mTV_Text.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        tHeight = mTV_Text.getMeasuredHeight();
        tWidth = mTV_Text.getMeasuredWidth();
        mTV_Count.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        tWidth += mTV_Count.getMeasuredWidth();
        int widthNewMeasureSpec;
        int heightNewMeasureSpec;
        widthNewMeasureSpec = MeasureSpec.makeMeasureSpec(tWidth , MeasureSpec.EXACTLY);
        heightNewMeasureSpec = MeasureSpec.makeMeasureSpec(tHeight  , MeasureSpec.EXACTLY);
        super.onMeasure(widthNewMeasureSpec, heightNewMeasureSpec);
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
      //  initViews();
    }

    // setter and getter
    // lifecylce and override
    // internal
    void initViews() {
        this.setId(View.generateViewId());
        int padding = 10;

        this.setOrientation(LinearLayout.VERTICAL);
        LinearLayout tLL_FirstLine = new LinearLayout(this.getContext());
        tLL_FirstLine.setId(View.generateViewId());
        tLL_FirstLine.setOrientation(HORIZONTAL);
        this.addView(tLL_FirstLine);
        //
        mTV_Text = new TextView(this.getContext());
        tLL_FirstLine.addView(mTV_Text);
        mTV_Text.setId(View.generateViewId());
        mTV_Text.setLayoutParams(new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));
        mTV_Text.setTextColor(getResources().getColor(R.color.scream_black, null));
        mTV_Text.setPadding(padding, padding, padding, padding);
        //
        mTV_Count = new TextView(this.getContext());
        tLL_FirstLine.addView(mTV_Count);
        mTV_Count.setId(View.generateViewId());
        mTV_Count.setLayoutParams(new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));

        mTV_Count.setText(String.format(Locale.ENGLISH, "(%d)",mCount ));
        mTV_Count.setPadding(0, padding, padding, padding);

        this.setSelected(false);
//        mIsSelected = false;
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
              //  mIsSelected = !mIsSelected;
                v.setSelected(! v.isSelected());
                shoutSelection();
                refresh();
            }
        });

        refresh();

    }


    @SuppressLint("SetTextI18n")
    void refresh() {
        final View fView = this;
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (mTV_Count != null) {
                    mTV_Count.setText("(" + mCount + ")");
                }
                if (mTV_Key != null) {
                    mTV_Key.setText(mKey);
                }
                if (mTV_Text != null) {
                    mTV_Text.setText(mText);
                }
                if (fView.isSelected()) {
                    fView.setBackgroundColor(getResources().getColor(R.color.bg_list_selected, null));
                } else {
                    fView.setBackgroundColor(getResources().getColor(R.color.bg_light, null));
                }
            }
        });
    }

    void shoutSelection() {
        if (mShouting != null) {
            Shout tShout = new Shout(this.getClass().getSimpleName());
            tShout.mLastAction = "changed";
            tShout.mLastObject = "Selection";
            mShouting.shoutUp(tShout);
        }
    }

    // public methods
    @Override
    public void shoutUp(Shout tShoutToCeiling) {
        Logg.i(TAG, tShoutToCeiling.toString());
    }
}
