package com.souja.lib.base;

import android.view.View;
import android.view.ViewGroup;


import androidx.constraintlayout.widget.ConstraintLayout;

import com.souja.lib.R;
import com.souja.lib.utils.ScreenUtil;

import org.xutils.common.util.LogUtil;


/**
 * Lazy Fragment
 * Created by ydz on 2020/1/13.
 */
public abstract class BaseLazyFragmentNew extends BaseFragmentNew {


    public abstract int setFragContentViewRes();

    public abstract void initPage();

    private boolean bCreate = true;
    private boolean bFirstVisible = true;

    protected ConstraintLayout _contentView;

    private void onFirstUserVisible() {
        setContentView(getLayoutInflater().inflate(setFragContentViewRes(), null, false));
        initPage();
    }


    @Override
    protected int setupLayoutRes() {
        return R.layout.base_lazy_fragment;
    }

    @Override
    protected void initMain() {
        _contentView = _rootView.findViewById(R.id.bodyView);
    }

    public void setContentView(View v) {
        if (v != null) {
            ScreenUtil.initScale(v);
            _contentView.addView(v, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtil.e("onResume");
        if (bCreate) {
            bCreate = false;
            onFirstUserVisible();
        }
        if (bFirstVisible) {
            bFirstVisible = false;
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        LogUtil.e("onHiddenChanged:" + bCreate);
        super.onHiddenChanged(hidden);

    }

}
