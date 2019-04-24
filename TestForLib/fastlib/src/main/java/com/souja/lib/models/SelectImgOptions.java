package com.souja.lib.models;

import java.util.ArrayList;

/**
 * 选择图片参数
 * Created by Ydz on 2019/3/20 0020 10:14
 */
public class SelectImgOptions {

    //已选择的图片路径
    public static final String IMAGE_PATH_LIST_SELECTED = "option1";
    //最多可选择张数
    public static final String IMAGES_MAX_SELECT_COUNT = "option2";
    //图片下标
    public static final String IMAGES_INDEX = "option3";
    //是否剪裁
    public static final String IMAGES_CROP = "option4";
    //是否可跳过剪裁
    public static final String IMAGES_SKIP_CROP = "option5";
    //剪裁x
    public static final String IMAGES_CROP_X = "option5";
    //剪裁y
    public static final String IMAGES_CROP_Y = "option6";
    //上次选择的相册目录
    public static final String GALLERY_LAST = "gallery_default_dir";

    //最大可选择张数
    public int max;
    public boolean crop,//是否需要剪裁
            skip;//是否可跳过剪裁
    public int x,//剪裁x宽度
            y;//剪裁y高度
    public ArrayList<String> selectedImgListPath;//已选图片路径列表

    public SelectImgOptions() {
    }

    public SelectImgOptions(int max) {
        this.max = max;
    }

    public SelectImgOptions(int max, ArrayList<String> pathList) {
        this.max = max;
        this.selectedImgListPath = pathList;
    }

    public SelectImgOptions(int max, boolean crop) {
        this.max = max;
        this.crop = crop;
    }

    public SelectImgOptions(int max, boolean crop, boolean skip) {
        this.max = max;
        this.crop = crop;
        this.skip = skip;
    }

    public SelectImgOptions(int max, boolean crop, boolean skip, int x, int y) {
        this.max = max;
        this.crop = crop;
        this.skip = skip;
        this.x = x;
        this.y = y;
    }

    public SelectImgOptions setSelected(ArrayList<String> selected) {
        this.selectedImgListPath = selected;
        return this;
    }

    //    public static final int RX_DEL_IMAGE = 40;//删除照片后发出通知
    //intent添加图片的requestCode
//    public static final int IMAGES_REQUEST_CODE = 10001;
    //intent添加图片的requestCode
//    public static final int IMAGES_REQUEST_CODE_DEL = 10000;
    //裁剪图片的requestCode
//    public static final int REQUEST_CODE_CROP_IMAGE = 10002;
}
