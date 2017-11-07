package com.orzangleli.library.download;

import com.jakewharton.disklrucache.DiskLruCache;
import com.orzangleli.library.bean.DownloadImageEntity;
import com.orzangleli.library.callback.DownloadCallback;
import com.orzangleli.library.util.StringUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Executor;

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

public class ImageDownloadRunnable extends Thread {

    private final int BUFFER_LENGTH = 4096;

    private DownloadImageEntity downloadImageEntity;

    private DownloadCallback callback;

    public ImageDownloadRunnable(DownloadImageEntity downloadImageEntity, DownloadCallback callback) {
        this.downloadImageEntity = downloadImageEntity;
        this.callback = callback;
    }

    @Override
    public void run() {
        /**
         * check url and saveDir before connect
         */
        checkBeforeConnect();
        BufferedInputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            URL url = new URL(downloadImageEntity.getUrl());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // todo 添加header
            addHeader(connection);
            connection.connect();
            int respCode = connection.getResponseCode();
            int fileLength = connection.getContentLength();
            downloadImageEntity.setFileLength(fileLength);

            String key = StringUtils.getStringByMD5(downloadImageEntity.getUrl());
            DiskLruCache.Editor editor = DownloadManager.getInstance().getDiskLruCache().edit(key);
            if (editor != null) {
                outputStream = editor.newOutputStream(0);
            }

            if (respCode == 200) {
                inputStream = new BufferedInputStream(connection.getInputStream());
                int allReadByte = 0;
                byte[] buff = new byte[BUFFER_LENGTH];
                int len = 0;
                while ((len = inputStream.read(buff)) != -1) {
                    outputStream.write(buff, 0 ,len);
                    outputStream.flush();
                    allReadByte += len;
                    double per = 1.0 * allReadByte / fileLength;
                    downloadImageEntity.setPercent(per);
                    if (callback != null) {
                        callback.onProgress(downloadImageEntity);
                    }
                }
                editor.commit();
                try {
                    DownloadManager.getInstance().getDiskLruCache().flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (callback != null) {
                    callback.onComplete(downloadImageEntity);
                }
            } else {
                editor.abort();
                try {
                    DownloadManager.getInstance().getDiskLruCache().flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // todo 添加请求头
    private void addHeader(HttpURLConnection connection) {
//        connection.setConnectTimeout(5*1000);
//        connection.setReadTimeout(10*1000);
//        connection.setDoInput(true);
//        connection.setDoOutput(true);
    }

    private void checkBeforeConnect() {
        if (StringUtils.isEmpty(downloadImageEntity.getUrl())) {
            throw new IllegalArgumentException("you try to load a image which url is null, maybe you should try to use method {setUrl} to set url.");
        }
    }


    public void execute(Executor executor) {
        executor.execute(this);
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
