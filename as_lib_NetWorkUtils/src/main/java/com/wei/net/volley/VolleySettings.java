package com.wei.net.volley;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by Wei on 2016/9/26.
 */

public class VolleySettings {
    public static final int FAIL_TOAST_TYPE_NULL = -1;//错误提示类型，什么都不提示
    public static final int FAIL_TOAST_TYPE_TOAST = 0;//错误提示类型，toast提示
    public static final int FAIL_TOAST_TYPE_DIALOG = 1;//错误提示类型，dialog提示


    /**
     * 设置是否允许缓存数据
     */
    private boolean allowCache = false;

    /**
     * 设置是否允许使用缓存数据
     */
    private boolean useCache = false;

    /**
     * 设置是否显示dialog
     */
    private boolean showDialog = false;
    /**
     * 默认缓存过期时间为一天，单位毫秒
     */
    private long expirationTime = 1000 * 60 * 24;
    /**
     * 加载进度
     */
    private ProgressDialog progressDialog;

    /**
     * 设置是否使用UserID标识缓存
     */
    private boolean useUserId = true;

    /**
     * 保存和取得缓存的key，也是request的tag
     */
    private String tag;

    /**
     * 标识整个数据处理过程中是是否出现异常，如果出现异常则为false可进行统一的安全处理或者取消网络连接
     */
    private boolean isSafe = true;


    /**
     * 统一的错误提示，默认是Toast提示
     */
    private int failToastType = FAIL_TOAST_TYPE_TOAST;
    private String url;


    public boolean isUseUserId() {
        return useUserId;
    }

    public VolleySettings setUseUserId(boolean useUserId) {
        this.useUserId = useUserId;
        return this;
    }

    public boolean isAllowCache() {
        return allowCache;
    }

    public VolleySettings setAllowCache(boolean allowCache) {
        this.allowCache = allowCache;
        return this;
    }

    public boolean isUseCache() {
        return useCache;
    }

    public VolleySettings setUseCache(boolean useCache) {
        this.useCache = useCache;
        return this;
    }

    public boolean isShowDialog() {
        return showDialog;
    }

    public VolleySettings setShowDialog(Context context, boolean showDialog, String title, String message, boolean cacleAble) {
        if (showDialog) {
            if (progressDialog == null || (progressDialog != null && !progressDialog.isShowing()))
                progressDialog = ProgressDialog.show(context, title, message, false, cacleAble);
        }
        this.showDialog = showDialog;
        return this;
    }

    public long getExpirationTime() {
        return expirationTime;
    }

    public VolleySettings setExpirationTime(long expirationTime) {
        this.expirationTime = expirationTime;
        return this;
    }

    public ProgressDialog getProgressDialog() {
        return progressDialog;
    }

    public String getTag() {
        return tag;
    }

    public VolleySettings setTag(String tag) {
        this.tag = tag;
        return this;
    }

    public boolean isSafe() {
        return isSafe;
    }

    public void setSafe(boolean safe) {
        isSafe = safe;
    }

    public VolleySettings setFailToastType(int failToastType) {
        this.failToastType = failToastType;
        return this;
    }

    public int getFailToastType() {
        return failToastType;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
