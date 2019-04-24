package com.souja.lib.inter;

import java.io.File;

/**
 * ClassName
 * Created by Ydz on 2019/3/13 0013 16:36
 */
public interface CompressImgCallBack {
    void onSuccess(File file);

    void onFail(String oriFilePath, String msg);

    /**
     * 无需压缩，跳过文件时触发
     */
    void onSkip(String filePath);
}
