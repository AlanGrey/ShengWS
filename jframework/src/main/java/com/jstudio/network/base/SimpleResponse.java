package com.jstudio.network.base;

import java.io.Serializable;

/**
 *
 */
public class SimpleResponse implements Serializable {

    public int code;
    public int status;

    public JsonResponse toJsonResponse() {
        JsonResponse jsonResponse = new JsonResponse();
        jsonResponse.code = code;
        jsonResponse.status = status;
        return jsonResponse;
    }
}