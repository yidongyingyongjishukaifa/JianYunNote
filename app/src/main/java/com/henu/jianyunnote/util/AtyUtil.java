package com.henu.jianyunnote.util;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

public class AtyUtil {
    private AtyUtil() {
    }

    private static AtyUtil instance = new AtyUtil();
    private static List<Activity> activityStack = new ArrayList<Activity>();

    public static AtyUtil getInstance() {
        return instance;
    }

    public void addActivity(Activity aty) {
        activityStack.add(aty);
    }

    public void removeActivity(Activity aty) {
        activityStack.remove(aty);
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (null != activityStack.get(i)) {
                activityStack.get(i).finish();
            }
        }
        activityStack.clear();
    }
}
