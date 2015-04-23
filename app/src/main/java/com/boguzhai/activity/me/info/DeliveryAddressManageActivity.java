package com.boguzhai.activity.me.info;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.boguzhai.R;
import com.boguzhai.activity.base.BaseActivity;
import com.boguzhai.logic.widget.ListViewForScrollView;

import java.util.ArrayList;

public class DeliveryAddressManageActivity extends BaseActivity {

    private ArrayList<DeliveryAddress> list;
    private ListViewForScrollView listview;
    DeliveryAddressListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        this.setScrollView(R.layout.me_myinfo_addr_manage);
        title.setText("收货地址管理");
        init();
	}

	protected void init(){
        showListView();
        this.listen(R.id.ok);
	}

    public void showListView(){
        listview = (ListViewForScrollView) findViewById(R.id.list);
        list = new ArrayList<DeliveryAddress>();

        for(int i=0; i<4; i++){
            DeliveryAddress addr = new DeliveryAddress();
            list.add(addr);
        }

        adapter = new DeliveryAddressListAdapter(this, list);
        listview.setAdapter(adapter);
    }

	@Override
	public void onClick(View view) {
		super.onClick(view);
		switch (view.getId()) {
        case R.id.ok:
            Intent intent = new Intent(this, DeliveryAddressEditActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("address", null);
            intent.putExtras(bundle);
            // 1 代表去往编辑收货地址页面
            startActivityForResult(intent, 1);
            break;
        default: break;
		};
	}

    @Override
    protected void onResume() {
        super.onResume();
    }

    protected void onActivityResult (int requestCode, int resultCode, Intent data){
        // 第一个requestCode是用来区分哪个activity回来的数据
        // 第二个resultCode，如果activity B有几种不同返回的结果，同样地可以通过resultCode来筛选

        switch (requestCode) {
            // 1 代表是从编辑收货地址页面跳转回来的
            case 1:
                int id = data.getIntExtra("id",-1);
                Bundle bundle=data.getExtras();
                DeliveryAddress newAddress = (DeliveryAddress) bundle.getSerializable("address");

                switch (resultCode) {
                    // 删除该地址
                    case 0:
                        if(id >= 0){
                            for(int i=0; i<list.size();++i){
                                if(list.get(i).id == id){
                                    list.remove(i);
                                    break;
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }
                        break;

                    // 新增一地址
                    case 1:
                        if(newAddress != null)
                            list.add(newAddress);
                        adapter.notifyDataSetChanged();
                        break;

                    // 更新一地址
                    case 2:
                        if(id >= 1 && newAddress != null) {
                            for (int i = 0; i < list.size(); ++i) {
                                if (list.get(i).id == id) {
                                    list.remove(i);
                                    list.add(i,newAddress);
                                    break;
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
    }

}
