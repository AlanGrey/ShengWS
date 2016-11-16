package com.swipebacklayout.lib.app;

import android.os.Bundle;
import android.view.View;

import com.jstudio.base.BaseAppCompatActivity;
import com.swipebacklayout.lib.SwipeBackLayout;
import com.swipebacklayout.lib.Utils;


public abstract class BaseSwipeActivity extends BaseAppCompatActivity implements SwipeBackActivityBase {
    private SwipeBackActivityHelper mHelper;

    @Override
    abstract protected boolean onRestoreState(Bundle paramSavedState);

    @Override
    final protected void setContentView(){
        mHelper = new SwipeBackActivityHelper(this);
        mHelper.onActivityCreate();
        setContentViewLayout();
    }

    protected abstract void setContentViewLayout();

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mHelper.onPostCreate();
    }

    @Override
    public View findViewById(int id) {
        View v = super.findViewById(id);
        if (v == null && mHelper != null)
            return mHelper.findViewById(id);
        return v;
    }

    @Override
    public SwipeBackLayout getSwipeBackLayout() {
        return mHelper.getSwipeBackLayout();
    }

    @Override
    public void setSwipeBackEnable(boolean enable) {
        getSwipeBackLayout().setEnableGesture(enable);
    }

    @Override
    public void scrollToFinishActivity() {
        Utils.convertActivityToTranslucent(this);
        getSwipeBackLayout().scrollToFinishActivity();
    }
}
