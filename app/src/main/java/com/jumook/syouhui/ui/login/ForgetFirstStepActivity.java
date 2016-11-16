package com.jumook.syouhui.ui.login;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jstudio.utils.RegularExpUtils;
import com.jumook.syouhui.R;
import com.jumook.syouhui.base.AppBaseActivity;
import com.jumook.syouhui.http.LoginHttp;
import com.jumook.syouhui.network.callback.JsonStringDialogCallback;
import com.jumook.syouhui.tools.ActivityTaskManager;

import org.json.JSONObject;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 忘记密码 步骤：1
 * Created by jumook on 2016/11/15.
 */

public class ForgetFirstStepActivity extends AppBaseActivity {

    public static final String TAG = "ForgetFirstStepActivity";

    @Bind(R.id.bar_back)
    ImageView mBarBack;
    @Bind(R.id.bar_title)
    TextView mBarTitle;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.item_phone)
    EditText mPhone;
    @Bind(R.id.item_next)
    Button mNextBtn;

    String phoneNumber;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_forget_first);
        ActivityTaskManager.getInstance().putActivity(TAG, this);
    }

    @Override
    protected void initialization() {
        mBarTitle.setText(getString(R.string.login_forget));
        setupToolBar(mToolbar, "", MODE_NONE);
    }

    @Override
    protected void bindEvent() {
        mPhone.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                phoneNumber = charSequence.toString();
                if (phoneNumber.length() >= 11) {
                    mNextBtn.setEnabled(true);
                } else {
                    mNextBtn.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    @Override
    public void onBackPressed() {
        ActivityTaskManager.getInstance().removeActivity(TAG);
    }

    @OnClick({R.id.bar_back, R.id.item_next})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bar_back:
                onBackPressed();
                break;
            case R.id.item_next:
                checkPhoneNumber();
                break;
        }
    }

    private void checkPhoneNumber() {
        if (!RegularExpUtils.isMobile(phoneNumber)) {
            showToast("请输入正确手机号码");
            return;
        }
        LoginHttp.getInstance().checkPhoneNumber(this, phoneNumber, new JsonStringDialogCallback(this, getString(R.string.is_doing)) {
            @Override
            public void onJsonSuccess(JSONObject data) {
                if (data.optBoolean("registed")) {
                    Bundle bundle = new Bundle();
                    bundle.putString("phone", phoneNumber);
                    openActivityWithBundle(ForgetSecondStepActivity.class, bundle);
                } else {
                    showToast("该手机号码未注册为圣卫士用户");
                }
            }

            @Override
            public void onJsonError(String errorMsg) {
                showToast(errorMsg);
            }
        });
    }

}
