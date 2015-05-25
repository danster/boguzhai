package com.boguzhai.activity.me.info;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.boguzhai.R;
import com.boguzhai.activity.base.BaseActivity;
import com.boguzhai.activity.base.Constant;
import com.boguzhai.activity.base.Variable;
import com.boguzhai.activity.mainpage.MainActivity;
import com.boguzhai.activity.me.capital.CapitalShowActivity;
import com.boguzhai.logic.thread.HttpJsonHandler;
import com.boguzhai.logic.thread.HttpPostRunnable;
import com.boguzhai.logic.thread.Tasks;
import com.boguzhai.logic.utils.HttpClient;
import com.boguzhai.logic.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

public class AccountInfoActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        this.setLinearView(R.layout.me_myinfo);
        title.setText("个人信息");
        title_right.setText("编辑");
        title_right.setVisibility(View.VISIBLE);
        init();
	}

	protected void init(){
        this.fillAccountInfo();
        int[] ids = { R.id.logout, R.id.my_more, R.id.my_delivery, R.id.my_capital, R.id.my_verify};
        this.listen(ids);
        Tasks.showImage(Variable.account.imageUrl, (ImageView) findViewById(R.id.image), 4);
	}

    private void fillAccountInfo(){
        ((TextView)findViewById(R.id.name)).setText(Variable.account.name);
        ((TextView)findViewById(R.id.nickname)).setText(Variable.account.nickname);
        ((TextView)findViewById(R.id.zone)).setText(Variable.account.address_1+" "+Variable.account.address_2+" "+Variable.account.address_3);
        ((TextView)findViewById(R.id.email)).setText(Variable.account.email);
        ((TextView)findViewById(R.id.mobile)).setText(Variable.account.mobile);
    }

	@Override
	public void onClick(View view) {
		super.onClick(view);
		switch (view.getId()) {
            case R.id.logout:
                Variable.isLogin = false;
                Variable.account.sessionid = "";
                Variable.mainTabIndex = R.id.rb_4;
                startActivity(new Intent(this, MainActivity.class));
                break;

            case R.id.my_more:     startActivity(new Intent(this, AccountInfoMoreActivity.class));  break;
            case R.id.my_delivery: startActivity(new Intent(this, DeliveryAddressManageActivity.class));break;
            case R.id.my_capital:
                // 获取账户资产信息
                HttpClient conn = new HttpClient();
                conn.setHeader("cookie", "JSESSIONID=" + Variable.account.sessionid);
                conn.setUrl(Constant.url + "pClientInfoAction!getCapitalInfo.htm");
                new Thread(new HttpPostRunnable(conn, new GetCaptialInfoHandler())).start();
                break;

            case R.id.my_verify:
                // 获取账户认证信息
                HttpClient connVerify = new HttpClient();
                connVerify.setHeader("cookie", "JSESSIONID=" + Variable.account.sessionid);
                connVerify.setUrl(Constant.url + "pClientInfoAction!getAuthInfo.htm");
                new Thread(new HttpPostRunnable(connVerify, new GetAuthInfoHandler())).start();
                break;

            case R.id.title_right: startActivity(new Intent(this, AccountInfoEditActivity.class));break;
            default: break;
		};
	}

    class GetAuthInfoHandler extends HttpJsonHandler {
        @Override
        public void handlerData(int code, JSONObject data){
            super.handlerData(code, data);
            switch(code){
                case 0:
                    // 解析账户认证信息
                    try {
                        JSONObject authInfo = data.getJSONObject("authInfo");
                        Variable.account.authInfo.status = authInfo.getString("status");
                        Variable.account.authInfo.property = authInfo.getString("property");
                        Variable.account.authInfo.name = authInfo.getString("name");
                        Variable.account.authInfo.type = authInfo.getString("type");
                        Variable.account.authInfo.number = authInfo.getString("number");
                        Variable.account.authInfo.licenseNumber = authInfo.getString("licenseNumber");
                        Variable.account.authInfo.taxNumber = authInfo.getString("taxNumber");
                        Variable.account.authInfo.organizationNumber = authInfo.getString("organizationNumber");
                        Variable.account.authInfo.legalPersonName = authInfo.getString("legalPersonName");
                        Variable.account.authInfo.legalPersonType = authInfo.getString("legalPersonType");
                        Variable.account.authInfo.legalPersonNumber = authInfo.getString("legalPersonNumber");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    String status = Variable.account.authInfo.status;

                    if(status.equals("-1")){
                        startActivity(new Intent(AccountInfoActivity.this, IdentityVerifyActivity.class));
                    }else if(status.equals("0") || status.equals("1") || status.equals("2")){
                        startActivity(new Intent(AccountInfoActivity.this, IdentityShowActivity.class));
                    }

                    break;
                default:
                    Utility.alertMessage("无法获取账户认证信息");
                    break;
            }
        }
    }

    class GetCaptialInfoHandler extends HttpJsonHandler {
        @Override
        public void handlerData(int code, JSONObject data){
            super.handlerData(code, data);
            switch(code){
                case 0:
                    // 解析账户资产信息
                    try {
                        JSONObject capitalInfo = data.getJSONObject("capitalInfo");
                        Variable.account.capitalInfo.status = capitalInfo.getString("status");
                        Variable.account.capitalInfo.bankName = capitalInfo.getString("bankName");
                        Variable.account.capitalInfo.bankNumber = capitalInfo.getString("bankNumber");
                        Variable.account.capitalInfo.name = capitalInfo.getString("name");
                        Variable.account.capitalInfo.balance = capitalInfo.getString("balance");
                        Variable.account.capitalInfo.bail = capitalInfo.getString("bail");
                        startActivity(new Intent(AccountInfoActivity.this, CapitalShowActivity.class));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    Utility.alertMessage("无法获取账户资产信息");
                    break;
            }
        }
    }
}


