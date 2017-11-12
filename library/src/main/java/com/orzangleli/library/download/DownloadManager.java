package com.orzangleli.library.download;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.jakewharton.disklrucache.DiskLruCache;
import com.orzangleli.library.OnlineSubsamplingScaleImageViewConfig;
import com.orzangleli.library.bean.DownloadImageEntity;
import com.orzangleli.library.callback.DownloadCallback;
import com.orzangleli.library.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <p>description：
 * <p>===============================
 * <p>creator：lixiancheng
 * <p>create time：2017/11/7 上午11:13
 * <p>===============================
 * <p>reasons for modification：
 * <p>Modifier：
 * <p>Modify time：
 * <p>@version
 */

public class DownloadManager {

    public final int MAX_CACHE_SIZE = 20*1024*1024;

    Executor executor = Executors.newFixedThreadPool(5);
    private static DiskLruCache diskLruCache;

    private static class Holder {
        private static DownloadManager instance = new DownloadManager();
    }

    public static DownloadManager getInstance() {
        return Holder.instance;
    }

    private DownloadManager() {
        try {
            diskLruCache = DiskLruCache.open(new File(OnlineSubsamplingScaleImageViewConfig.getCacheDir()),
                    getAppVersion(OnlineSubsamplingScaleImageViewConfig.getAppContext()),
                    1,
                    MAX_CACHE_SIZE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public int getAppVersion(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }

    // 将数据写入DiskLruCache
    public void requestCacheImage(String url, DownloadCallback callback) {
        DownloadImageEntity entity = new DownloadImageEntity();
        entity.setUrl(url);
        ImageDownloadRunnable runnable = new ImageDownloadRunnable(entity, callback);
        runnable.execute(executor);
    }


    /**
     * set width and height into downloadImageEntity
     * and set file length into downloadImageEntity
     * @param downloadImageEntity
     */
    public static void calculateImageSizeAndFileLength(DownloadImageEntity downloadImageEntity) {
        String bitmapPath = null;
        if (!StringUtils.isOnlinePicture(downloadImageEntity.getUrl())) {
            bitmapPath = downloadImageEntity.getUrl();
            if (bitmapPath.contains("file://")) {
                bitmapPath = bitmapPath.substring("file://".length());
            }
        } else if (StringUtils.isEmpty(DownloadManager.getCacheFileByUrl(downloadImageEntity.getUrl()))) {
            bitmapPath = DownloadManager.getCacheFileByUrl(downloadImageEntity.getUrl());
        }
        if (StringUtils.isEmpty(bitmapPath)) {
            return ;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        try {
            BitmapFactory.decodeStream(new FileInputStream(bitmapPath), null, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        downloadImageEntity.setWidth(options.outWidth);
        downloadImageEntity.setHeight(options.outHeight);

        File imageFile = new File(bitmapPath);
        if (imageFile != null && imageFile.exists()) {
            downloadImageEntity.setFileLength(imageFile.length());
        }
    }

    public static String getCacheFileByUrl (String url) {
        String key = StringUtils.getStringByMD5(url);
        File dir = diskLruCache.getDirectory();
        String filename = key + ".0";
        File cacheFile = new File(dir, filename);
        if (cacheFile.exists()) {
            return cacheFile.getAbsolutePath();
        }
        return null;
    }



    public static DiskLruCache getDiskLruCache() {
        return diskLruCache;
    }
}
