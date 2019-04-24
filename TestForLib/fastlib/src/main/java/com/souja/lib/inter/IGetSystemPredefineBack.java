package com.souja.lib.inter;

import com.google.gson.JsonArray;

/**
 * Created by Yangdz on 2016/8/18 0018.
 */
public interface IGetSystemPredefineBack {

    void OnSuccess(JsonArray dataList);

    void OnFailure(String msg);
}
