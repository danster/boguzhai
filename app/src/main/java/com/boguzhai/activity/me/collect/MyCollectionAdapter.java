package com.boguzhai.activity.me.collect;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.boguzhai.R;
import com.boguzhai.logic.dao.Lot;

import java.util.List;

/**
 * Created by bobo on 15/5/2.
 */


final class ViewHolder {
    public  TextView my_collection_lot_name;
    public  TextView my_collection_lot_status;
    public  TextView my_collection_lot_no;
    public  TextView my_collection_lot_appraisal;
    public  TextView my_collection_lot_startprice;
    public  TextView my_collection_lot_price;
    public  TextView tv_my_collection_lot_price;


}


public class MyCollectionAdapter extends BaseAdapter{


    public List<Lot> lots;
    private Context mContext;
    private LayoutInflater inflater;
    private final int baseCount = 5;//设置每页显示5个
    private int currentCount = 0;//当前需要展示的个数
    private int currentPageIndex = 0;//当前页索引

    MyCollectionAdapter(Context context, List<Lot> lots) {
        this.mContext = context;
        this.lots = lots;
        inflater = LayoutInflater.from(mContext);
    }

    public List<Lot> getLots() {
        return this.lots;
    }


    /**
     * 刷新当前页索引值
     */
    public void refreshCurrentPageIndex() {
        this.currentPageIndex++;
        setPageIndex(currentPageIndex);
    }
    /**
     * 设置索引，根据索引分页显示
     * @param index 索引
     */
    public void setPageIndex(int index) {
        if(lots.size() >= baseCount*(index + 1)) {
            currentCount = baseCount*(index + 1);
        }else {
            currentCount = lots.size();
        }
    }

    public void removeElem(int postion) {
        this.lots.remove(postion);
        currentCount--;
    }


    public int getCurrentCount() {
        return currentCount;
    }
    /**
     * 判断是否是最后一页
     * @return true 是  <br>  false 否
     */
    public boolean isLastPage() {
        boolean result = ((currentCount == lots.size()) ? true : false);
        return result;
    }

    @Override
    public int getCount() {

        return currentCount;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View view;
        if (convertView == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.me_my_collection_item, null);
            holder.my_collection_lot_name = (TextView) view.findViewById(R.id.my_collection_lot_name);
            holder.my_collection_lot_status = (TextView) view.findViewById(R.id.my_collection_lot_status);
            holder.my_collection_lot_no = (TextView) view.findViewById(R.id.my_collection_lot_no);
            holder.my_collection_lot_appraisal = (TextView) view.findViewById(R.id.my_collection_lot_appraisal);
            holder.my_collection_lot_startprice = (TextView) view.findViewById(R.id.my_collection_lot_startprice);
            holder.my_collection_lot_price = (TextView) view.findViewById(R.id.my_collection_lot_price);
            holder.tv_my_collection_lot_price = (TextView) view.findViewById(R.id.tv_my_collection_lot_price);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }
        holder.my_collection_lot_name.setText(lots.get(position).name);
        holder.my_collection_lot_status.setText(lots.get(position).status);
        holder.my_collection_lot_no.setText(String.valueOf(lots.get(position).No));
        holder.my_collection_lot_appraisal.setText(String.valueOf(lots.get(position).apprisal1) + "-" + String.valueOf(lots.get(position).apprisal2));
        holder.my_collection_lot_startprice.setText(String.valueOf(lots.get(position).startPrice));
        if("已成交".equals(lots.get(position).status)) {
            holder.my_collection_lot_price.setText(String.valueOf(lots.get(position).dealPrice));
            holder.my_collection_lot_price.setVisibility(View.VISIBLE);
            holder.tv_my_collection_lot_price.setText("成交价:");
            holder.tv_my_collection_lot_price.setVisibility(View.VISIBLE);
        }else if("拍卖中".equals(lots.get(position).status)) {
            holder.my_collection_lot_price.setText(String.valueOf(lots.get(position).currentPrice));
            holder.my_collection_lot_price.setVisibility(View.VISIBLE);
            holder.tv_my_collection_lot_price.setText("当前价:");
            holder.tv_my_collection_lot_price.setVisibility(View.VISIBLE);
        }else {
            holder.my_collection_lot_price.setVisibility(View.INVISIBLE);
            holder.tv_my_collection_lot_price.setVisibility(View.INVISIBLE);
        }


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
