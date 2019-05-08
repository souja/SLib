package com.souja.lib.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.souja.lib.R;
import com.souja.lib.utils.ScreenUtil;

public class IhisunEmptyView extends LinearLayout {

    public IhisunEmptyView(Context context) {
        super(context);
        init(context);
    }

    public IhisunEmptyView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public IhisunEmptyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public IhisunEmptyView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.widget_empty_view, this);
        ScreenUtil.initScale(this);
    }
}
