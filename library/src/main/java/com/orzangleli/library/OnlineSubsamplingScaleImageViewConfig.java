package com.orzangleli.library;

import android.content.Context;

/**
 * <p>description：
 * <p>===============================
 * <p>creator：lixiancheng
 * <p>create time：2017/11/6 下午11:37
 * <p>===============================
 * <p>reasons for modification：
 * <p>Modifier：
 * <p>Modify time：
 * <p>@version
 */

public class OnlineSubsamplingScaleImageViewConfig {

    private static String cacheDir;
    private static Context appContext;

    public static void initialize(Context context, String dir) {
        appContext = context;
        cacheDir = dir;
    }

    public static String getCacheDir() {
        if (cacheDir == null) {
            throw new IllegalArgumentException("cacheDir is null, try to use {OnlineSubsamplingScaleImageViewConfig#initialize} to initialize!");
        }
        return cacheDir;
    }

    public static Context getAppContext() {
        if (appContext == null) {
            throw new IllegalArgumentException("context is null, try to use {OnlineSubsamplingScaleImageViewConfig#initialize} to initialize!");
        }
        return appContext;
    }
}
