package com.jstudio.network.callback;

import com.google.gson.stream.JsonReader;
import com.jstudio.network.base.ErrorResponse;
import com.jstudio.network.base.JsonResponse;
import com.jstudio.network.base.SimpleResponse;
import com.jstudio.utils.GsonConvert;
import com.lzy.okgo.convert.Converter;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Response;

public class JsonConvert<T> implements Converter<T> {

    private Type type;

    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public T convertSuccess(Response response) throws Exception {
        JsonReader jsonReader = new JsonReader(response.body().charStream());

        if (type == null) {
            //以下代码是通过泛型解析实际参数,泛型必须传
            Type genType = getClass().getGenericSuperclass();
            Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
            type = params[0];
        }
        if (!(type instanceof ParameterizedType)) throw new IllegalStateException("没有填写泛型参数");
        Type rawType = ((ParameterizedType) type).getRawType();

        //无数据类型
        if (rawType == Void.class) {
            SimpleResponse baseWbgResponse = GsonConvert.fromJson(jsonReader, SimpleResponse.class);
            //noinspection unchecked
            return (T) baseWbgResponse.toJsonResponse();
        }

        //有数据类型
        if (rawType == JsonResponse.class) {
            JsonResponse jsonResponse = GsonConvert.fromJson(jsonReader, type);
            int status = jsonResponse.status;
            if (status == 1) {
                //noinspection unchecked
                return (T) jsonResponse;
            } else {
                JsonResponse errorResponse = GsonConvert.fromJson(jsonReader, ErrorResponse.class);
                throw new IllegalStateException("错误代码：" + jsonResponse.code + "，错误信息：" + errorResponse.data);
            }
        }
        throw new IllegalStateException("基类错误无法解析!");
    }
}