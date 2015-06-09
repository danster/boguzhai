package com.boguzhai.activity.me.order;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;

import com.boguzhai.R;
import com.boguzhai.activity.base.BaseActivity;
import com.boguzhai.activity.base.Constant;
import com.boguzhai.activity.base.Variable;
import com.boguzhai.activity.login.LoginActivity;
import com.boguzhai.logic.gaobo.OrderLot;
import com.boguzhai.logic.gaobo.PayOrder;
import com.boguzhai.logic.thread.HttpJsonHandler;
import com.boguzhai.logic.thread.HttpPostRunnable;
import com.boguzhai.logic.utils.HttpClient;
import com.boguzhai.logic.utils.Utility;
import com.boguzhai.logic.view.XListViewForScrollView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PayOrderActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener, XListViewForScrollView.IXListViewListener {



    private SwipeRefreshLayout swipe_layout;
    private XListViewForScrollView listview;
    private int totalCount;
    private int currentCount;
    private int size;
    private int number = 1;

    private ArrayList<PayOrder> payOrders;
    private PayOrderAdapter adapter;

    private HttpClient conn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        this.setLinearView(R.layout.me_pay_order);
        title.setText("我的订单");
        init();
	}

	protected void init(){
        swipe_layout = (SwipeRefreshLayout) findViewById(R.id.me_pay_order_swipe_layout);
        swipe_layout.setOnRefreshListener(this);
        swipe_layout.setColorSchemeResources(R.color.gold);

        listview = (XListViewForScrollView) findViewById(R.id.me_pay_order_lv);
        listview.setPullLoadEnable(true);
        listview.setXListViewListener(this);
	}

    private void initData() {
        adapter = new PayOrderAdapter(this, payOrders);
        listview.setAdapter(adapter);
    }



    private void requestData() {
        conn = new HttpClient();
        conn.setHeader("cookie", "JSESSIONID=" + Variable.account.sessionid);
        conn.setUrl(Constant.url + "pTraceAction!getMyOrderList.htm");
        conn.setParam("number", String.valueOf(number));
        new Thread(new HttpPostRunnable(conn, new MyPayOrderHandler())).start();
    }

	@Override
	public void onClick(View view) {
        super.onClick(view);
    }



    @Override
    public void onRefresh() {
        Log.i(TAG, "下拉刷新");
        totalCount = 0;
        currentCount = 0;
        size = 0;
        payOrders.clear();
        number = 1;
        swipe_layout.setRefreshing(true);
        requestData();
    }

    @Override
    public void onLoadMore() {
        Log.i(TAG, "加载更多");
        if (currentCount >= totalCount) {
            listview.stopLoadMore();
            Utility.toastMessage("没有更多数据了");
            listview.setPullLoadEnable(false);
        }
        number++;
        requestData();
    }

    private class MyPayOrderHandler extends HttpJsonHandler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                case 9:
                    swipe_layout.setRefreshing(false);
                    Log.i(TAG, "number:" + number);
                    break;
            }
        }


        @Override
        public void handlerData(int code, JSONObject data) {
            switch (code) {
                case 1:
                    number--;
                    Utility.toastMessage("网络异常，获取信息失败");
                    break;
                case -1:
                    number--;
                    Utility.toastMessage("用户名密码失效，请重新登录");
                    startActivityForResult(new Intent(context, LoginActivity.class), 0);
                    break;
                case 0:
                    Log.i(TAG, data.toString());
                    try {
                        size = Integer.parseInt(data.getString("size"));//每页的数目
                        totalCount = Integer.parseInt(data.getString("count"));//总的数目
                        if (number == 1 && totalCount == 0) {
                            Utility.toastMessage("暂无数据");
                        }else {
                            currentCount += size;
                            JSONArray jArray = data.getJSONArray("list");
                            PayOrder order;
                            OrderLot lot;
                            for (int i = 0; i < jArray.length(); i++) {
                                order = new PayOrder();
                                order.orderTime = jArray.getJSONObject(i).getString("orderTime");
                                order.orderId = jArray.getJSONObject(i).getString("orderId");
                                order.orderStatus = jArray.getJSONObject(i).getString("orderStatus");
                                order.expressPrice = jArray.getJSONObject(i).getString("expressPrice");
                                order.preferential = jArray.getJSONObject(i).getString("preferential");
                                order.realPayPrice = jArray.getJSONObject(i).getString("realPayPrice");
                                lot = new OrderLot();
                                JSONArray lotArray = jArray.getJSONObject(i).getJSONArray("auctionInfo");
                                for(int j = 0; j < lotArray.length(); j++) {
                                    lot.id = lotArray.getJSONObject(j).getString("id");
                                    lot.name = lotArray.getJSONObject(j).getString("name");
                                    lot.number = lotArray.getJSONObject(j).getString("number");
                                    lot.no = lotArray.getJSONObject(j).getString("no");
                                    lot.appraisal = lotArray.getJSONObject(j).getString("appraisal");
                                    lot.startPrice = Double.parseDouble(lotArray.getJSONObject(j).getString("startPrice"));
                                    lot.dealPrice = Double.parseDouble(lotArray.getJSONObject(j).getString("dealPrice"));
                                    lot.commission = lotArray.getJSONObject(j).getString("commission");
                                    lot.sum = lotArray.getJSONObject(j).getString("sum");
                                    order.orderLots.add(lot);
                                }
                                payOrders.add(order);
                            }
                            if (number == 1) {//刷新
                                Utility.toastMessage("刷新成功");
                                swipe_layout.setRefreshing(false);
                                initData();
                            } else {//加载更多
                                listview.stopLoadMore();
                                adapter.notifyDataSetChanged();
                            }
                        }
                    } catch (JSONException e) {
                        Log.i(TAG, "json解析异常");
                        Utility.toastMessage("数据解析异常");
                        e.printStackTrace();
                    }
                    break;
            }

        }
    }
}
