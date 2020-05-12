package com.project.mapping;

import android.app.Application;
import android.content.Context;

import com.tencent.stat.StatConfig;
import com.tencent.stat.StatService;

public class MappingApplication extends Application {
    public static Context mContext;
    public static int treeWidth;
    public static int treeHeight;

    @Override

    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        // 打开Logcat输出，上线时，一定要关闭
        StatConfig.setDebugEnable(false);
        // 注册activity生命周期，统计时长
        StatService.registerActivityLifecycleCallbacks(this);
    }
}
