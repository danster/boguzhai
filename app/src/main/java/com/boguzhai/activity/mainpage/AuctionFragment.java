package com.boguzhai.activity.mainpage;

import android.app.Fragment;
import android.os.Bundle;
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
import com.boguzhai.logic.widget.ListViewForScrollView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AuctionFragment extends Fragment {
    private static String TAG = "AuctionFragment";
    private View view;
    private MainActivity context;

    private ArrayList<Auction> list = new ArrayList<Auction>();
    private ListViewForScrollView listview;
    private AuctionListAdapter adapter;

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
                updateDynamicAuctions(checkedId);
            }
        });

        listview = (ListViewForScrollView) view.findViewById(R.id.auction_list);
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

        conn.setUrl(Constant.url + "pMainAction!getAuctionList.htm");
        new Thread(new HttpPostRunnable(conn,new AuctionListHandler())).start();
    }

    public class AuctionListHandler extends HttpJsonHandler {
        @Override
        public void handlerData(int code, JSONObject data){
            try {
                switch (code){
                    case 0:
                        JSONArray auctionArray = data.getJSONArray("auctionMainList");
                        list.clear();
                        for(int i=0; i<auctionArray.length(); ++i){
                            JSONObject auctionObj = auctionArray.getJSONObject(i);
                            Auction auction = Auction.parseJson(auctionObj);
                            list.add(auction);
                        }
                        adapter.notifyDataSetChanged();
                        break;
                    default:
                        break;
                }
            }catch(JSONException ex) {
                Toast.makeText(Variable.app_context, "数据解析报错", Toast.LENGTH_LONG).show();
            }
        }
    }

}