package com.boguzhai.activity.mainpage;

import android.app.Fragment;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.boguzhai.R;
import com.boguzhai.activity.base.Constant;
import com.boguzhai.activity.base.Variable;
import com.boguzhai.activity.items.AuctionListAdapter;
import com.boguzhai.logic.dao.Auction;
import com.boguzhai.logic.thread.HttpJsonHandler;
import com.boguzhai.logic.thread.HttpPostRunnable;
import com.boguzhai.logic.utils.HttpClient;
import com.boguzhai.logic.utils.JsonApi;
import com.boguzhai.logic.view.XListViewForScrollView;

import org.json.JSONObject;

import java.util.ArrayList;

public class AuctionFragment extends Fragment implements XListViewForScrollView.IXListViewListener {
    private static String TAG = "AuctionFragment";
    private View view;
    private MainActivity context;
    private int myCheckedId;


    private ArrayList<Auction> list = new ArrayList<Auction>();
    private XListViewForScrollView listview;
    private AuctionListAdapter adapter;
    private SwipeRefreshLayout refreshLayout;

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
                updateDynamicAuctions(myCheckedId);
            }
        });

        refreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.refresh);
        refreshLayout.setColorSchemeColors(R.color.gold);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(true);
                updateDynamicAuctions(myCheckedId);
            }
        });

        listview = (XListViewForScrollView) view.findViewById(R.id.auction_list);
        listview.setPullLoadEnable(true);
        listview.setPullRefreshEnable(false);
        listview.setXListViewListener(this);

        adapter = new AuctionListAdapter(context, list);
        listview.setAdapter(adapter);

        updateDynamicAuctions(R.id.auction_status_all);//默认展示全部拍卖会
        RadioButton radio = (RadioButton)view.findViewById(R.id.auction_status_all);
        radio.setChecked(true);

    }

    public void updateDynamicAuctions(int checkedId){
        HttpClient conn = new HttpClient();
        switch (checkedId){
            case R.id.auction_status_all:     conn.setParam("status", "");      break;
            case R.id.auction_status_preview: conn.setParam("status","预展中"); break;
            case R.id.auction_status_bid:     conn.setParam("status","拍卖中"); break;
            case R.id.auction_status_over:    conn.setParam("status","已结束"); break;
            default: break;
        }
        conn.setUrl(Constant.url + "pMainAction!getAuctionMainList.htm");
        new Thread(new HttpPostRunnable(conn,new AuctionListHandler())).start();
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onLoadMore() {
        new Thread() {
        @Override
        public void run() {
            SystemClock.sleep(1000);
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    listview.stopLoadMore();
                }
            });
            }
    }.start();
    }

    class AuctionListHandler extends HttpJsonHandler {
        @Override
        public void handlerData(int code, JSONObject data){
            switch (code){
                case 0:
                    list.clear();
                    list.addAll(JsonApi.getAuctionList(data));
                    adapter.notifyDataSetChanged();
                    Toast.makeText(Variable.app_context,"更新完成",Toast.LENGTH_SHORT).show();
                    refreshLayout.setRefreshing(false);
                    break;
                default:
                    break;
            }
        }
    }



}
