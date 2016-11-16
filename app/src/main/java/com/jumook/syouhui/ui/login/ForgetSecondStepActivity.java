package com.jumook.syouhui.ui.login;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.jstudio.utils.MD5Utils;
import com.jstudio.widget.TimerTextView;
import com.jumook.syouhui.R;
import com.jumook.syouhui.base.AppBaseActivity;
import com.jumook.syouhui.http.LoginHttp;
import com.jumook.syouhui.network.callback.JsonStringBack;
import com.jumook.syouhui.network.callback.JsonStringDialogCallback;
import com.jumook.syouhui.tools.ActivityTaskManager;
import com.jumook.syouhui.widget.BrightTextView;

import org.json.JSONObject;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 忘记密码 步骤：2
 * Created by jumook on 2016/11/15.
 */

public class ForgetSecondStepActivity extends AppBaseActivity {

    public static final String TAG = "ForgetSecondStepActivity";

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
    @Bind(R.id.show_phone_number)
    BrightTextView showPhoneNumber;

    String phoneNumber;
    String sessionId;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_forget_second);
        ActivityTaskManager.getInstance().putActivity(TAG, this);
    }

    @Override
    protected void initialization() {
        mBarTitle.setText(getString(R.string.login_register));
        setupToolBar(mToolbar, "", MODE_NONE);

        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            showToast(getString(R.string.bundle_error));
        } else {
            phoneNumber = bundle.getString("phone");
        }

        String showPhone = String.format("请手机号:%s注意查收短信验证码", phoneNumber);
        showPhoneNumber.setBrightTextsColor(showPhone, phoneNumber, getResources().getColor(R.color.theme_color));
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

    @OnClick({R.id.bar_back, R.id.get_code, R.id.show_password, R.id.complete})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bar_back:
                ActivityTaskManager.getInstance().removeActivity(TAG);
                break;
            case R.id.get_code:
                getPhoneCode();
                break;
            case R.id.show_password:
                changePassword();
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

    //修改密码
    private void changePassword() {
        String password = mUserPassword.getText().toString();
        String code = mPhoneCode.getText().toString();
        if (TextUtils.isEmpty(code)) {
            showToast("手机验证码不能为空");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            showToast("密码不能为空");
            return;
        }
        if (password.length() < 6) {
            showToast("请输入6位以上密码");
            return;
        }
        password = MD5Utils.get32bitsMD5(password, MD5Utils.ENCRYPTION_A);
        password = password + "shenyouhui";
        password = MD5Utils.get32bitsMD5(password, MD5Utils.ENCRYPTION_A);
        password = password.substring(0, password.length() - 2);
        password = truncateHeadString(password, 2);
        LoginHttp.getInstance().changePassword(this, phoneNumber, password, code, sessionId, new JsonStringDialogCallback(this,getString(R.string.is_doing)) {
            @Override
            public void onJsonSuccess(JSONObject data) {
                showToast("密码修改成功！");
                ActivityTaskManager.getInstance().removeActivity(ForgetFirstStepActivity.TAG);
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
