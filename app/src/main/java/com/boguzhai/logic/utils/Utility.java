package com.boguzhai.logic.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.boguzhai.R;
import com.boguzhai.activity.auction.AuctionActiveActivity;
import com.boguzhai.activity.auction.AuctionOverActivity;
import com.boguzhai.activity.auction.AuctionPreviewActivity;
import com.boguzhai.activity.base.Variable;
import com.boguzhai.logic.dao.Auction;
import com.boguzhai.logic.dao.Lottype_1;
import com.boguzhai.logic.dao.Lottype_2;
import com.boguzhai.logic.dao.Lottype_3;
import com.boguzhai.logic.dao.Session;
import com.boguzhai.logic.listener.SpinnerListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.ArrayList;


public class Utility {

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

    public static void setSpinner(Activity activity, Spinner spinner, String[] list, AdapterView.OnItemSelectedListener listener){
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item,list);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(listener);
        spinner.setVisibility(View.VISIBLE);
    }

    public static void setSpinner(Activity activity, Spinner spinner,  ArrayList<String> arrayList, AdapterView.OnItemSelectedListener listener){
        String[] list = arrayList.toArray(new String[arrayList.size()]);
        setSpinner(activity,spinner,list,listener);
    }

    public static void setSpinner(Activity activity, int viewId,  String[] list, StringBuffer result){
        SpinnerListener listener = new SpinnerListener(list,result);
        setSpinner(activity,(Spinner)activity.findViewById(viewId),list,listener);
    }

    public static void setSpinner(Activity activity, int viewId, ArrayList<String> arrayList, StringBuffer result){
        String[] list = arrayList.toArray(new String[arrayList.size()]);
        SpinnerListener listener = new SpinnerListener(list,result);
        setSpinner(activity,(Spinner)activity.findViewById(viewId),list,listener);
    }

    public static ArrayList<String> getValueList(ArrayList< Pair<String,String> > list){
        ArrayList<String> valueList = new ArrayList<String>();
        if(list == null){
            return valueList;
        }
        for(Pair<String,String> pair : list){
            valueList.add(pair.second);
        }
        return valueList;
    }

    public static void showAuctionInfo(Activity activity, Auction auction, Session session){

        ((TextView)activity.findViewById(R.id.auction_type)).setText(auction.type);
        ((TextView)activity.findViewById(R.id.auction_name)).setText(auction.name);
        ((TextView)activity.findViewById(R.id.auction_status)).setText(auction.status);
        ((TextView)activity.findViewById(R.id.auction_showNum)).setText(auction.showNum+"件");
        ((TextView)activity.findViewById(R.id.auction_dealNum)).setText(auction.dealNum+"件");

        double rate = auction.showNum>0?auction.dealNum*0.01/auction.showNum:0;
        ((TextView)activity.findViewById(R.id.auction_dealRate)).setText(rate+"%");
        ((TextView)activity.findViewById(R.id.auction_dealSum)).setText((((int)(auction.dealSum*100))/100)+"万元");


        ((TextView)activity.findViewById(R.id.session_name)).setText(session.name);
        ((TextView)activity.findViewById(R.id.session_pretime)).setText("预展:"+session.previewTime);
        ((TextView)activity.findViewById(R.id.session_prelocation)).setText("地点:"+session.previewLocation);
        ((TextView)activity.findViewById(R.id.session_time)).setText("拍卖:"+session.auctionTime);
        ((TextView)activity.findViewById(R.id.session_location)).setText("地点:"+session.auctionLocation);
    }

    public static void gotoAuction(Context context, String status){
        if (status.equals("已开拍")){
            context.startActivity(new Intent(context, AuctionActiveActivity.class));
        }else if(status.equals("未开拍")){
            context.startActivity(new Intent(context, AuctionPreviewActivity.class));
        }else if(status.equals("已结束")){
            context.startActivity(new Intent(context, AuctionOverActivity.class));
        }else{
        }

    }

    public static String getLottype1(String type_id1){
        String type_1="";
        for(Lottype_1 lottype_1 : Variable.mapLottype){
            if(lottype_1.id.equals(type_id1)){
                type_1=lottype_1.name;
                break;
            }
        }
        return type_1;
    }

    public static String getLottype2(String type_id1, String type_id2){
        String type_2="";
        for(Lottype_1 lottype_1 : Variable.mapLottype){
            if(lottype_1.id.equals(type_id1)){
                for(Lottype_2 lottype_2 : lottype_1.child){
                    if(lottype_2.id.equals(type_id2)){
                        type_2=lottype_2.name;
                        break;
                    }
                }
                break;
            }
        }
        return type_2;
    }

    public static String getLottype3(String type_id1, String type_id2, String type_id3){
        String type_3="";
        for(Lottype_1 lottype_1 : Variable.mapLottype){
            if(lottype_1.id.equals(type_id1)){
                for(Lottype_2 lottype_2 : lottype_1.child){
                    if(lottype_2.id.equals(type_id2)){
                        for(Lottype_3 lottype_3 : lottype_2.child){
                            if(lottype_3.id.equals(type_id3)){
                                type_3=lottype_3.name;
                                break;
                            }
                        }
                        break;
                    }
                }
                break;
            }
        }
        return type_3;
    }


}

