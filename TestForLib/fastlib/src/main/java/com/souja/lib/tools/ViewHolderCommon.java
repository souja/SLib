package com.souja.lib.tools;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.souja.lib.utils.ScreenUtil;


/**
 * Created by ydz on 2016/3/31.
 */
public class ViewHolderCommon extends RecyclerView.ViewHolder {

    public ViewHolderCommon(View itemView) {
        super(itemView);
        ScreenUtil.initScale(itemView);
    }
}