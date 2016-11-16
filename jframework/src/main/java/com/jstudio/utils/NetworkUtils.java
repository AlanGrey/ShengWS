package com.jstudio.utils;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.TelephonyManager;

/**
 * 网络工具类
 * Created by Jason
 */
public class NetworkUtils {

    public static enum Provider {
        NON, CMCC, CUCC, CTCC
    }

    public static enum NetType {
        NON, WIFI, GPRS, WCDMA, LTE
    }

    /**
     * 检查网络是否可用
     *
     * @param context Context
     * @return true表示可用
     */
    public static boolean isNetworkAvailable(Context context) {
        try {
            return isWifiNetworkEnable(context) || isMobileNetworkEnable(context);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 数据网络是否可用
     *
     * @param context Context
     * @return true表示可用
     * @throws Exception
     */
    public static boolean isMobileNetworkEnable(Context context) throws Exception {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean isMobileDataEnable;
        isMobileDataEnable = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
        return isMobileDataEnable;
    }

    /**
     * wifi网络是否可用
     *
     * @param context Context
     * @return true表示可用
     * @throws Exception
     */
    public static boolean isWifiNetworkEnable(Context context) throws Exception {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
    }

    /**
     * GPS是否打开
     *
     * @param context Context
     * @return true表示可用
     */
    public static boolean isGpsEnable(Context context) {
        LocationManager locationManager = ((LocationManager) context.getSystemService(Context.LOCATION_SERVICE));
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * 是否支持BLE
     *
     * @param context Context
     * @return 支持蓝牙4.0则返回true，否则false
     */
    public static boolean isBleSupported(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            BluetoothAdapter adapter = ((BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
            return adapter != null;
        } else {
            return false;
        }
    }

    /**
     * 蓝牙4.0是否打开，需要添加权限android.permission.BLUETOOTH
     *
     * @param context Context
     * @return true表示蓝牙已打开，否则返回false
     */
    @SuppressLint("NewApi")
    @SuppressWarnings("MissingPermission")
    public static boolean isBleEnable(Context context) {
        return isBleSupported(context) && ((BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter().isEnabled();
    }

    /**
     * 获取运营商
     *
     * @param context Context
     * @return 运营商的名字，没有则返回Provider.NON
     */
    public static Provider getProvider(Context context) {
        String IMSI;
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        IMSI = telephonyManager.getSubscriberId();
        // The first 3 IMSI nums 460 stand for nation code China
        // 00,02 stands for China Mobile, 01 stands for China Unicom, 03 stands for China Telecom
        if (IMSI == null)
            return Provider.NON;
        if (IMSI.startsWith("46000") || IMSI.startsWith("46002")) {
            return Provider.CMCC;
        } else if (IMSI.startsWith("46001")) {
            return Provider.CUCC;
        } else if (IMSI.startsWith("46003")) {
            return Provider.CTCC;
        } else {
            return Provider.NON;
        }
    }

    /**
     * Get the name of the provider
     *
     * @param context Context
     * @return The name of the provider, will return null if no Sim card detected
     */
    public static String getStringProvider(Context context) {
        String ProvidersName = "";
        String IMSI;
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        IMSI = telephonyManager.getSubscriberId();
        // The first 3 IMSI nums 460 stand for nation code China
        // 00,02 stands for China Mobile, 01 stands for China Unicom, 03 stands for China Telecom
        if (IMSI == null)
            return ProvidersName;
        if (IMSI.startsWith("46000") || IMSI.startsWith("46002")) {
            ProvidersName = "中国移动";
        } else if (IMSI.startsWith("46001")) {
            ProvidersName = "中国联通";
        } else if (IMSI.startsWith("46003")) {
            ProvidersName = "中国电信";
        }
        return ProvidersName;
    }

    /**
     * 返回数据网络类型，没有则返回NetType.NON
     *
     * @param context Context
     * @return 数据网络类型
     */
    public static NetType getNetworkType(Context context) {
        NetType type = null;
        ConnectivityManager connectMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectMgr.getActiveNetworkInfo();
        if (info == null) {
            type = NetType.NON;
        } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {
            type = NetType.WIFI;
        } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
            int subType = info.getSubtype();
            if (subType == TelephonyManager.NETWORK_TYPE_CDMA || subType == TelephonyManager.NETWORK_TYPE_GPRS
                    || subType == TelephonyManager.NETWORK_TYPE_EDGE) {
                type = NetType.GPRS;
            } else if (subType == TelephonyManager.NETWORK_TYPE_UMTS || subType == TelephonyManager.NETWORK_TYPE_HSDPA
                    || subType == TelephonyManager.NETWORK_TYPE_EVDO_A || subType == TelephonyManager.NETWORK_TYPE_EVDO_0
                    || subType == TelephonyManager.NETWORK_TYPE_EVDO_B) {
                type = NetType.WCDMA;
            } else if (subType == TelephonyManager.NETWORK_TYPE_LTE) {
                type = NetType.LTE;
            }
        }
        return type;
    }

    /**
     * Get the network type, will return wifi, 2G, 3G, LTE or null if the system can't judge the type
     *
     * @param context Context
     * @return The network type now you are using
     */
    public static String getStringNetworkType(Context context) {
        String type = "";
        ConnectivityManager connectMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectMgr.getActiveNetworkInfo();
        if (info == null) {
            type = "null";
        } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {
            type = "wifi";
        } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
            int subType = info.getSubtype();
            if (subType == TelephonyManager.NETWORK_TYPE_CDMA || subType == TelephonyManager.NETWORK_TYPE_GPRS
                    || subType == TelephonyManager.NETWORK_TYPE_EDGE) {
                type = "2G";
            } else if (subType == TelephonyManager.NETWORK_TYPE_UMTS || subType == TelephonyManager.NETWORK_TYPE_HSDPA
                    || subType == TelephonyManager.NETWORK_TYPE_EVDO_A || subType == TelephonyManager.NETWORK_TYPE_EVDO_0
                    || subType == TelephonyManager.NETWORK_TYPE_EVDO_B) {
                type = "3G";
            } else if (subType == TelephonyManager.NETWORK_TYPE_LTE) {
                type = "LTE";
            }
        }
        return type;
    }
}
