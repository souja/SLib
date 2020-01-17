package com.souja.lib.widget;

import android.content.Context;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import uk.co.senab.photoview.PhotoView;

public class ZoomGalleryAdapter extends PagerAdapter {

    private ArrayList<ZoomImageModel> mZoomImageList;

    private ZoomGalleryTapListener mListener;
    private ZoomGalleryInstantiateItem mItemListener;

    public ZoomGalleryAdapter(ZoomGalleryTapListener listener, ZoomGalleryInstantiateItem itemListener) {
        mListener = listener;
        mItemListener = itemListener;
    }

    public void update(ArrayList<ZoomImageModel> zoomImageList) {
        mZoomImageList = zoomImageList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mZoomImageList == null ? 0 : mZoomImageList.size();
    }

    @Override
    public View instantiateItem(final ViewGroup container, final int position) {
        Context context = container.getContext();

        PhotoView photoView = createPhotoView(context, position);

        if (mItemListener != null) {
            mItemListener.onItemInstantiate(container, position, photoView, mZoomImageList.get(position));
        }

        container.addView(photoView, ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.MATCH_PARENT);
        return photoView;
    }

    private PhotoView createPhotoView(Context context, int position) {
        PhotoView photoView = new PhotoView(context);

        photoView.setOnPhotoTapListener((view, v, v2) -> {
            if (mListener != null) {
                mListener.tap(position);
            }
        });
        photoView.setOnViewTapListener((view, v, v2) -> {
            if (mListener != null) {
                mListener.tap(position);
            }
        });
        return photoView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public interface ZoomGalleryTapListener {
        void tap(int position);
    }

    public interface ZoomGalleryInstantiateItem {
        void onItemInstantiate(ViewGroup container, final int position, PhotoView view, ZoomImageModel model);
    }

}
