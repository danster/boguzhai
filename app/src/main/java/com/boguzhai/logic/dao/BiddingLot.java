package com.boguzhai.logic.dao;

/**
 * Created by bobo on 15/4/7.
 */
public class BiddingLot extends Lot{
    public int priceCount = 0;//出价次数
    public int isLeader = -1;//1.领先 0.出局
    public double nowPrice = 0.0;
    public double topPrice = 0.0;
}
//public class Lot {
//    public int id = 0;   //拍品编号 primary key
//    public String name = ""; //拍品名称
//    public int No = 0;   //拍品内录号
//    public String dealType = "";   //1:现场 2:同步 3:网络
//    public double apprisal1 = 0;  //预估价1
//    public double apprisal2 = 0;  //预估价2
//    public double startPrice = 0; //起拍价
//    public double dealPrice = 0;  //成交价，没有成交则为空
//    public String status = "";     //1:预展中 2:拍卖中 3:已成交 4:流拍
//    public String imageUrl = "";      //图片url
//    public String auctionId = "";  //拍品所在的拍卖会ID
//    public String sessionId = "";  //拍品所在的拍卖会专场ID
//
//
//    public Lot() {
//    }
//
//}