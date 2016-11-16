package com.jstudio.widget.pager;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class NoSlidingViewPager extends ViewPager {

    private boolean mIsEnableSliding;

    public NoSlidingViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        mIsEnableSliding = false;
    }

    public NoSlidingViewPager(Context context) {
        super(context);
        mIsEnableSliding = false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        return mIsEnableSliding && super.onInterceptTouchEvent(arg0);
    }

    @Override
    public boolean onTouchEvent(MotionEvent arg0) {
        return mIsEnableSliding && super.onTouchEvent(arg0);
    }

    public boolean isEnableSliding() {
        return mIsEnableSliding;
    }

    /**
     * 设置可以左右滑动 default = false
     *
     * @param enableSliding 设置是否允许滑动
     */
    public void setEnableSliding(boolean enableSliding) {
        this.mIsEnableSliding = enableSliding;
    }
}
