package com.souja.lib.utils;

import java.lang.reflect.ParameterizedType;

public class TClass<T> {
    Class<T> getTClass() {
        Class<T> tClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
//        LogUtil.e("getTClass:" + tClass.getName());
        return tClass;
    }
}
