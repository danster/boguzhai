package com.boguzhai.activity.me.myauction;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.boguzhai.R;
import com.boguzhai.activity.auction.AuctionActiveActivity;
import com.boguzhai.activity.login.LoginActivity;
import com.boguzhai.logic.gaobo.MyAuction;
import com.boguzhai.logic.thread.HttpJsonHandler;
import com.boguzhai.logic.thread.HttpPostRunnable;
import com.boguzhai.logic.utils.HttpClient;
import com.boguzhai.logic.utils.Utility;
import com.boguzhai.logic.view.XListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class BaseFragment extends Fragment implements XListView.IXListViewListener, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "BaseFragment";

    private final int baseCount = 5;
    private String status = ""; //拍卖会状态 "" "预展中" "拍卖中" "已结束"

    //    private List<MyAuction> myOriginAuctions;//从网络获取的我的拍卖会集合
    private List<MyAuction> myAuctions;//需要展示的我的拍卖会集合
    private int pageIndex = 0;//分页显示的页数，从 "0" 开始
    private boolean isSearch = false;//是否处于搜索下的显示
    private List<MyAuction> searchAuctions;
    private int searchPageIndex = 0;
    private String key;//搜索关键字

    public BaseFragment(String status) {
        this.status = status;
    }

    public MyAuctionActivity mContext;//与fragment对应的activity上下文
    public View view;//fragment对应布局
    public LayoutInflater inflater;
    public MyAuctionAdapter myAuctionAdapter;//拍卖会集合的适配器
    public XListView lv_my_auction;//布局中的listview 支持上拉加载更多地listview
    private SwipeRefreshLayout swipe_layout;//支持下拉刷新的布局
    private Button btn_my_auction_search;//点击进行搜索
    private EditText et_my_auction_keyword;//关键字
    private Spinner sp_my_auction_choose;


    public Utility utility = new Utility();
    public String[] types = {"全部", "现场拍卖", "同步拍卖", "网络拍卖"};
    private StringBuffer choose_type = new StringBuffer();

    /**
     * fragment对应布局
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.inflater = inflater;
        view = inflater.inflate(R.layout.me_myauction_fg, container, false);
        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = (MyAuctionActivity) getActivity();
        initData(status);
    }

    /**
     * 初始化适配器
     *
     * @param myAuctions 需要展示的拍卖会信息集合
     * @return 适配器
     */
    public void initAdapter(List<MyAuction> myAuctions) {
        myAuctionAdapter = new MyAuctionAdapter(mContext, myAuctions);
    }


    /**
     * 初始化数据
     */
    public void initData(String type) {
        /**
         * 从网络获取数据
         */
        HttpClient conn = new HttpClient();
        conn.setParam("sessionid", "");
        conn.setParam("status", "");
        conn.setUrl("url");
        new Thread(new HttpPostRunnable(conn, new MyAuctionHandler())).start();


        /**
         * 支持下拉刷新的layout，设置监听，重写onRefresh()方法
         */
        swipe_layout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_layout_my_auction);
        swipe_layout.setColorSchemeResources(R.color.gold);
        swipe_layout.setOnRefreshListener(this);


        /**
         * 支持上拉加载更多的listView，设置不可以下拉刷新，可以上拉加载更多，重写onLoadMore()方法
         */
        lv_my_auction = (XListView) view.findViewById(R.id.lv_myauctions);
        lv_my_auction.setPullLoadEnable(true);
        lv_my_auction.setPullRefreshEnable(false);
        lv_my_auction.setXListViewListener(this);


        switch (type) {
            case "":
                myAuctions = testData1();
                Log.i(TAG, "全部");
                break;
            case "预展中":
                myAuctions = testData3();
                Log.i(TAG, "预展中");
                break;
            case "拍卖中":
                myAuctions = testData2();
                Log.i(TAG, "拍卖中");
                break;
            case "已结束":
                myAuctions = testData4();
                Log.i(TAG, "已结束");
                break;
        }


        /**
         * 初始化适配器
         */
        initAdapter(myAuctions);
        lv_my_auction.setAdapter(myAuctionAdapter);
        myAuctionAdapter.setPageIndex(pageIndex);


        /**
         * 设置listview条目的点击事件，跳转到相应的拍卖会
         */
        lv_my_auction.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, "跳转至第" + position + "个拍品");
                Intent intent = new Intent(mContext, AuctionActiveActivity.class);
                intent.putExtra("auctionId", myAuctions.get(position - 1).id);
                startActivity(intent);
            }
        });


        sp_my_auction_choose = (Spinner) view.findViewById(R.id.sp_my_auction_choose);
        /**
         * 设置spinner， 每选择一项，就会在原始数据中筛选对应的拍卖会集合并展示，
         */
        utility.setSpinner(mContext, (Spinner)view.findViewById(R.id.sp_my_auction_choose), types, new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, "Spinner Position:" + position);
                if(position == 0) {
                    key = "";
                }else {
                    key = types[position];
                }
                searchByKey();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        /**
         * 点击搜索进行关键字匹配，在原始数据中筛选符合条件的拍卖会集合并展示
         */
        et_my_auction_keyword = (EditText) view.findViewById(R.id.et_my_auction_keyword);
        btn_my_auction_search = (Button) view.findViewById(R.id.btn_my_auction_search);
        btn_my_auction_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp_my_auction_choose.setSelection(0);
                key = et_my_auction_keyword.getText().toString().trim();
                searchByKey();
            }
        });
    }

    /**
     * 通过关键字进行过滤并显示
     */
    private void searchByKey() {
        if (TextUtils.isEmpty(key)) {
            isSearch = false;
            myAuctionAdapter = new MyAuctionAdapter(mContext, myAuctions);
            myAuctionAdapter.setPageIndex(pageIndex);
            lv_my_auction.setAdapter(myAuctionAdapter);
        } else {
            isSearch = true;
            searchAuctions = new ArrayList<>();
            int count = 0;
            if (myAuctions.size() >= baseCount * ((pageIndex + 1))) {
                count = baseCount * (pageIndex + 1);
            } else {
                count = myAuctions.size();
            }
            for (int i = 0; i < count; i++) {
                if (myAuctions.get(i).name.indexOf(key) >= 0 || myAuctions.get(i).type.indexOf(key) >= 0 || myAuctions.get(i).status.indexOf(key) >= 0
                        || myAuctions.get(i).auctionTime.indexOf(key) >= 0 || (String.valueOf(myAuctions.get(i).deposit)).indexOf(key) >= 0) {
                    searchAuctions.add(myAuctions.get(i));
                }
            }
            myAuctionAdapter = new MyAuctionAdapter(mContext, searchAuctions);
            searchPageIndex = 0;
            myAuctionAdapter.setPageIndex(searchPageIndex);
            lv_my_auction.setAdapter(myAuctionAdapter);
        }
    }


    /**
     * 下拉刷新，执行网络数据请求，清空原来的拍卖会集合，并更新。展示之前，判断spinner所选内容，进行筛选
     */
    @Override
    public void onRefresh() {
        Log.i(TAG, "下拉刷新");

        new Thread() {
            @Override
            public void run() {
                swipe_layout.setRefreshing(true);
                SystemClock.sleep(2000);
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        swipe_layout.setRefreshing(false);
                        Toast.makeText(mContext, "刷新成功", Toast.LENGTH_SHORT).show();
                        isSearch = false;
                        myAuctionAdapter = new MyAuctionAdapter(mContext, myAuctions);//请求网络重新获取到myAuctions
                        pageIndex = 0;
                        myAuctionAdapter.setPageIndex(pageIndex);
                        lv_my_auction.setAdapter(myAuctionAdapter);
                    }
                });
            }
        }.start();
    }


    /**
     * 不进行网络数据请求，只进行分页处理
     */
    @Override
    public void onLoadMore() {
        Log.i(TAG, "加载更多");
        new Thread() {
            @Override
            public void run() {
                SystemClock.sleep(200);
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if((pageIndex + 1)> (myAuctions.size()/5)) {
                            if(isSearch && !myAuctionAdapter.isLastPage()) {
                                myAuctionAdapter.setPageIndex(++searchPageIndex);
                            }else {
                                Toast.makeText(mContext, "没有更多数据了", Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            if (isSearch) {
                                Log.i(TAG, "搜索中加载更多");
                                int count = 0;
                                if (myAuctions.size() >= baseCount * ((++pageIndex + 1) + 1)) {
                                    count = baseCount * (pageIndex + 1);
                                } else {
                                    count = myAuctions.size();
                                }
                                for (int i = (baseCount * pageIndex); i < count; i++) {
                                    if (myAuctions.get(i).name.indexOf(key) >= 0 || myAuctions.get(i).type.indexOf(key) >= 0 || myAuctions.get(i).status.indexOf(key) >= 0
                                            || myAuctions.get(i).auctionTime.indexOf(key) >= 0 || (String.valueOf(myAuctions.get(i).deposit)).indexOf(key) >= 0) {
                                        searchAuctions.add(myAuctions.get(i));
                                    }
                                }
                                Log.i(TAG, "条目个数:" + myAuctionAdapter.getCurrentCount());
                                myAuctionAdapter.notifyDataSetChanged();
                                myAuctionAdapter.setPageIndex(++searchPageIndex);
                            } else {
                                myAuctionAdapter.setPageIndex(++pageIndex);
                            }
                        }
                        lv_my_auction.stopLoadMore();
                    }
                });
            }
        }.start();

    }


    /**
     * handler，处理从网络返回的数据
     */
    private class MyAuctionHandler extends HttpJsonHandler {

        @Override
        public void handlerData(int code, JSONObject data) {
            switch (code) {
                case 1:
                    Toast.makeText(mContext, "网络异常，获取信息失败", Toast.LENGTH_SHORT).show();
                    break;
                case -1:
                    Toast.makeText(mContext, "用户名密码失效，请重新登录", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(mContext, LoginActivity.class));
                    break;
                case 0:
                    Log.i(TAG, "获取信息成功");
                    JSONArray jArray;
                    try {
                        jArray = data.getJSONArray("auctionList");
                        MyAuction myAction;
                        for (int i = 0; i < jArray.length(); i++) {
                            myAction = new MyAuction();
                            myAction.name = jArray.getJSONObject(i).getString("name");
                            myAction.id = jArray.getJSONObject(i).getString("type");
                            myAction.type = jArray.getJSONObject(i).getString("type");
                            myAction.status = jArray.getJSONObject(i).getString("status");
                            myAction.auctionTime = jArray.getJSONObject(i).getString("auctionTime");
                            myAction.deposit = jArray.getJSONObject(i).getInt("deposit");
                            myAuctions.add(myAction);
                        }
                    } catch (JSONException e) {
                        Log.i(TAG, "json解析异常");
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }


    /**
     * 以下为测试模拟数据
     */

    /*
     * "已结束"
     */
    private List<MyAuction> testData4() {
        List<MyAuction> myAuctions1 = new ArrayList<MyAuction>();
        MyAuction myAuction = new MyAuction();
        myAuction.auctionTime = "2014.7.15 18:00 - 2014.7.15 21:00";
        myAuction.name = "2014冬季艺术品大拍";
        myAuction.status = "已结束";
        myAuction.type = "网络拍卖";
        myAuction.deposit = 500;
        myAuctions1.add(myAuction);
        return myAuctions1;
    }

    /*
     * "预展中"
     */
    private List<MyAuction> testData3() {
        List<MyAuction> myAuctions1 = new ArrayList<MyAuction>();
        MyAuction myAuction = new MyAuction();
        myAuction.auctionTime = "2014.7.15 18:00 - 2014.7.15 21:00";
        myAuction.name = "2014冬季艺术品大拍";
        myAuction.status = "预展中";
        myAuction.type = "现场拍卖";
        myAuction.deposit = 500;
        myAuctions1.add(myAuction);
        return myAuctions1;
    }

    /*
     * "拍卖中"
     */
    private List<MyAuction> testData2() {
        List<MyAuction> myAuctions1 = new ArrayList<MyAuction>();
        MyAuction myAuction = new MyAuction();
        myAuction = new MyAuction();
        myAuction.auctionTime = "2014.7.15 18:00 - 2014.7.15 21:00";
        myAuction.name = "2014冬季艺术品大拍";
        myAuction.status = "拍卖中";
        myAuction.type = "同步拍卖";
        myAuction.deposit = 500;
        myAuctions1.add(myAuction);
        return myAuctions1;
    }

    /*
     * "全部"
     */
    private List<MyAuction> testData1() {
        List<MyAuction> myAuctions1 = new ArrayList<MyAuction>();
        MyAuction myAuction = new MyAuction();
        myAuction.auctionTime = "2014.7.15 18:00 - 2014.7.15 21:00";
        myAuction.name = "2014冬季艺术品大拍";
        myAuction.status = "预展中";
        myAuction.type = "现场拍卖";
        myAuction.deposit = 500;
        myAuctions1.add(myAuction);

        myAuction = new MyAuction();
        myAuction.auctionTime = "2014.7.15 18:00 - 2014.7.15 21:00";
        myAuction.name = "2014冬季艺术品大拍";
        myAuction.status = "拍卖中";
        myAuction.type = "同步拍卖";
        myAuction.deposit = 500;
        myAuctions1.add(myAuction);

        myAuction = new MyAuction();
        myAuction.auctionTime = "2014.7.15 18:00 - 2014.7.15 21:00";
        myAuction.name = "2014冬季艺术品大拍";
        myAuction.status = "已结束";
        myAuction.type = "网络拍卖";
        myAuction.deposit = 500;
        myAuctions1.add(myAuction);

        myAuction = new MyAuction();
        myAuction.auctionTime = "2014.7.15 18:00 - 2014.7.15 21:00";
        myAuction.name = "2014冬季艺术品大拍";
        myAuction.status = "预展中";
        myAuction.type = "现场拍卖";
        myAuction.deposit = 500;
        myAuctions1.add(myAuction);

        myAuction = new MyAuction();
        myAuction.auctionTime = "2014.7.15 18:00 - 2014.7.15 21:00";
        myAuction.name = "2014冬季艺术品大拍";
        myAuction.status = "拍卖中";
        myAuction.type = "同步拍卖";
        myAuction.deposit = 500;
        myAuctions1.add(myAuction);

        myAuction = new MyAuction();
        myAuction.auctionTime = "2014.7.15 18:00 - 2014.7.15 21:00";
        myAuction.name = "2014冬季艺术品大拍";
        myAuction.status = "已结束";
        myAuction.type = "网络拍卖";
        myAuction.deposit = 500;
        myAuctions1.add(myAuction);

        myAuction = new MyAuction();
        myAuction.auctionTime = "2014.7.15 18:00 - 2014.7.15 21:00";
        myAuction.name = "2014冬季艺术品大拍";
        myAuction.status = "预展中";
        myAuction.type = "现场拍卖";
        myAuction.deposit = 500;
        myAuctions1.add(myAuction);

        myAuction = new MyAuction();
        myAuction.auctionTime = "2014.7.15 18:00 - 2014.7.15 21:00";
        myAuction.name = "2014冬季艺术品大拍";
        myAuction.status = "拍卖中";
        myAuction.type = "同步拍卖";
        myAuction.deposit = 500;
        myAuctions1.add(myAuction);

        myAuction = new MyAuction();
        myAuction.auctionTime = "2014.7.15 18:00 - 2014.7.15 21:00";
        myAuction.name = "2014冬季艺术品大拍";
        myAuction.status = "已结束";
        myAuction.type = "网络拍卖";
        myAuction.deposit = 500;
        myAuctions1.add(myAuction);

        myAuction = new MyAuction();
        myAuction.auctionTime = "2014.7.15 18:00 - 2014.7.15 21:00";
        myAuction.name = "2014冬季艺术品大拍";
        myAuction.status = "预展中";
        myAuction.type = "现场拍卖";
        myAuction.deposit = 500;
        myAuctions1.add(myAuction);

        myAuction = new MyAuction();
        myAuction.auctionTime = "2014.7.15 18:00 - 2014.7.15 21:00";
        myAuction.name = "2014冬季艺术品大拍";
        myAuction.status = "拍卖中";
        myAuction.type = "同步拍卖";
        myAuction.deposit = 500;
        myAuctions1.add(myAuction);

        myAuction = new MyAuction();
        myAuction.auctionTime = "2014.7.15 18:00 - 2014.7.15 21:00";
        myAuction.name = "2014冬季艺术品大拍";
        myAuction.status = "已结束";
        myAuction.type = "网络拍卖";
        myAuction.deposit = 500;
        myAuctions1.add(myAuction);

        myAuction = new MyAuction();
        myAuction.auctionTime = "2014.7.15 18:00 - 2014.7.15 21:00";
        myAuction.name = "2014冬季艺术品大拍";
        myAuction.status = "预展中";
        myAuction.type = "现场拍卖";
        myAuction.deposit = 500;
        myAuctions1.add(myAuction);

        myAuction = new MyAuction();
        myAuction.auctionTime = "2014.7.15 18:00 - 2014.7.15 21:00";
        myAuction.name = "2014冬季艺术品大拍";
        myAuction.status = "拍卖中";
        myAuction.type = "同步拍卖";
        myAuction.deposit = 500;
        myAuctions1.add(myAuction);

        myAuction = new MyAuction();
        myAuction.auctionTime = "2014.7.15 18:00 - 2014.7.15 21:00";
        myAuction.name = "2014冬季艺术品大拍";
        myAuction.status = "已结束";
        myAuction.type = "网络拍卖";
        myAuction.deposit = 500;
        myAuctions1.add(myAuction);

        myAuction = new MyAuction();
        myAuction.auctionTime = "2014.7.15 18:00 - 2014.7.15 21:00";
        myAuction.name = "2014冬季艺术品大拍";
        myAuction.status = "预展中";
        myAuction.type = "现场拍卖";
        myAuction.deposit = 500;
        myAuctions1.add(myAuction);

        return myAuctions1;
    }
}
