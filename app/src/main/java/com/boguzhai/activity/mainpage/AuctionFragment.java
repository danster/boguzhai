package com.boguzhai.activity.mainpage;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.boguzhai.R;
import com.boguzhai.activity.base.Constant;
import com.boguzhai.activity.base.Variable;
import com.boguzhai.activity.items.AuctionListAdapter;
import com.boguzhai.logic.dao.Auction;
import com.boguzhai.logic.dao.MyInt;
import com.boguzhai.logic.thread.HttpJsonHandler;
import com.boguzhai.logic.thread.HttpPostRunnable;
import com.boguzhai.logic.utils.HttpClient;
import com.boguzhai.logic.utils.JsonApi;
import com.boguzhai.logic.view.XListView;

import org.json.JSONObject;

import java.util.ArrayList;

public class AuctionFragment extends Fragment implements XListView.IXListViewListener , SwipeRefreshLayout.OnRefreshListener{
    private static String TAG = "AuctionFragment";
    private View view;
    private MainActivity context;
    private int myCheckedId;


    private ArrayList<Auction> list = new ArrayList<Auction>();
    private XListView listview;
    private AuctionListAdapter adapter;

    // 分页信息必备条件
    private SwipeRefreshLayout swipe_layout;
    private MyInt order = new MyInt(1);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.main_fg_auction, null);
        context = (MainActivity)getActivity();

        ((TextView)view.findViewById(R.id.title_center)).setText("拍卖大厅");
        view.findViewById(R.id.title_left).setVisibility(View.INVISIBLE);
        view.findViewById(R.id.title_right).setVisibility(View.INVISIBLE);

        init();
        return view;
    }

    private void init(){
        RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.auction_status_tab);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                myCheckedId = checkedId;
                updateDynamicAuctions(myCheckedId, 1);
            }
        });

        listview = (XListView) view.findViewById(R.id.auction_list);
        Variable.currentListview = listview;
        listview.setPullLoadEnable(true);
        listview.setPullRefreshEnable(false);
        listview.setXListViewListener(this);
        adapter = new AuctionListAdapter(context, list);
        listview.setAdapter(adapter);

        swipe_layout = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
        swipe_layout.setColorSchemeResources(R.color.gold);
        swipe_layout.setOnRefreshListener(this);
        Variable.currentRefresh = swipe_layout;

        updateDynamicAuctions(R.id.auction_status_all, 1); //默认展示全部拍卖会
        RadioButton radio = (RadioButton)view.findViewById(R.id.auction_status_all);
        radio.setChecked(true);

    }

    @Override
    public void onRefresh() {
        swipe_layout.setRefreshing(true);
        this.order.value = 1;
        updateDynamicAuctions(R.id.auction_status_all, this.order.value);
    }

    @Override
    public void onLoadMore() {
        this.order.value ++ ;
        updateDynamicAuctions(R.id.auction_status_all, this.order.value);
    }

    public void updateDynamicAuctions(int checkedId ,int number){
        HttpClient conn = new HttpClient();
        switch (checkedId){
            case R.id.auction_status_all:     conn.setParam("status", "");      break;
            case R.id.auction_status_preview: conn.setParam("status","预展中"); break;
            case R.id.auction_status_bid:     conn.setParam("status","拍卖中"); break;
            case R.id.auction_status_over:    conn.setParam("status","已结束"); break;
            default: break;
        }
        conn.setParam("number", number + "");
        conn.setUrl(Constant.url + "pMainAction!getAuctionMainList.htm");
        new Thread(new HttpPostRunnable(conn,new AuctionListHandler())).start();
    }

    class AuctionListHandler extends HttpJsonHandler {
        @Override
        public void handlerData(int code, JSONObject data){
            switch (code){
                case 0:
                    swipe_layout.setRefreshing(false);
                    listview.stopLoadMore();

                    if(order.value == -1) {    break;             }
                    if(order.value == 1)  {    list.clear(); }

//                    try {
//                        int count = Integer.parseInt(data.getString("count"));
//                        int size = Integer.parseInt(data.getString("size"));
//
//                        if ((order.value - 1) * size == count) {
//                            order.value = -1;
//                            break;
//                        } else if (order.value * size > count) {
//                            order.value = -1;
//                        } else {
//                            order.value++;
//                        }
//                    }catch (JSONException e) {
//                        e.printStackTrace();
//                    }

                    list.addAll(JsonApi.getAuctionList(data));
                    adapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
    }



}
