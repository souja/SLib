package com.souja.lib;

import android.app.Application;

import com.souja.lib.utils.LibConstants;
import com.souja.lib.utils.SHttpUtil;
import com.souja.lib.utils.SPHelper;

import org.xutils.common.util.KeyValue;
import org.xutils.common.util.LogUtil;
import org.xutils.x;

public class SLib {

    public static class DebugInfo {
        boolean isDebug;
        String dirDebug;
        String dirPro;

        public DebugInfo(boolean isDebug, String dirDebug, String dirPro) {
            this.isDebug = isDebug;
            this.dirDebug = dirDebug;
            this.dirPro = dirPro;
        }
    }

    public static void init(Application context, String packageName, DebugInfo debugInfo,
                            String[] noVersionContrlApis, KeyValue... identifyParams) {

        x.Ext.init(context);
        x.Ext.setDebug(debugInfo.isDebug);
        LogUtil.customTagPrefix = "【" + packageName.substring(packageName.lastIndexOf(".") + 1) + "】";

        SPHelper.init(context, packageName);

        SHttpUtil.setContext(context);
        LibConstants.APP_NAME = debugInfo.isDebug ? debugInfo.dirDebug : debugInfo.dirPro;
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
