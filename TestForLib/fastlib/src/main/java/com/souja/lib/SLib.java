package com.souja.lib;

import android.content.Context;

import com.souja.lib.utils.LibConstants;
import com.souja.lib.utils.SHttpUtil;

import org.xutils.common.util.KeyValue;

public class SLib {


    public static void init(Context context, String appName, String packageName,
                            String[] noVersionContrlApis, KeyValue... identifyParams) {
        SHttpUtil.setContext(context);
        LibConstants.APP_NAME = appName;
        LibConstants.packageName = packageName;
        LibConstants.FILE_PROVIDER = packageName + ".fileProvider";

        if (noVersionContrlApis != null && noVersionContrlApis.length > 0) {
            for (String api : noVersionContrlApis)
                SHttpUtil.addOutVersionControl(api);
        }
        if (identifyParams != null && identifyParams.length > 0) {
            for (KeyValue kv : identifyParams) {
                SHttpUtil.addIdentifyParam(kv.key, (String) kv.value);
            }
        }
    }
}
