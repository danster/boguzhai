package com.boguzhai.activity.auction.apply;

import android.os.Bundle;

import com.boguzhai.R;
import com.boguzhai.activity.base.BaseActivity;
import com.boguzhai.activity.items.LotListAdapter;
import com.boguzhai.logic.dao.Lot;
import com.boguzhai.logic.widget.ListViewForScrollView;

import java.util.ArrayList;

public class ShowSpecialLotsActivity extends BaseActivity {

    private ArrayList<Lot> list;
    private ListViewForScrollView listview;
    LotListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLinearView(R.layout.auction_apply_show_special);
        title.setText("特殊拍品");

        showListView();
    }

    // 展示专场的拍品列表
    public void showListView(){
        listview = (ListViewForScrollView) findViewById(R.id.lotlist);
        list = new ArrayList<Lot>();

        for(int i=0; i<9; i++){
            Lot lot = new Lot();
            lot.name = "明朝景德镇花瓶 "+i;
            list.add(lot);
        }

        adapter = new LotListAdapter(this, list);
        listview.setAdapter(adapter);
    }



}
