package com.boguzhai.activity.me.capital;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

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

public class CapitalChargeActivity  extends BaseActivity {
    private String pay_money;
    private boolean useUnionpay=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setLinearView(R.layout.me_capital_charge);
        title.setText("充值");
        init();
    }

    protected void init(){
        listen(R.id.money_clear);
        listen(R.id.ly_unionpay);
        listen(R.id.submit);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
            case R.id.money_clear: ((EditText)findViewById(R.id.money)).setText(""); break;
            case R.id.ly_unionpay: // 用银联支付保证金
                if(useUnionpay){
                    ((ImageView)findViewById(R.id.choose_unionpay)).setImageResource(R.drawable.choose_no);
                    useUnionpay = false;
                } else {
                    ((ImageView)findViewById(R.id.choose_unionpay)).setImageResource(R.drawable.choose_yes);
                    useUnionpay = true;
                }
                break;
            case R.id.submit:
                pay_money = ((EditText)findViewById(R.id.money)).getText().toString();
                try {
                    Double.parseDouble(pay_money);
                } catch (Exception ex){
                    Utility.alertDialog("请输入正确的充值金额",null);
                    break;
                }
                if(Double.parseDouble(pay_money)<0){
                    Utility.alertDialog("请输入正确的充值金额",null);
                    break;
                }
                if(!useUnionpay){
                    Utility.alertDialog("请输入充值方式",null);
                    break;
                }

                HttpClient conn = new HttpClient();
                conn.setHeader("cookie", "JSESSIONID=" + Variable.account.sessionid);
                conn.setParam("money", pay_money);
                conn.setParam("type", "1");
                conn.setUrl(Constant.url + "pTraceAction!accountCharge.htm");
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
                                Utility.alertDialog("抱歉，充值失败",null);
                                break;
                            default:
                                break;
                        }
                    }
                })).start();

                break;
            default: break;
        };
    }

}