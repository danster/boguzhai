package com.boguzhai.activity.mainpage;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.boguzhai.logic.dao.MyInt;
import com.boguzhai.logic.dao.Session;
import com.boguzhai.logic.thread.HttpJsonHandler;
import com.boguzhai.logic.thread.HttpPostRunnable;
import com.boguzhai.logic.thread.ShowLotListHandler;
import com.boguzhai.logic.thread.Tasks;
import com.boguzhai.logic.utils.HttpClient;
import com.boguzhai.logic.utils.JsonApi;
import com.boguzhai.logic.utils.Utility;
import com.boguzhai.logic.view.XListView;

import org.json.JSONObject;

import java.util.ArrayList;

public class HomeFragment extends Fragment implements XListView.IXListViewListener, SwipeRefreshLayout.OnRefreshListener {
    private static String TAG = "HomeFragment";
    private View view;
    private MainActivity context;
    private int adsCount = 0;

    // 拍卖会展示
    private ArrayList<Auction> auctionList;
    private ViewGroup viewGroup;
    private ViewPager viewPager;
    private TextView viewInfo;
    private int currentIndex = 0;
    private ImageView[] mImageViews, tips;
    private ArrayList<String> sessionNames = new ArrayList<String>();

    // 拍品展示
    private ArrayList<Lot> lotList;
    private LotListAdapter adapter;

    // 分页信息必备条件
    private SwipeRefreshLayout swipe_layout;
    private MyInt order = new MyInt(1);

    private int index_i, index_j;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.main_fg_home, null);
        context = (MainActivity)getActivity();
        init();
        return view;
    }

    private void init(){
        pullDynamicInfo();
        showListView();
    }

    // 从网络获取首页的拍卖会信息
    private void pullDynamicInfo(){
        HttpClient conn = new HttpClient();
        conn.setParam("status", "");
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
                //container.removeView(mImageViews[position % mImageViews.length]);
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
                viewInfo.setText(sessionNames.get(position % adsCount));
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

    // 刷新展示首页拍品列表
    public void showListView(){
        XListView listview = (XListView) view.findViewById(R.id.lotlist);
        Variable.currentListview = listview;
        listview.setPullLoadEnable(true);
        listview.setPullRefreshEnable(false);
        listview.setXListViewListener(this);

        lotList = new ArrayList<Lot>();
        adapter = new LotListAdapter(context, lotList, true);
        listview.setAdapter(adapter);


        // 支持下拉刷新的布局，设置下拉监听事件，重写onRefresh()方法
        swipe_layout = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
        swipe_layout.setColorSchemeResources(R.color.gold);
        swipe_layout.setOnRefreshListener(this);
        Variable.currentRefresh = swipe_layout;


        this.order.value = 1;
        HttpClient conn = new HttpClient();
        conn.setParam("number", this.order.value + "");
        conn.setUrl(Constant.url + "pMainAction!getHomeAuctionMainList.htm");
        new Thread(new HttpPostRunnable(conn, new ShowLotListHandler(lotList, adapter, order))).start();
    }

    @Override
    public void onRefresh() {
        swipe_layout.setRefreshing(true);
        this.order.value = 1;
        HttpClient conn = new HttpClient();
        conn.setParam("number", this.order.value + "");
        conn.setUrl(Constant.url + "pMainAction!getHomeAuctionMainList.htm");
        new Thread(new HttpPostRunnable(conn, new ShowLotListHandler(lotList, adapter, order))).start();
    }

    @Override
    public void onLoadMore() {
        HttpClient conn = new HttpClient();
        conn.setParam("number", this.order.value + "");
        conn.setUrl(Constant.url + "pMainAction!getHomeAuctionMainList.htm");
        new Thread(new HttpPostRunnable(conn,new ShowLotListHandler(lotList, adapter, order))).start();
    }


    public class AuctionListHandler extends HttpJsonHandler {
        @Override
        public void handlerData(int code, JSONObject data){
            switch (code){
                case 0:
                    auctionList = JsonApi.getAuctionList(data);

                    // 查询含有专场的拍卖会数目（最大为4个）
                    adsCount = 0;
                    for (int i = 0; i < auctionList.size(); i++) {
                        adsCount += auctionList.get(i).sessionList.size() > 0 ? auctionList.get(i).sessionList.size() : 0;
                        if( adsCount >= 4) {
                            adsCount = 4;
                            break;
                        }
                    }

                    // 专场数最小为2
                    if(adsCount < 2){
                        break;
                    }

                    sessionNames.clear();

                    // 将静态图片ID装载到数组中
                    mImageViews = new ImageView[adsCount];
                    int count = 0;
                    for (index_i = 0; index_i < auctionList.size() && count < adsCount; index_i++) {
                        for(index_j = 0; index_j < auctionList.get(index_i).sessionList.size() && count < adsCount; index_j++) {
                            mImageViews[count] = new ImageView(getActivity());
                            mImageViews[count].setImageResource(R.drawable.default_image);

                            // 加载拍卖会专场图片和显示名称
                            Auction auction = auctionList.get(index_i);
                            Session session = auction.sessionList.get(index_j);

                            // 设置广告图片的点击响应
                            mImageViews[count].setOnClickListener(new ImageListener(auction, session));
                            Tasks.showImage(session.imageUrl, mImageViews[count], 4);
                            sessionNames.add(auction.name + ": " +session.name);
                            count ++ ;
                        }
                    }
                    showSessionAds();
                    break;
                default:
                    break;
            }
        }
    }

    class ImageListener implements View.OnClickListener{
        private Auction auction;
        private Session session;
        public ImageListener(Auction auction, Session session){
            this.auction = auction;
            this.session = session;
        }

        @Override
        public void onClick(View v) {
            Variable.currentAuction = this.auction;
            Variable.currentSession = this.session;
            Utility.gotoSession();
        }
    }

}