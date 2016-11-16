package com.jstudio.utils;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Build.VERSION;
import android.provider.Settings;
import android.telephony.TelephonyManager;

public class PhoneUtils {

    /**
     * 手机品牌名
     *
     * @return 品牌名
     */
    public static String getBrand() {
        return Build.BRAND;
    }

    /**
     * 手机型号
     *
     * @return 型号
     */
    public static String getModel() {
        return Build.MODEL;
    }

    /**
     * 系统版本号
     *
     * @return 版本号
     */
    public static String getVersion() {
        return VERSION.RELEASE;
    }

    /**
     * 获取设备的唯一标识码
     * @param activity Activity
     * @return 标识码
     */
    public static String getDeviceCode(Activity activity) {
        String packageName = PackageUtils.getPackageName(activity);
        String iMei = getIMEI(activity);
        String androidId = getAndroidId(activity);
        return MD5Utils.get32bitsMD5(packageName + iMei + androidId, MD5Utils.ENCRYPTION_A);
    }

    /**
     * 获取IMEI码
     * @param context Context
     * @return IMEI码
     */
    public static String getIMEI(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (tm == null) {
            return null;
        }
        return tm.getDeviceId();
    }

    /**
     * 获取Android Id
     * @param context Context
     * @return android id
     */
    public static String getAndroidId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * 获取IMSI码
     * @param context Context
     * @return IMSI码
     */
    public static String getIMSI(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (tm == null) {
            return null;
        }
        return tm.getSubscriberId();
    }

    /**
     * 获取Wi-Fi的mac地址
     * @param context Context
     * @return mac地址
     */
    @SuppressWarnings("MissingPermission")
    public String getMacAddress(Context context) {
        return ((WifiManager) context.getSystemService(Context.WIFI_SERVICE)).getConnectionInfo().getMacAddress();
    }

    /**
     * 是否处于飞行模式
     * @param context Context
     * @return true表示处于飞行模式
     */
    public boolean isAirModeOpen(Context context) {
        return Settings.System.getInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) == 1;
    }
}