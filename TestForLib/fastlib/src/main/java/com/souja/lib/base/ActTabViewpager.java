package com.souja.lib.base;

import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.souja.lib.R;
import com.souja.lib.utils.MTool;
import com.souja.lib.widget.TitleBar;

import org.xutils.common.util.LogUtil;


/**
 * 页面结构：TitleBar + TabLayout + ViewPager
 * */
public abstract class ActTabViewpager extends ActBase {
    public TitleBar mTitleBar;
    public TabLayout tabLayout;
    public ViewPager viewpager;

    /**
     * 设置各页面小标题
     * */
    protected abstract String[] setTitles();

    /**
     * 设置标题文字间距
     * */
    public abstract int[] setTabMargins();


    private String[] mTitles;
    private BaseFragment[] mFragments;
    protected int tabLeftMargin, tabRightMargin;

    @Override
    protected int setViewRes() {
        return R.layout.act_tab_viewpaer;
    }

    private void initViews(){
        mTitleBar = findViewById(R.id.m_title);
        tabLayout = findViewById(R.id.tabLayout);
        viewpager = findViewById(R.id.viewpager);
    }

    @Override
    protected void initMain() {
        initViews();
        mTitles = setTitles();
        mFragments = new BaseFragment[mTitles.length];
        initFragments(mFragments);
        for (String s : mTitles) {
            tabLayout.addTab(tabLayout.newTab().setText(s));
        }
        int[] margins = setTabMargins();
        if (margins != null && margins.length > 0) {
            if (margins.length == 1) {
                tabLeftMargin = tabRightMargin = margins[0];
            } else {
                tabLeftMargin = margins[0];
                tabRightMargin = margins[1];
            }
        }
        LogUtil.e("[tab] left margin:" + tabLeftMargin + "  right margin:" + tabRightMargin);
        if (tabLeftMargin == 0) tabLeftMargin = 10;
        if (tabRightMargin == 0) tabRightMargin = 10;
        MTool.reflex(tabLayout, tabLeftMargin, tabRightMargin);
        viewpager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mFragments[position];
            }

            @Override
            public int getCount() {
                return mTitles.length;
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return mTitles[position];
            }
        });
        tabLayout.setupWithViewPager(viewpager);
        viewpager.setOffscreenPageLimit(mTitles.length - 1);
    }

    protected abstract void initFragments(BaseFragment[] fragments);

}
