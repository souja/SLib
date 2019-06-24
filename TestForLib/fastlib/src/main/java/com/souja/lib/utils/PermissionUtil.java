package com.souja.lib.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class PermissionUtil {

    public static final int REQUEST_PHONE_PERMISSIONS = 66666;

    public interface PermissionCheckListener {
        void ok();

        void notOk();
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


    public void check() {
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
                ((AppCompatActivity) mContext).requestPermissions(
                        unGrantedPermissions.toArray(new String[unGrantedPermissions.size()]),
                        REQUEST_PHONE_PERMISSIONS);
            }
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

}
