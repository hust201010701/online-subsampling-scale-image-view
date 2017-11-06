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
    private String saveDir;
    private String filename;
    private double percent;
    private int width;
    private int height;
    private long fileLength;

    public String getTargetFile() {
        if (StringUtils.isEmpty(saveDir)) {
            if (!StringUtils.isEmpty(OnlineSubsamplingScaleImageViewConfig.getCacheDir())) {
                setSaveDir(OnlineSubsamplingScaleImageViewConfig.getCacheDir());
            } else {
                setSaveDir(OnlineSubsamplingScaleImageViewConfig.getAppContext().getExternalCacheDir().getAbsolutePath());
            }
        }
        if (StringUtils.isEmpty(filename)) {
            filename = FileNameGenerator.getName(url);
        }
        String path =  saveDir + File.separator + filename;
        File dir = new File(saveDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(path);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return path;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSaveDir() {
        return saveDir;
    }

    public void setSaveDir(String saveDir) {
        this.saveDir = saveDir;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
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
}
