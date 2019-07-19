package com.souja.lib.utils;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.souja.lib.R;
import com.souja.lib.inter.CompressImgCallBack;
import com.souja.lib.inter.GetCacheCallBack;
import com.souja.lib.inter.PopEditListener;
import com.souja.lib.models.CacheCity;
import com.souja.lib.models.MediaBean;
import com.souja.lib.tools.DensityUtil;
import com.souja.lib.widget.MImgSpan;
import com.souja.lib.widget.PopZoomGallery;
import com.souja.lib.widget.ZoomImageModel;

import org.xutils.common.util.LogUtil;
import org.xutils.x;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;
import top.zibin.luban.OnRenameListener;

/**
 * Created by Souja on 2018/6/29 0029.
 */

public class MTool {

    public static void bindEditDel(EditText editText, View deleteView) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    deleteView.setVisibility(View.VISIBLE);
                } else
                    deleteView.setVisibility(View.INVISIBLE);
            }
        });
        deleteView.setOnClickListener(v -> editText.setText(""));
    }

    public static void bindEditDelB(EditText editText, View deleteView) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    deleteView.setVisibility(View.VISIBLE);
                } else
                    deleteView.setVisibility(View.GONE);
            }
        });
        deleteView.setOnClickListener(v -> editText.setText(""));
    }

    public static AlertDialog createDialog(Context context) {
        return createDialog(context, null);
    }

    public static AlertDialog createDialog(Context context, String msg) {
        AlertDialog dialog = new AlertDialog.Builder(context, R.style.CustomProgressDialog).create();
        View loadView = LayoutInflater.from(context).inflate(R.layout.m_dialog_new, null);
        ScreenUtil.initScale(loadView);
        dialog.setView(loadView, 0, 0, 0, 0);
        dialog.setCanceledOnTouchOutside(false);
        if (!TextUtils.isEmpty(msg)) {
            TextView tvTip = loadView.findViewById(R.id.tvTip);
            tvTip.setText(msg);
        }
        return dialog;
    }

    public static int getStatusBarHeight(Context context) {
        int statusBarHeight = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen",
                "android");
        if (resourceId > 0) {
            statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
        }
        LogUtil.e("statusBarHeight=" + statusBarHeight);
        return statusBarHeight;
    }

    public static void Toast(Context context, String msg) {
        if (msg == null || msg.contains("onNext") || context == null) return;
        Toast toast = Toast.makeText(context, null, Toast.LENGTH_SHORT);
        toast.setText(msg);
        toast.show();
    }

    public static void Toast(Context context, int msg) {
        Toast toast = Toast.makeText(context, null, Toast.LENGTH_SHORT);
        toast.setText(msg);
        toast.show();
    }

    public static void Toast(Context context, String msg, int duration) {
        if (msg == null || msg.contains("onNext") || context == null) return;
        Toast toast = Toast.makeText(context, null, duration);
        toast.setText(msg);
        toast.show();
    }

    public static boolean isMobileNO(String mobiles) {
        Pattern p = Pattern.compile("^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(17[013678])|(18[0,5-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    public static boolean isEmpty(String s) {
        if (null == s || s.length() == 0 || s.trim().length() == 0 || s.equals("null"))
            return true;
        return false;
    }

    public static void showPopImage(Context context, View v, String url) {
        ArrayList<ZoomImageModel> zoomImageArrayList = new ArrayList<>();
        ZoomImageModel imageScale = new ZoomImageModel();
        int[] xy = new int[2];
        v.getLocationInWindow(xy);
        imageScale.rect = new Rect(xy[0], xy[1], xy[0] + v.getWidth(), xy[1] + v.getHeight());
        imageScale.smallImagePath = url;
        imageScale.bigImagePath = url;
        zoomImageArrayList.add(imageScale);
        PopZoomGallery popZoomGallery = new PopZoomGallery(context, zoomImageArrayList,
                (container, position, view, model) -> {
//                    GlideUtil.load(context, url, view, R.drawable.ic_default_s)
                    Glide.with(context).load(model.bigImagePath).into(view);
                });
        popZoomGallery.showPop(v, 0);
    }

    public static void showPopImage(Context context, View v, GlideUrl url) {
        ArrayList<ZoomImageModel> zoomImageArrayList = new ArrayList<>();
        ZoomImageModel imageScale = new ZoomImageModel();
        int[] xy = new int[2];
        v.getLocationInWindow(xy);
        imageScale.rect = new Rect(xy[0], xy[1], xy[0] + v.getWidth(), xy[1] + v.getHeight());
        imageScale.mGlideUrl = url;
        zoomImageArrayList.add(imageScale);
        PopZoomGallery popZoomGallery = new PopZoomGallery(context, zoomImageArrayList,
                (container, position, view, model) ->
                        Glide.with(context).load(model.mGlideUrl).into(view));
        popZoomGallery.showPop(v, 0);
    }

    /**
     * 全透状态栏
     */
    public static void setStatusBarFullTransparent(Window window) {
        if (Build.VERSION.SDK_INT >= 21) {//21表示5.0
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= 19) {//19表示4.4
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //虚拟键盘也透明
            //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    /**
     * 设置StatusBar字体颜色
     * <p>
     * 参数true表示StatusBar风格为Light，字体颜色为黑色
     * 参数false表示StatusBar风格不是Light，字体颜色为白色
     * <p>
     * <item name="android:windowLightStatusBar">true</item>
     * 在theme或style中使用这个属性改变StatusBar的字体颜色，这种形式相对不灵活
     */
    @TargetApi(Build.VERSION_CODES.M)
    public static void setStatusBarTextColor(Window window, boolean lightStatusBar) {
        if (window == null) return;
        View decor = window.getDecorView();
        int ui = decor.getSystemUiVisibility();
        if (lightStatusBar) {
            ui |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        } else {
            ui &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        }
        decor.setSystemUiVisibility(ui);
    }

    public static int getCurrentStartPosition(int prePos, int newSize) {
        if (prePos == 0) return newSize * 100;
        LogUtil.e("pre cur pos=" + prePos + ",new size=" + newSize);
        int curPos;
        while ((prePos + newSize * 100) % newSize != 0) {
            LogUtil.e("计算新起点  " + (prePos + newSize * 100) % newSize);
            prePos++;
            LogUtil.e("起点++ " + prePos);
        }
        curPos = prePos + newSize * 100;
        LogUtil.e("新起点 " + curPos);
        return curPos;
    }


    public static void reflex(final TabLayout tabLayout) {
        reflex(tabLayout, 10, 10);
    }

    public static void reflex(final TabLayout tabLayout, int left, int right) {
        //线的宽度是根据 tabView的宽度来设置的
        tabLayout.post(() -> {
            try {
                //拿到tabLayout的mTabStrip属性
                Field mTabStripField = tabLayout.getClass().getDeclaredField("mTabStrip");
                mTabStripField.setAccessible(true);

                LinearLayout mTabStrip = (LinearLayout) mTabStripField.get(tabLayout);

                int leftPadding = DensityUtil.dip2px(tabLayout.getContext(), left);
                int rightPadding = DensityUtil.dip2px(tabLayout.getContext(), right);

                for (int i = 0; i < mTabStrip.getChildCount(); i++) {
                    View tabView = mTabStrip.getChildAt(i);

                    //拿到tabView的mTextView属性
                    Field mTextViewField = tabView.getClass().getDeclaredField("mTextView");
                    mTextViewField.setAccessible(true);

                    TextView mTextView = (TextView) mTextViewField.get(tabView);

                    tabView.setPadding(0, 0, 0, 0);

                    int width = mTextView.getWidth();
                    if (width == 0) {
                        mTextView.measure(0, 0);
                        width = mTextView.getMeasuredWidth();
                    }
                    //设置tab左右间距为10dp  注意这里不能使用Padding 因为源码中线的宽度是根据 tabView的宽度来设置的
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tabView.getLayoutParams();
                    params.width = width;
                    params.leftMargin = leftPadding;
                    params.rightMargin = rightPadding;
                    tabView.setLayoutParams(params);

                    tabView.invalidate();
                }
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });

    }

    public static void reflexB(final TabLayout tabLayout, int left, int right) {
        //线的宽度是根据 tabView的宽度来设置的
        tabLayout.post(() -> {
            try {
                //拿到tabLayout的mTabStrip属性
                Field mTabStripField = tabLayout.getClass().getDeclaredField("mTabStrip");
                mTabStripField.setAccessible(true);

                LinearLayout mTabStrip = (LinearLayout) mTabStripField.get(tabLayout);


                for (int i = 0; i < mTabStrip.getChildCount(); i++) {
                    View tabView = mTabStrip.getChildAt(i);

                    //拿到tabView的mTextView属性
                    Field mTextViewField = tabView.getClass().getDeclaredField("mTextView");
                    mTextViewField.setAccessible(true);

                    TextView mTextView = (TextView) mTextViewField.get(tabView);

                    tabView.setPadding(0, 0, 0, 0);

                    int width = mTextView.getWidth();
                    if (width == 0) {
                        mTextView.measure(0, 0);
                        width = mTextView.getMeasuredWidth();
                    }
                    //设置tab左右间距为10dp  注意这里不能使用Padding 因为源码中线的宽度是根据 tabView的宽度来设置的
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tabView.getLayoutParams();
                    params.width = width;
                    params.leftMargin = (int) (left * ScreenUtil.mScale);
                    params.rightMargin = (int) (right * ScreenUtil.mScale);
                    tabView.setLayoutParams(params);

                    tabView.invalidate();
                }
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });

    }

    public static void GO(Context context, Class dClass) {
        context.startActivity(new Intent(context, dClass));
    }

    public static void setIndicator(TabLayout tabs, int leftDip, int rightDip) {
        Class<?> tabLayout = tabs.getClass();
        Field tabStrip = null;
        try {
            tabStrip = tabLayout.getDeclaredField("mTabStrip");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        tabStrip.setAccessible(true);
        LinearLayout llTab = null;
        try {
            llTab = (LinearLayout) tabStrip.get(tabs);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        int left = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, leftDip, Resources.getSystem().getDisplayMetrics());
        int right = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, rightDip, Resources.getSystem().getDisplayMetrics());

        for (int i = 0; i < llTab.getChildCount(); i++) {
            View child = llTab.getChildAt(i);
            child.setPadding(0, 0, 0, 0);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
            params.leftMargin = left;
            params.rightMargin = right;
            child.setLayoutParams(params);
            child.invalidate();
        }
    }

    public static int getCurrentVersionCode(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            LogUtil.e("当前版本信息：name=" + info.versionName + ",code=" + info.versionCode);
            return info.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e("获取当前版本信息出错");
            return 0;
        }
    }

    public static String getCurrentVersionName(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            LogUtil.e("当前版本信息：name=" + info.versionName + ",code=" + info.versionCode);
            return info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e("获取当前版本信息出错");
            return "";
        }
    }

    public static int getNavigationBarHeight(Context context) {
//        Resources resources = context.getResources();
//        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
//        int height = resources.getDimensionPixelSize(resourceId);
//        return height;
        int vh = 0;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        try {
            @SuppressWarnings("rawtypes")
            Class c = Class.forName("android.view.Display");
            @SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, dm);
            vh = dm.heightPixels - display.getHeight();
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogUtil.e("keybord height:" + vh);
        return vh;
    }

    public static void showPopEditView(Context context, PopEditListener listener, View anchor) {
        showPopEditView(context, listener, anchor, 200);
    }

    public static void showPopEditView(Context context, PopEditListener listener, View anchor, int maxLength) {
        View contentView = LayoutInflater.from(context).inflate(R.layout.popup_comment, null);
        ScreenUtil.initScale(contentView);
        PopupWindow mPopWindow = new PopupWindow(contentView,
                ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT, true);
        mPopWindow.setContentView(contentView);
        //防止PopupWindow被软件盘挡住（可能只要下面一句，可能需要这两句）
//        mPopWindow.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        mPopWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(1000, InputMethodManager.SHOW_FORCED);//这里给它设置了弹出的时间

        //设置各个控件的点击响应
        EditText edComment = contentView.findViewById(R.id.ed_comment);
        edComment.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        contentView.findViewById(R.id.btn_send).setOnClickListener(v ->
                listener.onResult(mPopWindow, edComment.getText().toString()));
        //是否具有获取焦点的能力
        mPopWindow.setFocusable(true);

        mPopWindow.showAtLocation(anchor, Gravity.BOTTOM, 0, 0);
    }

    public static void setSpannableStr(Context context, TextView textView, int strRes, int imgRes) {
        Resources res = context.getResources();
        String tips = res.getString(strRes);
        SpannableString spannable = new SpannableString(" " + tips);
        MImgSpan imgSpan = new MImgSpan(context, imgRes);
        spannable.setSpan(imgSpan, 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        textView.setText(spannable);
    }

    public static void setSpannableStr(Context context, TextView textView, String tips, int imgRes) {
        SpannableString spannable = new SpannableString(" " + tips);
        MImgSpan imgSpan = new MImgSpan(context, imgRes);
        spannable.setSpan(imgSpan, 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        textView.setText(spannable);
    }

    public static String formatStr(String format, Object... args) {
        return String.format(Locale.ENGLISH, format, args);
    }

    public static String formatStr(Locale locale, String format, Object... args) {
        return String.format(locale, format, args);
    }

    public static String getMoney(double money) {
        NumberFormat nf = NumberFormat.getInstance();
        return nf.format(money);
    }

    public static String getMoney(int money) {
        NumberFormat nf = NumberFormat.getInstance();
        return nf.format(money);
    }

    /**
     * 方法描述：判断某一Service是否正在运行
     *
     * @param context     上下文
     * @param serviceName Service的全路径： 包名 + service的类名
     * @return true 表示正在运行，false 表示没有运行
     */
    public static boolean isServiceRunning(Context context, String serviceName) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServiceInfos = am.getRunningServices(200);
        if (runningServiceInfos.size() <= 0) {
            return false;
        }
        for (ActivityManager.RunningServiceInfo serviceInfo : runningServiceInfos) {
            if (serviceInfo.service.getClassName().equals(serviceName)) {
                return true;
            }
        }
        return false;
    }

    public static void stopServices(Context context, String[] serviceName) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServiceInfos = am.getRunningServices(200);
        if (runningServiceInfos.size() <= 0) {
            return;
        }
        for (ActivityManager.RunningServiceInfo serviceInfo : runningServiceInfos) {
            String servName = serviceInfo.service.getClassName();
            for (String name : serviceName) {
                if (servName.equals(name)) {
                    try {
                        context.stopService(new Intent(context, Class.forName(name)));
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static String getKb(double oriLen) {
        DecimalFormat df = new DecimalFormat("#.00");
        return df.format(oriLen / 1024d) + "kb";
    }

    public static void floatAnim(View view) {
        List<Animator> animators = new ArrayList<>();
        /*ObjectAnimator translationXAnim = ObjectAnimator.ofFloat(view, "translationX", -6.0f, 6.0f, -6.0f);
        translationXAnim.setDuration(3000);
        translationXAnim.setRepeatCount(ValueAnimator.INFINITE);//无限循环
//        translationXAnim.setRepeatMode(ValueAnimator.INFINITE);//
        translationXAnim.start();
        animators.add(translationXAnim);*/

        float moveY = 6.0f * ScreenUtil.mScale;
        ObjectAnimator translationYAnim = ObjectAnimator.ofFloat(view, "translationY", -moveY, moveY, -moveY);
        translationYAnim.setDuration(3000);
        translationYAnim.setRepeatCount(ValueAnimator.INFINITE);
//        translationYAnim.setRepeatMode(ValueAnimator.INFINITE);
        translationYAnim.start();
        animators.add(translationYAnim);

        AnimatorSet btnSexAnimatorSet = new AnimatorSet();
        btnSexAnimatorSet.playTogether(animators);
        btnSexAnimatorSet.start();
    }

    public static void compressImage(Context context, String oriFilePath, OnRenameListener renameListener,
                                     CompressImgCallBack callBack) {
        compressImage(context, oriFilePath, renameListener, 100, callBack);
    }

    public static void compressImage(Context context, String oriFilePath, OnRenameListener renameListener,
                                     int maxSize, CompressImgCallBack callBack) {
        File oriFile = new File(oriFilePath);
        String saveFilePath = FilePath.getCompressedPath();

        double fileLen = (double) oriFile.length() / 1024d;
        LogUtil.e(oriFilePath + "\nlength:" + fileLen + "kb");

        if (fileLen <= 100) {
            LogUtil.e("无需压缩：" + oriFilePath);
            File tempFile = new File(saveFilePath, oriFile.getName());
            FileUtil.copyFile(oriFile, tempFile);
            callBack.onSkip(tempFile.getAbsolutePath());
        } else {
            Luban.Builder builder = Luban.with(context);
            builder.load(oriFilePath)
                    .ignoreBy(maxSize)
                    .setTargetDir(saveFilePath)
                    .setCompressListener(new OnCompressListener() {
                        @Override
                        public void onStart() {
                        }

                        @Override
                        public void onSuccess(File file) {
                            callBack.onSuccess(file);
                        }

                        @Override
                        public void onError(Throwable e) {
                            String msg = "压缩失败";
                            if (e != null) msg = e.getMessage();
                            LogUtil.e(msg);
                            callBack.onFail(oriFilePath, msg);
                        }
                    });
            if (renameListener != null)
                builder.setRenameListener(renameListener);
            builder.launch();
        }
    }


    public static void saveCityCache(String cityName, String cityCode) {
        CacheCity city = new CacheCity(cityName, cityCode);
        LogUtil.e("保存获得的定位数据：" + city.toString());
        SPHelper.putString(LibConstants.COMMON.CACHE_CITY, city.toString());
    }

    public static boolean isEmptyList(List<?> list) {
        return list == null || list.size() == 0;
    }

    public static boolean isNetUrlPath(String urlPath) {
        if (TextUtils.isEmpty(urlPath)) return false;
        return urlPath.contains("http:") || urlPath.contains("https:");
    }

    /**
     * 组装分组界面GridView的数据源，因为我们扫描手机的时候将图片信息放在HashMap中
     * 所以需要遍历HashMap将数据组装成List
     */
    public static List<MediaBean> subMediaGroup(HashMap<String, List<String>> groupMap, boolean isVideo) {
        if (groupMap.size() == 0) {
            return null;
        }
        List<MediaBean> list = new ArrayList<>();
        Iterator<Map.Entry<String, List<String>>> it = groupMap.entrySet().iterator();
        String flag = isVideo ? "所有视频" : "所有图片";
        while (it.hasNext()) {
            Map.Entry<String, List<String>> entry = it.next();
            MediaBean mMediaBean = new MediaBean();
            String key = entry.getKey();
            List<String> value = entry.getValue();
            if (value.size() > 0) {
                mMediaBean.setFolderName(key);
                mMediaBean.setMediaCount(value.size());
                mMediaBean.setTopMediaPath(value.get(0));//获取该组的第1个media的路径
                if (key.equals(flag))
                    list.add(0, mMediaBean);
                else
                    list.add(mMediaBean);
            }
        }
        return list;

    }

    @SuppressLint("MissingPermission")
    public static void CallPhone(Context mContext, String phoneNo) {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNo));
        mContext.startActivity(intent);
    }

    public static String getPriceString(double price) {
        return String.valueOf("¥" + getMoney(price));
    }

    public static String getPriceStringB(double price) {
        return String.valueOf("¥ " + getMoney(price));
    }


    public static void closeAndroidPDialog() {
        if (Build.VERSION.SDK_INT < 28) return;
        try {
            Class aClass = Class.forName("android.content.pm.PackageParser$Package");
            Constructor declaredConstructor = aClass.getDeclaredConstructor(String.class);
            declaredConstructor.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Class cls = Class.forName("android.app.ActivityThread");
            Method declaredMethod = cls.getDeclaredMethod("currentActivityThread");
            declaredMethod.setAccessible(true);
            Object activityThread = declaredMethod.invoke(null);
            Field mHiddenApiWarningShown = cls.getDeclaredField("mHiddenApiWarningShown");
            mHiddenApiWarningShown.setAccessible(true);
            mHiddenApiWarningShown.setBoolean(activityThread, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 保存数据到本地文件
     *
     * @param dir      存储的目录
     * @param filename 存储的文件名(a.txt)
     * @param content  存储的数据({'a':'b'})
     */
    public static void saveCache(File dir, String filename, String content) {
        try {
            File file = new File(dir, filename);
            FileOutputStream fos = new FileOutputStream(file);
            OutputStreamWriter writer = new OutputStreamWriter(fos, "UTF-8");
            writer.write(content);
            writer.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * ↑→读取
     */
    public static String readCache(File dir, String fileName) {
        StringBuilder sb = new StringBuilder();
        InputStream ins = null;
        long readTime = 0;
        if (x.isDebug()) {
            readTime = System.currentTimeMillis();
            LogUtil.e("【Read json start】" + readTime);
        }
        try {
            File file = new File(dir, fileName);
            if (!file.exists()) return null;
            ins = new FileInputStream(file);

            InputStreamReader reader = new InputStreamReader(ins, "UTF-8");
            BufferedReader br = new BufferedReader(reader);
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();
            reader.close();
            if (x.isDebug()) {
                long readTime2 = System.currentTimeMillis();
                LogUtil.e("【Read json finish】" + readTime2 + ",cost time:" +
                        (readTime2 - readTime) + "\nresult:" + sb);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ins != null) {
                try {
                    ins.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

    /**
     * ↑→读取并解析
     */
    public static <T> void getCache(File dir, String fileName, Class cacheClass,
                                    GetCacheCallBack<T> callBack) {
        LogUtil.e("读取缓存：" + fileName);
        String cacheJson = readCache(dir, fileName);
        if (TextUtils.isEmpty(cacheJson)) {
            LogUtil.e("无缓存:" + fileName);
            callBack.notExist();
            return;
        }
        try {
            ArrayList<T> cacheList = GsonUtil.getArr(cacheJson, cacheClass);
            if (MTool.isEmptyList(cacheList)) {
                LogUtil.e("无缓存:" + fileName);
                callBack.notExist();
            } else {
                LogUtil.e(cacheList.size() + "条");
                callBack.exist(cacheList);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (!TextUtils.isEmpty(e.getMessage())) LogUtil.e(e.getMessage());
            LogUtil.e("解析缓存失败");
            try {
                File cacheFile = new File(dir, fileName);
                if (cacheFile.exists()) cacheFile.delete();
            } catch (Exception e1) {
                e1.printStackTrace();
                if (!TextUtils.isEmpty(e1.getMessage())) LogUtil.e(e1.getMessage());
                LogUtil.e("删除缓存文件失败");
            }
            callBack.notExist();
        }
    }

 /*   public static double getDistance(PointF p1, PointF p2) {
        double _x = Math.abs(p1.x - p2.x);
        double _y = Math.abs(p1.y - p2.y);
        return Math.sqrt(_x * _x + _y * _y);
    }*/

    /**
     * 获取手指间的距离
     */
/*    public static float getSpaceDistance(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }*/

    /**
     * 获取手势中心点
     */
/*    public static PointF getMidPoint(MotionEvent event) {
        PointF point = new PointF();
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
        return point;
    }*/
}
//    public static String getCnLevel(int index) {
//        final String[] cnLevels = {"一", "二", "三", "四", "五", "六", "七", "八", "九", "十",
//                "十一", "十二", "十三", "十四", "十五", "十六", "十七", "十八", "十九", "二十"};
//        return cnLevels[index];
//    }

/*

    public static void SendSMS(AppCompatActivity mContext, String phoneNoArr, String msg) {
        try {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("smsto:" + Uri.encode(phoneNoArr)));
            intent.putExtra(Intent.EXTRA_TEXT, msg);
            intent.putExtra("sms_body", msg);
            mContext.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e(e.toString());
        }
    }

    public static String getCnIndex(int i) {
        String cnIndex;
        switch (i) {
            case 1:
                cnIndex = "二";
                break;
            case 2:
                cnIndex = "三";
                break;
            case 3:
                cnIndex = "四";
                break;
            case 4:
                cnIndex = "五";
                break;
            case 5:
                cnIndex = "六";
                break;
            default:
                cnIndex = "一";
        }
        return cnIndex;
    }

    public static String getAverage(String a, String b) {
        Double d1 = Double.parseDouble(a);
        Double d2 = Double.parseDouble(b);
        DecimalFormat df = new DecimalFormat("#.00");
        double c = d1 / d2;
        if (c > 0 && c < 1) {
            return "0" + df.format(c);
        } else
            return df.format(c);
    }
*/

   /*

    public static String getRate(double rate) {
        String r = rate + "";
        int i = r.indexOf(".");
        if (i > -1) {
            String e = r.substring(i);
            float f = Float.parseFloat(e);
            if (f > 0) {
                return r;
            } else return r.substring(0, i);
        } else return r;
    }

    public static String getVideoLength(int dur) {
        int h = dur / 3600;
        int m = (dur - h * 3600) / 60;
        int s = (dur - h * 3600) % 60;

        String hh = h < 10 ? "0" + h : "" + h;//小时

        String mm = m < 10 ? "0" + m : "" + m;//分钟

        String ss = s < 10 ? "0" + s : "" + s;//秒钟

        return hh + ":" + mm + ":" + ss;
    }


    public static String encodePhone(String tel) {
        if (tel.length() < 7) return tel;
        return tel.substring(0, 3) + "****" + tel.substring(7);
    }

    public static void launchWx(Context context) {
        try {
            Intent intent = new Intent();
            ComponentName cmp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI");
            intent.setAction(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setComponent(cmp);
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            Toast(context, "您还没有安装微信");
        }
    }

    public static void launchQq(Context context, String qqNo) {
        String url = "mqqwpa://im/chat?chat_type=wpa&uin=" + qqNo;
        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }

    public static String getLevelStr(int level) {
        final String[] starLevels = {"全部", "一星级", "二星级", "三星级", "四星级", "五星级"};
        return starLevels[level];
    }

    //判断服务是否处于运行状态.
   public static boolean isServiceRunning(String serviceName, Context context) {
       ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
       List<ActivityManager.RunningServiceInfo> infos = am.getRunningServices(100);
       for (ActivityManager.RunningServiceInfo info : infos) {
           if (serviceName.equals(info.service.getClassName())) {
               return true;
           }
       }
       return false;
   }
*/



 /*   public static int getNaviHeight(Context context) {
        int result = 0;
        int resourceId = 0;
        int rid = context.getResources().getIdentifier("config_showNavigationBar", "bool", "android");
        if (rid != 0) {
            resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
            return context.getResources().getDimensionPixelSize(resourceId);
        } else
            return 0;
    }

  //格式化资讯：阅读量、收藏量、点赞量
    public static String getFormatWebCount(int count) {
        String result;

        if (count / 10000 > 0) {
            result = count / 10000 + "w+";
        } else if (count / 1000 > 0) {
            result = count / 1000 + "k+";
        } else {
            result = count + "";
        }
        return result;
    }

    */
/*
    private void getGLESTextureLimitBelowLollipop() {
        int[] maxSize = new int[1];
        GLES10.glGetIntegerv(GLES10.GL_MAX_TEXTURE_SIZE, maxSize, 0);
        LogUtil.e("maxSize:" + maxSize[0]);
//        Toast.makeText(this, " " + maxSize[0], Toast.LENGTH_LONG).show();
    }

    private void getGLESTextureLimitEqualAboveLollipop() {
        EGL10 egl = (EGL10) EGLContext.getEGL();
        EGLDisplay dpy = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
        int[] vers = new int[2];
        egl.eglInitialize(dpy, vers);
        int[] configAttr = {
                EGL10.EGL_COLOR_BUFFER_TYPE, EGL10.EGL_RGB_BUFFER,
                EGL10.EGL_LEVEL, 0,
                EGL10.EGL_SURFACE_TYPE, EGL10.EGL_PBUFFER_BIT,
                EGL10.EGL_NONE
        };
        EGLConfig[] configs = new EGLConfig[1];
        int[] numConfig = new int[1];
        egl.eglChooseConfig(dpy, configAttr, configs, 1, numConfig);
        if (numConfig[0] == 0) {// TROUBLE! No config found.
        }
        EGLConfig config = configs[0];
        int[] surfAttr = {
                EGL10.EGL_WIDTH, 64,
                EGL10.EGL_HEIGHT, 64,
                EGL10.EGL_NONE
        };
        EGLSurface surf = egl.eglCreatePbufferSurface(dpy, config, surfAttr);
        final int EGL_CONTEXT_CLIENT_VERSION = 0x3098; // missing in EGL10
        int[] ctxAttrib = {
                EGL_CONTEXT_CLIENT_VERSION, 1,
                EGL10.EGL_NONE
        };
        EGLContext ctx = egl.eglCreateContext(dpy, config, EGL10.EGL_NO_CONTEXT, ctxAttrib);
        egl.eglMakeCurrent(dpy, surf, surf, ctx);
        int[] maxSize = new int[1];
        GLES10.glGetIntegerv(GLES10.GL_MAX_TEXTURE_SIZE, maxSize, 0);
        egl.eglMakeCurrent(dpy, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE,
                EGL10.EGL_NO_CONTEXT);
        egl.eglDestroySurface(dpy, surf);
        egl.eglDestroyContext(dpy, ctx);
        egl.eglTerminate(dpy);

        LogUtil.e("maxSize:" + maxSize[0]);
//        Toast.makeText(this, " " + maxSize[0], Toast.LENGTH_LONG).show();
    }*/
//    public static String encryptNick(String nick) {
//        if(MTool.isEmpty(nick))return nick;
//        if (tel.length() < 7) return tel;
//        return tel.substring(0, 3) + "****" + tel.substring(7);
//    }


    /*public static void stopService(Context context, String serviceName) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServiceInfos = am.getRunningServices(200);
        if (runningServiceInfos.size() <= 0) {
            return;
        }
        for (ActivityManager.RunningServiceInfo serviceInfo : runningServiceInfos) {
            String servName = serviceInfo.service.getClassName();
            if (servName.equals(serviceName)) {
                try {
                    context.stopService(new Intent(context, Class.forName(serviceName)));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }*/
    /*public static int getContentHeight(Context context) {
        WindowManager windowManager =
                (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        final Display display = windowManager.getDefaultDisplay();
        Point outPoint = new Point();
        if (Build.VERSION.SDK_INT >= 19) {
            // 可能有虚拟按键的情况
            display.getRealSize(outPoint);
        } else {
            // 不可能有虚拟按键
            display.getSize(outPoint);
        }
        int mRealSizeWidth;//手机屏幕真实宽度
        int mRealSizeHeight;//手机屏幕真实高度
        mRealSizeHeight = outPoint.y;
        mRealSizeWidth = outPoint.x;
        LogUtil.e("width&height:" + mRealSizeWidth + "&" + mRealSizeHeight);
        return mRealSizeHeight;
    }*/
