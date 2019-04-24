package com.souja.lib.utils;

import android.support.v4.util.ArrayMap;
import android.util.DisplayMetrics;

import com.souja.lib.models.BaseModel;

import org.xutils.common.util.LogUtil;

import io.reactivex.functions.Consumer;

/**
 * Created by Souja on 2018/3/12 0012.
 */

public class MGlobal {

    public static final String CACHE_CITY = "cacheCity";//缓存城市
    private final String KEY_SCREEN_PARAM = "windowScreenParams";

    public static boolean bQmp;//是否全面屏（刘海屏）
    private int deviceWidth, deviceHeight;
    private float density;

    private ArrayMap<Integer, Consumer<Object>> actionMap;

    private static MGlobal instance;

    public static MGlobal get() {
        if (instance == null) {
            synchronized (MGlobal.class) {
                if (instance == null) {
                    instance = new MGlobal();
                    instance.init();
                }
            }
        }
        return instance;
    }

    private void init() {
        actionMap = new ArrayMap<>();
    }

    private void initScreenParams(int w, int h, float de, boolean qmp) {
        deviceWidth = w;
        deviceHeight = h;
        density = de;
        bQmp = qmp;
        LogUtil.e("[DeviceInfo]width=" + deviceWidth
                + ",height=" + deviceHeight + ",density=" + density);
        LogUtil.e(bQmp ? "全面/刘海屏手机" : "非 全面/刘海屏手机");
    }

    public void initScreenParam(DisplayMetrics dm) {
        this.deviceWidth = dm.widthPixels;
        this.deviceHeight = dm.heightPixels;
        this.density = dm.density;
        LogUtil.e("mScale=" + (double) deviceWidth / 1080d);

        int sw = deviceWidth, sh = deviceHeight;
        bQmp = ((sw == 720 || sw == 768) && sh > 1280) ||
                (sw == 1080 && sh > 1920) || (sw == 1440 && sh > 2560);
        initScreenParams(dm.widthPixels, dm.heightPixels, dm.density, bQmp);
        SPHelper.putString(KEY_SCREEN_PARAM, new ScreenParam(deviceWidth, deviceHeight, density, bQmp ? 1 : 0).toString());
    }

    public boolean isInitializedScreenParams() {
        String screenParamStr = SPHelper.getString(KEY_SCREEN_PARAM);
        if (screenParamStr.isEmpty()) {
            return false;
        }
        ScreenParam param = (ScreenParam) GsonUtil.getObj(screenParamStr, ScreenParam.class);
        initScreenParams(param.width, param.height, param.density, param.bQmp == 1);
        return true;
    }

    //Rx functions======>>>
    public void addAction(int key, Consumer<Object> consumer) {
        actionMap.put(key, consumer);
    }

    public Consumer<Object> getAction(int key) {
        if (actionMap.containsKey(key))
            return actionMap.get(key);
        else return null;
    }

    public void delAction(int key) {
        if (actionMap.containsKey(key))
            actionMap.remove(key);
    }

    public boolean containsKey(int key) {
        return actionMap.containsKey(key);
    }

    public void clearActions() {
        if (actionMap == null) return;
        actionMap.clear();
    }

    public int getDeviceWidth() {
        return deviceWidth;
    }

    public int getDeviceHeight() {
        return deviceHeight;
    }

    public float getDensity() {
        return density;
    }

    class ScreenParam extends BaseModel {
        public int width, height;
        public float density;
        public int bQmp;//是否全面屏

        public ScreenParam(int width, int height, float density, int bQmp) {
            this.width = width;
            this.height = height;
            this.density = density;
            this.bQmp = bQmp;
        }
    }

    /*private int dpi;

    public void setDpi(int dpi) {
        this.dpi = dpi;
    }

    public int getDpi() {
        return dpi;
    }*/

   /* private int keybordHeight;

    public void setKeybordHeight(int height) {
        keybordHeight = height;
    }

    public int getKeybordHeight() {
        return keybordHeight;
    }*/

}
