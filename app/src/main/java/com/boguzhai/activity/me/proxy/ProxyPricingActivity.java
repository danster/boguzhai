package com.boguzhai.activity.me.proxy;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.boguzhai.R;
import com.boguzhai.activity.base.BaseActivity;

public class ProxyPricingActivity extends BaseActivity {

    private final String TAG = "ProxyPricingActivity";

    private FragmentManager fm;
    private RadioGroup rg;
    private RadioButton rb;
    private String aucctionName;


    public String getAucctionName() {
        return aucctionName;
    }


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        this.setLinearView(R.layout.me_proxy);
        title.setText("代理出价");
        init();
	}

	protected void init(){
        Log.i(TAG, "init()");
        if(null != getIntent()){
            if(null != getIntent().getExtras()){
                if(null != getIntent().getExtras().getString("auctionName")){
                    aucctionName = getIntent().getExtras().getString("auctionName");
                }
            }
        }
        Log.i(TAG, "aucctionName:" + aucctionName);

        rg = (RadioGroup) findViewById(R.id.rg_my_proxy);
        rb = (RadioButton) findViewById(R.id.rb_my_proxy);


        fm = getFragmentManager();
        rb.setChecked(true);
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.my_proxy_content, FragmentFactory.getInstanceByIndex(R.id.rb_my_proxy), "tag1");
        ft.commit();


        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Log.i(TAG, "checkedChanged!");
                FragmentTransaction ft = fm.beginTransaction();
                Fragment fragment;
                switch (checkedId) {
                    case R.id.rb_my_proxy :
                        if(fm.findFragmentByTag("tag2") != null) {
                            ft.hide(fm.findFragmentByTag("tag2"));
                        }
                        if(fm.findFragmentByTag("tag1") == null) {
                            fragment = FragmentFactory.getInstanceByIndex(checkedId);
                            ft.add(R.id.my_proxy_content, fragment, "tag1");
                        }else {
                            ft.show(fm.findFragmentByTag("tag1"));
                        }
                        break;
                    case R.id.rb_my_proxy_histroy :
                        if(fm.findFragmentByTag("tag1") != null) {
                            ft.hide(fm.findFragmentByTag("tag1"));
                        }
                        if(fm.findFragmentByTag("tag2") == null) {
                            fragment = FragmentFactory.getInstanceByIndex(checkedId);
                            ft.add(R.id.my_proxy_content, fragment, "tag2");
                        }else {
                            ft.show(fm.findFragmentByTag("tag2"));
                        }
                        break;
                }
                ft.commit();
            }
        });



	}

}
