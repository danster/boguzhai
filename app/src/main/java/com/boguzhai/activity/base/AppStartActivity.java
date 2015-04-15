package com.boguzhai.activity.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.boguzhai.R;
import com.boguzhai.activity.mainpage.MainActivity;
import com.boguzhai.logic.dao.SharedKeys;


public class AppStartActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.app_start_page);

		new Handler().postDelayed(new Runnable() {
            public void run() {
                if( App.settings.getBoolean(SharedKeys.firstlogin, true)){
                    App.settings_editor.putBoolean(SharedKeys.firstlogin, false);
                    App.settings_editor.commit();
                    startActivity(new Intent(AppStartActivity.this, AppGuideActivity.class));
                } else {
                    App.mainTabIndex = R.id.rb_1;
                    startActivity(new Intent(AppStartActivity.this, MainActivity.class));
                }
            }}, 1500);
	}
}
