package com.souja.lib.models;

import com.google.gson.Gson;

import org.xutils.common.util.LogUtil;

import java.io.Serializable;

/**
 * ClassName
 * Created by Ydz on 2019/3/11 0011 16:49
 */
public class BaseModel implements Serializable {

    public String toString() {
        String jsonStr = new Gson().toJson(this);
        LogUtil.e(jsonStr);
        return jsonStr;
    }
}
