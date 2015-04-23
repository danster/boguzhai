package com.boguzhai.activity.me.bidding;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.boguzhai.R;
import com.boguzhai.activity.base.BaseActivity;
import com.boguzhai.logic.dao.Auction;
import com.boguzhai.logic.dao.BiddingLot;
import com.boguzhai.logic.view.XListViewForSrollView;
import com.boguzhai.logic.widget.ListViewForScrollView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BiddingActivity extends BaseActivity implements XListViewForSrollView.IXListViewListener, SwipeRefreshLayout.OnRefreshListener {


    private XListViewForSrollView lv_bidding;//整个竞价列表
    private BiddingListAdapter adaper;
    private List<Auction> mAuctionList;
    private List<BiddingAuctionList> mList;
    private SwipeRefreshLayout swipe_refresh;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        this.setLinearView(R.layout.me_bidding);
        title.setText("正在竞价");
        init();
	}

	protected void init(){

        swipe_refresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipe_refresh.setColorSchemeResources(R.color.gold);
        swipe_refresh.setOnRefreshListener(this);


        lv_bidding = (XListViewForSrollView) findViewById(R.id.bidding_list);
        lv_bidding.setXListViewListener(this);
        lv_bidding.setPullLoadEnable(true);
        lv_bidding.setPullRefreshEnable(false);
        mAuctionList = new ArrayList<>();
        testData();
        adaper = new BiddingListAdapter(this, mAuctionList);
        lv_bidding.setAdapter(adaper);

	}

    public void testData() {
//        List<BiddingAuctionList> mList = new ArrayList<>();
//        BiddingAuctionList biddingAuctionList = new BiddingAuctionList();
//        biddingAuctionList.auction.name = "2015新春大拍";
//
//        BiddingLot lot = new BiddingLot();
//        lot.isLeader = 0;
//        lot.No = 123;
//        lot.name = "明代唐伯虎书法作品";
//        lot.priceCount = 5;
//        lot.apprisal1 = 5000;
//        lot.apprisal2 = 8000;
//        lot.startPrice = 3000;
//        lot.nowPrice = 4000;
//        lot.topPrice = 4000;
//        biddingAuctionList.lotList.add(lot);
//
//        lot = new BiddingLot();
//        lot.isLeader = 1;
//        lot.No = 312;
//        lot.name = "唐代瓷器";
//        lot.priceCount = 5;
//        lot.apprisal1 = 8000;
//        lot.apprisal2 = 10000;
//        lot.startPrice = 5000;
//        lot.nowPrice = 9000;
//        lot.topPrice = 8000;
//        biddingAuctionList.lotList.add(lot);


        Auction auction = new Auction();
        auction.type = "1";
        auction.name = "2015新春大拍";
        auction.dealCount = 8;
        mAuctionList.add(auction);

        auction = new Auction();
        auction.type = "2";
        auction.name = "2014年终拍卖会";
        auction.dealCount = 5;
        mAuctionList.add(auction);
    }



	@Override
	public void onClick(View view) {
        super.onClick(view);
    }

    @Override
    public void onRefresh() {
        swipe_refresh.setRefreshing(true);
        Log.i(TAG, "下拉刷新");
        new Thread() {
            @Override
            public void run() {
                SystemClock.sleep(2000);
                BiddingActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(BiddingActivity.this, "刷新成功", Toast.LENGTH_SHORT).show();
                        swipe_refresh.setRefreshing(false);
                    }
                });
            }
        }.start();
    }


    @Override
    public void onLoadMore() {
        Log.i(TAG, "加载更多");
        new Thread() {
            @Override
            public void run() {
                SystemClock.sleep(2000);
                BiddingActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(BiddingActivity.this, "加载更多", Toast.LENGTH_SHORT).show();
                        lv_bidding.stopLoadMore();
                    }
                });
            }
        }.start();

    }
}
