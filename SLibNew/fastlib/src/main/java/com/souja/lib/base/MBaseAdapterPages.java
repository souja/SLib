package com.souja.lib.base;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.souja.lib.R;
import com.souja.lib.tools.ViewHolderCommon;

import java.util.List;

/**
 * Created by soda on 2016/4/5.
 */
public abstract class MBaseAdapterPages<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public Context mContext;
    public List<T> mList;
    public LayoutInflater mInflater;
    public int pageIndex = 1, pageAmount = 1;
    public final int type_last = 2, type_empty = 3;

    public MBaseAdapterPages(Context context, List<T> list) {
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
        if (viewType == type_last)
            return getLastView(parent);
        else if (viewType == type_empty)
            return getEmptyView(parent);
        else
            return onCreateView(parent, viewType);
    }

    public RecyclerView.ViewHolder getLastView(ViewGroup parent) {
        return new ViewHolderCommon(LayoutInflater.from(mContext).inflate(
                R.layout.item_last, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (mList.size() == 0) bindEmpty(holder);
        else {
            if (position < mList.size()) {
                onBindView(holder, position);
            } else bindLast(holder);
        }
    }

    public void bindLast(RecyclerView.ViewHolder holder) {

    }


    @Override
    public int getItemCount() {
        return mList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (mList.size() == 0) {
            return type_empty;
        } else {
            int type_common = 0;
            if (position == mList.size()) {
                return type_last;
            } else return type_common;
        }
    }

    public void setPageIndex(int i) {
        this.pageIndex = i;
    }

    public void setPageAmount(int i) {
        this.pageAmount = i;
    }

    public T getItem(int position) {
        return mList.get(position);
    }
}
