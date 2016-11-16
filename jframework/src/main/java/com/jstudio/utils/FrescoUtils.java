package com.jstudio.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;

import com.facebook.binaryresource.BinaryResource;
import com.facebook.binaryresource.FileBinaryResource;
import com.facebook.cache.common.CacheKey;
import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.common.internal.Supplier;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.cache.CountingMemoryCache;
import com.facebook.imagepipeline.cache.DefaultCacheKeyFactory;
import com.facebook.imagepipeline.cache.ImageCacheStatsTracker;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.core.ImagePipelineFactory;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.BasePostprocessor;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.jstudio.graphic.BlurPostprocessor;
import com.jstudio.graphic.GrayscalePostprocessor;
import com.jstudio.graphic.MaskPostprocessor;

import java.io.File;

/**
 * Created by Jason
 */
public class FrescoUtils {

    /**
     * 配置指定缓存大小和预先配置并初始化Fresco，方法调用后不需要再调用初始化方法
     * 在加载webP，gif需要添加相应的以来包，默认只包含最基本的fresco
     *
     * @param context   Context
     * @param cacheSize 磁盘缓存大小，大小为mB
     */
    public static void initWithConfig(final Context context, int cacheSize, Bitmap.Config config) {
        DiskCacheConfig diskCacheConfig = DiskCacheConfig.newBuilder(context)
                .setMaxCacheSize(cacheSize * 1024 * 1024)
                .setBaseDirectoryPathSupplier(new Supplier<File>() {
                    @Override
                    public File get() {
                        //缓存至应用的缓存文件夹
                        return context.getCacheDir();
                    }
                })
                .build();
        ImagePipelineConfig pipelineConfig = ImagePipelineConfig.newBuilder(context)
                .setMainDiskCacheConfig(diskCacheConfig)
                .setImageCacheStatsTracker(new MyImageCacheStatsTracker()) //缓存监听接口
                .setDownsampleEnabled(true)
                .setBitmapsConfig(config) //使用此模式会使得模糊实效
                .build();
        Fresco.initialize(context, pipelineConfig);
    }

    /**
     * 根据地址请求图片的Bitmap对象，结果在BaseBitmapDataSubscriber中回调
     *
     * @param context    Context
     * @param uri        图片地址
     * @param subscriber 图片回调
     */
    public static void getImageBitmap(Context context, Uri uri, BaseBitmapDataSubscriber subscriber) {
        ImageRequest imageRequest = ImageRequest.fromUri(uri);
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        DataSource<CloseableReference<CloseableImage>> dataSource
                = imagePipeline.fetchDecodedImage(imageRequest, context);
        dataSource.subscribe(subscriber, null);
    }

    /**
     * 根据地址请求图片的Bitmap对象，结果在BaseBitmapDataSubscriber中回调
     *
     * @param context    Context
     * @param uri        图片地址
     * @param width      指定图片宽
     * @param height     指定图片高
     * @param processor  后处理器
     * @param subscriber 图片回调
     */
    public static void getImageBitmap(Context context, Uri uri, int width, int height,
                                      BasePostprocessor processor, BaseBitmapDataSubscriber subscriber) {
        ResizeOptions resizeOptions = null;
        if (width != 0 && height != 0) {
            resizeOptions = new ResizeOptions(width, height);
        }
        ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(uri)
                .setProgressiveRenderingEnabled(false)
                .setResizeOptions(resizeOptions)
                .build();
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        DataSource<CloseableReference<CloseableImage>> dataSource
                = imagePipeline.fetchDecodedImage(imageRequest, context);
        dataSource.subscribe(subscriber, CallerThreadExecutor.getInstance());
    }

    /**
     * 按需加载图片
     *
     * @param draweeView SimpleDraweeView实例
     * @param uri        图片Uri
     * @param width      图片宽
     * @param height     图片高
     * @param processor  后处理器，即获取图片后的处理器
     * @param listener   下载事件监听
     */
    public static void loadImage(SimpleDraweeView draweeView, Uri uri, int width, int height,
                                 BasePostprocessor processor, BaseControllerListener listener) {
        ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(uri)
                .setPostprocessor(processor) //设置后处理器
                .setResizeOptions(new ResizeOptions(width, height)) //设置所需图片宽高
                .setProgressiveRenderingEnabled(true) //设置渐进式加载
                .setAutoRotateEnabled(true) //自动旋转图片
                .build();

        PipelineDraweeController controller = (PipelineDraweeController) Fresco
                .newDraweeControllerBuilder()
                .setImageRequest(imageRequest)
                .setControllerListener(listener)
                .setOldController(draweeView.getController())
                .setAutoPlayAnimations(true) //自动播放gif
                .build();

        draweeView.setController(controller);
    }

    /**
     * 加载模糊图像
     *
     * @param context    Context
     * @param draweeView SimpleDraweeView
     * @param uri        图像地址
     * @param width      图片宽
     * @param height     图片高
     * @param radius     模糊半径，越大越模糊，速度越慢
     * @param sampling   采样率，越大越模糊，速度越快
     */
    public static void loadAndPostBlurImage(Context context, SimpleDraweeView draweeView, Uri uri,
                                            int width, int height, int radius, int sampling) {
        if (sampling < 2) {
            sampling = 2;
        }
        loadImage(draweeView, uri, width / sampling, height / sampling,
                new BlurPostprocessor(context, radius, sampling), null);
    }

    /**
     * 加载灰阶图片
     *
     * @param uri        图像地址
     * @param draweeView SimpleDraweeView
     * @param width      图片宽
     * @param height     图片高
     */
    public static void loadAndPostGrayImage(Uri uri, SimpleDraweeView draweeView, int width, int height) {
        loadImage(draweeView, uri, width, height, new GrayscalePostprocessor(), null);
    }

    /**
     * 加载自定义形状图片
     *
     * @param context    Context
     * @param uri        图像地址
     * @param draweeView SimpleDraweeView
     * @param width      图片宽
     * @param height     图片高
     * @param maskResId  自定形状资源id
     */
    public static void loadAndPostMaskImage(Context context, Uri uri, SimpleDraweeView draweeView,
                                            int width, int height, int maskResId) {
        loadImage(draweeView, uri, width, height, new MaskPostprocessor(context, maskResId), null);
    }

    /**
     * 根据图片uri获取磁盘缓存，得到二进制文件，可直接使用
     *
     * @param uri 图片uri
     * @return 缓存图片文件，没有则返回空
     */
    public static File getCacheImageFileFromDisk(Uri uri) {
        File imageCache = null;
        if (TextUtils.isEmpty(uri.toString())) {
            return null;
        }
        CacheKey cacheKey = DefaultCacheKeyFactory.getInstance().getEncodedCacheKey(ImageRequest.fromUri(uri), null);
        if (ImagePipelineFactory.getInstance().getMainFileCache().hasKey(cacheKey)) {
            BinaryResource resource = ImagePipelineFactory.getInstance().getMainFileCache().getResource(cacheKey);
            imageCache = ((FileBinaryResource) resource).getFile();
        } else if (ImagePipelineFactory.getInstance().getSmallImageFileCache().hasKey(cacheKey)) {
            BinaryResource resource = ImagePipelineFactory.getInstance().getSmallImageFileCache().getResource(cacheKey);
            imageCache = ((FileBinaryResource) resource).getFile();
        }
        return imageCache;
    }

    /**
     * 为无法显示为圆形的图片设置圆形遮罩达到显示成圆形
     *
     * @param draweeView
     * @param overlayColor
     */
    public static void setCircle(SimpleDraweeView draweeView, int overlayColor) {
        RoundingParams roundingParams = RoundingParams.asCircle();
        //添加圆形背景色一样的遮罩
        roundingParams.setOverlayColor(overlayColor);
        draweeView.getHierarchy().setRoundingParams(roundingParams);
    }

    /**
     * 清除磁盘缓存
     */
    public static void clearDiskCache() {
        Fresco.getImagePipeline().clearDiskCaches();
    }

    /**
     * 清除单张图片磁盘缓存
     *
     * @param uri 图片地址
     */
    public static void clearCacheByUri(Uri uri) {
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        imagePipeline.evictFromDiskCache(uri);
    }

    /**
     * 暂停网络请求，停止加载任务，配合ListView滑动时调用
     */
    public static void pauseFresco() {
        Fresco.getImagePipeline().pause();
    }

    /**
     * 恢复网络请求，当ListView不在滑动时调用
     */
    public static void resumeFresco() {
        Fresco.getImagePipeline().resume();
    }

    public static class MyImageCacheStatsTracker implements ImageCacheStatsTracker {

        @Override
        public void onBitmapCachePut() {

        }

        @Override
        public void onBitmapCacheHit() {

        }

        @Override
        public void onBitmapCacheMiss() {

        }

        @Override
        public void onMemoryCachePut() {

        }

        @Override
        public void onMemoryCacheHit() {

        }

        @Override
        public void onMemoryCacheMiss() {

        }

        @Override
        public void onStagingAreaHit() {

        }

        @Override
        public void onStagingAreaMiss() {

        }

        @Override
        public void onDiskCacheHit() {

        }

        @Override
        public void onDiskCacheMiss() {

        }

        @Override
        public void onDiskCacheGetFail() {

        }

        @Override
        public void registerBitmapMemoryCache(CountingMemoryCache<?, ?> bitmapMemoryCache) {

        }

        @Override
        public void registerEncodedMemoryCache(CountingMemoryCache<?, ?> encodedMemoryCache) {

        }
    }

}
