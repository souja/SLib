package com.souja.lib.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.souja.lib.R;
import com.souja.lib.base.MBaseAdapter;
import com.souja.lib.utils.GlideUtil;
import com.souja.lib.utils.ScreenUtil;

import java.util.List;

/**
 * 9宫格的图片列表适配器
 */
public class AdapterImgs extends MBaseAdapter<String> {
    private MListener mListener;
    private int maxCount = 9;
    private int imgColumns = 3;
    private boolean needImgIndex;

    private boolean enableDel = true;

    public void setEnableDel(boolean b) {
        enableDel = b;
    }

    public void setMaxCount(int max) {
        maxCount = max;
    }

    public interface MListener {
        void onAdd();

        void onDelete(int position);
    }

    public AdapterImgs(Context context, List<String> list, MListener listener) {
        super(context, list);
        mListener = listener;
    }

    public AdapterImgs(Context context, List<String> list, int imgColumns, MListener listener) {
        super(context, list);
        this.imgColumns = imgColumns;
        mListener = listener;
    }

    public AdapterImgs(Context context, List<String> list, boolean needImgIndex, MListener listener) {
        super(context, list);
        this.needImgIndex = needImgIndex;
        mListener = listener;
    }


    public AdapterImgs(Context context, List<String> list, int imgColumns, boolean needImgIndex, MListener listener) {
        super(context, list);
        this.imgColumns = imgColumns;
        this.needImgIndex = needImgIndex;
        mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateView(ViewGroup parent, int viewType) {
        return new HolderImg(LayoutInflater.from(mContext).inflate(imgColumns == 4
                ? R.layout.item_upload_img_b : R.layout.item_upload_img, parent, false));
    }

    @Override
    public void onBindView(RecyclerView.ViewHolder holder, int position) {
        HolderImg mHolder = (HolderImg) holder;
        if (hasAddDrawable() && position == mList.size()) {//'添加'图标
            mHolder.imageView.setImageResource(R.drawable.lib_img_add);
            mHolder.itemView.setOnClickListener(v -> {
                if (mListener != null)
                    mListener.onAdd();
            });
            mHolder.delView.setVisibility(View.GONE);
            if (needImgIndex) {
                mHolder.tvIndex.setVisibility(View.VISIBLE);
                mHolder.tvIndex.setText(mList.size() + "/" + maxCount);
            }
        } else {
            if (!enableDel) mHolder.delView.setVisibility(View.GONE);
            else
                mHolder.delView.setVisibility(View.VISIBLE);
            String path = getItem(position);

            mHolder.itemView.setOnClickListener(v ->
                    GlideUtil.showPopImages(mContext, v, mList, path));
            mHolder.delView.setOnClickListener(v -> {
                if (mListener != null)
                    mListener.onDelete(position);
            });
        }
        if (position != 0 && position % 2 == 0)
            mHolder.vRight.setVisibility(View.GONE);
        else mHolder.vRight.setVisibility(View.VISIBLE);
    }

    private boolean hasAddDrawable() {
        boolean has = false;
        if (maxCount > mList.size()) has = true;
        return has;
    }


    @Override
    public int getItemCount() {
        if (mList.size() == 0) return 1;
        else {
            if (hasAddDrawable()) return mList.size() + 1;
            return mList.size();
        }
    }

    static class HolderImg extends RecyclerView.ViewHolder {

        ImageView imageView;
        View delView;
        TextView tvIndex;
        View vRight;

        public HolderImg(View itemView) {
            super(itemView);
            ScreenUtil.initScale(itemView);
            imageView = itemView.findViewById(R.id.iv_img);
            tvIndex = itemView.findViewById(R.id.tv_index);
            delView = itemView.findViewById(R.id.ll_delImg);
            vRight = itemView.findViewById(R.id.v_right);
        }
    }
}
