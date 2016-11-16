package tencen.wechat.callback;

import java.util.ArrayList;
import java.util.List;

/**
 * 分享
 * Created by Administrator on 2015-11-10.
 */
public class WeChatShareObserver {

    private static WeChatShareObserver INSTANCE;
    /**
     * 用于存放已经注册的订阅者
     */
    private List<OnWeChatShareListener> mList;

    private WeChatShareObserver() {

    }

    public static WeChatShareObserver getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new WeChatShareObserver();
        }
        return INSTANCE;
    }

    /**
     * 添加订阅者者到队列，订阅者注册时需要将自己添加到队列中管理
     *
     * @param observer PushObserver
     */
    public void addSubscriber(OnWeChatShareListener observer) {
        if (mList == null) {
            mList = new ArrayList<>();
        }
        mList.add(observer);
    }

    /**
     * 从队列移除订阅者，订阅者反注册时调用此方法
     *
     * @param observer PushObserver
     */
    public void removeSubscriber(OnWeChatShareListener observer) {
        if (mList == null) {
            return;
        }
        if (mList.contains(observer)) {
            mList.remove(observer);
        }
    }

    /**
     * 回调
     */
    public void post(boolean isSucceed, String message) {
        if (mList != null && mList.size() > 0) {
            for (OnWeChatShareListener listener : mList) {
                listener.onShareCallBack(isSucceed, message);
            }
        }
    }


}
