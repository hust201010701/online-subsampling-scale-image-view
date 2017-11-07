package com.orzangleli.library.callback;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.orzangleli.library.bean.DownloadImageEntity;
import com.orzangleli.library.download.DownloadManager;
import com.orzangleli.library.view.OnlineSubsamplingScaleImageView;

/**
 * <p>description：
 * <p>===============================
 * <p>creator：lixiancheng
 * <p>create time：2017/11/7 下午12:32
 * <p>===============================
 * <p>reasons for modification：
 * <p>Modifier：
 * <p>Modify time：
 * <p>@version
 */

public class MessageHandler extends Handler {
    static final String FILE_SCHEME = "file:///";
    static final String ASSET_SCHEME = "file:///android_asset/";

    public static final int START = 1;
    public static final int PROGRESS = 2;
    public static final int COMPLETE = 3;
    public static final int ERROR = 4;

    public static final int THUMBNAIL = 0;
    public static final int NOTTHUMBNAIL = 1;

    private DownloadCallback imageDownloadListener;
    private DownloadCallback thumbnailImageDownloadListener;

    public MessageHandler(DownloadCallback imageDownloadListener, DownloadCallback thumbnailImageDownloadListener) {
        this.imageDownloadListener = imageDownloadListener;
        this.thumbnailImageDownloadListener = thumbnailImageDownloadListener;
    }

    public MessageHandler() {
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        Holder holder = (Holder) msg.obj;
        switch (msg.what) {
            case START:
                if (msg.arg1 == NOTTHUMBNAIL) {
                    if (imageDownloadListener != null) {
                        imageDownloadListener.onStart(holder.entity);
                    }
                } else {
                    if (thumbnailImageDownloadListener != null) {
                        thumbnailImageDownloadListener.onStart(holder.entity);
                    }
                }
                break;
            case PROGRESS:
                if (msg.arg1 == NOTTHUMBNAIL) {
                    if (imageDownloadListener != null) {
                        imageDownloadListener.onProgress(holder.entity);
                    }
                } else {
                    if (thumbnailImageDownloadListener != null) {
                        thumbnailImageDownloadListener.onProgress(holder.entity);
                    }
                }
                break;
            case COMPLETE:
                if (msg.arg1 == NOTTHUMBNAIL) {
                    // 显示下载文件
                    String path = DownloadManager.getInstance().getCacheFileByUrl(holder.entity.getUrl());
                    if (path != null) {
                        holder.imageView.setImage(ImageSource.uri(Uri.parse(FILE_SCHEME+path)));
                    }
                    if (imageDownloadListener != null) {
                        imageDownloadListener.onComplete(holder.entity);
                    }
                } else {
                    // 显示下载文件
                    String path = DownloadManager.getInstance().getCacheFileByUrl(holder.entity.getUrl());
                    if (path != null) {
                        holder.imageView.setImage((ImageSource) null, ImageSource.uri(Uri.parse(FILE_SCHEME+path)));
                    }

                    if (thumbnailImageDownloadListener != null) {
                        thumbnailImageDownloadListener.onComplete(holder.entity);
                    }
                }
                break;
            case ERROR:
                if (msg.arg1 == NOTTHUMBNAIL) {
                    if (imageDownloadListener != null) {
                        imageDownloadListener.onError(holder.entity, holder.exception);
                    }
                } else {
                    if (thumbnailImageDownloadListener != null) {
                        thumbnailImageDownloadListener.onError(holder.entity, holder.exception);
                    }
                }
                break;
        }
    }

    public static class Holder {
        DownloadImageEntity entity;
        Exception exception;
        OnlineSubsamplingScaleImageView imageView;

        public Holder(OnlineSubsamplingScaleImageView imageView, DownloadImageEntity entity, Exception exception) {
            this.imageView = imageView;
            this.entity = entity;
            this.exception = exception;
        }
    }


    public void setImageDownloadListener(DownloadCallback imageDownloadListener) {
        this.imageDownloadListener = imageDownloadListener;
    }

    public void setThumbnailImageDownloadListener(DownloadCallback thumbnailImageDownloadListener) {
        this.thumbnailImageDownloadListener = thumbnailImageDownloadListener;
    }
}
