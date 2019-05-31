package com.souja.lib.utils;

import android.app.Activity;

import org.xutils.common.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class ActMgr {
    private static ActMgr instance;
    private List<Activity> activityList;

    public synchronized static ActMgr getInstance() {
        if (instance == null) {
            instance = new ActMgr();
            instance.init();
        }
        return instance;
    }

    private void init() {
        activityList = new ArrayList<>();
    }

    public void addActivity(Activity activity) {
        activityList.add(activity);
    }

    public void removeActivity(Activity activity) {
        if (activity == null) return;
        if (activityList.contains(activity))
            activityList.remove(activity);
    }

    public void closeAct(int level) {
//        if (level == -1) level = 1;
        for (int i = 0; i < level; i++) {
            Activity lastAct = activityList.get(activityList.size() - 1);
            LogUtil.e("close act " + lastAct.getClass().getName());
            activityList.remove(lastAct);
            lastAct.finish();
        }
    }

    public void close(int level) {
        if (level < 1) level = 1;
        while (activityList.size() > level) {
            Activity lastAct = activityList.get(activityList.size() - 1);
            LogUtil.e("close act " + lastAct.getClass().getName());
            activityList.remove(lastAct);
            lastAct.finish();
        }
    }

}
