package com.souja.lib.inter;

import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

public interface IBaseAdapter<T, T1> {

    int setItemViewRes(int viewType);

    RecyclerView.ViewHolder onCreateView(ViewGroup parent, int viewType);

    void onBindView(T model, T1 holder, int position);
}
