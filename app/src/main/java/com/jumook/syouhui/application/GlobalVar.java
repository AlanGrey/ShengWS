package com.jumook.syouhui.application;

import android.content.Context;

import com.jstudio.base.GlobalObject;
import com.jstudio.utils.FileUtils;
import com.jstudio.utils.PreferencesUtils;
import com.jstudio.utils.SizeUtils;
import com.jumook.syouhui.bean.UserEntity;
import com.jumook.syouhui.constants.AppConstant;

import java.util.Map;

/**
 * Created by Jason
 */
public class GlobalVar extends GlobalObject {

    public static final String TAG = GlobalVar.class.getSimpleName();

    public UserEntity mUserEntity;
    public PreferencesUtils basePreference;
    public PreferencesUtils infoPreference;

    /**
     * GlobalObject实例，有且只有一个
     */
    private static GlobalVar INSTANCE;

    private GlobalVar(Context context) {
        super();
        //屏幕宽度
        mScreenWidth = SizeUtils.getScreenWidth(context);
        //屏幕高度
        mScreenHeight = SizeUtils.getScreenHeight(context);
        //外置存储是否可用
        mIsExternalAvailable = FileUtils.isExternalStorageAvailable();
        //应用主文件夹完整路径
        mAppFolderPath = FileUtils.getAppMainFolder(context);
        //应用主文件夹名字
        mAppFolderName = mIsExternalAvailable ? "." + context.getPackageName() : context.getPackageName();

        //应用渠道包
        Channel = "guanfang";//渠道包
        //初始化
        basePreference = PreferencesUtils.getInstance(context, AppConstant.APP_BASE_PREFERENCE);
        mAppToken = basePreference.getString(AppConstant.APP_TOKEN, "");
        mSessionId = basePreference.getString(AppConstant.APP_SESSION, "");
        mUserId = basePreference.getInt(AppConstant.USER_ID, 0);
        mIsLogin = basePreference.getBoolean(AppConstant.USER_IS_LOGIN, false);

        infoPreference = PreferencesUtils.getInstance(context, AppConstant.APP_Info_PREFERENCE);

    }

    @Override
    public Map<String, String> getCommonParams() {
        return null;
    }

    public static GlobalVar getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new GlobalVar(MyApp.getInstance());
        }
        return INSTANCE;
    }

    public String getSign() {
        return mSign;
    }

    public String getPhoneType() {
        return phoneType;
    }

    public void clearNativeData() {
        basePreference.cleadData();
        basePreference.putString(AppConstant.APP_TOKEN, mAppToken).apply();
    }
}
