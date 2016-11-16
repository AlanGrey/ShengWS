package com.jstudio.utils;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 常用正则表达式
 * <p/>
 * Created by Jason
 */
public class RegularExpUtils {

    /**
     * 判断是否为email地址
     *
     * @param mail email地址
     * @return true表示当前是email
     */
    public static boolean isMail(String mail) {
        boolean flag;
        try {
            String check = "^[\\w-]+(\\.[\\w-]+)*@[\\w-]+(\\.[\\w-]+)+$";
            Pattern regex = Pattern.compile(check);
            Matcher matcher = regex.matcher(mail);
            flag = matcher.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

    /**
     * 判断是否为手机号码
     *
     * @param mobile 手机号码
     * @return true表示当前是手机号码
     */
    public static boolean isMobile(String mobile) {
        return mobile.matches("^[1][3,5,8,4,7]+\\d{9}");
    }

    /**
     * 判断字符串中是否含有数字
     *
     * @param paramString 待验证的字符串
     * @return true表示字符串中有数字
     */
    public static boolean isNumeric(String paramString) {
        if (TextUtils.isEmpty(paramString)) {
            return false;
        }
        int i = paramString.length();
        for (int j = 0; ; j++) {
            if (j >= i)
                break;
            if (!Character.isDigit(paramString.charAt(j)))
                break;
        }
        return true;
    }

    public static boolean isStartWithLetter(String paramString) {
        if (TextUtils.isEmpty(paramString)) {
            return false;
        }
        char i = paramString.charAt(0);
        if ((i >= 65 && i <= 90) || (i >= 97 && i <= 122)) {
            return true;
        } else {
            return false;
        }
    }

}
