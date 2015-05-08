package com.boguzhai.activity.login;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.boguzhai.R;
import com.boguzhai.activity.base.BaseActivity;
import com.boguzhai.activity.base.Constant;
import com.boguzhai.logic.thread.GetCheckcodeHandler;
import com.boguzhai.logic.thread.HttpJsonHandler;
import com.boguzhai.logic.thread.HttpPostRunnable;
import com.boguzhai.logic.utils.HttpClient;
import com.boguzhai.logic.utils.StringApi;

import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class RegisterActivity extends BaseActivity {
	private EditText username, check_code, password;
    private TextView get_check_code;
    private CheckBox isAgreedView;
    boolean isAgreed = false;

    private StringApi stringApi = new StringApi();

    int time = 30;
    TimerTask task;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setScrollView(R.layout.login_register);
        title.setText("用户注册");
		setBaseEnv();
	}
	
	protected void setBaseEnv() {
        username = (EditText)findViewById(R.id.username);
        check_code = (EditText)findViewById(R.id.check_code);
        password = (EditText)findViewById(R.id.password);
        get_check_code = (TextView)findViewById(R.id.get_check_code);

		int[] ids = {R.id.get_check_code, R.id.agree, R.id.register, R.id.login, R.id.protocol};
		this.listen(ids);

        isAgreedView = (CheckBox)findViewById(R.id.agree);
        isAgreedView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                    if(isChecked){
                        isAgreed = true;
                    } else {
                        isAgreed = false;
                    }
                }
            }
        );
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {  
		case R.id.get_check_code:
            if(! stringApi.checkPhoneNumber(username.getText().toString())){
                this.alertMessage(stringApi.tips);
                break;
            }
            get_check_code.setEnabled(false);
            task = new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() { // UI thread
                        @Override
                        public void run() {
                            if (time <= 0) {
                                get_check_code.setEnabled(true);
                                get_check_code.setText("获取验证码");
                                time=30;
                                task.cancel();
                            } else {
                                get_check_code.setText("获取验证码(" + time+")");
                            }
                            time--;
                        }
                    });
                }
            };

            HttpClient conn = new HttpClient();
            conn.setParam("mobile", username.getText().toString());
            conn.setUrl(Constant.url+"pLoginAction!getMobileCheckCode.htm");
            new Thread(new HttpPostRunnable(conn, new GetCheckcodeHandler())).start();

            new Timer().schedule(task, 0, 1000); // 一秒后启动task
            break;

        case R.id.register:
            if(!stringApi.checkPhoneNumber(username.getText().toString())){
                this.alertMessage(stringApi.tips);
                break;
            }
            if(check_code.getText().toString().equals("")){
                this.alertMessage("请输入手机验证码");
                break;
            }
            if(password.getText().toString().length() < 6){
                this.alertMessage("请输入6位有效密码");
                break;
            }
            if(!isAgreed){
                this.alertMessage("同意博古斋拍卖网注册协议后，才能注册");
                break;
            }

            HttpClient conn2 = new HttpClient();
            conn2.setParam("mobile", username.getText().toString());
            conn2.setParam("password", password.getText().toString());
            conn2.setParam("checkcode", check_code.getText().toString());
            conn2.setUrl(Constant.url+"pLoginAction!register.htm");
            new Thread(new HttpPostRunnable(conn2, new RegisterHandler())).start();

            break;
        case R.id.login:
            startActivity(new Intent(this, LoginActivity.class));
            break;
        case R.id.protocol:
            Intent link = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.shbgz.com"));
            startActivity(link);
            break;
        default:  break;
		};
	}

    public class RegisterHandler extends HttpJsonHandler {
        @Override
        public void handlerData(int code, JSONObject data){
            switch(code){
                case 0:
                    baseActivity.getAlert("恭喜您，注册成功，请重新登录")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                baseActivity.startActivity(new Intent(baseActivity, LoginActivity.class));
                            }
                        }).show();
                    break;
                default:
                    baseActivity.alertMessage("注册失败, 请检查您的注册信息");
                    break;
            }
        }
    }


}
