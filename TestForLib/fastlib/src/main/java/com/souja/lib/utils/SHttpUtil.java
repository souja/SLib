package com.souja.lib.utils;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.v4.util.ArrayMap;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.souja.lib.enums.EnumExceptions;
import com.souja.lib.inter.IHttpCallBack;
import com.souja.lib.models.ODataPage;
import com.souja.lib.models.RequestResult;

import org.xutils.common.Callback;
import org.xutils.common.util.LogUtil;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;

public class SHttpUtil {

    protected static String HTTP, VERSION;
    private static Context mContext;

    private static final int M_HTTP_SUCCESS = 1;//接口成功
    private static final int M_MULT_LOGIN = 9;//其它设备登录

    public static void setContext(Context context){
        mContext = context;
    }

    public static <T> Callback.Cancelable Post(ProgressDialog dialog, String url, RequestParams mParams,
                                               final Class<T> dataClass, IHttpCallBack<T> callBack) {
        LogUtil.e("POST");
        return Request(dialog, url, HttpMethod.POST, mParams, dataClass, callBack);
    }

    public static <T> Callback.Cancelable Get(ProgressDialog dialog, String url, RequestParams mParams,
                                              final Class<T> dataClass, IHttpCallBack<T> callBack) {
        LogUtil.e("GET");
        return Request(dialog, url, HttpMethod.GET, mParams, dataClass, callBack);
    }

    public static <T> Callback.Cancelable Delete(ProgressDialog dialog, String url, RequestParams mParams,
                                                 final Class<T> dataClass, IHttpCallBack<T> callBack) {
        LogUtil.e("DELETE");
        return Request(dialog, url, HttpMethod.DELETE, mParams, dataClass, callBack);
    }

    public static <T> Callback.Cancelable Request(ProgressDialog dialog, String url, HttpMethod method, RequestParams mParams,
                                                  final Class<T> dataClass, IHttpCallBack<T> callBack) {
        if (!NetWorkUtils.isNetworkAvailable(mContext)) {
            callBack.OnFailure(EnumExceptions.NO_INTERNET.getDesc());
            return null;
        }
        mParams.setUri(formatUrl(url));
        mParams.setHeader("Content-Type", "application/json");
        addIdentify(mParams);

        return x.http().request(method, mParams, new Callback.ProgressCallback<String>() {

            @Override
            public void onSuccess(String result) {
                handleOnRequestSuccess(result, mParams, dataClass, callBack);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                handleOnRequestErr(dialog, mParams, ex, callBack);
            }

            @Override
            public void onStarted() {
                if (dialog != null) dialog.show();
            }

            @Override
            public void onFinished() {
                if (dialog != null && dialog.isShowing()) dialog.dismiss();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                if (dialog != null && dialog.isShowing()) dialog.dismiss();
            }

            @Override
            public void onWaiting() {

            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {

            }

        });
    }

    private static <T> void handleOnRequestSuccess(String result, RequestParams params,
                                                   final Class<T> dataClass, IHttpCallBack<T> callBack) {
        LogUtil.e("===" + params.getUri() + "===\nresponse===>>>" + result);
        if (result == null) {
            callBack.OnFailure("服务器异常");
            return;
        }
        RequestResult resultObj = (RequestResult) GsonUtil.getObj(result, RequestResult.class);
        int code = resultObj.code;
        String msg = resultObj.msg;
        if (code == M_HTTP_SUCCESS) {
            ODataPage pageObj;//分页模型
            if (isNull(resultObj.pagination)) pageObj = new ODataPage();
            else
                pageObj = (ODataPage) GsonUtil.getObj(resultObj.pagination.toString(), ODataPage.class);

            if (isNull(resultObj.data) || ((JsonArray) resultObj.data).size() == 0) {
                callBack.OnSuccess(msg, pageObj, new ArrayList<>());
            } else if (resultObj.data.isJsonArray()) {
                String dataArr = resultObj.data.toString();
                if (dataClass == String.class) {
                    Gson gson = new Gson();
                    String[] array = gson.fromJson(dataArr, String[].class);
                    List<String> parseData = Arrays.asList(array);
                    ArrayList<String> rtnData = new ArrayList<>();
                    rtnData.addAll(parseData);
                    callBack.OnSuccess(msg, pageObj, (ArrayList<T>) rtnData);
                } else if (dataClass == Double.class) {
                    Gson gson = new Gson();
                    Double[] array = gson.fromJson(dataArr, Double[].class);
                    List<Double> parseData = Arrays.asList(array);
                    ArrayList<Double> rtnData = new ArrayList<>();
                    rtnData.addAll(parseData);
                    callBack.OnSuccess(msg, pageObj, (ArrayList<T>) rtnData);
                } else if (dataClass == Integer.class) {
                    Gson gson = new Gson();
                    Integer[] array = gson.fromJson(resultObj.data.toString(), Integer[].class);
                    List<Integer> parseData = Arrays.asList(array);
                    ArrayList<Integer> rtnData = new ArrayList<>();
                    rtnData.addAll(parseData);
                    callBack.OnSuccess(msg, pageObj, (ArrayList<T>) rtnData);
                } else
                    callBack.OnSuccess(msg, pageObj, GsonUtil.getArr(dataArr, dataClass));
            } else {
                String dataStr = resultObj.data.toString();
                ArrayList<T> dataList = new ArrayList<>();
                dataList.add(new Gson().fromJson(dataStr, dataClass));
                callBack.OnSuccess(msg, pageObj, dataList);
            }
        } else {
            if (code == M_MULT_LOGIN) loginOutDate(callBack);
            else callBack.OnFailure(msg == null ? "服务器异常" : msg);
        }

    }

    private Disposable mDisposable;

    @SuppressLint("CheckResult")
    private static <T> void handleOnRequestErr(ProgressDialog dialog, RequestParams params, Throwable ex, IHttpCallBack<T> callBack) {
        if (dialog != null && dialog.isShowing()) dialog.dismiss();

        String errStr = ex.toString();
        LogUtil.e("===" + params.getUri() + "===\n===>>>onError:" + errStr);
        if (errStr.contains("404")) {
            loginOutDate(callBack);
        } else {
            callBack.OnFailure(getErrMsgStr(errStr));
        }
    }

    private static ArrayList<String> outVersionControl = new ArrayList<>();
    private static ArrayMap<String, String> identifyMap = new ArrayMap<>();

    public static void addOutVersionControl(ArrayList<String> list) {
        outVersionControl.addAll(list);
    }

    public static void addOutVersionControl(String control) {
        outVersionControl.add(control);
    }

    public static void addIdentifyParam(String key, String value) {
        identifyMap.put(key, value);
    }

    //给请求的接口增加版本控制
    public static String formatUrl(String url) {
        if (url.contains("http:") || url.contains("https:")) return url;

        boolean outControl = false;
        for (String control : outVersionControl) {
            if (url.contains(control)) {
                outControl = true;
                break;
            }
        }
        if (outControl) {
            url = HTTP + url;
        } else {
            url = HTTP + VERSION + url;
        }
        LogUtil.e("===RequestUrl===" + url);
        return url;
    }

    public static RequestParams formatParams(String paramJStr) {
        RequestParams paramJson = new RequestParams();
        paramJson.setAsJsonContent(true);
        LogUtil.e("===Request params===" + paramJStr);
        if (!MTool.isEmpty(paramJStr)) {
            paramJson.setBodyContent(paramJStr);
        }
        paramJson.addHeader("Content-Type", "application/json");
        return paramJson;
    }


    private static void addIdentify(RequestParams params) {
        if (identifyMap.size() == 0) return;
        for (String key : identifyMap.keySet()) {
            params.setHeader(key, identifyMap.get(key));
            LogUtil.e(key + ":" + identifyMap.get(key));
        }
    }

    private static String getErrMsgStr(String errStr) {
        if (errStr.contains("ConnectException") || errStr.contains("NoRouteToHostException")) {
            return EnumExceptions.SERVER_FAILED.getDesc();
        } else if (errStr.contains("Software caused connection abort")) {
            return EnumExceptions.NO_INTERNET.getDesc();
        } else if (errStr.contains("SocketTimeoutException")) {
            return EnumExceptions.SERVER_TIMEOUT.getDesc();
        } else if (errStr.contains("UnknownHostException") || errStr.contains("EOFException")) {
            return EnumExceptions.NO_INTERNET_A.getDesc();
        } else if (errStr.contains("502") || errStr.contains("NullPointerException")) {
            return EnumExceptions.BAD_GET_AWAY.getDesc();
        } else if (errStr.contains("404")) {
            VERSION = "";
            return EnumExceptions.BAD_GET_AWAY.getDesc();
        } else
            return errStr;
    }

    private static boolean isNull(JsonElement element) {
        return element == null || element.isJsonNull();
    }

    private static <T> void loginOutDate(IHttpCallBack<T> callBack) {
        VERSION = "";

        if (MGlobal.get().containsKey(LibConstants.RX_LOGIN_OUTDATE)) {
            Flowable.just("").subscribe(MGlobal.get().getAction(LibConstants.RX_LOGIN_OUTDATE));
        } else callBack.OnFailure("登录过期，请重新登录");
    }


}
