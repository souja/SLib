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
public abstract class BaseAdapterPage<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public Context mContext;
    public List<T> mList;
    public LayoutInflater mInflater;
    public int pageIndex = 1, pageAmount = 1;
    public final int type_loading = 1, type_last = 2, type_empty = 3;

    public BaseAdapterPage(Context context, List<T> list) {
        mContext = context;
        mList = list;
        mInflater = LayoutInflater.from(mContext);
    }

    public RecyclerView.ViewHolder getEmptyView(ViewGroup parent) {
        return new ViewHolderCommon(LayoutInflater.from(mContext).inflate(
                R.layout.item_common_nodata, parent, false));
    }

    public void bindEmpty(RecyclerView.ViewHolder holder, int position) {
    }

    public abstract RecyclerView.ViewHolder onCreateView(ViewGroup parent, int viewType);

    public abstract void onBindView(RecyclerView.ViewHolder holder, int position);

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == type_loading)
            return getLoadingView(parent);
        else if (viewType == type_last)
            return new ViewHolderCommon(LayoutInflater.from(mContext).inflate(
                    R.layout.item_last, parent, false));
        else if (viewType == type_empty)
            return getEmptyView(parent);
        else
            return onCreateView(parent, viewType);
    }

    public RecyclerView.ViewHolder getLoadingView(ViewGroup parent) {
        return new ViewHolderCommon(LayoutInflater.from(mContext).inflate(
                R.layout.item_loading, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (mList.size() == 0) bindEmpty(holder, position);
        else {
            if (position < mList.size()) {
                onBindView(holder, position);
            } else bindLoading(holder);
        }
    }

    public void bindLoading(RecyclerView.ViewHolder holder) {

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
                if (pageIndex < pageAmount) return type_loading;
                else return type_last;
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
