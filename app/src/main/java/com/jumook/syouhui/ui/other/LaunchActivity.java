package com.jumook.syouhui.ui.other;

import com.jumook.syouhui.base.AppBaseActivity;

/**
 * 空页面,预留界面
 * Created by jumook on 2016/10/25.
 */
public class LaunchActivity extends AppBaseActivity {

    @Override
    protected void setContentView() {
    }

    @Override
    protected void initialization() {
    }

    @Override
    protected void bindEvent() {
    }

    @Override
    protected void doMoreInOnCreate() {
        openActivityWithBundle(SplashActivity.class, null);
        finish();
    }

    @Override
    public void onBackPressed() {
    }
}
