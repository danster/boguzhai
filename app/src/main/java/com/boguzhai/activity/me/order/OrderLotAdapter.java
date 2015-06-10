package com.boguzhai.activity.me.order;

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
import com.boguzhai.activity.items.ImageDetailsActivity;
import com.boguzhai.logic.gaobo.OrderLot;
import com.boguzhai.logic.thread.LoadImageTask;
import com.boguzhai.logic.utils.Utility;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;


public class OrderLotAdapter extends BaseAdapter {


    private Context mContext;
    private ArrayList<OrderLot> lots;
    private LayoutInflater inflater;
    private boolean isShowing = false;

//    private XListViewForScrollView listView;
//
//    public OrderLotAdapter(Context context, ArrayList<OrderLot> lots, XListViewForScrollView listView) {
//        this.listView = listView;
//        inflater = LayoutInflater.from(context);
//        mContext = context;
//        this.lots = lots;
//    }

    public OrderLotAdapter(Context context, ArrayList<OrderLot> lots) {
        inflater = LayoutInflater.from(context);
        mContext = context;
        this.lots = lots;
    }

    private final class ViewHolder {
        TextView me_pay_order_lot_name,
                me_pay_order_lot_no,
                me_pay_order_lot_appraisal,
                me_pay_order_lot_deal_price,
                me_pay_order_lot_sum,
                me_pay_order_lot_commission;
        ImageView me_pay_order_lot_image;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View view;
        final ViewHolder holder;

        if (convertView == null) {
            view = inflater.inflate(R.layout.item_list_pay_order_lot, null);
            holder = new ViewHolder();
            holder.me_pay_order_lot_name = (TextView) view.findViewById(R.id.me_pay_order_lot_name);
            holder.me_pay_order_lot_no = (TextView) view.findViewById(R.id.me_pay_order_lot_no);
            holder.me_pay_order_lot_appraisal = (TextView) view.findViewById(R.id.me_pay_order_lot_appraisal);
            holder.me_pay_order_lot_deal_price = (TextView) view.findViewById(R.id.me_pay_order_lot_deal_price);
            holder.me_pay_order_lot_sum = (TextView) view.findViewById(R.id.me_pay_order_lot_sum);
            holder.me_pay_order_lot_commission = (TextView) view.findViewById(R.id.me_pay_order_lot_commission);
            holder.me_pay_order_lot_image = (ImageView) view.findViewById(R.id.me_pay_order_lot_image);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }
        holder.me_pay_order_lot_name.setText(lots.get(position).name);
        holder.me_pay_order_lot_no.setText(lots.get(position).no);
        holder.me_pay_order_lot_appraisal.setText(lots.get(position).appraisal);
        holder.me_pay_order_lot_deal_price.setText(String.valueOf(lots.get(position).dealPrice));
        holder.me_pay_order_lot_sum.setText(lots.get(position).sum);
        holder.me_pay_order_lot_commission.setText(lots.get(position).commission);



//        if (listView != null) {
//            listView.setOnScrollListener(new XListViewForScrollView.OnScrollListener() {
//                @Override
//                public void onScrollStateChanged(AbsListView view, int scrollState) {
//                    Log.i("scroll", "state:" + scrollState);
//
//                    if(scrollState != XListViewForScrollView.SCROLL_STATE_FLING) {
//                        new LoadImageTask(holder.me_pay_order_lot_image, 4).execute(lots.get(position).imageUrl);
//                    }
////                    switch (scrollState) {
////                        case XListViewForScrollView.SCROLL_STATE_IDLE:
////                            new LoadImageTask(holder.me_pay_order_lot_image, 4).execute(lots.get(position).imageUrl);
////                            break;
////                    }
//                }
//
//                @Override
//                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                }
//            });
//
//        } else {
//            new LoadImageTask(holder.me_pay_order_lot_image, 4).execute(lots.get(position).imageUrl);
//        }
        new LoadImageTask(holder.me_pay_order_lot_image, 4).execute(lots.get(position).imageUrl);


        holder.me_pay_order_lot_image.setOnClickListener(new View.OnClickListener() {
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
                                Log.i("AsyncTask", "image get: succeed !");

                                Variable.currentBitmap = bmp;
                                Utility.gotoActivity(ImageDetailsActivity.class);
                                isShowing = false;
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
    public int getCount() {
        return lots.size();
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
