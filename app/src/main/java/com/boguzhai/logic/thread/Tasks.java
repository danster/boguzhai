package com.boguzhai.logic.thread;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.boguzhai.R;
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
    private static int imageRatio=1;

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
        conn.setHeader("cookie", "JSESSIONID=" + Variable.account.sessionid);
        conn.setUrl(Constant.url + "pClientInfoAction!getAccountInfo.htm");
        new Thread(new HttpPostRunnable(conn, new UpdateHandler())).start();

    }

    public static void getCheckCode(String mobile){
        HttpClient conn = new HttpClient();
        conn.setHeader("cookie", "JSESSIONID=" + Variable.account.sessionid);
        conn.setParam("mobile", mobile);
        conn.setUrl(Constant.url+"pLoginAction!getMobileCheckCode.htm");
        new Thread(new HttpPostRunnable(conn, new GetCheckcodeHandler())).start();
    }

    public static void getCheckCodeNoLogin(String mobile){
        HttpClient conn = new HttpClient();
        conn.setParam("mobile", mobile);
        conn.setUrl(Constant.url + "pLoginAction!getMobileCheckCodeNoLogin.htm");
        new Thread(new HttpPostRunnable(conn, new GetCheckcodeHandler())).start();
    }

    // 下载图片并在ImageView上加载, ratio为下载原图时的缩放比
    public static void showImage(String url, ImageView img, int ratio){
        imageUrl = url;
        imageView = img;
        imageRatio = ratio;
        Log.i("AsyncTask", "image get: " + url);
        new AsyncTask<Void, Void, Void>() {
            Bitmap bmp=null;
            @Override
            protected Void doInBackground(Void... params) {
                if(imageUrl.equals("")){
                    return null;
                }
                try {
                    Log.i("AsyncTask", "image get: " + imageUrl);
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = false;
                    options.inSampleSize = imageRatio; // height, width 变为原来的ratio分之一
                    InputStream inputStream = new URL(imageUrl).openStream();
                    bmp = BitmapFactory.decodeStream(inputStream, null, options);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                if (bmp != null) {
                    Log.i("AsyncTask", "image get: succeed !");
                    imageView.setImageBitmap(bmp);
                } else {
                    Log.i("AsyncTask", "image get: failed !");
                    imageView.setImageResource(R.color.transparent);
                }
            }
        }.execute();

    }

    // 点击缩略图时下载高清原图并用弹出框查看, ratio为下载原图时的缩放比
    public static void showBigImage(String url, ImageView img, int ratio){
        imageUrl = url;
        imageView = img;
        imageRatio = ratio;

        new AsyncTask<Void, Void, Void>() {
            Bitmap bmp=null;
            @Override
            protected Void doInBackground(Void... params) {
                if(imageUrl.equals("")){
                    return null;
                }
                try {
                    Log.i("AsyncTask", "image get: "+imageUrl);
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = false;
                    options.inSampleSize = imageRatio; // height, width 变为原来的ratio分之一
                    InputStream inputStream = new URL(imageUrl).openStream();
                    bmp = BitmapFactory.decodeStream(inputStream, null, options);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                if (bmp != null) {
                    Log.i("AsyncTask", "image get: succeed !");
                    imageView.setOnClickListener(new View.OnClickListener() { // 点击放大
                        public void onClick(View paramView) {
                            LayoutInflater inflater = LayoutInflater.from(Variable.currentActivity);
                            View imgEntryView = inflater.inflate(R.layout.dialog_big_photo, null); // 加载自定义的布局文件
                            ((ImageView)imgEntryView.findViewById(R.id.large_image)).setImageBitmap(bmp); // 设置图片
                            final AlertDialog dialog = new AlertDialog.Builder(Variable.currentActivity).create();
                            dialog.setView(imgEntryView); // 自定义dialog
                            dialog.show();

                            // 点击布局文件（也可以理解为点击大图）后关闭dialog，这里的dialog不需要按钮
                            imgEntryView.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View paramView) {
                                    dialog.cancel();
                                }
                            });
                        }
                    });
                } else {
                    Log.i("AsyncTask", "image get: failed !");
                }
            }
        }.execute();
    }

    public static void getMapZone(){
        if(Variable.mapZone == null || Variable.mapProvince == null){
            HttpClient conn_address = new HttpClient();
            conn_address.setUrl(Constant.url + "pCommonAction!getAddressZoneMap.htm");
            new Thread(new HttpPostRunnable(conn_address, new AddressHandler())).start();
        }
    }

    public static void getMapLottype(){
        if(Variable.mapLottype == null || Variable.mapLottype1 == null){
            HttpClient conn_lotType = new HttpClient();
            conn_lotType.setUrl(Constant.url+"pCommonAction!getAuctionTypeMap.htm");
            new Thread(new HttpPostRunnable(conn_lotType, new LotTypeHandler())).start();
        }
    }

}

class UpdateHandler extends HttpJsonHandler {
    @Override
    public void handlerData(int code, JSONObject data){
        super.handlerData(code, data);
        switch (code){
            case 0:
                JsonApi.getAccountInfo(data);
                break;
            default:
                break;
        }
    }
}

class GetCheckcodeHandler extends HttpJsonHandler {
    @Override
    public void handlerData(int code, JSONObject data){
        super.handlerData(code, data);
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
