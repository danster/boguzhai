package com.boguzhai.logic.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.boguzhai.R;

public class FooterView extends LinearLayout {

	public final static int FOOTER_OPTIONS_PULL = 0;
	public final static int FOOTER_OPTIONS_CLICK = 1;
	private static int sFooterOps = FOOTER_OPTIONS_PULL;
	
	public final static int STATE_NORMAL = 0;
	public final static int STATE_WILL_RELEASE = 1;
	public final static int STATE_LOADING = 2;
	private int mState = STATE_NORMAL;
	
	private View mFooter = null;
	private ImageView mArrow = null;
	private ProgressBar mProgressBar = null;
	private TextView mLoaderTips = null;
	
	private RotateAnimation mRotateUp = null;
	private RotateAnimation mRotateDown = null;
	private final static int ROTATE_DURATION = 250;
	
	public FooterView(Context context) {
		this(context, null);
	}
	
	public FooterView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initFooterView(context);
	}
	
	private void initFooterView(Context context){
		LayoutParams lp = new LayoutParams(
				LayoutParams.MATCH_PARENT, 0);
		mFooter = LayoutInflater.from(context).inflate(R.layout.loader_footer, null);
		addView(mFooter, lp);
		
		mArrow = (ImageView) mFooter.findViewById(R.id.ivLoaderArrow);
		mProgressBar = (ProgressBar) mFooter.findViewById(R.id.pbLoaderWaiting);
		mLoaderTips = (TextView) mFooter.findViewById(R.id.loader_tips);
		
		mRotateDown = new RotateAnimation(0.0f, 180.0f,  
				Animation.RELATIVE_TO_SELF, 0.5f, 
				Animation.RELATIVE_TO_SELF, 0.5f);
		mRotateDown.setDuration(ROTATE_DURATION);
		mRotateDown.setFillAfter(true);
		
		mRotateUp = new RotateAnimation(180.0f, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, 
				Animation.RELATIVE_TO_SELF, 0.5f);
		mRotateUp.setDuration(ROTATE_DURATION);
		mRotateUp.setFillAfter(true);
		
		setFooterViewOptions(FOOTER_OPTIONS_CLICK);
	}
	
	public void setFooterViewOptions(int options){
		sFooterOps = options;
		
		switch(sFooterOps){
		case FOOTER_OPTIONS_PULL:
			hide();
			break;
			
		case FOOTER_OPTIONS_CLICK:
			show();
			break;
			
		default:
			break;
		}
	}
	
	public int getFooterViewOptions(){
		return sFooterOps;
	}
	
	public void setFooterState(int state){
		if(mState == state){
			return;
		}
		
		mArrow.clearAnimation();
		if(state == STATE_LOADING){
			mProgressBar.setVisibility(View.VISIBLE);
			mArrow.setVisibility(View.GONE);
		}else{
			mProgressBar.setVisibility(View.GONE);
			mArrow.setVisibility(View.VISIBLE);
		}
		
		switch(state){
		case STATE_NORMAL:
			mArrow.startAnimation(mRotateUp);
			mLoaderTips.setText("上拉更多");
			break;
			
		case STATE_WILL_RELEASE:
			mArrow.startAnimation(mRotateDown);
			mLoaderTips.setText("松开加载");
			break;
			
		case STATE_LOADING:
			mLoaderTips.setText("正在加载 ...");
			break;
		
		default:
			break;
		}
		mState = state;
	}
	
	public int getCurrentState(){
		return mState;
	}
	
	public void setFooterHeight(int height){
		if(height <= 0){
			height = 0;
		}
		
		LayoutParams lp = (LayoutParams) mFooter.getLayoutParams();
		lp.height = height;
		mFooter.setLayoutParams(lp);
	}
	
	public int getFooterHeight(){
		return mFooter.getHeight();
	}
	
	public void hide(){
		mArrow.clearAnimation();
		mArrow.setVisibility(View.VISIBLE);
		mLoaderTips.setText("上拉更多");
		setFooterHeight(0);
	}
	
	public void show(){
		mArrow.clearAnimation();
		mArrow.setVisibility(View.GONE);
		mLoaderTips.setText("点击加载更多");
		
		LayoutParams lp = (LayoutParams) mFooter.getLayoutParams();
		lp.height = LayoutParams.WRAP_CONTENT;
		mFooter.setLayoutParams(lp);
	}
}
