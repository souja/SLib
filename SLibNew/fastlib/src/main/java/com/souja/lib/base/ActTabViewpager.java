package com.souja.lib.base;

import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.souja.lib.R;
import com.souja.lib.utils.MTool;
import com.souja.lib.widget.TitleBar;



/**
 * 页面结构：TitleBar + TabLayout + ViewPager
 */
public abstract class ActTabViewpager extends ActBase {

    /**
     * 设置各页面小标题
     */
    public abstract String[] setTitles();

    /**
     * 设置标题文字间距
     */
    public abstract int[] setTabMargins();

    @Override
    public int setViewRes() {
        return R.layout.act_tab_viewpaer;
    }

    public TitleBar mTitleBar;
    public TabLayout tabLayout;
    public ViewPager viewpager;
    public String[] mTitles;
    public BaseFragment[] mFragments;
    public int tabLeftMargin, tabRightMargin;

    private void initViews() {
        mTitleBar = findViewById(R.id.m_title);
        tabLayout = findViewById(R.id.tabLayout);
        viewpager = findViewById(R.id.viewpager);
    }


    @Override
    public void initMain() {
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
//        LogUtil.e("[tab] left margin:" + tabLeftMargin + "  right margin:" + tabRightMargin);
        if (tabLeftMargin == 0) tabLeftMargin = 10;
        if (tabRightMargin == 0) tabRightMargin = 10;
        MTool.reflex(tabLayout, tabLeftMargin, tabRightMargin);
        viewpager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager(),FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
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

    public abstract void initFragments(BaseFragment[] fragments);

}
