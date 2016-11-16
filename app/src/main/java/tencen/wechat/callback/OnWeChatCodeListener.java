package tencen.wechat.callback;

/**
 * AccessToken回调
 * Created by Administrator on 2015-08-31.
 */
public interface OnWeChatCodeListener {

    void onWeChatCodeCallBack(boolean isSucceed, String code);

}
