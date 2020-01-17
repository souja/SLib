package com.souja.lib.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.souja.lib.R;
import com.souja.lib.inter.IBaseLazyFragmentListener;
import com.souja.lib.utils.ScreenUtil;
import com.souja.lib.widget.MLoadingDialog;


/**
 * Created by ydz on 2015/5/11.
 */
public abstract class BaseLazyFragmentB extends BaseFragment implements IBaseLazyFragmentListener {

    //Show Loading并设置loading文字:tip
    public void setTip(String tip) {
        mLoadingDialog.setTip(tip);
    }

    //Show Loading并设置默认loading文字
    public void setRetryDefaultTip() {
        mLoadingDialog.setRetryDefaultTip();
    }

    public void setErrMsg(String msg) {
        mLoadingDialog.setErrMsg(msg);
    }

    public void setErrMsg(int msgRes) {
        mLoadingDialog.setErrMsg(msgRes);
    }

    public void setErrMsgRetry(String msg) {
        mLoadingDialog.setErrMsgRetry(msg);
    }

    public void ShowEmptyView() {
        mLoadingDialog.showEmptyView();
    }

    public void ShowEmptyView(String emptyTip) {
        mLoadingDialog.showEmptyView(emptyTip);
    }

    public void setEmptyTip(String emptyTip) {
        mLoadingDialog.setEmptyTip(emptyTip);
    }

    public void hideEmptyView() {
        mLoadingDialog.hideEmptyView();
    }

    public void hideProgress() {
        mLoadingDialog.hideProgress();
    }

    public void setMClick(View.OnClickListener listener) {
        mLoadingDialog.setMClick(listener);
    }

    public void resetEmptyImg(int imgRes, boolean reset) {
        mLoadingDialog.resetEmptyImg(imgRes, reset);
    }

    public void resetEmptyImg(int imgRes) {
        mLoadingDialog.resetEmptyImg(imgRes);
    }

    public void resetEmptyImg(int imgRes, int width, int height) {
        mLoadingDialog.resetEmptyImg(imgRes, width, height);
    }

    public void emptyAlignTop() {
        mLoadingDialog.emptyAlignTop();
    }

    public FrameLayout contentView;
    public MLoadingDialog mLoadingDialog;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initPrepare();
    }

    @Override
    public int setupLayoutRes() {
        return R.layout.base_fb;
    }

    @Override
    public void initMain() {
        contentView = _rootView.findViewById(R.id.base_f_frame);
        mLoadingDialog = _rootView.findViewById(R.id.m_loading);
    }

    public void setContentView(View v) {
        ScreenUtil.initScale(v);
        contentView.addView(v, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    public boolean isProgressing() {
        return mLoadingDialog.isShowing();
    }

    @Override
    public void ShowProgress() {
        mLoadingDialog.show();
        if (contentView.getVisibility() != View.GONE)
            contentView.setVisibility(View.GONE);
    }

    @Override
    public void ShowContentView() {
        mLoadingDialog.dismiss();
        if (contentView.getVisibility() != View.VISIBLE)
            contentView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public abstract int setFragContentViewRes();

    public abstract void initPage();

    @Override
    public void onFirstUserVisible() {
        View _contentView = LayoutInflater.from(mBaseActivity).inflate(setFragContentViewRes(),
                null, false);
        setContentView(_contentView);
        initPage();
    }
}
