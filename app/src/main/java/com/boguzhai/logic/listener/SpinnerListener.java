package com.boguzhai.logic.listener;

import android.view.View;
import android.widget.AdapterView;

import java.util.ArrayList;

// Spinner监听类，使用数组形式操作spinner
public class SpinnerListener implements AdapterView.OnItemSelectedListener {
    private String[] list;
    private StringBuffer result;

    public SpinnerListener(String[] list, StringBuffer result){
        this.list=list;
        this.result=result;
    }

    public SpinnerListener(ArrayList<String> arrayList, StringBuffer result){
        this.list = arrayList.toArray(new String[arrayList.size()]);
        this.result=result;
    }

    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        if(list[arg2].equals("不限")){
            result.replace(0,result.length(),"");
            return;
        }
        result.replace(0,result.length(),list[arg2]);
    }
    public void onNothingSelected(AdapterView<?> arg0) {
        result.replace(0,result.length(),"");
    }
}
