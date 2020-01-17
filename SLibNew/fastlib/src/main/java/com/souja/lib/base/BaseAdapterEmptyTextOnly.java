package com.souja.lib.base;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.souja.lib.R;
import com.souja.lib.inter.CommonItemClickListener;
import com.souja.lib.inter.ICommonEmptyCallBack;
import com.souja.lib.tools.ViewHolderCommon;
import com.souja.lib.utils.NetWorkUtils;

import java.util.ArrayList;
import java.util.Collection;

/**
 * only text tip
 * Created by soda on 2016/4/5.
 */
public abstract class BaseAdapterEmptyTextOnly<T, T1 extends BaseHolder>
        extends MBaseAdapter<T, T1> {

    public BaseAdapterEmptyTextOnly(Context context) {
        this(context, new ArrayList<>());
    }

    public BaseAdapterEmptyTextOnly(Context context, Collection<T> list) {
        this(context, list, null);
    }

    public BaseAdapterEmptyTextOnly(Context context, Collection<T> list, CommonItemClickListener listener) {
        super(context, list, listener);
    }

    public RecyclerView.ViewHolder getEmptyView(ViewGroup parent) {
        return new ViewHolderCommon(LayoutInflater.from(mContext).inflate(
                R.layout.item_nodata_txt, parent, false));
    }


    public final int type_empty = 3;
    public String emptyTip;//空数据提示
    private ICommonEmptyCallBack mCallBack;

    public void setEmptyCallBack(ICommonEmptyCallBack callBack) {
        mCallBack = callBack;
    }

    public void bindEmpty(RecyclerView.ViewHolder holder, int position) {
        TextView tvEmpty = holder.itemView.findViewById(R.id.tv_empty);
        if (!NetWorkUtils.isNetworkAvailable(mContext)) {

            if (tvEmpty != null)
                if (mCallBack != null) {
                    tvEmpty.setText(R.string.noNetWork);
                    tvEmpty.setOnClickListener(view -> mCallBack.handleOnCallBack());
                } else {
                    tvEmpty.setText(R.string.noNetWorkB);
                    tvEmpty.setOnClickListener(null);
                }
        } else {
            if (tvEmpty != null) {
                if (TextUtils.isEmpty(emptyTip))
                    tvEmpty.setText("暂无数据");
                else tvEmpty.setText(emptyTip);

                if (mCallBack != null) {
                    tvEmpty.setOnClickListener(view -> mCallBack.handleOnCallBack());
                } else tvEmpty.setOnClickListener(null);
            }
        }
    }

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
            return 0;
        }
    }
}
