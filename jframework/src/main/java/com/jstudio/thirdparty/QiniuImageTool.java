package com.jstudio.thirdparty;

import java.util.Locale;

/**
 * Created by Jason
 */
public class QiniuImageTool {

    private static final String PATTERN = "?imageView2/%d/";
    private static final String WIDTH = "w/%d";
    private static final String HEIGHT = "h/%d";

    /**
     * 限定缩略图的长边最多为<LongEdge>，短边最多为<ShortEdge>，进行等比缩放，不裁剪。
     * 如果只指定 w 参数则表示限定长边（短边自适应），只指定 h 参数则表示限定短边（长边自适应）
     */
    private static final int MODE_MAX_EDGE = 0;

    /**
     * 限定缩略图的宽最少为<Width>，高最少为<Height>，进行等比缩放，居中裁剪。
     * 转后的缩略图通常恰好是 <Width>x<Height> 的大小（有一个边缩放的时候会因为超出矩形框而被裁剪掉多余部分）。
     * 如果只指定 w 参数或只指定 h 参数，代表限定为长宽相等的正方图
     */
    private static final int MODE_FILL = 1;

    /**
     * 限定缩略图的宽最多为<Width>，高最多为<Height>，进行等比缩放，不裁剪。
     * 如果只指定 w 参数则表示限定宽（长自适应），只指定 h 参数则表示限定长（宽自适应）。
     * 它和模式0类似，区别只是限定宽和高，不是限定长边和短边。
     */
    private static final int MODE_SCALE = 2;

    /**
     * 指定宽的最大值，不改变比例，获取缩略图
     *
     * @param imgUrl       原图地址
     * @param requestWidth 缩略图的宽
     * @return 缩略图地址
     */
    public static String getScaleByWidth(String imgUrl, int requestWidth) {
        return String.format(Locale.getDefault(), imgUrl + PATTERN + WIDTH, MODE_SCALE, requestWidth);
    }

    /**
     * 指定高的最大值，不改变比例，获取缩略图
     *
     * @param imgUrl        原图地址
     * @param requestHeight 缩略图的高
     * @return 缩略图地址
     */
    public static String getScaleByHeight(String imgUrl, int requestHeight) {
        return String.format(Locale.getDefault(), imgUrl + PATTERN + HEIGHT, MODE_SCALE, requestHeight);
    }

    /**
     * 指定高宽的最大值，不改变比例，获取缩略图
     *
     * @param imgUrl        原图地址
     * @param requestWidth  缩略图的宽
     * @param requestHeight 缩略图的高
     * @return 缩略图地址
     */
    public static String getScaleByBoth(String imgUrl, int requestWidth, int requestHeight) {
        return String.format(Locale.getDefault(), imgUrl + PATTERN + WIDTH + "/" + HEIGHT, MODE_FILL, requestWidth, requestHeight);
    }


}
