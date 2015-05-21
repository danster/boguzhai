package com.boguzhai.activity.me.collect;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.boguzhai.R;
import com.boguzhai.activity.base.Constant;
import com.boguzhai.activity.base.Variable;
import com.boguzhai.activity.login.LoginActivity;
import com.boguzhai.logic.dao.CollectionLot;
import com.boguzhai.logic.thread.HttpJsonHandler;
import com.boguzhai.logic.thread.HttpPostRunnable;
import com.boguzhai.logic.utils.HttpClient;
import com.boguzhai.logic.view.XListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link android.app.Fragment} subclass.
 */
public class MyCollectionFragment extends Fragment implements XListView.IXListViewListener, SwipeRefreshLayout.OnRefreshListener {

    public static String TAG = "MyCollectionFragment";

    private List<CollectionLot> myCollections;//所有收藏拍品集合，只有在刷新的情况下才会更新此集合。
    private int number = 1;//分页显示的页数，从 "0" 开始
    private String key;//搜索关键字
    private boolean isSearch = false;//是否处于搜索
    private List<CollectionLot> tempCollections;//搜索时显示的集合
    private String status = "";//"" "预展中" "拍卖中" "已结束" "已流拍"

    private MyCollectionActivity mContext;//fragment关联的activity
    private MyCollectionAdapter adapter;//适配器

    private SwipeRefreshLayout swipe_layout_my_collection;//支持下拉刷新的布局
    private XListView lv_my_collection;//支持加载更多的listview
    private View view;//fragment对应的视图
    private LayoutInflater inflater;
    private EditText et_my_collection_keyword;//查询关键字
    private Button btn_my_collection_search;//点击查询按钮

    private HttpClient conn;
    private int size;
    private int totalCount;//结果总数
    private int currentCount;//当前数量


    public MyCollectionFragment(String status) {
        this.status = status;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.me_mycollection_fg, null);
        this.inflater = inflater;
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = (MyCollectionActivity) getActivity();
        initView();
        requestData();
    }


    private void requestData() {
        conn = new HttpClient();
        conn.setHeader("cookie", "JSESSIONID=" + Variable.account.sessionid);
        conn.setUrl(Constant.url + "pClientStowAction!getCollectedAuctionList.htm");
        conn.setParam("status", status);//拍卖会状态 "" "预展中" "拍卖中" "已结束"
        conn.setParam("number", String.valueOf(number));//分页序号，从1开始
        new Thread(new HttpPostRunnable(conn, new MyCollectionHandler())).start();
    }


    private void initView() {
        tempCollections = new ArrayList<>();
        myCollections = new ArrayList<>();
        /**
         * 支持上拉加载更多地listview，设置上拉监听事件，重写onLoadMore()方法
         */
        lv_my_collection = (XListView) view.findViewById(R.id.lv_my_collections);
        lv_my_collection.setPullLoadEnable(true);
        lv_my_collection.setPullRefreshEnable(false);
        lv_my_collection.setXListViewListener(this);


        /**
         * 搜索关键字
         */
        et_my_collection_keyword = (EditText) view.findViewById(R.id.et_my_collection_keyword);
        et_my_collection_keyword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isSearch = false;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        btn_my_collection_search = (Button) view.findViewById(R.id.btn_my_collection_search);

        /**
         * 支持下拉刷新的布局，设置下拉监听事件，重写onRefresh()方法
         */
        swipe_layout_my_collection = (SwipeRefreshLayout) view.findViewById(R.id.swipe_layout_my_collection);
        swipe_layout_my_collection.setColorSchemeResources(R.color.gold);
        swipe_layout_my_collection.setOnRefreshListener(this);
//        listenPopupWindow(swipe_layout_my_proxy);

    }


    public void lotFilter() {
        tempCollections.clear();
        if (!TextUtils.isEmpty(key)) {
            for (CollectionLot lot : myCollections) {
                if ((lot.name.indexOf(key) >= 0) || (String.valueOf(lot.id).indexOf(key) >= 0) || (lot.status.indexOf(key) >= 0)
                        || (lot.biddingTime.indexOf(key) >= 0)) {
                    tempCollections.add(lot);
                }
            }
            /**
             * 更新adapter
             */
            adapter.notifyDataSetChanged();
        } else {
            for (CollectionLot lot : myCollections) {
                tempCollections.add(lot);
            }
            /**
             * 更新adapter
             */
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 初始化数据
     */
    private void initData() {

        for (CollectionLot lot : myCollections) {
            tempCollections.add(lot);
        }


        /**
         * 设置数据适配器
         */
        adapter = new MyCollectionAdapter(mContext, tempCollections);
        lv_my_collection.setAdapter(adapter);

        /**
         * 点击进行关键字搜索
         */
        btn_my_collection_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                key = et_my_collection_keyword.getText().toString().trim();
                isSearch = true;
                lotFilter();
            }

        });


    }

    @Override
    public void onRefresh() {
        Log.i(TAG, "下拉刷新");
        currentCount = 0;
        number = 1;
        myCollections.clear();
        tempCollections.clear();
        swipe_layout_my_collection.setRefreshing(true);
        requestData();
    }

    @Override
    public void onLoadMore() {
        Log.i(TAG, "加载更多");
        number++;
        requestData();
    }

    private class MyCollectionHandler extends HttpJsonHandler {
        @Override
        public void handlerData(int code, JSONObject data) {
            switch (code) {
                case 1:
                    number--;
                    Toast.makeText(mContext, "网络异常，获取信息失败", Toast.LENGTH_SHORT).show();
                    break;
                case -1:
                    number--;
                    Toast.makeText(mContext, "用户名密码失效，请重新登录", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(mContext, LoginActivity.class));
                    break;
                case 0:
                    Log.i(TAG, "获取我的收藏信息成功");
                    Log.i(TAG, data.toString());
                    try {
                        size = Integer.parseInt(data.getString("size"));//每页的数目
                        totalCount = Integer.parseInt(data.getString("count"));//总的数目
                        currentCount += size;
                        JSONArray jArray = data.getJSONArray("auctionList");
                        CollectionLot lot;
                        for (int i = 0; i < jArray.length(); i++) {
                            lot = new CollectionLot();
                            lot.name = jArray.getJSONObject(i).getString("name");
                            lot.id = jArray.getJSONObject(i).getString("id");
                            lot.startPrice = Double.parseDouble(jArray.getJSONObject(i).getString("startPrice"));
                            lot.status = jArray.getJSONObject(i).getString("status");
                            lot.apprisal = jArray.getJSONObject(i).getString("appraisal");
                            if(!"".equals(jArray.getJSONObject(i).getString("dealPrice"))){
                                lot.dealPrice = Double.parseDouble(jArray.getJSONObject(i).getString("dealPrice"));
                            }
                            lot.biddingTime = jArray.getJSONObject(i).getString("biddingTime");
                            lot.forBidding = Integer.parseInt(jArray.getJSONObject(i).getString("forBidding"));
                            myCollections.add(lot);
                        }
                        Log.i(TAG, "我的收藏数据获取完成！");
                        if (number == 1) {//刷新
                            Toast.makeText(mContext, "刷新成功", Toast.LENGTH_SHORT).show();
                            swipe_layout_my_collection.setRefreshing(false);
                            initData();
                        } else {//加载更多
                            if(!isSearch) {
                                key = "";
                            }
                            lotFilter();
                            lv_my_collection.stopLoadMore();
                        }
                    } catch (JSONException e) {
                        Log.i(TAG, "json解析异常");
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }
}