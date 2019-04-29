package com.souja.lib.base;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.souja.lib.R;
import com.souja.lib.utils.ScreenUtil;

import org.xutils.common.util.LogUtil;

import java.util.List;

/**
 * 图片列表适配器
 * Created by Ydz on 2017/3/16 0016.
 */

public class PhotoAdapter extends MBaseAdapter<String> {
    private PhotoClickListener mListener;

    public PhotoAdapter(Context context, List<String> pathList, PhotoClickListener listener) {
        super(context, pathList);
        mListener = listener;
    }

    public void addPath(String path) {
        mList.add(0, path);
        notifyDataSetChanged();
    }

    public void setPhotoPathList(List<String> pathList) {
        mList = pathList;
        notifyDataSetChanged();
    }

    public List<String> getPathList() {
        return mList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateView(ViewGroup parent, int viewType) {
        return new HolderPhoto(LayoutInflater.from(mContext)
                .inflate(R.layout.item_photo, parent, false));
    }

    @Override
    public void onBindView(RecyclerView.ViewHolder holder, int position) {
        HolderPhoto mHolder = (HolderPhoto) holder;
        if (position == 0) {
            Uri path = Uri.parse("file:///android_asset/images/gallery_to_camera.png");
            Glide.with(mContext)
                    .load(path)
                    .into(mHolder.mIvPreview);
            mHolder.mCheckBox.setVisibility(View.GONE);
            mHolder.vChoose.setVisibility(View.GONE);
            mHolder.vTemp.setVisibility(View.GONE);
        } else {
            mHolder.vChoose.setVisibility(View.VISIBLE);
            mHolder.mCheckBox.setVisibility(View.VISIBLE);
            int fPosition = position - 1;
            String path = mList.get(fPosition);
            Glide.with(mContext)
                    .load(path)
                    .into(mHolder.mIvPreview);

            if (ActPhotoGallery.getInstance().selectedPathList.contains(path)) {
                mHolder.mCheckBox.setChecked(true);
                mHolder.vTemp.setVisibility(View.VISIBLE);
            } else {
                mHolder.mCheckBox.setChecked(false);
                mHolder.vTemp.setVisibility(View.GONE);
            }
            mHolder.vChoose.setOnClickListener(view -> {
                if (ActPhotoGallery.getInstance().maxCount == 1) {
                    if (!ActPhotoGallery.getInstance().selectedPathList.contains(path)) {
                        ActPhotoGallery.getInstance().selectedPathList.clear();
                        ActPhotoGallery.getInstance().selectedPathList.add(path);
                    } else {
                        ActPhotoGallery.getInstance().selectedPathList.remove(path);
                    }
                    notifyDataSetChanged();
                } else {
                    if (!ActPhotoGallery.getInstance().selectedPathList.contains(path)
                            && ActPhotoGallery.getInstance().selectedPathList.size() <
                            ActPhotoGallery.getInstance().maxCount) {
                        ActPhotoGallery.getInstance().selectedPathList.add(path);
                        mHolder.mCheckBox.setChecked(true);
                        mHolder.vTemp.setVisibility(View.VISIBLE);
                    } else if (ActPhotoGallery.getInstance().selectedPathList.contains(path)) {
                        ActPhotoGallery.getInstance().selectedPathList.remove(path);
                        mHolder.mCheckBox.setChecked(false);
                        mHolder.vTemp.setVisibility(View.GONE);
                    } else {
                        LogUtil.e("can not choose");
                    }
                }
                ActPhotoGallery.getInstance().refreshNum();
            });
        }
        mHolder.mIvPreview.setOnClickListener(v -> mListener.onClick(position));
    }

    public interface PhotoClickListener {
        void onClick(int position);
    }

    static class HolderPhoto extends RecyclerView.ViewHolder {
        AppCompatCheckBox mCheckBox;
        ImageView mIvPreview;
        View vTemp;
        View vChoose;

        HolderPhoto(View itemView) {
            super(itemView);
            ScreenUtil.initScale(itemView);
            mCheckBox = itemView.findViewById(R.id.cb_chooseMedia);
            mIvPreview = itemView.findViewById(R.id.iv_mediaPreview);
            vTemp = itemView.findViewById(R.id.v_temp);
            vChoose = itemView.findViewById(R.id.v_choose);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size() + 1;
    }
}