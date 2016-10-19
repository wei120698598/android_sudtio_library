package com.wei.net.volley;


import android.widget.Toast;

/**
 * Created by Wei on 2016/9/26.
 */

public class VolleyHelp {
    /**
     * 测试
     */
    public static <T> void pocketMoney(String target_user_id, final RequestCallback<T> rCallback) {
        rCallback.putParams("target_user_id", target_user_id);
        rCallback.setUriCmdAction("api", "get", "user");
        rCallback.showDialog();
        VolleyRequest.RequestPost(rCallback, new RequestCallbackAgain<T>() {
            @Override
            public void onMySuccess(T result) throws Exception {
                super.onMySuccess(result);
                Toast.makeText(rCallback.context, "again" + result, Toast.LENGTH_SHORT).show();
            }
        });
    }


}
