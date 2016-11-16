package com.jumook.syouhui.constants.ip;

/**
 * 登录、注册、验证码、AppToken 等接口
 * Created by jumook on 2016/11/10.
 */

public class LoginUrls {


    //加载界面-广告
    public static final String GET_AD_INFORMATION = "adver/qidongAd";

    //获取AppToken接口
    public static final String GET_APP_TOKEN = "application/getAppToken";

    //校验手机注册状态
    public static final String CHECK_PHONE_STATE = "application/checkUserMobile";

    //获取手机验证码
    public static final String GET_PHONE_CODE = "user/sendUserMobileCode";

    //用户注册
    public static final String REGISTER_USER = "user/bindUserMobile";

    //获取病情种类
    public static final String GET_ILLNESS_TYPE = "user/illnessSelect";

    //提交用户注册信息
    public static final String POST_REGISTER_USER_INFO = "user/register";

    //修改用户密码
    public static final String CHANGE_PASSWORD = "user/sendUserMobileCode";

    //登录接口
    public static final String USER_LOGIN = "user/login";


}
