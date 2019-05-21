package com.souja.lib.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.souja.lib.R;
import com.souja.lib.base.MBaseAdapter;
import com.souja.lib.utils.MTool;
import com.souja.lib.utils.ScreenUtil;

import java.util.List;


/**
 * 创建学术/八卦 选择的图片列表 适配器
 */
public class AdapterImgs extends MBaseAdapter<String> {
    private RequestManager mRequestManager;
    private MListener mListener;
    private int maxCount = 9;

    public void setMaxCount(int max) {
        maxCount = max;
    }

    public interface MListener {
        void onAdd();

        void onDelete(int position);
    }

    public AdapterImgs(Context context, List<String> list, MListener listener) {
        super(context, list);
        mRequestManager = Glide.with(mContext);
        mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateView(ViewGroup parent, int viewType) {
        return new HolderImg(LayoutInflater.from(mContext).inflate(R.layout.item_simple_img, parent, false));
    }

    @Override
    public void onBindView(RecyclerView.ViewHolder holder, int position) {
        HolderImg mHolder = (HolderImg) holder;
        if (hasAddDrawable() && position == mList.size()) {//'添加'图标
            mHolder.imageView.setImageResource(R.drawable.ic_lib_add);
            mHolder.itemView.setOnClickListener(v -> mListener.onAdd());
            mHolder.delView.setVisibility(View.GONE);
        } else {
            mHolder.delView.setVisibility(View.VISIBLE);
            String path = getItem(position);
            mRequestManager.load(path)
                    .apply(new RequestOptions().placeholder(R.drawable.ic_default_rec))
                    .into(mHolder.imageView);
            mHolder.itemView.setOnClickListener(v -> MTool.showPopImages(mContext, v, mList, path));
            mHolder.delView.setOnClickListener(v -> mListener.onDelete(position));
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
        View vRight;

        public HolderImg(View itemView) {
            super(itemView);
            ScreenUtil.initScale(itemView);
            imageView = itemView.findViewById(R.id.iv_img);
            delView = itemView.findViewById(R.id.ib_del);
            vRight = itemView.findViewById(R.id.v_right);
        }
    }
}
