package com.boguzhai.activity.search;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import com.boguzhai.R;
import com.boguzhai.activity.base.BaseActivity;
import com.boguzhai.activity.items.LotListAdapter;
import com.boguzhai.logic.dao.Lot;
import com.boguzhai.logic.widget.ListViewForScrollView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SearchResultActivity extends BaseActivity {
    private static final String TAG = "SearchResultActivity";
    private ArrayList<Lot> list;
    private ListViewForScrollView listview;
    LotListAdapter adapter;


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

        showLotList();
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
                            sortLots();
                            dialog.dismiss();
                        }
                    }).setNegativeButton("取消", null).show();
                break;
            default:
                break;
        }
    }

    public void sortLots(){
        LotComparator comparator = new LotComparator();
        Collections.sort(list, comparator);
        adapter.notifyDataSetChanged();
    }

    class LotComparator implements Comparator<Lot>{
        public int compare(Lot l1, Lot l2) {
            switch (sortType){
                case 0:
                    if(!l1.name.equals(l2.name)){
                        return l1.name.compareTo(l2.name);
                    } else if(l1.id != l2.id){
                        return l1.id - l2.id;
                    }
                    break;
                case 1:
                    if(l1.No != l2.No){
                        return l1.No - l2.No;
                    } else if(l1.id != l2.id){
                        return l1.id - l2.id;
                    }
                    break;
                case 2:
                    if(l1.startPrice != l2.startPrice){
                        double gap = l1.startPrice - l2.startPrice;
                        return gap > 0 ? 1 : (gap < 0 ? -1 : 0);
                    } else if(l1.id != l2.id){
                        return l1.id - l2.id;
                    }
                    break;
                case 3:
                    if(l1.startPrice != l2.startPrice){
                        double gap = l2.startPrice - l1.startPrice;
                        return gap > 0 ? 1 : (gap < 0 ? -1 : 0);
                    } else if(l1.id != l2.id){
                        return l1.id - l2.id;
                    }
                    break;
                case 4:
                    if(l1.apprisal1 != l2.apprisal1){
                        double gap = l1.apprisal1 - l2.apprisal1;
                        return gap > 0 ? 1 : (gap < 0 ? -1 : 0);
                    } else if(l1.id != l2.id){
                        return l1.id - l2.id;
                    }
                    break;
                case 5:
                    if(l1.apprisal1 != l2.apprisal1){
                        double gap = l2.apprisal1 - l1.apprisal1;
                        return gap > 0 ? 1 : (gap < 0 ? -1 : 0);
                    } else if(l1.id != l2.id){
                        return l1.id - l2.id;
                    }
                    break;
                case 6:
                    if(l1.apprisal2 != l2.apprisal2){
                        double gap = l1.apprisal2 - l2.apprisal2;
                        return gap > 0 ? 1 : (gap < 0 ? -1 : 0);
                    } else if(l1.id != l2.id){
                        return l1.id - l2.id;
                    }
                    break;
                case 7:
                    if(l1.apprisal2 != l2.apprisal2){
                        double gap = l2.apprisal2 - l1.apprisal2;
                        return gap > 0 ? 1 : (gap < 0 ? -1 : 0);
                    } else if(l1.id != l2.id){
                        return l1.id - l2.id;
                    }
                    break;
            }

            return 0;
        }

    }

    // 展示专场的拍品列表
    public void showLotList(){
        listview = (ListViewForScrollView) findViewById(R.id.lotlist);
        list = new ArrayList<Lot>();

        for(int i=0; i<10; i++){
            Lot lot = new Lot();
            lot.name = "明朝景德镇花瓶 "+i;
            lot.id = i;
            lot.No = 100 - i;
            lot.apprisal1 = Math.random()*10000;
            lot.apprisal1 = Math.random()*20000;
            lot.startPrice = Math.random()*5000;
            list.add(lot);
        }

        adapter = new LotListAdapter(this, list);
        listview.setAdapter(adapter);
    }


}
