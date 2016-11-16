package com.jumook.syouhui.http;

import android.app.Activity;
import android.content.Context;

import com.jstudio.utils.PackageUtils;
import com.jstudio.utils.PhoneUtils;
import com.jumook.syouhui.application.GlobalVar;
import com.jumook.syouhui.constants.NetParams;
import com.jumook.syouhui.constants.ip.LoginUrls;
import com.jumook.syouhui.constants.ip.Urls;
import com.jumook.syouhui.network.HttpUtils;
import com.jumook.syouhui.network.callback.JsonStringBack;
import com.jumook.syouhui.network.callback.JsonStringDialogCallback;
import com.lzy.okgo.callback.StringCallback;

import java.util.HashMap;

/**
 * 登录、注册、验证码等接口
 * Created by jumook on 2016/11/10.
 */

public class LoginHttp {

    public static final String TAG = "LoginHttp";

    private static LoginHttp instance = null;

    public static synchronized LoginHttp getInstance() {
        if (instance == null) {
            instance = new LoginHttp();
        }
        return instance;
    }

    public LoginHttp() {
    }

    /**
     * 校验手机号码是否注册
     *
     * @param context     文本
     * @param phoneNumber 手机号码
     * @param callBack    回调监听
     */
    public void checkPhoneNumber(Context context, String phoneNumber, JsonStringDialogCallback callBack) {
        String url = Urls.URL + LoginUrls.CHECK_PHONE_STATE;
        //请求参数
        HashMap<String, String> params = new HashMap<>();
        params.put(NetParams.SIGN, GlobalVar.getInstance().getSign());
        params.put(NetParams.APP_TOKEN, GlobalVar.getInstance().mAppToken);
        params.put("mobile", phoneNumber);
        HttpUtils.basePost(url, context, params, callBack);
    }

    /**
     * 获取手机验证码
     *
     * @param context     文本
     * @param phoneNumber 手机号码
     * @param callBack    回调监听
     */
    public void getPhoneCode(Context context, String phoneNumber, StringCallback callBack) {
        String url = Urls.URL + LoginUrls.GET_PHONE_CODE;
        HashMap<String, String> params = new HashMap<>();
        params.put(NetParams.SIGN, GlobalVar.getInstance().getSign());
        params.put(NetParams.APP_TOKEN, GlobalVar.getInstance().mAppToken);
        params.put("mobile", phoneNumber);
        HttpUtils.basePost(url, context, params, callBack);
    }

    /**
     * 手机注册帐号
     *
     * @param activity    activity
     * @param phoneNumber 手机号码
     * @param password    密码
     * @param code        验证码
     * @param sessionId   session值
     * @param inviteCode  邀请码
     * @param callback    回调监听
     */
    public void registerUser(Activity activity, String phoneNumber, String password, String code, String sessionId, String inviteCode, StringCallback callback) {
        String url = Urls.URL + LoginUrls.REGISTER_USER;
        HashMap<String, String> params = new HashMap<>();
        params.put(NetParams.SIGN, GlobalVar.getInstance().getSign());
        params.put(NetParams.APP_TOKEN, GlobalVar.getInstance().mAppToken);
        params.put("mobile", phoneNumber);
        params.put("password", password);
        params.put("type", "1");
        params.put("mobile_code", code);
        params.put("invite_code", inviteCode);
        params.put("mobile_imei", PhoneUtils.getDeviceCode(activity));
        params.put("app_version", String.format("android_%s", PackageUtils.getVersionCode(activity)));
        params.put("platform", GlobalVar.getInstance().Channel);
        params.put("session_id", sessionId);
        HttpUtils.basePost(url, activity, params, callback);
    }

    /**
     * 获取病情种类
     *
     * @param context  文本
     * @param callback 回调监听
     */
    public void getIllnessType(Context context, JsonStringBack callback) {
        String url = Urls.URL + LoginUrls.GET_ILLNESS_TYPE;
        HashMap<String, String> params = new HashMap<>();
        params.put(NetParams.SIGN, GlobalVar.getInstance().getSign());
        params.put(NetParams.APP_TOKEN, GlobalVar.getInstance().mAppToken);
        HttpUtils.basePost(url, context, params, callback);
    }

    /**
     * 用户注册资料提交
     *
     * @param context    文本
     * @param userId     用户Id
     * @param sessId     sessId
     * @param userAvatar 头像
     * @param nickName   昵称
     * @param gender     性别
     * @param birthday   生日
     * @param area       地址
     * @param illness    病情
     * @param callback   回调监听
     */
    public void postRegisterInfo(Context context, int userId, String sessId, String userAvatar, String nickName, int gender, long birthday, String area, int illness, JsonStringDialogCallback callback) {
        String url = Urls.URL + LoginUrls.POST_REGISTER_USER_INFO;
        HashMap<String, String> params = new HashMap<>();
        params.put(NetParams.SIGN, GlobalVar.getInstance().getSign());
        params.put(NetParams.APP_TOKEN, GlobalVar.getInstance().mAppToken);
        params.put(NetParams.SESS_ID, sessId);
        params.put(NetParams.USER_ID, String.valueOf(userId));
        params.put("user_avatar", userAvatar);
        params.put("username", nickName);
        params.put("user_gender", String.valueOf(gender));
        params.put("user_birth", String.valueOf(birthday));
        params.put("user_address", area);
        params.put("illness_id", String.valueOf(illness));
        HttpUtils.basePost(url, context, params, callback);
    }

    /**
     * 修改密码（忘记密码）
     *
     * @param context     文本
     * @param phoneNumber 手机号码
     * @param password    新密码
     * @param code        验证码
     * @param sessionId   sessionId
     * @param callback    回调监听
     */
    public void changePassword(Context context, String phoneNumber, String password, String code, String sessionId, JsonStringDialogCallback callback) {
        String url = Urls.URL + LoginUrls.CHANGE_PASSWORD;
        HashMap<String, String> params = new HashMap<>();
        params.put(NetParams.SIGN, GlobalVar.getInstance().getSign());
        params.put(NetParams.APP_TOKEN, GlobalVar.getInstance().mAppToken);
        params.put("mobile", phoneNumber);
        params.put("password", password);
        params.put("mobile_code", code);
        params.put("session_id", sessionId);
        HttpUtils.basePost(url, context, params, callback);
    }

    public void thirdPartyLogin(Context context, int loginType, String openId, JsonStringDialogCallback callback) {
        GlobalVar.getInstance().clearNativeData();
        String url = Urls.URL + LoginUrls.USER_LOGIN;
        HashMap<String, String> params = new HashMap<>();
        params.put(NetParams.SIGN, GlobalVar.getInstance().getSign());
        params.put(NetParams.APP_TOKEN, GlobalVar.getInstance().mAppToken);
        params.put(NetParams.OPEN_ID, openId);
        params.put("login_type", String.valueOf(loginType));
        HttpUtils.basePost(url, context, params, callback);
    }
}