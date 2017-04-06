package com.focustech.securityexception;

import com.focustech.securityexception.handlers.IgnoreHandler;

/**
 * Created by zhangzheng on 2017/4/5.
 */

public class ChildThreadHandlerExceptionFactory implements IHandlerExceptionFactory {
    @Override
    public IHandlerException get(Throwable e) {
        return new IgnoreHandler();
    }
}
