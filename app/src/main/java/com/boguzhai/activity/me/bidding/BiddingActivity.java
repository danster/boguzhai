package com.boguzhai.activity.me.bidding;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.boguzhai.R;
import com.boguzhai.activity.base.BaseActivity;
import com.boguzhai.activity.base.Constant;
import com.boguzhai.activity.base.Variable;
import com.boguzhai.activity.login.LoginActivity;
import com.boguzhai.logic.dao.Auction;
import com.boguzhai.logic.dao.BiddingLot;
import com.boguzhai.logic.thread.HttpJsonHandler;
import com.boguzhai.logic.thread.HttpPostRunnable;
import com.boguzhai.logic.utils.HttpClient;
import com.boguzhai.logic.widget.ListViewForScrollView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BiddingActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {


    private ListViewForScrollView lv_bidding;//竞价列表
    private BiddingAuctionAdapter adaper;
    private List<BiddingAuction> biddingAuctionList;
    private HttpClient conn;
    private SwipeRefreshLayout swipe_layout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        this.setLinearView(R.layout.me_bidding);
        title.setText("正在竞价");
        init();
	}

	protected void init(){

        swipe_layout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout_me_bidding);
        swipe_layout.setColorSchemeColors(R.color.gold);
        swipe_layout.setOnRefreshListener(this);
        biddingAuctionList = new ArrayList<>();

        lv_bidding = (ListViewForScrollView) findViewById(R.id.bidding_list);

        requestData();
	}

    public void initData() {
        adaper = new BiddingAuctionAdapter(this, biddingAuctionList);
        lv_bidding.setAdapter(adaper);
    }

    public void requestData() {
        conn = new HttpClient();
        conn.setHeader("cookie", "JSESSIONID=" + Variable.account.sessionid);
        conn.setUrl(Constant.url + "pClientInfoAction!getBiddingLotList.htm");
        new Thread(new HttpPostRunnable(conn, new BiddingHandler())).start();
    }


	@Override
	public void onClick(View view) {
        super.onClick(view);
    }

    @Override
    public void onRefresh() {
        swipe_layout.setRefreshing(true);
        biddingAuctionList.clear();
        requestData();
    }


    /**
     * handler，处理从网络返回的数据
     */
    private class BiddingHandler extends HttpJsonHandler {

        @Override
        public void handlerData(int code, JSONObject data) {
            switch (code) {
                case 1:
                    Toast.makeText(Variable.app_context, "网络异常，获取信息失败", Toast.LENGTH_SHORT).show();
                    break;
                case -1:
                    Toast.makeText(Variable.app_context, "用户名密码失效，请重新登录", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Variable.app_context, LoginActivity.class));
                    break;
                case 0:
                    Log.i(TAG, "获取信息成功");
                    JSONArray jArray;
                    try {
                        jArray = data.getJSONArray("biddingLotList");

                        List<BiddingAuction> biddingAuctionList = new ArrayList<>();
                        BiddingAuction biddingAuction;

                        for(int j = 0; j < jArray.length(); j++) {
                            biddingAuction = new BiddingAuction();
                            biddingAuction.auction = new Auction();
                            biddingAuction.lotList = new ArrayList<>();

                            biddingAuction.auction.name = jArray.getJSONObject(j).getString("name");
                            biddingAuction.auction.type = jArray.getJSONObject(j).getString("type");
                            biddingAuction.auction.id = jArray.getJSONObject(j).getString("id");
                            biddingAuction.auction.showNum = Integer.parseInt(jArray.getJSONObject(j).getString("showNum"));

                            JSONArray array = jArray.getJSONObject(j).getJSONArray("auctionList");

                            for (int i = 0; i < array.length(); i++) {
                                BiddingLot lot = new BiddingLot();
                                lot.id = array.getJSONObject(j).getString("id");
                                lot.isLeader = array.getJSONObject(j).getInt("isLeader");
                                lot.no = array.getJSONObject(j).getString("no");
                                lot.name = array.getJSONObject(j).getString("name");
                                lot.biddingCount = array.getJSONObject(j).getInt("biddingCount");
                                lot.appraisal1 = Double.parseDouble(array.getJSONObject(j).getString("apprisal1"));
                                lot.appraisal2 = Double.parseDouble(array.getJSONObject(j).getString("apprisal2"));
                                lot.startPrice = Double.parseDouble(array.getJSONObject(j).getString("startPrice"));
                                lot.currentPrice = Double.parseDouble(array.getJSONObject(j).getString("currentPrice"));
                                lot.topPrice = Double.parseDouble(array.getJSONObject(j).getString("myTopPrice"));
                                biddingAuction.lotList.add(lot);
                            }
                            biddingAuctionList.add(biddingAuction);
                        }
                        initData();
                    } catch (JSONException e) {
                        Log.i(TAG, "json解析异常");
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }
}
