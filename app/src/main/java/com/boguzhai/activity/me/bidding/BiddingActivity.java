package com.boguzhai.activity.me.bidding;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;

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
import com.boguzhai.logic.utils.Utility;
import com.boguzhai.logic.widget.ListViewForScrollView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class BiddingActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {


    private ListViewForScrollView lv_bidding;//竞价列表
    private BiddingAuctionAdapter adapter;
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
        swipe_layout.setColorSchemeResources(R.color.gold);
        swipe_layout.setOnRefreshListener(this);
        biddingAuctionList = new ArrayList<>();

        lv_bidding = (ListViewForScrollView) findViewById(R.id.bidding_list);
        Utility.showLoadingDialog("正在加载...");
        onRefresh();
	}

    public void initData() {
        adapter = new BiddingAuctionAdapter(this, biddingAuctionList);
        lv_bidding.setAdapter(adapter);
    }

    public void requestData() {
        conn = new HttpClient();
        conn.setHeader("cookie", "JSESSIONID=" + Variable.getSessionId());
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
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Utility.dismissLoadingDialog();
            switch (msg.what) {
                case 1:
                case 9:
                    swipe_layout.setRefreshing(false);
                    break;
            }
        }

        @Override
        public void handlerData(int code, JSONObject data) {
            switch (code) {
                case -1:
                    startActivityForResult(new Intent(context, LoginActivity.class), 0);
                    break;
                case 1:
                    Utility.toastMessage("网络异常，获取信息失败");
                    break;
                case 0:
                    Log.i(TAG, "获取信息成功");

                    swipe_layout.setRefreshing(false);
                    JSONArray jArray;
                    try {
                        jArray = data.getJSONArray("biddingLotList");

                        BiddingAuction biddingAuction;

                        if(jArray.length() == 0) {
                            Utility.toastMessage("暂无数据");
                        }else {
                        }
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
                                lot.isLeader = array.getJSONObject(j).getString("isLeader");
                                lot.no = array.getJSONObject(j).getString("no");
                                lot.name = array.getJSONObject(j).getString("name");
                                lot.biddingCount = array.getJSONObject(j).getInt("biddingCount");
                                lot.appraisal1 = Double.parseDouble(array.getJSONObject(j).getString("appraisal1"));
                                lot.appraisal2 = Double.parseDouble(array.getJSONObject(j).getString("appraisal2"));
                                lot.startPrice = Double.parseDouble(array.getJSONObject(j).getString("startPrice"));
                                lot.currentPrice = Double.parseDouble(array.getJSONObject(j).getString("currentPrice"));
                                lot.topPrice = array.getJSONObject(j).getString("myTopPrice");
                                lot.imageUrl = array.getJSONObject(j).getString("image");
                                biddingAuction.lotList.add(lot);
                            }
                            biddingAuctionList.add(biddingAuction);
                        }
                        initData();
                        Utility.dismissLoadingDialog();
                        // 网络批量下载拍品图片
                        new AsyncTask<Void, Void, Void>() {
                            @Override
                            protected Void doInBackground(Void... params) {
                                try {
                                    BitmapFactory.Options options = new BitmapFactory.Options();
                                    options.inJustDecodeBounds = false;
                                    options.inSampleSize = 5; //width，hight设为原来的 .. 分之一

                                    Log.i(TAG, "开始下载图片");
                                    for(BiddingAuction auction : biddingAuctionList) {
                                        for (BiddingLot lot : auction.lotList) {
                                            InputStream in = new URL(lot.imageUrl).openStream();
                                            lot.image = BitmapFactory.decodeStream(in, null, options);
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void result) {
                                adapter.notifyDataSetChanged();
                                Log.i(TAG, "图片下载完成");
                            }
                        }.execute();
                        Log.i(TAG, "我的竞价数据解析完毕");
                    } catch (JSONException e) {
                        Log.i(TAG, "json解析异常");
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(!Variable.isLogin){
            finish();
        }
    }
}
