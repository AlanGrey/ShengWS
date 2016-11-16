package com.jumook.syouhui.network;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.jstudio.utils.JLog;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.callback.BitmapCallback;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * 基于OkGo封装的网络请求工具
 * Created by jumook on 2016/11/4.
 */

public class HttpUtils {

    private static final String TAG = "HttpUtils";

    public static void baseGet(String url, Object tag, AbsCallback callback) {
        OkGo.get(url)
                .tag(tag)
                .execute(callback);
    }

    public static Bitmap baseGetImage(String url, Object tag) {
        final Bitmap[] imageBitmap = {null};
        OkGo.get(url)
                .tag(tag)
                .execute(new BitmapCallback() {
                    @Override
                    public void onSuccess(Bitmap bitmap, Call call, Response response) {
                        imageBitmap[0] = bitmap;
                    }
                });
        return imageBitmap[0];
    }

    public static void basePost(String url, Object tag, HashMap<String, String> paramsMap, AbsCallback callback) {
        JLog.d(TAG, parseApi(url, paramsMap));
        OkGo.post(url)
                .tag(tag)
                .params(paramsMap, true)
                .execute(callback);
    }


    public static void basePost(String url, Object tag, HashMap<String, String> paramsMap, String cacheKey, CacheMode cacheMode, AbsCallback callback) {
        JLog.d(TAG, parseApi(url, paramsMap));
        OkGo.post(url)
                .tag(tag)
                .params(paramsMap, true)
                .cacheKey(cacheKey)
                .cacheMode(cacheMode)
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        return chain.proceed(chain.request());
                    }
                })
                .execute(callback);
    }

    public static void cancelHttp(Context context) {
        OkGo.getInstance().cancelTag(context);
    }

    /**
     * 打印完整的请求地址
     *
     * @param url Api地址
     * @param map 请求参数
     * @return 完整的Api路径
     */
    private static String parseApi(String url, HashMap<String, String> map) {
        StringBuffer params = new StringBuffer(url);
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (TextUtils.isEmpty(params)) {
                params.append("?").append(entry.getKey()).append("=").append(entry.getValue());
            } else {
                params.append("&").append(entry.getKey()).append("=").append(entry.getValue());
            }
        }
        return params.toString();
    }

}
