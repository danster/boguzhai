package com.boguzhai.activity.me.info;

import java.io.Serializable;

// 收货信息类
public class DeliveryAddress implements Serializable {
    private static final long serialVersionUID = -2813661480481031241L;
    public String id = ""; //Primary key
    public String receiver = "";  //收件人
    public String addr_1 = "" ;   //省ID
    public String addr_2 = "" ;   //市ID
    public String addr_3 = "" ;   //区ID
    public String address = "" ;  //具体地址
    public String mobile = "" ;   //手机
    public String telephone = "" ;//座机
    public String zip = "" ;      //邮编
    public Boolean isDefault = false; //是否是默认收货地址

    public DeliveryAddress() {
    }

    public String toString(){
        String str = "";
        str = isDefault ? "【默认地址】" + str : str;
        str += receiver == "" ? "" : receiver + ", ";
        str += addr_1 + " " + addr_2 + " "+addr_3 + ", ";
        str += address+", ";
        str += mobile == "" ? "" : mobile ;
        str += telephone == "" ? "" : ", " + telephone;
        str += zip == "" ? "" : ", " + zip ;

        return str;
    }

}