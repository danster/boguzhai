package com.boguzhai.activity.me.info;

import android.content.DialogInterface;
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
import com.boguzhai.logic.thread.Tasks;
import com.boguzhai.logic.utils.HttpClient;
import com.boguzhai.logic.utils.StringApi;
import com.boguzhai.logic.utils.Utility;

import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class AccountBindActivity extends BaseActivity {
    private TextView get_check_code;
    private EditText email, check_code;

    private int time = 30;
    private TimerTask task;
    private String bind_info = "邮箱";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        this.setScrollView(R.layout.me_myinfo_bind);
        init();
	}

    public void init(){
        bind_info = getIntent().getStringExtra("bind_info");

        title.setText("绑定"+bind_info+"");
        get_check_code = (TextView)findViewById(R.id.get_check_code);
        email = (EditText)findViewById(R.id.mobile);
        check_code = (EditText)findViewById(R.id.check_code);
        email.setHint("请填写绑定"+bind_info+"");

        TextView status = (TextView)findViewById(R.id.status);
        if(bind_info.equals("邮箱")){
            if(Variable.account.email.equals("")){
                status.setText("未绑定"+bind_info+"");
            }else {
                status.setText("已绑定"+bind_info+"：" + Variable.account.email);
            }
        } else if(bind_info.equals("手机")){
            if(Variable.account.mobile.equals("")){
                status.setText("未绑定"+bind_info+"");
            }else {
                status.setText("已绑定"+bind_info+"：" + Variable.account.mobile);
            }
        }

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
                if(bind_info.equals("邮箱")){
                    if(!StringApi.checkEmail(email.getText().toString())){
                        Utility.alertDialog("绑定" + bind_info + "错误: " + StringApi.tips);
                        break;
                    }
                } else if(bind_info.equals("手机")){
                    if(!StringApi.checkPhoneNumber(email.getText().toString())){
                        Utility.alertDialog("绑定" + bind_info + "错误: " + StringApi.tips);
                        break;
                    }
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

                Tasks.getCheckCode(email.getText().toString());
                new Timer().schedule(task, 0, 1000); // 一秒后启动task
                break;
            case R.id.submit:
                if(email.getText().toString().equals("")){
                    Utility.alertDialog("绑定" + bind_info + "不能为空！");
                    break;
                } else if(check_code.getText().toString().equals("")){
                    Utility.alertDialog("" + bind_info + "验证码不能为空");
                    break;
                }

                HttpClient conn_bind = new HttpClient();
                conn_bind.setHeader("cookie", "JSESSIONID=" + Variable.account.sessionid);
                conn_bind.setParam("checkCode", check_code.getText().toString());

                if(bind_info.equals("邮箱")){
                    if(!StringApi.checkEmail(email.getText().toString())){
                        Utility.alertDialog("绑定" + bind_info + "错误: " + StringApi.tips);
                        break;
                    }
                    conn_bind.setParam("email", email.getText().toString());
                    conn_bind.setUrl(Constant.url + "pClientInfoAction!bindEmail.htm");
                } else if(bind_info.equals("手机")){
                    if(!StringApi.checkPhoneNumber(email.getText().toString())){
                        Utility.alertDialog("绑定" + bind_info + "错误: " + StringApi.tips);
                        break;
                    }
                    conn_bind.setParam("mobile", email.getText().toString());
                    conn_bind.setUrl(Constant.url + "pClientInfoAction!bindMobile.htm");
                }
                new Thread(new HttpPostRunnable(conn_bind, new SubmitHandler())).start();
                Utility.showLoadingDialog("正在绑定" + bind_info + "，请稍后...");
                break;
        }
    }

    public class SubmitHandler extends HttpJsonHandler {
        @Override
        public void handlerData(int code, JSONObject data){
            Utility.dismissLoadingDialog();
            super.handlerData(code,data);
            switch(code){
                case 0:
                    Utility.alertDialog("绑定"+bind_info+"成功",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                if(bind_info.equals("邮箱")){
                                    Variable.account.email = email.getText().toString();
                                } else if(bind_info.equals("手机")){
                                    Variable.account.mobile = email.getText().toString();
                                }
                                finish();
                            }
                        });
                    break;
                case 2: Utility.alertDialog("验证码错误"); break;
                default: Utility.alertDialog("绑定" + bind_info + "失败"); break;
            }
        }
    }


}
