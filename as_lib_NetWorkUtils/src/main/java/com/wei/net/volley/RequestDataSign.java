package com.wei.net.volley;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;


/**
 * Created by Wei on 2016/8/23.
 */

public class RequestDataSign {
    public static String SHA1(String decript) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.update(decript.getBytes());
            byte messageDigest[] = digest.digest();
            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            // 字节数组转换为 十六进制 数
            for (int i = 0; i < messageDigest.length; i++) {
                String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
                if (shaHex.length() < 2) {
                    hexString.append(0);
                }
                hexString.append(shaHex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }


    /**
     * 加密
     *
     * @param content  需要加密的内容
     * @param password 加密密码
     * @return
     */
    public static String encryptAES(String content, String password) {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            kgen.init(256, new SecureRandom(password.getBytes()));
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
            Cipher cipher = Cipher.getInstance("AES");// 创建密码器
            byte[] byteContent = content.getBytes("utf-8");
            cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化
            byte[] result = cipher.doFinal(byteContent);
            return Base64.encodeToString(result, Base64.DEFAULT); // 加密
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String encrypt(String content, String password) {
        try {
            //"AES"：请求的密钥算法的标准名称
//            KeyGenerator kgen = KeyGenerator.getInstance("AES");
//            //256：密钥生成参数；securerandom：密钥生成器的随机源
//            SecureRandom securerandom = new SecureRandom(tohash256Deal(password));
////            SecretKeySpec securerandom = new SecretKeySpec(key, "AES");
//            kgen.init(256, securerandom);
//            //生成秘密（对称）密钥
//            SecretKey secretKey = kgen.generateKey();
//            //返回基本编码格式的密钥
//            byte[] enCodeFormat = secretKey.getEncoded();
            //根据给定的字节数组构造一个密钥。enCodeFormat：密钥内容；"AES"：与给定的密钥内容相关联的密钥算法的名称
            SecretKeySpec key = new SecretKeySpec(password.getBytes(), "AES");
            //将提供程序添加到下一个可用位置
//            Security.addProvider(new BouncyCastleProvider());
            //创建一个实现指定转换的 Cipher对象，该转换由指定的提供程序提供。
            //"AES/ECB/PKCS7Padding"：转换的名称；"BC"：提供程序的名称
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding", "BC");

            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] cryptograph = cipher.doFinal(content.getBytes("utf-8"));
            return Base64.encodeToString(cryptograph, Base64.NO_WRAP);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param content  要被解密的字节数组
     * @param password 加/解密要用的长度为32的字节数组（256位）密钥
     * @return String  解密后的字符串
     */
    public static String Aes256Decode(String content, String password) {
        String result = null;
        try {
//            Security.addProvider(new BouncyCastleProvider());
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding", "BC");
            SecretKeySpec keySpec = new SecretKeySpec(password.getBytes("UTF-8"), "AES"); //生成加密解密需要的Key
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] decoded = cipher.doFinal(Base64.decode(content, Base64.NO_WRAP));
            result = new String(decoded, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    private static byte[] tohash256Deal(String datastr) {
        try {
            MessageDigest digester = MessageDigest.getInstance("SHA-256");
            digester.update(datastr.getBytes());
            byte[] hex = digester.digest();
            return hex;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 将二进制转换成16进制
     *
     * @param buf
     * @return
     */
    public static String parseByte2HexStr(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * MD5加密
     *
     * @param paramArrayOfByte
     * @return
     */
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



