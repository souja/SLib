package com.souja.lib.inter;


import java.util.ArrayList;

public interface GetCacheCallBack<T> {
    void exist(ArrayList<T> cacheList);

    void notExist();
}
