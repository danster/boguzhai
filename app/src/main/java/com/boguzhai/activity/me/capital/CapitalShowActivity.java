package com.boguzhai.activity.me.capital;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.boguzhai.R;
import com.boguzhai.activity.base.BaseActivity;
import com.boguzhai.activity.base.Constant;
import com.boguzhai.activity.base.Variable;
import com.boguzhai.activity.pay.CapitalChargeActivity;
import com.boguzhai.activity.pay.CapitalWithdrawalActivity;
import com.boguzhai.logic.thread.HttpJsonHandler;
import com.boguzhai.logic.thread.HttpPostRunnable;
import com.boguzhai.logic.utils.HttpClient;
import com.boguzhai.logic.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

public class CapitalShowActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setLinearView(R.layout.me_capital_account);
        title.setText("我的资金账户");
        init();
	}

	protected void init(){
        int ids[] = {R.id.bind_bank, R.id.charge, R.id.withdrawal, R.id.my_balance, R.id.my_bail};
        this.listen(ids);
	}

    @Override
    public void onResume(){
        super.onResume();

        // 获取账户资产信息
        HttpClient conn = new HttpClient();
        conn.setHeader("cookie", "JSESSIONID=" + Variable.account.sessionid);
        conn.setUrl(Constant.url + "pClientInfoAction!getCapitalInfo.htm");
        new Thread(new HttpPostRunnable(conn, new GetCaptialInfoHandler())).start();
    }

	@Override
	public void onClick(View view) {
		super.onClick(view);
		switch (view.getId()){
            case R.id.bind_bank:
                startActivity(new Intent(this, CapitalBindbankActivity.class));
                break;
            case R.id.my_balance:
                Intent intent1 = new Intent(this, CapitalDetailActivity.class);
                intent1.putExtra("type","balance");
                startActivity(intent1);
                break;
            case R.id.my_bail:
                Intent intent2 = new Intent(this, CapitalDetailActivity.class);
                intent2.putExtra("type","bail");
                startActivity(intent2);
                break;
            case R.id.charge:
                startActivity(new Intent(this, CapitalChargeActivity.class));
                break;
            case R.id.withdrawal:
                if(Variable.account.capitalInfo.status.equals("0")){
                    Utility.alertDialog("未绑定银行卡不能提现",null);
                }else{
                    startActivity(new Intent(this, CapitalWithdrawalActivity.class));
                }
                break;
            default: break;
		};
	}

    class GetCaptialInfoHandler extends HttpJsonHandler {
        @Override
        public void handlerData(int code, JSONObject data){
            super.handlerData(code, data);
            switch(code){
                case 0:
                    // 解析账户资产信息
                    try {
                        // 获取信息
                        JSONObject capitalInfo = data.getJSONObject("capitalInfo");
                        Variable.account.capitalInfo.status = capitalInfo.getString("status");
                        Variable.account.capitalInfo.bankName = capitalInfo.getString("bankName");
                        Variable.account.capitalInfo.bankNumber = capitalInfo.getString("bankNumber");
                        Variable.account.capitalInfo.name = capitalInfo.getString("name");
                        Variable.account.capitalInfo.balance = capitalInfo.getString("balance");
                        Variable.account.capitalInfo.bail = capitalInfo.getString("bail");

                        // 展示信息
                        ((TextView)findViewById(R.id.bank_name)).setText(Variable.account.capitalInfo.bankName);
                        String info = Variable.account.capitalInfo.status.equals("0")?" 绑定银行卡 ":" 重新绑定银行卡 ";
                        ((TextView) findViewById(R.id.bind_bank)).setText(info);
                        ((TextView)findViewById(R.id.bank_number)).setText(Variable.account.capitalInfo.bankNumber);
                        ((TextView)findViewById(R.id.name)).setText(Variable.account.capitalInfo.name);
                        ((TextView)findViewById(R.id.balance)).setText("￥"+Variable.account.capitalInfo.balance);
                        ((TextView)findViewById(R.id.bail)).setText("￥"+Variable.account.capitalInfo.bail);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    Utility.alertDialog("无法获取账户资产信息");
                    break;
            }
        }
    }

}


