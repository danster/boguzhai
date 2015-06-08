package com.boguzhai.activity.mainpage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.boguzhai.R;
import com.boguzhai.activity.base.Variable;
import com.boguzhai.logic.dao.SharedKeys;

public class AppStartActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.app_start_page);

		new Handler().postDelayed(new Runnable() {
            public void run() {
                if( Variable.settings.getBoolean(SharedKeys.firstlogin, true)){
                    Variable.settings_editor.putBoolean(SharedKeys.firstlogin, false);
                    Variable.settings_editor.commit();
                    startActivity(new Intent(AppStartActivity.this, AppGuideActivity.class));
                } else {
                    startActivity(new Intent(AppStartActivity.this, MainActivity.class));
                }
            }}, 1500); // 显示页面1.5秒后跳转
	}
}
