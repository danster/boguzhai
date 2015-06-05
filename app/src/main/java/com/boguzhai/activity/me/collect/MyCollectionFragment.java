package com.boguzhai.activity.me.collect;


import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.boguzhai.R;
import com.boguzhai.activity.auction.LotInfoActivity;
import com.boguzhai.activity.base.Constant;
import com.boguzhai.activity.base.Variable;
import com.boguzhai.activity.login.LoginActivity;
import com.boguzhai.logic.dao.CollectionLot;
import com.boguzhai.logic.thread.HttpJsonHandler;
import com.boguzhai.logic.thread.HttpPostRunnable;
import com.boguzhai.logic.utils.HttpClient;
import com.boguzhai.logic.utils.Utility;
import com.boguzhai.logic.view.XListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
    private String[] status = {"", ""};//"" "预展中" "拍卖中" "已结束" "已流拍"

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

    private TextView title_right;
    private String[] sortTypes = {"按拍品名称", "按拍品号",
            "按拍品起拍价升序", "按拍品起拍价降序",
            "按拍品估价最低值升序", "按拍品估价最低价降序",
            "按拍品估价最高值升序", "按拍品估价最高值降序"};
    private int sortType = 0;

    public MyCollectionFragment(String mainState, String status) {
        this.status[0] = mainState;
        this.status[1] = status;
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

        onRefresh();
    }


    private void requestData() {
        conn = new HttpClient();
        conn.setHeader("cookie", "JSESSIONID=" + Variable.account.sessionid);
        conn.setUrl(Constant.url + "pClientStowAction!getCollectedAuctionList.htm");
        conn.setParam("mainState", status[0]);//拍卖会状态 "" "预展中" "拍卖中" "已结束"
        conn.setParam("status", status[1]);//拍卖会状态 "" "预展中" "拍卖中" "已结束"
        conn.setParam("number", String.valueOf(number));//分页序号，从1开始
        new Thread(new HttpPostRunnable(conn, new MyCollectionHandler())).start();
    }


    private void deleteCollection(int position, View view) {
        conn = new HttpClient();
        conn.setHeader("cookie", "JSESSIONID=" + Variable.account.sessionid);
        conn.setUrl(Constant.url + "pClientStowAction!removeCollectedAuction.htm");
        conn.setParam("auctionId", adapter.lots.get(position - 1).id);//收藏拍品id
        new Thread(new HttpPostRunnable(conn, new DeleteCollectionHandler(position, view))).start();
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
        lv_my_collection.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //跳转到该拍品信息界面
                Variable.currentLot = adapter.lots.get(position - 1);
                mContext.startActivity(new Intent(mContext, LotInfoActivity.class));

            }
        });
        lv_my_collection.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, final View view, final int position, long id) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                View dialog = View.inflate(mContext, R.layout.me_mycollection_delete_dialog, null);
                TextView tv = (TextView) dialog.findViewById(R.id.delete_collection);
                final AlertDialog alertDialog = builder.setView(dialog).show();
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //http请求
                        Log.i(TAG, "删除收藏http请求");
                        deleteCollection(position, view);
                        //关闭对话框
                        alertDialog.dismiss();
                    }
                });

                return true;
            }
        });

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


        //排序
        title_right = (TextView) getActivity().findViewById(R.id.title_right);
        title_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(mContext).setSingleChoiceItems(sortTypes, sortType,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int index) {
                                sortType = index;
                                Collections.sort(tempCollections, new LotComparator());
                                adapter.notifyDataSetChanged();
                                dialog.dismiss();
                            }
                        }).setNegativeButton("取消", null).show();
            }
        });
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

        Collections.sort(tempCollections, new LotComparator());
        adapter.notifyDataSetChanged();
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


        Collections.sort(tempCollections, new LotComparator());
        adapter.notifyDataSetChanged();

    }

    @Override
    public void onRefresh() {
        Log.i(TAG, "下拉刷新");
        isSearch = false;
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
        if (currentCount >= totalCount) {
            Toast.makeText(mContext, "没有更多数据了", Toast.LENGTH_SHORT).show();
        }
        number++;
        requestData();
    }

    private class MyCollectionHandler extends HttpJsonHandler {


        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                case 9:
                    swipe_layout_my_collection.setRefreshing(false);
                    break;
            }
        }


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
                        if(number == 1 && totalCount == 0) {
                            Utility.toastMessage("暂无数据");
                        }
                        currentCount += size;
                        JSONArray jArray = data.getJSONArray("auctionList");
                        CollectionLot lot;
                        for (int i = 0; i < jArray.length(); i++) {
                            lot = new CollectionLot();
                            lot.name = jArray.getJSONObject(i).getString("name");
                            lot.id = jArray.getJSONObject(i).getString("id");
                            lot.no = jArray.getJSONObject(i).getString("no");
                            lot.startPrice = Double.parseDouble(jArray.getJSONObject(i).getString("startPrice"));
                            lot.status = jArray.getJSONObject(i).getString("status");
                            lot.apprisal = jArray.getJSONObject(i).getString("appraisal");
                            if (!"".equals(jArray.getJSONObject(i).getString("dealPrice"))) {
                                lot.dealPrice = Double.parseDouble(jArray.getJSONObject(i).getString("dealPrice"));
                            }
                            lot.biddingTime = jArray.getJSONObject(i).getString("biddingTime");
                            lot.forBidding = Integer.parseInt(jArray.getJSONObject(i).getString("forBidding"));
                            lot.imageUrl = jArray.getJSONObject(i).getString("image");
                            myCollections.add(lot);
                        }

                        // 网络批量下载拍品图片
                        new AsyncTask<Void, Void, Void>() {
                            @Override
                            protected Void doInBackground(Void... params) {
                                try {
                                    BitmapFactory.Options options = new BitmapFactory.Options();
                                    options.inJustDecodeBounds = false;
                                    options.inSampleSize = 5; //width，hight设为原来的 .. 分之一

                                    for (CollectionLot lot : myCollections) {
                                        InputStream in = new URL(lot.imageUrl).openStream();
                                        lot.image = BitmapFactory.decodeStream(in, null, options);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void result) {
                                adapter.notifyDataSetChanged();
                            }
                        }.execute();

                        Log.i(TAG, "我的收藏数据获取完成！");
                        if (number == 1) {//刷新
                            Toast.makeText(mContext, "刷新成功", Toast.LENGTH_SHORT).show();
                            swipe_layout_my_collection.setRefreshing(false);
                            initData();
                        } else {//加载更多
                            if (!isSearch) {
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

    private class DeleteCollectionHandler extends HttpJsonHandler {

        private View view;
        private int position;

        DeleteCollectionHandler(int position, View view) {
            this.view = view;
            this.position = position;
        }

        @Override
        public void handlerData(int code, JSONObject data) {
            super.handlerData(code, data);
            switch (code) {
                case 0:
                    Utility.toastMessage("删除成功");
                    //显示位移动画
                    TranslateAnimation ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,
                            Animation.RELATIVE_TO_SELF, -1.0f,
                            Animation.RELATIVE_TO_SELF, 0,
                            Animation.RELATIVE_TO_SELF, 0);
                    ta.setDuration(200);
                    view.startAnimation(ta);
                    //从原始数据中删除
                    for (int i = 0; i < myCollections.size(); i++) {
                        if (adapter.lots.get(position - 1).name.equals(myCollections.get(i).name)) {
                            myCollections.remove(i);
                            break;
                        }
                    }
                    //从集合中删除该收藏
                    adapter.lots.remove(position - 1);
                    adapter.notifyDataSetChanged();
                    break;
                case 1:
                    Utility.toastMessage("服务器出错，删除失败");
                    break;
                case 2:
                    Utility.toastMessage("服务器出错，删除失败");
                    break;
            }

        }
    }

    class LotComparator implements Comparator<CollectionLot> {
        public int compare(CollectionLot l1, CollectionLot l2) {
            switch (sortType) {
                case 0:
                    if (!l1.name.equals(l2.name)) {
                        return l1.name.compareTo(l2.name);
                    } else if (!l1.id.equals(l2.id)) {
                        return l1.id.compareTo(l2.id);
                    }
                    break;
                case 1:
                    if (l1.no.equals(l2.no)) {
                        return l1.no.compareTo(l2.no);
                    } else if (!l1.id.equals(l2.id)) {
                        return l1.id.compareTo(l2.id);
                    }
                    break;
                case 2:
                    if (l1.startPrice != l2.startPrice) {
                        double gap = l1.startPrice - l2.startPrice;
                        return gap > 0 ? 1 : (gap < 0 ? -1 : 0);
                    } else if (!l1.id.equals(l2.id)) {
                        return l1.id.compareTo(l2.id);
                    }
                    break;
                case 3:
                    if (l1.startPrice != l2.startPrice) {
                        double gap = l2.startPrice - l1.startPrice;
                        return gap > 0 ? 1 : (gap < 0 ? -1 : 0);
                    } else if (!l1.id.equals(l2.id)) {
                        return l1.id.compareTo(l2.id);
                    }
                    break;
                case 4:
                    if (l1.appraisal1 != l2.appraisal1) {
                        double gap = l1.appraisal1 - l2.appraisal1;
                        return gap > 0 ? 1 : (gap < 0 ? -1 : 0);
                    } else if (!l1.id.equals(l2.id)) {
                        return l1.id.compareTo(l2.id);
                    }
                    break;
                case 5:
                    if (l1.appraisal1 != l2.appraisal1) {
                        double gap = l2.appraisal1 - l1.appraisal1;
                        return gap > 0 ? 1 : (gap < 0 ? -1 : 0);
                    } else if (!l1.id.equals(l2.id)) {
                        return l1.id.compareTo(l2.id);
                    }
                    break;
                case 6:
                    if (l1.appraisal2 != l2.appraisal2) {
                        double gap = l1.appraisal2 - l2.appraisal2;
                        return gap > 0 ? 1 : (gap < 0 ? -1 : 0);
                    } else if (!l1.id.equals(l2.id)) {
                        return l1.id.compareTo(l2.id);
                    }
                    break;
                case 7:
                    if (l1.appraisal2 != l2.appraisal2) {
                        double gap = l2.appraisal2 - l1.appraisal2;
                        return gap > 0 ? 1 : (gap < 0 ? -1 : 0);
                    } else if (!l1.id.equals(l2.id)) {
                        return l1.id.compareTo(l2.id);
                    }
                    break;
            }
            return 0;
        }
    }


}