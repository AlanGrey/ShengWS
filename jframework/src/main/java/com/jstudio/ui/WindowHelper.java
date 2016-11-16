package com.jstudio.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.util.Locale;

/**
 * Created by Jason
 */
public class WindowHelper {

    /**
     * 用于更改语言环境，调用完之后需要重新启动Activity
     *
     * @param context Context
     * @param locale  Locale
     */
    @SuppressWarnings("unused")
    public static void setUpLocalization(Context context, Locale locale) {
        Resources resources = context.getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;
        resources.updateConfiguration(configuration, displayMetrics);
    }

    /**
     * 降低状态栏的可视度
     *
     * @param activity Context为Activity
     */
    @SuppressLint("NewApi")
    public static void lowerStatusBarVisibility(@NonNull Activity activity) {
        Window localWindow = activity.getWindow();
        WindowManager.LayoutParams localLayoutParams = localWindow.getAttributes();
        localLayoutParams.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE;
        localWindow.setAttributes(localLayoutParams);
    }

    public static void setStatusBarColor(Activity activity, int color) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            SystemBarTintManager tintManager = new SystemBarTintManager(activity);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setNavigationBarTintEnabled(true);
            tintManager.setTintColor(color);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
        }
    }

    /**
     * 切换传入Activity为全屏模式
     *
     * @param activity     Context为Activity
     * @param isFullScreen true为设置全屏，否则退出全屏
     */
    public static void setFullScreen(@NonNull Activity activity, boolean isFullScreen) {
        if (isFullScreen) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        } else {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    /**
     * 打开或关闭屏幕常亮
     *
     * @param activity Context为Activity
     * @param keep     true为常亮，false取消常亮
     */
    public static void keepScreenOn(@NonNull Activity activity, boolean keep) {
        if (keep) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    /**
     * 设置当前Activity为竖屏方向
     *
     * @param activity   Activity
     * @param isPortrait true设置到竖屏，否则设置为未指定
     */
    public static void setActivityPortrait(Activity activity, boolean isPortrait) {
        if (isPortrait) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
    }

    /**
     * 锁住屏幕的方向
     *
     * @param activity Activity
     */
    public static void lockScreenOrientation(Activity activity) {
        if (activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        if (activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    /**
     * 解除屏幕方向锁
     *
     * @param activity Activity
     */
    public static void unlockScreenOrientation(Activity activity) {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

}
