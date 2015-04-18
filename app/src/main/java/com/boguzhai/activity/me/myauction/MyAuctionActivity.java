package com.boguzhai.activity.me.myauction;

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
    private FragmentManager fragmentManager;
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
        fragmentManager = getFragmentManager();
        radioGroup = (RadioGroup) findViewById(R.id.rg_my_auction_tab);
        rb_1 = (RadioButton) findViewById(R.id.rb_my_auction_all);

        rb_1.setChecked(true);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.my_auction_content, FragmentFactory.getInstanceByIndex(R.id.rb_my_auction_all));
        fragmentTransaction.commit();


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.my_auction_content, FragmentFactory.getInstanceByIndex(checkedId));
                fragmentTransaction.commit();
            }
        });

	}




	@Override
	public void onClick(View view) {
        super.onClick(view);
    }
}
