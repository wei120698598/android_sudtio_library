package com.wei.net.volley;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.wei.net.volley.RequestCallback.ERROR_DESC1;


/**
 * Created by Wei on 2016/8/22.
 */

public class RequestUtils {
    private static String oUa = null;

    public static final String key1 = "wywy_payment";
    public static final String key2 = "2a32555b228d4c02350f9a93d5835ed5";

    public static final String uri = "http://pay.5u51.cn/wywy_payment/core ";

//    public static final String uri = "http://192.168.1.3:8888/wywy_payment/core ";


    /**
     * 手机信息
     *
     * @param context
     * @return
     */
    public static String getPhoneInfo(Context context) {
//        oUa = "0|0|0|0|0|0|0|0|0";
        oUa = "";
        if (TextUtils.isEmpty(oUa)) {
//            String wifiMac = GetPhoneInfo.getWifiMac(context);
//            String phoneName = GetPhoneInfo.getPhoneName();
//            String sysVersion = GetPhoneInfo.getSysVersion();
//            String screen = GetPhoneInfo.getScreen();
//            String UUID = GetPhoneInfo.getIMEI(context);
//            String imsi = GetPhoneInfo.getIMSI(context);
//            String simNum = GetPhoneInfo.getSIMNum(context);
//            String manufacturer = GetPhoneInfo.getManufacturer();
//            oUa += TextUtils.isEmpty(wifiMac) ? "0|" : wifiMac + "|";
//            oUa += TextUtils.isEmpty(phoneName) ? "0|" : phoneName + "|";
//            oUa += TextUtils.isEmpty(sysVersion) ? "0|" : sysVersion + "|";
//            oUa += TextUtils.isEmpty(screen) ? "0|" : screen + "|";
//            oUa += TextUtils.isEmpty(UUID) ? "0|" : UUID + "|";
//            oUa += TextUtils.isEmpty(imsi) ? "0|" : imsi + "|";
//            oUa += TextUtils.isEmpty(simNum) ? "0|" : simNum + "|";
//            oUa += TextUtils.isEmpty(manufacturer) ? "0" : manufacturer + "";
        }


        return oUa;
    }

    /**
     * 签名、加密
     *
     * @param context
     * @param params
     * @return
     * @throws Exception
     */
    @NonNull
    public static String encrypt(Context context, Map<String, String> params) throws Exception {
        //设置appId
//        String appId = BaseApplication.getPid() + "_android_" + BaseApplication.getCid() + "_01";
//        params.put("appId", appId);

        //排序
        LinkedHashMap<String, String> orderMap = RequestUtils.getOrder(params);

        Map<String, String> common = new HashMap<>();
        Map<String, String> business = new HashMap<>();

        StringBuffer stringBuffer = new StringBuffer();

        Set<String> keySet = orderMap.keySet();
        for (String key : keySet) {
            //键不能为空
            if (TextUtils.isEmpty(key)) {
                showToast(context, ERROR_DESC1);
                throw new Exception(ERROR_DESC1);
            }

            String value = orderMap.get(key);
            //值可以为null或""
//            if (TextUtils.isEmpty(value)) {
//                showToast(context, ERROR_DESC1);
//                throw new Exception(ERROR_DESC1);
//            }

            if ("service".equals(key) || "appId".equals(key) || "sign".equals(key)) {
                common.put(key, value);
            } else {
                business.put(key, value);
            }
            if (stringBuffer != null && stringBuffer.length() != 0 && !stringBuffer.equals(""))//如果不是第一个就先插入&符号
                stringBuffer.append("&");
            stringBuffer.append(value);
        }

        stringBuffer.append(RequestUtils.key1);

        //做SHA1加密
        String sha1String = RequestDataSign.SHA1(stringBuffer.toString());
        if (TextUtils.isEmpty(sha1String)) {
            showToast(context, ERROR_DESC1);
            throw new Exception(ERROR_DESC1);
        }
        common.put("sign", sha1String);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("common", new JSONObject(common));
        jsonObject.put("business", new JSONObject(business));

        String text = jsonObject.toString();
        if (TextUtils.isEmpty(text)) {
            showToast(context, ERROR_DESC1);
            throw new Exception(ERROR_DESC1);
        }
        Log.i(RequestCallback.LOG_TAG, "参数生成的json----->" + text);

        //将所有的参数包括sign进行AES加密
        String encrypt = RequestDataSign.encrypt(text, RequestUtils.key2);

        if (TextUtils.isEmpty(encrypt)) {
            showToast(context, ERROR_DESC1);
            throw new Exception(ERROR_DESC1);
        }

//        Log.i(RequestCallback.LOG_TAG, "AES加密后------>" + encrypt);

        return encrypt;
    }


    /**
     * map 按字母顺序排序
     *
     * @param map
     * @return
     */
    public static LinkedHashMap<String, String> getOrder(Map<String, String> map) {
        List<Map.Entry<String, String>> infoIds = new ArrayList<>(map.entrySet());

        //排序
        Collections.sort(infoIds, new Comparator<Map.Entry<String, String>>() {
            public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
                return o1.getKey().compareToIgnoreCase(o2.getKey());
            }
        }); 
         
        /*转换成新map输出*/
        LinkedHashMap<String, String> newMap = new LinkedHashMap<>();

        for (Map.Entry<String, String> entity : infoIds) {
            newMap.put(entity.getKey(), entity.getValue());
        }

        return newMap;
    }

    public static void showToast(final Context context, final String msg) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * 针对应用获取json值
     *
     * @param jsonString
     * @param key
     * @return
     */
    public static String getStringByKey(String jsonString, String key) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            if (jsonObject.has(key)) {
                return jsonObject.getString(key);
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 根据Json串获取字符串，Response的响应数据
     *
     * @param jsonStr
     * @param key
     * @return
     */
    public static String getStringByStr(String jsonStr, String key) {
        String value = null;
        try {
            if (android.text.TextUtils.isEmpty(jsonStr)) {
//                LogUtils.myI("getStringByStr---->json数据为空");
                return null;
            }
            JSONObject jsonObject = new JSONObject(jsonStr);
            JSONObject object = jsonObject.getJSONObject("Response");
            if (object.has(key)) {
                value = object.getString(key).trim();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }
}
