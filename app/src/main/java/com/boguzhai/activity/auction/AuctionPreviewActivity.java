package com.boguzhai.activity.auction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.boguzhai.R;
import com.boguzhai.activity.base.BaseActivity;
import com.boguzhai.activity.base.Constant;
import com.boguzhai.activity.base.Variable;
import com.boguzhai.activity.items.LotListAdapter;
import com.boguzhai.logic.dao.Lot;
import com.boguzhai.logic.thread.HttpPostRunnable;
import com.boguzhai.logic.thread.ShowLotListHandler;
import com.boguzhai.logic.utils.HttpClient;
import com.boguzhai.logic.utils.Utility;
import com.boguzhai.logic.widget.ListViewForScrollView;

import java.util.ArrayList;

public class AuctionPreviewActivity extends BaseActivity {

    private ArrayList<Lot> list;
    private ListViewForScrollView listview;
    LotListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLinearView(R.layout.auction_preview);
        title.setText("拍卖预展");
        title_right.setText("筛选");
        title_right.setVisibility(View.VISIBLE);
        init();
    }

    private void init(){
        Utility.showAuctionInfo(baseActivity, Variable.currentAuction, Variable.currentSession);
        showListView();
    }

    @Override
    public void onClick(View v){
        super.onClick(v);
        switch (v.getId()) {
            case R.id.title_right:
                startActivity(new Intent(this, LotFilterActivity.class));
            default:
            break;
        }
    }

    // 展示专场的拍品列表
    public void showListView(){
        listview = (ListViewForScrollView) findViewById(R.id.lotlist);
        list = new ArrayList<Lot>();
        adapter = new LotListAdapter(this, list);
        listview.setAdapter(adapter);

        HttpClient conn = new HttpClient();
        conn.setUrl(Constant.url+"pAuctionInfoAction!getAuctionInfoListBySessionId.htm?auctionSessionId="+Variable.currentSession.id);
        new Thread(new HttpPostRunnable(conn,new ShowLotListHandler(list, adapter))).start();
    }

}
