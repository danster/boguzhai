package com.boguzhai.logic.utils;

import android.widget.Toast;

import com.boguzhai.activity.base.Variable;
import com.boguzhai.logic.dao.Account;
import com.boguzhai.logic.dao.Auction;
import com.boguzhai.logic.dao.Lot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JsonApi {


    public static ArrayList<Auction> getAuctionList(JSONObject data){
        ArrayList<Auction> list = new ArrayList<Auction>();
        try {
            JSONArray auctionArray = data.getJSONArray("auctionMainList");
            for(int i=0; i<auctionArray.length(); ++i){
                JSONObject auctionObj = auctionArray.getJSONObject(i);
                list.add( Auction.parseJson(auctionObj) );
            }
        }catch(JSONException ex) {
            Toast.makeText(Variable.app_context, "数据解析报错", Toast.LENGTH_LONG).show();
        }
        return list;
    }

    public static void getAccountInfo(JSONObject data){
        try {
            Variable.account = new Account();
            // 登录时返回有sessionid，更新时没有
            Variable.account.sessionid = data.has("sessionid") ? data.getString("sessionid") : Variable.account.sessionid ;

            // 解析账户基本信息
            JSONObject account = data.getJSONObject("account");
            Variable.account.name = account.has("name") ? account.getString("name") : "";
            Variable.account.nickname = account.has("nickname") ? account.getString("nickname"): "";

            Variable.account.address_1 = account.has("address_1") ? account.getString("address_1"): "";
            Variable.account.addressIndex1 = Utility.getAddressIndex(Variable.account.address_1);
            Variable.account.address_2 = account.has("address_2") ? account.getString("address_2"): "";
            Variable.account.addressIndex2 = Utility.getAddressIndex(Variable.account.address_1, Variable.account.address_2);
            Variable.account.address_3 = account.has("address_3") ? account.getString("address_3"): "";
            Variable.account.addressIndex3 = Utility.getAddressIndex(Variable.account.address_1, Variable.account.address_2, Variable.account.address_3);

            Variable.account.address = account.has("address") ? account.getString("address"): "";
            Variable.account.email = account.has("email") ? account.getString("email"): "";
            Variable.account.mobile = account.has("mobile") ? account.getString("mobile"): "";
            Variable.account.imageUrl = account.has("image") ? account.getString("image"): "";
            Variable.account.telephone = account.has("telephone") ? account.getString("telephone"): "";
            Variable.account.fax = account.has("fax") ? account.getString("fax"): "";
            Variable.account.qq = account.has("qq") ? account.getString("qq"): "";

            // 解析账户认证信息
            JSONObject authInfo = data.getJSONObject("authInfo");

            // 解析资金账户信息
            JSONObject capitalInfo = data.getJSONObject("capitalInfo");
            Variable.account.capitalInfo.status = capitalInfo.getString("status");
            Variable.account.capitalInfo.bankName = capitalInfo.getString("bankName");
            Variable.account.capitalInfo.bankNumber = capitalInfo.getString("bankNumber");
            Variable.account.capitalInfo.name = capitalInfo.getString("name");
            Variable.account.capitalInfo.balance = capitalInfo.getString("balance");
            Variable.account.capitalInfo.bail = capitalInfo.getString("bail");

        }catch(JSONException ex) {
            Toast.makeText(Variable.app_context, "数据解析报错", Toast.LENGTH_LONG).show();
        }
    }

    public static ArrayList<Lot> getLotList(JSONObject data){
        ArrayList<Lot> list = new ArrayList<Lot>();
        try {
            JSONArray array = data.getJSONArray("auctionInfoList");
            for(int i=0; i<array.length(); ++i){
                JSONObject auctionObj = array.getJSONObject(i);
                list.add( Lot.parseSimpleJson(auctionObj) );
            }
        }catch(JSONException ex) {
            Toast.makeText(Variable.app_context, "getLotList 数据解析报错", Toast.LENGTH_LONG).show();
        }
        return list;
    }
}
