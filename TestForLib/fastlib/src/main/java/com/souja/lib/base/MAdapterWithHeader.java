package com.souja.lib.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.souja.lib.R;
import com.souja.lib.tools.ViewHolderCommon;

import java.util.List;

/**
 * Created by Ydz on 2017/5/10 0010.
 */

public abstract class MAdapterWithHeader<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public Context mContext;
    public List<T> mList;
    private int pageIndex = 1, pageAmount = 1;
    private final int LOADING = 1, LAST = 2, EMPTY = 3, HEADER = 4;

    public MAdapterWithHeader(Context context, List<T> list) {
        mContext = context;
        mList = list;
    }


    public RecyclerView.ViewHolder getEmptyView(ViewGroup parent) {
        return new ViewHolderEmpty(LayoutInflater.from(mContext).inflate(
                R.layout.item_empty, parent, false));
    }

    public RecyclerView.ViewHolder getHeaderView(ViewGroup parent) {
        return new ViewHolderCommon(LayoutInflater.from(mContext).inflate(
                R.layout.item_header_footer, parent, false));
    }

    public abstract RecyclerView.ViewHolder onCreateView(ViewGroup parent);

    public abstract void onBindView(RecyclerView.ViewHolder holder, int position);


    public void bindEmpty(RecyclerView.ViewHolder holder) {
    }

    public void bindLast(RecyclerView.ViewHolder holder) {
    }

    public void bindHeader(RecyclerView.ViewHolder holder) {
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == LOADING)
            return new ViewHolderCommon(LayoutInflater.from(mContext).inflate(
                    R.layout.item_loading, parent, false));
        else if (viewType == LAST)
            return new ViewHolderCommon(LayoutInflater.from(mContext).inflate(
                    R.layout.item_last, parent, false));
        else if (viewType == EMPTY)
            return getEmptyView(parent);
        else if (viewType == HEADER)
            return getHeaderView(parent);
        else
            return onCreateView(parent);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position == 0) {
            bindHeader(holder);
        } else {
            if (mList.size() == 0) bindEmpty(holder);
            else {
                int dataPosition = position - 1;
                if (dataPosition < mList.size()) {
                    onBindView(holder, dataPosition);
                } else {
                    bindLast(holder);
                }
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return HEADER;
        } else {
            if (mList.size() == 0) {
                return EMPTY;
            } else {
                int type_common = 0;
                if (position - 1 == mList.size()) {
                    if (pageIndex < pageAmount) return LOADING;
                    else return LAST;
                } else return type_common;
            }
        }
    }

    @Override
    public int getItemCount() {
        return mList.size() + 2;
    }

    public void setPageIndex(int i) {
        this.pageIndex = i;
    }

    public void setPageAmount(int i) {
        this.pageAmount = i;
    }
}
