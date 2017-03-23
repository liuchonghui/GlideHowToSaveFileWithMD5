package com.bumptech.glide.load.engine.cache;

import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.engine.EmagInternalCacheUtil;
import com.bumptech.glide.util.LruCache;

/**
 * Created by liuchonghui on 2017/3/2.
 */
public class EmagSafeKeyGenerator extends SafeKeyGenerator {
    private final LruCache<Key, String> loadIdToSafeHash = new LruCache(1000);

    public String getSafeKey(Key key) {
        LruCache e = this.loadIdToSafeHash;
        String safeKey;
        synchronized (this.loadIdToSafeHash) {
            safeKey = (String) this.loadIdToSafeHash.get(key);
        }
        if (safeKey == null) {
            safeKey = EmagInternalCacheUtil.makeKeyStringFromKey(key);
            if (safeKey == null) {
                safeKey = super.getSafeKey(key);
            }
        }
        return safeKey;
    }
}