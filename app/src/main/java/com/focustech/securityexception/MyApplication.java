package com.focustech.securityexception;

import android.app.Application;

/**
 * Created by zhangzheng on 2017/4/5.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SecyrityCrash.install();
    }

}
