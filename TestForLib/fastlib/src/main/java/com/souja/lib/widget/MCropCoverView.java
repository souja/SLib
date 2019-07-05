package com.souja.lib.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.Region;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.souja.lib.R;
import com.souja.lib.utils.MGlobal;
import com.souja.lib.utils.ScreenUtil;

import org.xutils.common.util.LogUtil;

public class MCropCoverView extends View {
    public MCropCoverView(Context context) {
        this(context, null);
    }

    public MCropCoverView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MCropCoverView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private int size;
    private int drawHeight;
    private int mLeft, mTop, mRight, mBot;
    private int mRatioType;//1-正方形（1:1），2-长方形（2:1）
    private int cropWidth,//裁剪的宽度
            cropHeight;//裁剪的高度
    private Path mPath;
    private boolean bInited;

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MCropCoverView);
            try {
                size = a.getInt(R.styleable.MCropCoverView_ccv_size, 960);
                mLeft = a.getInt(R.styleable.MCropCoverView_ccv_left_dis, 60);
                mRatioType = a.getInt(R.styleable.MCropCoverView_ccv_ratio_type, 1);

                cropWidth = a.getInt(R.styleable.MCropCoverView_ccv_crop_width, -1);
                cropHeight = a.getInt(R.styleable.MCropCoverView_ccv_crop_height, -1);
                LogUtil.e("size:" + size + ",left:" + mLeft + ",type:" + mRatioType
                        + ",width:" + cropWidth + ",height:" + cropHeight);
            } finally {
                a.recycle();
            }
        }

        setupValues();
    }

    private void setupValues() {
        if (!bInited) {
            bInited = true;
            int screenHeight = MGlobal.get().getDeviceHeight();
            LogUtil.e("screenHeight:" + screenHeight);

            if (cropWidth != -1) {
                if (cropWidth <= 0) cropWidth = 960;
                mLeft = (int) (((1080 - cropWidth) / 2) * ScreenUtil.mScale);
                cropWidth = (int) (cropWidth * ScreenUtil.mScale);
                cropHeight = (int) ((cropHeight == -1 ? cropWidth : cropHeight) * ScreenUtil.mScale);
                mRight = cropWidth + mLeft;
                drawHeight = cropHeight;
            } else {
                size = (int) (size * ScreenUtil.mScale);
                mLeft = (int) (mLeft * ScreenUtil.mScale);
                mRight = size + mLeft;
                if (mRatioType == 1) {
                    drawHeight = size;
                } else {
//            drawHeight = size / 3 * 2;
                    drawHeight = size / 2;
                }
            }

            mTop = (screenHeight - drawHeight) / 2;
            mBot = mTop + drawHeight;

            LogUtil.e("draw w-h:" + size + "&" + drawHeight);
            LogUtil.e("t-b-l-r:" + mTop + "&" + mBot + "&" + mLeft + "&" + mRight);

            mPath = new Path();
            mPath.addRect(mLeft, mTop, mRight, mBot, Path.Direction.CW);
        }
    }


    public void setRatioType(int type) {
        mRatioType = type;
        setupValues();
        invalidate();
    }


    public void setWidthHeight(int width, int height) {
        if (width <= 0 || height <= 0) return;
        cropWidth = width;
        cropHeight = height;
        setupValues();
        invalidate();
    }

    public int getMLeft() {
        return mLeft;
    }

    public int getMTop() {
        return mTop;
    }

    public int getMRight() {
        return mRight;
    }

    public int getMBot() {
        return mBot;
    }

    public int getCropWidth() {
        if (cropWidth != -1) return cropWidth;
        return size;
    }

    public int getCropHeight() {
        if (cropWidth != -1) {
            return cropHeight <= 0 ? cropWidth : cropHeight;
        } else
            return drawHeight;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //保存当前canvas 状态
        canvas.save();
        //将当前画布可以绘画区域限制死为预览框外的区域
        canvas.clipPath(mPath, Region.Op.DIFFERENCE);
        //绘画半透明遮罩
        canvas.drawColor(Color.parseColor("#90000000"));
        //还原画布状态
        canvas.restore();
    }
}
