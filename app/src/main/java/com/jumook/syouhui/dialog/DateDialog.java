package com.jumook.syouhui.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.widget.DatePicker;

/**
 * 时间选择器弹出框
 * <p>
 * Created by jumook on 2016/11/14.
 */

abstract public class DateDialog {

    private Activity activity;

    private DatePickerDialog dialog;

    public DateDialog(Activity activity) {
        this.activity = activity;
    }

    abstract public void dataSelectCallBack(int year, int month, int day);

    public void init() {
        dialog = new DatePickerDialog(activity, AlertDialog.THEME_HOLO_LIGHT, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                dataSelectCallBack(year, monthOfYear + 1, dayOfMonth);
            }
        }, 1980, 0, 1);
        dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        dialog.getDatePicker().setMinDate(315504000L);
    }

    public void showDialog() {
        dialog.show();
    }

    public void updateDate(int year, int month, int day) {
        dialog.updateDate(year, month, day);
    }

    public void setMaxDate(long time) {
        dialog.getDatePicker().setMaxDate(time);
    }

    public void setMinDate(long time) {
        dialog.getDatePicker().setMinDate(time);
    }
}
