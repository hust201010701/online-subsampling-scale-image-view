package com.orzangleli.library.callback;

import com.orzangleli.library.bean.DownloadImageEntity;

/**
 * <p>description：
 * <p>===============================
 * <p>creator：lixiancheng
 * <p>create time：2017/11/6 下午11:19
 * <p>===============================
 * <p>reasons for modification：
 * <p>Modifier：
 * <p>Modify time：
 * <p>@version
 */

public class DownloadCallback {
    public void onStart(DownloadImageEntity entity){};
    public void onProgress(DownloadImageEntity entity){};
    public void onComplete(DownloadImageEntity entity){};
    public void onError(DownloadImageEntity entity, Exception e){};
}
