package com.jstudio.utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;

import java.io.File;

/**
 * Created by Jason
 */
public class IntentHelper {

    public static final String IMAGE_UNSPECIFIED = "image/*";
    public static final String VIDEO_UNSPECIFIED = "video/*";
    public static final String AUDIO_UNSPECIFIED = "audio/*";
    public static final String TEXT_UNSPECIFIED = "text/*";
    public static final String EMAIL_BODY = "plain/text";
    public static final String SEND_SMS = "vnd.android-dir/mms-sms";

    /**
     * 获取图片的requestCode
     */
    public static final int PICK_PHOTO = 100;
    /**
     * 拍摄图片的requestCode
     */
    public static final int TAKE_PHOTO = 200;

    /**
     * 拨打电话
     *
     * @param context  Context
     * @param phoneNum 电话号码
     */
    @SuppressWarnings("MissingPermission")
    public static void callPhoneNum(Context context, String phoneNum) {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNum));
        context.startActivity(intent);
    }

    /**
     * 发送短信
     *
     * @param activity    Activity
     * @param receiver    接收者的电话号码
     * @param body        短信内容
     * @param requestCode RequestCode
     */
    private static void sendSMS(Activity activity, String receiver, String body, int requestCode) {
        Uri smsToUri = Uri.parse("smsto:");
        Intent sendIntent = new Intent(Intent.ACTION_VIEW, smsToUri);
        sendIntent.putExtra("address", receiver);
        sendIntent.putExtra("sms_body", body);
        sendIntent.setType(SEND_SMS);
        activity.startActivityForResult(sendIntent, requestCode);
    }

    /**
     * 用浏览器打开指定的连接
     *
     * @param context Context
     * @param url     连接地址，包含scheme，如http://
     */
    public static void openWebPage(Context context, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        context.startActivity(intent);
    }

    /**
     * 打开地图并现实指定的经纬度
     *
     * @param activity Activity
     * @param lat      经度
     * @param lng      纬度
     */
    public static void showMapLocation(Activity activity, double lat, double lng) {
        Uri uri = Uri.parse("geo:" + lat + "," + lng);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        activity.startActivity(intent);
    }

    /**
     * 从图库中选择图片，在onActivityResult中通过Uri uri = data.getData()获得
     *
     * @param activity Activity
     */
    public static void pickPhotoFromGallery(Activity activity) {
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activity.startActivityForResult(intent, PICK_PHOTO);
            } else {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
                intent.setType(IMAGE_UNSPECIFIED);
                activity.startActivityForResult(intent, PICK_PHOTO);
            }
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 启动相机并存储照片
     *
     * @param activity Activity
     * @param dir      保存图片的文件夹，最后包含“/”
     * @param filename 保存图片的文件名
     * @return true表示相机打开成功否则返回false
     */
    public static boolean takePhoto(Activity activity, String dir, String filename) {
        String filePath = dir + filename;
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File cameraDir = new File(dir);
        if (!cameraDir.exists()) {
            return false;
        }

        File file = new File(filePath);
        Uri outputFileUri = Uri.fromFile(file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        try {
            activity.startActivityForResult(intent, TAKE_PHOTO);
        } catch (ActivityNotFoundException e) {
            return false;
        }
        return true;
    }

    /**
     * 打开GPS设置界面
     *
     * @param context Context
     */
    public static void openGPSSetting(Context context) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            intent.setAction(Settings.ACTION_SETTINGS);
            try {
                context.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 打开邮箱客户端并发送邮件
     *
     * @param context Context
     * @param email   邮箱地址
     * @param subject 接收者
     * @param content 邮件内容
     */
    public static void sendEmail(Context context, String email, String subject, String content) {
        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType(EMAIL_BODY);
            String address[] = new String[]{email};
            if (!TextUtils.isEmpty(subject)) {
                intent.putExtra(Intent.EXTRA_SUBJECT, subject);
            }
            if (!TextUtils.isEmpty(content)) {
                intent.putExtra(Intent.EXTRA_TEXT, content);
            }
            intent.putExtra(Intent.EXTRA_EMAIL, address);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 分享文字内容
     *
     * @param context Context
     * @param title   分享选择器的标题
     * @param content 分享的内容
     */
    public static void shareTextAction(Context context, String title, String content) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType(TEXT_UNSPECIFIED).putExtra(Intent.EXTRA_TEXT, content);
        Intent choose = Intent.createChooser(intent, title);
        context.startActivity(choose);
    }

    /**
     * 分享图片内容
     *
     * @param context Context
     * @param title   分享选择器的标题
     * @param file    分享的图片文件
     */
    public static void shareImageAction(Context context, String title, File file) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType(IMAGE_UNSPECIFIED);
        Intent choose = Intent.createChooser(intent, title);
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        context.startActivity(choose);
    }

    /**
     * 根据APK文件安装应用
     *
     * @param context Context
     * @param apkFile 安装包的文件
     */
    public static void installApp(Context context, File apkFile) {
        Intent install = new Intent(Intent.ACTION_VIEW);
        install.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
        install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(install);
    }

    /**
     * 回到桌面的方法
     *
     * @param activity Activity
     */
    public static void showLauncher(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    /**
     * 返回重启应用的Intent
     *
     * @param activity Activity
     * @return Intent
     */
    public static Intent getRebootAppIntent(Activity activity) {
        Intent launchIntent = activity.getPackageManager().getLaunchIntentForPackage(activity.getPackageName());
        launchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return launchIntent;
    }

}
