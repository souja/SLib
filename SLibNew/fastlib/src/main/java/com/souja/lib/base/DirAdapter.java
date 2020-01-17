package com.souja.lib.base;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.souja.lib.R;
import com.souja.lib.models.MediaBean;
import com.souja.lib.models.SelectImgOptions;
import com.souja.lib.utils.SPHelper;
import com.souja.lib.utils.ScreenUtil;

import java.util.ArrayList;

/**
 * Created by Ydz on 2017/3/16 0016.
 */

public class DirAdapter extends MBaseAdapterB<MediaBean, DirAdapter.DirHolder> {

    private String flag;
    private int dirIndex;
    private DirClickListener mListener;

    public int getDirIndex() {
        return dirIndex;
    }

    public DirAdapter(Context context, ArrayList<MediaBean> list, String defaultDir, boolean isVideo, DirClickListener listener) {
        super(context, list);
        if (!TextUtils.isEmpty(defaultDir))
            setDefaultDir(defaultDir);
        flag = isVideo ? "个视频" : "张图片";
        mListener = listener;
    }

    public void setDefaultDir(String dir) {
        for (int i = 0; i < mList.size(); i++) {
            MediaBean bean = mList.get(i);
            if (bean.getFolderName().equals(dir)) {
                dirIndex = i;
                if (mListener != null)
                    mListener.onClick(bean);
                break;
            }
        }
    }

    @Override
    public int setItemViewRes(int viewType) {
        return R.layout.item_gallery_dir;
    }

    @Override
    public RecyclerView.ViewHolder onCreateView(ViewGroup parent, int viewType) {
        return new DirHolder(getItemView(parent, viewType));
    }

    @Override
    public void onBindView(MediaBean model, DirHolder mHolder, int position) {
        mHolder.name.setText(mList.get(position).getFolderName() + "");
        mHolder.num.setText(mList.get(position).getMediaCount() + flag);
        if (dirIndex == position)
            mHolder.chioce.setVisibility(View.VISIBLE);
        else
            mHolder.chioce.setVisibility(View.GONE);
        Glide.with(mContext)
                .load(mList.get(position).getTopMediaPath())
                .into(mHolder.icon);

        mHolder.itemView.setOnClickListener(v -> {
            SPHelper.putString(SelectImgOptions.GALLERY_LAST, model.getFolderName());
            dirIndex = position;
            mListener.onClick(model);
            notifyDataSetChanged();
        });
    }

    class DirHolder extends RecyclerView.ViewHolder {
        ImageView icon, chioce;
        TextView name, num;

        public DirHolder(View itemView) {
            super(itemView);
            ScreenUtil.initScale(itemView);
            icon = itemView.findViewById(R.id.dir_icon);
            chioce = itemView.findViewById(R.id.dir_choice);
            name = itemView.findViewById(R.id.dir_name);
            num = itemView.findViewById(R.id.dir_num);
        }
    }

    public interface DirClickListener {
        void onClick(MediaBean mediaBean);
    }

}