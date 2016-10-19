package com.wei.net.volley;

/*  * 文 件 名:  DataCleanManager.java  * 描    述:  主要功能有清除内/外缓存，清除数据库，清除sharedPreference，清除files和清除自定义目录  */

/*  * 文 件 名:  DataCleanManager.java
 * * 描    述:  主要功能有清除内/外缓存，清除数据库，清除sharedPreference，清除files和清除自定义目录
 * */

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.text.TextUtils;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import java.io.File;
import java.math.BigDecimal;

/**
 * 本应用数据清除管理器
 */
public class CacheUtils {
    /**
     * * 清除本应用内部缓存(/data/data/com.xxx.xxx/cache) * *
     *
     * @param context
     */
    public static void cleanInternalCache(Context context) {
        deleteFilesByDirectory(context.getCacheDir());
    }

    /**
     * * 清除本应用所有数据库(/data/data/com.xxx.xxx/databases) * *
     *
     * @param context
     */
    public static void cleanDatabases(Context context) {
        deleteFilesByDirectory(new File("/data/data/" + context.getPackageName() + "/databases"));
    }

    /**
     * * 清除本应用SharedPreference(/data/data/com.xxx.xxx/shared_prefs) *
     *
     * @param context
     */
    public static void cleanSharedPreference(Context context) {
        deleteFilesByDirectory(new File("/data/data/" + context.getPackageName() + "/shared_prefs"));
    }

    /**
     * * 按名字清除本应用数据库 * *
     *
     * @param context
     * @param dbName
     */
    public static void cleanDatabaseByName(Context context, String dbName) {
        context.deleteDatabase(dbName);
    }

    /**
     * * 清除/data/data/com.xxx.xxx/files下的内容 * *
     *
     * @param context
     */
    public static void cleanFiles(Context context) {
        deleteFilesByDirectory(context.getFilesDir());
    }

    /**
     * * 清除外部cache下的内容(/mnt/sdcard/android/data/com.xxx.xxx/cache)
     *
     * @param context
     */
    public static void cleanExternalCache(Context context) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            deleteFilesByDirectory(context.getExternalCacheDir());
        }
    }

    /**
     * * 清除自定义路径下的文件，使用需小心，请不要误删。而且只支持目录下的文件删除 * *
     *
     * @param filePath
     */
    public static void cleanCustomCache(String filePath) {
        deleteFilesByDirectory(new File(filePath));
    }

    /**
     * * 清除本应用所有的数据 * *
     *
     * @param context
     * @param filepath
     */
    public static void cleanApplicationData(Context context, String... filepath) {
        cleanInternalCache(context);
        cleanExternalCache(context);
        cleanDatabases(context);
        cleanSharedPreference(context);
        cleanFiles(context);
        if (filepath == null) {
            return;
        }
        for (String filePath : filepath) {
            cleanCustomCache(filePath);
        }
    }

    /**
     * * 删除方法 这里只会删除某个文件夹下的文件，如果传入的directory是个文件，将不做处理 * *
     *
     * @param directory
     */
    private static void deleteFilesByDirectory(File directory) {
        if (directory != null && directory.exists() && directory.isDirectory()) {
            for (File item : directory.listFiles()) {
                item.delete();
            }
        }
    }

    /**
     * 递归删除
     *
     */
    public static void deleteFiles(File file) {
        if (file != null && file.exists() && file.isDirectory()) {
            try {
                File[] fileList = file.listFiles();
                for (int i = 0; i < fileList.length; i++) {
                    // 如果下面还有文件
                    if (fileList[i].isDirectory()) {
                        deleteFiles(fileList[i]);
                    } else {
                        fileList[i].delete();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取文件 Context.getExternalFilesDir() --> SDCard/Android/data/你的应用的包名/files/
     * 目录，一般放一些长时间保存的数据
     * Context.getExternalCacheDir()-->SDCard/Android/data/你的应用包名
     * /cache/目录，一般存放临时缓存数据
     */
    public static long getFolderSize(File file) throws Exception {
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                // 如果下面还有文件
                if (fileList[i].isDirectory()) {
                    size = size + getFolderSize(fileList[i]);
                } else {
                    size = size + fileList[i].length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    /**
     * 删除指定目录下文件及目录
     *
     * @param deleteThisPath
     * @return
     */
    public static void deleteFolderFile(String filePath, boolean deleteThisPath) {
        if (!TextUtils.isEmpty(filePath)) {
            try {
                File file = new File(filePath);
                if (file.isDirectory()) {// 如果下面还有文件
                    File files[] = file.listFiles();
                    for (int i = 0; i < files.length; i++) {
                        deleteFolderFile(files[i].getAbsolutePath(), true);
                    }
                }
                if (deleteThisPath) {
                    if (!file.isDirectory()) {// 如果是文件，删除
                        file.delete();
                    } else {// 目录
                        if (file.listFiles().length == 0) {// 目录下没有文件或者目录，删除
                            file.delete();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 格式化单位
     *
     * @param size
     * @return
     */
    public static String getFormatSize(double size) {
        double kiloByte = size / 1024;
        if (kiloByte < 1) {
            if (kiloByte == 0) {
                return "0.00M";
            }
            return size + "Byte";
        }

        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "KB";
        }

        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "MB";
        }

        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB";
    }

    public static String getCacheSize(File file) throws Exception {
        return getFormatSize(getFolderSize(file));
    }

    @SuppressWarnings("deprecation")
    public static void removeCookie(Context context) {
        CookieSyncManager.createInstance(context);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
        CookieManager.getInstance().removeSessionCookie();
        CookieSyncManager.getInstance().sync();
        CookieSyncManager.getInstance().startSync();
        deleteFolderFile("/data/data/" + context.getPackageName() + "app_webview", true);
    }

    /**
     * 清除图片的缓存
     */
    public static void cleanImageLoader() {
//        ImageLoader.getInstance().clearDiskCache();
//        ImageLoader.getInstance().clearMemoryCache();
    }

    /**
     * 清除缓存
     */
    public static void cleanCache(Context context, boolean clearSP) {
//        ImageLoader.getInstance().clearDiskCache();
//        ImageLoader.getInstance().clearMemoryCache();

        // deleteFiles(Environment.getExternalStorageDirectory());
        deleteFiles(context.getCacheDir());
        deleteFiles(context.getFilesDir());
        deleteFiles(context.getExternalCacheDir());
//        deleteFiles(BaseApplication.getInstance().cacheDir);
        if (clearSP) {
            cleanSharedPreference(context);
        }
        deleteFiles(new File("/data/data/" + context.getPackageName() + "app_webview"));
    }

    /**
     * 获取缓存大小
     */

    public static String getCacheSize(Context context) {
        double size = 0;
        try {
            // size +=
            // getFolderSize(Environment.getExternalStorageDirectory());// 外置储存卡

            size += getFolderSize(context.getExternalCacheDir());
            size += getFolderSize(context.getFilesDir());
            size += getFolderSize(context.getCacheDir());
//            size += getFolderSize(BaseApplication.getInstance().cacheDir);
            return getFormatSize(size);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static void saveCache(Context context, String cachekey, String cacheValue, boolean useUserID) {
        SharedPreferences sp = context.getSharedPreferences(getConfigName(context, useUserID), Context.MODE_MULTI_PROCESS | Context.MODE_PRIVATE);
        sp.edit().putString(cachekey, cacheValue).commit();
    }

    public static void saveCache(Context context, String cachekey, String cacheValue) {
        saveCache(context, cachekey, cacheValue, true);
    }

    public static String getCache(Context context, String cacheKey, boolean useUserID) {
        SharedPreferences sp = context.getSharedPreferences(getConfigName(context, useUserID), Context.MODE_MULTI_PROCESS | Context.MODE_PRIVATE);
        return sp.getString(cacheKey, "");
    }

    public static String getCache(Context context, String cachekey) {
        return getCache(context, cachekey, true);
    }

    // ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static void saveCache(Context context, String cachekey, boolean cacheValue, boolean useUserID) {
        SharedPreferences sp = context.getSharedPreferences(getConfigName(context, useUserID), Context.MODE_MULTI_PROCESS | Context.MODE_PRIVATE);
        sp.edit().putBoolean(cachekey, cacheValue).commit();
    }

    public static void saveCache(Context context, String cachekey, boolean cacheValue) {
        saveCache(context, cachekey, cacheValue, true);
    }

    public static boolean getCacheBoolean(Context context, String cacheKey, boolean useUserID) {
        SharedPreferences sp = context.getSharedPreferences(getConfigName(context, useUserID), Context.MODE_MULTI_PROCESS | Context.MODE_PRIVATE);
        return sp.getBoolean(cacheKey, false);
    }

    public static boolean getCacheBoolean(Context context, String cacheKey) {
        return getCacheBoolean(context, cacheKey, true);
    }

    // ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static void saveConstantsCache(Context context, String cachekey, String cacheValue, boolean useUserID) {
        SharedPreferences sp = context.getSharedPreferences(getConfigName(context, useUserID), Context.MODE_MULTI_PROCESS | Context.MODE_PRIVATE);
        sp.edit().putString(cachekey, cacheValue).commit();
    }

    public static void saveConstantsCache(Context context, String cachekey, String cacheValue) {
        saveConstantsCache(context, cachekey, cacheValue, true);
    }


    public static String getConstantsCache(Context context, String cacheKey, boolean useUserID) {
        SharedPreferences sp = context.getSharedPreferences(getConfigName(context, useUserID), Context.MODE_MULTI_PROCESS | Context.MODE_PRIVATE);
        return sp.getString(cacheKey, "");
    }

    public static String getConstantsCache(Context context, String cacheKey) {
        return getConstantsCache(context, cacheKey, true);
    }
    public static String getUserId(Context context) {
        return getConstantsCache(context, "user_id", true);
    }


    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static void saveConstantsIntCache(Context context, String cachekey, int cacheValue, boolean useUserID) {
        SharedPreferences sp = context.getSharedPreferences(getConfigName(context, useUserID), Context.MODE_MULTI_PROCESS | Context.MODE_PRIVATE);
        sp.edit().putInt(cachekey, cacheValue).commit();
    }

    public static void saveConstantsIntCache(Context context, String cachekey, int cacheValue) {
        saveConstantsIntCache(context, cachekey, cacheValue, true);
    }

    public static int getConstantsIntCache(Context context, String cacheKey, boolean useUserID) {
        SharedPreferences sp = context.getSharedPreferences(getConfigName(context, useUserID), Context.MODE_MULTI_PROCESS | Context.MODE_PRIVATE);
        return sp.getInt(cacheKey, 0);
    }

    public static int getConstantsIntCache(Context context, String cacheKey) {
        return getConstantsIntCache(context, cacheKey, true);
    }

    // ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static void saveConstantsCache(Context context, String cachekey, boolean cacheValue, boolean useUserID) {
        SharedPreferences sp = context.getSharedPreferences(getConfigName(context, useUserID), Context.MODE_MULTI_PROCESS | Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putBoolean(cachekey, cacheValue);
        edit.commit();
    }

    public static void saveConstantsCache(Context context, String cachekey, boolean cacheValue) {
        saveConstantsCache(context, cachekey, cacheValue, true);
    }

    public static boolean getConstantsBooleanCache(Context context, String cacheKey, boolean useUserID) {
        SharedPreferences sp = context.getSharedPreferences(getConfigName(context, useUserID), Context.MODE_MULTI_PROCESS | Context.MODE_PRIVATE);
        return sp.getBoolean(cacheKey, false);
    }

    public static boolean getConstantsBooleanCache(Context context, String cacheKey) {
        return getConstantsBooleanCache(context, cacheKey, true);
    }

    public static boolean getConstantsBooleanCacheTrue(Context context, String cacheKey, boolean useUserID) {
        SharedPreferences sp = context.getSharedPreferences(getConfigName(context, useUserID), Context.MODE_MULTI_PROCESS | Context.MODE_PRIVATE);
        return sp.getBoolean(cacheKey, true);
    }

    public static boolean getConstantsBooleanCacheTrue(Context context, String cacheKey) {
        SharedPreferences sp = context.getSharedPreferences(getConfigName(context, true), Context.MODE_MULTI_PROCESS | Context.MODE_PRIVATE);
        return sp.getBoolean(cacheKey, true);
    }
    // ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static String getConfigName(Context context, boolean useUserID) {
        String userId = getUserId(context);
        if (TextUtils.isEmpty(userId)) {
            userId = "";
        }
        if (!useUserID) {
            userId = "";
        }
        return "config" + userId;
    }

    public static void removeCacheByKey(Context context, String key, boolean useUserID) {
        SharedPreferences sp = context.getSharedPreferences(getConfigName(context, useUserID), Context.MODE_MULTI_PROCESS | Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.remove(key);
        edit.commit();
    }

}
