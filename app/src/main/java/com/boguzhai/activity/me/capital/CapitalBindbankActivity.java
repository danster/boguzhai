package com.boguzhai.activity.me.capital;

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
import com.boguzhai.logic.utils.Utility;

import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class CapitalBindbankActivity extends BaseActivity {

    private int time = 30;
    private TimerTask task;
    private TextView get_check_code;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setLinearView(R.layout.me_capital_bind);
        title.setText("绑定银行卡");
        init();
	}

	protected void init(){
        ((TextView)findViewById(R.id.name)).setText(Variable.account.capitalInfo.name);
        get_check_code = (TextView)findViewById(R.id.get_check_code);
        listen(R.id.get_check_code);
        listen(R.id.ok);
        listen(R.id.bank_name_clear);
        listen(R.id.bank_number_clear);
	}

	@Override
	public void onClick(View view) {
		super.onClick(view);

		switch (view.getId()) {
            case R.id.bank_name_clear:
                ((EditText)findViewById(R.id.bank_name)).setText("");
                break;
            case R.id.bank_number_clear:
                ((EditText)findViewById(R.id.bank_number)).setText("");
                break;
            case R.id.get_check_code:
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

                Tasks.getCheckCode(Variable.account.mobile);
                new Timer().schedule(task, 0, 1000); // 一秒后启动task
                break;
            case R.id.ok:
                String bank_number = ((EditText)findViewById(R.id.bank_number)).getText().toString();
                String bank = ((EditText)findViewById(R.id.bank_name)).getText().toString();
                String check_code = ((EditText)findViewById(R.id.check_code)).getText().toString();

                if(bank.equals("")){
                    Utility.alertMessage("银行名为空");
                } else if(bank_number.equals("")){
                    Utility.alertMessage("银行账号不能为空");
                } else if(check_code.equals("")){
                    Utility.alertMessage("验证码不能为空");
                } else {

                    HttpClient conn_bind = new HttpClient();
                    conn_bind.setHeader("cookie", "JSESSIONID=" + Variable.account.sessionid);
                    conn_bind.setParam("bankName", bank);
                    conn_bind.setParam("bankNumber", bank_number);
                    conn_bind.setParam("checkCode", check_code);
                    conn_bind.setUrl(Constant.url+"pClientInfoAction!bindBankCard.htm");
                    new Thread(new HttpPostRunnable(conn_bind, new SubmitHandler())).start();
                }

                break;
            default: break;
		};
	}

    class SubmitHandler extends HttpJsonHandler {
        @Override
        public void handlerData(int code, JSONObject data){
            super.handlerData(code,data);
            switch(code){
                case 0:
                    Utility.alertMessage("绑定成功");
                    break;
                case 2:
                    Utility.alertMessage("验证码错误");
                    break;
                default:
                    Utility.alertMessage("绑定失败");
                    break;
            }
        }
    }

}


