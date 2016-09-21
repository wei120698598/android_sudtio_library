package com.wei.net.volley;


/**
 * Created by Wei on 2016/8/24.
 *
 * 主要用于进行某一类的请求统一处理，包括封装每次做同样请求需要的静态参数，例如请求体中的公共参数service
 *
 * 请求结果的统一处理，调用接口请根据注释来使用
 */

public class VolleyRequestHelp {


    /**
     * 接口使用实例
     */
    public static void bindBankCard(String realName, String idCard, String bank_id, String card_no, String type_code, String mobile, final RequestCallback rCallback) {
        String service = "mobile.pocketAccount.bindBankCard";
        rCallback.putParams(RequestCallback.TAG_KEY_WORD_NAME, service)
                .putParams("realName", realName)
                .putParams("idCard", idCard)
                .putParams("bank_id", bank_id)
                .putParams("card_no", card_no)
                .putParams("type_code", type_code)
                .putParams("mobile", mobile)
                .putParams("cvn2", "")
                .putParams("expired", "")
                .putParams("smsCode", "");
        rCallback.setFailToastType(RequestCallback.FAIL_TOAST_TYPE_DIALOG);
        rCallback.showDialog();
        VolleyRequest.RequestPost(RequestUtils.uri, rCallback, new RequestCallbackAgain() {
            @Override
            public void onMySuccess(Object result) throws Exception {
                super.onMySuccess(result);
                //进行公共请求处理
            }
        });
    }


}
