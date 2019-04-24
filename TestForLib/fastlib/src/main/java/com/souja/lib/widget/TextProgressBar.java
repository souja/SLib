package com.souja.lib.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import com.souja.lib.R;
import com.souja.lib.utils.MTool;
import com.souja.lib.utils.ScreenUtil;

/**
 * Created by Jingjing.hu on 2016/11/18.
 */

public class TextProgressBar extends ProgressBar {

    private final float textSize = ScreenUtil.mScale * 36;
    private String mText = "0%";
    private Rect mRect;
    private Paint mPaint;
    private PorterDuffXfermode mPorterDuffXfermode;
    private float mProgress;
    private float mWidth;
    private float dy = 30 * ScreenUtil.mScale;

    public TextProgressBar(Context context) {
        this(context, null);
    }

    public TextProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private int textX;

    public void init(Context context, AttributeSet attrs) {

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TextProgressBar);
        try {
            String text = a.getString(R.styleable.TextProgressBar_mPTxt);
            if (!MTool.isEmpty(text)) mText = text;
        } finally {
            a.recycle();
        }

        mRect = new Rect();

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.WHITE);
        mPaint.setTextSize(textSize);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setXfermode(null);
        mPaint.setTextAlign(Paint.Align.LEFT);

        mPorterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        mWidth = width * mProgress / 100;

        mPaint.getTextBounds(mText, 0, mText.length(), mRect);
        textX = width / 2 - mRect.width() / 2;
//        LogUtil.e("Rect height:" + getHeight() + " " + mRect.height());
        Bitmap srcBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas srcCanvas = new Canvas(srcBitmap);

        drawTextUI(canvas, textX, mText, srcBitmap, srcCanvas);
    }

    private void drawTextUI(Canvas canvas, int x, String textContent, Bitmap srcBitmap, Canvas srcCanvas) {
        canvas.drawText(textContent, x, dy, mPaint);
        srcCanvas.drawText(textContent, x, dy, mPaint);

        // 设置混合模式
        mPaint.setXfermode(mPorterDuffXfermode);
        mPaint.setColor(Color.WHITE);
        RectF rectF = new RectF(0, 0, mWidth, getHeight());//mWidth是不断变化的
        // 绘制源图形
        srcCanvas.drawRect(rectF, mPaint);
        // 绘制目标图
        canvas.drawBitmap(srcBitmap, 0, 0, null);
        // 清除混合模式
        mPaint.setXfermode(null);
    }

    public synchronized void setProgress(float progress) {
        mProgress = progress;
        mText = progress + "%";
        super.setProgress((int) progress);
    }

    public synchronized void setProgress(int progress) {
        mProgress = progress;
        mText = progress + "%";
        super.setProgress(progress);
    }

    public synchronized void setProgress(int progress, String txt) {
        mProgress = progress;
        mText = txt;
        super.setProgress(progress);
    }

}
