package com.souja.lib.inter;

import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

public interface IListPage<T> {

     void setAdapter(RecyclerView recyclerView, ArrayList<T> baseList);

     String getRequestUrl(int pageIndex);

     Class getResultClass();

     void onRequestFinish(boolean success);
}
