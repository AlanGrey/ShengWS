package com.jstudio.network;

import android.app.Application;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.cookie.store.MemoryCookieStore;

/**
 * Created by jumook on 2016/11/4.
 */

public class OkGoUtils {

    public static final int CONNECT_TIME = 15 * 1000;

    public static void initWithConfig(Application app) {
        OkGo.init(app);
        OkGo.getInstance()
                //打开该调试开关,控制台会使用 红色error 级别打印log,并不是错误,是为了显眼,不需要就不要加入该行
//                .debug("OkGo")
                //如果使用默认的 60秒,以下三行也不需要传
                .setConnectTimeout(CONNECT_TIME)  //全局的连接超时时间
                .setReadTimeOut(CONNECT_TIME)     //全局的读取超时时间
                .setWriteTimeOut(CONNECT_TIME)    //全局的写入超时时间
                //可以全局统一设置缓存模式,默认是不使用缓存,可以不传
                .setCacheMode(CacheMode.NO_CACHE)
                //可以全局统一设置缓存时间,默认永不过期,具体使用方法看 github 介绍
                .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)
                //cookie使用内存缓存（app退出后，cookie消失）
                .setCookieStore(new MemoryCookieStore());
    }
}
