package com.boguzhai.activity.base;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Pair;

import com.boguzhai.R;
import com.boguzhai.activity.me.info.DeliveryAddress;
import com.boguzhai.logic.dao.Account;
import com.boguzhai.logic.dao.Address_1;
import com.boguzhai.logic.dao.Auction;
import com.boguzhai.logic.dao.Lot;
import com.boguzhai.logic.dao.Lottype_1;
import com.boguzhai.logic.dao.Session;
import com.boguzhai.logic.view.XListView;

import java.util.ArrayList;

// 程序启动后值会不时变动的变量
public class Variable {

    public static Account account = new Account(); //用户的账号信息
    public static boolean isLogin = false;      //用户的登录状态
    public static int mainTabIndex = R.id.rb_1; //首页的tab位置

    // 启动程序(Application)时初始化或重新赋值
    public static SharedPreferences settings = null ;
    public static SharedPreferences.Editor settings_editor = null ;
    public static PackageInfo pInfo = null;     // APP包的相关信息

    // 第一次进入Activity(ActivityEntry)时初始化
    public static Context app_context = null;
    public static Application app = null;
    public static ArrayList<Address_1> mapZone = null;
    public static ArrayList<Lottype_1> mapLottype = null;
    public static ArrayList< Pair<String,String> > mapProvince = null;
    public static ArrayList< Pair<String,String> > mapLottype1 = null;


    /** 下列信息需要在用户登录时立刻从服务器端获取 **/
    /** 下列信息需要在用户登录后实时从服务器端获取 **/
    /** 下列信息需要在 SD 卡里进行缓存          **/
    /** 下列信息需要在 SQlite 数据库里进行缓存   **/

    // 响应用户不同的选择
    public static Auction currentAuction = null;
    public static Session currentSession = null;
    public static Lot currentLot = null;
    public static XListView currentListview=null; // 加载更多
    public static SwipeRefreshLayout currentRefresh=null; // 下拉更新
    public static DeliveryAddress currentDeliveryAddress = null; //修改收货地址时初始化

    public static String biddingNo="";  // 我的网络拍卖号,如N007,没有为空字符串
    public static Activity currentActivity = null; //当前的Activity
    public static Activity lastActivity = null;    //跳转进来之前的Activity

    public static Bitmap currentBitmap = null;    //即将进行缩放的图片


}
