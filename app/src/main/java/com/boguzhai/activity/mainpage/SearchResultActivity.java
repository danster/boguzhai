package com.boguzhai.activity.mainpage;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import com.boguzhai.R;
import com.boguzhai.activity.base.BaseActivity;
import com.boguzhai.activity.items.LotListAdapter;
import com.boguzhai.logic.dao.Lot;
import com.boguzhai.logic.dao.MyInt;
import com.boguzhai.logic.thread.HttpJsonHandler;
import com.boguzhai.logic.thread.HttpPostRunnable;
import com.boguzhai.logic.utils.HttpClient;
import com.boguzhai.logic.utils.JsonApi;
import com.boguzhai.logic.utils.Utility;
import com.boguzhai.logic.view.XListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.Collator;
import java.text.RuleBasedCollator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

public class SearchResultActivity extends BaseActivity implements XListView.IXListViewListener, SwipeRefreshLayout.OnRefreshListener{
    private static final String TAG = "SearchResultActivity";
    private ArrayList<Lot> list;
    private XListView listview;
    private LotListAdapter adapter;
    private String searchUrl=null;

    // 分页信息必备条件
    private SwipeRefreshLayout swipe_layout;
    private MyInt order = new MyInt(1);

    private String[] sortTypes = {"按拍品名称","按图录号",
                                  "按拍品起拍价升序", "按拍品起拍价降序",
                                  "按拍品估价最低值升序", "按拍品估价最低价降序",
                                  "按拍品估价最高值升序", "按拍品估价最高值降序" };
    private int sortType=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLinearView(R.layout.main_fg_search_result);
        title.setText("搜索结果");
        title_right.setText("排序");
        title_right.setVisibility(View.VISIBLE);

        init();
    }

    private void init(){
        listview = (XListView) findViewById(R.id.lotlist);
        listview.setPullLoadEnable(true);
        listview.setPullRefreshEnable(false);
        listview.setXListViewListener(this);

        list = new ArrayList<Lot>();
        adapter = new LotListAdapter(this, list);
        listview.setAdapter(adapter);

        swipe_layout = (SwipeRefreshLayout) findViewById(R.id.refresh);
        swipe_layout.setColorSchemeResources(R.color.gold);
        swipe_layout.setOnRefreshListener(this);

        searchUrl=getIntent().getStringExtra("url");

        HttpClient conn = new HttpClient();
        conn.setUrl(searchUrl + "&number=1");
        new Thread(new HttpPostRunnable(conn,new ShowLotListHandler())).start();
    }

    @Override
    public void onRefresh() {
        order.value = 1;
        swipe_layout.setRefreshing(true);
        HttpClient conn = new HttpClient();
        conn.setUrl(searchUrl + "&number=" + order.value);
        new Thread(new HttpPostRunnable(conn,new ShowLotListHandler())).start();
    }

    @Override
    public void onLoadMore() {
        HttpClient conn = new HttpClient();
        conn.setUrl(searchUrl + "&number=" + order.value);
        new Thread(new HttpPostRunnable(conn,new ShowLotListHandler())).start();
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

    // 拍品排序器
    public class LotComparator implements Comparator<Lot> {
        public int compare(Lot l1, Lot l2) {
            RuleBasedCollator collator = (RuleBasedCollator) Collator.getInstance(Locale.CHINA);
            switch (sortType){
                case 0:
                    if(!l1.name.equals(l2.name)){
                        return collator.compare(l1.name, l2.name);
                    } else {
                        return l1.id.compareTo(l2.id);
                    }
                case 1:
                    if(!l1.no.equals(l2.no)){
                        return Integer.parseInt(l1.no) - Integer.parseInt(l2.no);
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

            switch (code){
                case 0:
                    if(order.value == -1) {
                        Utility.toastMessage("已无更多信息");
                        break;
                    } else if(order.value == 1)  {
                        list.clear();
                    }

                    try {
                        int count = Integer.parseInt(data.getString("count"));
                        int size = Integer.parseInt(data.getString("size"));

                        if ((order.value-1)*size == count ) {
                            order.value = -1;
                            Utility.toastMessage("已无更多信息");
                            break;
                        } else if (order.value*size > count ) {
                            order.value = -1;
                        } else {
                            order.value ++;
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
