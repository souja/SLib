package com.souja.lib.models;

import android.app.Activity;

import java.io.Serializable;

public class RxCropInfo implements Serializable {
    public Activity mActivity;
    public String cropFilePath;

    public RxCropInfo(Activity activity, String cropFilePath) {
        mActivity = activity;
        this.cropFilePath = cropFilePath;
    }
}
