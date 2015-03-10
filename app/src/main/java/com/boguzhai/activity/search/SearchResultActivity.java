package com.boguzhai.activity.search;

import android.os.Bundle;
import android.view.View;

import com.boguzhai.R;
import com.boguzhai.activity.base.BaseActivity;
import com.boguzhai.activity.items.LotListAdapter;
import com.boguzhai.logic.dao.Lot;
import com.boguzhai.logic.utils.ListViewForScrollView;

import java.util.ArrayList;

public class SearchResultActivity extends BaseActivity {
    private static final String TAG = "SearchResultActivity";
    private ArrayList<Lot> list;
    private ListViewForScrollView listview;
    LotListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLinearView(R.layout.search_result);
        title.setText("搜索结果");

        title_right.setText("排序");
        title_right.setVisibility(View.VISIBLE);

        showLotList();
    }

    @Override
    public void onClick(View v){
        super.onClick(v);
        switch (v.getId()) {

            default:
                break;
        }
    }

    // 展示专场的拍品列表
    public void showLotList(){
        listview = (ListViewForScrollView) findViewById(R.id.lotlist);
        list = new ArrayList<Lot>();

        for(int i=0; i<10; i++){
            Lot lot = new Lot();
            list.add(lot);
        }

        adapter = new LotListAdapter(this, list);
        listview.setAdapter(adapter);
    }


}
