package com.souja.lib.models;

import android.app.Activity;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Administrator on 2017/8/30 0030.
 */

public class RxImgPath implements Serializable {
    public Activity mActivity;
    public ArrayList<String> pathList;

    public RxImgPath(Activity activity, ArrayList<String> pathList) {
        mActivity = activity;
        this.pathList = pathList;
    }
}
