package com.souja.lib.models;

import android.app.Activity;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/8/30 0030.
 */

public class RxScanResult implements Serializable {
    public Activity mActivity;
    public String result;

    public RxScanResult(Activity activity, String result) {
        mActivity = activity;
        this.result = result;
    }
}
