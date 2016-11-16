package com.jstudio.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 异常捕获类
 * <p>Usage: in Application, method onCreate;
 * <p>CrashHandler crashHandler = CrashHandler.getInstance();
 * <p>crashHandler.init(getApplicationContext(),"/crash/", "Application crashed, exiting...", new ExceptionOperator(){...});
 * <p>add permission: android.permission.WRITE_EXTERNAL_STORAGE in manifest
 * <p/>
 * Created by Jason
 */
public class CrashHandler implements UncaughtExceptionHandler {

    public static final String TAG = CrashHandler.class.getSimpleName();
    private static String mFolderPath;
    private static CrashHandler INSTANCE;
    //奔溃后Toast显示的文字
    public String mShowMessage = null;
    private Context mContext;
    private UncaughtExceptionHandler mDefaultHandler;
    private Map<String, String> mCrashInfo = new HashMap<>();
    private ExceptionOperator mExceptionOperator;
    private boolean mIsDebug = false;

    private CrashHandler() {
    }

    public static CrashHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CrashHandler();
        }
        return INSTANCE;
    }

    /**
     * 初始化CrashHandler
     *
     * @param context     Context
     * @param filePath    保存Crash信息的文件夹名
     * @param showMessage 奔溃时的Toast显示的文字
     * @param isDebug     处于Debug模式不会产生日志文件，release环境中才会保存
     * @param operator    奔溃后的收尾工作处理类
     */
    public void init(Context context, String filePath, String showMessage, boolean isDebug, ExceptionOperator operator) {
        this.mIsDebug = isDebug;
        mExceptionOperator = operator;
        if (!TextUtils.isEmpty(filePath)) {
            mFolderPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + filePath + File.separator;
        }
        if (!TextUtils.isEmpty(showMessage)) {
            this.mShowMessage = showMessage;
        }
        mContext = context;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable exception) {
        boolean isHandle = handleException(exception);
        if (mIsDebug) {
            mDefaultHandler.uncaughtException(thread, exception);
        }
        if (!isHandle && mDefaultHandler != null) {
            mDefaultHandler.uncaughtException(thread, exception);
        } else {
            try {
                Thread.sleep(2500);
                if (mExceptionOperator != null) {
                    mExceptionOperator.onExceptionThrows();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean handleException(Throwable exception) {
        if (exception == null) {
            return false;
        }
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                if (mShowMessage != null && !mIsDebug) {
                    Toast.makeText(mContext, mShowMessage, Toast.LENGTH_LONG).show();
                }
                Looper.loop();
            }
        }.start();
        collectDeviceInfo(mContext);
        saveCrashInfo2File(exception);
        return true;
    }

    /**
     * 收集异常信息
     *
     * @param context Context
     */
    public void collectDeviceInfo(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                String versionName = pi.versionName == null ? "null" : pi.versionName;
                String versionCode = pi.versionCode + "";
                mCrashInfo.put("versionName", versionName);
                mCrashInfo.put("versionCode", versionCode);
                mCrashInfo.put("currentTime", formatter.format(new Date(System.currentTimeMillis())));
            }
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                mCrashInfo.put(field.getName(), field.get(null).toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 保存信息到文件
     *
     * @param ex Exception
     * @return true表示保存成功
     */
    private boolean saveCrashInfo2File(Throwable ex) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : mCrashInfo.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key).append("=").append(value).append("\n");
        }

        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        sb.append(result);
        try {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                String path = mFolderPath;
                String filePath = path + "crash.txt";
                File dir = new File(path);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                FileOutputStream fos = new FileOutputStream(filePath);
                fos.write(sb.toString().getBytes());
                fos.close();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 读取存在的日志
     *
     * @return 如果日志存在，返回日志内容，否则返回空
     */
    public static String getCrashInfo() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return null;
        }
        String info = null;
        String filePath = getCrashFilePath();
        if (filePath == null) {
            return null;
        }
        File f = new File(filePath);
        if (f.exists()) {
            StringBuilder log = new StringBuilder();
            try {
                FileReader reader = new FileReader(f);
                BufferedReader bReader = new BufferedReader(reader);
                String buff;
                while ((buff = bReader.readLine()) != null) {
                    log.append(buff);
                }
                bReader.close();
                reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            info = log.toString();
            if (TextUtils.isEmpty(info)) {
                return null;
            }
        }
        return info;
    }

    /**
     * 获取奔溃日志文件的路径
     *
     * @return 日志文件路径
     */
    public static String getCrashFilePath() {
        return mFolderPath == null ? null : mFolderPath + "crash.txt";
    }

    /**
     * 删除日志文件
     */
    public static void deleteLogFile() {
        String filePath = mFolderPath + "crash.txt";
        File f = new File(filePath);
        if (f.exists()) {
            JLog.i(TAG, f.delete() ? "Deleted" : "Not delete");
        }
    }

    public interface ExceptionOperator {
        /**
         * 处理收尾工作的方法体，退出app，保存该保存的内容等
         */
        void onExceptionThrows();
    }
}
