package com.jstudio.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;

public class SizeUtils {

    /**
     * 获取DisplayMetrics，可获取屏幕高宽，密度
     *
     * @param activity Activity
     * @return DisplayMetrics
     */
    public static DisplayMetrics getDisplayMetrics(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm;
    }

    /**
     * 获取物理屏幕高度，pixel
     *
     * @param context Context
     * @return px(int)
     */
    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 获取物理屏幕宽度，pixel
     *
     * @param context Context
     * @return px(int)
     */
    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 获取状态栏的高度
     *
     * @param activity Activity
     * @return 状态栏的高度，px
     */
    public static int getStatusBarHeight(Activity activity) {
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        return frame.top;
    }

    /**
     * 获取状态栏的高度
     *
     * @param activity Activity
     * @return 状态栏的高度，px
     */
    public static int getStatusBarHeight2(Activity activity) {
        int result = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                result = activity.getResources().getDimensionPixelSize(resourceId);
            }
        }
        return result;
    }

    /**
     * 转换dp到px
     *
     * @param context Context
     * @param dpValue dp值
     * @return pixel值
     */
    public static int convertDp2Px(Context context, float dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue,
                context.getResources().getDisplayMetrics());
    }

    /**
     * 转换dp到px
     *
     * @param context Context
     * @param dpValue dp值
     * @return pixel值
     */
    public static int simpleDp2Px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 转换px到dp
     *
     * @param context Context
     * @param pxValue pixel值
     * @return dp值
     */
    public static int convertPx2Dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 转换sp到px
     *
     * @param context Context
     * @param spValue sp值
     * @return pixel值
     */
    public static int simpleSp2Px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 转换sp到px
     *
     * @param context Context
     * @param spValue sp值
     * @return pixel值
     */
    public static int convertSp2Px(Context context, float spValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue,
                context.getResources().getDisplayMetrics());
    }

    /**
     * 转换px到sp
     *
     * @param context Context
     * @param pxValue pixel值
     * @return sp值
     */
    public static int convertPx2Sp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 获取View的宽度
     *
     * @param view 带测量的View
     * @return 宽度
     */
    public static int getWidgetWidth(View view) {
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(w, h);
        return view.getMeasuredWidth();
    }

    /**
     * 获取View的高度
     *
     * @param view 带测量的View
     * @return 高度
     */
    public static int getWidgetHeight(View view) {
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(w, h);
        return view.getMeasuredHeight();
    }

    /**
     * 通过onPreDrawListener获取高宽
     *
     * @param view     待测量的View
     * @param listener OnMeasuredBoundsListener
     */
    public static void getHeightAndWidth(final View view, final OnMeasuredBoundsListener listener) {
        view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                view.getViewTreeObserver().removeOnPreDrawListener(this);
                listener.onSuccess(view.getMeasuredHeight(), view.getMeasuredWidth());
                return true;
            }
        });
    }

    /**
     * 通过onGlobalLayoutListener获取高宽
     *
     * @param view     待测量的View
     * @param listener OnMeasuredBoundsListener
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void getHeightAndWidthWithGlobal(final View view, final OnMeasuredBoundsListener listener) {
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                listener.onSuccess(view.getMeasuredHeight(), view.getMeasuredWidth());
            }
        });
    }

    public interface OnMeasuredBoundsListener {
        void onSuccess(int height, int width);
    }
}
