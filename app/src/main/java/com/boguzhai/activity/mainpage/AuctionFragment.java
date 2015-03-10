package com.boguzhai.activity.mainpage;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.boguzhai.R;
import com.boguzhai.logic.dao.Auction;
import com.boguzhai.logic.utils.ListViewForScrollView;

import java.util.ArrayList;

public class AuctionFragment extends Fragment {
    private static String TAG = "AuctionFragment";
    private View view;
    private MainActivity context;

    private ArrayList<Auction> list;
    private ListViewForScrollView listview;
    AuctionListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.main_fg_auction, null);
        context = (MainActivity)getActivity(); //getApplicationContext()

        TextView title = (TextView)view.findViewById(R.id.title_center);
        title.setText("拍卖大厅");
        view.findViewById(R.id.title_left).setVisibility(View.INVISIBLE);
        view.findViewById(R.id.title_right).setVisibility(View.INVISIBLE);

        RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.auction_status_tab);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
            }
        });

        setDynamicAuctions();

        return view;
    }

    public void setDynamicAuctions(){

        listview = (ListViewForScrollView) view.findViewById(R.id.auction_list);
        list = new ArrayList<Auction>();

        for(int i=0; i<2; i++){
            Auction auction = new Auction();
            list.add(auction);
        }

        adapter = new AuctionListAdapter(context, list);
        listview.setAdapter(adapter);
    }

}