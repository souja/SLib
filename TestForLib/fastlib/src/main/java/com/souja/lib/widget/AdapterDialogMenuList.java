package com.souja.lib.widget;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.souja.lib.R;
import com.souja.lib.base.MBaseAdapter;
import com.souja.lib.inter.CommonItemClickListener;
import com.souja.lib.utils.ScreenUtil;

import java.util.List;


public class AdapterDialogMenuList extends MBaseAdapter<String> {

    private CommonItemClickListener mListener;
    private int itemRes;
    private int colorGray;

    public AdapterDialogMenuList(Context context, List<String> list, CommonItemClickListener listener, int res) {
        super(context, list);
        colorGray = mContext.getResources().getColor(R.color.grey_80);
        mListener = listener;
        itemRes = res;
    }

    @Override
    public RecyclerView.ViewHolder onCreateView(ViewGroup parent, int viewType) {
        return new HolderListDialog(mInflater.inflate(itemRes == 0 ?
                R.layout.item_dialog_menu : itemRes, parent, false));
    }

    @Override
    public void onBindView(RecyclerView.ViewHolder holder, int position) {
        HolderListDialog mHolder = (HolderListDialog) holder;
        String menuStr = getItem(position);
        mHolder.tvItemName.setText(menuStr);
        if (menuStr.equals("取消"))
            mHolder.tvItemName.setTextColor(colorGray);
        mHolder.itemView.setOnClickListener(v -> mListener.onItemClick(position));
    }

    static class HolderListDialog extends RecyclerView.ViewHolder{
        TextView tvItemName;

        public HolderListDialog(View itemView) {
            super(itemView);
            ScreenUtil.initScale(itemView);
            tvItemName = itemView.findViewById(R.id.tv_itemName);
        }
    }
}
