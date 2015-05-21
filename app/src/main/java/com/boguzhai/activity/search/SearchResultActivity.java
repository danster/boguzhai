package com.boguzhai.activity.search;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import com.boguzhai.R;
import com.boguzhai.activity.base.BaseActivity;
import com.boguzhai.activity.base.Variable;
import com.boguzhai.activity.items.LotListAdapter;
import com.boguzhai.logic.dao.Lot;
import com.boguzhai.logic.dao.MyInt;
import com.boguzhai.logic.thread.HttpPostRunnable;
import com.boguzhai.logic.thread.ShowLotListHandler;
import com.boguzhai.logic.utils.HttpClient;
import com.boguzhai.logic.view.XListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SearchResultActivity extends BaseActivity implements XListView.IXListViewListener, SwipeRefreshLayout.OnRefreshListener{
    private static final String TAG = "SearchResultActivity";
    private ArrayList<Lot> list;
    private XListView listview;
    private LotListAdapter adapter;
    private String searchUrl=null;

    // 分页信息必备条件
    private SwipeRefreshLayout swipe_layout;
    private MyInt order = new MyInt(1);

    private String[] sortTypes = {"按拍品名称","按拍品号",
                                  "按拍品起拍价升序", "按拍品起拍价降序",
                                  "按拍品估价最低值升序", "按拍品估价最低价降序",
                                  "按拍品估价最高值升序", "按拍品估价最高值降序" };
    private int sortType=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLinearView(R.layout.search_result);
        title.setText("搜索结果");
        title_right.setText("排序");
        title_right.setVisibility(View.VISIBLE);

        init();
    }

    private void init(){
        listview = (XListView) findViewById(R.id.lotlist);
        list = new ArrayList<Lot>();
        adapter = new LotListAdapter(this, list);
        listview.setAdapter(adapter);

        listview.setPullLoadEnable(true);
        listview.setPullRefreshEnable(false);
        listview.setXListViewListener(this);
        Variable.currentListview = listview;

        swipe_layout = (SwipeRefreshLayout) findViewById(R.id.refresh);
        swipe_layout.setColorSchemeResources(R.color.gold);
        swipe_layout.setOnRefreshListener(this);
        Variable.currentRefresh = swipe_layout;

        searchUrl=getIntent().getStringExtra("url");

        HttpClient conn = new HttpClient();
        order.value = 1;
        conn.setUrl(searchUrl + "&number=1");
        new Thread(new HttpPostRunnable(conn,new ShowLotListHandler(list, adapter, order))).start();
    }


    @Override
    public void onRefresh() {
        swipe_layout.setRefreshing(true);HttpClient conn = new HttpClient();
        order.value = 1;
        conn.setUrl(searchUrl + "&number=1");
        new Thread(new HttpPostRunnable(conn,new ShowLotListHandler(list, adapter, order))).start();
    }

    @Override
    public void onLoadMore() {
        swipe_layout.setRefreshing(true);HttpClient conn = new HttpClient();
        conn.setUrl(searchUrl + "&number=" + order.value);
        new Thread(new HttpPostRunnable(conn,new ShowLotListHandler(list, adapter, order))).start();
    }

    @Override
    public void onClick(View v){
        super.onClick(v);
        switch (v.getId()) {
            case R.id.title_right:
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
            default:
                break;
        }
    }

    class LotComparator implements Comparator<Lot>{
        public int compare(Lot l1, Lot l2) {
            switch (sortType){
                case 0:
                    if(!l1.name.equals(l2.name)){
                        return l1.name.compareTo(l2.name);
                    } else if(!l1.id.equals(l2.id)){
                        return l1.id.compareTo(l2.id);
                    }
                    break;
                case 1:
                    if(l1.no.equals(l2.no)){
                        return l1.no.compareTo(l2.no);
                    } else if(!l1.id.equals(l2.id)){
                        return l1.id.compareTo(l2.id);
                    }
                    break;
                case 2:
                    if(l1.startPrice != l2.startPrice){
                        double gap = l1.startPrice - l2.startPrice;
                        return gap > 0 ? 1 : (gap < 0 ? -1 : 0);
                    } else if(!l1.id.equals(l2.id)){
                        return l1.id.compareTo(l2.id);
                    }
                    break;
                case 3:
                    if(l1.startPrice != l2.startPrice){
                        double gap = l2.startPrice - l1.startPrice;
                        return gap > 0 ? 1 : (gap < 0 ? -1 : 0);
                    } else if(!l1.id.equals(l2.id)){
                        return l1.id.compareTo(l2.id);
                    }
                    break;
                case 4:
                    if(l1.appraisal1 != l2.appraisal1){
                        double gap = l1.appraisal1 - l2.appraisal1;
                        return gap > 0 ? 1 : (gap < 0 ? -1 : 0);
                    } else if(!l1.id.equals(l2.id)){
                        return l1.id.compareTo(l2.id);
                    }
                    break;
                case 5:
                    if(l1.appraisal1 != l2.appraisal1){
                        double gap = l2.appraisal1 - l1.appraisal1;
                        return gap > 0 ? 1 : (gap < 0 ? -1 : 0);
                    } else if(!l1.id.equals(l2.id)){
                        return l1.id.compareTo(l2.id);
                    }
                    break;
                case 6:
                    if(l1.appraisal2 != l2.appraisal2){
                        double gap = l1.appraisal2 - l2.appraisal2;
                        return gap > 0 ? 1 : (gap < 0 ? -1 : 0);
                    } else if(!l1.id.equals(l2.id)){
                        return l1.id.compareTo(l2.id);
                    }
                    break;
                case 7:
                    if(l1.appraisal2 != l2.appraisal2){
                        double gap = l2.appraisal2 - l1.appraisal2;
                        return gap > 0 ? 1 : (gap < 0 ? -1 : 0);
                    } else if(!l1.id.equals(l2.id)){
                        return l1.id.compareTo(l2.id);
                    }
                    break;
            }
            return 0;
        }
    }

}
