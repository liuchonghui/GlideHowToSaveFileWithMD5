package com.bumptech.glide.load.engine;

import android.content.Context;

import com.bumptech.glide.load.engine.cache.EmagDiskLruCacheFactory;

import java.io.File;

/**
 * Created by liuchonghui on 2017/3/2.
 */
public class EmagInternalCacheDiskCacheFactory extends EmagDiskLruCacheFactory {
    public EmagInternalCacheDiskCacheFactory(Context context) {
        this(context, "emag_image_disk_cache", 262144000);
    }

    public EmagInternalCacheDiskCacheFactory(Context context, int diskCacheSize) {
        this(context, "emag_image_disk_cache", diskCacheSize);
    }

    public EmagInternalCacheDiskCacheFactory(final Context context, final String diskCacheName, int diskCacheSize) {
        super(new CacheDirectoryGetter() {
            public File getCacheDirectory() {
                File cacheDirectory = context.getCacheDir();
                return cacheDirectory == null ? null : (diskCacheName != null ? new File(cacheDirectory, diskCacheName) : cacheDirectory);
            }
        }, diskCacheSize);
    }
}