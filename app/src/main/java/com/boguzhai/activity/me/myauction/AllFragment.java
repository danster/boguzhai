package com.boguzhai.activity.me.myauction;


import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.boguzhai.R;
import com.boguzhai.logic.gaobo.MyAuction;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class AllFragment extends BaseFragment {

    private static final String TAG = "AllFragment";
    private List<MyAuction> myAuctions;//我的拍卖会


    @Override
    public void initData() {
        lv_my_auction = (ListView) view.findViewById(R.id.lv_myauctions);

        //从网络获取数据
        myAuctions = testData();

        initAdapter(myAuctions);
        lv_my_auction.setAdapter(myAuctionAdapter);
    }






    /**
     * 测试数据
     *
     * @return
     */
    private List<MyAuction> testData() {
        List<MyAuction> myAuctions1 = new ArrayList<MyAuction>();
        MyAuction myAuction = new MyAuction();
        myAuction.auctionTime = "2014.7.15 18:00 - 2014.7.15 21:00";
        myAuction.name = "2014冬季艺术品大拍" ;
        myAuction.status = "1";
        myAuction.type = "1";
        myAuction.deposit = 500;
        myAuctions1.add(myAuction);

        myAuction.auctionTime = "2014.7.15 18:00 - 2014.7.15 21:00";
        myAuction.name = "2014冬季艺术品大拍" ;
        myAuction.status = "2";
        myAuction.type = "2";
        myAuction.deposit = 500;
        myAuctions1.add(myAuction);

        myAuction.auctionTime = "2014.7.15 18:00 - 2014.7.15 21:00";
        myAuction.name = "2014冬季艺术品大拍" ;
        myAuction.status = "3";
        myAuction.type = "3";
        myAuction.deposit = 500;
        myAuctions1.add(myAuction);

        myAuction.auctionTime = "2014.7.15 18:00 - 2014.7.15 21:00";
        myAuction.name = "2014冬季艺术品大拍" ;
        myAuction.status = "1";
        myAuction.type = "1";
        myAuction.deposit = 500;
        myAuctions1.add(myAuction);

        return myAuctions1;
    }

}
