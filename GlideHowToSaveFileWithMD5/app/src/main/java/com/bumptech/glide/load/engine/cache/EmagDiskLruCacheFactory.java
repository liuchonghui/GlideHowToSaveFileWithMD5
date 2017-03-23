package com.bumptech.glide.load.engine.cache;

import java.io.File;

/**
 * Created by liuchonghui on 2017/3/2.
 */
public class EmagDiskLruCacheFactory implements DiskCache.Factory {
    private final int diskCacheSize;
    private final EmagDiskLruCacheFactory.CacheDirectoryGetter cacheDirectoryGetter;

    public EmagDiskLruCacheFactory(final String diskCacheFolder, int diskCacheSize) {
        this(new EmagDiskLruCacheFactory.CacheDirectoryGetter() {
            public File getCacheDirectory() {
                return new File(diskCacheFolder);
            }
        }, diskCacheSize);
    }

    public EmagDiskLruCacheFactory(final String diskCacheFolder, final String diskCacheName, int diskCacheSize) {
        this(new EmagDiskLruCacheFactory.CacheDirectoryGetter() {
            public File getCacheDirectory() {
                return new File(diskCacheFolder, diskCacheName);
            }
        }, diskCacheSize);
    }

    public EmagDiskLruCacheFactory(EmagDiskLruCacheFactory.CacheDirectoryGetter cacheDirectoryGetter, int diskCacheSize) {
        this.diskCacheSize = diskCacheSize;
        this.cacheDirectoryGetter = cacheDirectoryGetter;
    }

    public DiskCache build() {
        File cacheDir = this.cacheDirectoryGetter.getCacheDirectory();
        return cacheDir == null ? null :
                (cacheDir.mkdirs() || cacheDir.exists() && cacheDir.isDirectory() ?
                        EmagDiskLruCacheWrapper.get(cacheDir, this.diskCacheSize) :
                        null);
    }

    public interface CacheDirectoryGetter {
        File getCacheDirectory();
    }
}