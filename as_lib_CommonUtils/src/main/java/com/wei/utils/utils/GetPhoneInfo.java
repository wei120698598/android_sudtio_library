package com.wei.utils.utils;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * Created by wei on 2016/7/25.
 */
public class GetPhoneInfo {

    /**
     * 获取DeviceID
     */
    public static String getANDROID_ID(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * 获取IMEI
     * 权限：android.permission.READ_PHONE_STATE
     */
    public static String getIMEI(Context context) {
        return ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
    }

    /**
     * 获取IMSI
     * 权限：android.permission.READ_PHONE_STATE
     */
    public static String getIMSI(Context context) {
        return ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getSubscriberId();
    }


    /**
     * 获取SIM
     * 权限：android.permission.READ_PHONE_STATE
     */
    public static String getSIMNum(Context context) {
        return ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getLine1Number();
    }

    /**
     * 获取手机厂商
     *
     * @return
     */
    public static String getManufacturer() {
        return Build.MANUFACTURER.replace(" ", "-");
    }

    /**
     * 获取序列号sn
     *
     * @param context
     * @return
     */
    public static String getSn(Context context) {
        return ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getSimSerialNumber();
    }

    /**
     * 获取手机型号
     */
    public static String getModel() {
        return Build.MODEL.replace(" ", "-");
    }

    /**
     * 获取手机名称
     */

    public static String getPhoneName() {
        return Build.BRAND.replace(" ", "-");
    }

    /**
     * 获取分辨率
     */
    public static String getScreen(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        dm = context.getResources().getDisplayMetrics();
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
        return screenWidth + "*" + screenHeight;
    }

    /**
     * 获取系统版本
     */
    public static String getSysVersion() {
        return Build.VERSION.RELEASE;
    }

    /**
     * 获取当前应用程序的版本号
     */
    public static String getVersion(Context context, boolean isGetVersionCode) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo packinfo = pm.getPackageInfo(context.getPackageName(), 0);
            String version = packinfo.versionName;
            if (isGetVersionCode)
                version += "_" + packinfo.versionCode;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return "版本号有误";
        }
    }

    /**
     * 获取网络mac地址
     * 权限：android.permission.ACCESS_WIFI_STATE
     *
     * @param context
     * @return
     */
    public static String getWifiMac(Context context) {
        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        String macAddress = wm.getConnectionInfo().getMacAddress();
        return macAddress;
    }

    /**
     * 获取蓝牙mac地址
     * 权限：android.permission.BLUETOOTH  需要打开蓝牙
     *
     * @return
     */
    public static String getBluetoothMac() {
        BluetoothAdapter m_BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        String m_szBTMAC = m_BluetoothAdapter.getAddress();
        return m_szBTMAC;
    }

    public static String getPseudo_Unique_ID() {
        StringBuffer sb = new StringBuffer();
        sb.append("35");
        sb.append(Build.BOARD.length() % 10);
        sb.append(Build.BRAND.length() % 10);
        sb.append(Build.CPU_ABI.length() % 10);
        sb.append(Build.DEVICE.length() % 10);
        sb.append(Build.DISPLAY.length() % 10);
        sb.append(Build.HOST.length() % 10);
        sb.append(Build.ID.length() % 10);
        sb.append(Build.MANUFACTURER.length() % 10);
        sb.append(Build.MODEL.length() % 10);
        sb.append(Build.PRODUCT.length() % 10);
        sb.append(Build.TAGS.length() % 10);
        sb.append(Build.TYPE.length() % 10);
        sb.append(Build.USER.length() % 10);
        return sb.toString();
    }

    /**
     * 手机CPU信息
     */
    private String[] getCpuInfo() {
        String str1 = "/proc/cpuinfo";
        String str2 = "";
        String[] cpuInfo = {"", ""};  //1-cpu型号  //2-cpu频率
        String[] arrayOfString;
        try {
            FileReader fr = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(fr, 8192);
            str2 = localBufferedReader.readLine();
            arrayOfString = str2.split("\\s+");
            for (int i = 2; i < arrayOfString.length; i++) {
                cpuInfo[0] = cpuInfo[0] + arrayOfString[i] + " ";
            }
            str2 = localBufferedReader.readLine();
            arrayOfString = str2.split("\\s+");
            cpuInfo[1] += arrayOfString[2];
            localBufferedReader.close();
        } catch (IOException e) {
        }
        return cpuInfo;
    }


    /**
     * 给设备生成UUID
     */
    private static String sID = null;
    private static final String INSTALLATION = "INSTALLATION";

    public synchronized static String Installtion_ID(Context context) {
        if (sID == null) {
            File installation = new File(context.getFilesDir(), INSTALLATION);
            try {
                if (!installation.exists())
                    writeInstallationFile(installation);
                sID = readInstallationFile(installation);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return sID;
    }

    private static String readInstallationFile(File installation) throws IOException {
        RandomAccessFile f = new RandomAccessFile(installation, "r");
        byte[] bytes = new byte[(int) f.length()];
        f.readFully(bytes);
        f.close();
        return new String(bytes);
    }

    private static void writeInstallationFile(File installation) throws IOException {
        FileOutputStream out = new FileOutputStream(installation);
        String id = UUID.randomUUID().toString();
        out.write(id.getBytes());
        out.close();
    }


    public static String getAppSignature(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            List<PackageInfo> apps = pm.getInstalledPackages(PackageManager.GET_SIGNATURES);
            Iterator<PackageInfo> iter = apps.iterator();
            while (iter.hasNext()) {
                PackageInfo packageinfo = iter.next();
                String packageName = packageinfo.packageName;
                if (packageName.equals(context.getPackageName())) {
                    Signature[] signatures = packageinfo.signatures;
                    return MD5Hexdigest(signatures[0].toByteArray());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String MD5Hexdigest(byte[] paramArrayOfByte) {
        char[] hexDigits = {48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97, 98, 99, 100, 101, 102};
        try {
            MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
            localMessageDigest.update(paramArrayOfByte);
            byte[] arrayOfByte = localMessageDigest.digest();
            char[] arrayOfChar = new char[32];
            int i = 0;
            int j = 0;
            while (true) {
                if (i >= 16)
                    return new String(arrayOfChar);
                int k = arrayOfByte[i];
                int m = j + 1;
                arrayOfChar[j] = hexDigits[(0xF & k >>> 4)];
                j = m + 1;
                arrayOfChar[m] = hexDigits[(k & 0xF)];
                i++;
            }
        } catch (Exception localException) {
            localException.printStackTrace();
        }
        return null;
    }
}
