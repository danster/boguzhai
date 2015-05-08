package com.boguzhai.activity.me.info;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.boguzhai.R;
import com.boguzhai.activity.base.BaseActivity;
import com.boguzhai.activity.base.Variable;
import com.boguzhai.logic.widget.ListViewForScrollView;

public class DeliveryAddressManageActivity extends BaseActivity {

    private ListViewForScrollView listview;
    private DeliveryAddressListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        this.setScrollView(R.layout.me_myinfo_addr_manage);
        title.setText("收货地址管理");
        init();
	}

	protected void init(){
        showListView();
        this.listen(R.id.add_address);
	}

    public void showListView(){
        listview = (ListViewForScrollView) findViewById(R.id.list);
        adapter = new DeliveryAddressListAdapter(this, Variable.account.deliveryAddressList);
        listview.setAdapter(adapter);
    }

	@Override
	public void onClick(View view) {
		super.onClick(view);
		switch (view.getId()) {
        case R.id.add_address:
            Variable.currentDeliveryAddress = null;
            startActivity(new Intent(this, DeliveryAddressEditActivity.class));
            break;
        default: break;
		};
	}

    @Override
    protected void onResume() {
        super.onResume();
    }

}
