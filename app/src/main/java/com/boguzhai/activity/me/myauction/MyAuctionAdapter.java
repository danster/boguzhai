package com.boguzhai.activity.me.myauction;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.boguzhai.R;
import com.boguzhai.activity.base.Variable;
import com.boguzhai.activity.me.proxy.ProxyPricingActivity;
import com.boguzhai.logic.gaobo.MyAuction;

import java.util.List;

/**
 * Created by bobo on 15/4/23.
 */
final class ViewHolder {
    public  TextView tv_my_auction_status;
    public  TextView tv_my_auction_name;
    public  TextView tv_my_auction_type;
    public  TextView tv_my_auction_date;
    public  TextView tv_my_auction_deposit;
    public  TextView tv_my_auction_set_deposit;
}



public class MyAuctionAdapter extends BaseAdapter {

    private final String TAG = "MyAuctionAdapter";

    private List<MyAuction> myAuctions;//要展示的拍卖会信息
    private Context mContext;
    private LayoutInflater inflater;

    public MyAuctionAdapter(Context context, List<MyAuction> myAuctions) {
        this.mContext = context;
        this.inflater = LayoutInflater.from(mContext);
        this.myAuctions = myAuctions;
    }

    @Override
    public int getCount() {
        return myAuctions.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.me_my_auction_item, null);
            holder.tv_my_auction_status = (TextView) convertView.findViewById(R.id.tv_my_auction_status);
            holder.tv_my_auction_name = (TextView) convertView.findViewById(R.id.tv_my_auction_name);
            holder.tv_my_auction_type = (TextView) convertView.findViewById(R.id.tv_my_auction_type);
            holder.tv_my_auction_date = (TextView) convertView.findViewById(R.id.tv_my_auction_date);
            holder.tv_my_auction_deposit = (TextView) convertView.findViewById(R.id.tv_my_auction_deposit);
            holder.tv_my_auction_set_deposit = (TextView) convertView.findViewById(R.id.tv_my_auction_set_deposit);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tv_my_auction_status.setText(myAuctions.get(position).status);
        holder.tv_my_auction_name.setText(myAuctions.get(position).name);
        holder.tv_my_auction_type.setText(myAuctions.get(position).type);
        holder.tv_my_auction_date.setText(myAuctions.get(position).auctionTime);
        holder.tv_my_auction_deposit.setText(String.valueOf(myAuctions.get(position).deposit));
        holder.tv_my_auction_set_deposit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "设置/修改代理出价");
                Intent intent = new Intent(mContext, ProxyPricingActivity.class);
                intent.putExtra("auctionName", myAuctions.get(position).name);
                mContext.startActivity(intent);
            }
        });

        convertView.setOnClickListener(new MyOnClickListener(position));
        return convertView;
    }

    protected class MyOnClickListener implements View.OnClickListener{
        private int position;
        public MyOnClickListener(int position){
            this.position = position;
        }
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mContext, MyAuctionSessionActivity.class);
            intent.putExtra("auctionId", myAuctions.get(position).id);
            Variable.currentAuction = myAuctions.get(position);
            mContext.startActivity(intent);
        }
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
