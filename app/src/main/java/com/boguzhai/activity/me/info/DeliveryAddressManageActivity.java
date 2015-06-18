package com.boguzhai.activity.me.info;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.boguzhai.R;
import com.boguzhai.activity.base.BaseActivity;
import com.boguzhai.activity.base.Constant;
import com.boguzhai.activity.base.Variable;
import com.boguzhai.logic.thread.HttpJsonHandler;
import com.boguzhai.logic.thread.HttpPostRunnable;
import com.boguzhai.logic.utils.HttpClient;
import com.boguzhai.logic.utils.Utility;
import com.boguzhai.logic.widget.XListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DeliveryAddressManageActivity extends BaseActivity{

    private XListView listview;
    private DeliveryAddressListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        this.setLinearView(R.layout.me_myinfo_addr_manage);
        title.setText("收货地址管理");
        init();
	}

	protected void init(){
        listview = (XListView) findViewById(R.id.list);
        listview.setPullLoadEnable(false);
        listview.setPullRefreshEnable(false);
        adapter = new DeliveryAddressListAdapter(this, Variable.account.deliveryAddressList);
        listview.setAdapter(adapter);
        this.listen(R.id.add_address);
	}

    protected void getDeliveryAddress(){
        HttpClient conn = new HttpClient();
        conn.setHeader("cookie", "JSESSIONID=" + Variable.getSessionId());
        conn.setUrl(Constant.url + "pClientInfoAction!getDeliveryAddress.htm");
        new Thread(new HttpPostRunnable(conn, new UpdateAddressHandler())).start();
    }

    @Override
    public void onResume(){
        super.onResume();
        this.getDeliveryAddress();
    }

	@Override
	public void onClick(View view) {
		super.onClick(view);
		switch (view.getId()) {
            case R.id.add_address:
                Variable.currentDeliveryAddress = null;
                startActivity(new Intent(this, DeliveryAddressEditActivity.class));
                break;
            default: break;
		};
	}

    class UpdateAddressHandler extends HttpJsonHandler {
        @Override
        public void handlerData(int code, JSONObject data){
            super.handlerData(code, data);
            switch(code){
                case 0:
                    // 解析收货地址信息（列表）
                    try {
                        JSONArray deliveryAddressInfo = null;
                        deliveryAddressInfo = data.getJSONArray("deliveryAddressInfo");
                        Variable.account.deliveryAddressList.clear();
                        for(int i=0; i<deliveryAddressInfo.length(); ++i){
                            JSONObject auctionObj = deliveryAddressInfo.getJSONObject(i);
                            Variable.account.deliveryAddressList.add(DeliveryAddress.parseJson(auctionObj));
                        }
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    Utility.alertDialog("网络数据错误");
                    break;
            }
        }
    }


}
