package com.souja.lib.utils;

/**
 * Created by Yangdz on 2016/10/14 0014.
 */


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.RectF;
import android.graphics.Typeface;

public class GLFont {

    private static int bmpSize = 135;
    private static float textSize = 42f;

    public static Bitmap getImage(String mString) {
        return getImage(mString, "#cfdddd", Typeface.create("宋体", Typeface.NORMAL));
    }

    public static Bitmap getImage(String mString, String color, Typeface font) {

        Bitmap bmp = Bitmap.createBitmap(bmpSize, bmpSize, Bitmap.Config.ARGB_8888);
        //图象大小要根据文字大小算下,以和文本长度对应
        Canvas canvasTemp = new Canvas(bmp);

        Paint p = new Paint();
        p.setColor(Color.parseColor(color));

        RectF rect = new RectF(0, 0, bmpSize, bmpSize);
        canvasTemp.drawArc(rect, 0, 360, true, p);

        Paint p1 = new Paint();
        p1.setColor(Color.WHITE);
        p1.setTypeface(font);
        p1.setAntiAlias(true);//去除锯齿
        p1.setFilterBitmap(true);//对位图进行滤波处理
//        float textsize = size * ScreenUtil.mScale;
        p1.setTextSize(textSize);

        //获取paint中的字体信息  settextSize要在他前面
        FontMetrics fontMetrics = p1.getFontMetrics();
        // 计算文字高度
        float fontHeight = fontMetrics.bottom - fontMetrics.top;
        // 计算文字高度baseline
        float textBaseY = bmp.getHeight() - (bmp.getHeight() - fontHeight) / 2 - fontMetrics.bottom;
        //获取字体的长度
        float fontWidth = p1.measureText(mString);
        //计算文字长度的baseline
        float textBaseX = (bmpSize - fontWidth) / 2;
        canvasTemp.drawText(mString, textBaseX, textBaseY, p1);


        return bmp;
    }
}