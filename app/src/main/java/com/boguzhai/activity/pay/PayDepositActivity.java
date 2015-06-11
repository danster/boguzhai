package com.boguzhai.activity.pay;

import android.content.Intent;
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

public class PayDepositActivity extends BaseActivity {

    private String pay_info, pay_money, pay_balance, biddingNo="";
    private boolean useBalance=false, useUnionpay=false;
    private String payType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLinearView(R.layout.pay_deposit);
        title.setText("支付保证金");
        init();
    }

    public void init(){
        pay_info = getIntent().getStringExtra("info");
        pay_money = getIntent().getStringExtra("money");
        pay_balance = getIntent().getStringExtra("balance");

        ((TextView)findViewById(R.id.info)).setText(pay_info);
        ((TextView)findViewById(R.id.money)).setText(pay_money + "元");
        ((TextView)findViewById(R.id.balance)).setText("暂存款(还有: " + pay_balance + "元)支付");

        listen(R.id.ly_balance);
        listen(R.id.ly_unionpay);
        listen(R.id.submit);
    }

    // 支付保证金
    private void payHttpConnect(){

        HttpClient conn = new HttpClient();
        conn.setHeader("cookie", "JSESSIONID=" + Variable.account.sessionid);
        conn.setParam("auctionMainId", Variable.currentAuction.id);
        conn.setParam("auctionId", "");
        conn.setParam("useBalance", useBalance?"1":"0");
        conn.setParam("type", useUnionpay?"1":"0");
        conn.setUrl(Constant.url + "pTraceAction!payDeposit.htm");

        if (useUnionpay) { //使用银联支付方式支付
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
                        case 1:
                            try {
                                biddingNo = data.getString("biddingNo");
                                gotoPayResult(true, "恭喜您，支付成功！");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            break;
                        case 2:  gotoPayResult(false, "抱歉，支付失败！");    break;
                        default: gotoPayResult(false, "抱歉，支付失败！");    break;
                    }
                }
            })).start();

        } else{ // 不使用任何网银支付,使用暂存款
            new Thread(new HttpPostRunnable(conn, new HttpJsonHandler() {
                @Override
                public void handlerData(int code, JSONObject data) {
                    super.handlerData(code, data);
                    switch (code) {
                        case 0:
                            try {
                                biddingNo = data.getString("biddingNo");
                                gotoPayResult(true, "恭喜您，支付成功！");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            break;
                        case 1:  gotoPayResult(false, "暂存款余额不足，支付失败！"); break;
                        case 2:  gotoPayResult(false, "抱歉，支付失败！");          break;
                        default: gotoPayResult(false, "抱歉，支付失败！");          break;
                    }
                }
            })).start();

        }
    }

    private void gotoPayResult(boolean success, String tips){
        Intent intent = new Intent(Variable.currentActivity, PayDepositResultActivity.class);
        intent.putExtra("result", success?"1":"0");
        intent.putExtra("tips", tips);
        intent.putExtra("info", pay_info);
        intent.putExtra("biddingNO", biddingNo);
        startActivity(intent);
    }

    @Override
    public void onClick(View v){
        super.onClick(v);
        switch (v.getId()) {
            case R.id.submit:
                if(!useBalance && !useUnionpay){
                    Utility.alertDialog("请选择支付方式", null);
                } else {
                    payHttpConnect();
                }

                break;
            case R.id.ly_balance: // 用暂存款支付保证金
                if(useBalance) {
                    ((ImageView)findViewById(R.id.choose_balance)).setImageResource(R.drawable.choose_no);
                    useBalance = false;
                } else {
                    ((ImageView)findViewById(R.id.choose_balance)).setImageResource(R.drawable.choose_yes);
                    useBalance = true;
                }
                break;
            case R.id.ly_unionpay: // 用银联支付保证金
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
