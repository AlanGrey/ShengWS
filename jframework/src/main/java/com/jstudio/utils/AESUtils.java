package com.jstudio.utils;

import android.annotation.SuppressLint;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES加密解密类
 * <p/>
 * Created by Jason
 */
public class AESUtils {

    private Cipher mCiper;
    private final static String HEX = "0123456789ABCDEF";

    @SuppressLint("GetInstance")
    public AESUtils() {
        try {
            mCiper = Cipher.getInstance("AES/ECB/PKCS5Padding");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }

    /**
     * key生成器
     *
     * @param seed 生成key的种子
     * @return Key
     */
    public Key generateKey(byte[] seed) {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "Crypto");
            sr.setSeed(seed);
            keyGenerator.init(128, sr);
            SecretKey secretKey = keyGenerator.generateKey();
            byte[] keyBytes = secretKey.getEncoded();
            return new SecretKeySpec(keyBytes, "AES");
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将字符串加密成字节数组
     *
     * @param key    Key
     * @param source 原字符串
     * @return 加密后的字节数组
     */
    public byte[] encryptToBytes(Key key, String source) {
        try {
            mCiper.init(Cipher.ENCRYPT_MODE, key);
            return mCiper.doFinal(source.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 解密成字节数组
     *
     * @param key       Key
     * @param encrypted 加密的字节数组
     * @return 解密后的字节数组
     */
    public byte[] decryptToBytes(Key key, byte[] encrypted) {
        try {
            mCiper.init(Cipher.DECRYPT_MODE, key);
            return mCiper.doFinal(encrypted);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将字符串转换成16进制字符串
     *
     * @param txt 原字符串
     * @return 转换后的字符串
     */
    public String toHex(String txt) {
        return toHex(txt.getBytes());
    }

    /**
     * 从16进制字符串中解析出原字符串
     *
     * @param hex 16进制字符串
     * @return 原字符串
     */
    public String fromHex(String hex) {
        return new String(toByte(hex));
    }

    /**
     * 将16进制字符串转换成字节数组
     *
     * @param hexString 16进制字符串
     * @return 转换后的字节数组
     */
    public byte[] toByte(String hexString) {
        int len = hexString.length() / 2;
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++)
            result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2), 16).byteValue();
        return result;
    }

    /**
     * 字节数组转换成字符串
     *
     * @param buf 字节数组
     * @return 转换后的字符串
     */
    public String toHex(byte[] buf) {
        if (buf == null)
            return "";
        StringBuffer result = new StringBuffer(2 * buf.length);
        for (byte aBuf : buf) {
            appendHex(result, aBuf);
        }
        return result.toString();
    }

    private void appendHex(StringBuffer sb, byte b) {
        sb.append(HEX.charAt((b >> 4) & 0x0f)).append(HEX.charAt(b & 0x0f));
    }

}
