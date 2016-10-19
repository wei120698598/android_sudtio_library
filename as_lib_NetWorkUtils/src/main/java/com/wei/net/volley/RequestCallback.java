package com.wei.net.volley;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.ConnectException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by wei on 16/8/24.
 * <p>
 * 针对不是必须重写请求结果回调，可以不使用抽象类和抽象方法，同时可以自行重写请求结果处理方法
 * <p>
 * 如果需求时必须重写请求结果回调，那么要用抽象方法
 */
public class RequestCallback<T> {


    public static final String LOG_TAG = "VolleyInterface";

    public static final String TAG_KEY_WORD_NAME = "cmd";

    private Class<T> clazz;

    public Context context;
    private Response.Listener<String> listener;
    private Response.ErrorListener errorListener;

    private LinkedHashMap<String, String> params = new LinkedHashMap<>();
    private LinkedHashMap<String, String> headers = new LinkedHashMap<>();

    public static final String FAILED_DESC1 = "数据解析异常,请稍后重试!";
    public static final String FAILED_DESC2 = "获取数据失败，请稍后重试!";
    public static final String FAILED_DESC3 = "未知错误，请稍后重试!";
    public static final String ERROR_DESC1 = "参数错误，请稍后重试!";
    private VolleySettings settings;

    /**
     * 请求回调接口
     *
     * @param context      上下文
     * @param bodyParams   所有的请求参数，包含了业务参数和公共参数，不可为null
     * @param headerParams 请求头参数，可为null，无论是否为null都会自动加入公共的请求头参数
     * @throws Exception
     */
    public RequestCallback(Context context, final LinkedHashMap<String, String> bodyParams, final LinkedHashMap<String, String> headerParams, Class<T> clazz) {
        this.context = context;
        this.clazz = clazz;
        settings = new VolleySettings();
        setParams(bodyParams);

        setHeaders(headerParams);
    }

    /**
     * 请求回调接口，没有自定义的请求头参数
     *
     * @param context
     * @param bodyParams
     * @throws Exception
     */
    public RequestCallback(Context context, final LinkedHashMap<String, String> bodyParams, Class<T> clazz) {
        this(context, bodyParams, null, clazz);
    }


    /**
     * 请求回调接口，没有每次调用的特殊参数，没有自定义请求头参数
     */
    public RequestCallback(Context context, Class<T> clazz) {
        this(context, new LinkedHashMap<String, String>(), null, clazz);
    }

    /**
     * 获取到数据，解析结果正常
     *
     * @param result
     */

    public void onMySuccess(T result) {
    }

    /**
     * 无论是否出现异常，都会执行此方法，主要在此方法中进行必须执行的操作，例如停止listView刷新
     */
    public void onMyFinally() {
    }


    /**
     * 请求响应不是200，错误处理，也可以重写此方法
     *
     * @param error
     */
    public void onMyError(VolleyError error) {
        if (settings.getFailToastType() != VolleySettings.FAIL_TOAST_TYPE_DIALOG) {
            String message = error.getMessage();
            Throwable cause = error.getCause();
            if (cause instanceof ConnectException) {
                message = "请检查网络或稍后重试!";
            }
            String msg = TextUtils.isEmpty(message) ? FAILED_DESC2 : message;
            if (settings.getFailToastType() == VolleySettings.FAIL_TOAST_TYPE_TOAST) {
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            } else {
                showToastDialog("", msg);
            }
        }
    }

    /**
     * 获取到数据，但是result_code不为0，错误处理，也可以重写此方法
     *
     * @param result
     */
    public void onMyFailed(String result) {
        if (settings.getFailToastType() != VolleySettings.FAIL_TOAST_TYPE_NULL) {
            try {
                String msg = FAILED_DESC3;
                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.has("desc")) {
                    msg = jsonObject.getString("desc");
                    msg = TextUtils.isEmpty(msg) ? FAILED_DESC3 : msg;
                }
                if (settings.getFailToastType() == VolleySettings.FAIL_TOAST_TYPE_TOAST) {
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                } else if (settings.getFailToastType() == VolleySettings.FAIL_TOAST_TYPE_DIALOG) {
                    showToastDialog("", msg);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 进行公共请求体参数处理，加密，当前没有传递参数，而是传递加密字符串放入body中，所以返回null
     *
     * @return
     */
    public void setParams(LinkedHashMap<String, String> bodyParams) {
        if (bodyParams == null) {
            Toast.makeText(context, ERROR_DESC1, Toast.LENGTH_SHORT).show();
            try {
                throw new Exception(ERROR_DESC1);
            } catch (Exception e) {
                settings.setSafe(false);
                e.printStackTrace();
            }
        } else {
            this.params = bodyParams;
        }
    }


    public LinkedHashMap<String, String> getParams() {
        try {
//            PhoneInfo phoneInfo = BaseApplication.getInstance().getPhoneInfo();
//            params.put("APIVersion", phoneInfo.getApiversion());
//            params.put("Client-Agent", phoneInfo.getClient_agent());
//            params.put("Client-Type", phoneInfo.getClient_type());
//            params.put("Client-Version-Type", phoneInfo.getClient_version_type());
//            params.put("device_id", phoneInfo.getDevice_id());
//            params.put("test", phoneInfo.getSign());
//            long rTime = System.currentTimeMillis();
//            params.put("Rtime", rTime + "");
//            params.put("copyRightId", phoneInfo.getCopyRightId());
//            params.put("IMEI", phoneInfo.getImei());
//
//            String latitude = CacheUtils.getConstantsCache(context, "latitude");
//            String longitude = CacheUtils.getConstantsCache(context, "longitude");
//            if (!TextUtils.isEmpty(latitude) && !TextUtils.isEmpty(longitude)) {
//                params.put("Longitude", longitude);
//                params.put("Latitude", latitude);
//            } else {
//                params.put("Longitude", "0");
//                params.put("Latitude", "0");
//            }

//            String sessionId = CacheUtils.getSessionId(context);
//            if (TextUtils.isEmpty(sessionId)) {
//                Toast.makeText(context, ERROR_DESC1, Toast.LENGTH_SHORT).show();
//                throw new Exception(ERROR_DESC1);
//            } else {
//                params.put("sessionId", sessionId);
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return params;
    }


    /**
     * 进行请求体处理，包括签名加密
     *
     * @return
     */
    public String getBody() {
        //设置请求体
        String body = null;
        try {
            body = URLEncoder.encode(RequestUtils.encrypt(context, params), "UTF-8");
//            Log.i(LOG_TAG, "URLEncoder格式化后----->" + body);
        } catch (Exception e) {
            settings.setSafe(false);
            e.printStackTrace();
        }
        return body;
    }


    public void setHeaders(LinkedHashMap<String, String> headersParams) {
        if (headersParams != null)
            this.headers = headersParams;
    }

    /**
     * 进行公共请求头参数处理,用的时候再加入公共参数
     *
     * @return
     */
    public Map<String, String> getHeaders() {
        try {
//            String sessionId = CacheUtils.getSessionId(context);
//            if (TextUtils.isEmpty(sessionId)) {
//                Toast.makeText(context, ERROR_DESC1, Toast.LENGTH_SHORT).show();
//                throw new Exception(ERROR_DESC1);
//            } else {
//                headers.put("token", sessionId);
//            }

//            String version = GetPhoneInfo.getVersion(context, false);
//            headers.put("version", version);

            String position = "0|0";
            String latitude = CacheUtils.getConstantsCache(context, "latitude");
            String longitude = CacheUtils.getConstantsCache(context, "longitude");
            if (!TextUtils.isEmpty(latitude) && !TextUtils.isEmpty(longitude)) {
                position = longitude + "|" + latitude;
            }
            headers.put("position", position);
            headers.put("oUa", RequestUtils.getPhoneInfo(context));

            headers.put("Charset", "UTF-8");
            headers.put("Content-Type", "text/xml");
        } catch (Exception e) {
            settings.setSafe(false);
            e.printStackTrace();
        }
        return headers;
    }

    /**
     * 进行请求进度显示等操作，并优先进行请求结果统一处理
     *
     * @return
     */


    public Response.Listener<String> loadingListener(final RequestCallbackAgain<T> rCallbackAgain) {
        listener = new MyResponse.MyListener<String>() {
            @Override
            public void onResponse(String response) {
                if (TextUtils.isEmpty(response)) {
//                    Toast.makeText(context, "获取到的数据为空,请稍后重试!", Toast.LENGTH_SHORT).show();
                    if (errorListener != null)
                        errorListener.onErrorResponse(new VolleyError("响应数据为空!"));
                } else {
                    try {
//                        response = RequestDataSign.Aes256Decode(response, RequestUtils.key2);
                        response = URLDecoder.decode(response, "UTF-8").replace("result-code", "result_code");
                        Log.i(LOG_TAG, "请求响应解析后---->" + response);
                        JSONObject jsonObject = new JSONObject(response);
                        JSONObject object = jsonObject.getJSONObject("Response");
                        if (object.has("result_code")) {
                            String code = object.getString("result_code");
                            T result;
                            //将response强转成String，如果出现异常则证明T就是String，否则请求数据格式是bean
                            if (clazz.equals(String.class)) {
                                result = (T) response;
                            } else {
                                result = new Gson().fromJson(response, clazz);
                            }
                            if ("0".equals(code)) {
                                if (rCallbackAgain != null) {
                                    rCallbackAgain.onMySuccess(result);
                                }
                                if (settings.isAllowCache()) {
                                    CacheUtils.saveCache(context, settings.getTag(), response, settings.isUseUserId());
                                }
                                onMySuccess(result);
                            } else {
                                if (rCallbackAgain != null) {
                                    rCallbackAgain.onMyFailed(response);
                                }
                                onMyFailed(response);
                            }
                        } else {
                            Toast.makeText(context, FAILED_DESC1, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(context, FAILED_DESC1, Toast.LENGTH_SHORT).show();
                    }
                }

                if (settings.isShowDialog() && settings.getProgressDialog() != null && settings.getProgressDialog().isShowing()) {
                    settings.getProgressDialog().dismiss();
                }
                onMyFinally();
            }
        };


        return listener;
    }


    /**
     * 进行请求失败结果处理等操作，并优先进行请求结果统一处理
     *
     * @return
     */
    public Response.ErrorListener errorListener(final RequestCallbackAgain rCallbackAgain) {
        errorListener = new MyResponse.MyErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error == null) {
                    Toast.makeText(context, FAILED_DESC3, Toast.LENGTH_SHORT).show();
                } else {
                    if (rCallbackAgain != null) {
                        rCallbackAgain.onMyError(error);
                    }
                    onMyError(error);
                }
                if (settings.isShowDialog() && settings.getProgressDialog() != null && settings.getProgressDialog().isShowing()) {
                    settings.getProgressDialog().dismiss();
                }
                onMyFinally();
            }
        };
        return errorListener;
    }


    public VolleySettings showDialog() {
        return settings.setShowDialog(context, true, "", "正在加载中...", true);
    }

    //错误提示dialog消失监听，主要用来触发跳转页面时机
    private DialogInterface.OnDismissListener errorDialogDismissListener;

    public RequestCallback setErrorDialogDismissListener(DialogInterface.OnDismissListener errorDialogDismissListener) {
        this.errorDialogDismissListener = errorDialogDismissListener;
        return this;
    }

    private void showToastDialog(String title, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setCancelable(false);

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (errorDialogDismissListener != null)
                    errorDialogDismissListener.onDismiss(dialog);
            }
        });
        builder.create().show();
    }


    public RequestCallback putParams(String key, String value) {
        if (params != null) {
            params.put(key, value);
        }
        return this;
    }


    public VolleySettings getSettings() {
        return settings;
    }


    public RequestCallback setUriCmdAction(String uri, String cmd, String action) {
        if (params != null) {
            params.put("Action", action);
            params.put("cmd", cmd);
        }
        settings.setUrl("www.5u51.com" + uri);
        return this;
    }
}
