package com.wei.net.volley;

import com.android.volley.Response;

/**
 * Created by Wei on 2016/8/25.
 */

public class MyResponse<T> {

    public interface  MyListener<T> extends Response.Listener<T> {
    }

    public interface MyErrorListener extends Response.ErrorListener {
    }

}
