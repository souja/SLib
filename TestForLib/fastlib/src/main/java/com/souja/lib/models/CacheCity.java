package com.souja.lib.models;

/**
 * ClassName
 * Created by Ydz on 2019/3/14 0014 14:52
 */
public class CacheCity extends BaseModel {

    public String cityName;
    public String cityCode;

    public CacheCity(String cityName, String cityCode) {
        this.cityName = cityName;
        this.cityCode = cityCode;
    }
}
