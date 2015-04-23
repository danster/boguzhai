package com.boguzhai.activity.base;

import android.app.Application;
import android.content.pm.PackageManager;

public class MyApplication extends Application {
	@Override
    public void onCreate() {

        //打开 SharedPreferences, 存储 Key-Value 值
        Variable.settings = this.getSharedPreferences("settings", 0);
        Variable.settings_editor = Variable.settings.edit();

        try {
            Variable.pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
                                                                                                                              
    @Override
    public void onTerminate() {

    }
}
