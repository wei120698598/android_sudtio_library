package com.wei.utils.utils;

import java.util.List;

/**
 * Created by wei on 2016/7/9.
 */
public class TextUtils {

    /**
     * 获取不为null的字符串
     *
     * @param text
     * @return
     */
    public static String getTextNotNull(String text) {
        if (text == null) {
            return "";
        } else {
            return text;
        }
    }

    /**
     * 判断字符串是否为空
     *
     * @param text
     * @return
     */
    public static boolean isEmpty(String text) {
        if (text == null || "".equals(text) || text.length() == 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * String 转 double
     *
     * @param text
     * @return
     */
    public static double getDouble(String text) {
        try {
            Double d = Double.valueOf(text);
            return d;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * String 转 int
     *
     * @param text
     * @return
     */
    public static int getInt(String text) {
        try {
            int d = Integer.valueOf(text);
            return d;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Stirng 转 float
     *
     * @param text
     * @return
     */
    public static float getFloat(String text) {
        try {
            float d = Float.valueOf(text);
            return d;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
    }


    /**
     * 判断是否是中文
     *
     * @param chineseStr
     * @return
     */
    public static boolean isChineseCharacter(String chineseStr) {
        char[] charArray = chineseStr.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            if ((charArray[i] >= '\u0000' && charArray[i] < '\uFFFD') || ((charArray[i] > '\uFFFD' && charArray[i] < '\uFFFF'))) {
                continue;
            } else {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断集合是否为空
     * @param list
     * @return
     */
    public static boolean isEmpty(List list) {
        if (list == null || list.size() == 0) {
            return true;
        } else {
            return false;
        }
    }

}
