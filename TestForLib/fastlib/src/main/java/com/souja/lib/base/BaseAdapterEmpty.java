package com.souja.lib.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.souja.lib.R;
import com.souja.lib.tools.ViewHolderCommon;

import java.util.List;

/**
 * Created by soda on 2016/4/5.
 */
public abstract class BaseAdapterEmpty<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public Context mContext;
    public List<T> mList;
    public LayoutInflater mInflater;

    public BaseAdapterEmpty(Context context, List<T> list) {
        mContext = context;
        mList = list;
        mInflater = LayoutInflater.from(mContext);
    }

    public RecyclerView.ViewHolder getEmptyView(ViewGroup parent) {
        return new ViewHolderCommon(LayoutInflater.from(mContext).inflate(
                R.layout.item_common_nodata, parent, false));
    }

    public void bindEmpty(RecyclerView.ViewHolder holder) {
    }

    public abstract RecyclerView.ViewHolder onCreateView(ViewGroup parent, int viewType);

    public abstract void onBindView(RecyclerView.ViewHolder holder, int position);

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 1)
            return getEmptyView(parent);
        else
            return onCreateView(parent, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (mList.size() == 0) bindEmpty(holder);
        else {
            onBindView(holder, position);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (mList.size() == 0) {
            return 1;
        } else {
            return 0;
        }
    }

    public T getItem(int position) {
        return mList.get(position);
    }
}
