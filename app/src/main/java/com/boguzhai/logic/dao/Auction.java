package com.boguzhai.logic.dao;

import java.util.ArrayList;

/**
 * Created by danster on 3/2/15.
 */

public class Auction {
    public String id = "";
    public String name = "";
    public String type = "";       //"同步" "网络"
    public String status = "";     //"预展中" "进行中" "已结束"
    public String location = "";
    public String auctionTime = "";
    public String previewTime = "";

    public int showCount = 0;
    public int dealCount = 0;

    public ArrayList<Session> sessionList = new ArrayList<Session>();

    public Auction() {
    }

}