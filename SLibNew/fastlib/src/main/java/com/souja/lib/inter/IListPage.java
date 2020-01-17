package com.souja.lib.inter;

import androidx.recyclerview.widget.RecyclerView;

import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;

import java.lang.reflect.ParameterizedType;

public interface IListPage<T> {

    void setAdapter(RecyclerView recyclerView);

    String setRequestUrl(int pageIndex);

    default Class<T> getTClass() {
        Class<T> tClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
//        LogUtil.e("getTClass:" + tClass.getName());
        return tClass;
    }

    default HttpMethod setMethod() {
        return HttpMethod.GET;
    }


    default RequestParams setRequestParams(String txt) {
        //如果接口需要自定义参数，重写此方法
        return new RequestParams();
    }

    default void onRequestFinish(boolean requestOk, String msg) {
        //如果有自己的处理，重写此方法
    }
}
