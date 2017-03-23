package com.bumptech.glide.load.engine.cache;

import android.util.Log;

import com.bumptech.glide.disklrucache.DiskLruCache;
import com.bumptech.glide.load.Key;

import java.io.File;
import java.io.IOException;

/**
 * Created by liuchonghui on 2017/3/2.
 */
public class EmagDiskLruCacheWrapper implements DiskCache {
    private static final String TAG = "DiskLruCacheWrapper";

    private static final int APP_VERSION = 1;
    private static final int VALUE_COUNT = 1;
    private static EmagDiskLruCacheWrapper wrapper = null;

    private final EmagSafeKeyGenerator safeKeyGenerator;
    private final File directory;
    private final int maxSize;
    private final EmagDiskCacheWriteLocker writeLocker = new EmagDiskCacheWriteLocker();
    private DiskLruCache diskLruCache;

    public static synchronized DiskCache get(File directory, int maxSize) {
        if (wrapper == null) {
            wrapper = new EmagDiskLruCacheWrapper(directory, maxSize);
        }
        return wrapper;
    }

    protected EmagDiskLruCacheWrapper(File directory, int maxSize) {
        this.directory = directory;
        this.maxSize = maxSize;
        this.safeKeyGenerator = new EmagSafeKeyGenerator();
    }

    private synchronized DiskLruCache getDiskCache() throws IOException {
        if (diskLruCache == null) {
            diskLruCache = DiskLruCache.open(directory, APP_VERSION, VALUE_COUNT, maxSize);
        }
        return diskLruCache;
    }

    @Override
    public File get(Key key) {
        String safeKey = safeKeyGenerator.getSafeKey(key);
        if (Log.isLoggable(TAG, Log.VERBOSE)) {
            Log.v(TAG, "Get: Obtained: " + safeKey + " for for Key: " + key);
        }
        File result = null;
        try {
            final DiskLruCache.Value value = getDiskCache().get(safeKey);
            if (value != null) {
                result = value.getFile(0);
            }
        } catch (IOException e) {
            if (Log.isLoggable(TAG, Log.WARN)) {
                Log.w(TAG, "Unable to get from disk cache", e);
            }
        }
        return result;
    }

    @Override
    public void put(Key key, Writer writer) {
        writeLocker.acquire(key);
        try {
            String safeKey = safeKeyGenerator.getSafeKey(key);
            if (Log.isLoggable(TAG, Log.VERBOSE)) {
                Log.v(TAG, "Put: Obtained: " + safeKey + " for for Key: " + key);
            }
            try {
                // We assume we only need to put once, so if data was written while we were trying to get
                // the lock, we can simply abort.
                DiskLruCache diskCache = getDiskCache();
                DiskLruCache.Value current = diskCache.get(safeKey);
                if (current != null) {
                    return;
                }

                DiskLruCache.Editor editor = diskCache.edit(safeKey);
                if (editor == null) {
                    throw new IllegalStateException("Had two simultaneous puts for: " + safeKey);
                }
                try {
                    File file = editor.getFile(0);
                    if (writer.write(file)) {
                        editor.commit();
                    }
                } finally {
                    editor.abortUnlessCommitted();
                }
            } catch (IOException e) {
                if (Log.isLoggable(TAG, Log.WARN)) {
                    Log.w(TAG, "Unable to put to disk cache", e);
                }
            }
        } finally {
            writeLocker.release(key);
        }
    }

    @Override
    public void delete(Key key) {
        String safeKey = safeKeyGenerator.getSafeKey(key);
        try {
            getDiskCache().remove(safeKey);
        } catch (IOException e) {
            if (Log.isLoggable(TAG, Log.WARN)) {
                Log.w(TAG, "Unable to delete from disk cache", e);
            }
        }
    }

    @Override
    public synchronized void clear() {
        try {
            getDiskCache().delete();
            resetDiskCache();
        } catch (IOException e) {
            if (Log.isLoggable(TAG, Log.WARN)) {
                Log.w(TAG, "Unable to clear disk cache", e);
            }
        }
    }

    private synchronized void resetDiskCache() {
        diskLruCache = null;
    }
}
