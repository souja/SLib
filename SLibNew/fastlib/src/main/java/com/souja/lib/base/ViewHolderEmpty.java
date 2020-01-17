package com.souja.lib.base;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.souja.lib.utils.ScreenUtil;

/**
 * Created by Ydz on 2017/5/11 0011.
 */

public class ViewHolderEmpty extends RecyclerView.ViewHolder {
    public ViewHolderEmpty(View itemView) {
        super(itemView);
        ScreenUtil.initScale(itemView);
    }
}
