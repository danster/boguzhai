package com.boguzhai.logic.thread;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;

import com.boguzhai.activity.base.Constant;
import com.boguzhai.activity.base.Variable;
import com.boguzhai.activity.items.ImageDetailsActivity;
import com.boguzhai.logic.dao.Address_1;
import com.boguzhai.logic.dao.Address_2;
import com.boguzhai.logic.dao.Address_3;
import com.boguzhai.logic.dao.Lottype_1;
import com.boguzhai.logic.dao.Lottype_2;
import com.boguzhai.logic.dao.Lottype_3;
import com.boguzhai.logic.dao.SharedKeys;
import com.boguzhai.logic.utils.HttpClient;
import com.boguzhai.logic.utils.JsonApi;
import com.boguzhai.logic.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

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

    public static void uploadAuctionImage(Bitmap bitmap, HttpJsonHandler handler){
        HttpClient conn = new HttpClient();
        conn.setHeader("cookie", "JSESSIONID=" + Variable.account.sessionid);

        conn.setParamBitmap("fileStr", bitmap);
        conn.setUrl(Constant.url.replace("/phones/","/") + "fileUploadAction!uploadAuctionImage.htm");
        new Thread(new HttpPostRunnable(conn, handler)).start();
    }

    public static void updateAccount(){
        HttpClient conn = new HttpClient();
        conn.setHeader("cookie", "JSESSIONID=" + Variable.account.sessionid);
        conn.setUrl(Constant.url + "pClientInfoAction!getAccountInfo.htm");
        new Thread(new HttpPostRunnable(conn, new UpdateAccountHandler())).start();
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
        new AsyncTask<Void, Void, Void>() {
            Bitmap bmp=null;
            @Override
            protected Void doInBackground(Void... params) {
                if(imageUrl.equals("")){
                    Log.i("AsyncTask", "image get: ");
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
                }
            }
        }.execute();

    }

    // 点击缩略图时下载高清原图并用全屏查看, ratio为下载原图时的缩放比
    public static void showBigImage(String url, ImageView img, int ratio){

        imageUrl = url;
        imageView = img;
        imageRatio = ratio;

        new AsyncTask<Void, Void, Void>() {
            Bitmap bmp=null;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Void... params) {
                if(imageUrl.equals("")){
                    Log.i("AsyncTask", "image get: ");
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

                            Variable.currentBitmap = bmp;
                            Utility.gotoActivity(ImageDetailsActivity.class);
                        }
                    });
                } else {
                    Log.i("AsyncTask", "image get: failed !");
                }
            }
        }.execute();
    }

    public static void getMapZone(){
        try {
            JSONObject data = new JSONObject(Variable.settings.getString(SharedKeys.deliveryAddress, ""));
            JSONObject zoneMap = data.getJSONObject("data").getJSONObject("addressZoneMap");

            if(Variable.mapZone == null)
                Variable.mapZone = new ArrayList<Address_1>();
            else
                Variable.mapZone.clear();

            if(Variable.mapProvince == null)
                Variable.mapProvince = new ArrayList<Pair<String,String>>();
            else
                Variable.mapProvince.clear();

            Address_1 address_1 = new Address_1(); address_1.id=""; address_1.name="不限";
            Address_2 address_2 = new Address_2(); address_2.id=""; address_2.name="不限";
            Address_3 address_3 = new Address_3(); address_3.id=""; address_3.name="不限";

            address_2.child.add(address_3);
            address_1.child.add(address_2);
            Variable.mapZone.add(address_1);

            Iterator<?> keys_1 = zoneMap.keys();
            while( keys_1.hasNext() ) {
                Address_1 object1 = new Address_1();
                String key_1 = (String)keys_1.next();
                object1.id = key_1;

                Address_2 address2 = new Address_2(); address2.id=""; address2.name="不限";
                object1.child.add(address2);

                if (zoneMap.get(key_1) instanceof JSONObject){
                    JSONObject jsonObject1 = (JSONObject)zoneMap.get(key_1);
                    object1.name = jsonObject1.getString("value");

                    Iterator<?> keys_2 = jsonObject1.keys();
                    while( keys_2.hasNext() ) {
                        String key_2 = (String)keys_2.next();
                        if(!key_2.equals("value") && jsonObject1.get(key_2) instanceof JSONObject ){
                            JSONObject jsonObject2 = (JSONObject) jsonObject1.get(key_2);
                            Address_2 object2 = new Address_2();
                            object2.id = key_2;
                            object2.name = jsonObject2.getString("value");

                            Address_3 address3 = new Address_3(); address3.id=""; address3.name="不限";
                            object2.child.add(address3);

                            Iterator<?> keys_3 = jsonObject2.keys();
                            while( keys_3.hasNext() ) {
                                String key_3 = (String)keys_3.next();
                                if(!key_3.equals("value")) {
                                    Address_3 object3 = new Address_3();

                                    object3.id = key_3;
                                    object3.name = jsonObject2.getString(key_3);
                                    object2.child.add(object3);
                                }
                            }
                            object1.child.add(object2);
                        }
                    }
                }
                Variable.mapZone.add(object1);
            }

            for(Address_1 addr: Variable.mapZone){
                Variable.mapProvince.add(new Pair<String, String>(addr.id, addr.name));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void getMapLottype(){
        try {
            Log.i("lottype",Variable.settings.getString(SharedKeys.lotType, ""));
            JSONObject data = new JSONObject(Variable.settings.getString(SharedKeys.lotType, ""));
            JSONObject lottypeMap = data.getJSONObject("auctionTypeMap");

            if(Variable.mapLottype == null)
                Variable.mapLottype = new ArrayList<Lottype_1>();
            else
                Variable.mapLottype.clear();

            if(Variable.mapLottype1 == null)
                Variable.mapLottype1 = new ArrayList<Pair<String,String>>();
            else
                Variable.mapLottype1.clear();

            Lottype_1 lottype_1 = new Lottype_1(); lottype_1.id=""; lottype_1.name="不限";
            Lottype_2 lottype_2 = new Lottype_2(); lottype_2.id=""; lottype_2.name="不限";
            Lottype_3 lottype_3 = new Lottype_3(); lottype_3.id=""; lottype_3.name="不限";

            lottype_2.child.add(lottype_3);
            lottype_1.child.add(lottype_2);
            Variable.mapLottype.add(lottype_1);

            Iterator<?> keys_1 = lottypeMap.keys();
            while( keys_1.hasNext() ) {
                Lottype_1 object1 = new Lottype_1();
                String key_1 = (String)keys_1.next();
                object1.id = key_1;

                Lottype_2 lottype2 = new Lottype_2(); lottype2.id=""; lottype2.name="不限";
                object1.child.add(lottype2);

                if (lottypeMap.get(key_1) instanceof JSONObject){
                    JSONObject jsonObject1 = (JSONObject) lottypeMap.get(key_1);
                    object1.name = jsonObject1.getString("value");

                    Iterator<?> keys_2 = jsonObject1.keys();
                    while( keys_2.hasNext() ) {
                        String key_2 = (String)keys_2.next();
                        if(!key_2.equals("value") && jsonObject1.get(key_2) instanceof JSONObject ){
                            JSONObject jsonObject2 = (JSONObject) jsonObject1.get(key_2);
                            Lottype_2 object2 = new Lottype_2();
                            object2.id = key_2;
                            object2.name = jsonObject2.getString("value");

                            Lottype_3 lottype3 = new Lottype_3(); lottype3.id=""; lottype3.name="不限";
                            object2.child.add(lottype3);

                            Iterator<?> keys_3 = jsonObject2.keys();
                            while( keys_3.hasNext() ) {
                                String key_3 = (String) keys_3.next();
                                if(!key_3.equals("value")) {
                                    Lottype_3 object3 = new Lottype_3();

                                    object3.id = key_3;
                                    object3.name = jsonObject2.getString(key_3);
                                    object2.child.add(object3);
                                }
                            }
                            object1.child.add(object2);
                        }
                    }
                }
                Variable.mapLottype.add(object1);
            }
            for(Lottype_1 type: Variable.mapLottype){
                Variable.mapLottype1.add(new Pair<String, String>(type.id, type.name));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}

class UpdateAccountHandler extends HttpJsonHandler {
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
