package com.bumptech.glide.load.engine;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.bumptech.glide.load.Key;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.util.HashMap;

/**
 * Created by liuchonghui on 2017/3/2.
 */
public class EmagInternalCacheUtil {

    public static File getCacheDirectory(Context context) {
        File cacheDirectory = context.getCacheDir();
        return cacheDirectory == null ? null : new File(cacheDirectory, "emag_image_disk_cache");

    }

    public static String getCachePathByUrl(Context context, String imageUrl) {
        if (imageUrl == null || imageUrl.length() == 0) {
            return null;
        }
        String path = null;
        final String md5 = getMD5(imageUrl);
        File cacheDir = EmagInternalCacheUtil.getCacheDirectory(context);
        if (cacheDir.exists()) {
            File[] files = cacheDir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File file, String filename) {
                    if (filename.contains(md5)) {
                        return true;
                    }
                    return false;
                }
            });
            if (files != null && files.length > 0) {
                File cache = files[0];
                if (cache != null && cache.exists()) {
                    path = cache.getAbsolutePath();
                }
            }
        }
        return path;
    }

    public static String makeKeyStringFromKey(Key extendedKeyShouldBe) {
        String safeKey = null;
        String url = null;
        String md5 = null;
        if (extendedKeyShouldBe instanceof EngineKey) {
            String strKey = null;
            strKey = ((EngineKey) extendedKeyShouldBe).toString();
            int start = strKey.indexOf('{');
            int end = strKey.indexOf('+');
            url = strKey.substring(start + 1, end);
            md5 = getMD5(url.getBytes());
        } else if (extendedKeyShouldBe instanceof OriginalKey) {
            OriginalKey orikey = (OriginalKey) extendedKeyShouldBe;
            Object obj = getPrivateValueFromObject(orikey, "id");
            if (obj instanceof String) {
                url = obj.toString();
                md5 = getMD5(url.getBytes());
            }
        }
        safeKey = md5;
        return safeKey;
    }

    public static Bitmap getCachedBitmap(Context context, String path) {
        Bitmap thumbnailCache = null;
        File file = new File(path);
        if (file.exists()) {
            try {
                thumbnailCache = BitmapFactory.decodeFile(file.getAbsolutePath());
            } catch (Exception e) {
                e.printStackTrace();
                thumbnailCache = null;
            }
        }
        return thumbnailCache;
    }

    private static HashMap<Class, Field> getterCache = new HashMap<Class, Field>();

    private static Object getPrivateValueFromObject(Object obj, String fieldName) {
        Class clazz = obj.getClass();
        Field field = null;
        Object result = null;
        if (getterCache.containsKey(clazz)) {
            field = getterCache.get(clazz);
        } else {
            try {
                field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                getterCache.put(clazz, field);
            } catch (Exception e) {
                e.printStackTrace();
                field = null;
            }
        }
        if (field != null) {
            try {
                result = field.get(obj);
            } catch (Exception e) {
                e.printStackTrace();
                result = null;
            }
        }
        return result;
    }

    public static String getMD5(String message) {
        if (message == null)
            return "";

        return getMD5(message.getBytes());
    }

    public static String getMD5(byte[] bytes) {
        if (bytes == null)
            return "";

        String digest = "";
        try {
            MessageDigest algorithm = MessageDigest.getInstance("MD5");
            algorithm.reset();
            algorithm.update(bytes);
            digest = toHexString(algorithm.digest());
        } catch (Exception e) {
        }
        return digest;
    }

    private static String toHexString(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String str = Integer.toHexString(0xFF & b);
            while (str.length() < 2) {
                str = "0" + str;
            }
            hexString.append(str);
        }
        return hexString.toString();
    }
}
