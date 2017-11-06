package com.orzangleli.library.util;

/**
 * <p>description：
 * <p>===============================
 * <p>creator：lixiancheng
 * <p>create time：2017/11/7 上午12:21
 * <p>===============================
 * <p>reasons for modification：
 * <p>Modifier：
 * <p>Modify time：
 * <p>@version
 */

public class FileNameGenerator {
    public static String getName(String url) {
        return "BigImg_"+Math.abs(url.hashCode())+".png";
    }

}
