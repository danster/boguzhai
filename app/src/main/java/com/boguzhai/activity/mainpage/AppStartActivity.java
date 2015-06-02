package com.boguzhai.activity.mainpage;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.boguzhai.R;
import com.boguzhai.activity.base.BaseActivity;
import com.boguzhai.activity.base.Variable;
import com.boguzhai.logic.dao.SharedKeys;
import com.boguzhai.logic.utils.Utility;

public class AppStartActivity extends BaseActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setLinearView(R.layout.app_start_page);
        title_bar.setVisibility(View.GONE);

		new Handler().postDelayed(new Runnable() {
            public void run() {
                if( Variable.settings.getBoolean(SharedKeys.firstlogin, true)){
                    Variable.settings_editor.putBoolean(SharedKeys.firstlogin, false);
                    Variable.settings_editor.commit();
                    startActivity(new Intent(AppStartActivity.this, AppGuideActivity.class));
                } else {
                    Utility.gotoMainpage(1);
                }
            }}, 1500);
	}
}
