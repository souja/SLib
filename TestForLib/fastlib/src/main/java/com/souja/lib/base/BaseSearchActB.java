package com.souja.lib.base;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.souja.lib.R;
import com.souja.lib.inter.IHttpCallBack;
import com.souja.lib.models.ODataPage;
import com.souja.lib.utils.NetWorkUtils;
import com.souja.lib.utils.SHttpUtil;

import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;

import java.util.ArrayList;


public abstract class BaseSearchActB<T> extends ActBaseEd {

    public abstract void setAdapter(ListView listView, ArrayList<T> baseList);

    public abstract Class getResultClass();

    public abstract String getRequestUrl(int pageIndex);

    //设置接口请求参数
    public RequestParams getRequestParams(String txt) {
        //默认无附加参数，如需附加参数，重写此方法进行添加
        return new RequestParams();
    }

    //请求类型，默认POST
    public boolean isGet() {
        //如果请求方法是GET，重写此方法，return true;
        return false;
    }

    //在获取到列表数据并且ui更新完毕后，如果有自己的一些其他处理，重写此方法进行
    public void notifyAdapter() {

    }

    public void initIntent() {
    }

    public String setHintTip() {
        return null;
    }

    public int setHintImg() {
        return -1;
    }

    @Override
    public int setViewRes() {
        return R.layout.act_search_base_b;
    }

    public EditText mEdSearch;
    public ListView mListView;
    public SmartRefreshLayout mRefreshLayout;
    public View emptyView;
    public ImageView ivEmpty;

    public ArrayList<T> baseList;
    public int pageIndex = 1, pageAmount = 1;

    private void initViews() {
        mEdSearch = findViewById(R.id.ed_search);
        mListView = findViewById(R.id.lv_search);
        mRefreshLayout = findViewById(R.id.smartRefresh);
        emptyView = findViewById(R.id.layout_nodata);
        ivEmpty = findViewById(R.id.iv_empty);
    }

    @Override
    public void initMain() {
        initViews();
        initIntent();
        if (!TextUtils.isEmpty(setHintTip())) {
            mEdSearch.setHint(setHintTip());
        }
        if (setHintImg() != -1) ivEmpty.setBackgroundResource(setHintImg());
        findViewById(R.id.tv_cancel).setOnClickListener(v -> onBackPressed());

        mEdSearch.setOnEditorActionListener((textView, actionId, keyEvent) -> {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                        hideSoftKeyboard();
                        doSearch();
                        return true;
                    }
                    return false;
                }
        );

        baseList = new ArrayList<>();
        setAdapter(mListView, baseList);

        mRefreshLayout.setEnableRefresh(false);
        mRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                if (pageIndex < pageAmount) {
                    pageIndex++;
                    doSearch();
                }
            }

            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                mEdSearch.setEnabled(false);
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
        String txt = mEdSearch.getText().toString().trim();
        if (txt.isEmpty()) {
            showToast("请输入搜索内容");
            return;
        }

        mRefreshLayout.setEnableRefresh(true);
        addRequest(SHttpUtil.Request(getDialog(),
                getRequestUrl(pageIndex),
                isGet() ? HttpMethod.GET : HttpMethod.POST,
                getRequestParams(txt), getResultClass(), new IHttpCallBack<T>() {
                    @Override
                    public void OnSuccess(String msg, ODataPage page, ArrayList<T> dataList) {
                        mEdSearch.setEnabled(true);
                        mRefreshLayout.finishRefresh();
                        mRefreshLayout.finishLoadMore();

                        if (pageIndex == 1) {
                            baseList.clear();
                            pageAmount = page.getTotalPages();
                        }
                        mRefreshLayout.setEnableLoadMore(pageIndex < pageAmount);

                        if (dataList != null && dataList.size() > 0) {
                            emptyView.setVisibility(View.GONE);
                            baseList.addAll(dataList);
                        } else {
                            emptyView.setVisibility(View.VISIBLE);
                        }

                        ((BaseListAdapter) mListView.getAdapter()).notifyDataSetChanged();

                        notifyAdapter();
                    }

                    @Override
                    public void OnFailure(String msg) {
                        mEdSearch.setEnabled(true);
                        emptyView.setVisibility(View.GONE);
                        if (pageIndex > 1) pageIndex--;
                        showToast(msg);
                    }
                }));
    }


/*
    public static void open(Context context) {
        context.startActivity(new Intent(context, BaseSearchAct.class));
    }
    public static void open(Context context, String hint) {
        context.startActivity(new Intent(context, BaseSearchAct.class)
                .putExtra("hint", hint));
    }

    public static void open(Context context, String hint, int emptyRes) {
        context.startActivity(new Intent(context, BaseSearchAct.class)
                .putExtra("hint", hint)
                .putExtra("emptyRes", emptyRes));
    }

    public static void open(Context context, String hint, String emptyTip, int emptyRes) {
        context.startActivity(new Intent(context, BaseSearchAct.class)
                .putExtra("hint", hint)
                .putExtra("emptyTip", emptyTip)
                .putExtra("emptyRes", emptyRes));
    }
    */

   /* @Override
    public String getPageTitle() {
        return MConstants.PAGE.SEARCH_DOCTOR;
    }*/
}