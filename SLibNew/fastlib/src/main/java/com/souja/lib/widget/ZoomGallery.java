package com.souja.lib.widget;

import android.animation.Animator;
import android.content.Context;
import androidx.viewpager.widget.ViewPager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.souja.lib.R;
import com.souja.lib.tools.DensityUtil;

import java.util.ArrayList;

public class ZoomGallery extends RelativeLayout {

    private PopZoomGallery mPop;

    private ViewPager mViewPager;
    private ImageView mBackground;
    private PointIndicator mIndicator;

    private ZoomGalleryAdapter mAdapter;

    private ArrayList<ZoomImageModel> mZoomImageList;

    private ZoomGalleryAdapter.ZoomGalleryInstantiateItem mItemListener;

    public ZoomGallery(Context context, PopZoomGallery popZoomGallery,
                       ZoomGalleryAdapter.ZoomGalleryInstantiateItem itemListener) {
        super(context);
        mPop = popZoomGallery;
        mItemListener = itemListener;
        init(context);
    }

    private void init(Context context) {
        //set backgroudColor
        mBackground = new ImageView(getContext());
        mBackground.setBackgroundColor(getResources().getColor(R.color.black_19));
        LayoutParams layoutParams1 = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addView(mBackground, layoutParams1);

        //view pager
        mViewPager = new HackyViewPager(context);
        LayoutParams layoutParams2 = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addView(mViewPager, layoutParams2);
        mAdapter = new ZoomGalleryAdapter(position -> close(), mItemListener);
        mViewPager.setAdapter(mAdapter);

        //indicator
        mIndicator = new PointIndicator(context);
        mIndicator.setViewPager(mViewPager);
        LayoutParams layoutParams3 = new LayoutParams(LayoutParams.MATCH_PARENT, DensityUtil.dip2px(context, 8));
        layoutParams3.bottomMargin = DensityUtil.dip2px(context, 40);
        layoutParams3.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        addView(mIndicator, layoutParams3);
    }

    public void open(ArrayList<ZoomImageModel> zoomImageList, final int position) {
        mZoomImageList = zoomImageList;
        mAdapter.update(mZoomImageList);
        mIndicator.notifyDataSetChanged();
        mViewPager.setCurrentItem(position);

        mViewPager.post(() ->
                ZoomImageUtil.zoomImageFromThumb(mZoomImageList.get(position).rect, mBackground, mViewPager));
    }

    public void close() {
        ZoomImageUtil.closeZoomAnim(mZoomImageList.get(mViewPager.getCurrentItem()).rect,
                mBackground, mViewPager, new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (mPop != null) {
                            try {
                                mPop.dismiss();
                            } catch (Exception e) {
                            }
                        }
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
