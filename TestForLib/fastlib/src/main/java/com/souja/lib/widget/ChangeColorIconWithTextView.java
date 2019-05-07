package com.souja.lib.widget;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.souja.lib.R;
import com.souja.lib.utils.MGlobal;

import org.xutils.common.util.LogUtil;


public class ChangeColorIconWithTextView extends View {

    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Paint mPaint;
    private boolean isLeft;
    /**
     * 颜色
     */
    private int mColor = 0xFF45C01A;
    private int bgColor = 0xFF45C01A;
    /**
     * 透明度 0.0-1.0
     */
    private float mAlpha = 0f;
    /**
     * 图标
     */
    private Bitmap mIconBitmap;
    /**
     * 限制绘制icon的范围
     */
    private Rect mIconRect;
    /**
     * icon底部文本
     */
    private String mText = "";
    private int mTextSize = (int) TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics());
    private Paint mTextPaint;
    private Rect mTextBound = new Rect();

    private int iconOffset, textOffset;

    public ChangeColorIconWithTextView(Context context) {
        super(context);
    }


    public ChangeColorIconWithTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 初始化自定义属性值
     *
     * @param context
     * @param attrs
     */
    public ChangeColorIconWithTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // 获取设置的图标
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.ChangeColorIconView);

        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            if (attr == R.styleable.ChangeColorIconView_cciv_icon) {
                BitmapDrawable drawable = (BitmapDrawable) a.getDrawable(attr);
                mIconBitmap = drawable.getBitmap();

            } else if (attr == R.styleable.ChangeColorIconView_cciv_color) {
                mColor = a.getColor(attr, 0x45C01A);

            } else if (attr == R.styleable.ChangeColorIconView_cciv_text) {
                mText = a.getString(attr);

            } else if (attr == R.styleable.ChangeColorIconView_cciv_text_size) {
                mTextSize = (int) a.getDimension(attr, TypedValue
                        .applyDimension(TypedValue.COMPLEX_UNIT_SP, 10,
                                getResources().getDisplayMetrics()));

            } else if (attr == R.styleable.ChangeColorIconView_cciv_bgcolor) {
                bgColor = a.getColor(attr, 0x45C01A);

            }
        }

        a.recycle();

        iconOffset = (int) (MGlobal.get().getDensity() * 9);
        textOffset = (int) (MGlobal.get().getDensity() * 7);
        mTextSize = (int) (MGlobal.get().getDensity() * 14);
        mTextPaint = new Paint();
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(0xff555555);
        // 得到text绘制范围
        mTextPaint.getTextBounds(mText, 0, mText.length(), mTextBound);

    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // 得到绘制icon的宽
        int bitmapWidth = Math.min(getMeasuredWidth() - getPaddingLeft()
                - getPaddingRight(), getMeasuredHeight() - getPaddingTop()
                - getPaddingBottom() - mTextBound.height());

        int left = getMeasuredWidth() / 2 - bitmapWidth / 2;
        int top = (getMeasuredHeight() - mTextBound.height()) / 2 - bitmapWidth
                / 2;
        // 设置icon的绘制范围
        mIconRect = new Rect(left, top, left + bitmapWidth, top + bitmapWidth);

    }

    @Override
    public void onDraw(Canvas canvas) {
        if (mIconBitmap != null) {
            mIconRect = new Rect(getWidth() / 2 - mIconBitmap.getWidth() / 2, iconOffset, getWidth() / 2 + mIconBitmap.getWidth() / 2, iconOffset + mIconBitmap.getHeight());
            canvas.drawBitmap(mIconBitmap, null, mIconRect, null);
            canvas.drawBitmap(mBitmap, 0, 0, null);
        }
        int alpha = (int) Math.ceil((255 * mAlpha));
//		canvas.drawBitmap(mIconBitmap,mIconRect.left+mIconBitmap.getWidth()/2,mIconRect.top+mIconBitmap.getHeight()/2,null);
        setupBg(canvas, mAlpha);
        setupTargetBitmap(alpha);
        drawSourceText(canvas, alpha);
        drawTargetText(canvas, alpha);

    }

    private void setupBg(Canvas canvas, float alpha) {
        int offset = (int) (getWidth() * mAlpha);
        mPaint = new Paint();
        mPaint.setColor(bgColor);
        mPaint.setStyle(Paint.Style.FILL);
        if (isLeft)
            canvas.drawRect(getWidth() - offset, 0, getWidth(),
                    getHeight(), mPaint);
        else
            canvas.drawRect(0, 0, offset, getHeight(), mPaint);
    }

    private void setupTargetBitmap(int alpha) {
        if (mBitmap == null) return;
        mBitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(),
                Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        mPaint = new Paint();
        mPaint.setColor(mColor);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setAlpha(alpha);
        mCanvas.drawRect(mIconRect, mPaint);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        mPaint.setAlpha(255);
//		mCanvas.drawBitmap(mIconBitmap, mIconRect.left + mIconBitmap.getWidth() / 2, mIconRect.top + mIconBitmap.getHeight() / 2, mPaint);
        mCanvas.drawBitmap(mIconBitmap, null, mIconRect, mPaint);
    }

    private void drawSourceText(Canvas canvas, int alpha) {
        if (mText == null) {
            LogUtil.e("no text");
            return;
        }
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(Color.parseColor("#a7a7a7"));
        mTextPaint.setAlpha(255 - alpha);
        canvas.drawText(mText, mIconRect.left + mIconRect.width() / 2
                        - mTextBound.width() / 2,
                mIconRect.bottom + mTextBound.height() + textOffset, mTextPaint);
    }

    private void drawTargetText(Canvas canvas, int alpha) {
        if (mText == null) {
            LogUtil.e("no text");
            return;
        }
        mTextPaint.setColor(mColor);
        mTextPaint.setAlpha(alpha);
        canvas.drawText(mText, mIconRect.left + mIconRect.width() / 2
                        - mTextBound.width() / 2,
                mIconRect.bottom + mTextBound.height() + textOffset, mTextPaint);

    }

    public void isLeft(boolean isLeft) {
        this.isLeft = isLeft;
    }

    public void setIconAlpha(float alpha) {
        this.mAlpha = alpha;
        invalidateView();
    }

    private void invalidateView() {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            invalidate();
        } else {
            postInvalidate();
        }
    }

    public void setIconColor(int color) {
        mColor = color;
    }

    public void setIcon(int resId) {
        this.mIconBitmap = BitmapFactory.decodeResource(getResources(), resId);
        if (mIconRect != null)
            invalidateView();
    }

    public void setIcon(Bitmap iconBitmap) {
        this.mIconBitmap = iconBitmap;
        if (mIconRect != null)
            invalidateView();
    }

    private static final String INSTANCE_STATE = "instance_state";
    private static final String STATE_ALPHA = "state_alpha";

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE_STATE, super.onSaveInstanceState());
        bundle.putFloat(STATE_ALPHA, mAlpha);
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            mAlpha = bundle.getFloat(STATE_ALPHA);
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATE));
        } else {
            super.onRestoreInstanceState(state);
        }

    }

}
