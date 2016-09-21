package com.wei.utils.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 从配置文件中读取键值
 */

public class PropDao {
    private static final String PREFERENCES = "wywy.prop";

    private static class LocaleConfiguration {
        public String locale;
        public int mcc = -1;
        public int mnc = -1;
    }

    private static void readConfiguration(Context context, LocaleConfiguration configuration) {
        DataInputStream in = null;
        try {
            in = new DataInputStream(context.openFileInput(PREFERENCES));
            configuration.locale = in.readUTF();
            configuration.mcc = in.readInt();
            configuration.mnc = in.readInt();
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }
    }

    private static void writeConfiguration(Context context, LocaleConfiguration configuration) {
        DataOutputStream out = null;
        try {
            out = new DataOutputStream(context.openFileOutput(PREFERENCES, Context.MODE_PRIVATE));
            out.writeUTF(configuration.locale);
            out.writeInt(configuration.mcc);
            out.writeInt(configuration.mnc);
            out.flush();
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
            context.getFileStreamPath(PREFERENCES).delete();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * 使用时： Properties.getProperty("key")
     */

    public static String getConfigPropertiesStr(Context context, String key) {
        Properties props = new Properties();
        try {
            InputStream in = context.getAssets().open("wywy.properties");
            // InputStream in = PropDao.class.getResourceAsStream("wywy.properties");

            props.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return props.getProperty(key, "");
    }

    public static int getConfigPropertiesInt(Context context, String key) {
        return Integer.parseInt(getConfigPropertiesStr(context, key));
    }

    /**
     * 从Manifest.xml配置文件中获取数据
     */

    // <meta-data android:name="api_key" android:value="xxx" />
    public static String getMetaValue(Context context, String metaKey) {
        Bundle metaData = null;
        String metaValue = null;
        if (context == null || metaKey == null) {
            return null;
        }
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            if (null != ai) {
                metaData = ai.metaData;
            }
            if (null != metaData) {
                metaValue = metaData.getString(metaKey);
            }
        } catch (NameNotFoundException e) {
        }
        return metaValue;// xxx
    }

}
