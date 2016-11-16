package com.jumook.syouhui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.jumook.syouhui.R;

/**
 * Created by jumook on 2016/11/10.
 */

public class SystemDialog {


    private static SystemDialog instance = null;

    public static synchronized SystemDialog getInstance() {
        if (instance == null) {
            instance = new SystemDialog();
        }
        return instance;
    }


    public SystemDialog() {

    }


    /**
     * 确定|取消
     * @param context
     * @param title
     * @param cancel
     * @param confirm
     */
    public void showSystemDialog(Context context, String title, DialogInterface.OnClickListener cancel, DialogInterface.OnClickListener confirm) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyDialogTheme);
        builder.setTitle(title);
        builder.setPositiveButton("取消", cancel);
        builder.setNeutralButton("确定", confirm);
        builder.show();
    }

    public void showSystemDialog(Context context, String title, String b1, DialogInterface.OnClickListener cancel, String b2, DialogInterface.OnClickListener confirm) {
        Dialog dialog = new AlertDialog.Builder(context, R.style.MyDialogTheme)
                .setMessage(title)
                .setNeutralButton(b1, confirm)
                .setPositiveButton(b2, cancel)
                .create();
        dialog.show();
    }


}
