package com.boguzhai.activity.login;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.boguzhai.R;
import com.boguzhai.activity.base.BaseActivity;
import com.boguzhai.activity.base.Constant;
import com.boguzhai.activity.me.info.IdentityVerifyActivity;
import com.boguzhai.logic.thread.HttpJsonHandler;
import com.boguzhai.logic.thread.HttpPostRunnable;
import com.boguzhai.logic.thread.Tasks;
import com.boguzhai.logic.utils.HttpClient;
import com.boguzhai.logic.utils.StringApi;
import com.boguzhai.logic.utils.Utility;

import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class RegisterActivity extends BaseActivity {
	private EditText username, check_code, password;
    private TextView get_check_code;
    private CheckBox isAgreedView;
    boolean isAgreed = false;

    private int time = 30;
    private TimerTask task;
    private ProgressDialog dialog;

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
        dialog = Utility.getProgressDialog("正在注册，请稍后...");

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
            if(! StringApi.checkPhoneNumber(username.getText().toString())){
                Utility.alertMessage(StringApi.tips);
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

            Tasks.getCheckCodeNoLogin(username.getText().toString());
            new Timer().schedule(task, 0, 1000);
            break;

        case R.id.register:
            if(!StringApi.checkPhoneNumber(username.getText().toString())){
                Utility.alertMessage(StringApi.tips);
                break;
            }
            if(check_code.getText().toString().equals("")){
                Utility.alertMessage("请输入手机验证码");
                break;
            }
            if(password.getText().toString().length() < 6){
                Utility.alertMessage("请输入6位有效密码");
                break;
            }
            if(!isAgreed){
                Utility.alertMessage("同意博古斋拍卖网注册协议后，才能注册");
                break;
            }

            dialog.show();
            HttpClient conn = new HttpClient();
            conn.setParam("mobile", username.getText().toString());
            conn.setParam("password", password.getText().toString());
            conn.setParam("checkcode", check_code.getText().toString());
            conn.setUrl(Constant.url + "pLoginAction!register.htm");
            new Thread(new HttpPostRunnable(conn, new RegisterHandler())).start();

            break;
        case R.id.login: startActivity(new Intent(this, LoginActivity.class)); break;
        case R.id.protocol:
            Utility.openUrl("http://www.shbgz.com/otherAction!autionrule.htm?target=3_3_8");
            break;
        default:  break;
		};
	}

    public class RegisterHandler extends HttpJsonHandler {
        @Override
        public void handlerData(int code, JSONObject data){
            dialog.dismiss();
            super.handlerData(code, data);
            switch(code){
                case 0:
                    Utility.alertDialog("注册成功，请进行实名认证",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Utility.gotoActivity(IdentityVerifyActivity.class);
                        }
                    });
                    break;
                case 1:  Utility.alertMessage("该账户已经被注册");   break;
                case 2:  Utility.alertMessage("验证码错误");        break;
                case 3:  Utility.alertMessage("账户号码错误");   break;
                default: Utility.alertMessage("注册失败, 请检查您的注册信息");   break;
            }
        }
    }


}
