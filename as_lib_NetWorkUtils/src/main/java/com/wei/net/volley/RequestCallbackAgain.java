package com.wei.net.volley;

import com.android.volley.VolleyError;

/**
 * Created by Wei on 2016/8/24.
 */

public abstract class RequestCallbackAgain<T> {
    /**
     * 获取到数据，解析结果正常
     *
     * @param result
     */

    public  void onMySuccess(T result) throws Exception{};

    /**
     * 请求响应不是200，错误处理，也可以重写此方法
     *
     * @param error
     */
    public void onMyError(VolleyError error){
    }

    /**
     * 获取到数据，但是result_code不为0，错误处理，也可以重写此方法
     *
     * @param result
     */
    public void onMyFailed(String result) throws Exception{
    }
}
