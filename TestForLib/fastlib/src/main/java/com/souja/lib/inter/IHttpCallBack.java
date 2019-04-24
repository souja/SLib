package com.souja.lib.inter;

import com.souja.lib.models.ODataPage;

import java.util.ArrayList;

/**
 * Created by Yangdz on 2016/8/18 0018.
 */
public interface IHttpCallBack<T> {

    void OnSuccess(String msg, ODataPage page, ArrayList<T> data);

    void OnFailure(String msg);
}
