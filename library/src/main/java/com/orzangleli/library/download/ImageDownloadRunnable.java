package com.orzangleli.library.download;

import com.orzangleli.library.OnlineSubsamplingScaleImageViewConfig;
import com.orzangleli.library.bean.DownloadImageEntity;
import com.orzangleli.library.callback.DownloadCallback;
import com.orzangleli.library.util.StringUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * <p>description：
 * <p>===============================
 * <p>creator：lixiancheng
 * <p>create time：2017/11/6 下午11:06
 * <p>===============================
 * <p>reasons for modification：
 * <p>Modifier：
 * <p>Modify time：
 * <p>@version
 */

public class ImageDownloadRunnable implements Runnable {

    private final int BUFFER_LENGTH = 4096;

    private DownloadImageEntity downloadImageEntity;

    private DownloadCallback callback;

    @Override
    public void run() {
        /**
         * check url and saveDir before connect
         */
        checkBeforeConnect();
        try {
            URL url = new URL(downloadImageEntity.getUrl());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // todo 添加header
            addHeader(connection);
            connection.connect();
            int respCode = connection.getResponseCode();
            int fileLength = connection.getContentLength();
            downloadImageEntity.setFileLength(fileLength);

            if (respCode == 200) {
                BufferedInputStream inputStream = new BufferedInputStream(connection.getInputStream());
                FileOutputStream outputStream = new FileOutputStream(downloadImageEntity.getTargetFile());
                int allReadByte = 0;
                byte[] buff = new byte[BUFFER_LENGTH];
                while (true) {
                    int byteCount = inputStream.read(buff);
                    if (byteCount == -1) {
                        break;
                    }
                    allReadByte += byteCount;
                    double per = 1.0 * allReadByte / fileLength;
                    downloadImageEntity.setPercent(per);
                    if (callback != null) {
                        callback.onProgress(downloadImageEntity);
                    }
                }
                if (callback != null) {
                    callback.onComplete(downloadImageEntity);
                }
            } else {
                if (callback != null) {
                    callback.onError(downloadImageEntity, new Exception("connect to url { " + downloadImageEntity.getUrl() + " } is fail"));
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            if (callback != null) {
                callback.onError(downloadImageEntity, e);
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (callback != null) {
                callback.onError(downloadImageEntity, e);
            }
        }

    }

    // todo 添加请求头
    private void addHeader(HttpURLConnection connection) {

    }

    private void checkBeforeConnect() {
        if (StringUtils.isEmpty(downloadImageEntity.getUrl())) {

            throw new IllegalArgumentException("you try to load a image which url is null, maybe you should try to use method {setUrl} to set url.");
        }
        if (StringUtils.isEmpty(downloadImageEntity.getSaveDir())) {
            if (!StringUtils.isEmpty(OnlineSubsamplingScaleImageViewConfig.getCacheDir())) {
                downloadImageEntity.setSaveDir(OnlineSubsamplingScaleImageViewConfig.getCacheDir());
            } else {
                downloadImageEntity.setSaveDir(OnlineSubsamplingScaleImageViewConfig.getAppContext().getExternalCacheDir().getAbsolutePath());
            }
        }
    }


    // -------------------------------------------------------- getter and setter ---------------------------------------------------------------------- //


    public DownloadImageEntity getDownloadImageEntity() {
        return downloadImageEntity;
    }

    public void setDownloadImageEntity(DownloadImageEntity downloadImageEntity) {
        this.downloadImageEntity = downloadImageEntity;
    }

    public DownloadCallback getCallback() {
        return callback;
    }

    public void setCallback(DownloadCallback callback) {
        this.callback = callback;
    }
}
