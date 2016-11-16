package com.jumook.syouhui.network.callback;

import com.jstudio.utils.JLog;
import com.jumook.syouhui.network.ResponseResult;
import com.lzy.okgo.callback.StringCallback;

import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by jumook on 2016/11/10.
 */

public abstract class JsonStringBack extends StringCallback {

    public abstract void onJsonSuccess(JSONObject data);
    public abstract void onJsonError(String errorMsg);

    @Override
    public void onSuccess(String s, Call call, Response response) {
        JLog.d("JsonStringBack", s);
        ResponseResult responseResult = new ResponseResult(s);
        if (responseResult.isReqSuccess()) {
            onJsonSuccess(responseResult.getData());
        } else {
            onJsonError(responseResult.getErrorMsg());
        }
    }

    @Override
    public void onError(Call call, Response response, Exception e) {
        JLog.e("JsonStringBack", e.toString());
        onJsonError(e.toString());
    }

}
