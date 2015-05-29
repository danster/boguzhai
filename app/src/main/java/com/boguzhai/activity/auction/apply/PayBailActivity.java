package com.boguzhai.activity.auction.apply;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

public class PayBailActivity extends BaseActivity {

    private String pay_info, pay_money, pay_balance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLinearView(R.layout.auction_apply_paybail);
        title.setText("支付保证金");

        init();
    }

    public void init(){
        pay_info = getIntent().getStringExtra("info");
        pay_money = getIntent().getStringExtra("money");
        pay_balance = getIntent().getStringExtra("balance");

        ((TextView)findViewById(R.id.info)).setText(pay_info);
        ((TextView)findViewById(R.id.money)).setText(pay_money+"元");
        ((TextView)findViewById(R.id.balance)).setText("暂存款(还有: "+pay_balance+"元)支付");

        listen(R.id.capital);
    }

    @Override
    public void onClick(View v){
        super.onClick(v);
        switch (v.getId()) {
            case R.id.capital:
                Utility.alertDialog("确认支付 ?", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        // 用暂存款支付保证金
                        HttpClient conn = new HttpClient();
                        conn.setHeader("cookie", "JSESSIONID=" + Variable.account.sessionid);
                        conn.setParam("auctionMainId", Variable.currentAuction.id);
                        conn.setParam("type", "1");
                        conn.setUrl(Constant.url + "pTraceAction!payDeposit.htm");

                        new Thread(new HttpPostRunnable(conn, new HttpJsonHandler() {
                            @Override
                            public void handlerData(int code, JSONObject data) {
                                super.handlerData(code, data);
                                Intent intent = new Intent(Variable.currentActivity, PayBailResultActivity.class);
                                switch (code){
                                    // 支付成功
                                    case 0:
                                        try {
                                            Variable.biddingNo = data.getString("biddingNo");
                                            intent.putExtra("result", "0");
                                            intent.putExtra("info", pay_info);
                                            intent.putExtra("number", Variable.biddingNo);
                                            startActivity(intent);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        break;

                                    // 余额不足,支付失败
                                    case 1:
                                        intent.putExtra("result", "1");
                                        startActivity(intent);
                                        break;

                                    // 其它原因,支付失败
                                    case 2:
                                        intent.putExtra("result", "2");
                                        startActivity(intent);
                                        break;

                                    default:
                                        break;
                                }
                            }
                        })).start();

                    }
                }, null);
                break;
            default:
                break;
        }
    }

}
