package com.boguzhai.activity.me.mylot;

import android.widget.Toast;

import com.boguzhai.activity.base.Variable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

// 收货信息类
public class MylotAuction implements Serializable {
    private static final long serialVersionUID = -2813661480481038361L;
    public String id = "";    //拍卖会ID
    public String name = "";  //拍卖会名称
    public String bail = "" ; //已交保证金
    public String endtime = "" ; //截止日期
    public String info = "" ;    //优惠说明
    public ArrayList<MylotItem> lotlist=null;

    public MylotAuction() {
        lotlist = new ArrayList<MylotItem>();
    }

    public static MylotAuction parseJson( JSONObject obj){
        MylotAuction auction = new MylotAuction();
        try {
            auction.id = obj.getString("auctionMainId");
            auction.name = obj.getString("auctionMainName");
            auction.bail = obj.getString("deposit");
            auction.endtime = obj.getString("endTime");
            auction.info = obj.getString("preferentialInfo");
            JSONArray list = obj.getJSONArray("auctionList");
            for(int i=0; i<list.length(); ++i){
                JSONObject lotobj = list.getJSONObject(i);
                MylotItem mylot = MylotItem.parseJson(lotobj);
                auction.lotlist.add(mylot);
            }
        }catch(JSONException ex) {
            ex.printStackTrace();
            Toast.makeText(Variable.app_context, "数据解析报错", Toast.LENGTH_LONG).show();
        }
        return auction;
    }
}