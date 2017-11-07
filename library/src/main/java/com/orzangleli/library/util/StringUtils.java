package com.orzangleli.library.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

/**
 * <p>description：
 * <p>===============================
 * <p>creator：lixiancheng
 * <p>create time：2017/11/6 下午11:32
 * <p>===============================
 * <p>reasons for modification：
 * <p>Modifier：
 * <p>Modify time：
 * <p>@version
 */

public class StringUtils {

    public static boolean isEmpty (String str) {
        if (str == null || str.isEmpty()) {
            return true;
        }
        return false;
    }
    public static String formatString(final String msg, Object... args) {
        return String.format(Locale.ENGLISH, msg, args);
    }

    public static boolean isOnlinePicture(String url) {
        if (!isEmpty(url) && url.startsWith("http")) {
            return true;
        }
        return false;
    }

    /**
     * 将字符串用MD5编码.
     * 比如在改示例中将url进行MD5编码
     */
    public static String getStringByMD5(String string) {
        String md5String = null;
        try {
            // Create MD5 Hash
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(string.getBytes());
            byte messageDigestByteArray[] = messageDigest.digest();
            if (messageDigestByteArray == null || messageDigestByteArray.length == 0) {
                return md5String;
            }

            // Create hexadecimal String
            StringBuffer hexadecimalStringBuffer = new StringBuffer();
            int length = messageDigestByteArray.length;
            for (int i = 0; i < length; i++) {
                hexadecimalStringBuffer.append(Integer.toHexString(0xFF & messageDigestByteArray[i]));
            }
            md5String = hexadecimalStringBuffer.toString();
            return md5String;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return md5String;
    }


}
