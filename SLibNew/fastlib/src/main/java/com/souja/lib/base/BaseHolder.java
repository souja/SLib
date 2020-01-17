package com.souja.lib.base;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.souja.lib.utils.ScreenUtil;

import butterknife.ButterKnife;

/**
 * Created by Ydz on 2017/3/29 0029.
 */

public class BaseHolder extends RecyclerView.ViewHolder {

    public BaseHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        ScreenUtil.initScale(itemView);
    }
}
