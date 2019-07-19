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
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.souja.lib.R;
import com.souja.lib.inter.ICommonEmptyCallBack;
import com.souja.lib.utils.MTool;
import com.souja.lib.utils.NetWorkUtils;
import com.souja.lib.utils.ScreenUtil;


public class MLoadingDialog extends LinearLayout {

    /*================================
                  FUNCTIONS
    ================================*/


    public void show() {
        hideEmptyView();
        mTvTip.setVisibility(GONE);
        mTvBigTip.setVisibility(GONE);
        mTvSmallTip.setVisibility(GONE);
        if (getVisibility() != VISIBLE)
            setVisibility(VISIBLE);
    }

    public void dismiss() {
        hideEmptyView();
        if (getVisibility() != GONE)
            setVisibility(GONE);
    }

    public boolean isShowing() {
        return getVisibility() == VISIBLE;
    }

    public void hideProgress() {
        mProgressBar.setVisibility(GONE);
    }

    //Show Loading并设置loading文字:tip
    public void setTip(String tip) {
        hideEmptyView();
        mTvEmpty.setOnClickListener(null);
        if (mProgressBar != null && mProgressBar.getVisibility() != VISIBLE)
            mProgressBar.setVisibility(VISIBLE);
        mTip = tip;
        if (mTvTip != null) {
            if (mTvTip.getVisibility() != VISIBLE) mTvTip.setVisibility(VISIBLE);
            mTvTip.setText(mTip);
        }
    }

    //Show Loading并设置默认loading文字
    public void setRetryDefaultTip() {
        hideEmptyView();
        mTvEmpty.setOnClickListener(null);
        if (mProgressBar != null && mProgressBar.getVisibility() != VISIBLE)
            mProgressBar.setVisibility(VISIBLE);
        mTip = defaultTip;
        if (mTvTip != null) {
            if (mTvTip.getVisibility() != VISIBLE) mTvTip.setVisibility(VISIBLE);
            mTvTip.setText(mTip);
        }
    }

    public void setErrMsg(String big, String small) {
        hideEmptyView();
        hideProgress();
        mTvTip.setVisibility(GONE);
        mTvBigTip.setVisibility(VISIBLE);
        mTvSmallTip.setVisibility(VISIBLE);
        mTvBigTip.setText(big);
        mTvSmallTip.setText(small);
    }

    public void setErrMsg(String msg) {
        if (NetWorkUtils.isNetworkAvailable(getContext())) {
            errTextDefault();
            mTvTip.setText(msg);
        } else {
            showEmpty();
        }
    }

    public void setErrMsg(int msgRes) {
        if (NetWorkUtils.isNetworkAvailable(getContext())) {
            errTextDefault();
            mTvTip.setText(msgRes);
        } else {
            showEmpty();
        }
    }

    public void setErrMsgRetry(String msg) {
        if (NetWorkUtils.isNetworkAvailable(getContext())) {
            errTextDefault();
            mTvTip.setText(msg + "\n\n点击重试");
        } else {
            showEmpty();
        }
    }

    private void showEmpty() {
        MTool.Toast(getContext(), R.string.netNoGeilible);
        if (getVisibility() != VISIBLE) setVisibility(VISIBLE);
        resetEmptyImg(R.drawable.ic_no_net);
        if (mClick != null) {
            mTvEmpty.setText(R.string.noNetWork);
            mTvEmpty.setOnClickListener(v -> mClick.handleOnCallBack());
        } else
            mTvEmpty.setText(R.string.noNetWorkB);
        emptyView.setVisibility(VISIBLE);
    }

    private void errTextDefault() {
        hideEmptyView();
        if (getVisibility() != VISIBLE)
            setVisibility(VISIBLE);
        hideProgress();
        mTvTip.setVisibility(VISIBLE);
    }

    public void showEmptyView() {
        showEmptyView(null, -1);
    }

    public void showEmptyView(String emptyTip) {
        showEmptyView(emptyTip, -1);
    }

    public void showEmptyView(String emptyTip, int emptyImgRes) {
        if (getVisibility() != VISIBLE) setVisibility(VISIBLE);

        if (NetWorkUtils.isNetworkAvailable(getContext())) {
            if (!TextUtils.isEmpty(emptyTip))
                mTvEmpty.setText(emptyTip);
            if (emptyImgRes != -1)
                resetEmptyImg(emptyImgRes);
        } else {
            MTool.Toast(getContext(), R.string.netNoGeilible);
            resetEmptyImg(R.drawable.ic_no_net);
            mTvEmpty.setText(R.string.noNetWork);
            mTvEmpty.setOnClickListener(v -> {
                if (mClick != null) mClick.handleOnCallBack();
            });
        }

        emptyView.setVisibility(VISIBLE);
    }

    public void hideEmptyView() {
        emptyView.setVisibility(GONE);
    }

    public void emptyAlignTop() {
        emptyView.setGravity(Gravity.CENTER_HORIZONTAL);
    }

    public void resetEmptyImg(int imgRes, boolean reset) {
        if (reset)
            resetEmptyImg(res, 780, 780);
        else
            resetEmptyImg(imgRes);
    }

    public void resetEmptyImg(int imgRes) {
        res = imgRes;
        mEmptyImgView.setBackgroundResource(imgRes);
    }

    public void resetEmptyImg(int imgRes, int width, int height) {
        res = imgRes;
        mEmptyImgView.setBackgroundResource(imgRes);
        LayoutParams params = (LayoutParams) mEmptyImgView.getLayoutParams();
        params.width = (int) (width * ScreenUtil.mScale);
        params.height = (int) (height * ScreenUtil.mScale);
        mEmptyImgView.setLayoutParams(params);
    }

    public void setEmptyTip(String emptyTip) {
        mTvEmpty.setText(emptyTip);
    }

    public void addEmptyView(View emptyView) {
        wholeBody.addView(emptyView);
    }

    public void hideAllTip() {
        hideEmptyView();
        mProgressBar.setVisibility(GONE);
        mTvTip.setVisibility(GONE);
        mTvBigTip.setVisibility(GONE);
        mTvSmallTip.setVisibility(GONE);
    }


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
    private TextView mTvTip, mTvBigTip, mTvSmallTip, mTvEmpty;
    private LinearLayout emptyView;
    private FrameLayout wholeBody;
    private ProgressBar mProgressBar;
    private View llBody;

    private ICommonEmptyCallBack mClick;
    private String mTip;

    private ImageView mEmptyImgView;
    private int res;

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.m_loading_dialog, this);
        mTvTip = findViewById(R.id.content);
        mTvBigTip = findViewById(R.id.tv_big);
        mTvSmallTip = findViewById(R.id.tv_small);
        llBody = findViewById(R.id.ll_body);
        wholeBody = findViewById(R.id.progress_body);
        mProgressBar = findViewById(R.id.progressBar);
        emptyView = findViewById(R.id.ll_empty);
        mTvEmpty = findViewById(R.id.tv_emptyTip);
        mEmptyImgView = findViewById(R.id.iv_empty);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MLoadingDialog);
        try {
            String tip = a.getString(R.styleable.MLoadingDialog_mld_tip);
            mTip = tip == null ? defaultTip : tip;
            mTvTip.setText(mTip);
            int txtColor = a.getColor(R.styleable.MLoadingDialog_mld_txt_color, -123);
            if (txtColor != -123) {
                mTvTip.setTextColor(txtColor);
            }
            int bgColor = a.getColor(R.styleable.MLoadingDialog_mld_load_bg, Color.WHITE);
            llBody.setBackgroundColor(bgColor);
//            boolean noShape = a.getBoolean(R.styleable.MLoadingDialog_noShape, false);
//            if (noShape) {
//                mProgressBar.setBackgroundResource(R.drawable.drawable_transparent);
//            }
            boolean defineId = a.getBoolean(R.styleable.MLoadingDialog_mld_defined_id, false);
            if (!defineId)
                setId(R.id.m_loading);
        } finally {
            a.recycle();
        }

        setClickable(true);
    }

    public void setMClick(ICommonEmptyCallBack listener) {
        mClick = listener;

        mTvEmpty.setOnClickListener(v -> mClick.handleOnCallBack());
    }
}