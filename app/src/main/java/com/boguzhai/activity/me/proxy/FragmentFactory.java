package com.boguzhai.activity.me.proxy;

import android.app.Fragment;
import android.util.Log;

import com.boguzhai.R;

public class FragmentFactory {


    private static final String TAG = "FragmentFactory";
    public static Fragment getInstanceByIndex(int index) {
        Log.i("FragmentFactory","fragment index "+index);
        Fragment fragment = null;
        switch (index) {
            case R.id.rb_my_proxy:
                fragment = new MyProxyFragment(1);
                Log.i(TAG, "我的代理出价");
                break;
            case R.id.rb_my_proxy_histroy:
                fragment = new MyProxyFragment(2);
                Log.i(TAG, "历史代理");
                break;
        }
        return fragment;
    }
}
