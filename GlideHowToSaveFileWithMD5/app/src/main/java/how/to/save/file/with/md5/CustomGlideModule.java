package how.to.save.file.with.md5;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.EmagInternalCacheDiskCacheFactory;
import com.bumptech.glide.module.GlideModule;

/**
 * Created by liuchonghui on 16/12/7.
 */
public class CustomGlideModule implements GlideModule {
    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        builder.setDecodeFormat(DecodeFormat.ALWAYS_ARGB_8888);
        builder.setDiskCache(new EmagInternalCacheDiskCacheFactory(context));
    }

    @Override
    public void registerComponents(Context context, Glide glide) {
        // Do nothing.
    }
}
