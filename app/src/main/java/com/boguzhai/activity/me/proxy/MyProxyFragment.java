package com.boguzhai.activity.me.proxy;


import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.boguzhai.activity.base.Variable;
import com.boguzhai.activity.login.LoginActivity;
import com.boguzhai.logic.dao.ProxyLot;
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
    public final static int REFRESH = 0;//刷新
    public final static int LOADMORE = 1;//加载更多
    public int which = REFRESH;
    private int totalCount = 0;//结果总数
    private int currentCount = 0;//当前总数
    private int number = 1;//分页序号，从1开始
    private int size = 0;//每次分页数目
    private boolean isSearch = false;//是否处于搜索下的显示

    private List<ProxyLot> myProxyLots;//所有代理出价拍品集合，只有在刷新的情况下才会更新此集合。
    //    private List<ProxyLot> searchProxyLots;//搜索时显示的集合
    private String spinnerAuctionText = "";//选择拍卖会spinner显示的文字
    private String spinnerSessionText = "";//选择专场spinner显示的文字
    private String searchText = "";//搜索的关键字
    private List<ProxyLot> selectedAuctionLots;//通过选择的拍卖会过滤后的代理拍品
    private List<ProxyLot> tempLots;
    private int status = 1;//1可修改的代理出价(拍卖未结束) 2历史代理(不可修改)

    private ProxyPricingActivity mContext;//fragment关联的activity
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


    public Utility utility = new Utility();
    private String[] list_type1 = {"不限"};
    private String[] list_type2 = {"不限"};
    private String selectedAuctionName = "不限";
    private String selectedSessionName = "不限";


    private HttpClient conn;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.i(TAG, "接收到<数据获取完成>消息");
            switch (which) {
                case REFRESH:
                    swipe_layout_my_proxy.setRefreshing(false);
                    initData();
                    break;
                case LOADMORE:
                    lv_my_proxy.stopLoadMore();
                    if (isSearch) {
                        proxyLotFilterByKey(searchText);
                    } else {
                        proxyLotFilterByKey("");
                    }
                    break;
            }
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
        requestData();
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
    }

    /**
     * 请求网络数据
     */
    private void requestData() {
        /**
         * 从网络获取数据,从第一页开始
         */
        conn = new HttpClient();
        conn.setUrl("http://60.191.203.80/phones/pAuctionUserAction!getAuctionProxyList.htm");
        conn.setParam("sessionid", "");//
        conn.setParam("status", String.valueOf(status));//0可修改的代理出价(拍卖未结束) 1历史代理(不可修改)
        conn.setParam("number", String.valueOf(number));//分页序号，从1开始
        new Thread(new HttpPostRunnable(conn, new MyProxyHandler())).start();
    }


    /**
     * 初始化数据
     */
    private void initData() {

        isSearch = false;
        myProxyLots = new ArrayList<>();

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
                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                            builder.setMessage("确认删除该代理出价吗?");
                            builder.setTitle("提示");
                            builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    //网络请求
                                    conn = new HttpClient();
                                    conn.setUrl("http://60.191.203.80/phones/pAuctionUserAction!removeAuctionProxyPrice.htm");
                                    conn.setParam("sessionid", "");//
                                    conn.setParam("number", adapter.getLots().get(position - 1).id);
                                    new Thread(new HttpPostRunnable(conn, new MyProxyDeleteHandler(position, view))).start();
                                }
                            });
                            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                            builder.create().show();


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
        Utility.setSpinner(mContext, (Spinner) view.findViewById(R.id.sp_my_proxy_choose_auction), list_type1, new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view0, int position,
                                               long id) {
                        final List<String> sessionNames;

                        selectedAuctionLots = new ArrayList<>();


                        /**
                         * 选中"不限"，遍历所有代理拍品所在拍卖会下的所有专场，
                         */
                        if (position == 0) {
                            spinnerAuctionText = "不限";
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
                            /**
                             * 选中某一拍卖会，遍历该专场下所有专场
                             */
                        } else if (position >= 1) {
                            spinnerAuctionText = auctions.get(position - 1).auctionName;
                            Log.i(TAG, "选中的拍卖会为:" + selectedAuctionName);
                            sessionNames = auctions.get(position - 1).sessionNames;
                            list_type2 = new String[sessionNames.size() + 1];
                            Log.i(TAG, "该拍卖会中有:" + sessionNames.size() + "个专场");
                            list_type2[0] = "不限";
                            for (int i = 1; i < list_type2.length; i++) {
                                list_type2[i] = auctions.get(position - 1).sessionNames.get(i - 1);
                            }
                        } else {//防止后面用到sesionNames报might not have been initialized错误
                            sessionNames = null;//can't reach
                        }


                        if (isSearch) {
                            proxyLotFilterByKey(searchText);
                        } else {
                            proxyLotFilterByKey("");
                        }

                        /**
                         * 设置专场spinner，及监听事件
                         */
                        Utility.setSpinner(mContext, (Spinner) view.findViewById(R.id.sp_my_proxy_choose_session), list_type2, new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                /**
                                 * 选中"不限"，遍历对应拍卖会下的所有专场，
                                 */
                                if (position == 0) {
                                    spinnerSessionText = "不限";
                                    Log.i(TAG, "选中的专场为:" + spinnerSessionText);
                                } else if (position > 0) { //选中某一专场
                                    spinnerSessionText = sessionNames.get(position - 1);
                                    Log.i(TAG, "选中的专场为:" + spinnerSessionText);
                                }


                                if (isSearch) {
                                    proxyLotFilterByKey(searchText);
                                } else {
                                    proxyLotFilterByKey("");
                                }
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
        lv_my_proxy.setAdapter(adapter);


        btn_my_proxy_search.addTextChangedListener(new TextWatcher() {
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
        /**
         * 点击进行关键字搜索
         */
        btn_my_proxy_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSearch = true;
                final String key = et_my_proxy_keyword.getText().toString().trim();
                proxyLotFilterByKey(key);
            }

        });

        Log.i(TAG, "getAucctionName()" + mContext.getAucctionName());
        for (int i = 0; i < list_type1.length; i++) {
            if (list_type1[i].equals(mContext.getAucctionName())) {
                sp_my_proxy_choose_auction.setSelection(i);
            }

        }

    }

    /**
     * 通过关键字进行过滤并显示
     */
    private void proxyLotFilterByKey(String key) {

        tempLots = new ArrayList<>();
        if (TextUtils.isEmpty(key)) {//搜索的关键字为""
            isSearch = false;
            if ("不限".equals(spinnerAuctionText)) {//拍卖会选择"不限"
                if ("不限".equals(spinnerSessionText)) {//专场选择"不限"
                    adapter = new MyProxyAdapter(mContext, myProxyLots);
                } else {
                    for (int i = 0; i < myProxyLots.size(); i++) {
                        if (myProxyLots.get(i).auctionSessionName.indexOf(spinnerSessionText) >= 0) {
                            tempLots.add(myProxyLots.get(i));
                        }
                    }
                    adapter = new MyProxyAdapter(mContext, tempLots);
                }
            } else {
                if ("不限".equals(spinnerSessionText)) {//专场选择"不限"
                    for (int i = 0; i < myProxyLots.size(); i++) {
                        if (myProxyLots.get(i).auctionMainName.indexOf(spinnerAuctionText) >= 0) {
                            tempLots.add(myProxyLots.get(i));
                        }
                    }
                } else {
                    for (int i = 0; i < myProxyLots.size(); i++) {
                        if (myProxyLots.get(i).auctionSessionName.indexOf(spinnerSessionText) >= 0
                                && myProxyLots.get(i).auctionMainName.indexOf(spinnerAuctionText) >= 0) {
                            tempLots.add(myProxyLots.get(i));
                        }
                    }
                }
                adapter = new MyProxyAdapter(mContext, tempLots);
            }
            lv_my_proxy.setAdapter(adapter);
        } else {//搜索的关键字不为空
            isSearch = true;
            tempLots = new ArrayList<>();
            if ("不限".equals(spinnerAuctionText)) {//拍卖会选择"不限"
                if ("不限".equals(spinnerSessionText)) {//专场选择"不限"
                    for (int i = 0; i < myProxyLots.size(); i++) {
                        if (myProxyLots.get(i).auctionSessionName.indexOf(searchText) >= 0
                                || myProxyLots.get(i).auctionMainName.indexOf(searchText) >= 0
                                || myProxyLots.get(i).name.indexOf(searchText) >= 0
                                || myProxyLots.get(i).id.indexOf(searchText) >= 0) {
                            tempLots.add(myProxyLots.get(i));
                        }
                    }
                } else {
                    for (int i = 0; i < myProxyLots.size(); i++) {
                        if (myProxyLots.get(i).auctionSessionName.indexOf(spinnerSessionText) >= 0
                                && (myProxyLots.get(i).auctionMainName.indexOf(searchText) >= 0
                                || myProxyLots.get(i).name.indexOf(searchText) >= 0
                                || myProxyLots.get(i).id.indexOf(searchText) >= 0)) {
                            tempLots.add(myProxyLots.get(i));
                        }
                    }
                }
            } else {
                if ("不限".equals(spinnerSessionText)) {//专场选择"不限"
                    for (int i = 0; i < myProxyLots.size(); i++) {
                        if (myProxyLots.get(i).auctionMainName.indexOf(spinnerAuctionText) >= 0
                                && (myProxyLots.get(i).auctionMainName.indexOf(searchText) >= 0
                                || myProxyLots.get(i).name.indexOf(searchText) >= 0
                                || myProxyLots.get(i).id.indexOf(searchText) >= 0)) {
                            tempLots.add(myProxyLots.get(i));
                        }
                    }
                } else {
                    for (int i = 0; i < myProxyLots.size(); i++) {
                        if (myProxyLots.get(i).auctionSessionName.indexOf(spinnerSessionText) >= 0
                                && myProxyLots.get(i).auctionMainName.indexOf(spinnerAuctionText) >= 0
                                && (myProxyLots.get(i).name.indexOf(searchText) >= 0
                                || myProxyLots.get(i).id.indexOf(searchText) >= 0)) {
                            tempLots.add(myProxyLots.get(i));
                        }
                    }
                }
            }
            adapter = new MyProxyAdapter(mContext, tempLots);
            lv_my_proxy.setAdapter(adapter);
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
        which = REFRESH;
        swipe_layout_my_proxy.setRefreshing(true);
        myProxyLots.clear();//清空之前所有数据
        isSearch = false;
        et_my_proxy_keyword.setText("");//清空搜索关键字
        number = 1;//从第一页开始
        Log.i(TAG, "下拉刷新");
        requestData();
    }


    @Override
    public void onLoadMore() {
        which = LOADMORE;
        Log.i(TAG, "加载更多");
        if (totalCount == currentCount) {
            Toast.makeText(mContext, "没有更多数据了", Toast.LENGTH_SHORT).show();
        } else {
            number++;//页数加1
            requestData();
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


    class MyProxyHandler extends HttpJsonHandler {

        @Override
        public void handlerData(int code, JSONObject data) {
            switch (code) {
                case 1:
                    number--;
                    Toast.makeText(Variable.app_context, "网络异常，获取信息失败", Toast.LENGTH_SHORT).show();
                    break;
                case -1:
                    number--;
                    Toast.makeText(Variable.app_context, "用户名密码失效，请重新登录", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(mContext, LoginActivity.class));
                    break;
                case 0:
                    Log.i(TAG, "获取信息成功");
                    try {
                        size = Integer.parseInt(data.getString(""));//每页的数目
                        totalCount = Integer.parseInt(data.getString("count"));//总的数目
                        currentCount += size;
                        JSONArray jArray = data.getJSONArray("proxyList");
                        ProxyLot lot;
                        for (int i = 0; i < jArray.length(); i++) {
                            lot = new ProxyLot();
                            lot.name = jArray.getJSONObject(i).getString("name");
                            lot.id = jArray.getJSONObject(i).getString("id");
                            lot.auctionMainName = jArray.getJSONObject(i).getString("auctionMainName");
                            lot.auctionSessionName = jArray.getJSONObject(i).getString("auctionSessionName");
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


    class MyProxyDeleteHandler extends HttpJsonHandler {

        int position = 0;
        View itemView;
        MyProxyDeleteHandler(int position, View view) {
            this.position = position;
            this.itemView = view;
        }

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
                    // 位移动画
                    TranslateAnimation ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,
                            Animation.RELATIVE_TO_SELF, -1.0f,
                            Animation.RELATIVE_TO_SELF, 0,
                            Animation.RELATIVE_TO_SELF, 0);
                    ta.setDuration(200);
                    itemView.startAnimation(ta);
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            if (adapter.getLots() != myProxyLots) {
                                for (int i = 0; i < myProxyLots.size(); i++) {
                                    if (adapter.getLots().get(position - 1).name.equals(myProxyLots.get(i).name)) {
                                        myProxyLots.remove(i);
                                        break;
                                    }
                                }
                            }
                            //然后在当前显示的数据中删除
                            adapter.removeElem(position - 1);
                            adapter.notifyDataSetChanged();
                        }
                    });
                    break;
            }
        }

    }

}
