package com.jumook.syouhui.ui.login;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.jstudio.utils.JLog;
import com.jstudio.utils.TimeUtils;
import com.jumook.syouhui.R;
import com.jumook.syouhui.base.AppBaseActivity;
import com.jumook.syouhui.bean.OptionItem;
import com.jumook.syouhui.dialog.AreaSelectDialog;
import com.jumook.syouhui.dialog.DateDialog;
import com.jumook.syouhui.dialog.SingleSelectDialog;
import com.jumook.syouhui.http.LoginHttp;
import com.jumook.syouhui.network.callback.JsonStringBack;
import com.jumook.syouhui.network.callback.JsonStringDialogCallback;

import org.json.JSONObject;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 完善用户注册资料
 * Created by jumook on 2016/11/11.
 */

public class CompleteInfoActivity extends AppBaseActivity {

    public static final String TAG = "CompleteInfoActivity";

    @Bind(R.id.bar_back)
    ImageView mBarBack;
    @Bind(R.id.bar_title)
    TextView mBarTitle;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.item_gender)
    RadioGroup mGenderGroup;
    @Bind(R.id.item_nick_name)
    EditText mNickName;
    @Bind(R.id.item_birthday)
    TextView mBirthday;
    @Bind(R.id.item_birthday_layout)
    LinearLayout mBirthdayLayout;
    @Bind(R.id.item_address)
    TextView mAddress;
    @Bind(R.id.item_address_layout)
    LinearLayout mAddressLayout;
    @Bind(R.id.item_condition)
    TextView mCondition;
    @Bind(R.id.item_condition_layout)
    LinearLayout mConditionLayout;
    @Bind(R.id.item_complete)
    Button mCompleteBtn;

    //Dialog
    AreaSelectDialog areaDialog;
    DateDialog dateDialog;
    SingleSelectDialog selectDialog;
    List<OptionItem> selectList;
    boolean isLoading = false;

    int userId;
    String sessionId;
    String mOpenId;
    String mAccessToken;
    int mType;

    String userAvatar;
    String nickName;
    int genderId = 1;
    String addressStr;
    long birthdayL = 0L;
    int illnessId = 0;
    int lastTime = 0;//上一次时间

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_complete_info);
    }

    @Override
    protected void initialization() {
        mBarTitle.setText("完善资料");
        setupToolBar(mToolbar, "", MODE_NONE);
        //初始化Dialog
        initDialog();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            userId = bundle.getInt("user_id");
            sessionId = bundle.getString("session_id");
            mOpenId = bundle.getString("open_id");
            mAccessToken = bundle.getString("access_token");
            mType = bundle.getInt("login_type");
        }

    }

    @Override
    protected void bindEvent() {
        mGenderGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.item_male:
                        genderId = 1;
                        break;
                    case R.id.item_female:
                        genderId = 2;
                        break;
                }
                JLog.d(TAG, "性别:" + genderId);
            }
        });
    }

    @Override
    protected void doMoreInOnCreate() {
        getIllnessList();
    }


    @Override
    public void onBackPressed() {
        int currentTime = (int) (System.currentTimeMillis() / 1000);
        if (currentTime - lastTime < 2) {
            super.onBackPressed();
        } else {
            showToast("注册还差一步就完成了,再按一次就回到登录界面了");
            lastTime = currentTime;
        }
    }

    @OnClick({R.id.bar_back, R.id.item_birthday_layout, R.id.item_address_layout, R.id.item_condition_layout, R.id.item_complete})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bar_back:
                onBackPressed();
                break;
            case R.id.item_birthday_layout:
                dateDialog.showDialog();
                break;
            case R.id.item_address_layout:
                areaDialog.showDialog();
                break;
            case R.id.item_condition_layout:
                if (selectList != null && selectList.size() != 0) {
                    selectDialog.showDialog(selectList);
                } else {
                    showToast("正在重新获取数据...请稍后...");
                    getIllnessList();
                }
                break;
            case R.id.item_complete:
                checkInfo();
                break;
        }
    }

    private void initDialog() {
        areaDialog = new AreaSelectDialog(this) {
            @Override
            public void selectAreaCallBack(String province, String city) {
                addressStr = String.format("%s %s", province, city);
                mAddress.setText(addressStr);
            }
        };
        areaDialog.init();

        dateDialog = new DateDialog(this) {
            @Override
            public void dataSelectCallBack(int year, int month, int day) {
                String birthdayStr = String.format("%s-%s-%s", year, month, day);
                birthdayL = TimeUtils.toTimeMillis(birthdayStr, TimeUtils.mShortPattern);
                mBirthday.setText(birthdayStr);
                JLog.d(TAG, "时间：" + birthdayStr + "     时间戳：" + birthdayL);
            }
        };
        dateDialog.init();

        selectDialog = new SingleSelectDialog(this) {
            @Override
            public void singleSelectCallBack(int position, OptionItem value) {
                setOptionState(selectList, position);
                illnessId = selectList.get(position).id;
                mCondition.setText(selectList.get(position).name);
                JLog.d(TAG, selectList.get(position).toString());
            }
        };
        selectDialog.init(getString(R.string.select_illness));
    }


    //设置单选项选中状态
    private void setOptionState(List<OptionItem> list, int position) {
        for (OptionItem item : list) {
            item.isChecked = false;
        }
        list.get(position).isChecked = true;
    }

    //校对填写的信息是否为空
    private void checkInfo() {
        nickName = mNickName.getText().toString();
        if (TextUtils.isEmpty(nickName)) {
            showToast("昵称不能为空");
            return;
        }
        if (birthdayL == 0) {
            showToast("生日不能为空");
            return;
        }
        if (TextUtils.isEmpty(addressStr)) {
            showToast("地址不能为空");
            return;
        }
        if (illnessId == 0) {
            showToast("病情不能为空");
            return;
        }
        submitInformation();
    }

    //获取病情类型
    private void getIllnessList() {
        if (isLoading) {
            return;
        }
        isLoading = true;
        LoginHttp.getInstance().getIllnessType(this, new JsonStringBack() {
            @Override
            public void onJsonSuccess(JSONObject data) {
                isLoading = false;
                selectList = OptionItem.getDiagnoseList(data.optJSONArray("list"));
            }

            @Override
            public void onJsonError(String errorMsg) {
                isLoading = false;
            }
        });
    }

    //提交用户注册信息
    private void submitInformation() {
        LoginHttp.getInstance().postRegisterInfo(this, userId, sessionId, userAvatar, nickName, genderId, birthdayL, addressStr, illnessId, new JsonStringDialogCallback(this, getString(R.string.is_doing)) {
            @Override
            public void onJsonSuccess(JSONObject data) {
                boolean result = data.optBoolean("result");
                if (result) {
                    showToast("资料提交成功");
//                    GlobalVar.getInstance().mUserId = userId;
//                    GlobalVar.getInstance().mSessionId = sessionId;
//                    GlobalVar.getInstance().mIsLogin = true;
//                    GlobalVar.getInstance().basePreference
//                            .putInt(AppConstant.USER_ID, userId)
//                            .putString(AppConstant.APP_SESSION, sessionId)
//                            .apply();
                    finish();
                } else {
                    showToast("资料提交失败");
                }
            }

            @Override
            public void onJsonError(String errorMsg) {
                showToast(errorMsg);
            }
        });
    }

}
