package com.souja.lib.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.souja.lib.inter.CommonItemClickListener;
import com.souja.lib.inter.IBaseAdapter;
import com.souja.lib.utils.MTool;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Ydz on 2017/7/5 0005.
 */
public abstract class MBaseAdapter<T, T1 extends BaseHolder>
        extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements IBaseAdapter<T, T1> {

    public Context mContext;
    public ArrayList<T> mList;
    public LayoutInflater mInflater;
    public CommonItemClickListener mItemClickListener;

    public MBaseAdapter(Context context) {
        this(context, null, null);
    }

    public MBaseAdapter(Context context, CommonItemClickListener listener) {
        this(context, null, listener);
    }

    public MBaseAdapter(Context context, Collection<T> list) {
        this(context, list, null);
    }

    public MBaseAdapter(Context context, Collection<T> list, CommonItemClickListener listener) {
        mContext = context;
        mList = new ArrayList<>();
        if (!MTool.isEmptyList(list))
            mList.addAll(list);
        if (listener != null)
            mItemClickListener = listener;
        mInflater = LayoutInflater.from(mContext);
    }

    public View defaultOnCreateView(ViewGroup parent, int viewType) {
        return mInflater.inflate(setItemViewRes(viewType), parent, false);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return onCreateView(parent, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        onBindView(getItem(position), (T1) holder, position);
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

    public void addList(Collection<T> data) {
        if (!MTool.isEmptyList(data)) {
            mList.addAll(data);
        }
        notifyDataSetChanged();
    }

    public void addListNoChange(Collection<T> data) {
        if (!MTool.isEmptyList(data)) {
            mList.addAll(data);
        }
    }

    public void add(T data) {
        if (data != null) {
            mList.add(data);
        }
        notifyDataSetChanged();
    }

    public void addOnly(T data) {
        if (data != null) {
            mList.add(data);
        }
    }

    public void update(T data, int index) {
        mList.set(index, data);
        notifyDataSetChanged();
    }

    public Collection<T> getList() {
        return mList;
    }

    public void deleteByChildPosition(int deletePosition) {
        if (deletePosition >= 0 && deletePosition <= mList.size() - 1) {
            mList.remove(deletePosition);
        }
        notifyDataSetChanged();
    }
}

