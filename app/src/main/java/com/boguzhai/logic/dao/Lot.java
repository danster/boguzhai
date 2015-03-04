package com.boguzhai.logic.dao;

/**
 * Created by danster on 3/2/15.
 */

public class Lot {
    public String id = "";   //拍品编号 primary key
    public String name = ""; //拍品名称
    public String No = "";   //拍品内录号
    public String dealType = "";   //1:现场 2:同步 3:网络
    public String apprisal1 = "";  //预估价1
    public String apprisal2 = "";  //预估价2
    public String startPrice = ""; //起拍价
    public String dealPrice = "";  //成交价，没有成交则为空
    public String status = "";     //1:预展中 2:拍卖中 3:已成交 4:流拍
    public String image = "";      //图片url
    public String auctionId = "";  //拍品所在的拍卖会ID
    public String sessionId = "";  //拍品所在的拍卖会专场ID


    public Lot() {
    }

}