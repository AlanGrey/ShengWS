package com.jstudio.graphic;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

import com.facebook.cache.common.CacheKey;
import com.facebook.cache.common.SimpleCacheKey;
import com.facebook.imagepipeline.request.BasePostprocessor;

public class GrayscalePostprocessor extends BasePostprocessor {

    @Override
    public void process(Bitmap dest, Bitmap source) {
        super.process(dest, source);

        Canvas canvas = new Canvas(dest);
        ColorMatrix saturation = new ColorMatrix();
        saturation.setSaturation(0f);
        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(saturation));
        canvas.drawBitmap(source, 0, 0, paint);
    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public CacheKey getPostprocessorCacheKey() {
        return new SimpleCacheKey("gray");
    }
}