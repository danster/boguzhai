package com.boguzhai.activity.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.boguzhai.R;
import com.boguzhai.activity.mainpage.MainActivity;


public class AppStartActivity extends Activity {
    String TAG = "AppStartActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.app_start_page);

		new Handler().postDelayed(new Runnable() {
            public void run() {
                if( App.settings.getBoolean("firstlogin", true)){
                    App.settings_editor.putBoolean("firstlogin", false);
                    App.settings_editor.commit();
                    Log.i(TAG,"first login, go to app guide");
                    startActivity(new Intent(AppStartActivity.this, AppGuideActivity.class));
                } else {
                    Log.i(TAG,"not first login, go to main activity");
                    App.mainTabIndex = R.id.rb_1;
                    startActivity(new Intent(AppStartActivity.this, MainActivity.class));
                }
            }}, 2000);
	}
}
