package com.boguzhai.activity.me.proxy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.boguzhai.R;
import com.boguzhai.logic.dao.ProxyLot;

import java.util.List;

/**
 * Created by bobo on 15/4/21.
 */

final class ViewHolder {
    TextView tv_my_proxy_lot_name;
    TextView tv_my_proxy_auction_name;
    TextView tv_my_proxy_session_name;
    TextView tv_my_proxy_lot_no;
    TextView tv_my_proxy_appraisal;
    TextView tv_my_proxy_startprice;
    TextView tv_my_proxy_price;
}


/**
 * 我的代理，适配器
 */
class MyProxyAdapter extends BaseAdapter {

    public List<ProxyLot> lots;
    private Context mContext;
    private LayoutInflater inflater;
    private final int baseCount = 5;//设置每页显示5个
    private int currentCount = 0;//当前需要展示的个数


    MyProxyAdapter(Context context, List<ProxyLot> lots) {
        this.mContext = context;
        this.lots = lots;
        inflater = LayoutInflater.from(mContext);
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

        return lots.size();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View view;
        if (convertView == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.me_my_proxy_item, null);
            holder.tv_my_proxy_lot_name = (TextView) view.findViewById(R.id.tv_my_proxy_lot_name);
            holder.tv_my_proxy_auction_name = (TextView) view.findViewById(R.id.tv_my_proxy_auction_name);
            holder.tv_my_proxy_session_name = (TextView) view.findViewById(R.id.tv_my_proxy_session_name);
            holder.tv_my_proxy_lot_no = (TextView) view.findViewById(R.id.tv_my_proxy_lot_no);
            holder.tv_my_proxy_appraisal = (TextView) view.findViewById(R.id.tv_my_proxy_appraisal);
            holder.tv_my_proxy_startprice = (TextView) view.findViewById(R.id.tv_my_proxy_startprice);
            holder.tv_my_proxy_price = (TextView) view.findViewById(R.id.tv_my_proxy_price);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }
        holder.tv_my_proxy_lot_name.setText(lots.get(position).name);
        holder.tv_my_proxy_auction_name.setText(lots.get(position).auctionId);
        holder.tv_my_proxy_session_name.setText(lots.get(position).sessionId);
        holder.tv_my_proxy_lot_no.setText(lots.get(position).no);
        holder.tv_my_proxy_appraisal.setText(String.valueOf(lots.get(position).appraisal1) + "-" + String.valueOf(lots.get(position).appraisal2));
        holder.tv_my_proxy_startprice.setText(String.valueOf(lots.get(position).startPrice));
        holder.tv_my_proxy_price.setText(lots.get(position).proxyPrice);


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
