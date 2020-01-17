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

public abstract class ActBaseLoading extends ActBase {
    @BindView(R2.id.m_title)
    public TitleBar mTitleBar;
    @BindView(R2.id.progressBg)
    public View whiteBg;
    @BindView(R2.id.progressBar)
    ProgressBar progressBar;
    @BindView(R2.id.tv_loadingTip)
    TextView tvLoadingTip;
    @BindView(R2.id.tv_error)
    TextView errorView;

    private FrameLayout mBodyView;

    private String defaultLoadingTip = "请稍候...";

    private LoadingViewListener mListener;

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
        whiteBg.setOnClickListener(v -> {
            if (mListener != null) mListener.onViewClick();
        });
        initContentView();
    }

    public abstract int setupContentRes();

    public abstract void initContentView();

    public interface LoadingViewListener {
        void onViewClick();
    }

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
        if (tvLoadingTip.getVisibility() != View.VISIBLE)
            tvLoadingTip.setVisibility(View.VISIBLE);
        if (progressBar.getVisibility() != View.VISIBLE)
            progressBar.setVisibility(View.VISIBLE);
        if (whiteBg.getVisibility() != View.VISIBLE)
            whiteBg.setVisibility(View.VISIBLE);
    }

    public boolean isDialogShowing() {
        return progressBar.getVisibility() == View.VISIBLE;
    }

    public void hideLoading() {
        if (tvLoadingTip.getVisibility() != View.GONE)
            tvLoadingTip.setVisibility(View.GONE);
        if (progressBar.getVisibility() != View.GONE)
            progressBar.setVisibility(View.GONE);
        if (whiteBg.getVisibility() != View.GONE)
            whiteBg.setVisibility(View.GONE);
    }

    public void showErrorView(int errorMsgRes) {
        showErrorView(getResources().getString(errorMsgRes));
    }

    public void showErrorView(String errorMsg) {
        showErrorView(errorMsg, null);
    }

    public void showErrorRetry(String errorMsg, LoadingViewListener listener) {
        showErrorView(errorMsg + "\n\n点击重试", listener);
    }

    public void showErrorView(String errorMsg, LoadingViewListener listener) {
        if (tvLoadingTip.getVisibility() != View.GONE)
            tvLoadingTip.setVisibility(View.GONE);
        if (progressBar.getVisibility() != View.GONE)
            progressBar.setVisibility(View.GONE);
        errorView.setText(TextUtils.isEmpty(errorMsg) ?
                getResources().getString(R.string.sorry) : errorMsg);
        if (whiteBg.getVisibility() != View.VISIBLE)
            whiteBg.setVisibility(View.VISIBLE);
        if (errorView.getVisibility() != View.VISIBLE)
            errorView.setVisibility(View.VISIBLE);

        mListener = listener;
    }

    public void hideErrorView() {
        if (whiteBg.getVisibility() != View.GONE)
            whiteBg.setVisibility(View.GONE);
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
