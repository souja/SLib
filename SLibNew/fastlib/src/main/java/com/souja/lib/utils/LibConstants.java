package com.souja.lib.utils;


public class LibConstants {

    public static String FILE_PROVIDER;
    public static String APP_NAME;
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
        String USERINFO_KEY = "user-info";//用户信息缓存KEY
        String CACHE_CITY = "cacheCity";//缓存城市
        String FIRST_USE = "hospitalFirstUse";//首次使用
        String KEY_SCREEN_PARAM = "windowScreenParams";
        String REQUEST_URL = "requestUrlPre";//接口请求前缀
        String STATUS_BAR_HEIGHT = "statusBarHeight";//statusBar height
        String DEVICE_INFO = "deviceInfo";//设备信息
        String INTERFACE_VERSION_CODE = "docInterVer";//接口版本
        String LAST_LOGIN_ACCOUNT = "lastLogin";
    }

}
