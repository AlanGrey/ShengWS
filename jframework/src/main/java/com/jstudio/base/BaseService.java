package com.jstudio.base;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import com.jstudio.utils.ExitAppUtils;
import com.jstudio.utils.JLog;

/**
 * 框架Service基类
 * <p/>
 * Created by Jason
 */
@SuppressLint("ShowToast")
public abstract class BaseService extends Service {

    public static String TAG = null;

    @Override
    public abstract IBinder onBind(Intent intent);

    @Override
    public void onCreate() {
        super.onCreate();
        TAG = this.getClass().getSimpleName();
        ExitAppUtils.getInstance().addService(this);
    }

    @Override
    public void onDestroy() {
        ExitAppUtils.getInstance().removeService(this);
        super.onDestroy();
    }

    /**
     * 显示Toast的方法，此方法不管调用多少次，都不会重新创建新的Toast
     *
     * @param message Toast的显示内容
     */
    protected void showToast(String message) {
        Toast singleToast = CommonApplication.getSingleToast();
        if (singleToast == null) {
            JLog.e(TAG, CommonApplication.class.getSimpleName() + "not initialize");
            return;
        }
        singleToast.setText(message);
        singleToast.show();
    }
}
