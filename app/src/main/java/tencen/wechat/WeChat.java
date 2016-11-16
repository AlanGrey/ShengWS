package tencen.wechat;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.jstudio.utils.JLog;
import com.jumook.syouhui.R;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXEmojiObject;
import com.tencent.mm.sdk.modelmsg.WXImageObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXMusicObject;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.modelmsg.WXVideoObject;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;
import tencen.wechat.callback.OnAccessTokenListener;
import tencen.wechat.callback.OnCheckOutListener;
import tencen.wechat.callback.OnRefreshTokenListener;
import tencen.wechat.callback.OnWeChatCodeListener;
import tencen.wechat.callback.OnWeChatUserInfoListener;
import tencen.wechat.callback.WeChatShareObserver;
import tencen.wechat.model.WeChatAccessToken;
import tencen.wechat.model.WeChatPay;
import tencen.wechat.model.WeChatUserInfo;
import tencen.wechat.tool.Util;

/**
 * Created by zbb on 2016/7/15.
 */
public class WeChat {

    public static final String TAG = "WeChatUtil";
    public static final int LOGIN = 1;
    public static final int SHARE = 2;
    private static final String ERR_CODE = "errcode";
    private static final String ERR_MSG = "errmsg";

    private static WeChat instance = null;

    public IWXAPI wxApi;
    public String appId;
    public String secret;
    public int state;//当前回调动态
    public OnWeChatCodeListener onWeChatCodeListener;

    public static synchronized WeChat getInstance() {
        if (instance == null) {
            instance = new WeChat();
        }
        return instance;
    }

    private WeChat() {

    }

    /**
     * 微信SDK初始化
     *
     * @param appId 微信appId
     */
    public void initWeChat(Context context, String appId, String secret) {
        this.appId = appId;
        this.secret = secret;
        wxApi = WXAPIFactory.createWXAPI(context, appId, true);
        //将应用的appID注册到微信
        wxApi.registerApp(appId);
    }

    /**
     * 微信登录:获取Code码
     *
     * @param stateStr 描述(随意的字符串,作为返回的唯一标识)
     */
    public void weChatLogin(String stateStr) {
        state = LOGIN;
        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = stateStr;
        wxApi.sendReq(req);
    }


    /**
     * 获取access_token
     *
     * @param code Code值
     */
    public void getAccessToken(final Context context, final String code, final OnAccessTokenListener onAccessTokenListener) {
        Map<String, String> params = new HashMap<>();
        params.put("appid", appId);
        params.put("secret", secret);
        params.put("code", code);
        params.put("grant_type", "authorization_code");
        OkGo.post(WeChatUrl.REFRESH_TOKEN)
                .tag(context)
                .params(params, false)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        JSONObject json = null;
                        try {
                            json = new JSONObject(s);
                            WeChatAccessToken weChatAccessToken = new WeChatAccessToken();
                            weChatAccessToken.setAccessToken(json.getString(WeChatAccessToken.ACCESS_TOKEN));
                            weChatAccessToken.setExpiresIn(json.getInt(WeChatAccessToken.EXPIRES_IN));
                            weChatAccessToken.setRefreshToken(json.getString(WeChatAccessToken.REFRESH_TOKEN));
                            weChatAccessToken.setOpenId(json.getString(WeChatAccessToken.OPENID));
                            weChatAccessToken.setScope(json.getString(WeChatAccessToken.SCOPE));
                            onAccessTokenListener.onAccessTokenSuccess(weChatAccessToken);
                        } catch (JSONException e) {
                            assert json != null;
                            int code = json.optInt(ERR_CODE);
                            String msg = json.optString(ERR_MSG);
                            onAccessTokenListener.onAccessTokenError(code, msg);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        onAccessTokenListener.onAccessTokenError(-1, context.getString(R.string.network_error));
                    }
                });
    }

    /**
     * 重新获取access_token值
     *
     * @param refresh_token 用户刷新access_token
     */
    public void refreshToken(final Context context, String refresh_token, final OnRefreshTokenListener onRefreshTokenListener) {
        Map<String, String> params = new HashMap<>();
        params.put("appid", appId);
        params.put("grant_type", "refresh_token");
        params.put("refresh_token", refresh_token);
        OkGo.post(WeChatUrl.REFRESH_TOKEN)
                .tag(context)
                .params(params, false)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        JSONObject json = null;
                        try {
                            json = new JSONObject(s);
                            WeChatAccessToken weChatAccessToken = new WeChatAccessToken();
                            weChatAccessToken.setAccessToken(json.getString(WeChatAccessToken.ACCESS_TOKEN));
                            weChatAccessToken.setExpiresIn(json.getInt(WeChatAccessToken.EXPIRES_IN));
                            weChatAccessToken.setRefreshToken(json.getString(WeChatAccessToken.REFRESH_TOKEN));
                            weChatAccessToken.setOpenId(json.getString(WeChatAccessToken.OPENID));
                            weChatAccessToken.setScope(json.getString(WeChatAccessToken.SCOPE));
                            onRefreshTokenListener.onRefreshTokenSuccess(weChatAccessToken);
                        } catch (JSONException e) {
                            assert json != null;
                            int code = json.optInt(ERR_CODE);
                            String msg = json.optString(ERR_MSG);
                            onRefreshTokenListener.onRefreshTokenError(code, msg);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        onRefreshTokenListener.onRefreshTokenError(-1, context.getString(R.string.network_error));
                    }
                });
    }

    /**
     * 校验accessToken是否可用
     *
     * @param accessToken accessToken值
     * @param openId      openId值
     */
    public void checkOutAccessToken(final Context context, String accessToken, String openId, final OnCheckOutListener onCheckOutListener) {
        Map<String, String> params = new HashMap<>();
        params.put("access_token", accessToken);
        params.put("openid", openId);
        OkGo.post(WeChatUrl.REFRESH_TOKEN)
                .tag(context)
                .params(params, false)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        JSONObject json = null;
                        try {
                            json = new JSONObject(s);
                            int code = json.optInt(ERR_CODE);
                            String msg = json.optString(ERR_MSG);
                            onCheckOutListener.onCheckOutResult(code, msg);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        onCheckOutListener.onCheckOutResult(-1, context.getString(R.string.network_error));
                    }
                });
    }

    /**
     * 获取用户信息
     *
     * @param accessToken accessToken值
     * @param openId      openId值
     */
    public void getUserInfo(final Context context, String accessToken, String openId, final OnWeChatUserInfoListener onWeChatUserInfoListener) {
        Map<String, String> params = new HashMap<>();
        params.put("access_token", accessToken);
        params.put("openid", openId);
        OkGo.post(WeChatUrl.REFRESH_TOKEN)
                .tag(context)
                .params(params, false)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        JSONObject json = null;
                        try {
                            json = new JSONObject(s);
                            WeChatUserInfo userInfo = new WeChatUserInfo();
                            userInfo.setOpenId(json.getString(WeChatUserInfo.OPENID));
                            userInfo.setNickName(json.getString(WeChatUserInfo.NICKNAME));
                            userInfo.setSex(json.getInt(WeChatUserInfo.SEX));
                            userInfo.setProvince(json.getString(WeChatUserInfo.PROVINCE));
                            userInfo.setCity(json.getString(WeChatUserInfo.CITY));
                            userInfo.setCountry(json.getString(WeChatUserInfo.COUNTRY));
                            userInfo.setHeadImgUrl(json.getString(WeChatUserInfo.HEADIMGURL));
                            userInfo.setUnionId(json.getString(WeChatUserInfo.UNIONID));
                            onWeChatUserInfoListener.onUserInfoSuccess(userInfo);
                        } catch (JSONException e) {
                            assert json != null;
                            int code = json.optInt(ERR_CODE);
                            String msg = json.optString(ERR_MSG);
                            onWeChatUserInfoListener.onUserInfoError(code, msg);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        onWeChatUserInfoListener.onUserInfoError(-1, context.getString(R.string.network_error));
                    }
                });
    }

    public void weChatPay(WeChatPay weChatPay) {
        JLog.d(TAG, "调用微信支付");
        PayReq req = new PayReq();
        req.appId = weChatPay.appId;
        req.partnerId = weChatPay.partnerId;
        req.prepayId = weChatPay.prepayId;
        req.packageValue = weChatPay.packageName;
        req.nonceStr = weChatPay.nonceStr;
        req.timeStamp = weChatPay.timestamp;
        req.sign = weChatPay.sign;
        wxApi.registerApp(appId);
        JLog.d(TAG, "APPID:" + req.appId + "   partnerId:" + req.partnerId + "   prepayId:" + req.prepayId + "   package:" + req.packageValue + "   nonceStr:" + req.nonceStr + "   timeStamp:" + req.timeStamp + "   sign:" + req.sign);
        wxApi.sendReq(req);
    }

    /**
     * 分享文本
     *
     * @param text      文本内容
     * @param sceneType 朋友圈(1):SendMessageToWX.Req.WXSceneTimeline   会话(0): SendMessageToWX.Req.WXSceneSession
     */
    public void shareText(String text, int sceneType) {
        state = SHARE;
        if (TextUtils.isEmpty(text)) {
            WeChatShareObserver.getInstance().post(false, "分享内容不能为空");
            return;
        }
        // 初始化一个WXTextObject对象
        WXTextObject textObj = new WXTextObject();
        textObj.text = text;

        // 用WXTextObject对象初始化一个WXMediaMessage对象
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = textObj;
        // 发送文本类型的消息时，title字段不起作用
        // msg.name = "Will be ignored";
        msg.description = text;

        // 构造一个Req
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("text"); // transaction字段用于唯一标识一个请求
        req.message = msg;
        req.scene = sceneType;//分享朋友圈or分享好友

        // 调用api接口发送数据到微信
        wxApi.sendReq(req);
    }

    /**
     * 分享图片(根据Bitmap)
     *
     * @param bmp       Bitmap
     * @param dstWidth  缩略图的宽(建议150)
     * @param dstHeight 缩略图的高(建议150)
     * @param sceneType 分享的目的地  朋友圈(1):SendMessageToWX.Req.WXSceneTimeline   会话(0): SendMessageToWX.Req.WXSceneSession
     */
    public void shareImageBitmap(Bitmap bmp, int dstWidth, int dstHeight, int sceneType) {
        state = SHARE;
        if (bmp == null) {
            WeChatShareObserver.getInstance().post(false, "图片资源不存在");
            return;
        }
        WXImageObject imgObj = new WXImageObject(bmp);

        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = imgObj;
        Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, dstWidth, dstHeight, true);
        bmp.recycle();
        msg.thumbData = Util.bmpToByteArray(thumbBmp, true);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("img");
        req.message = msg;
        req.scene = sceneType;
        wxApi.sendReq(req);
    }

    /**
     * 分享图片(根据路径)
     *
     * @param path      图片路径
     * @param dstWidth  缩略图的宽(建议150)
     * @param dstHeight 缩略图的高(建议150)
     * @param sceneType 分享的目的地  朋友圈(1):SendMessageToWX.Req.WXSceneTimeline   会话(0): SendMessageToWX.Req.WXSceneSession
     */
    public void shareImagePath(String path, int dstWidth, int dstHeight, int sceneType) {
        state = SHARE;
        if (TextUtils.isEmpty(path)) {
            WeChatShareObserver.getInstance().post(false, "图片地址不存在");
            return;
        }
        WXImageObject imgObj = new WXImageObject();
        imgObj.setImagePath(path);

        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = imgObj;
        Bitmap bmp = BitmapFactory.decodeFile(path);
        Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, dstWidth, dstHeight, true);
        bmp.recycle();
        msg.thumbData = Util.bmpToByteArray(thumbBmp, true);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("img");
        req.message = msg;
        req.scene = sceneType;
        wxApi.sendReq(req);
    }

    /**
     * 分享图片(根据网络地址)
     *
     * @param url       网络地址
     * @param dstWidth  缩略图的宽(建议150)
     * @param dstHeight 缩略图的高(建议150)
     * @param sceneType 分享的目的地  朋友圈(1):SendMessageToWX.Req.WXSceneTimeline   会话(0): SendMessageToWX.Req.WXSceneSession
     */
    public void shareImageUrl(String url, int dstWidth, int dstHeight, int sceneType) {
        state = SHARE;
        if (TextUtils.isEmpty(url)) {
            WeChatShareObserver.getInstance().post(false, "图片地址不存在");
            return;
        }
        try {
            WXImageObject imgObj = new WXImageObject();
            imgObj.imagePath = url;

            WXMediaMessage msg = new WXMediaMessage();
            msg.mediaObject = imgObj;

            Bitmap bmp = BitmapFactory.decodeStream(new URL(url).openStream());
            Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, dstWidth, dstHeight, true);
            bmp.recycle();
            msg.thumbData = Util.bmpToByteArray(thumbBmp, true);

            SendMessageToWX.Req req = new SendMessageToWX.Req();
            req.transaction = buildTransaction("img");
            req.message = msg;
            req.scene = sceneType;
            wxApi.sendReq(req);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 分享音乐
     *
     * @param musicUrl    音乐路径
     * @param title       标题
     * @param description 描述
     * @param bmp         标志
     * @param sceneType   分享的目的地  朋友圈(1):SendMessageToWX.Req.WXSceneTimeline   会话(0): SendMessageToWX.Req.WXSceneSession
     */
    public void shareMusicUrl(String musicUrl, String title, String description, Bitmap bmp, int sceneType) {
        state = SHARE;
        if (TextUtils.isEmpty(musicUrl)) {
            WeChatShareObserver.getInstance().post(false, "音乐地址不存在");
            return;
        }
        WXMusicObject music = new WXMusicObject();
        music.musicUrl = musicUrl;

        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = music;
        msg.title = title;
        msg.description = description;

        Bitmap thumb = Bitmap.createScaledBitmap(bmp, 150, 150, true);
        msg.thumbData = Util.bmpToByteArray(thumb, true);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("music");
        req.message = msg;
        req.scene = sceneType;
        wxApi.sendReq(req);

    }

    /**
     * 分享视频
     *
     * @param videoUrl    分享视频URl
     * @param title       标题
     * @param description 描述
     * @param bmp         标志
     * @param sceneType   分享的目的地  朋友圈(1):SendMessageToWX.Req.WXSceneTimeline   会话(0): SendMessageToWX.Req.WXSceneSession
     */
    public void shareVideoUrl(String videoUrl, String title, String description, Bitmap bmp, int sceneType) {
        state = SHARE;
        if (TextUtils.isEmpty(videoUrl)) {
            WeChatShareObserver.getInstance().post(false, "视频地址不存在");
            return;
        }
        WXVideoObject video = new WXVideoObject();
        video.videoUrl = videoUrl;

        WXMediaMessage msg = new WXMediaMessage(video);
        msg.title = title;
        msg.description = description;
        Bitmap thumb = Bitmap.createScaledBitmap(bmp, 150, 150, true);
        msg.thumbData = Util.bmpToByteArray(thumb, true);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("video");
        req.message = msg;
        req.scene = sceneType;
        wxApi.sendReq(req);
    }

    /**
     * 分享链接
     *
     * @param url         链接地址
     * @param title       标题
     * @param description 描述
     * @param bmp         标志
     * @param sceneType   分享的目的地  朋友圈(1):SendMessageToWX.Req.WXSceneTimeline   会话(0): SendMessageToWX.Req.WXSceneSession
     */
    public void shareBitmapWebage(String url, String title, String description, Bitmap bmp, int sceneType) {
        state = SHARE;
        if (TextUtils.isEmpty(url)) {
            WeChatShareObserver.getInstance().post(false, "网络地址不存在");
            return;
        }
        if (bmp == null) {
            WeChatShareObserver.getInstance().post(false, "图片资源不存在");
            return;
        }
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = url;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = title;
        msg.description = description;
//        Bitmap thumb = Bitmap.createScaledBitmap(bmp, 100, 100, true);
        msg.thumbData = Util.bmpToByteArray(bmp, false);
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = msg;
        req.scene = sceneType;
        wxApi.sendReq(req);
    }

    /**
     * 分享表情
     *
     * @param path        表情的路径
     * @param title       消息标题
     * @param description 消息描述
     * @param sceneType   分享的目的地  朋友圈(1):SendMessageToWX.Req.WXSceneTimeline   会话(0): SendMessageToWX.Req.WXSceneSession
     */
    public void shareEmoji(String path, String title, String description, int sceneType) {
        state = SHARE;
        if (TextUtils.isEmpty(path)) {
            WeChatShareObserver.getInstance().post(false, "图片路径不存在");
            return;
        }
        try {
            FileInputStream fileIn = new FileInputStream(new File(path));
            int length = fileIn.available();
            byte[] buffer = new byte[length];
            fileIn.read(buffer);
            fileIn.close();
            // 初始化一个WXEmojiObject对象
            WXEmojiObject emoji = new WXEmojiObject();
            emoji.emojiData = buffer;

            // 用WXEmojiObject对象初始化一个WXMediaMessage对象
            WXMediaMessage msg = new WXMediaMessage(emoji);
            msg.title = title;
            msg.description = description;
            msg.thumbData = buffer;

            // 构造一个Req
            SendMessageToWX.Req req = new SendMessageToWX.Req();
            req.message = msg;
            req.transaction = buildTransaction("emoji");
            req.scene = sceneType;
            wxApi.sendReq(req);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断微信是否安装
     *
     * @param context
     * @return
     */
    public boolean isWeChatAvilible(Context context) {
        final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mm")) {
                    return true;
                }
            }
        }
        return false;
    }

    public void setOnWeChatCodeListener(OnWeChatCodeListener onWeChatCodeListener) {
        this.onWeChatCodeListener = onWeChatCodeListener;
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

//    AsyncRequestCreator.getClient().post(context, WeChatUrl.REFRESH_TOKEN, new RequestParams(params), new TextHttpResponseHandler() {
//        @Override
//        public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
//    onRefreshTokenListener.onRefreshTokenError(-1, context.getString(R.string.network_error));
//        }
//
//        @Override
//        public void onSuccess(int i, Header[] headers, String s) {
//        }
//    }
//    );

}
