package com.boguzhai.activity.me.proxy;


import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
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
import com.boguzhai.activity.base.Variable;
import com.boguzhai.activity.login.LoginActivity;
import com.boguzhai.logic.dao.ProxyLot;
import com.boguzhai.logic.gaobo.MyAuction;
import com.boguzhai.logic.thread.HttpJsonHandler;
import com.boguzhai.logic.thread.HttpPostRunnable;
import com.boguzhai.logic.utils.DensityUtils;
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
public class MyProxyFragment extends Fragment implements XListView.IXListViewListener, SwipeRefreshLayout.OnRefreshListener {

    public static String TAG = "MyProxyFragment";


    private final int baseCount = 5;
    private List<ProxyLot> myProxyLots;//所有代理出价拍品集合，只有在刷新的情况下才会更新此集合。
    private int pageIndex = 0;//分页显示的页数，从 "0" 开始
    private boolean isSearch = false;//是否处于搜索下的显示
    private List<ProxyLot> searchProxyLots;//搜索时显示的集合
    private int searchPageIndex = 0;//搜索状态下的页数
    private String key;//搜索关键字
    private List<ProxyLot> selectedAuctionLots;//通过选择的拍卖会过滤后的代理拍品
    private List<ProxyLot> tempLots;
    private int status = 1;//1可修改的代理出价(拍卖未结束) 2历史代理(不可修改)

    private ProxyPricingActivity mContext;//fragment关联的activity
    //    private List<ProxyLot> lotList;//我的代理集合
    private MyProxyAdapter adapter;//适配器


    private SwipeRefreshLayout swipe_layout_my_proxy;//支持下拉刷新的布局
    private XListView lv_my_proxy;//支持加载更多的listview
    private View view;//fragment对应的视图
    private LayoutInflater inflater;
    private PopupWindow popupWindow;//跳出修改或者删除按钮
    private TextView tv_popupwindow_modify, tv_popupwindow_delete;//修改，删除
    private EditText et_my_proxy_keyword;//查询关键字
    private Button btn_my_proxy_search;//点击查询按钮
    private Spinner sp_my_proxy_choose_auction;//选择拍卖会
    private Spinner sp_my_proxy_choose_session;//选择专场
    //    private List<ProxyLot> newLots;//用于与adapter绑定的数据


    public Utility utility = new Utility();
    private String[] list_type1 = {"不限"};
    private String[] list_type2 = {"不限"};
    private StringBuffer type1 = new StringBuffer();
    private StringBuffer type2 = new StringBuffer();
    private String selectedAuctionName = "不限";
    private String selectedSessionName = "不限";


    private HttpClient conn;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.i(TAG, "接收到<数据获取完成>消息");
            initData();
        }
    };

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
        listenPopupWindow(view);
        initView();
        initData();
    }


    /**
     * 触摸监听事件，用来关闭已经显示的popupwindow
     *
     * @param v
     */
    private void listenPopupWindow(View v) {
        v.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (popupWindow != null) {
                    dismissPopWindow();
                }
                return false;
            }
        });
    }


    private void initView() {
        /**
         * 支持上拉加载更多地listview，设置上拉监听事件，重写onLoadMore()方法
         */
        lv_my_proxy = (XListView) view.findViewById(R.id.lv_my_proxy);
        lv_my_proxy.setPullLoadEnable(true);
        lv_my_proxy.setPullRefreshEnable(false);
        lv_my_proxy.setXListViewListener(this);


        /**
         * 搜索关键字
         */
        et_my_proxy_keyword = (EditText) view.findViewById(R.id.et_my_proxy_keyword);
        listenPopupWindow(et_my_proxy_keyword);

        btn_my_proxy_search = (Button) view.findViewById(R.id.btn_my_proxy_search);
        listenPopupWindow(btn_my_proxy_search);

        /**
         * 支持下拉刷新的布局，设置下拉监听事件，重写onRefresh()方法
         */
        swipe_layout_my_proxy = (SwipeRefreshLayout) view.findViewById(R.id.swipe_layout_my_proxy);
        swipe_layout_my_proxy.setColorSchemeResources(R.color.gold);
        swipe_layout_my_proxy.setOnRefreshListener(this);
        listenPopupWindow(swipe_layout_my_proxy);


        sp_my_proxy_choose_session = (Spinner) view.findViewById(R.id.sp_my_proxy_choose_session);
        sp_my_proxy_choose_auction = (Spinner) view.findViewById(R.id.sp_my_proxy_choose_auction);


        /**
         * 网络请求，获取代理拍品列表
         */

//        conn = new HttpClient();
//        conn.setParam("sessionid", "");
//        conn.setParam("status", String.valueOf(status));
//        conn.setUrl("http://60.191.203.80/phones/pAuctionUserAction!getAuctionProxyList.htm");
//        new Thread(new HttpPostRunnable(conn, new MyProxyHandler())).start();
    }

    /**
     * 初始化数据
     */
    private void initData() {

        isSearch = false;










        myProxyLots = testData();

//        newLots = new ArrayList<>();
//        for (ProxyLot lot : lotList) {
//            newLots.add(lot);
//        }


        /**
         * 解析所有代理拍品，得到所有拍卖会名称和专场名称，提供spinner选择
         */
        final List<SelectedAuction> auctions = proxyLotsParser(myProxyLots);
        list_type1 = new String[auctions.size() + 1];
        list_type1[0] = "不限";//设置第一个条目为“不限”

        /**
         * 得到所有拍卖会名称(已经去重复)
         */
        for (int i = 1; i < list_type1.length; i++) {
            list_type1[i] = auctions.get(i - 1).auctionName;
        }


        /**
         * listview点击事件，跳转到相应的拍品信息
         */
        lv_my_proxy.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (popupWindow != null) {
                    dismissPopWindow();
                } else {
                    Intent intent = new Intent(mContext, LotInfoActivity.class);
                    intent.putExtra("auctionId", myProxyLots.get(position - 1).name);
                    Log.i(TAG, "代理拍品的id为:" + String.valueOf(myProxyLots.get(position - 1).name));
                    startActivity(intent);
                }
            }
        });


        /**
         * 如果是可以修改的代理，则设置可以长按弹出"修改"和"删除"
         */
        if (status == 1) {

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
                            intent.putExtra("auctionId", myProxyLots.get(position - 1).name);
                            startActivity(intent);
                        }
                    });
                    tv_popupwindow_delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (popupWindow != null) {
                                dismissPopWindow();
                            }
                            //弹出对话框，询问是否确认删除
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
                                            if (isSearch) {//先在源数据中删除
                                                for (int i = 0; i < myProxyLots.size(); i++) {
                                                    if (adapter.getLots().get(position - 1).name.equals(myProxyLots.get(i).name)) {
                                                        myProxyLots.remove(i);
                                                        break;
                                                    }
                                                }
                                                Log.i(TAG, "----------------------");
                                                Log.i(TAG, adapter.getLots().toString());
                                                Log.i(TAG, selectedAuctionLots.toString());

                                                if(adapter.getLots() != selectedAuctionLots) {
                                                    Log.i(TAG, "当前显示的数据不是“selectedAuctions”");
                                                    for (int i = 0; i < adapter.getLots().size(); i++) {
                                                        if (adapter.getLots().get(position - 1).name.equals(selectedAuctionLots.get(i).name)) {
                                                            selectedAuctionLots.remove(i);
                                                            break;
                                                        }
                                                    }
                                                }

                                            }

                                            Log.i(TAG, "----------------------");
                                            Log.i(TAG, adapter.getLots().toString());
                                            Log.i(TAG, selectedAuctionLots.toString());
                                            //然后在当前显示的数据中删除
                                            adapter.removeElem(position - 1);
                                            Log.i(TAG, "----------------------");
                                            Log.i(TAG, adapter.getLots().toString());
                                            Log.i(TAG, selectedAuctionLots.toString());
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



        /**
         * 设置spinner，及监听事件
         */
        utility.setSpinner(mContext, view, R.id.sp_my_proxy_choose_auction, list_type1, type1, new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view0, int position,
                                               long id) {
//
//                        if (popupWindow != null) {
//                            dismissPopWindow();
//                        }
                        isSearch = true;
                        final List<String> sessionNames;

                        selectedAuctionLots = new ArrayList<>();


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
                            //得到所有拍卖会下所有专场的名字，与spinner绑定
                            for (int i = 1; i < list_type2.length; i++) {
                                list_type2[i] = sessionNames.get(i - 1);
                            }

                            for (ProxyLot lot : myProxyLots) {
                                selectedAuctionLots.add(lot);
                            }

                            /**
                             * 选中某一拍卖会，遍历该专场下所有专场
                             */
                        } else if (position >= 1) {
                            selectedAuctionName = auctions.get(position - 1).auctionName;
                            Log.i(TAG, "选中的拍卖会为:" + selectedAuctionName);
                            sessionNames = auctions.get(position - 1).sessionNames;
                            list_type2 = new String[sessionNames.size() + 1];
                            Log.i(TAG, "该拍卖会中有:" + sessionNames.size() + "个专场");
                            list_type2[0] = "不限";
                            for (int i = 1; i < list_type2.length; i++) {
                                list_type2[i] = auctions.get(position - 1).sessionNames.get(i - 1);
                            }
                            for (ProxyLot lot : myProxyLots) {
                                if (lot.auctionId.equals(selectedAuctionName)) {
                                    selectedAuctionLots.add(lot);
                                }
                            }
                        } else {//防止后面用到sesionNames报might not have been initialized错误
                            sessionNames = null;//can't reach
                        }


                        /**
                         * 更新adapter，绑定新的数据，并且设置页面索引值为0
                         */
                        adapter = new MyProxyAdapter(mContext, selectedAuctionLots);
                        adapter.setPageIndex(0);
                        lv_my_proxy.setAdapter(adapter);


                        /**
                         * 设置专场spinner，及监听事件
                         */
                        utility.setSpinner(mContext, view, R.id.sp_my_proxy_choose_session, list_type2, type2, new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                isSearch = true;
                                tempLots = new ArrayList<>();
                                /**
                                 * 选中"不限"，遍历对应拍卖会下的所有专场，
                                 */
                                if (position == 0) {
                                    selectedSessionName = "不限";
                                    Log.i(TAG, "选中的专场为:" + selectedSessionName);
                                    for (ProxyLot lot : selectedAuctionLots) {
                                        tempLots.add(lot);
                                    }

                                } else if (position > 0) { //选中某一专场
                                    selectedSessionName = sessionNames.get(position - 1);
                                    Log.i(TAG, "选中的专场为:" + selectedSessionName);

                                    //从拍卖会中过滤出符合该专场的拍品
                                    for (ProxyLot lot : selectedAuctionLots) {
                                        if (lot.sessionId.equals(selectedSessionName)) {
                                            tempLots.add(lot);
                                        }
                                    }
                                }
                                /**
                                 * 更新adapter，绑定新的数据，并且设置页面索引值为0
                                 */
                                Log.i(TAG, "spinner结果，更新集合,线程名：" + Thread.currentThread().getName());
                                adapter = new MyProxyAdapter(mContext, tempLots);
                                adapter.setPageIndex(0);
                                lv_my_proxy.setAdapter(adapter);

                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                                Log.i(TAG, "Spinner2: Nothing selected!");
                            }
                        });
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        Log.i(TAG, "Spinner1: Nothing selected!");
                    }

                }


        );


        /**
         * 设置数据适配器
         */
        adapter = new MyProxyAdapter(mContext, myProxyLots);
        adapter.setPageIndex(0);
        lv_my_proxy.setAdapter(adapter);







        /**
         * 点击进行关键字搜索
         */
        btn_my_proxy_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp_my_proxy_choose_auction.setSelection(0);
                sp_my_proxy_choose_session.setSelection(0);


                final String key = et_my_proxy_keyword.getText().toString().trim();
                if (!TextUtils.isEmpty(key)) {
                    tempLots = new ArrayList<>();

                    for (ProxyLot lot : myProxyLots) {
                        if ((lot.name.indexOf(key) >= 0) || (lot.auctionId.indexOf(key) >= 0) || (lot.sessionId.indexOf(key) >= 0)) {
                            tempLots.add(lot);
                        }
                    }
                    /**
                     * 更新adapter，绑定新的数据，并且设置页面索引值为0
                     */
                    Log.i(TAG, "搜索结果，更新集合,线程名：" + Thread.currentThread().getName());
                    adapter = new MyProxyAdapter(mContext, tempLots);
                    adapter.setPageIndex(0);
                    lv_my_proxy.setAdapter(adapter);

                } else {
                    /**
                     * 更新adapter，绑定新的数据，并且设置页面索引值为0
                     */
                    adapter = new MyProxyAdapter(mContext, myProxyLots);
                    adapter.setPageIndex(0);
                    lv_my_proxy.setAdapter(adapter);
                }
            }

        });

        Log.i(TAG, "getAucctionName()" + mContext.getAucctionName());
        for(int i = 0; i < list_type1.length; i++) {
            if(list_type1[i].equals(mContext.getAucctionName())) {
                sp_my_proxy_choose_auction.setSelection(i);
            }

        }

        /*
         * 请求网络数据
         */
//        HttpRequestApi conn = new HttpRequestApi();
//        conn.addParam("sessionid", "");
//        conn.addParam("status", "");
//        conn.setUrl("url");
//        new Thread(new HttpPostRunnable(conn, new MyProxyHandler(mContext))).start();
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

//        new Thread(new HttpPostRunnable(conn, new MyProxyHandler())).start();
        new Thread() {
            @Override
            public void run() {
                SystemClock.sleep(1000);
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initData();
                        Toast.makeText(mContext, "刷新成功", Toast.LENGTH_SHORT).show();
                        swipe_layout_my_proxy.setRefreshing(false);
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
                SystemClock.sleep(1000);
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (adapter.isLastPage()) {
                            Toast.makeText(mContext, "没有更多数据了", Toast.LENGTH_SHORT).show();
                        } else {
                            adapter.refreshCurrentPageIndex();
                        }
                        lv_my_proxy.stopLoadMore();
                    }
                });
            }
        }.start();

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




    class MyProxyHandler extends HttpJsonHandler {

        @Override
        public void handlerData(int code, JSONObject data) {
            switch (code) {
                case 1:
                    Toast.makeText(Variable.app_context, "网络异常，获取信息失败", Toast.LENGTH_SHORT).show();
                    break;
                case -1:
                    Toast.makeText(Variable.app_context, "用户名密码失效，请重新登录", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(mContext, LoginActivity.class));
                    break;
                case 0:
                    Log.i(TAG, "获取信息成功");
                    JSONArray jArray;
                    try {
                        jArray = data.getJSONArray("proxyList");
                        ProxyLot lot;
                        for (int i = 0; i < jArray.length(); i++) {
                            lot = new ProxyLot();
                            lot.name = jArray.getJSONObject(i).getString("name");
                            lot.id = jArray.getJSONObject(i).getInt("id");
                            lot.auctionMainName = jArray.getJSONObject(i).getString("auctionMainName");
                            lot.auctionSessionName= jArray.getJSONObject(i).getString("auctionSessionName");
                            lot.apprisal = jArray.getJSONObject(i).getString("appraisal");
                            lot.proxyPrice = jArray.getJSONObject(i).getString("proxyPrice");
                            lot.imageUrl = jArray.getJSONObject(i).getString("image");
                            myProxyLots.add(lot);
                        }
                        Log.i(TAG, "数据获取完成");
                        handler.sendEmptyMessage(0);
                    } catch (JSONException e) {
                        Log.i(TAG, "json解析异常");
                        e.printStackTrace();
                    }
                    break;
            }
        }

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

        lot = new ProxyLot();
        lot.No = 1240;
        lot.name = "拍品2";
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
