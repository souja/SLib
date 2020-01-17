package com.souja.lib.inter;

public interface GetSingleCacheBack<T> {
    void exist(T cacheObj);

    void notExist();
}
