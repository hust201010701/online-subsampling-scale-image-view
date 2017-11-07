package com.orzangleli.onlinebigimageviewer;

import android.app.Application;

import com.orzangleli.library.OnlineSubsamplingScaleImageViewConfig;

/**
 * <p>description：
 * <p>===============================
 * <p>creator：lixiancheng
 * <p>create time：2017/11/7 下午2:15
 * <p>===============================
 * <p>reasons for modification：
 * <p>Modifier：
 * <p>Modify time：
 * <p>@version
 */

public class MyApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        OnlineSubsamplingScaleImageViewConfig.initialize(this.getApplicationContext(), this.getExternalCacheDir().getAbsolutePath());
    }
}
