package tencen.wechat.model;

import org.json.JSONObject;

/**
 * 微信支付
 * Created by Administrator on 2015-11-17.
 */
public class WeChatPay {

    public static final String TAG = "WeChatPay";

    public static final String WXPARAMETER = "wxParameters";
    public static final String APP_ID = "appid";
    public static final String PARTNER_ID = "mch_id";
    public static final String PREPAY_ID = "prepay_id";
    public static final String PACKAGE = "package";
    public static final String NONCE_STR = "nonce_str";
    public static final String TIMESTAMP = "timeStamp";
    public static final String SIGN = "sign";

    public String appId;
    public String partnerId;
    public String prepayId;
    public String packageName;
    public String nonceStr;
    public String timestamp;
    public String sign;

    public static WeChatPay getItem(JSONObject object) {
        WeChatPay pay = new WeChatPay();
        pay.appId = object.optString(APP_ID);
        pay.partnerId = object.optString(PARTNER_ID);
        pay.prepayId = object.optString(PREPAY_ID);
        pay.packageName = "Sign=WXPay";
        pay.nonceStr = object.optString(NONCE_STR);
        pay.timestamp = object.optString(TIMESTAMP);
        pay.sign = object.optString(SIGN);
        return pay;
    }
}
