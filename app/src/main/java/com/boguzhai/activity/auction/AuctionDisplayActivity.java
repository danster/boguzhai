package com.boguzhai.activity.auction;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.boguzhai.R;
import com.boguzhai.activity.base.BaseActivity;
import com.boguzhai.activity.base.Constant;
import com.boguzhai.activity.base.Variable;
import com.boguzhai.activity.items.LotListAdapter;
import com.boguzhai.logic.dao.Lot;
import com.boguzhai.logic.thread.HttpJsonHandler;
import com.boguzhai.logic.thread.HttpPostRunnable;
import com.boguzhai.logic.utils.HttpClient;
import com.boguzhai.logic.utils.JsonApi;
import com.boguzhai.logic.utils.Utility;
import com.boguzhai.logic.widget.XListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.Collator;
import java.text.RuleBasedCollator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

public class AuctionDisplayActivity extends BaseActivity implements XListView.IXListViewListener,
        SwipeRefreshLayout.OnRefreshListener{

    private ArrayList<Lot> list, temp_list;
    private XListView listview;
    private LotListAdapter adapter;

    private SwipeRefreshLayout swipe_layout;
    private int number = 1;

    private String[] sortTypes = {"按图录号", "按拍品名称",
                                  "按拍品起拍价升序", "按拍品起拍价降序",
                                  "按拍品估价最低值升序", "按拍品估价最低价降序",
                                  "按拍品估价最高值升序", "按拍品估价最高值降序" };
    private int sortType=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLinearView(R.layout.auction_preview);
        title.setText("拍卖专场");
        init();
    }

    private void init(){
        list = new ArrayList<Lot>();
        temp_list = new ArrayList<Lot>();
        Utility.showAuctionInfo(this, Variable.currentAuction, Variable.currentSession);

        this.listen(R.id.sort);
        this.listen(R.id.search);

        if(Variable.currentAuction.type.equals("同步")){
            title_right.setText("进入拍卖");
            title_right.setVisibility(View.VISIBLE);
        } else {
            title_right.setVisibility(View.INVISIBLE);
        }

        this.showListView();
    }

    @Override
    public boolean onKeyDown (int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK: Utility.gotoMainpage(1); break;
        }
        return true;
    }

    @Override
    public void onClick(View v){
        super.onClick(v);
        switch (v.getId()) {
            case R.id.ly_title_left:  Utility.gotoMainpage(1);  break;
            case R.id.ly_title_right: startActivity(new Intent(context, AuctionActiveActivity.class));   break;
            case R.id.sort:
                new AlertDialog.Builder(this).setSingleChoiceItems(sortTypes, sortType,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int index) {
                                sortType = index;
                                Collections.sort(list, new LotComparator());
                                adapter.notifyDataSetChanged();
                                dialog.dismiss();
                            }
                        }).setNegativeButton("取消", null).show();
                break;
            case R.id.search:
                String key = ((EditText)findViewById(R.id.search_value)).getText().toString();
                if (!TextUtils.isEmpty(key)) {
                    temp_list.clear();
                    temp_list.addAll(list);

                    list.clear();
                    for (Lot lot : temp_list) {
                        if (lot.name.indexOf(key) >= 0) {
                            list.add(lot);
                        }
                    }
                    adapter.notifyDataSetChanged();;
                }

                break;
            default:
                break;
        }
    }

    // 展示专场的拍品列表
    public void showListView(){
        listview = (XListView) findViewById(R.id.lotlist);
        adapter = new LotListAdapter(this, list);
        listview.setAdapter(adapter);

        listview.setPullLoadEnable(true);
        listview.setPullRefreshEnable(false);
        listview.setXListViewListener(this);

        // 支持下拉刷新的布局，设置下拉监听事件，重写onRefresh()方法
        swipe_layout = (SwipeRefreshLayout) findViewById(R.id.refresh);
        swipe_layout.setColorSchemeResources(R.color.gold);
        swipe_layout.setOnRefreshListener(this);

        number = 1;
        Utility.showLoadingDialog("正在加载...");
        this.httpConnect(1);
    }

    @Override
    public void onRefresh() {
        swipe_layout.setRefreshing(true);
        listview.setPullLoadEnable(true);
        number = 1;
        this.httpConnect(1);
    }

    @Override
    public void onLoadMore() {
        this.httpConnect(number);
    }

    private void httpConnect(int number){
        ((EditText)findViewById(R.id.search_value)).setText("");
        if(temp_list.size() > 0){
            list.clear();
            list.addAll(temp_list);
            temp_list.clear();
        }
        HttpClient conn = new HttpClient();
        conn.setParam("number", number + "");
        conn.setUrl(Constant.url + "pAuctionInfoAction!getAuctionInfoListBySessionId.htm?auctionSessionId=" + Variable.currentSession.id);
        new Thread(new HttpPostRunnable(conn, new ShowLotListHandler())).start();
    }

    // 拍品排序器
    public class LotComparator implements Comparator<Lot> {
        public int compare(Lot l1, Lot l2) {
            RuleBasedCollator collator = (RuleBasedCollator) Collator.getInstance(Locale.CHINA);
            switch (sortType){
                case 0:
                    if(!l1.no.equals(l2.no)){
                        return Integer.parseInt(l1.no) - Integer.parseInt(l2.no);
                    } else {
                        return l1.id.compareTo(l2.id);
                    }
                case 1:
                    if(!l1.name.equals(l2.name)){
                        return collator.compare(l1.name, l2.name);
                    } else {
                        return l1.id.compareTo(l2.id);
                    }
                case 2:
                    if(l1.startPrice != l2.startPrice){
                        double gap = l1.startPrice - l2.startPrice;
                        return gap > 0 ? 1 : (gap < 0 ? -1 : 0);
                    } else {
                        return l1.id.compareTo(l2.id);
                    }
                case 3:
                    if(l1.startPrice != l2.startPrice){
                        double gap = l2.startPrice - l1.startPrice;
                        return gap > 0 ? 1 : (gap < 0 ? -1 : 0);
                    } else {
                        return l1.id.compareTo(l2.id);
                    }
                case 4:
                    if(l1.appraisal1 != l2.appraisal1){
                        double gap = l1.appraisal1 - l2.appraisal1;
                        return gap > 0 ? 1 : (gap < 0 ? -1 : 0);
                    } else {
                        return l1.id.compareTo(l2.id);
                    }
                case 5:
                    if(l1.appraisal1 != l2.appraisal1){
                        double gap = l2.appraisal1 - l1.appraisal1;
                        return gap > 0 ? 1 : (gap < 0 ? -1 : 0);
                    } else {
                        return l1.id.compareTo(l2.id);
                    }
                case 6:
                    if(l1.appraisal2 != l2.appraisal2){
                        double gap = l1.appraisal2 - l2.appraisal2;
                        return gap > 0 ? 1 : (gap < 0 ? -1 : 0);
                    } else {
                        return l1.id.compareTo(l2.id);
                    }
                case 7:
                    if(l1.appraisal2 != l2.appraisal2){
                        double gap = l2.appraisal2 - l1.appraisal2;
                        return gap > 0 ? 1 : (gap < 0 ? -1 : 0);
                    } else {
                        return l1.id.compareTo(l2.id);
                    }
                default:
                    break;
            }
            return 0;
        }
    }

    class ShowLotListHandler extends HttpJsonHandler {
        private ArrayList<Lot> lots;

        @Override
        public void handlerData(int code, JSONObject data){
            swipe_layout.setRefreshing(false);
            listview.stopLoadMore();
            Utility.dismissLoadingDialog();
            switch (code){
                case 0:
                    if(number == 1)  {
                        list.clear();
                    }

                    try {
                        int count = Integer.parseInt(data.getString("count"));
                        int size = Integer.parseInt(data.getString("size"));

                        if (number*size >= count ) {
                            listview.setPullLoadEnable(false);
                        } else {
                            number ++;
                        }

                        lots = JsonApi.getLotList(data);
                        list.addAll(lots);
                        Collections.sort(list, new LotComparator());
                        adapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
    }


}
