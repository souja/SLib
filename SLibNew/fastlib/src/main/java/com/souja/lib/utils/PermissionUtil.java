package com.souja.lib.utils;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;

import androidx.appcompat.app.AppCompatActivity;

import android.provider.Settings;
import android.text.TextUtils;

import org.xutils.common.util.LogUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class PermissionUtil {

    public static final int REQUEST_PERMISSIONS = 66666;

    public interface PermissionCheckListener {
        void ok();

        void notOk();

        void denied();
    }

    public PermissionUtil(Context context, PermissionCheckListener listener, String... permissions) {
        mContext = context;
        mListener = listener;
        needCheckPermissions = new ArrayList<>();

        if (permissions != null && permissions.length > 0) {
            for (String permission : permissions) {
                if (!TextUtils.isEmpty(permission))
                    needCheckPermissions.add(permission);
            }
        }
    }

    public PermissionUtil(Context context) {
        mContext = context;
    }

    public void setListener(PermissionCheckListener listener) {
        mListener = listener;
    }

    public void addPermissions(String... permissions) {
        if (needCheckPermissions == null) needCheckPermissions = new ArrayList<>();
        else needCheckPermissions.clear();
        if (permissions != null && permissions.length > 0) {
            for (String permission : permissions) {
                if (!TextUtils.isEmpty(permission))
                    needCheckPermissions.add(permission);
            }
        }
    }

    private Context mContext;
    private PermissionCheckListener mListener;//检查权限后的回调
    private List<String> needCheckPermissions;//需要检查的权限列表


    public void checkPermissions() {
        if (needCheckPermissions.size() == 0) {
            if (mListener != null)
                mListener.ok();
            return;
        }
        List<String> unGrantedPermissions = new ArrayList<>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            for (String permission : needCheckPermissions) {
                if ((mContext.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED))
                    unGrantedPermissions.add(permission);
            }

            if (unGrantedPermissions.size() == 0) {//所有权限都已获取
                if (mListener != null)
                    mListener.ok();
            } else {
//                boolean denied = false;
//                for (String permission : unGrantedPermissions) {
//                    if ((mContext.checkSelfPermission(permission) == PackageManager.PERMISSION_DENIED)) {
//                        denied = true;
//                        break;
//                    }
//                }
//                if (denied) {
//                    mListener.denied();
//                } else {
                    ((AppCompatActivity) mContext).requestPermissions(
                            unGrantedPermissions.toArray(new String[unGrantedPermissions.size()]),
                            REQUEST_PERMISSIONS);
//                }
            }
        }else{
            if (mListener != null)
                mListener.ok();
        }

    }

    public void handleResults(int[] grantResults) {
        if (grantResults == null && grantResults.length == 0) {
            if (mListener != null) mListener.ok();
        } else {
            boolean allGrant = true;
            for (int status : grantResults) {
                if (status != PackageManager.PERMISSION_GRANTED) {
                    allGrant = false;
                    break;
                }
            }
            if (allGrant) {
                if (mListener != null) mListener.ok();
            } else {
                if (mListener != null) mListener.notOk();
            }
        }
    }

    public void jumpPermissionPage() {
        String name = Build.MANUFACTURER;
        LogUtil.e("jumpPermissionPage --- name : " + name);
        switch (name) {
            case "HUAWEI":
                goHuaWeiMainager();
                break;
            case "vivo":
                goVivoMainager();
                break;
            case "OPPO":
                goOppoMainager();
                break;
            case "Coolpad":
                goCoolpadMainager();
                break;
            case "Meizu":
                goMeizuMainager();
                break;
            case "Xiaomi":
                goXiaoMiMainager();
                break;
            case "samsung":
                goSangXinMainager();
                break;
            case "Sony":
                goSonyMainager();
                break;
            case "LG":
                goLGMainager();
                break;
            default:
                goIntentSetting();
                break;
        }
    }

    private void goLGMainager() {
        try {
            Intent intent = new Intent(mContext.getPackageName());
            ComponentName comp = new ComponentName("com.android.settings", "com.android.settings.Settings$AccessLockSummaryActivity");
            intent.setComponent(comp);
            mContext.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            goIntentSetting();
        }
    }

    private void goSonyMainager() {
        try {
            Intent intent = new Intent(mContext.getPackageName());
            ComponentName comp = new ComponentName("com.sonymobile.cta", "com.sonymobile.cta.SomcCTAMainActivity");
            intent.setComponent(comp);
            mContext.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            goIntentSetting();
        }
    }

    private void goHuaWeiMainager() {
        try {
            Intent intent = new Intent(mContext.getPackageName());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ComponentName comp = new ComponentName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MainActivity");
            intent.setComponent(comp);
            mContext.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            goIntentSetting();
        }
    }

    private static String getMiuiVersion() {
        String propName = "ro.miui.ui.version.name";
        String line;
        BufferedReader input = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop " + propName);
            input = new BufferedReader(
                    new InputStreamReader(p.getInputStream()), 1024);
            line = input.readLine();
            input.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        } finally {
            try {
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return line;
    }

    private void goXiaoMiMainager() {
        String rom = getMiuiVersion();
        LogUtil.e("goMiaoMiMainager --- rom : " + rom);
        Intent intent = new Intent();
        if ("V6".equals(rom) || "V7".equals(rom)) {
            intent.setAction("miui.intent.action.APP_PERM_EDITOR");
            intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
            intent.putExtra("extra_pkgname", mContext.getPackageName());
        } else if ("V8".equals(rom) || "V9".equals(rom)) {
            intent.setAction("miui.intent.action.APP_PERM_EDITOR");
            intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity");
            intent.putExtra("extra_pkgname", mContext.getPackageName());
        } else {
            goIntentSetting();
        }
        mContext.startActivity(intent);
    }

    private void goMeizuMainager() {
        try {
            Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.putExtra("packageName", mContext.getPackageName());
            mContext.startActivity(intent);
        } catch (ActivityNotFoundException localActivityNotFoundException) {
            localActivityNotFoundException.printStackTrace();
            goIntentSetting();
        }
    }

    private void goSangXinMainager() {
        //三星4.3可以直接跳转
        goIntentSetting();
    }

    private void goIntentSetting() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", mContext.getPackageName(), null);
        intent.setData(uri);
        try {
            mContext.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            mContext.startActivity(new Intent(Settings.ACTION_SETTINGS));
        }
    }

    private void goOppoMainager() {
        doStartApplicationWithPackageName("com.coloros.safecenter");
    }

    /**
     * doStartApplicationWithPackageName("com.yulong.android.security:remote")
     * 和Intent open = getPackageManager().getLaunchIntentForPackage("com.yulong.android.security:remote");
     * startActivity(open);
     * 本质上没有什么区别，通过Intent open...打开比调用doStartApplicationWithPackageName方法更快，也是android本身提供的方法
     */
    private void goCoolpadMainager() {
        doStartApplicationWithPackageName("com.yulong.android.security:remote");
      /*  Intent openQQ = getPackageManager().getLaunchIntentForPackage("com.yulong.android.security:remote");
        startActivity(openQQ);*/
    }

    private void goVivoMainager() {
        doStartApplicationWithPackageName("com.bairenkeji.icaller");
     /*   Intent openQQ = getPackageManager().getLaunchIntentForPackage("com.vivo.securedaemonservice");
        startActivity(openQQ);*/
    }

    private void doStartApplicationWithPackageName(String packagename) {
        // 通过包名获取此APP详细信息，包括Activities、services、versioncode、name等等
        PackageInfo packageinfo = null;
        try {
            packageinfo = mContext.getPackageManager().getPackageInfo(packagename, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageinfo == null) {
            return;
        }
        // 创建一个类别为CATEGORY_LAUNCHER的该包名的Intent
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(packageinfo.packageName);
        // 通过getPackageManager()的queryIntentActivities方法遍历
        List<ResolveInfo> resolveinfoList = mContext.getPackageManager()
                .queryIntentActivities(resolveIntent, 0);
        LogUtil.e("resolveinfoList" + resolveinfoList.size());
        for (int i = 0; i < resolveinfoList.size(); i++) {
            LogUtil.e(resolveinfoList.get(i).activityInfo.packageName + resolveinfoList.get(i).activityInfo.name);
        }
        ResolveInfo resolveinfo = resolveinfoList.iterator().next();
        if (resolveinfo != null) {
            // packageName参数2 = 参数 packname
            String packageName = resolveinfo.activityInfo.packageName;
            // 这个就是我们要找的该APP的LAUNCHER的Activity[组织形式：packageName参数2.mainActivityname]
            String className = resolveinfo.activityInfo.name;
            // LAUNCHER Intent
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            // 设置ComponentName参数1:packageName参数2:MainActivity路径
            ComponentName cn = new ComponentName(packageName, className);
            intent.setComponent(cn);
            try {
                mContext.startActivity(intent);
            } catch (Exception e) {
                goIntentSetting();
                e.printStackTrace();
            }
        }
    }
}