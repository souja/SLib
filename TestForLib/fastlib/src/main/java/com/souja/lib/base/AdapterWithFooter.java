package com.souja.lib.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.souja.lib.R;
import com.souja.lib.tools.ViewHolderCommon;

import java.util.List;

/**
 * 咩有分页but有Footer
 * Created by Ydz on 2017/6/12 0012.
 */
public abstract class AdapterWithFooter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public Context mContext;
    public List<T> mList;
    private final int COMMON = 1, EMPTY = 2, FOOTER = 3;

    public AdapterWithFooter(Context context, List<T> list) {
        mContext = context;
        mList = list;
    }

    public RecyclerView.ViewHolder getEmptyView(ViewGroup parent) {
        return new ViewHolderEmpty(LayoutInflater.from(mContext).inflate(
                R.layout.item_empty, parent, false));
    }

    public RecyclerView.ViewHolder getFooterView(ViewGroup parent) {
        return new ViewHolderCommon(LayoutInflater.from(mContext).inflate(
                R.layout.item_header_footer, parent, false));
    }

    public abstract RecyclerView.ViewHolder onCreateView(ViewGroup parent);

    public abstract void onBindView(RecyclerView.ViewHolder holder, int position);

    public void bindEmpty(RecyclerView.ViewHolder holder) {
    }

    public void bindFooter(RecyclerView.ViewHolder holder) {
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == EMPTY)
            return getEmptyView(parent);
        else if (viewType == FOOTER)
            return getFooterView(parent);
        else
            return onCreateView(parent);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position == mList.size()) {
            bindFooter(holder);
        } else {
            if (mList.size() == 0) bindEmpty(holder);
            else {
                onBindView(holder, position);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == mList.size()) {
            return FOOTER;
        } else {
            if (mList.size() == 0) {
                return EMPTY;
            } else {
                return COMMON;
            }
        }
    }

    @Override
    public int getItemCount() {
        return mList.size() + 1;
    }
}
