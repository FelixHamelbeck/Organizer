package org.pochette.organizer.gui_assist;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import org.pochette.organizer.BuildConfig;
import org.pochette.utils_lib.logg.Logg;

/**
 * ImageView that keeps aspect ratio when scaled
 */
public class BitmapView extends androidx.appcompat.widget.AppCompatImageView {

    //Variables
    private static final String TAG = "FEHA (BitmapView)";
    private int mUseWidth;
    private Bitmap mBitmap;

    public BitmapView(Context context) {
        super(context);
        init();
    }

    public BitmapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BitmapView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    void init() {
        mUseWidth = 0;
    }

    @Override
    public void setImageBitmap(Bitmap iBitmap) {
        mBitmap = iBitmap;
        scaleBitmap();
    }

    void scaleBitmap() {
        if (mUseWidth == 0) {
            super.setImageBitmap(mBitmap);
        } else {
            int tHeight;
            tHeight = mUseWidth * mBitmap.getHeight() / mBitmap.getWidth();
            Bitmap tBitmapScaled = Bitmap.createScaledBitmap(mBitmap, mUseWidth, tHeight, true);
            super.setImageBitmap(tBitmapScaled);
            setMeasuredDimension(mUseWidth, tHeight);
        }
    }

    @Override
    protected void onMeasure(int iWidthMeasureSpec, int iHeightMeasureSpec) {
        if (BuildConfig.DEBUG) {
            Logg.d(TAG, "OnMeasure");
        }
        try {
            Drawable tDrawable = getDrawable();
            if (tDrawable == null) {
                setMeasuredDimension(0, 0);
            } else {
                int tMeasuredWidth = MeasureSpec.getSize(iWidthMeasureSpec);
                int tMeasuredHeight = MeasureSpec.getSize(iHeightMeasureSpec);
                if (tMeasuredHeight == 0 && tMeasuredWidth == 0) { // Height and
                    // width set
                    // to
                    // wrap_content
                    setMeasuredDimension(tMeasuredWidth, tMeasuredHeight);
                } else if (tMeasuredHeight == 0) { // Height set to wrap_content
                    int height;
                    height = tMeasuredWidth * tDrawable.getIntrinsicHeight()
                            / tDrawable.getIntrinsicWidth();
                    setMeasuredDimension(tMeasuredWidth, height);
                } else if (tMeasuredWidth == 0) { // Width set to wrap_content
                    int width = tMeasuredHeight * tDrawable.getIntrinsicWidth()
                            / tDrawable.getIntrinsicHeight();
                    setMeasuredDimension(width, tMeasuredHeight);
                } else { // Width and height are explicitly set (either to
                    // match_parent or to exact value)
                    mUseWidth = tMeasuredWidth;
                    if (mBitmap == null) {
                        setMeasuredDimension(tMeasuredWidth, tMeasuredHeight);
                    } else {
                        scaleBitmap();
                    }
                }
            }
        } catch(Exception e) {
            super.onMeasure(iWidthMeasureSpec, iHeightMeasureSpec);
        }
    }
}