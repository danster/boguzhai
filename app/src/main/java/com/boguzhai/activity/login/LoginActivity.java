package com.boguzhai.activity.login;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.boguzhai.R;
import com.boguzhai.activity.base.App;
import com.boguzhai.activity.base.BaseActivity;
import com.boguzhai.activity.mainpage.MainActivity;
import com.boguzhai.logic.dao.Account;
import com.boguzhai.logic.dao.SharedKeys;
import com.boguzhai.logic.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;


public class LoginActivity extends BaseActivity {
	private static final String TAG = "LoginActivity";
	protected Account account=null;
	protected TextView username_tv, password_tv;
    private String username, password;
    private Class<?> cls = null;

	private static ProgressDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setScrollView(R.layout.login_page);
        title.setText("会员登录");
		init();
	}

	protected void init(){
        Intent intent = getIntent();
        cls =  (Class<?>)getIntent().getSerializableExtra("cls");

        if(cls != null ){
            Log.i("cls name: ",cls.getName());
        }
		this.username_tv = (TextView)findViewById(R.id.username);
		this.password_tv = (TextView)findViewById(R.id.password);

        username = App.settings.getString(SharedKeys.username, null);
		password= App.settings.getString(SharedKeys.password, null);

		this.username_tv.setText(username == null?"":username);
		this.password_tv.setText(password == null?"":password);

		dialog = new ProgressDialog(this);
		dialog.setTitle("提示");
		dialog.setMessage("正在登录，请稍后...");
		dialog.setCancelable(true); // could be killed by backward
		dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消",
			new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog, int which) {
			        dialog.dismiss();
			    }
		});

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
            App.isLogin = true;
            if(cls != null){
                startActivity(new Intent(this, cls));
            }else{
                App.mainTabIndex = R.id.rb_4;
                startActivity(new Intent(this, MainActivity.class));
            }


//            username = username_tv.getText().toString();
//            password = password_tv.getText().toString();
//            App.settings_editor.putString(SharedKeys.username, username);
//            App.settings_editor.putString(SharedKeys.password, password);
//            App.settings_editor.commit();
//
//
//            if(!StringApi.checkPhoneNumber(username)){
//                tips.setMessage(StringApi.tips).create().show();
//                break;
//            }else if(password.length() <= 0){
//                tips.setMessage("密码不能为空").create().show();
//                break;
//            }else {
//                HttpRequestApi conn = new HttpRequestApi();
//                conn.addParam("m", "login");
//                conn.addParam("username", username);
//                conn.addParam("password", password);
//                conn.setUrl("http://www.boguzhai.com/api.jhtml");
//        		new Thread(new HttpPostRunnable(conn,new LoginHandler())).start();
//        		LoginActivity.dialog.show();  //出现在finish()之后会出错
//        	}

        break;
        default:  break;
		};
	}

	@SuppressLint("HandlerLeak")
	public class LoginHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
        	LoginActivity.dialog.dismiss();
            if(msg.what == 1){
            	try {
                    JSONObject result = new JSONObject((String)msg.obj);
                    int code = Integer.parseInt(result.getString("code"));
                    if(code == 0){
                        Utility.updateUserInfo(result.getJSONObject("data"));
                        finish();
                    } else {
                		tips.setMessage("登录失败: 用户名或者密码错误").create().show();
                	}
        		} catch (JSONException ex) {
                    tips.setMessage("服务器出错").create().show();
                }
            }else if(msg.what== 0 ){
        		tips.setTitle("网络提示").setMessage("网络连接超时").create().show();
            }

        }
	}
}
