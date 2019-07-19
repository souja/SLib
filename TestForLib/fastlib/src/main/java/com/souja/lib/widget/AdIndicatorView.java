package com.souja.lib.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;

import com.souja.lib.utils.ScreenUtil;

public class AdIndicatorView extends LinearLayout {
    public AdIndicatorView(Context context) {
        this(context, null);
    }

    public AdIndicatorView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AdIndicatorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setGravity(Gravity.CENTER);
    }

    private int focusIndex = 0;

    public void setCount(int count) {
        if (getChildCount() > 0) removeAllViews();
        for (int i = 0; i < count; i++) {
            MCheckableView indicatorView = new MCheckableView(getContext());
            LayoutParams params = new LayoutParams(36, 6);
            indicatorView.setLayoutParams(params);
            if (i > 0) {
                params.leftMargin = 15;
            }
            ScreenUtil.initScale(indicatorView);
            addView(indicatorView);
            if (i == 0) {
                indicatorView.toggle();
            }
        }
    }

    public void setFocusIndex(int i) {
        if (i == focusIndex) return;
//        LogUtil.e("setFocusIndex:" + i);
        ((MCheckableView) getChildAt(i)).toggle();
        ((MCheckableView) getChildAt(focusIndex)).toggle();
        focusIndex = i;
    }

}
