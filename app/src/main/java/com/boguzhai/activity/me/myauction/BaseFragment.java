package com.boguzhai.activity.me.myauction;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.boguzhai.R;
import com.boguzhai.activity.auction.LotInfoActivity;
import com.boguzhai.activity.login.LoginActivity;
import com.boguzhai.activity.me.proxy.ProxyPricingActivity;
import com.boguzhai.activity.me.proxy.SetProxyPricingActivity;
import com.boguzhai.logic.gaobo.MyAuction;
import com.boguzhai.logic.thread.HttpPostHandler;
import com.boguzhai.logic.thread.HttpPostRunnable;
import com.boguzhai.logic.utils.HttpRequestApi;
import com.boguzhai.logic.view.XListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class BaseFragment extends Fragment implements XListView.IXListViewListener {

    private static final String TAG = "BaseFragment";
    private String status = ""; //拍卖会状态 "" "预展中" "拍卖中" "已结束"
    public BaseFragment(String status) {
        this.status = status;
    }

    private List<MyAuction> myAuctions;
    public MyAuctionActivity context;
    public View view;//fragment对应布局
    public LayoutInflater inflater;
    public MyAuctionAdapter myAuctionAdapter;//适配器
    public XListView lv_my_auction;//布局中的listview
    public String[] types = {"现场拍卖", "同步拍卖", "网络拍卖"};

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


    /**
     * 初始化数据
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = (MyAuctionActivity) getActivity();
        initData(status);
    }

    /**
     * 初始化适配器，子类直接传递需要展示的拍卖会信息集合
     *
     * @param myAuctions 需要展示的拍卖会信息集合
     * @return 适配器
     */
    public void initAdapter(List<MyAuction> myAuctions) {
        myAuctionAdapter = new MyAuctionAdapter(myAuctions);
    }



    public void initData(String type) {

        lv_my_auction = (XListView) view.findViewById(R.id.lv_myauctions);
        lv_my_auction.setPullLoadEnable(true);
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
        initAdapter(myAuctions);
        lv_my_auction.setAdapter(myAuctionAdapter);
        lv_my_auction.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, "跳转至第" + position + "个拍品");
                Intent intent = new Intent(context, LotInfoActivity.class);
                intent.putExtra("auctionId", myAuctions.get(position - 1).id);
                startActivity(intent);
            }
        });

        /*
         * 请求网络数据
         */
//        HttpRequestApi conn = new HttpRequestApi();
//        conn.addParam("sessionid", "");
//        conn.addParam("status", "");
//        conn.setUrl("url");
//        new Thread(new HttpPostRunnable(conn, new MyAuctionHandler(context))).start();
    }


    private class MyAuctionHandler extends HttpPostHandler {

        public MyAuctionHandler(Context context) {
            super(context);
        }

        @Override
        public void handlerData(int code, JSONObject data) {
            switch (code) {
                case 1:
                    Toast.makeText(context, "网络异常，获取信息失败", Toast.LENGTH_SHORT).show();
                    break;
                case -1:
                    Toast.makeText(context, "用户名密码失效，请重新登录", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(context, LoginActivity.class));
                    break;
                case 0:
                    Log.i(TAG, "获取信息成功");
                    JSONArray jArray;
                    try {
                        jArray = data.getJSONArray("auctionList");
                        MyAuction myAction;
                        for(int i = 0; i < jArray.length(); i++) {
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


    @Override
    public void onRefresh() {
        Log.i(TAG, "下拉刷新");

        new Thread() {
            @Override
            public void run() {
                SystemClock.sleep(2000);


                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onLoad();
                    }
                });
            }
        }.start();
    }


    @Override
    public void onLoadMore() {
        Log.i(TAG, "加载更多");
        new Thread() {
            @Override
            public void run() {
                SystemClock.sleep(2000);
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onLoad();
                    }
                });
//
            }
        }.start();

    }

    private String getLastTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(new Date());
    }

    private void onLoad() {
        lv_my_auction.stopRefresh();
        lv_my_auction.stopLoadMore();
        lv_my_auction.setRefreshTime(getLastTime());
    }


    public static class ViewHolder {
        public static TextView tv_my_auction_status;
        public static TextView tv_my_auction_name;
        public static TextView tv_my_auction_type;
        public static TextView tv_my_auction_date;
        public static TextView tv_my_auction_deposit;
        public static TextView tv_my_auction_set_deposit;
    }

    /**
     * 适配器
     */
    public class MyAuctionAdapter extends BaseAdapter {

        private List<MyAuction> myAuctions;//要展示的拍卖会信息

        private MyAuctionAdapter(List<MyAuction> myAuctions) {
            this.myAuctions = myAuctions;
        }

        @Override
        public int getCount() {
            return myAuctions.size();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            View view;
            if (convertView == null) {
                holder = new ViewHolder();
                view = inflater.inflate(R.layout.me_my_auction_item, null);
                holder.tv_my_auction_status = (TextView) view.findViewById(R.id.tv_my_auction_status);
                holder.tv_my_auction_name = (TextView) view.findViewById(R.id.tv_my_auction_name);
                holder.tv_my_auction_type = (TextView) view.findViewById(R.id.tv_my_auction_type);
                holder.tv_my_auction_date = (TextView) view.findViewById(R.id.tv_my_auction_date);
                holder.tv_my_auction_deposit = (TextView) view.findViewById(R.id.tv_my_auction_deposit);
                holder.tv_my_auction_set_deposit = (TextView) view.findViewById(R.id.tv_my_auction_set_deposit);
                view.setTag(holder);
            } else {
                view = convertView;
                holder = (ViewHolder) view.getTag();

            }
            holder.tv_my_auction_status.setText("[" + myAuctions.get(position).status + "] ");
            holder.tv_my_auction_name.setText(myAuctions.get(position).name);
            holder.tv_my_auction_type.setText(types[Integer.parseInt(myAuctions.get(position).type) - 1]);
            holder.tv_my_auction_date.setText(myAuctions.get(position).auctionTime);
            holder.tv_my_auction_deposit.setText(String.valueOf(myAuctions.get(position).deposit));
            holder.tv_my_auction_set_deposit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG, "设置/修改代理出价");
                    Intent intent = new Intent(context, SetProxyPricingActivity.class);
                    intent.putExtra("auctionId", myAuctions.get(position).name);
                    startActivity(intent);
                }
            });
            return view;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
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
        myAuction.type = "3";
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
        myAuction.type = "1";
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
        myAuction.type = "2";
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
        myAuction.type = "1";
        myAuction.deposit = 500;
        myAuctions1.add(myAuction);

        myAuction = new MyAuction();
        myAuction.auctionTime = "2014.7.15 18:00 - 2014.7.15 21:00";
        myAuction.name = "2014冬季艺术品大拍";
        myAuction.status = "拍卖中";
        myAuction.type = "2";
        myAuction.deposit = 500;
        myAuctions1.add(myAuction);

        myAuction = new MyAuction();
        myAuction.auctionTime = "2014.7.15 18:00 - 2014.7.15 21:00";
        myAuction.name = "2014冬季艺术品大拍";
        myAuction.status = "已结束";
        myAuction.type = "3";
        myAuction.deposit = 500;
        myAuctions1.add(myAuction);

        myAuction = new MyAuction();
        myAuction.auctionTime = "2014.7.15 18:00 - 2014.7.15 21:00";
        myAuction.name = "2014冬季艺术品大拍";
        myAuction.status = "预展中";
        myAuction.type = "1";
        myAuction.deposit = 500;
        myAuctions1.add(myAuction);

        return myAuctions1;
    }

}
