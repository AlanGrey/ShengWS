package com.jumook.syouhui.application;

import android.os.Bundle;
import android.view.Gravity;
import android.widget.Toast;

import com.jstudio.base.CommonApplication;
import com.jstudio.utils.JLog;
import com.jstudio.utils.PackageUtils;
import com.jstudio.widget.toast.SnackToast;
import com.jumook.syouhui.BuildConfig;
import com.jumook.syouhui.R;
import com.jumook.syouhui.http.AppBaseHttp;

import tencen.qq.QQUtils;

/**
 * Created by jumook on 2016/10/24.
 */

public class MyApp extends CommonApplication {

    private static MyApp INSTANCE;
    private static SnackToast mSnackToast;

    /**
     * 实现此方法返回BuildConfig.DEBUG的值，用于打印日志和创建crash log
     *
     * @return 返回true表示在debug环境，否则是正式环境
     */
    @Override
    protected boolean isInDebugMode() {
        return BuildConfig.DEBUG;
    }

    /**
     * 方便外部访问
     *
     * @return 本实例
     */
    public static MyApp getInstance() {
        return INSTANCE;
    }

    /**
     * 父类调用onCreate方法时调用
     */
    @Override
    protected void onApplicationCreate() {
        super.onApplicationCreate();
        INSTANCE = this;
        mSnackToast = new SnackToast(this);
        mSnackToast.setDuration(Toast.LENGTH_SHORT).setGravity(Gravity.TOP).setText("")
                .setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
    }

    /**
     * 在主进程创建时调用一次，用于处理初始化的一些操作，如创建文件夹，赋值mGlobalObject
     */
    @Override
    protected void init() {
        //初始化全局常用变量容器GlobalObject
        mGlobalObject = GlobalVar.getInstance();
        //获取MetaData对象
        Bundle bundle = PackageUtils.getMetaData(this);
        if (bundle != null) {
            //初始化微信
//            String wxId = bundle.getString("wx_id");
//            String wxSv = bundle.getString("wx_sv");
//            JLog.d(TAG, wxId + "            " + wxSv);
//            WeChat.getInstance().initWeChat(this.getApplicationContext(), wxId, wxSv);
            //初始化QQ
            int tencentAppId = bundle.getInt("tencent_id");
            QQUtils.getInstance().initQQ(this.getApplicationContext(), String.valueOf(tencentAppId));
        } else {
            JLog.d(TAG, "获取MetaDta_Bundle失败");
        }
    }

    public static SnackToast getSnackToast() {
        return mSnackToast;
    }


    /**
     * 扫描到日志文件执行
     *
     * @param crashFilePath 日志文件路径
     * @param crashInfo     错误日志内容
     */
    @Override
    protected void onDiscoverCrashLog(String crashFilePath, String crashInfo) {
        AppBaseHttp.getInstance().uploadCrashLog(this, crashInfo);
    }

}
