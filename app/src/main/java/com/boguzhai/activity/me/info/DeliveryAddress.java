package com.boguzhai.activity.me.info;

import java.io.Serializable;

// 收货信息类
public class DeliveryAddress implements Serializable {
    private static final long serialVersionUID = -2813661480481031241L;
    private static int keyCount=0;
    public int id = 0; //key
    public String receiver = "";  //收件人
    public String addr_1 = "" ;   //收货地址，某某i省
    public String addr_2 = "" ;   //收货地址，某某市
    public String addr_3 = "" ;   //收货地址，某某区
    public String addr_4 = "" ;   //收货地址，具体地址
    public String mobile = "" ;
    public String telephone = "" ;
    public String zip = "" ;
    public Boolean isDefault = false;  //是否是默认收货地址

    public DeliveryAddress() {
        id = keyCount;
        keyCount += 1;
    }

    public String toString(){
        String address = "";
        address = isDefault ? "【默认地址】" + address : address;
        address += receiver == "" ? "" : receiver + ", ";
        address += addr_1 + " " + addr_2 + " "+addr_3 + ",";
        address += mobile == "" ? "" : mobile ;
        address += telephone == "" ? "" : ", " + telephone;
        address += zip == "" ? "" : ", " + zip ;

        return address;
    }

}