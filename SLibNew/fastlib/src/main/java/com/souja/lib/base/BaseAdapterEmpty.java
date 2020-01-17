package com.souja.lib.base;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.souja.lib.R;
import com.souja.lib.inter.CommonItemClickListener;
import com.souja.lib.inter.ICommonEmptyCallBack;
import com.souja.lib.tools.HolderEmpty;
import com.souja.lib.utils.NetWorkUtils;

import java.util.Collection;

/**
 * Created by soda on 2016/4/5.
 */
public abstract class BaseAdapterEmpty<T, T1 extends BaseHolder>
        extends MBaseAdapter<T, T1> {

    public BaseAdapterEmpty(Context context) {
        super(context);
    }

    public BaseAdapterEmpty(Context context, CommonItemClickListener listener) {
        super(context, listener);
    }

    public BaseAdapterEmpty(Context context, Collection<T> list) {
        super(context, list);
    }

    public BaseAdapterEmpty(Context context, Collection<T> list, CommonItemClickListener listener) {
        super(context, list, listener);
    }

    //EmptyView start=========================

    private final int type_empty = 3, type_common = 1;
    private String emptyTip;//空数据提示
    public int emptyRes = -1;//空数据图片
    private ICommonEmptyCallBack mCallBack;

    public void setEmptyTip(String tip) {
        emptyTip = tip;
    }

    public RecyclerView.ViewHolder onCreateEmptyView(ViewGroup parent) {
        return new HolderEmpty(LayoutInflater.from(mContext).inflate(
                R.layout.item_common_nodata, parent, false));
    }

    public void onBindEmptyView(HolderEmpty holder) {
        if (!NetWorkUtils.isNetworkAvailable(mContext)) {
            if (holder.ivEmpty != null)
                holder.ivEmpty.setBackgroundResource(R.drawable.ic_no_net);
            if (holder.tvEmpty != null)
                if (mCallBack != null) {
                    holder.tvEmpty.setText(R.string.noNetWork);
                    holder.tvEmpty.setOnClickListener(view -> mCallBack.handleOnCallBack());
                } else {
                    holder.tvEmpty.setText(R.string.noNetWorkB);
                    holder.tvEmpty.setOnClickListener(null);
                }
        } else {
            if (holder.ivEmpty != null)
                if (emptyRes == -1)
                    holder.ivEmpty.setBackgroundResource(R.drawable.ic_empty);
                else holder.ivEmpty.setBackgroundResource(emptyRes);

            if (holder.tvEmpty != null) {
                if (TextUtils.isEmpty(emptyTip))
                    holder.tvEmpty.setText("暂无数据");
                else holder.tvEmpty.setText(emptyTip);

                if (mCallBack != null) {
                    holder.tvEmpty.setOnClickListener(view -> mCallBack.handleOnCallBack());
                } else holder.tvEmpty.setOnClickListener(null);
            }
        }
    }

    public void setEmptyCallBack(ICommonEmptyCallBack callBack) {
        mCallBack = callBack;
    }

    //EmptyView end=========================

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == type_empty)
            return onCreateEmptyView(parent);
        else
            return onCreateView(parent, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (mList.size() == 0) {
            onBindEmptyView((HolderEmpty) holder);
        } else {
            onBindView(getItem(position), (T1) holder, position);
        }
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
            return type_common;
        }
    }
}
