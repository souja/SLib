package com.souja.lib.base;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.souja.lib.R;
import com.souja.lib.inter.IBaseLazyFragmentListener;
import com.souja.lib.utils.ScreenUtil;


/**
 * Created by ydz on 2015/5/11.
 */
public abstract class BaseLazyFragmentB extends BaseFragment implements IBaseLazyFragmentListener {

    private FrameLayout contentView;
    private View progressView, emptyView;
    private TextView mTvTip, mTvEmpty;
    private ProgressBar mProgressBar;
    public View _contentView;

    private ImageView mEmptyImgView;
    private int res;

    public void resetEmptyImg(int imgRes, int width, int height) {
        if (imgRes == res) return;
        res = imgRes;
        mEmptyImgView.setBackgroundResource(imgRes);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mEmptyImgView.getLayoutParams();
        params.width = (int) (width * ScreenUtil.mScale);
        params.height = (int) (height * ScreenUtil.mScale);
        mEmptyImgView.setLayoutParams(params);
    }

    @Override
    public int setupLayoutRes() {
        return R.layout.base_fb;
    }

    @Override
    public void initMain() {
        contentView = _rootView.findViewById(R.id.base_f_frame);
        progressView = _rootView.findViewById(R.id.rl_progress);
        emptyView = _rootView.findViewById(R.id.layout_empty);
        mEmptyImgView = _rootView.findViewById(R.id.iv_empty);
        mProgressBar = _rootView.findViewById(R.id.progress_bar);
        mTvTip = _rootView.findViewById(R.id.content);
        mTvEmpty = _rootView.findViewById(R.id.tv_emptyTip);

        progressView.setOnClickListener(vv -> {
            if (mClick != null)
                mClick.onLoadingClick();
        });
    }


    public void setContentView(View v) {
        ScreenUtil.initScale(v);
        contentView.addView(v, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initPrepare();
    }

    public void setTip(String tip) {
        setMClick(null);
        if (mProgressBar.getVisibility() != View.VISIBLE) mProgressBar.setVisibility(View.VISIBLE);
        mTvTip.setText(tip);
    }

    public void setRetryDefaultTip() {
        setMClick(null);
        if (progressView != null && progressView.getVisibility() != View.VISIBLE)
            progressView.setVisibility(View.VISIBLE);
        if (mProgressBar.getVisibility() != View.VISIBLE) mProgressBar.setVisibility(View.VISIBLE);
        if (mTvTip != null) {
            if (mTvTip.getVisibility() != View.VISIBLE) mTvTip.setVisibility(View.VISIBLE);
            mTvTip.setText("请稍候...");
        }
    }


    public void setErrMsgRetry(String msg) {
        if (progressView.getVisibility() != View.VISIBLE)
            progressView.setVisibility(View.VISIBLE);
        hideProgress();
        mTvTip.setVisibility(View.VISIBLE);
        mTvTip.setText(msg + "\n\n点击重试");
    }

    public void setErrMsg(String msg) {
        hideProgress();
        mTvTip.setVisibility(View.VISIBLE);
        mTvTip.setText(msg);
    }

    public void setErrMsg(int msgRes) {
        if (progressView.getVisibility() != View.VISIBLE)
            progressView.setVisibility(View.VISIBLE);
        hideProgress();
        mTvTip.setVisibility(View.VISIBLE);
        mTvTip.setText(msgRes);
    }

    public void hideProgress() {
        mProgressBar.setVisibility(View.GONE);
    }

    private MLoadingClick mClick;

    public void setMClick(MLoadingClick listener) {
        mClick = listener;
    }

    public interface MLoadingClick {
        void onLoadingClick();
    }

    public void ShowEmptyView() {
        emptyView.setVisibility(View.VISIBLE);
    }

    public void ShowEmptyView(String emptyTip) {
        mTvEmpty.setText(emptyTip);
        emptyView.setVisibility(View.VISIBLE);
    }

    public void setEmptyTip(String emptyTip) {
        mTvEmpty.setText(emptyTip);
    }

    public void HideEmptyView() {
        emptyView.setVisibility(View.GONE);
    }

    public boolean isProgressing() {
        return progressView.getVisibility() == View.VISIBLE;
    }

    @Override
    public void ShowProgress() {
        if (progressView.getVisibility() != View.VISIBLE)
            progressView.setVisibility(View.VISIBLE);
        if (contentView.getVisibility() != View.GONE)
            contentView.setVisibility(View.GONE);
    }

    @Override
    public void ShowContentView() {
        if (progressView.getVisibility() != View.GONE)
            progressView.setVisibility(View.GONE);
        if (contentView.getVisibility() != View.VISIBLE)
            contentView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
