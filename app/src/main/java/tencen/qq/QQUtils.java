package tencen.qq;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;

import com.jstudio.utils.JLog;
import com.tencent.connect.UserInfo;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import tencen.qq.callback.OnQQLoginListener;
import tencen.qq.callback.OnQQReAuthListener;
import tencen.qq.callback.OnQQShareListener;
import tencen.qq.callback.OnQQUserInfoListener;
import tencen.qq.model.QQAccessToken;
import tencen.qq.model.QQUserInfo;

/**
 * QQ登录、分享工具
 * Created by zbb on 2016/7/15.
 */
public class QQUtils {

    public static final String SCOPE_ALL = "all";
    public static final String SCOPE_USER_INFO = "get_user_info";
    public static final String TAG = "QQUtil";

    private static QQUtils instance = null;

    public static Tencent mTencent;

    public String appId;
    public UserInfo userInfo;//tencentSDK自带
    public IUiListener iUiListener;

    /**
     * 单例模式初始化
     *
     * @return instance
     */
    public static synchronized QQUtils getInstance() {
        if (instance == null) {
            instance = new QQUtils();
        }
        return instance;
    }

    private QQUtils() {

    }

    /**
     * QQ SDK初始化
     *
     * @param appId 企鹅号 appId
     */
    public void initQQ(Context context, String appId) {
        this.appId = appId;
        if (mTencent == null) {
            mTencent = Tencent.createInstance(appId, context);
        }
    }

    /**
     * QQ 授权登录
     *
     * @param scope 应用需要获得哪些接口的权限，由“,”分隔，例如：SCOPE = “get_user_info,add_topic”；如果需要所有权限则使用"all"。
     */
    public void qqLogin(final Activity activity, String scope, final OnQQLoginListener onQQLoginListener) {
        if (!mTencent.isSessionValid()) {
            mTencent.login(activity, scope, iUiListener = new IUiListener() {
                @Override
                public void onComplete(Object obj) {
                    QQAccessToken accessToken = new QQAccessToken();
                    if (obj == null) {
                        return;
                    }
                    JLog.d(TAG, obj.toString());
                    JSONObject json = (JSONObject) obj;
                    accessToken.setRet(json.optInt(QQAccessToken.RET));
                    accessToken.setPayToken(json.optString(QQAccessToken.PAY_TOKEN));
                    accessToken.setPf(json.optString(QQAccessToken.PF));
                    accessToken.setExpiresIn(json.optString(QQAccessToken.EXPIRES_IN));
                    accessToken.setOpenId(json.optString(QQAccessToken.OPENID));
                    accessToken.setPfKey(json.optString(QQAccessToken.PFKEY));
                    accessToken.setMsg(json.optString(QQAccessToken.MSG));
                    accessToken.setAccessToken(json.optString(QQAccessToken.ACCESS_TOKEN));
                    mTencent.setAccessToken(accessToken.getAccessToken(), accessToken.getExpiresIn());
                    mTencent.setOpenId(accessToken.getOpenId());
                    onQQLoginListener.onComplete(accessToken);
                }

                @Override
                public void onError(UiError uiError) {
                    onQQLoginListener.onError(uiError);
                }

                @Override
                public void onCancel() {
                    onQQLoginListener.onCancel();
                }
            });
        }
    }

    /**
     * QQ注销登录
     */
    public void qqLoginOut(Activity activity) {
        mTencent.logout(activity);
    }

    /**
     * 返回码为100030时，用户可以增量授权
     *
     * @param scope 应用需要获得哪些API的权限，由“，”分隔。
     *              例如：SCOPE = “get_user_info,add_t”；
     */
    public void qqReAuth(Activity activity, String scope, final OnQQReAuthListener onQQReAuthListener) {
        mTencent.reAuth(activity, scope, new IUiListener() {
            @Override
            public void onComplete(Object obj) {
                QQAccessToken accessToken = new QQAccessToken();
                if (obj == null) {
                    return;
                }
                JSONObject json = (JSONObject) obj;
                accessToken.setRet(json.optInt(QQAccessToken.RET));
                accessToken.setPayToken(json.optString(QQAccessToken.PAY_TOKEN));
                accessToken.setPf(json.optString(QQAccessToken.PF));
                accessToken.setExpiresIn(json.optString(QQAccessToken.EXPIRES_IN));
                accessToken.setOpenId(json.optString(QQAccessToken.OPENID));
                accessToken.setPfKey(json.optString(QQAccessToken.PFKEY));
                accessToken.setMsg(json.optString(QQAccessToken.MSG));
                accessToken.setAccessToken(json.optString(QQAccessToken.ACCESS_TOKEN));
                onQQReAuthListener.onComplete(accessToken);
            }

            @Override
            public void onError(UiError uiError) {
                onQQReAuthListener.onError(uiError);
            }

            @Override
            public void onCancel() {
                onQQReAuthListener.onCancel();
            }
        });
    }

    /**
     * 获取用户QQ信息
     */
    public void getQQUserInfo(Context context, final OnQQUserInfoListener onQQUserInfoListener) {
        if (mTencent.getQQToken() == null) {
            return;
        }
        userInfo = new UserInfo(context, mTencent.getQQToken());
        userInfo.getUserInfo(new IUiListener() {
            @Override
            public void onComplete(Object obj) {
                QQUserInfo userInfo = new QQUserInfo();
                if (obj == null) {
                    return;
                }
                JLog.d(TAG, obj.toString());
                JSONObject json = (JSONObject) obj;
                userInfo.setRet(json.optInt(QQUserInfo.RET));
                userInfo.setMsg(json.optString(QQUserInfo.MSG));
                userInfo.setIsYellowVip(json.optString(QQUserInfo.IS_YELLOW_VIP));
                userInfo.setIsYellowYearVip(json.optString(QQUserInfo.IS_YELLOW_YEAR_VIP));
                userInfo.setYellowVipLevel(json.optString(QQUserInfo.YELLOW_VIP_LEVEL));
                userInfo.setVip(json.optString(QQUserInfo.VIP));
                userInfo.setLevel(json.optString(QQUserInfo.LEVEL));
                userInfo.setNickName(json.optString(QQUserInfo.NICKNAME));
                userInfo.setGender(json.optString(QQUserInfo.GENDER));
                userInfo.setFigureurl(json.optString(QQUserInfo.FIGUREURL));
                userInfo.setFigureurl1(json.optString(QQUserInfo.FIGUREURL_1));
                userInfo.setFigureurl2(json.optString(QQUserInfo.FIGUREURL_2));
                userInfo.setFigureurlQQ1(json.optString(QQUserInfo.FIGUREURL_QQ_1));
                userInfo.setFigureurlQQ2(json.optString(QQUserInfo.FIGUREURL_QQ_2));
                onQQUserInfoListener.onComplete(userInfo);
            }

            @Override
            public void onError(UiError uiError) {
                onQQUserInfoListener.onError(uiError);
            }

            @Override
            public void onCancel() {
                onQQUserInfoListener.onCancel();
            }
        });
    }

    /**
     * 图文分享
     *
     * @param title     分享的标题, 最长30个字符。（必填）
     * @param summary   分享的标题, 最长30个字符。
     * @param targetUrl 这条分享消息被好友点击后的跳转URL。（必填）
     * @param imageUrl  分享图片的URL或者本地路径
     * @param appName   手Q客户端顶部，替换“返回”按钮文字，如果为空，用返回代替
     * @param extInt    分享额外选项，两种类型可选（默认是不隐藏分享到QZone按钮且不自动打开分享到QZone的对话框）：
     *                  QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN = 1，分享时自动打开分享到QZone的对话框。
     *                  QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE = 2，分享时隐藏分享到QZone按钮.
     *                  默认 = 0，则不填。
     */
    public void shareToQQImageText(Activity activity, String title, String summary, String targetUrl, String imageUrl, String appName, int extInt, final OnQQShareListener onQQShareListener) {
        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(targetUrl)) {
            return;
        }
        Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, title);
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, targetUrl);
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, summary);
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, imageUrl);
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, appName);
        if (extInt != 0) {
            params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, extInt);
        }
        mTencent.shareToQQ(activity, params, new IUiListener() {
            @Override
            public void onComplete(Object obj) {
                onQQShareListener.onComplete(obj);
            }

            @Override
            public void onError(UiError uiError) {
                onQQShareListener.onError(uiError);
            }

            @Override
            public void onCancel() {
                onQQShareListener.onCancel();
            }
        });
    }

    /**
     * 分享纯图片
     *
     * @param imageUrl 需要分享的本地图片路径。(只支持本地图片)
     * @param appName  手Q客户端顶部，替换“返回”按钮文字，如果为空，用返回代替。
     * @param extInt   分享额外选项，两种类型可选（默认是不隐藏分享到QZone按钮且不自动打开分享到QZone的对话框）：
     *                 QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN = 1，分享时自动打开分享到QZone的对话框。
     *                 QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE = 2，分享时隐藏分享到QZone按钮.
     *                 默认 = 0，则不填。
     */
    public void shareToQQImage(Activity activity, String imageUrl, String appName, int extInt, final OnQQShareListener onQQShareListener) {
        if (TextUtils.isEmpty(imageUrl)) {
            return;
        }
        Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, imageUrl);
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, appName);
        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, extInt);
        mTencent.shareToQQ(activity, params, new IUiListener() {
            @Override
            public void onComplete(Object obj) {
                onQQShareListener.onComplete(obj);
            }

            @Override
            public void onError(UiError uiError) {
                onQQShareListener.onError(uiError);
            }

            @Override
            public void onCancel() {
                onQQShareListener.onCancel();
            }
        });
    }

    /**
     * 分享音乐
     *
     * @param title     分享的标题, 最长30个字符。（必选）
     * @param summary   分享的消息摘要，最长40个字符。
     * @param targetUrl 这条分享消息被好友点击后的跳转URL。（必选）
     * @param imageUrl  分享图片的URL或者本地路径。
     * @param audioUrl  音乐文件的远程链接, 以URL的形式传入, 不支持本地音乐。（必填）
     * @param appName   手Q客户端顶部，替换“返回”按钮文字，如果为空，用返回代替。
     * @param extInt    分享额外选项，两种类型可选（默认是不隐藏分享到QZone按钮且不自动打开分享到QZone的对话框）：
     *                  QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN = 1，分享时自动打开分享到QZone的对话框。
     *                  QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE = 2，分享时隐藏分享到QZone按钮.
     *                  默认 = 0，则不填。
     */
    public void shareToQQMusic(Activity activity, String title, String summary, String targetUrl, String imageUrl, String audioUrl, String appName, int extInt, final OnQQShareListener onQQShareListener) {
        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(targetUrl) || TextUtils.isEmpty(audioUrl)) {
            return;
        }
        Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_AUDIO);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, title);
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, targetUrl);
        params.putString(QQShare.SHARE_TO_QQ_AUDIO_URL, audioUrl);
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, summary);
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, imageUrl);
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, appName);
        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, extInt);
        mTencent.shareToQQ(activity, params, new IUiListener() {
            @Override
            public void onComplete(Object obj) {
                onQQShareListener.onComplete(obj);
            }

            @Override
            public void onError(UiError uiError) {
                onQQShareListener.onError(uiError);
            }

            @Override
            public void onCancel() {
                onQQShareListener.onCancel();
            }
        });
    }

    /**
     * 分享到QQ空间
     *
     * @param title     分享的标题，最多200个字符。(必选)
     * @param summary   分享的摘要，最多600字符。
     * @param targetUrl 需要跳转的链接，URL字符串。（必选）
     * @param imageList 分享的图片, 以ArrayList<String>的类型传入，以便支持多张图片（注：图片最多支持9张图片，多余的图片会被丢弃）。
     */
    public void shareToQzone(Activity activity, String title, String summary, String targetUrl, ArrayList<String> imageList, final OnQQShareListener onQQShareListener) {
        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(targetUrl)) {
            return;
        }
        Bundle params = new Bundle();
        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, title);
        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, targetUrl);
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, summary);
        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageList);
        mTencent.shareToQzone(activity, params, new IUiListener() {
            @Override
            public void onComplete(Object obj) {
                onQQShareListener.onComplete(obj);
            }

            @Override
            public void onError(UiError uiError) {
                onQQShareListener.onError(uiError);
            }

            @Override
            public void onCancel() {
                onQQShareListener.onCancel();
            }
        });
    }

    /**
     * 判断qq是否可用
     *
     * @param context 文本
     * @return true , false
     */
    public boolean isQQClientAvailable(Context context) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mobileqq")) {
                    return true;
                }
            }
        }
        return false;
    }

}
