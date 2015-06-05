package com.boguzhai.activity.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.boguzhai.activity.mainpage.AppStartActivity;
import com.boguzhai.logic.thread.AddressHandler;
import com.boguzhai.logic.thread.HttpPostRunnable;
import com.boguzhai.logic.thread.LotTypeHandler;
import com.boguzhai.logic.utils.HttpClient;

public class ActivityEntry extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        startActivity(new Intent(this, AppStartActivity.class));
    }

    private void init(){
        Variable.app = this.getApplication();
        Variable.app_context = this.getApplicationContext();

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

}


