package com.boguzhai.activity.login;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.boguzhai.R;
import com.boguzhai.activity.base.BaseActivity;

import java.util.TimerTask;


public class ForgetPwdActivity extends BaseActivity {
	
	private EditText phone, check_code, pwd_input, pwd_confirm;

    int time = 20;
    TimerTask task;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setScrollView(R.layout.login_forget_pwd);
		title.setText("忘记密码");
		setBaseEnv();
	}
	
	protected void setBaseEnv() {
		phone = (EditText)findViewById(R.id.phone);
        check_code = (EditText)findViewById(R.id.check_code);
        pwd_input = (EditText)findViewById(R.id.pwd_input);
        pwd_confirm = (EditText)findViewById(R.id.pwd_confirm);

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

        default:  break;
		};
	}


}
