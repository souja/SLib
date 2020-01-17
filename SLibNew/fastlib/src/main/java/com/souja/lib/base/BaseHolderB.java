package com.souja.lib.base;

import android.view.View;

import com.souja.lib.utils.ScreenUtil;

import butterknife.ButterKnife;

/**
 * Created by Ydz on 2017/3/29 0029.
 */

public class BaseHolderB {

    public BaseHolderB(View itemView) {
        ButterKnife.bind(this, itemView);
        ScreenUtil.initScale(itemView);
    }
}
