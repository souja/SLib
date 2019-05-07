package com.souja.lib.widget;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.souja.lib.R;
import com.souja.lib.inter.CommonItemClickListener;
import com.souja.lib.utils.ScreenUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 底部弹出菜单的Dialog
 * Created by Yangdz on 2016/12/15.
 */
public class DialogMenuList extends ProgressDialog {

    private Context mContext;
    private List<String> mMenuList;
    private CommonItemClickListener mListener;
    private AdapterDialogMenuList mAdapter;

    private boolean noCancel;

    public void noCancel() {
        noCancel = true;
        if (mMenuList != null) {
            mMenuList.remove("取消");
        }
    }

    public DialogMenuList(Context context, List<String> list, CommonItemClickListener listener, int itemRes) {
        super(context, R.style.Theme_dialog_with_parent);
        mContext = context;
        mMenuList = list;
        mMenuList.add("取消");
        mListener = listener;
        mAdapter = new AdapterDialogMenuList(mContext, mMenuList, position -> {
            //最后一个是取消，不响应
            if (noCancel || position != mMenuList.size() - 1) {
                mListener.onItemClick(position);
            }
            dismiss();
        }, itemRes);
    }

    public DialogMenuList(Context context, String menu, CommonItemClickListener listener, int itemRes) {
        super(context, R.style.Theme_dialog_with_parent);
        mContext = context;
        mMenuList = new ArrayList<>();
        mMenuList.add(menu);
        mMenuList.add("取消");
        mListener = listener;
        mAdapter = new AdapterDialogMenuList(mContext, mMenuList, position -> {
            dismiss();
            //最后一个是取消，不响应
            if (noCancel || position != mMenuList.size() - 1) {
                mListener.onItemClick(position);
            }
        }, itemRes);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.dialog_menu_list, null);
        ScreenUtil.initScale(contentView);
        setContentView(contentView);


        setCanceledOnTouchOutside(false);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        View mLayoutTop = contentView.findViewById(R.id.layout_top);
        RecyclerView mRecyclerView = contentView.findViewById(R.id.rv_menu);

        mLayoutTop.setOnClickListener(view -> dismiss());

        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setAdapter(mAdapter);
    }

}