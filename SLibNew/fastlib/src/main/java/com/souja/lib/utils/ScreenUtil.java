package com.souja.lib.utils;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ScreenUtil {

    // Screen Params
    public static final int BASE_SCREEN_WIDTH = 1080;
    public static final int BASE_SCREEN_HEIGHT = 1920;
    public static float mScale = 1;
    public static float mScaleH = 1;

    /**
     * Set the screen scale value
     *
     * @param context current activity
     */
//    private static Context mContext;
    public static void setScale(Activity context) {
//        mContext = context;
        int width, height;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        width = displayMetrics.widthPixels;
        height = displayMetrics.heightPixels;
        mScale = (float) width / BASE_SCREEN_WIDTH;
        mScaleH = (float) height / BASE_SCREEN_HEIGHT;
    }

    public static ViewGroup.LayoutParams scaleParams(ViewGroup.LayoutParams params) {
        if (params == null) {
            throw new RuntimeException("params can't be null");
        }
        if (params.width > 0) {
            params.width = getScalePxValue(params.width);
        }

        if (params.height > 0) {
            params.height = getScalePxValueH(params.height);
        }

        if (params instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) params;
            lp.topMargin = getScalePxValueH(lp.topMargin);
            lp.bottomMargin = getScalePxValueH(lp.bottomMargin);
            lp.leftMargin = getScalePxValue(lp.leftMargin);
            lp.rightMargin = getScalePxValue(lp.rightMargin);
        }

        return params;
    }

    /**
     * Scale the view
     *
     * @param view The scale view
     * @return null if the view is null. the view all param value.
     */
    public static void scaleProcess(View view) {
        if (view == null)
            return;

        // set padding
        int top = getScalePxValueH(view.getPaddingTop());
        int bottom = getScalePxValueH(view.getPaddingBottom());
        int left = getScalePxValue(view.getPaddingLeft());
        int right = getScalePxValue(view.getPaddingRight());

        view.setPadding(left, top, right, bottom);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            int minHeight = view.getMinimumHeight();
            view.setMinimumHeight((int) (minHeight * mScale));
            int minWidth = view.getMinimumWidth();
            view.setMinimumWidth((int) (minWidth * mScale));
        }

        // set drawable
        if (view instanceof TextView) {
            Drawable[] drawables = ((TextView) view).getCompoundDrawables();
            setCompoundDrawablesWithIntrinsicBounds((TextView) view, drawables[0], drawables[1], drawables[2], drawables[3]);

            ((TextView) view).setCompoundDrawablePadding(getScalePxValue(((TextView) view).getCompoundDrawablePadding()));
        }
        view.setLayoutParams(scaleParams(view.getLayoutParams()));
    }

    private static void setCompoundDrawablesWithIntrinsicBounds(TextView view, Drawable left,
                                                                Drawable top, Drawable right, Drawable bottom) {

        if (left != null) {
            left.setBounds(0, 0, getScalePxValue(left.getIntrinsicWidth()),
                    getScalePxValue(left.getIntrinsicHeight()));
        }
        if (right != null) {
            right.setBounds(0, 0, getScalePxValue(right.getIntrinsicWidth()),
                    getScalePxValue(right.getIntrinsicHeight()));
        }
        if (top != null) {
            top.setBounds(0, 0, getScalePxValueH(top.getIntrinsicWidth()),
                    getScalePxValueH(top.getIntrinsicHeight()));
        }
        if (bottom != null) {
            bottom.setBounds(0, 0, getScalePxValueH(bottom.getIntrinsicWidth()),
                    getScalePxValueH(bottom.getIntrinsicHeight()));
        }

        view.setCompoundDrawables(left, top, right, bottom);
    }


    /**
     * Scale the textview's font size
     *
     * @param view
     */
    public static void scaleProcessTextSize(TextView view) {
        if (view == null) return;

        float size = view.getTextSize();
        size *= mScale;
        float maxWidth = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            maxWidth = view.getMaxWidth();
            maxWidth *= mScale;
            view.setMaxWidth((int) maxWidth);
        }

        // Size's unit use pixel,so param unit use TypedValue.COMPLEX_UNIT_PX.
        view.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);

    }

    /**
     * Scale the textview
     *
     * @param view The scale view
     * @return
     */
    public static void scaleProcessTextView(TextView view) {
        if (view == null)
            return;
        scaleProcess(view);
        scaleProcessTextSize(view);
    }

    public static int getScalePxValue(int value) {
        if (Math.abs(value) <= 4) {
            return value;
        }
        return (int) Math.ceil(mScale * value);
    }

    public static int getScalePxValueH(int value) {
        if (Math.abs(value) <= 4) {
            return value;
        }
        return (int) Math.ceil(mScale * value);
    }

    public static void initScale(View v) {
        if (v != null) {
            if (v instanceof ViewGroup) {
                scaleViewGroup((ViewGroup) v);
            } else {
                scaleView(v);
            }
        }
    }

    private static void scaleViewGroup(ViewGroup viewGroup) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View view = viewGroup.getChildAt(i);
            if (view instanceof ViewGroup) {
                scaleViewGroup((ViewGroup) view);
            }
            scaleView(view);
        }

    }

    private static void scaleView(View view) {
        if (view instanceof TextView) {
            scaleProcessTextView((TextView) view);
            return;
        }

        scaleProcess(view);
    }
}
