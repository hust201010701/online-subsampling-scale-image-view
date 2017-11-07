package com.orzangleli.library.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.ImageViewState;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.orzangleli.library.bean.DownloadImageEntity;
import com.orzangleli.library.callback.DownloadCallback;
import com.orzangleli.library.callback.MessageHandler;
import com.orzangleli.library.download.DownloadManager;
import com.orzangleli.library.util.StringUtils;

/**
 * <p>description：
 * <p>===============================
 * <p>creator：lixiancheng
 * <p>create time：2017/11/6 下午10:56
 * <p>===============================
 * <p>reasons for modification：
 * <p>Modifier：
 * <p>Modify time：
 * <p>@version
 */

public class OnlineSubsamplingScaleImageView extends SubsamplingScaleImageView {

    private DownloadCallback imageDownloadListener;
    private DownloadCallback thumbnailImageDownloadListener;

    private MessageHandler messageHandler;


    public OnlineSubsamplingScaleImageView(Context context) {
        this(context, null);
    }


    public OnlineSubsamplingScaleImageView(Context context, AttributeSet attr) {
        super(context, attr);
        messageHandler = new MessageHandler();
    }

    /**
     * this method support local and online picture
     * @param url
     */
    public void setImageUri(String url) {
        this.setImageUri(url, null, null);
    }

    public void setImageUri(String url, String thumbUrl) {
        this.setImageUri(url, thumbUrl, null);
    }


    public void setImageUri(String url, ImageViewState state) {
        this.setImageUri(url, null, state);
    }

    public void setImageUri(final String url, String thumbUrl, final ImageViewState state) {
        // download thumbnail first
        if (StringUtils.isOnlinePicture(thumbUrl)) {
            DownloadManager.getInstance().requestCacheImage(thumbUrl, new DownloadCallback(){
                @Override
                public void onStart(DownloadImageEntity entity) {
                    super.onStart(entity);
                    Message msg = new Message();
                    msg.what = MessageHandler.START;
                    msg.arg1 = MessageHandler.THUMBNAIL;
                    msg.obj = new MessageHandler.Holder(OnlineSubsamplingScaleImageView.this, entity, null);
                    messageHandler.sendMessage(msg);
                }

                @Override
                public void onProgress(DownloadImageEntity entity) {
                    super.onProgress(entity);
                    Message msg = new Message();
                    msg.what = MessageHandler.PROGRESS;
                    msg.arg1 = MessageHandler.THUMBNAIL;
                    msg.obj = new MessageHandler.Holder(OnlineSubsamplingScaleImageView.this, entity, null);
                    messageHandler.sendMessage(msg);
                }

                @Override
                public void onComplete(DownloadImageEntity entity) {
                    super.onComplete(entity);
                    if (StringUtils.isOnlinePicture(url)) {
                        // 原图是在线的，需要先去下载，准备加载原图
                        loadOnlineImage(url);
                    } else {
                        // 原图是本地的，直接加载就行
//                        OnlineSubsamplingScaleImageView.this.setImage(ImageSource.uri(Uri.parse(url)), ImageSource.bitmap(bitmap), state);
                    }
                    // 通知下载成功
                    Message msg = new Message();
                    msg.what = MessageHandler.COMPLETE;
                    msg.arg1 = MessageHandler.THUMBNAIL;
                    msg.obj = new MessageHandler.Holder(OnlineSubsamplingScaleImageView.this, entity, null);
                    messageHandler.sendMessage(msg);


                }

                @Override
                public void onError(DownloadImageEntity entity, Exception e) {
                    super.onError(entity, e);
                    // todo 显示加载失败的图片
                    Message msg = new Message();
                    msg.what = MessageHandler.ERROR;
                    msg.arg1 = MessageHandler.THUMBNAIL;
                    msg.obj = new MessageHandler.Holder(OnlineSubsamplingScaleImageView.this, entity, e);
                    messageHandler.sendMessage(msg);
                }
            });
        } else {
            if (StringUtils.isOnlinePicture(url)) {
                // 原图是在线的，需要先去下载，准备加载原图
                loadOnlineImage(url);
            } else {
                // 原图时本地的，直接加载就行
                OnlineSubsamplingScaleImageView.this.setImage(ImageSource.uri(Uri.parse(url)), ImageSource.uri(thumbUrl), state);
            }
        }

    }


    private void loadOnlineImage(String url) {
            DownloadManager.getInstance().requestCacheImage(url, new DownloadCallback(){
                @Override
                public void onStart(DownloadImageEntity entity) {
                    super.onStart(entity);
                    Message msg = new Message();
                    msg.what = MessageHandler.START;
                    msg.arg1 = MessageHandler.NOTTHUMBNAIL;
                    msg.obj = new MessageHandler.Holder(OnlineSubsamplingScaleImageView.this, entity, null);
                    messageHandler.sendMessage(msg);
                }

                @Override
                public void onProgress(DownloadImageEntity entity) {
                    super.onProgress(entity);
                    Message msg = new Message();
                    msg.what = MessageHandler.PROGRESS;
                    msg.arg1 = MessageHandler.NOTTHUMBNAIL;
                    msg.obj = new MessageHandler.Holder(OnlineSubsamplingScaleImageView.this, entity, null);
                    messageHandler.sendMessage(msg);
                }

                @Override
                public void onComplete(DownloadImageEntity entity) {
                    super.onComplete(entity);
                    // 通知下载成功
                    Message msg = new Message();
                    msg.what = MessageHandler.COMPLETE;
                    msg.arg1 = MessageHandler.NOTTHUMBNAIL;
                    msg.obj = new MessageHandler.Holder(OnlineSubsamplingScaleImageView.this, entity, null);
                    messageHandler.sendMessage(msg);
                }

                @Override
                public void onError(DownloadImageEntity entity, Exception e) {
                    super.onError(entity, e);
                    // todo 显示加载失败的图片
                    Message msg = new Message();
                    msg.what = MessageHandler.ERROR;
                    msg.arg1 = MessageHandler.NOTTHUMBNAIL;
                    msg.obj = new MessageHandler.Holder(OnlineSubsamplingScaleImageView.this, entity, e);
                    messageHandler.sendMessage(msg);
                }
            });
    }








    public DownloadCallback getImageDownloadListener() {
        return imageDownloadListener;
    }

    public void setImageDownloadListener(DownloadCallback imageDownloadListener) {
        this.imageDownloadListener = imageDownloadListener;
        messageHandler.setImageDownloadListener(imageDownloadListener);
    }

    public DownloadCallback getThumbnailImageDownloadListener() {
        return thumbnailImageDownloadListener;
    }

    public void setThumbnailImageDownloadListener(DownloadCallback thumbnailImageDownloadListener) {
        this.thumbnailImageDownloadListener = thumbnailImageDownloadListener;
        messageHandler.setThumbnailImageDownloadListener(thumbnailImageDownloadListener);
    }

}
