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

    MyProxyAdapter(Context context, List<ProxyLot> lots) {
        this.mContext = context;
        this.lots = lots;
        inflater = LayoutInflater.from(mContext);
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
        holder.tv_my_proxy_lot_no.setText(String.valueOf(lots.get(position).No));
        holder.tv_my_proxy_appraisal.setText(String.valueOf(lots.get(position).apprisal1) + "-" + String.valueOf(lots.get(position).apprisal2));
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
