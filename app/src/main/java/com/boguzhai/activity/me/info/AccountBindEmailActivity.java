package com.boguzhai.activity.me.info;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.boguzhai.R;
import com.boguzhai.activity.base.BaseActivity;
import com.boguzhai.activity.base.Constant;
import com.boguzhai.activity.base.Variable;
import com.boguzhai.logic.thread.GetCheckcodeHandler;
import com.boguzhai.logic.thread.HttpJsonHandler;
import com.boguzhai.logic.thread.HttpPostRunnable;
import com.boguzhai.logic.utils.HttpClient;
import com.boguzhai.logic.utils.StringApi;

import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class AccountBindEmailActivity extends BaseActivity {
    private TextView staus, get_check_code;
    private EditText email, check_code;

    int time = 30;
    TimerTask task;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        this.setScrollView(R.layout.me_myinfo_bind_email);
        title.setText("绑定邮箱");

        init();
	}

    public void init(){
        staus = (TextView)findViewById(R.id.status);
        get_check_code = (TextView)findViewById(R.id.get_check_code);
        email = (EditText)findViewById(R.id.email);
        check_code = (EditText)findViewById(R.id.check_code);

        this.listen(R.id.get_check_code);
        this.listen(R.id.submit);
        this.listen(R.id.check_code_clear);
    }

    @Override
    public void onClick(View v){
        super.onClick(v);
        switch (v.getId()) {
            case R.id.check_code_clear:  check_code.setText(""); break;
            case R.id.get_check_code:
                if(!StringApi.checkEmail(email.getText().toString())){
                    this.alertMessage(StringApi.tips);
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

                Log.i(TAG, "去获取验证码");
                HttpClient conn = new HttpClient();
                conn.setParam("email", email.getText().toString());
                conn.setUrl(Constant.url+"pLoginAction!getMobileCheckCode.htm");
                new Thread(new HttpPostRunnable(conn, new GetCheckcodeHandler(this))).start();
                new Timer().schedule(task, 0, 1000); // 一秒后启动task
                break;
            case R.id.submit:
                if(!StringApi.checkEmail(email.getText().toString())){
                    this.alertMessage(StringApi.tips);
                    break;
                }
                if(check_code.getText().toString().equals("")){
                    this.alertMessage("请输入邮箱验证码");
                    break;
                }
                HttpClient conn2 = new HttpClient();
                conn2.setParam("sessionid", Variable.account.sessionid);
                conn2.setParam("mobile", email.getText().toString());
                conn2.setParam("checkCode", check_code.getText().toString());
                conn2.setUrl(Constant.url+"pClientInfoAction!rebindEmail.htm");
                new Thread(new HttpPostRunnable(conn2, new SubmitHandler())).start();
                break;
        }
    }

    public class SubmitHandler extends HttpJsonHandler {
        @Override
        public void handlerData(int code, JSONObject data){
            switch(code){
            case 0:
                baseActivity.getAlert("绑定邮箱成功")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            baseActivity.startActivity(new Intent(baseActivity, AccountInfoActivity.class));
                        }
                    }).show();
                break;
            default:
                baseActivity.alertMessage("绑定邮箱失败");
                break;
            }
        }
    }


}
