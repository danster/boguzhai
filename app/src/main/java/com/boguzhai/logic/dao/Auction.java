package com.boguzhai.logic.dao;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by danster on 3/2/15.
 */

public class Auction {
    public String id = "";
    public String name = "";
    public String type = "";       //"" "同步" "网络"
    public String status = "";     //"" "预展中" "拍卖中" "已结束"
    public String location = "";
    public String auctionTime = "";
    public String previewTime = "";

    public int showNum = 0; // 上拍件数
    public int dealNum = 0; // 成交件数
    public double dealSum = 0.00; // 成交金额

    public ArrayList<Session> sessionList = new ArrayList<Session>();

    public Auction() {
    }

    public static Auction parseJson(JSONObject auctionObj){
        Auction auction = null;
        try {
            auction = new Auction();
            auction.id = auctionObj.getString("id");
            auction.name = auctionObj.getString("name");
            auction.location = auctionObj.getString("auctionLocation");
            auction.status = auctionObj.getString("status");
            auction.type = auctionObj.getString("type");
            auction.auctionTime = auctionObj.getString("auctionTime");
            auction.previewTime = auctionObj.getString("previewTime");

            auction.dealNum = auctionObj.getString("dealNum").equals("") ? 0 : Integer.parseInt(auctionObj.getString("dealNum"));
            auction.dealSum = auctionObj.getString("dealSum").equals("") ? 0.0 : Double.parseDouble(auctionObj.getString("dealSum"));
            auction.showNum = auctionObj.getString("showNum").equals("")?0:Integer.parseInt(auctionObj.getString("showNum"));

            if(auctionObj.has("auctionSessionList")){
                JSONArray sessionArray = auctionObj.getJSONArray("auctionSessionList");
                for(int i=0; i<sessionArray.length(); ++i){
                    JSONObject sessionObj = sessionArray.getJSONObject(i);
                    Session session = Session.parseJson(sessionObj);
                    auction.sessionList.add(session);
                }
            }

        }catch(JSONException ex) {
            ex.printStackTrace();
        }

        return auction;
    }

}