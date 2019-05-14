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

    public void initScreenParam(DisplayMetrics dm) {
        this.deviceWidth = dm.widthPixels;
        this.deviceHeight = dm.heightPixels;
        this.density = dm.density;
        LogUtil.e("mScale=" + (double) deviceWidth / 1080d);
        initScreenParams(dm.widthPixels, dm.heightPixels, dm.density);
        SPHelper.putString(LibConstants.COMMON.KEY_SCREEN_PARAM,
                new ScreenParam(deviceWidth, deviceHeight, density).toString());
    }

    public boolean isInitializedScreenParams() {
        String screenParamStr = SPHelper.getString(LibConstants.COMMON.KEY_SCREEN_PARAM);
        if (screenParamStr.isEmpty()) {
            return false;
        }
        ScreenParam param = (ScreenParam) GsonUtil.getObj(screenParamStr, ScreenParam.class);
        initScreenParams(param.width, param.height, param.density);
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
