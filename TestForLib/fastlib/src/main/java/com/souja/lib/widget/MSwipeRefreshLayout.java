package com.souja.lib.widget;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;

import com.souja.lib.R;


/**
 * Created by ydz on 2016/4/5.
 */
public class MSwipeRefreshLayout extends SwipeRefreshLayout {
    public MSwipeRefreshLayout(Context context) {
        this(context, null);
        init();
    }

    public MSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setColorSchemeResources(
                android.R.color.holo_red_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_blue_light,
                android.R.color.holo_green_light);
        setId(R.id.m_swipeRefresh);
    }

}
