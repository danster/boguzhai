package com.boguzhai.logic.dao;

import com.boguzhai.activity.me.info.DeliveryAddress;

import java.util.ArrayList;

public class Account{

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

    public CapitalInfo capitalInfo=new CapitalInfo();  //资金账户信息
    public AuthInfo authInfo=new AuthInfo();           //账户认证信息
    public ArrayList<DeliveryAddress> deliveryAddressList=new ArrayList<DeliveryAddress>();  //收货地址信息（列表）

	public Account(){
    }
}

