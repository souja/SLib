package com.souja.lib.tools;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.souja.lib.R;
import com.souja.lib.utils.ScreenUtil;


/**
 * Created by ydz on 2016/3/31.
 */
public class HolderEmpty extends RecyclerView.ViewHolder {
    public ImageView ivEmpty;
    public TextView tvEmpty;

    public HolderEmpty(View itemView) {
        super(itemView);
        ScreenUtil.initScale(itemView);

        ivEmpty = itemView.findViewById(R.id.iv_empty);
        tvEmpty = itemView.findViewById(R.id.tv_empty);
    }
}