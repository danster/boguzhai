package com.boguzhai.activity.base;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;

import com.boguzhai.R;
import com.boguzhai.logic.dao.Account;
import com.boguzhai.logic.dao.Address_1;
import com.boguzhai.logic.dao.Auction;
import com.boguzhai.logic.dao.Lottype_1;
import com.boguzhai.logic.dao.Session;

import java.util.ArrayList;

// 程序启动后值会不时变动的变量
public class Variable {

    public static Account account = new Account(); //用户的账号信息
    public static boolean isLogin = true;      //用户的登录状态
    public static int mainTabIndex = R.id.rb_1; //首页的tab位置

    // 启动程序(Application)时初始化或重新赋值
    public static SharedPreferences settings = null ;
    public static SharedPreferences.Editor settings_editor = null ;
    public static PackageInfo pInfo = null;     // APP包的相关信息

    // 第一次进入Activity(ActivityEntry)时初始化
    public static Context app_context = null;
    public static Application app = null;
    public static ArrayList<Address_1> zoneMap = null;
    public static ArrayList<Lottype_1> lottypeMap = null;

    /** 下列信息需要在用户登录时立刻从服务器端获取 **/
    /** 下列信息需要在用户登录后实时从服务器端获取 **/
    /** 下列信息需要在 SD 卡里进行缓存          **/
    /** 下列信息需要在 SQlite 数据库里进行缓存   **/

    // 响应用户不同的选择
    public static Auction currentAuction = null;
    public static Session currentSession = null;

}
