package com.jstudio.animation;

import android.animation.AnimatorSet;
import android.animation.LayoutTransition;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;

/**
 * Created by Jason on 3/30/16.
 */
public class EasyAnimation {

    /**
     * 更改窗口透明度
     *
     * @param activity 要更改的Activity
     * @param duration 持续时间
     * @param from     开始透明度
     * @param to       结束透明度
     */
    public static void dimBackground(Activity activity, long duration, float from, float to) {
        final Window window = activity.getWindow();
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(from, to);
        valueAnimator.setDuration(duration);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                WindowManager.LayoutParams params = window.getAttributes();
                params.alpha = (Float) animation.getAnimatedValue();
                window.setAttributes(params);
            }
        });
        valueAnimator.start();
    }

    public static void verticalMove(final View view, long duration, float from, float to) {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(from, to);
        valueAnimator.setTarget(view);
        valueAnimator.setDuration(duration);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                view.setTranslationY((Float) animation.getAnimatedValue());
            }
        });
    }

    public static void horizontalMove(final View view, long duration, float from, float to) {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(from, to);
        valueAnimator.setTarget(view);
        valueAnimator.setDuration(duration);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                view.setTranslationX((Float) animation.getAnimatedValue());
            }
        });
    }

    public static void changeAlpha(final View view, long duration, float from, float to) {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(from, to);
        valueAnimator.setTarget(view);
        valueAnimator.setDuration(duration);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                view.setAlpha((Float) animation.getAnimatedValue());
            }
        });
    }

    public static void rotateView(final View view, long duration, float pivotX, float pivotY, float from, float to) {
        view.setPivotX(pivotX);
        view.setPivotY(pivotY);
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(from, to);
        valueAnimator.setTarget(view);
        valueAnimator.setDuration(duration);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                view.setRotation((Float) animation.getAnimatedValue());
            }
        });
    }

    public static void scaleView(final View view, long duration, float pivotX, float pivotY, float from, float to) {
        view.setPivotX(pivotX);
        view.setPivotY(pivotY);
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(from, to);
        valueAnimator.setTarget(view);
        valueAnimator.setDuration(duration);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float scale = (Float) animation.getAnimatedValue();
                view.setScaleX(scale);
                view.setScaleY(scale);
            }
        });
    }

    public static void playQueue(long duration, ValueAnimator first, ValueAnimator second) {
        AnimatorSet animSet = new AnimatorSet();
        animSet.play(first).with(second);
        animSet.setDuration(duration);
        animSet.start();
    }

    public static void enableLayoutTransition(ViewGroup viewGroup){
        viewGroup.setLayoutTransition(new LayoutTransition());
    }

}
