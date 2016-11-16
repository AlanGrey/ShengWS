package com.jstudio.thirdparty;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;

import com.jstudio.exception.JFWException;
import com.jstudio.utils.IntentHelper;
import com.jstudio.utils.PackageUtils;

import java.io.File;

/**
 * Created by Jason on 2/2/16.
 */
public class SpecialIntent {

    /**
     * 分享图片给微信朋友
     *
     * @param activity Activity
     * @param file     图片的文件
     */
    public static void shareToFriend(Activity activity, File file) throws JFWException {
        String weChatPkgName = "com.tencent.mm";
        if (!PackageUtils.isAppInstalled(activity, weChatPkgName)) {
            throw new JFWException();
        }
        Intent intent = new Intent();
        ComponentName componentName = new ComponentName(weChatPkgName, "com.tencent.mm.ui.tools.ShareImgUI");
        intent.setComponent(componentName);
        intent.setAction(Intent.ACTION_SEND);
        intent.setType(IntentHelper.IMAGE_UNSPECIFIED);
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        activity.startActivity(intent);
    }

    /**
     * 分享图片到微信朋友圈
     *
     * @param activity Activity
     * @param file     图片的文件
     */
    public void shareToTimeLine(Activity activity, File file) throws JFWException {
        String weChatPkgName = "com.tencent.mm";
        if (!PackageUtils.isAppInstalled(activity, weChatPkgName)) {
            throw new JFWException();
        }
        Intent intent = new Intent();
        ComponentName componentName = new ComponentName(weChatPkgName, "com.tencent.mm.ui.tools.ShareToTimeLineUI");
        intent.setComponent(componentName);
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        intent.setType(IntentHelper.IMAGE_UNSPECIFIED);
        activity.startActivity(intent);
    }

    /**
     * 使用高得客户端现实路线规划
     * http://lbs.amap.com/api/uri-api/android-uri-explain/
     */
    public static void routePlanning() {
//        act=android.intent.action.VIEW
//        cat=android.intent.category.DEFAULT
//        dat=androidamap://route?sourceApplication=softname&slat=36.2&slon=116.1&sname=abc&dlat=36.3&dlon=116.2&dname=def&dev=0&m=0&t=1
//        pkg=com.autonavi.minimap
    }
}
