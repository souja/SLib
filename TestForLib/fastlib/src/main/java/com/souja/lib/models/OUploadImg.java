package com.souja.lib.models;

import java.io.Serializable;

/**
 * 上传图片后返回的图片路径模型
 */
public class OUploadImg implements Serializable {
    private String relativeUrl;//使用这个
    private String absoluteUrl;

    public String getRelativeUrl() {
        return relativeUrl;
    }

    public void setRelativeUrl(String relativeUrl) {
        this.relativeUrl = relativeUrl;
    }

    public String getAbsoluteUrl() {
        return absoluteUrl;
    }

    public void setAbsoluteUrl(String absoluteUrl) {
        this.absoluteUrl = absoluteUrl;
    }
}
