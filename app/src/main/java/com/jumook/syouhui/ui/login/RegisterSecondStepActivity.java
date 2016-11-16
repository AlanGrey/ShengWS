package com.jumook.syouhui.ui.login;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jstudio.utils.MD5Utils;
import com.jstudio.widget.TimerTextView;
import com.jumook.syouhui.R;
import com.jumook.syouhui.base.AppBaseActivity;
import com.jumook.syouhui.constants.NetParams;
import com.jumook.syouhui.http.LoginHttp;
import com.jumook.syouhui.network.callback.JsonStringBack;
import com.jumook.syouhui.tools.ActivityTaskManager;
import com.jumook.syouhui.widget.BrightTextView;

import org.json.JSONObject;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 用户注册步骤二
 * Created by jumook on 2016/10/31.
 */

public class RegisterSecondStepActivity extends AppBaseActivity {

    public static final String TAG = "RegisterSecondStepActivity";

    @Bind(R.id.bar_back)
    ImageView mBarBack;
    @Bind(R.id.bar_title)
    TextView mBarTitle;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.phone_code)
    EditText mPhoneCode;
    @Bind(R.id.get_code)
    TimerTextView mGetCode;
    @Bind(R.id.user_password)
    EditText mUserPassword;
    @Bind(R.id.show_password)
    CheckBox mShowPassword;
    @Bind(R.id.invite_code)
    EditText mInviteCode;
    @Bind(R.id.complete)
    Button mCompleteBtn;
    @Bind(R.id.show_phone_number)
    BrightTextView mShowPhoneNumber;


    Bundle bundle;
    String phoneNumber;
    int type;
    String code;
    String password;
    String sessionId;
    String inviteCode;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_register_second);
        ActivityTaskManager.getInstance().putActivity(TAG, this);
    }

    @Override
    protected void initialization() {
        mBarTitle.setText(getString(R.string.login_register));
        setupToolBar(mToolbar, "", MODE_NONE);
        bundle = getIntent().getExtras();
        if (bundle == null) {
            showToast(getString(R.string.bundle_error));
        } else {
            phoneNumber = bundle.getString("phone");
            type = bundle.getInt("login_type");
        }

        String showPhone = String.format("请手机号:%s注意查收短信验证码", phoneNumber);
        mShowPhoneNumber.setBrightTextsColor(showPhone, phoneNumber, getResources().getColor(R.color.theme_color));
        //初始化TimerTextView颜色
        mGetCode.setColor(getResources().getColor(R.color.theme_color), getResources().getColor(R.color.hint_tip_grey));
    }

    @Override
    protected void bindEvent() {

        mShowPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //文本正常显示
                    mUserPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    //文本以密码形式显示
                    mUserPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                //下面两行代码实现: 输入框光标一直在输入文本后面
                Editable able = mUserPassword.getText();
                Selection.setSelection(able, able.length());
            }
        });
    }

    @Override
    public void onBackPressed() {
        ActivityTaskManager.getInstance().removeActivity(TAG);
    }

    @OnClick({R.id.bar_back, R.id.get_code, R.id.complete})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bar_back:
                onBackPressed();
                break;
            case R.id.get_code:
                getPhoneCode();
                break;
            case R.id.complete:
                registerUser();
                break;
        }
    }

    //获取手机验证码
    private void getPhoneCode() {
        mGetCode.startTimer();
        LoginHttp.getInstance().getPhoneCode(this, phoneNumber, new JsonStringBack() {
            @Override
            public void onJsonSuccess(JSONObject data) {
                showToast("验证码已发送,请查收");
                sessionId = data.optString("session_id");
            }

            @Override
            public void onJsonError(String errorMsg) {
                showToast(errorMsg);
                mGetCode.reStartTimer();
            }
        });
    }

    //注册用户,正式加入圣卫士
    private void registerUser() {
        password = mUserPassword.getText().toString();
        code = mPhoneCode.getText().toString();
        inviteCode = mInviteCode.getText().toString();
        if (TextUtils.isEmpty(code)) {
            showToast("手机验证码不能为空");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            showToast("登录密码不能为空");
            return;
        }
        if (password.length() < 6) {
            showToast("请输入6位以上登录密码");
            return;
        }
        password = MD5Utils.get32bitsMD5(password, MD5Utils.ENCRYPTION_A);
        password = password + "shenyouhui";
        password = MD5Utils.get32bitsMD5(password, MD5Utils.ENCRYPTION_A);
        password = password.substring(0, password.length() - 2);
        password = truncateHeadString(password, 2);
        LoginHttp.getInstance().registerUser(this, phoneNumber, password, code, sessionId, inviteCode, new JsonStringBack() {
            @Override
            public void onJsonSuccess(JSONObject data) {
                boolean isRegister = data.optBoolean("registed");
                bundle.putInt("user_id", data.optInt(NetParams.USER_ID));
                bundle.putString("session_id", data.optString(NetParams.SESS_ID));
                ActivityTaskManager.getInstance().removeActivity(RegisterFirstStepActivity.TAG);
                if (isRegister) {
                    //已注册用户信息
                    openActivityWithBundle(LoginActivity.class, null);
                } else {
                    //完善用户信息
                    openActivityWithBundle(CompleteInfoActivity.class, bundle);
                }
                showToast("手机注册成功");
                ActivityTaskManager.getInstance().removeActivity(RegisterFirstStepActivity.TAG);
                ActivityTaskManager.getInstance().removeActivity(TAG);
            }

            @Override
            public void onJsonError(String errorMsg) {
                showToast(errorMsg);
            }
        });
    }

    public String truncateHeadString(String origin, int count) {
        if (origin == null || origin.length() < count) {
            return null;
        }
        char[] arr = origin.toCharArray();
        char[] ret = new char[arr.length - count];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = arr[i + count];
        }
        return String.copyValueOf(ret);
    }

}