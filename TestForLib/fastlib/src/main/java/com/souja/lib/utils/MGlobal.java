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


    private ArrayMap<Integer, Consumer<Object>> actionMap;
    private int deviceWidth, deviceHeight;
    private float density;

    private void init() {
        actionMap = new ArrayMap<>();
    }

    private void initScreenParams(int w, int h, float de) {
        deviceWidth = w;
        deviceHeight = h;
        density = de;
        LogUtil.e("[DeviceInfo]width=" + deviceWidth
                + ",height=" + deviceHeight + ",density=" + density);
    }

    public static void initScreenParam(DisplayMetrics dm) {
        get().deviceWidth = dm.widthPixels;
        instance.deviceHeight = dm.heightPixels;
        instance.density = dm.density;
        LogUtil.e("mScale=" + (double) instance.deviceWidth / 1080d);
        instance.initScreenParams(dm.widthPixels, dm.heightPixels, dm.density);
        instance.saveParam();
    }

    private void saveParam() {
        SPHelper.putString(LibConstants.COMMON.KEY_SCREEN_PARAM,
                new ScreenParam(deviceWidth, deviceHeight, density).toString());
    }

    public static boolean isInitializedScreenParams() {
        String screenParamStr = SPHelper.getString(LibConstants.COMMON.KEY_SCREEN_PARAM);
        if (screenParamStr.isEmpty()) {
            return false;
        }
        ScreenParam param = (ScreenParam) GsonUtil.getObj(screenParamStr, ScreenParam.class);
        get().initScreenParams(param.width, param.height, param.density);
        return true;
    }

    //Rx functions======>>>
    public static void addAction(int key, Consumer<Object> consumer) {
        get().actionMap.put(key, consumer);
    }

    public static Consumer<Object> getAction(int key) {
        if (get().actionMap.containsKey(key))
            return get().actionMap.get(key);
        else return null;
    }

    public static void delAction(int key) {
        if (get().actionMap.containsKey(key))
            get().actionMap.remove(key);
    }

    public static boolean containsKey(int key) {
        return get().actionMap.containsKey(key);
    }

    public static void clearActions() {
        if (get().actionMap == null) return;
        get().actionMap.clear();
    }

    public static int getDeviceWidth() {
        return get().deviceWidth;
    }

    public static int getDeviceHeight() {
        return get().deviceHeight;
    }

    public static float getDensity() {
        return get().density;
    }

    class ScreenParam extends BaseModel {
        public int width, height;
        public float density;

        public ScreenParam(int width, int height, float density) {
            this.width = width;
            this.height = height;
            this.density = density;
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
