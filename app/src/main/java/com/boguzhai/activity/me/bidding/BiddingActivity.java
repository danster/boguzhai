package com.boguzhai.activity.me.bidding;

import android.os.Bundle;
import android.view.View;

import com.boguzhai.R;
import com.boguzhai.activity.base.BaseActivity;
import com.boguzhai.activity.me.items.BiddingListAdapter;
import com.boguzhai.logic.dao.Auction;
import com.boguzhai.logic.utils.ListViewForScrollView;

import java.util.ArrayList;

public class BiddingActivity extends BaseActivity {


    private ListViewForScrollView lv_bidding;//整个竞价列表
    private BiddingListAdapter adaper;
    private ArrayList<Auction> mAuctionList;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        this.setLinearView(R.layout.me_bidding);
        title.setText("正在竞价");
        init();
	}

	protected void init(){

        lv_bidding = (ListViewForScrollView) findViewById(R.id.bidding_list);
        mAuctionList = new ArrayList<>();
        testData();
        adaper = new BiddingListAdapter(this, mAuctionList);
        lv_bidding.setAdapter(adaper);

	}

    public void testData() {
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
}
