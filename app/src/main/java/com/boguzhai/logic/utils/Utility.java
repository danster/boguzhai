package com.boguzhai.logic.utils;

import android.graphics.Bitmap;

import com.boguzhai.activity.base.App;
import com.boguzhai.logic.dao.SharedKeys;
import com.boguzhai.logic.thread.HttpPostRunnable;
import com.boguzhai.logic.thread.UpdateUserInfoHandler;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import org.json.JSONException;
import org.json.JSONObject;


public class Utility {

    public static void updateUserInfoByTokenid(){
        HttpRequestApi conn = new HttpRequestApi();
        conn.addParam("m", "getAccountInfo");
        conn.addParam("sessionid", App.settings.getString(SharedKeys.sessionid, ""));
        conn.setUrl("http://www.boguzhai.com/api..jhtml");
        new Thread(new HttpPostRunnable(conn,new UpdateUserInfoHandler())).start();
    }

	public static void updateUserInfo(JSONObject jsonObject){
        try {
            App.isLogin = true;
            App.settings_editor.putString(SharedKeys.sessionid, jsonObject.getString("sessionid"));
            App.settings_editor.commit();

            JSONObject account = jsonObject.getJSONObject("account");

            if(account.has("username")){
                App.account.setUsername(account.getString("username"));
            }else{   App.account.setUsername("未设置");}

            if(account.has("name")){
                App.account.setName(account.getString("name"));
            }else{   App.account.setUsername("未设置");}

            if(account.has("sex")) {
                App.account.setSex(account.getInt("sex"));
            }else{   App.account.setSex(-1);}

            if(account.has("photo_uri")){
                App.account.setPhotoUrl(account.getString("photo_uri"));
            }else{   App.account.setPhotoUrl("");}

            if(account.has("phone")){
                App.account.setPhone(account.getString("phone"));
            }else{   App.account.setPhone("未设置");}

            if(account.has("wxcode")){
                App.account.setWxcode(account.getString("wxcode"));
            }else{   App.account.setWxcode("未设置");}

            if(account.has("birthday")){
                App.account.setBirthday(account.getString("birthday"));
            }else{   App.account.setBirthday("未设置");}

            if(account.has("email")){
                App.account.setEmail(account.getString("email"));
            }else{   App.account.setEmail("未设置");}

        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    public static Bitmap Create2DCode(String str) throws WriterException {
        //生成二维矩阵,编码时指定大小,不要生成了图片以后再进行缩放,这样会模糊导致识别失败
        BitMatrix matrix = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, 300, 300);
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        //二维矩阵转为一维像素数组,也就是一直横着排了
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if(matrix.get(x, y)){
                    pixels[y * width + x] = 0xff000000;
                }

            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        //通过像素数组生成bitmap,具体参考api
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

}

