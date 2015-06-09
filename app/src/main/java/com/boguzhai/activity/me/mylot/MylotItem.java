package com.boguzhai.activity.me.mylot;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

// 收货信息类
public class MylotItem implements Serializable {
    private static final long serialVersionUID = -2813634580481038361L;
    public String id = "";
    public String auctionId = "";
    public String name = "";
    public String number = "";
    public String no = "";
    public String image = "";
    public String appraisal = "";
    public String startPrice = "";
    public String dealPrice = "";
    public String originalCommission = "";
    public String currentCommission = "";
    public String sum = "";

    public static MylotItem parseJson( JSONObject obj){
        MylotItem mylot = new MylotItem();
        try {
            mylot.id = obj.getString("id");
            mylot.name = obj.getString("name");
            mylot.number = obj.getString("number");
            mylot.no = obj.getString("no");
            mylot.image = obj.getString("image");
            mylot.appraisal = obj.getString("appraisal");
            mylot.startPrice = obj.getString("startPrice");
            mylot.dealPrice = obj.getString("dealPrice");
            mylot.originalCommission = obj.getString("originalCommission");
            mylot.currentCommission = obj.getString("currentCommission");
            mylot.sum = obj.getString("sum");
        }catch(JSONException ex) {
            ex.printStackTrace();
        }

        return mylot;
    }

    public MylotItem() {}

}