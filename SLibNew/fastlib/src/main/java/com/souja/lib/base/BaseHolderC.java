package com.souja.lib.base;

import android.view.View;

import butterknife.ButterKnife;

/**
 * Created by Ydz on 2017/3/29 0029.
 */

public class BaseHolderC {

    public BaseHolderC(View itemView) {
        ButterKnife.bind(this, itemView);
    }
}
