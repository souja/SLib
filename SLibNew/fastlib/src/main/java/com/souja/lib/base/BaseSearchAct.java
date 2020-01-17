package com.souja.lib.base;

import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.souja.lib.R;
import com.souja.lib.R2;
import com.souja.lib.inter.IHttpCallBack;
import com.souja.lib.inter.IListPage;
import com.souja.lib.models.ODataPage;
import com.souja.lib.utils.MTool;
import com.souja.lib.utils.NetWorkUtils;
import com.souja.lib.utils.SHttpUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public abstract class BaseSearchAct<T> extends ActBaseEd implements IListPage<T> {

    @BindView(R2.id.ed_search)
    public EditText edSearch;
    @BindView(R2.id.deleteWords)
    LinearLayout deleteWords;
    @BindView(R2.id.tv_cancel)
    TextView tvCancel;
    @BindView(R2.id.layout_top)
    LinearLayout layoutTop;
    @BindView(R2.id.rv_search)
    RecyclerView mRecyclerView;
    @BindView(R2.id.smartRefresh)
    SmartRefreshLayout smartRefresh;
    @BindView(R2.id.iv_empty)
    ImageView ivEmpty;
    @BindView(R2.id.tv_emptyTip)
    public TextView tvEmptyTip;
    @BindView(R2.id.layout_nodata)
    View emptyView;

    public int pageIndex = 1, pageAmount = 1;

    //设置适配器布局类型，如LinearLayoutManager（线性布局）、GridLayoutManager（网格布局）。
    public RecyclerView.LayoutManager setLayoutMgr() {
        //默认返回LinearLayoutManger，如需其他类型 重写此方法进行修改
        return new LinearLayoutManager(_this);
    }

    public void initIntent() {
    }

    public int setHintTip() {
        return 0;
    }

    public int setHintImg() {
        return -1;
    }

    @Override
    public int setViewRes() {
        return R.layout.act_search_base;
    }

    @Override
    public void initMain() {
        ButterKnife.bind(this);
        initIntent();
        if (setHintTip() != 0) {
            edSearch.setHint(setHintTip());
        }
        if (setHintImg() != -1) ivEmpty.setBackgroundResource(setHintImg());
        MTool.bindEditDel(edSearch, deleteWords);
        tvCancel.setOnClickListener(v -> finish());
        edSearch.setOnEditorActionListener((textView, actionId, keyEvent) -> {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH
                            || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                        hideSoftKeyboard();
                        doSearch();
                        return true;
                    }
                    return false;
                }
        );

        mRecyclerView.setLayoutManager(setLayoutMgr());
        setAdapter(mRecyclerView);

        smartRefresh.setEnableRefresh(false);
        smartRefresh.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                if (pageIndex < pageAmount) {
                    pageIndex++;
                    doSearch();
                }
            }

            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                edSearch.setEnabled(false);
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                pageIndex = 1;
                doSearch();
            }
        });
    }

    private void doSearch() {
        if (!NetWorkUtils.isNetworkAvailable(_this)) {
            showToast(R.string.netErrTip);
            return;
        }
        String txt = edSearch.getText().toString().trim();
        if (txt.isEmpty()) {
            showToast("请输入搜索内容");
            return;
        }

        smartRefresh.setEnableRefresh(true);
        addRequest(SHttpUtil.Request(getDialog(), setRequestUrl(pageIndex),
                setMethod(), setRequestParams(txt), getTClass(), new IHttpCallBack<T>() {
                    @Override
                    public void OnSuccess(String msg, ODataPage page, ArrayList<T> dataList) {
                        edSearch.setEnabled(true);
                        smartRefresh.finishRefresh();
                        smartRefresh.finishLoadMore();

                        if (pageIndex == 1) {
                            ((MBaseAdapter) mRecyclerView.getAdapter()).clearDataSet();
                            pageAmount = page.getTotalPages();
                        }
                        smartRefresh.setEnableLoadMore(pageIndex < pageAmount);

                        if (dataList != null && dataList.size() > 0) {
                            emptyView.setVisibility(View.GONE);
                            ((MBaseAdapter) mRecyclerView.getAdapter()).addList(dataList);
                        } else {
                            emptyView.setVisibility(View.VISIBLE);
                        }

                        mRecyclerView.getAdapter().notifyDataSetChanged();

                        onRequestFinish(true, msg);
                    }

                    @Override
                    public void OnFailure(String msg) {
                        edSearch.setEnabled(true);
                        emptyView.setVisibility(View.GONE);
                        if (pageIndex > 1) pageIndex--;
                        showToast(msg);
                        onRequestFinish(false, msg);
                    }
                }));
    }

    public void updateList(){
        doSearch();
    }
}