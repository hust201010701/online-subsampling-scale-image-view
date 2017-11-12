package com.orzangleli.library.bean;

import com.orzangleli.library.OnlineSubsamplingScaleImageViewConfig;
import com.orzangleli.library.util.FileNameGenerator;
import com.orzangleli.library.util.StringUtils;

import java.io.File;
import java.io.IOException;

/**
 * <p>description：
 * <p>===============================
 * <p>creator：lixiancheng
 * <p>create time：2017/11/6 下午11:13
 * <p>===============================
 * <p>reasons for modification：
 * <p>Modifier：
 * <p>Modify time：
 * <p>@version
 */

 public class DownloadImageEntity {
    private String url;
    private double percent;
    private int width;
    private int height;
    private long fileLength;
    private String failureImage;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public double getPercent() {
        return percent;
    }

    public void setPercent(double percent) {
        this.percent = percent;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public long getFileLength() {
        return fileLength;
    }

    public void setFileLength(long fileLength) {
        this.fileLength = fileLength;
    }

    public String getFailureImage() {
        return failureImage;
    }

    public void setFailureImage(String failureImage) {
        this.failureImage = failureImage;
    }
}
