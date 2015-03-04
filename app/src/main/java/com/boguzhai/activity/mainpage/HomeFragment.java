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
import android.widget.Toast;

import com.boguzhai.R;
import com.boguzhai.logic.dao.Lot;
import com.boguzhai.logic.utils.HttpRequestApi;
import com.boguzhai.logic.utils.ListViewForScrollView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    private static String TAG = "HomeFragment";
    private View view;
    private MainActivity context;

    ViewGroup viewGroup;
    ViewPager viewPager;
    int[] imgIdArray;
    ImageView[] mImageViews, tips;
    TextView viewInfo;
    int index=0;

    private ArrayList<Lot> list;
    private ListViewForScrollView listview;
    LotListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.main_fg_home, null);
        context = (MainActivity)getActivity(); //getApplicationContext()
        setDynamicAds();
        setDynamicLots();
        return view;
    }

    // 动态加载广告位
    public void setDynamicAds(){
        viewGroup = (ViewGroup) view.findViewById(R.id.viewGroup);
        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        viewInfo = (TextView)view.findViewById(R.id.viewInfo);
        imgIdArray = new int[] { R.drawable.image,
                                 R.drawable.image,
                                 R.drawable.image,
                                 R.drawable.image };

        // 将静态图片ID装载到数组中
        mImageViews = new ImageView[imgIdArray.length];
        for (int i = 0; i < mImageViews.length; i++) {
            ImageView imageView = new ImageView(getActivity());
            mImageViews[i] = imageView;
            imageView.setBackgroundResource(imgIdArray[i]);
        }

        // 将导航小图标加入到ViewGroup中
        tips = new ImageView[imgIdArray.length];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(60,60);
        layoutParams.leftMargin = 3;
        layoutParams.rightMargin = 3;

        for (int i = 0; i < tips.length; i++) {
            ImageView imageView = new ImageView(getActivity());
            tips[i] = imageView;
            if (i == 0) {
                tips[i].setBackgroundResource(R.drawable.circle_selected);
            } else {
                tips[i].setBackgroundResource(R.drawable.circle_unselected);
            }
            viewGroup.addView(imageView, layoutParams);
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

        // 设置监听，主要是设置点点的背景
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener()  {
            @Override
            public void onPageScrollStateChanged(int arg0) {}
            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {}

            @Override
            public void onPageSelected(int position) {
                index=position;
                int selectItems = position % mImageViews.length;
                for (int i = 0; i < tips.length; i++) {
                    if (i == selectItems) {
                        tips[i].setBackgroundResource(R.drawable.circle_selected);
                    } else {
                        tips[i].setBackgroundResource(R.drawable.circle_unselected);
                    }
                }
            }
        });

        // 设置ViewPager的默认项, 如果想往左边滑动, index初始化要是大整数才行
        viewPager.setCurrentItem(1000*mImageViews.length);

        // 设置ViewPager的滑动策略
        new Handler() {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                viewPager.setCurrentItem(index);
                index++;
                sendEmptyMessageDelayed(0, 3000);
            }
        }.sendEmptyMessageDelayed(0, 3000);


    }

    // 动态加载首页拍品
    public void setDynamicLots(){
        listview = (ListViewForScrollView) view.findViewById(R.id.lotlist);
        list = new ArrayList<Lot>();

        for(int i=0; i<2; i++){
            Lot lot = new Lot();
            list.add(lot);
        }

        adapter = new LotListAdapter(context, list);
        listview.setAdapter(adapter);

        HttpRequestApi conn = new HttpRequestApi();
        conn.addParam("m", "getMainLotList");
        conn.setUrl("http://www.boguzhai.com/api.jhtml");
        //new Thread(new HttpPostRunnable(conn,new MyHandler())).start();

    }

    public class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 1){
                try {
                    JSONObject result = new JSONObject((String)msg.obj);
                    int code = Integer.parseInt(result.getString("code"));
                    switch(code){
                        case 0:
                            JSONObject data = result.getJSONObject("data");
                            Lot lot = new Lot();
                            list.add(lot);
                            adapter.notifyDataSetChanged();
                            break;
                        case 1:
                            context.tips.setMessage("服务器出错, 获取信息失败").create().show();
                            break;
                        case -1:
                            break;
                        default:
                            break;
                    }
                } catch (JSONException ex) {
                    context.tips.setMessage("服务器出错").create().show();
                }
            }else if(msg.what == 0 ){
                Toast.makeText(context, "网络连接失败", Toast.LENGTH_LONG).show();
            }
        }
    }

    // 初始化动态广告栏中导航小图标样式
    public void setTipPic(ViewGroup vGroup, int selected, int regular) {
        tips = new ImageView[imgIdArray.length];

        for (int i = 0; i < tips.length; i++) {
            ImageView imageView = new ImageView(getActivity());

            imageView.setLayoutParams(new ViewGroup.LayoutParams(10, 10));
            tips[i] = imageView;
            if (i == 0) {
                tips[i].setBackgroundResource(selected);
            } else {
                tips[i].setBackgroundResource(regular);
            }

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            layoutParams.leftMargin = 7;
            layoutParams.rightMargin = 7;
            vGroup.addView(imageView, layoutParams);
        }
    }

}