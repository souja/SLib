package com.souja.lib.models;

import android.app.Activity;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Administrator on 2017/8/30 0030.
 */

public class RxImgPathB implements Serializable {
    public Activity mActivity;
    public ArrayList<OImageBase> pathList;

    public RxImgPathB(Activity activity, ArrayList<OImageBase> pathList) {
        mActivity = activity;
        this.pathList = pathList;
    }
}
