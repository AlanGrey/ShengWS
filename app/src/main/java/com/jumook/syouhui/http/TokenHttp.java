package com.jumook.syouhui.http;

import android.content.Context;
import android.os.Build;

import com.jstudio.network.base.JsonResponse;
import com.jstudio.network.callback.JsonCallback;
import com.jstudio.utils.JLog;
import com.jumook.syouhui.application.GlobalVar;
import com.jumook.syouhui.bean.Token;
import com.jumook.syouhui.constants.AppConstant;
import com.jumook.syouhui.constants.NetParams;
import com.jumook.syouhui.constants.ip.LoginUrls;
import com.jumook.syouhui.constants.ip.Urls;
import com.jumook.syouhui.network.HttpUtils;

import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Response;

import static com.lzy.okgo.interceptor.LoggerInterceptor.TAG;

/**
 * 获取AppToken
 * Created by jumook on 2016/11/10.
 */

public class TokenHttp {

    public static void getAppToken(Context context, JsonCallback<JsonResponse<Token>> callback) {
        String url = Urls.URL + LoginUrls.GET_APP_TOKEN;
        //请求参数
        HashMap<String, String> params = new HashMap<>();
        params.put(NetParams.SIGN, GlobalVar.getInstance().getSign());
        params.put(NetParams.PHONE_TYPE, GlobalVar.getInstance().getPhoneType());
        params.put(NetParams.PLATFORM, GlobalVar.getInstance().Channel);
        params.put(NetParams.DPI, String.format("%s*%s", GlobalVar.getInstance().mScreenWidth, GlobalVar.getInstance().mScreenHeight));
        params.put(NetParams.OS, Build.VERSION.RELEASE);
        params.put(NetParams.BRAND, Build.BRAND);
        if (callback == null) {
            HttpUtils.basePost(url, context, params, new JsonCallback<JsonResponse<Token>>() {
                @Override
                public void onSuccess(JsonResponse<Token> jsonResponse, Call call, Response response) {
                    GlobalVar.getInstance().basePreference.putString(AppConstant.APP_TOKEN, jsonResponse.data.app_token).apply();
                    GlobalVar.getInstance().mAppToken = jsonResponse.data.app_token;
                }

                @Override
                public void onError(Call call, Response response, Exception e) {
                    JLog.d(TAG, e.toString());
                }
            });
        } else {
            HttpUtils.basePost(url, context, params, callback);
        }
    }
}
