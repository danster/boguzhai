package com.boguzhai.activity.mainpage;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.boguzhai.R;
import com.boguzhai.activity.items.AuctionListAdapter;
import com.boguzhai.logic.dao.Auction;
import com.boguzhai.logic.widget.ListViewForScrollView;

import java.util.ArrayList;

public class AuctionFragment extends Fragment {
    private static String TAG = "AuctionFragment";
    private View view;
    private MainActivity context;

    private ArrayList<Auction> list, list_show;
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
                updateDynamicAuctions(checkedId);
            }
        });

        setDynamicAuctions();
        RadioButton radio = (RadioButton)view.findViewById(R.id.auction_status_all);
        radio.setChecked(true);

        return view;
    }

    public void setDynamicAuctions(){
        listview = (ListViewForScrollView) view.findViewById(R.id.auction_list);
        list = new ArrayList<Auction>();
        list_show = new ArrayList<Auction>();

        Auction at = new Auction(); at.status="预展中"; at.type="网络"; list.add(at);
        at = new Auction(); at.status="预展中"; at.type="同步"; list.add(at);
        at = new Auction(); at.status="进行中"; at.type="网络"; list.add(at);
        at = new Auction(); at.status="进行中"; at.type="同步"; list.add(at);
        at = new Auction(); at.status="已结束"; at.type="网络"; list.add(at);
        at = new Auction(); at.status="已结束"; at.type="同步"; list.add(at);

        list_show.addAll(list);
        adapter = new AuctionListAdapter(context, list_show);

        listview.setAdapter(adapter);
    }

    public void updateDynamicAuctions(int checkedId){
        list_show.clear();
        switch (checkedId){
            case R.id.auction_status_all:
                list_show.addAll(list);
                break;
            case R.id.auction_status_preview:
                for(int i=0; i<list.size(); i++)
                    if(list.get(i).status=="预展中")
                        list_show.add(list.get(i));
                break;
            case R.id.auction_status_bid:
                for(int i=0; i<list.size(); i++)
                    if(list.get(i).status=="进行中")
                        list_show.add(list.get(i));
                break;
            case R.id.auction_status_over:
                for(int i=0; i<list.size(); i++)
                    if(list.get(i).status=="已结束")
                        list_show.add(list.get(i));
                break;
            default:
                break;
        }
        adapter.notifyDataSetChanged();
    }

}