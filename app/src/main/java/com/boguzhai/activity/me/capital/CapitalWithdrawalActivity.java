package com.boguzhai.activity.me.capital;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.boguzhai.R;
import com.boguzhai.activity.base.BaseActivity;
import com.boguzhai.activity.base.Constant;
import com.boguzhai.activity.base.Variable;
import com.boguzhai.logic.thread.HttpJsonHandler;
import com.boguzhai.logic.thread.HttpPostRunnable;
import com.boguzhai.logic.utils.HttpClient;
import com.boguzhai.logic.utils.Utility;

import org.json.JSONObject;

public class CapitalWithdrawalActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setLinearView(R.layout.me_capital_withdrawal);
		title.setText("提现");
		init();
	}

	protected void init(){
		((TextView)findViewById(R.id.bank_name)).setText(Variable.account.capitalInfo.bankName);
		((TextView)findViewById(R.id.bank_number)).setText(Variable.account.capitalInfo.bankNumber);
		listen(R.id.money_clear);
		listen(R.id.submit);
	}

	@Override
	public void onClick(View view) {
		super.onClick(view);
		switch (view.getId()){
			case R.id.money_clear: ((EditText)findViewById(R.id.money)).setText(""); break;
			case R.id.submit:
				String money = ((EditText)findViewById(R.id.money)).getText().toString();

				try {
					Double.parseDouble(money);
				} catch (Exception ex){
					Utility.alertDialog("请输入正确的提现金额", null);
					break;
				}
				if(Double.parseDouble(money)<0){
					Utility.alertDialog("请输入正确的提现金额",null);
					break;
				}

				HttpClient conn = new HttpClient();
				conn.setHeader("cookie", "JSESSIONID=" + Variable.account.sessionid);
				conn.setParam("money", money);
				conn.setUrl(Constant.url + "pTraceAction!accountWithdrawal.htm");

				new Thread(new HttpPostRunnable(conn, new HttpJsonHandler() {
					@Override
					public void handlerData(int code, JSONObject data) {
						super.handlerData(code, data);
						switch (code){
							case 0:	Utility.alertDialog("提现成功",null);	break;
							case 1:	Utility.alertDialog("由于没有绑定银行卡，提现失败",null);	break;
							case 2:	Utility.alertDialog("无法提现到绑定的银行卡，提现失败",null);	break;
							case 3:	Utility.alertDialog("其它原因，提现失败",null);	break;
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


