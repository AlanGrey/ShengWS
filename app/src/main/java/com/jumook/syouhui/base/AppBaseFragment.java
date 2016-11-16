package com.jumook.syouhui.base;

import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import com.jstudio.base.BaseSupportFragment;
import com.jstudio.base.CommonApplication;
import com.jstudio.utils.JLog;
import com.jstudio.widget.toast.SnackToast;
import com.jumook.syouhui.application.MyApp;
import com.jumook.syouhui.R;

/**
 * Created by Jason
 */
public abstract class AppBaseFragment extends BaseSupportFragment {

    protected AlertDialog.Builder mDialogBuilder;

    @Override
    protected void findViews(View view) {
    }

    protected void showSnackToast(String message) {
        SnackToast snackToast = MyApp.getSnackToast();
        if (snackToast == null) {
            JLog.e(AppBaseFragment.class.getSimpleName(), CommonApplication.class.getSimpleName() + "not initialize");
            return;
        }
        snackToast.setText(message);
        snackToast.show();
    }

    protected AlertDialog.Builder getDialogBuilder() {
        if (mDialogBuilder == null) {
            mDialogBuilder = new AlertDialog.Builder(mContext, R.style.MyDialogTheme);
        }
        return mDialogBuilder;
    }

    protected void setLayoutAnimation(ViewGroup viewGroup) {
        if (viewGroup != null) {
            Animation layoutAnim = AnimationUtils.loadAnimation(mContext, R.anim.fragment_enter);
            LayoutAnimationController controller = new LayoutAnimationController(layoutAnim);
            controller.setOrder(LayoutAnimationController.ORDER_NORMAL);
            controller.setDelay(0.2f);
            viewGroup.setLayoutAnimation(controller);
        }
    }

}
