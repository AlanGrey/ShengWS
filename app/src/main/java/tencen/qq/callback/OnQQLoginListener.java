package tencen.qq.callback;

import com.tencent.tauth.UiError;

import tencen.qq.model.QQAccessToken;

/**
 * QQ授权登录回调
 * Created by Administrator on 2015-08-31.
 */
public interface OnQQLoginListener {
    void onComplete(QQAccessToken accessToken);

    void onError(UiError uiError);

    void onCancel();
}
