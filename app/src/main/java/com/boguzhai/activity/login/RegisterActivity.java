package com.boguzhai.activity.login;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.boguzhai.R;
import com.boguzhai.activity.base.App;
import com.boguzhai.activity.base.BaseActivity;
import com.boguzhai.activity.mainpage.MainActivity;

import java.util.TimerTask;


public class RegisterActivity extends BaseActivity {
	
	private EditText username, check_code, password;
    private CheckBox isAgreedView;
    boolean isAgreed = false;

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
		case R.id.get_check_code:  	break;
        case R.id.register:
            App.mainTabIndex = R.id.rb_4;
            startActivity(new Intent(this, MainActivity.class));
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


}
