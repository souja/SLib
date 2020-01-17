package com.souja.lib.widget;

/**
 * 菊花...
 * Created by Yangdz on 2015/2/4.
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.souja.lib.R;
import com.souja.lib.inter.ICommonEmptyCallBack;
import com.souja.lib.utils.MTool;
import com.souja.lib.utils.NetWorkUtils;
import com.souja.lib.utils.ScreenUtil;


public class MLoadingDialog extends ConstraintLayout {

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

    /*================================
                 PUBLIC FUNCTIONS
    ================================*/


    public void show() {
        hideEmptyView();
        mTvTip.setVisibility(GONE);
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
        mEmptyView.setOnClickListener(null);
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
        mEmptyView.setOnClickListener(null);
        if (mProgressBar != null && mProgressBar.getVisibility() != VISIBLE)
            mProgressBar.setVisibility(VISIBLE);
        mTip = defaultTip;
        if (mTvTip != null) {
            if (mTvTip.getVisibility() != VISIBLE) mTvTip.setVisibility(VISIBLE);
            mTvTip.setText(mTip);
        }
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
        setErrMsgRetry(msg, null);
    }

    public void setErrMsgRetry(String msg, View.OnClickListener listener) {
//        if (callBack != null) {
//            mClick = callBack;
//        }
        if (NetWorkUtils.isNetworkAvailable(getContext())) {
            errTextDefault();
            mTvTip.setText(msg + "\n\n点击重试");
            if (listener != null)
                mEmptyView.setOnClickListener(listener);
//            mEmptyView.setOnClickListener(v -> {
//                    LogUtil.e("click12213..");
//                    mClick.handleOnCallBack();
//                });
        } else {
            showEmpty();
        }
    }

    public void setMClick(View.OnClickListener listener) {
//        mClick = listener;

//        mEmptyView.setOnClickListener(v -> {
//            LogUtil.e("click...");
//            mClick.handleOnCallBack();
//        });
        mEmptyView.setOnClickListener(listener);
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
            mEmptyView.setOnClickListener(v -> {
                if (mClick != null) mClick.handleOnCallBack();
            });
        }

        mEmptyView.setVisibility(VISIBLE);
    }

    public void hideEmptyView() {
        mEmptyView.setVisibility(GONE);
    }

    public void emptyAlignTop() {
        mEmptyView.setGravity(Gravity.CENTER_HORIZONTAL);
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

    /*================================
                 PRIVATE FUNCTIONS
    ================================*/

    private void showEmpty() {
        MTool.Toast(getContext(), R.string.netNoGeilible);
        if (getVisibility() != VISIBLE) setVisibility(VISIBLE);
        resetEmptyImg(R.drawable.ic_no_net);
        if (mClick != null) {
            mTvEmpty.setText(R.string.noNetWork);
            mEmptyView.setOnClickListener(v -> mClick.handleOnCallBack());
        } else
            mTvEmpty.setText(R.string.noNetWorkB);
        mEmptyView.setVisibility(VISIBLE);
    }

    private void errTextDefault() {
        hideEmptyView();
        if (getVisibility() != VISIBLE)
            setVisibility(VISIBLE);
        hideProgress();
        mTvTip.setVisibility(VISIBLE);
    }

    private TextView mTvTip, mTvEmpty;
    private ImageView mEmptyImgView;
    private LinearLayout mEmptyView;
    private ProgressBar mProgressBar;

    private ICommonEmptyCallBack mClick;

    private final String defaultTip = "请稍候...";
    private String mTip;
    private int res;

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.m_loading_dialog, this);
        mTvTip = findViewById(R.id.content);
        mProgressBar = findViewById(R.id.progressBar);
        mEmptyView = findViewById(R.id.ll_empty);
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
            setBackgroundColor(bgColor);
            boolean defineId = a.getBoolean(R.styleable.MLoadingDialog_mld_defined_id, false);
            if (!defineId)
                setId(R.id.m_loading);
        } finally {
            a.recycle();
        }

        setClickable(true);
        mEmptyView.setClickable(true);
    }
}