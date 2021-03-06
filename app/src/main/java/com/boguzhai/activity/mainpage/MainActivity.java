package com.boguzhai.activity.mainpage;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.boguzhai.R;
import com.boguzhai.activity.base.App;

public class MainActivity extends Activity {
    private FragmentManager fragmentManager;
    private RadioGroup radioGroup;
    public AlertDialog.Builder tips;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);

        fragmentManager = getFragmentManager();
        radioGroup = (RadioGroup) findViewById(R.id.rg_tab);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                Fragment fragment = FragmentFactory.getInstanceByIndex(checkedId);
                App.mainTabIndex = checkedId;
                transaction.replace(R.id.content, fragment);
                transaction.commit();
            }
        });

        tips = new AlertDialog.Builder(this);
        tips.setTitle("提示").setPositiveButton("确定", null);

        RadioButton radio1 = (RadioButton)findViewById(App.mainTabIndex);
        radio1.setChecked(true);

    }

}