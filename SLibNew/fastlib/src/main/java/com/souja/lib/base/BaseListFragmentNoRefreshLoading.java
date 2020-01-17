package com.souja.lib.base;

import android.text.TextUtils;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.souja.lib.R;
import com.souja.lib.inter.IHttpCallBack;
import com.souja.lib.inter.IListPage;
import com.souja.lib.models.ODataPage;
import com.souja.lib.utils.SHttpUtil;

import java.util.ArrayList;

public abstract class BaseListFragmentNoRefreshLoading<T> extends BaseFragment implements IListPage<T> {

    public RecyclerView mRecyclerView;
    public int pageIndex = 1, pageAmount = 1;
    private boolean bEmptyControl = false;

    public void setEmptyControl(boolean b) {
        bEmptyControl = b;
    }

    public RecyclerView.LayoutManager getLayoutMgr() {
        //默认返回LinearLayoutManger，如需其他类型 重写此方法
        return new LinearLayoutManager(mBaseActivity);
    }

    public void updateList() {
        pageIndex = 1;
        getList();
    }

    public T getItem(int i) {
        return (T) ((MBaseAdapter) mRecyclerView.getAdapter()).getItem(i);
    }

    public RecyclerView.Adapter getAdapter() {
        return mRecyclerView.getAdapter();
    }

    public void notifyDatasetChanged() {
        mRecyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public int setupLayoutRes() {
        return R.layout.layout_common_recycler;
    }

    private void initViews() {
        mRecyclerView = _rootView.findViewById(R.id.recyclerView);
    }

    @Override
    public void initMain() {
        initViews();
        mRecyclerView.setLayoutManager(getLayoutMgr());
        setAdapter(mRecyclerView);
        getList();
    }

    public void getList() {
        String url = setRequestUrl(pageIndex);
        if (TextUtils.isEmpty(url)) return;
        addRequest(SHttpUtil.Request(null, url, setMethod(), setRequestParams(""),
                getTClass(), new IHttpCallBack<T>() {
                    @Override
                    public void OnSuccess(String msg, ODataPage page, ArrayList<T> data) {
                        if (pageIndex == 1) {
                            ((MBaseAdapter) mRecyclerView.getAdapter()).clearDataSet();
                            pageAmount = page.getTotalPages();
                        }

                        ((MBaseAdapter) mRecyclerView.getAdapter()).reset(data);
                        notifyDatasetChanged();
                        onRequestFinish(true, msg);
                    }

                    @Override
                    public void OnFailure(String msg) {
                        showToast(msg);
                        onRequestFinish(false, msg);
                    }
                }));
    }
}
