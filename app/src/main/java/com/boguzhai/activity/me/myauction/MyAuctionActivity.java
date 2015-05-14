package com.boguzhai.activity.me.myauction;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.boguzhai.R;
import com.boguzhai.activity.base.BaseActivity;

public class MyAuctionActivity extends BaseActivity {


    private final static String TAG = "MyAuctionActivity";

    private RadioGroup radioGroup;
    private FragmentManager fm;
    private RadioButton rb_1;





	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        Log.i(TAG, "MyAuctionActivity onCreate()");
        this.setLinearView(R.layout.me_myauction);
        title.setText("我的拍卖会");

        init();
	}

	protected void init(){
        fm = getFragmentManager();
        radioGroup = (RadioGroup) findViewById(R.id.rg_my_auction_tab);
        rb_1 = (RadioButton) findViewById(R.id.rb_my_auction_all);

        /**
         * 默认选择第一个radioButton
         */
        rb_1.setChecked(true);
        FragmentTransaction ft = fm.beginTransaction();
        showFragmentByTag(ft, "tag_all", R.id.rb_my_auction_all);
        ft.commit();


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {


                FragmentTransaction ft = fm.beginTransaction();
                switch (checkedId) {
                    case R.id.rb_my_auction_all:

                        hideFragmentByTag(ft, "tag_on_display");
                        hideFragmentByTag(ft, "tag_on_auction");
                        hideFragmentByTag(ft, "tag_histroy");
                        showFragmentByTag(ft, "tag_all", checkedId);

                        break;


                    case R.id.rb_my_auction_display:

                        hideFragmentByTag(ft, "tag_all");
                        hideFragmentByTag(ft, "tag_on_auction");
                        hideFragmentByTag(ft, "tag_histroy");
                        showFragmentByTag(ft, "tag_on_display", checkedId);

                        break;
                    case R.id.rb_my_auction_onauction:

                        hideFragmentByTag(ft, "tag_all");
                        hideFragmentByTag(ft, "tag_on_display");
                        hideFragmentByTag(ft, "tag_histroy");
                        showFragmentByTag(ft, "tag_on_auction", checkedId);

                        break;
                    case R.id.rb_my_auction_histroy:

                        hideFragmentByTag(ft, "tag_on_display");
                        hideFragmentByTag(ft, "tag_on_auction");
                        hideFragmentByTag(ft, "tag_all");
                        showFragmentByTag(ft, "tag_histroy", checkedId);

                        break;
                }
                ft.commit();
            }
        });

	}

    /**
     * 通过tag隐藏fragment
     * @param ft FragmentTransaction
     * @param tag Fragment TAG
     */
    public void hideFragmentByTag(FragmentTransaction ft, String tag) {
        if(fm.findFragmentByTag(tag) != null) {
            ft.hide(fm.findFragmentByTag(tag));
        }
    }


    /**
     * 通过tag,id显示fragment
     * @param ft FragmentTransaction
     * @param tag Fragment TAG
     * @param id RadioButton id
     */
    public void showFragmentByTag(FragmentTransaction ft, String tag, int id) {
        if(fm.findFragmentByTag(tag) == null) {
            Fragment fragment = FragmentFactory.getInstanceByIndex(id);
            ft.add(R.id.my_auction_content, fragment, tag);
        }else {
            ft.show(fm.findFragmentByTag(tag));
        }
    }



	@Override
	public void onClick(View view) {
        super.onClick(view);
    }
}
