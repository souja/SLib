package com.souja.lib.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
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

import java.util.List;

/**
 * Created by Ydz on 2017/3/16 0016.
 */

public class DirAdapter extends MBaseAdapter<MediaBean> {

    private String flag;
    private int dirIndex;
    private DirClickListener mListener;

    public int getDirIndex() {
        return dirIndex;
    }

    public DirAdapter(Context context, List<MediaBean> list, String defaultDir, boolean isVideo, DirClickListener listener) {
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
    public RecyclerView.ViewHolder onCreateView(ViewGroup parent, int viewType) {
        return new DirHolder(LayoutInflater.from(mContext).inflate(R.layout.item_gallery_dir, parent, false));
    }

    @Override
    public void onBindView(RecyclerView.ViewHolder holder, int position) {
        DirHolder mHolder = (DirHolder) holder;
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
            MediaBean bean = mList.get(position);
            SPHelper.putString(SelectImgOptions.GALLERY_LAST, bean.getFolderName());
            dirIndex = position;
            mListener.onClick(bean);
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