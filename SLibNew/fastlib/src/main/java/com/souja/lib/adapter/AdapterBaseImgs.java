package com.souja.lib.adapter;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.souja.lib.R;
import com.souja.lib.base.MBaseAdapterB;
import com.souja.lib.models.OImageBase;
import com.souja.lib.utils.GlideUtil;
import com.souja.lib.utils.ScreenUtil;

import java.util.ArrayList;

/**
 * 9宫格的图片列表适配器-只展示
 */
public class AdapterBaseImgs extends MBaseAdapterB<OImageBase, AdapterBaseImgs.HolderImg> {


    public AdapterBaseImgs(Context context, ArrayList<OImageBase> list) {
        super(context, list);
    }

    @Override
    public int setItemViewRes(int viewType) {
        return R.layout.item_base_img;
    }

    @Override
    public RecyclerView.ViewHolder onCreateView(ViewGroup parent, int viewType) {
        return new HolderImg(getItemView(parent, viewType));
    }

    @Override
    public void onBindView(OImageBase model, HolderImg mHolder, int position) {
        String path = model.getPictureUrl();
        if (TextUtils.isEmpty(path)) path = model.getImageUrl();
        GlideUtil.load(mContext, path, R.drawable.ic_loading, mHolder.imageView);
        mHolder.itemView.setOnClickListener(v ->
                GlideUtil.showPopImgs22(mContext, v, mList, position));

        if (position != 0 && position % 2 == 0)
            mHolder.vRight.setVisibility(View.GONE);
        else mHolder.vRight.setVisibility(View.VISIBLE);
    }


    public static class HolderImg extends RecyclerView.ViewHolder {

        ImageView imageView;
        View vRight;

        public HolderImg(View itemView) {
            super(itemView);
            ScreenUtil.initScale(itemView);
            imageView = itemView.findViewById(R.id.iv_img);
            vRight = itemView.findViewById(R.id.v_right);
        }
    }
}
