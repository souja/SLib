package com.souja.lib.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.souja.lib.R;
import com.souja.lib.inter.CommonItemClickListener;
import com.souja.lib.utils.ScreenUtil;

public class LayoutMenu extends LinearLayout {


    public LayoutMenu(Context context) {
        super(context);
        init(context);
    }

    public LayoutMenu(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LayoutMenu(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public LayoutMenu(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private View viewTop, viewBot;
    private Animation slideIn, slideOut;
    private CommonItemClickListener mListener;

    public void setListener(CommonItemClickListener listener) {
        mListener = listener;
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.layout_img_download_menu, this);
        ScreenUtil.initScale(this);
        slideIn = AnimationUtils.loadAnimation(context, R.anim.slide_in_from_bottom);
        slideOut = AnimationUtils.loadAnimation(context, R.anim.slide_out_to_bottom);
        slideOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                viewBot.setVisibility(GONE);
                setVisibility(GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        viewTop = findViewById(R.id.v_top);
        viewBot = findViewById(R.id.v_bot);
        findViewById(R.id.tv_cancel).setOnClickListener(v -> close());
        viewTop.setOnClickListener(v -> close());
        findViewById(R.id.layout_save).setOnClickListener(v -> {
            if (mListener != null) mListener.onItemClick(0);
        });

    }

    public void open() {
        setVisibility(VISIBLE);
        viewBot.setVisibility(VISIBLE);
        viewBot.startAnimation(slideIn);
    }

    public void close() {
        viewBot.startAnimation(slideOut);
    }
}
