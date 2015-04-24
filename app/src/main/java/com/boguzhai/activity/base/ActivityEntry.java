package com.boguzhai.activity.base;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.boguzhai.R;
import com.boguzhai.activity.mainpage.MainActivity;
import com.boguzhai.logic.dao.Address_1;
import com.boguzhai.logic.dao.Address_2;
import com.boguzhai.logic.dao.Address_3;
import com.boguzhai.logic.dao.Lottype_1;
import com.boguzhai.logic.dao.Lottype_2;
import com.boguzhai.logic.dao.Lottype_3;
import com.boguzhai.logic.service.NetworkStateService;
import com.boguzhai.logic.thread.HttpJsonHandler;
import com.boguzhai.logic.thread.HttpPostRunnable;
import com.boguzhai.logic.utils.HttpClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class ActivityEntry extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        Log.i("TAG","ActivityEntry");
        System.out.print("ActivityEntry2222");
        init();
    }

    private void init(){

        Log.i("TAG","ActivityEntry init");
        Variable.app = this.getApplication();
        Variable.app_context = this.getApplicationContext();
        Variable.account.image = BitmapFactory.decodeResource(getResources(), R.drawable.image);

        //启动后端的所有服务
        startService(new Intent(this, NetworkStateService.class));

        //预先获取一些网络数据备用
        prepareNetworkData();

        //进入正式程序
        startActivity(new Intent(this, MainActivity.class));
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


    class AddressHandler extends HttpJsonHandler{
        @Override
        public void handlerData(int code, JSONObject data){
            try {
                switch (code){
                    case 0:
                        if(data==null) break;
                        JSONObject zoneMap = data.getJSONObject("addressZoneMap");

                        Variable.zoneMap = new ArrayList<Address_1>();
                        Iterator<?> keys_1 = zoneMap.keys();
                        while( keys_1.hasNext() ) {
                            Address_1 object1 = new Address_1(); //

                            String key_1 = (String)keys_1.next();
                            object1.id = key_1;

                            if (zoneMap.get(key_1) instanceof JSONObject){
                                JSONObject jsonObject1 = (JSONObject)zoneMap.get(key_1);
                                object1.name = jsonObject1.getString("value");

                                Iterator<?> keys_2 = jsonObject1.keys();
                                while( keys_2.hasNext() ) {
                                    String key_2 = (String)keys_2.next();
                                    if(key_2 != "name" && jsonObject1.get(key_2) instanceof JSONObject ){
                                        JSONObject jsonObject2 = (JSONObject) jsonObject1.get(key_2);
                                        Address_2 object2 = new Address_2(); //
                                        object2.id = key_2;
                                        object2.name = jsonObject2.getString("value");

                                        Iterator<?> keys_3 = jsonObject2.keys();
                                        while( keys_3.hasNext() ) {
                                            String key_3 = (String)keys_3.next();
                                            Address_3 object3 = new Address_3(); //

                                            object3.id = key_3;
                                            object3.name = jsonObject2.getString(key_3);
                                            object2.child.add(object3);
                                        }
                                        object1.child.add(object2);
                                    }
                                }
                            }
                            Variable.zoneMap.add(object1);
                        }
                        break;
                    case 1:
                        Toast.makeText(ActivityEntry.this,"无法获取行政区域映射表",Toast.LENGTH_LONG).show();
                        break;
                }
            }catch(JSONException ex) {
                Toast.makeText(ActivityEntry.this,"网络数据出现错误",Toast.LENGTH_LONG).show();
            }
        }
    }

    class LotTypeHandler  extends HttpJsonHandler{
        @Override
        public void handlerData(int code, JSONObject data){
            try {
                switch (code){
                    case 0:
                        if(data==null) break;
                        JSONObject zoneMap = data.getJSONObject("auctionTypeMap");

                        Variable.lottypeMap = new ArrayList<Lottype_1>();
                        Iterator<?> keys_1 = zoneMap.keys();
                        while( keys_1.hasNext() ) {
                            Lottype_1 object1 = new Lottype_1(); //

                            String key_1 = (String)keys_1.next();
                            object1.id = key_1;

                            if (zoneMap.get(key_1) instanceof JSONObject){
                                JSONObject jsonObject1 = (JSONObject)zoneMap.get(key_1);
                                object1.name = jsonObject1.getString("value");

                                Iterator<?> keys_2 = jsonObject1.keys();
                                while( keys_2.hasNext() ) {
                                    String key_2 = (String)keys_2.next();
                                    if(key_2 != "name" && jsonObject1.get(key_2) instanceof JSONObject ){
                                        JSONObject jsonObject2 = (JSONObject) jsonObject1.get(key_2);
                                        Lottype_2 object2 = new Lottype_2(); //
                                        object2.id = key_2;
                                        object2.name = jsonObject2.getString("value");

                                        Iterator<?> keys_3 = jsonObject2.keys();
                                        while( keys_3.hasNext() ) {
                                            String key_3 = (String)keys_3.next();
                                            Lottype_3 object3 = new Lottype_3(); //

                                            object3.id = key_3;
                                            object3.name = jsonObject2.getString(key_3);
                                            object2.child.add(object3);
                                        }
                                        object1.child.add(object2);
                                    }
                                }
                            }
                            Variable.lottypeMap.add(object1);
                        }
                        break;
                    case 1:
                        Toast.makeText(ActivityEntry.this,"无法获取拍品类型映射表",Toast.LENGTH_LONG).show();
                        break;
                }
            }catch(JSONException ex) {
                Toast.makeText(ActivityEntry.this,"网络数据出现错误",Toast.LENGTH_LONG).show();
            }
        }
    }

}
