package com.boguzhai.logic.gaobo;

import com.boguzhai.logic.dao.Auction;

/**
 * Created by bobo on 15/4/5.
 */
public class MyAuction extends Auction{




    public int useProxy = 0;//1启用代理出价，0没有启用代理出价
    public int upperLimit = 0;//代理出价上限
    public int deposit = 0;//保证金数额(人民币)


}
