package com.boguzhai.activity.me.collect;

import android.app.Fragment;
import android.util.Log;

import com.boguzhai.R;

public class FragmentFactory {


    private static final String TAG = "FragmentFactory";


    public static Fragment getInstanceByIndex(int index) {
        Log.i("FragmentFactory","fragment index "+index);
        Fragment fragment = null;
        switch (index) {
            case R.id.rb_my_collection_all:
                fragment = new MyCollectionFragment("");
                break;
            case R.id.rb_my_collection_display:
                fragment = new MyCollectionFragment("预展中");
                break;
            case R.id.rb_my_collection_onauction:
                fragment = new MyCollectionFragment("拍卖中");
                break;
            case R.id.rb_my_collection_history:
                fragment = new MyCollectionFragment("已成交");
                break;
            case R.id.rb_my_collection_abort:
                fragment = new MyCollectionFragment("已流拍");
                break;
        }
        return fragment;
    }
}
