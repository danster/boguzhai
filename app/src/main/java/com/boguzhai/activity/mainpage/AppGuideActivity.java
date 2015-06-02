package com.boguzhai.activity.mainpage;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.boguzhai.R;
import com.boguzhai.activity.base.BaseActivity;
import com.boguzhai.logic.utils.Utility;

public class AppGuideActivity extends BaseActivity {
	ViewPager viewPager;
    ViewGroup viewGroup;
	ImageView[] imageViews, tips;
	ImageView startTips, signTips;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setLinearView(R.layout.app_guide_page);
        title_bar.setVisibility(View.GONE);
		setBaseEnv();
	}
	
	public void setBaseEnv(){
        viewGroup = (ViewGroup) findViewById(R.id.viewGroup);
		viewPager = (ViewPager) findViewById(R.id.viewPager);
		int[] imageIds = new int[] { R.drawable.app_guide1, R.drawable.app_guide2,
				                     R.drawable.app_guide3, R.drawable.app_guide4 };
		
		// 将静态图片ID装载到数组中
		imageViews = new ImageView[imageIds.length];
		for (int i = 0; i < imageViews.length; i++) {
			ImageView imageView = new ImageView(this);
			imageViews[i] = imageView;
			imageView.setBackgroundResource(imageIds[i]);
		}

        // 将导航小图标加入到ViewGroup中
        tips = new ImageView[imageIds.length];

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(80,80); //设定圆点大小
        layoutParams.leftMargin = 5;
        layoutParams.rightMargin = 5;

        for (int i = 0; i < tips.length; i++) {
            ImageView imageView = new ImageView(this);
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
            public int getCount(){ return 4;} // 只能来回滑动，不能循环

            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {return arg0 == arg1;}

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(imageViews[position % imageViews.length]);
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(imageViews[position % imageViews.length], 0);
                return imageViews[position % imageViews.length];
            }
        });
		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener()  {
            public void onPageScrollStateChanged(int arg0) {}
            public void onPageScrolled(int arg0, float arg1, int arg2) {} //当当前页面被滑动时调用

            // 设置ViewPage被选中时的动作
            public void onPageSelected(int arg0) {
                for (int i = 0; i < tips.length; i++) {
                    if (i == arg0 % imageViews.length) {
                        tips[i].setBackgroundResource(R.drawable.circle_selected);
                    } else {
                        tips[i].setBackgroundResource(R.drawable.circle_unselected);
                    }
                }

                if ( arg0 % imageViews.length == imageViews.length-1){
                    startTips.setVisibility(View.VISIBLE);
                    signTips.setVisibility(View.VISIBLE);
                } else {
                    startTips.setVisibility(View.INVISIBLE);
                    signTips.setVisibility(View.INVISIBLE);
                }
            }

        });
		viewPager.setCurrentItem(0);
		
		startTips = (ImageView)findViewById(R.id.app_enter);
        signTips = (ImageView)findViewById(R.id.app_sign);
		startTips.setVisibility(View.INVISIBLE);
        signTips.setVisibility(View.INVISIBLE);

        startTips.setOnClickListener(this);
        signTips.setOnClickListener(this);
	}

    @Override
    public void onClick(View v){
        switch (v.getId()) {
            case R.id.app_enter:
                Utility.gotoMainpage(1);
                break;

            case R.id.app_sign:
                Utility.gotoMainpage(4);
                break;
        }
    };



}
