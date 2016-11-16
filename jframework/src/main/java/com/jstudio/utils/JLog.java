package com.jstudio.utils;

import android.util.Log;


/**
 * 打印出行方法名的日志工具
 * <p/>
 * Created by Jason
 */
public class JLog {

    private static JLog INSTANCE;
    private static boolean ENABLE_LOGGER = false;

    private JLog() {
    }

    /**
     * 是否允许打印日志
     *
     * @param enable true为允许，false为不允许
     */
    public static void setEnable(boolean enable) {
        ENABLE_LOGGER = enable;
    }

    /**
     * 获取实例
     *
     * @return 本实例
     */
    private static JLog getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new JLog();
        }
        return INSTANCE;
    }

    /**
     * 获取行数及方法名
     *
     * @return 行数及方法名
     */
    private String getLogDetail() {
        StackTraceElement[] sts = Thread.currentThread().getStackTrace();
        if (sts == null) {
            return null;
        }
        for (StackTraceElement st : sts) {
            if (st.isNativeMethod()) {
                continue;
            }
            if (st.getClassName().equals(Thread.class.getName())) {
                continue;
            }
            if (st.getClassName().equals(this.getClass().getName())) {
                continue;
            }
            return "[(" + st.getFileName() + ":" + st.getLineNumber() + ") " + st.getMethodName() + "]";
        }
        return null;
    }

    /**
     * Log.i
     *
     * @param tag 标识log的level
     * @param msg 打印的日志内容
     */
    public static void i(String tag, String msg) {
        if (ENABLE_LOGGER) {
            String name = getInstance().getLogDetail();
            if (name != null) {
                Log.i(tag, name + " -> " + msg);
            } else {
                Log.i(tag, msg);
            }
        }
    }

    /**
     * Log.d
     *
     * @param tag 标识log的level
     * @param msg 打印的日志内容
     */
    public static void d(String tag, String msg) {
        if (ENABLE_LOGGER) {
            String name = getInstance().getLogDetail();
            if (name != null) {
                Log.d(tag, name + " -> " + msg);
            } else {
                Log.d(tag, msg);
            }
        }
    }

    /**
     * Log.v
     *
     * @param tag 标识log的level
     * @param msg 打印的日志内容
     */
    public static void v(String tag, String msg) {
        if (ENABLE_LOGGER) {
            String name = getInstance().getLogDetail();
            if (name != null) {
                Log.v(tag, name + " -> " + msg);
            } else {
                Log.v(tag, msg);
            }
        }
    }

    /**
     * Log.w
     *
     * @param tag 标识log的level
     * @param msg 打印的日志内容
     */
    public static void w(String tag, String msg) {
        if (ENABLE_LOGGER) {
            String name = getInstance().getLogDetail();
            if (name != null) {
                Log.w(tag, name + " -> " + msg);
            } else {
                Log.w(tag, msg);
            }
        }
    }

    /**
     * Log.e
     *
     * @param tag 标识log的level
     * @param msg 打印的日志内容
     */
    public static void e(String tag, String msg) {
        if (ENABLE_LOGGER) {
            String name = getInstance().getLogDetail();
            if (name != null) {
                Log.e(tag, name + " -> " + msg);
            } else {
                Log.e(tag, msg);
            }
        }
    }

    /**
     * Log.e
     *
     * @param tag 标识log的level
     * @param ex  打印的异常内容
     */
    public static void e(String tag, Exception ex) {
        if (ENABLE_LOGGER) {
            Log.e(tag, "ERROR", ex);
        }
    }
}


