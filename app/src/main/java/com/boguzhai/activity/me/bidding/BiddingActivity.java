package com.boguzhai.activity.me.bidding;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.boguzhai.R;
import com.boguzhai.activity.base.BaseActivity;
import com.boguzhai.activity.base.Variable;
import com.boguzhai.activity.login.LoginActivity;
import com.boguzhai.logic.dao.Auction;
import com.boguzhai.logic.dao.BiddingLot;
import com.boguzhai.logic.thread.HttpJsonHandler;
import com.boguzhai.logic.utils.HttpClient;
import com.boguzhai.logic.widget.ListViewForScrollView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BiddingActivity extends BaseActivity {


    private ListViewForScrollView lv_bidding;//竞价列表
    private BiddingAuctionAdapter adaper;
    private List<BiddingAuction> biddingAuctionList;
    private HttpClient conn;


    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.i(TAG, "接收到<数据获取完成>的消息！");

//            initData();

        }
    };
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        this.setLinearView(R.layout.me_bidding);
        title.setText("正在竞价");
        init();
	}

	protected void init(){
//        conn = new HttpClient();
//        conn.setParam("sessionid", "");
//        conn.setUrl("http://60.191.203.80/phones/pClientInfoAction!getBiddingLotList.htm");
//        new Thread(new HttpPostRunnable(conn, new BiddingHandler())).start();






        /**
         * 支持上拉加载更多的listView，设置不可以下拉刷新，可以上拉加载更多，重写onLoadMore()方法
         */
        lv_bidding = (ListViewForScrollView) findViewById(R.id.bidding_list);

        initData();
	}

    public void initData() {
        biddingAuctionList = testData();
        adaper = new BiddingAuctionAdapter(this, biddingAuctionList);
        lv_bidding.setAdapter(adaper);
    }
    public List<BiddingAuction> testData() {
        List<BiddingAuction> biddingAuctionList = new ArrayList<>();
        BiddingAuction biddingAuction;

        for(int j = 1; j < 11; j++) {
            biddingAuction = new BiddingAuction();
            biddingAuction.auction = new Auction();
            biddingAuction.lotList = new ArrayList<>();

            biddingAuction.auction.name = "2015新春大拍" + j;
            biddingAuction.auction.type = "同步";
            biddingAuction.auction.id = "ASC1231" + j;
            biddingAuction.auction.dealNum = 4;
            for(int i = 1; i < 3; i++)  {
                BiddingLot lot = new BiddingLot();
                lot.isLeader = 0;
                lot.no = "123";
                lot.name = "明代唐伯虎书法作品";
                lot.biddingCount = 5;
                lot.appraisal1 = 5000;
                lot.appraisal2 = 8000;
                lot.startPrice = 3000;
                lot.currentPrice = 4000;
                lot.topPrice = 4000;
                biddingAuction.lotList.add(lot);
            }
            biddingAuctionList.add(biddingAuction);
        }
        return biddingAuctionList;
    }



	@Override
	public void onClick(View view) {
        super.onClick(view);
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
                        Log.i(TAG, "数据获取完成！");
                        handler.sendEmptyMessage(0);
                    } catch (JSONException e) {
                        Log.i(TAG, "json解析异常");
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }
}
