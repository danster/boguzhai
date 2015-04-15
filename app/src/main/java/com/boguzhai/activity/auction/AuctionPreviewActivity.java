package com.boguzhai.activity.auction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.boguzhai.R;
import com.boguzhai.activity.base.BaseActivity;
import com.boguzhai.activity.items.LotListAdapter;
import com.boguzhai.logic.dao.Lot;
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

        for(int i=0; i<9; i++){
            Lot lot = new Lot();
            lot.name = "明朝景德镇花瓶 "+i;
            lot.id = i;
            lot.No = 100 - i;
            lot.apprisal1 = Math.random()*10000;
            lot.apprisal1 = Math.random()*20000;
            lot.startPrice = Math.random()*5000;
            list.add(lot);
        }

        adapter = new LotListAdapter(this, list);
        listview.setAdapter(adapter);
    }

}
