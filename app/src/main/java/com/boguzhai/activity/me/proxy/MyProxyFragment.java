package com.boguzhai.activity.me.proxy;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.boguzhai.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyProxyFragment extends Fragment {

    private boolean isHistroy = false;//是否是历史代理
    private ProxyPricingActivity mContext;
    public MyProxyFragment() {
        // Required empty public constructor
    }


    public MyProxyFragment(boolean isHistroy) {
        this.isHistroy = isHistroy;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.me_my_proxy_fg, null);
//        if(isHistroy) {//如果是历史代理，则隐藏这两个
//            view.findViewById(R.id.tv_my_proxy_delete).setVisibility(View.GONE);
//            view.findViewById(R.id.tv_my_proxy_change).setVisibility(View.GONE);
//        }
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = (ProxyPricingActivity) getActivity();
        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {



    }


    class MyProxyAdapter extends BaseAdapter {


        @Override
        public int getCount() {
            return 0;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return null;
        }


        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }


    }


}
