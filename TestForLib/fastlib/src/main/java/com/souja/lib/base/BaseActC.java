package com.souja.lib.base;

import android.os.Bundle;

import com.souja.lib.utils.MTool;

/**
 * 透明状态栏
 * */
public abstract class BaseActC extends ActBase {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MTool.setStatusBarFullTransparent(getWindow());
        MTool.setStatusBarTextColor(getWindow(), true);
    }


}
