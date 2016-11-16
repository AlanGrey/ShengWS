package com.jstudio.base;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.jstudio.R;
import com.jstudio.ui.SystemBarTintManager;
import com.jstudio.ui.WindowHelper;
import com.jstudio.utils.ExitAppUtils;
import com.jstudio.utils.JLog;
import com.jstudio.widget.dialog.DialogCreator;

import butterknife.ButterKnife;

/**
 * 框架Activity基类
 * Created by Jason
 */
@SuppressWarnings("unused")
public abstract class BaseAppCompatActivity extends AppCompatActivity {

    public static String TAG = null;

    protected static final short MODE_NONE = 0;
    protected static final short MODE_UP = 1;
    protected static final short MODE_MENU = 2;

    protected Dialog mProgressDialog;

    protected android.support.v4.app.FragmentManager mSupportFragmentManager;
    protected android.app.FragmentManager mFragmentManager;

    /**
     * 将onCreate分割成多个方法，用于处理savedInstanceState，负责findViewById，初始化数据，绑定View事件等等
     *
     * @param savedInstanceState 用于做现场恢复的参数
     */
    @Override
    final protected void onCreate(Bundle savedInstanceState) {
        TAG = this.getClass().getSimpleName();
        ExitAppUtils.getInstance().addActivity(this);
        if (!onRestoreState(savedInstanceState)) {
            if (savedInstanceState != null) {
                savedInstanceState.clear();
                savedInstanceState = null;
                restartApp();
            }
        }
        setActivityTheme();
        super.onCreate(savedInstanceState);
        mSupportFragmentManager = getSupportFragmentManager();
        mFragmentManager = getFragmentManager();
        setContentView();
        ButterKnife.bind(this);
        findViews();
        initialization();
        bindEvent();
        doMoreInOnCreate();
    }

    /**
     * 当Activity处理现场恢复时处理逻辑的主体，可以根据情况处理savedInstanceState，
     * 如果不处理将重启整个应用来防止由于长时间在后台被杀后恢复带来空指针导致的应用奔溃
     *
     * @param paramSavedState onCreate传入的参数savedInstanceState
     * @return 如果决定自行处理savedInstanceState，请返回true, 默认返回false表示重启整个应用
     */
    protected abstract boolean onRestoreState(Bundle paramSavedState);

    /**
     * 为Activity设置主题相关，通过setTheme(int resId)方法指定主题
     */
    protected void setActivityTheme() {
    }

    /**
     * 在此调用setContentView，亦可以在此设置主题
     */
    protected abstract void setContentView();

    /**
     * 在此处理findViewById等操作
     */
    protected abstract void findViews();

    /**
     * 在此处理getIntent，初始化数据等操作
     */
    protected abstract void initialization();

    /**
     * 在此为View绑定事件
     */
    protected abstract void bindEvent();

    /**
     * 在此完成额外操作
     */
    protected abstract void doMoreInOnCreate();

    /**
     * 重启当前应用的方法
     */
    private void restartApp() {
        Intent intentForPackage = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
        intentForPackage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intentForPackage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intentForPackage);
        finish();
    }

    /**
     * 显示Toast的方法，此方法不管调用多少次，都不会重新创建新的Toast
     *
     * @param message Toast的显示内容
     */
    protected void showToast(String message) {
        Toast singleToast = CommonApplication.getSingleToast();
        if (singleToast == null) {
            JLog.e(TAG, CommonApplication.class.getSimpleName() + "not initialize");
            return;
        }
        singleToast.setText(message);
        singleToast.show();
    }

    /**
     * 显示进度Dialog
     *
     * @param message    Dialog的message内容
     * @param cancelable 此进度Dialog是否可以取消
     */
    protected void showProgressDialog(String message, boolean cancelable) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
        mProgressDialog = null;
        mProgressDialog = DialogCreator.createNoDimProgressDialog(this, message);
        mProgressDialog.setCancelable(cancelable);
        mProgressDialog.show();
    }

    /**
     * 隐藏进度Dialog
     */
    protected void dismissProgressDialog() {
        try {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    /**
     * 指定Bundle来打开Activity
     *
     * @param paramClass  新的Activity的class
     * @param paramBundle 你想传入到Intent的Bundle数据
     */
    protected void openActivityWithBundle(Class<?> paramClass, Bundle paramBundle) {
        Intent intent = new Intent(this, paramClass);
        if (paramBundle != null) {
            intent.putExtras(paramBundle);
        }
        startActivity(intent);
    }

    /**
     * Kitkat后设置Status bar的颜色的方法
     *
     * @param color 颜色的值
     */
    protected void setStatusBarColor(int color) {
        WindowHelper.setStatusBarColor(this, color);
    }

    /**
     * 初始化ToolBar并设置为ActionBar的方法
     *
     * @param toolBarId ToolBar的id
     * @param mode      HomeAsUp按钮的形式
     */
    protected void setupToolBar(int toolBarId, int mode) {
        setupToolBar(toolBarId, null, null, mode);
    }

    /**
     * 初始化ToolBar并设置为ActionBar的方法
     *
     * @param toolBar ToolBar的id
     * @param mode    HomeAsUp按钮的形式
     */
    protected void setupToolBar(Toolbar toolBar, int mode) {
        setupToolBar(toolBar, null, null, mode);
    }

    /**
     * 初始化ToolBar并设置为ActionBar的方法
     *
     * @param toolBarId ToolBar的id
     * @param title     ToolBar显示的标题
     * @param mode      HomeAsUp按钮的形式
     */
    protected void setupToolBar(int toolBarId, CharSequence title, int mode) {
        setupToolBar(toolBarId, title, null, mode);
    }

    /**
     * 初始化ToolBar并设置为ActionBar的方法
     *
     * @param toolBar ToolBar的id
     * @param title   ToolBar显示的标题
     * @param mode    HomeAsUp按钮的形式
     */
    protected void setupToolBar(Toolbar toolBar, CharSequence title, int mode) {
        setupToolBar(toolBar, title, null, mode);
    }

    /**
     * 初始化ToolBar并设置为ActionBar的方法
     *
     * @param toolBarId ToolBar的id
     * @param title     ToolBar显示的标题
     * @param subTitle  ToolBar显示的副标题
     * @param mode      HomeAsUp按钮的形式
     */
    protected void setupToolBar(int toolBarId, CharSequence title, CharSequence subTitle, int mode) {
        Toolbar toolbar = (Toolbar) findViewById(toolBarId);
        setupToolBar(toolbar, title, subTitle, mode);
    }

    /**
     * 初始化ToolBar并设置为ActionBar的方法
     *
     * @param toolBar  ToolBar实例
     * @param title    ToolBar显示的标题
     * @param subTitle ToolBar显示的副标题
     * @param mode     HomeAsUp按钮的形式
     */
    protected void setupToolBar(Toolbar toolBar, CharSequence title, CharSequence subTitle, int mode) {
        setSupportActionBar(toolBar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            switch (mode) {
                case MODE_NONE:
                    actionBar.setDisplayHomeAsUpEnabled(false);
                    break;
                case MODE_MENU:
                    actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
                    actionBar.setDisplayHomeAsUpEnabled(true);
                    break;
                case MODE_UP:
                    actionBar.setDisplayHomeAsUpEnabled(true);
                    break;
            }
            if (title != null) {
                actionBar.setTitle(title);
            }
            if (subTitle != null) {
                actionBar.setSubtitle(subTitle);
            }
        }
    }

    /**
     * 软键盘是否有显示
     *
     * @return 返回true表示有显示，否则返回false
     */
    protected boolean isKeyboardShown() {
        return getWindow().getAttributes().softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED;
    }

    /**
     * 隐藏软键盘
     *
     * @param view 传入任意一个View来获取WindowToken
     */
    protected void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 强制隐藏软键盘
     */
    protected void forceHideKeyboard() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    /**
     * 显示软键盘
     *
     * @param view 传入任意一个View来获取WindowToken
     */
    protected void showKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.SHOW_FORCED);
        imm.showSoftInput(view, 0);
    }

    /**
     * 让一个View获取焦点
     *
     * @param view 需要获取焦点的View
     */
    protected void gainFocus(View view) {
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
    }

    /**
     * 子类可以重写改变状态栏颜色
     */
    protected int setStatusBarColor() {
        return getColorPrimary();
    }

    /**
     * 获取主题色
     */
    public int getColorPrimary() {
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        return typedValue.data;
    }

    /**
     * 子类可以重写决定是否使用透明状态栏
     */
    protected boolean translucentStatusBar() {
        return false;
    }

    /**
     * 设置状态栏颜色
     */
    protected void initSystemBarTint() {
        Window window = getWindow();
        if (translucentStatusBar()) {
            // 设置状态栏全透明
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.TRANSPARENT);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }
            return;
        }
        // 沉浸式状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //5.0以上使用原生方法
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(setStatusBarColor());
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //4.4-5.0使用三方工具类
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintColor(setStatusBarColor());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        ExitAppUtils.getInstance().removeActivity(this);
    }
}