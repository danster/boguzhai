package com.boguzhai.activity.me.collect;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.boguzhai.R;
import com.boguzhai.activity.base.Variable;
import com.boguzhai.activity.photowallfalls.ImageDetailsActivity;
import com.boguzhai.logic.dao.CollectionLot;
import com.boguzhai.logic.thread.LoadImageTask;
import com.boguzhai.logic.utils.Utility;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

/**
 * Created by bobo on 15/5/2.
 */


final class ViewHolder {
    public TextView my_collection_lot_name;//拍品名
    public TextView my_collection_lot_no;//拍品编号
    public TextView my_collection_lot_startprice;//起拍价
    public TextView my_collection_lot_status;//拍品状态
    public TextView my_collection_lot_appraisal;//预估价
    public TextView my_collection_lot_dealprice;//成交价
    public TextView tv_my_collection_lot_dealprice;//"成交价:"
    public TextView my_collection_biddingTime;//"开拍时间"
    public TextView my_collection_forBidding;//是否参拍
    public ImageView my_collection_lot_image;//图片
}


public class MyCollectionAdapter extends BaseAdapter {

    public List<CollectionLot> lots;
    private Context mContext;
    private LayoutInflater inflater;
    private boolean isShowing = false;

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
        holder.my_collection_lot_no.setText(String.format("%05d", Integer.parseInt(lots.get(position).no)));
        holder.my_collection_lot_startprice.setText(String.valueOf(lots.get(position).startPrice));
        holder.my_collection_lot_status.setText(lots.get(position).status);
        holder.my_collection_lot_appraisal.setText(lots.get(position).apprisal);
        holder.my_collection_biddingTime.setText(lots.get(position).biddingTime);


        holder.my_collection_lot_dealprice.setVisibility(View.VISIBLE);
        holder.tv_my_collection_lot_dealprice.setVisibility(View.VISIBLE);

        if ("已成交".equals(lots.get(position).status)) {//已成交的拍品需要显示成交价
            holder.my_collection_lot_dealprice.setText(String.valueOf(lots.get(position).dealPrice));
        } else {
            holder.my_collection_lot_dealprice.setVisibility(View.INVISIBLE);
            holder.tv_my_collection_lot_dealprice.setVisibility(View.INVISIBLE);
        }


        if (lots.get(position).forBidding == 1) {
            holder.my_collection_forBidding.setText("已参拍");
        } else {
            holder.my_collection_forBidding.setText("未参拍");
        }


        new LoadImageTask(holder.my_collection_lot_image, 4).execute(lots.get(position).imageUrl);

        holder.my_collection_lot_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isShowing) {
                    isShowing = true;
                    new AsyncTask<Void, Void, Void>() {
                        Bitmap bmp = null;
                        int imageRatio = 1;
                        String imageUrl = lots.get(position).imageUrl;

                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();
                        }

                        @Override
                        protected void onCancelled() {
                            super.onCancelled();
                            isShowing = false;
                        }

                        @Override
                        protected Void doInBackground(Void... params) {
                            if (imageUrl.equals("")) {
                                return null;
                            }
                            try {
                                Log.i("AsyncTask", "image get: " + imageUrl);
                                BitmapFactory.Options options = new BitmapFactory.Options();
                                options.inJustDecodeBounds = false;
                                options.inSampleSize = imageRatio; // height, width 变为原来的ratio分之一
                                InputStream inputStream = new URL(imageUrl).openStream();
                                bmp = BitmapFactory.decodeStream(inputStream, null, options);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void result) {
                            if (bmp != null) {
                                Variable.currentBitmap = bmp;
                                Utility.gotoActivity(ImageDetailsActivity.class);
                                isShowing = false;
//                                Log.i("AsyncTask", "image get: succeed !");
//                                LayoutInflater inflater = LayoutInflater.from(Variable.currentActivity);
//                                View imgEntryView = inflater.inflate(R.layout.dialog_big_photo, null); // 加载自定义的布局文件
//                                ((ImageView) imgEntryView.findViewById(R.id.large_image)).setImageBitmap(bmp); // 设置图片
//                                final AlertDialog dialog = new AlertDialog.Builder(Variable.currentActivity).create();
//                                dialog.setView(imgEntryView); // 自定义dialog
//                                dialog.show();
//
//                                // 点击布局文件（也可以理解为点击大图）后关闭dialog，这里的dialog不需要按钮
//                                imgEntryView.setOnClickListener(new View.OnClickListener() {
//                                    public void onClick(View paramView) {
//                                        dialog.cancel();
//                                        isShowing = false;
//                                    }
//                                });
                            } else {
                                Log.i("AsyncTask", "image get: failed !");
                            }
                        }
                    }.execute();
                }
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
