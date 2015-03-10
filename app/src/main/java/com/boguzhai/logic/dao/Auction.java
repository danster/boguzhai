package com.boguzhai.logic.dao;

/**
 * Created by danster on 3/2/15.
 */

public class Auction {
    public String id = "";
    public String name = "";
    public String type = "";       //1:现场拍卖 2:同步拍卖 3:网络拍卖
    public String status = "";     //1:预展中 2:拍卖中 3:已成交
    public String location = "";
    public String auctionTime = "";
    public String previewTime = "";

    public int showCount = 0;
    public int dealCount = 0;

    public String[] sessionIds;

    public Auction() {
    }

}