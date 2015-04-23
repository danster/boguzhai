package com.boguzhai.activity.me.myauction;

import android.app.Fragment;
import android.util.Log;

import com.boguzhai.R;
import com.boguzhai.activity.mainpage.AuctionFragment;
import com.boguzhai.activity.mainpage.HomeFragment;
import com.boguzhai.activity.mainpage.MeFragment;
import com.boguzhai.activity.mainpage.SearchFragment;

public class FragmentFactory {


    private static final String TAG = "FragmentFactory";
    public static Fragment getInstanceByIndex(int index) {
        Log.i("FragmentFactory","fragment index "+index);
        Fragment fragment = null;
        switch (index) {
            case R.id.rb_my_auction_all:
                fragment = new BaseFragment("");
                break;
            case R.id.rb_my_auction_display:
                fragment = new BaseFragment("预展中");
                break;
            case R.id.rb_my_auction_onauction:
                fragment = new BaseFragment("拍卖中");
                break;
            case R.id.rb_my_auction_histroy:
                fragment = new BaseFragment("已结束");
                break;
        }
        return fragment;
    }
}
