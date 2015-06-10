package com.boguzhai.logic.gaobo;

import java.util.ArrayList;

/**
 * Created by bobo on 15/6/9.
 */


public class PayOrder {
    public String orderId;//订单id
    public String orderNo;//订单no
    public String orderTime;//产生订单的日期
    public String orderStatus;//订单状态
    public String expressPrice;//运费
    public String preferential;//优惠
    public String realPayPrice;//实付款
    public String supportPrice;//保费



    public String addressInfo; //收货信息
    public String deliveryInfo; //配送信息
    public String payType; //付款方式
    public String invoiceInfo; //发票信息
    public String auctionInfo; //拍品信息
    public String myRemark; //我的留言
    public String sellerRemark; //买家留言


    public ArrayList<String> orderLogs;// 操作时间       包裹日志



    public ArrayList<OrderLot> orderLots;
}
