package com.jumook.syouhui.dialog;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.jumook.syouhui.application.GlobalVar;
import com.jumook.syouhui.ui.login.LoginActivity;

/**
 * 登录判断工具
 * Created by Administrator on 2015-07-29.
 */
public class AuthLogin {

    private static AuthLogin instance = null;

    public static synchronized AuthLogin getInstance() {
        if (instance == null) {
            instance = new AuthLogin();
        }
        return instance;
    }

    private AuthLogin() {
    }

    public boolean isLogin(Context context) {
        boolean state = isLogin();
        if (!state) {
            Intent intent = new Intent(context, LoginActivity.class);
            context.startActivity(intent);
        }
        return state;
    }


    public boolean isLogin() {
        if (GlobalVar.getInstance().mIsLogin) {
            if (GlobalVar.getInstance().mUserId != 0 && !TextUtils.isEmpty(GlobalVar.getInstance().mSessionId)) {
                return true;
            }
            GlobalVar.getInstance().clearNativeData();
        }
        return false;
    }

}
