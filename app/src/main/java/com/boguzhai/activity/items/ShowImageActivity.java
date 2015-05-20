package com.boguzhai.activity.items;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.boguzhai.R;
import com.boguzhai.activity.base.Variable;

import java.io.InputStream;
import java.net.URL;


public class ShowImageActivity extends Activity {

    private ImageView imageView = null;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_big_photo);
        imageView = (ImageView)this.findViewById(R.id.large_image );

        // 网络下载拍品图片
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    BitmapFactory.Options options=new BitmapFactory.Options();
                    options.inJustDecodeBounds = false;
                    options.inSampleSize = 2;
                    InputStream in = new URL(Variable.currentLot.imageUrl).openStream();
                    imageView.setImageBitmap(BitmapFactory.decodeStream(in, null, options));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();


        imageView.setOnClickListener(new View.OnClickListener() { // 点击返回
            public void onClick(View paramView) {
                finish();
            }
        });
    }
}
