package com.boguzhai.activity.me.collect;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.boguzhai.R;
import com.boguzhai.logic.dao.CollectionLot;
import com.boguzhai.logic.thread.Tasks;

import java.util.List;

/**
 * Created by bobo on 15/5/2.
 */


final class ViewHolder {
    public  TextView my_collection_lot_name;//拍品名
    public  TextView my_collection_lot_no;//拍品编号
    public  TextView my_collection_lot_startprice;//起拍价
    public  TextView my_collection_lot_status;//拍品状态
    public  TextView my_collection_lot_appraisal;//预估价
    public  TextView my_collection_lot_dealprice;//成交价
    public  TextView tv_my_collection_lot_dealprice;//"成交价:"
    public  TextView my_collection_biddingTime;//"开拍时间"
    public  TextView my_collection_forBidding;//是否参拍
    public ImageView my_collection_lot_image;//图片
}


public class MyCollectionAdapter extends BaseAdapter{

    public List<CollectionLot> lots;
    private Context mContext;
    private LayoutInflater inflater;

    MyCollectionAdapter(Context context, List<CollectionLot> lots) {
        this.mContext = context;
        this.lots = lots;
        inflater = LayoutInflater.from(mContext);
    }


    @Override
    public int getCount() {

        return lots.size();
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        View view;
        if (convertView == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.me_my_collection_item, null);
            holder.my_collection_lot_name = (TextView) view.findViewById(R.id.my_collection_lot_name);
            holder.my_collection_lot_status = (TextView) view.findViewById(R.id.my_collection_lot_status);
            holder.my_collection_lot_no = (TextView) view.findViewById(R.id.my_collection_lot_no);
            holder.my_collection_lot_appraisal = (TextView) view.findViewById(R.id.my_collection_lot_appraisal);
            holder.my_collection_lot_startprice = (TextView) view.findViewById(R.id.my_collection_lot_startprice);
            holder.my_collection_lot_dealprice = (TextView) view.findViewById(R.id.my_collection_lot_dealprice);
            holder.my_collection_forBidding = (TextView) view.findViewById(R.id.my_collection_forBidding);
            holder.tv_my_collection_lot_dealprice = (TextView) view.findViewById(R.id.tv_my_collection_lot_dealprice);
            holder.my_collection_biddingTime = (TextView) view.findViewById(R.id.my_collection_biddingTime);
            holder.my_collection_lot_image = (ImageView) view.findViewById(R.id.my_collection_lot_image);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }

        holder.my_collection_lot_name.setText(lots.get(position).name);
        holder.my_collection_lot_no.setText(String.format("%05d" ,Integer.parseInt(lots.get(position).no)));
        holder.my_collection_lot_startprice.setText(String.valueOf(lots.get(position).startPrice));
        holder.my_collection_lot_status.setText(lots.get(position).status);
        holder.my_collection_lot_appraisal.setText(lots.get(position).apprisal);
        holder.my_collection_biddingTime.setText(lots.get(position).biddingTime);


        holder.my_collection_lot_dealprice.setVisibility(View.VISIBLE);
        holder.tv_my_collection_lot_dealprice.setVisibility(View.VISIBLE);

        if("已成交".equals(lots.get(position).status)) {//已成交的拍品需要显示成交价
            holder.my_collection_lot_dealprice.setText(String.valueOf(lots.get(position).dealPrice));
        }else {
            holder.my_collection_lot_dealprice.setVisibility(View.INVISIBLE);
            holder.tv_my_collection_lot_dealprice.setVisibility(View.INVISIBLE);
        }


        if(lots.get(position).forBidding == 1) {
            holder.my_collection_forBidding.setText("已参拍");
        }else {
            holder.my_collection_forBidding.setText("未参拍");
        }


//        Tasks.showBigImage(lots.get(position).imageUrl, holder.my_collection_lot_image, 4);
        holder.my_collection_lot_image.setImageBitmap(lots.get(position).image);

        // 点击缩略图时显示大图
        Tasks.showBigImage(lots.get(position).imageUrl, holder.my_collection_lot_image, 1);


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到该拍品信息界面
                Toast.makeText(mContext, "跳转到" + lots.get(position).name, Toast.LENGTH_SHORT).show();
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
