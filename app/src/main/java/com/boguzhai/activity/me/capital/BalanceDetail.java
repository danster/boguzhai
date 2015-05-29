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
public class BalanceDetail {
    public String id="";
    public String type="";
    public String time="";
    public String in="";
    public String out="";
    public String balance="";

    public static ArrayList<BalanceDetail> parseJson(JSONObject data) {
        ArrayList<BalanceDetail> list = new ArrayList<BalanceDetail>();
        try {
            JSONArray details = data.getJSONArray("bailDetail");
            for (int i = 0; i < details.length(); ++i) {
                JSONObject obj = details.getJSONObject(i);
                BalanceDetail detail = new BalanceDetail();

                detail.id = obj.getString("id");
                detail.type = obj.getString("type");
                detail.time = obj.getString("time");
                detail.in = obj.getString("in");
                detail.out = obj.getString("out");
                detail.balance = obj.getString("balance");

                list.add(detail);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;
    }

    public static ArrayList<String> uniqType(ArrayList<BalanceDetail> list){
        ArrayList<String> unique_list = new ArrayList<String>();

        for(BalanceDetail detail: list){
            if(!unique_list.contains(detail.type)){
                unique_list.add(detail.type);
            }
        }
        return unique_list;
    }

    public static ArrayList<BalanceDetail> filter(ArrayList<BalanceDetail> list, Date lDate, Date rDate){
        ArrayList<BalanceDetail> newList = new ArrayList<BalanceDetail>();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        try {
            for(BalanceDetail detail: list){
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


}
