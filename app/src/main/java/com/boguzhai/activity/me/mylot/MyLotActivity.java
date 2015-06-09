package com.boguzhai.activity.me.mylot;

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
import com.boguzhai.logic.view.XListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MyLotActivity extends BaseActivity {
	private XListView listview;
	private MylotAuctionListAdapter adapter;
	private ArrayList<MylotAuction> list;
	public static ArrayList<MylotItem> mylots; // 将购买的拍品ID

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        this.setLinearView(R.layout.me_pay_mylot);
        title.setText("我的拍品");
        init();
	}

	protected void init(){
		mylots = new ArrayList<MylotItem>();
		listview = (XListView) findViewById(R.id.list);
		listview.setPullLoadEnable(false);
		listview.setPullRefreshEnable(false);
		list = new ArrayList<MylotAuction>();
		adapter = new MylotAuctionListAdapter(this, list);
		listview.setAdapter(adapter);
		this.listen(R.id.submit);

	}

	@Override
	public void onResume() {
		super.onResume();
		HttpClient conn = new HttpClient();
		conn.setHeader("cookie", "JSESSIONID=" + Variable.account.sessionid);
		conn.setUrl(Constant.url + "pTraceAction!getMyAuctionList.htm");
		new Thread(new HttpPostRunnable(conn,new MylotHandler())).start();

	}

	@Override
	public void onClick(View view) {
        super.onClick(view);
		switch (view.getId()){
			case R.id.submit:
				if(mylots.size()<=0){
					Utility.alertDialog("请选择需要结算的拍品",null);
					break;
				}
				Utility.toastMessage("mylot length :"+mylots.size());
				Intent intent = new Intent(this, EditOrderActivity.class);
				intent.putExtra("mylots", mylots);
				startActivity(intent);
				break;
			default:
				break;
		}
    }

	class MylotHandler extends HttpJsonHandler {
		@Override
		public void handlerData(int code, JSONObject data){
			super.handlerData(code, data);
			switch (code){
				case 0:
					try {
						JSONArray auctionList = data.getJSONArray("list");
						list.clear();
						for(int i=0; i<auctionList.length(); ++i){
							list.add(MylotAuction.parseJson(auctionList.getJSONObject(i)));
						}
						adapter.notifyDataSetChanged();
					} catch (JSONException e) {
						e.printStackTrace();
					}
					break;
				default:
					break;
			}
		}
	}
}
