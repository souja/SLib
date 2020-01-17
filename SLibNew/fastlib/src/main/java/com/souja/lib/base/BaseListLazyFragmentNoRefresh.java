package com.souja.lib.base;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;

import com.souja.lib.R;
import com.souja.lib.inter.IHttpCallBack;
import com.souja.lib.inter.IListPage;
import com.souja.lib.models.ODataPage;
import com.souja.lib.utils.SHttpUtil;

import java.util.ArrayList;

public abstract class BaseListLazyFragmentNoRefresh<T> extends BaseLazyFragment implements IListPage<T> {

    public void setEmptyControl(boolean b) {
        bEmptyControl = b;
    }
    public int pageIndex = 1, pageAmount = 1;
    private boolean bEmptyControl = false;
    private int totalCount = 0;

    public RecyclerView mRecyclerView;

    public void updateList() {
        pageIndex = 1;
        getList(false);
    }

    public RecyclerView.LayoutManager getLayoutMgr() {
        //默认返回LinearLayoutManger，如需其他类型 重写此方法
        return new LinearLayoutManager(mBaseActivity);
    }

    public void notifyDatasetChanged() {
        mRecyclerView.getAdapter().notifyDataSetChanged();
    }

    public void setTotalCount(int count) {
        totalCount = count;
    }

    public int getTotalCount() {
        return totalCount;
    }

    private void initViews() {
        mRecyclerView = _rootView.findViewById(R.id.recyclerView);
    }

    @Override
    public int setFragContentViewRes() {
        return R.layout.layout_common_recycler;
    }

    @Override
    public void initPage() {
        initViews();

        mRecyclerView.setLayoutManager(getLayoutMgr());
        setAdapter(mRecyclerView);
        getList(false);
    }

    public void getList(boolean retry) {
        String url = setRequestUrl(pageIndex);
        if (TextUtils.isEmpty(url)) return;

        if (retry) setRetryDefaultTip();

        addRequest(SHttpUtil.Request(null, url, setMethod(), setRequestParams(""),
                getTClass(), new IHttpCallBack<T>() {
                    @Override
                    public void OnSuccess(String msg, ODataPage page, ArrayList<T> data) {
                        if (pageIndex == 1) {
                            ((MBaseAdapter) mRecyclerView.getAdapter()).clearDataSet();
                            pageAmount = page.getTotalPages();
                            setTotalCount(page.getTotal());
                        }
                        ((MBaseAdapter) mRecyclerView.getAdapter()).reset(data);
                        if (pageIndex == 1 && data.size() == 0) {
                            if (!bEmptyControl) ShowEmptyView();
                            else ShowContentView();
                        } else ShowContentView();
                        notifyDatasetChanged();
                        onRequestFinish(true, msg);
                    }

                    @Override
                    public void OnFailure(String msg) {
                        if (!bEmptyControl && isProgressing()) {
                            setMClick(v -> getList(true));
                            setErrMsgRetry(msg);
                        } else {
                            ShowContentView();
                            showToast(msg);
                            if (pageIndex > 1) pageIndex--;
                        }
                        onRequestFinish(false, msg);
                    }
                }));
    }
}
