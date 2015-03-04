package com.boguzhai.activity.mainpage;

import android.app.Fragment;
import android.util.Log;

import com.boguzhai.R;

public class FragmentFactory {
    public static Fragment getInstanceByIndex(int index) {
        Log.i("FragmentFactory","fragment index "+index);
        Fragment fragment = null;
        switch (index) {
            case R.id.rb_1:
                fragment = new HomeFragment();
                break;
            case R.id.rb_2:
                fragment = new AuctionFragment();
                break;
            case R.id.rb_3:
                fragment = new SearchFragment();
                break;
            case R.id.rb_4:
                fragment = new MeFragment();
                break;
            default:
                fragment = new HomeFragment();
                break;

        }
        return fragment;
    }
}
