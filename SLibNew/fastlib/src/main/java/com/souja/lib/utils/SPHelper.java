package com.souja.lib.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SPHelper
 */
public class SPHelper {

    private static SharedPreferences sp;

    public static void init(Context context, String name) {
        sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    public static String getString(String key) {
        return sp.getString(key, "");
    }

    public static boolean putString(String key, String value) {
        return sp.edit().putString(key, value).commit();
    }

    public static int getInt(String key) {
        return sp.getInt(key, 0);
    }

    public static boolean putInt(String key, int value) {
        return sp.edit().putInt(key, value).commit();
    }

    public static long getLong(String key) {
        return sp.getLong(key, 0);
    }

    public static boolean putLong(String key, long value) {
        return sp.edit().putLong(key, value).commit();
    }

    public static boolean getBoolean(String key) {
        return sp.getBoolean(key, false);
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        return sp.getBoolean(key, defaultValue);
    }

    public static void putBoolean(String key, boolean value) {
        sp.edit().putBoolean(key, value).commit();
    }

    public static void remove(String key) {
        sp.edit().remove(key).commit();
    }

}
