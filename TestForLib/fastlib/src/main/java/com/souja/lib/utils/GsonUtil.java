package com.souja.lib.utils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by Yangdz on 2017/3/5 0005.
 */

public class GsonUtil {

    // 将对象编译成json
    public static String objToJson(Object m) {
        return new Gson().toJson(m);

    }

    public static JsonElement toJsonTree(Object obj) {
        return new Gson().toJsonTree(obj);
    }

    public static Object getObj(String json, Class<?> clazz) {
        return new Gson().fromJson(json, clazz);
    }

    public static <T> ArrayList<T> getArr(String json, Class<T> clazz) {
        Type type = new TypeToken<ArrayList<JsonObject>>() {
        }.getType();
        ArrayList<JsonObject> jsonObjects = new Gson().fromJson(json, type);

        ArrayList<T> arrayList = new ArrayList<>();
        for (JsonObject jsonObject : jsonObjects) {
            arrayList.add(new Gson().fromJson(jsonObject, clazz));
        }
        return arrayList;
    }
}
