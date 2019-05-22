package com.souja.lib;

import android.content.Context;
import android.support.multidex.MultiDexApplication;

import com.android.tony.defenselib.DefenseCrash;
import com.android.tony.defenselib.handler.IExceptionHandler;

public class BaseApp extends MultiDexApplication implements IExceptionHandler {


    @Override
    public void onCaughtException(Thread thread, Throwable throwable, boolean b) {
        throwable.printStackTrace();
    }

    @Override
    public void onEnterSafeMode() {

    }

    @Override
    public void onMayBeBlackScreen(Throwable throwable) {

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        // step1: Initialize the lib.
        DefenseCrash.initialize();
        // setp2: Install the fire wall defense.
        DefenseCrash.install(this);
    }

}
