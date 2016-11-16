package com.jstudio.graphic;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.os.Build;

import com.facebook.cache.common.CacheKey;
import com.facebook.cache.common.SimpleCacheKey;
import com.facebook.imagepipeline.request.BasePostprocessor;

public class MaskPostprocessor extends BasePostprocessor {

    private static Paint paint = new Paint();
    private Context context;
    private int maskId;

    static {
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
    }

    /**
     * @param maskId If you change the mask file, please also rename the mask file, or Glide will get
     *               the cache with the old mask. Because getId() return the same values if using the
     *               same make file name. If you have a good idea please tell us, thanks.
     */
    public MaskPostprocessor(Context context, int maskId) {
        this.context = context.getApplicationContext();
        this.maskId = maskId;
    }

    @Override
    public void process(Bitmap dest, Bitmap source) {
        Bitmap result = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        Drawable mask = getMaskDrawable(context, maskId);
        Canvas canvas = new Canvas(result);
        mask.setBounds(0, 0, source.getWidth(), source.getHeight());
        mask.draw(canvas);
        canvas.drawBitmap(source, 0, 0, paint);
        super.process(dest, result);
    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public CacheKey getPostprocessorCacheKey() {
        return new SimpleCacheKey("mask=" + context.getResources().getResourceEntryName(maskId));
    }

    public static Drawable getMaskDrawable(Context context, int maskId) {
        Drawable drawable;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            drawable = context.getDrawable(maskId);
        } else {
            drawable = context.getResources().getDrawable(maskId);
        }
        if (drawable == null) {
            throw new IllegalArgumentException("maskId is invalid");
        }
        return drawable;
    }
}