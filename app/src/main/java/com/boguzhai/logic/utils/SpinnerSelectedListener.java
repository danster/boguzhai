package com.boguzhai.logic.utils;

import android.view.View;
import android.widget.AdapterView;

// Spinner监听类，使用数组形式操作spinner
public class SpinnerSelectedListener implements AdapterView.OnItemSelectedListener {
    private String[] list;
    StringBuffer result;

    public SpinnerSelectedListener(String[] list, StringBuffer result){
        this.list=list; this.result=result;
    }

    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        result.replace(0,result.length(),list[arg2]);
    }
    public void onNothingSelected(AdapterView<?> arg0) {}
}
