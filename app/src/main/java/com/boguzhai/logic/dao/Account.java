package com.boguzhai.logic.dao;

import android.graphics.Bitmap;

import java.io.Serializable;

public class Account implements Serializable {
    private static final long serialVersionUID = -2813661484140531241L;

    public String id="";          //primary key
    public String sessionid = ""; //登录后的session
    public String mobile="";    //手机
    public String password="";  //密码
    public String name="";      //姓名
    public String nickname="";  //昵称
    public String address_1=""; //省
    public String address_2=""; //市
    public String address_3=""; //区
    public String address="";   //具体地址

    public String email="";     //邮箱
    public String telephone=""; //座机
    public String fax="";       //传真
    public String qq="";        //QQ

    public String imageUrl =""; //图片地址
    public Bitmap image=null;   //图片

	public Account(){
        // imageUrl = BitmapFactory.decodeResource(getResources(), R.drawable.imageUrl);
        // imageUrl = BitmapFactory.decodeFile("/sdcard/test.png");
    }

}
