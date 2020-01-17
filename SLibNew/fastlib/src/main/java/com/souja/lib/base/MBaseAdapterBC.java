package com.souja.lib.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.souja.lib.inter.CommonItemClickListener;
import com.souja.lib.inter.IBaseAdapterB;
import com.souja.lib.utils.MTool;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Ydz on 2017/7/5 0005.
 */
public abstract class MBaseAdapterBC<T, T1 extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements IBaseAdapterB<T, T1> {

    public Context mContext;
    public ArrayList<T> mList;
    public LayoutInflater mInflater;
    public CommonItemClickListener mItemClickListener;

    public MBaseAdapterBC(Context context) {
        this(context, new ArrayList<>(), null);
    }

    public MBaseAdapterBC(Context context, CommonItemClickListener listener) {
        this(context, new ArrayList<>(), listener);
    }

    public MBaseAdapterBC(Context context, ArrayList<T> list) {
        this(context, list, null);
    }

    public MBaseAdapterBC(Context context, ArrayList<T> list, CommonItemClickListener listener) {
        mContext = context;
        mList = list;
        if (listener != null)
            mItemClickListener = listener;
        mInflater = LayoutInflater.from(mContext);
    }

    public View getItemView(ViewGroup parent, int viewType) {
        return mInflater.inflate(setItemViewRes(viewType), parent, false);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return onCreateView(parent, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        onBindView((T1) holder, position);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public boolean isLastItem(int position) {
        return position == mList.size() - 1;
    }

    public T getItem(int position) {
        return mList.get(position);
    }

    public void clearDataSet() {
        mList.clear();
    }

    public void reset(Collection<T> data) {
        mList.clear();
        if (!MTool.isEmptyList(data))
            mList.addAll(data);
        notifyDataSetChanged();
    }

    public void add(Collection<T> data) {
        if (!MTool.isEmptyList(data)) {
            mList.addAll(data);
            notifyDataSetChanged();
        }
    }

    public Collection<T> getList() {
        return mList;
    }
}

