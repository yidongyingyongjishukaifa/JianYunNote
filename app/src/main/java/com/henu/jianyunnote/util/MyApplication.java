package com.henu.jianyunnote.util;

import android.app.Application;
import android.content.Context;

import org.litepal.LitePal;

//获取全局状态信息
public class MyApplication extends Application {

    private static Context context;

    @Override
    public void onCreate(){
        super.onCreate();
        context = getApplicationContext();
        LitePal.initialize(this);
    }

    //全局context
    public static Context getContext(){
        return context;
    }
}
