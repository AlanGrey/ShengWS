package com.jstudio.network.base;

import java.io.Serializable;

public class JsonResponse<T> implements Serializable {
    public int code;
    public int status;
    public T data;
}
