package com.boguzhai.logic.utils;

public class StringApi {
	public static boolean valid; // 是否有效
	public static String tips =""; // 提示信息

	// boolean属性的get方法写法为isXXX()
	public static boolean checkPhoneNumber(String str) {
		
		if(str.length() != 11){
			tips ="须填写11位手机号码";
			valid = false;
			return valid;
		}

        // 字符串转换为字符数组
        char cArr[] = str.toCharArray();
        if(cArr[0] != '1'){
            tips ="须填写11位手机号码";
            valid = false;
            return valid;
        }

        for (int i = 1; i < cArr.length; i++) {
            if(cArr[i] < '0' || cArr[i] > '9' ){
                tips ="须填写11位手机号码";
                valid = false;
                return valid;
            }
        }

        valid = true;
        return valid;
	}

}