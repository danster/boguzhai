package com.boguzhai.activity.me.info;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.boguzhai.R;
import com.boguzhai.activity.base.BaseActivity;
import com.boguzhai.activity.base.Constant;
import com.boguzhai.activity.base.Variable;
import com.boguzhai.activity.me.capital.CapitalShowActivity;
import com.boguzhai.logic.dao.SharedKeys;
import com.boguzhai.logic.thread.HttpJsonHandler;
import com.boguzhai.logic.thread.HttpPostRunnable;
import com.boguzhai.logic.thread.Tasks;
import com.boguzhai.logic.utils.HttpClient;
import com.boguzhai.logic.utils.JsonApi;
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
	}

    private void fillAccountInfo(){
        ((TextView)findViewById(R.id.name)).setText(Variable.account.name);
        ((TextView)findViewById(R.id.nickname)).setText(Variable.account.nickname);
        ((TextView)findViewById(R.id.zone)).setText(Variable.account.address_1 + " " + Variable.account.address_2 + " " + Variable.account.address_3);
        ((TextView)findViewById(R.id.email)).setText(Variable.account.email);
        ((TextView)findViewById(R.id.mobile)).setText(Variable.account.mobile);
    }

    @Override
    public void onResume(){
        super.onResume();
        // 更新账户基本信息
        HttpClient conn = new HttpClient();
        conn.setHeader("cookie", "JSESSIONID=" + Variable.getSessionId());
        conn.setUrl(Constant.url + "pClientInfoAction!getAccountInfo.htm");
        new Thread(new HttpPostRunnable(conn, new UpdateInfoHandler())).start();
    }

	@Override
	public void onClick(View view) {
		super.onClick(view);
		switch (view.getId()) {
            case R.id.ly_title_left:  Utility.gotoMainpage(3);  break;
            case R.id.logout:
                Variable.isLogin = false;
                Variable.settings_editor.putString(SharedKeys.sessionid,"");
                Variable.settings_editor.commit();
                Utility.gotoMainpage(3);
                break;

            case R.id.my_more:startActivity(new Intent(this, AccountInfoMoreActivity.class)); break;
            case R.id.my_delivery:startActivity(new Intent(this, DeliveryAddressManageActivity.class));break;
            case R.id.my_capital:startActivity(new Intent(this, CapitalShowActivity.class)); break;

            case R.id.my_verify:
                // 获取账户认证信息
                HttpClient connVerify = new HttpClient();
                connVerify.setHeader("cookie", "JSESSIONID=" + Variable.getSessionId());
                connVerify.setUrl(Constant.url + "pClientInfoAction!getAuthInfo.htm");
                new Thread(new HttpPostRunnable(connVerify, new GetAuthInfoHandler())).start();
                break;

            case R.id.ly_title_right: startActivity(new Intent(this, AccountInfoEditActivity.class));break;
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
                    Utility.alertDialog("无法获取账户认证信息");
                    break;
            }
        }
    }

    public class UpdateInfoHandler extends HttpJsonHandler {
        @Override
        public void handlerData(int code, JSONObject data){
            super.handlerData(code,data);
            switch (code){
                case 0:
                    JsonApi.getAccountInfo(data);
                    Tasks.showImage(Variable.account.imageUrl, (ImageView) findViewById(R.id.image), 1);
                    Tasks.showBigImage(Variable.account.imageUrl, (ImageView) findViewById(R.id.image), 1);
                    break;
                case 1:
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public boolean onKeyDown (int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK: Utility.gotoMainpage(3); break;
        }
        return true;
    }


}


