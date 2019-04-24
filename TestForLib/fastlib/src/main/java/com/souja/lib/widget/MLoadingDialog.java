package com.souja.lib.widget;

/**
 * 菊花...
 * Created by Yangdz on 2015/2/4.
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.souja.lib.R;


public class MLoadingDialog extends LinearLayout {

    public MLoadingDialog(Context context) {
        this(context, null);
    }

    public MLoadingDialog(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MLoadingDialog(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MLoadingDialog(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private final String defaultTip = "请稍候...";
    private TextView mTvTip, mTvBigTip, mTvSmallTip;
    private View llBody;
    private FrameLayout wholeBody;
    private ProgressBar mProgressBar;

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.common_progress, this);
        mTvTip = findViewById(R.id.content);
        mTvBigTip = findViewById(R.id.tv_big);
        mTvSmallTip = findViewById(R.id.tv_small);
        llBody = findViewById(R.id.ll_body);
        wholeBody = findViewById(R.id.progress_body);
        mProgressBar = findViewById(R.id.progressBar);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MLoadingDialog);
        try {
            String tip = a.getString(R.styleable.MLoadingDialog_mTip);
            mTip = tip == null ? defaultTip : tip;
            mTvTip.setText(mTip);
            int txtColor = a.getColor(R.styleable.MLoadingDialog_txtColor, -123);
            if (txtColor != -123) {
                mTvTip.setTextColor(txtColor);
            }
            int bgColor = a.getColor(R.styleable.MLoadingDialog_mLoadBg, Color.WHITE);
            llBody.setBackgroundColor(bgColor);
//            boolean noShape = a.getBoolean(R.styleable.MLoadingDialog_noShape, false);
//            if (noShape) {
//                mProgressBar.setBackgroundResource(R.drawable.drawable_transparent);
//            }
            boolean defineId = a.getBoolean(R.styleable.MLoadingDialog_definedId, false);
            if (!defineId)
                setId(R.id.m_loading);
        } finally {
            a.recycle();
        }

        llBody.setOnClickListener(v -> {
            if (mClick != null)
                mClick.onLoadingClick();
        });
    }

    private MLoadingClick mClick;

    public void setMClick(MLoadingClick listener) {
        mClick = listener;
    }

    public interface MLoadingClick {
        void onLoadingClick();
    }

    private String mTip;

    public void setTip(String tip) {
        setMClick(null);
        if (mProgressBar != null && mProgressBar.getVisibility() != VISIBLE)
            mProgressBar.setVisibility(VISIBLE);
        mTip = tip;
        if (mTvTip != null) {
            if (mTvTip.getVisibility() != VISIBLE) mTvTip.setVisibility(VISIBLE);
            mTvTip.setText(mTip);
        }
    }

    public void setRetryDefaultTip() {
        setMClick(null);
        if (mProgressBar != null && mProgressBar.getVisibility() != VISIBLE)
            mProgressBar.setVisibility(VISIBLE);
        mTip = defaultTip;
        if (mTvTip != null) {
            if (mTvTip.getVisibility() != VISIBLE) mTvTip.setVisibility(VISIBLE);
            mTvTip.setText(mTip);
        }
    }

    public void show() {
        mTvTip.setVisibility(GONE);
        mTvBigTip.setVisibility(GONE);
        mTvSmallTip.setVisibility(GONE);
//        mTvTip.setText(mTip);
        if (getVisibility() != VISIBLE)
            setVisibility(VISIBLE);
    }

    public void dismiss() {
        if (getVisibility() != GONE)
            setVisibility(GONE);
    }

    public boolean isShowing() {
        return getVisibility() == VISIBLE;
    }

    public void hideProgress() {
        mProgressBar.setVisibility(GONE);
    }

    public void setErrMsg(String msg) {
        if (getVisibility() != VISIBLE)
            setVisibility(VISIBLE);
        hideProgress();
        mTvTip.setVisibility(VISIBLE);
        mTvTip.setText(msg);
    }

    public void setErrMsg(int msgRes) {
        if (getVisibility() != VISIBLE)
            setVisibility(VISIBLE);
        hideProgress();
        mTvTip.setVisibility(VISIBLE);
        mTvTip.setText(msgRes);
    }

    public void setErrMsgRetry(String msg) {
        if (getVisibility() != VISIBLE)
            setVisibility(VISIBLE);
        hideProgress();
        mTvTip.setVisibility(VISIBLE);
        mTvTip.setText(msg + "\n\n点击重试");
    }

    public void setErrMsg(String big, String small) {
        hideProgress();
        mTvTip.setVisibility(GONE);
        mTvBigTip.setVisibility(VISIBLE);
        mTvSmallTip.setVisibility(VISIBLE);
        mTvBigTip.setText(big);
        mTvSmallTip.setText(small);
    }

    public void addEmptyView(View emptyView) {
        wholeBody.addView(emptyView);
    }

    public void hideAllTip() {
        mProgressBar.setVisibility(GONE);
        mTvTip.setVisibility(GONE);
        mTvBigTip.setVisibility(GONE);
        mTvSmallTip.setVisibility(GONE);
    }
}