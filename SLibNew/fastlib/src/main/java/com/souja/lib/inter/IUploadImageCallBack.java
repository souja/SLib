package com.souja.lib.inter;

/**
 * Created by Yangdz on 2016/8/18 0018.
 */
public interface IUploadImageCallBack {

    //    void OnUploadSuccess(ArrayList<String> urls);
    void OnUploadSuccess(String msg, int status);

    void OnFailure(String msg);
}
