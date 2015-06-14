package com.boguzhai.activity.me.order;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boguzhai.R;
import com.boguzhai.activity.base.BaseActivity;
import com.boguzhai.activity.base.Variable;
import com.boguzhai.activity.pay.PayOrderActivity;
import com.boguzhai.logic.widget.ListViewForScrollView;

/**
 * Created by bobo on 15/6/9.
 */
public class PayOrderDetailsActivity extends BaseActivity {

    private LinearLayout ll;

    private OrderLotAdapter adapter;
    private ListViewForScrollView listView;

    private MyPayOrder payOrder;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLinearView(R.layout.me_pay_order_details);
        setTitle("订单详情");
        init();

    }

    private void init() {
        ll = (LinearLayout) findViewById(R.id.ll_order_details_log);
        listView = (ListViewForScrollView) findViewById(R.id.lv_order_details_lot);
        listView.setFocusable(false);

        button = (Button) findViewById(R.id.btn_order_details);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Variable.currentActivity, PayOrderActivity.class);
                intent.putExtra("orderId",payOrder.orderNo);
                startActivity(intent);
            }
        });
        payOrder = Variable.payOrder;

        initOrderBasicInfo();
        initOrderLog();
        initOrderLot();

    }

    //订单基本信息
    private void initOrderBasicInfo() {


        setText(R.id.tv_order_details_status, payOrder.orderStatus);
        if("待付款".equals(payOrder.orderStatus)) {
            button.setVisibility(View.VISIBLE);
        }else {
            button.setVisibility(View.GONE);
        }
        setText(R.id.tv_order_details_no, payOrder.orderNo);
        setText(R.id.tv_order_details_addressInfo, payOrder.addressInfo);
        setText(R.id.tv_order_details_deliveryInfo, payOrder.deliveryInfo);
        setText(R.id.tv_order_details_payType, payOrder.payType);
        setText(R.id.tv_order_details_invoiceInfo, payOrder.invoiceInfo);
        setText(R.id.tv_order_details_auctionInfo, payOrder.addressInfo);
        setText(R.id.tv_order_details_myRemark, payOrder.myRemark);
        setText(R.id.tv_order_details_sellerRemark, payOrder.sellerRemark);

        setText(R.id.tv_me_pay_order_details_expressPrice, "¥ " + payOrder.expressPrice);
        setText(R.id.tv_me_pay_order_details_preferential, "¥ " + payOrder.preferential);
        setText(R.id.tv_me_pay_order_details_realPayPrice, "¥ " + payOrder.realPayPrice);
        setText(R.id.tv_me_pay_order_details_supportPrice, "¥ " + payOrder.supportPrice);
    }

    //订单日志
    private void initOrderLog() {
        for(String log : payOrder.orderLogs) {
            addOrderLog(log);
        }
    }

    //订单拍品信息
    private void initOrderLot() {
        adapter = new OrderLotAdapter(context, payOrder.orderLots);
        listView.setAdapter(adapter);
    }




    private void setText(int resourceId, String text) {
        TextView tv = (TextView) findViewById(resourceId);
        tv.setText(text);
    }


    private void addOrderLog(String log) {
        TextView tv = new TextView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tv.setLayoutParams(params);
        tv.setTextSize(16);
        tv.setText(log);
        ll.addView(tv);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Variable.payOrder = null;
    }

}
