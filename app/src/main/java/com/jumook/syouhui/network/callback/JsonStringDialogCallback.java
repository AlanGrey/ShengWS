package com.jumook.syouhui.network.callback;

import android.app.Activity;
import android.app.ProgressDialog;
import android.support.annotation.Nullable;
import android.view.Window;

import com.jstudio.utils.JLog;
import com.jumook.syouhui.network.ResponseResult;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;

import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Response;

public abstract class JsonStringDialogCallback extends StringCallback {

    private ProgressDialog dialog;

    public abstract void onJsonSuccess(JSONObject data);

    public abstract void onJsonError(String errorMsg);

    public JsonStringDialogCallback(Activity activity, String message) {
        dialog = new ProgressDialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage(message);
    }

    @Override
    public void onBefore(BaseRequest request) {
        super.onBefore(request);
        //网络请求前显示对话框
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }
    }

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


    @Override
    public void onAfter(@Nullable String s, @Nullable Exception e) {
        super.onAfter(s, e);
        //网络请求结束后关闭对话框
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
