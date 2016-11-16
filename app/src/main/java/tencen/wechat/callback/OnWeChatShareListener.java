package tencen.wechat.callback;

/**
 * 微博分享回调
 * Created by Administrator on 2015-09-02.
 */
public interface OnWeChatShareListener {
    void onShareCallBack(boolean isSucceed, String message);
}
