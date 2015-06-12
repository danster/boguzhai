package com.boguzhai.activity.login;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.boguzhai.R;
import com.boguzhai.activity.base.BaseActivity;
import com.boguzhai.activity.base.Constant;
import com.boguzhai.logic.thread.HttpJsonHandler;
import com.boguzhai.logic.thread.HttpPostRunnable;
import com.boguzhai.logic.utils.HttpClient;
import com.boguzhai.logic.utils.StringApi;
import com.boguzhai.logic.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;


public class ResetPwdActivity extends BaseActivity {
	
	private EditText mobile, check_code, pwd_input, pwd_confirm;
    private TextView get_check_code;
    private String realCheckcode = "";

    private StringApi stringApi = new StringApi();

    private int time = 30;
    private TimerTask task;
    private ProgressDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setScrollView(R.layout.login_forget_pwd);
		title.setText("重置密码");
		setBaseEnv();
	}
	
	protected void setBaseEnv() {
        dialog = Utility.getProgressDialog("正在重置密码，请稍后...");
        mobile = (EditText)findViewById(R.id.mobile);
        check_code = (EditText)findViewById(R.id.check_code);
        pwd_input = (EditText)findViewById(R.id.pwd_input);
        pwd_confirm = (EditText)findViewById(R.id.pwd_confirm);
        get_check_code = (TextView)findViewById(R.id.get_check_code);

        int[] ids = { R.id.get_check_code, R.id.check_code_clear, R.id.pwd_input_clear,
                        R.id.pwd_confirm_clear, R.id.submit};
        this.listen(ids);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {  
		case R.id.check_code_clear:  check_code.setText(""); break;
        case R.id.pwd_input_clear:   pwd_input.setText(""); break;
        case R.id.pwd_confirm_clear: pwd_confirm.setText(""); break;
        case R.id.get_check_code:
            if(! stringApi.checkPhoneNumber(mobile.getText().toString())){
                Utility.alertDialog(stringApi.tips);
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

            HttpClient conn_checkcode = new HttpClient();
            conn_checkcode.setParam("mobile", mobile.getText().toString());
            conn_checkcode.setUrl(Constant.url + "pLoginAction!getMobileCheckCodeNoLogin.htm");
            new Thread(new HttpPostRunnable(conn_checkcode, new HttpJsonHandler() {
                @Override
                public void handlerData(int code, JSONObject data) {
                    super.handlerData(code, data);
                    switch(code){
                        case 0:
                            Utility.toastMessage("发送验证码成功，请注意查收");
                            try {
                                realCheckcode = data.getString("check_code");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            break;
                        case 1:
                            Utility.toastMessage("发送验证码失败，请重新获取验证码");
                            break;
                        default:
                            break;
                    }

                }
            })).start();

            new Timer().schedule(task, 0, 1000); // 一秒后启动task
            break;

        case R.id.submit:
            if(!stringApi.checkPhoneNumber(mobile.getText().toString())){
                Utility.alertDialog(stringApi.tips);
                break;
            }
            if(check_code.getText().toString().equals("")){
                Utility.alertDialog("请输入手机验证码");
                break;
            }
            if(pwd_input.getText().toString().length() < 6){
                Utility.alertDialog("请输入6位有效密码");
                break;
            }
            if(!pwd_confirm.getText().toString().equals(pwd_input.getText().toString())){
                Utility.alertDialog("请确认您的新密码");
                break;
            }

            HttpClient conn2 = new HttpClient();
            conn2.setParam("mobile", mobile.getText().toString());
            conn2.setParam("password", pwd_input.getText().toString());
            conn2.setParam("checkcode", check_code.getText().toString());
            conn2.setParam("realCheckcode", check_code.getText().toString());
            conn2.setUrl(Constant.url+"pLoginAction!resetPwd.htm");
            new Thread(new HttpPostRunnable(conn2, new SubmitHandler())).start();
            dialog.show();

            break;

            default:  break;
		};
	}

    public class SubmitHandler extends HttpJsonHandler {
        @Override
        public void handlerData(int code, JSONObject data){
            dialog.dismiss();
            super.handlerData(code,data);
            switch(code){
                case 0:
                    Utility.alertDialog("重置密码成功", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Utility.gotoMainpage(3);
                        }
                    });
                    break;
                case 1: Utility.alertDialog("手机号码填写错误");  break;
                case 2: Utility.alertDialog("验证码错误");       break;
                case 3: Utility.alertDialog("重置密码失败, 请检查您的密码修改信息"); break;
                default:Utility.alertDialog("重置密码失败, 请检查您的密码修改信息"); break;
            }
        }
    }


}
