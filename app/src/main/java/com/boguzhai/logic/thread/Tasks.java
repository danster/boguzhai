package com.boguzhai.logic.thread;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.boguzhai.activity.base.Constant;
import com.boguzhai.activity.base.Variable;
import com.boguzhai.logic.utils.HttpClient;
import com.boguzhai.logic.utils.JsonApi;
import com.boguzhai.logic.utils.Utility;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;

/**
 * Created by danster on 5/6/15.
 * Tasks类主要包含一些网络连接的任务
 */
public class Tasks {

    private static String imageUrl;
    private static ImageView imageView;

    public static void uploadImage(String type, Bitmap bitmap, HttpJsonHandler handler){
        HttpClient conn = new HttpClient();
        conn.setHeader("cookie", "JSESSIONID=" + Variable.account.sessionid);
        conn.setParam("type", type);
        conn.setParamBitmap("fileStr", bitmap);
        conn.setUrl(Constant.url.replace("/phones/","/") + "fileUploadAction!uploadImage.htm");
        new Thread(new HttpPostRunnable(conn, handler)).start();
    }

    public static void updateAccount(){
        HttpClient conn = new HttpClient();
        conn.setUrl(Constant.url + "pClientInfoAction!getAccountInfo.htm");
        new Thread(new HttpPostRunnable(conn, new UpdateHandler())).start();

    }

    public static void getCheckCode(String mobile){
        HttpClient conn = new HttpClient();
        conn.setParam("mobile", mobile);
        conn.setUrl(Constant.url+"pLoginAction!getMobileCheckCode.htm");
        new Thread(new HttpPostRunnable(conn, new GetCheckcodeHandler())).start();
    }

    public static void showImage(String url, ImageView img){
        imageUrl = url;
        imageView = img;
        Log.i("AsyncTask", "http get: "+url);
        new AsyncTask<Void, Void, Void>() {
            Bitmap bmp;
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    BitmapFactory.Options options=new BitmapFactory.Options();
                    options.inJustDecodeBounds = false;
                    options.inSampleSize = 10; //width，hight设为原来的十分之一

                    InputStream in = new URL(imageUrl).openStream();
                    bmp = BitmapFactory.decodeStream(in,null,options);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                if (bmp != null) {
                    Log.i("AsyncTask", "http get: succeed !");
                    imageView.setImageBitmap(bmp);
                }
            }

        }.execute();

    }


}

class UpdateHandler extends HttpJsonHandler {
    @Override
    public void handlerData(int code, JSONObject data){
        switch (code){
            case 0:
                JsonApi.getAccountInfo(data);
                break;
            case -1:
                Utility.gotoLogin();
                break;
            default:
                break;
        }
    }
}

class GetCheckcodeHandler extends HttpJsonHandler {
    @Override
    public void handlerData(int code, JSONObject data){
        switch(code){
            case 0:
                Utility.toastMessage("发送验证码成功，请注意查收");
                break;
            case 1:
                Utility.toastMessage("发送验证码失败，请重新获取验证码");
                break;
            default:
                break;
        }
    }
}
