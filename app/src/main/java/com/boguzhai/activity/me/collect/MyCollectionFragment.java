package com.boguzhai.activity.me.collect;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.boguzhai.activity.auction.LotInfoActivity;
import com.boguzhai.logic.dao.Lot;
import com.boguzhai.logic.dao.ProxyLot;
import com.boguzhai.logic.utils.HttpClient;
import com.boguzhai.logic.view.XListView;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link android.app.Fragment} subclass.
 */
public class MyCollectionFragment extends Fragment implements XListView.IXListViewListener, SwipeRefreshLayout.OnRefreshListener {

    public static String TAG = "MyCollectionFragment";


    private final int baseCount = 5;
    private List<Lot> myCollections;//所有收藏拍品集合，只有在刷新的情况下才会更新此集合。
    private int pageIndex = 0;//分页显示的页数，从 "0" 开始
    private boolean isSearch = false;//是否处于搜索下的显示
    private List<Lot> searchCollections;//搜索时显示的集合
    private int searchPageIndex = 0;//搜索状态下的页数
    private String key;//搜索关键字
    private List<Lot> tempLots;
    private String status = "";//"" "预展中" "拍卖中" "已结束" "已流拍"

    private MyCollectionActivity mContext;//fragment关联的activity
    private MyCollectionAdapter adapter;//适配器


    private SwipeRefreshLayout swipe_layout_my_collection;//支持下拉刷新的布局
    private XListView lv_my_collection;//支持加载更多的listview
    private View view;//fragment对应的视图
    private LayoutInflater inflater;
//    private PopupWindow popupWindow;//跳出修改或者删除按钮
//    private TextView tv_popupwindow_modify, tv_popupwindow_delete;//修改，删除
    private EditText et_my_collection_keyword;//查询关键字
    private Button btn_my_collection_search;//点击查询按钮
    private Spinner sp_my_collection_choose_auction;//选择拍卖会
    private Spinner sp_my_collection_choose_session;//选择专场


    private HttpClient conn;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.i(TAG, "接收到<数据获取完成>消息");
            initData();
        }
    };


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
//        listenPopupWindow(view);
        initView();
        initData();
    }


//    /**
//     * 触摸监听事件，用来关闭已经显示的popupwindow
//     *
//     * @param v
//     */
//    private void listenPopupWindow(View v) {
//        v.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (popupWindow != null) {
//                    dismissPopWindow();
//                }
//                return false;
//            }
//        });
//    }


    private void initView() {
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
//        listenPopupWindow(et_my_proxy_keyword);

        btn_my_collection_search = (Button) view.findViewById(R.id.btn_my_collection_search);
//        listenPopupWindow(btn_my_proxy_search);

        /**
         * 支持下拉刷新的布局，设置下拉监听事件，重写onRefresh()方法
         */
        swipe_layout_my_collection = (SwipeRefreshLayout) view.findViewById(R.id.swipe_layout_my_collection);
        swipe_layout_my_collection.setColorSchemeResources(R.color.gold);
        swipe_layout_my_collection.setOnRefreshListener(this);
//        listenPopupWindow(swipe_layout_my_proxy);

        /**
         * 网络请求，获取代理拍品列表
         */

    }

    /**
     * 初始化数据
     */
    private void initData() {

        isSearch = false;

        switch (status) {
            case "" :
                myCollections = testData();
                break;

            case "预展中" :
                myCollections = testData1();
                break;

            case "拍卖中" :
                myCollections = testData2();
                break;

            case "已成交" :
                myCollections = testData3();
                break;

            case "已流拍" :
                myCollections = testData4();
                break;

        }

//        newLots = new ArrayList<>();
//        for (ProxyLot lot : lotList) {
//            newLots.add(lot);
//        }




        /**
         * listview点击事件，跳转到相应的拍品信息
         */
        lv_my_collection.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                if (popupWindow != null) {
//                    dismissPopWindow();
//                } else {
                    Intent intent = new Intent(mContext, LotInfoActivity.class);
                    intent.putExtra("auctionId", myCollections.get(position - 1).name);
                    Log.i(TAG, "代理拍品的id为:" + String.valueOf(myCollections.get(position - 1).name));
                    startActivity(intent);
//                }
            }
        });


//        /**
//         * 如果是可以修改的代理，则设置可以长按弹出"修改"和"删除"
//         */
//        if (status == 1) {
//
//            lv_my_proxy.setOnScrollListener(new AbsListView.OnScrollListener() {
//                @Override
//                public void onScrollStateChanged(AbsListView view, int scrollState) {
//                    if (popupWindow != null) {
//                        dismissPopWindow();
//                    }
//                }
//
//                @Override
//                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//
//                }
//            });
//
//            lv_my_proxy.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//                @Override
//                public boolean onItemLongClick(AdapterView<?> parent, final View view, final int position, long id) {
//                    if (popupWindow != null) {
//                        dismissPopWindow();
//                    }
//                    View item = View.inflate(mContext.getApplicationContext(),
//                            R.layout.item_popupwindow, null);
//
//                    tv_popupwindow_modify = (TextView) item
//                            .findViewById(R.id.tv_popupwindow_modify);
//                    tv_popupwindow_delete = (TextView) item
//                            .findViewById(R.id.tv_popupwindow_delete);
//
//                    tv_popupwindow_modify.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            if (popupWindow != null) {
//                                dismissPopWindow();
//                            }
//                            Intent intent = new Intent(mContext, SetProxyPricingActivity.class);
//                            intent.putExtra("auctionId", myProxyLots.get(position - 1).name);
//                            startActivity(intent);
//                        }
//                    });
//                    tv_popupwindow_delete.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            if (popupWindow != null) {
//                                dismissPopWindow();
//                            }
//                            //弹出对话框，询问是否确认删除
//                            //网络请求
//
//
//                            // 位移动画
//                            TranslateAnimation ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,
//                                    Animation.RELATIVE_TO_SELF, -1.0f,
//                                    Animation.RELATIVE_TO_SELF, 0,
//                                    Animation.RELATIVE_TO_SELF, 0);
//                            ta.setDuration(200);
//                            view.startAnimation(ta);
//                            new Thread() {
//                                public void run() {
//                                    try {
//                                        Thread.sleep(200);
//                                    } catch (InterruptedException e) {
//                                        e.printStackTrace();
//                                    }
//                                    getActivity().runOnUiThread(new Runnable() {
//                                        public void run() {
//                                            if (isSearch) {//先在源数据中删除
//                                                for (int i = 0; i < myProxyLots.size(); i++) {
//                                                    if (adapter.getLots().get(position - 1).name.equals(myProxyLots.get(i).name)) {
//                                                        myProxyLots.remove(i);
//                                                        break;
//                                                    }
//                                                }
//                                                Log.i(TAG, "----------------------");
//                                                Log.i(TAG, adapter.getLots().toString());
//                                                Log.i(TAG, selectedAuctionLots.toString());
//
//                                                if(adapter.getLots() != selectedAuctionLots) {
//                                                    Log.i(TAG, "当前显示的数据不是“selectedAuctions”");
//                                                    for (int i = 0; i < adapter.getLots().size(); i++) {
//                                                        if (adapter.getLots().get(position - 1).name.equals(selectedAuctionLots.get(i).name)) {
//                                                            selectedAuctionLots.remove(i);
//                                                            break;
//                                                        }
//                                                    }
//                                                }
//
//                                            }
//
//                                            Log.i(TAG, "----------------------");
//                                            Log.i(TAG, adapter.getLots().toString());
//                                            Log.i(TAG, selectedAuctionLots.toString());
//                                            //然后在当前显示的数据中删除
//                                            adapter.removeElem(position - 1);
//                                            Log.i(TAG, "----------------------");
//                                            Log.i(TAG, adapter.getLots().toString());
//                                            Log.i(TAG, selectedAuctionLots.toString());
//                                            adapter.notifyDataSetChanged();
//                                        }
//                                    });
//                                }
//                            }.start();
//                        }
//                    });
//
//                    popupWindow = new PopupWindow(item,
//                            LinearLayout.LayoutParams.WRAP_CONTENT,
//                            LinearLayout.LayoutParams.WRAP_CONTENT);
//                    // 动画播放的前提条件，窗体必须有背景资源
//                    popupWindow.setBackgroundDrawable(new ColorDrawable(
//                            Color.TRANSPARENT));// 设置透明背景
//                    int[] pos = new int[2];
//                    view.getLocationInWindow(pos);
//                    int dp_x = 130;
//                    int dp_y = 150;
//                    int px_x = DensityUtils.dip2px(mContext.getApplicationContext(), dp_x);
//                    int px_y = DensityUtils.dip2px(mContext.getApplicationContext(), dp_y);
//                    popupWindow.showAtLocation(parent,
//                            Gravity.TOP + Gravity.LEFT, dp_x, pos[1] + dp_y);
//
//                    ScaleAnimation sa = new ScaleAnimation(0.5f, 1.0f, 0.5f,
//                            1.0f, Animation.RELATIVE_TO_SELF, 0.1f,
//                            Animation.RELATIVE_TO_SELF, 0);
//                    sa.setDuration(200);
//                    AlphaAnimation aa = new AlphaAnimation(0.5f, 1.0f);
//                    aa.setDuration(200);
//                    AnimationSet set = new AnimationSet(false);
//                    set.addAnimation(sa);
//                    set.addAnimation(aa);
//                    item.setAnimation(set);
//                    return true;
//                }
//            });
//
//
//        }





        /**
         * 设置数据适配器
         */
        adapter = new MyCollectionAdapter(mContext, myCollections);
        adapter.setPageIndex(0);
        lv_my_collection.setAdapter(adapter);






        /**
         * 点击进行关键字搜索
         */
        btn_my_collection_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String key = et_my_collection_keyword.getText().toString().trim();
                if (!TextUtils.isEmpty(key)) {
                    tempLots = new ArrayList<>();

                    for (Lot lot : myCollections) {
                        if ((lot.name.indexOf(key) >= 0) || (String.valueOf(lot.no).indexOf(key) >= 0) || (lot.status.indexOf(key) >= 0)) {
                            tempLots.add(lot);
                        }
                    }

                    /**
                     * 更新adapter，绑定新的数据，并且设置页面索引值为0
                     */
                    adapter = new MyCollectionAdapter(mContext, tempLots);
                    adapter.setPageIndex(0);
                    lv_my_collection.setAdapter(adapter);

                } else {
                    /**
                     * 更新adapter，绑定新的数据，并且设置页面索引值为0
                     */
                    adapter = new MyCollectionAdapter(mContext, myCollections);
                    adapter.setPageIndex(0);
                    lv_my_collection.setAdapter(adapter);
                }
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
    }

//    /**
//     * 释放popWindow
//     */
//    private void dismissPopWindow() {
//        popupWindow.dismiss();
//        popupWindow = null;
//    }

//    @Override
//    public void onDestroy() {
//        if (popupWindow != null) {
//            dismissPopWindow();
//        }
//        super.onDestroy();
//    }


    @Override
    public void onRefresh() {
        swipe_layout_my_collection.setRefreshing(true);
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
                        swipe_layout_my_collection.setRefreshing(false);
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
                        lv_my_collection.stopLoadMore();
                    }
                });
            }
        }.start();

    }







//    class MyCollectionHandler extends HttpJsonHandler {
//
//        @Override
//        public void handlerData(int code, JSONObject data) {
//            switch (code) {
//                case 1:
//                    Toast.makeText(Variable.app_context, "网络异常，获取信息失败", Toast.LENGTH_SHORT).show();
//                    break;
//                case -1:
//                    Toast.makeText(Variable.app_context, "用户名密码失效，请重新登录", Toast.LENGTH_SHORT).show();
//                    startActivity(new Intent(mContext, LoginActivity.class));
//                    break;
//                case 0:
//                    Log.i(TAG, "获取信息成功");
//                    JSONArray jArray;
//                    try {
//                        jArray = data.getJSONArray("proxyList");
//                        ProxyLot lot;
//                        for (int i = 0; i < jArray.length(); i++) {
//                            lot = new ProxyLot();
//                            lot.name = jArray.getJSONObject(i).getString("name");
//                            lot.id = jArray.getJSONObject(i).getInt("id");
//                            lot.auctionMainName = jArray.getJSONObject(i).getString("auctionMainName");
//                            lot.auctionSessionName= jArray.getJSONObject(i).getString("auctionSessionName");
//                            lot.apprisal = jArray.getJSONObject(i).getString("appraisal");
//                            lot.proxyPrice = jArray.getJSONObject(i).getString("proxyPrice");
//                            lot.imageUrl = jArray.getJSONObject(i).getString("image");
//                            myProxyLots.add(lot);
//                        }
//                        Log.i(TAG, "数据获取完成");
//                        handler.sendEmptyMessage(0);
//                    } catch (JSONException e) {
//                        Log.i(TAG, "json解析异常");
//                        e.printStackTrace();
//                    }
//                    break;
//            }
//        }
//
//    }

    private List<Lot> testData() {
        ArrayList<Lot> lotList = new ArrayList<>();
        ProxyLot lot = new ProxyLot();
        lot.no = "1233";
        lot.name = "明代唐伯虎书法作品";
        lot.status = "已成交";
        lot.appraisal1 = 5000;
        lot.appraisal2= 8000;
        lot.startPrice = 3000;
        lot.dealPrice = 10000;
        lotList.add(lot);

        lot = new ProxyLot();
        lot.no = "1234";
        lot.name = "张曦之书法作品";
        lot.status = "拍卖中";
        lot.appraisal1 = 5000;
        lot.appraisal2 = 8000;
        lot.startPrice = 3000;
        lot.currentPrice = 4000;
        lotList.add(lot);


        lot = new ProxyLot();
        lot.no = "1235";
        lot.name = "景德镇陶瓷";
        lot.status = "预展中";
        lot.appraisal1 = 5000;
        lot.appraisal2 = 8000;
        lot.startPrice = 3000;
        lotList.add(lot);

        lot = new ProxyLot();
        lot.no = "1236";
        lot.name = "光绪丁未年双龙寿字币";
        lot.status = "已流拍";
        lot.appraisal1 = 5000;
        lot.appraisal2 = 8000;
        lot.startPrice = 3000;
        lotList.add(lot);

        lot = new ProxyLot();
        lot.no = "1237";
        lot.name = "吴月亭款紫砂壶";
        lot.status = "已成交";
        lot.appraisal1 = 5000;
        lot.appraisal2 = 8000;
        lot.startPrice = 3000;
        lot.dealPrice = 10000;
        lotList.add(lot);

        lot = new ProxyLot();
        lot.no = "1238";
        lot.name = "冰种描金翡翠手镯";
        lot.status = "已成交";
        lot.appraisal1 = 5000;
        lot.appraisal2 = 8000;
        lot.startPrice = 3000;
        lot.dealPrice = 11000;
        lotList.add(lot);

        lot = new ProxyLot();
        lot.no = "1239";
        lot.name = "乾隆五彩盘";
        lot.status = "拍卖中";
        lot.appraisal1 = 5000;
        lot.appraisal2 = 8000;
        lot.startPrice = 3000;
        lot.currentPrice = 5000;
        lotList.add(lot);


        lot = new ProxyLot();
        lot.no = "1230";
        lot.name = "青花葫芦瓶";
        lot.status = "预展中";
        lot.appraisal1 = 5000;
        lot.appraisal2 = 8000;
        lot.startPrice = 3000;
        lotList.add(lot);


        lot = new ProxyLot();
        lot.no = "1231";
        lot.name = "叶圣陶书法";
        lot.status = "拍卖中";
        lot.appraisal1 = 5000;
        lot.appraisal2 = 8000;
        lot.startPrice = 3000;
        lot.currentPrice = 5000;
        lotList.add(lot);


        lot = new ProxyLot();
        lot.no = "1232";
        lot.name = "宋占魁虎字书法";
        lot.status = "已流拍";
        lot.appraisal1 = 5000;
        lot.appraisal2 = 8000;
        lot.startPrice = 3000;
        lotList.add(lot);

        lot = new ProxyLot();
        lot.no = "1240";
        lot.name = "拍品2";
        lot.status = "拍卖中";
        lot.appraisal1 = 5000;
        lot.appraisal2 = 8000;
        lot.startPrice = 3000;
        lot.currentPrice = 5000;
        lotList.add(lot);

        return lotList;


    }
    private List<Lot> testData1() {
        ArrayList<Lot> lotList = new ArrayList<>();
        ProxyLot lot = new ProxyLot();


        lot = new ProxyLot();
        lot.no = "1235";
        lot.name = "景德镇陶瓷";
        lot.status = "预展中";
        lot.appraisal1 = 5000;
        lot.appraisal2 = 8000;
        lot.startPrice = 3000;
        lotList.add(lot);


        lot = new ProxyLot();
        lot.no = "1230";
        lot.name = "青花葫芦瓶";
        lot.status = "预展中";
        lot.appraisal1 = 5000;
        lot.appraisal2 = 8000;
        lot.startPrice = 3000;
        lotList.add(lot);

        return lotList;


    }
    private List<Lot> testData2() {
        ArrayList<Lot> lotList = new ArrayList<>();
        ProxyLot lot = new ProxyLot();

        lot = new ProxyLot();
        lot.no = "1234";
        lot.name = "张曦之书法作品";
        lot.status = "拍卖中";
        lot.appraisal1 = 5000;
        lot.appraisal2 = 8000;
        lot.startPrice = 3000;
        lot.currentPrice = 4000;
        lotList.add(lot);


        lot = new ProxyLot();
        lot.no = "1239";
        lot.name = "乾隆五彩盘";
        lot.status = "拍卖中";
        lot.appraisal1 = 5000;
        lot.appraisal2 = 8000;
        lot.startPrice = 3000;
        lot.currentPrice = 5000;
        lotList.add(lot);




        lot = new ProxyLot();
        lot.no = "1231";
        lot.name = "叶圣陶书法";
        lot.status = "拍卖中";
        lot.appraisal1 = 5000;
        lot.appraisal2 = 8000;
        lot.startPrice = 3000;
        lot.currentPrice = 5000;
        lotList.add(lot);



        lot = new ProxyLot();
        lot.no = "1240";
        lot.name = "拍品2";
        lot.status = "拍卖中";
        lot.appraisal1 = 5000;
        lot.appraisal2 = 8000;
        lot.startPrice = 3000;
        lot.currentPrice = 5000;
        lotList.add(lot);

        return lotList;


    }
    private List<Lot> testData3() {
        ArrayList<Lot> lotList = new ArrayList<>();
        ProxyLot lot = new ProxyLot();
        lot.no = "1233";
        lot.name = "明代唐伯虎书法作品";
        lot.status = "已成交";
        lot.appraisal1 = 5000;
        lot.appraisal2 = 8000;
        lot.startPrice = 3000;
        lot.dealPrice = 10000;
        lotList.add(lot);


        lot = new ProxyLot();
        lot.no = "1237";
        lot.name = "吴月亭款紫砂壶";
        lot.status = "已成交";
        lot.appraisal1 = 5000;
        lot.appraisal2 = 8000;
        lot.startPrice = 3000;
        lot.dealPrice = 10000;
        lotList.add(lot);

        lot = new ProxyLot();
        lot.no = "1238";
        lot.name = "冰种描金翡翠手镯";
        lot.status = "已成交";
        lot.appraisal1 = 5000;
        lot.appraisal2 = 8000;
        lot.startPrice = 3000;
        lot.dealPrice = 11000;
        lotList.add(lot);


        return lotList;


    }
    private List<Lot> testData4() {
        ArrayList<Lot> lotList = new ArrayList<>();
        ProxyLot lot = new ProxyLot();

        lot = new ProxyLot();
        lot.no = "1236";
        lot.name = "光绪丁未年双龙寿字币";
        lot.status = "已流拍";
        lot.appraisal1 = 5000;
        lot.appraisal2 = 8000;
        lot.startPrice = 3000;
        lotList.add(lot);





        lot = new ProxyLot();
        lot.no = "1232";
        lot.name = "宋占魁虎字书法";
        lot.status = "已流拍";
        lot.appraisal1 = 5000;
        lot.appraisal2 = 8000;
        lot.startPrice = 3000;
        lotList.add(lot);


        return lotList;


    }
}
