package com.boguzhai.activity.base;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boguzhai.R;

public abstract class BaseActivity extends Activity implements OnClickListener {
    public static String TAG = "BaseActivity";
    public LinearLayout content, title_bar;
    public TextView title_left, title, title_right;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        this.setContentView(R.layout.base_frame);
		
		title_bar = (LinearLayout)findViewById(R.id.title);
		title_left = (TextView)findViewById(R.id.title_left);
        title = (TextView)findViewById(R.id.title_center);
		title_right = (TextView)findViewById(R.id.title_right);
    	
    	listen(title_left);
        listen(title);
        listen(title_right);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR); //禁止手机横屏

        title_right.setVisibility(View.INVISIBLE);

//        title_left.setTextSize(20);
//        title_center.setTextSize(30);
//        title_right.setTextSize(20);
//        title_left.setTextColor(this.getResources().getColor(R.color.white));
//        title_center.setTextColor(this.getResources().getColor(R.color.white));
//        title_right.setTextColor(this.getResources().getColor(R.color.white));

	}

	/************************ Set Content View ****************************/
	protected void setScrollView( int LayoutResrouce ){
        content = (LinearLayout)findViewById(R.id.scrollview);
        getLayoutInflater().inflate(LayoutResrouce,content);
	}
	
	protected void setLinearView( int LayoutResrouce ){
        content = (LinearLayout)findViewById(R.id.linearview);
        getLayoutInflater().inflate(LayoutResrouce,content);
	}

	/*************************** View Listener ****************************/
    // 监听一个 VieW
    public void listen(View view){
        if ( view != null ){
            view.setOnClickListener(this);
        }
    }
	// 监听一个 ViewId
	public void listen( int id){
		View view = findViewById(id);
		if ( view != null ){
			view.setOnClickListener(this);
		}
	}
	// 监听一组 View Ids
	public void listen( int[] ids){
		for(int id: ids){ 
			this.listen(id);
		}
	}

	/********************* Override  & Activity Manager *****************/
	@Override
 	public void onClick(View v){
		switch (v.getId()) {
			case R.id.title_left: finish(); return;
		}
	};
	
	@Override
	public boolean onKeyDown (int keyCode, KeyEvent event) {
		switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_DOWN:
            break;
            case KeyEvent.KEYCODE_VOLUME_UP:
            break;
            case KeyEvent.KEYCODE_BACK:
            break;
            case KeyEvent.KEYCODE_MENU:
            break;
            case KeyEvent.KEYCODE_HOME:
            break;
		}
		return super.onKeyDown (keyCode, event);
    }


}

	


