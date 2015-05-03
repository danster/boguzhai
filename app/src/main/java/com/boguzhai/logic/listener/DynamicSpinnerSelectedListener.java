package com.boguzhai.logic.listener;

import android.view.View;
import android.widget.AdapterView;

import java.util.Arrays;

// Spinner监听类，使用数组形式操作spinner
public class DynamicSpinnerSelectedListener implements AdapterView.OnItemSelectedListener {
    public String[] list={};
    public StringBuffer result=new StringBuffer();

    public DynamicSpinnerSelectedListener(String[] list, StringBuffer result){
        this.list=list; this.result=result;
    }
    public DynamicSpinnerSelectedListener(StringBuffer result){ this.result=result;}
    public DynamicSpinnerSelectedListener(){}

    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        result.replace(0,result.length(),list[arg2]);
    }
    public void onNothingSelected(AdapterView<?> arg0) {}

    public int getIndex(){
        return Arrays.asList(list).indexOf(result.toString());
    }
}
