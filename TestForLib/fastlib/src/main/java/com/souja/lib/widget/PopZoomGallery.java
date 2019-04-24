package com.souja.lib.widget;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.souja.lib.R;
import com.souja.lib.utils.MTool;
import com.souja.lib.utils.ScreenUtil;

import java.util.ArrayList;

/**
 * Created by wangyue on 2015/3/9.
 */
public class PopZoomGallery extends PopupWindow {

    private Context mContext;
    private RelativeLayout contentView;
    private View mViewBg;
    private TextView mTvIndex;
    private HackyViewPager mViewPager;
    private ZoomGalleryAdapter mAdapter;
    //    private PopZoomGallery _this;
    private ArrayList<ZoomImageModel> mZoomImageList;

    private ZoomGalleryAdapter.ZoomGalleryInstantiateItem mItemListener;

    public PopZoomGallery(Context context, ArrayList<ZoomImageModel> zoomImageList,
                          ZoomGalleryAdapter.ZoomGalleryInstantiateItem itemListener) {
        super(context);
//        _this = this;
        mContext = context;
        mZoomImageList = zoomImageList;
        mItemListener = itemListener;
        init();
    }

    private void init() {
        initView();
        setupPop();
    }

    private void initView() {
        contentView = (RelativeLayout) LayoutInflater.from(mContext)
                .inflate(R.layout.layout_pop_zoom_gallery, null);
        ScreenUtil.initScale(contentView);
        mViewPager = contentView.findViewById(R.id.vp_gallery);
        mViewBg = contentView.findViewById(R.id.v_bg);
        mTvIndex = contentView.findViewById(R.id.tv_index);
        int naviHeight = MTool.getNavigationBarHeight(mContext);
        if (naviHeight > 0) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mTvIndex.getLayoutParams();
            int botMargin = params.bottomMargin;
            botMargin += naviHeight;
            params.bottomMargin = botMargin;
            mTvIndex.setLayoutParams(params);
        }
        mAdapter = new ZoomGalleryAdapter(position -> close(), mItemListener);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setupIndex(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setupPop() {
        setContentView(contentView);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setFocusable(true);
        setBackgroundDrawable(new ColorDrawable(Color.parseColor("#90000000")));// 给popupWindow设置背景
        setAnimationStyle(0);
        setClippingEnabled(false);
    }

    //显示
    public void showPop(View view, int position) {
        showAtLocation(view, Gravity.BOTTOM, 0, 0);

        open(mZoomImageList, position);
    }

    private void setupIndex(int position) {
        mTvIndex.setText(String.valueOf((position + 1) + "/" + mZoomImageList.size()));
    }

    private void open(ArrayList<ZoomImageModel> zoomImageList, final int position) {
        mZoomImageList = zoomImageList;
        mAdapter.update(mZoomImageList);
        setupIndex(position);
        mViewPager.setCurrentItem(position);

        mViewPager.post(() -> ZoomImageUtil.zoomImageFromThumb(mZoomImageList.get(position).rect,
                mViewBg, mViewPager));
    }


    public void close() {
        ZoomImageUtil.closeZoomAnim(mZoomImageList.get(mViewPager.getCurrentItem()).rect,
                mViewBg, mViewPager, new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        dismiss();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                });

    }

}
