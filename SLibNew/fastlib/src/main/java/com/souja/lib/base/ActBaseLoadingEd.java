package com.souja.lib.base;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.souja.lib.R;
import com.souja.lib.R2;
import com.souja.lib.utils.ScreenUtil;
import com.souja.lib.widget.TitleBar;

import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class ActBaseLoadingEd extends ActBaseEd {
    @BindView(R2.id.m_title)
    public TitleBar mTitleBar;
    @BindView(R2.id.progressBg)
    public View loadingView;
    @BindView(R2.id.progressBar)
    ProgressBar progressBar;
    @BindView(R2.id.tv_loadingTip)
    TextView tvLoadingTip;
    @BindView(R2.id.tv_error)
    TextView errorView;

    FrameLayout mBodyView;

    private String defaultLoadingTip = "请稍候...";

    @Override
    public int setViewRes() {
        return R.layout.act_base_title_loading;
    }

    @Override
    public void initMain() {
        mBodyView = findViewById(R.id.body);

        View contentView = getLayoutInflater().inflate(setupContentRes(), null);
        ScreenUtil.initScale(contentView);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
//        params.topToBottom = R.id.m_title;
//        params.leftToLeft = R.id.body;
//        params.rightToRight = R.id.body;
//        params.bottomToBottom = R.id.body;
        mBodyView.addView(contentView, params);

        ButterKnife.bind(this);

        initContentView();
    }

    public abstract int setupContentRes();

    public abstract void initContentView();

    public void setContentBg(int color) {
        mBodyView.setBackgroundColor(color);
    }

    public void showLoading(String... tip) {
        hideErrorView();
        if (tip != null && tip.length > 0) {
            tvLoadingTip.setText(tip[0]);
        } else {
            tvLoadingTip.setText(defaultLoadingTip);
        }
        tvLoadingTip.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        loadingView.setVisibility(View.VISIBLE);
    }

    public boolean isDialogShowing() {
        return progressBar.getVisibility() == View.VISIBLE;
    }

    public void hideLoading() {
        tvLoadingTip.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        loadingView.setVisibility(View.GONE);
    }

    public void showErrorView(int errorMsgRes) {
        showErrorView(getResources().getString(errorMsgRes));
    }

    public void showErrorView(String errorMsg) {
        hideLoading();
        errorView.setText(TextUtils.isEmpty(errorMsg) ?
                getResources().getString(R.string.sorry) : errorMsg);
        errorView.setVisibility(View.VISIBLE);
    }

    public void hideErrorView() {
        if (errorView.getVisibility() != View.GONE)
            errorView.setVisibility(View.GONE);
    }

    public void setLoadingTip(int tipRes) {
        setLoadingTip(getResources().getString(tipRes));
    }

    public void setLoadingTip(String tipStr) {
        defaultLoadingTip = tipStr;
    }
}
