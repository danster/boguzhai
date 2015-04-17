package com.boguzhai.logic.utils;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.boguzhai.activity.base.BaseActivity;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.Arrays;


public class Utility {

    public static Bitmap Create2DCode(String str) throws WriterException {
        //生成二维矩阵,编码时指定大小,不要生成了图片以后再进行缩放,这样会模糊导致识别失败
        BitMatrix matrix = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, 300, 300);
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        //二维矩阵转为一维像素数组,也就是一直横着排了
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if(matrix.get(x, y)){
                    pixels[y * width + x] = 0xff000000;
                }

            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        //通过像素数组生成bitmap,具体参考api
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }


    // 给spinner设置自定义Listener
    public void setSpinner(BaseActivity activity, View v, int view_id, String[] list, StringBuffer result, AdapterView.OnItemSelectedListener listener){
        Spinner spinner;
        if(v == null){ spinner = (Spinner) activity.findViewById(view_id);
        }else {  spinner = (Spinner) v.findViewById(view_id);}

        AdapterView.OnItemSelectedListener _listener;
        if(listener==null){_listener = new SpinnerSelectedListener(list, result);
        }else{ _listener = listener; }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item,list);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(_listener);
        spinner.setVisibility(View.VISIBLE);
        spinner.setSelection( Arrays.asList(list).indexOf(result.toString()));
    }

    // 给spinner设置初始环境
    public void setSpinner(BaseActivity activity, View v, int view_id, String[] list, StringBuffer result){
        setSpinner(activity,v,view_id,list,result,null);
    }

    // Activity环境下，给spinner设置初始环境
    public void setSpinner(BaseActivity activity, int view_id, String[] list, StringBuffer result){
        setSpinner(activity,null,view_id,list,result, null);
    }

    // 使用数组形式操作spinner
    class SpinnerSelectedListener implements AdapterView.OnItemSelectedListener {
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

}

