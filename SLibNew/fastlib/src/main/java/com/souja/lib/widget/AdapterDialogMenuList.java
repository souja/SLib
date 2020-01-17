package com.souja.lib.widget;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.souja.lib.R;
import com.souja.lib.base.MBaseAdapterB;
import com.souja.lib.inter.CommonItemClickListener;
import com.souja.lib.utils.ScreenUtil;

import java.util.ArrayList;


public class AdapterDialogMenuList extends MBaseAdapterB<String, AdapterDialogMenuList.HolderListDialog> {

    private CommonItemClickListener mListener;
    private int itemRes;
    private int colorGray;

    public AdapterDialogMenuList(Context context, ArrayList<String> list, CommonItemClickListener listener, int res) {
        super(context, list);
        colorGray = mContext.getResources().getColor(R.color.grey_80);
        mListener = listener;
        itemRes = res;
    }

    @Override
    public int setItemViewRes(int viewType) {
        return itemRes == 0 ? R.layout.item_dialog_menu : itemRes;
    }

    @Override
    public RecyclerView.ViewHolder onCreateView(ViewGroup parent, int viewType) {
        return new HolderListDialog(getItemView(parent, viewType));
    }

    @Override
    public void onBindView(String menuStr, HolderListDialog mHolder, int position) {
        mHolder.tvItemName.setText(menuStr);
        if (menuStr.equals("取消"))
            mHolder.tvItemName.setTextColor(colorGray);
        mHolder.itemView.setOnClickListener(v -> mListener.onItemClick(position));
    }

    static class HolderListDialog extends RecyclerView.ViewHolder {
        TextView tvItemName;

        public HolderListDialog(View itemView) {
            super(itemView);
            ScreenUtil.initScale(itemView);
            tvItemName = itemView.findViewById(R.id.tv_itemName);
        }
    }
}
