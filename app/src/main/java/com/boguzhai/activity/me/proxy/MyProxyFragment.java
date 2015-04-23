package com.boguzhai.activity.me.proxy;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.app.Fragment;
import android.os.SystemClock;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.boguzhai.R;
import com.boguzhai.activity.auction.LotInfoActivity;
import com.boguzhai.activity.login.LoginActivity;
import com.boguzhai.logic.dao.ProxyLot;
import com.boguzhai.logic.thread.HttpPostHandler;
import com.boguzhai.logic.utils.DensityUtils;
import com.boguzhai.logic.utils.Utility;
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
public class MyProxyFragment extends Fragment implements XListView.IXListViewListener, SwipeRefreshLayout.OnRefreshListener {

    public static String TAG = "MyProxyFragment";

    private int status = 1;//1可修改的代理出价(拍卖未结束) 2历史代理(不可修改)
    private ProxyPricingActivity mContext;//fragment关联的activity
    private List<ProxyLot> lotList;//我的代理集合
    private MyProxyAdapter adapter;//适配器
    private XListView lv_my_proxy;//listview
    private View view;//fragment对应的视图
    private LayoutInflater inflater;
    private PopupWindow popupWindow;//跳出修改或者删除按钮
    private TextView tv_popupwindow_modify, tv_popupwindow_delete;//修改，删除
    private EditText et_my_proxy_keyword;//查询关键字
    private Button btn_my_proxy_search;//点击查询按钮
    private Spinner sp_my_proxy_choose_auction;//选择拍卖会
    private Spinner sp_my_proxy_choose_session;//选择专场
    private List<ProxyLot> newLots;//用于与adapter绑定的数据
    private List<ProxyLot> selectedAuctionLots;//通过选择的拍卖会过滤后的代理拍品
    private SwipeRefreshLayout swipe_layout_my_proxy;


    public Utility utility = new Utility();
    private String[] list_type1 = {"不限"};
    private String[] list_type2 = {"不限"};
    private StringBuffer type1 = new StringBuffer();
    private StringBuffer type2 = new StringBuffer();
    private String selectedAuctionName = "不限";
    private String selectedSessionName = "不限";

    public MyProxyFragment() {
    }


    public MyProxyFragment(int status) {
        this.status = status;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.me_my_proxy_fg, null);
        this.inflater = inflater;
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = (ProxyPricingActivity) getActivity();
        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        swipe_layout_my_proxy = (SwipeRefreshLayout) view.findViewById(R.id.swipe_layout_my_proxy);
        swipe_layout_my_proxy.setColorSchemeResources(R.color.gold);
        swipe_layout_my_proxy.setOnRefreshListener(this);
        swipe_layout_my_proxy.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (popupWindow != null) {
                    dismissPopWindow();
                }
                return false;
            }
        });
        et_my_proxy_keyword = (EditText) view.findViewById(R.id.et_my_proxy_keyword);
        btn_my_proxy_search = (Button) view.findViewById(R.id.btn_my_proxy_search);
        sp_my_proxy_choose_auction = (Spinner) view.findViewById(R.id.sp_my_proxy_choose_auction);
        sp_my_proxy_choose_session = (Spinner) view.findViewById(R.id.sp_my_proxy_choose_session);
        btn_my_proxy_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String key = et_my_proxy_keyword.getText().toString().trim();
                if(TextUtils.isEmpty(key)) {
                    return;
                }else {

                    newLots = selectedAuctionLots;

                    List<ProxyLot> tempLots = new ArrayList<>();
                    if ("不限".equals(selectedSessionName)) {

                    } else {
                        for (ProxyLot lot : newLots) {
                            if (lot.sessionId.equals(selectedSessionName)) {
                                tempLots.add(lot);
                            }
                        }
                        newLots = new ArrayList<>();
                        for (ProxyLot lot : tempLots) {
                            newLots.add(lot);
                        }
                    }

                    tempLots = new ArrayList<>();
                    for(ProxyLot lot : newLots) {
                        if((lot.name.indexOf(key) >= 0) || (lot.auctionId.indexOf(key) >= 0) || (lot.sessionId.indexOf(key) >= 0)) {
                            tempLots.add(lot);
                        }
                    }
                    newLots = new ArrayList<>();
                    for (ProxyLot lot : tempLots) {
                        newLots.add(lot);
                    }
                    adapter = new MyProxyAdapter(mContext, newLots);
                    lv_my_proxy.setAdapter(adapter);
                }
            }
        });

        lotList = testData();
        newLots = new ArrayList<>();
        for (ProxyLot lot : lotList) {
            newLots.add(lot);
        }
        adapter = new MyProxyAdapter(mContext, newLots);

        lv_my_proxy = (XListView) view.findViewById(R.id.lv_my_proxy);
        lv_my_proxy.setPullLoadEnable(true);
        lv_my_proxy.setPullRefreshEnable(false);
        lv_my_proxy.setXListViewListener(this);
        lv_my_proxy.setAdapter(adapter);

        et_my_proxy_keyword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (popupWindow != null) {
                        dismissPopWindow();
                    }
                }
                return false;
            }
        });
        btn_my_proxy_search.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (popupWindow != null) {
                        dismissPopWindow();
                    }
                }
                return false;
            }
        });
        sp_my_proxy_choose_auction.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (popupWindow != null) {
                        dismissPopWindow();
                    }
                }
                return false;
            }
        });
        sp_my_proxy_choose_session.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (popupWindow != null) {
                        dismissPopWindow();
                    }
                }
                return false;
            }
        });

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (popupWindow != null) {
                        dismissPopWindow();
                    }
                }
                return false;
            }
        });

        /*
         * 请求网络数据
         */
//        HttpRequestApi conn = new HttpRequestApi();
//        conn.addParam("sessionid", "");
//        conn.addParam("status", "");
//        conn.setUrl("url");
//        new Thread(new HttpPostRunnable(conn, new MyProxyHandler(mContext))).start();


        /**
         * 解析所有代理拍品，得到所有拍卖会名称和专场名称，提供spinner选择
         */
        final List<SelectedAuction> auctions = proxyLotsParser(lotList);
        list_type1 = new String[auctions.size() + 1];
        list_type1[0] = "不限";

        /**
         * 得到所有拍卖会名称
         */
        for (int i = 1; i < list_type1.length; i++) {
            list_type1[i] = auctions.get(i - 1).auctionName;
        }

        /**
         * 设置spinner，及监听事件
         */
        utility.setSpinner(mContext, view, R.id.sp_my_proxy_choose_auction, list_type1, type1, new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view0, int position,
                                               long id) {

                        if (popupWindow != null) {
                            dismissPopWindow();
                        }


                        final List<String> sessionNames;
                        /**
                         * 选中"不限"，遍历所有代理拍品所在拍卖会下的所有专场，
                         */
                        if (position == 0) {
                            selectedAuctionName = "不限";
                            Log.i(TAG, "选中的拍卖会为:" + selectedAuctionName);
                            sessionNames = new ArrayList<>();
                            for (SelectedAuction auction : auctions) {
                                for (String name : auction.sessionNames) {
                                    if (!sessionNames.contains(name)) {
                                        sessionNames.add(name);
                                    }
                                }
                            }
                            list_type2 = new String[sessionNames.size() + 1];
                            list_type2[0] = "不限";
                            for (int i = 1; i < list_type2.length; i++) {
                                list_type2[i] = sessionNames.get(i - 1);
                            }

                        } else if (position >= 1) {//选中某一拍卖会，遍历该专场下所有专场
                            selectedAuctionName = auctions.get(position - 1).auctionName;
                            Log.i(TAG, "选中的拍卖会为:" + selectedAuctionName);
                            sessionNames = auctions.get(position - 1).sessionNames;
                            list_type2 = new String[sessionNames.size() + 1];
                            Log.i(TAG, "该拍卖会中有:" + sessionNames.size() + "个专场");
                            list_type2[0] = "不限";
                            for (int i = 1; i < list_type2.length; i++) {
                                list_type2[i] = auctions.get(position - 1).sessionNames.get(i - 1);
                            }

                        } else {
                            sessionNames = new ArrayList<>();
                        }

                        newLots = new ArrayList<>();
                        if ("不限".equals(selectedAuctionName)) {
                            for (ProxyLot lot : lotList) {
                                newLots.add(lot);
                            }
//                            lv_my_proxy.setAdapter(adapter);

                        } else {
                            for (ProxyLot lot : lotList) {
                                if (lot.auctionId.equals(selectedAuctionName)) {
                                    newLots.add(lot);
                                }
                            }
//                            lv_my_proxy.setAdapter(adapter);
                        }
                        selectedAuctionLots = newLots;
                        adapter = new MyProxyAdapter(mContext, newLots);
                        lv_my_proxy.setAdapter(adapter);

                        utility.setSpinner(mContext, view, R.id.sp_my_proxy_choose_session, list_type2, type2, new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                if (popupWindow != null) {
                                    dismissPopWindow();
                                }
                                /**
                                 * 选中"不限"，遍历对应拍卖会下的所有专场，
                                 */
                                if (position == 0) {
                                    selectedSessionName = "不限";
                                    Log.i(TAG, "选中的专场为:" + selectedSessionName);
                                }
                                /**
                                 * 选中某一专场
                                 */
                                if (position >= 1) {
                                    selectedSessionName = sessionNames.get(position - 1);
                                    Log.i(TAG, "选中的专场为:" + selectedSessionName);
                                }

                                newLots = selectedAuctionLots;

                                List<ProxyLot> tempLots = new ArrayList<>();
                                if ("不限".equals(selectedSessionName)) {

                                } else {
                                    for (ProxyLot lot : newLots) {
                                        if (lot.sessionId.equals(selectedSessionName)) {
                                            tempLots.add(lot);
                                        }
                                    }
                                    newLots = new ArrayList<>();
                                    for (ProxyLot lot : tempLots) {
                                        newLots.add(lot);
                                    }
                                }

                                adapter = new MyProxyAdapter(mContext, newLots);
                                lv_my_proxy.setAdapter(adapter);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                }

        );

        /**
         * 如果是可以修改的代理，则设置可以长按弹出"修改"和"删除"
         */
        if (status == 1) {
            lv_my_proxy.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (popupWindow != null) {
                        dismissPopWindow();
                    } else {
                        Intent intent = new Intent(mContext, LotInfoActivity.class);
                        intent.putExtra("auctionId", newLots.get(position - 1).name);
                        Log.i(TAG, "代理拍品的id为:" + String.valueOf(newLots.get(position - 1).name));
                        startActivity(intent);
                    }

                }
            });
            lv_my_proxy.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    if (popupWindow != null) {
                        dismissPopWindow();
                    }
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                }
            });

            lv_my_proxy.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, final View view, final int position, long id) {
                    if (popupWindow != null) {
                        dismissPopWindow();
                    }
                    View item = View.inflate(mContext.getApplicationContext(),
                            R.layout.item_popupwindow, null);

                    tv_popupwindow_modify = (TextView) item
                            .findViewById(R.id.tv_popupwindow_modify);
                    tv_popupwindow_delete = (TextView) item
                            .findViewById(R.id.tv_popupwindow_delete);

                    tv_popupwindow_modify.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (popupWindow != null) {
                                dismissPopWindow();
                            }
                            Intent intent = new Intent(mContext, SetProxyPricingActivity.class);
                            intent.putExtra("auctionId", newLots.get(position - 1).name);
                            startActivity(intent);
                        }
                    });
                    tv_popupwindow_delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (popupWindow != null) {
                                dismissPopWindow();
                            }
                            //网络请求


                            // 位移动画
                            TranslateAnimation ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,
                                    Animation.RELATIVE_TO_SELF, -1.0f,
                                    Animation.RELATIVE_TO_SELF, 0,
                                    Animation.RELATIVE_TO_SELF, 0);
                            ta.setDuration(200);
                            view.startAnimation(ta);
                            new Thread() {
                                public void run() {
                                    try {
                                        Thread.sleep(200);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    getActivity().runOnUiThread(new Runnable() {
                                        public void run() {
                                            for (int i = 0; i < lotList.size(); i++) {

                                                if (newLots.get(position - 1).name.equals(lotList.get(i).name)) {
                                                    lotList.remove(i);
                                                    break;
                                                }
                                            }
                                            for (int i = 0; i < selectedAuctionLots.size(); i++) {

                                                if (newLots.get(position - 1).name.equals(selectedAuctionLots.get(i).name)) {
                                                    selectedAuctionLots.remove(i);
                                                    break;
                                                }
                                            }
                                            newLots.remove(position - 1);
                                            adapter.notifyDataSetChanged();
                                        }
                                    });
                                }
                            }.start();
                        }
                    });

                    popupWindow = new PopupWindow(item,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    // 动画播放的前提条件，窗体必须有背景资源
                    popupWindow.setBackgroundDrawable(new ColorDrawable(
                            Color.TRANSPARENT));// 设置透明背景
                    int[] pos = new int[2];
                    view.getLocationInWindow(pos);
                    int dp_x = 130;
                    int dp_y = 150;
                    int px_x = DensityUtils.dip2px(mContext.getApplicationContext(), dp_x);
                    int px_y = DensityUtils.dip2px(mContext.getApplicationContext(), dp_y);
                    popupWindow.showAtLocation(parent,
                            Gravity.TOP + Gravity.LEFT, dp_x, pos[1] + dp_y);

                    ScaleAnimation sa = new ScaleAnimation(0.5f, 1.0f, 0.5f,
                            1.0f, Animation.RELATIVE_TO_SELF, 0.1f,
                            Animation.RELATIVE_TO_SELF, 0);
                    sa.setDuration(200);
                    AlphaAnimation aa = new AlphaAnimation(0.5f, 1.0f);
                    aa.setDuration(200);
                    AnimationSet set = new AnimationSet(false);
                    set.addAnimation(sa);
                    set.addAnimation(aa);
                    item.setAnimation(set);
                    return true;
                }
            });


        }

    }

    /**
     * 释放popWindow
     */
    private void dismissPopWindow() {
        popupWindow.dismiss();
        popupWindow = null;
    }

    @Override
    public void onDestroy() {
        if (popupWindow != null) {
            dismissPopWindow();
        }
        super.onDestroy();
    }


    @Override
    public void onRefresh() {
        swipe_layout_my_proxy.setRefreshing(true);
        Log.i(TAG, "下拉刷新");
        new Thread() {
            @Override
            public void run() {
                SystemClock.sleep(2000);
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        swipe_layout_my_proxy.setRefreshing(false);
                        Toast.makeText(mContext, "刷新成功", Toast.LENGTH_SHORT).show();
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
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext, "加载更多", Toast.LENGTH_SHORT).show();
                        lv_my_proxy.stopLoadMore();
                    }
                });
            }
        }.start();

    }





    class MyProxyHandler extends HttpPostHandler {

        public MyProxyHandler(Context context) {
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
                        jArray = data.getJSONArray("proxyList");
                        ProxyLot lot;
                        for (int i = 0; i < jArray.length(); i++) {
                            lot = new ProxyLot();
                            lot.name = jArray.getJSONObject(i).getString("auctionName");
                            lot.id = jArray.getJSONObject(i).getInt("id");
                            lot.auctionId = jArray.getJSONObject(i).getString("auctionMain");
                            lot.sessionId = jArray.getJSONObject(i).getString("auctionSession");
                            //lot.appraisal = jArray.getJSONObject(i).getString("appraisal");
                            lot.proxyPrice = jArray.getJSONObject(i).getString("proxyPrice");
                            lotList.add(lot);
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
     * 解析代理出价中的所有拍品，提取拍品中的拍卖会名称和拍卖会对应的专场名称，拍卖会名称和该拍卖会下的所有专场的名称组成SelectedAuction类对象
     *
     * @param proxyLots 代理出价中所有拍品
     * @return
     */
    private List<SelectedAuction> proxyLotsParser(List<ProxyLot> proxyLots) {
        List<SelectedAuction> auctions = new ArrayList<>();
        SelectedAuction auction;
        for (ProxyLot lot : proxyLots) {
            int result;
            if ((result = isAuctionExist(auctions, lot.auctionId)) != -1) {
                auctions.get(result).addSession(lot.sessionId);
            } else {
                auction = new SelectedAuction();
                auction.auctionName = lot.auctionId;
                auction.sessionNames = new ArrayList<>();
                auction.sessionNames.add(lot.sessionId);
                auctions.add(auction);
            }

        }
        return auctions;
    }

    /**
     * 查询某个拍卖会名称是否在拍卖会集合中存在
     *
     * @param auctions    被查询的拍卖会集合
     * @param auctionName 要查询的拍卖会名称
     * @return -1 不存在 <br> 非-1 存在，位置
     */
    private int isAuctionExist(List<SelectedAuction> auctions, String auctionName) {
        int result = -1;
        if (auctions.size() <= 0) {
            return result;
        }
        for (int i = 0; i < auctions.size(); i++) {
            if (auctionName.equals(auctions.get(i).auctionName)) {
                result = i;
                break;
            }
        }
        return result;
    }


    private List<ProxyLot> testData() {
        ArrayList<ProxyLot> lotList = new ArrayList<>();
        ProxyLot lot = new ProxyLot();
        lot.No = 1233;
        lot.name = "明代唐伯虎书法作品";
        lot.apprisal1 = 5000;
        lot.apprisal2 = 8000;
        lot.startPrice = 3000;
        lot.proxyPrice = "4000";
        lot.auctionId = "2015新春大拍";
        lot.sessionId = "字画专场";
        lotList.add(lot);

        lot = new ProxyLot();
        lot.No = 1234;
        lot.name = "张曦之书法作品";
        lot.apprisal1 = 5000;
        lot.apprisal2 = 8000;
        lot.startPrice = 3000;
        lot.proxyPrice = "4000";
        lot.auctionId = "2015新春大拍";
        lot.sessionId = "字画专场";
        lotList.add(lot);


        lot = new ProxyLot();
        lot.No = 1235;
        lot.name = "景德镇陶瓷";
        lot.apprisal1 = 5000;
        lot.apprisal2 = 8000;
        lot.startPrice = 3000;
        lot.proxyPrice = "4000";
        lot.auctionId = "2015新春大拍";
        lot.sessionId = "瓷器专场";
        lotList.add(lot);

        lot = new ProxyLot();
        lot.No = 1236;
        lot.name = "光绪丁未年双龙寿字币";
        lot.apprisal1 = 5000;
        lot.apprisal2 = 8000;
        lot.startPrice = 3000;
        lot.proxyPrice = "4000";
        lot.auctionId = "2015新春大拍";
        lot.sessionId = "瓷器专场";
        lotList.add(lot);

        lot = new ProxyLot();
        lot.No = 1237;
        lot.name = "吴月亭款紫砂壶";
        lot.apprisal1 = 5000;
        lot.apprisal2 = 8000;
        lot.startPrice = 3000;
        lot.proxyPrice = "4000";
        lot.auctionId = "2015新春大拍";
        lot.sessionId = "瓷器专场";
        lotList.add(lot);

        lot = new ProxyLot();
        lot.No = 1238;
        lot.name = "冰种描金翡翠手镯";
        lot.apprisal1 = 5000;
        lot.apprisal2 = 8000;
        lot.startPrice = 3000;
        lot.proxyPrice = "4000";
        lot.auctionId = "2014年终大拍";
        lot.sessionId = "玉器专场";
        lotList.add(lot);

        lot = new ProxyLot();
        lot.No = 1239;
        lot.name = "乾隆五彩盘";
        lot.apprisal1 = 5000;
        lot.apprisal2 = 8000;
        lot.startPrice = 3000;
        lot.proxyPrice = "4000";
        lot.auctionId = "2014年终大拍";
        lot.sessionId = "瓷器专场";
        lotList.add(lot);


        lot = new ProxyLot();
        lot.No = 1230;
        lot.name = "青花葫芦瓶";
        lot.apprisal1 = 5000;
        lot.apprisal2 = 8000;
        lot.startPrice = 3000;
        lot.proxyPrice = "4000";
        lot.auctionId = "2014年终大拍";
        lot.sessionId = "瓷器专场";
        lotList.add(lot);


        lot = new ProxyLot();
        lot.No = 1231;
        lot.name = "叶圣陶书法";
        lot.apprisal1 = 5000;
        lot.apprisal2 = 8000;
        lot.startPrice = 3000;
        lot.proxyPrice = "4000";
        lot.auctionId = "2014年终大拍";
        lot.sessionId = "书画专场";
        lotList.add(lot);


        lot = new ProxyLot();
        lot.No = 1232;
        lot.name = "宋占魁虎字书法";
        lot.apprisal1 = 5000;
        lot.apprisal2 = 8000;
        lot.startPrice = 3000;
        lot.proxyPrice = "4000";
        lot.auctionId = "2014年终大拍";
        lot.sessionId = "书画专场";
        lotList.add(lot);

        return lotList;


    }

}
