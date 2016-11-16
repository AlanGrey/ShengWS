package com.jumook.syouhui.ui.other;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import com.jstudio.network.base.JsonResponse;
import com.jstudio.network.callback.JsonCallback;
import com.jstudio.utils.PackageUtils;
import com.jstudio.utils.PreferencesUtils;
import com.jumook.syouhui.R;
import com.jumook.syouhui.application.GlobalVar;
import com.jumook.syouhui.base.AppBaseActivity;
import com.jumook.syouhui.bean.Token;
import com.jumook.syouhui.constants.AppConstant;
import com.jumook.syouhui.http.TokenHttp;
import com.jumook.syouhui.network.HttpUtils;
import com.jumook.syouhui.ui.login.LoginActivity;

import okhttp3.Call;
import okhttp3.Response;


/**
 * 加载界面
 * Created by jumook on 2016/10/25.
 */
public class SplashActivity extends AppBaseActivity {

    public static final String TAG = "SplashActivity";

    @Override
    protected boolean onRestoreState(Bundle paramSavedState) {
        return false;
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void initialization() {
        if (TextUtils.isEmpty(GlobalVar.getInstance().mAppToken)) {
            TokenHttp.getAppToken(this, new JsonCallback<JsonResponse<Token>>() {
                @Override
                public void onSuccess(JsonResponse<Token> jsonResponse, Call call, Response response) {
                    GlobalVar.getInstance().basePreference.putString(AppConstant.APP_TOKEN, jsonResponse.data.app_token);
                    GlobalVar.getInstance().mAppToken = jsonResponse.data.app_token;
                    checkVersion();
                }

                @Override
                public void onError(Call call, Response response, Exception e) {
                    checkVersion();
                }
            });
        } else {
            checkVersion();
        }
    }

    @Override
    protected void bindEvent() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        HttpUtils.cancelHttp(this);
    }

    private void openActivity(final Intent intent) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(intent);
                finish();
            }
        }, 3 * 1000);
    }

    private void checkVersion() {
        PreferencesUtils preferences = GlobalVar.getInstance().infoPreference;
        int oldVersion = preferences.getInt(AppConstant.APP_VERSION, 0);
        int newVersion = PackageUtils.getVersionCode(this);
        if (oldVersion == 0 || newVersion > oldVersion) {
            preferences.putInt(AppConstant.APP_VERSION, newVersion);
            openActivityWithBundle(LoginActivity.class, null);
        } else if (oldVersion == newVersion) {
            openActivity(new Intent(SplashActivity.this, LoginActivity.class));
        }
    }

}
