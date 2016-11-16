package tencen.wechat.callback;

/**
 * 微信支付回调接口
 * Created by Administrator on 2015-11-20.
 */
public interface OnWeChatPayListener {
    void onPayCallBack(boolean isSucceed, String error);
}
