package com.jstudio.base;

import java.util.Map;

/**
 * 存放常用的变量，在app启动时刻就必须初始化，供后续调用
 * <p/>
 * Created by Jason
 */
@SuppressWarnings("unused")
public abstract class GlobalObject {

    /**
     * AppToken，App唯一标识，请求公参之一
     */
    public String mAppToken;

    /**
     * Sign，请求公参之一
     */
    protected String mSign = "shenyouhuiAPI";

    /**
     * SessionId，请求公参之一
     */
    public String mSessionId;

    /**
     * UserId，用户Id
     */
    public int mUserId;

    /**
     * mOldVersion  App版本号
     */
    public int mOldVersion;

    /**
     * mIsFirstStar 判断是否第一次启动APP
     */
    public boolean mIsFirstStar;

    /**
     * 外置存储是否就位
     */
    public boolean mIsExternalAvailable;

    /**
     * 应用主文件夹名字
     */
    public String mAppFolderName;

    /**
     * 应用主文件夹完整路径
     */
    public String mAppFolderPath;

    /**
     * 屏幕宽度
     */
    public int mScreenWidth;

    /**
     * 屏幕高度
     */
    public int mScreenHeight;

    /**
     * 网络状态
     */
    public boolean mNetWorkState;

    /**
     * 登录状态
     */
    public boolean mIsLogin;

    /**
     * 渠道包
     */
    public String Channel;

    /**
     * 经纬度，地址
     */
    public float lat;
    public float lng;
    public String address;

    /**
     * 手机类型
     */
    protected String phoneType = "2";

    /**
     * 获取公参的方法，子类实现
     *
     * @return 存有公参的HashMap
     */
    public abstract Map<String, String> getCommonParams();


}
