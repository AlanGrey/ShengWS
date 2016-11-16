package com.jstudio.base;

import android.annotation.SuppressLint;
import android.app.Application;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.widget.Toast;

import com.jstudio.R;
import com.jstudio.network.OkGoUtils;
import com.jstudio.utils.CrashHandler;
import com.jstudio.utils.ExitAppUtils;
import com.jstudio.utils.FrescoUtils;
import com.jstudio.utils.JLog;
import com.jstudio.utils.PackageUtils;

/**
 * 子类要添加getInstance方法方便App调用，CommonApplication中已经初始化网络请求，图片加载，奔溃日志的初始化
 * 其他的操作请在init方法中实现
 * <p/>
 * Created by Jason
 */
abstract public class CommonApplication extends Application {

    public static String TAG = null;
    protected static GlobalObject mGlobalObject;
    private static Toast mSingleToast;

    @SuppressLint("ShowToast")
    @Override
    public void onCreate() {
        super.onCreate();
        TAG = this.getClass().getSimpleName();
        onApplicationCreate();
        //为log设置开关
        JLog.setEnable(isInDebugMode());
        if (getPackageName().equals(PackageUtils.getCurrentProcessName(this))) {
            //创建全局唯一Toast
            mSingleToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
            //初始化一些操作
            init();
            //检查GlobalObject是否已经实例化，否则跳出onCreate方法， 必须在init中实例化
            if (mGlobalObject == null) {
                JLog.e(TAG, "GlobalObject should not be null, please initialize it in method init");
                return;
            }
            //初始化网络请求框架
            OkGoUtils.initWithConfig(this);
            //初始化图片加载框架Fresco
//            Fresco.initialize(this);
            FrescoUtils.initWithConfig(this, 50, Bitmap.Config.ARGB_8888);
            //初始化奔溃日志收集
            CrashHandler.getInstance().init(this, mGlobalObject.mAppFolderName, getString(R.string.crash_occur_toast), isInDebugMode(), new CrashHandler.ExceptionOperator() {
                @Override
                public void onExceptionThrows() {
                    ExitAppUtils.getInstance().exit();
                }
            });
            //检索是否有存在的奔溃日志
            String crashInfo = CrashHandler.getCrashInfo();
            if (!TextUtils.isEmpty(crashInfo)) {
                //如果存在日志将日志信息暴露出去处理
                onDiscoverCrashLog(CrashHandler.getCrashFilePath(), crashInfo);
            }
        }
    }

    /**
     * 获取全局唯一的Toast，所有界面共用
     *
     * @return Toast
     */
    public static Toast getSingleToast() {
        return mSingleToast;
    }

    /**
     * 实现此方法返回BuildConfig.DEBUG的值，用于打印日志和创建crash log
     *
     * @return 返回true表示在debug环境，否则是正式环境
     */
    protected abstract boolean isInDebugMode();

    /**
     * 进程创建时调用，每次创建进程都调用
     */
    protected void onApplicationCreate() {
    }

    /**
     * 在主进程创建时调用一次，用于处理初始化的一些操作
     */
    protected abstract void init();

    /**
     * 在扫描到crash log时会被调用
     *
     * @param crashFilePath 日志文件路径
     * @param crashInfo     错误日志内容
     */
    protected abstract void onDiscoverCrashLog(String crashFilePath, String crashInfo);

}
