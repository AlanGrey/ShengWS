package com.jstudio.base;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.jstudio.utils.JLog;

import butterknife.ButterKnife;

/**
 * 框架Fragment基类
 * Created by Jason
 */
@SuppressWarnings("unused")
public abstract class BaseSupportFragment extends Fragment {

    protected Context mContext;
    protected boolean mIsViewCreated;
    protected boolean mIsStartLoading;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(setLayout(), container, false);
        ButterKnife.bind(this, fragmentView);
        findViews(fragmentView);
        initialization();
        bindEvent();
        onCreateView();
        mIsViewCreated = true;
        return fragmentView;
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getUserVisibleHint()) {
            lazyLoad();
            mIsStartLoading = true;
        }
    }

    /**
     * 设置Fragment的布局id
     *
     * @return 返回布局的资源id
     */
    protected abstract int setLayout();

    /**
     * 在此处理findViewById等操作
     */
    protected abstract void findViews(View view);

    /**
     * 在此处理或初始化数据操作
     */
    protected abstract void initialization();

    /**
     * 在此为View绑定事件
     */
    protected abstract void bindEvent();

    /**
     * 在此完成额外操作
     */
    protected abstract void onCreateView();

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && mIsViewCreated && !mIsStartLoading) {
            lazyLoad();
            mIsStartLoading = true;
        }
    }

    /**
     * 在ViewPager中实现懒加载的方法
     */
    protected void lazyLoad() {
    }

    /**
     * 显示Toast的方法，此方法不管调用多少次，都不会重新创建新的Toast
     *
     * @param message Toast的显示内容
     */
    protected void showToast(String message) {
        Toast singleToast = CommonApplication.getSingleToast();
        if (singleToast == null) {
            JLog.e(BaseSupportFragment.class.getSimpleName(), CommonApplication.class.getSimpleName() + "not initialize");
            return;
        }
        singleToast.setText(message);
        singleToast.show();
    }

    /**
     * 显示进度Dialog，当前Fragment必须可见并处于BaseAppCompatActivity中
     *
     * @param message    显示内容
     * @param cancelable dialog是否可取消
     */
    protected void showProgressDialog(String message, boolean cancelable) {
        if (mIsViewCreated && mContext instanceof BaseAppCompatActivity) {
            ((BaseAppCompatActivity) mContext).showProgressDialog(message, cancelable);
        }
    }

    /**
     * 隐藏进度Dialog，当前Fragment必须可见并处于BaseAppCompatActivity中
     */
    protected void dismissProgressDialog() {
        if (mIsViewCreated && mContext instanceof BaseAppCompatActivity) {
            ((BaseAppCompatActivity) mContext).dismissProgressDialog();
        }
    }

    /**
     * 隐藏软键盘
     *
     * @param view 传入任意一个View来获取WindowToken
     */
    protected void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 强制隐藏软键盘
     */
    protected void forceHideKeyboard() {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    /**
     * 显示软键盘
     *
     * @param view 传入任意一个View来获取WindowToken
     */
    protected void showKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
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
     * 指定Bundle来打开Activity
     *
     * @param paramClass  新的Activity的class
     * @param paramBundle 你想传入到Intent的Bundle数据
     */
    protected void openActivityWithBundle(Class<?> paramClass, Bundle paramBundle) {
        Intent intent = new Intent(mContext, paramClass);
        if (paramBundle != null) {
            intent.putExtras(paramBundle);
        }
        startActivity(intent);
    }
}
