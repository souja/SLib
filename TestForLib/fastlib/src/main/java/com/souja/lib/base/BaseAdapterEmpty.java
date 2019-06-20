package com.souja.lib.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.souja.lib.R;
import com.souja.lib.inter.ICommonEmptyCallBack;
import com.souja.lib.tools.ViewHolderCommon;
import com.souja.lib.utils.NetWorkUtils;

import java.util.List;

/**
 * Created by soda on 2016/4/5.
 */
public abstract class BaseAdapterEmpty<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public Context mContext;
    public List<T> mList;
    public LayoutInflater mInflater;
    public final int type_empty = 3;

    public BaseAdapterEmpty(Context context, List<T> list) {
        mContext = context;
        mList = list;
        mInflater = LayoutInflater.from(mContext);
    }

    public RecyclerView.ViewHolder getEmptyView(ViewGroup parent) {
        return new ViewHolderCommon(LayoutInflater.from(mContext).inflate(
                R.layout.item_common_nodata, parent, false));
    }

    private ICommonEmptyCallBack mCallBack;

    public void setCallBack(ICommonEmptyCallBack callBack) {
        mCallBack = callBack;
    }

    public void bindEmpty(RecyclerView.ViewHolder holder, int position) {
        ImageView ivEmpty = holder.itemView.findViewById(R.id.iv_empty);
        TextView tvEmpty = holder.itemView.findViewById(R.id.tv_empty);
        if (!NetWorkUtils.isNetworkAvailable(mContext)) {
            ivEmpty.setBackgroundResource(R.drawable.ic_no_net);
            if (mCallBack != null) {
                tvEmpty.setText(R.string.noNetWork);
                holder.itemView.setOnClickListener(view -> mCallBack.handleOnCallBack());
            } else tvEmpty.setText(R.string.noNetWorkB);
        } else {
            ivEmpty.setBackgroundResource(R.drawable.ic_empty);
            tvEmpty.setText("暂无数据");
        }
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
        if (mList.size() == 0) return 1;
        return mList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (mList.size() == 0) {
            return type_empty;
        } else {
            return 0;
        }
    }

    public T getItem(int position) {
        return mList.get(position);
    }
}
