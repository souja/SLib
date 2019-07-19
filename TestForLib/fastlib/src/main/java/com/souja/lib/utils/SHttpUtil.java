package com.souja.lib.utils;

import android.content.Context;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.souja.lib.enums.EnumExceptions;
import com.souja.lib.inter.IHttpCallBack;
import com.souja.lib.inter.SelfHandleCallBack;
import com.souja.lib.models.ODataPage;
import com.souja.lib.models.RequestResult;

import org.xutils.common.Callback;
import org.xutils.common.task.PriorityExecutor;
import org.xutils.common.util.LogUtil;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Flowable;

public class SHttpUtil {

    public static String M_HTTP, VERSION = "/v1";
    private static Context mContext;

    private static final int M_HTTP_SUCCESS = 1;//接口成功
    private static final int M_MULT_LOGIN = 9;//其它设备登录

    public interface SelfControl<T> {
        void onSuc(String url, String result, IHttpCallBack<T> callBack, final Class<T> dataClass);

        void onErr(String msg);
    }

    private static SelfControl mSelfControl;

    public static void setSelfControl(SelfControl control) {
        mSelfControl = control;
    }

    public static void setContext(Context context) {
        mContext = context;
    }

    public static <T> Callback.Cancelable Post(AlertDialog dialog, String url, RequestParams mParams,
                                               final Class<T> dataClass, IHttpCallBack<T> callBack) {
        LogUtil.e("POST");
        return Request(dialog, url, HttpMethod.POST, mParams, dataClass, callBack);
    }

    public static <T> Callback.Cancelable Get(AlertDialog dialog, String url, RequestParams mParams,
                                              final Class<T> dataClass, IHttpCallBack<T> callBack) {
        LogUtil.e("GET");
        return Request(dialog, url, HttpMethod.GET, mParams, dataClass, callBack);
    }

    public static <T> Callback.Cancelable Delete(AlertDialog dialog, String url, RequestParams mParams,
                                                 final Class<T> dataClass, IHttpCallBack<T> callBack) {
        LogUtil.e("DELETE");
        return Request(dialog, url, HttpMethod.DELETE, mParams, dataClass, callBack);
    }

    public static <T> Callback.Cancelable Put(AlertDialog dialog, String url, RequestParams mParams,
                                              final Class<T> dataClass, IHttpCallBack<T> callBack) {
        LogUtil.e("PUT");
        return Request(dialog, url, HttpMethod.PUT, mParams, dataClass, callBack);
    }

    public static <T> Callback.Cancelable Request(AlertDialog dialog, String url, HttpMethod method, RequestParams mParams,
                                                  final Class<T> dataClass, IHttpCallBack<T> callBack) {
        return Request(dialog, url, method, mParams, dataClass, callBack, null);
    }

    public static <T> Callback.Cancelable Request(AlertDialog dialog, String url, HttpMethod method, RequestParams mParams,
                                                  final Class<T> dataClass, IHttpCallBack<T> callBack, SelfHandleCallBack callBack2) {
        if (!NetWorkUtils.isNetworkAvailable(mContext)) {
            if (callBack2 != null) {
                callBack2.error(EnumExceptions.NO_INTERNET.getDesc());
                return null;
            }
            callBack.OnFailure(EnumExceptions.NO_INTERNET.getDesc());
            return null;
        }
        mParams.setUri(formatUrl(url));
        mParams.setHeader("Content-Type", "application/json");
        addIdentify(mParams);

        return x.http().request(method, mParams, new Callback.ProgressCallback<String>() {

            @Override
            public void onSuccess(String result) {
                if (mSelfControl != null)
                    mSelfControl.onSuc(mParams.getUri(), result, callBack, dataClass);
                else
                    handleOnRequestSuccess(result, mParams, dataClass, callBack, callBack2);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (mSelfControl != null) {
                    if (dialog != null) dialog.dismiss();
                    mSelfControl.onErr(ex.toString());
                } else
                    handleOnRequestErr(dialog, mParams, ex, callBack, callBack2);
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

    public static <T> void handleOnRequestSuccess(String result, RequestParams params,
                                                  final Class<T> dataClass, IHttpCallBack<T> callBack, SelfHandleCallBack callBack2) {
        LogUtil.e("===" + params.getUri() + "===\nresponse===>>>" + result);
        if (result == null) {
            if (callBack2 != null) {
                callBack2.error(EnumExceptions.BAD_GET_AWAY.getDesc());
                return;
            }
            callBack.OnFailure(EnumExceptions.BAD_GET_AWAY.getDesc());
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
                if (callBack2 != null) {
                    callBack2.handle("");
                    return;
                }
                callBack.OnSuccess(msg, pageObj, new ArrayList<>());
            } else if (resultObj.data.isJsonArray()) {
                String dataArr = resultObj.data.toString();
                if (callBack2 != null) {
                    callBack2.handle(dataArr);
                    return;
                }
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
                if (callBack2 != null) {
                    callBack2.handle(dataStr);
                    return;
                }
                ArrayList<T> dataList = new ArrayList<>();
                dataList.add(new Gson().fromJson(dataStr, dataClass));
                callBack.OnSuccess(msg, pageObj, dataList);
            }
        } else {
            if (code == M_MULT_LOGIN || (msg != null && msg.equals("用户非法")))
                loginOutDate(callBack, callBack2);
            else {
                if (callBack2 != null) {
                    callBack2.handle("服务器异常");
                    return;
                }
                callBack.OnFailure(msg == null ? "服务器异常" : msg);
            }
        }

    }

    public static <T> void handleOnRequestErr(AlertDialog dialog, RequestParams params, Throwable ex,
                                              IHttpCallBack<T> callBack, SelfHandleCallBack callBack2) {
        if (dialog != null && dialog.isShowing()) dialog.dismiss();

        String errStr = ex.toString();


        LogUtil.e("===" + params.getUri() + "===\n===>>>onError:" + errStr);
        if (errStr.contains("404") || errStr.equals("用户非法")) {
            loginOutDate(callBack, callBack2);
        } else {
            if (callBack2 != null) {
                callBack2.error(getErrMsgStr(errStr));
                return;
            }
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
        if (TextUtils.isEmpty(M_HTTP)) M_HTTP = SPHelper.getString(LibConstants.COMMON.REQUEST_URL);
        if (outControl) {
            url = M_HTTP + url;
        } else {
            url = M_HTTP + VERSION + url;
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


    public static RequestParams formatJStrParams(String paramJStr) {
        RequestParams paramJson = new RequestParams();
        if (!MTool.isEmpty(paramJStr)) {
            String finalStr = "\"" + paramJStr.replace("\"", "\\\"") + "\"";
            LogUtil.e("===Request params===" + paramJStr);
            paramJson.setBodyContent(finalStr);
        }
        paramJson.addHeader("Content-Type", "application/json");
        return paramJson;
    }


    public static RequestParams defaultParam() {
        RequestParams paramJson = new RequestParams();
        paramJson.addHeader("Content-Type", "application/json");
        return paramJson;
    }

    public static void addIdentify(RequestParams params) {
        if (identifyMap.size() == 0) return;
        for (String key : identifyMap.keySet()) {
            params.setHeader(key, identifyMap.get(key));
            LogUtil.e(key + ":" + identifyMap.get(key));
        }
    }

    public static String getErrMsgStr(String errStr) {
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

    private static <T> void loginOutDate(IHttpCallBack<T> callBack, SelfHandleCallBack callBack2) {
        VERSION = "";
        if (MGlobal.containsKey(LibConstants.COMMON.RX_LOGIN_OUTDATE)) {
            Flowable.just("").subscribe(MGlobal.getAction(LibConstants.COMMON.RX_LOGIN_OUTDATE));
        } else {
            if (callBack2 != null) {
                callBack2.handle("登录过期，请重新登录");
                return;
            }
            callBack.OnFailure("登录过期，请重新登录");
        }
    }


    public interface DownloadListener {
        void onStart();

        void onSuc(File file);

        void onErr(Throwable ex);

        void onProgress(long totalProgress, long currentProgress, int current);
    }

    public static Callback.Cancelable downloadFile(String sourceUrl, String savePath, DownloadListener downloadListener) {
        RequestParams params = new RequestParams(sourceUrl);
        //设置保存数据
        LogUtil.e("save path :" + savePath);
        params.setSaveFilePath(savePath);
        //设置是否可以立即取消下载
        params.setCancelFast(true);
        //设置是否自动根据头信息命名
        params.setAutoRename(false);
        //设置断点续传
        params.setAutoResume(true);

        params.setExecutor(new PriorityExecutor(3, true));//自定义线程池,有效的值范围[1, 3], 设置为3时, 可能阻塞图片加载.
        params.setRedirectHandler(request -> {
            RequestParams requestParams = request.getParams();
            String location = request.getResponseHeader("Location"); //协定的重定向地址
            if (!TextUtils.isEmpty(location)) {
                requestParams.setUri(location); //重新设置url地址
            }
            return requestParams;
        });
        params.setMaxRetryCount(5);
        addIdentify(params);
        return x.http().get(params, new Callback.ProgressCallback<File>() {

            @Override
            public void onSuccess(File file) {
                downloadListener.onSuc(file);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                downloadListener.onErr(ex);
            }

            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onFinished() {
                LogUtil.e("完成..." + sourceUrl);
            }

            @Override
            public void onWaiting() {
                LogUtil.e("等待中..." + sourceUrl);
            }

            @Override
            public void onStarted() {
                LogUtil.e("开始下载 ..." + sourceUrl);
                downloadListener.onStart();
            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
                LogUtil.e("on loading " + current + "/" + total);
                int cur = 0;
                if (current > 0) {
                    float c = (float) current / (float) total;
                    cur = (int) (c * 100);
                }

                if (isDownloading) {
                    downloadListener.onProgress(total, current, cur);
                }
            }
        });
    }
}
