package com.boguzhai.activity.me.myauction;


import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.boguzhai.R;
import com.boguzhai.activity.me.proxy.ProxyPricingActivity;
import com.boguzhai.logic.gaobo.MyAuction;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class BaseFragment extends Fragment {

    private static final String TAG = "BaseFragment";

    public MyAuctionActivity context;
    public View view;//fragment对应布局
    public LayoutInflater inflater;
    public MyAuctionAdapter myAuctionAdapter;//适配器
    public ListView lv_my_auction;//布局中的listview
    public String[] status= {"预展中", "拍卖中", "已结束", "全部"};
    public String[] types= {"现场拍卖", "同步拍卖", "网络拍卖"};

    /**
     * fragment对应布局
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.inflater = inflater;
        view  = inflater.inflate(R.layout.me_myauction_fg, container, false);
        return view;
    }


    /**
     * 初始化数据
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = (MyAuctionActivity) getActivity();
        initData();
    }

    /**
     * 初始化适配器，子类直接传递需要展示的拍卖会信息集合
     * @param myAuctions 需要展示的拍卖会信息集合
     * @return 适配器
     */
    public void initAdapter(List<MyAuction> myAuctions) {
        myAuctionAdapter = new MyAuctionAdapter(myAuctions);
    }


    /**
     * 子类需要复写此方法,用于初始化数据
     */
    public abstract  void initData();



    public static class ViewHolder {
        public static TextView tv_my_auction_status;
        public static TextView tv_my_auction_name;
        public static TextView tv_my_auction_type;
        public static TextView tv_my_auction_date;
        public static TextView tv_my_auction_deposit;
        public static TextView tv_my_auction_set_deposit;
    }
    /**
     * 适配器
     */
    public class MyAuctionAdapter extends BaseAdapter {

        private List<MyAuction> myAuctions;//要展示的拍卖会信息

        private MyAuctionAdapter(List<MyAuction> myAuctions) {
            this.myAuctions = myAuctions;
        }

        @Override
        public int getCount() {
            return myAuctions.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            View view = null;
            if(convertView == null){
                holder = new ViewHolder();
                view = inflater.inflate(R.layout.me_my_auction_item, null);
                holder.tv_my_auction_status = (TextView) view.findViewById(R.id.tv_my_auction_status);
                holder.tv_my_auction_name = (TextView) view.findViewById(R.id.tv_my_auction_name);
                holder.tv_my_auction_type = (TextView) view.findViewById(R.id.tv_my_auction_type);
                holder.tv_my_auction_date = (TextView) view.findViewById(R.id.tv_my_auction_date);
                holder.tv_my_auction_deposit = (TextView) view.findViewById(R.id.tv_my_auction_deposit);
                holder.tv_my_auction_set_deposit = (TextView) view.findViewById(R.id.tv_my_auction_set_deposit);
                view.setTag(holder);
            }else {
                view = convertView;
                holder = (ViewHolder) view.getTag();

            }
            holder.tv_my_auction_status.setText(status[Integer.parseInt(myAuctions.get(position).status) - 1]);
            holder.tv_my_auction_name.setText(myAuctions.get(position).name);
            holder.tv_my_auction_type.setText(types[Integer.parseInt(myAuctions.get(position).type) - 1]);
            holder.tv_my_auction_date.setText(myAuctions.get(position).auctionTime);
            holder.tv_my_auction_deposit.setText(String.valueOf(myAuctions.get(position).deposit));
            holder.tv_my_auction_set_deposit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG, "设置/修改代理出价");
                    startActivity(new Intent(context, ProxyPricingActivity.class));
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



}
