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
import com.boguzhai.activity.base.Variable;
import com.boguzhai.activity.items.LotListAdapter;
import com.boguzhai.logic.dao.Auction;
import com.boguzhai.logic.dao.Lot;
import com.boguzhai.logic.thread.HttpGetRunnable;
import com.boguzhai.logic.thread.HttpJsonHandler;
import com.boguzhai.logic.thread.HttpPostRunnable;
import com.boguzhai.logic.thread.ShowImageHandler;
import com.boguzhai.logic.thread.ShowLotListHandler;
import com.boguzhai.logic.utils.HttpClient;
import com.boguzhai.logic.utils.JsonApi;
import com.boguzhai.logic.utils.Utility;
import com.boguzhai.logic.widget.ListViewForScrollView;

import org.json.JSONObject;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    private static String TAG = "HomeFragment";
    private View view;
    private MainActivity context;
    private int adsCount = 0;

    // 拍卖会展示
    private ArrayList<Auction> auctionList;
    private ViewGroup viewGroup;
    private ViewPager viewPager;
    private TextView viewInfo;
    private int currentIndex =0;
    private ImageView[] mImageViews, tips;

    // 拍品展示
    private ArrayList<Lot> lotList;
    private ListViewForScrollView listview;
    private LotListAdapter adapter;

    private int index_i;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.main_fg_home, null);
        context = (MainActivity)getActivity(); //getApplicationContext()
        init();
        return view;
    }

    private void init(){
        pullDynamicInfo();
        showListView();
    }

    // 从网络获取首页的拍卖会和拍品信息
    private void pullDynamicInfo(){
        HttpClient conn = new HttpClient();
        conn.setParam("status", "拍卖中");
        conn.setUrl(Constant.url + "pMainAction!getAuctionMainList.htm");
        new Thread(new HttpPostRunnable(conn,new AuctionListHandler())).start();

    }

    // 展示拍卖会专场广告位
    public void showSessionAds(){
        if(adsCount <= 0){
            return;
        }

        viewGroup = (ViewGroup) view.findViewById(R.id.viewGroup);
        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        viewInfo = (TextView)view.findViewById(R.id.viewInfo);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(20,20);
        layoutParams.leftMargin = 3;
        layoutParams.rightMargin = 3;

        // 将导航小图标加入到ViewGroup中
        tips = new ImageView[adsCount];
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
                viewInfo.setText(auctionList.get(position % adsCount).name);
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
    public void showListView(){
        listview = (ListViewForScrollView) view.findViewById(R.id.lotlist);
        lotList = new ArrayList<Lot>();

        adapter = new LotListAdapter(context, lotList, true);
        listview.setAdapter(adapter);

        HttpClient conn = new HttpClient();
        conn.setUrl(Constant.url+"pMainAction!getHomeAuctionMainList.htm");
        new Thread(new HttpPostRunnable(conn,new ShowLotListHandler(lotList, adapter))).start();
    }

    public class AuctionListHandler extends HttpJsonHandler {
        @Override
        public void handlerData(int code, JSONObject data){
            switch (code){
                case 0:
                    auctionList = JsonApi.getAuctionList(data);
                    adsCount = auctionList.size();

                    if (adsCount <= 0)
                        break;

                    // 将静态图片ID装载到数组中
                    mImageViews = new ImageView[adsCount];
                    for (int i = 0; i < mImageViews.length; i++) {
                        index_i = i;
                        mImageViews[i] = new ImageView(getActivity());
                        // 设置广告图片的点击响应
                        mImageViews[i].setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Variable.currentAuction = auctionList.get(index_i);
                                Variable.currentSession =  auctionList.get(index_i).sessionList.get(0);
                                Utility.gotoAuction(context, Variable.currentSession.status);
                            }
                        });
                        mImageViews[i].setBackgroundResource(R.drawable.default_image);
                        HttpClient conn = new HttpClient();
                        conn.setUrl( auctionList.get(i).sessionList.get(0).imageUrl );
                        new Thread(new HttpGetRunnable(conn, new ShowImageHandler(mImageViews[i]))).start();
                    }
                    showSessionAds();
                    break;
                default:
                    break;
            }
        }
    }


}