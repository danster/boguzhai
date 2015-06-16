package com.boguzhai.logic.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.boguzhai.R;
import com.boguzhai.activity.base.Variable;
import com.boguzhai.activity.items.BaseHtmlActivity;
import com.boguzhai.activity.login.LoginActivity;
import com.boguzhai.activity.mainpage.MainActivity;
import com.boguzhai.logic.dao.Account;
import com.boguzhai.logic.dao.Address_1;
import com.boguzhai.logic.dao.Address_2;
import com.boguzhai.logic.dao.Address_3;
import com.boguzhai.logic.dao.Auction;
import com.boguzhai.logic.dao.Lottype_1;
import com.boguzhai.logic.dao.Lottype_2;
import com.boguzhai.logic.dao.Lottype_3;
import com.boguzhai.logic.dao.Session;
import com.boguzhai.logic.listener.SpinnerListener;

import java.util.ArrayList;

public class Utility {
    /*********************************** 提示小工具 ********************************************/
    public static void toastMessage(String msg){
        Toast.makeText(Variable.app_context, msg, Toast.LENGTH_SHORT).show();
    }

    static ProgressDialog progressDialog;

//    // 显示进度循环圈
//    public static void showProgressDialog(String msg){
//        progressDialog = new ProgressDialog(Variable.currentActivity);
//        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        progressDialog.setMessage(msg);
//        progressDialog.setCancelable(true); // could be killed by backward
//        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                });
//    }
//
//    // 取消进度循环圈
//    public static void dismissProgressDialog() {
//        if(null != progressDialog) {
//            if(progressDialog.isShowing()) {
//                progressDialog.dismiss();
//            }
//        }
//    }

    // 添加确定信息,确定无动作
    public static void alertDialog(String msg){
        if (!Variable.currentActivity.isFinishing()){
            AlertDialog.Builder tips = new AlertDialog.Builder(Variable.currentActivity);
            AlertDialog dialog = tips.setMessage(msg).setPositiveButton("确定", null).create();
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.show();
            ((TextView)dialog.findViewById(android.R.id.message)).setGravity(Gravity.CENTER);
        }
    }

    // 添加确定信息,确定动作
    public static void alertDialog(String msg, DialogInterface.OnClickListener ok){
        if (!Variable.currentActivity.isFinishing()){
            AlertDialog.Builder tips = new AlertDialog.Builder(Variable.currentActivity);
            // tips.setIcon(android.R.drawable.ic_dialog_info).setTitle("提示");
            AlertDialog dialog = tips.setMessage(msg).setPositiveButton("确定", ok).create();
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.show();
            ((TextView)dialog.findViewById(android.R.id.message)).setGravity(Gravity.CENTER);
        }
    }

    // 添加确定信息,确定动作,取消动作
    public static void alertDialog(String msg, DialogInterface.OnClickListener ok,
                                   DialogInterface.OnClickListener cancel){
        if (!Variable.currentActivity.isFinishing()){
            AlertDialog.Builder tips = new AlertDialog.Builder(Variable.currentActivity);
            AlertDialog dialog = tips.setMessage(msg).setPositiveButton("确定", ok)
                                     .setNegativeButton("取消", cancel).create();
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.show();
            ((TextView)dialog.findViewById(android.R.id.message)).setGravity(Gravity.CENTER);
        }
    }

    // 由于sessionid不对或未登录状态，需要跳转到登陆页面
    public static void gotoLogin(){
        Variable.account = new Account();
        Variable.isLogin = false;
        Variable.currentActivity.startActivity(new Intent(Variable.currentActivity, LoginActivity.class));
    }

    // Activity 跳转
    public static void gotoActivity(Class<?> cls){
        Variable.lastActivity = Variable.currentActivity;
        Variable.currentActivity.startActivity(new Intent(Variable.currentActivity, cls));
    }

    // 根据index跳转到主页面版块
    public static void gotoMainpage(int index){
        switch (index){
            case 1:
                Variable.mainTabIndex = R.id.rb_1;
                break;
            case 2:
                Variable.mainTabIndex = R.id.rb_2;
                break;
            case 3:
                Variable.mainTabIndex = R.id.rb_3;
                break;
            default:
                Variable.mainTabIndex = R.id.rb_1;
                break;
        }
        Utility.gotoActivity(MainActivity.class);
    }

    // 在新的Activity里用webview打开链接
    public static void openUrl(String url){
        Intent intent = new Intent(Variable.currentActivity, BaseHtmlActivity.class);
        intent.putExtra("url", url);
        Variable.currentActivity.startActivity(intent);
    }


    /************************************* 设置Spinner的几种方法 **********************************/
    public static void setSpinner(Activity activity, Spinner spinner, String[] list,
                                  AdapterView.OnItemSelectedListener listener){
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item,list);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(listener);
        spinner.setVisibility(View.VISIBLE);
    }

    public static void setSpinner(Activity activity, Spinner spinner,  ArrayList<String> arrayList,
                                  AdapterView.OnItemSelectedListener listener){
        String[] list = arrayList.toArray(new String[arrayList.size()]);
        setSpinner(activity, spinner, list, listener);
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

    /** 获取PairList里的ValueList **/
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
        ((TextView)activity.findViewById(R.id.session_location)).setText("地点:" + session.auctionLocation);
    }

    /********************************* 根据条件获取拍品类型(一、二、三)的名称 *************************/

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

    /********************************* 根据条件获取省市区的名称 *************************/
    public static String getAddressName(String address_id1){
        String name="";
        for(Address_1 address1 : Variable.mapZone){
            if(address1.id.equals(address_id1)){
                name = address1.name;
                break;
            }
        }
        return name;
    }

    public static String getAddressName(String address_id1, String address_id2){
        String name="";
        for(Address_1 address1 : Variable.mapZone){
            if(address1.id.equals(address_id1)){
                for(Address_2 address2 : address1.child){
                    if(address2.id.equals(address_id2)){
                        name = address2.name;
                        break;
                    }
                }
                break;
            }
        }
        return name;
    }

    public static String getAddressName(String address_id1,String address_id2,String address_id3){
        String name="";
        for(Address_1 address1 : Variable.mapZone){
            if(address1.id.equals(address_id1)){
                for(Address_2 address2 : address1.child){
                    if(address2.id.equals(address_id2)){
                        for(Address_3 address3 : address2.child){
                            if(address3.id.equals(address_id3)){
                                name = address3.name;
                                break;
                            }
                        }
                        break;
                    }
                }
                break;
            }
        }
        return name;
    }

    public static int getAddressIndex(String address_1){
        int index = -1;
        for(Address_1 address1 : Variable.mapZone){
            if(address1.name.equals(address_1)){
                index = Variable.mapZone.indexOf(address1);
                break;
            }
        }
        return index;
    }

    public static int getAddressIndex(String address_1, String address_2){
        int index = -1;
        for(Address_1 address1 : Variable.mapZone){
            if(address1.name.equals(address_1)){
                for(Address_2 address2 : address1.child){
                    if(address2.name.equals(address_2)){
                        index = address1.child.indexOf(address2);
                        break;
                    }
                }
                break;
            }
        }
        return index;
    }

    public static int getAddressIndex(String address_1, String address_2, String address_3){
        int index = -1;
        for(Address_1 address1 : Variable.mapZone){
            if(address1.name.equals(address_1)){
                for(Address_2 address2 : address1.child){
                    if(address2.name.equals(address_2)){
                        for(Address_3 address3 : address2.child){
                            if(address3.name.equals(address_3)){
                                index = address2.child.indexOf(address3);
                                break;
                            }
                        }
                        break;
                    }
                }
                break;
            }
        }
        return index;
    }


    /**
     * 加载数据时，显示提示对话框
     */
    static Dialog dialog;
    public static void showLoadingDialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Variable.currentActivity);
        View view = View.inflate(Variable.currentActivity, R.layout.loading_tip, null);
        ((TextView) view.findViewById(R.id.tv_progress_dialog)).setText(msg);
        dialog = builder.setView(view).create();
        dialog.show();
    }

    public static void dismissLoadingDialog() {
        if(null != dialog) {
            if(dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }


}

