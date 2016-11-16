package com.jstudio.network.callback;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.view.Window;

import com.lzy.okgo.callback.BitmapCallback;
import com.lzy.okgo.request.BaseRequest;

/**
 * 求图图片的时候显示对话框请
 */
public abstract class BitmapDialogCallback extends BitmapCallback {

    private ProgressDialog dialog;

    public BitmapDialogCallback(Activity activity) {
        dialog = new ProgressDialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("正在操作...请稍候...");
    }

    @Override
    public void onBefore(BaseRequest request) {
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }
    }

    @Override
    public void onAfter(@Nullable Bitmap bitmap, @Nullable Exception e) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
