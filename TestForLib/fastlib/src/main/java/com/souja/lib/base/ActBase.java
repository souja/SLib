package com.souja.lib.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.souja.lib.R;
import com.souja.lib.inter.IHttpCallBack;
import com.souja.lib.utils.MGlobal;
import com.souja.lib.utils.SHttpUtil;
import com.souja.lib.utils.ScreenUtil;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by Souja on 2018/3/12 0012.
 */

public abstract class ActBase extends AppCompatActivity {

    public AlertDialog _mDialog;
    public TextView _tvProgressTip;
    public ActBase _this;
    /**
     * 设置页面视图Resource
     * e.g. R.layout.act_test
     */
    public abstract int setViewRes();

    //页面逻辑处理
    public abstract void initMain();

    public void showToast(String msg) {
        if (msg == null || msg.contains("onNext")) return;
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public void showToast(int msgRes) {
        Toast.makeText(this, msgRes, Toast.LENGTH_SHORT).show();
    }

    public void showToast(String msg, int duration) {
        if (msg == null || msg.contains("onNext")) return;
        Toast.makeText(this, msg, duration).show();
    }

    //跳转页面
    public void NEXT(Intent it) {
        startActivity(it);
    }

    //跳转页面
    public void GO(Class dclass) {
        startActivity(new Intent(getApplicationContext(), dclass));
    }

    //添加请求
    public void addRequest(Callback.Cancelable req) {
        if (mCancelables == null) mCancelables = new ArrayList<>();
        mCancelables.add(req);
    }

    /**
     * “添加订阅”
     * Consumer是简易版的Observer，他有多重重载，可以自定义你需要处理的信息
     * 这里调用的是只接受onNext消息的方法，只提供一个回调接口accept
     * http://reactivex.io/RxJava/javadoc/io/reactivex/functions/Consumer.html
     */
    public void addAction(int actionCode, Consumer<Object> action) {
        if (!containsKey(actionCode)) {
            MGlobal.get().addAction(actionCode, action);
            if (actions == null) actions = new ArrayList<>();
            actions.add(actionCode);
        }
    }

    public Consumer<Object> getAction(int key) {
        return MGlobal.get().getAction(key);
    }

    public void delAction(int actionCode) {
        MGlobal.get().delAction(actionCode);
    }

    public boolean containsKey(int actionCode) {
        return MGlobal.get().containsKey(actionCode);
    }

    //“发送订阅”
    public void addSubscription(Object obj, Consumer<Object> consumer) {
        if (consumer != null)
            mDisposable = Flowable.just(obj).subscribe(consumer);
    }

    public void addSubscription(int key, Object o) {
        if (containsKey(key))
            addSubscription(o, getAction(key));
    }

    public void addSubscription(int key) {
        if (containsKey(key))
            addSubscription("", getAction(key));
    }

    public AlertDialog getDialog() {
        createDialog(null);
        return _mDialog;
    }

    public AlertDialog getDialog(String msg) {
        createDialog(msg);
        return _mDialog;
    }

    private void createDialog(@Nullable String msg) {
        if (_mDialog == null) {
            _mDialog = new AlertDialog.Builder(this, R.style.CustomProgressDialog).create();
            View loadView = LayoutInflater.from(this).inflate(R.layout.m_dialog_new, null);
            ScreenUtil.initScale(loadView);
            _mDialog.setView(loadView, 0, 0, 0, 0);
            _mDialog.setCanceledOnTouchOutside(false);
            _tvProgressTip = loadView.findViewById(R.id.tvTip);
        }
        _tvProgressTip.setText(TextUtils.isEmpty(msg) ? "请稍候..." : msg);
    }

    public <T> void Post(AlertDialog dialog, String url, RequestParams params, final Class<T> dataClass,
                         IHttpCallBack callBack) {
        addRequest(SHttpUtil.Post(dialog, url, params, dataClass, callBack));
    }

    //不需要Dialog
    public <T> void Post(String url, RequestParams params, final Class<T> dataClass, IHttpCallBack callBack) {
        Post(null, url, params, dataClass, callBack);
    }

    //不需要请求参数
    public <T> void Post(AlertDialog dialog, String url, final Class<T> dataClass, IHttpCallBack callBack) {
        Post(dialog, url, new RequestParams(), dataClass, callBack);
    }

    //不需要解析返回数据模型
    public void Post(AlertDialog dialog, String url, RequestParams params, IHttpCallBack callBack) {
        addRequest(SHttpUtil.Post(dialog, url, params, Object.class, callBack));
    }

    //不需要（Dialog 和 请求参数）
    public <T> void Post(String url, final Class<T> dataClass, IHttpCallBack callBack) {
        Post(null, url, new RequestParams(), dataClass, callBack);
    }


    //不需要（Dialog 和 解析返回数据模型）
    public void Post(String url, RequestParams params, IHttpCallBack callBack) {
        Post(null, url, params, Object.class, callBack);
    }


    //不需要（请求参数 和 解析返回数据模型）
    public void Post(AlertDialog dialog, String url, IHttpCallBack callBack) {
        Post(dialog, url, new RequestParams(), Object.class, callBack);
    }


    public <T> void Get(AlertDialog dialog, String url, RequestParams params, final Class<T> dataClass,
                        IHttpCallBack callBack) {
        addRequest(SHttpUtil.Get(dialog, url, params, dataClass, callBack));
    }

    //no dialog
    public <T> void Get(String url, RequestParams params, final Class<T> dataClass, IHttpCallBack callBack) {
        Get(null, url, params, dataClass, callBack);
    }

    //no param
    public <T> void Get(AlertDialog dialog, String url, final Class<T> dataClass, IHttpCallBack callBack) {
        Get(dialog, url, new RequestParams(), dataClass, callBack);
    }

    //no model
    public void Get(AlertDialog dialog, String url, RequestParams params, IHttpCallBack callBack) {
        Get(dialog, url, params, Object.class, callBack);
    }

    //no dialog && param
    public <T> void Get(String url, Class<T> dataClass, IHttpCallBack<T> callBack) {
        Get(null, url, new RequestParams(), dataClass, callBack);
    }

    //no dialog && model
    public void Get(String url, RequestParams params, IHttpCallBack callBack) {
        Get(null, url, params, Object.class, callBack);
    }

    //no param && model
    public void Get(AlertDialog dialog, String url, IHttpCallBack callBack) {
        Get(dialog, url, new RequestParams(), Object.class, callBack);
    }

    public <T> void Delete(AlertDialog dialog, String url, RequestParams params,
                           final Class<T> dataClass, IHttpCallBack callBack) {
        addRequest(SHttpUtil.Delete(dialog, url, params, dataClass, callBack));
    }

    //no dialog
    public <T> void Delete(String url, RequestParams params, final Class<T> dataClass, IHttpCallBack callBack) {
        Delete(null, url, params, dataClass, callBack);
    }

    //no param
    public <T> void Delete(AlertDialog dialog, String url, final Class<T> dataClass, IHttpCallBack callBack) {
        Delete(dialog, url, new RequestParams(), dataClass, callBack);
    }

    //no model
    public void Delete(AlertDialog dialog, String url, RequestParams params, IHttpCallBack callBack) {
        Get(dialog, url, params, Object.class, callBack);
    }

    //no dialog && param
    public <T> void Delete(String url, Class<T> dataClass, IHttpCallBack<T> callBack) {
        Delete(null, url, new RequestParams(), dataClass, callBack);
    }

    //no dialog && model
    public void Delete(String url, RequestParams params, IHttpCallBack callBack) {
        Delete(null, url, params, Object.class, callBack);
    }

    //no param && model
    public void Delete(AlertDialog dialog, String url, IHttpCallBack callBack) {
        Delete(dialog, url, new RequestParams(), Object.class, callBack);
    }


    public <T> void Put(AlertDialog dialog, String url, RequestParams params,
                           final Class<T> dataClass, IHttpCallBack callBack) {
        addRequest(SHttpUtil.Put(dialog, url, params, dataClass, callBack));
    }

    //no dialog
    public <T> void Put(String url, RequestParams params, final Class<T> dataClass, IHttpCallBack callBack) {
        Put(null, url, params, dataClass, callBack);
    }

    //no param
    public <T> void Put(AlertDialog dialog, String url, final Class<T> dataClass, IHttpCallBack callBack) {
        Put(dialog, url, new RequestParams(), dataClass, callBack);
    }

    //no model
    public void Put(AlertDialog dialog, String url, RequestParams params, IHttpCallBack callBack) {
        Get(dialog, url, params, Object.class, callBack);
    }

    //no dialog && param
    public <T> void Put(String url, Class<T> dataClass, IHttpCallBack<T> callBack) {
        Put(null, url, new RequestParams(), dataClass, callBack);
    }

    //no dialog && model
    public void Put(String url, RequestParams params, IHttpCallBack callBack) {
        Put(null, url, params, Object.class, callBack);
    }

    //no param && model
    public void Put(AlertDialog dialog, String url, IHttpCallBack callBack) {
        Put(dialog, url, new RequestParams(), Object.class, callBack);
    }

    public InputMethodManager inputMethodManager;
    private List<Callback.Cancelable> mCancelables;
    private List<Integer> actions;
    private Disposable mDisposable;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleOnCreate(savedInstanceState);
        ScreenUtil.setScale(this);
        _this=this;
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (setViewRes() != 0)
            setContentView(setViewRes());
        initMain();
    }

    public void handleOnCreate(Bundle savedInstanceState) {
    }

    @Override
    public void setContentView(int layoutResID) {
        View v = getLayoutInflater().inflate(layoutResID, null);
        ScreenUtil.initScale(v);
        super.setContentView(v);
    }

    @Override
    public void onDestroy() {
        if (mCancelables != null) {
            for (Callback.Cancelable req : mCancelables) {
                if (req != null && !req.isCancelled()) req.cancel();
            }
        }
        if (actions != null) {
            for (int actionCode : actions) {
                MGlobal.get().delAction(actionCode);
            }
        }
        if (mDisposable != null) {
            if (!mDisposable.isDisposed())
                mDisposable.dispose();
        }
        mDisposable = null;
        super.onDestroy();
    }
}
