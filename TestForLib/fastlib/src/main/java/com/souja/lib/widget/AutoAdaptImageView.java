package com.souja.lib.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.widget.ImageView;

import org.xutils.common.util.LogUtil;

/**
 * Created by Souja on 2018/4/24 0024.
 */

@SuppressLint("AppCompatCustomView")
public class AutoAdaptImageView extends ImageView {


    public AutoAdaptImageView(Context context) {
        super(context);
    }

    public AutoAdaptImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoAdaptImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    // 资源图片的位图
    private Bitmap mBitmap;

    // 绘制图片的矩阵
    private Matrix matrix = new Matrix();

    public void setupImage(Bitmap bmp) {
        mBitmap = bmp;
        matrix = new Matrix();
        calcStartXY();
        invalidate();
    }

    public float increase = 1;

    public int startX, startY;

    private void calcStartXY() {
        int viewWidth = getWidth();
        int viewHeight = getHeight();

        int bmpWidth = mBitmap.getWidth();
        int bmpHeight = mBitmap.getHeight();

        LogUtil.e("view size：" + viewWidth + "*" + viewHeight + " bmp size：" + bmpWidth + "*" + bmpHeight);
        float tempWidth = 0, tempHeight = 0;
        if (bmpWidth < viewWidth && bmpHeight < viewHeight) {
            LogUtil.e("图片的宽高都小于画布，适当放大");
            boolean transOk = false;
            while (!transOk) {
                increase += 0.05;
                tempWidth = (float) bmpWidth * increase;
                tempHeight = (float) bmpHeight * increase;
                if (tempWidth > viewWidth || tempHeight > viewHeight) {
                    increase -= 0.05;
                    tempWidth = (float) bmpWidth * increase;
                    tempHeight = (float) bmpHeight * increase;
                    transOk = true;
                    LogUtil.e("Last calc param:" + tempWidth + "*" + tempHeight);
                }
            }
            bmpWidth = Math.round(tempWidth);
            bmpHeight = Math.round(tempHeight);
            LogUtil.e("放大比率为：" + increase + " 放大后的宽度=" + bmpWidth + " 放大后的高度=" + bmpHeight);
        }else if(bmpWidth>viewWidth&&bmpHeight>viewHeight){
            LogUtil.e("图片的宽高都大于画布，适当缩小");
            boolean transOk = false;
            while (!transOk) {
                increase -= 0.01;
                tempWidth = (float) bmpWidth * increase;
                tempHeight = (float) bmpHeight * increase;
                if (tempWidth <= viewWidth && tempHeight <= viewHeight) {
//                    increase -= 0.05;
//                    tempWidth = (float) bmpWidth * increase;
//                    tempHeight = (float) bmpHeight * increase;
                    transOk = true;
                    LogUtil.e("Last calc param:" + tempWidth + "*" + tempHeight);
                }
            }
            bmpWidth = Math.round(tempWidth);
            bmpHeight = Math.round(tempHeight);
            LogUtil.e("缩小比率为：" + increase + " 缩小后的宽度=" + bmpWidth + " 缩小后的高度=" + bmpHeight);
        }

        startX = (viewWidth - bmpWidth) / 2;
        startY = (viewHeight - bmpHeight) / 2;

        LogUtil.e("startX:" + startX + ",startY:" + startY);
        LogUtil.e("increase:" + increase);
        if (increase != 1)
            matrix.postScale(increase, increase);
        matrix.postTranslate(startX, startY);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mBitmap == null) return;
        canvas.drawBitmap(mBitmap, matrix, null);
    }

    @Override
    public void setImageResource(@DrawableRes int resId) {
        setupImage(BitmapFactory.decodeResource(getResources(), resId));
    }
}
