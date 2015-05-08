package com.boguzhai.logic.dao;

import android.graphics.Bitmap;
import android.widget.Toast;

import com.boguzhai.activity.base.Variable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by danster on 3/2/15.
 */

public class Session {
    public String id = "";
    public String name = "";
    public String status = "";     //"" "未开拍" "已开拍" "已结束"
    public String imageUrl = "";
    public Bitmap image = null;

    public String auctionLocation = "";
    public String previewLocation = "";
    public String auctionTime = "";
    public String previewTime = "";

    public String remark = "";
    public String auctionId = ""; // 专场所在拍卖会ID

    public int showNum = 0; // 上拍件数
    public int dealNum = 0; // 成交件数
    public double dealSum = 0.00; // 成交金额

    public ArrayList<Lot> lotArrayList = new ArrayList<Lot>();

    public Session() {
    }

    public static Session parseJson(JSONObject sessionObj){
        Session session = null;

        try {
            session = new Session();
            session.id          = sessionObj.getString("id");
            session.name        = sessionObj.getString("name");
            session.imageUrl    = sessionObj.getString("image");
            session.status      = sessionObj.getString("status");
            session.auctionId   = sessionObj.getString("auctionMainId");
            session.auctionTime = sessionObj.getString("auctionDate");
            session.auctionLocation = sessionObj.getString("auctionLocation");
            session.previewTime     = sessionObj.getString("previewDate");
            session.previewLocation = sessionObj.getString("previewLocation");
            session.remark  = sessionObj.getString("remark");
            session.dealNum = sessionObj.getString("dealNum").equals("")?0:Integer.parseInt(sessionObj.getString("dealNum"));
            session.dealSum = sessionObj.getString("dealSum").equals("")?0.0:Double.parseDouble(sessionObj.getString("dealSum"));
            session.showNum = sessionObj.getString("showNum").equals("")?0:Integer.parseInt(sessionObj.getString("showNum"));

        }catch(JSONException ex) {
            ex.printStackTrace();
            Toast.makeText(Variable.app_context, "数据解析报错", Toast.LENGTH_LONG).show();
        }

        return session;
    }

}