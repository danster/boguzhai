package com.boguzhai.logic.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringApi {
	public static boolean valid; // 是否有效
	public static String tips =""; // 提示信息

	// boolean属性的get方法写法为isXXX()
	public static boolean checkPhoneNumber(String str) {

        Pattern p = Pattern.compile("^(1)\\d{10}$");
        Matcher m = p.matcher(str);
        valid = m.matches();

        if(!valid){
            tips ="请正确填写11位手机号码";
        }
        return valid;
	}

    public static boolean checkEmail(String str) {
        String check = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        Pattern regex = Pattern.compile(check);
        Matcher matcher = regex.matcher(str);
        valid = matcher.matches();
        if(!valid){
            tips ="请正确填写邮箱";
        }
        return valid;
    }



}