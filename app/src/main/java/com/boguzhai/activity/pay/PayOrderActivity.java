package com.boguzhai.activity.pay;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.boguzhai.R;
import com.boguzhai.activity.base.BaseActivity;
import com.boguzhai.activity.base.Constant;
import com.boguzhai.activity.base.Variable;
import com.boguzhai.logic.thread.HttpJsonHandler;
import com.boguzhai.logic.thread.HttpPostRunnable;
import com.boguzhai.logic.utils.HttpClient;
import com.boguzhai.logic.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

public class PayOrderActivity extends BaseActivity {

    private String balance, deposit, sum;
    private boolean useBalance=false, useDeposit=false, useUnionpay=false;
    private String payType = "", orderId="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLinearView(R.layout.pay_order);
        title.setText("支付订单");
        init();
    }

    public void init(){

        orderId = getIntent().getStringExtra("orderId");

        HttpClient conn = new HttpClient();
        conn.setHeader("cookie", "JSESSIONID=" + Variable.account.sessionid);
        conn.setParam("orderId", orderId);
        conn.setUrl(Constant.url + "pTraceAction!getOrderPayInfoById.htm");
        new Thread(new HttpPostRunnable(conn, new HttpJsonHandler() {
            @Override
            public void handlerData(int code, JSONObject data) {
                super.handlerData(code, data);
                switch (code) {
                    case 0:
                        try {
                            String orderNo = data.getString("orderNo");
                            String payMoney = data.getString("payMoney");
                            String balance = data.getString("payStatus");
                            String deposit = data.getString("deposit");

                            ((TextView)findViewById(R.id.order_no)).setText(orderNo);
                            ((TextView)findViewById(R.id.order_money)).setText("￥"+payMoney);
                            ((TextView)findViewById(R.id.balance)).setText("暂存款余额为:￥"+balance);
                            ((TextView)findViewById(R.id.deposit)).setText("保证金金额为:￥"+deposit);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        break;
                    default:
                        break;
                }
            }
        })).start();


        listen(R.id.ly_balance);
        listen(R.id.ly_deposit);
        listen(R.id.ly_unionpay);
        listen(R.id.submit);
    }

    private void gotoPayResult(boolean success, String tips){
//        Intent intent = new Intent(Variable.currentActivity, PayDepositResultActivity.class);
//        intent.putExtra("result", success?"1":"0");
//        intent.putExtra("tips", tips);
//        intent.putExtra("biddingNO", Variable.biddingNo);
//        startActivity(intent);
        Utility.alertDialog(tips,null);
    }

    // 支付保证金
    private void payDepositConn(String type){
        HttpClient conn = new HttpClient();
        conn.setHeader("cookie", "JSESSIONID=" + Variable.account.sessionid);
        conn.setParam("orderId", orderId);
        conn.setParam("useBalance", useDeposit?"1":"0");
        conn.setParam("useDeposit", useDeposit?"1":"0");
        conn.setParam("type", type);
        conn.setUrl(Constant.url + "pTraceAction!payOrderById.htm");

        switch (type){
            case "0": // 不使用任何网银支付,使用暂存款或保证金或两者都用
                new Thread(new HttpPostRunnable(conn, new HttpJsonHandler() {
                    @Override
                    public void handlerData(int code, JSONObject data) {
                        super.handlerData(code, data);
                        switch (code) {
                            case 0:  gotoPayResult(true, "恭喜您，支付成功！");         break;
                            case 1:  gotoPayResult(false, "金额不足,支付失败！"); break;
                            case 2:  gotoPayResult(false, "抱歉，支付失败！");          break;
                            default: gotoPayResult(false, "抱歉，支付失败！");          break;
                        }
                    }
                })).start();
                break;

            case "1": //使用银联支付方式支付
                new Thread(new HttpPostRunnable(conn, new HttpJsonHandler() {
                    @Override
                    public void handlerData(int code, JSONObject data) {
                        super.handlerData(code, data);
                        switch (code){
                            // 获取支付链接信息成功
                            case 0:
                                try {
                                    Utility.openUrl(data.getString("payUrl"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                break;
                            case 1:  gotoPayResult(false, "抱歉，支付失败！");    break;
                            default: gotoPayResult(false, "抱歉，支付失败！");    break;
                        }
                    }
                })).start();
                break;
            default:
                break;
        }
    }


    @Override
    public void onClick(View v){
        super.onClick(v);
        switch (v.getId()) {
            case R.id.submit:
                if(!useBalance && !useUnionpay && !useDeposit){
                    Utility.alertDialog("请选择支付方式", null);
                    break;
                }

                if(useUnionpay){
                    payDepositConn("1");
                }else {
                    payDepositConn("0");
                }

                break;
            case R.id.ly_balance: // 用暂存款支付
                if(useBalance) {
                    ((ImageView)findViewById(R.id.choose_balance)).setImageResource(R.drawable.choose_no);
                    useBalance = false;
                } else {
                    ((ImageView)findViewById(R.id.choose_balance)).setImageResource(R.drawable.choose_yes);
                    useBalance = true;
                }
                break;
            case R.id.ly_deposit: // 用保证金支付
                if(useDeposit) {
                    ((ImageView)findViewById(R.id.choose_deposit)).setImageResource(R.drawable.choose_no);
                    useDeposit = false;
                } else {
                    ((ImageView)findViewById(R.id.choose_deposit)).setImageResource(R.drawable.choose_yes);
                    useDeposit = true;
                }
                break;
            case R.id.ly_unionpay: // 用银联支付
                if(useUnionpay){
                    ((ImageView)findViewById(R.id.choose_unionpay)).setImageResource(R.drawable.choose_no);
                    useUnionpay = false;
                } else {
                    ((ImageView)findViewById(R.id.choose_unionpay)).setImageResource(R.drawable.choose_yes);
                    useUnionpay = true;
                }
                break;
            default:
                break;
        }
    }

}
