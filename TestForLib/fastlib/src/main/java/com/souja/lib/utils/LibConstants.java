package com.souja.lib.utils;


public class LibConstants {

    public static String packageName;

    public interface COMMON {
        //rx Consumer->key//rx Consumer->key
        int RX_CHOOSE_PHOTO = 12345;//从相册选择图片
        int CROP_IMG = 54321;//剪裁图片
        int RX_EDIT_IMG = 11111;//编辑图片
        //编辑图片RequestCode
        int REQ_IMAGE_EDIT = 2018;
        //预览图片RequestCode
        int REQ_IMAGE_GALLERY = 2019;
        int RX_LOGIN_OUTDATE = 66666;//登录过期
        String CACHE_CITY = "cacheCity";//缓存城市
        String KEY_SCREEN_PARAM = "windowScreenParams";
    }

}
