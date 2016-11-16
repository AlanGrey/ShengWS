package com.jstudio.utils;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;

/**
 * Created by Jason
 */
public class SystemServiceUtils {

    /**
     * 复制文字到剪贴板
     *
     * @param context Context
     * @param text    待复制的文字
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static void copyTextToClipBoard(Context context, String text) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        // Creates a new text clip to put on the clipboard
        ClipData clip = ClipData.newPlainText("simple text", text);
        // Set the clipboard's primary clip.
        clipboard.setPrimaryClip(clip);
    }

    /**
     * 从剪贴板中获取文字
     *
     * @param context Context
     * @return 粘贴过来的文字
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static String pasteTextFromClipBoard(Context context) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        String resultString = "";
        // 检查剪贴板是否有内容
        if (clipboard.hasPrimaryClip()) {
            ClipData clipData = clipboard.getPrimaryClip();
            int count = clipData.getItemCount();
            for (int i = 0; i < count; ++i) {
                ClipData.Item item = clipData.getItemAt(i);
                CharSequence str = item.coerceToText(context);
                resultString += str;
            }
        }
        return resultString;
    }
}
