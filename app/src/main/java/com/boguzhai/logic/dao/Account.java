package com.boguzhai.logic.dao;

import android.graphics.Bitmap;

import com.boguzhai.activity.me.info.DeliveryAddress;

import java.io.Serializable;
import java.util.ArrayList;

public class Account implements Serializable {
    private static final long serialVersionUID = -2813661484140531241L;

    public String id="";          //Primary key
    public String sessionid = ""; //登录后的session
    public String mobile="";    //手机
    public String password="";  //密码
    public String name="";      //姓名
    public String nickname="";  //昵称
    public String address_1=""; //省
    public int addressIndex1=-1;
    public String address_2=""; //市
    public int addressIndex2=-1;
    public String address_3=""; //区
    public int addressIndex3=-1;
    public String address="";   //具体地址

    public String email="";     //邮箱
    public String telephone=""; //座机
    public String fax="";       //传真
    public String qq="";        //QQ

    public String imageUrl="";  //图片地址
    public Bitmap image=null;   //图片

    public CapitalInfo capitalInfo=new CapitalInfo();  //资金账户信息
    public AuthInfo authInfo=new AuthInfo();           //账户认证信息
    public ArrayList<DeliveryAddress> deliveryAddressList=new ArrayList<DeliveryAddress>();  //收货地址信息（列表）

	public Account(){
    }

}

class CapitalInfo {
    public String status = "";      //资金账户状态：1已绑定，0未绑定
    public String bankId = "";      //开户行编号
    public String bankNumber = "";  //银行卡账户
    public String name = "";        //户名
    public String balance = "";     //可用余额
    public String bail = "";        //已交保证金
}

class AuthInfo {
    public String status = "";        //认证状态: 0未认证, 1审核中, 2审核未通过, 3审核通过
    public String name = "";          //真实姓名
    public String mobile = "";        //手机号码
    public String property = "";      //用户性质:0个人, 1单位
    public String type = "";          //证件类型:0二代身份证, 1军官证, 2港澳通行证, 3护照
    public String number = "";        //证件号码
    public String imageUrl1 = "";     //证件照片URL1
    public String imageUrl2 = "";     //证件照片URL2
    public String imageUrl3 = "";     //证件照片URL3
}

