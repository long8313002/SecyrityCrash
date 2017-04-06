package com.focustech.securityexception;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by zhangzheng on 2017/4/5.
 */

public class SecyrityCrash {

    private static SecyrityCrash instance = new SecyrityCrash();

    private Thread.UncaughtExceptionHandler defaultUncaughtExceptionHandler;
    private IHandlerExceptionFactory mainThreadFactory;
    private IHandlerExceptionFactory childThreadFactory;
    private OnExceptionCallBack onExceptionCallBack;

    private SecyrityCrash() {
        mainThreadFactory = new HandlerExceptionFactory();
        childThreadFactory = new ChildThreadHandlerExceptionFactory();
        defaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
    }

    public static void setMainThreadFactory(IHandlerExceptionFactory mainThreadFactory) {
        instance.mainThreadFactory = mainThreadFactory;
    }

    public static void setChildThreadFactory(IHandlerExceptionFactory childThreadFactory) {
        instance.childThreadFactory = childThreadFactory;
    }

    public static void setOnExceptionCallBack(OnExceptionCallBack onExceptionCallBack) {
        instance.onExceptionCallBack = onExceptionCallBack;
    }

    public static void install() {
        instance.installImp();
    }

    private void installImp() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Looper.loop();
                    } catch (Throwable e) {
                        IHandlerException handler = mainThreadFactory.get(e);
                        if (onExceptionCallBack != null) {
                            onExceptionCallBack.onThrowException(Thread.currentThread(), e, handler);
                        }
                        if (handler == null) {
                            defaultUncaughtExceptionHandler.uncaughtException(Thread.currentThread(), e);
                            return;
                        }
                        if (handler.handler(e)) {
                            return;
                        }
                    }
                }
            }
        });

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                IHandlerException handler = childThreadFactory.get(e);
                if (onExceptionCallBack != null) {
                    onExceptionCallBack.onThrowException(t, e, handler);
                }
                if (handler == null) {
                    defaultUncaughtExceptionHandler.uncaughtException(t, e);
                    return;
                }
                handler.handler(e);
            }
        });

    }

}
