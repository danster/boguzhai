package com.boguzhai.activity.me.mylot;

import android.os.Bundle;
import android.view.View;

import com.boguzhai.R;
import com.boguzhai.activity.base.BaseActivity;
import com.boguzhai.activity.base.Constant;
import com.boguzhai.activity.base.Variable;
import com.boguzhai.logic.thread.HttpJsonHandler;
import com.boguzhai.logic.thread.HttpPostRunnable;
import com.boguzhai.logic.utils.HttpClient;
import com.boguzhai.logic.view.XListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MylotActivity extends BaseActivity {
	private XListView listview;
	private MylotAuctionListAdapter adapter;
	private ArrayList<MylotAuction> list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        this.setLinearView(R.layout.me_pay_mylot);
        title.setText("我的拍品");
        init();
	}

	protected void init(){
		listview = (XListView) findViewById(R.id.list);
		listview.setPullLoadEnable(false);
		listview.setPullRefreshEnable(false);
		list = new ArrayList<MylotAuction>();
		adapter = new MylotAuctionListAdapter(this, list);
		listview.setAdapter(adapter);
		this.listen(R.id.submit);

		// 获取发票内容
//		HttpClient conn = new HttpClient();
//		conn.setHeader("cookie", "JSESSIONID=" + Variable.account.sessionid);
//		conn.setUrl(Constant.url + "pTraceAction!getInvoiceContent.htm");
//		new Thread(new HttpPostRunnable(conn, new HttpJsonHandler() {
//			@Override
//			public void handlerData(int code, JSONObject data) {
//				super.handlerData(code, data);
//			}
//		})).start();

		// 获取运费
//		HttpClient conn = new HttpClient();
//		conn.setHeader("cookie", "JSESSIONID=" + Variable.account.sessionid);
//		conn.setParam("addressId", "2040");
//		conn.setParam("auctionId", "418609");
//		conn.setUrl(Constant.url + "pTraceAction!getFreight.htm");
//		new Thread(new HttpPostRunnable(conn, new HttpJsonHandler() {
//			@Override
//			public void handlerData(int code, JSONObject data) {
//				super.handlerData(code, data);
//			}
//		})).start();

		// 计算保费
//		HttpClient conn = new HttpClient();
//		conn.setHeader("cookie", "JSESSIONID=" + Variable.account.sessionid);
//		conn.setParam("price","2300.5");
//		conn.setUrl(Constant.url + "pTraceAction!getSupportPrice.htm");
//		new Thread(new HttpPostRunnable(conn, new HttpJsonHandler() {
//			@Override
//			public void handlerData(int code, JSONObject data) {
//				super.handlerData(code, data);
//			}
//		})).start();

		// 提交订单信息
		HttpClient conn = new HttpClient();
		conn.setHeader("cookie", "JSESSIONID=" + Variable.account.sessionid);
		conn.setParam("price","2300.5");
		conn.setUrl(Constant.url + "pTraceAction!getSupportPrice.htm");
		new Thread(new HttpPostRunnable(conn, new HttpJsonHandler() {
			@Override
			public void handlerData(int code, JSONObject data) {
				super.handlerData(code, data);
			}
		})).start();

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
