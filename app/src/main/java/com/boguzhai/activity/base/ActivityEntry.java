package com.boguzhai.activity.base;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.boguzhai.R;
import com.boguzhai.activity.mainpage.MainActivity;
import com.boguzhai.logic.service.NetworkStateService;
import com.boguzhai.logic.thread.AddressHandler;
import com.boguzhai.logic.thread.HttpPostRunnable;
import com.boguzhai.logic.thread.LotTypeHandler;
import com.boguzhai.logic.utils.HttpClient;

public class ActivityEntry extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        init();

        //进入正式程序
        Variable.mainTabIndex=R.id.rb_4;
        startActivity(new Intent(this, MainActivity.class));
    }

    private void init(){
        Variable.app = this.getApplication();
        Variable.app_context = this.getApplicationContext();
        Variable.account.image = BitmapFactory.decodeResource(getResources(), R.drawable.image);

        //启动后端的所有服务
        startService(new Intent(this, NetworkStateService.class));

        //预先获取一些网络数据备用
        prepareNetworkData();
    }

    private void prepareNetworkData(){

        HttpClient conn_address = new HttpClient();
        conn_address.setUrl(Constant.url+"pCommonAction!getAddressZoneMap.htm");
        new Thread(new HttpPostRunnable(conn_address, new AddressHandler())).start();

        HttpClient conn_lotType = new HttpClient();
        conn_lotType.setUrl(Constant.url+"pCommonAction!getAuctionTypeMap.htm");
        new Thread(new HttpPostRunnable(conn_lotType, new LotTypeHandler())).start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopService(new Intent(this, NetworkStateService.class));
    }

}


