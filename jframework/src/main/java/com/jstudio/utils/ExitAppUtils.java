package com.jstudio.utils;

import android.app.Activity;
import android.app.Service;

import java.util.LinkedList;
import java.util.List;

/**
 * 完全退出App的工具类，需要配合BaseAppCompatActivity和BaseService使用，要使用自定义的Activity则要做一下两步
 * <p>1.在每个Activity的onCreate方法中执行addActivity()，Service则执行addService()
 * <p>2.在每个Activity的onDestroy方法中执行removeActivity()，Service则执行removeService()
 */
public class ExitAppUtils {

    /**
     * 存储Activity的容器
     */
    private List<Activity> mActivityList = new LinkedList<>();
    /**
     * 存储Service的容器
     */
    private List<Service> mServiceList = new LinkedList<>();
    private static ExitAppUtils INSTANCE;

    private ExitAppUtils() {
    }

    /**
     * 获取此组件管理类
     *
     * @return ExitAppUtils单例
     */
    public static ExitAppUtils getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ExitAppUtils();
        }
        return INSTANCE;
    }

    /**
     * 添加Activity到容器当中，在onCreate中执行
     *
     * @param activity Activity
     */
    public void addActivity(Activity activity) {
        mActivityList.add(activity);
    }

    /**
     * 返回Activity栈中的数量
     *
     * @return Activity数
     */
    public int getActivityStackCount() {
        return mActivityList.size();
    }

    /**
     * 清除Activity栈，并结束
     */
    public void terminateAllActivity() {
        for (Activity activity : mActivityList) {
            activity.finish();
        }
        if (mActivityList != null) {
            mActivityList.clear();
        }
    }

    /**
     * 添加Service到容器当中，在onCreate中执行
     *
     * @param service Service
     */
    public void addService(Service service) {
        mServiceList.add(service);
    }

    /**
     * 返回Service栈中的数量
     *
     * @return Service数
     */
    public int getServiceStackCount() {
        return mServiceList.size();
    }

    /**
     * 清除Service栈，并结束
     */
    public void terminateAllService() {
        for (Service service : mServiceList) {
            service.stopSelf();
        }
        if (mServiceList != null) {
            mServiceList.clear();
        }
    }

    /**
     * 从容器中移除Activity，在onDestroy中执行
     *
     * @param activity Activity
     */
    public void removeActivity(Activity activity) {
        mActivityList.remove(activity);
    }

    /**
     * 从容器中移除Service，在onDestroy中执行
     *
     * @param service Activity
     */
    public void removeService(Service service) {
        mServiceList.remove(service);
    }

    /**
     * 终结容器当中所有的Activity以及Service
     */
    public void exit() {
        terminateAllActivity();
        terminateAllService();
        System.exit(0);
    }
}
