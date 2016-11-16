package com.jstudio.utils;

import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class Base64Utils {

    /**
     * 将字节数组转换为文件
     *
     * @param bytes    字节数组
     * @param filePath 文件路径
     * @throws Exception
     */
    public static void byteArrayToFile(byte[] bytes, String filePath) throws Exception {
        ByteArrayInputStream localByteArrayInputStream = new ByteArrayInputStream(bytes);
        File localFile = new File(filePath);
        if (!localFile.getParentFile().exists()) {
            localFile.getParentFile().mkdirs();
        }
        localFile.createNewFile();
        FileOutputStream localFileOutputStream = new FileOutputStream(localFile);
        byte[] arrayOfByte = new byte[1024];
        while (true) {
            int i = localByteArrayInputStream.read(arrayOfByte);
            if (i == -1)
                break;
            localFileOutputStream.write(arrayOfByte, 0, i);
            localFileOutputStream.flush();
        }
        localFileOutputStream.close();
        localByteArrayInputStream.close();
    }

    /**
     * 将Base64字符串转换为字节数组
     *
     * @param base64Str Base64字符串
     * @return 字节数组
     * @throws Exception
     */
    public static byte[] decode(String base64Str) throws Exception {
        return Base64.decode(base64Str, Base64.DEFAULT);
    }

    /**
     * 将Base64字符串转换为文件
     *
     * @param filePath  文件路径
     * @param base64Str Base64字符串
     * @throws Exception
     */
    public static void decodeToFile(String filePath, String base64Str) throws Exception {
        byteArrayToFile(decode(base64Str), filePath);
    }

    /**
     * 将字节数组转换为Base64字符串
     *
     * @param bytes 待转换的字节数组
     * @return Base64字符串
     * @throws Exception
     */
    public static String encode(byte[] bytes) throws Exception {
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    /**
     * 将文件转为Base64字符串
     *
     * @param filePath 文件路径
     * @return 由该文件转化得到的Base64字符串
     * @throws Exception
     */
    public static String encodeFile(String filePath) throws Exception {
        return encode(fileToByte(filePath));
    }

    /**
     * 将文件转为字节数组
     *
     * @param filePath 文件路径
     * @return 由该文件转化得到的字节数组
     * @throws Exception
     */
    public static byte[] fileToByte(String filePath) throws Exception {
        byte[] arrayOfByte1 = new byte[0];
        File localFile = new File(filePath);
        if (localFile.exists()) {
            FileInputStream fileInputStream = new FileInputStream(localFile);
            ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream(2048);
            byte[] arrayOfByte2 = new byte[1024];
            while (true) {
                int i = fileInputStream.read(arrayOfByte2);
                if (i == -1)
                    break;
                arrayOutputStream.write(arrayOfByte2, 0, i);
                arrayOutputStream.flush();
            }
            arrayOutputStream.close();
            fileInputStream.close();
            arrayOfByte1 = arrayOutputStream.toByteArray();
        }
        return arrayOfByte1;
    }
}