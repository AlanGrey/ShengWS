package com.jumook.syouhui.ui.login;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jstudio.utils.RegularExpUtils;
import com.jstudio.widget.dialog.DialogCreator;
import com.jumook.syouhui.R;
import com.jumook.syouhui.base.AppBaseActivity;
import com.jumook.syouhui.dialog.SystemDialog;
import com.jumook.syouhui.http.LoginHttp;
import com.jumook.syouhui.network.callback.JsonStringDialogCallback;
import com.jumook.syouhui.tools.ActivityTaskManager;
import com.jumook.syouhui.widget.BrightTextView;

import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 注册界面-校验手机
 * Created by jumook on 2016/10/31.
 */
public class RegisterFirstStepActivity extends AppBaseActivity implements View.OnClickListener {

    public static final String TAG = "RegisterFirstStepActivity";

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
    @Bind(R.id.item_agreement)
    BrightTextView mAgreement;

    Dialog mAgreeDialog;
    WebView mWebContext;
    Button mAgreeBtn;
    Button mDisagreeBtn;

    String phoneNumber = "";
    boolean isAgree = true;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_register_first);
        ActivityTaskManager.getInstance().putActivity(TAG, this);
    }

    @Override
    protected void findViews() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_text_selector, null);
        mWebContext = ButterKnife.findById(view, R.id.web_content);
        mAgreeBtn = ButterKnife.findById(view, R.id.agree);
        mDisagreeBtn = ButterKnife.findById(view, R.id.disagree);
        mAgreeDialog = DialogCreator.createNormalDialog(this, view, DialogCreator.Position.CENTER);
    }

    @Override
    protected void initialization() {
        mBarTitle.setText(getString(R.string.login_register));
        setupToolBar(mToolbar, "", MODE_NONE);

        //设置协议状态
        isAgree = true;
        mAgreement.setBrightTextsColor(getString(R.string.login_agreement), "注册协议", getResources().getColor(R.color.tf_green_4b7));
        mAgreement.setLeftIconColor(R.color.theme_color);
        mWebContext.getSettings().setJavaScriptEnabled(true);
        mWebContext.getSettings().setPluginState(WebSettings.PluginState.ON);
        mWebContext.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebContext.getSettings().setAllowFileAccess(true);
        mWebContext.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                mWebContext.setVisibility(View.VISIBLE);
                super.onPageFinished(view, url);
            }
        });
        mWebContext.loadUrl("http://admin.shengws.com/page/agree");
    }

    @Override
    protected void bindEvent() {
        mAgreeBtn.setOnClickListener(this);
        mDisagreeBtn.setOnClickListener(this);

        mPhone.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                phoneNumber = charSequence.toString();
                setBtnState();
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

    @OnClick({R.id.bar_back, R.id.item_next, R.id.item_agreement})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bar_back:
                onBackPressed();
                break;
            case R.id.item_next:
                checkPhoneNumber();
                break;
            case R.id.item_agreement:
                mAgreeDialog.show();
                break;
            case R.id.agree:
                mAgreeDialog.dismiss();
                isAgree = true;
                mAgreement.setLeftIconColor(R.color.theme_color);
                setBtnState();
                break;
            case R.id.disagree:
                mAgreeDialog.dismiss();
                isAgree = false;
                mAgreement.setLeftIconColor(R.color.button_null);
                setBtnState();
                break;
        }
    }

    private void setBtnState() {
        if (mPhone.length() >= 11 && isAgree) {
            mNextBtn.setEnabled(true);
        } else {
            mNextBtn.setEnabled(false);
        }
    }


    private void checkPhoneNumber() {
        if (!isAgree) {
            showToast("请阅读并同意注册协议");
            return;
        }
        if (!RegularExpUtils.isMobile(phoneNumber)) {
            showToast("请输入正确手机号码");
            return;
        }

        LoginHttp.getInstance().checkPhoneNumber(this, phoneNumber, new JsonStringDialogCallback(this, getString(R.string.is_doing)) {
            @Override
            public void onJsonSuccess(JSONObject data) {
                if (data.optBoolean("registed")) {
                    showDialog();
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString("phone", phoneNumber);
                    bundle.putInt("login_type", 1);
                    openActivityWithBundle(RegisterSecondStepActivity.class, bundle);
                }
            }

            @Override
            public void onJsonError(String errorMsg) {
                showToast(errorMsg);
            }
        });
    }

    private void showDialog() {
        SystemDialog.getInstance().showSystemDialog(this, "此号码已在平台注册,抱歉!",
                "立即登录", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }, "忘记密码", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
    }

}
