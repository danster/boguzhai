package com.boguzhai.activity.me.order;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.boguzhai.R;
import com.boguzhai.activity.base.Variable;
import com.boguzhai.logic.gaobo.PayOrder;
import com.boguzhai.logic.view.XListViewForScrollView;
import com.boguzhai.logic.widget.ListViewForScrollView;

import java.util.ArrayList;


public class PayOrderAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<PayOrder> payOrders;
    private LayoutInflater inflater;

    private XListViewForScrollView listView;

    private final class ViewHolder {
        TextView tv_me_pay_order_no,
                tv_me_pay_order_time,
                tv_me_pay_order_status,
                tv_me_pay_order_expressPrice,
                tv_me_pay_order_preferential,
                tv_me_pay_order_realPayPrice;
        ListViewForScrollView listView;
        OrderLotAdapter orderLotAdapter;

    }

    public PayOrderAdapter(Context context, ArrayList<PayOrder> payOrders, XListViewForScrollView listView) {
        this.listView = listView;
        inflater = LayoutInflater.from(context);
        mContext = context;
        this.payOrders = payOrders;
    }

    ;

    @Override
    public int getCount() {
        return payOrders.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view;
        final ViewHolder holder;
        if (convertView == null) {
            view = inflater.inflate(R.layout.item_list_pay_order, null);
            holder = new ViewHolder();
            holder.tv_me_pay_order_no = (TextView) view.findViewById(R.id.tv_me_pay_order_no);
            holder.tv_me_pay_order_time = (TextView) view.findViewById(R.id.tv_me_pay_order_time);
            holder.tv_me_pay_order_status = (TextView) view.findViewById(R.id.tv_me_pay_order_status);
            holder.tv_me_pay_order_realPayPrice = (TextView) view.findViewById(R.id.tv_me_pay_order_realPayPrice);
            holder.tv_me_pay_order_preferential = (TextView) view.findViewById(R.id.tv_me_pay_order_preferential);
            holder.tv_me_pay_order_expressPrice = (TextView) view.findViewById(R.id.tv_me_pay_order_expressPrice);
            holder.listView = (ListViewForScrollView) view.findViewById(R.id.lv_pay_order_lot_list);
            holder.orderLotAdapter = new OrderLotAdapter(mContext, payOrders.get(position).orderLots);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }

        holder.tv_me_pay_order_no.setText(payOrders.get(position).orderId);
        holder.tv_me_pay_order_time.setText(payOrders.get(position).orderTime);
        holder.tv_me_pay_order_status.setText(payOrders.get(position).orderStatus);
        holder.tv_me_pay_order_realPayPrice.setText("¥ " + payOrders.get(position).realPayPrice);
        holder.tv_me_pay_order_preferential.setText("¥ " + payOrders.get(position).preferential);
        holder.tv_me_pay_order_expressPrice.setText("¥ " + payOrders.get(position).expressPrice);

        holder.tv_me_pay_order_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转订单详情
                Variable.payOrder = payOrders.get(position);
                Intent intent = new Intent(mContext, PayOrderDetailsActivity.class);
                mContext.startActivity(intent);
            }
        });



        holder.listView.setAdapter(holder.orderLotAdapter);

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
