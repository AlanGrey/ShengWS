package com.jumook.syouhui.http;

import android.content.Context;
import android.text.TextUtils;

import com.jstudio.utils.CrashHandler;
import com.jstudio.utils.JLog;
import com.jstudio.utils.NetworkUtils;
import com.jstudio.utils.PackageUtils;
import com.jstudio.utils.PreferencesUtils;
import com.jumook.syouhui.application.GlobalVar;
import com.jumook.syouhui.constants.AppConstant;
import com.jumook.syouhui.constants.NetParams;
import com.jumook.syouhui.constants.ip.BaseUrls;
import com.jumook.syouhui.constants.ip.Urls;
import com.jumook.syouhui.network.HttpUtils;
import com.lzy.okgo.callback.StringCallback;

import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Response;

/**
 * App基本请求类
 * Created by jumook on 2016/11/8.
 */

public class AppBaseHttp {

    public static final String TAG = "AppBaseHttp";

    private static AppBaseHttp instance = null;
    private long timestampNow = 0;
    private PreferencesUtils statisticsSp = GlobalVar.getInstance().infoPreference;


    // 全局自定义变量
    private static int interval = 60;//默认间隔时间超过15秒为一次启动

    public static synchronized AppBaseHttp getInstance() {
        if (instance == null) {
            instance = new AppBaseHttp();
        }
        return instance;
    }

    public AppBaseHttp() {

    }

    /**
     * 上传CrashLog
     */
    public void uploadCrashLog(Context context, String crashText) {
        String url = Urls.URL + BaseUrls.APP_CRASH_COMMIT;
        HashMap<String, String> params = new HashMap<>();
        params.put(NetParams.APP_TOKEN, GlobalVar.getInstance().mAppToken);
        params.put(NetParams.SIGN, GlobalVar.getInstance().getSign());
        params.put(NetParams.USER_ID, String.valueOf(GlobalVar.getInstance().mUserId));
        params.put(NetParams.TJ_VERSION, Urls.TJ_VERSION);
        params.put(NetParams.VERSION, String.valueOf(PackageUtils.getVersionCode(context)));
        params.put(NetParams.PHONE_TIME, String.valueOf(System.currentTimeMillis() / 1000));
        params.put(NetParams.PHONE_TYPE, GlobalVar.getInstance().getPhoneType());
        params.put(NetParams.CRASH_LOG, crashText);
        HttpUtils.basePost(url, context, params, new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                CrashHandler.deleteLogFile();//删除crash log
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                JLog.d(TAG, "Crash Upload fail Reason : " + e.toString());
            }
        });
    }

    /**
     * 登录触发保存登录时间
     */
    public void saveLoginTime(Context context) {
        long timestamp = statisticsSp.getLong(AppConstant.APP_START_TIME, 0);
        timestampNow = System.currentTimeMillis() / 1000;
        if (timestampNow - timestamp >= interval) {
            statisticsSp.putLong(AppConstant.APP_START_TIME, timestampNow).apply();
            uploadStartCount(context, 2, timestamp);
        }
    }

    /**
     * 退出触发保存退出时间
     */
    public void saveExitTime() {
        long timestamp = System.currentTimeMillis() / 1000;
        statisticsSp.putLong(AppConstant.APP_START_TIME, timestamp).apply();
    }

    /**
     * app启动次数信息上传到服务器
     */
    private void uploadStartCount(final Context context, final int statics, long timestamp) {
        String url = Urls.URL + BaseUrls.APP_STATUS_COMMIT;
        HashMap<String, String> params = new HashMap<>();
        if (TextUtils.isEmpty(GlobalVar.getInstance().mAppToken)) {
            return;
        }
        params.put(NetParams.APP_TOKEN, GlobalVar.getInstance().mAppToken);
        params.put(NetParams.SIGN, GlobalVar.getInstance().getSign());
        params.put(NetParams.USER_ID, String.valueOf(GlobalVar.getInstance().mUserId));
        params.put(NetParams.NETWORK, NetworkUtils.getStringNetworkType(context));
        params.put(NetParams.OPERATORS, NetworkUtils.getStringProvider(context));
        params.put(NetParams.STATUS, String.valueOf(statics));
        params.put(NetParams.PHONE_TIME, String.valueOf(timestamp));
        String province = statisticsSp.getString(AppConstant.APP_PROVINCE, "");
        String city = statisticsSp.getString(AppConstant.APP_CITY, "");
        String area = statisticsSp.getString(AppConstant.APP_AREA, "");
        if (!TextUtils.isEmpty(province)) {
            params.put(NetParams.SHENG, province);
        }
        if (!TextUtils.isEmpty(city)) {
            params.put(NetParams.SHI, city);
        }
        if (!TextUtils.isEmpty(area)) {
            params.put(NetParams.QU, area);
        }
        float lat = statisticsSp.getFloat(AppConstant.APP_LAT, 0f);
        float lng = statisticsSp.getFloat(AppConstant.APP_LNG, 0f);
        params.put(NetParams.LAT, String.valueOf(lat));
        params.put(NetParams.LNG, String.valueOf(lng));
        params.put(NetParams.TJ_VERSION, Urls.TJ_VERSION);
        params.put(NetParams.VERSION, String.valueOf(PackageUtils.getVersionCode(context)));
        HttpUtils.basePost(url, context, params, new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                if (statics == 2) {
                    uploadStartCount(context, 1, timestampNow);
                }
                JLog.d(TAG, "统计信息上传成功");
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                JLog.e(TAG, "Exception = " + e.toString());
            }
        });
    }

}
