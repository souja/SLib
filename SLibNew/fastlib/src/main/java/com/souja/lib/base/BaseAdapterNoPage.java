package com.souja.lib.base;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.souja.lib.R;
import com.souja.lib.tools.ViewHolderCommon;

import java.util.List;

/**
 * 不需要分页且没有最后一个案例提示的adapter，有空数据提示
 * Created by ydz on 2016/4/5.
 */
public abstract class BaseAdapterNoPage<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public Context mContext;
    public List<T> mList;
    private final int type_empty = 1;
    public LayoutInflater mInflater;

    public BaseAdapterNoPage(Context context, List<T> list) {
        mContext = context;
        mList = list;
        mInflater = LayoutInflater.from(mContext);
    }

    public abstract RecyclerView.ViewHolder onCreateView(ViewGroup parent, int viewType);

    public abstract void onBindView(RecyclerView.ViewHolder holder, int position);

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == type_empty)
            return getEmptyView(parent);
        else
            return onCreateView(parent, viewType);
    }

    public RecyclerView.ViewHolder getEmptyView(ViewGroup parent) {
        return new ViewHolderCommon(LayoutInflater.from(mContext).inflate(
                R.layout.item_common_nodata, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (mList.size() > 0) {
            onBindView(holder, position);
        } else bindEmpty(holder);
    }

    public void bindEmpty(RecyclerView.ViewHolder holder) {
    }

    @Override
    public int getItemCount() {
        return mList.size() == 0 ? 1 : mList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (mList.size() == 0) return type_empty;
        else return 0;
    }

    public T getItem(int position) {
        return mList.get(position);
    }
}
