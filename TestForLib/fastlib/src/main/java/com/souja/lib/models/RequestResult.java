package com.souja.lib.models;

import com.google.gson.JsonElement;

import java.io.Serializable;

/**
 * Created by Ydz on 2017/3/17 0017.
 */

public class RequestResult implements Serializable {
    public int code;
    public String msg;
    public JsonElement data;
    public JsonElement pagination;
}