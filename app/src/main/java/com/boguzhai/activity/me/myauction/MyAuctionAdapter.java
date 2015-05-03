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
import com.boguzhai.activity.me.proxy.ProxyPricingActivity;
import com.boguzhai.activity.me.proxy.SetProxyPricingActivity;
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
    private final int baseCount = 5;//设置每页显示5个
    private int currentCount = 0;//当前需要展示的个数

    public MyAuctionAdapter(Context context, List<MyAuction> myAuctions) {
        this.mContext = context;
        this.inflater = LayoutInflater.from(mContext);
        this.myAuctions = myAuctions;
    }



    /**
     * 设置索引，根据索引分页显示
     * @param index 索引
     */
    public void setPageIndex(int index) {
        if(myAuctions.size() >= baseCount*(index + 1)) {
            currentCount = baseCount*(index + 1);
        }else {
            currentCount = myAuctions.size();
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
        boolean result = ((currentCount == myAuctions.size()) ? true : false);
        return result;
    }


    @Override
    public int getCount() {
        return currentCount;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View view;
        if (convertView == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.me_my_auction_item, null);
            holder.tv_my_auction_status = (TextView) view.findViewById(R.id.tv_my_auction_status);
            holder.tv_my_auction_name = (TextView) view.findViewById(R.id.tv_my_auction_name);
            holder.tv_my_auction_type = (TextView) view.findViewById(R.id.tv_my_auction_type);
            holder.tv_my_auction_date = (TextView) view.findViewById(R.id.tv_my_auction_date);
            holder.tv_my_auction_deposit = (TextView) view.findViewById(R.id.tv_my_auction_deposit);
            holder.tv_my_auction_set_deposit = (TextView) view.findViewById(R.id.tv_my_auction_set_deposit);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();

        }
        holder.tv_my_auction_status.setText("[" + myAuctions.get(position).status + "] ");
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
