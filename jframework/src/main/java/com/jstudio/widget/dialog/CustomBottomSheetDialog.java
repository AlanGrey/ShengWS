package com.jstudio.widget.dialog;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.design.widget.BottomSheetDialog;
import android.view.ViewGroup;

import com.jstudio.utils.SizeUtils;

/**
 * Created by Jason
 */

public class CustomBottomSheetDialog extends BottomSheetDialog {

    private Activity mActivity;

    public CustomBottomSheetDialog(@NonNull Context context) {
        super(context);
        if (context instanceof Activity) {
            mActivity = (Activity) context;
        }
    }

    public CustomBottomSheetDialog(@NonNull Context context, @StyleRes int theme) {
        super(context, theme);
    }

    protected CustomBottomSheetDialog(@NonNull Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mActivity == null) {
            return;
        }
        int screenHeight = SizeUtils.getScreenHeight(mActivity);
        int statusBarHeight = SizeUtils.getStatusBarHeight2(mActivity);
        int dialogHeight = screenHeight - statusBarHeight;
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, dialogHeight == 0 ? ViewGroup.LayoutParams.MATCH_PARENT : dialogHeight);
    }
}
