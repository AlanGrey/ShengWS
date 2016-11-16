package com.jstudio.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Jason
 */
@SuppressWarnings("unused")
public class PackageUtils {

    /**
     * 检查Service是否正在运行
     *
     * @param context Context
     * @param cls     Service的class
     * @return true表示服务正在运行
     */
    public static boolean isServiceRunning(Context context, Class<?> cls) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);
        for (ActivityManager.RunningServiceInfo serviceInfo : services) {
            ComponentName componentName = serviceInfo.service;
            String serviceName = componentName.getClassName();
            if (serviceName.equalsIgnoreCase(cls.getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 返回当前屏幕显示的Activity的名称，必须指定GET_TASKS权限
     *
     * @param context Context
     * @return 获取失败将返回null
     */
    public static String getCurrentActivity(Context context) {
        try {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            return activityManager.getRunningTasks(1).get(0).topActivity.getClassName();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 检测当前硬件是否支持某些功能
     *
     * @param context Context
     * @param feature PackageManager中的功能常量，如{@link PackageManager#FEATURE_BLUETOOTH_LE}
     * @return true表示支持，否则返回false
     */
    public static boolean isFeatureSupported(Context context, String feature) {
        return context.getPackageManager().hasSystemFeature(feature);
    }

    /**
     * 通过指定包名获取该App的Icon
     *
     * @param context     Context
     * @param packageName 包名
     * @return 该App的Icon对应的Drawable对象
     */
    public static Drawable getAppIcon(Context context, String packageName) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> packageInfo = packageManager.getInstalledPackages(0);
        List<String> packageNames = new ArrayList<>();
        if (packageInfo != null) {
            for (int i = 0; i < packageInfo.size(); i++) {
                String packName = packageInfo.get(i).packageName;
                packageNames.add(packName);
                if (packageNames.contains(packageName)) {
                    return packageInfo.get(i).applicationInfo.loadIcon(packageManager);
                }
            }
        }
        return null;
    }

    /**
     * 通过包名检查对应的App是否由安装
     *
     * @param context     Context
     * @param packageName 包名
     * @return true表示有安装该应用
     */
    public static boolean isAppInstalled(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return false;
        }
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> packageInfo = packageManager.getInstalledPackages(0);
        for (int i = 0; i < packageInfo.size(); i++) {
            if (packageInfo.get(i).packageName.equalsIgnoreCase(packageName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取App的版本号
     *
     * @param context Context
     * @return 返回App的版本号，－1表示获取失败
     */
    public static int getVersionCode(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            return -1;
        }
    }

    /**
     * 获取App的版本名
     *
     * @param context Context
     * @return 返回App的版本名，获取失败将返回null
     */
    public static String getVersionName(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    /**
     * 获取当前Context的包名
     *
     * @param context Context
     * @return 返回包名，获取失败将返回null
     */
    public static String getPackageName(Context context) {
        return context.getPackageName();
    }

    /**
     * 获取AndroidManifest中的meta data
     *
     * @param context Context
     * @return 存有meta data的Bundle
     */
    public static Bundle getMetaData(Context context) {
        PackageManager manager = context.getPackageManager();
        try {
            ApplicationInfo info = manager.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            return info.metaData;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 通过pId获得应用名
     *
     * @param context Context
     * @param pId     进程的pId
     * @return 应用名
     */
    public static String getAppName(Context context, int pId) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        PackageManager pm = context.getPackageManager();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pId) {
                    CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));
                    return info.processName;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 获取当前的进程名
     *
     * @param context Context
     * @return 当前进程名
     */
    public static String getCurrentProcessName(Context context) {
        ActivityManager actMgr = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appList = actMgr.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info : appList) {
            if (info.pid == android.os.Process.myPid()) {
                return info.processName;
            }
        }
        return "";
    }
}