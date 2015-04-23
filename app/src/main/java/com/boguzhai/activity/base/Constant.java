package com.boguzhai.activity.base;

// 程序启动后值不会变动的常量
public class Constant {
    public static final String[] auction_status={"不限","预展中","进行中","已结束"};        //拍卖会状态
    public static final String[] auction_type={"不限","同步", "网络"};                     //拍卖会类型
    public static final String[] lot_status={"不限","已上拍","未上拍","成交","流拍","撤拍"}; //拍品状态
    public static final String[] lot_deal_type={"不限","现场", "网络"};                //拍品成交方式
    public static final String[] session_status={"不限","未开拍","已开拍","已结束"};    //拍卖会专场状态
    public static final String url = "http://60.191.203.80/phones";            //服务器host

}
