package com.boguzhai.logic.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.widget.ImageView;

import com.boguzhai.activity.items.ImageLoader;
import com.boguzhai.logic.thread.LoadImageTask;

import java.io.File;
import java.util.List;

public class ServiceApi {

    /**
     * 检测网络是否连接
     */
    public static boolean isNetConnected(Context context) {
        if (context != null) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null) {
                NetworkInfo mNetworkInfo = cm.getActiveNetworkInfo();
                if (mNetworkInfo != null) {
                    return mNetworkInfo.isAvailable();
                }

                /** 另外一种全面检查方法
                NetworkInfo[] infos = cm.getAllNetworkInfo();
                if (infos != null) {
                    for (NetworkInfo ni : infos) {
                        if (ni.isConnected()) {
                            return true;
                        }
                    }
                }
                */
            }
        }
        return false;
    }

    /**
     * 检测wifi是否连接
     */
    public static boolean isWifiConnected(Context context) {
        if (context != null) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null) {
                NetworkInfo networkInfo = cm.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 检测Mobile/3G是否连接
     */
    public static boolean isMobileConnected(Context context) {
        if (context != null) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null) {
                NetworkInfo networkInfo = cm.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取当前网络连接的类型信息
     * TYPE_MOBILE:0, TYPE_WIFI:1, 更多请查阅ConnectivityManager类中网络状态定义
     * 无网络连接时返回 -1
     */
    public static int getConnectedType(Context context) {
        if (context != null) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = cm.getActiveNetworkInfo();
            if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {
                return mNetworkInfo.getType();
            }
        }
        return -1;
    }

    /**
     * 检测GPS是否打开
     */
    public static boolean isGpsEnabled(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        List<String> accessibleProviders = lm.getProviders(true);
        for (String name : accessibleProviders) {
            if ("gps".equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断手机是否有SD卡。
     *
     * @return 有SD卡返回true，没有返回false。
     */
    public static boolean hasSDCard() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    // 判断该URL图片是否缓存在内存中或者保存在SD卡中，若是直接取出填充，否则返回false
    public static boolean setImageFromMemOrSd(String imageUrl, ImageView imageView){
        Bitmap bitmap = null;
        bitmap =  ImageLoader.getInstance().getBitmapFromMemoryCache(imageUrl);
        if(bitmap == null){
            File imageFile = new File(LoadImageTask.getImagePath(imageUrl));
            if (imageFile.exists()) {
                bitmap = LoadImageTask.decodeBitmapFromResource(imageFile.getPath(), 1);
            }
        }

        if(bitmap == null){
            return false;
        }else {
            imageView.setImageBitmap(bitmap);
            return true;
        }
    }

}

