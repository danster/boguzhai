package com.boguzhai.logic.gaobo;

import java.util.ArrayList;

/**
 * Created by bobo on 15/6/9.
 */


public class PayOrder {
    public String orderId;//订单id
    public String orderTime;//产生订单的日期
    public String orderStatus;//订单状态
    public String expressPrice;//运费
    public String preferential;//优惠
    public String realPayPrice;//实付款
    public ArrayList<OrderLot> orderLots;
}
