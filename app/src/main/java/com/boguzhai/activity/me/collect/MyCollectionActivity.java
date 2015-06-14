package com.boguzhai.activity.me.collect;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.boguzhai.R;
import com.boguzhai.activity.base.BaseActivity;

public class MyCollectionActivity extends BaseActivity {




    private final static String TAG = "MyCollectionActivity";

    private RadioGroup radioGroup;
    private FragmentManager fm;
    private RadioButton rb_1;

    private String[] sortTypes = {"按拍品名称","按拍品号",
            "按拍品起拍价升序", "按拍品起拍价降序",
            "按拍品估价最低值升序", "按拍品估价最低价降序",
            "按拍品估价最高值升序", "按拍品估价最高值降序" };
    private int sortType=0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        Log.i(TAG, "MyCollectionActivity, onCreate()");
        this.setLinearView(R.layout.me_mycollection);
        title.setText("我的收藏");
        title_right.setText("排序");
        title_right.setVisibility(View.VISIBLE);
        init();
	}

	protected void init(){
        fm = getFragmentManager();
        radioGroup = (RadioGroup) findViewById(R.id.rg_my_auction_tab);
        rb_1 = (RadioButton) findViewById(R.id.rb_my_collection_all);

        rb_1.setChecked(true);
        FragmentTransaction ft = fm.beginTransaction();
        showFragmentByTag(ft, "tag_all", R.id.rb_my_collection_all);
        ft.commit();

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {


                FragmentTransaction ft = fm.beginTransaction();
                switch (checkedId) {
                    case R.id.rb_my_collection_all:

                        hideFragmentByTag(ft, "tag_on_display");
                        hideFragmentByTag(ft, "tag_abort");
                        hideFragmentByTag(ft, "tag_on_auction");
                        hideFragmentByTag(ft, "tag_history");
                        showFragmentByTag(ft, "tag_all", checkedId);

                        break;


                    case R.id.rb_my_collection_display:

                        hideFragmentByTag(ft, "tag_all");
                        hideFragmentByTag(ft, "tag_abort");
                        hideFragmentByTag(ft, "tag_on_auction");
                        hideFragmentByTag(ft, "tag_history");
                        showFragmentByTag(ft, "tag_on_display", checkedId);

                        break;
                    case R.id.rb_my_collection_onauction:

                        hideFragmentByTag(ft, "tag_all");
                        hideFragmentByTag(ft, "tag_abort");
                        hideFragmentByTag(ft, "tag_on_display");
                        hideFragmentByTag(ft, "tag_history");
                        showFragmentByTag(ft, "tag_on_auction", checkedId);

                        break;
                    case R.id.rb_my_collection_history:

                        hideFragmentByTag(ft, "tag_on_display");
                        hideFragmentByTag(ft, "tag_abort");
                        hideFragmentByTag(ft, "tag_on_auction");
                        hideFragmentByTag(ft, "tag_all");
                        showFragmentByTag(ft, "tag_history", checkedId);

                        break;

                    case R.id.rb_my_collection_abort:

                        hideFragmentByTag(ft, "tag_on_display");
                        hideFragmentByTag(ft, "tag_on_auction");
                        hideFragmentByTag(ft, "tag_all");
                        hideFragmentByTag(ft, "tag_history");
                        showFragmentByTag(ft, "tag_abort", checkedId);

                        break;
                }
                ft.commit();
            }
        });


	}

    /**
     * 通过tag隐藏fragment
     * @param ft FragmentTransaction
     * @param tag Fragment TAG
     */
    public void hideFragmentByTag(FragmentTransaction ft, String tag) {
        if(fm.findFragmentByTag(tag) != null) {
            ft.hide(fm.findFragmentByTag(tag));
        }
    }


    /**
     * 通过tag,id显示fragment
     * @param ft FragmentTransaction
     * @param tag Fragment TAG
     * @param id RadioButton id
     */
    public void showFragmentByTag(FragmentTransaction ft, String tag, int id) {
        if(fm.findFragmentByTag(tag) == null) {
            Fragment fragment = FragmentFactory.getInstanceByIndex(id);
            ft.add(R.id.my_collection_content, fragment, tag);
        }else {
            ft.show(fm.findFragmentByTag(tag));
        }
    }



    @Override
    public void onClick(View view) {
        super.onClick(view);
    }
}
