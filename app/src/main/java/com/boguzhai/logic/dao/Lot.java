package com.boguzhai.logic.dao;

import android.graphics.Bitmap;
import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by danster on 3/2/15.
 */

public class Lot {
    public String id = "";   //拍品编号 primary key
    public String name = ""; //拍品名称
    public String no = "";   //拍品内录号

    public double appraisal1 = 0;  //预估价1
    public double appraisal2 = 0;  //预估价2
    public double startPrice = 0; //起拍价
    public double dealPrice = 0;  //成交价，没有成交则为空
    public double currentPrice = 0;//当前价
    public String status = "";     //"" "成交" "已上拍" "未上拍" "流拍" "撤拍"
    public String dealType = "";   //"现场" "网络"
    public String auctionId = "";  //拍品所在的拍卖会ID
    public String sessionId = "";  //拍品所在的拍卖会专场ID

    public String type1 = "";  //所属分类一
    public String type2 = "";  //所属分类二
    public String type3 = "";  //所属分类三
    public String description = "";  //详细描述

    public String imageUrl = "";   //图片url
    public Bitmap image = null;

    public ArrayList<Pair<String,String>> specials = new ArrayList<Pair<String,String>>();
    public ArrayList<String> records = new ArrayList<String>(); //["2015-02-12 09:38:30,N091,网络, 120000.00",...]

    public Lot() {
    }

    public static Lot parseJson(JSONObject lotObj){
        Lot lot = null;
        try {
            lot = new Lot();
            JSONObject base = lotObj.getJSONObject("base");
            JSONArray special = lotObj.getJSONArray("special");
            JSONArray records = lotObj.getJSONArray("records");

            lot.id = base.getString("id");
            lot.no = base.getString("no");
            lot.name = base.getString("name");
            lot.imageUrl = base.getString("image");
            lot.status = base.getString("status");
            lot.dealType = base.getString("dealType");
            lot.auctionId = base.getString("auctionMainId");
            lot.sessionId = base.getString("auctionPartId");

            lot.type1 = base.getString("type1");
            lot.type2 = base.getString("type2");
            lot.type3 = base.getString("type3");
            lot.description = base.getString("description");

            lot.appraisal1 = base.getString("appraisal1").equals("")?0.0:Double.parseDouble(base.getString("appraisal1"));
            lot.appraisal2 = base.getString("appraisal2").equals("")?0.0:Double.parseDouble(base.getString("appraisal2"));
            lot.startPrice = base.getString("startPrice").equals("")?0.0:Double.parseDouble(base.getString("startPrice"));
            lot.dealPrice = base.getString("dealPrice").equals("")?0.0:Double.parseDouble(base.getString("dealPrice"));

            for(int i=0; i< special.length(); ++i){
                JSONArray k_v = special.getJSONArray(i);
                lot.specials.add(new Pair<String, String>(k_v.getString(0), k_v.getString(1)));
            }

            for(int i=0; i< records.length(); ++i){
                JSONArray a_record = records.getJSONArray(i);
                lot.records.add(a_record.getString(0)+","+a_record.getString(1)+","
                        + a_record.getString(2)+","+a_record.getString(3));
            }

        }catch(JSONException ex) {
            ex.printStackTrace();
        }

        return lot;
    }

    public static Lot parseSimpleJson(JSONObject lotObj){
        Lot lot = new Lot();
        try {
            JSONObject base = lotObj.getJSONObject("base");
            lot.id = base.getString("id");
            lot.no = base.getString("no");
            lot.name = base.getString("name");
            lot.imageUrl = base.getString("image");
            lot.status = base.getString("status");
            lot.dealType = base.getString("dealType");
            lot.auctionId = base.getString("auctionMainId");
            lot.sessionId = base.getString("auctionPartId");
            lot.description = base.getString("description");

            lot.appraisal1 = base.getString("appraisal1").equals("")?0.0:Double.parseDouble(base.getString("appraisal1"));
            lot.appraisal2 = base.getString("appraisal2").equals("")?0.0:Double.parseDouble(base.getString("appraisal2"));
            lot.startPrice = base.getString("startPrice").equals("")?0.0:Double.parseDouble(base.getString("startPrice"));
            lot.dealPrice = base.getString("dealPrice").equals("")?0.0:Double.parseDouble(base.getString("dealPrice"));

        }catch(JSONException ex) {
            ex.printStackTrace();
        }

        return lot;
    }

}