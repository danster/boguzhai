package com.boguzhai.activity.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.boguzhai.R;
import com.boguzhai.activity.base.App;
import com.boguzhai.activity.base.BaseActivity;
import com.boguzhai.activity.mainpage.MainActivity;
import com.boguzhai.logic.dao.SharedKeys;
import com.boguzhai.logic.thread.HttpPostHandler;
import com.boguzhai.logic.thread.HttpPostRunnable;
import com.boguzhai.logic.utils.HttpRequestApi;
import com.boguzhai.logic.utils.StringApi;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends BaseActivity {
	private static final String TAG = "LoginActivity";
	protected TextView username_tv, password_tv;
    private String username, password;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setScrollView(R.layout.login_page);
        title.setText("会员登录");
		init();
	}

	protected void init(){
		this.username_tv = (TextView)findViewById(R.id.username);
		this.password_tv = (TextView)findViewById(R.id.password);
        username = App.settings.getString(SharedKeys.username, null);
		password= App.settings.getString(SharedKeys.password, null);
		this.username_tv.setText(username == null?"":username);
		this.password_tv.setText(password == null?"":password);

		int[] ids = { R.id.username_clear, R.id.password_clear, R.id.register,
					  R.id.forget_pwd, R.id.login};
		this.listen(ids);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.username_clear: username_tv.setText("");	break;
		case R.id.password_clear: password_tv.setText("");	break;
        case R.id.register:
        	startActivity(new Intent(this, RegisterActivity.class));
        break;
        case R.id.forget_pwd:
        	startActivity(new Intent(this, ForgetPwdActivity.class));
        break;
        case R.id.login:
            username = username_tv.getText().toString();
            password = password_tv.getText().toString();
            App.settings_editor.putString(SharedKeys.username, username);
            App.settings_editor.putString(SharedKeys.password, password);
            App.settings_editor.commit();

            if(!StringApi.checkPhoneNumber(username)){
                tips.setMessage(StringApi.tips).create().show();
                break;
            }else if(password.length() <= 0){
                tips.setMessage("密码不能为空").create().show();
                break;
            }else {
                HttpRequestApi conn = new HttpRequestApi();
                conn.addParam("mobile", username);
                conn.addParam("password", password);
                conn.setUrl("http://test.shbgz.com/tradingsys/phones/pLoginAction!login.htm");
        		new Thread(new HttpPostRunnable(conn, new LoginHandler(this))).start();
        	}
        break;
        default:  break;
		};
	}

	public class LoginHandler extends HttpPostHandler {
        public LoginHandler(Context context){ super(context);}
        @Override
        public void handlerData(int code, JSONObject data){
            try {
            switch (code){
            case 0:
                App.isLogin = true;
                App.account.password = password;
                App.account.sessionid = data.has("sessionid") ? data.getString("sessionid") : "";

                JSONObject account = data.getJSONObject("account");
                Log.i(TAG, account.toString());

                App.account.name = account.has("name") ? account.getString("name") : "";
                App.account.nickname = account.has("nickname") ? account.getString("nickname"): "";
                App.account.address_1 = account.has("address_1") ? account.getString("address_1"): "";
                App.account.address_2 = account.has("address_2") ? account.getString("address_2"): "";
                App.account.address_3 = account.has("address_3") ? account.getString("address_3"): "";
                App.account.address = account.has("address") ? account.getString("address"): "";
                App.account.email = account.has("email") ? account.getString("email"): "";
                App.account.mobile = account.has("mobile") ? account.getString("mobile"): "";
                App.account.image = account.has("image") ? account.getString("image"): "";
                App.account.telephone = account.has("telephone") ? account.getString("telephone"): "";
                App.account.fax = account.has("fax") ? account.getString("fax"): "";
                App.account.qq = account.has("qq") ? account.getString("qq"): "";

                App.mainTabIndex = R.id.rb_1;
                this.context.startActivity(new Intent(this.context, MainActivity.class));
            break;
            case 1: this.context.alertMessage("登录失败: 用户名或者密码错误！"); break;
            }
            }catch(JSONException ex) {
                this.context.alertMessage("抱歉, 解析信息时报错了");
            }
        }
	}
}
