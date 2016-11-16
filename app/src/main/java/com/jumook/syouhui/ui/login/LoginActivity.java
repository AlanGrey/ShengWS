package com.jumook.syouhui.ui.login;

import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.jstudio.utils.JLog;
import com.jumook.syouhui.R;
import com.jumook.syouhui.base.AppBaseActivity;
import com.jumook.syouhui.http.LoginHttp;
import com.jumook.syouhui.network.callback.JsonStringDialogCallback;
import com.jumook.syouhui.widget.TintEditText;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONObject;

import butterknife.Bind;
import butterknife.OnClick;
import tencen.qq.QQUtils;
import tencen.qq.callback.OnQQLoginListener;
import tencen.qq.model.QQAccessToken;

import static com.jumook.syouhui.R.color.theme_color;
import static com.jumook.syouhui.R.color.under_grey_line;
import static com.jumook.syouhui.R.id.toolbar;

/**
 * 登录界面
 * Created by jumook on 2016/11/9.
 */

public class LoginActivity extends AppBaseActivity implements TintEditText.OnFocusChangeListener {

    public static final String TAG = "LoginActivity";

    public static final int LOGIN_WE_CHAT = 2;
    public static final int LOGIN_QQ = 3;

    @Bind(R.id.bar_back)
    ImageView mBarBlack;
    @Bind(R.id.bar_title)
    TextView mBarTitle;
    @Bind(toolbar)
    Toolbar mToolbar;
    @Bind(R.id.login_mobile)
    TintEditText mLoginMobile;
    @Bind(R.id.login_mobile_line)
    View mMobileLine;
    @Bind(R.id.login_password)
    TintEditText mLoginPassword;
    @Bind(R.id.login_password_line)
    View mPasswordLine;
    @Bind(R.id.login_btn)
    Button mLoginBtn;
    @Bind(R.id.login_forget)
    TextView mLoginForget;
    @Bind(R.id.login_register)
    TextView mLoginRegister;
    @Bind(R.id.login_we_chat)
    TextView loginWeChat;
    @Bind(R.id.login_qq)
    TextView loginQq;


    String openId;
    String accessToken;
    String mobile;
    String password;
    int loginType;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_login);
    }

    @Override
    protected void initialization() {
        mBarTitle.setText(getString(R.string.login_sws));
        setupToolBar(mToolbar, "", MODE_NONE);
    }

    @Override
    protected void bindEvent() {
        mLoginMobile.setOnFocusChangeListener(this);
        mLoginPassword.setOnFocusChangeListener(this);

        mLoginMobile.setOnTextChangeListener(new TintEditText.OnTextChangeListener() {
            @Override
            public void onTextChanged(CharSequence s) {
                mobile = s.toString();
                checkLoginEdit();
            }
        });

        mLoginPassword.setOnTextChangeListener(new TintEditText.OnTextChangeListener() {
            @Override
            public void onTextChanged(CharSequence s) {
                password = s.toString();
                checkLoginEdit();
            }
        });
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.login_mobile:
                mMobileLine.setBackgroundResource(theme_color);
                mPasswordLine.setBackgroundResource(under_grey_line);
                break;
            case R.id.login_password:
                mMobileLine.setBackgroundResource(under_grey_line);
                mPasswordLine.setBackgroundResource(theme_color);
                break;
        }
    }

    @OnClick({R.id.login_btn, R.id.login_forget, R.id.login_register, R.id.login_we_chat, R.id.login_qq, R.id.bar_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bar_back:
                onBackPressed();
                break;
            case R.id.login_btn:

                break;
            case R.id.login_forget:
                openActivityWithBundle(ForgetFirstStepActivity.class, null);
                break;
            case R.id.login_register:
                openActivityWithBundle(RegisterFirstStepActivity.class, null);
                break;
            case R.id.login_we_chat:
                break;
            case R.id.login_qq:
                loginQq();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Tencent.onActivityResultData(requestCode, resultCode, data, QQUtils.getInstance().iUiListener);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void checkLoginEdit() {
        //判断用户帐号与密码是否都存在
        mLoginBtn.setEnabled(!TextUtils.isEmpty(mobile) && !TextUtils.isEmpty(password));
    }

    private void loginQq() {
        if (QQUtils.getInstance().isQQClientAvailable(this)) {
            if (!QQUtils.mTencent.isSessionValid()) {
                showProgressDialog("正在授权...请稍后...", true);
                QQUtils.getInstance().qqLogin(LoginActivity.this, "all", new OnQQLoginListener() {
                    @Override
                    public void onComplete(QQAccessToken accessToken) {
                        dismissProgressDialog();
                        loginType = LOGIN_QQ;
                        openId = accessToken.getOpenId();
                        LoginActivity.this.accessToken = accessToken.getAccessToken();
                        thirdPartyLogin();
                    }

                    @Override
                    public void onError(UiError uiError) {
                        JLog.d(TAG, "code:" + uiError.errorCode + "message:" + uiError.errorMessage);
                        dismissProgressDialog();
                        showToast("QQ授权失败,可以使用其它方式登录。");
                    }

                    @Override
                    public void onCancel() {
                        dismissProgressDialog();
                        showToast("取消登录");
                    }
                });
            } else {
                loginType = LOGIN_QQ;
                openId = QQUtils.mTencent.getOpenId();
                accessToken = QQUtils.mTencent.getAccessToken();
                thirdPartyLogin();
            }
        } else {
            showToast("请检查手机是否安装了QQ");
        }
    }

    private void thirdPartyLogin() {
        LoginHttp.getInstance().thirdPartyLogin(this, loginType, openId, new JsonStringDialogCallback(this, getString(R.string.is_login)) {
            @Override
            public void onJsonSuccess(JSONObject data) {
                String sessionId = data.optString("sess_id");
                boolean isRegister = data.optBoolean("registed");
                boolean isBinding = data.optBoolean("binding");
                int userId = 0;
                JSONObject info = data.optJSONObject("user_info");
                if (info != null) {
                    userId = info.optInt("user_id");
                }
//                    MyApp.getGlobal().mSessionId = sessionId;
//                    JSONObject info = data.optJSONObject(UserInfo.USER_INFO);
//                    if (info != null) {
//                        UserInfo u = UserInfo.getUserInfo(data.optJSONObject(UserInfo.USER_INFO));
//                        GlobalVar.getInstance().mUserId = u.id;
//                        MyApp.getGlobal().mUserInfo = u;
//                        MyApp.getGlobal().mAppSp.putUserId(u.id).putSessId(sessionId);
//                    }
//                    if (!isBinding) {
//                        Bundle bundle = new Bundle();
//                        bundle.putString("open_id", mOpenId);
//                        bundle.putInt("login_type", mLoginType);
//                        bundle.putString("access_token", mAccessToken);
//                        openActivityWithBundle(BindingFirstStepActivity.class, bundle);
//                    } else if (isRegister) {
//                        MyApp.getGlobal().isLoginState = true;
//                        MyApp.getGlobal().mAppSp.putLoginState(true).apply();
//                        EventBus.getDefault().post(new BaseEvent(TAG, BaseEvent.REFRESH, null));
//                        ActivityTaskManager.getInstance().closeAllActivity();
//                    } else {
//                        MyApp.getGlobal().isLoginState = false;
//                        MyApp.getGlobal().mAppSp.putLoginState(false).apply();
//                        Bundle bundle = new Bundle();
//                        bundle.putString("open_id", mOpenId);
//                        bundle.putInt("login_type", mLoginType);
//                        bundle.putString("access_token", mAccessToken);
//                        openActivityWithBundle(CompleteInfoActivity.class, bundle);
//                    }
            }

            @Override
            public void onJsonError(String errorMsg) {
                showToast(errorMsg);
            }
        });
    }


}
