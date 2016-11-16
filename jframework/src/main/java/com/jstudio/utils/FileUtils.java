package com.jstudio.utils;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;

/**
 * 文件夹创建工具类
 * Created by Jason
 */
public class FileUtils {

    /**
     * 获取应用的主文件夹
     *
     * @param context Context
     * @return 返回应用主文件夹，返回的同时文件夹创建完毕
     */
    public static String getAppMainFolder(Context context) {
        String folder;
        File folderFile;
        if (isExternalStorageAvailable()) {
            folder = getExternalRootDirectory() + File.separator + "." + context.getPackageName() + File.separator;
            folderFile = new File(folder);
            if (!folderFile.exists()) {
                folderFile.mkdirs();
            }
            return folder;
        } else {
            folder = getInternalCacheDir(context) + File.separator + context.getPackageName() + File.separator;
            folderFile = new File(folder);
            if (!folderFile.exists()) {
                folderFile.mkdirs();
            }
            return folder;
        }
    }

    /**
     * 外置存储是否可用
     *
     * @return true标识可用，否则为false
     */
    public static boolean isExternalStorageAvailable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取外置存储根目录，外置存储不可用返回空
     *
     * @return 外置存储根目录路径，/storage/emulated/0
     */
    public static String getExternalRootDirectory() {
        if (isExternalStorageAvailable()) {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        return null;
    }

    /**
     * 获取外置公用文件夹路径，外置存储必须可用
     *
     * @param type 文件夹路径的类型，必须是以下类型 DIRECTORY_MUSIC,
     *             DIRECTORY_PODCASTS, DIRECTORY_RINGTONES, DIRECTORY_ALARMS,
     *             DIRECTORY_NOTIFICATIONS, DIRECTORY_PICTURES, DIRECTORY_MOVIES,
     *             DIRECTORY_DOWNLOADS, or DIRECTORY_DCIM.
     * @return 文件夹路径，比如storage/emulated/0/Movies，有可能正确返回路径，但实际文件夹为空的情况
     */
    public static String getPublicDirectory(String type) {
        return Environment.getExternalStoragePublicDirectory(type).getAbsolutePath();
    }

    /**
     * 获取外置存储的缓存文件夹路径
     *
     * @param context Context
     * @return 外置存储的路径，storage/emulated/0/Android/data/"packageName"/cache
     */
    public static String getExternalCacheDir(Context context) {
        if (isExternalStorageAvailable()) {
            return context.getExternalCacheDir().getAbsolutePath();
        }
        return null;
    }

    /**
     * 获取内置存储的缓存文件夹路径
     *
     * @param context Context
     * @return 内置存储的路径，data/data/"packageName"/cache
     */
    public static String getInternalCacheDir(Context context) {
        return context.getCacheDir().getAbsolutePath();
    }

    /**
     * 在已知的文件夹路径下创建子文件夹
     *
     * @param parentFolderPath 父级路径
     * @param subFolderName    子文件夹名
     * @return true标识创建成功，否则返回false
     */
    public boolean createSubFolder(String parentFolderPath, String subFolderName) {
        if (!TextUtils.isEmpty(parentFolderPath)) {
            return false;
        }
        if (!parentFolderPath.endsWith(File.separator)) {
            parentFolderPath = parentFolderPath + File.separator;
        }
        File subFolder = new File(parentFolderPath + subFolderName + File.separator);
        return subFolder.exists() || subFolder.mkdirs();
    }
}
