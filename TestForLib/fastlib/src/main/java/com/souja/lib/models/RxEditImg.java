package com.souja.lib.models;

/**
 * 编辑图片相关参数
 * Created by Ydz on 2019/3/28 0028 10:22
 */
public class RxEditImg extends BaseModel {
    public String oriPath;//图片原路径
    public String editPath;//编辑后的图片路径

    public RxEditImg(String oriPath, String editPath) {
        this.oriPath = oriPath;
        this.editPath = editPath;
    }
}
