package com.souja.lib.base;

import android.text.TextUtils;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.souja.lib.R;
import com.souja.lib.inter.IHttpCallBack;
import com.souja.lib.inter.IListPage;
import com.souja.lib.models.ODataPage;
import com.souja.lib.utils.SHttpUtil;
import com.souja.lib.widget.MLoadingDialog;

import java.util.ArrayList;

public abstract class BaseListFragmentNoRefresh<T> extends BaseFragment implements IListPage<T> {

    public RecyclerView mRecyclerView;
    public MLoadingDialog mLoadingDialog;
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
        getList(false);
    }

    public void loadMore() {
        mRecyclerView.stopScroll();
        mRecyclerView.stopNestedScroll();
        if (pageIndex < pageAmount) {
            pageIndex++;
            getList(false);
        }
    }

    public void hideLoading() {
        mLoadingDialog.dismiss();
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
        return R.layout.frag_list_no_refresh;
    }

    private void initViews() {
        mRecyclerView = _rootView.findViewById(R.id.recyclerView);
        mLoadingDialog = _rootView.findViewById(R.id.m_loading);
    }

    @Override
    public void initMain() {
        initViews();
        mRecyclerView.setLayoutManager(getLayoutMgr());
        setAdapter(mRecyclerView);
        getList(false);
    }

    public void getList(boolean retry) {
        String url = setRequestUrl(pageIndex);
        if (TextUtils.isEmpty(url)) return;
        if (retry) mLoadingDialog.setRetryDefaultTip();
        addRequest(SHttpUtil.Request(null, url, setMethod(), setRequestParams(""),
                getTClass(), new IHttpCallBack<T>() {
                    @Override
                    public void OnSuccess(String msg, ODataPage page, ArrayList<T> data) {
                        if (pageIndex == 1) {
                            ((MBaseAdapter) mRecyclerView.getAdapter()).clearDataSet();
                            pageAmount = page.getTotalPages();
                        }

                        ((MBaseAdapter) mRecyclerView.getAdapter()).reset(data);
                        if (pageIndex == 1 && data.size() == 0) {
                            if (!bEmptyControl) mLoadingDialog.showEmptyView();
                            else hideLoading();
                        } else hideLoading();
                        notifyDatasetChanged();
                        onRequestFinish(true, msg);
                    }

                    @Override
                    public void OnFailure(String msg) {
                        if (!bEmptyControl && mLoadingDialog.isShowing()) {
                            mLoadingDialog.setMClick(v -> getList(true));
                            mLoadingDialog.setErrMsgRetry(msg);
                        } else {
                            hideLoading();
                            showToast(msg);
                            if (pageIndex > 1) pageIndex--;
                        }
                        onRequestFinish(false, msg);
                    }
                }));
    }
}
