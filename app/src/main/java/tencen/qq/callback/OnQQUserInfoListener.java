package tencen.qq.callback;

import com.tencent.tauth.UiError;

import tencen.qq.model.QQUserInfo;

/**
 * QQ用户信息
 * Created by Administrator on 2015-09-01.
 */
public interface OnQQUserInfoListener {
    void onComplete(QQUserInfo userInfo);

    void onError(UiError uiError);

    void onCancel();


}
