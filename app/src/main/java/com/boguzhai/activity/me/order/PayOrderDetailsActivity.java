package com.boguzhai.activity.me.order;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boguzhai.R;
import com.boguzhai.activity.base.BaseActivity;
import com.boguzhai.activity.base.Constant;
import com.boguzhai.activity.base.Variable;
import com.boguzhai.activity.login.LoginActivity;
import com.boguzhai.logic.gaobo.OrderLot;
import com.boguzhai.logic.thread.HttpJsonHandler;
import com.boguzhai.logic.thread.HttpPostRunnable;
import com.boguzhai.logic.utils.HttpClient;
import com.boguzhai.logic.utils.Utility;
import com.boguzhai.logic.widget.ListViewForScrollView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by bobo on 15/6/9.
 */
public class PayOrderDetailsActivity extends BaseActivity{


    private HttpClient conn;
    private String orderId;

    private LinearLayout ll;

    private ArrayList<OrderLot> lots;
    private OrderLotAdapter adapter;
    private ListViewForScrollView listView;

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
        orderId = getIntent().getStringExtra("orderId");

        requestData();
    }


    private void requestData() {
        conn = new HttpClient();
        conn.setHeader("cookie", "JSESSIONID=" + Variable.account.sessionid);
        conn.setUrl(Constant.url + "pTraceAction!lookOrderById.htm");
        conn.setParam("orderId", String.valueOf(orderId));
        new Thread(new HttpPostRunnable(conn, new MyOrderDetailsHandler())).start();
    }


    private void setText(int resourceId, String text) {
        TextView tv = (TextView) findViewById(resourceId);
        tv.setText(text);
    }

    private class MyOrderDetailsHandler extends HttpJsonHandler {

        @Override
        public void handlerData(int code, JSONObject data) {
            switch (code) {
                case 1:
                    Utility.toastMessage("网络异常，获取信息失败");
                    break;
                case -1:
                    Utility.toastMessage("用户名密码失效，请重新登录");
                    startActivityForResult(new Intent(context, LoginActivity.class), 0);
                    break;
                case 0:
                    Log.i(TAG, data.toString());
                    try {
                        setText(R.id.tv_order_details_id, data.getString("orderId"));
                        setText(R.id.tv_order_details_status, data.getString("orderStatus"));
                        setText(R.id.tv_order_details_addressInfo, data.getString("addressInfo"));
                        setText(R.id.tv_order_details_deliveryInfo, data.getString("deliveryInfo"));
                        setText(R.id.tv_order_details_payType, data.getString("payType"));
                        setText(R.id.tv_order_details_invoiceInfo, data.getString("invoiceInfo"));
                        setText(R.id.tv_order_details_auctionInfo, data.getString("auctionInfo"));
                        setText(R.id.tv_order_details_myRemark, data.getString("myRemark"));
                        setText(R.id.tv_order_details_sellerRemark, data.getString("sellerRemark"));

                        JSONArray logArray = data.getJSONArray("orderActionList");
                        for(int i = 0; i < logArray.length(); i++) {
                            String log = logArray.getJSONArray(i).getString(0) + logArray.getJSONArray(i).getString(1);
                            addOrderLog(log);
                        }


                        OrderLot lot = new OrderLot();
                        JSONArray lotArray = data.getJSONArray("auctionInfo");
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
                            lots.add(lot);
                        }

                        adapter = new OrderLotAdapter(context, lots);
                        listView.setAdapter(adapter);

                    } catch (JSONException e) {
                        Log.i(TAG, "json解析异常");
                        Utility.toastMessage("数据解析异常");
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }


    private void addOrderLog(String log) {
        TextView tv = new TextView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tv.setLayoutParams(params);
        tv.setTextSize(16);
        tv.setText(log);
        ll.addView(tv);
    }


}
