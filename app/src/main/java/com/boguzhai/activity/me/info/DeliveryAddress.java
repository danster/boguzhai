package com.boguzhai.activity.me.info;

import android.widget.Toast;

import com.boguzhai.activity.base.Variable;
import com.boguzhai.logic.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

// 收货信息类
public class DeliveryAddress implements Serializable {
    private static final long serialVersionUID = -2813661480481031241L;
    public String id = ""; //Primary key
    public String receiver = "";  //收件人
    public String address_1 = "" ; //省
    public String address_2 = "" ; //市
    public String address_3 = "" ; //区
    public String address = "" ;  //具体地址
    public String mobile = "" ;   //手机
    public String telephone = "" ;//座机
    public String zip = "" ;      //邮编
    public Boolean isDefault = false; //是否是默认收货地址

    public static DeliveryAddress parseJson( JSONObject obj){
        DeliveryAddress address = new DeliveryAddress();
        try {
            address.id = obj.getString("addressId");
            address.receiver = obj.getString("receiver");

            String address_id1 = obj.getString("address_1");
            String address_id2 = obj.getString("address_2");
            String address_id3 = obj.getString("address_3");
            address.address_1 = Utility.getAddressName(address_id1);
            address.address_2 = Utility.getAddressName(address_id1,address_id2);
            address.address_3 = Utility.getAddressName(address_id1,address_id2,address_id3);
            address.address = obj.getString("address");
            address.mobile = obj.getString("mobile");
            address.telephone = obj.getString("telephone");
            address.zip = obj.getString("zip");
            address.isDefault = obj.getString("isDefault").equals("1")?true:false;

        }catch(JSONException ex) {
            ex.printStackTrace();
            Toast.makeText(Variable.app_context, "数据解析报错", Toast.LENGTH_LONG).show();
        }

        return address;
    }

    public DeliveryAddress() {}

    public String toString(){
        String str = "";
        str = isDefault ? "【默认地址】" + str : str;
        str += receiver == "" ? "" : receiver + ", ";
        str += address_1 + " " + address_2 + " "+ address_3 + ", ";
        str += address+", ";
        str += mobile == "" ? "" : mobile ;
        str += telephone == "" ? "" : ", " + telephone;
        str += zip == "" ? "" : ", " + zip ;

        return str;
    }

}