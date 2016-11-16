package com.jumook.syouhui.network;

import android.text.TextUtils;

import org.json.JSONObject;

public class ResponseResult {

    private String response;
    private JSONObject jsonObject;

    public ResponseResult(String response) {
        this.response = response;
    }

    public boolean isReqSuccess() {
        if (TextUtils.isEmpty(response)) {
            return false;
        }
        try {
            jsonObject = new JSONObject(response);
            int status = jsonObject.optInt("status");
            return status == 1;
        } catch (Exception e) {
            return false;
        }
    }

    public JSONObject getData() {
        return jsonObject.optJSONObject("data");
    }

    public int getCode() {
        return jsonObject.optInt("code");
    }

    public String getErrorMsg() {
        JSONObject data = jsonObject.optJSONObject("data");
        return data.optString("msg");
    }

}
