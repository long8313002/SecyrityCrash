package com.focustech.securityexception;

/**
 * Created by zhangzheng on 2017/4/5.
 */

public interface IHandlerExceptionFactory {

    IHandlerException get(Throwable e);

}
