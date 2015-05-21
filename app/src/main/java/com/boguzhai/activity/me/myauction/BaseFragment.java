package com.boguzhai.activity.me.myauction;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.boguzhai.activity.base.Constant;
import com.boguzhai.activity.base.Variable;
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

    private static final String TAG = "MyAuction_BaseFragment";


    private int totalCount = 0;//结果总数
    private int currentCount = 0;//当前总数
    private int number = 1;//分页序号，从1开始
    private int size = 0;//每次分页数目

    public MyAuctionActivity mContext;//与fragment对应的activity上下文


    private String status = ""; //拍卖会状态 "" "预展中" "拍卖中" "已结束"
    private List<MyAuction> myAuctions;//需要展示的我的拍卖会集合
    private List<MyAuction> tempAuctions;//临时的拍卖会集合，用于搜索过滤后的
    private boolean isSearch = false;//是否处于关键字查询下的显示
    private String spinnerText = "全部";//spinner显示的文字
    private String searchText = "";//搜索的关键字
    public View view;//fragment对应布局
    public LayoutInflater inflater;
    public MyAuctionAdapter myAuctionAdapter;//拍卖会集合的适配器
    public XListView lv_my_auction;//布局中的listview 支持上拉加载更多地listview
    private SwipeRefreshLayout swipe_layout;//支持下拉刷新的布局
    private Button btn_my_auction_search;//点击进行搜索
    private EditText et_my_auction_keyword;//关键字
    public String[] types = {"全部", "现场拍卖", "同步拍卖", "网络拍卖"};
    private HttpClient conn;

    /**
     * Constructor
     *
     * @param status
     */
    public BaseFragment(String status) {
        this.status = status;
    }


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
        init();
    }



    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.i(TAG, "接收到<数据获取完成>的消息！");
            swipe_layout.setRefreshing(false);
            lv_my_auction.stopLoadMore();
            Toast.makeText(mContext, "获取数据成功", Toast.LENGTH_SHORT).show();
            if (isSearch) {
                auctionFilterByKey(searchText);
            } else {
                auctionFilterByKey("");
            }
        }
    };



    /**
     * 初始化数据
     */
    public void init() {

        myAuctions = new ArrayList<>();
        /**
         * 从网络获取数据,从第一页开始
         */
        conn = new HttpClient();
        conn.setHeader("cookie", "JSESSIONID=" + Variable.account.sessionid);
        conn.setUrl(Constant.url + "pAuctionUserAction!getMyAuctionMainList.htm");
        conn.setParam("status", status);//拍卖会状态 "" "预展中" "拍卖中" "已结束"
        conn.setParam("number", String.valueOf(number));//分页序号，从1开始
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

        myAuctionAdapter = new MyAuctionAdapter(mContext, myAuctions);
        lv_my_auction.setAdapter(myAuctionAdapter);

        /**
         * 设置spinner， 每选择一项，就会在原始数据中筛选对应的拍卖会集合并展示，
         */
        Utility.setSpinner(mContext, (Spinner) view.findViewById(R.id.sp_my_auction_choose), types, new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, "Spinner Position:" + position);
                spinnerText = types[position];
                auctionFilterByKey("");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        /**
         * 点击搜索进行关键字匹配，在原始数据中筛选符合条件的拍卖会集合并展示
         */
        et_my_auction_keyword = (EditText) view.findViewById(R.id.et_my_auction_keyword);
        et_my_auction_keyword.addTextChangedListener(new TextWatcher() {
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
        btn_my_auction_search = (Button) view.findViewById(R.id.btn_my_auction_search);
        btn_my_auction_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSearch = true;
                searchText = et_my_auction_keyword.getText().toString().trim();
                auctionFilterByKey(searchText);
            }
        });
    }

    /**
     * 通过关键字进行过滤并显示
     */
    private void auctionFilterByKey(String key) {

        if (TextUtils.isEmpty(key)) {//搜索的关键字为""
            isSearch = false;
            Log.i(TAG, spinnerText);
            if ("全部".equals(spinnerText)) {//选择"全部"
                myAuctionAdapter = new MyAuctionAdapter(mContext, myAuctions);
            } else {
                tempAuctions = new ArrayList<>();
                for (int i = 0; i < myAuctions.size(); i++) {
                    if (myAuctions.get(i).type.indexOf(spinnerText) >= 0) {
                        tempAuctions.add(myAuctions.get(i));
                    }
                }
                myAuctionAdapter = new MyAuctionAdapter(mContext, tempAuctions);
            }
            lv_my_auction.setAdapter(myAuctionAdapter);
        } else {//搜索的关键字不为空
            isSearch = true;
            tempAuctions = new ArrayList<>();
            if ("全部".equals(spinnerText)) {
                for (int i = 0; i < myAuctions.size(); i++) {
                    if (myAuctions.get(i).name.indexOf(key) >= 0 || myAuctions.get(i).type.indexOf(key) >= 0 || myAuctions.get(i).status.indexOf(key) >= 0
                            || myAuctions.get(i).auctionTime.indexOf(key) >= 0 || (String.valueOf(myAuctions.get(i).deposit)).indexOf(key) >= 0) {
                        tempAuctions.add(myAuctions.get(i));
                    }
                }
            } else {
                for (int i = 0; i < myAuctions.size(); i++) {
                    if (myAuctions.get(i).name.indexOf(key) >= 0 || myAuctions.get(i).type.indexOf(spinnerText) >= 0 || myAuctions.get(i).status.indexOf(key) >= 0
                            || myAuctions.get(i).auctionTime.indexOf(key) >= 0 || (String.valueOf(myAuctions.get(i).deposit)).indexOf(key) >= 0) {
                        tempAuctions.add(myAuctions.get(i));
                    }
                }
            }
            myAuctionAdapter = new MyAuctionAdapter(mContext, tempAuctions);
            lv_my_auction.setAdapter(myAuctionAdapter);
        }
    }


    /**
     * 下拉刷新，执行网络数据请求，清空原来的拍卖会集合，并更新。展示之前，判断spinner所选内容，进行筛选
     */
    @Override
    public void onRefresh() {
        myAuctions.clear();//清空之前所有数据
        isSearch = false;
        et_my_auction_keyword.setText("");//清空搜索关键字
        swipe_layout.setRefreshing(true);//设置正在刷新
        number = 1;//从第一页开始
        Log.i(TAG, "下拉刷新");

        conn = new HttpClient();
        conn.setHeader("cookie", "JSESSIONID=" + Variable.account.sessionid);
        conn.setUrl(Constant.url + "pAuctionUserAction!getMyAuctionMainList.htm");
        conn.setParam("status", status);//拍卖会状态 "" "预展中" "拍卖中" "已结束"
        conn.setParam("number", String.valueOf(number));//分页序号，从1开始
        new Thread(new HttpPostRunnable(conn, new MyAuctionHandler())).start();
    }


    /**
     * 加载更多
     */
    @Override
    public void onLoadMore() {
        Log.i(TAG, "加载更多");
        if (totalCount == currentCount) {
            Toast.makeText(mContext, "没有更多数据了", Toast.LENGTH_SHORT).show();
        } else {
            number++;//页数加1
            conn = new HttpClient();
            conn.setHeader("cookie", "JSESSIONID=" + Variable.account.sessionid);
            conn.setUrl(Constant.url + "pAuctionUserAction!getMyAuctionMainList.htm");
            conn.setParam("status", status);//拍卖会状态 "" "预展中" "拍卖中" "已结束"
            conn.setParam("number", String.valueOf(number));
            new Thread(new HttpPostRunnable(conn, new MyAuctionHandler())).start();
        }
    }


    /**
     * handler，处理从网络返回的数据
     */
    private class MyAuctionHandler extends HttpJsonHandler {

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
                    Log.i(TAG, "获取信息成功");
                    Log.i(TAG, data.toString());
                    try {
                        size = Integer.parseInt(data.getString("size"));//每页的数目
                        totalCount = Integer.parseInt(data.getString("count"));//总的数目
                        currentCount += size;
                        JSONArray jArray = data.getJSONArray("auctionList");
                        MyAuction myAction;
                        for (int i = 0; i < jArray.length(); i++) {
                            myAction = new MyAuction();
                            myAction.name = jArray.getJSONObject(i).getString("name");
                            myAction.id = jArray.getJSONObject(i).getString("id");
                            myAction.type = jArray.getJSONObject(i).getString("type");
                            myAction.status = jArray.getJSONObject(i).getString("status");
                            myAction.auctionTime = jArray.getJSONObject(i).getString("auctionTime");
                            myAction.deposit = jArray.getJSONObject(i).getInt("deposit");
                            myAction.location = jArray.getJSONObject(i).getString("location");
                            myAuctions.add(myAction);
                        }
                        Log.i(TAG, "数据获取完成！");
                        handler.sendEmptyMessage(0);
                    } catch (JSONException e) {
                        Log.i(TAG, "json解析异常");
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }



}
