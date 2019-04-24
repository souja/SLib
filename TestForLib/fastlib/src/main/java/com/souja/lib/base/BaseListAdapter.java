package com.souja.lib.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * base adapter for ListView
 * Created by Souja on 2017/12/1 0001.
 */

public abstract class BaseListAdapter<T> extends BaseAdapter {

    public Context mContext;
    public List<T> mList;
    protected LayoutInflater mInflater;

    public BaseListAdapter(Context context, List<T> list) {
        mContext = context;
        mList = list;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public T getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return createView(position, convertView);
    }


    public abstract View createView(int position, View convertView);

    public boolean isFirstItem(int position) {
        return position == 0;
    }

    public boolean isLastItem(int position) {
        return position == mList.size() - 1;
    }
}
