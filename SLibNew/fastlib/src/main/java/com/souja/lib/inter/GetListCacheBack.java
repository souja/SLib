package com.souja.lib.inter;


import java.util.ArrayList;

public interface GetListCacheBack<T> {
    void exist(ArrayList<T> cacheList);

    void notExist();
}
