package com.boguzhai.activity.mainpage;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.boguzhai.R;
import com.boguzhai.activity.search.SearchAuctionActivity;
import com.boguzhai.activity.search.SearchLotActivity;

public class SearchFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fg_search, null);

        TextView title = (TextView)view.findViewById(R.id.title_center);
        title.setText("查询");
        view.findViewById(R.id.title_left).setVisibility(View.INVISIBLE);
        view.findViewById(R.id.title_right).setVisibility(View.INVISIBLE);

        MyOnClickLister listener = new MyOnClickLister();
        view.findViewById(R.id.search_auction).setOnClickListener(listener);
        view.findViewById(R.id.search_lot).setOnClickListener(listener);
        return view;
    }

    class MyOnClickLister implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if(view.getId() == R.id.search_auction){
                startActivity(new Intent(getActivity(), SearchAuctionActivity.class));
            }
            if(view.getId() == R.id.search_lot){
                startActivity(new Intent(getActivity(), SearchLotActivity.class));
            }

        }
    }
}