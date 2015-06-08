package com.boguzhai.activity.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.boguzhai.activity.mainpage.AppStartActivity;

public class ActivityEntry extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        init();
        startActivity(new Intent(this, AppStartActivity.class));
    }

    private void init(){
        Variable.app = this.getApplication();
        Variable.app_context = this.getApplicationContext();
    }


}


