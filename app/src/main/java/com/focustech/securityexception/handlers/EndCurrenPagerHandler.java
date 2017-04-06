package com.focustech.securityexception.handlers;

import android.app.Activity;

import com.focustech.securityexception.IHandlerException;
import com.focustech.securityexception.WindowManagerGlobal;

/**
 * Created by zhangzheng on 2017/4/5.
 */

public class EndCurrenPagerHandler implements IHandlerException {
    @Override
    public boolean handler(Throwable e) {
        Activity currenActivity = WindowManagerGlobal.getInstance().getCurrenActivity();
        if (currenActivity != null) {
            currenActivity.finish();
        }
        return false;
    }
}
