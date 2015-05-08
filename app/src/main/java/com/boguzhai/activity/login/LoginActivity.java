package com.boguzhai.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.boguzhai.R;
import com.boguzhai.activity.base.BaseActivity;
import com.boguzhai.activity.base.Constant;
import com.boguzhai.activity.base.Variable;
import com.boguzhai.activity.mainpage.MainActivity;
import com.boguzhai.logic.dao.SharedKeys;
import com.boguzhai.logic.thread.HttpJsonHandler;
import com.boguzhai.logic.thread.HttpPostRunnable;
import com.boguzhai.logic.utils.HttpClient;
import com.boguzhai.logic.utils.JsonApi;
import com.boguzhai.logic.utils.StringApi;

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
        Variable.isLogin = false;
		init();
	}

	protected void init(){
		this.username_tv = (TextView)findViewById(R.id.username);
		this.password_tv = (TextView)findViewById(R.id.password);
        username = Variable.settings.getString(SharedKeys.username, null);
		password= Variable.settings.getString(SharedKeys.password, null);
		this.username_tv.setText(username == null?"":username);
		this.password_tv.setText(password == null?"":password);

		int[] ids = { R.id.username_clear, R.id.password_clear, R.id.register, R.id.forget_pwd, R.id.login};
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
            Variable.settings_editor.putString(SharedKeys.username, username);
            Variable.settings_editor.putString(SharedKeys.password, password);
            Variable.settings_editor.commit();

            if(!StringApi.checkPhoneNumber(username)){
                tips.setMessage(StringApi.tips).create().show();
                break;
            }else if(password.length() <= 0){
                tips.setMessage("密码不能为空").create().show();
                break;
            }else {
                HttpClient conn = new HttpClient();
                conn.setParam("mobile", username);
                conn.setParam("password", password);
                conn.setUrl(Constant.url + "pClientInfoAction!login.htm");

           		new Thread(new HttpPostRunnable(conn, new LoginHandler())).start();
        	}
        break;
        default:  break;
		};
	}

    public class LoginHandler extends HttpJsonHandler {
        @Override
        public void handlerData(int code, JSONObject data){
            switch (code){
                case 0:
                    Variable.isLogin = true;
                    Variable.account.password = password;
                    JsonApi.getAccountInfo(data);
                    Variable.mainTabIndex = R.id.rb_4;
                    context.startActivity(new Intent(context, MainActivity.class));
                break;
                case 1:
                    Toast.makeText(Variable.app_context, "登录失败: 用户名或者密码错误！", Toast.LENGTH_LONG).show();
                    break;
            }
        }
	}
}
