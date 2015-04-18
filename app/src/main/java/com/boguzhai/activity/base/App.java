package com.boguzhai.activity.base;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.boguzhai.R;
import com.boguzhai.logic.dao.Account;

public class App extends Application {
	public static final String TAG = "App";
	
	/** 下列信息需要在用户登录时立刻从服务器端获取 **/
    public static Account account = new Account();
	
	/** 下列信息需要在用户登录后实时从服务器端获取 **/

	/** 下列信息需要在 SD 卡里进行缓存 **/
	
	/** 下列信息需要在 SQlite 数据库里进行缓存 **/

	/** 下列信息需要在启动APP时进行初始化 **/
    public static boolean isLogin = true;         // 用户登录状态
    public static SharedPreferences settings = null ;
    public static SharedPreferences.Editor settings_editor = null ;
    public static PackageInfo pInfo = null;       // APP包的相关信息
    public static int mainTabIndex = R.id.rb_1; //首页的tab位置
	
	@Override
    public void onCreate() { //应用程序启动时被系统调用
        Log.i(TAG,"APP onCreate()");
        //打开 SharedPreferences, 存储 Key-Value 值
        settings = this.getSharedPreferences("settings", 0);
        settings_editor = settings.edit();

        isLogin = true;

        try {
            pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
                                                                                                                              
    @Override
    public void onTerminate() { //应用程序退出时会被系统调用
        Log.i(TAG,"APP onTerminate()");
    	//关闭所有数据库接口
    }


}
