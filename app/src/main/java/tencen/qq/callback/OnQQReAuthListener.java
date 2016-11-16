package tencen.qq.callback;

import com.tencent.tauth.UiError;

import tencen.qq.model.QQAccessToken;

/**
 * 增量授权
 * Created by Administrator on 2015-09-01.
 */
public interface OnQQReAuthListener {
    void onComplete(QQAccessToken accessToken);

    void onError(UiError uiError);

    void onCancel();
}
