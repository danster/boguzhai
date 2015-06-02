package com.boguzhai.activity.me.capital;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by bobo on 15/5/25.
 */
public class BailDetail {
    public String id="";
    public String auctionMainName="";
    public String time="";
    public String from="";
    public String money="";

    public static ArrayList<BailDetail> parseJson(JSONObject data) {
        ArrayList<BailDetail> list = new ArrayList<BailDetail>();
        try {
            JSONArray details = data.getJSONArray("bailDetail");
            for (int i = 0; i < details.length(); ++i) {
                JSONObject obj = details.getJSONObject(i);
                BailDetail detail = new BailDetail();

                detail.id = obj.getString("id");
                detail.auctionMainName = obj.getString("auctionMainName");
                detail.time = obj.getString("time");
                detail.from = obj.getString("from");
                detail.money = obj.getString("money");
                list.add(detail);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;
    }

    public static ArrayList<String> uniqFrom(ArrayList<BailDetail> list){
        ArrayList<String> unique_list = new ArrayList<String>();

        for(BailDetail detail: list){
            if(!unique_list.contains(detail.from)){
                unique_list.add(detail.from);
            }
        }
        return unique_list;
    }

    public static ArrayList<BailDetail> filter(ArrayList<BailDetail> list, Date lDate, Date rDate){
        ArrayList<BailDetail> newList = new ArrayList<BailDetail>();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        try {
            for(BailDetail detail: list){
                Date date = dateFormat.parse(detail.time);
                if(date.compareTo(lDate)>=0 && date.compareTo(rDate)<=0){
                    newList.add(detail);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return newList;
    }

    public static ArrayList<BailDetail> filter(ArrayList<BailDetail> list, String from){
        ArrayList<BailDetail> newList = new ArrayList<BailDetail>();
        for(BailDetail detail: list){
            if(detail.from.equals(from)){
                newList.add(detail);
            }
        }
        return newList;
    }

}
