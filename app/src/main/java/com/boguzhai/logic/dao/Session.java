package com.boguzhai.logic.dao;

/**
 * Created by danster on 3/2/15.
 */

public class Session {
    public String id = "";
    public String name = "";
    public String type = "";       //"古籍书刊"等
    public String status = "";     //1:预展中 2:拍卖中 3:已成交
    public String location = "";
    public String preLocation = "";
    public String remark = "";
    public String image = "";
    public String auctionTime = "";
    public String previewTime = "";

    public int showCount = 0;
    public int dealCount = 0;

    public String auctionId = "";

    public Session() {
    }

}