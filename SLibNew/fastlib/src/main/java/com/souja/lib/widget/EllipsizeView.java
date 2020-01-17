package com.souja.lib.widget;

import android.content.Context;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.souja.lib.R;
import com.souja.lib.utils.ScreenUtil;

public class EllipsizeView extends LinearLayout {

    public EllipsizeView(Context context) {
        this(context, null, 0);
    }

    public EllipsizeView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EllipsizeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.widget_ellipsiz_view, this);
        ScreenUtil.initScale(this);
    }

}
