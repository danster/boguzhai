package com.boguzhai.activity.base;

import android.app.Application;

public class MyApplication extends Application {
	@Override
    public void onCreate() {

        //打开 SharedPreferences, 存储 Key-Value 值
        Variable.settings = this.getSharedPreferences("settings", 0);
        Variable.settings_editor = Variable.settings.edit();
    }
                                                                                                                              
    @Override
    public void onTerminate() {

    }
}
