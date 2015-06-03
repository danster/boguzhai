package com.boguzhai.activity.mainpage;

import android.app.Fragment;

import com.boguzhai.R;

public class FragmentFactory {
    public static Fragment getInstanceByIndex(int index) {
        Fragment fragment = null;
        switch (index) {
            case R.id.rb_1:
                fragment = new AuctionFragment();
                break;
            case R.id.rb_2:
                fragment = new SearchFragment();
                break;
            case R.id.rb_3:
                fragment = new MeFragment();
                break;
            default:
                fragment = new AuctionFragment();
                break;

        }
        return fragment;
    }
}
