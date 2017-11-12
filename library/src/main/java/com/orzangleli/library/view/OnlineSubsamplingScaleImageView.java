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

import static com.orzangleli.library.callback.MessageHandler.FILE_SCHEME;

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

    /**
     * 不建议使用(在线的缩略图,本地的原图) 以及 (本地的缩略图和本地的原图) 这样的组合，即在原图时本地存在时，不建议使用缩略图，如果想实现缩略图最好自己实现一个控件，覆盖在上面
     * @param url
     * @param thumbUrl
     */
    public void setImageUri(String url, String thumbUrl) {
        this.setImageUri(url, thumbUrl, null);
    }

    public void setImageUri(String url, ImageViewState state) {
        this.setImageUri(url, null, state);
    }

    private void setImageUri(String iurl, String ithumbUrl, final ImageViewState state) {

        LocalCacheHolder localCacheHolder = getLocalCache(iurl, ithumbUrl);
        final String url = localCacheHolder.iurl;
        final String thumbUrl = localCacheHolder.ithumbUrl;

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
                    // 显示下载缩略图文件
                    final String path = DownloadManager.getInstance().getCacheFileByUrl(entity.getUrl());
                    if (path != null) {
                        OnlineSubsamplingScaleImageView.this.post(new Runnable() {
                            @Override
                            public void run() {
                                OnlineSubsamplingScaleImageView.this.setImage(ImageSource.uri(Uri.parse(FILE_SCHEME+path)));
                            }
                        });
                    }

                    if (StringUtils.isOnlinePicture(url)) {
                        // 原图是在线的，需要先去下载，准备加载原图
                        loadOnlineImage(url);
                    } else {
                        if (!StringUtils.isEmpty(url)) {
                            DownloadImageEntity entity2 = new DownloadImageEntity();
                            entity2.setUrl(url);
                            if (imageDownloadListener != null) {
                                imageDownloadListener.onStart(entity2);
                            }
                            // 原图是本地的，直接加载就行
                            OnlineSubsamplingScaleImageView.this.post(new Runnable() {
                                @Override
                                public void run() {
                                    OnlineSubsamplingScaleImageView.this.setImage(ImageSource.uri(Uri.parse(url)), state);
                                }
                            });
                            if (imageDownloadListener != null) {
                                DownloadManager.calculateImageSizeAndFileLength(entity2);
                                imageDownloadListener.onComplete(entity2);
                            }
                        }
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
                DownloadImageEntity thumbEntity = new DownloadImageEntity();
                thumbEntity.setUrl(thumbUrl);
                if (thumbnailImageDownloadListener != null) {
                    thumbnailImageDownloadListener.onStart(thumbEntity);
                }
                if (!StringUtils.isEmpty(thumbUrl)) {
                    OnlineSubsamplingScaleImageView.this.post(new Runnable() {
                        @Override
                        public void run() {
                            OnlineSubsamplingScaleImageView.this.setImage(ImageSource.uri(thumbUrl), state);
                        }
                    });
                }
                if (thumbnailImageDownloadListener != null) {
                    DownloadManager.calculateImageSizeAndFileLength(thumbEntity);
                    thumbnailImageDownloadListener.onComplete(thumbEntity);
                }
                // 原图是在线的，需要先去下载，准备加载原图
                loadOnlineImage(url);

            } else {
                DownloadImageEntity entity = new DownloadImageEntity();
                entity.setUrl(url);
                if (imageDownloadListener != null) {
                    imageDownloadListener.onStart(entity);
                }
                // 如果缩略图和原图都是本地图片，直接显示原图
                OnlineSubsamplingScaleImageView.this.post(new Runnable() {
                    @Override
                    public void run() {
                        OnlineSubsamplingScaleImageView.this.setImage(ImageSource.uri(url), state);
                    }
                });
                if (imageDownloadListener != null) {
                    DownloadManager.calculateImageSizeAndFileLength(entity);
                    imageDownloadListener.onComplete(entity);
                }
            }
        }

    }

    private LocalCacheHolder getLocalCache(String iurl, String ithumbUrl) {
        String path = DownloadManager.getInstance().getCacheFileByUrl(iurl);
        if (!StringUtils.isEmpty(path)) {
            iurl = path;
        }
        String thumbPath = DownloadManager.getInstance().getCacheFileByUrl(ithumbUrl);
        if (!StringUtils.isEmpty(thumbPath)) {
            ithumbUrl = thumbPath;
        }
        return new LocalCacheHolder(iurl, ithumbUrl);
    }

    class LocalCacheHolder {
        String iurl;
        String ithumbUrl;

        public LocalCacheHolder(String iurl, String ithumbUrl) {
            this.iurl = iurl;
            this.ithumbUrl = ithumbUrl;
        }
    }

    public void loadOnlineImage(String url) {
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

                    // 显示下载缩略图文件
                    final String path = DownloadManager.getInstance().getCacheFileByUrl(entity.getUrl());
                    if (path != null) {
                        OnlineSubsamplingScaleImageView.this.post(new Runnable() {
                            @Override
                            public void run() {
                                OnlineSubsamplingScaleImageView.this.setImage(ImageSource.uri(Uri.parse(FILE_SCHEME+path)));
                            }
                        });
                    }

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
