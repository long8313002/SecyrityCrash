package com.focustech.securityexception;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by zhangzheng on 2017/3/30.
 */

public class WindowManagerGlobal {

    private Object windowGlobal;
    private Field mViews;
    private static WindowManagerGlobal windowManagerGlobal;

    public static WindowManagerGlobal getInstance() {
        if (windowManagerGlobal == null) {
            windowManagerGlobal = new WindowManagerGlobal();
        }
        return windowManagerGlobal;
    }

    private WindowManagerGlobal() {
        try {
            Class<?> windowManagerGlobal = Class.forName("android.view.WindowManagerGlobal");
            Method getInstance = windowManagerGlobal.getDeclaredMethod("getInstance");
            getInstance.setAccessible(true);
            windowGlobal = getInstance.invoke(null);
            mViews = windowManagerGlobal.getDeclaredField("mViews");
            mViews.setAccessible(true);
        } catch (Exception e) {
        }
    }

    public boolean rootViewIsArray() {
        return mViews.getType() == View[].class;
    }

    public boolean rootViewIsList() {
        return mViews.getType() == ArrayList.class;
    }

    public Object getRootViews() {
        try {
            return mViews.get(windowGlobal);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<View> getAllActivityRootViews() {
        Object rootViews = getRootViews();
        List<View> activityRootViews = new ArrayList<>();
        if (rootViewIsArray()) {
            for (View view : (View[]) rootViews) {
                if (view.getContext() instanceof Activity) {
                    activityRootViews.add(view);
                }
            }
        } else if (rootViewIsList()) {
            for (View view : (List<View>) rootViews) {
                if (view.getContext() instanceof Activity) {
                    activityRootViews.add(view);
                }
            }
        }
        return activityRootViews;
    }

    public List<View> getAllActiveActivityViews() {
        List<View> allActivityRootViews = getAllActivityRootViews();
        Iterator<View> iterator = allActivityRootViews.iterator();
        while (iterator.hasNext()) {
            if (((Activity) iterator.next().getContext()).isFinishing()) {
                iterator.remove();
            }
        }
        return allActivityRootViews;
    }

    public Activity getCurrenActivity() {
        List<View> views = getAllActiveActivityViews();
        if (views == null || views.size() == 0) {
            return null;
        }
        int index = views.size() - 1;

        while (index >= 0) {
            Context context = views.get(index).getContext();
            if (context instanceof Activity) {
                return (Activity) context;
            }
            index--;
        }
        return null;
    }
}
