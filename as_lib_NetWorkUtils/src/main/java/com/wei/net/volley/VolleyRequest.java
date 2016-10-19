package com.wei.net.volley;

import android.text.TextUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.StringRequest;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by wei on 16/8/9.
 */
public class VolleyRequest {

    public static <T> void RequestPost(String url, final RequestCallback rCallback) {
        RequestPost(url, rCallback, null);
    }

    public static <T> void RequestPost( final RequestCallback rCallback) {
        RequestPost(rCallback.getSettings().getUrl(), rCallback, null);
    }

    public  static <T> void RequestPost( final RequestCallback rCallback,final RequestCallbackAgain rCallbackAgain) {
        RequestPost(rCallback.getSettings().getUrl(), rCallback, rCallbackAgain);
    }



    /**
     * Volley二次封装
     *
     * @param url  请求地址
     * @param rCallback  请求结果回调，里面进行了加密、签名、添加公共参数等操作
     * @param rCallbackAgain 某一类的请求结果统一处理，例如获取联系人信息后需要存储信息，那么就可以在这个回调中统一处理，为null时不进行处理
     */
    public static <T> void RequestPost(String url, final RequestCallback<T> rCallback, final RequestCallbackAgain<T> rCallbackAgain) {

        //设置tag，由于本接口中的service参数可做为请求的唯一标识，所以直接使用service作为tag，如果接口中没有合适的参数作为tag，可以在本方法中增加tag参数，单独进行设置
        String service = (String) rCallback.getParams().get(RequestCallback.TAG_KEY_WORD_NAME);
        VolleySettings settings = rCallback.getSettings();
        settings.setTag(service);

        //如果使用缓存也不进行联网
        if (settings.isUseCache()) {
            try {
                String cache = CacheUtils.getCache(rCallback.context, settings.getTag());
                String time = cache.substring(0, cache.indexOf("-"));
                String text = cache.substring(cache.indexOf("-") + 1);
                if (!TextUtils.isEmpty(time) && !TextUtils.isEmpty(cache)) {
                    long t = Long.parseLong(time);
                    long l = System.currentTimeMillis();
                    if (l <= t) {// 缓存已过期
                        rCallback.loadingListener(rCallbackAgain).onResponse(text);
                        return;
                    } else {
                        CacheUtils.removeCacheByKey(rCallback.context, settings.getTag(), settings.isUseUserId());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        StringRequest request = new StringRequest(Request.Method.POST, url, rCallback.loadingListener(rCallbackAgain), rCallback.errorListener(rCallbackAgain)) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                LinkedHashMap params = rCallback.getParams();
                return params == null ? super.getParams() : params;
            }

//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                return rCallback.getHeaders() == null ? super.getHeaders() : rCallback.getHeaders();
//            }

//            @Override
//            public String getBodyContentType() {
//                return "text/xml";
//            }

            //设置请求时间
            @Override
            public Request<?> setRetryPolicy(RetryPolicy retryPolicy) {
                return super.setRetryPolicy(retryPolicy);
            }

            //重写getBody后，getParams方法失效
//            @Override
//            public byte[] getBody() throws AuthFailureError {
//                String body = rCallback.getBody();
//                return TextUtils.isEmpty(body) ? super.getBody() : body.getBytes();
//            }
        };
        //数据处理出现问题
        if (!settings.isSafe()) return;


        //设置请求的Tag标签，可以在全局请求队列中通过Tag标签进行请求的查找
        request.setTag(settings.getTag());

        //将请求加入全局队列中
//        BaseApplication.getHttpQueues().add(request);
    }
}
