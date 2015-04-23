package com.boguzhai.activity.mainpage;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boguzhai.R;
import com.boguzhai.activity.base.Constant;
import com.boguzhai.activity.items.LotListAdapter;
import com.boguzhai.logic.dao.Lot;
import com.boguzhai.logic.dao.Session;
import com.boguzhai.logic.thread.HttpJsonHandler;
import com.boguzhai.logic.thread.HttpPostRunnable;
import com.boguzhai.logic.utils.HttpClient;
import com.boguzhai.logic.widget.ListViewForScrollView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    private static String TAG = "HomeFragment";
    private View view;
    private MainActivity context;

    private int adsCount =4;

    // 拍卖会展示
    private ArrayList<Session> sessionList;
    private ViewGroup viewGroup;
    private ViewPager viewPager;
    private TextView viewInfo;
    private int currentIndex =0;

    private ImageView[] mImageViews, tips;

    // 拍品展示
    private ArrayList<Lot> lotList;
    private ListViewForScrollView listview;
    private LotListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.main_fg_home, null);
        context = (MainActivity)getActivity(); //getApplicationContext()
        pullDynamicInfo();
        showSessionAds();
        showLotList();
        return view;
    }

    // 从网络获取首页的拍卖会和拍品信息
    private void pullDynamicInfo(){
        HttpClient conn_session = new HttpClient();
        conn_session.setUrl(Constant.url+"");
        new Thread(new HttpPostRunnable(conn_session, new MyHandler()));

        HttpClient conn_lot = new HttpClient();
        conn_lot.setUrl(Constant.url+"");
        new Thread(new HttpPostRunnable(conn_lot, new MyHandler()));
    }

    // 展示拍卖会专场广告位
    public void showSessionAds(){
        viewGroup = (ViewGroup) view.findViewById(R.id.viewGroup);
        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        viewInfo = (TextView)view.findViewById(R.id.viewInfo);

        mImageViews = new ImageView[adsCount];
        tips = new ImageView[adsCount];

        // 将静态图片ID装载到数组中
        for (int i = 0; i < mImageViews.length; i++) {
            mImageViews[i] = new ImageView(getActivity());
            mImageViews[i].setBackgroundResource(R.drawable.default_image);
        }

        // 将导航小图标加入到ViewGroup中
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(20,20);
        layoutParams.leftMargin = 3;
        layoutParams.rightMargin = 3;

        for (int i = 0; i < adsCount; i++) {
            tips[i] = new ImageView(getActivity());
            viewGroup.addView(tips[i], layoutParams);
        }

        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() { return Integer.MAX_VALUE;}
            @Override
            public boolean isViewFromObject(View view, Object object) { return view == object;}

            // PagerAdapter只缓存三张要显示的图片，如果滑动的图片超出了缓存的范围，就会调用这个方法，将图片销毁
            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(mImageViews[position % mImageViews.length]);
            }

            //载入图片进去，用当前的position 除以 图片数组长度取余数是关键
            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(mImageViews[position % mImageViews.length], 0);
                return mImageViews[position % mImageViews.length];
            }
        });

        // 设置监听，主要是设置点点的背景和专场名称
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener()  {
            @Override
            public void onPageScrollStateChanged(int arg0) {}
            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {}
            @Override
            public void onPageSelected(int position) {
                currentIndex=position;
                viewInfo.setText("2015拍卖会字画专场"+(position%adsCount));
                for (int i = 0; i < adsCount; i++) {
                    if (i == position % adsCount) {
                        tips[i].setBackgroundResource(R.drawable.circle_selected_little);
                    } else {
                        tips[i].setBackgroundResource(R.drawable.circle_unselected_little);
                    }
                }
            }
        });


        // 设置ViewPager的默认项, 如果想往左边滑动, index初始化要是大整数才行
        viewPager.setCurrentItem(1000* adsCount);

        // 设置ViewPager的滑动策略
        new Handler() {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                viewPager.setCurrentItem(currentIndex);
                currentIndex++;
                sendEmptyMessageDelayed(0, 3000);
            }
        }.sendEmptyMessageDelayed(0, 3000);
    }

    // 展示首页拍品列表
    public void showLotList(){
        listview = (ListViewForScrollView) view.findViewById(R.id.lotlist);
        lotList = new ArrayList<Lot>();

        adapter = new LotListAdapter(context, lotList, true);
        listview.setAdapter(adapter);
    }

    public class MyHandler extends HttpJsonHandler {
        @Override
        public void handlerData(int code, JSONObject data){
            try {
                switch (code){
                    case 0:
                        if(data.has("auctionInfoList")){
                            JSONArray ids = data.getJSONArray("auctionInfoList");
                            for(int i=0; i < ids.length(); ++i){
                            }
                        }
                        if(data.has("auctionSessionList")){
                            JSONArray ids = data.getJSONArray("auctionSessionList");
                            for(int i=0; i < ids.length(); ++i){
                            }
                        }
                    break;
                    default:
                    break;
                }
            }catch(JSONException ex) {
                context.toastMessage("网络数据错误");
            }
        }
    }


}